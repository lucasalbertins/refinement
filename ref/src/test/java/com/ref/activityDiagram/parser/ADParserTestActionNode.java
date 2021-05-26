package com.ref.activityDiagram.parser;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
//import com.change_vision.jude.api.inf.model.IActivityDiagram;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.project.ModelFinder;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.ref.exceptions.ParsingException;
import com.ref.interfaces.activityDiagram.IActivityDiagram;
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
	public void TestNodesAction1() throws ParsingException {
		String actual = parser1.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("init1_action1_t(id) = update_action1.id.1!(1-0) -> ((ce_action1.id.1 -> SKIP))\n" +
				"act1_action1(id) = ((ce_action1.id.1 -> SKIP)); event_act1_action1.id -> ((ce_action1.id.2 -> SKIP)); act1_action1(id)\n" +
				"act1_action1_t(id) = act1_action1(id) /\\ END_DIAGRAM_action1(id)\n" +
				"fin1_action1(id) = ((ce_action1.id.2 -> SKIP)); clear_action1.id.1 -> SKIP\n" +
				"fin1_action1_t(id) = fin1_action1(id) /\\ END_DIAGRAM_action1(id)\n" +
				"init_action1_t(id) = (init1_action1_t(id)) /\\ END_DIAGRAM_action1(id)\n");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução dos tipos de elementos
	 * */
	@Test
	public void TestNodesAction2() throws ParsingException {
		String actual = parser2.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("init1_action2_t(id) = update_action2.id.1!(1-0) -> ((ce_action2.id.1 -> SKIP))\n" +
				"act1_action2(id) = ((ce_action2.id.1 -> SKIP)); event_act1_action2.id -> update_action2.id.2!(2-1) -> ((ce_action2.id.2 -> SKIP) ||| (ce_action2.id.3 -> SKIP)); act1_action2(id)\n" +
				"act1_action2_t(id) = act1_action2(id) /\\ END_DIAGRAM_action2(id)\n" +
				"act2_action2(id) = ((ce_action2.id.2 -> SKIP)); event_act2_action2.id -> ((ce_action2.id.4 -> SKIP)); act2_action2(id)\n" +
				"act2_action2_t(id) = act2_action2(id) /\\ END_DIAGRAM_action2(id)\n" +
				"act3_action2(id) = ((ce_action2.id.3 -> SKIP)); event_act3_action2.id -> ((ce_action2.id.5 -> SKIP)); act3_action2(id)\n" +
				"act3_action2_t(id) = act3_action2(id) /\\ END_DIAGRAM_action2(id)\n" +
				"fin1_action2(id) = ((ce_action2.id.4 -> SKIP) [] (ce_action2.id.5 -> SKIP)); clear_action2.id.1 -> SKIP\n" +
				"fin1_action2_t(id) = fin1_action2(id) /\\ END_DIAGRAM_action2(id)\n" +
				"init_action2_t(id) = (init1_action2_t(id)) /\\ END_DIAGRAM_action2(id)\n");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução dos tipos de elementos
	 * */
	@Test
	public void TestNodesAction3() throws ParsingException {
		String actual = parser3.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("init1_action3_t(id) = update_action3.id.1!(1-0) -> ((ce_action3.id.1 -> SKIP))\n" +
				"act3_action3(id) = ((oe_int_action3.id.1?w -> set_w_act3_action3.id.1!w -> SKIP)); event_act3_action3.id -> get_w_act3_action3.id.1?w -> ((((w) >= 0 and (w) <= 1) & oe_int_action3.id.3!(w) -> SKIP)); act3_action3(id)\n" +
				"act3_action3_t(id) = ((act3_action3(id) /\\ END_DIAGRAM_action3(id)) [|{|get_w_act3_action3.id,set_w_act3_action3.id,endDiagram_action3.id|}|] Mem_act3_action3_w_t(id,0)) \\{|get_w_act3_action3.id,set_w_act3_action3.id|}\n" +
				"parameter_x_action3_t(id) = update_action3.id.2!(1-0) -> get_x_action3.id.2?x -> ((oe_int_action3.id.4!x -> SKIP))\n" +
				"act2_action3(id) = ((oe_int_action3.id.2?z -> set_z_act2_action3.id.2!z -> SKIP)); event_act2_action3.id -> get_z_act2_action3.id.3?z -> ((((z) >= 0 and (z) <= 1) & oe_int_action3.id.5!(z) -> SKIP)); act2_action3(id)\n" +
				"act2_action3_t(id) = ((act2_action3(id) /\\ END_DIAGRAM_action3(id)) [|{|get_z_act2_action3.id,set_z_act2_action3.id,endDiagram_action3.id|}|] Mem_act2_action3_z_t(id,0)) \\{|get_z_act2_action3.id,set_z_act2_action3.id|}\n" +
				"act1_action3(id) = ((ce_action3.id.1 -> SKIP) ||| (oe_int_action3.id.4?x -> set_x_act1_action3.id.3!x -> SKIP)); event_act1_action3.id -> get_x_act1_action3.id.4?x -> ((((x) >= 0 and (x) <= 1) & oe_int_action3.id.1!(x) -> SKIP) ||| (((x) >= 0 and (x) <= 1) & oe_int_action3.id.2!(x) -> SKIP)); act1_action3(id)\n" +
				"act1_action3_t(id) = ((act1_action3(id) /\\ END_DIAGRAM_action3(id)) [|{|get_x_act1_action3.id,set_x_act1_action3.id,endDiagram_action3.id|}|] Mem_act1_action3_x_t(id,0)) \\{|get_x_act1_action3.id,set_x_act1_action3.id|}\n" +
				"fin1_action3(id) = ((oe_int_action3.id.5?w -> SKIP) [] (oe_int_action3.id.3?z -> SKIP)); clear_action3.id.1 -> SKIP\n" +
				"fin1_action3_t(id) = fin1_action3(id) /\\ END_DIAGRAM_action3(id)\n" +
				"init_action3_t(id) = (init1_action3_t(id) ||| parameter_x_action3_t(id)) /\\ END_DIAGRAM_action3(id)\n");
		
		assertEquals(expected.toString(), actual);
	}

	/*
	 * Teste de Tradução dos tipos de elementos
	 * */
	@Test
	public void TestNodesAction4() throws ParsingException {
		String actual = parser4.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("parameter_y_action4_t(id) = update_action4.id.1!(1-0) -> get_y_action4.id.1?y -> ((oe_int_action4.id.1!y -> SKIP))\n" +
				"act2_action4(id) = ((oe_int_action4.id.2?w -> set_w_act2_action4.id.1!w -> SKIP)); event_act2_action4.id -> get_w_act2_action4.id.2?w -> ((((w) >= 0 and (w) <= 1) & oe_int_action4.id.4!(w) -> SKIP)); act2_action4(id)\n" +
				"act2_action4_t(id) = ((act2_action4(id) /\\ END_DIAGRAM_action4(id)) [|{|get_w_act2_action4.id,set_w_act2_action4.id,endDiagram_action4.id|}|] Mem_act2_action4_w_t(id,0)) \\{|get_w_act2_action4.id,set_w_act2_action4.id|}\n" +
				"parameter_x_action4_t(id) = update_action4.id.2!(1-0) -> get_x_action4.id.3?x -> ((oe_int_action4.id.5!x -> SKIP))\n" +
				"act3_action4(id) = ((oe_int_action4.id.3?w -> set_w_act3_action4.id.2!w -> SKIP)); event_act3_action4.id -> get_w_act3_action4.id.4?w -> ((((w) >= 0 and (w) <= 1) & oe_int_action4.id.6!(w) -> SKIP)); act3_action4(id)\n" +
				"act3_action4_t(id) = ((act3_action4(id) /\\ END_DIAGRAM_action4(id)) [|{|get_w_act3_action4.id,set_w_act3_action4.id,endDiagram_action4.id|}|] Mem_act3_action4_w_t(id,0)) \\{|get_w_act3_action4.id,set_w_act3_action4.id|}\n" +
				"act1_action4(id) = ((oe_int_action4.id.5?x -> set_x_act1_action4.id.3!x -> SKIP) ||| (oe_int_action4.id.1?y -> set_y_act1_action4.id.4!y -> SKIP)); event_act1_action4.id -> get_y_act1_action4.id.5?y -> set_x_act1_action4.id.5!(y) -> get_x_act1_action4.id.6?x -> get_y_act1_action4.id.7?y -> ((((x) >= 0 and (x) <= 1) & oe_int_action4.id.2!(x) -> SKIP) ||| (((x) >= 0 and (x) <= 1) & oe_int_action4.id.3!(x) -> SKIP)); act1_action4(id)\n" +
				"act1_action4_t(id) = (((act1_action4(id) /\\ END_DIAGRAM_action4(id)) [|{|get_x_act1_action4.id,set_x_act1_action4.id,endDiagram_action4.id|}|] Mem_act1_action4_x_t(id,0)) [|{|get_y_act1_action4.id,set_y_act1_action4.id,endDiagram_action4.id|}|] Mem_act1_action4_y_t(id,0)) \\{|get_x_act1_action4.id,set_x_act1_action4.id,get_y_act1_action4.id,set_y_act1_action4.id|}\n" +
				"fin1_action4(id) = ((oe_int_action4.id.6?z -> SKIP) [] (oe_int_action4.id.4?z -> SKIP)); clear_action4.id.1 -> SKIP\n" +
				"fin1_action4_t(id) = fin1_action4(id) /\\ END_DIAGRAM_action4(id)\n" +
				"init_action4_t(id) = (parameter_y_action4_t(id) ||| parameter_x_action4_t(id)) /\\ END_DIAGRAM_action4(id)\n");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução dos tipos de elementos
	 * */
	@Test
	public void TestNodesAction5() throws ParsingException {
		String actual = parser5.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("parameter_x_action5_t(id) = update_action5.id.1!(1-0) -> get_x_action5.id.1?x -> ((oe_int_action5.id.1!x -> SKIP))\n" +
				"act1_action5(id) = ((oe_int_action5.id.1?x -> set_x_act1_action5.id.1!x -> SKIP)); event_act1_action5.id -> get_x_act1_action5.id.2?x -> ((((x) >= 0 and (x) <= 1) & oe_int_action5.id.2!(x) -> SKIP)); act1_action5(id)\n" +
				"act1_action5_t(id) = ((act1_action5(id) /\\ END_DIAGRAM_action5(id)) [|{|get_x_act1_action5.id,set_x_act1_action5.id,endDiagram_action5.id|}|] Mem_act1_action5_x_t(id,0)) \\{|get_x_act1_action5.id,set_x_act1_action5.id|}\n" +
				"fin1_action5(id) = ((oe_int_action5.id.2?y -> SKIP)); clear_action5.id.1 -> SKIP\n" +
				"fin1_action5_t(id) = fin1_action5(id) /\\ END_DIAGRAM_action5(id)\n" +
				"init_action5_t(id) = (parameter_x_action5_t(id)) /\\ END_DIAGRAM_action5(id)\n");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução dos tipos de elementos
	 * */
	@Test
	public void TestNodesAction6() throws ParsingException {
		String actual = parser6.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("parameter_x_action6_t(id) = update_action6.id.1!(1-0) -> get_x_action6.id.1?x -> ((oe_int_action6.id.1!x -> SKIP))\n" +
				"act1_action6(id) = ((oe_int_action6.id.1?x -> set_x_act1_action6.id.1!x -> SKIP)); event_act1_action6.id -> update_action6.id.2!(2-1) -> get_x_act1_action6.id.2?x -> ((((x) >= 0 and (x) <= 1) & oe_int_action6.id.2!(x) -> SKIP) ||| (((x) >= 0 and (x) <= 1) & oe_int_action6.id.3!(x) -> SKIP)); act1_action6(id)\n" +
				"act1_action6_t(id) = ((act1_action6(id) /\\ END_DIAGRAM_action6(id)) [|{|get_x_act1_action6.id,set_x_act1_action6.id,endDiagram_action6.id|}|] Mem_act1_action6_x_t(id,0)) \\{|get_x_act1_action6.id,set_x_act1_action6.id|}\n" +
				"act2_action6(id) = ((oe_int_action6.id.2?y -> set_y_act2_action6.id.2!y -> SKIP)); event_act2_action6.id -> get_y_act2_action6.id.3?y -> ((((y) >= 0 and (y) <= 1) & oe_int_action6.id.4!(y) -> SKIP)); act2_action6(id)\n" +
				"act2_action6_t(id) = ((act2_action6(id) /\\ END_DIAGRAM_action6(id)) [|{|get_y_act2_action6.id,set_y_act2_action6.id,endDiagram_action6.id|}|] Mem_act2_action6_y_t(id,0)) \\{|get_y_act2_action6.id,set_y_act2_action6.id|}\n" +
				"act3_action6(id) = ((oe_int_action6.id.3?y -> set_y_act3_action6.id.3!y -> SKIP)); event_act3_action6.id -> get_y_act3_action6.id.4?y -> ((((y) >= 0 and (y) <= 1) & oe_int_action6.id.5!(y) -> SKIP)); act3_action6(id)\n" +
				"act3_action6_t(id) = ((act3_action6(id) /\\ END_DIAGRAM_action6(id)) [|{|get_y_act3_action6.id,set_y_act3_action6.id,endDiagram_action6.id|}|] Mem_act3_action6_y_t(id,0)) \\{|get_y_act3_action6.id,set_y_act3_action6.id|}\n" +
				"fin1_action6(id) = ((oe_int_action6.id.4?z -> SKIP) [] (oe_int_action6.id.5?z -> SKIP)); clear_action6.id.1 -> SKIP\n" +
				"fin1_action6_t(id) = fin1_action6(id) /\\ END_DIAGRAM_action6(id)\n" +
				"init_action6_t(id) = (parameter_x_action6_t(id)) /\\ END_DIAGRAM_action6(id)\n");
		
		assertEquals(expected.toString(), actual);
	}
	
}
