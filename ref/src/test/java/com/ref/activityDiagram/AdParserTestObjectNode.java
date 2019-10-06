package com.ref.activityDiagram;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IActivityDiagram;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.project.ModelFinder;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.ref.parser.activityDiagram.ADParser;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AdParserTestObjectNode {

	public static IActivityDiagram ad;
	private static ADParser parser1;
	private static ADParser parser2;
	private static ADParser parser3;
	
	@BeforeClass
	public static void GetDiagram() throws Exception {
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/objectNode1.asta");
			INamedElement[] findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];

			parser1 = new ADParser(ad.getActivity(), ad.getName(), ad);

			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/objectNode2.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];

			parser2 = new ADParser(ad.getActivity(), ad.getName(), ad);

			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/objectNode3.asta");
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
	
	/* Teste de Tradução do Object Node
	 */
	@Test
	public void TestDefineObjectNode1() {
		String actual = parser1.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("parameter_x_objectNode1_t = update_objectNode1.1!(1-0) -> get_x_objectNode1.1?x -> ((oe_x_objectNode1.1!x -> SKIP))\n" +
				"obj1_objectNode1 = ((oe_x_objectNode1.1?x -> set_x_obj1_objectNode1.1!x -> SKIP)); get_x_obj1_objectNode1.2?x -> ((oe_x_objectNode1.2!x -> SKIP)); obj1_objectNode1\n" +
				"obj1_objectNode1_t = ((obj1_objectNode1 /\\ END_DIAGRAM_objectNode1) [|{|get_x_obj1_objectNode1,set_x_obj1_objectNode1,endDiagram_objectNode1|}|] Mem_obj1_objectNode1_x_t(0)) \\{|get_x_obj1_objectNode1,set_x_obj1_objectNode1|}\n" +
				"act1_objectNode1 = ((oe_x_objectNode1.2?w -> set_w_act1_objectNode1.2!w -> SKIP)); event_act1_objectNode1 -> get_w_act1_objectNode1.3?w -> ((((w) >= 0 and (w) <= 1) & oe_x_objectNode1.3!(w) -> SKIP)); act1_objectNode1\n" +
				"act1_objectNode1_t = ((act1_objectNode1 /\\ END_DIAGRAM_objectNode1) [|{|get_w_act1_objectNode1,set_w_act1_objectNode1,endDiagram_objectNode1|}|] Mem_act1_objectNode1_w_t(0)) \\{|get_w_act1_objectNode1,set_w_act1_objectNode1|}\n" +
				"fin1_objectNode1 = ((oe_x_objectNode1.3?x -> SKIP)); clear_objectNode1.1 -> SKIP\n" +
				"fin1_objectNode1_t = fin1_objectNode1 /\\ END_DIAGRAM_objectNode1\n" +
				"init_objectNode1_t = (parameter_x_objectNode1_t) /\\ END_DIAGRAM_objectNode1\n");

		assertEquals(expected.toString(), actual);
	}

	/* Teste de Tradução do Object Node
	 */
	@Test
	public void TestDefineObjectNode2() {
		String actual = parser2.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("parameter_x_objectNode2_t = update_objectNode2.1!(1-0) -> get_x_objectNode2.1?x -> ((oe_x_objectNode2.1!x -> SKIP))\n" +
				"act1_objectNode2 = ((oe_xy_objectNode2.2?w -> set_w_act1_objectNode2.1!w -> SKIP)); event_act1_objectNode2 -> get_w_act1_objectNode2.2?w -> ((((w) >= 0 and (w) <= 1) & oe_xy_objectNode2.3!(w) -> SKIP)); act1_objectNode2\n" +
				"act1_objectNode2_t = ((act1_objectNode2 /\\ END_DIAGRAM_objectNode2) [|{|get_w_act1_objectNode2,set_w_act1_objectNode2,endDiagram_objectNode2|}|] Mem_act1_objectNode2_w_t(0)) \\{|get_w_act1_objectNode2,set_w_act1_objectNode2|}\n" +
				"fin1_objectNode2 = ((oe_xy_objectNode2.3?xy -> SKIP)); clear_objectNode2.1 -> SKIP\n" +
				"fin1_objectNode2_t = fin1_objectNode2 /\\ END_DIAGRAM_objectNode2\n" +
				"parameter_y_objectNode2_t = update_objectNode2.2!(1-0) -> get_y_objectNode2.3?y -> ((oe_y_objectNode2.4!y -> SKIP))\n" +
				"obj1_objectNode2 = ((oe_x_objectNode2.1?x -> set_xy_obj1_objectNode2.2!x -> SKIP) [] (oe_y_objectNode2.4?y -> set_xy_obj1_objectNode2.3!y -> SKIP)); get_xy_obj1_objectNode2.4?xy -> ((oe_xy_objectNode2.2!xy -> SKIP)); obj1_objectNode2\n" +
				"obj1_objectNode2_t = ((obj1_objectNode2 /\\ END_DIAGRAM_objectNode2) [|{|get_xy_obj1_objectNode2,set_xy_obj1_objectNode2,endDiagram_objectNode2|}|] Mem_obj1_objectNode2_xy_t(0)) \\{|get_xy_obj1_objectNode2,set_xy_obj1_objectNode2|}\n" +
				"init_objectNode2_t = (parameter_x_objectNode2_t ||| parameter_y_objectNode2_t) /\\ END_DIAGRAM_objectNode2\n");

		assertEquals(expected.toString(), actual);
	}

	/* Teste de Tradução do Object Node
	 */
	@Test
	public void TestDefineObjectNode3() {
		String actual = parser3.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("parameter_y_objectNode3_t = update_objectNode3.1!(1-0) -> get_y_objectNode3.1?y -> ((oe_y_objectNode3.1!y -> SKIP))\n" +
				"act1_objectNode3 = ((oe_yx_objectNode3.2?w -> set_w_act1_objectNode3.1!w -> SKIP)); event_act1_objectNode3 -> get_w_act1_objectNode3.2?w -> ((((w) >= 0 and (w) <= 1) & oe_yx_objectNode3.4!(w) -> SKIP)); act1_objectNode3\n" +
				"act1_objectNode3_t = ((act1_objectNode3 /\\ END_DIAGRAM_objectNode3) [|{|get_w_act1_objectNode3,set_w_act1_objectNode3,endDiagram_objectNode3|}|] Mem_act1_objectNode3_w_t(0)) \\{|get_w_act1_objectNode3,set_w_act1_objectNode3|}\n" +
				"fin1_objectNode3 = ((oe_yx_objectNode3.4?yx -> SKIP)); clear_objectNode3.1 -> SKIP\n" +
				"fin1_objectNode3_t = fin1_objectNode3 /\\ END_DIAGRAM_objectNode3\n" +
				"parameter_x_objectNode3_t = update_objectNode3.2!(1-0) -> get_x_objectNode3.3?x -> ((oe_x_objectNode3.5!x -> SKIP))\n" +
				"flowFinal1_objectNode3 = ((oe_yx_objectNode3.3?yx -> SKIP)); update_objectNode3.3!(0-1) -> flowFinal1_objectNode3\n" +
				"flowFinal1_objectNode3_t = flowFinal1_objectNode3 /\\ END_DIAGRAM_objectNode3\n" +
				"obj1_objectNode3 = ((oe_y_objectNode3.1?y -> set_yx_obj1_objectNode3.2!y -> SKIP) [] (oe_x_objectNode3.5?x -> set_yx_obj1_objectNode3.3!x -> SKIP)); update_objectNode3.4!(2-1) -> get_yx_obj1_objectNode3.4?yx -> ((oe_yx_objectNode3.2!yx -> SKIP) ||| (oe_yx_objectNode3.3!yx -> SKIP)); obj1_objectNode3\n" +
				"obj1_objectNode3_t = ((obj1_objectNode3 /\\ END_DIAGRAM_objectNode3) [|{|get_yx_obj1_objectNode3,set_yx_obj1_objectNode3,endDiagram_objectNode3|}|] Mem_obj1_objectNode3_yx_t(0)) \\{|get_yx_obj1_objectNode3,set_yx_obj1_objectNode3|}\n" +
				"init_objectNode3_t = (parameter_y_objectNode3_t ||| parameter_x_objectNode3_t) /\\ END_DIAGRAM_objectNode3\n");

		assertEquals(expected.toString(), actual);
	}
}
