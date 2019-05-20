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

public class ADParserTestSignal {

	public static IActivityDiagram ad;
	private static ADParser parser1;
	private static ADParser parser2;
	private static ADParser parser3;
	private static ADParser parser4;

	
	@BeforeClass
	public static void GetDiagram() throws Exception {
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/signal1.asta");
			INamedElement[] findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser1 = new ADParser(ad.getActivity(), ad.getName(), ad);

			projectAccessor.open("src/test/resources/activityDiagram/signal2.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];

			parser2 = new ADParser(ad.getActivity(), ad.getName(), ad);

			projectAccessor.open("src/test/resources/activityDiagram/signal3.asta");
			findElements = findElements(projectAccessor);

			for (INamedElement diagram: findElements) {
				if (diagram.getName().equals("signal3_1")) {
					ad = (IActivityDiagram) diagram;
				}
			}

			parser3 = new ADParser(ad.getActivity(), ad.getName(), ad);

			projectAccessor.open("src/test/resources/activityDiagram/signal4.asta");
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

	@AfterClass
	public static void CloseProject() throws Exception {
		AstahAPI.getAstahAPI().getProjectAccessor().close();
	}
	
	@Test
	public void testSignal1() {
		parser1.clearBuffer();
		String atual = parser1.parserDiagram();
		String expected = "ID_signal1 = {1..1}\n" +
				"datatype T = lock | unlock\n" +
				"countSignal_signal_signal1 = {1..1}\n" +
				"countAccept_signal_signal1 = {1..1}\n" +
				"datatype alphabet_signal1 = init_signal1_t_alphabet | signal_signal1_1_signal1_t_alphabet| fin1_signal1_t_alphabet| accept_signal1_1_signal1_t_alphabet\n" +
				"countCe_signal1 = {1..2}\n" +
				"countUpdate_signal1 = {1..1}\n" +
				"countClear_signal1 = {1..1}\n" +
				"limiteUpdate_signal1 = {(1)..(1)}\n" +
				"channel startActivity_signal1: ID_signal1\n" +
				"channel endActivity_signal1: ID_signal1\n" +
				"channel ce_signal1: countCe_signal1\n" +
				"channel clear_signal1: countClear_signal1\n" +
				"channel update_signal1: countUpdate_signal1.limiteUpdate_signal1\n" +
				"channel endDiagram_signal1\n" +
				"channel signal_signal1: countSignal_signal_signal1.countAccept_signal_signal1\n" +
				"channel loop\n" +
				"channel dc\n" +
				"MAIN = signal1(1); LOOP\n" +
				"LOOP = loop -> LOOP\n" +
				"END_DIAGRAM_signal1 = endDiagram_signal1 -> SKIP\n" +
				"signal1(ID_signal1) = (Internal_signal1(ID_signal1) [|{|update_signal1,clear_signal1,endDiagram_signal1|}|] TokenManager_signal1_t(0,0))\n" +
				"Internal_signal1(ID_signal1) = StartActivity_signal1(ID_signal1); Node_signal1; EndActivity_signal1(ID_signal1)\n" +
				"StartActivity_signal1(ID_signal1) = startActivity_signal1.ID_signal1 -> SKIP\n" +
				"EndActivity_signal1(ID_signal1) = endActivity_signal1.ID_signal1 -> SKIP\n" +
				"A_signal1(init_signal1_t_alphabet) = {|update_signal1.1,ce_signal1.2,endDiagram_signal1|}\n" +
				"A_signal1(signal_signal1_1_signal1_t_alphabet) = {|ce_signal1.2,signal_signal1,endDiagram_signal1|}\n" +
				"A_signal1(fin1_signal1_t_alphabet) = {|ce_signal1.1,clear_signal1.1,endDiagram_signal1|}\n" +
				"A_signal1(accept_signal1_1_signal1_t_alphabet) = {|signal_signal1,ce_signal1.1,endDiagram_signal1|}\n" +
				"P_signal1(init_signal1_t_alphabet) = init_signal1_t\n" +
				"P_signal1(signal_signal1_1_signal1_t_alphabet) = signal_signal1_1_signal1_t\n" +
				"P_signal1(fin1_signal1_t_alphabet) = fin1_signal1_t\n" +
				"P_signal1(accept_signal1_1_signal1_t_alphabet) = accept_signal1_1_signal1_t\n" +
				"Node_signal1 = || x:alphabet_signal1 @ [A_signal1(x)] P_signal1(x)\n" +
				"accept_signal1_1_signal1 = signal_signal1?x!1 -> ((ce_signal1.1 -> SKIP)); accept_signal1_1_signal1\n" +
				"accept_signal1_1_signal1_t = accept_signal1_1_signal1 /\\ END_DIAGRAM_signal1\n" +
				"fin1_signal1 = ((ce_signal1.1 -> SKIP)); clear_signal1.1 -> SKIP\n" +
				"fin1_signal1_t = fin1_signal1 /\\ END_DIAGRAM_signal1\n" +
				"init1_signal1_t = update_signal1.1!(1-0) -> ((ce_signal1.2 -> SKIP))\n" +
				"signal_signal1_1_signal1 = ((ce_signal1.2 -> SKIP)); ((signal_signal1!1?x -> SKIP) [] (SKIP)); signal_signal1_1_signal1\n" +
				"signal_signal1_1_signal1_t = signal_signal1_1_signal1 /\\ END_DIAGRAM_signal1\n" +
				"init_signal1_t = (init1_signal1_t) /\\ END_DIAGRAM_signal1\n" +
				"\n" +
				"TokenManager_signal1(x,init) = update_signal1?c?y:limiteUpdate_signal1 -> x+y < 10 & x+y > -10 & TokenManager_signal1(x+y,1) [] clear_signal1?c -> endDiagram_signal1 -> SKIP [] x == 0 & init == 1 & endDiagram_signal1 -> SKIP\n" +
				"TokenManager_signal1_t(x,init) = TokenManager_signal1(x,init)\n" +
				"\n" +
				"assert MAIN :[deadlock free]\n" +
				"assert MAIN :[divergence free]\n" +
				"assert MAIN :[deterministic]";
		assertEquals(expected, atual);
	}

	@Test
	public void testSignal2() {
		parser2.clearBuffer();
		String atual = parser2.parserDiagram();
		String expected = "ID_signal2 = {1..1}\n" +
				"datatype T = lock | unlock\n" +
				"countSignal_signal_signal1 = {1..1}\n" +
				"countAccept_signal_signal1 = {1..1}\n" +
				"datatype alphabet_signal2 = init_signal2_t_alphabet | signal_signal1_1_signal2_t_alphabet| fin1_signal2_t_alphabet| accept_signal1_1_signal2_t_alphabet\n" +
				"countCe_signal2 = {1..3}\n" +
				"countUpdate_signal2 = {1..2}\n" +
				"countClear_signal2 = {1..1}\n" +
				"limiteUpdate_signal2 = {(1)..(1)}\n" +
				"channel startActivity_signal2: ID_signal2\n" +
				"channel endActivity_signal2: ID_signal2\n" +
				"channel ce_signal2: countCe_signal2\n" +
				"channel clear_signal2: countClear_signal2\n" +
				"channel update_signal2: countUpdate_signal2.limiteUpdate_signal2\n" +
				"channel endDiagram_signal2\n" +
				"channel signal_signal1: countSignal_signal_signal1.countAccept_signal_signal1\n" +
				"channel loop\n" +
				"channel dc\n" +
				"MAIN = signal2(1); LOOP\n" +
				"LOOP = loop -> LOOP\n" +
				"END_DIAGRAM_signal2 = endDiagram_signal2 -> SKIP\n" +
				"signal2(ID_signal2) = (Internal_signal2(ID_signal2) [|{|update_signal2,clear_signal2,endDiagram_signal2|}|] TokenManager_signal2_t(0,0))\n" +
				"Internal_signal2(ID_signal2) = StartActivity_signal2(ID_signal2); Node_signal2; EndActivity_signal2(ID_signal2)\n" +
				"StartActivity_signal2(ID_signal2) = startActivity_signal2.ID_signal2 -> SKIP\n" +
				"EndActivity_signal2(ID_signal2) = endActivity_signal2.ID_signal2 -> SKIP\n" +
				"A_signal2(init_signal2_t_alphabet) = {|update_signal2.1,ce_signal2.1,update_signal2.2,ce_signal2.2,endDiagram_signal2|}\n" +
				"A_signal2(signal_signal1_1_signal2_t_alphabet) = {|ce_signal2.1,ce_signal2.2,signal_signal1,endDiagram_signal2|}\n" +
				"A_signal2(fin1_signal2_t_alphabet) = {|ce_signal2.3,clear_signal2.1,endDiagram_signal2|}\n" +
				"A_signal2(accept_signal1_1_signal2_t_alphabet) = {|signal_signal1,ce_signal2.3,endDiagram_signal2|}\n" +
				"P_signal2(init_signal2_t_alphabet) = init_signal2_t\n" +
				"P_signal2(signal_signal1_1_signal2_t_alphabet) = signal_signal1_1_signal2_t\n" +
				"P_signal2(fin1_signal2_t_alphabet) = fin1_signal2_t\n" +
				"P_signal2(accept_signal1_1_signal2_t_alphabet) = accept_signal1_1_signal2_t\n" +
				"Node_signal2 = || x:alphabet_signal2 @ [A_signal2(x)] P_signal2(x)\n" +
				"init1_signal2_t = update_signal2.1!(1-0) -> ((ce_signal2.1 -> SKIP))\n" +
				"init2_signal2_t = update_signal2.2!(1-0) -> ((ce_signal2.2 -> SKIP))\n" +
				"accept_signal1_1_signal2 = signal_signal1?x!1 -> ((ce_signal2.3 -> SKIP)); accept_signal1_1_signal2\n" +
				"accept_signal1_1_signal2_t = accept_signal1_1_signal2 /\\ END_DIAGRAM_signal2\n" +
				"fin1_signal2 = ((ce_signal2.3 -> SKIP)); clear_signal2.1 -> SKIP\n" +
				"fin1_signal2_t = fin1_signal2 /\\ END_DIAGRAM_signal2\n" +
				"signal_signal1_1_signal2 = ((ce_signal2.1 -> SKIP) ||| (ce_signal2.2 -> SKIP)); ((signal_signal1!1?x -> SKIP) [] (SKIP)); signal_signal1_1_signal2\n" +
				"signal_signal1_1_signal2_t = signal_signal1_1_signal2 /\\ END_DIAGRAM_signal2\n" +
				"init_signal2_t = (init1_signal2_t ||| init2_signal2_t) /\\ END_DIAGRAM_signal2\n" +
				"\n" +
				"TokenManager_signal2(x,init) = update_signal2?c?y:limiteUpdate_signal2 -> x+y < 10 & x+y > -10 & TokenManager_signal2(x+y,1) [] clear_signal2?c -> endDiagram_signal2 -> SKIP [] x == 0 & init == 1 & endDiagram_signal2 -> SKIP\n" +
				"TokenManager_signal2_t(x,init) = TokenManager_signal2(x,init)\n" +
				"\n" +
				"assert MAIN :[deadlock free]\n" +
				"assert MAIN :[divergence free]\n" +
				"assert MAIN :[deterministic]";
		assertEquals(expected, atual);
	}

	@Test
	public void testSignal3() {
		parser3.clearBuffer();
		String atual = parser3.parserDiagram();
		String expected = "ID_signal3_1 = {1..1}\n" +
				"ID_signal3_2 = {1..1}\n" +
				"datatype T = lock | unlock\n" +
				"countSignal_signal_signal1 = {1..1}\n" +
				"countAccept_signal_signal1 = {1..1}\n" +
				"datatype alphabet_signal3_1 = init_signal3_1_t_alphabet | call1_signal3_1_t_alphabet| signal_signal1_1_signal3_1_t_alphabet| fin1_signal3_1_t_alphabet\n" +
				"countCe_signal3_1 = {1..3}\n" +
				"countUpdate_signal3_1 = {1..1}\n" +
				"countClear_signal3_1 = {1..1}\n" +
				"limiteUpdate_signal3_1 = {(2)..(2)}\n" +
				"channel startActivity_signal3_1: ID_signal3_1\n" +
				"channel endActivity_signal3_1: ID_signal3_1\n" +
				"channel ce_signal3_1: countCe_signal3_1\n" +
				"channel clear_signal3_1: countClear_signal3_1\n" +
				"channel update_signal3_1: countUpdate_signal3_1.limiteUpdate_signal3_1\n" +
				"channel endDiagram_signal3_1\n" +
				"channel signal_signal1: countSignal_signal_signal1.countAccept_signal_signal1\n" +
				"channel loop\n" +
				"channel dc\n" +
				"MAIN = signal3_1(1); LOOP\n" +
				"LOOP = loop -> LOOP\n" +
				"END_DIAGRAM_signal3_1 = endDiagram_signal3_1 -> SKIP\n" +
				"signal3_1(ID_signal3_1) = ((Internal_signal3_1(ID_signal3_1) [|{|startActivity_signal3_2.1,endActivity_signal3_2.1,signal_signal1|}|] signal3_2(1)) [|{|update_signal3_1,clear_signal3_1,endDiagram_signal3_1|}|] TokenManager_signal3_1_t(0,0))\n" +
				"Internal_signal3_1(ID_signal3_1) = StartActivity_signal3_1(ID_signal3_1); Node_signal3_1; EndActivity_signal3_1(ID_signal3_1)\n" +
				"StartActivity_signal3_1(ID_signal3_1) = startActivity_signal3_1.ID_signal3_1 -> SKIP\n" +
				"EndActivity_signal3_1(ID_signal3_1) = endActivity_signal3_1.ID_signal3_1 -> SKIP\n" +
				"A_signal3_1(init_signal3_1_t_alphabet) = {|update_signal3_1.1,ce_signal3_1.1,ce_signal3_1.2,endDiagram_signal3_1|}\n" +
				"A_signal3_1(call1_signal3_1_t_alphabet) = {|ce_signal3_1.2,startActivity_signal3_2.1,endActivity_signal3_2.1,ce_signal3_1.3,endDiagram_signal3_1|}\n" +
				"A_signal3_1(signal_signal1_1_signal3_1_t_alphabet) = {|ce_signal3_1.1,signal_signal1,endDiagram_signal3_1|}\n" +
				"A_signal3_1(fin1_signal3_1_t_alphabet) = {|ce_signal3_1.3,clear_signal3_1.1,endDiagram_signal3_1|}\n" +
				"P_signal3_1(init_signal3_1_t_alphabet) = init_signal3_1_t\n" +
				"P_signal3_1(call1_signal3_1_t_alphabet) = call1_signal3_1_t\n" +
				"P_signal3_1(signal_signal1_1_signal3_1_t_alphabet) = signal_signal1_1_signal3_1_t\n" +
				"P_signal3_1(fin1_signal3_1_t_alphabet) = fin1_signal3_1_t\n" +
				"Node_signal3_1 = || x:alphabet_signal3_1 @ [A_signal3_1(x)] P_signal3_1(x)\n" +
				"init1_signal3_1_t = update_signal3_1.1!(2-0) -> ((ce_signal3_1.1 -> SKIP) ||| (ce_signal3_1.2 -> SKIP))\n" +
				"signal_signal1_1_signal3_1 = ((ce_signal3_1.1 -> SKIP)); ((signal_signal1!1?x -> SKIP) [] (SKIP)); signal_signal1_1_signal3_1\n" +
				"signal_signal1_1_signal3_1_t = signal_signal1_1_signal3_1 /\\ END_DIAGRAM_signal3_1\n" +
				"call1_signal3_1 = ((ce_signal3_1.2 -> SKIP)); startActivity_signal3_2.1 -> endActivity_signal3_2.1 -> ((ce_signal3_1.3 -> SKIP)); call1_signal3_1\n" +
				"call1_signal3_1_t = call1_signal3_1 /\\ END_DIAGRAM_signal3_1\n" +
				"fin1_signal3_1 = ((ce_signal3_1.3 -> SKIP)); clear_signal3_1.1 -> SKIP\n" +
				"fin1_signal3_1_t = fin1_signal3_1 /\\ END_DIAGRAM_signal3_1\n" +
				"init_signal3_1_t = (init1_signal3_1_t) /\\ END_DIAGRAM_signal3_1\n" +
				"\n" +
				"TokenManager_signal3_1(x,init) = update_signal3_1?c?y:limiteUpdate_signal3_1 -> x+y < 10 & x+y > -10 & TokenManager_signal3_1(x+y,1) [] clear_signal3_1?c -> endDiagram_signal3_1 -> SKIP [] x == 0 & init == 1 & endDiagram_signal3_1 -> SKIP\n" +
				"TokenManager_signal3_1_t(x,init) = TokenManager_signal3_1(x,init)\n" +
				"\n" +
				"datatype alphabet_signal3_2 = fin1_signal3_2_t_alphabet | accept_signal1_1_signal3_2_t_alphabet\n" +
				"countCe_signal3_2 = {1..1}\n" +
				"countUpdate_signal3_2 = {1..0}\n" +
				"countClear_signal3_2 = {1..1}\n" +
				"limiteUpdate_signal3_2 = {(99)..(-99)}\n" +
				"channel startActivity_signal3_2: ID_signal3_2\n" +
				"channel endActivity_signal3_2: ID_signal3_2\n" +
				"channel ce_signal3_2: countCe_signal3_2\n" +
				"channel clear_signal3_2: countClear_signal3_2\n" +
				"channel update_signal3_2: countUpdate_signal3_2.limiteUpdate_signal3_2\n" +
				"channel endDiagram_signal3_2\n" +
				"END_DIAGRAM_signal3_2 = endDiagram_signal3_2 -> SKIP\n" +
				"signal3_2(ID_signal3_2) = (Internal_signal3_2(ID_signal3_2) [|{|update_signal3_2,clear_signal3_2,endDiagram_signal3_2|}|] TokenManager_signal3_2_t(0,0))\n" +
				"Internal_signal3_2(ID_signal3_2) = StartActivity_signal3_2(ID_signal3_2); Node_signal3_2; EndActivity_signal3_2(ID_signal3_2)\n" +
				"StartActivity_signal3_2(ID_signal3_2) = startActivity_signal3_2.ID_signal3_2 -> SKIP\n" +
				"EndActivity_signal3_2(ID_signal3_2) = endActivity_signal3_2.ID_signal3_2 -> SKIP\n" +
				"A_signal3_2(fin1_signal3_2_t_alphabet) = {|ce_signal3_2.1,clear_signal3_2.1,endDiagram_signal3_2|}\n" +
				"A_signal3_2(accept_signal1_1_signal3_2_t_alphabet) = {|signal_signal1,ce_signal3_2.1,endDiagram_signal3_2|}\n" +
				"P_signal3_2(fin1_signal3_2_t_alphabet) = fin1_signal3_2_t\n" +
				"P_signal3_2(accept_signal1_1_signal3_2_t_alphabet) = accept_signal1_1_signal3_2_t\n" +
				"Node_signal3_2 = || x:alphabet_signal3_2 @ [A_signal3_2(x)] P_signal3_2(x)\n" +
				"accept_signal1_1_signal3_2 = signal_signal1?x!1 -> ((ce_signal3_2.1 -> SKIP)); accept_signal1_1_signal3_2\n" +
				"accept_signal1_1_signal3_2_t = accept_signal1_1_signal3_2 /\\ END_DIAGRAM_signal3_2\n" +
				"fin1_signal3_2 = ((ce_signal3_2.1 -> SKIP)); clear_signal3_2.1 -> SKIP\n" +
				"fin1_signal3_2_t = fin1_signal3_2 /\\ END_DIAGRAM_signal3_2\n" +
				"\n" +
				"\n" +
				"TokenManager_signal3_2(x,init) = update_signal3_2?c?y:limiteUpdate_signal3_2 -> x+y < 10 & x+y > -10 & TokenManager_signal3_2(x+y,1) [] clear_signal3_2?c -> endDiagram_signal3_2 -> SKIP [] x == 0 & init == 1 & endDiagram_signal3_2 -> SKIP\n" +
				"TokenManager_signal3_2_t(x,init) = TokenManager_signal3_2(x,init)\n" +
				"\n" +
				"assert MAIN :[deadlock free]\n" +
				"assert MAIN :[divergence free]\n" +
				"assert MAIN :[deterministic]";
		assertEquals(expected, atual);
	}

	@Test
	public void testSignal4() {
		parser4.clearBuffer();
		String atual = parser4.parserDiagram();
		String expected = "ID_signal4 = {1..1}\n" +
				"datatype T = lock | unlock\n" +
				"countSignal_signal_signal1 = {1..1}\n" +
				"countAccept_signal_signal1 = {1..1}\n" +
				"datatype alphabet_signal4 = init_signal4_t_alphabet | act1_signal4_t_alphabet| signal_signal1_1_signal4_t_alphabet| fin1_signal4_t_alphabet| accept_signal1_1_signal4_t_alphabet| act2_signal4_t_alphabet\n" +
				"countCe_signal4 = {1..5}\n" +
				"countUpdate_signal4 = {1..2}\n" +
				"countClear_signal4 = {1..1}\n" +
				"limiteUpdate_signal4 = {(1)..(1)}\n" +
				"channel startActivity_signal4: ID_signal4\n" +
				"channel endActivity_signal4: ID_signal4\n" +
				"channel ce_signal4: countCe_signal4\n" +
				"channel clear_signal4: countClear_signal4\n" +
				"channel update_signal4: countUpdate_signal4.limiteUpdate_signal4\n" +
				"channel endDiagram_signal4\n" +
				"channel event_act1_signal4,event_act2_signal4\n" +
				"channel signal_signal1: countSignal_signal_signal1.countAccept_signal_signal1\n" +
				"channel loop\n" +
				"channel dc\n" +
				"MAIN = signal4(1); LOOP\n" +
				"LOOP = loop -> LOOP\n" +
				"END_DIAGRAM_signal4 = endDiagram_signal4 -> SKIP\n" +
				"signal4(ID_signal4) = (Internal_signal4(ID_signal4) [|{|update_signal4,clear_signal4,endDiagram_signal4|}|] TokenManager_signal4_t(0,0))\n" +
				"Internal_signal4(ID_signal4) = StartActivity_signal4(ID_signal4); Node_signal4; EndActivity_signal4(ID_signal4)\n" +
				"StartActivity_signal4(ID_signal4) = startActivity_signal4.ID_signal4 -> SKIP\n" +
				"EndActivity_signal4(ID_signal4) = endActivity_signal4.ID_signal4 -> SKIP\n" +
				"A_signal4(init_signal4_t_alphabet) = {|update_signal4.1,ce_signal4.1,endDiagram_signal4|}\n" +
				"A_signal4(act1_signal4_t_alphabet) = {|ce_signal4.2,event_act1_signal4,ce_signal4.4,endDiagram_signal4|}\n" +
				"A_signal4(signal_signal1_1_signal4_t_alphabet) = {|ce_signal4.1,signal_signal1,endDiagram_signal4|}\n" +
				"A_signal4(fin1_signal4_t_alphabet) = {|ce_signal4.4,ce_signal4.5,clear_signal4.1,endDiagram_signal4|}\n" +
				"A_signal4(accept_signal1_1_signal4_t_alphabet) = {|signal_signal1,update_signal4.2,ce_signal4.2,ce_signal4.3,endDiagram_signal4|}\n" +
				"A_signal4(act2_signal4_t_alphabet) = {|ce_signal4.3,event_act2_signal4,ce_signal4.5,endDiagram_signal4|}\n" +
				"P_signal4(init_signal4_t_alphabet) = init_signal4_t\n" +
				"P_signal4(act1_signal4_t_alphabet) = act1_signal4_t\n" +
				"P_signal4(signal_signal1_1_signal4_t_alphabet) = signal_signal1_1_signal4_t\n" +
				"P_signal4(fin1_signal4_t_alphabet) = fin1_signal4_t\n" +
				"P_signal4(accept_signal1_1_signal4_t_alphabet) = accept_signal1_1_signal4_t\n" +
				"P_signal4(act2_signal4_t_alphabet) = act2_signal4_t\n" +
				"Node_signal4 = || x:alphabet_signal4 @ [A_signal4(x)] P_signal4(x)\n" +
				"init1_signal4_t = update_signal4.1!(1-0) -> ((ce_signal4.1 -> SKIP))\n" +
				"signal_signal1_1_signal4 = ((ce_signal4.1 -> SKIP)); ((signal_signal1!1?x -> SKIP) [] (SKIP)); signal_signal1_1_signal4\n" +
				"signal_signal1_1_signal4_t = signal_signal1_1_signal4 /\\ END_DIAGRAM_signal4\n" +
				"accept_signal1_1_signal4 = signal_signal1?x!1 -> update_signal4.2!(2-1) -> ((ce_signal4.2 -> SKIP) ||| (ce_signal4.3 -> SKIP)); accept_signal1_1_signal4\n" +
				"accept_signal1_1_signal4_t = accept_signal1_1_signal4 /\\ END_DIAGRAM_signal4\n" +
				"act1_signal4 = ((ce_signal4.2 -> SKIP)); event_act1_signal4 -> ((ce_signal4.4 -> SKIP)); act1_signal4\n" +
				"act1_signal4_t = act1_signal4 /\\ END_DIAGRAM_signal4\n" +
				"act2_signal4 = ((ce_signal4.3 -> SKIP)); event_act2_signal4 -> ((ce_signal4.5 -> SKIP)); act2_signal4\n" +
				"act2_signal4_t = act2_signal4 /\\ END_DIAGRAM_signal4\n" +
				"fin1_signal4 = ((ce_signal4.4 -> SKIP) [] (ce_signal4.5 -> SKIP)); clear_signal4.1 -> SKIP\n" +
				"fin1_signal4_t = fin1_signal4 /\\ END_DIAGRAM_signal4\n" +
				"init_signal4_t = (init1_signal4_t) /\\ END_DIAGRAM_signal4\n" +
				"\n" +
				"TokenManager_signal4(x,init) = update_signal4?c?y:limiteUpdate_signal4 -> x+y < 10 & x+y > -10 & TokenManager_signal4(x+y,1) [] clear_signal4?c -> endDiagram_signal4 -> SKIP [] x == 0 & init == 1 & endDiagram_signal4 -> SKIP\n" +
				"TokenManager_signal4_t(x,init) = TokenManager_signal4(x,init)\n" +
				"\n" +
				"assert MAIN :[deadlock free]\n" +
				"assert MAIN :[divergence free]\n" +
				"assert MAIN :[deterministic]";
		assertEquals(expected, atual);
	}

}
