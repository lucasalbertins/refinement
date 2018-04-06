package com.ref.fdr;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import uk.ac.ox.cs.fdr.Session;

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
	private Class<?> TraceBehaviour;
	private Class<?> Node;
	private Class<?> ProcessName;
	private Class<?> Canceller;
	private File fdrJar;
	private List<String> classes;

	public boolean loadFDR(String path) {
		File file = new File(path);

		if (file.exists()) {
			fdrJar = file;
			return true;
		} else
			return false;

	}

	public void loadClasses() throws MalformedURLException, ClassNotFoundException {
		classes = new ArrayList<String>();

		urlCl = new URLClassLoader(new URL[] { fdrJar.toURL() }, System.class.getClassLoader());
		fdrClass = urlCl.loadClass("uk.ac.ox.cs.fdr.fdr");
		sessionClass = urlCl.loadClass("uk.ac.ox.cs.fdr.Session");
		assertionClass = urlCl.loadClass("uk.ac.ox.cs.fdr.Assertion");
		counterexampleClass = urlCl.loadClass("uk.ac.ox.cs.fdr.Counterexample");
		traceCounterexampleClass = urlCl.loadClass("uk.ac.ox.cs.fdr.TraceCounterexample");
		debugContextClass = urlCl.loadClass("uk.ac.ox.cs.fdr.DebugContext");
		refinementCounterexampleClass = urlCl.loadClass("uk.ac.ox.cs.fdr.RefinementCounterexample");
		behaviourClass = urlCl.loadClass("uk.ac.ox.cs.fdr.Behaviour");
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

	public void verify() {

		try {
			Object session = Class.forName(sessionClass.getName()).newInstance();
			invokeProperty(session.getClass(), session, "loadFile", String.class, "result.csp");
			for (Object assertion : (Iterable<?>) invokeProperty(session.getClass(), session, "assertions", null,
					null)) {
				Object canceller = Class.forName(Canceller.getName()).newInstance();
				invokeProperty(assertion.getClass(), assertion, "execute", canceller.getClass(), null);

				for (Object counterExample : (Iterable<?>) invokeProperty(assertion.getClass(), assertion,
						"counterexamples", null, null)) {
					describeCounterExample(session, counterExample);
				}

				// for (Method metodo : assertion.getClass().getMethods()) {
				// System.out.println("nome do método:" + metodo.getName());
				// Class<?>[] parametros = metodo.getParameterTypes();
				// for (int i = 0; i < parametros.length; i++) {
				// System.out.println("tipo do parametro: " +
				// parametros[i].getName());
				// }
				// }

			}
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<String> describeCounterExample(Object session, Object counterExample) throws Exception {
		List<String> result = new ArrayList<String>();

		if (counterExample.getClass().getName().equals(traceCounterexampleClass.getName())) {
			// Object refinementCounterExample =
			// Class.forName(refinementCounterexampleClass.getName())
			// .cast(counterExample);

			// System.out.println(Class.forName(refinementCounterexampleClass.getName()).newInstance().getClass().getName());

			for (Method metodo : counterExample.getClass().getMethods()) {
				System.out.println("nome do método:" + metodo.getName());
				Class<?>[] parametros = metodo.getParameterTypes();
				for (int i = 0; i < parametros.length; i++) {
					System.out.println("tipo do parametro: " + parametros[i].getName());
				}
			}

			Object behaviour = invokeProperty(traceCounterexampleClass.getClass(), counterExample,
					"specificationBehaviour", null, null);
			// for(Long event :(Iterable<Long>)
			// invokeProperty(behaviourClass.getClass(), behaviour, "trace",
			// null, null)){
			// System.out.println(invokeProperty(sessionClass.getClass(),
			// session, "uncompileEvent", Long.class, event).toString());
			// }
		}

		return null;
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
		} catch (NoSuchMethodException e) {
			throw new Exception("NoSuchMethod");
		} catch (SecurityException e) {
			throw new Exception("SecurityException");
		} catch (IllegalAccessException e) {
			throw new Exception("IllegalAccess");
		} catch (IllegalArgumentException e) {
			throw new Exception("IllegalArgument");
		} catch (InvocationTargetException e) {
			throw new Exception("InvocationTarget");
		}

	}

}
