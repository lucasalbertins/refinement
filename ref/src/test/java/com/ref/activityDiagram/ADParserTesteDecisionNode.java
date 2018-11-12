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

public class ADParserTesteDecisionNode {
	
	public static IActivityDiagram ad;
	private static ADParser parser1;
	
	@BeforeClass
	public static void GetDiagram() throws Exception {
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/decision1.asta");
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
	 * Teste de Tradução dos tipos de elementos
	 * */
	@Test
	public void TestNodesDecision1() {
		String actual = parser1.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("init1_decision1_t = update_decision1.1!(1-0) -> ((cn_decision1.1 -> SKIP))\n" + 
				"dec1_decision1 = cn_decision1.1 -> update_decision1.2!(1-1) -> get_x_decision1.1?x -> (x == 1 & (cn_decision1.2 -> SKIP) [] x == 0 & (cn_decision1.3 -> SKIP)); dec1_decision1\n" + 
				"dec1_decision1_t = dec1_decision1 /\\ END_DIAGRAM_decision1\n" + 
				"act1_decision1 = cn_decision1.2 -> lock_act1_decision1.lock -> event_act1_decision1 -> lock_act1_decision1.unlock -> update_decision1.3!(1-1) -> cn_decision1.4 -> act1_decision1\n" + 
				"act1_decision1_t = act1_decision1 /\\ END_DIAGRAM_decision1\n" + 
				"act2_decision1 = cn_decision1.3 -> lock_act2_decision1.lock -> event_act2_decision1 -> lock_act2_decision1.unlock -> update_decision1.4!(1-1) -> cn_decision1.5 -> act2_decision1\n" + 
				"act2_decision1_t = act2_decision1 /\\ END_DIAGRAM_decision1\n" + 
				"fin1_decision1 = ((cn_decision1.5 -> SKIP) [] (cn_decision1.4 -> SKIP)); clear_decision1.1 -> SKIP\n" + 
				"fin1_decision1_t = fin1_decision1 /\\ END_DIAGRAM_decision1\n" + 
				"init_decision1_t = (init1_decision1_t) /\\ END_DIAGRAM_decision1");
		
		assertEquals(expected.toString(), actual);
	}
	
}
