package com.ref;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.ref.fdr.FdrWrapper;
import com.ref.fdr.FdrWrapper2;

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
		classes.add("uk.ac.ox.cs.fdr.CompiledEventList");
		classes.add("uk.ac.ox.cs.fdr.IrrelevantBehaviour");
		classes.add("uk.ac.ox.cs.fdr.TraceCounterexample");
		classes.add("uk.ac.ox.cs.fdr.DebugContext");
		classes.add("uk.ac.ox.cs.fdr.RefinementCounterexample");
		classes.add("uk.ac.ox.cs.fdr.Behaviour");
		classes.add("uk.ac.ox.cs.fdr.TraceBehaviour");
		classes.add("uk.ac.ox.cs.fdr.Node");
		classes.add("uk.ac.ox.cs.fdr.ProcessName");
		classes.add("uk.ac.ox.cs.fdr.Canceller");
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
	
	@Test
	public void refinementTest1(){
		FdrWrapper wrapper = new FdrWrapper();
		wrapper.loadFDR("C:\\Program Files\\FDR\\bin\\fdr.jar");
		try {
			wrapper.loadClasses();
			List<String> result = wrapper.verify();
			assertEquals("B_mOP.s.lf1id.lf2id.m0_I", result.get(0));
			assertEquals("beginInteraction, B_mOP.s.lf1id.lf2id.m0_I, B_mOP.r.lf1id.lf2id.m0_I, B_mOP.s.lf2id.lf1id.m0_O, B_mOP.r.lf2id.lf1id.m0_O, ",
					result.get(1));//Especificação
			assertEquals("beginInteraction, B_mOP.s.lf1id.lf2id.m0_I, B_mOP.r.lf1id.lf2id.m0_I, B_mOP.s.lf2id.lf1id.m0_O, B_mOP.r.lf2id.lf1id.m0_O, ",
					result.get(2));
		} catch (MalformedURLException e) {
			fail(e.getMessage());
		} catch (ClassNotFoundException e) {
			fail(e.getMessage());
		} catch (Throwable e) {
			fail(e.getMessage());
		}
	}
	
	//Testes no FdrWrapper2
	
	@Ignore
	@Test
	public void refinementTest2(){
		FdrWrapper2 wrapper = new FdrWrapper2();
		try {
			List<String> result = wrapper.fdrRefinement("result.csp");
			assertEquals("beginInteraction, B_mOP.s.lf1id.lf2id.m0_I, B_mOP.r.lf1id.lf2id.m0_I, B_mOP.s.lf2id.lf1id.m0_O, B_mOP.r.lf2id.lf1id.m0_O, ",
					result.get(0));//Especificação
			assertEquals("beginInteraction, B_mOP.s.lf1id.lf2id.m0_I, B_mOP.r.lf1id.lf2id.m0_I, B_mOP.s.lf2id.lf1id.m0_O, B_mOP.r.lf2id.lf1id.m0_O, ",
					result.get(1));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
}
