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

public class AdParserTestMergeNode {

	public static IActivityDiagram ad;
	private static ADParser parser1;
	private static ADParser parser2;
	
	@BeforeClass
	public static void GetDiagram() throws Exception {
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/merge1.asta");
			INamedElement[] findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser1 = new ADParser(ad.getActivity(), ad.getName());
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/merge4.asta");
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
	 * Teste de Tradução Merge Node
	 * */
	@Test
	public void TestMergeNode1() {
		String actual = parser1.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("init1_merge1_t = update_merge1.1!(2-0) -> ((ce_merge1.1 -> SKIP) ||| (ce_merge1.2 -> SKIP))\n" + 
				"act1_merge1 = ce_merge1.1 -> lock_act1_merge1.lock -> event_act1_merge1 -> lock_act1_merge1.unlock -> update_merge1.2!(1-1) -> ((ce_merge1.3 -> SKIP)); act1_merge1\n" + 
				"act1_merge1_t = act1_merge1 /\\ END_DIAGRAM_merge1\n" + 
				"act2_merge1 = ce_merge1.2 -> lock_act2_merge1.lock -> event_act2_merge1 -> lock_act2_merge1.unlock -> update_merge1.3!(1-1) -> ((ce_merge1.4 -> SKIP)); act2_merge1\n" + 
				"act2_merge1_t = act2_merge1 /\\ END_DIAGRAM_merge1\n" + 
				"merge1_merge1 = ((ce_merge1.3 -> SKIP) [] (ce_merge1.4 -> SKIP)); update_merge1.4!(1-1) -> ce_merge1.5 -> merge1_merge1\n" + 
				"merge1_merge1_t = merge1_merge1 /\\ END_DIAGRAM_merge1\n" + 
				"fin1_merge1 = ((ce_merge1.5 -> SKIP)); clear_merge1.1 -> SKIP\n" + 
				"fin1_merge1_t = fin1_merge1 /\\ END_DIAGRAM_merge1\n" + 
				"init_merge1_t = (init1_merge1_t) /\\ END_DIAGRAM_merge1");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução Merge Node
	 * */
	@Test
	public void TestMergeNode2() {
		String actual = parser2.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("parameter_x_t = update_merge4.1!(2-0) -> get_x_merge4.1?x -> ((oe_x_merge4.1!x -> SKIP) ||| (oe_x_merge4.2!x -> SKIP))\n" + 
				"act1_merge4 = oe_x_merge4.1?x -> lock_act1_merge4.lock -> event_act1_merge4 -> lock_act1_merge4.unlock -> update_merge4.2!(1-1) -> ((oe_x_merge4.3!x -> SKIP)); act1_merge4\n" + 
				"act1_merge4_t = act1_merge4 /\\ END_DIAGRAM_merge4\n" + 
				"act2_merge4 = oe_x_merge4.2?x -> lock_act2_merge4.lock -> event_act2_merge4 -> lock_act2_merge4.unlock -> update_merge4.3!(1-1) -> ((oe_x_merge4.4!x -> SKIP)); act2_merge4\n" + 
				"act2_merge4_t = act2_merge4 /\\ END_DIAGRAM_merge4\n" + 
				"merge1_merge4(x) = ((oe_x_merge4.3?x -> SKIP) [] (oe_x_merge4.4?x -> SKIP)); update_merge4.4!(1-1) -> oe_x_merge4.5!x -> merge1_merge4(x)\n" + 
				"merge1_merge4_t = merge1_merge4(0) /\\ END_DIAGRAM_merge4\n" + 
				"fin1_merge4 = ((oe_x_merge4.5?x -> SKIP)); clear_merge4.1 -> SKIP\n" + 
				"fin1_merge4_t = fin1_merge4 /\\ END_DIAGRAM_merge4\n" + 
				"init_merge4_t = (parameter_x_t) /\\ END_DIAGRAM_merge4");
		
		assertEquals(expected.toString(), actual);
	}
	
}
