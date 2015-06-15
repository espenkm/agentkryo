package agentkryo;

import java.io.File;
import java.util.concurrent.Callable;

import org.junit.Assert;
import org.junit.Test;

public class KyroDiskCacheTest {
	
	@Test
	public void callOnUncachedObjectShouldPersistToDisk() throws Exception {
		Foo decoratedClazz = new Foo();
		
		KryoDiskCache cache = new KryoDiskCache("/tmp/test/", "call", String.class, decoratedClazz); 
		File file = new File(cache.getFileName());
		
		cleanUp(file);
		
		try {
			Assert.assertEquals("This is a test", cache.call());
			Assert.assertEquals(1, decoratedClazz.calls);
			
			Assert.assertTrue("File was not created " + cache.getFileName(), file.exists());
			
			Assert.assertEquals("This is a test", cache.call());
			Assert.assertEquals(1, decoratedClazz.calls);
		} finally {
			cleanUp(file);
		}
	}

	private void cleanUp(File file) {
		if (file.exists()) {
			file.delete();
		}
	}

	private static class Foo implements Callable<String> {
		private int calls = 0;
		
		@Override
		public String call() throws Exception {
			calls++;
			return "This is a test";
		}
	}
}
