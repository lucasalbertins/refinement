package com.ref;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ref.fdr.SDRefinementChecker;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.ref.fdr.FdrWrapper;

import static org.junit.Assert.*;

public class SDRefinementTest {

    @BeforeClass
    public static void setup() {
        try {
            FdrWrapper.getInstance().loadFDR("/usr/local/lib/fdr4/lib/fdr.jar");
            FdrWrapper.getInstance().loadClasses();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void loadClasses() {
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

        List<String> actual = FdrWrapper.getInstance().getClasses();
        for (String classe : actual) {
            assertEquals(true, classes.contains(classe));
        }

    }

    @Test
    public void newRefinementTestWeak() {

        SDRefinementChecker sdchecker = new SDRefinementChecker();
        boolean isRefinement = sdchecker.checkRefinement("result - Copia.csp");
        assertFalse(isRefinement);
        Map<Integer, List<String>> result = sdchecker.describeCounterExample("weak");
        assertEquals(null, result.get(0));
        assertEquals("B_mOP.s.lf1id.lf2id.m0_I", result.get(1).get(0));
        assertEquals(
                "beginInteraction, B_mOP.s.lf1id.lf2id.m0_I, B_mOP.r.lf1id.lf2id.m0_I, B_mOP.s.lf2id.lf1id.m0_O, B_mOP.r.lf2id.lf1id.m0_O, ",
                result.get(1).get(1));
    }

    @Test
    public void newRefinementTestStrict() {
        SDRefinementChecker sdchecker = new SDRefinementChecker();
        boolean isRefinement = sdchecker.checkRefinement("result - Copia.csp");
        assertFalse(isRefinement);
        Map<Integer, List<String>> result = sdchecker.describeCounterExample("strict");
        assertEquals("endInteraction", result.get(0).get(0));
        assertEquals(
                "beginInteraction, B_mOP.s.lf1id.lf2id.m0_I, B_mOP.r.lf1id.lf2id.m0_I, B_mOP.s.lf2id.lf1id.m0_O, B_mOP.r.lf2id.lf1id.m0_O, "
                        + "B_mOP.s.lf1id.lf2id.m0_I, B_mOP.r.lf1id.lf2id.m0_I, A_mSIG.s.lf2id.lf1id.m1, A_mSIG.r.lf2id.lf1id.m1, endInteraction",
                result.get(0).get(1));// Especificação
        assertEquals(
                "beginInteraction, B_mOP.s.lf1id.lf2id.m0_I, B_mOP.r.lf1id.lf2id.m0_I, B_mOP.s.lf2id.lf1id.m0_O, B_mOP.r.lf2id.lf1id.m0_O, τ, endInteraction",
                result.get(0).get(2));
        assertEquals("B_mOP.s.lf1id.lf2id.m0_I", result.get(1).get(0));
        assertEquals(
                "beginInteraction, B_mOP.s.lf1id.lf2id.m0_I, B_mOP.r.lf1id.lf2id.m0_I, B_mOP.s.lf2id.lf1id.m0_O, B_mOP.r.lf2id.lf1id.m0_O, ",
                result.get(1).get(1));
    }

    @Test
    public void refinementSucces(){
        SDRefinementChecker sdchecker = new SDRefinementChecker();
        boolean isRefinement = sdchecker.checkRefinement("resultado2.csp");
        assertTrue(isRefinement);
    }

//	 @Test
//	 public void refinementSucess(){
//		 FdrWrapper wrapper = FdrWrapper.getInstance();
//		 wrapper.loadFDR("/usr/local/lib/fdr4/lib/fdr.jar");
//		 try{
//			 wrapper.loadClasses();
//			 boolean hasCounterExample = wrapper.verify("resultado2.csp","WEAK");
//			 assertEquals(false, hasCounterExample);
//		 }catch (Exception e) {
//			 fail(e.getMessage());
//		 }
//	 }

}
