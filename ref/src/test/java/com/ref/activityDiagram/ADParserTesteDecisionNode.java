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

public class ADParserTesteDecisionNode {
	
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
			projectAccessor.open("src/test/resources/activityDiagram/decision1.asta");
			INamedElement[] findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser1 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
			projectAccessor.open("src/test/resources/activityDiagram/decision2.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser2 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
			projectAccessor.open("src/test/resources/activityDiagram/decision3.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser3 = new ADParser(ad.getActivity(), ad.getName(), ad);

			projectAccessor.open("src/test/resources/activityDiagram/decision4.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];

			parser4 = new ADParser(ad.getActivity(), ad.getName(), ad);

			projectAccessor.open("src/test/resources/activityDiagram/decision5.asta");
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
	

	@Test
	public void TestNodesDecision1() {
		String actual = parser1.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("init1_decision1_t = update_decision1.1!(1-0) -> ((ce_decision1.1 -> SKIP))\n" +
				"act1_decision1 = ((ce_decision1.2 -> SKIP)); event_act1_decision1 -> ((ce_decision1.4 -> SKIP)); act1_decision1\n" +
				"act1_decision1_t = act1_decision1 /\\ END_DIAGRAM_decision1\n" +
				"parameter_x_decision1_t = update_decision1.2!(1-0) -> get_x_decision1.1?x -> ((oe_x_decision1.1!x -> SKIP))\n" +
				"act2_decision1 = ((ce_decision1.3 -> SKIP)); event_act2_decision1 -> ((ce_decision1.5 -> SKIP)); act2_decision1\n" +
				"act2_decision1_t = act2_decision1 /\\ END_DIAGRAM_decision1\n" +
				"dec1_decision1 = ((ce_decision1.1 -> SKIP) ||| (oe_x_decision1.1?x -> set_x_dec1_decision1.1!x -> SKIP)); update_decision1.3!(1-2) -> get_x_dec1_decision1.2?x -> (x == 1 & (dc -> ce_decision1.2 -> SKIP) [] x == 0 & (dc -> ce_decision1.3 -> SKIP)); dec1_decision1\n" +
				"dec1_decision1_t = ((dec1_decision1 /\\ END_DIAGRAM_decision1) [|{|get_x_dec1_decision1,set_x_dec1_decision1,endDiagram_decision1|}|] Mem_dec1_decision1_x_t(0)) \\{|get_x_dec1_decision1,set_x_dec1_decision1,dc|}\n" +
				"fin1_decision1 = ((ce_decision1.5 -> SKIP) [] (ce_decision1.4 -> SKIP)); clear_decision1.1 -> SKIP\n" +
				"fin1_decision1_t = fin1_decision1 /\\ END_DIAGRAM_decision1\n" +
				"init_decision1_t = (init1_decision1_t ||| parameter_x_decision1_t) /\\ END_DIAGRAM_decision1\n");
		
		assertEquals(expected.toString(), actual);
	}

	@Test
	public void TestNodesDecision2() {
		String actual = parser2.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("init1_decision2_t = update_decision2.1!(1-0) -> ((ce_decision2.1 -> SKIP))\n" +
				"dec1_decision2 = ce_decision2.1 -> ((dc -> ce_decision2.2 -> SKIP) [] (dc -> ce_decision2.3 -> SKIP)); dec1_decision2\n" +
				"dec1_decision2_t = dec1_decision2 /\\ END_DIAGRAM_decision2 \\{|dc|}\n" +
				"act1_decision2 = ((ce_decision2.2 -> SKIP)); event_act1_decision2 -> ((ce_decision2.4 -> SKIP)); act1_decision2\n" +
				"act1_decision2_t = act1_decision2 /\\ END_DIAGRAM_decision2\n" +
				"act2_decision2 = ((ce_decision2.3 -> SKIP)); event_act2_decision2 -> ((ce_decision2.5 -> SKIP)); act2_decision2\n" +
				"act2_decision2_t = act2_decision2 /\\ END_DIAGRAM_decision2\n" +
				"fin1_decision2 = ((ce_decision2.4 -> SKIP) [] (ce_decision2.5 -> SKIP)); clear_decision2.1 -> SKIP\n" +
				"fin1_decision2_t = fin1_decision2 /\\ END_DIAGRAM_decision2\n" +
				"init_decision2_t = (init1_decision2_t) /\\ END_DIAGRAM_decision2\n");
		
		assertEquals(expected.toString(), actual);
	}
	

	@Test
	public void TestNodesDecision3() {
		String actual = parser3.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("parameter_z_decision3_t = update_decision3.1!(1-0) -> get_z_decision3.1?z -> ((oe_z_decision3.1!z -> SKIP))\n" +
				"dec1_decision3 = oe_z_decision3.1?z -> (z > 0 & (dc -> oe_z_decision3.2!z -> SKIP) [] z <= 0 & (dc -> oe_z_decision3.3!z -> SKIP)); dec1_decision3\n" +
				"dec1_decision3_t = (dec1_decision3 /\\ END_DIAGRAM_decision3) \\{|dc|}\n" +
				"act1_decision3 = ((oe_z_decision3.2?z -> set_z_act1_decision3.1!z -> SKIP)); event_act1_decision3 -> get_z_act1_decision3.2?z -> ((((z) >= 0 and (z) <= 1) & oe_z_decision3.4!(z) -> SKIP)); act1_decision3\n" +
				"act1_decision3_t = ((act1_decision3 /\\ END_DIAGRAM_decision3) [|{|get_z_act1_decision3,set_z_act1_decision3,endDiagram_decision3|}|] Mem_act1_decision3_z_t(0)) \\{|get_z_act1_decision3,set_z_act1_decision3|}\n" +
				"act2_decision3 = ((oe_z_decision3.3?z -> set_z_act2_decision3.2!z -> SKIP)); event_act2_decision3 -> get_z_act2_decision3.3?z -> ((((z) >= 0 and (z) <= 1) & oe_z_decision3.5!(z) -> SKIP)); act2_decision3\n" +
				"act2_decision3_t = ((act2_decision3 /\\ END_DIAGRAM_decision3) [|{|get_z_act2_decision3,set_z_act2_decision3,endDiagram_decision3|}|] Mem_act2_decision3_z_t(0)) \\{|get_z_act2_decision3,set_z_act2_decision3|}\n" +
				"fin1_decision3 = ((oe_z_decision3.5?z -> SKIP) [] (oe_z_decision3.4?z -> SKIP)); clear_decision3.1 -> SKIP\n" +
				"fin1_decision3_t = fin1_decision3 /\\ END_DIAGRAM_decision3\n" +
				"init_decision3_t = (parameter_z_decision3_t) /\\ END_DIAGRAM_decision3\n");

		assertEquals(expected.toString(), actual);
	}

	@Test
	public void TestNodesDecision4() {
		String actual = parser4.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("init1_decision4_t = update_decision4.1!(1-0) -> ((ce_decision4.1 -> SKIP))\n" +
				"dec1_decision4 = ce_decision4.1 -> dec1_decision4_guard?teste2?teste1 -> (teste2 & (ce_decision4.2 -> SKIP) [] teste1 & (ce_decision4.3 -> SKIP)); dec1_decision4\n" +
				"dec1_decision4_t = dec1_decision4 /\\ END_DIAGRAM_decision4 \\{|dc|}\n" +
				"act2_decision4 = ((ce_decision4.2 -> SKIP)); event_act2_decision4 -> ((ce_decision4.4 -> SKIP)); act2_decision4\n" +
				"act2_decision4_t = act2_decision4 /\\ END_DIAGRAM_decision4\n" +
				"act1_decision4 = ((ce_decision4.3 -> SKIP)); event_act1_decision4 -> ((ce_decision4.5 -> SKIP)); act1_decision4\n" +
				"act1_decision4_t = act1_decision4 /\\ END_DIAGRAM_decision4\n" +
				"fin1_decision4 = ((ce_decision4.4 -> SKIP) [] (ce_decision4.5 -> SKIP)); clear_decision4.1 -> SKIP\n" +
				"fin1_decision4_t = fin1_decision4 /\\ END_DIAGRAM_decision4\n" +
				"init_decision4_t = (init1_decision4_t) /\\ END_DIAGRAM_decision4\n");

		assertEquals(expected.toString(), actual);
	}

	@Test
	public void TestNodesDecision5() {
		String actual = parser5.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("init1_decision5_t = update_decision5.1!(1-0) -> ((ce_decision5.1 -> SKIP))\n" +
				"dec1_decision5 = ce_decision5.1 -> dec1_decision5_guard?teste2?teste1 -> (teste2 & (ce_decision5.2 -> SKIP) [] teste1 & (ce_decision5.3 -> SKIP) [] not(teste2) and not(teste1) & (ce_decision5.4 -> SKIP)); dec1_decision5\n" +
				"dec1_decision5_t = dec1_decision5 /\\ END_DIAGRAM_decision5 \\{|dc|}\n" +
				"act2_decision5 = ((ce_decision5.2 -> SKIP)); event_act2_decision5 -> ((ce_decision5.5 -> SKIP)); act2_decision5\n" +
				"act2_decision5_t = act2_decision5 /\\ END_DIAGRAM_decision5\n" +
				"act1_decision5 = ((ce_decision5.3 -> SKIP)); event_act1_decision5 -> ((ce_decision5.6 -> SKIP)); act1_decision5\n" +
				"act1_decision5_t = act1_decision5 /\\ END_DIAGRAM_decision5\n" +
				"act3_decision5 = ((ce_decision5.4 -> SKIP)); event_act3_decision5 -> ((ce_decision5.7 -> SKIP)); act3_decision5\n" +
				"act3_decision5_t = act3_decision5 /\\ END_DIAGRAM_decision5\n" +
				"fin1_decision5 = ((ce_decision5.5 -> SKIP) [] (ce_decision5.6 -> SKIP) [] (ce_decision5.7 -> SKIP)); clear_decision5.1 -> SKIP\n" +
				"fin1_decision5_t = fin1_decision5 /\\ END_DIAGRAM_decision5\n" +
				"init_decision5_t = (init1_decision5_t) /\\ END_DIAGRAM_decision5\n");

		assertEquals(expected.toString(), actual);
	}
}
