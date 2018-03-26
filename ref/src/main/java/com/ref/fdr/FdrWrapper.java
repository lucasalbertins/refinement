package com.ref.fdr;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class FdrWrapper {

	private URLClassLoader urlCl;
	private Class fdrClass;
	private Class sessionClass;
	private Class assertionClass;
	private Class counterexampleClass;
	private Class traceCounterexampleClass;
	private Class debugContextClass;
	private Class refinementCounterexampleClass;
	private Class behaviourClass;
	private Class TraceBehaviour;
	private Class Node;
	private Class ProcessName;
	private File fdrJar;
	private List<String> classes;
	
	public boolean loadFDR(String path) {
		File file = new File(path);
		
		if(file.exists()){
			fdrJar = file;
			return true;
		}else
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
		
	}
	
	public List<String> getClasses(){
		return classes;
	}

	public void verify() {
		
		try {
			Object session = sessionClass.newInstance();
			invokeProperty(sessionClass, session, "loadFile", String.class, "result.csp");
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

	private static Object invokeProperty(Class dsClass, Object ds, String propertyName, Class paramClass,
			Object paramValue) throws Exception {
		try {
			Method method;
			if (paramClass != null) {
				method = dsClass.getDeclaredMethod(propertyName, paramClass);
				method.setAccessible(true);
				return method.invoke(ds, paramValue);
			} else {
				method = dsClass.getDeclaredMethod(propertyName);
				method.setAccessible(true);
				return method.invoke(ds);
			}

		} catch (Exception e) {
			throw new Exception("Failed to invoke method");
		}
	}

}
