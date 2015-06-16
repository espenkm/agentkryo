package agentkryo;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import net.bytebuddy.agent.ByteBuddyAgent;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Lists;

public class AgentKryoTest {
	
	private static final String rootDir = System.getProperty("java.io.tmpdir") + "/AgentKryoTest";
	private static final String classStartsWith = ",agentkryo.TestFoo";

	@BeforeClass
	public static void instrument() {
		deleteDir(new File(rootDir));
		AgentKryo.premain(rootDir + classStartsWith,  ByteBuddyAgent.installOnOpenJDK());
	}

	@Test
	public void shouldInteceptCalls() throws Exception {
		TestFoo foo = new TestFoo();
		assertEquals(Integer.valueOf(1), foo.getNumberOfCalls());
		assertEquals(Integer.valueOf(1), foo.getNumberOfCalls());
	}
	
	@Test
	public void shouldInteceptCallsWithArrays() {
		TestFoo foo = new TestFoo();
		List<TestBar> actual = Lists.newArrayList(foo.getBars());
		List<TestBar> expected = Lists.newArrayList(foo.getBars());
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void shouldInteceptCallsWithParameters() {
		TestFoo foo = new TestFoo();
		assertEquals("into", foo.getFoo(0));
		assertEquals("broke", foo.getFoo(1));
		assertEquals("into", foo.getFoo(0));
		assertEquals("home", foo.getFoo(2));
	}

	private static void deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (String child : children) {
				deleteDir(new File(dir, child));
			}
		}
		
		dir.delete();
	}
}
