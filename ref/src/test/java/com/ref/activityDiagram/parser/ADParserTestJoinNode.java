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
import com.ref.parser.activityDiagram.ADParser;
import com.ref.interfaces.activityDiagram.IActivityDiagram;

public class ADParserTestJoinNode {

	public static IActivityDiagram ad;
	private static ADParser parser1;
	private static ADParser parser2;
	private static ADParser parser3;
	private static ADParser parser4;
	
	@BeforeClass
	public static void GetDiagram() throws Exception {
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/join1.asta");
			INamedElement[] findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser1 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/join2.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser2 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/join3.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser3 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/join4.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser4 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
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
	 * Teste de Tradução Join Node
	 * */
	@Test
	public void TestNodesJoin1() throws ParsingException {
		String actual = parser1.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("init1_join1_t = update_join1.1!(2-0) -> ((ce_join1.1 -> SKIP) ||| (ce_join1.2 -> SKIP))\n" +
				"act1_join1 = ((ce_join1.1 -> SKIP)); event_act1_join1 -> ((ce_join1.3 -> SKIP)); act1_join1\n" +
				"act1_join1_t = act1_join1 /\\ END_DIAGRAM_join1\n" +
				"fin1_join1 = ((ce_join1.4 -> SKIP)); clear_join1.1 -> SKIP\n" +
				"fin1_join1_t = fin1_join1 /\\ END_DIAGRAM_join1\n" +
				"act2_join1 = ((ce_join1.2 -> SKIP)); event_act2_join1 -> ((ce_join1.5 -> SKIP)); act2_join1\n" +
				"act2_join1_t = act2_join1 /\\ END_DIAGRAM_join1\n" +
				"join1_join1 = ((ce_join1.3 -> SKIP) ||| (ce_join1.5 -> SKIP)); update_join1.2!(1-2) -> ((ce_join1.4 -> SKIP)); join1_join1\n" +
				"join1_join1_t = (join1_join1 /\\ END_DIAGRAM_join1)\n" +
				"init_join1_t = (init1_join1_t) /\\ END_DIAGRAM_join1\n");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução Join Node
	 * */
	@Test
	public void TestNodesJoin2() throws ParsingException {
		String actual = parser2.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("parameter_x_join2_t = update_join2.1!(2-0) -> get_x_join2.1?x -> ((oe_x_join2.1!x -> SKIP) ||| (oe_x_join2.2!x -> SKIP))\n" +
				"act2_join2 = ((oe_x_join2.1?x -> set_x_act2_join2.1!x -> SKIP)); event_act2_join2 -> get_x_act2_join2.2?x -> ((((x) >= 0 and (x) <= 1) & oe_x_join2.3!(x) -> SKIP)); act2_join2\n" +
				"act2_join2_t = ((act2_join2 /\\ END_DIAGRAM_join2) [|{|get_x_act2_join2,set_x_act2_join2,endDiagram_join2|}|] Mem_act2_join2_x_t(0)) \\{|get_x_act2_join2,set_x_act2_join2|}\n" +
				"fin1_join2 = ((oe_x_join2.4?x -> SKIP)); clear_join2.1 -> SKIP\n" +
				"fin1_join2_t = fin1_join2 /\\ END_DIAGRAM_join2\n" +
				"act1_join2 = ((oe_x_join2.2?x -> set_x_act1_join2.2!x -> SKIP)); event_act1_join2 -> get_x_act1_join2.3?x -> ((((x) >= 0 and (x) <= 1) & oe_x_join2.5!(x) -> SKIP)); act1_join2\n" +
				"act1_join2_t = ((act1_join2 /\\ END_DIAGRAM_join2) [|{|get_x_act1_join2,set_x_act1_join2,endDiagram_join2|}|] Mem_act1_join2_x_t(0)) \\{|get_x_act1_join2,set_x_act1_join2|}\n" +
				"join1_join2 = ((oe_x_join2.3?x -> set_x_join1_join2.3!x -> SKIP) ||| (oe_x_join2.5?x -> set_x_join1_join2.4!x -> SKIP)); update_join2.2!(1-2) -> get_x_join1_join2.4?x -> ((oe_x_join2.4!x -> SKIP)); join1_join2\n" +
				"join1_join2_t = ((join1_join2 /\\ END_DIAGRAM_join2) [|{|get_x_join1_join2,set_x_join1_join2,endDiagram_join2|}|] Mem_join1_join2_x_t(0)) \\{|get_x_join1_join2,set_x_join1_join2|}\n" +
				"init_join2_t = (parameter_x_join2_t) /\\ END_DIAGRAM_join2\n");
		
		assertEquals(expected.toString(), actual);
	}

	/*
	 * Teste de Tradução Join Node
	 * */
	@Test
	public void TestNodesJoin3() throws ParsingException {
		String actual = parser3.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("parameter_x_join3_t = update_join3.1!(1-0) -> get_x_join3.1?x -> ((oe_x_join3.1!x -> SKIP))\n" +
				"act1_join3 = ((oe_x_join3.2?x -> set_x_act1_join3.1!x -> SKIP)); event_act1_join3 -> get_x_act1_join3.2?x -> ((((x) >= 0 and (x) <= 1) & oe_x_join3.3!(x) -> SKIP)); act1_join3\n" +
				"act1_join3_t = ((act1_join3 /\\ END_DIAGRAM_join3) [|{|get_x_act1_join3,set_x_act1_join3,endDiagram_join3|}|] Mem_act1_join3_x_t(0)) \\{|get_x_act1_join3,set_x_act1_join3|}\n" +
				"fin1_join3 = ((oe_x_join3.3?x -> SKIP)); clear_join3.1 -> SKIP\n" +
				"fin1_join3_t = fin1_join3 /\\ END_DIAGRAM_join3\n" +
				"init1_join3_t = update_join3.2!(1-0) -> ((ce_join3.1 -> SKIP))\n" +
				"join1_join3 = ((ce_join3.1 -> SKIP) ||| (oe_x_join3.1?x -> set_x_join1_join3.2!x -> SKIP)); update_join3.3!(1-2) -> get_x_join1_join3.3?x -> ((oe_x_join3.2!x -> SKIP)); join1_join3\n" +
				"join1_join3_t = ((join1_join3 /\\ END_DIAGRAM_join3) [|{|get_x_join1_join3,set_x_join1_join3,endDiagram_join3|}|] Mem_join1_join3_x_t(0)) \\{|get_x_join1_join3,set_x_join1_join3|}\n" +
				"init_join3_t = (parameter_x_join3_t ||| init1_join3_t) /\\ END_DIAGRAM_join3\n");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução Join Node
	 * */
	@Test
	public void TestNodesJoin4() throws ParsingException {
		String actual = parser4.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("parameter_y_join4_t = update_join4.1!(1-0) -> get_y_join4.1?y -> ((oe_y_join4.1!y -> SKIP))\n" +
				"act1_join4 = ((oe_yx_join4.2?x -> set_x_act1_join4.1!x -> SKIP)); event_act1_join4 -> get_x_act1_join4.2?x -> ((((x) >= 0 and (x) <= 1) & oe_yx_join4.3!(x) -> SKIP)); act1_join4\n" +
				"act1_join4_t = ((act1_join4 /\\ END_DIAGRAM_join4) [|{|get_x_act1_join4,set_x_act1_join4,endDiagram_join4|}|] Mem_act1_join4_x_t(0)) \\{|get_x_act1_join4,set_x_act1_join4|}\n" +
				"fin1_join4 = ((oe_yx_join4.3?yx -> SKIP)); clear_join4.1 -> SKIP\n" +
				"fin1_join4_t = fin1_join4 /\\ END_DIAGRAM_join4\n" +
				"parameter_x_join4_t = update_join4.2!(1-0) -> get_x_join4.3?x -> ((oe_x_join4.4!x -> SKIP))\n" +
				"join1_join4 = ((oe_y_join4.1?y -> set_y_join1_join4.2!y -> SKIP) ||| (oe_x_join4.4?x -> set_x_join1_join4.3!x -> SKIP)); update_join4.3!(1-2) -> get_y_join1_join4.4?y -> get_x_join1_join4.5?x -> ((oe_yx_join4.2!y -> SKIP) |~| (oe_yx_join4.2!x -> SKIP)); join1_join4\n" +
				"join1_join4_t = (((join1_join4 /\\ END_DIAGRAM_join4) [|{|get_y_join1_join4,set_y_join1_join4,endDiagram_join4|}|] Mem_join1_join4_y_t(0)) [|{|get_x_join1_join4,set_x_join1_join4,endDiagram_join4|}|] Mem_join1_join4_x_t(0)) \\{|get_y_join1_join4,set_y_join1_join4,get_x_join1_join4,set_x_join1_join4|}\n" +
				"init_join4_t = (parameter_y_join4_t ||| parameter_x_join4_t) /\\ END_DIAGRAM_join4\n");
		
		assertEquals(expected.toString(), actual);
	}
	
}
