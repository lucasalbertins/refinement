package com.ref.activityDiagram.parser;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
//import com.change_vision.jude.api.inf.model.IActivityDiagram;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.project.ModelFinder;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.ref.exceptions.ParsingException;
import com.ref.parser.activityDiagram.ADParser;
import com.ref.interfaces.activityDiagram.IActivityDiagram;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ADParserTestSignal {

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

			projectAccessor.open("src/test/resources/activityDiagram/signal6.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];

			parser5 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
			projectAccessor.open("src/test/resources/activityDiagram/CalibrateSimple.asta");
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

	@AfterClass
	public static void CloseProject() throws Exception {
		AstahAPI.getAstahAPI().getProjectAccessor().close();
	}
	
	@Test
	public void testSignal1() throws ParsingException {
		parser1.clearBuffer();
		String atual = parser1.parserDiagram();
		String expected = "ID_signal1 = {1..1}\n" +
				"datatype T = lock | unlock\n" +
				"countSignal_signal = {1..1}\n" +
				"countAccept_signal = {1..1}\n" +
				//"datatype event_signal_signal1 = Int\n" +
				//"event_signal_signal1 = Int\n" +
				"datatype alphabet_signal1 = init_signal1_t_alphabet | signal_signal_1_signal1_t_alphabet| accept_signal_1_signal1_t_alphabet| fin1_signal1_t_alphabet\n" +
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
				"channel signal_signal: countSignal_signal\n" +
				"channel accept_signal: countAccept_signal.countSignal_signal\n" +
				"channel loop\n" +
				"channel dc\n" +
				"MAIN = signal1(1); LOOP\n" +
				"LOOP = loop -> LOOP\n" +
				"END_DIAGRAM_signal1 = endDiagram_signal1 -> SKIP\n" +
				"signal1(ID_signal1) = ((Internal_signal1(ID_signal1) [|{|update_signal1,clear_signal1,endDiagram_signal1|}|] TokenManager_signal1_t(0,0)) [|{|signal_signal,accept_signal,endDiagram_signal1|}|]pools)\n" +
				"Internal_signal1(ID_signal1) = StartActivity_signal1(ID_signal1); Node_signal1; EndActivity_signal1(ID_signal1)\n" +
				"StartActivity_signal1(ID_signal1) = startActivity_signal1.ID_signal1 -> SKIP\n" +
				"EndActivity_signal1(ID_signal1) = endActivity_signal1.ID_signal1 -> SKIP\n" +
				"AlphabetDiagram_signal1(init_signal1_t_alphabet) = {|update_signal1.1,ce_signal1.2,endDiagram_signal1|}\n" +
				"AlphabetDiagram_signal1(signal_signal_1_signal1_t_alphabet) = {|ce_signal1.2,signal_signal.1,endDiagram_signal1|}\n" +
				"AlphabetDiagram_signal1(accept_signal_1_signal1_t_alphabet) = {|accept_signal.1,ce_signal1.1,endDiagram_signal1|}\n" +
				"AlphabetDiagram_signal1(fin1_signal1_t_alphabet) = {|ce_signal1.1,clear_signal1.1,endDiagram_signal1|}\n" +
				"ProcessDiagram_signal1(init_signal1_t_alphabet) = init_signal1_t\n" +
				"ProcessDiagram_signal1(signal_signal_1_signal1_t_alphabet) = signal_signal_1_signal1_t\n" +
				"ProcessDiagram_signal1(accept_signal_1_signal1_t_alphabet) = accept_signal_1_signal1_t\n" +
				"ProcessDiagram_signal1(fin1_signal1_t_alphabet) = fin1_signal1_t\n" +
				"Node_signal1 = || x:alphabet_signal1 @ [AlphabetDiagram_signal1(x)] ProcessDiagram_signal1(x)\n" +
				"accept_signal_1_signal1 = accept_signal.1?x -> ((ce_signal1.1 -> SKIP)); accept_signal_1_signal1\n" +
				"accept_signal_1_signal1_t = accept_signal_1_signal1 /\\ END_DIAGRAM_signal1\n" +
				"fin1_signal1 = ((ce_signal1.1 -> SKIP)); clear_signal1.1 -> SKIP\n" +
				"fin1_signal1_t = fin1_signal1 /\\ END_DIAGRAM_signal1\n" +
				"init1_signal1_t = update_signal1.1!(1-0) -> ((ce_signal1.2 -> SKIP))\n" +
				"signal_signal_1_signal1 = ((ce_signal1.2 -> SKIP)); signal_signal!1 -> signal_signal_1_signal1\n" +
				"signal_signal_1_signal1_t = signal_signal_1_signal1 /\\ END_DIAGRAM_signal1\n" +
				"init_signal1_t = (init1_signal1_t) /\\ END_DIAGRAM_signal1\n" +
				"\n" +
				"TokenManager_signal1(x,init) = update_signal1?c?y:limiteUpdate_signal1 -> x+y < 10 & x+y > -10 & TokenManager_signal1(x+y,1) [] clear_signal1?c -> endDiagram_signal1 -> SKIP [] x == 0 & init == 1 & endDiagram_signal1 -> SKIP\n" +
				"TokenManager_signal1_t(x,init) = TokenManager_signal1(x,init)\n" +
				"datatype POOLNAME = signal\n"+
				"POOL(signal) = pool_signal_t(<>)\n"+
				"pools =[|{|endDiagram_signal1|}|]x:POOLNAME @ POOL(x)\n"+
				"pool_signal(l) = (signal_signal?event_signal_signal1 -> if length(l) < 5 then pool_signal(l^<event_signal_signal1>) else pool_signal(l)) [] (length(l) > 0 & accept_signal.1!head(l) -> pool_signal(tail(l)))\n" +
				"pool_signal_t(l) = pool_signal(l) /\\ END_DIAGRAM_signal1\n" +				
				"\n" +
				"assert MAIN :[deadlock free]\n" +
				"assert MAIN :[divergence free]\n" +
				"assert MAIN :[deterministic]";
		assertEquals(expected, atual);
	}
	
	@Test
	public void testSignal2() throws ParsingException {
		parser2.clearBuffer();
		String atual = parser2.parserDiagram();
		String expected = "ID_signal2 = {1..1}\n" +
				"datatype T = lock | unlock\n" +
				"countSignal_signal1 = {1..1}\n" +
				"countAccept_signal1 = {1..1}\n" +
				//"datatype event_signal1_signal2 = Int\n" +
				//"event_signal1_signal2 = Int\n" +
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
				"channel signal_signal1: countSignal_signal1\n" +
				"channel accept_signal1: countAccept_signal1.countSignal_signal1\n" +
				"channel loop\n" +
				"channel dc\n" +
				"MAIN = signal2(1); LOOP\n" +
				"LOOP = loop -> LOOP\n" +
				"END_DIAGRAM_signal2 = endDiagram_signal2 -> SKIP\n" +
				"signal2(ID_signal2) = ((Internal_signal2(ID_signal2) [|{|update_signal2,clear_signal2,endDiagram_signal2|}|] TokenManager_signal2_t(0,0)) [|{|signal_signal1,accept_signal1,endDiagram_signal2|}|]pools)\n" +
				"Internal_signal2(ID_signal2) = StartActivity_signal2(ID_signal2); Node_signal2; EndActivity_signal2(ID_signal2)\n" +
				"StartActivity_signal2(ID_signal2) = startActivity_signal2.ID_signal2 -> SKIP\n" +
				"EndActivity_signal2(ID_signal2) = endActivity_signal2.ID_signal2 -> SKIP\n" +
				"AlphabetDiagram_signal2(init_signal2_t_alphabet) = {|update_signal2.1,ce_signal2.1,update_signal2.2,ce_signal2.2,endDiagram_signal2|}\n" +
				"AlphabetDiagram_signal2(signal_signal1_1_signal2_t_alphabet) = {|ce_signal2.1,ce_signal2.2,signal_signal1.1,endDiagram_signal2|}\n" +
				"AlphabetDiagram_signal2(fin1_signal2_t_alphabet) = {|ce_signal2.3,clear_signal2.1,endDiagram_signal2|}\n" +
				"AlphabetDiagram_signal2(accept_signal1_1_signal2_t_alphabet) = {|accept_signal1.1,ce_signal2.3,endDiagram_signal2|}\n" +
				"ProcessDiagram_signal2(init_signal2_t_alphabet) = init_signal2_t\n" +
				"ProcessDiagram_signal2(signal_signal1_1_signal2_t_alphabet) = signal_signal1_1_signal2_t\n" +
				"ProcessDiagram_signal2(fin1_signal2_t_alphabet) = fin1_signal2_t\n" +
				"ProcessDiagram_signal2(accept_signal1_1_signal2_t_alphabet) = accept_signal1_1_signal2_t\n" +
				"Node_signal2 = || x:alphabet_signal2 @ [AlphabetDiagram_signal2(x)] ProcessDiagram_signal2(x)\n" +
				"init1_signal2_t = update_signal2.1!(1-0) -> ((ce_signal2.1 -> SKIP))\n" +
				"init2_signal2_t = update_signal2.2!(1-0) -> ((ce_signal2.2 -> SKIP))\n" +
				"accept_signal1_1_signal2 = accept_signal1.1?x -> ((ce_signal2.3 -> SKIP)); accept_signal1_1_signal2\n" +
				"accept_signal1_1_signal2_t = accept_signal1_1_signal2 /\\ END_DIAGRAM_signal2\n" +
				"fin1_signal2 = ((ce_signal2.3 -> SKIP)); clear_signal2.1 -> SKIP\n" +
				"fin1_signal2_t = fin1_signal2 /\\ END_DIAGRAM_signal2\n" +
				"signal_signal1_1_signal2 = ((ce_signal2.1 -> SKIP) ||| (ce_signal2.2 -> SKIP)); signal_signal1!1 -> signal_signal1_1_signal2\n" +
				"signal_signal1_1_signal2_t = signal_signal1_1_signal2 /\\ END_DIAGRAM_signal2\n" +
				"init_signal2_t = (init1_signal2_t ||| init2_signal2_t) /\\ END_DIAGRAM_signal2\n" +
				"\n" +
				"TokenManager_signal2(x,init) = update_signal2?c?y:limiteUpdate_signal2 -> x+y < 10 & x+y > -10 & TokenManager_signal2(x+y,1) [] clear_signal2?c -> endDiagram_signal2 -> SKIP [] x == 0 & init == 1 & endDiagram_signal2 -> SKIP\n" +
				"TokenManager_signal2_t(x,init) = TokenManager_signal2(x,init)\n" +
				"datatype POOLNAME = signal1\n"+
				"POOL(signal1) = pool_signal1_t(<>)\n"+
				"pools =[|{|endDiagram_signal2|}|]x:POOLNAME @ POOL(x)\n"+	
				"pool_signal1(l) = (signal_signal1?event_signal1_signal2 -> if length(l) < 5 then pool_signal1(l^<event_signal1_signal2>) else pool_signal1(l)) [] (length(l) > 0 & accept_signal1.1!head(l) -> pool_signal1(tail(l)))\n" +
				"pool_signal1_t(l) = pool_signal1(l) /\\ END_DIAGRAM_signal2\n" +						
				"\n" +
				"assert MAIN :[deadlock free]\n" +
				"assert MAIN :[divergence free]\n" +
				"assert MAIN :[deterministic]";
		
		assertEquals(expected, atual);
	}
	
	@Test
	public void testSignal3() throws ParsingException {
		parser3.clearBuffer();
		String atual = parser3.parserDiagram();
		String expected = "ID_signal3_1 = {1..1}\n" +
				"ID_signal3_2 = {1..1}\n" +
				"datatype T = lock | unlock\n" +
				"countSignal_signal1 = {1..1}\n" +
				"countAccept_signal1 = {1..1}\n" +
				//"datatype event_signal1_signal3_1 = Int\n" +
				//"event_signal1_signal3_1 = Int\n" +
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
				"channel signal_signal1: countSignal_signal1\n" +
				"channel accept_signal1: countAccept_signal1.countSignal_signal1\n" +
				"channel loop\n" +
				"channel dc\n" +
				"MAIN = signal3_1(1); LOOP\n" +
				"LOOP = loop -> LOOP\n" +
				"END_DIAGRAM_signal3_1 = endDiagram_signal3_1 -> SKIP\n" +
				"signal3_1(ID_signal3_1) = (((Internal_signal3_1(ID_signal3_1) [|{|startActivity_signal3_2.1,endActivity_signal3_2.1|}|] signal3_2(1)) [|{|update_signal3_1,clear_signal3_1,endDiagram_signal3_1|}|] TokenManager_signal3_1_t(0,0)) [|{|signal_signal1,accept_signal1,endDiagram_signal3_1|}|]pools)\n" +
				"Internal_signal3_1(ID_signal3_1) = StartActivity_signal3_1(ID_signal3_1); Node_signal3_1; EndActivity_signal3_1(ID_signal3_1)\n" +
				"StartActivity_signal3_1(ID_signal3_1) = startActivity_signal3_1.ID_signal3_1 -> SKIP\n" +
				"EndActivity_signal3_1(ID_signal3_1) = endActivity_signal3_1.ID_signal3_1 -> SKIP\n" +
				"AlphabetDiagram_signal3_1(init_signal3_1_t_alphabet) = {|update_signal3_1.1,ce_signal3_1.1,ce_signal3_1.2,endDiagram_signal3_1|}\n" +
				"AlphabetDiagram_signal3_1(call1_signal3_1_t_alphabet) = {|ce_signal3_1.2,startActivity_signal3_2.1,endActivity_signal3_2.1,ce_signal3_1.3,endDiagram_signal3_1|}\n" +
				"AlphabetDiagram_signal3_1(signal_signal1_1_signal3_1_t_alphabet) = {|ce_signal3_1.1,signal_signal1.1,endDiagram_signal3_1|}\n" +
				"AlphabetDiagram_signal3_1(fin1_signal3_1_t_alphabet) = {|ce_signal3_1.3,clear_signal3_1.1,endDiagram_signal3_1|}\n" +
				"ProcessDiagram_signal3_1(init_signal3_1_t_alphabet) = init_signal3_1_t\n" +
				"ProcessDiagram_signal3_1(call1_signal3_1_t_alphabet) = call1_signal3_1_t\n" +
				"ProcessDiagram_signal3_1(signal_signal1_1_signal3_1_t_alphabet) = signal_signal1_1_signal3_1_t\n" +
				"ProcessDiagram_signal3_1(fin1_signal3_1_t_alphabet) = fin1_signal3_1_t\n" +
				"Node_signal3_1 = || x:alphabet_signal3_1 @ [AlphabetDiagram_signal3_1(x)] ProcessDiagram_signal3_1(x)\n" +
				"init1_signal3_1_t = update_signal3_1.1!(2-0) -> ((ce_signal3_1.1 -> SKIP) ||| (ce_signal3_1.2 -> SKIP))\n" +
				"signal_signal1_1_signal3_1 = ((ce_signal3_1.1 -> SKIP)); signal_signal1!1 -> signal_signal1_1_signal3_1\n" +
				"signal_signal1_1_signal3_1_t = signal_signal1_1_signal3_1 /\\ END_DIAGRAM_signal3_1\n" +
				"call1_signal3_1 = ((ce_signal3_1.2 -> SKIP)); startActivity_signal3_2.1 -> endActivity_signal3_2.1 -> ((ce_signal3_1.3 -> SKIP)); call1_signal3_1\n" +
				"call1_signal3_1_t = call1_signal3_1 /\\ END_DIAGRAM_signal3_1\n" +
				"fin1_signal3_1 = ((ce_signal3_1.3 -> SKIP)); clear_signal3_1.1 -> SKIP\n" +
				"fin1_signal3_1_t = fin1_signal3_1 /\\ END_DIAGRAM_signal3_1\n" +
				"init_signal3_1_t = (init1_signal3_1_t) /\\ END_DIAGRAM_signal3_1\n" +
				"\n" +
				"TokenManager_signal3_1(x,init) = update_signal3_1?c?y:limiteUpdate_signal3_1 -> x+y < 10 & x+y > -10 & TokenManager_signal3_1(x+y,1) [] clear_signal3_1?c -> endDiagram_signal3_1 -> SKIP [] x == 0 & init == 1 & endDiagram_signal3_1 -> SKIP\n" +
				"TokenManager_signal3_1_t(x,init) = TokenManager_signal3_1(x,init)\n" +
				"datatype POOLNAME = signal1\n"+
				"POOL(signal1) = pool_signal1_t(<>)\n"+
				"pools =[|{|endDiagram_signal3_1|}|]x:POOLNAME @ POOL(x)\n"+
				"pool_signal1(l) = (signal_signal1?event_signal1_signal3_1 -> if length(l) < 5 then pool_signal1(l^<event_signal1_signal3_1>) else pool_signal1(l)) [] (length(l) > 0 & accept_signal1.1!head(l) -> pool_signal1(tail(l)))\n" +
				"pool_signal1_t(l) = pool_signal1(l) /\\ END_DIAGRAM_signal3_1\n" +
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
				"AlphabetDiagram_signal3_2(fin1_signal3_2_t_alphabet) = {|ce_signal3_2.1,clear_signal3_2.1,endDiagram_signal3_2|}\n" +
				"AlphabetDiagram_signal3_2(accept_signal1_1_signal3_2_t_alphabet) = {|accept_signal1.1,ce_signal3_2.1,endDiagram_signal3_2|}\n" +
				"ProcessDiagram_signal3_2(fin1_signal3_2_t_alphabet) = fin1_signal3_2_t\n" +
				"ProcessDiagram_signal3_2(accept_signal1_1_signal3_2_t_alphabet) = accept_signal1_1_signal3_2_t\n" +
				"Node_signal3_2 = || x:alphabet_signal3_2 @ [AlphabetDiagram_signal3_2(x)] ProcessDiagram_signal3_2(x)\n" +
				"accept_signal1_1_signal3_2 = accept_signal1.1?x -> ((ce_signal3_2.1 -> SKIP)); accept_signal1_1_signal3_2\n" +
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
	public void testSignal4() throws ParsingException {
		parser4.clearBuffer();
		String atual = parser4.parserDiagram();
		String expected = "ID_signal4 = {1..1}\n" +
				"datatype T = lock | unlock\n" +
				"countSignal_signal1 = {1..1}\n" +
				"countAccept_signal1 = {1..1}\n" +
				//"datatype event_signal1_signal4 = Int\n" +
				//"event_signal1_signal4 = Int\n" +
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
				"channel signal_signal1: countSignal_signal1\n" +
				"channel accept_signal1: countAccept_signal1.countSignal_signal1\n" +
				"channel loop\n" +
				"channel dc\n" +
				"MAIN = signal4(1); LOOP\n" +
				"LOOP = loop -> LOOP\n" +
				"END_DIAGRAM_signal4 = endDiagram_signal4 -> SKIP\n" +
				"signal4(ID_signal4) = ((Internal_signal4(ID_signal4) [|{|update_signal4,clear_signal4,endDiagram_signal4|}|] TokenManager_signal4_t(0,0)) [|{|signal_signal1,accept_signal1,endDiagram_signal4|}|]pools)\n" +
				"Internal_signal4(ID_signal4) = StartActivity_signal4(ID_signal4); Node_signal4; EndActivity_signal4(ID_signal4)\n" +
				"StartActivity_signal4(ID_signal4) = startActivity_signal4.ID_signal4 -> SKIP\n" +
				"EndActivity_signal4(ID_signal4) = endActivity_signal4.ID_signal4 -> SKIP\n" +
				"AlphabetDiagram_signal4(init_signal4_t_alphabet) = {|update_signal4.1,ce_signal4.1,endDiagram_signal4|}\n" +
				"AlphabetDiagram_signal4(act1_signal4_t_alphabet) = {|ce_signal4.2,event_act1_signal4,ce_signal4.4,endDiagram_signal4|}\n" +
				"AlphabetDiagram_signal4(signal_signal1_1_signal4_t_alphabet) = {|ce_signal4.1,signal_signal1.1,endDiagram_signal4|}\n" +
				"AlphabetDiagram_signal4(fin1_signal4_t_alphabet) = {|ce_signal4.4,ce_signal4.5,clear_signal4.1,endDiagram_signal4|}\n" +
				"AlphabetDiagram_signal4(accept_signal1_1_signal4_t_alphabet) = {|accept_signal1.1,update_signal4.2,ce_signal4.2,ce_signal4.3,endDiagram_signal4|}\n" +
				"AlphabetDiagram_signal4(act2_signal4_t_alphabet) = {|ce_signal4.3,event_act2_signal4,ce_signal4.5,endDiagram_signal4|}\n" +
				"ProcessDiagram_signal4(init_signal4_t_alphabet) = init_signal4_t\n" +
				"ProcessDiagram_signal4(act1_signal4_t_alphabet) = act1_signal4_t\n" +
				"ProcessDiagram_signal4(signal_signal1_1_signal4_t_alphabet) = signal_signal1_1_signal4_t\n" +
				"ProcessDiagram_signal4(fin1_signal4_t_alphabet) = fin1_signal4_t\n" +
				"ProcessDiagram_signal4(accept_signal1_1_signal4_t_alphabet) = accept_signal1_1_signal4_t\n" +
				"ProcessDiagram_signal4(act2_signal4_t_alphabet) = act2_signal4_t\n" +
				"Node_signal4 = || x:alphabet_signal4 @ [AlphabetDiagram_signal4(x)] ProcessDiagram_signal4(x)\n" +
				"init1_signal4_t = update_signal4.1!(1-0) -> ((ce_signal4.1 -> SKIP))\n" +
				"signal_signal1_1_signal4 = ((ce_signal4.1 -> SKIP)); signal_signal1!1 -> signal_signal1_1_signal4\n" +
				"signal_signal1_1_signal4_t = signal_signal1_1_signal4 /\\ END_DIAGRAM_signal4\n" +
				"accept_signal1_1_signal4 = accept_signal1.1?x -> update_signal4.2!(2-1) -> ((ce_signal4.2 -> SKIP) ||| (ce_signal4.3 -> SKIP)); accept_signal1_1_signal4\n" +
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
				"datatype POOLNAME = signal1\n"+
				"POOL(signal1) = pool_signal1_t(<>)\n"+
				"pools =[|{|endDiagram_signal4|}|]x:POOLNAME @ POOL(x)\n"+
				"pool_signal1(l) = (signal_signal1?event_signal1_signal4 -> if length(l) < 5 then pool_signal1(l^<event_signal1_signal4>) else pool_signal1(l)) [] (length(l) > 0 & accept_signal1.1!head(l) -> pool_signal1(tail(l)))\n" +
				"pool_signal1_t(l) = pool_signal1(l) /\\ END_DIAGRAM_signal4\n" +				
				"\n" +
				"assert MAIN :[deadlock free]\n" +
				"assert MAIN :[divergence free]\n" +
				"assert MAIN :[deterministic]";
		assertEquals(expected, atual);
	}

	@Test
	public void testSignal5() throws ParsingException {
		parser5.clearBuffer();
		String atual = parser5.parserDiagram();
		String expected = "ID_signal6 = {1..1}\n" +
				"datatype T = lock | unlock\n" +
				"countSignal_signal1 = {1..2}\n" +
				"countAccept_signal1 = {1..2}\n" +
				//"datatype event_signal1_signal6 = Int\n" +
				//"event_signal1_signal6 = Int\n" +
				"datatype alphabet_signal6 = init_signal6_t_alphabet | signal_signal1_2_signal6_t_alphabet| signal_signal1_1_signal6_t_alphabet| fin1_signal6_t_alphabet| JoinNode0_signal6_t_alphabet| accept_signal1_1_signal6_t_alphabet| accept_signal1_2_signal6_t_alphabet\n" +
				"countCe_signal6 = {1..5}\n" +
				"countUpdate_signal6 = {1..2}\n" +
				"countClear_signal6 = {1..1}\n" +
				"limiteUpdate_signal6 = {(-1)..(2)}\n" +
				"channel startActivity_signal6: ID_signal6\n" +
				"channel endActivity_signal6: ID_signal6\n" +
				"channel ce_signal6: countCe_signal6\n" +
				"channel clear_signal6: countClear_signal6\n" +
				"channel update_signal6: countUpdate_signal6.limiteUpdate_signal6\n" +
				"channel endDiagram_signal6\n" +
				"channel signal_signal1: countSignal_signal1\n" +
				"channel accept_signal1: countAccept_signal1.countSignal_signal1\n" +
				"channel loop\n" +
				"channel dc\n" +
				"MAIN = signal6(1); LOOP\n" +
				"LOOP = loop -> LOOP\n" +
				"END_DIAGRAM_signal6 = endDiagram_signal6 -> SKIP\n" +
				"signal6(ID_signal6) = ((Internal_signal6(ID_signal6) [|{|update_signal6,clear_signal6,endDiagram_signal6|}|] TokenManager_signal6_t(0,0)) [|{|signal_signal1,accept_signal1,endDiagram_signal6|}|]pools)\n" +
				"Internal_signal6(ID_signal6) = StartActivity_signal6(ID_signal6); Node_signal6; EndActivity_signal6(ID_signal6)\n" +
				"StartActivity_signal6(ID_signal6) = startActivity_signal6.ID_signal6 -> SKIP\n" +
				"EndActivity_signal6(ID_signal6) = endActivity_signal6.ID_signal6 -> SKIP\n" +
				"AlphabetDiagram_signal6(init_signal6_t_alphabet) = {|update_signal6.1,ce_signal6.1,ce_signal6.2,endDiagram_signal6|}\n" +
				"AlphabetDiagram_signal6(signal_signal1_2_signal6_t_alphabet) = {|ce_signal6.2,signal_signal1.2,endDiagram_signal6|}\n" +
				"AlphabetDiagram_signal6(signal_signal1_1_signal6_t_alphabet) = {|ce_signal6.1,signal_signal1.1,endDiagram_signal6|}\n" +
				"AlphabetDiagram_signal6(fin1_signal6_t_alphabet) = {|ce_signal6.4,clear_signal6.1,endDiagram_signal6|}\n" +
				"AlphabetDiagram_signal6(JoinNode0_signal6_t_alphabet) = {|ce_signal6.3,ce_signal6.5,update_signal6.2,ce_signal6.4,endDiagram_signal6|}\n" +
				"AlphabetDiagram_signal6(accept_signal1_1_signal6_t_alphabet) = {|accept_signal1.1,ce_signal6.3,endDiagram_signal6|}\n" +
				"AlphabetDiagram_signal6(accept_signal1_2_signal6_t_alphabet) = {|accept_signal1.2,ce_signal6.5,endDiagram_signal6|}\n" +
				"ProcessDiagram_signal6(init_signal6_t_alphabet) = init_signal6_t\n" +
				"ProcessDiagram_signal6(signal_signal1_2_signal6_t_alphabet) = signal_signal1_2_signal6_t\n" +
				"ProcessDiagram_signal6(signal_signal1_1_signal6_t_alphabet) = signal_signal1_1_signal6_t\n" +
				"ProcessDiagram_signal6(fin1_signal6_t_alphabet) = fin1_signal6_t\n" +
				"ProcessDiagram_signal6(JoinNode0_signal6_t_alphabet) = JoinNode0_signal6_t\n" +
				"ProcessDiagram_signal6(accept_signal1_1_signal6_t_alphabet) = accept_signal1_1_signal6_t\n" +
				"ProcessDiagram_signal6(accept_signal1_2_signal6_t_alphabet) = accept_signal1_2_signal6_t\n" +
				"Node_signal6 = || x:alphabet_signal6 @ [AlphabetDiagram_signal6(x)] ProcessDiagram_signal6(x)\n" +
				"init1_signal6_t = update_signal6.1!(2-0) -> ((ce_signal6.1 -> SKIP) ||| (ce_signal6.2 -> SKIP))\n" +
				"signal_signal1_1_signal6 = ((ce_signal6.1 -> SKIP)); signal_signal1!1 -> signal_signal1_1_signal6\n" +
				"signal_signal1_1_signal6_t = signal_signal1_1_signal6 /\\ END_DIAGRAM_signal6\n" +
				"signal_signal1_2_signal6 = ((ce_signal6.2 -> SKIP)); signal_signal1!2 -> signal_signal1_2_signal6\n" +
				"signal_signal1_2_signal6_t = signal_signal1_2_signal6 /\\ END_DIAGRAM_signal6\n" +
				"accept_signal1_1_signal6 = accept_signal1.1?x -> ((ce_signal6.3 -> SKIP)); accept_signal1_1_signal6\n" +
				"accept_signal1_1_signal6_t = accept_signal1_1_signal6 /\\ END_DIAGRAM_signal6\n" +
				"fin1_signal6 = ((ce_signal6.4 -> SKIP)); clear_signal6.1 -> SKIP\n" +
				"fin1_signal6_t = fin1_signal6 /\\ END_DIAGRAM_signal6\n" +
				"accept_signal1_2_signal6 = accept_signal1.2?x -> ((ce_signal6.5 -> SKIP)); accept_signal1_2_signal6\n" +
				"accept_signal1_2_signal6_t = accept_signal1_2_signal6 /\\ END_DIAGRAM_signal6\n" +
				"JoinNode0_signal6 = ((ce_signal6.3 -> SKIP) ||| (ce_signal6.5 -> SKIP)); update_signal6.2!(1-2) -> ((ce_signal6.4 -> SKIP)); JoinNode0_signal6\n" +
				"JoinNode0_signal6_t = (JoinNode0_signal6 /\\ END_DIAGRAM_signal6)\n" +
				"init_signal6_t = (init1_signal6_t) /\\ END_DIAGRAM_signal6\n" +
				"\n" +
				"TokenManager_signal6(x,init) = update_signal6?c?y:limiteUpdate_signal6 -> x+y < 10 & x+y > -10 & TokenManager_signal6(x+y,1) [] clear_signal6?c -> endDiagram_signal6 -> SKIP [] x == 0 & init == 1 & endDiagram_signal6 -> SKIP\n" +
				"TokenManager_signal6_t(x,init) = TokenManager_signal6(x,init)\n" +
				"datatype POOLNAME = signal1\n"+
				"POOL(signal1) = pool_signal1_t(<>)\n"+
				"pools =[|{|endDiagram_signal6|}|]x:POOLNAME @ POOL(x)\n"+
				"pool_signal1(l) = (signal_signal1?event_signal1_signal6 -> if length(l) < 5 then pool_signal1(l^<event_signal1_signal6>) else pool_signal1(l)) [] (length(l) > 0 & accept_signal1.1!head(l) -> pool_signal1(tail(l))) [] (length(l) > 0 & accept_signal1.2!head(l) -> pool_signal1(tail(l)))\n" +
				"pool_signal1_t(l) = pool_signal1(l) /\\ END_DIAGRAM_signal6\n" +
				"\n" +
				"assert MAIN :[deadlock free]\n" +
				"assert MAIN :[divergence free]\n" +
				"assert MAIN :[deterministic]";
		assertEquals(expected, atual);
	}
	
	@Test
	public void testSignal6() throws ParsingException {
		parser6.clearBuffer();
		String atual = parser6.parserDiagram();
		String expected = "ID_Calibratesimple = {1..1}\n" + 
				"ID_StopPITTracking = {1..1}\n" + 
				"datatype T = lock | unlock\n" + 
				"countSignal_StopPITTracking_Cmd = {1..1}\n" + 
				"countAccept_StopPITTracking_Cmd = {1..1}\n" + 
				"countAccept_StopPITTracking_Ack = {1..1}\n" + 
				"countSignal_StopPITTracking_Ack = {1..1}\n" + 
				"datatype alphabet_Calibratesimple = init_Calibratesimple_t_alphabet | ActivityFinal0_Calibratesimple_t_alphabet| StopPITTracking_Calibratesimple_t_alphabet\n" + 
				"countCe_Calibratesimple = {1..2}\n" + 
				"countUpdate_Calibratesimple = {1..1}\n" + 
				"countClear_Calibratesimple = {1..1}\n" + 
				"limiteUpdate_Calibratesimple = {(1)..(1)}\n" + 
				"channel startActivity_Calibratesimple: ID_Calibratesimple\n" + 
				"channel endActivity_Calibratesimple: ID_Calibratesimple\n" + 
				"channel ce_Calibratesimple: countCe_Calibratesimple\n" + 
				"channel clear_Calibratesimple: countClear_Calibratesimple\n" + 
				"channel update_Calibratesimple: countUpdate_Calibratesimple.limiteUpdate_Calibratesimple\n" + 
				"channel endDiagram_Calibratesimple\n" + 
				"channel signal_StopPITTracking_Ack: countSignal_StopPITTracking_Ack\n" + 
				"channel accept_StopPITTracking_Ack: countAccept_StopPITTracking_Ack.countSignal_StopPITTracking_Ack\n" + 
				"channel signal_StopPITTracking_Cmd: countSignal_StopPITTracking_Cmd\n" + 
				"channel accept_StopPITTracking_Cmd: countAccept_StopPITTracking_Cmd.countSignal_StopPITTracking_Cmd\n" + 
				"channel loop\n" + 
				"channel dc\n" + 
				"MAIN = Calibratesimple(1); LOOP\n" + 
				"LOOP = loop -> LOOP\n" + 
				"END_DIAGRAM_Calibratesimple = endDiagram_Calibratesimple -> SKIP\n" + 
				"Calibratesimple(ID_Calibratesimple) = ((Internal_Calibratesimple(ID_Calibratesimple) [|{|startActivity_StopPITTracking.1,endActivity_StopPITTracking.1|}|] StopPITTracking(1)) [|{|update_Calibratesimple,clear_Calibratesimple,endDiagram_Calibratesimple|}|] TokenManager_Calibratesimple_t(0,0))\n" + 
				"Internal_Calibratesimple(ID_Calibratesimple) = StartActivity_Calibratesimple(ID_Calibratesimple); Node_Calibratesimple; EndActivity_Calibratesimple(ID_Calibratesimple)\n" + 
				"StartActivity_Calibratesimple(ID_Calibratesimple) = startActivity_Calibratesimple.ID_Calibratesimple -> SKIP\n" + 
				"EndActivity_Calibratesimple(ID_Calibratesimple) = endActivity_Calibratesimple.ID_Calibratesimple -> SKIP\n" + 
				"AlphabetDiagram_Calibratesimple(init_Calibratesimple_t_alphabet) = {|update_Calibratesimple.1,ce_Calibratesimple.1,endDiagram_Calibratesimple|}\n" + 
				"AlphabetDiagram_Calibratesimple(ActivityFinal0_Calibratesimple_t_alphabet) = {|ce_Calibratesimple.2,clear_Calibratesimple.1,endDiagram_Calibratesimple|}\n" + 
				"AlphabetDiagram_Calibratesimple(StopPITTracking_Calibratesimple_t_alphabet) = {|ce_Calibratesimple.1,startActivity_StopPITTracking.1,endActivity_StopPITTracking.1,ce_Calibratesimple.2,endDiagram_Calibratesimple|}\n" + 
				"ProcessDiagram_Calibratesimple(init_Calibratesimple_t_alphabet) = init_Calibratesimple_t\n" + 
				"ProcessDiagram_Calibratesimple(ActivityFinal0_Calibratesimple_t_alphabet) = ActivityFinal0_Calibratesimple_t\n" + 
				"ProcessDiagram_Calibratesimple(StopPITTracking_Calibratesimple_t_alphabet) = StopPITTracking_Calibratesimple_t\n" + 
				"Node_Calibratesimple = || x:alphabet_Calibratesimple @ [AlphabetDiagram_Calibratesimple(x)] ProcessDiagram_Calibratesimple(x)\n" + 
				"InitialNode0_Calibratesimple_t = update_Calibratesimple.1!(1-0) -> ((ce_Calibratesimple.1 -> SKIP))\n" + 
				"StopPITTracking_Calibratesimple = ((ce_Calibratesimple.1 -> SKIP)); startActivity_StopPITTracking.1 -> endActivity_StopPITTracking.1 -> ((ce_Calibratesimple.2 -> SKIP)); StopPITTracking_Calibratesimple\n" + 
				"StopPITTracking_Calibratesimple_t = StopPITTracking_Calibratesimple /\\ END_DIAGRAM_Calibratesimple\n" + 
				"ActivityFinal0_Calibratesimple = ((ce_Calibratesimple.2 -> SKIP)); clear_Calibratesimple.1 -> SKIP\n" + 
				"ActivityFinal0_Calibratesimple_t = ActivityFinal0_Calibratesimple /\\ END_DIAGRAM_Calibratesimple\n" + 
				"init_Calibratesimple_t = (InitialNode0_Calibratesimple_t) /\\ END_DIAGRAM_Calibratesimple\n" + 
				"\n" + 
				"TokenManager_Calibratesimple(x,init) = update_Calibratesimple?c?y:limiteUpdate_Calibratesimple -> x+y < 10 & x+y > -10 & TokenManager_Calibratesimple(x+y,1) [] clear_Calibratesimple?c -> endDiagram_Calibratesimple -> SKIP [] x == 0 & init == 1 & endDiagram_Calibratesimple -> SKIP\n" + 
				"TokenManager_Calibratesimple_t(x,init) = TokenManager_Calibratesimple(x,init)\n" + 
				"datatype POOLNAME = StopPITTracking_Ack|StopPITTracking_Cmd\n" + 
				"POOL(StopPITTracking_Ack) = pool_StopPITTracking_Ack_t(<>)\n" + 
				"POOL(StopPITTracking_Cmd) = pool_StopPITTracking_Cmd_t(<>)\n" + 
				"pools =[|{|endDiagram_Calibratesimple|}|]x:POOLNAME @ POOL(x)\n" + 
				"pool_StopPITTracking_Ack(l) = (signal_StopPITTracking_Ack?event_StopPITTracking_Ack_Calibratesimple -> if length(l) < 5 then pool_StopPITTracking_Ack(l^<event_StopPITTracking_Ack_Calibratesimple>) else pool_StopPITTracking_Ack(l)) [] (length(l) > 0 & accept_StopPITTracking_Ack.1!head(l) -> pool_StopPITTracking_Ack(tail(l)))\n" + 
				"pool_StopPITTracking_Ack_t(l) = pool_StopPITTracking_Ack(l) /\\ END_DIAGRAM_Calibratesimple\n" + 
				"pool_StopPITTracking_Cmd(l) = (signal_StopPITTracking_Cmd?event_StopPITTracking_Cmd_Calibratesimple -> if length(l) < 5 then pool_StopPITTracking_Cmd(l^<event_StopPITTracking_Cmd_Calibratesimple>) else pool_StopPITTracking_Cmd(l))\n" + 
				"pool_StopPITTracking_Cmd_t(l) = pool_StopPITTracking_Cmd(l) /\\ END_DIAGRAM_Calibratesimple\n" + 
				"\n" + 
				"datatype alphabet_StopPITTracking = init_StopPITTracking_t_alphabet | accept_StopPITTracking_Ack_1_StopPITTracking_t_alphabet| ActivityFinal0_StopPITTracking_t_alphabet| ForkNode0_StopPITTracking_t_alphabet| signal_StopPITTracking_Cmd_1_StopPITTracking_t_alphabet| println2_StopPITTracking_t_alphabet| println1_StopPITTracking_t_alphabet| JoinNode0_StopPITTracking_t_alphabet\n" + 
				"countCe_StopPITTracking = {1..8}\n" + 
				"countUpdate_StopPITTracking = {1..3}\n" + 
				"countClear_StopPITTracking = {1..1}\n" + 
				"limiteUpdate_StopPITTracking = {(-1)..(1)}\n" + 
				"channel startActivity_StopPITTracking: ID_StopPITTracking\n" + 
				"channel endActivity_StopPITTracking: ID_StopPITTracking\n" + 
				"channel ce_StopPITTracking: countCe_StopPITTracking\n" + 
				"channel clear_StopPITTracking: countClear_StopPITTracking\n" + 
				"channel update_StopPITTracking: countUpdate_StopPITTracking.limiteUpdate_StopPITTracking\n" + 
				"channel endDiagram_StopPITTracking\n" + 
				"channel event_println1_StopPITTracking,event_println2_StopPITTracking\n" + 
				"END_DIAGRAM_StopPITTracking = endDiagram_StopPITTracking -> SKIP\n" + 
				"StopPITTracking(ID_StopPITTracking) = ((Internal_StopPITTracking(ID_StopPITTracking) [|{|update_StopPITTracking,clear_StopPITTracking,endDiagram_StopPITTracking|}|] TokenManager_StopPITTracking_t(0,0)) [|{|signal_StopPITTracking_Cmd,accept_StopPITTracking_Cmd,endDiagram_StopPITTracking|}|]pools)\n" + 
				"Internal_StopPITTracking(ID_StopPITTracking) = StartActivity_StopPITTracking(ID_StopPITTracking); Node_StopPITTracking; EndActivity_StopPITTracking(ID_StopPITTracking)\n" + 
				"StartActivity_StopPITTracking(ID_StopPITTracking) = startActivity_StopPITTracking.ID_StopPITTracking -> SKIP\n" + 
				"EndActivity_StopPITTracking(ID_StopPITTracking) = endActivity_StopPITTracking.ID_StopPITTracking -> SKIP\n" + 
				"AlphabetDiagram_StopPITTracking(init_StopPITTracking_t_alphabet) = {|update_StopPITTracking.1,ce_StopPITTracking.1,endDiagram_StopPITTracking|}\n" + 
				"AlphabetDiagram_StopPITTracking(accept_StopPITTracking_Ack_1_StopPITTracking_t_alphabet) = {|ce_StopPITTracking.2,accept_StopPITTracking_Ack.1,ce_StopPITTracking.4,endDiagram_StopPITTracking|}\n" + 
				"AlphabetDiagram_StopPITTracking(ActivityFinal0_StopPITTracking_t_alphabet) = {|ce_StopPITTracking.6,clear_StopPITTracking.1,endDiagram_StopPITTracking|}\n" + 
				"AlphabetDiagram_StopPITTracking(ForkNode0_StopPITTracking_t_alphabet) = {|ce_StopPITTracking.1,update_StopPITTracking.2,ce_StopPITTracking.2,ce_StopPITTracking.3,endDiagram_StopPITTracking|}\n" + 
				"AlphabetDiagram_StopPITTracking(signal_StopPITTracking_Cmd_1_StopPITTracking_t_alphabet) = {|ce_StopPITTracking.3,signal_StopPITTracking_Cmd.1,ce_StopPITTracking.7,endDiagram_StopPITTracking|}\n" + 
				"AlphabetDiagram_StopPITTracking(println2_StopPITTracking_t_alphabet) = {|ce_StopPITTracking.7,event_println2_StopPITTracking,ce_StopPITTracking.8,endDiagram_StopPITTracking|}\n" + 
				"AlphabetDiagram_StopPITTracking(println1_StopPITTracking_t_alphabet) = {|ce_StopPITTracking.4,event_println1_StopPITTracking,ce_StopPITTracking.5,endDiagram_StopPITTracking|}\n" + 
				"AlphabetDiagram_StopPITTracking(JoinNode0_StopPITTracking_t_alphabet) = {|ce_StopPITTracking.5,ce_StopPITTracking.8,update_StopPITTracking.3,ce_StopPITTracking.6,endDiagram_StopPITTracking|}\n" + 
				"ProcessDiagram_StopPITTracking(init_StopPITTracking_t_alphabet) = init_StopPITTracking_t\n" + 
				"ProcessDiagram_StopPITTracking(accept_StopPITTracking_Ack_1_StopPITTracking_t_alphabet) = accept_StopPITTracking_Ack_1_StopPITTracking_t\n" + 
				"ProcessDiagram_StopPITTracking(ActivityFinal0_StopPITTracking_t_alphabet) = ActivityFinal0_StopPITTracking_t\n" + 
				"ProcessDiagram_StopPITTracking(ForkNode0_StopPITTracking_t_alphabet) = ForkNode0_StopPITTracking_t\n" + 
				"ProcessDiagram_StopPITTracking(signal_StopPITTracking_Cmd_1_StopPITTracking_t_alphabet) = signal_StopPITTracking_Cmd_1_StopPITTracking_t\n" + 
				"ProcessDiagram_StopPITTracking(println2_StopPITTracking_t_alphabet) = println2_StopPITTracking_t\n" + 
				"ProcessDiagram_StopPITTracking(println1_StopPITTracking_t_alphabet) = println1_StopPITTracking_t\n" + 
				"ProcessDiagram_StopPITTracking(JoinNode0_StopPITTracking_t_alphabet) = JoinNode0_StopPITTracking_t\n" + 
				"Node_StopPITTracking = || x:alphabet_StopPITTracking @ [AlphabetDiagram_StopPITTracking(x)] ProcessDiagram_StopPITTracking(x)\n" + 
				"InitialNode0_StopPITTracking_t = update_StopPITTracking.1!(1-0) -> ((ce_StopPITTracking.1 -> SKIP))\n" + 
				"ForkNode0_StopPITTracking = ce_StopPITTracking.1 -> update_StopPITTracking.2!(2-1) -> ((ce_StopPITTracking.2 -> SKIP) ||| (ce_StopPITTracking.3 -> SKIP)); ForkNode0_StopPITTracking\n" + 
				"ForkNode0_StopPITTracking_t = ForkNode0_StopPITTracking /\\ END_DIAGRAM_StopPITTracking\n" + 
				"accept_StopPITTracking_Ack_1_StopPITTracking = ((ce_StopPITTracking.2 -> SKIP)); accept_StopPITTracking_Ack.1?x -> ((ce_StopPITTracking.4 -> SKIP)); accept_StopPITTracking_Ack_1_StopPITTracking\n" + 
				"accept_StopPITTracking_Ack_1_StopPITTracking_t = accept_StopPITTracking_Ack_1_StopPITTracking /\\ END_DIAGRAM_StopPITTracking\n" + 
				"println1_StopPITTracking = ((ce_StopPITTracking.4 -> SKIP)); event_println1_StopPITTracking -> ((ce_StopPITTracking.5 -> SKIP)); println1_StopPITTracking\n" + 
				"println1_StopPITTracking_t = println1_StopPITTracking /\\ END_DIAGRAM_StopPITTracking\n" + 
				"ActivityFinal0_StopPITTracking = ((ce_StopPITTracking.6 -> SKIP)); clear_StopPITTracking.1 -> SKIP\n" + 
				"ActivityFinal0_StopPITTracking_t = ActivityFinal0_StopPITTracking /\\ END_DIAGRAM_StopPITTracking\n" + 
				"signal_StopPITTracking_Cmd_1_StopPITTracking = ((ce_StopPITTracking.3 -> SKIP)); signal_StopPITTracking_Cmd!1 -> ((ce_StopPITTracking.7 -> SKIP)); signal_StopPITTracking_Cmd_1_StopPITTracking\n" + 
				"signal_StopPITTracking_Cmd_1_StopPITTracking_t = signal_StopPITTracking_Cmd_1_StopPITTracking /\\ END_DIAGRAM_StopPITTracking\n" + 
				"println2_StopPITTracking = ((ce_StopPITTracking.7 -> SKIP)); event_println2_StopPITTracking -> ((ce_StopPITTracking.8 -> SKIP)); println2_StopPITTracking\n" + 
				"println2_StopPITTracking_t = println2_StopPITTracking /\\ END_DIAGRAM_StopPITTracking\n" + 
				"JoinNode0_StopPITTracking = ((ce_StopPITTracking.5 -> SKIP) ||| (ce_StopPITTracking.8 -> SKIP)); update_StopPITTracking.3!(1-2) -> ((ce_StopPITTracking.6 -> SKIP)); JoinNode0_StopPITTracking\n" + 
				"JoinNode0_StopPITTracking_t = (JoinNode0_StopPITTracking /\\ END_DIAGRAM_StopPITTracking)\n" + 
				"init_StopPITTracking_t = (InitialNode0_StopPITTracking_t) /\\ END_DIAGRAM_StopPITTracking\n" + 
				"\n" + 
				"TokenManager_StopPITTracking(x,init) = update_StopPITTracking?c?y:limiteUpdate_StopPITTracking -> x+y < 10 & x+y > -10 & TokenManager_StopPITTracking(x+y,1) [] clear_StopPITTracking?c -> endDiagram_StopPITTracking -> SKIP [] x == 0 & init == 1 & endDiagram_StopPITTracking -> SKIP\n" + 
				"TokenManager_StopPITTracking_t(x,init) = TokenManager_StopPITTracking(x,init)\n" + 
				"\n" + 
				"assert MAIN :[deadlock free]\n" + 
				"assert MAIN :[divergence free]\n" + 
				"assert MAIN :[deterministic]";
		assertEquals(expected, atual);
	}
}
