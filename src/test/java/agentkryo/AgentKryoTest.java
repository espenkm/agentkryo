package agentkryo;

import static org.junit.Assert.assertEquals;

import java.lang.instrument.Instrumentation;

import net.bytebuddy.agent.ByteBuddyAgent;

import org.junit.Test;

public class AgentKryoTest {

	@Test
	public void shouldInteceptCalls() {
		Instrumentation installOnOpenJDK = ByteBuddyAgent.installOnOpenJDK();
		AgentKryo.premain("agentkryo.AgentKryoTest", installOnOpenJDK);
		
		Foo foo = new Foo();
		assertEquals(1, foo.getNumberOfCalls().intValue());
		assertEquals(1, foo.getNumberOfCalls().intValue());
	}
	
	public class Foo  {
		int numerOfClass = 0;
		
		public Integer getNumberOfCalls() {
			return ++numerOfClass;
		}
	}
}
