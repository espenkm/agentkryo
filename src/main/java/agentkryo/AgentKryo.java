package agentkryo;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
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

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public class AgentKryo {
	
	private static String rootDir;
	private static String startsWith; 
	
	public static void premain(final String agentArgs, Instrumentation inst) {
		List<String> args = Lists.newArrayList(Splitter.on(",").split(agentArgs));
		rootDir = args.get(0);
		startsWith = args.get(1);
		
		System.out.println("==================================================================");
		System.out.println("May the agent be with you in " + rootDir + " and classes starting with " + startsWith);
		System.out.println("==================================================================");
		
		new File(rootDir).mkdirs();
		new AgentBuilder.Default()
				.rebase(new ElementMatcher<TypeDescription>() { @Override
							public boolean matches(TypeDescription target) {
								return target.getCanonicalName().startsWith(startsWith);
							}
						})
						
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
		public static Object intercept(@AllArguments Object[] args, @Origin Method method, @SuperCall Callable<?> superCallable) throws Exception {
			if (method.getName().equals("toString") || method.getName().equals("equals")  || method.getName().equals("hashCode") || !Modifier.isPublic(method.getModifiers())) {
				return superCallable.call();
			}
			
			return new KryoDiskCache(rootDir, method, args, superCallable).call();
		}
	}

}
