package agentkryo;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import net.bytebuddy.agent.ByteBuddyAgent;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

public class AgentKryoTest {
	
	private static final String rootDir = System.getProperty("java.io.tmpdir") + "/AgentKryoTest";

	@Before
	public void cleanFiles() {
		deleteDir(new File(rootDir));
	}

	@Test
	public void shouldInteceptCalls() {
		AgentKryo.premain(rootDir + ";agentkryo.Test",  ByteBuddyAgent.installOnOpenJDK());
		
		TestFoo foo = new TestFoo();
		assertEquals(Integer.valueOf(1), foo.getNumberOfCalls());
		assertEquals(Integer.valueOf(1), foo.getNumberOfCalls());
	}
	
	@Test
	public void shouldInteceptCallsWithArrays() {
		AgentKryo.premain(rootDir + ";agentkryo.Test", ByteBuddyAgent.installOnOpenJDK());
		
		TestFoo foo = new TestFoo();
		List<TestBar> actual = Lists.newArrayList(foo.getBars());
		List<TestBar> expected = Lists.newArrayList(foo.getBars());
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void shouldInteceptCallsWithParameters() {
		AgentKryo.premain(rootDir + ";agentkryo.Test", ByteBuddyAgent.installOnOpenJDK());
		
		TestFoo foo = new TestFoo();
		assertEquals("into", foo.getBars(0));
		assertEquals("broke", foo.getBars(1));
		assertEquals("into", foo.getBars(0));
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
