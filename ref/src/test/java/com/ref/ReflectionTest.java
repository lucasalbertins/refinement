package com.ref;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.ref.fdr.FdrWrapper;

public class ReflectionTest {

	@Test
	public void loadJar() {
		FdrWrapper wrapper = FdrWrapper.getInstance();
		boolean actual = wrapper.loadFDR("C:\\Program Files\\FDR\\bin\\fdr.jar");
		assertEquals(true, actual);
	}

	@Test
	public void loadClasses() {
		FdrWrapper wrapper = FdrWrapper.getInstance();
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
		classes.add("uk.ac.ox.cs.fdr.Machine");
		classes.add("uk.ac.ox.cs.fdr.TransitionList");
		classes.add("uk.ac.ox.cs.fdr.Transition");
		classes.add("uk.ac.ox.cs.fdr.ProcessName");
		classes.add("uk.ac.ox.cs.fdr.Canceller");
		try {
			wrapper.loadClasses();
			List<String> actual = wrapper.getClasses();
			for (String classe : actual) {
				assertEquals(true, classes.contains(classe));
			}
		} catch (MalformedURLException e) {
			fail(e.getMessage());
		} catch (ClassNotFoundException e) {
			fail(e.getMessage());
		}
	}

	@Ignore
	@Test
	public void refinementTestSTRICT() {
		FdrWrapper wrapper = FdrWrapper.getInstance();
		wrapper.loadFDR("C:\\Program Files\\FDR\\bin\\fdr.jar");
		try {
			wrapper.loadClasses();
			boolean hasCounterExample = wrapper.verify("result - Copia.csp","STRICT");
			Map<Integer, List<String>> result = wrapper.getCounterExamples();
			assertEquals(true, hasCounterExample);
			assertEquals("endInteraction", result.get(0).get(0));
			assertEquals(
					"beginInteraction, B_mOP.s.lf1id.lf2id.m0_I, B_mOP.r.lf1id.lf2id.m0_I, B_mOP.s.lf2id.lf1id.m0_O, B_mOP.r.lf2id.lf1id.m0_O, "
					+ "B_mOP.s.lf1id.lf2id.m0_I, B_mOP.r.lf1id.lf2id.m0_I, A_mSIG.s.lf2id.lf1id.m1, A_mSIG.r.lf2id.lf1id.m1, endInteraction",
					result.get(0).get(1));// Especificação
			assertEquals(
					"beginInteraction, B_mOP.s.lf1id.lf2id.m0_I, B_mOP.r.lf1id.lf2id.m0_I, B_mOP.s.lf2id.lf1id.m0_O, B_mOP.r.lf2id.lf1id.m0_O, τ, endInteraction",
					result.get(0).get(2));
			// assertEquals("endInteraction", result.get(1).get(0));
			// System.out.println(result.get(1).get(1));
			assertEquals("B_mOP.s.lf1id.lf2id.m0_I",result.get(1).get(0));
			assertEquals(
					"beginInteraction, B_mOP.s.lf1id.lf2id.m0_I, B_mOP.r.lf1id.lf2id.m0_I, B_mOP.s.lf2id.lf1id.m0_O, B_mOP.r.lf2id.lf1id.m0_O, ",
					result.get(1).get(1));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	 @Ignore
	 @Test
	 public void refinementTestWEAK() {
	 FdrWrapper wrapper = FdrWrapper.getInstance();
	 wrapper.loadFDR("C:\\Program Files\\FDR\\bin\\fdr.jar");
	 try{
		 wrapper.loadClasses();
		 boolean hasCounterExample = wrapper.verify("result - Copia.csp","WEAK");
		 Map<Integer, List<String>> result = wrapper.getCounterExamples();
		 assertEquals(true, hasCounterExample);
		 assertEquals(null, result.get(0));
		 assertEquals("B_mOP.s.lf1id.lf2id.m0_I",result.get(1).get(0));
			assertEquals(
					"beginInteraction, B_mOP.s.lf1id.lf2id.m0_I, B_mOP.r.lf1id.lf2id.m0_I, B_mOP.s.lf2id.lf1id.m0_O, B_mOP.r.lf2id.lf1id.m0_O, ",
					result.get(1).get(1));
		 
	 }catch (Exception e) {
		 fail(e.getMessage());
	 }
	 
	 }

	 @Test
	 public void refinementSucess(){
		 FdrWrapper wrapper = FdrWrapper.getInstance();
		 wrapper.loadFDR("C:\\Program Files\\FDR\\bin\\fdr.jar");
		 try{
			 wrapper.loadClasses();
			 boolean hasCounterExample = wrapper.verify("resultado2.csp","WEAK");
			 assertEquals(false, hasCounterExample);			 
		 }catch (Exception e) {
			 fail(e.getMessage());
		 }
	 }
	 
}
