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
import com.ref.exceptions.ParsingException;
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
	public void TestNodesDecision1() throws ParsingException {
		String actual = parser1.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("init1_decision1_t(id) = update_decision1.id.1!(1-0) -> ((ce_decision1.id.1 -> SKIP))\n" + 
				"act1_decision1(id) = ((ce_decision1.id.2 -> SKIP)); event_act1_decision1.id -> ((ce_decision1.id.4 -> SKIP)); act1_decision1(id)\n" + 
				"act1_decision1_t(id) = act1_decision1(id) /\\ END_DIAGRAM_decision1(id)\n" + 
				"parameter_x_decision1_t(id) = update_decision1.id.2!(1-0) -> get_x_decision1.id.1?x -> ((oe_int_decision1.id.1!x -> SKIP))\n" + 
				"act2_decision1(id) = ((ce_decision1.id.3 -> SKIP)); event_act2_decision1.id -> ((ce_decision1.id.5 -> SKIP)); act2_decision1(id)\n" + 
				"act2_decision1_t(id) = act2_decision1(id) /\\ END_DIAGRAM_decision1(id)\n" + 
				"dec1_decision1(id) = ((ce_decision1.id.1 -> SKIP) ||| (oe_int_decision1.id.1?x -> set_dec1_decision1_dec1_decision1.id.1!x -> SKIP)); update_decision1.id.3!(1-2) -> get_dec1_decision1_dec1_decision1.id.2?x -> (x == 1 & (dc -> ce_decision1.id.2 -> SKIP) [] x == 0 & (dc -> ce_decision1.id.3 -> SKIP)); dec1_decision1(id)\n" + 
				"dec1_decision1_t(id) = ((dec1_decision1(id) /\\ END_DIAGRAM_decision1(id)) [|{|get_dec1_decision1_dec1_decision1,set_dec1_decision1_dec1_decision1,endDiagram_decision1.id|}|] Mem_dec1_decision1_dec1_decision1_t(id,0)) \\{|get_dec1_decision1_dec1_decision1,set_dec1_decision1_dec1_decision1,dc|}\n" + 
				"fin1_decision1(id) = ((ce_decision1.id.5 -> SKIP) [] (ce_decision1.id.4 -> SKIP)); clear_decision1.id.1 -> SKIP\n" + 
				"fin1_decision1_t(id) = fin1_decision1(id) /\\ END_DIAGRAM_decision1(id)\n" + 
				"init_decision1_t(id) = (init1_decision1_t(id) ||| parameter_x_decision1_t(id)) /\\ END_DIAGRAM_decision1(id)");
		
		assertEquals(expected.toString(), actual);
	}

	@Test
	public void TestNodesDecision2() throws ParsingException {
		String actual = parser2.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("init1_decision2_t(id) = update_decision2.id.1!(1-0) -> ((ce_decision2.id.1 -> SKIP))\n" + 
				"dec1_decision2(id) = ce_decision2.id.1 -> ((dc -> ce_decision2.id.2 -> SKIP) [] (dc -> ce_decision2.id.3 -> SKIP)); dec1_decision2(id)\r\n" + 
				"dec1_decision2_t(id) = dec1_decision2(id) /\\ END_DIAGRAM_decision2(id) \\{|dc|}\n" + 
				"act1_decision2(id) = ((ce_decision2.id.2 -> SKIP)); event_act1_decision2.id -> ((ce_decision2.id.4 -> SKIP)); act1_decision2(id)\r\n" + 
				"act1_decision2_t(id) = act1_decision2(id) /\\ END_DIAGRAM_decision2(id)\n" + 
				"act2_decision2(id) = ((ce_decision2.id.3 -> SKIP)); event_act2_decision2.id -> ((ce_decision2.id.5 -> SKIP)); act2_decision2(id)\r\n" + 
				"act2_decision2_t(id) = act2_decision2(id) /\\ END_DIAGRAM_decision2(id)\n" + 
				"fin1_decision2(id) = ((ce_decision2.id.4 -> SKIP) [] (ce_decision2.id.5 -> SKIP)); clear_decision2.id.1 -> SKIP\n" + 
				"fin1_decision2_t(id) = fin1_decision2(id) /\\ END_DIAGRAM_decision2(id)\n" + 
				"init_decision2_t(id) = (init1_decision2_t(id)) /\\ END_DIAGRAM_decision2(id)");
		
		assertEquals(expected.toString(), actual);
	}
	

	@Test
	public void TestNodesDecision3() throws ParsingException {
		String actual = parser3.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("parameter_z_decision3_t(id) = update_decision3.id.1!(1-0) -> get_z_decision3.id.1?z -> ((oe_int_decision3.id.1!z -> SKIP))\n" + 
				"dec1_decision3(id) = oe_int_decision3.id.1?z -> (z > 0 & (dc -> oe_int_decision3.id.2!z -> SKIP) [] z <= 0 & (dc -> oe_int_decision3.id.3!z -> SKIP)); dec1_decision3(id)\n" + 
				"dec1_decision3_t(id) = (dec1_decision3(id) /\\ END_DIAGRAM_decision3(id)) \\{|dc|}\n" + 
				"act1_decision3(id) = ((oe_int_decision3.id.2?z -> set_z_act1_decision3.id.1!z -> SKIP)); event_act1_decision3.id -> get_z_act1_decision3.id.2?z -> ((((z) >= 0 and (z) <= 1) & oe_int_decision3.id.4!(z) -> SKIP)); act1_decision3(id)\n" + 
				"act1_decision3_t(id) = ((act1_decision3(id) /\\ END_DIAGRAM_decision3(id)) [|{|get_z_act1_decision3.id,set_z_act1_decision3.id,endDiagram_decision3.id|}|] Mem_act1_decision3_z_t(id,0)) \\{|get_z_act1_decision3.id,set_z_act1_decision3.id|}\n" + 
				"act2_decision3(id) = ((oe_int_decision3.id.3?z -> set_z_act2_decision3.id.2!z -> SKIP)); event_act2_decision3.id -> get_z_act2_decision3.id.3?z -> ((((z) >= 0 and (z) <= 1) & oe_int_decision3.id.5!(z) -> SKIP)); act2_decision3(id)\n" + 
				"act2_decision3_t(id) = ((act2_decision3(id) /\\ END_DIAGRAM_decision3(id)) [|{|get_z_act2_decision3.id,set_z_act2_decision3.id,endDiagram_decision3.id|}|] Mem_act2_decision3_z_t(id,0)) \\{|get_z_act2_decision3.id,set_z_act2_decision3.id|}\n" + 
				"fin1_decision3(id) = ((oe_int_decision3.id.5?w -> SKIP) [] (oe_int_decision3.id.4?w -> SKIP)); clear_decision3.id.1 -> SKIP\n" + 
				"fin1_decision3_t(id) = fin1_decision3(id) /\\ END_DIAGRAM_decision3(id)\n" + 
				"init_decision3_t(id) = (parameter_z_decision3_t(id)) /\\ END_DIAGRAM_decision3(id)");

		assertEquals(expected.toString(), actual);
	}

	@Test
	public void TestNodesDecision4() throws ParsingException {
		String actual = parser4.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("init1_decision4_t(id) = update_decision4.id.1!(1-0) -> ((ce_decision4.id.1 -> SKIP))\n" + 
				"dec1_decision4(id) = ce_decision4.id.1 -> dec1_decision4_guard?teste2?teste1 -> (teste2 & (ce_decision4.id.2 -> SKIP) [] teste1 & (ce_decision4.id.3 -> SKIP)); dec1_decision4(id)\n" + 
				"dec1_decision4_t(id) = dec1_decision4(id) /\\ END_DIAGRAM_decision4(id) \\{|dc|}\n" + 
				"act2_decision4(id) = ((ce_decision4.id.2 -> SKIP)); event_act2_decision4.id -> ((ce_decision4.id.4 -> SKIP)); act2_decision4(id)\n" + 
				"act2_decision4_t(id) = act2_decision4(id) /\\ END_DIAGRAM_decision4(id)\n" + 
				"act1_decision4(id) = ((ce_decision4.id.3 -> SKIP)); event_act1_decision4.id -> ((ce_decision4.id.5 -> SKIP)); act1_decision4(id)\n" + 
				"act1_decision4_t(id) = act1_decision4(id) /\\ END_DIAGRAM_decision4(id)\n" + 
				"fin1_decision4(id) = ((ce_decision4.id.4 -> SKIP) [] (ce_decision4.id.5 -> SKIP)); clear_decision4.id.1 -> SKIP\n" + 
				"fin1_decision4_t(id) = fin1_decision4(id) /\\ END_DIAGRAM_decision4(id)\n" + 
				"init_decision4_t(id) = (init1_decision4_t(id)) /\\ END_DIAGRAM_decision4(id)");

		assertEquals(expected.toString(), actual);
	}

	@Test
	public void TestNodesDecision5() throws ParsingException {
		String actual = parser5.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("init1_decision5_t(id) = update_decision5.id.1!(1-0) -> ((ce_decision5.id.1 -> SKIP))\n" + 
				"dec1_decision5(id) = ce_decision5.id.1 -> dec1_decision5_guard?teste2?teste1 -> (teste2 & (ce_decision5.id.2 -> SKIP) [] teste1 & (ce_decision5.id.3 -> SKIP) [] not(teste2) and not(teste1) & (ce_decision5.id.4 -> SKIP)); dec1_decision5(id)\n" + 
				"dec1_decision5_t(id) = dec1_decision5(id) /\\ END_DIAGRAM_decision5(id) \\{|dc|}\n" + 
				"act2_decision5(id) = ((ce_decision5.id.2 -> SKIP)); event_act2_decision5.id -> ((ce_decision5.id.5 -> SKIP)); act2_decision5(id)\n" + 
				"act2_decision5_t(id) = act2_decision5(id) /\\ END_DIAGRAM_decision5(id)\n" + 
				"act1_decision5(id) = ((ce_decision5.id.3 -> SKIP)); event_act1_decision5.id -> ((ce_decision5.id.6 -> SKIP)); act1_decision5(id)\n" + 
				"act1_decision5_t(id) = act1_decision5(id) /\\ END_DIAGRAM_decision5(id)\n" + 
				"act3_decision5(id) = ((ce_decision5.id.4 -> SKIP)); event_act3_decision5.id -> ((ce_decision5.id.7 -> SKIP)); act3_decision5(id)\n" + 
				"act3_decision5_t(id) = act3_decision5(id) /\\ END_DIAGRAM_decision5(id)\n" + 
				"fin1_decision5(id) = ((ce_decision5.id.5 -> SKIP) [] (ce_decision5.id.6 -> SKIP) [] (ce_decision5.id.7 -> SKIP)); clear_decision5.id.1 -> SKIP\n" + 
				"fin1_decision5_t(id) = fin1_decision5(id) /\\ END_DIAGRAM_decision5(id)\n" + 
				"init_decision5_t(id) = (init1_decision5_t(id)) /\\ END_DIAGRAM_decision5(id)");

		assertEquals(expected.toString(), actual);
	}
}
