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

public class ADParserTestInitialNode {

	public static IActivityDiagram ad;
	private static ADParser parser1;
	private static ADParser parser2;
	private static ADParser parser3;
	private static ADParser parser4;
	private static ADParser parser5;
	
	@BeforeClass
	public static void GetDiagram() throws Exception {
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/action1.asta");
			INamedElement[] findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser1 = new ADParser(ad.getActivity(), ad.getName());
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/initial2.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser2 = new ADParser(ad.getActivity(), ad.getName());
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/join1.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser3 = new ADParser(ad.getActivity(), ad.getName());
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/decision1.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser4 = new ADParser(ad.getActivity(), ad.getName());
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/decision3.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser5 = new ADParser(ad.getActivity(), ad.getName());
			
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
		parser3.clearBuffer();
	}
	
	@AfterClass
	public static void CloseProject() throws Exception {
		AstahAPI.getAstahAPI().getProjectAccessor().close();
	}
	
	/*
	 * Teste de Tradução Initial Node
	 * */
	@Test
	public void TestNodesInitial1() {
		String actual = parser1.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("init1_action1_t = update_action1.1!(1-0) -> ((ce_action1.1 -> SKIP))\n" + 
				"act1_action1 = ce_action1.1 -> lock_act1_action1.lock -> event_act1_action1 -> lock_act1_action1.unlock -> update_action1.2!(1-1) -> ce_action1.2 -> act1_action1\n" + 
				"act1_action1_t = act1_action1 /\\ END_DIAGRAM_action1\n" + 
				"fin1_action1 = ((ce_action1.2 -> SKIP)); clear_action1.1 -> SKIP\n" + 
				"fin1_action1_t = fin1_action1 /\\ END_DIAGRAM_action1\n" + 
				"init_action1_t = (init1_action1_t) /\\ END_DIAGRAM_action1");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução Initial Node
	 * */
	@Test
	public void TestNodesInitial2() {
		String actual = parser2.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("init1_initial2_t = update_initial2.1!(1-0) -> ((ce_initial2.1 -> SKIP))\n" + 
				"act1_initial2 = ce_initial2.1 -> lock_act1_initial2.lock -> event_act1_initial2 -> lock_act1_initial2.unlock -> update_initial2.2!(1-1) -> ce_initial2.2 -> act1_initial2\n" + 
				"act1_initial2_t = act1_initial2 /\\ END_DIAGRAM_initial2\n" + 
				"fin1_initial2 = ((ce_initial2.2 -> SKIP)); clear_initial2.1 -> SKIP\n" + 
				"fin1_initial2_t = fin1_initial2 /\\ END_DIAGRAM_initial2\n" + 
				"init2_initial2_t = update_initial2.3!(1-0) -> ((ce_initial2.3 -> SKIP))\n" + 
				"act2_initial2 = ce_initial2.3 -> lock_act2_initial2.lock -> event_act2_initial2 -> lock_act2_initial2.unlock -> update_initial2.4!(1-1) -> ce_initial2.4 -> act2_initial2\n" + 
				"act2_initial2_t = act2_initial2 /\\ END_DIAGRAM_initial2\n" + 
				"init_initial2_t = (init1_initial2_t ||| init2_initial2_t) /\\ END_DIAGRAM_initial2");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução Initial Node
	 * */
	@Test
	public void TestNodesInitial3() {
		String actual = parser3.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("init1_join1_t = update_join1.1!(2-0) -> ((ce_join1.1 -> SKIP) ||| (ce_join1.2 -> SKIP))\n" + 
				"act1_join1 = ce_join1.1 -> lock_act1_join1.lock -> event_act1_join1 -> lock_act1_join1.unlock -> update_join1.2!(1-1) -> ce_join1.3 -> act1_join1\n" + 
				"act1_join1_t = act1_join1 /\\ END_DIAGRAM_join1\n" + 
				"act2_join1 = ce_join1.2 -> lock_act2_join1.lock -> event_act2_join1 -> lock_act2_join1.unlock -> update_join1.3!(1-1) -> ce_join1.4 -> act2_join1\n" + 
				"act2_join1_t = act2_join1 /\\ END_DIAGRAM_join1\n" + 
				"join1_join1 = ((ce_join1.3 -> SKIP) ||| (ce_join1.4 -> SKIP)); update_join1.4!(1-2) -> ce_join1.5 -> join1_join1\n" + 
				"join1_join1_t = join1_join1 /\\ END_DIAGRAM_join1\n" + 
				"fin1_join1 = ((ce_join1.5 -> SKIP)); clear_join1.1 -> SKIP\n" + 
				"fin1_join1_t = fin1_join1 /\\ END_DIAGRAM_join1\n" + 
				"init_join1_t = (init1_join1_t) /\\ END_DIAGRAM_join1");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução Initial Node
	 * */
	@Test
	public void TestNodesInitial4() {
		String actual = parser4.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("init1_decision1_t = update_decision1.1!(1-0) -> ((ce_decision1.1 -> SKIP))\n" + 
				"parameter_x_t = update_decision1.2!(1-0) -> get_x_decision1.1?x -> ((oe_x_decision1.1!x -> SKIP))\n" + 
				"dec1_decision1(x) = ((ce_decision1.1 -> SKIP) ||| (oe_x_decision1.1?x -> SKIP)); update_decision1.3!(1-2) -> (x == 1 & (ce_decision1.2 -> SKIP) [] x == 0 & (ce_decision1.3 -> SKIP)); dec1_decision1(x)\n" + 
				"dec1_decision1_t = dec1_decision1(0) /\\ END_DIAGRAM_decision1\n" + 
				"act1_decision1 = ce_decision1.2 -> lock_act1_decision1.lock -> event_act1_decision1 -> lock_act1_decision1.unlock -> update_decision1.4!(1-1) -> ce_decision1.4 -> act1_decision1\n" + 
				"act1_decision1_t = act1_decision1 /\\ END_DIAGRAM_decision1\n" + 
				"act2_decision1 = ce_decision1.3 -> lock_act2_decision1.lock -> event_act2_decision1 -> lock_act2_decision1.unlock -> update_decision1.5!(1-1) -> ce_decision1.5 -> act2_decision1\n" + 
				"act2_decision1_t = act2_decision1 /\\ END_DIAGRAM_decision1\n" + 
				"fin1_decision1 = ((ce_decision1.5 -> SKIP) [] (ce_decision1.4 -> SKIP)); clear_decision1.1 -> SKIP\n" + 
				"fin1_decision1_t = fin1_decision1 /\\ END_DIAGRAM_decision1\n" + 
				"init_decision1_t = (init1_decision1_t ||| parameter_x_t) /\\ END_DIAGRAM_decision1");
		
		assertEquals(expected.toString(), actual);
	}

	/*
	 * Teste de Tradução Initial Node
	 * */
	@Test
	public void TestNodesInitial5() {
		String actual = parser5.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("parameter_z_t = update_decision3.1!(1-0) -> get_z_decision3.1?z -> ((oe_z_decision3.1!z -> SKIP))\n" + 
				"dec1_decision3 = oe_z_decision3.1?z -> update_decision3.2!(1-1) -> (z > 0 & (oe_z_decision3.2!z -> SKIP) [] z <= 0 & (oe_z_decision3.3!z -> SKIP)); dec1_decision3\n" + 
				"dec1_decision3_t = dec1_decision3 /\\ END_DIAGRAM_decision3\n" + 
				"act1_decision3 = oe_z_decision3.2?z -> lock_act1_decision3.lock -> event_act1_decision3 -> lock_act1_decision3.unlock -> update_decision3.3!(1-1) -> oe_z_decision3.4!z -> act1_decision3\n" + 
				"act1_decision3_t = act1_decision3 /\\ END_DIAGRAM_decision3\n" + 
				"act2_decision3 = oe_z_decision3.3?z -> lock_act2_decision3.lock -> event_act2_decision3 -> lock_act2_decision3.unlock -> update_decision3.4!(1-1) -> oe_z_decision3.5!z -> act2_decision3\n" + 
				"act2_decision3_t = act2_decision3 /\\ END_DIAGRAM_decision3\n" + 
				"fin1_decision3 = ((oe_z_decision3.5?z -> SKIP) [] (oe_z_decision3.4?z -> SKIP)); clear_decision3.1 -> SKIP\n" + 
				"fin1_decision3_t = fin1_decision3 /\\ END_DIAGRAM_decision3\n" + 
				"init_decision3_t = (parameter_z_t) /\\ END_DIAGRAM_decision3");
		
		assertEquals(expected.toString(), actual);
	}
}
