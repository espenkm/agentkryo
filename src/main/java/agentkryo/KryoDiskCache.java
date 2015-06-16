package agentkryo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.base.Joiner;

public class KryoDiskCache {
	
	private ThreadLocal<Kryo> kryos = new ThreadLocal<Kryo>() {
	    protected Kryo initialValue() {
	        return new Kryo();
	    };
	};

	private String rootDir;

	private String signature;

	private Method method;

	private Callable<?> called;

	public KryoDiskCache(String rootDir, Method method, Object[] args, Callable<?> called) {
		this.rootDir = rootDir;
		this.called = called;
		this.signature = method.getClass().getCanonicalName() + "." + method.getName() + "(" + Joiner.on("::").join(args)  + ")";
		this.method = method;
	}

	public Object call() {
		Object result = null;
		String fileName = null;
		
		try {
			Kryo kryo = kryos.get();
			fileName = getFileName();
			File file = new File(fileName);

			if (file.exists()) {
				result = reloadFromDisk(kryo, file);
			} else {
				result = called.call(); 
				if (!file.createNewFile()) {
					System.out.println("Unable to create file :" + file.getName());
				}
				
				storeToDisk(file, kryo, result);
			}
		} catch (Exception e) {
			System.out.println(fileName + ": " + e);
		}

		return result;
	}

	protected String getFileName() throws UnsupportedEncodingException {
		return rootDir + "/" + java.net.URLEncoder.encode(signature + ".cache", "UTF-8");
	}

	private void storeToDisk(final File file, final Kryo kryo, final Object objectToStore) throws Exception {
		Output output = new Output(new FileOutputStream(file));
		
		if (method.getReturnType().isArray()) {
			kryo.writeObject(output, objectToStore);
		} else {
			kryo.writeClassAndObject(output, objectToStore);
		}
		
		output.close();
	}

	private Object reloadFromDisk(Kryo kryo, File file) throws Exception {
		return kryo.readObject(new Input(new FileInputStream(file)), method.getReturnType());
	}
}
