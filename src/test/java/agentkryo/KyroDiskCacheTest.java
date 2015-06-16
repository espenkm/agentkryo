package agentkryo;

import java.io.File;
import java.util.concurrent.Callable;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class KyroDiskCacheTest {

	private static final String rootDir = System.getProperty("java.io.tmpdir") + "/KyroDiskCacheTest";

	@Before
	@After
	public void cleanFiles() {
		File dir = new File(rootDir);
		deleteDir(dir);
		dir.mkdirs();
	}

	@Test
	public void callOnUncachedObjectShouldPersistToDisk() throws Exception {
		final TestFoo decoratedClazz = new TestFoo();
		
		KryoDiskCache cache = new KryoDiskCache(rootDir, decoratedClazz.getClass().getMethod("getNumberOfCalls"), new Object[0], new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return decoratedClazz.getNumberOfCalls();
			}
		}); 
		
		File file = new File(cache.getFileName());
		
		Assert.assertEquals(Integer.valueOf(1), cache.call());
		Assert.assertTrue("File was not created " + cache.getFileName(), file.exists());
		Assert.assertEquals(Integer.valueOf(1), cache.call());
		
		Assert.assertTrue(file.delete());
		Assert.assertEquals(Integer.valueOf(2), cache.call());
	}
	
	@Test
	public void shouldInteceptCallsWithArrays() throws Exception {
		final TestFoo decoratedClazz = new TestFoo();
		
		KryoDiskCache cache = new KryoDiskCache(rootDir, decoratedClazz.getClass().getMethod("getBars"), new Object[0], new Callable<TestBar[]>() {
			@Override
			public TestBar[] call() throws Exception {
				return decoratedClazz.getBars();
			}
		}); 
		
		File file = new File(cache.getFileName());
		
		Assert.assertEquals(3, ((TestBar[])cache.call()).length);
		Assert.assertTrue("File was not created " + cache.getFileName(), file.exists());
		Assert.assertEquals(3, ((TestBar[])cache.call()).length);
	}
	
	private void deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (String child : children) {
				deleteDir(new File(dir, child));
			}
		}
		
		dir.delete();
	}
	
}


