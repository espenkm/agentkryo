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

	private String rootDir;

	private String signature;

	private Object that;

	private Object[] args;

	private Method method;

	public KryoDiskCache(String rootDir, Object obj, Method method, Object[] args) {
		this.rootDir = rootDir;
		this.signature = method.getClass().getCanonicalName() + "." + method.getName() + "(" + Joiner.on("::").join(args)  + ")";
		this.args = args;
		this.method = method;
		this.that = obj;
	}

	public Object call() {
		Object result = null;
		String fileName = null;
		
		try {
			Kryo kryo = new Kryo();
			fileName = getFileName();
			File file = new File(fileName);

			if (file.exists()) {
				result = reloadFromDisk(kryo, file);
			} else {
				file.createNewFile();
				result =  method.invoke(that, args);
				storeToDisk(file, kryo, result);
			}
		} catch (Exception e) {
			System.out.println(fileName + ": " + e);
		}

		return result;
	}

	protected String getFileName() throws UnsupportedEncodingException {
		return rootDir + java.net.URLEncoder.encode(signature + ".cache", "UTF-8");
	}

	private void storeToDisk(final File file, final Kryo kryo, final Object result) throws Exception {
		Output output = new Output(new FileOutputStream(file));
		
		try {
			kryo.writeObject(output, result);
		} catch (Exception ex) {
			System.out.println(ex);
			file.delete();
		} finally {
			output.close();
		}
	}

	private Object reloadFromDisk(Kryo kryo, File file) throws Exception {
		return kryo.readObjectOrNull(new Input(new FileInputStream(file)), method.getReturnType());
	}
}
