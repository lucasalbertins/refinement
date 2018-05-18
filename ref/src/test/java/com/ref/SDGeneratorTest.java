package com.ref;

import static org.junit.Assert.assertEquals;

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
import com.ref.refinement.CounterexampleDescriptor;

public class SDGeneratorTest {

	private static INamedElement[] findSequence;

	@BeforeClass
	public static void setup() {
		try {
			CounterexampleDescriptor descript = new CounterexampleDescriptor();
			String entrada = "beginInteraction, B_mOP.s.lf1id.lf2id.m0_I, B_mOP.r.lf1id.lf2id.m0_I, B_mOP.s.lf2id.lf1id.m0_O, B_mOP.r.lf2id.lf1id.m0_O, ";
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
		assertEquals("u", lifelines[0].getName());
		assertEquals("B", lifelines[0].getBase().getName());
		assertEquals("t", lifelines[1].getName());
		assertEquals("A", lifelines[1].getBase().getName());
		
	}
	
	@Test
	public void messageTest(){
		IMessage[] messages = ((ISequenceDiagram)findSequence[0]).getInteraction().getMessages();
		assertEquals("m0", messages[0].getName());
	}
	
}
