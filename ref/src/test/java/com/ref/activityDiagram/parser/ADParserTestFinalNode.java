package com.ref.activityDiagram.parser;

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
import com.ref.exceptions.ParsingException;
import com.ref.parser.activityDiagram.ADParser;

public class ADParserTestFinalNode {
	
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
			
			parser1 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/action2.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser2 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/action5.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser3 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/action6.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser4 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/final1.asta");
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
	 * Teste de Tradução Final node
	 * */
	@Test
	public void TestNodesFinal1() throws ParsingException {
		String actual = parser1.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("init1_action1_t = update_action1.1!(1-0) -> ((ce_action1.1 -> SKIP))\n" +
				"act1_action1 = ((ce_action1.1 -> SKIP)); event_act1_action1 -> ((ce_action1.2 -> SKIP)); act1_action1\n" +
				"act1_action1_t = act1_action1 /\\ END_DIAGRAM_action1\n" +
				"fin1_action1 = ((ce_action1.2 -> SKIP)); clear_action1.1 -> SKIP\n" +
				"fin1_action1_t = fin1_action1 /\\ END_DIAGRAM_action1\n" +
				"init_action1_t = (init1_action1_t) /\\ END_DIAGRAM_action1\n");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução Final node
	 * */
	@Test
	public void TestNodesFinal2() throws ParsingException {
		String actual = parser2.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("init1_action2_t = update_action2.1!(1-0) -> ((ce_action2.1 -> SKIP))\n" +
				"act1_action2 = ((ce_action2.1 -> SKIP)); event_act1_action2 -> update_action2.2!(2-1) -> ((ce_action2.2 -> SKIP) ||| (ce_action2.3 -> SKIP)); act1_action2\n" +
				"act1_action2_t = act1_action2 /\\ END_DIAGRAM_action2\n" +
				"act2_action2 = ((ce_action2.2 -> SKIP)); event_act2_action2 -> ((ce_action2.4 -> SKIP)); act2_action2\n" +
				"act2_action2_t = act2_action2 /\\ END_DIAGRAM_action2\n" +
				"act3_action2 = ((ce_action2.3 -> SKIP)); event_act3_action2 -> ((ce_action2.5 -> SKIP)); act3_action2\n" +
				"act3_action2_t = act3_action2 /\\ END_DIAGRAM_action2\n" +
				"fin1_action2 = ((ce_action2.4 -> SKIP) [] (ce_action2.5 -> SKIP)); clear_action2.1 -> SKIP\n" +
				"fin1_action2_t = fin1_action2 /\\ END_DIAGRAM_action2\n" +
				"init_action2_t = (init1_action2_t) /\\ END_DIAGRAM_action2\n");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução Final node
	 * */
	@Test
	public void TestNodesFinal3() throws ParsingException {
		String actual = parser3.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("parameter_x_action5_t = update_action5.1!(1-0) -> get_x_action5.1?x -> ((oe_x_action5.1!x -> SKIP))\n" +
				"act1_action5 = ((oe_x_action5.1?x -> set_x_act1_action5.1!x -> SKIP)); event_act1_action5 -> get_x_act1_action5.2?x -> ((((x) >= 0 and (x) <= 1) & oe_x_action5.2!(x) -> SKIP)); act1_action5\n" +
				"act1_action5_t = ((act1_action5 /\\ END_DIAGRAM_action5) [|{|get_x_act1_action5,set_x_act1_action5,endDiagram_action5|}|] Mem_act1_action5_x_t(0)) \\{|get_x_act1_action5,set_x_act1_action5|}\n" +
				"fin1_action5 = ((oe_x_action5.2?x -> SKIP)); clear_action5.1 -> SKIP\n" +
				"fin1_action5_t = fin1_action5 /\\ END_DIAGRAM_action5\n" +
				"init_action5_t = (parameter_x_action5_t) /\\ END_DIAGRAM_action5\n");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução Final node
	 * */
	@Test
	public void TestNodesFinal4() throws ParsingException {
		String actual = parser4.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("parameter_x_action6_t = update_action6.1!(1-0) -> get_x_action6.1?x -> ((oe_x_action6.1!x -> SKIP))\n" +
				"act1_action6 = ((oe_x_action6.1?x -> set_x_act1_action6.1!x -> SKIP)); event_act1_action6 -> update_action6.2!(2-1) -> get_x_act1_action6.2?x -> ((((x) >= 0 and (x) <= 1) & oe_x_action6.2!(x) -> SKIP) ||| (((x) >= 0 and (x) <= 1) & oe_x_action6.3!(x) -> SKIP)); act1_action6\n" +
				"act1_action6_t = ((act1_action6 /\\ END_DIAGRAM_action6) [|{|get_x_act1_action6,set_x_act1_action6,endDiagram_action6|}|] Mem_act1_action6_x_t(0)) \\{|get_x_act1_action6,set_x_act1_action6|}\n" +
				"act2_action6 = ((oe_x_action6.2?y -> set_y_act2_action6.2!y -> SKIP)); event_act2_action6 -> get_y_act2_action6.3?y -> ((((y) >= 0 and (y) <= 1) & oe_x_action6.4!(y) -> SKIP)); act2_action6\n" +
				"act2_action6_t = ((act2_action6 /\\ END_DIAGRAM_action6) [|{|get_y_act2_action6,set_y_act2_action6,endDiagram_action6|}|] Mem_act2_action6_y_t(0)) \\{|get_y_act2_action6,set_y_act2_action6|}\n" +
				"act3_action6 = ((oe_x_action6.3?y -> set_y_act3_action6.3!y -> SKIP)); event_act3_action6 -> get_y_act3_action6.4?y -> ((((y) >= 0 and (y) <= 1) & oe_x_action6.5!(y) -> SKIP)); act3_action6\n" +
				"act3_action6_t = ((act3_action6 /\\ END_DIAGRAM_action6) [|{|get_y_act3_action6,set_y_act3_action6,endDiagram_action6|}|] Mem_act3_action6_y_t(0)) \\{|get_y_act3_action6,set_y_act3_action6|}\n" +
				"fin1_action6 = ((oe_x_action6.4?x -> SKIP) [] (oe_x_action6.5?x -> SKIP)); clear_action6.1 -> SKIP\n" +
				"fin1_action6_t = fin1_action6 /\\ END_DIAGRAM_action6\n" +
				"init_action6_t = (parameter_x_action6_t) /\\ END_DIAGRAM_action6\n");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução Final node
	 * */
	@Test
	public void TestNodesFinal5() throws ParsingException {
		String actual = parser5.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("parameter_x_final1_t = update_final1.1!(1-0) -> get_x_final1.1?x -> ((oe_x_final1.1!x -> SKIP))\n" +
				"act2_final1 = ((oe_x_final1.1?x -> set_x_act2_final1.1!x -> SKIP)); event_act2_final1 -> get_x_act2_final1.2?x -> ((((x) >= 0 and (x) <= 1) & oe_x_final1.2!(x) -> SKIP)); act2_final1\n" +
				"act2_final1_t = ((act2_final1 /\\ END_DIAGRAM_final1) [|{|get_x_act2_final1,set_x_act2_final1,endDiagram_final1|}|] Mem_act2_final1_x_t(0)) \\{|get_x_act2_final1,set_x_act2_final1|}\n" +
				"init1_final1_t = update_final1.2!(1-0) -> ((ce_final1.1 -> SKIP))\n" +
				"act1_final1 = ((ce_final1.1 -> SKIP)); event_act1_final1 -> ((ce_final1.2 -> SKIP)); act1_final1\n" +
				"act1_final1_t = act1_final1 /\\ END_DIAGRAM_final1\n" +
				"act3_final1 = ((ce_final1.2 -> SKIP)); event_act3_final1 -> ((ce_final1.3 -> SKIP)); act3_final1\n" +
				"act3_final1_t = act3_final1 /\\ END_DIAGRAM_final1\n" +
				"fin1_final1 = ((oe_x_final1.2?x -> SKIP) [] (ce_final1.3 -> SKIP)); clear_final1.1 -> SKIP\n" +
				"fin1_final1_t = fin1_final1 /\\ END_DIAGRAM_final1\n" +
				"init_final1_t = (parameter_x_final1_t ||| init1_final1_t) /\\ END_DIAGRAM_final1\n");
		
		assertEquals(expected.toString(), actual);
	}
	
}