package com.ref.activityDiagram;

import static org.junit.Assert.assertEquals;

import org.junit.*;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IActivityDiagram;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.project.ModelFinder;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.ref.parser.activityDiagram.ADParser;

public class ADParserTestBehaviourCall {
	
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
			projectAccessor.open("src/test/resources/activityDiagram/behavior1.asta");
			INamedElement[] findElements = findElements(projectAccessor);

			for (int i = 0; i < findElements.length; i++) {
				if (findElements[i].getName().equals("behavior1")) {
					ad = (IActivityDiagram) findElements[i];
				}
			}
			
			parser1 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/behavior2.asta");
			findElements = findElements(projectAccessor);

			for (int i = 0; i < findElements.length; i++) {
				if (findElements[i].getName().equals("behavior2")) {
					ad = (IActivityDiagram) findElements[i];
				}
			}
			
			parser2 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/behavior3.asta");
			findElements = findElements(projectAccessor);

			for (int i = 0; i < findElements.length; i++) {
				if (findElements[i].getName().equals("behavior3")) {
					ad = (IActivityDiagram) findElements[i];
				}
			}
			
			parser3 = new ADParser(ad.getActivity(), ad.getName(), ad);

			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/behavior4.asta");
			findElements = findElements(projectAccessor);

			for (int i = 0; i < findElements.length; i++) {
				if (findElements[i].getName().equals("behavior4")) {
					ad = (IActivityDiagram) findElements[i];
				}
			}

			parser4 = new ADParser(ad.getActivity(), ad.getName(), ad);

			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/behavior5.asta");
			findElements = findElements(projectAccessor);

			for (int i = 0; i < findElements.length; i++) {
				if (findElements[i].getName().equals("behavior5")) {
					ad = (IActivityDiagram) findElements[i];
				}
			}

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
	
	@AfterClass
	public static void CloseProject() throws Exception {
		AstahAPI.getAstahAPI().getProjectAccessor().close();
	}
	
	/*
	 * Teste de Tradução do call behavior
	 * */
	@Test
	public void TestNodesBehavior1() {
		parser1.clearBuffer();
		String actual = parser1.parserDiagram();
		StringBuffer expected = new StringBuffer();
		expected.append("ID_CB1 = {1..1}\n" +
				"ID_behavior1 = {1..1}\n" +
				"datatype T = lock | unlock\n" +
				"datatype alphabet_behavior1 = init_behavior1_t_alphabet | CB1_behavior1_t_alphabet| fin1_behavior1_t_alphabet\n" +
				"countCe_behavior1 = {1..2}\n" +
				"countUpdate_behavior1 = {1..1}\n" +
				"countClear_behavior1 = {1..1}\n" +
				"limiteUpdate_behavior1 = {(1)..(1)}\n" +
				"channel startActivity_behavior1: ID_behavior1\n" +
				"channel endActivity_behavior1: ID_behavior1\n" +
				"channel ce_behavior1: countCe_behavior1\n" +
				"channel clear_behavior1: countClear_behavior1\n" +
				"channel update_behavior1: countUpdate_behavior1.limiteUpdate_behavior1\n" +
				"channel endDiagram_behavior1\n" +
				"channel loop\n" +
				"channel dc\n" +
				"MAIN = behavior1(1); LOOP\n" +
				"LOOP = loop -> LOOP\n" +
				"END_DIAGRAM_behavior1 = endDiagram_behavior1 -> SKIP\n" +
				"behavior1(ID_behavior1) = ((Internal_behavior1(ID_behavior1) [|{|startActivity_CB1.1,endActivity_CB1.1|}|] CB1(1)) [|{|update_behavior1,clear_behavior1,endDiagram_behavior1|}|] TokenManager_behavior1_t(0,0))\n" +
				"Internal_behavior1(ID_behavior1) = StartActivity_behavior1(ID_behavior1); Node_behavior1; EndActivity_behavior1(ID_behavior1)\n" +
				"StartActivity_behavior1(ID_behavior1) = startActivity_behavior1.ID_behavior1 -> SKIP\n" +
				"EndActivity_behavior1(ID_behavior1) = endActivity_behavior1.ID_behavior1 -> SKIP\n" +
				"AlphabetDiagram_behavior1(init_behavior1_t_alphabet) = {|update_behavior1.1,ce_behavior1.1,endDiagram_behavior1|}\n" +
				"AlphabetDiagram_behavior1(CB1_behavior1_t_alphabet) = {|ce_behavior1.1,startActivity_CB1.1,endActivity_CB1.1,ce_behavior1.2,endDiagram_behavior1|}\n" +
				"AlphabetDiagram_behavior1(fin1_behavior1_t_alphabet) = {|ce_behavior1.2,clear_behavior1.1,endDiagram_behavior1|}\n" +
				"ProcessDiagram_behavior1(init_behavior1_t_alphabet) = init_behavior1_t\n" +
				"ProcessDiagram_behavior1(CB1_behavior1_t_alphabet) = CB1_behavior1_t\n" +
				"ProcessDiagram_behavior1(fin1_behavior1_t_alphabet) = fin1_behavior1_t\n" +
				"Node_behavior1 = || x:alphabet_behavior1 @ [AlphabetDiagram_behavior1(x)] ProcessDiagram_behavior1(x)\n" +
				"init1_behavior1_t = update_behavior1.1!(1-0) -> ((ce_behavior1.1 -> SKIP))\n" +
				"CB1_behavior1 = ((ce_behavior1.1 -> SKIP)); startActivity_CB1.1 -> endActivity_CB1.1 -> ((ce_behavior1.2 -> SKIP)); CB1_behavior1\n" +
				"CB1_behavior1_t = CB1_behavior1 /\\ END_DIAGRAM_behavior1\n" +
				"fin1_behavior1 = ((ce_behavior1.2 -> SKIP)); clear_behavior1.1 -> SKIP\n" +
				"fin1_behavior1_t = fin1_behavior1 /\\ END_DIAGRAM_behavior1\n" +
				"init_behavior1_t = (init1_behavior1_t) /\\ END_DIAGRAM_behavior1\n" +
				"\n" +
				"TokenManager_behavior1(x,init) = update_behavior1?c?y:limiteUpdate_behavior1 -> x+y < 10 & x+y > -10 & TokenManager_behavior1(x+y,1) [] clear_behavior1?c -> endDiagram_behavior1 -> SKIP [] x == 0 & init == 1 & endDiagram_behavior1 -> SKIP\n" +
				"TokenManager_behavior1_t(x,init) = TokenManager_behavior1(x,init)\n" +
				"\n" +
				"datatype alphabet_CB1 = init_CB1_t_alphabet | act1_CB1_t_alphabet| fin1_CB1_t_alphabet\n" +
				"countCe_CB1 = {1..2}\n" +
				"countUpdate_CB1 = {1..1}\n" +
				"countClear_CB1 = {1..1}\n" +
				"limiteUpdate_CB1 = {(1)..(1)}\n" +
				"channel startActivity_CB1: ID_CB1\n" +
				"channel endActivity_CB1: ID_CB1\n" +
				"channel ce_CB1: countCe_CB1\n" +
				"channel clear_CB1: countClear_CB1\n" +
				"channel update_CB1: countUpdate_CB1.limiteUpdate_CB1\n" +
				"channel endDiagram_CB1\n" +
				"channel event_act1_CB1\n" +
				"channel lock_act1_CB1: T\n" +
				"END_DIAGRAM_CB1 = endDiagram_CB1 -> SKIP\n" +
				"CB1(ID_CB1) = ((Internal_CB1(ID_CB1) [|{|update_CB1,clear_CB1,endDiagram_CB1|}|] TokenManager_CB1_t(0,0)) [|{|lock_act1_CB1,endDiagram_CB1|}|] Lock_CB1)\n" +
				"Internal_CB1(ID_CB1) = StartActivity_CB1(ID_CB1); Node_CB1; EndActivity_CB1(ID_CB1)\n" +
				"StartActivity_CB1(ID_CB1) = startActivity_CB1.ID_CB1 -> SKIP\n" +
				"EndActivity_CB1(ID_CB1) = endActivity_CB1.ID_CB1 -> SKIP\n" +
				"AlphabetDiagram_CB1(init_CB1_t_alphabet) = {|update_CB1.1,ce_CB1.1,endDiagram_CB1|}\n" +
				"AlphabetDiagram_CB1(act1_CB1_t_alphabet) = {|ce_CB1.1,lock_act1_CB1,event_act1_CB1,ce_CB1.2,endDiagram_CB1|}\n" +
				"AlphabetDiagram_CB1(fin1_CB1_t_alphabet) = {|ce_CB1.2,clear_CB1.1,endDiagram_CB1|}\n" +
				"ProcessDiagram_CB1(init_CB1_t_alphabet) = init_CB1_t\n" +
				"ProcessDiagram_CB1(act1_CB1_t_alphabet) = act1_CB1_t\n" +
				"ProcessDiagram_CB1(fin1_CB1_t_alphabet) = fin1_CB1_t\n" +
				"Node_CB1 = || x:alphabet_CB1 @ [AlphabetDiagram_CB1(x)] ProcessDiagram_CB1(x)\n" +
				"init1_CB1_t = update_CB1.1!(1-0) -> ((ce_CB1.1 -> SKIP))\n" +
				"act1_CB1 = ((ce_CB1.1 -> SKIP)); lock_act1_CB1.lock -> event_act1_CB1 -> lock_act1_CB1.unlock -> ((ce_CB1.2 -> SKIP)); act1_CB1\n" +
				"act1_CB1_t = act1_CB1 /\\ END_DIAGRAM_CB1\n" +
				"fin1_CB1 = ((ce_CB1.2 -> SKIP)); clear_CB1.1 -> SKIP\n" +
				"fin1_CB1_t = fin1_CB1 /\\ END_DIAGRAM_CB1\n" +
				"init_CB1_t = (init1_CB1_t) /\\ END_DIAGRAM_CB1\n" +
				"\n" +
				"TokenManager_CB1(x,init) = update_CB1?c?y:limiteUpdate_CB1 -> x+y < 10 & x+y > -10 & TokenManager_CB1(x+y,1) [] clear_CB1?c -> endDiagram_CB1 -> SKIP [] x == 0 & init == 1 & endDiagram_CB1 -> SKIP\n" +
				"TokenManager_CB1_t(x,init) = TokenManager_CB1(x,init)\n" +
				"Lock_act1_CB1 = lock_act1_CB1.lock -> lock_act1_CB1.unlock -> Lock_act1_CB1 [] endDiagram_CB1 -> SKIP\n" +
				"Lock_CB1 = Lock_act1_CB1\n" +
				"\n" +
				"assert MAIN :[deadlock free]\n" +
				"assert MAIN :[divergence free]\n" +
				"assert MAIN :[deterministic]");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução do call behavior
	 * */
	@Test
	public void TestNodesBehavior2() {
		parser2.clearBuffer();
		String actual = parser2.parserDiagram();
		StringBuffer expected = new StringBuffer();
		expected.append("ID_CB1 = {1..2}\n" +
				"ID_behavior2 = {1..1}\n" +
				"datatype T = lock | unlock\n" +
				"datatype alphabet_behavior2 = init_behavior2_t_alphabet | CB2_behavior2_t_alphabet| CB1_behavior2_t_alphabet| fin1_behavior2_t_alphabet\n" +
				"countCe_behavior2 = {1..3}\n" +
				"countUpdate_behavior2 = {1..1}\n" +
				"countClear_behavior2 = {1..1}\n" +
				"limiteUpdate_behavior2 = {(1)..(1)}\n" +
				"channel startActivity_behavior2: ID_behavior2\n" +
				"channel endActivity_behavior2: ID_behavior2\n" +
				"channel ce_behavior2: countCe_behavior2\n" +
				"channel clear_behavior2: countClear_behavior2\n" +
				"channel update_behavior2: countUpdate_behavior2.limiteUpdate_behavior2\n" +
				"channel endDiagram_behavior2\n" +
				"channel loop\n" +
				"channel dc\n" +
				"MAIN = behavior2(1); LOOP\n" +
				"LOOP = loop -> LOOP\n" +
				"END_DIAGRAM_behavior2 = endDiagram_behavior2 -> SKIP\n" +
				"behavior2(ID_behavior2) = (((Internal_behavior2(ID_behavior2) [|{|startActivity_CB1.1,endActivity_CB1.1|}|] CB1(1)) [|{|startActivity_CB1.2,endActivity_CB1.2|}|] CB1(2)) [|{|update_behavior2,clear_behavior2,endDiagram_behavior2|}|] TokenManager_behavior2_t(0,0))\n" +
				"Internal_behavior2(ID_behavior2) = StartActivity_behavior2(ID_behavior2); Node_behavior2; EndActivity_behavior2(ID_behavior2)\n" +
				"StartActivity_behavior2(ID_behavior2) = startActivity_behavior2.ID_behavior2 -> SKIP\n" +
				"EndActivity_behavior2(ID_behavior2) = endActivity_behavior2.ID_behavior2 -> SKIP\n" +
				"AlphabetDiagram_behavior2(init_behavior2_t_alphabet) = {|update_behavior2.1,ce_behavior2.1,endDiagram_behavior2|}\n" +
				"AlphabetDiagram_behavior2(CB2_behavior2_t_alphabet) = {|ce_behavior2.2,startActivity_CB1.2,endActivity_CB1.2,ce_behavior2.3,endDiagram_behavior2|}\n" +
				"AlphabetDiagram_behavior2(CB1_behavior2_t_alphabet) = {|ce_behavior2.1,startActivity_CB1.1,endActivity_CB1.1,ce_behavior2.2,endDiagram_behavior2|}\n" +
				"AlphabetDiagram_behavior2(fin1_behavior2_t_alphabet) = {|ce_behavior2.3,clear_behavior2.1,endDiagram_behavior2|}\n" +
				"ProcessDiagram_behavior2(init_behavior2_t_alphabet) = init_behavior2_t\n" +
				"ProcessDiagram_behavior2(CB2_behavior2_t_alphabet) = CB2_behavior2_t\n" +
				"ProcessDiagram_behavior2(CB1_behavior2_t_alphabet) = CB1_behavior2_t\n" +
				"ProcessDiagram_behavior2(fin1_behavior2_t_alphabet) = fin1_behavior2_t\n" +
				"Node_behavior2 = || x:alphabet_behavior2 @ [AlphabetDiagram_behavior2(x)] ProcessDiagram_behavior2(x)\n" +
				"init1_behavior2_t = update_behavior2.1!(1-0) -> ((ce_behavior2.1 -> SKIP))\n" +
				"CB1_behavior2 = ((ce_behavior2.1 -> SKIP)); startActivity_CB1.1 -> endActivity_CB1.1 -> ((ce_behavior2.2 -> SKIP)); CB1_behavior2\n" +
				"CB1_behavior2_t = CB1_behavior2 /\\ END_DIAGRAM_behavior2\n" +
				"CB2_behavior2 = ((ce_behavior2.2 -> SKIP)); startActivity_CB1.2 -> endActivity_CB1.2 -> ((ce_behavior2.3 -> SKIP)); CB2_behavior2\n" +
				"CB2_behavior2_t = CB2_behavior2 /\\ END_DIAGRAM_behavior2\n" +
				"fin1_behavior2 = ((ce_behavior2.3 -> SKIP)); clear_behavior2.1 -> SKIP\n" +
				"fin1_behavior2_t = fin1_behavior2 /\\ END_DIAGRAM_behavior2\n" +
				"init_behavior2_t = (init1_behavior2_t) /\\ END_DIAGRAM_behavior2\n" +
				"\n" +
				"TokenManager_behavior2(x,init) = update_behavior2?c?y:limiteUpdate_behavior2 -> x+y < 10 & x+y > -10 & TokenManager_behavior2(x+y,1) [] clear_behavior2?c -> endDiagram_behavior2 -> SKIP [] x == 0 & init == 1 & endDiagram_behavior2 -> SKIP\n" +
				"TokenManager_behavior2_t(x,init) = TokenManager_behavior2(x,init)\n" +
				"\n" +
				"datatype alphabet_CB1 = init_CB1_t_alphabet | act1_CB1_t_alphabet| fin1_CB1_t_alphabet\n" +
				"countCe_CB1 = {1..2}\n" +
				"countUpdate_CB1 = {1..1}\n" +
				"countClear_CB1 = {1..1}\n" +
				"limiteUpdate_CB1 = {(1)..(1)}\n" +
				"channel startActivity_CB1: ID_CB1\n" +
				"channel endActivity_CB1: ID_CB1\n" +
				"channel ce_CB1: countCe_CB1\n" +
				"channel clear_CB1: countClear_CB1\n" +
				"channel update_CB1: countUpdate_CB1.limiteUpdate_CB1\n" +
				"channel endDiagram_CB1\n" +
				"channel event_act1_CB1\n" +
				"channel lock_act1_CB1: T\n" +
				"END_DIAGRAM_CB1 = endDiagram_CB1 -> SKIP\n" +
				"CB1(ID_CB1) = ((Internal_CB1(ID_CB1) [|{|update_CB1,clear_CB1,endDiagram_CB1|}|] TokenManager_CB1_t(0,0)) [|{|lock_act1_CB1,endDiagram_CB1|}|] Lock_CB1)\n" +
				"Internal_CB1(ID_CB1) = StartActivity_CB1(ID_CB1); Node_CB1; EndActivity_CB1(ID_CB1)\n" +
				"StartActivity_CB1(ID_CB1) = startActivity_CB1.ID_CB1 -> SKIP\n" +
				"EndActivity_CB1(ID_CB1) = endActivity_CB1.ID_CB1 -> SKIP\n" +
				"AlphabetDiagram_CB1(init_CB1_t_alphabet) = {|update_CB1.1,ce_CB1.1,endDiagram_CB1|}\n" +
				"AlphabetDiagram_CB1(act1_CB1_t_alphabet) = {|ce_CB1.1,lock_act1_CB1,event_act1_CB1,ce_CB1.2,endDiagram_CB1|}\n" +
				"AlphabetDiagram_CB1(fin1_CB1_t_alphabet) = {|ce_CB1.2,clear_CB1.1,endDiagram_CB1|}\n" +
				"ProcessDiagram_CB1(init_CB1_t_alphabet) = init_CB1_t\n" +
				"ProcessDiagram_CB1(act1_CB1_t_alphabet) = act1_CB1_t\n" +
				"ProcessDiagram_CB1(fin1_CB1_t_alphabet) = fin1_CB1_t\n" +
				"Node_CB1 = || x:alphabet_CB1 @ [AlphabetDiagram_CB1(x)] ProcessDiagram_CB1(x)\n" +
				"init1_CB1_t = update_CB1.1!(1-0) -> ((ce_CB1.1 -> SKIP))\n" +
				"act1_CB1 = ((ce_CB1.1 -> SKIP)); lock_act1_CB1.lock -> event_act1_CB1 -> lock_act1_CB1.unlock -> ((ce_CB1.2 -> SKIP)); act1_CB1\n" +
				"act1_CB1_t = act1_CB1 /\\ END_DIAGRAM_CB1\n" +
				"fin1_CB1 = ((ce_CB1.2 -> SKIP)); clear_CB1.1 -> SKIP\n" +
				"fin1_CB1_t = fin1_CB1 /\\ END_DIAGRAM_CB1\n" +
				"init_CB1_t = (init1_CB1_t) /\\ END_DIAGRAM_CB1\n" +
				"\n" +
				"TokenManager_CB1(x,init) = update_CB1?c?y:limiteUpdate_CB1 -> x+y < 10 & x+y > -10 & TokenManager_CB1(x+y,1) [] clear_CB1?c -> endDiagram_CB1 -> SKIP [] x == 0 & init == 1 & endDiagram_CB1 -> SKIP\n" +
				"TokenManager_CB1_t(x,init) = TokenManager_CB1(x,init)\n" +
				"Lock_act1_CB1 = lock_act1_CB1.lock -> lock_act1_CB1.unlock -> Lock_act1_CB1 [] endDiagram_CB1 -> SKIP\n" +
				"Lock_CB1 = Lock_act1_CB1\n" +
				"\n" +
				"assert MAIN :[deadlock free]\n" +
				"assert MAIN :[divergence free]\n" +
				"assert MAIN :[deterministic]");
		
		assertEquals(expected.toString(), actual);
	}
	
	

	/*
	 * Teste de Tradução do call behavior
	 * */
	@Test
	public void TestNodesBehavior3() {
		parser3.clearBuffer();
		String actual = parser3.parserDiagram();
		StringBuffer expected = new StringBuffer();
		expected.append("ID_CB2 = {1..1}\n" +
				"ID_CB1 = {1..1}\n" +
				"ID_behavior3 = {1..1}\n" +
				"datatype T = lock | unlock\n" +
				"datatype alphabet_behavior3 = init_behavior3_t_alphabet | CB2_behavior3_t_alphabet| CB1_behavior3_t_alphabet| fin1_behavior3_t_alphabet\n" +
				"countCe_behavior3 = {1..3}\n" +
				"countUpdate_behavior3 = {1..1}\n" +
				"countClear_behavior3 = {1..1}\n" +
				"limiteUpdate_behavior3 = {(1)..(1)}\n" +
				"channel startActivity_behavior3: ID_behavior3\n" +
				"channel endActivity_behavior3: ID_behavior3\n" +
				"channel ce_behavior3: countCe_behavior3\n" +
				"channel clear_behavior3: countClear_behavior3\n" +
				"channel update_behavior3: countUpdate_behavior3.limiteUpdate_behavior3\n" +
				"channel endDiagram_behavior3\n" +
				"channel loop\n" +
				"channel dc\n" +
				"MAIN = behavior3(1); LOOP\n" +
				"LOOP = loop -> LOOP\n" +
				"END_DIAGRAM_behavior3 = endDiagram_behavior3 -> SKIP\n" +
				"behavior3(ID_behavior3) = (((Internal_behavior3(ID_behavior3) [|{|startActivity_CB1.1,endActivity_CB1.1|}|] CB1(1)) [|{|startActivity_CB2.1,endActivity_CB2.1|}|] CB2(1)) [|{|update_behavior3,clear_behavior3,endDiagram_behavior3|}|] TokenManager_behavior3_t(0,0))\n" +
				"Internal_behavior3(ID_behavior3) = StartActivity_behavior3(ID_behavior3); Node_behavior3; EndActivity_behavior3(ID_behavior3)\n" +
				"StartActivity_behavior3(ID_behavior3) = startActivity_behavior3.ID_behavior3 -> SKIP\n" +
				"EndActivity_behavior3(ID_behavior3) = endActivity_behavior3.ID_behavior3 -> SKIP\n" +
				"AlphabetDiagram_behavior3(init_behavior3_t_alphabet) = {|update_behavior3.1,ce_behavior3.1,endDiagram_behavior3|}\n" +
				"AlphabetDiagram_behavior3(CB2_behavior3_t_alphabet) = {|ce_behavior3.2,startActivity_CB2.1,endActivity_CB2.1,ce_behavior3.3,endDiagram_behavior3|}\n" +
				"AlphabetDiagram_behavior3(CB1_behavior3_t_alphabet) = {|ce_behavior3.1,startActivity_CB1.1,endActivity_CB1.1,ce_behavior3.2,endDiagram_behavior3|}\n" +
				"AlphabetDiagram_behavior3(fin1_behavior3_t_alphabet) = {|ce_behavior3.3,clear_behavior3.1,endDiagram_behavior3|}\n" +
				"ProcessDiagram_behavior3(init_behavior3_t_alphabet) = init_behavior3_t\n" +
				"ProcessDiagram_behavior3(CB2_behavior3_t_alphabet) = CB2_behavior3_t\n" +
				"ProcessDiagram_behavior3(CB1_behavior3_t_alphabet) = CB1_behavior3_t\n" +
				"ProcessDiagram_behavior3(fin1_behavior3_t_alphabet) = fin1_behavior3_t\n" +
				"Node_behavior3 = || x:alphabet_behavior3 @ [AlphabetDiagram_behavior3(x)] ProcessDiagram_behavior3(x)\n" +
				"init1_behavior3_t = update_behavior3.1!(1-0) -> ((ce_behavior3.1 -> SKIP))\n" +
				"CB1_behavior3 = ((ce_behavior3.1 -> SKIP)); startActivity_CB1.1 -> endActivity_CB1.1 -> ((ce_behavior3.2 -> SKIP)); CB1_behavior3\n" +
				"CB1_behavior3_t = CB1_behavior3 /\\ END_DIAGRAM_behavior3\n" +
				"CB2_behavior3 = ((ce_behavior3.2 -> SKIP)); startActivity_CB2.1 -> endActivity_CB2.1 -> ((ce_behavior3.3 -> SKIP)); CB2_behavior3\n" +
				"CB2_behavior3_t = CB2_behavior3 /\\ END_DIAGRAM_behavior3\n" +
				"fin1_behavior3 = ((ce_behavior3.3 -> SKIP)); clear_behavior3.1 -> SKIP\n" +
				"fin1_behavior3_t = fin1_behavior3 /\\ END_DIAGRAM_behavior3\n" +
				"init_behavior3_t = (init1_behavior3_t) /\\ END_DIAGRAM_behavior3\n" +
				"\n" +
				"TokenManager_behavior3(x,init) = update_behavior3?c?y:limiteUpdate_behavior3 -> x+y < 10 & x+y > -10 & TokenManager_behavior3(x+y,1) [] clear_behavior3?c -> endDiagram_behavior3 -> SKIP [] x == 0 & init == 1 & endDiagram_behavior3 -> SKIP\n" +
				"TokenManager_behavior3_t(x,init) = TokenManager_behavior3(x,init)\n" +
				"\n" +
				"datatype alphabet_CB1 = init_CB1_t_alphabet | act1_CB1_t_alphabet| fin1_CB1_t_alphabet\n" +
				"countCe_CB1 = {1..2}\n" +
				"countUpdate_CB1 = {1..1}\n" +
				"countClear_CB1 = {1..1}\n" +
				"limiteUpdate_CB1 = {(1)..(1)}\n" +
				"channel startActivity_CB1: ID_CB1\n" +
				"channel endActivity_CB1: ID_CB1\n" +
				"channel ce_CB1: countCe_CB1\n" +
				"channel clear_CB1: countClear_CB1\n" +
				"channel update_CB1: countUpdate_CB1.limiteUpdate_CB1\n" +
				"channel endDiagram_CB1\n" +
				"channel event_act1_CB1\n" +
				"channel lock_act1_CB1: T\n" +
				"END_DIAGRAM_CB1 = endDiagram_CB1 -> SKIP\n" +
				"CB1(ID_CB1) = ((Internal_CB1(ID_CB1) [|{|update_CB1,clear_CB1,endDiagram_CB1|}|] TokenManager_CB1_t(0,0)) [|{|lock_act1_CB1,endDiagram_CB1|}|] Lock_CB1)\n" +
				"Internal_CB1(ID_CB1) = StartActivity_CB1(ID_CB1); Node_CB1; EndActivity_CB1(ID_CB1)\n" +
				"StartActivity_CB1(ID_CB1) = startActivity_CB1.ID_CB1 -> SKIP\n" +
				"EndActivity_CB1(ID_CB1) = endActivity_CB1.ID_CB1 -> SKIP\n" +
				"AlphabetDiagram_CB1(init_CB1_t_alphabet) = {|update_CB1.1,ce_CB1.1,endDiagram_CB1|}\n" +
				"AlphabetDiagram_CB1(act1_CB1_t_alphabet) = {|ce_CB1.1,lock_act1_CB1,event_act1_CB1,ce_CB1.2,endDiagram_CB1|}\n" +
				"AlphabetDiagram_CB1(fin1_CB1_t_alphabet) = {|ce_CB1.2,clear_CB1.1,endDiagram_CB1|}\n" +
				"ProcessDiagram_CB1(init_CB1_t_alphabet) = init_CB1_t\n" +
				"ProcessDiagram_CB1(act1_CB1_t_alphabet) = act1_CB1_t\n" +
				"ProcessDiagram_CB1(fin1_CB1_t_alphabet) = fin1_CB1_t\n" +
				"Node_CB1 = || x:alphabet_CB1 @ [AlphabetDiagram_CB1(x)] ProcessDiagram_CB1(x)\n" +
				"init1_CB1_t = update_CB1.1!(1-0) -> ((ce_CB1.1 -> SKIP))\n" +
				"act1_CB1 = ((ce_CB1.1 -> SKIP)); lock_act1_CB1.lock -> event_act1_CB1 -> lock_act1_CB1.unlock -> ((ce_CB1.2 -> SKIP)); act1_CB1\n" +
				"act1_CB1_t = act1_CB1 /\\ END_DIAGRAM_CB1\n" +
				"fin1_CB1 = ((ce_CB1.2 -> SKIP)); clear_CB1.1 -> SKIP\n" +
				"fin1_CB1_t = fin1_CB1 /\\ END_DIAGRAM_CB1\n" +
				"init_CB1_t = (init1_CB1_t) /\\ END_DIAGRAM_CB1\n" +
				"\n" +
				"TokenManager_CB1(x,init) = update_CB1?c?y:limiteUpdate_CB1 -> x+y < 10 & x+y > -10 & TokenManager_CB1(x+y,1) [] clear_CB1?c -> endDiagram_CB1 -> SKIP [] x == 0 & init == 1 & endDiagram_CB1 -> SKIP\n" +
				"TokenManager_CB1_t(x,init) = TokenManager_CB1(x,init)\n" +
				"Lock_act1_CB1 = lock_act1_CB1.lock -> lock_act1_CB1.unlock -> Lock_act1_CB1 [] endDiagram_CB1 -> SKIP\n" +
				"Lock_CB1 = Lock_act1_CB1\n" +
				"\n" +
				"datatype alphabet_CB2 = init_CB2_t_alphabet | act1_CB2_t_alphabet| fin1_CB2_t_alphabet\n" +
				"countCe_CB2 = {1..2}\n" +
				"countUpdate_CB2 = {1..1}\n" +
				"countClear_CB2 = {1..1}\n" +
				"limiteUpdate_CB2 = {(1)..(1)}\n" +
				"channel startActivity_CB2: ID_CB2\n" +
				"channel endActivity_CB2: ID_CB2\n" +
				"channel ce_CB2: countCe_CB2\n" +
				"channel clear_CB2: countClear_CB2\n" +
				"channel update_CB2: countUpdate_CB2.limiteUpdate_CB2\n" +
				"channel endDiagram_CB2\n" +
				"channel event_act1_CB2\n" +
				"channel lock_act1_CB2: T\n" +
				"END_DIAGRAM_CB2 = endDiagram_CB2 -> SKIP\n" +
				"CB2(ID_CB2) = ((Internal_CB2(ID_CB2) [|{|update_CB2,clear_CB2,endDiagram_CB2|}|] TokenManager_CB2_t(0,0)) [|{|lock_act1_CB2,endDiagram_CB2|}|] Lock_CB2)\n" +
				"Internal_CB2(ID_CB2) = StartActivity_CB2(ID_CB2); Node_CB2; EndActivity_CB2(ID_CB2)\n" +
				"StartActivity_CB2(ID_CB2) = startActivity_CB2.ID_CB2 -> SKIP\n" +
				"EndActivity_CB2(ID_CB2) = endActivity_CB2.ID_CB2 -> SKIP\n" +
				"AlphabetDiagram_CB2(init_CB2_t_alphabet) = {|update_CB2.1,ce_CB2.1,endDiagram_CB2|}\n" +
				"AlphabetDiagram_CB2(act1_CB2_t_alphabet) = {|ce_CB2.1,lock_act1_CB2,event_act1_CB2,ce_CB2.2,endDiagram_CB2|}\n" +
				"AlphabetDiagram_CB2(fin1_CB2_t_alphabet) = {|ce_CB2.2,clear_CB2.1,endDiagram_CB2|}\n" +
				"ProcessDiagram_CB2(init_CB2_t_alphabet) = init_CB2_t\n" +
				"ProcessDiagram_CB2(act1_CB2_t_alphabet) = act1_CB2_t\n" +
				"ProcessDiagram_CB2(fin1_CB2_t_alphabet) = fin1_CB2_t\n" +
				"Node_CB2 = || x:alphabet_CB2 @ [AlphabetDiagram_CB2(x)] ProcessDiagram_CB2(x)\n" +
				"init1_CB2_t = update_CB2.1!(1-0) -> ((ce_CB2.1 -> SKIP))\n" +
				"act1_CB2 = ((ce_CB2.1 -> SKIP)); lock_act1_CB2.lock -> event_act1_CB2 -> lock_act1_CB2.unlock -> ((ce_CB2.2 -> SKIP)); act1_CB2\n" +
				"act1_CB2_t = act1_CB2 /\\ END_DIAGRAM_CB2\n" +
				"fin1_CB2 = ((ce_CB2.2 -> SKIP)); clear_CB2.1 -> SKIP\n" +
				"fin1_CB2_t = fin1_CB2 /\\ END_DIAGRAM_CB2\n" +
				"init_CB2_t = (init1_CB2_t) /\\ END_DIAGRAM_CB2\n" +
				"\n" +
				"TokenManager_CB2(x,init) = update_CB2?c?y:limiteUpdate_CB2 -> x+y < 10 & x+y > -10 & TokenManager_CB2(x+y,1) [] clear_CB2?c -> endDiagram_CB2 -> SKIP [] x == 0 & init == 1 & endDiagram_CB2 -> SKIP\n" +
				"TokenManager_CB2_t(x,init) = TokenManager_CB2(x,init)\n" +
				"Lock_act1_CB2 = lock_act1_CB2.lock -> lock_act1_CB2.unlock -> Lock_act1_CB2 [] endDiagram_CB2 -> SKIP\n" +
				"Lock_CB2 = Lock_act1_CB2\n" +
				"\n" +
				"assert MAIN :[deadlock free]\n" +
				"assert MAIN :[divergence free]\n" +
				"assert MAIN :[deterministic]");
		
		assertEquals(expected.toString(), actual);
	}

	/*
	 * Teste de Tradução do call behavior
	 * */
	@Test
	public void TestNodesBehavior4() {
		parser4.clearBuffer();
		String actual = parser4.parserDiagram();
		StringBuffer expected = new StringBuffer();
		expected.append("ID_CB1 = {1..1}\n" +
				"ID_behavior4 = {1..1}\n" +
				"datatype T = lock | unlock\n" +
				"datatype alphabet_behavior4 = init_behavior4_t_alphabet | CB1_behavior4_t_alphabet| fin1_behavior4_t_alphabet\n" +
				"x_behavior4 = {0..1}\n" +
				"z_behavior4 = {0..1}\n" +
				"countGet_behavior4 = {1..2}\n" +
				"countSet_behavior4 = {1..2}\n" +
				"countOe_behavior4 = {1..2}\n" +
				"countUpdate_behavior4 = {1..1}\n" +
				"countClear_behavior4 = {1..1}\n" +
				"limiteUpdate_behavior4 = {(1)..(1)}\n" +
				"channel startActivity_behavior4: ID_behavior4.x_behavior4\n" +
				"channel endActivity_behavior4: ID_behavior4\n" +
				"channel get_x_behavior4: countGet_behavior4.x_behavior4\n" +
				"channel get_z_CB1_behavior4: countGet_behavior4.z_behavior4\n" +
				"channel set_x_behavior4: countSet_behavior4.x_behavior4\n" +
				"channel set_z_CB1_behavior4: countSet_behavior4.z_behavior4\n" +
				"channel oe_x_behavior4: countOe_behavior4.x_behavior4\n" +
				"channel clear_behavior4: countClear_behavior4\n" +
				"channel update_behavior4: countUpdate_behavior4.limiteUpdate_behavior4\n" +
				"channel endDiagram_behavior4\n" +
				"channel loop\n" +
				"channel dc\n" +
				"MAIN = behavior4(1); LOOP\n" +
				"LOOP = loop -> LOOP\n" +
				"END_DIAGRAM_behavior4 = endDiagram_behavior4 -> SKIP\n" +
				"behavior4(ID_behavior4) = (((Internal_behavior4(ID_behavior4) [|{|startActivity_CB1.1,endActivity_CB1.1|}|] CB1(1)) [|{|update_behavior4,clear_behavior4,endDiagram_behavior4|}|] TokenManager_behavior4_t(0,0)) [|{|get_x_behavior4,set_x_behavior4,endActivity_behavior4|}|] Mem_behavior4)\n" +
				"Internal_behavior4(ID_behavior4) = StartActivity_behavior4(ID_behavior4); Node_behavior4; EndActivity_behavior4(ID_behavior4)\n" +
				"StartActivity_behavior4(ID_behavior4) = startActivity_behavior4.ID_behavior4?x -> set_x_behavior4.2!x -> SKIP\n" +
				"EndActivity_behavior4(ID_behavior4) = endActivity_behavior4.ID_behavior4 -> SKIP\n" +
				"AlphabetDiagram_behavior4(init_behavior4_t_alphabet) = {|update_behavior4.1,get_x_behavior4.1,oe_x_behavior4.1,endDiagram_behavior4|}\n" +
				"AlphabetDiagram_behavior4(CB1_behavior4_t_alphabet) = {|oe_x_behavior4.1,startActivity_CB1.1,endActivity_CB1.1,oe_x_behavior4.2,endDiagram_behavior4|}\n" +
				"AlphabetDiagram_behavior4(fin1_behavior4_t_alphabet) = {|oe_x_behavior4.2,clear_behavior4.1,endDiagram_behavior4|}\n" +
				"ProcessDiagram_behavior4(init_behavior4_t_alphabet) = init_behavior4_t\n" +
				"ProcessDiagram_behavior4(CB1_behavior4_t_alphabet) = CB1_behavior4_t\n" +
				"ProcessDiagram_behavior4(fin1_behavior4_t_alphabet) = fin1_behavior4_t\n" +
				"Node_behavior4 = || x:alphabet_behavior4 @ [AlphabetDiagram_behavior4(x)] ProcessDiagram_behavior4(x)\n" +
				"parameter_x_behavior4_t = update_behavior4.1!(1-0) -> get_x_behavior4.1?x -> ((oe_x_behavior4.1!x -> SKIP))\n" +
				"CB1_behavior4 = ((oe_x_behavior4.1?z -> set_z_CB1_behavior4.1!z -> SKIP)); get_z_CB1_behavior4.2?z -> startActivity_CB1.1!z -> endActivity_CB1.1?y -> ((oe_x_behavior4.2!(y) -> SKIP)); CB1_behavior4\n" +
				"CB1_behavior4_t = ((CB1_behavior4 /\\ END_DIAGRAM_behavior4) [|{|get_z_CB1_behavior4,set_z_CB1_behavior4,endDiagram_behavior4|}|] Mem_CB1_behavior4_z_t(0)) \\{|get_z_CB1_behavior4,set_z_CB1_behavior4|}\n" +
				"fin1_behavior4 = ((oe_x_behavior4.2?x -> SKIP)); clear_behavior4.1 -> SKIP\n" +
				"fin1_behavior4_t = fin1_behavior4 /\\ END_DIAGRAM_behavior4\n" +
				"init_behavior4_t = (parameter_x_behavior4_t) /\\ END_DIAGRAM_behavior4\n" +
				"Mem_CB1_behavior4_z(z) = get_z_CB1_behavior4?c!z -> Mem_CB1_behavior4_z(z) [] set_z_CB1_behavior4?c?z -> Mem_CB1_behavior4_z(z)\n" +
				"Mem_CB1_behavior4_z_t(z) = Mem_CB1_behavior4_z(z) /\\ END_DIAGRAM_behavior4\n" +
				"Mem_behavior4_x(x) = get_x_behavior4?c!x -> Mem_behavior4_x(x) [] set_x_behavior4?c?x -> Mem_behavior4_x(x)\n" +
				"Mem_behavior4_x_t(x) = Mem_behavior4_x(x) /\\ (endActivity_behavior4?x -> SKIP)\n" +
				"Mem_behavior4 = Mem_behavior4_x_t(0)\n" +
				"TokenManager_behavior4(x,init) = update_behavior4?c?y:limiteUpdate_behavior4 -> x+y < 10 & x+y > -10 & TokenManager_behavior4(x+y,1) [] clear_behavior4?c -> endDiagram_behavior4 -> SKIP [] x == 0 & init == 1 & endDiagram_behavior4 -> SKIP\n" +
				"TokenManager_behavior4_t(x,init) = TokenManager_behavior4(x,init)\n" +
				"\n" +
				"datatype alphabet_CB1 = init_CB1_t_alphabet | act1_CB1_t_alphabet| parameter_y_CB1_t_alphabet\n" +
				"z_CB1 = {0..1}\n" +
				"y_CB1 = {0..1}\n" +
				"x_CB1 = {0..1}\n" +
				"countGet_CB1 = {1..4}\n" +
				"countSet_CB1 = {1..4}\n" +
				"countOe_CB1 = {1..2}\n" +
				"countUpdate_CB1 = {1..2}\n" +
				"countClear_CB1 = {1..0}\n" +
				"limiteUpdate_CB1 = {(-1)..(1)}\n" +
				"channel startActivity_CB1: ID_CB1.z_CB1\n" +
				"channel endActivity_CB1: ID_CB1.y_CB1\n" +
				"channel get_z_CB1: countGet_CB1.z_CB1\n" +
				"channel get_y_CB1: countGet_CB1.z_CB1\n" +
				"channel get_x_act1_CB1: countGet_CB1.x_CB1\n" +
				"channel get_z_y_CB1: countGet_CB1.z_CB1\n" +
				"channel set_z_CB1: countSet_CB1.z_CB1\n" +
				"channel set_y_CB1: countSet_CB1.z_CB1\n" +
				"channel set_x_act1_CB1: countSet_CB1.x_CB1\n" +
				"channel set_z_y_CB1: countSet_CB1.z_CB1\n" +
				"channel oe_z_CB1: countOe_CB1.z_CB1\n" +
				"channel clear_CB1: countClear_CB1\n" +
				"channel update_CB1: countUpdate_CB1.limiteUpdate_CB1\n" +
				"channel endDiagram_CB1\n" +
				"channel event_act1_CB1\n" +
				"channel lock_act1_CB1: T\n" +
				"END_DIAGRAM_CB1 = endDiagram_CB1 -> SKIP\n" +
				"CB1(ID_CB1) = (((Internal_CB1(ID_CB1) [|{|update_CB1,clear_CB1,endDiagram_CB1|}|] TokenManager_CB1_t(0,0)) [|{|lock_act1_CB1,endDiagram_CB1|}|] Lock_CB1) [|{|get_z_CB1,set_z_CB1,get_y_CB1,set_y_CB1,endActivity_CB1|}|] Mem_CB1)\n" +
				"Internal_CB1(ID_CB1) = StartActivity_CB1(ID_CB1); Node_CB1; EndActivity_CB1(ID_CB1)\n" +
				"StartActivity_CB1(ID_CB1) = startActivity_CB1.ID_CB1?z -> set_z_CB1.4!z -> SKIP\n" +
				"EndActivity_CB1(ID_CB1) = get_y_CB1.4?y -> endActivity_CB1.ID_CB1!y -> SKIP\n" +
				"AlphabetDiagram_CB1(init_CB1_t_alphabet) = {|update_CB1.1,get_z_CB1.1,oe_z_CB1.1,endDiagram_CB1|}\n" +
				"AlphabetDiagram_CB1(act1_CB1_t_alphabet) = {|oe_z_CB1.1,lock_act1_CB1,event_act1_CB1,oe_z_CB1.2,endDiagram_CB1|}\n" +
				"AlphabetDiagram_CB1(parameter_y_CB1_t_alphabet) = {|oe_z_CB1.2,set_y_CB1.3,update_CB1.2,endDiagram_CB1|}\n" +
				"ProcessDiagram_CB1(init_CB1_t_alphabet) = init_CB1_t\n" +
				"ProcessDiagram_CB1(act1_CB1_t_alphabet) = act1_CB1_t\n" +
				"ProcessDiagram_CB1(parameter_y_CB1_t_alphabet) = parameter_y_CB1_t\n" +
				"Node_CB1 = || x:alphabet_CB1 @ [AlphabetDiagram_CB1(x)] ProcessDiagram_CB1(x)\n" +
				"parameter_z_CB1_t = update_CB1.1!(1-0) -> get_z_CB1.1?z -> ((oe_z_CB1.1!z -> SKIP))\n" +
				"act1_CB1 = ((oe_z_CB1.1?x -> set_x_act1_CB1.1!x -> SKIP)); lock_act1_CB1.lock -> event_act1_CB1 -> lock_act1_CB1.unlock -> get_x_act1_CB1.2?x -> ((((x) >= 0 and (x) <= 1) & oe_z_CB1.2!(x) -> SKIP)); act1_CB1\n" +
				"act1_CB1_t = ((act1_CB1 /\\ END_DIAGRAM_CB1) [|{|get_x_act1_CB1,set_x_act1_CB1,endDiagram_CB1|}|] Mem_act1_CB1_x_t(0)) \\{|get_x_act1_CB1,set_x_act1_CB1|}\n" +
				"parameter_y_CB1 = ((oe_z_CB1.2?z -> set_z_y_CB1.2!z -> SKIP)); get_z_y_CB1.3?z -> set_y_CB1.3!z -> update_CB1.2!(0-1) -> parameter_y_CB1\n" +
				"parameter_y_CB1_t = ((parameter_y_CB1 /\\ END_DIAGRAM_CB1) [|{|get_z_y_CB1,set_z_y_CB1,endDiagram_CB1|}|] Mem_y_CB1_z_t(0)) \\{|get_z_y_CB1,set_z_y_CB1|}\n" +
				"init_CB1_t = (parameter_z_CB1_t) /\\ END_DIAGRAM_CB1\n" +
				"Mem_act1_CB1_x(x) = get_x_act1_CB1?c!x -> Mem_act1_CB1_x(x) [] set_x_act1_CB1?c?x -> Mem_act1_CB1_x(x)\n" +
				"Mem_act1_CB1_x_t(x) = Mem_act1_CB1_x(x) /\\ END_DIAGRAM_CB1\n" +
				"Mem_y_CB1_z(z) = get_z_y_CB1?c!z -> Mem_y_CB1_z(z) [] set_z_y_CB1?c?z -> Mem_y_CB1_z(z)\n" +
				"Mem_y_CB1_z_t(z) = Mem_y_CB1_z(z) /\\ END_DIAGRAM_CB1\n" +
				"Mem_CB1_z(z) = get_z_CB1?c!z -> Mem_CB1_z(z) [] set_z_CB1?c?z -> Mem_CB1_z(z)\n" +
				"Mem_CB1_z_t(z) = Mem_CB1_z(z) /\\ (endActivity_CB1?z -> SKIP)\n" +
				"Mem_CB1_y(y) = get_y_CB1?c!y -> Mem_CB1_y(y) [] set_y_CB1?c?y -> Mem_CB1_y(y)\n" +
				"Mem_CB1_y_t(y) = Mem_CB1_y(y) /\\ (endActivity_CB1?y -> SKIP)\n" +
				"Mem_CB1 = (Mem_CB1_z_t(0) [|{|endActivity_CB1|}|] Mem_CB1_y_t(0))\n" +
				"TokenManager_CB1(x,init) = update_CB1?c?y:limiteUpdate_CB1 -> x+y < 10 & x+y > -10 & TokenManager_CB1(x+y,1) [] clear_CB1?c -> endDiagram_CB1 -> SKIP [] x == 0 & init == 1 & endDiagram_CB1 -> SKIP\n" +
				"TokenManager_CB1_t(x,init) = TokenManager_CB1(x,init)\n" +
				"Lock_act1_CB1 = lock_act1_CB1.lock -> lock_act1_CB1.unlock -> Lock_act1_CB1 [] endDiagram_CB1 -> SKIP\n" +
				"Lock_CB1 = Lock_act1_CB1\n" +
				"\n" +
				"assert MAIN :[deadlock free]\n" +
				"assert MAIN :[divergence free]\n" +
				"assert MAIN :[deterministic]");

		assertEquals(expected.toString(), actual);
	}

	/*
	 * Teste de Tradução do call behavior
	 * */
	@Test
	public void TestNodesBehavior5() {
		parser5.clearBuffer();
		String actual = parser5.parserDiagram();
		StringBuffer expected = new StringBuffer();
		expected.append("ID_CB1 = {1..1}\n" +
				"ID_behavior5 = {1..1}\n" +
				"datatype T = lock | unlock\n" +
				"datatype alphabet_behavior5 = init_behavior5_t_alphabet | CB1_behavior5_t_alphabet| fin1_behavior5_t_alphabet\n" +
				"m_behavior5 = {0..1}\n" +
				"n_behavior5 = {0..1}\n" +
				"nm_behavior5 = {0..1}\n" +
				"b_behavior5 = {0..1}\n" +
				"x_behavior5 = {0..1}\n" +
				"countGet_behavior5 = {1..4}\n" +
				"countSet_behavior5 = {1..4}\n" +
				"countOe_behavior5 = {1..3}\n" +
				"countUpdate_behavior5 = {1..3}\n" +
				"countClear_behavior5 = {1..1}\n" +
				"limiteUpdate_behavior5 = {(-1)..(1)}\n" +
				"channel startActivity_behavior5: ID_behavior5.m_behavior5.n_behavior5\n" +
				"channel endActivity_behavior5: ID_behavior5\n" +
				"channel get_m_behavior5: countGet_behavior5.m_behavior5\n" +
				"channel get_n_behavior5: countGet_behavior5.n_behavior5\n" +
				"channel get_b_CB1_behavior5: countGet_behavior5.b_behavior5\n" +
				"channel get_x_CB1_behavior5: countGet_behavior5.x_behavior5\n" +
				"channel set_m_behavior5: countSet_behavior5.m_behavior5\n" +
				"channel set_n_behavior5: countSet_behavior5.n_behavior5\n" +
				"channel set_b_CB1_behavior5: countSet_behavior5.b_behavior5\n" +
				"channel set_x_CB1_behavior5: countSet_behavior5.x_behavior5\n" +
				"channel oe_n_behavior5: countOe_behavior5.n_behavior5\n" +
				"channel oe_m_behavior5: countOe_behavior5.m_behavior5\n" +
				"channel oe_nm_behavior5: countOe_behavior5.nm_behavior5\n" +
				"channel clear_behavior5: countClear_behavior5\n" +
				"channel update_behavior5: countUpdate_behavior5.limiteUpdate_behavior5\n" +
				"channel endDiagram_behavior5\n" +
				"channel loop\n" +
				"channel dc\n" +
				"MAIN = behavior5(1); LOOP\n" +
				"LOOP = loop -> LOOP\n" +
				"END_DIAGRAM_behavior5 = endDiagram_behavior5 -> SKIP\n" +
				"behavior5(ID_behavior5) = (((Internal_behavior5(ID_behavior5) [|{|startActivity_CB1.1,endActivity_CB1.1|}|] CB1(1)) [|{|update_behavior5,clear_behavior5,endDiagram_behavior5|}|] TokenManager_behavior5_t(0,0)) [|{|get_m_behavior5,set_m_behavior5,get_n_behavior5,set_n_behavior5,endActivity_behavior5|}|] Mem_behavior5)\n" +
				"Internal_behavior5(ID_behavior5) = StartActivity_behavior5(ID_behavior5); Node_behavior5; EndActivity_behavior5(ID_behavior5)\n" +
				"StartActivity_behavior5(ID_behavior5) = startActivity_behavior5.ID_behavior5?m?n -> set_m_behavior5.3!m -> set_n_behavior5.4!n -> SKIP\n" +
				"EndActivity_behavior5(ID_behavior5) = endActivity_behavior5.ID_behavior5 -> SKIP\n" +
				"AlphabetDiagram_behavior5(init_behavior5_t_alphabet) = {|update_behavior5.1,get_n_behavior5.1,oe_n_behavior5.1,update_behavior5.2,get_m_behavior5.2,oe_m_behavior5.3,endDiagram_behavior5|}\n" +
				"AlphabetDiagram_behavior5(CB1_behavior5_t_alphabet) = {|oe_n_behavior5.1,oe_m_behavior5.3,startActivity_CB1.1,endActivity_CB1.1,update_behavior5.3,oe_nm_behavior5.2,endDiagram_behavior5|}\n" +
				"AlphabetDiagram_behavior5(fin1_behavior5_t_alphabet) = {|oe_nm_behavior5.2,clear_behavior5.1,endDiagram_behavior5|}\n" +
				"ProcessDiagram_behavior5(init_behavior5_t_alphabet) = init_behavior5_t\n" +
				"ProcessDiagram_behavior5(CB1_behavior5_t_alphabet) = CB1_behavior5_t\n" +
				"ProcessDiagram_behavior5(fin1_behavior5_t_alphabet) = fin1_behavior5_t\n" +
				"Node_behavior5 = || x:alphabet_behavior5 @ [AlphabetDiagram_behavior5(x)] ProcessDiagram_behavior5(x)\n" +
				"parameter_n_behavior5_t = update_behavior5.1!(1-0) -> get_n_behavior5.1?n -> ((oe_n_behavior5.1!n -> SKIP))\n" +
				"fin1_behavior5 = ((oe_nm_behavior5.2?nm -> SKIP)); clear_behavior5.1 -> SKIP\n" +
				"fin1_behavior5_t = fin1_behavior5 /\\ END_DIAGRAM_behavior5\n" +
				"parameter_m_behavior5_t = update_behavior5.2!(1-0) -> get_m_behavior5.2?m -> ((oe_m_behavior5.3!m -> SKIP))\n" +
				"CB1_behavior5 = ((oe_n_behavior5.1?b -> set_b_CB1_behavior5.1!b -> SKIP) ||| (oe_m_behavior5.3?x -> set_x_CB1_behavior5.2!x -> SKIP)); get_b_CB1_behavior5.3?b -> get_x_CB1_behavior5.4?x -> startActivity_CB1.1!b!x -> endActivity_CB1.1?y -> update_behavior5.3!(1-2) -> ((oe_nm_behavior5.2!(y) -> SKIP)); CB1_behavior5\n" +
				"CB1_behavior5_t = (((CB1_behavior5 /\\ END_DIAGRAM_behavior5) [|{|get_b_CB1_behavior5,set_b_CB1_behavior5,endDiagram_behavior5|}|] Mem_CB1_behavior5_b_t(0)) [|{|get_x_CB1_behavior5,set_x_CB1_behavior5,endDiagram_behavior5|}|] Mem_CB1_behavior5_x_t(0)) \\{|get_b_CB1_behavior5,set_b_CB1_behavior5,get_x_CB1_behavior5,set_x_CB1_behavior5|}\n" +
				"init_behavior5_t = (parameter_n_behavior5_t ||| parameter_m_behavior5_t) /\\ END_DIAGRAM_behavior5\n" +
				"Mem_CB1_behavior5_b(b) = get_b_CB1_behavior5?c!b -> Mem_CB1_behavior5_b(b) [] set_b_CB1_behavior5?c?b -> Mem_CB1_behavior5_b(b)\n" +
				"Mem_CB1_behavior5_b_t(b) = Mem_CB1_behavior5_b(b) /\\ END_DIAGRAM_behavior5\n" +
				"Mem_CB1_behavior5_x(x) = get_x_CB1_behavior5?c!x -> Mem_CB1_behavior5_x(x) [] set_x_CB1_behavior5?c?x -> Mem_CB1_behavior5_x(x)\n" +
				"Mem_CB1_behavior5_x_t(x) = Mem_CB1_behavior5_x(x) /\\ END_DIAGRAM_behavior5\n" +
				"Mem_behavior5_m(m) = get_m_behavior5?c!m -> Mem_behavior5_m(m) [] set_m_behavior5?c?m -> Mem_behavior5_m(m)\n" +
				"Mem_behavior5_m_t(m) = Mem_behavior5_m(m) /\\ (endActivity_behavior5?m -> SKIP)\n" +
				"Mem_behavior5_n(n) = get_n_behavior5?c!n -> Mem_behavior5_n(n) [] set_n_behavior5?c?n -> Mem_behavior5_n(n)\n" +
				"Mem_behavior5_n_t(n) = Mem_behavior5_n(n) /\\ (endActivity_behavior5?n -> SKIP)\n" +
				"Mem_behavior5 = (Mem_behavior5_m_t(0) [|{|endActivity_behavior5|}|] Mem_behavior5_n_t(0))\n" +
				"TokenManager_behavior5(x,init) = update_behavior5?c?y:limiteUpdate_behavior5 -> x+y < 10 & x+y > -10 & TokenManager_behavior5(x+y,1) [] clear_behavior5?c -> endDiagram_behavior5 -> SKIP [] x == 0 & init == 1 & endDiagram_behavior5 -> SKIP\n" +
				"TokenManager_behavior5_t(x,init) = TokenManager_behavior5(x,init)\n" +
				"\n" +
				"datatype alphabet_CB1 = init_CB1_t_alphabet | act1_CB1_t_alphabet| parameter_y_CB1_t_alphabet\n" +
				"b_CB1 = {0..1}\n" +
				"x_CB1 = {0..1}\n" +
				"y_CB1 = {0..1}\n" +
				"xb_CB1 = {0..1}\n" +
				"a_CB1 = {0..1}\n" +
				"countGet_CB1 = {1..6}\n" +
				"countSet_CB1 = {1..7}\n" +
				"countOe_CB1 = {1..4}\n" +
				"countUpdate_CB1 = {1..3}\n" +
				"countClear_CB1 = {1..0}\n" +
				"limiteUpdate_CB1 = {(-1)..(1)}\n" +
				"channel startActivity_CB1: ID_CB1.b_CB1.x_CB1\n" +
				"channel endActivity_CB1: ID_CB1.y_CB1\n" +
				"channel get_b_CB1: countGet_CB1.b_CB1\n" +
				"channel get_x_CB1: countGet_CB1.x_CB1\n" +
				"channel get_y_CB1: countGet_CB1.xb_CB1\n" +
				"channel get_xb_y_CB1: countGet_CB1.xb_CB1\n" +
				"channel get_x_act1_CB1: countGet_CB1.x_CB1\n" +
				"channel get_a_act1_CB1: countGet_CB1.a_CB1\n" +
				"channel set_b_CB1: countSet_CB1.b_CB1\n" +
				"channel set_x_CB1: countSet_CB1.x_CB1\n" +
				"channel set_y_CB1: countSet_CB1.xb_CB1\n" +
				"channel set_xb_y_CB1: countSet_CB1.xb_CB1\n" +
				"channel set_x_act1_CB1: countSet_CB1.x_CB1\n" +
				"channel set_a_act1_CB1: countSet_CB1.a_CB1\n" +
				"channel oe_xb_CB1: countOe_CB1.xb_CB1\n" +
				"channel oe_x_CB1: countOe_CB1.x_CB1\n" +
				"channel oe_b_CB1: countOe_CB1.b_CB1\n" +
				"channel clear_CB1: countClear_CB1\n" +
				"channel update_CB1: countUpdate_CB1.limiteUpdate_CB1\n" +
				"channel endDiagram_CB1\n" +
				"channel event_act1_CB1\n" +
				"channel lock_act1_CB1: T\n" +
				"END_DIAGRAM_CB1 = endDiagram_CB1 -> SKIP\n" +
				"CB1(ID_CB1) = (((Internal_CB1(ID_CB1) [|{|update_CB1,clear_CB1,endDiagram_CB1|}|] TokenManager_CB1_t(0,0)) [|{|lock_act1_CB1,endDiagram_CB1|}|] Lock_CB1) [|{|get_b_CB1,set_b_CB1,get_x_CB1,set_x_CB1,get_y_CB1,set_y_CB1,endActivity_CB1|}|] Mem_CB1)\n" +
				"Internal_CB1(ID_CB1) = StartActivity_CB1(ID_CB1); Node_CB1; EndActivity_CB1(ID_CB1)\n" +
				"StartActivity_CB1(ID_CB1) = startActivity_CB1.ID_CB1?b?x -> set_b_CB1.6!b -> set_x_CB1.7!x -> SKIP\n" +
				"EndActivity_CB1(ID_CB1) = get_y_CB1.6?y -> endActivity_CB1.ID_CB1!y -> SKIP\n" +
				"AlphabetDiagram_CB1(init_CB1_t_alphabet) = {|update_CB1.1,get_x_CB1.1,oe_x_CB1.1,update_CB1.3,get_b_CB1.3,oe_b_CB1.4,endDiagram_CB1|}\n" +
				"AlphabetDiagram_CB1(act1_CB1_t_alphabet) = {|oe_x_CB1.1,oe_b_CB1.4,lock_act1_CB1,event_act1_CB1,oe_xb_CB1.2,oe_xb_CB1.3,endDiagram_CB1|}\n" +
				"AlphabetDiagram_CB1(parameter_y_CB1_t_alphabet) = {|oe_xb_CB1.3,oe_xb_CB1.2,set_y_CB1.3,update_CB1.2,endDiagram_CB1|}\n" +
				"ProcessDiagram_CB1(init_CB1_t_alphabet) = init_CB1_t\n" +
				"ProcessDiagram_CB1(act1_CB1_t_alphabet) = act1_CB1_t\n" +
				"ProcessDiagram_CB1(parameter_y_CB1_t_alphabet) = parameter_y_CB1_t\n" +
				"Node_CB1 = || x:alphabet_CB1 @ [AlphabetDiagram_CB1(x)] ProcessDiagram_CB1(x)\n" +
				"parameter_x_CB1_t = update_CB1.1!(1-0) -> get_x_CB1.1?x -> ((oe_x_CB1.1!x -> SKIP))\n" +
				"parameter_y_CB1 = ((oe_xb_CB1.3?xb -> set_xb_y_CB1.1!xb -> SKIP) [] (oe_xb_CB1.2?xb -> set_xb_y_CB1.2!xb -> SKIP)); get_xb_y_CB1.2?xb -> set_y_CB1.3!xb -> update_CB1.2!(0-1) -> parameter_y_CB1\n" +
				"parameter_y_CB1_t = ((parameter_y_CB1 /\\ END_DIAGRAM_CB1) [|{|get_xb_y_CB1,set_xb_y_CB1,endDiagram_CB1|}|] Mem_y_CB1_xb_t(0)) \\{|get_xb_y_CB1,set_xb_y_CB1|}\n" +
				"parameter_b_CB1_t = update_CB1.3!(1-0) -> get_b_CB1.3?b -> ((oe_b_CB1.4!b -> SKIP))\n" +
				"act1_CB1 = ((oe_x_CB1.1?x -> set_x_act1_CB1.4!x -> SKIP) ||| (oe_b_CB1.4?a -> set_a_act1_CB1.5!a -> SKIP)); lock_act1_CB1.lock -> event_act1_CB1 -> lock_act1_CB1.unlock -> get_x_act1_CB1.4?x -> get_a_act1_CB1.5?a -> ((((a) >= 0 and (a) <= 1) & oe_xb_CB1.2!(a) -> SKIP) ||| (((x) >= 0 and (x) <= 1) & oe_xb_CB1.3!(x) -> SKIP)); act1_CB1\n" +
				"act1_CB1_t = (((act1_CB1 /\\ END_DIAGRAM_CB1) [|{|get_x_act1_CB1,set_x_act1_CB1,endDiagram_CB1|}|] Mem_act1_CB1_x_t(0)) [|{|get_a_act1_CB1,set_a_act1_CB1,endDiagram_CB1|}|] Mem_act1_CB1_a_t(0)) \\{|get_x_act1_CB1,set_x_act1_CB1,get_a_act1_CB1,set_a_act1_CB1|}\n" +
				"init_CB1_t = (parameter_x_CB1_t ||| parameter_b_CB1_t) /\\ END_DIAGRAM_CB1\n" +
				"Mem_y_CB1_xb(xb) = get_xb_y_CB1?c!xb -> Mem_y_CB1_xb(xb) [] set_xb_y_CB1?c?xb -> Mem_y_CB1_xb(xb)\n" +
				"Mem_y_CB1_xb_t(xb) = Mem_y_CB1_xb(xb) /\\ END_DIAGRAM_CB1\n" +
				"Mem_act1_CB1_x(x) = get_x_act1_CB1?c!x -> Mem_act1_CB1_x(x) [] set_x_act1_CB1?c?x -> Mem_act1_CB1_x(x)\n" +
				"Mem_act1_CB1_x_t(x) = Mem_act1_CB1_x(x) /\\ END_DIAGRAM_CB1\n" +
				"Mem_act1_CB1_a(a) = get_a_act1_CB1?c!a -> Mem_act1_CB1_a(a) [] set_a_act1_CB1?c?a -> Mem_act1_CB1_a(a)\n" +
				"Mem_act1_CB1_a_t(a) = Mem_act1_CB1_a(a) /\\ END_DIAGRAM_CB1\n" +
				"Mem_CB1_b(b) = get_b_CB1?c!b -> Mem_CB1_b(b) [] set_b_CB1?c?b -> Mem_CB1_b(b)\n" +
				"Mem_CB1_b_t(b) = Mem_CB1_b(b) /\\ (endActivity_CB1?b -> SKIP)\n" +
				"Mem_CB1_x(x) = get_x_CB1?c!x -> Mem_CB1_x(x) [] set_x_CB1?c?x -> Mem_CB1_x(x)\n" +
				"Mem_CB1_x_t(x) = Mem_CB1_x(x) /\\ (endActivity_CB1?x -> SKIP)\n" +
				"Mem_CB1_y(y) = get_y_CB1?c!y -> Mem_CB1_y(y) [] set_y_CB1?c?y -> Mem_CB1_y(y)\n" +
				"Mem_CB1_y_t(y) = Mem_CB1_y(y) /\\ (endActivity_CB1?y -> SKIP)\n" +
				"Mem_CB1 = ((Mem_CB1_b_t(0) [|{|endActivity_CB1|}|] Mem_CB1_x_t(0)) [|{|endActivity_CB1|}|] Mem_CB1_y_t(0))\n" +
				"TokenManager_CB1(x,init) = update_CB1?c?y:limiteUpdate_CB1 -> x+y < 10 & x+y > -10 & TokenManager_CB1(x+y,1) [] clear_CB1?c -> endDiagram_CB1 -> SKIP [] x == 0 & init == 1 & endDiagram_CB1 -> SKIP\n" +
				"TokenManager_CB1_t(x,init) = TokenManager_CB1(x,init)\n" +
				"Lock_act1_CB1 = lock_act1_CB1.lock -> lock_act1_CB1.unlock -> Lock_act1_CB1 [] endDiagram_CB1 -> SKIP\n" +
				"Lock_CB1 = Lock_act1_CB1\n" +
				"\n" +
				"assert MAIN :[deadlock free]\n" +
				"assert MAIN :[divergence free]\n" +
				"assert MAIN :[deterministic]");

		assertEquals(expected.toString(), actual);
	}
}
