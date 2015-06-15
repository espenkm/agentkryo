package agentkryo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.Callable;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class KryoDiskCache {

	private String rootDir;

	private Callable<?> decoratedClazz;

	private String signature;

	private Class<?> returnType;

	public KryoDiskCache(String rootDir, String signature, Class<?> returnType, Callable<?> decoratedClazz) {
		this.rootDir = rootDir;
		this.signature = signature;
		this.returnType = returnType;
		this.decoratedClazz = decoratedClazz;
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
				result = decoratedClazz.call();
				storeToDisk(file, kryo, result);
			}
		} catch (Exception e) {
			System.out.println(fileName + ": " + e);
		}

		return result;
	}

	protected String getFileName() throws UnsupportedEncodingException {
		return rootDir + java.net.URLEncoder.encode(decoratedClazz.getClass().getName() + "." + signature + ".cache", "UTF-8");
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
		return kryo.readObject(new Input(new FileInputStream(file)), returnType);
	}

	@Override
	public String toString() {
		return "FromDiskCacheLoader [actualLoader=" + decoratedClazz.getClass().getName() + ", rootDir=" + rootDir + "]";
	}
}
