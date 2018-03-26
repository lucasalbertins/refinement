package com.ref;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ref.fdr.FdrWrapper;

public class ReflectionTest {

	@Test
	public void loadJar() {
		FdrWrapper wrapper = new FdrWrapper();
		boolean actual = wrapper.loadFDR("C:\\Program Files\\FDR\\bin\\fdr.jar");
		assertEquals(true, actual);
	}

	@Test
	public void loadClasses(){
		FdrWrapper wrapper = new FdrWrapper();
		wrapper.loadFDR("C:\\Program Files\\FDR\\bin\\fdr.jar");
		List<String> classes = new ArrayList<String>();
		classes.add("uk.ac.ox.cs.fdr.fdr");
		classes.add("uk.ac.ox.cs.fdr.Session");
		classes.add("uk.ac.ox.cs.fdr.Assertion");
		classes.add("uk.ac.ox.cs.fdr.Counterexample");
		classes.add("uk.ac.ox.cs.fdr.TraceCounterexample");
		classes.add("uk.ac.ox.cs.fdr.DebugContext");
		classes.add("uk.ac.ox.cs.fdr.RefinementCounterexample");
		classes.add("uk.ac.ox.cs.fdr.Behaviour");
		classes.add("uk.ac.ox.cs.fdr.TraceBehaviour");
		classes.add("uk.ac.ox.cs.fdr.Node");
		classes.add("uk.ac.ox.cs.fdr.ProcessName");
		try {
			wrapper.loadClasses();
			List<String> actual = wrapper.getClasses();
			for(String classe : actual){
				assertEquals(true, classes.contains(classe));
			}			
		} catch (MalformedURLException e) {
			fail(e.getMessage());
		} catch (ClassNotFoundException e) {
			fail(e.getMessage());
		}
	}
}
