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

public class ADParserTestProcessSync {

	public static IActivityDiagram ad;
	private static ADParser parser1;
	private static ADParser parser2;
	private static ADParser parser3;
	
	@BeforeClass
	public static void GetDiagram() throws Exception {
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/initial2.asta");
			INamedElement[] findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser1 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/decision1.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser2 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/action1.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser3 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
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
	 * Teste de Tradução dos tipos de elementos
	 * */
	@Test
	public void TestNodesProcessSync1() {
		parser1.defineNodesActionAndControl();
		String actual = parser1.defineProcessSync();
		StringBuffer expected = new StringBuffer();
		expected.append("Node_initial2 = (((init_initial2_t [{|update_initial2.1,ce_initial2.1,update_initial2.2,ce_initial2.3,endDiagram_initial2|}||{|ce_initial2.1,event_act1_initial2,ce_initial2.2,endDiagram_initial2|}] act1_initial2_t) [{|update_initial2.1,ce_initial2.1,update_initial2.2,ce_initial2.3,endDiagram_initial2,event_act1_initial2,ce_initial2.2|}||{|ce_initial2.2,ce_initial2.4,clear_initial2.1,endDiagram_initial2|}] fin1_initial2_t) [{|update_initial2.1,ce_initial2.1,update_initial2.2,ce_initial2.3,endDiagram_initial2,event_act1_initial2,ce_initial2.2,ce_initial2.4,clear_initial2.1|}||{|ce_initial2.3,event_act2_initial2,ce_initial2.4,endDiagram_initial2|}] act2_initial2_t)\n");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução dos tipos de elementos
	 * */
	@Test
	public void TestNodesProcessSync2() {
		parser2.defineNodesActionAndControl();
		String actual = parser2.defineProcessSync();
		StringBuffer expected = new StringBuffer();
		expected.append("Node_decision1 = ((((dec1_decision1_t [{|ce_decision1.1,oe_x_decision1.1,update_decision1.3,dc,ce_decision1.2,ce_decision1.3,endDiagram_decision1|}||{|update_decision1.1,ce_decision1.1,update_decision1.2,get_x_decision1.1,oe_x_decision1.1,endDiagram_decision1|}] init_decision1_t) [{|ce_decision1.1,oe_x_decision1.1,update_decision1.3,dc,ce_decision1.2,ce_decision1.3,endDiagram_decision1,update_decision1.1,update_decision1.2,get_x_decision1.1|}||{|ce_decision1.2,event_act1_decision1,ce_decision1.4,endDiagram_decision1|}] act1_decision1_t) [{|ce_decision1.1,oe_x_decision1.1,update_decision1.3,dc,ce_decision1.2,ce_decision1.3,endDiagram_decision1,update_decision1.1,update_decision1.2,get_x_decision1.1,event_act1_decision1,ce_decision1.4|}||{|ce_decision1.5,ce_decision1.4,clear_decision1.1,endDiagram_decision1|}] fin1_decision1_t) [{|ce_decision1.1,oe_x_decision1.1,update_decision1.3,dc,ce_decision1.2,ce_decision1.3,endDiagram_decision1,update_decision1.1,update_decision1.2,get_x_decision1.1,event_act1_decision1,ce_decision1.4,ce_decision1.5,clear_decision1.1|}||{|ce_decision1.3,event_act2_decision1,ce_decision1.5,endDiagram_decision1|}] act2_decision1_t)\n");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução dos tipos de elementos
	 * */
	@Test
	public void TestNodesProcessSync3() {
		parser3.defineNodesActionAndControl();
		String actual = parser3.defineProcessSync();
		StringBuffer expected = new StringBuffer();
		expected.append("Node_action1 = ((init_action1_t [{|update_action1.1,ce_action1.1,endDiagram_action1|}||{|ce_action1.1,event_act1_action1,ce_action1.2,endDiagram_action1|}] act1_action1_t) [{|update_action1.1,ce_action1.1,endDiagram_action1,event_act1_action1,ce_action1.2|}||{|ce_action1.2,clear_action1.1,endDiagram_action1|}] fin1_action1_t)\n");
		
		assertEquals(expected.toString(), actual);
	}
	
	
}
