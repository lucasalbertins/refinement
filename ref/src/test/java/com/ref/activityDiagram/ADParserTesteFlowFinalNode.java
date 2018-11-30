package com.ref.activityDiagram;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IActivityDiagram;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.project.ModelFinder;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.ref.parser.activityDiagram.ADParser;

public class ADParserTesteFlowFinalNode {

	public static IActivityDiagram ad;
	private static ADParser parser1;
	
	@BeforeClass
	public static void GetDiagram() throws Exception {
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/flowFinal1.asta");
			INamedElement[] findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser1 = new ADParser(ad.getActivity(), ad.getName());
			
		} catch (ProjectNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private static INamedElement[] findElements(ProjectAccessor projectAccessor) throws ProjectNotFoundException {
		INamedElement[] foundElements = projectAccessor.findElements(new ModelFinder() {
			public boolean isTarget(INamedElement namedElement) {
				return namedElement instanceof IActivityDiagram;
			}
		});
		return foundElements;
	}
	
	@Before
	public void clearBuffer() {
		parser1.clearBuffer();
	}
	
	@AfterClass
	public static void CloseProject() throws Exception {
		AstahAPI.getAstahAPI().getProjectAccessor().close();
	}
	
	/*
	 * Teste de Tradução Flow Final Node
	 * */
	@Test
	public void TestNodesFlowFinal1() {
		String actual = parser1.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("init1_flowFinal1_t = update_flowFinal1.1!(1-0) -> ((ce_flowFinal1.1 -> SKIP))\n" + 
				"act1_flowFinal1 = ce_flowFinal1.1 -> lock_act1_flowFinal1.lock -> event_act1_flowFinal1 -> lock_act1_flowFinal1.unlock -> update_flowFinal1.2!(1-1) -> ce_flowFinal1.2 -> act1_flowFinal1\n" + 
				"act1_flowFinal1_t = act1_flowFinal1 /\\ END_DIAGRAM_flowFinal1\n" + 
				"flowFinal1_flowFinal1 = ((ce_flowFinal1.2 -> SKIP)); update_flowFinal1.3!(0-1) -> SKIP\n" + 
				"flowFinal1_flowFinal1_t = flowFinal1_flowFinal1 /\\ END_DIAGRAM_flowFinal1\n" + 
				"init_flowFinal1_t = (init1_flowFinal1_t) /\\ END_DIAGRAM_flowFinal1");
		
		assertEquals(expected.toString(), actual);
	}
	
}
