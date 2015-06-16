package agentkryo;

import static org.junit.Assert.assertEquals;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;

import net.bytebuddy.agent.ByteBuddyAgent;

import org.junit.Test;

import com.google.common.collect.Lists;

public class AgentKryoTest {

	@Test
	public void shouldInteceptCalls() {
		Instrumentation installOnOpenJDK = ByteBuddyAgent.installOnOpenJDK();
		AgentKryo.premain("agentkryo.AgentKryoTest", installOnOpenJDK);
		
		Foo foo = new Foo();
		assertEquals(1, foo.getNumberOfCalls().intValue());
		assertEquals(1, foo.getNumberOfCalls().intValue());
	}
	
	@Test
	public void shouldInteceptCallsWithArrays() {
		Instrumentation installOnOpenJDK = ByteBuddyAgent.installOnOpenJDK();
		AgentKryo.premain("agentkryo.AgentKryoTest", installOnOpenJDK);
		
		Foo foo = new Foo();
		List<Bar> actual = Lists.newArrayList(foo.getBars());
		List<Bar> expected = Lists.newArrayList(foo.getBars());
		assertEquals(expected, actual);
	}
	
	public class Foo  {
		int numberOfClass = 0;
		
		public Integer getNumberOfCalls() {
			return ++numberOfClass;
		}
		
		public Bar[] getBars(){
			return new Bar[] {new Bar("walks", "into"), new Bar("leaves", "broke")};
		}
	}
	
	public static class Bar  {
		String foo;
		String bar;
		
		public Bar(){};
		
		public Bar(String foo, String bar) {
			this.foo = foo;
			this.bar = bar;
		}

		@Override
		public String toString() {
			return "Bar [foo=" + foo + ", bar=" + bar + "]";
		}
		
	}
}
