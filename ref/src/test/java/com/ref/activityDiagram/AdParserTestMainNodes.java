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

public class AdParserTestMainNodes {

	public static IActivityDiagram ad;
	private static ADParser parser1;
	private static ADParser parser2;
	private static ADParser parser3;
	private static ADParser parser4;
	
	@BeforeClass
	public static void GetDiagram() throws Exception {
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/action1.asta");
			INamedElement[] findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser1 = new ADParser(ad.getActivity(), ad.getName());
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/action3.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser2 = new ADParser(ad.getActivity(), ad.getName());
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/join3.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser3 = new ADParser(ad.getActivity(), ad.getName());
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/merge3.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser4 = new ADParser(ad.getActivity(), ad.getName());
			
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
		parser4.clearBuffer();
	}
	
	@AfterClass
	public static void CloseProject() throws Exception {
		AstahAPI.getAstahAPI().getProjectAccessor().close();
	}
	
	/*
	 * Teste de Tradução MainNode
	 * */
	@Test
	public void TestMainNodeNode1() {
		parser1.defineNodesActionAndControl();
		parser1.defineChannels();
		String actual = parser1.defineMainNodes();
		StringBuffer expected = new StringBuffer();
		expected.append("MAIN = action1(1); LOOP\n" + 
				"LOOP = loop -> LOOP\n" + 
				"END_DIAGRAM_action1 = endDiagram_action1 -> SKIP\n" + 
				"action1(ID_action1) = ((Internal_action1(ID_action1) [|{|update_action1,clear_action1,endDiagram_action1|}|] TokenManager_action1_t(0,0)) [|{|lock_act1_action1,endDiagram_action1|}|] Lock_action1)\n" + 
				"Internal_action1(ID_action1) = StartActivity_action1(ID_action1); Node_action1; EndActivity_action1(ID_action1)\n" + 
				"StartActivity_action1(ID_action1) = startActivity_action1.ID_action1 -> SKIP\n" + 
				"EndActivity_action1(ID_action1) = endActivity_action1.ID_action1 -> SKIP");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução MainNode
	 * */
	@Test
	public void TestMainNodeNode2() {
		parser2.defineNodesActionAndControl();
		parser2.defineChannels();
		String actual = parser2.defineMainNodes();
		StringBuffer expected = new StringBuffer();
		expected.append("MAIN = action3(1); LOOP\n" + 
				"LOOP = loop -> LOOP\n" + 
				"END_DIAGRAM_action3 = endDiagram_action3 -> SKIP\n" + 
				"action3(ID_action3) = (((Internal_action3(ID_action3) [|{|update_action3,clear_action3,endDiagram_action3|}|] TokenManager_action3_t(0,0)) [|{|lock_act1_action3,lock_act3_action3,lock_act2_action3,endDiagram_action3|}|] Lock_action3) [|{|get_x_action3,set_x_action3,endActivity_action3|}|] Mem_action3)\n" + 
				"Internal_action3(ID_action3) = StartActivity_action3(ID_action3); Node_action3; EndActivity_action3(ID_action3)\n" + 
				"StartActivity_action3(ID_action3) = startActivity_action3.ID_action3?x -> set_x_action3.4!x -> SKIP\n" + 
				"EndActivity_action3(ID_action3) = endActivity_action3.ID_action3 -> SKIP");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução MainNode
	 * */
	@Test
	public void TestMainNodeNode3() {
		parser3.defineNodesActionAndControl();
		parser3.defineChannels();
		String actual = parser3.defineMainNodes();
		StringBuffer expected = new StringBuffer();
		expected.append("MAIN = join3(1); LOOP\n" + 
				"LOOP = loop -> LOOP\n" + 
				"END_DIAGRAM_join3 = endDiagram_join3 -> SKIP\n" + 
				"join3(ID_join3) = (((Internal_join3(ID_join3) [|{|update_join3,clear_join3,endDiagram_join3|}|] TokenManager_join3_t(0,0)) [|{|lock_act1_join3,endDiagram_join3|}|] Lock_join3) [|{|get_x_join3,set_x_join3,endActivity_join3|}|] Mem_join3)\n" + 
				"Internal_join3(ID_join3) = StartActivity_join3(ID_join3); Node_join3; EndActivity_join3(ID_join3)\n" + 
				"StartActivity_join3(ID_join3) = startActivity_join3.ID_join3?x -> set_x_join3.3!x -> SKIP\n" + 
				"EndActivity_join3(ID_join3) = endActivity_join3.ID_join3 -> SKIP");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução MainNode
	 * */
	@Test
	public void TestMainNodeNode4() {
		parser4.defineNodesActionAndControl();
		parser4.defineChannels();
		String actual = parser4.defineMainNodes();
		StringBuffer expected = new StringBuffer();
		expected.append("MAIN = merge3(1); LOOP\n" + 
				"LOOP = loop -> LOOP\n" + 
				"END_DIAGRAM_merge3 = endDiagram_merge3 -> SKIP\n" + 
				"merge3(ID_merge3) = (((Internal_merge3(ID_merge3) [|{|update_merge3,clear_merge3,endDiagram_merge3|}|] TokenManager_merge3_t(0,0)) [|{|lock_act1_merge3,endDiagram_merge3|}|] Lock_merge3) [|{|get_x_merge3,set_x_merge3,get_y_merge3,set_y_merge3,endActivity_merge3|}|] Mem_merge3)\n" + 
				"Internal_merge3(ID_merge3) = StartActivity_merge3(ID_merge3); Node_merge3; EndActivity_merge3(ID_merge3)\n" + 
				"StartActivity_merge3(ID_merge3) = startActivity_merge3.ID_merge3?x?y -> set_x_merge3.4!x -> set_y_merge3.5!y -> SKIP\n" + 
				"EndActivity_merge3(ID_merge3) = endActivity_merge3.ID_merge3 -> SKIP");
		
		assertEquals(expected.toString(), actual);
	}
	
}
