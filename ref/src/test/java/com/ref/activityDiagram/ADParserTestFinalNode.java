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

public class ADParserTestFinalNode {
	
	public static IActivityDiagram ad;
	private static ADParser parser1;
	private static ADParser parser2;
	
	@BeforeClass
	public static void GetDiagram() throws Exception {
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/action1.asta");
			INamedElement[] findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser1 = new ADParser(ad.getActivity(), ad.getName());
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/action2.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser2 = new ADParser(ad.getActivity(), ad.getName());
			
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
		parser2.clearBuffer();
	}
	
	@AfterClass
	public static void CloseProject() throws Exception {
		AstahAPI.getAstahAPI().getProjectAccessor().close();
	}
	
	/*
	 * Teste de Tradução Final node
	 * */
	@Test
	public void TestNodesFinal1() {
		String actual = parser1.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("init1_action1_t = update_action1.1!(1-0) -> ((cn_action1.1 -> SKIP))\n" + 
				"act1_action1 = cn_action1.1 -> lock_act1_action1.lock -> event_act1_action1 -> lock_act1_action1.unlock -> update_action1.2!(1-1) -> cn_action1.2 -> act1_action1\n" + 
				"act1_action1_t = act1_action1 /\\ END_DIAGRAM_action1\n" + 
				"fin1_action1 = ((cn_action1.2 -> SKIP)); clear_action1.1 -> SKIP\n" + 
				"fin1_action1_t = fin1_action1 /\\ END_DIAGRAM_action1\n" + 
				"init_action1_t = (init1_action1_t) /\\ END_DIAGRAM_action1");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução Final node
	 * */
	@Test
	public void TestNodesFinal2() {
		String actual = parser2.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("init1_action2_t = update_action2.1!(1-0) -> ((cn_action2.1 -> SKIP))\n" + 
				"act1_action2 = cn_action2.1 -> lock_act1_action2.lock -> event_act1_action2 -> lock_act1_action2.unlock -> update_action2.2!(2-1) -> cn_action2.2 -> cn_action2.3 -> act1_action2\n" + 
				"act1_action2_t = act1_action2 /\\ END_DIAGRAM_action2\n" + 
				"act2_action2 = cn_action2.2 -> lock_act2_action2.lock -> event_act2_action2 -> lock_act2_action2.unlock -> update_action2.3!(1-1) -> cn_action2.4 -> act2_action2\n" + 
				"act2_action2_t = act2_action2 /\\ END_DIAGRAM_action2\n" + 
				"act3_action2 = cn_action2.3 -> lock_act3_action2.lock -> event_act3_action2 -> lock_act3_action2.unlock -> update_action2.4!(1-1) -> cn_action2.5 -> act3_action2\n" + 
				"act3_action2_t = act3_action2 /\\ END_DIAGRAM_action2\n" + 
				"fin1_action2 = ((cn_action2.5 -> SKIP) [] (cn_action2.4 -> SKIP)); clear_action2.1 -> SKIP\n" + 
				"fin1_action2_t = fin1_action2 /\\ END_DIAGRAM_action2\n" + 
				"init_action2_t = (init1_action2_t) /\\ END_DIAGRAM_action2");
		
		assertEquals(expected.toString(), actual);
	}
	
}
