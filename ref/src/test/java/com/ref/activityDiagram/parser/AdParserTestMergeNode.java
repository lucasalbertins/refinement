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

public class AdParserTestMergeNode {

	public static IActivityDiagram ad;
	private static ADParser parser1;
	private static ADParser parser2;
	private static ADParser parser3;
	
	@BeforeClass
	public static void GetDiagram() throws Exception {
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/merge1.asta");
			INamedElement[] findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser1 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/merge3.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser2 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/merge4.asta");
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
	 * Teste de Tradução Merge Node
	 * */
	@Test
	public void TestMergeNode1() throws ParsingException {
		String actual = parser1.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("init1_merge1_t = update_merge1.1!(2-0) -> ((ce_merge1.1 -> SKIP) ||| (ce_merge1.2 -> SKIP))\n" +
				"act1_merge1 = ((ce_merge1.1 -> SKIP)); event_act1_merge1 -> ((ce_merge1.3 -> SKIP)); act1_merge1\n" +
				"act1_merge1_t = act1_merge1 /\\ END_DIAGRAM_merge1\n" +
				"fin1_merge1 = ((ce_merge1.4 -> SKIP)); clear_merge1.1 -> SKIP\n" +
				"fin1_merge1_t = fin1_merge1 /\\ END_DIAGRAM_merge1\n" +
				"act2_merge1 = ((ce_merge1.2 -> SKIP)); event_act2_merge1 -> ((ce_merge1.5 -> SKIP)); act2_merge1\n" +
				"act2_merge1_t = act2_merge1 /\\ END_DIAGRAM_merge1\n" +
				"merge1_merge1 = ((ce_merge1.3 -> SKIP) [] (ce_merge1.5 -> SKIP)); ce_merge1.4 -> merge1_merge1\n" +
				"merge1_merge1_t = merge1_merge1 /\\ END_DIAGRAM_merge1\n" +
				"init_merge1_t = (init1_merge1_t) /\\ END_DIAGRAM_merge1\n");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução Merge Node
	 * */
	@Test
	public void TestMergeNode2() throws ParsingException {
		String actual = parser2.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("parameter_x_merge3_t = update_merge3.1!(1-0) -> get_x_merge3.1?x -> ((oe_x_merge3.1!x -> SKIP))\n" +
				"act1_merge3 = ((oe_yx_merge3.2?x -> set_x_act1_merge3.1!x -> SKIP)); event_act1_merge3 -> get_x_act1_merge3.2?x -> ((((x) >= 0 and (x) <= 1) & oe_yx_merge3.3!(x) -> SKIP)); act1_merge3\n" +
				"act1_merge3_t = ((act1_merge3 /\\ END_DIAGRAM_merge3) [|{|get_x_act1_merge3,set_x_act1_merge3,endDiagram_merge3|}|] Mem_act1_merge3_x_t(0)) \\{|get_x_act1_merge3,set_x_act1_merge3|}\n" +
				"fin1_merge3 = ((oe_yx_merge3.3?yx -> SKIP)); clear_merge3.1 -> SKIP\n" +
				"fin1_merge3_t = fin1_merge3 /\\ END_DIAGRAM_merge3\n" +
				"parameter_y_merge3_t = update_merge3.2!(1-0) -> get_y_merge3.3?y -> ((oe_y_merge3.4!y -> SKIP))\n" +
				"merge1_merge3 = ((oe_y_merge3.4?y -> set_yx_merge1_merge3.2!y -> SKIP) [] (oe_x_merge3.1?x -> set_yx_merge1_merge3.3!x -> SKIP)); get_yx_merge1_merge3.4?yx -> oe_yx_merge3.2!yx -> merge1_merge3\n" +
				"merge1_merge3_t = ((merge1_merge3 /\\ END_DIAGRAM_merge3) [|{|get_yx_merge1_merge3,set_yx_merge1_merge3,endDiagram_merge3|}|] Mem_merge1_merge3_yx_t(0)) \\{|get_yx_merge1_merge3,set_yx_merge1_merge3|}\n" +
				"init_merge3_t = (parameter_x_merge3_t ||| parameter_y_merge3_t) /\\ END_DIAGRAM_merge3\n");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução Merge Node
	 * */
	@Test
	public void TestMergeNode3() throws ParsingException {
		String actual = parser3.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("parameter_x_merge4_t = update_merge4.1!(2-0) -> get_x_merge4.1?x -> ((oe_x_merge4.1!x -> SKIP) ||| (oe_x_merge4.2!x -> SKIP))\n" +
				"act1_merge4 = ((oe_x_merge4.1?x -> set_x_act1_merge4.1!x -> SKIP)); event_act1_merge4 -> get_x_act1_merge4.2?x -> ((((x) >= 0 and (x) <= 1) & oe_x_merge4.3!(x) -> SKIP)); act1_merge4\n" +
				"act1_merge4_t = ((act1_merge4 /\\ END_DIAGRAM_merge4) [|{|get_x_act1_merge4,set_x_act1_merge4,endDiagram_merge4|}|] Mem_act1_merge4_x_t(0)) \\{|get_x_act1_merge4,set_x_act1_merge4|}\n" +
				"fin1_merge4 = ((oe_x_merge4.4?x -> SKIP)); clear_merge4.1 -> SKIP\n" +
				"fin1_merge4_t = fin1_merge4 /\\ END_DIAGRAM_merge4\n" +
				"act2_merge4 = ((oe_x_merge4.2?x -> set_x_act2_merge4.2!x -> SKIP)); event_act2_merge4 -> get_x_act2_merge4.3?x -> ((((x) >= 0 and (x) <= 1) & oe_x_merge4.5!(x) -> SKIP)); act2_merge4\n" +
				"act2_merge4_t = ((act2_merge4 /\\ END_DIAGRAM_merge4) [|{|get_x_act2_merge4,set_x_act2_merge4,endDiagram_merge4|}|] Mem_act2_merge4_x_t(0)) \\{|get_x_act2_merge4,set_x_act2_merge4|}\n" +
				"merge1_merge4 = ((oe_x_merge4.3?x -> set_x_merge1_merge4.3!x -> SKIP) [] (oe_x_merge4.5?x -> set_x_merge1_merge4.4!x -> SKIP)); get_x_merge1_merge4.4?x -> oe_x_merge4.4!x -> merge1_merge4\n" +
				"merge1_merge4_t = ((merge1_merge4 /\\ END_DIAGRAM_merge4) [|{|get_x_merge1_merge4,set_x_merge1_merge4,endDiagram_merge4|}|] Mem_merge1_merge4_x_t(0)) \\{|get_x_merge1_merge4,set_x_merge1_merge4|}\n" +
				"init_merge4_t = (parameter_x_merge4_t) /\\ END_DIAGRAM_merge4\n");
		
		assertEquals(expected.toString(), actual);
	}
	
}
