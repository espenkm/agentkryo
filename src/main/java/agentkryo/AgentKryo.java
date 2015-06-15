package agentkryo;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import com.google.common.base.Joiner;

public class AgentKryo {
	
	public static void premain(final String agentArgs, Instrumentation inst) {
		System.out.println("==================================================================");
		System.out.println("May the agent be with you in " + agentArgs);
		System.out.println("==================================================================");
		
		new AgentBuilder.Default()
				.rebase(ElementMatchers.anyOf(Cachable.class).or(
						new ElementMatcher<TypeDescription>() { @Override
							public boolean matches(TypeDescription target) {
								return target.getCanonicalName().startsWith(agentArgs);
							}
						}))
				.transform(new CachingTransformer())
				.installOn(inst);
	}
 
	private static class CachingTransformer implements AgentBuilder.Transformer {
 		@Override
		public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription) {
			return builder
					.method(ElementMatchers.any())
					.intercept(MethodDelegation.to(CachingInterceptor.class));
		}
	}
 
	public static class CachingInterceptor {
		@RuntimeType
		public static Object intercept(@Origin String signature, @AllArguments Object[] allArguments, @Origin Method method, @SuperCall Callable<?> zuper) throws Exception {
			return new KryoDiskCache("/tmp/agentkryo/", method.getName() + "(" + Joiner.on("::").join(allArguments)  + ")", method.getReturnType(), zuper).call();
		}
	}

}
