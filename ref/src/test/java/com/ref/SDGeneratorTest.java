package com.ref;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.ILifeline;
import com.change_vision.jude.api.inf.model.IMessage;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.ISequenceDiagram;
import com.change_vision.jude.api.inf.project.ModelFinder;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.ref.fdr.FdrWrapper;
import com.ref.refinement.CounterexampleDescriptor;

public class SDGeneratorTest {

	private static INamedElement[] findSequence;

	@BeforeClass
	public static void setup() {
		try {
			FdrWrapper wrapper = new FdrWrapper();
			CounterexampleDescriptor descript = new CounterexampleDescriptor();
			//String entrada = "beginInteraction, B_mOP.s.lf1id.lf2id.m0_I, B_mOP.r.lf1id.lf2id.m0_I, B_mOP.s.lf2id.lf1id.m0_O, B_mOP.r.lf2id.lf1id.m0_O, B_mOP.s.lf1id.lf2id.m0_I, ";
			wrapper.loadFDR("C:\\Program Files\\FDR\\bin\\fdr.jar");
			wrapper.loadClasses();
			Map<Integer, List<String>> result = wrapper.verify("result.csp");
			List<String> entrada = result.get(0);
			descript.createSD("SDteste.asta", entrada);
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("SDteste.asta");
			findSequence = findSequence(projectAccessor);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static INamedElement[] findSequence(ProjectAccessor projectAccessor) throws ProjectNotFoundException {
		INamedElement[] foundElements = projectAccessor.findElements(new ModelFinder() {
			public boolean isTarget(INamedElement namedElement) {
				return namedElement instanceof ISequenceDiagram;
			}
		});
		return foundElements;
	}

	@Test
	public void diagramNameTest() {
		assertEquals("Sequence Diagram1", ((ISequenceDiagram) findSequence[0]).getName());
	}
	
	@Test
	public void lifelineTest(){
		ILifeline[] lifelines = ((ISequenceDiagram)findSequence[0]).getInteraction().getLifelines();
		assertEquals("t", lifelines[0].getName());
		assertEquals("A", lifelines[0].getBase().getName());	
		assertEquals("u", lifelines[1].getName());
		assertEquals("B", lifelines[1].getBase().getName());
	}
	
	@Test
	public void messageTest(){
		IMessage[] messages = ((ISequenceDiagram)findSequence[0]).getInteraction().getMessages();
		assertEquals("m0", messages[0].getName());
	}
	
}
