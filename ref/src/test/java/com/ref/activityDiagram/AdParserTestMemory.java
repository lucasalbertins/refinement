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

public class AdParserTestMemory {

	public static IActivityDiagram ad;
	private static ADParser parser1;
	private static ADParser parser2;
	private static ADParser parser3;
	
	@BeforeClass
	public static void GetDiagram() throws Exception {
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/action3.asta");
			INamedElement[] findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser1 = new ADParser(ad.getActivity(), ad.getName());
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/action4.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser2 = new ADParser(ad.getActivity(), ad.getName());
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/decision3.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser3 = new ADParser(ad.getActivity(), ad.getName());
			
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
	 * Teste de Tradução Memory
	 * */
	@Test
	public void TestMemoryNode1() {
		parser1.defineNodesActionAndControl();
		parser1.defineChannels();
		String actual = parser1.defineMemorys();
		StringBuffer expected = new StringBuffer();
		expected.append("Mem_act1_action3_x(x) = get_x_act1_action3?c!x -> Mem_act1_action3_x(x) [] set_x_act1_action3?c?x -> Mem_act1_action3_x(x)\n" + 
				"Mem_act1_action3_x_t(x) = Mem_act1_action3_x(x) /\\ END_DIAGRAM_action3\n" + 
				"Mem_act2_action3_z(z) = get_z_act2_action3?c!z -> Mem_act2_action3_z(z) [] set_z_act2_action3?c?z -> Mem_act2_action3_z(z)\n" + 
				"Mem_act2_action3_z_t(z) = Mem_act2_action3_z(z) /\\ END_DIAGRAM_action3\n" + 
				"Mem_act3_action3_w(w) = get_w_act3_action3?c!w -> Mem_act3_action3_w(w) [] set_w_act3_action3?c?w -> Mem_act3_action3_w(w)\n" + 
				"Mem_act3_action3_w_t(w) = Mem_act3_action3_w(w) /\\ END_DIAGRAM_action3\n" + 
				"Mem_action3_x(x) = get_x_action3?c!x -> Mem_action3_x(x) [] set_x_action3?c?x -> Mem_action3_x(x)\n" + 
				"Mem_action3_x_t(x) = Mem_action3_x(x) /\\ (endActivity_action3?x -> SKIP)\n" + 
				"Mem_action3 = Mem_action3_x_t(0)");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução Memory
	 * */
	@Test
	public void TestMemoryNode2() {
		parser2.defineNodesActionAndControl();
		parser2.defineChannels();
		String actual = parser2.defineMemorys();
		StringBuffer expected = new StringBuffer();
		expected.append("Mem_act1_action4_x(x) = get_x_act1_action4?c!x -> Mem_act1_action4_x(x) [] set_x_act1_action4?c?x -> Mem_act1_action4_x(x)\n" + 
				"Mem_act1_action4_x_t(x) = Mem_act1_action4_x(x) /\\ END_DIAGRAM_action4\n" + 
				"Mem_act1_action4_y(y) = get_y_act1_action4?c!y -> Mem_act1_action4_y(y) [] set_y_act1_action4?c?y -> Mem_act1_action4_y(y)\n" + 
				"Mem_act1_action4_y_t(y) = Mem_act1_action4_y(y) /\\ END_DIAGRAM_action4\n" + 
				"Mem_act2_action4_w(w) = get_w_act2_action4?c!w -> Mem_act2_action4_w(w) [] set_w_act2_action4?c?w -> Mem_act2_action4_w(w)\n" + 
				"Mem_act2_action4_w_t(w) = Mem_act2_action4_w(w) /\\ END_DIAGRAM_action4\n" + 
				"Mem_act3_action4_w(w) = get_w_act3_action4?c!w -> Mem_act3_action4_w(w) [] set_w_act3_action4?c?w -> Mem_act3_action4_w(w)\n" + 
				"Mem_act3_action4_w_t(w) = Mem_act3_action4_w(w) /\\ END_DIAGRAM_action4\n" + 
				"Mem_action4_x(x) = get_x_action4?c!x -> Mem_action4_x(x) [] set_x_action4?c?x -> Mem_action4_x(x)\n" + 
				"Mem_action4_x_t(x) = Mem_action4_x(x) /\\ (endActivity_action4?x -> SKIP)\n" + 
				"Mem_action4_y(y) = get_y_action4?c!y -> Mem_action4_y(y) [] set_y_action4?c?y -> Mem_action4_y(y)\n" + 
				"Mem_action4_y_t(y) = Mem_action4_y(y) /\\ (endActivity_action4?y -> SKIP)\n" + 
				"Mem_action4 = (Mem_action4_x_t(0) [|{|endActivity_action4|}|] Mem_action4_y_t(0))");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução Memory
	 * */
	@Test
	public void TestMemoryNode3() {
		parser3.defineNodesActionAndControl();
		parser3.defineChannels();
		String actual = parser3.defineMemorys();
		StringBuffer expected = new StringBuffer();
		expected.append("Mem_act1_decision3_z(z) = get_z_act1_decision3?c!z -> Mem_act1_decision3_z(z) [] set_z_act1_decision3?c?z -> Mem_act1_decision3_z(z)\n" + 
				"Mem_act1_decision3_z_t(z) = Mem_act1_decision3_z(z) /\\ END_DIAGRAM_decision3\n" + 
				"Mem_act2_decision3_z(z) = get_z_act2_decision3?c!z -> Mem_act2_decision3_z(z) [] set_z_act2_decision3?c?z -> Mem_act2_decision3_z(z)\n" + 
				"Mem_act2_decision3_z_t(z) = Mem_act2_decision3_z(z) /\\ END_DIAGRAM_decision3\n" + 
				"Mem_decision3_z(z) = get_z_decision3?c!z -> Mem_decision3_z(z) [] set_z_decision3?c?z -> Mem_decision3_z(z)\n" + 
				"Mem_decision3_z_t(z) = Mem_decision3_z(z) /\\ (endActivity_decision3?z -> SKIP)\n" + 
				"Mem_decision3 = Mem_decision3_z_t(0)");
		
		assertEquals(expected.toString(), actual);
	}

}
