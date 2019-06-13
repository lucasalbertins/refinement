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
import com.ref.parser.SDParser;
import com.ref.refinement.CounterexampleDescriptor;

public class SDGeneratorTest {

	private static INamedElement[] findSequence;

	@BeforeClass
	public static void setup() {
		try {
			FdrWrapper wrapper = FdrWrapper.getInstance();
			//String entrada = "beginInteraction, B_mOP.s.lf1id.lf2id.m0_I, B_mOP.r.lf1id.lf2id.m0_I, B_mOP.s.lf2id.lf1id.m0_O, B_mOP.r.lf2id.lf1id.m0_O, B_mOP.s.lf1id.lf2id.m0_I, ";
			wrapper.loadFDR("/usr/local/lib/fdr4/lib/fdr.jar");
			wrapper.loadClasses();
			wrapper.verify("result - Copia.csp","STRICT");
			Map<Integer, List<String>> result = wrapper.getCounterExamples();
			List<String> entrada = result.get(0);
			CounterexampleDescriptor descript = loadInfo();
			descript.buildCounterExample("SDteste.asta", entrada,null);
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("SDteste.asta");
			findSequence = findSequence(projectAccessor);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static CounterexampleDescriptor loadInfo() {

		SDParser parser = null;
		CounterexampleDescriptor descript = null;
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/testRef3.asta");
			INamedElement[] findSequence = findSequence(projectAccessor);
			// buildCounterExample(projectAccessor);

			ISequenceDiagram seq1;
			ISequenceDiagram seq2;

			if (findSequence[0].getName().equals("Seq0")) {
				seq1 = (ISequenceDiagram) findSequence[0];
				seq2 = (ISequenceDiagram) findSequence[1];
			} else {
				seq1 = (ISequenceDiagram) findSequence[1];
				seq2 = (ISequenceDiagram) findSequence[0];
			}
			parser = new SDParser(seq1, seq2);
			parser.carregaLifelines();
			descript = new CounterexampleDescriptor(parser.getLifelineMapping());

		} catch (ProjectNotFoundException e) {
			System.out.println("aqui");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return descript;
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
		assertEquals("Sequence Diagram1", findSequence[0].getName());
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
