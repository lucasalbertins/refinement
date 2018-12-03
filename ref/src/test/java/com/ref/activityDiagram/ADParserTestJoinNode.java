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

public class ADParserTestJoinNode {

	public static IActivityDiagram ad;
	private static ADParser parser1;
	private static ADParser parser2;
	
	@BeforeClass
	public static void GetDiagram() throws Exception {
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/join1.asta");
			INamedElement[] findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser1 = new ADParser(ad.getActivity(), ad.getName());
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/join2.asta");
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
	 * Teste de Tradução Join Node
	 * */
	@Test
	public void TestNodesJoin1() {
		String actual = parser1.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("init1_join1_t = update_join1.1!(2-0) -> ((ce_join1.1 -> SKIP) ||| (ce_join1.2 -> SKIP))\n" + 
				"act1_join1 = ce_join1.1 -> lock_act1_join1.lock -> event_act1_join1 -> lock_act1_join1.unlock -> update_join1.2!(1-1) -> ((ce_join1.3 -> SKIP)); act1_join1\n" + 
				"act1_join1_t = act1_join1 /\\ END_DIAGRAM_join1\n" + 
				"act2_join1 = ce_join1.2 -> lock_act2_join1.lock -> event_act2_join1 -> lock_act2_join1.unlock -> update_join1.3!(1-1) -> ((ce_join1.4 -> SKIP)); act2_join1\n" + 
				"act2_join1_t = act2_join1 /\\ END_DIAGRAM_join1\n" + 
				"join1_join1 = ((ce_join1.3 -> SKIP) ||| (ce_join1.4 -> SKIP)); update_join1.4!(1-2) -> ((ce_join1.5 -> SKIP)); join1_join1\n" + 
				"join1_join1_t = join1_join1 /\\ END_DIAGRAM_join1\n" + 
				"fin1_join1 = ((ce_join1.5 -> SKIP)); clear_join1.1 -> SKIP\n" + 
				"fin1_join1_t = fin1_join1 /\\ END_DIAGRAM_join1\n" + 
				"init_join1_t = (init1_join1_t) /\\ END_DIAGRAM_join1");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução Join Node
	 * */
	@Test
	public void TestNodesJoin2() {
		String actual = parser2.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("parameter_x_t = update_join2.1!(2-0) -> get_x_join2.1?x -> ((oe_x_join2.1!x -> SKIP) ||| (oe_x_join2.2!x -> SKIP))\n" + 
				"act1_join2 = oe_x_join2.1?x -> lock_act1_join2.lock -> event_act1_join2 -> lock_act1_join2.unlock -> update_join2.2!(1-1) -> ((oe_x_join2.3!x -> SKIP)); act1_join2\n" + 
				"act1_join2_t = act1_join2 /\\ END_DIAGRAM_join2\n" + 
				"act2_join2 = oe_x_join2.2?x -> lock_act2_join2.lock -> event_act2_join2 -> lock_act2_join2.unlock -> update_join2.3!(1-1) -> ((oe_x_join2.4!x -> SKIP)); act2_join2\n" + 
				"act2_join2_t = act2_join2 /\\ END_DIAGRAM_join2\n" + 
				"join1_join2(x) = ((oe_x_join2.3?x -> SKIP) ||| (oe_x_join2.4?x -> SKIP)); update_join2.4!(1-2) -> ((oe_x_join2.5!x -> SKIP)); join1_join2(x)\n" + 
				"join1_join2_t = join1_join2(0) /\\ END_DIAGRAM_join2\n" + 
				"fin1_join2 = ((oe_x_join2.5?x -> SKIP)); clear_join2.1 -> SKIP\n" + 
				"fin1_join2_t = fin1_join2 /\\ END_DIAGRAM_join2\n" + 
				"init_join2_t = (parameter_x_t) /\\ END_DIAGRAM_join2");
		
		assertEquals(expected.toString(), actual);
	}

}
