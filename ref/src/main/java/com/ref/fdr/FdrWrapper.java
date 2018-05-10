package com.ref.fdr;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class FdrWrapper {

	private URLClassLoader urlCl;

	private Class fdrClass;

	private Class<?> sessionClass;

	private Class<?> assertionClass;

	private Class<?> counterexampleClass;

	private Class<?> traceCounterexampleClass;

	private Class<?> debugContextClass;

	private Class<?> refinementCounterexampleClass;

	private Class<?> behaviourClass;

	private Class<?> irrelevantBehaviourClass;

	private Class<?> compiledEventListClass;

	private Class<?> TraceBehaviour;

	private Class<?> Node;

	private Class<?> ProcessName;

	private Class<?> Canceller;

	private File fdrJar;

	private List<String> classes;

	public boolean loadFDR(String path) {

		File file = new File(path);

		// System.load("C:\\Program
		// Files\\FDR\\bin\\libboost_date_time-mgw53-mt-1_60.dll");
		// System.load("C:\\Program
		// Files\\FDR\\bin\\libboost_filesystem-mgw53-mt-1_60.dll");
		// System.load("C:\\Program
		// Files\\FDR\\bin\\libboost_iostreams-mgw53-mt-1_60.dll");
		// System.load("C:\\Program
		// Files\\FDR\\bin\\libboost_program_options-mgw53-mt-1_60.dll");
		// System.load("C:\\Program
		// Files\\FDR\\bin\\libboost_serialization-mgw53-mt-1_60.dll");
		// System.load("C:\\Program
		// Files\\FDR\\bin\\libboost_system-mgw53-mt-1_60.dll");
		// System.load("C:\\Program Files\\FDR\\bin\\libcsp_operators.dll");
		// System.load("C:\\Program
		// Files\\FDR\\bin\\libcspm_process_compiler.dll");
		// System.load("C:\\Program Files\\FDR\\bin\\libfdr.dll");
		// System.load("C:\\Program Files\\FDR\\bin\\libfdr_java.dll");
		// System.load("C:\\Program Files\\FDR\\bin\\libgcc_s_seh-1.dll");
		// System.load("C:\\Program Files\\FDR\\bin\\libprocess_compiler.dll");
		// System.load("C:\\Program Files\\FDR\\bin\\librefines.dll");
		// System.load("C:\\Program Files\\FDR\\bin\\librefines_gui.dll");
		// System.load("C:\\Program Files\\FDR\\bin\\librefines_licensing.dll");
		// System.load("C:\\Program Files\\FDR\\bin\\libssp-0.dll");
		// System.load("C:\\Program Files\\FDR\\bin\\libstdc++-6.dll");
		// System.load("C:\\Program Files\\FDR\\bin\\libwinpthread-1.dll");
		// System.load("C:\\Program Files\\FDR\\bin\\qt5core.dll");
		// System.load("C:\\Program Files\\FDR\\bin\\qt5gui.dll");
		// System.load("C:\\Program Files\\FDR\\bin\\qt5widgets.dll");
		// System.load("C:\\Program Files\\FDR\\bin\\qt5winextras.dll");
		// System.load("C:\\Program Files\\FDR\\bin\\winsparkle.dll");

		if (file.exists()) {

			fdrJar = file;

			return true;

		} else

			return false;

	}

	public void loadClasses() throws MalformedURLException, ClassNotFoundException {

		classes = new ArrayList<String>();

		urlCl = new URLClassLoader(new URL[] { fdrJar.toURI().toURL() }, System.class.getClassLoader());

		fdrClass = urlCl.loadClass("uk.ac.ox.cs.fdr.fdr");

		sessionClass = urlCl.loadClass("uk.ac.ox.cs.fdr.Session");

		assertionClass = urlCl.loadClass("uk.ac.ox.cs.fdr.Assertion");

		counterexampleClass = urlCl.loadClass("uk.ac.ox.cs.fdr.Counterexample");

		traceCounterexampleClass = urlCl.loadClass("uk.ac.ox.cs.fdr.TraceCounterexample");

		debugContextClass = urlCl.loadClass("uk.ac.ox.cs.fdr.DebugContext");

		refinementCounterexampleClass = urlCl.loadClass("uk.ac.ox.cs.fdr.RefinementCounterexample");

		behaviourClass = urlCl.loadClass("uk.ac.ox.cs.fdr.Behaviour");

		irrelevantBehaviourClass = urlCl.loadClass("uk.ac.ox.cs.fdr.IrrelevantBehaviour");

		compiledEventListClass = urlCl.loadClass("uk.ac.ox.cs.fdr.CompiledEventList");

		TraceBehaviour = urlCl.loadClass("uk.ac.ox.cs.fdr.TraceBehaviour");

		Node = urlCl.loadClass("uk.ac.ox.cs.fdr.Node");

		ProcessName = urlCl.loadClass("uk.ac.ox.cs.fdr.ProcessName");

		classes.add(fdrClass.getName());

		classes.add(sessionClass.getName());

		classes.add(assertionClass.getName());

		classes.add(counterexampleClass.getName());

		classes.add(traceCounterexampleClass.getName());

		classes.add(debugContextClass.getName());

		classes.add(refinementCounterexampleClass.getName());

		classes.add(behaviourClass.getName());

		classes.add(TraceBehaviour.getName());

		classes.add(Node.getName());

		classes.add(ProcessName.getName());

		// Classes extras que são usadas como parametro

		Canceller = urlCl.loadClass("uk.ac.ox.cs.fdr.Canceller");

		classes.add(Canceller.getName());

	}

	public List<String> getClasses() {

		return classes;

	}

	public List<String> verify(String filename, int assertNum) throws Exception {

		List<String> result = null;
		int iteration = 0;
		try {

			Object session = sessionClass.newInstance();

			invokeProperty(session.getClass(), session, "loadFile", String.class, filename);

			for (Object assertion : (Iterable<?>) invokeProperty(session.getClass(), session, "assertions", null,null)) {

				if(iteration == assertNum){ //verifica qual asserção vai rodar
					invokeProperty(assertion.getClass(), assertion, "execute", Canceller, null);
					
					for (Object counterExample : (Iterable<?>) invokeProperty(assertion.getClass(), assertion,"counterexamples", null, null)) {
						
						result = describeCounterExample(session, counterExample);
					}					
				}
				iteration++;
			}
			
		} catch (NullPointerException e) {
			return null;
		}catch(Exception e){
			e.printStackTrace();
		}

		return result;
	}

	public List<String> describeCounterExample(Object session, Object counterExample) throws Exception {

		StringBuilder sb = new StringBuilder();

		List<String> result = new ArrayList<String>();

		if (counterExample.getClass().getName().equals(traceCounterexampleClass.getName())) {

			Field IMPL_LOOKUP = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");

			IMPL_LOOKUP.setAccessible(true);

			MethodHandles.Lookup lkp = (MethodHandles.Lookup) IMPL_LOOKUP.get(null);

			MethodHandle h1 = lkp.findSpecial(refinementCounterexampleClass, "specificationBehaviour",
					MethodType.methodType(behaviourClass), traceCounterexampleClass);

			Object behaviour = null;
			
			try {
				behaviour = h1.invoke(counterExample);
				MethodHandle h2 = lkp.findSpecial(behaviourClass, "trace",MethodType.methodType(compiledEventListClass), irrelevantBehaviourClass);
				for (Long event : (Iterable<Long>) h2.invoke(behaviour)) {
					if(event == 1 || event == 0){
						sb.append("-, ");
					}else{
						sb.append(invokeProperty(sessionClass,session, "uncompileEvent", long.class, event).toString() + ", ");						
					}
//					System.out.println(invokeProperty(sessionClass,session, "uncompileEvent", long.class, event).toString() + ", ");
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}

			Object error = invokeProperty(traceCounterexampleClass, counterExample, "errorEvent", null, null);
			
			if((Long)error == 1 || (Long)error == 0){
				
			}else{
				result.add(invokeProperty(sessionClass, session, "uncompileEvent", long.class,(Long)error).toString());
				result.add(sb.toString());				
			}
			
		}

		Constructor[] constructors = debugContextClass.getConstructors();
		// System.out.println(parameters[0].getName());
		Constructor constructor = null;
		for (int i = 0; i < constructors.length; i++) {
			Class[] parameters = constructors[i].getParameterTypes();
			if (parameters[0].getName().equals(refinementCounterexampleClass.getName())) {
				constructor = constructors[i];
			}
		}

		Object debugContext = constructor.newInstance(counterExample, true);
		invokeProperty(debugContextClass, debugContext, "initialise", Canceller, null);
		for (Object behaviour : (Iterable<?>) invokeProperty(debugContextClass, debugContext, "rootBehaviours", null,
				null)) {
			result.add(describeBehaviour(session, behaviour));
			break;
		}

		return result;

	}

	private String describeBehaviour(Object session, Object behaviour) throws Exception {

		StringBuilder sb = new StringBuilder();

		for (Long event : (Iterable<Long>) invokeProperty(behaviourClass, behaviour, "trace", null, null)) {
			
			if(event == 1 || event == 0){
				sb.append("-, ");
			}else{
				Object result = invokeProperty(sessionClass, session, "uncompileEvent", long.class, event);
				System.out.println(result.toString());
				sb.append(result.toString() + ", ");				
			}
		}

		return sb.toString();
	}

	private static Object invokeProperty(Class<?> dsClass, Object ds, String propertyName, Class<?> paramClass,

			Object paramValue) throws Exception {

		Method method;

		try {

			if (paramClass != null) {
				method = dsClass.getMethod(propertyName, paramClass);
				method.setAccessible(true);
				return method.invoke(ds, paramValue);

			} else {

				method = dsClass.getMethod(propertyName);
				method.setAccessible(true);
				return method.invoke(ds);

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}

	}

}
