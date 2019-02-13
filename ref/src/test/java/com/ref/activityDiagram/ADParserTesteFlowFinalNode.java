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
	private static ADParser parser2;
	private static ADParser parser3;
	private static ADParser parser4;
	private static ADParser parser5;
	
	@BeforeClass
	public static void GetDiagram() throws Exception {
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/flowFinal1.asta");
			INamedElement[] findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser1 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/flowFinal2.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser2 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/flowFinal3.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser3 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/flowFinal4.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser4 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/flowFinal5.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser5 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
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
		parser5.clearBuffer();
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
				"act1_flowFinal1 = ((ce_flowFinal1.1 -> SKIP)); lock_act1_flowFinal1.lock -> event_act1_flowFinal1 -> lock_act1_flowFinal1.unlock -> update_flowFinal1.2!(1-1) -> ((ce_flowFinal1.2 -> SKIP)); act1_flowFinal1\n" + 
				"act1_flowFinal1_t = act1_flowFinal1 /\\ END_DIAGRAM_flowFinal1\n" + 
				"flowFinal1_flowFinal1 = ((ce_flowFinal1.2 -> SKIP)); update_flowFinal1.3!(0-1) -> flowFinal1_flowFinal1\n" + 
				"flowFinal1_flowFinal1_t = flowFinal1_flowFinal1 /\\ END_DIAGRAM_flowFinal1\n" + 
				"init_flowFinal1_t = (init1_flowFinal1_t) /\\ END_DIAGRAM_flowFinal1\n");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução Flow Final Node
	 * */
	@Test
	public void TestNodesFlowFinal2() {
		String actual = parser2.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("init1_flowFinal2_t = update_flowFinal2.1!(2-0) -> ((ce_flowFinal2.1 -> SKIP) ||| (ce_flowFinal2.2 -> SKIP))\n" + 
				"act1_flowFinal2 = ((ce_flowFinal2.1 -> SKIP)); lock_act1_flowFinal2.lock -> event_act1_flowFinal2 -> lock_act1_flowFinal2.unlock -> update_flowFinal2.2!(1-1) -> ((ce_flowFinal2.3 -> SKIP)); act1_flowFinal2\n" + 
				"act1_flowFinal2_t = act1_flowFinal2 /\\ END_DIAGRAM_flowFinal2\n" + 
				"act2_flowFinal2 = ((ce_flowFinal2.2 -> SKIP)); lock_act2_flowFinal2.lock -> event_act2_flowFinal2 -> lock_act2_flowFinal2.unlock -> update_flowFinal2.3!(1-1) -> ((ce_flowFinal2.4 -> SKIP)); act2_flowFinal2\n" + 
				"act2_flowFinal2_t = act2_flowFinal2 /\\ END_DIAGRAM_flowFinal2\n" + 
				"flowFinal1_flowFinal2 = ((ce_flowFinal2.3 -> SKIP) [] (ce_flowFinal2.4 -> SKIP)); update_flowFinal2.4!(0-1) -> flowFinal1_flowFinal2\n" + 
				"flowFinal1_flowFinal2_t = flowFinal1_flowFinal2 /\\ END_DIAGRAM_flowFinal2\n" + 
				"init_flowFinal2_t = (init1_flowFinal2_t) /\\ END_DIAGRAM_flowFinal2\n");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução Flow Final Node
	 * */
	@Test
	public void TestNodesFlowFinal3() {
		String actual = parser3.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("parameter_x_t = update_flowFinal3.1!(1-0) -> get_x_flowFinal3.1?x -> ((oe_x_flowFinal3.1!x -> SKIP))\n" + 
				"act1_flowFinal3 = ((oe_x_flowFinal3.1?x -> set_x_act1_flowFinal3.1!x -> SKIP)); lock_act1_flowFinal3.lock -> event_act1_flowFinal3 -> lock_act1_flowFinal3.unlock -> update_flowFinal3.2!(1-1) -> get_x_act1_flowFinal3.2?x -> ((oe_x_flowFinal3.2!(x) -> SKIP)); act1_flowFinal3\n" + 
				"act1_flowFinal3_t = ((act1_flowFinal3 /\\ END_DIAGRAM_flowFinal3) [|{|get_x_act1_flowFinal3,set_x_act1_flowFinal3,endDiagram_flowFinal3|}|] Mem_act1_flowFinal3_x_t(0)) \\{|get_x_act1_flowFinal3,set_x_act1_flowFinal3|}\n" + 
				"flowFinal1_flowFinal3 = ((oe_x_flowFinal3.2?x -> SKIP)); update_flowFinal3.3!(0-1) -> flowFinal1_flowFinal3\n" + 
				"flowFinal1_flowFinal3_t = flowFinal1_flowFinal3 /\\ END_DIAGRAM_flowFinal3\n" + 
				"init_flowFinal3_t = (parameter_x_t) /\\ END_DIAGRAM_flowFinal3\n");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução Flow Final Node
	 * */
	@Test
	public void TestNodesFlowFinal4() {
		String actual = parser4.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("parameter_x_t = update_flowFinal4.1!(1-0) -> get_x_flowFinal4.1?x -> ((oe_x_flowFinal4.1!x -> SKIP))\n" + 
				"act1_flowFinal4 = ((oe_x_flowFinal4.1?x -> set_x_act1_flowFinal4.1!x -> SKIP)); lock_act1_flowFinal4.lock -> event_act1_flowFinal4 -> lock_act1_flowFinal4.unlock -> update_flowFinal4.2!(2-1) -> get_x_act1_flowFinal4.2?x -> ((oe_x_flowFinal4.2!(x) -> SKIP) ||| (oe_x_flowFinal4.3!(x) -> SKIP)); act1_flowFinal4\n" + 
				"act1_flowFinal4_t = ((act1_flowFinal4 /\\ END_DIAGRAM_flowFinal4) [|{|get_x_act1_flowFinal4,set_x_act1_flowFinal4,endDiagram_flowFinal4|}|] Mem_act1_flowFinal4_x_t(0)) \\{|get_x_act1_flowFinal4,set_x_act1_flowFinal4|}\n" + 
				"act2_flowFinal4 = ((oe_x_flowFinal4.2?x -> set_x_act2_flowFinal4.2!x -> SKIP)); lock_act2_flowFinal4.lock -> event_act2_flowFinal4 -> lock_act2_flowFinal4.unlock -> update_flowFinal4.3!(1-1) -> get_x_act2_flowFinal4.3?x -> ((oe_x_flowFinal4.4!(x) -> SKIP)); act2_flowFinal4\n" + 
				"act2_flowFinal4_t = ((act2_flowFinal4 /\\ END_DIAGRAM_flowFinal4) [|{|get_x_act2_flowFinal4,set_x_act2_flowFinal4,endDiagram_flowFinal4|}|] Mem_act2_flowFinal4_x_t(0)) \\{|get_x_act2_flowFinal4,set_x_act2_flowFinal4|}\n" + 
				"act3_flowFinal4 = ((oe_x_flowFinal4.3?x -> set_x_act3_flowFinal4.3!x -> SKIP)); lock_act3_flowFinal4.lock -> event_act3_flowFinal4 -> lock_act3_flowFinal4.unlock -> update_flowFinal4.4!(1-1) -> get_x_act3_flowFinal4.4?x -> ((oe_x_flowFinal4.5!(x) -> SKIP)); act3_flowFinal4\n" + 
				"act3_flowFinal4_t = ((act3_flowFinal4 /\\ END_DIAGRAM_flowFinal4) [|{|get_x_act3_flowFinal4,set_x_act3_flowFinal4,endDiagram_flowFinal4|}|] Mem_act3_flowFinal4_x_t(0)) \\{|get_x_act3_flowFinal4,set_x_act3_flowFinal4|}\n" + 
				"flowFinal1_flowFinal4 = ((oe_x_flowFinal4.4?x -> SKIP) [] (oe_x_flowFinal4.5?x -> SKIP)); update_flowFinal4.5!(0-1) -> flowFinal1_flowFinal4\n" + 
				"flowFinal1_flowFinal4_t = flowFinal1_flowFinal4 /\\ END_DIAGRAM_flowFinal4\n" + 
				"init_flowFinal4_t = (parameter_x_t) /\\ END_DIAGRAM_flowFinal4\n");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução Flow Final Node
	 * */
	@Test
	public void TestNodesFlowFinal5() {
		String actual = parser5.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("parameter_x_t = update_flowFinal5.1!(1-0) -> get_x_flowFinal5.1?x -> ((oe_x_flowFinal5.1!x -> SKIP))\n" + 
				"act2_flowFinal5 = ((oe_x_flowFinal5.1?x -> set_x_act2_flowFinal5.1!x -> SKIP)); lock_act2_flowFinal5.lock -> event_act2_flowFinal5 -> lock_act2_flowFinal5.unlock -> update_flowFinal5.2!(1-1) -> get_x_act2_flowFinal5.2?x -> ((oe_x_flowFinal5.2!(x) -> SKIP)); act2_flowFinal5\n" + 
				"act2_flowFinal5_t = ((act2_flowFinal5 /\\ END_DIAGRAM_flowFinal5) [|{|get_x_act2_flowFinal5,set_x_act2_flowFinal5,endDiagram_flowFinal5|}|] Mem_act2_flowFinal5_x_t(0)) \\{|get_x_act2_flowFinal5,set_x_act2_flowFinal5|}\n" + 
				"init1_flowFinal5_t = update_flowFinal5.3!(1-0) -> ((ce_flowFinal5.1 -> SKIP))\n" + 
				"act1_flowFinal5 = ((ce_flowFinal5.1 -> SKIP)); lock_act1_flowFinal5.lock -> event_act1_flowFinal5 -> lock_act1_flowFinal5.unlock -> update_flowFinal5.4!(1-1) -> ((ce_flowFinal5.2 -> SKIP)); act1_flowFinal5\n" + 
				"act1_flowFinal5_t = act1_flowFinal5 /\\ END_DIAGRAM_flowFinal5\n" + 
				"act3_flowFinal5 = ((ce_flowFinal5.2 -> SKIP)); lock_act3_flowFinal5.lock -> event_act3_flowFinal5 -> lock_act3_flowFinal5.unlock -> update_flowFinal5.5!(1-1) -> ((ce_flowFinal5.3 -> SKIP)); act3_flowFinal5\n" + 
				"act3_flowFinal5_t = act3_flowFinal5 /\\ END_DIAGRAM_flowFinal5\n" + 
				"flowFinal1_flowFinal5 = ((oe_x_flowFinal5.2?x -> SKIP) [] (ce_flowFinal5.3 -> SKIP)); update_flowFinal5.6!(0-1) -> flowFinal1_flowFinal5\n" + 
				"flowFinal1_flowFinal5_t = flowFinal1_flowFinal5 /\\ END_DIAGRAM_flowFinal5\n" + 
				"init_flowFinal5_t = (parameter_x_t ||| init1_flowFinal5_t) /\\ END_DIAGRAM_flowFinal5\n");
		
		assertEquals(expected.toString(), actual);
	}
	
}
