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

public class ADParserTestActionNode {
	
	public static IActivityDiagram ad;
	private static ADParser parser1;
	private static ADParser parser2;
	private static ADParser parser3;
	private static ADParser parser4;
	private static ADParser parser5;
	private static ADParser parser6;
	
	@BeforeClass
	public static void GetDiagram() throws Exception {
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/action1.asta");
			INamedElement[] findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser1 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/action2.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser2 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/action3.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser3 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/action4.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser4 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/action5.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser5 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/action6.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser6 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
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
		parser6.clearBuffer();
	}
	
	@AfterClass
	public static void CloseProject() throws Exception {
		AstahAPI.getAstahAPI().getProjectAccessor().close();
	}
	
	/*
	 * Teste de Tradução dos tipos de elementos
	 * */
	@Test
	public void TestNodesAction1() {
		String actual = parser1.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("init1_action1_t = update_action1.1!(1-0) -> ((ce_action1.1 -> SKIP))\n" + 
				"act1_action1 = ((ce_action1.1 -> SKIP)); lock_act1_action1.lock -> event_act1_action1 -> lock_act1_action1.unlock -> update_action1.2!(1-1) -> ((ce_action1.2 -> SKIP)); act1_action1\n" + 
				"act1_action1_t = act1_action1 /\\ END_DIAGRAM_action1\n" + 
				"fin1_action1 = ((ce_action1.2 -> SKIP)); clear_action1.1 -> SKIP\n" + 
				"fin1_action1_t = fin1_action1 /\\ END_DIAGRAM_action1\n" + 
				"init_action1_t = (init1_action1_t) /\\ END_DIAGRAM_action1\n");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução dos tipos de elementos
	 * */
	@Test
	public void TestNodesAction2() {
		String actual = parser2.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("init1_action2_t = update_action2.1!(1-0) -> ((ce_action2.1 -> SKIP))\n" + 
				"act1_action2 = ((ce_action2.1 -> SKIP)); lock_act1_action2.lock -> event_act1_action2 -> lock_act1_action2.unlock -> update_action2.2!(2-1) -> ((ce_action2.2 -> SKIP) ||| (ce_action2.3 -> SKIP)); act1_action2\n" + 
				"act1_action2_t = act1_action2 /\\ END_DIAGRAM_action2\n" + 
				"act2_action2 = ((ce_action2.2 -> SKIP)); lock_act2_action2.lock -> event_act2_action2 -> lock_act2_action2.unlock -> update_action2.3!(1-1) -> ((ce_action2.4 -> SKIP)); act2_action2\n" + 
				"act2_action2_t = act2_action2 /\\ END_DIAGRAM_action2\n" + 
				"act3_action2 = ((ce_action2.3 -> SKIP)); lock_act3_action2.lock -> event_act3_action2 -> lock_act3_action2.unlock -> update_action2.4!(1-1) -> ((ce_action2.5 -> SKIP)); act3_action2\n" + 
				"act3_action2_t = act3_action2 /\\ END_DIAGRAM_action2\n" + 
				"fin1_action2 = ((ce_action2.4 -> SKIP) [] (ce_action2.5 -> SKIP)); clear_action2.1 -> SKIP\n" + 
				"fin1_action2_t = fin1_action2 /\\ END_DIAGRAM_action2\n" + 
				"init_action2_t = (init1_action2_t) /\\ END_DIAGRAM_action2\n");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução dos tipos de elementos
	 * */
	@Test
	public void TestNodesAction3() {
		String actual = parser3.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("init1_action3_t = update_action3.1!(1-0) -> ((ce_action3.1 -> SKIP))\n" + 
				"act3_action3 = ((oe_x_action3.1?w -> set_w_act3_action3.1!w -> SKIP)); lock_act3_action3.lock -> event_act3_action3 -> lock_act3_action3.unlock -> update_action3.2!(1-1) -> get_w_act3_action3.1?w -> ((oe_x_action3.3!(w) -> SKIP)); act3_action3\n" + 
				"act3_action3_t = ((act3_action3 /\\ END_DIAGRAM_action3) [|{|get_w_act3_action3,set_w_act3_action3,endDiagram_action3|}|] Mem_act3_action3_w_t(0)) \\{|get_w_act3_action3,set_w_act3_action3|}\n" + 
				"parameter_x_t = update_action3.3!(1-0) -> get_x_action3.2?x -> ((oe_x_action3.4!x -> SKIP))\n" + 
				"act2_action3 = ((oe_x_action3.2?z -> set_z_act2_action3.2!z -> SKIP)); lock_act2_action3.lock -> event_act2_action3 -> lock_act2_action3.unlock -> update_action3.4!(1-1) -> get_z_act2_action3.3?z -> ((oe_x_action3.5!(z) -> SKIP)); act2_action3\n" + 
				"act2_action3_t = ((act2_action3 /\\ END_DIAGRAM_action3) [|{|get_z_act2_action3,set_z_act2_action3,endDiagram_action3|}|] Mem_act2_action3_z_t(0)) \\{|get_z_act2_action3,set_z_act2_action3|}\n" + 
				"act1_action3 = ((ce_action3.1 -> SKIP) ||| (oe_x_action3.4?x -> set_x_act1_action3.3!x -> SKIP)); lock_act1_action3.lock -> event_act1_action3 -> lock_act1_action3.unlock -> update_action3.5!(2-2) -> get_x_act1_action3.4?x -> ((oe_x_action3.1!(x) -> SKIP) ||| (oe_x_action3.2!(x) -> SKIP)); act1_action3\n" + 
				"act1_action3_t = ((act1_action3 /\\ END_DIAGRAM_action3) [|{|get_x_act1_action3,set_x_act1_action3,endDiagram_action3|}|] Mem_act1_action3_x_t(0)) \\{|get_x_act1_action3,set_x_act1_action3|}\n" + 
				"fin1_action3 = ((oe_x_action3.5?x -> SKIP) [] (oe_x_action3.3?x -> SKIP)); clear_action3.1 -> SKIP\n" + 
				"fin1_action3_t = fin1_action3 /\\ END_DIAGRAM_action3\n" + 
				"init_action3_t = (init1_action3_t ||| parameter_x_t) /\\ END_DIAGRAM_action3\n");
		
		assertEquals(expected.toString(), actual);
	}

	/*
	 * Teste de Tradução dos tipos de elementos
	 * */
	@Test
	public void TestNodesAction4() {
		String actual = parser4.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("parameter_y_t = update_action4.1!(1-0) -> get_y_action4.1?y -> ((oe_y_action4.1!y -> SKIP))\n" + 
				"act2_action4 = ((oe_xy_action4.2?w -> set_w_act2_action4.1!w -> SKIP)); lock_act2_action4.lock -> event_act2_action4 -> lock_act2_action4.unlock -> update_action4.2!(1-1) -> get_w_act2_action4.2?w -> ((oe_xy_action4.4!(w) -> SKIP)); act2_action4\n" + 
				"act2_action4_t = ((act2_action4 /\\ END_DIAGRAM_action4) [|{|get_w_act2_action4,set_w_act2_action4,endDiagram_action4|}|] Mem_act2_action4_w_t(0)) \\{|get_w_act2_action4,set_w_act2_action4|}\n" + 
				"parameter_x_t = update_action4.3!(1-0) -> get_x_action4.3?x -> ((oe_x_action4.5!x -> SKIP))\n" + 
				"act3_action4 = ((oe_xy_action4.3?w -> set_w_act3_action4.2!w -> SKIP)); lock_act3_action4.lock -> event_act3_action4 -> lock_act3_action4.unlock -> update_action4.4!(1-1) -> get_w_act3_action4.4?w -> ((oe_xy_action4.6!(w) -> SKIP)); act3_action4\n" + 
				"act3_action4_t = ((act3_action4 /\\ END_DIAGRAM_action4) [|{|get_w_act3_action4,set_w_act3_action4,endDiagram_action4|}|] Mem_act3_action4_w_t(0)) \\{|get_w_act3_action4,set_w_act3_action4|}\n" + 
				"act1_action4 = ((oe_x_action4.5?x -> set_x_act1_action4.3!x -> SKIP) ||| (oe_y_action4.1?y -> set_y_act1_action4.4!y -> SKIP)); lock_act1_action4.lock -> event_act1_action4 -> get_y_act1_action4.5?y -> set_x_act1_action4.5!(y) -> lock_act1_action4.unlock -> update_action4.5!(2-2) -> get_x_act1_action4.6?x -> get_y_act1_action4.7?y -> ((oe_xy_action4.2!(x) -> SKIP) ||| (oe_xy_action4.3!(x) -> SKIP)); act1_action4\n" + 
				"act1_action4_t = (((act1_action4 /\\ END_DIAGRAM_action4) [|{|get_x_act1_action4,set_x_act1_action4,endDiagram_action4|}|] Mem_act1_action4_x_t(0)) [|{|get_y_act1_action4,set_y_act1_action4,endDiagram_action4|}|] Mem_act1_action4_y_t(0)) \\{|get_x_act1_action4,set_x_act1_action4,get_y_act1_action4,set_y_act1_action4|}\n" + 
				"fin1_action4 = ((oe_xy_action4.6?xy -> SKIP) [] (oe_xy_action4.4?xy -> SKIP)); clear_action4.1 -> SKIP\n" + 
				"fin1_action4_t = fin1_action4 /\\ END_DIAGRAM_action4\n" + 
				"init_action4_t = (parameter_y_t ||| parameter_x_t) /\\ END_DIAGRAM_action4\n");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução dos tipos de elementos
	 * */
	@Test
	public void TestNodesAction5() {
		String actual = parser5.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("parameter_x_t = update_action5.1!(1-0) -> get_x_action5.1?x -> ((oe_x_action5.1!x -> SKIP))\n" + 
				"act1_action5 = ((oe_x_action5.1?x -> set_x_act1_action5.1!x -> SKIP)); lock_act1_action5.lock -> event_act1_action5 -> lock_act1_action5.unlock -> update_action5.2!(1-1) -> get_x_act1_action5.2?x -> ((oe_x_action5.2!(x) -> SKIP)); act1_action5\n" + 
				"act1_action5_t = ((act1_action5 /\\ END_DIAGRAM_action5) [|{|get_x_act1_action5,set_x_act1_action5,endDiagram_action5|}|] Mem_act1_action5_x_t(0)) \\{|get_x_act1_action5,set_x_act1_action5|}\n" + 
				"fin1_action5 = ((oe_x_action5.2?x -> SKIP)); clear_action5.1 -> SKIP\n" + 
				"fin1_action5_t = fin1_action5 /\\ END_DIAGRAM_action5\n" + 
				"init_action5_t = (parameter_x_t) /\\ END_DIAGRAM_action5\n");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução dos tipos de elementos
	 * */
	@Test
	public void TestNodesAction6() {
		String actual = parser6.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("parameter_x_t = update_action6.1!(1-0) -> get_x_action6.1?x -> ((oe_x_action6.1!x -> SKIP))\n" + 
				"act1_action6 = ((oe_x_action6.1?x -> set_x_act1_action6.1!x -> SKIP)); lock_act1_action6.lock -> event_act1_action6 -> lock_act1_action6.unlock -> update_action6.2!(2-1) -> get_x_act1_action6.2?x -> ((oe_x_action6.2!(x) -> SKIP) ||| (oe_x_action6.3!(x) -> SKIP)); act1_action6\n" + 
				"act1_action6_t = ((act1_action6 /\\ END_DIAGRAM_action6) [|{|get_x_act1_action6,set_x_act1_action6,endDiagram_action6|}|] Mem_act1_action6_x_t(0)) \\{|get_x_act1_action6,set_x_act1_action6|}\n" + 
				"act2_action6 = ((oe_x_action6.2?y -> set_y_act2_action6.2!y -> SKIP)); lock_act2_action6.lock -> event_act2_action6 -> lock_act2_action6.unlock -> update_action6.3!(1-1) -> get_y_act2_action6.3?y -> ((oe_x_action6.4!(y) -> SKIP)); act2_action6\n" + 
				"act2_action6_t = ((act2_action6 /\\ END_DIAGRAM_action6) [|{|get_y_act2_action6,set_y_act2_action6,endDiagram_action6|}|] Mem_act2_action6_y_t(0)) \\{|get_y_act2_action6,set_y_act2_action6|}\n" + 
				"act3_action6 = ((oe_x_action6.3?y -> set_y_act3_action6.3!y -> SKIP)); lock_act3_action6.lock -> event_act3_action6 -> lock_act3_action6.unlock -> update_action6.4!(1-1) -> get_y_act3_action6.4?y -> ((oe_x_action6.5!(y) -> SKIP)); act3_action6\n" + 
				"act3_action6_t = ((act3_action6 /\\ END_DIAGRAM_action6) [|{|get_y_act3_action6,set_y_act3_action6,endDiagram_action6|}|] Mem_act3_action6_y_t(0)) \\{|get_y_act3_action6,set_y_act3_action6|}\n" + 
				"fin1_action6 = ((oe_x_action6.4?x -> SKIP) [] (oe_x_action6.5?x -> SKIP)); clear_action6.1 -> SKIP\n" + 
				"fin1_action6_t = fin1_action6 /\\ END_DIAGRAM_action6\n" + 
				"init_action6_t = (parameter_x_t) /\\ END_DIAGRAM_action6\n");
		
		assertEquals(expected.toString(), actual);
	}
	
}
