package com.ref.activityDiagram.parser;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.*;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.LicenseNotFoundException;
import com.change_vision.jude.api.inf.exception.NonCompatibleException;
import com.change_vision.jude.api.inf.exception.ProjectLockedException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IActivityDiagram;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.project.ModelFinder;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.ref.exceptions.ParsingException;
import com.ref.parser.activityDiagram.ADParser;

public class ADParserTestBehaviourCall {
	
	public static IActivityDiagram ad;
	private static ADParser parser1;
	private static ADParser parser2;
	private static ADParser parser3;
	private static ADParser parser4;
	private static ADParser parser5;
	private static ADParser parser6;
	private static ADParser parser8;
	private static ProjectAccessor projectAccessor1;
	private static ProjectAccessor projectAccessor2;
	private static ProjectAccessor projectAccessor3;
	private static ProjectAccessor projectAccessor4;
	private static ProjectAccessor projectAccessor5;
	private static ProjectAccessor projectAccessor6;
	private static ProjectAccessor projectAccessor8;
	@BeforeClass
	public static void GetDiagram() throws Exception {
		/*try {
			
			
			
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
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/behavior6.asta");
			findElements = findElements(projectAccessor);
			
			for (int i = 0; i < findElements.length; i++) {
				if (findElements[i].getName().equals("behavior6")) {
					ad = (IActivityDiagram) findElements[i];
				}
			}

			parser6 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/behavior8.asta");
			findElements = findElements(projectAccessor);

			for (int i = 0; i < findElements.length; i++) {
				if (findElements[i].getName().equals("behavior7")) {
					ad = (IActivityDiagram) findElements[i];
				}
			}
			
			parser8 = new ADParser(ad.getActivity(), ad.getName(), ad);

		} catch (ProjectNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
*/
	}
	
	private static INamedElement[] findElements(ProjectAccessor projectAccessor) throws ProjectNotFoundException {
		INamedElement[] foundElements = projectAccessor.findElements(new ModelFinder() {
			public boolean isTarget(INamedElement namedElement) {
				return namedElement instanceof IActivityDiagram;
			}
		});
		return foundElements;
	}
	
	/*@AfterClass
	public static void CloseProject() throws Exception {
		AstahAPI.getAstahAPI().getProjectAccessor().close();
		projectAccessor.close();
	}*/
	
	/*
	 * Teste de Tradução do call behavior
	 * */
	@Test
	public void TestNodesBehavior1() throws ParsingException, ClassNotFoundException, LicenseNotFoundException, ProjectNotFoundException, NonCompatibleException, IOException, ProjectLockedException {
		projectAccessor1 = AstahAPI.getAstahAPI().getProjectAccessor();
		projectAccessor1.open("src/test/resources/activityDiagram/behavior1.asta");
		INamedElement[] findElements = findElements(projectAccessor1);

		for (int i = 0; i < findElements.length; i++) {
			if (findElements[i].getName().equals("behavior1")) {
				ad = (IActivityDiagram) findElements[i];
			}
		}
		
		parser1 = new ADParser(ad.getActivity(), ad.getName(), ad);
		
		parser1.clearBuffer();
		String actual = parser1.parserDiagram();
		StringBuffer expected = new StringBuffer();
		expected.append("transparent normal\n" + 
				"ID_CB1 = {1..1}\n" + 
				"ID_behavior1 = {1..1}\n" + 
				"datatype alphabet_behavior1 = init_behavior1_t_alphabet | CB1_behavior1_t_alphabet| fin1_behavior1_t_alphabet\n" + 
				"countCe_behavior1 = {1..2}\n" + 
				"countUpdate_behavior1 = {1..1}\n" + 
				"countClear_behavior1 = {1..1}\n" + 
				"limiteUpdate_behavior1 = {(1)..(1)}\n" + 
				"channel startActivity_behavior1: ID_behavior1\n" + 
				"channel endActivity_behavior1: ID_behavior1\n" + 
				"channel ce_behavior1: ID_behavior1.countCe_behavior1\n" + 
				"channel clear_behavior1: ID_behavior1.countClear_behavior1\n" + 
				"channel update_behavior1: ID_behavior1.countUpdate_behavior1.limiteUpdate_behavior1\n" + 
				"channel endDiagram_behavior1: ID_behavior1\n" + 
				"channel loop\n" + 
				"channel dc\n" + 
				"MAIN = normal(behavior1(1)); LOOP\n" + 
				"LOOP = loop -> LOOP\n" + 
				"END_DIAGRAM_behavior1(id) = endDiagram_behavior1.id -> SKIP\n" + 
				"behavior1(ID_behavior1) = (Internal_behavior1(ID_behavior1) [|{|update_behavior1,clear_behavior1,endDiagram_behavior1|}|] TokenManager_behavior1_t(ID_behavior1,0,0))\n" + 
				"Internal_behavior1(id) = StartActivity_behavior1(id); Node_behavior1(id); EndActivity_behavior1(id)\n" + 
				"StartActivity_behavior1(id) = startActivity_behavior1.id -> SKIP\n" + 
				"EndActivity_behavior1(id) = endActivity_behavior1.id -> SKIP\n" + 
				"AlphabetDiagram_behavior1(id,init_behavior1_t_alphabet) = {|update_behavior1.id.1,ce_behavior1.id.1,endDiagram_behavior1.id|}\n" + 
				"AlphabetDiagram_behavior1(id,CB1_behavior1_t_alphabet) = union({|ce_behavior1.id.1,startActivity_CB1.1,endActivity_CB1.1,ce_behavior1.id.2,endDiagram_behavior1.id|},AlphabetDiagram_CB1_t(1))\n" + 
				"AlphabetDiagram_behavior1(id,fin1_behavior1_t_alphabet) = {|ce_behavior1.id.2,clear_behavior1.id.1,endDiagram_behavior1.id|}\n" + 
				"AlphabetDiagram_behavior1_t(id) = union(union(AlphabetDiagram_behavior1(id,init_behavior1_t_alphabet),AlphabetDiagram_behavior1(id,CB1_behavior1_t_alphabet)),AlphabetDiagram_behavior1(id,fin1_behavior1_t_alphabet))\n" + 
				"\n" + 
				"ProcessDiagram_behavior1(id,init_behavior1_t_alphabet) = normal(init_behavior1_t(id))\n" + 
				"ProcessDiagram_behavior1(id,CB1_behavior1_t_alphabet) = normal(CB1_behavior1_t(id))\n" + 
				"ProcessDiagram_behavior1(id,fin1_behavior1_t_alphabet) = normal(fin1_behavior1_t(id))\n" + 
				"Node_behavior1(id) = || x:alphabet_behavior1 @ [AlphabetDiagram_behavior1(id,x)] ProcessDiagram_behavior1(id,x)\n" + 
				"init1_behavior1_t(id) = update_behavior1.id.1!(1-0) -> ((ce_behavior1.id.1 -> SKIP))\n" + 
				"CB1_behavior1(id) = ((ce_behavior1.id.1 -> SKIP)); normal(CB1(1));((ce_behavior1.id.2 -> SKIP)); CB1_behavior1(id)\n" + 
				"CB1_behavior1_t(id) = CB1_behavior1(id) /\\ END_DIAGRAM_behavior1(id)\n" + 
				"fin1_behavior1(id) = ((ce_behavior1.id.2 -> SKIP)); clear_behavior1.id.1 -> SKIP\n" + 
				"fin1_behavior1_t(id) = fin1_behavior1(id) /\\ END_DIAGRAM_behavior1(id)\n" + 
				"init_behavior1_t(id) = (init1_behavior1_t(id)) /\\ END_DIAGRAM_behavior1(id)\n" + 
				"\n\nAlphabetMemCB1(id) = {|endDiagram_behavior1.id|}\n\n" + 
				"TokenManager_behavior1(id,x,init) = update_behavior1.id?c?y:limiteUpdate_behavior1 -> x+y < 10 & x+y > -10 & TokenManager_behavior1(id,x+y,1) [] clear_behavior1.id?c -> endDiagram_behavior1.id -> SKIP [] x == 0 & init == 1 & endDiagram_behavior1.id -> SKIP\n" + 
				"TokenManager_behavior1_t(id,x,init) = TokenManager_behavior1(id,x,init)\n" + 
				"\n" + 
				"AlphabetPool = {|endDiagram_behavior1|}\n" + 
				"\n" + 
				"datatype alphabet_CB1 = init_CB1_t_alphabet | act1_CB1_t_alphabet| fin1_CB1_t_alphabet\n" + 
				"countCe_CB1 = {1..2}\n" + 
				"countUpdate_CB1 = {1..1}\n" + 
				"countClear_CB1 = {1..1}\n" + 
				"limiteUpdate_CB1 = {(1)..(1)}\n" + 
				"channel startActivity_CB1: ID_CB1\n" + 
				"channel endActivity_CB1: ID_CB1\n" + 
				"channel ce_CB1: ID_CB1.countCe_CB1\n" + 
				"channel clear_CB1: ID_CB1.countClear_CB1\n" + 
				"channel update_CB1: ID_CB1.countUpdate_CB1.limiteUpdate_CB1\n" + 
				"channel endDiagram_CB1: ID_CB1\n" + 
				"channel event_act1_CB1: ID_CB1\n" + 
				"END_DIAGRAM_CB1(id) = endDiagram_CB1.id -> SKIP\n" + 
				"CB1(ID_CB1) = (Internal_CB1(ID_CB1) [|{|update_CB1,clear_CB1,endDiagram_CB1|}|] TokenManager_CB1_t(ID_CB1,0,0))\n" + 
				"Internal_CB1(id) = StartActivity_CB1(id); Node_CB1(id); EndActivity_CB1(id)\n" + 
				"StartActivity_CB1(id) = startActivity_CB1.id -> SKIP\n" + 
				"EndActivity_CB1(id) = endActivity_CB1.id -> SKIP\n" + 
				"AlphabetDiagram_CB1(id,init_CB1_t_alphabet) = {|update_CB1.id.1,ce_CB1.id.1,endDiagram_CB1.id|}\n" + 
				"AlphabetDiagram_CB1(id,act1_CB1_t_alphabet) = {|ce_CB1.id.1,event_act1_CB1.id,ce_CB1.id.2,endDiagram_CB1.id|}\n" + 
				"AlphabetDiagram_CB1(id,fin1_CB1_t_alphabet) = {|ce_CB1.id.2,clear_CB1.id.1,endDiagram_CB1.id|}\n" + 
				"AlphabetDiagram_CB1_t(id) = union(union(AlphabetDiagram_CB1(id,init_CB1_t_alphabet),AlphabetDiagram_CB1(id,act1_CB1_t_alphabet)),AlphabetDiagram_CB1(id,fin1_CB1_t_alphabet))\n" + 
				"\n" + 
				"ProcessDiagram_CB1(id,init_CB1_t_alphabet) = normal(init_CB1_t(id))\n" + 
				"ProcessDiagram_CB1(id,act1_CB1_t_alphabet) = normal(act1_CB1_t(id))\n" + 
				"ProcessDiagram_CB1(id,fin1_CB1_t_alphabet) = normal(fin1_CB1_t(id))\n" + 
				"Node_CB1(id) = || x:alphabet_CB1 @ [AlphabetDiagram_CB1(id,x)] ProcessDiagram_CB1(id,x)\n" + 
				"init1_CB1_t(id) = update_CB1.id.1!(1-0) -> ((ce_CB1.id.1 -> SKIP))\n" + 
				"act1_CB1(id) = ((ce_CB1.id.1 -> SKIP)); event_act1_CB1.id -> ((ce_CB1.id.2 -> SKIP)); act1_CB1(id)\n" + 
				"act1_CB1_t(id) = act1_CB1(id) /\\ END_DIAGRAM_CB1(id)\n" + 
				"fin1_CB1(id) = ((ce_CB1.id.2 -> SKIP)); clear_CB1.id.1 -> SKIP\n" + 
				"fin1_CB1_t(id) = fin1_CB1(id) /\\ END_DIAGRAM_CB1(id)\n" + 
				"init_CB1_t(id) = (init1_CB1_t(id)) /\\ END_DIAGRAM_CB1(id)\n" + 
				"\n\n" + 
				"TokenManager_CB1(id,x,init) = update_CB1.id?c?y:limiteUpdate_CB1 -> x+y < 10 & x+y > -10 & TokenManager_CB1(id,x+y,1) [] clear_CB1.id?c -> endDiagram_CB1.id -> SKIP [] x == 0 & init == 1 & endDiagram_CB1.id -> SKIP\n" + 
				"TokenManager_CB1_t(id,x,init) = TokenManager_CB1(id,x,init)\n" + 
				"\n" + 
				"assert MAIN :[deadlock free]\n" + 
				"assert MAIN :[divergence free]\n" + 
				"assert MAIN :[deterministic]");
		
		assertEquals(expected.toString(), actual);
		projectAccessor1.close();

	}
	
	/*
	 * Teste de Tradução do call behavior
	 * */
	@Test
	public void TestNodesBehavior2() throws ParsingException, ClassNotFoundException, LicenseNotFoundException, ProjectNotFoundException, NonCompatibleException, IOException, ProjectLockedException {
		projectAccessor2 = AstahAPI.getAstahAPI().getProjectAccessor();
		projectAccessor2.open("src/test/resources/activityDiagram/behavior2.asta");
		INamedElement[] findElements = findElements(projectAccessor2);
		findElements = findElements(projectAccessor2);

		for (int i = 0; i < findElements.length; i++) {
			if (findElements[i].getName().equals("behavior2")) {
				ad = (IActivityDiagram) findElements[i];
			}
		}
		
		parser2 = new ADParser(ad.getActivity(), ad.getName(), ad);
		
		parser2.clearBuffer();
		String actual = parser2.parserDiagram();
		StringBuffer expected = new StringBuffer();
		expected.append("transparent normal\n" + 
				"ID_CB1 = {1..2}\n" + 
				"ID_behavior2 = {1..1}\n" + 
				"datatype alphabet_behavior2 = init_behavior2_t_alphabet | CB2_behavior2_t_alphabet| CB1_behavior2_t_alphabet| fin1_behavior2_t_alphabet\n" + 
				"countCe_behavior2 = {1..3}\n" + 
				"countUpdate_behavior2 = {1..1}\n" + 
				"countClear_behavior2 = {1..1}\n" + 
				"limiteUpdate_behavior2 = {(1)..(1)}\n" + 
				"channel startActivity_behavior2: ID_behavior2\n" + 
				"channel endActivity_behavior2: ID_behavior2\n" + 
				"channel ce_behavior2: ID_behavior2.countCe_behavior2\n" + 
				"channel clear_behavior2: ID_behavior2.countClear_behavior2\n" + 
				"channel update_behavior2: ID_behavior2.countUpdate_behavior2.limiteUpdate_behavior2\n" + 
				"channel endDiagram_behavior2: ID_behavior2\n" + 
				"channel loop\n" + 
				"channel dc\n" + 
				"MAIN = normal(behavior2(1)); LOOP\n" + 
				"LOOP = loop -> LOOP\n" + 
				"END_DIAGRAM_behavior2(id) = endDiagram_behavior2.id -> SKIP\n" + 
				"behavior2(ID_behavior2) = (Internal_behavior2(ID_behavior2) [|{|update_behavior2,clear_behavior2,endDiagram_behavior2|}|] TokenManager_behavior2_t(ID_behavior2,0,0))\n" + 
				"Internal_behavior2(id) = StartActivity_behavior2(id); Node_behavior2(id); EndActivity_behavior2(id)\n" + 
				"StartActivity_behavior2(id) = startActivity_behavior2.id -> SKIP\n" + 
				"EndActivity_behavior2(id) = endActivity_behavior2.id -> SKIP\n" + 
				"AlphabetDiagram_behavior2(id,init_behavior2_t_alphabet) = {|update_behavior2.id.1,ce_behavior2.id.1,endDiagram_behavior2.id|}\n" + 
				"AlphabetDiagram_behavior2(id,CB2_behavior2_t_alphabet) = union({|ce_behavior2.id.2,startActivity_CB1.2,endActivity_CB1.2,ce_behavior2.id.3,endDiagram_behavior2.id|},AlphabetDiagram_CB1_t(2))\n" + 
				"AlphabetDiagram_behavior2(id,CB1_behavior2_t_alphabet) = union({|ce_behavior2.id.1,startActivity_CB1.1,endActivity_CB1.1,ce_behavior2.id.2,endDiagram_behavior2.id|},AlphabetDiagram_CB1_t(1))\n" + 
				"AlphabetDiagram_behavior2(id,fin1_behavior2_t_alphabet) = {|ce_behavior2.id.3,clear_behavior2.id.1,endDiagram_behavior2.id|}\n" + 
				"AlphabetDiagram_behavior2_t(id) = union(union(union(AlphabetDiagram_behavior2(id,init_behavior2_t_alphabet),AlphabetDiagram_behavior2(id,CB2_behavior2_t_alphabet)),AlphabetDiagram_behavior2(id,CB1_behavior2_t_alphabet)),AlphabetDiagram_behavior2(id,fin1_behavior2_t_alphabet))\n" + 
				"\n" + 
				"ProcessDiagram_behavior2(id,init_behavior2_t_alphabet) = normal(init_behavior2_t(id))\n" + 
				"ProcessDiagram_behavior2(id,CB2_behavior2_t_alphabet) = normal(CB2_behavior2_t(id))\n" + 
				"ProcessDiagram_behavior2(id,CB1_behavior2_t_alphabet) = normal(CB1_behavior2_t(id))\n" + 
				"ProcessDiagram_behavior2(id,fin1_behavior2_t_alphabet) = normal(fin1_behavior2_t(id))\n" + 
				"Node_behavior2(id) = || x:alphabet_behavior2 @ [AlphabetDiagram_behavior2(id,x)] ProcessDiagram_behavior2(id,x)\n" + 
				"init1_behavior2_t(id) = update_behavior2.id.1!(1-0) -> ((ce_behavior2.id.1 -> SKIP))\n" + 
				"CB1_behavior2(id) = ((ce_behavior2.id.1 -> SKIP)); normal(CB1(1));((ce_behavior2.id.2 -> SKIP)); CB1_behavior2(id)\n" + 
				"CB1_behavior2_t(id) = CB1_behavior2(id) /\\ END_DIAGRAM_behavior2(id)\n" + 
				"CB2_behavior2(id) = ((ce_behavior2.id.2 -> SKIP)); normal(CB1(2));((ce_behavior2.id.3 -> SKIP)); CB2_behavior2(id)\n" + 
				"CB2_behavior2_t(id) = CB2_behavior2(id) /\\ END_DIAGRAM_behavior2(id)\n" + 
				"fin1_behavior2(id) = ((ce_behavior2.id.3 -> SKIP)); clear_behavior2.id.1 -> SKIP\n" + 
				"fin1_behavior2_t(id) = fin1_behavior2(id) /\\ END_DIAGRAM_behavior2(id)\n" + 
				"init_behavior2_t(id) = (init1_behavior2_t(id)) /\\ END_DIAGRAM_behavior2(id)\n" + 
				"\n\n" + 
				"AlphabetMemCB1(id) = {|endDiagram_behavior2.id|}\n\n" + 
				"TokenManager_behavior2(id,x,init) = update_behavior2.id?c?y:limiteUpdate_behavior2 -> x+y < 10 & x+y > -10 & TokenManager_behavior2(id,x+y,1) [] clear_behavior2.id?c -> endDiagram_behavior2.id -> SKIP [] x == 0 & init == 1 & endDiagram_behavior2.id -> SKIP\n" + 
				"TokenManager_behavior2_t(id,x,init) = TokenManager_behavior2(id,x,init)\n" + 
				"\n" + 
				"AlphabetPool = {|endDiagram_behavior2|}\n" + 
				"\n" + 
				"datatype alphabet_CB1 = init_CB1_t_alphabet | act1_CB1_t_alphabet| fin1_CB1_t_alphabet\n" + 
				"countCe_CB1 = {1..2}\n" + 
				"countUpdate_CB1 = {1..1}\n" + 
				"countClear_CB1 = {1..1}\n" + 
				"limiteUpdate_CB1 = {(1)..(1)}\n" + 
				"channel startActivity_CB1: ID_CB1\n" + 
				"channel endActivity_CB1: ID_CB1\n" + 
				"channel ce_CB1: ID_CB1.countCe_CB1\n" + 
				"channel clear_CB1: ID_CB1.countClear_CB1\n" + 
				"channel update_CB1: ID_CB1.countUpdate_CB1.limiteUpdate_CB1\n" + 
				"channel endDiagram_CB1: ID_CB1\n" + 
				"channel event_act1_CB1: ID_CB1\n" + 
				"END_DIAGRAM_CB1(id) = endDiagram_CB1.id -> SKIP\n" + 
				"CB1(ID_CB1) = (Internal_CB1(ID_CB1) [|{|update_CB1,clear_CB1,endDiagram_CB1|}|] TokenManager_CB1_t(ID_CB1,0,0))\n" + 
				"Internal_CB1(id) = StartActivity_CB1(id); Node_CB1(id); EndActivity_CB1(id)\n" + 
				"StartActivity_CB1(id) = startActivity_CB1.id -> SKIP\n" + 
				"EndActivity_CB1(id) = endActivity_CB1.id -> SKIP\n" + 
				"AlphabetDiagram_CB1(id,init_CB1_t_alphabet) = {|update_CB1.id.1,ce_CB1.id.1,endDiagram_CB1.id|}\n" + 
				"AlphabetDiagram_CB1(id,act1_CB1_t_alphabet) = {|ce_CB1.id.1,event_act1_CB1.id,ce_CB1.id.2,endDiagram_CB1.id|}\n" + 
				"AlphabetDiagram_CB1(id,fin1_CB1_t_alphabet) = {|ce_CB1.id.2,clear_CB1.id.1,endDiagram_CB1.id|}\n" + 
				"AlphabetDiagram_CB1_t(id) = union(union(AlphabetDiagram_CB1(id,init_CB1_t_alphabet),AlphabetDiagram_CB1(id,act1_CB1_t_alphabet)),AlphabetDiagram_CB1(id,fin1_CB1_t_alphabet))\n" + 
				"\n" + 
				"ProcessDiagram_CB1(id,init_CB1_t_alphabet) = normal(init_CB1_t(id))\n" + 
				"ProcessDiagram_CB1(id,act1_CB1_t_alphabet) = normal(act1_CB1_t(id))\n" + 
				"ProcessDiagram_CB1(id,fin1_CB1_t_alphabet) = normal(fin1_CB1_t(id))\n" + 
				"Node_CB1(id) = || x:alphabet_CB1 @ [AlphabetDiagram_CB1(id,x)] ProcessDiagram_CB1(id,x)\n" + 
				"init1_CB1_t(id) = update_CB1.id.1!(1-0) -> ((ce_CB1.id.1 -> SKIP))\n" + 
				"act1_CB1(id) = ((ce_CB1.id.1 -> SKIP)); event_act1_CB1.id -> ((ce_CB1.id.2 -> SKIP)); act1_CB1(id)\n" + 
				"act1_CB1_t(id) = act1_CB1(id) /\\ END_DIAGRAM_CB1(id)\n" + 
				"fin1_CB1(id) = ((ce_CB1.id.2 -> SKIP)); clear_CB1.id.1 -> SKIP\n" + 
				"fin1_CB1_t(id) = fin1_CB1(id) /\\ END_DIAGRAM_CB1(id)\n" + 
				"init_CB1_t(id) = (init1_CB1_t(id)) /\\ END_DIAGRAM_CB1(id)\n" + 
				"\n\n" + 
				"TokenManager_CB1(id,x,init) = update_CB1.id?c?y:limiteUpdate_CB1 -> x+y < 10 & x+y > -10 & TokenManager_CB1(id,x+y,1) [] clear_CB1.id?c -> endDiagram_CB1.id -> SKIP [] x == 0 & init == 1 & endDiagram_CB1.id -> SKIP\n" + 
				"TokenManager_CB1_t(id,x,init) = TokenManager_CB1(id,x,init)\n" + 
				"\n" + 
				"assert MAIN :[deadlock free]\n" + 
				"assert MAIN :[divergence free]\n" + 
				"assert MAIN :[deterministic]");
		
		assertEquals(expected.toString(), actual);
		projectAccessor2.close();

	}
	
	

	/*
	 * Teste de Tradução do call behavior
	 * */
	@Test
	public void TestNodesBehavior3() throws ParsingException, ClassNotFoundException, LicenseNotFoundException, ProjectNotFoundException, NonCompatibleException, IOException, ProjectLockedException {
		projectAccessor3 = AstahAPI.getAstahAPI().getProjectAccessor();
		projectAccessor3.open("src/test/resources/activityDiagram/behavior3.asta");
		INamedElement[] findElements = findElements(projectAccessor3);
		findElements = findElements(projectAccessor3);

		for (int i = 0; i < findElements.length; i++) {
			if (findElements[i].getName().equals("behavior3")) {
				ad = (IActivityDiagram) findElements[i];
			}
		}
		
		parser3 = new ADParser(ad.getActivity(), ad.getName(), ad);
		
		parser3.clearBuffer();
		String actual = parser3.parserDiagram();
		StringBuffer expected = new StringBuffer();
		expected.append("transparent normal\n" + 
				"ID_CB2 = {1..1}\n" + 
				"ID_CB1 = {1..1}\n" + 
				"ID_behavior3 = {1..1}\n" + 
				"datatype alphabet_behavior3 = init_behavior3_t_alphabet | CB2_behavior3_t_alphabet| CB1_behavior3_t_alphabet| fin1_behavior3_t_alphabet\n" + 
				"countCe_behavior3 = {1..3}\n" + 
				"countUpdate_behavior3 = {1..1}\n" + 
				"countClear_behavior3 = {1..1}\n" + 
				"limiteUpdate_behavior3 = {(1)..(1)}\n" + 
				"channel startActivity_behavior3: ID_behavior3\n" + 
				"channel endActivity_behavior3: ID_behavior3\n" + 
				"channel ce_behavior3: ID_behavior3.countCe_behavior3\n" + 
				"channel clear_behavior3: ID_behavior3.countClear_behavior3\n" + 
				"channel update_behavior3: ID_behavior3.countUpdate_behavior3.limiteUpdate_behavior3\n" + 
				"channel endDiagram_behavior3: ID_behavior3\n" + 
				"channel loop\n" + 
				"channel dc\n" + 
				"MAIN = normal(behavior3(1)); LOOP\n" + 
				"LOOP = loop -> LOOP\n" + 
				"END_DIAGRAM_behavior3(id) = endDiagram_behavior3.id -> SKIP\n" + 
				"behavior3(ID_behavior3) = (Internal_behavior3(ID_behavior3) [|{|update_behavior3,clear_behavior3,endDiagram_behavior3|}|] TokenManager_behavior3_t(ID_behavior3,0,0))\n" + 
				"Internal_behavior3(id) = StartActivity_behavior3(id); Node_behavior3(id); EndActivity_behavior3(id)\n" + 
				"StartActivity_behavior3(id) = startActivity_behavior3.id -> SKIP\n" + 
				"EndActivity_behavior3(id) = endActivity_behavior3.id -> SKIP\n" + 
				"AlphabetDiagram_behavior3(id,init_behavior3_t_alphabet) = {|update_behavior3.id.1,ce_behavior3.id.1,endDiagram_behavior3.id|}\n" + 
				"AlphabetDiagram_behavior3(id,CB2_behavior3_t_alphabet) = union({|ce_behavior3.id.2,startActivity_CB2.1,endActivity_CB2.1,ce_behavior3.id.3,endDiagram_behavior3.id|},AlphabetDiagram_CB2_t(1))\n" + 
				"AlphabetDiagram_behavior3(id,CB1_behavior3_t_alphabet) = union({|ce_behavior3.id.1,startActivity_CB1.1,endActivity_CB1.1,ce_behavior3.id.2,endDiagram_behavior3.id|},AlphabetDiagram_CB1_t(1))\n" + 
				"AlphabetDiagram_behavior3(id,fin1_behavior3_t_alphabet) = {|ce_behavior3.id.3,clear_behavior3.id.1,endDiagram_behavior3.id|}\n" + 
				"AlphabetDiagram_behavior3_t(id) = union(union(union(AlphabetDiagram_behavior3(id,init_behavior3_t_alphabet),AlphabetDiagram_behavior3(id,CB2_behavior3_t_alphabet)),AlphabetDiagram_behavior3(id,CB1_behavior3_t_alphabet)),AlphabetDiagram_behavior3(id,fin1_behavior3_t_alphabet))\n" + 
				"\n" + 
				"ProcessDiagram_behavior3(id,init_behavior3_t_alphabet) = normal(init_behavior3_t(id))\n" + 
				"ProcessDiagram_behavior3(id,CB2_behavior3_t_alphabet) = normal(CB2_behavior3_t(id))\n" + 
				"ProcessDiagram_behavior3(id,CB1_behavior3_t_alphabet) = normal(CB1_behavior3_t(id))\n" + 
				"ProcessDiagram_behavior3(id,fin1_behavior3_t_alphabet) = normal(fin1_behavior3_t(id))\n" + 
				"Node_behavior3(id) = || x:alphabet_behavior3 @ [AlphabetDiagram_behavior3(id,x)] ProcessDiagram_behavior3(id,x)\n" + 
				"init1_behavior3_t(id) = update_behavior3.id.1!(1-0) -> ((ce_behavior3.id.1 -> SKIP))\n" + 
				"CB1_behavior3(id) = ((ce_behavior3.id.1 -> SKIP)); normal(CB1(1));((ce_behavior3.id.2 -> SKIP)); CB1_behavior3(id)\n" + 
				"CB1_behavior3_t(id) = CB1_behavior3(id) /\\ END_DIAGRAM_behavior3(id)\n" + 
				"CB2_behavior3(id) = ((ce_behavior3.id.2 -> SKIP)); normal(CB2(1));((ce_behavior3.id.3 -> SKIP)); CB2_behavior3(id)\n" + 
				"CB2_behavior3_t(id) = CB2_behavior3(id) /\\ END_DIAGRAM_behavior3(id)\n" + 
				"fin1_behavior3(id) = ((ce_behavior3.id.3 -> SKIP)); clear_behavior3.id.1 -> SKIP\n" + 
				"fin1_behavior3_t(id) = fin1_behavior3(id) /\\ END_DIAGRAM_behavior3(id)\n" + 
				"init_behavior3_t(id) = (init1_behavior3_t(id)) /\\ END_DIAGRAM_behavior3(id)\n" + 
				"\n\n" + 
				"AlphabetMemCB1(id) = {|endDiagram_behavior3.id|}\n" + 
				"\n" + 
				"AlphabetMemCB2(id) = {|endDiagram_behavior3.id|}\n\n"+ 
				"TokenManager_behavior3(id,x,init) = update_behavior3.id?c?y:limiteUpdate_behavior3 -> x+y < 10 & x+y > -10 & TokenManager_behavior3(id,x+y,1) [] clear_behavior3.id?c -> endDiagram_behavior3.id -> SKIP [] x == 0 & init == 1 & endDiagram_behavior3.id -> SKIP\n" + 
				"TokenManager_behavior3_t(id,x,init) = TokenManager_behavior3(id,x,init)\n" + 
				"\n" + 
				"AlphabetPool = {|endDiagram_behavior3|}\n" + 
				"\n" + 
				"datatype alphabet_CB1 = init_CB1_t_alphabet | act1_CB1_t_alphabet| fin1_CB1_t_alphabet\n" + 
				"countCe_CB1 = {1..2}\n" + 
				"countUpdate_CB1 = {1..1}\n" + 
				"countClear_CB1 = {1..1}\n" + 
				"limiteUpdate_CB1 = {(1)..(1)}\n" + 
				"channel startActivity_CB1: ID_CB1\n" + 
				"channel endActivity_CB1: ID_CB1\n" + 
				"channel ce_CB1: ID_CB1.countCe_CB1\n" + 
				"channel clear_CB1: ID_CB1.countClear_CB1\n" + 
				"channel update_CB1: ID_CB1.countUpdate_CB1.limiteUpdate_CB1\n" + 
				"channel endDiagram_CB1: ID_CB1\n" + 
				"channel event_act1_CB1: ID_CB1\n" + 
				"END_DIAGRAM_CB1(id) = endDiagram_CB1.id -> SKIP\n" + 
				"CB1(ID_CB1) = (Internal_CB1(ID_CB1) [|{|update_CB1,clear_CB1,endDiagram_CB1|}|] TokenManager_CB1_t(ID_CB1,0,0))\n" + 
				"Internal_CB1(id) = StartActivity_CB1(id); Node_CB1(id); EndActivity_CB1(id)\n" + 
				"StartActivity_CB1(id) = startActivity_CB1.id -> SKIP\n" + 
				"EndActivity_CB1(id) = endActivity_CB1.id -> SKIP\n" + 
				"AlphabetDiagram_CB1(id,init_CB1_t_alphabet) = {|update_CB1.id.1,ce_CB1.id.1,endDiagram_CB1.id|}\n" + 
				"AlphabetDiagram_CB1(id,act1_CB1_t_alphabet) = {|ce_CB1.id.1,event_act1_CB1.id,ce_CB1.id.2,endDiagram_CB1.id|}\n" + 
				"AlphabetDiagram_CB1(id,fin1_CB1_t_alphabet) = {|ce_CB1.id.2,clear_CB1.id.1,endDiagram_CB1.id|}\n" + 
				"AlphabetDiagram_CB1_t(id) = union(union(AlphabetDiagram_CB1(id,init_CB1_t_alphabet),AlphabetDiagram_CB1(id,act1_CB1_t_alphabet)),AlphabetDiagram_CB1(id,fin1_CB1_t_alphabet))\n" + 
				"\n" + 
				"ProcessDiagram_CB1(id,init_CB1_t_alphabet) = normal(init_CB1_t(id))\n" + 
				"ProcessDiagram_CB1(id,act1_CB1_t_alphabet) = normal(act1_CB1_t(id))\n" + 
				"ProcessDiagram_CB1(id,fin1_CB1_t_alphabet) = normal(fin1_CB1_t(id))\n" + 
				"Node_CB1(id) = || x:alphabet_CB1 @ [AlphabetDiagram_CB1(id,x)] ProcessDiagram_CB1(id,x)\n" + 
				"init1_CB1_t(id) = update_CB1.id.1!(1-0) -> ((ce_CB1.id.1 -> SKIP))\n" + 
				"act1_CB1(id) = ((ce_CB1.id.1 -> SKIP)); event_act1_CB1.id -> ((ce_CB1.id.2 -> SKIP)); act1_CB1(id)\n" + 
				"act1_CB1_t(id) = act1_CB1(id) /\\ END_DIAGRAM_CB1(id)\n" + 
				"fin1_CB1(id) = ((ce_CB1.id.2 -> SKIP)); clear_CB1.id.1 -> SKIP\n" + 
				"fin1_CB1_t(id) = fin1_CB1(id) /\\ END_DIAGRAM_CB1(id)\n" + 
				"init_CB1_t(id) = (init1_CB1_t(id)) /\\ END_DIAGRAM_CB1(id)\n" + 
				"\n\n" + 
				"TokenManager_CB1(id,x,init) = update_CB1.id?c?y:limiteUpdate_CB1 -> x+y < 10 & x+y > -10 & TokenManager_CB1(id,x+y,1) [] clear_CB1.id?c -> endDiagram_CB1.id -> SKIP [] x == 0 & init == 1 & endDiagram_CB1.id -> SKIP\n" + 
				"TokenManager_CB1_t(id,x,init) = TokenManager_CB1(id,x,init)\n" + 
				"\n" + 
				"datatype alphabet_CB2 = init_CB2_t_alphabet | act1_CB2_t_alphabet| fin1_CB2_t_alphabet\n" + 
				"countCe_CB2 = {1..2}\n" + 
				"countUpdate_CB2 = {1..1}\n" + 
				"countClear_CB2 = {1..1}\n" + 
				"limiteUpdate_CB2 = {(1)..(1)}\n" + 
				"channel startActivity_CB2: ID_CB2\n" + 
				"channel endActivity_CB2: ID_CB2\n" + 
				"channel ce_CB2: ID_CB2.countCe_CB2\n" + 
				"channel clear_CB2: ID_CB2.countClear_CB2\n" + 
				"channel update_CB2: ID_CB2.countUpdate_CB2.limiteUpdate_CB2\n" + 
				"channel endDiagram_CB2: ID_CB2\n" + 
				"channel event_act1_CB2: ID_CB2\n" + 
				"END_DIAGRAM_CB2(id) = endDiagram_CB2.id -> SKIP\n" + 
				"CB2(ID_CB2) = (Internal_CB2(ID_CB2) [|{|update_CB2,clear_CB2,endDiagram_CB2|}|] TokenManager_CB2_t(ID_CB2,0,0))\n" + 
				"Internal_CB2(id) = StartActivity_CB2(id); Node_CB2(id); EndActivity_CB2(id)\n" + 
				"StartActivity_CB2(id) = startActivity_CB2.id -> SKIP\n" + 
				"EndActivity_CB2(id) = endActivity_CB2.id -> SKIP\n" + 
				"AlphabetDiagram_CB2(id,init_CB2_t_alphabet) = {|update_CB2.id.1,ce_CB2.id.1,endDiagram_CB2.id|}\n" + 
				"AlphabetDiagram_CB2(id,act1_CB2_t_alphabet) = {|ce_CB2.id.1,event_act1_CB2.id,ce_CB2.id.2,endDiagram_CB2.id|}\n" + 
				"AlphabetDiagram_CB2(id,fin1_CB2_t_alphabet) = {|ce_CB2.id.2,clear_CB2.id.1,endDiagram_CB2.id|}\n" + 
				"AlphabetDiagram_CB2_t(id) = union(union(AlphabetDiagram_CB2(id,init_CB2_t_alphabet),AlphabetDiagram_CB2(id,act1_CB2_t_alphabet)),AlphabetDiagram_CB2(id,fin1_CB2_t_alphabet))\n" + 
				"\n" + 
				"ProcessDiagram_CB2(id,init_CB2_t_alphabet) = normal(init_CB2_t(id))\n" + 
				"ProcessDiagram_CB2(id,act1_CB2_t_alphabet) = normal(act1_CB2_t(id))\n" + 
				"ProcessDiagram_CB2(id,fin1_CB2_t_alphabet) = normal(fin1_CB2_t(id))\n" + 
				"Node_CB2(id) = || x:alphabet_CB2 @ [AlphabetDiagram_CB2(id,x)] ProcessDiagram_CB2(id,x)\n" + 
				"init1_CB2_t(id) = update_CB2.id.1!(1-0) -> ((ce_CB2.id.1 -> SKIP))\n" + 
				"act1_CB2(id) = ((ce_CB2.id.1 -> SKIP)); event_act1_CB2.id -> ((ce_CB2.id.2 -> SKIP)); act1_CB2(id)\n" + 
				"act1_CB2_t(id) = act1_CB2(id) /\\ END_DIAGRAM_CB2(id)\n" + 
				"fin1_CB2(id) = ((ce_CB2.id.2 -> SKIP)); clear_CB2.id.1 -> SKIP\n" + 
				"fin1_CB2_t(id) = fin1_CB2(id) /\\ END_DIAGRAM_CB2(id)\n" + 
				"init_CB2_t(id) = (init1_CB2_t(id)) /\\ END_DIAGRAM_CB2(id)\n" + 
				"\n\n" + 
				"TokenManager_CB2(id,x,init) = update_CB2.id?c?y:limiteUpdate_CB2 -> x+y < 10 & x+y > -10 & TokenManager_CB2(id,x+y,1) [] clear_CB2.id?c -> endDiagram_CB2.id -> SKIP [] x == 0 & init == 1 & endDiagram_CB2.id -> SKIP\n" + 
				"TokenManager_CB2_t(id,x,init) = TokenManager_CB2(id,x,init)\n" + 
				"\n" + 
				"assert MAIN :[deadlock free]\n" + 
				"assert MAIN :[divergence free]\n" + 
				"assert MAIN :[deterministic]");
		
		assertEquals(expected.toString(), actual);
		projectAccessor3.close();

	}

	/*
	 * Teste de Tradução do call behavior
	 * */
	@Test
	public void TestNodesBehavior4() throws ParsingException, ClassNotFoundException, LicenseNotFoundException, ProjectNotFoundException, NonCompatibleException, IOException, ProjectLockedException {
		projectAccessor4 = AstahAPI.getAstahAPI().getProjectAccessor();
		projectAccessor4.open("src/test/resources/activityDiagram/behavior4.asta");
		INamedElement[] findElements = findElements(projectAccessor4);
		findElements = findElements(projectAccessor4);

		for (int i = 0; i < findElements.length; i++) {
			if (findElements[i].getName().equals("behavior4")) {
				ad = (IActivityDiagram) findElements[i];
			}
		}
		
		parser4 = new ADParser(ad.getActivity(), ad.getName(), ad);
		
		parser4.clearBuffer();
		String actual = parser4.parserDiagram();
		StringBuffer expected = new StringBuffer();
		expected.append("transparent normal\n" + 
				"ID_CB1 = {1..1}\n" + 
				"ID_behavior4 = {1..1}\n" + 
				"datatype alphabet_behavior4 = init_behavior4_t_alphabet | CB1_behavior4_t_alphabet| fin1_behavior4_t_alphabet\n" + 
				"int_behavior4 = {0..1}\n" + 
				"countGet_behavior4 = {1..3}\n" + 
				"countSet_behavior4 = {1..3}\n" + 
				"countOe_behavior4 = {1..2}\n" + 
				"countUpdate_behavior4 = {1..1}\n" + 
				"countClear_behavior4 = {1..1}\n" + 
				"limiteUpdate_behavior4 = {(1)..(1)}\n" + 
				"channel startActivity_behavior4: ID_behavior4.int_behavior4\n" + 
				"channel endActivity_behavior4: ID_behavior4\n" + 
				"channel get_x_behavior4: ID_behavior4.countGet_behavior4.int_behavior4\n" + 
				"channel set_x_behavior4: ID_behavior4.countSet_behavior4.int_behavior4\n" + 
				"channel get_z_CB1_behavior4: ID_behavior4.countGet_behavior4.int_behavior4\n" + 
				"channel set_z_CB1_behavior4: ID_behavior4.countSet_behavior4.int_behavior4\n" + 
				"channel get_y_CB1_behavior4: ID_behavior4.countGet_behavior4.int_behavior4\n" + 
				"channel set_y_CB1_behavior4: ID_behavior4.countSet_behavior4.int_behavior4\n" + 
				"channel oe_int_behavior4: ID_behavior4.countOe_behavior4.int_behavior4\n" + 
				"channel clear_behavior4: ID_behavior4.countClear_behavior4\n" + 
				"channel update_behavior4: ID_behavior4.countUpdate_behavior4.limiteUpdate_behavior4\n" + 
				"channel endDiagram_behavior4: ID_behavior4\n" + 
				"channel loop\n" + 
				"channel dc\n" + 
				"MAIN = normal(behavior4(1)); LOOP\n" + 
				"LOOP = loop -> LOOP\n" + 
				"END_DIAGRAM_behavior4(id) = endDiagram_behavior4.id -> SKIP\n" + 
				"behavior4(ID_behavior4) = ((Internal_behavior4(ID_behavior4) [|{|update_behavior4,clear_behavior4,endDiagram_behavior4|}|] TokenManager_behavior4_t(ID_behavior4,0,0)) [|{|get_x_behavior4,set_x_behavior4,endActivity_behavior4|}|] Mem_behavior4(ID_behavior4)) \\{|get_x_behavior4,set_x_behavior4|}\n" + 
				"Internal_behavior4(id) = StartActivity_behavior4(id); Node_behavior4(id); EndActivity_behavior4(id)\n" + 
				"StartActivity_behavior4(id) = startActivity_behavior4.id?x -> set_x_behavior4.id.3!x -> SKIP\n" + 
				"EndActivity_behavior4(id) = endActivity_behavior4.id -> SKIP\n" + 
				"AlphabetDiagram_behavior4(id,init_behavior4_t_alphabet) = {|update_behavior4.id.1,get_x_behavior4.id.1,oe_int_behavior4.id.1,endDiagram_behavior4.id|}\n" + 
				"AlphabetDiagram_behavior4(id,CB1_behavior4_t_alphabet) = union({|oe_int_behavior4.id.1,set_z_CB1_behavior4.id.1,get_z_CB1_behavior4.id.2,startActivity_CB1.1,endActivity_CB1.1,set_y_CB1_behavior4.id.2,get_y_CB1_behavior4.id.3,oe_int_behavior4.id.2,endDiagram_behavior4.id|},AlphabetDiagram_CB1_t(1))\n" + 
				"AlphabetDiagram_behavior4(id,fin1_behavior4_t_alphabet) = {|oe_int_behavior4.id.2,clear_behavior4.id.1,endDiagram_behavior4.id|}\n" + 
				"AlphabetDiagram_behavior4_t(id) = union(union(AlphabetDiagram_behavior4(id,init_behavior4_t_alphabet),AlphabetDiagram_behavior4(id,CB1_behavior4_t_alphabet)),AlphabetDiagram_behavior4(id,fin1_behavior4_t_alphabet))\n" + 
				"\n" + 
				"ProcessDiagram_behavior4(id,init_behavior4_t_alphabet) = normal(init_behavior4_t(id))\n" + 
				"ProcessDiagram_behavior4(id,CB1_behavior4_t_alphabet) = normal(CB1_behavior4_t(id))\n" + 
				"ProcessDiagram_behavior4(id,fin1_behavior4_t_alphabet) = normal(fin1_behavior4_t(id))\n" + 
				"Node_behavior4(id) = || x:alphabet_behavior4 @ [AlphabetDiagram_behavior4(id,x)] ProcessDiagram_behavior4(id,x)\n" + 
				"parameter_x_behavior4_t(id) = update_behavior4.id.1!(1-0) -> get_x_behavior4.id.1?x -> ((oe_int_behavior4.id.1!x -> SKIP))\n" + 
				"CB1_behavior4(id) = ((oe_int_behavior4.id.1?z -> set_z_CB1_behavior4.id.1!z -> SKIP)); get_z_CB1_behavior4.id.2?z -> (normal(CB1(1)) [|{|startActivity_CB1.1,endActivity_CB1.1|}|] (startActivity_CB1.1!z -> endActivity_CB1.1?y -> set_y_CB1_behavior4.id.2!y -> SKIP));((get_y_CB1_behavior4.id.3?y -> oe_int_behavior4.id.2!(y) -> SKIP)); CB1_behavior4(id)\n" + 
				"CB1_behavior4_t(id) = ((CB1_behavior4(id)) [|AlphabetMemCB1(id)|] Mem_CB1_behavior4(id)) \\diff(AlphabetMemCB1(id),{|endDiagram_behavior4.id|}) /\\ END_DIAGRAM_behavior4(id)\n" + 
				"fin1_behavior4(id) = ((oe_int_behavior4.id.2?y -> SKIP)); clear_behavior4.id.1 -> SKIP\n" + 
				"fin1_behavior4_t(id) = fin1_behavior4(id) /\\ END_DIAGRAM_behavior4(id)\n" + 
				"init_behavior4_t(id) = (parameter_x_behavior4_t(id)) /\\ END_DIAGRAM_behavior4(id)\n" + 
				"Mem_CB1_behavior4_z(id,z) = get_z_CB1_behavior4.id?c!z -> Mem_CB1_behavior4_z(id,z) [] set_z_CB1_behavior4.id?c?z -> Mem_CB1_behavior4_z(id,z)\n" + 
				"Mem_CB1_behavior4_z_t(id,z) = Mem_CB1_behavior4_z(id,z) /\\ END_DIAGRAM_behavior4(id)\n" + 
				"Mem_CB1_behavior4_y(id,y) = get_y_CB1_behavior4.id?c!y -> Mem_CB1_behavior4_y(id,y) [] set_y_CB1_behavior4.id?c?y -> Mem_CB1_behavior4_y(id,y)\n" + 
				"Mem_CB1_behavior4_y_t(id,y) = Mem_CB1_behavior4_y(id,y) /\\ END_DIAGRAM_behavior4(id)\n" + 
				"Mem_CB1_behavior4(id) = Mem_CB1_behavior4_z_t(id,0) ||| Mem_CB1_behavior4_y_t(id,0) \n" + 
				"Mem_behavior4_x(id,x) = get_x_behavior4.id?c!x -> Mem_behavior4_x(id,x) [] set_x_behavior4.id?c?x -> Mem_behavior4_x(id,x)\n" + 
				"Mem_behavior4_x_t(id,x) = Mem_behavior4_x(id,x) /\\ (endActivity_behavior4.id -> SKIP)\n" + 
				"Mem_behavior4(id) = Mem_behavior4_x_t(id,0)\n" + 
				"AlphabetMemCB1(id) = {|get_z_CB1_behavior4.id,set_z_CB1_behavior4.id,get_y_CB1_behavior4.id,set_y_CB1_behavior4.id,endDiagram_behavior4.id|}\n" + 
				"\n" + 
				"TokenManager_behavior4(id,x,init) = update_behavior4.id?c?y:limiteUpdate_behavior4 -> x+y < 10 & x+y > -10 & TokenManager_behavior4(id,x+y,1) [] clear_behavior4.id?c -> endDiagram_behavior4.id -> SKIP [] x == 0 & init == 1 & endDiagram_behavior4.id -> SKIP\n" + 
				"TokenManager_behavior4_t(id,x,init) = TokenManager_behavior4(id,x,init)\n" + 
				"\n" + 
				"AlphabetPool = {|endDiagram_behavior4|}\n" + 
				"\n" + 
				"datatype alphabet_CB1 = init_CB1_t_alphabet | act1_CB1_t_alphabet| parameter_y_CB1_t_alphabet\n" + 
				"int_CB1 = {0..1}\n" + 
				"countGet_CB1 = {1..4}\n" + 
				"countSet_CB1 = {1..4}\n" + 
				"countOe_CB1 = {1..2}\n" + 
				"countUpdate_CB1 = {1..2}\n" + 
				"countClear_CB1 = {1..0}\n" + 
				"limiteUpdate_CB1 = {(-1)..(1)}\n" + 
				"channel startActivity_CB1: ID_CB1.int_CB1\n" + 
				"channel endActivity_CB1: ID_CB1.int_CB1\n" + 
				"channel get_z_CB1: ID_CB1.countGet_CB1.int_CB1\n" + 
				"channel set_z_CB1: ID_CB1.countSet_CB1.int_CB1\n" + 
				"channel get_y_CB1: ID_CB1.countGet_CB1.int_CB1\n" + 
				"channel set_y_CB1: ID_CB1.countSet_CB1.int_CB1\n" + 
				"channel get_x_act1_CB1: ID_CB1.countGet_CB1.int_CB1\n" + 
				"channel set_x_act1_CB1: ID_CB1.countSet_CB1.int_CB1\n" + 
				"channel get_int_y_CB1: ID_CB1.countGet_CB1.int_CB1\n" + 
				"channel set_int_y_CB1: ID_CB1.countSet_CB1.int_CB1\n" + 
				"channel oe_int_CB1: ID_CB1.countOe_CB1.int_CB1\n" + 
				"channel clear_CB1: ID_CB1.countClear_CB1\n" + 
				"channel update_CB1: ID_CB1.countUpdate_CB1.limiteUpdate_CB1\n" + 
				"channel endDiagram_CB1: ID_CB1\n" + 
				"channel event_act1_CB1: ID_CB1\n" + 
				"END_DIAGRAM_CB1(id) = endDiagram_CB1.id -> SKIP\n" + 
				"CB1(ID_CB1) = ((Internal_CB1(ID_CB1) [|{|update_CB1,clear_CB1,endDiagram_CB1|}|] TokenManager_CB1_t(ID_CB1,0,0)) [|{|get_z_CB1,set_z_CB1,get_y_CB1,set_y_CB1,endActivity_CB1|}|] Mem_CB1(ID_CB1)) \\{|get_z_CB1,set_z_CB1,get_y_CB1,set_y_CB1|}\n" + 
				"Internal_CB1(id) = StartActivity_CB1(id); Node_CB1(id); EndActivity_CB1(id)\n" + 
				"StartActivity_CB1(id) = startActivity_CB1.id?z -> set_z_CB1.id.4!z -> SKIP\n" + 
				"EndActivity_CB1(id) = get_y_CB1.id.4?y -> endActivity_CB1.id!y -> SKIP\n" + 
				"AlphabetDiagram_CB1(id,init_CB1_t_alphabet) = {|update_CB1.id.1,get_z_CB1.id.1,oe_int_CB1.id.1,endDiagram_CB1.id|}\n" + 
				"AlphabetDiagram_CB1(id,act1_CB1_t_alphabet) = {|oe_int_CB1.id.1,set_x_act1_CB1.id.1,event_act1_CB1.id,get_x_act1_CB1.id.2,oe_int_CB1.id.2,endDiagram_CB1.id|}\n" + 
				"AlphabetDiagram_CB1(id,parameter_y_CB1_t_alphabet) = {|oe_int_CB1.id.2,set_int_y_CB1.id.2,get_int_y_CB1.id.3,set_y_CB1.id.3,update_CB1.id.2,endDiagram_CB1.id|}\n" + 
				"AlphabetDiagram_CB1_t(id) = union(union(AlphabetDiagram_CB1(id,init_CB1_t_alphabet),AlphabetDiagram_CB1(id,act1_CB1_t_alphabet)),AlphabetDiagram_CB1(id,parameter_y_CB1_t_alphabet))\n" + 
				"\n" + 
				"ProcessDiagram_CB1(id,init_CB1_t_alphabet) = normal(init_CB1_t(id))\n" + 
				"ProcessDiagram_CB1(id,act1_CB1_t_alphabet) = normal(act1_CB1_t(id))\n" + 
				"ProcessDiagram_CB1(id,parameter_y_CB1_t_alphabet) = normal(parameter_y_CB1_t(id))\n" + 
				"Node_CB1(id) = || x:alphabet_CB1 @ [AlphabetDiagram_CB1(id,x)] ProcessDiagram_CB1(id,x)\n" + 
				"parameter_z_CB1_t(id) = update_CB1.id.1!(1-0) -> get_z_CB1.id.1?z -> ((oe_int_CB1.id.1!z -> SKIP))\n" + 
				"act1_CB1(id) = ((oe_int_CB1.id.1?x -> set_x_act1_CB1.id.1!x -> SKIP)); event_act1_CB1.id -> get_x_act1_CB1.id.2?x -> ((((x) >= 0 and (x) <= 1) & oe_int_CB1.id.2!(x) -> SKIP)); act1_CB1(id)\n" + 
				"act1_CB1_t(id) = ((act1_CB1(id) /\\ END_DIAGRAM_CB1(id)) [|{|get_x_act1_CB1.id,set_x_act1_CB1.id,endDiagram_CB1.id|}|] Mem_act1_CB1_x_t(id,0)) \\{|get_x_act1_CB1.id,set_x_act1_CB1.id|}\n" + 
				"parameter_y_CB1(id) = ((oe_int_CB1.id.2?int -> set_int_y_CB1.id.2!int -> SKIP)); get_int_y_CB1.id.3?int -> set_y_CB1.id.3!int -> update_CB1.id.2!(0-1) -> parameter_y_CB1(id)\n" + 
				"parameter_y_CB1_t(id) = ((parameter_y_CB1(id) /\\ END_DIAGRAM_CB1(id)) [|{|get_int_y_CB1.id,set_int_y_CB1.id,endDiagram_CB1.id|}|] Mem_y_CB1_int_t(id,0)) \\{|get_int_y_CB1.id,set_int_y_CB1.id|}\n" + 
				"init_CB1_t(id) = (parameter_z_CB1_t(id)) /\\ END_DIAGRAM_CB1(id)\n" + 
				"Mem_act1_CB1_x(id,x) = get_x_act1_CB1.id?c!x -> Mem_act1_CB1_x(id,x) [] set_x_act1_CB1.id?c?x -> Mem_act1_CB1_x(id,x)\n" + 
				"Mem_act1_CB1_x_t(id,x) = Mem_act1_CB1_x(id,x) /\\ END_DIAGRAM_CB1(id)\n" + 
				"Mem_y_CB1_int(id,int) = get_int_y_CB1.id?c!int -> Mem_y_CB1_int(id,int) [] set_int_y_CB1.id?c?int -> Mem_y_CB1_int(id,int)\n" + 
				"Mem_y_CB1_int_t(id,int) = Mem_y_CB1_int(id,int) /\\ END_DIAGRAM_CB1(id)\n" + 
				"Mem_act1_CB1(id) = Mem_act1_CB1_x_t(id,0) ||| Mem_y_CB1_int_t(id,0) \n" + 
				"Mem_CB1_z(id,z) = get_z_CB1.id?c!z -> Mem_CB1_z(id,z) [] set_z_CB1.id?c?z -> Mem_CB1_z(id,z)\n" + 
				"Mem_CB1_z_t(id,z) = Mem_CB1_z(id,z) /\\ (endActivity_CB1.id?z -> SKIP)\n" + 
				"Mem_CB1_y(id,y) = get_y_CB1.id?c!y -> Mem_CB1_y(id,y) [] set_y_CB1.id?c?y -> Mem_CB1_y(id,y)\n" + 
				"Mem_CB1_y_t(id,y) = Mem_CB1_y(id,y) /\\ (endActivity_CB1.id?y -> SKIP)\n" + 
				"Mem_CB1(id) = (Mem_CB1_z_t(id,0) [|{|endActivity_CB1|}|] Mem_CB1_y_t(id,0))\n" + 
				"TokenManager_CB1(id,x,init) = update_CB1.id?c?y:limiteUpdate_CB1 -> x+y < 10 & x+y > -10 & TokenManager_CB1(id,x+y,1) [] clear_CB1.id?c -> endDiagram_CB1.id -> SKIP [] x == 0 & init == 1 & endDiagram_CB1.id -> SKIP\n" + 
				"TokenManager_CB1_t(id,x,init) = TokenManager_CB1(id,x,init)\n" + 
				"\n" + 
				"assert MAIN :[deadlock free]\n" + 
				"assert MAIN :[divergence free]\n" + 
				"assert MAIN :[deterministic]");

		assertEquals(expected.toString(), actual);
		projectAccessor4.close();

	}

	/*
	 * Teste de Tradução do call behavior
	 * */
	@Test
	public void TestNodesBehavior5() throws ParsingException, ClassNotFoundException, LicenseNotFoundException, ProjectNotFoundException, NonCompatibleException, IOException, ProjectLockedException {
		projectAccessor5 = AstahAPI.getAstahAPI().getProjectAccessor();
		projectAccessor5.open("src/test/resources/activityDiagram/behavior5.asta");
		INamedElement[] findElements = findElements(projectAccessor5);
		findElements = findElements(projectAccessor5);

		for (int i = 0; i < findElements.length; i++) {
			if (findElements[i].getName().equals("behavior5")) {
				ad = (IActivityDiagram) findElements[i];
			}
		}
		
		parser5 = new ADParser(ad.getActivity(), ad.getName(), ad);
		
		parser5.clearBuffer();
		String actual = parser5.parserDiagram();
		StringBuffer expected = new StringBuffer();
		expected.append("transparent normal\n" + 
				"ID_CB1 = {1..1}\n" + 
				"ID_behavior5 = {1..1}\n" + 
				"datatype alphabet_behavior5 = init_behavior5_t_alphabet | CB1_behavior5_t_alphabet| fin1_behavior5_t_alphabet\n" + 
				"int_behavior5 = {0..1}\n" + 
				"countGet_behavior5 = {1..4}\n" + 
				"countSet_behavior5 = {1..4}\n" + 
				"countOe_behavior5 = {1..3}\n" + 
				"countUpdate_behavior5 = {1..3}\n" + 
				"countClear_behavior5 = {1..1}\n" + 
				"limiteUpdate_behavior5 = {(-1)..(1)}\n" + 
				"channel startActivity_behavior5: ID_behavior5.int_behavior5.int_behavior5\n" + 
				"channel endActivity_behavior5: ID_behavior5\n" + 
				"channel get_m_behavior5: ID_behavior5.countGet_behavior5.int_behavior5\n" + 
				"channel set_m_behavior5: ID_behavior5.countSet_behavior5.int_behavior5\n" + 
				"channel get_n_behavior5: ID_behavior5.countGet_behavior5.int_behavior5\n" + 
				"channel set_n_behavior5: ID_behavior5.countSet_behavior5.int_behavior5\n" + 
				"channel get_x_CB1_behavior5: ID_behavior5.countGet_behavior5.int_behavior5\n" + 
				"channel set_x_CB1_behavior5: ID_behavior5.countSet_behavior5.int_behavior5\n" + 
				"channel get_b_CB1_behavior5: ID_behavior5.countGet_behavior5.int_behavior5\n" + 
				"channel set_b_CB1_behavior5: ID_behavior5.countSet_behavior5.int_behavior5\n" + 
				"channel oe_int_behavior5: ID_behavior5.countOe_behavior5.int_behavior5\n" + 
				"channel clear_behavior5: ID_behavior5.countClear_behavior5\n" + 
				"channel update_behavior5: ID_behavior5.countUpdate_behavior5.limiteUpdate_behavior5\n" + 
				"channel endDiagram_behavior5: ID_behavior5\n" + 
				"channel loop\n" + 
				"channel dc\n" + 
				"MAIN = normal(behavior5(1)); LOOP\n" + 
				"LOOP = loop -> LOOP\n" + 
				"END_DIAGRAM_behavior5(id) = endDiagram_behavior5.id -> SKIP\n" + 
				"behavior5(ID_behavior5) = ((Internal_behavior5(ID_behavior5) [|{|update_behavior5,clear_behavior5,endDiagram_behavior5|}|] TokenManager_behavior5_t(ID_behavior5,0,0)) [|{|get_m_behavior5,set_m_behavior5,get_n_behavior5,set_n_behavior5,endActivity_behavior5|}|] Mem_behavior5(ID_behavior5)) \\{|get_m_behavior5,set_m_behavior5,get_n_behavior5,set_n_behavior5|}\n" + 
				"Internal_behavior5(id) = StartActivity_behavior5(id); Node_behavior5(id); EndActivity_behavior5(id)\n" + 
				"StartActivity_behavior5(id) = startActivity_behavior5.id?m?n -> set_m_behavior5.id.3!m -> set_n_behavior5.id.4!n -> SKIP\n" + 
				"EndActivity_behavior5(id) = endActivity_behavior5.id -> SKIP\n" + 
				"AlphabetDiagram_behavior5(id,init_behavior5_t_alphabet) = {|update_behavior5.id.1,get_n_behavior5.id.1,oe_int_behavior5.id.1,update_behavior5.id.2,get_m_behavior5.id.2,oe_int_behavior5.id.3,endDiagram_behavior5.id|}\n" + 
				"AlphabetDiagram_behavior5(id,CB1_behavior5_t_alphabet) = union({|oe_int_behavior5.id.1,set_b_CB1_behavior5.id.1,oe_int_behavior5.id.3,set_x_CB1_behavior5.id.2,get_b_CB1_behavior5.id.3,get_x_CB1_behavior5.id.4,startActivity_CB1.1,endActivity_CB1.1,update_behavior5.id.3,oe_int_behavior5.id.2,endDiagram_behavior5|},AlphabetDiagram_CB1_t(1))\n" + 
				"AlphabetDiagram_behavior5(id,fin1_behavior5_t_alphabet) = {|oe_int_behavior5.id.2,clear_behavior5.id.1,endDiagram_behavior5.id|}\n" + 
				"AlphabetDiagram_behavior5_t(id) = union(union(AlphabetDiagram_behavior5(id,init_behavior5_t_alphabet),AlphabetDiagram_behavior5(id,CB1_behavior5_t_alphabet)),AlphabetDiagram_behavior5(id,fin1_behavior5_t_alphabet))\n" + 
				"\n" + 
				"ProcessDiagram_behavior5(id,init_behavior5_t_alphabet) = normal(init_behavior5_t(id))\n" + 
				"ProcessDiagram_behavior5(id,CB1_behavior5_t_alphabet) = normal(CB1_behavior5_t(id))\n" + 
				"ProcessDiagram_behavior5(id,fin1_behavior5_t_alphabet) = normal(fin1_behavior5_t(id))\n" + 
				"Node_behavior5(id) = || x:alphabet_behavior5 @ [AlphabetDiagram_behavior5(id,x)] ProcessDiagram_behavior5(id,x)\n" + 
				"parameter_n_behavior5_t(id) = update_behavior5.id.1!(1-0) -> get_n_behavior5.id.1?n -> ((oe_int_behavior5.id.1!n -> SKIP))\n" + 
				"fin1_behavior5(id) = ((oe_int_behavior5.id.2?y -> SKIP)); clear_behavior5.id.1 -> SKIP\n" + 
				"fin1_behavior5_t(id) = fin1_behavior5(id) /\\ END_DIAGRAM_behavior5(id)\n" + 
				"parameter_m_behavior5_t(id) = update_behavior5.id.2!(1-0) -> get_m_behavior5.id.2?m -> ((oe_int_behavior5.id.3!m -> SKIP))\n" + 
				"CB1_behavior5(id) = ((oe_int_behavior5.id.1?b -> set_b_CB1_behavior5.id.1!b -> SKIP) ||| (oe_int_behavior5.id.3?x -> set_x_CB1_behavior5.id.2!x -> SKIP)); get_b_CB1_behavior5.id.3?b -> get_x_CB1_behavior5.id.4?x -> startActivity_CB1.1!b!x -> endActivity_CB1.1?y -> update_behavior5.id.3!(1-2) -> ((oe_int_behavior5.id.2!(y) -> SKIP)); CB1_behavior5(id)\n" + 
				"CB1_behavior5_t(id) = (((CB1_behavior5(id) /\\ END_DIAGRAM_behavior5(id)) [|{|get_b_CB1_behavior5,set_b_CB1_behavior5,endDiagram_behavior5|}|] Mem_CB1_behavior5_b_t(id,0)) [|{|get_x_CB1_behavior5,set_x_CB1_behavior5,endDiagram_behavior5|}|] Mem_CB1_behavior5_x_t(id,0)) \\{|get_b_CB1_behavior5,set_b_CB1_behavior5,get_x_CB1_behavior5,set_x_CB1_behavior5|}\n" + 
				"init_behavior5_t(id) = (parameter_n_behavior5_t(id) ||| parameter_m_behavior5_t(id)) /\\ END_DIAGRAM_behavior5(id)\n" + 
				"Mem_CB1_behavior5_x(id,x) = get_x_CB1_behavior5.id?c!x -> Mem_CB1_behavior5_x(id,x) [] set_x_CB1_behavior5.id?c?x -> Mem_CB1_behavior5_x(id,x)\n" + 
				"Mem_CB1_behavior5_x_t(id,x) = Mem_CB1_behavior5_x(id,x) /\\ END_DIAGRAM_behavior5(id)\n" + 
				"Mem_CB1_behavior5_b(id,b) = get_b_CB1_behavior5.id?c!b -> Mem_CB1_behavior5_b(id,b) [] set_b_CB1_behavior5.id?c?b -> Mem_CB1_behavior5_b(id,b)\n" + 
				"Mem_CB1_behavior5_b_t(id,b) = Mem_CB1_behavior5_b(id,b) /\\ END_DIAGRAM_behavior5(id)\n" + 
				"Mem_CB1_behavior5(id) = Mem_CB1_behavior5_x_t(id,0) ||| Mem_CB1_behavior5_b_t(id,0) \n" + 
				"Mem_behavior5_m(id,m) = get_m_behavior5.id?c!m -> Mem_behavior5_m(id,m) [] set_m_behavior5.id?c?m -> Mem_behavior5_m(id,m)\n" + 
				"Mem_behavior5_m_t(id,m) = Mem_behavior5_m(id,m) /\\ (endActivity_behavior5.id -> SKIP)\n" + 
				"Mem_behavior5_n(id,n) = get_n_behavior5.id?c!n -> Mem_behavior5_n(id,n) [] set_n_behavior5.id?c?n -> Mem_behavior5_n(id,n)\n" + 
				"Mem_behavior5_n_t(id,n) = Mem_behavior5_n(id,n) /\\ (endActivity_behavior5.id -> SKIP)\n" + 
				"Mem_behavior5(id) = (Mem_behavior5_m_t(id,0) [|{|endActivity_behavior5|}|] Mem_behavior5_n_t(id,0))\n" + 
				"AlphabetMemCB1(id) = {|get_x_CB1_behavior5.id,set_x_CB1_behavior5.id,get_b_CB1_behavior5.id,set_b_CB1_behavior5.id,endDiagram_behavior5.id|}\n" + 
				"\n" + 
				"TokenManager_behavior5(id,x,init) = update_behavior5.id?c?y:limiteUpdate_behavior5 -> x+y < 10 & x+y > -10 & TokenManager_behavior5(id,x+y,1) [] clear_behavior5.id?c -> endDiagram_behavior5.id -> SKIP [] x == 0 & init == 1 & endDiagram_behavior5.id -> SKIP\n" + 
				"TokenManager_behavior5_t(id,x,init) = TokenManager_behavior5(id,x,init)\n" + 
				"\n" + 
				"AlphabetPool = {|endDiagram_behavior5|}\n" + 
				"\n" + 
				"datatype alphabet_CB1 = init_CB1_t_alphabet | act1_CB1_t_alphabet| parameter_y_CB1_t_alphabet\n" + 
				"int_CB1 = {0..1}\n" + 
				"countGet_CB1 = {1..6}\n" + 
				"countSet_CB1 = {1..7}\n" + 
				"countOe_CB1 = {1..4}\n" + 
				"countUpdate_CB1 = {1..3}\n" + 
				"countClear_CB1 = {1..0}\n" + 
				"limiteUpdate_CB1 = {(-1)..(1)}\n" + 
				"channel startActivity_CB1: ID_CB1.int_CB1.int_CB1\n" + 
				"channel endActivity_CB1: ID_CB1.int_CB1\n" + 
				"channel get_b_CB1: ID_CB1.countGet_CB1.int_CB1\n" + 
				"channel set_b_CB1: ID_CB1.countSet_CB1.int_CB1\n" + 
				"channel get_x_CB1: ID_CB1.countGet_CB1.int_CB1\n" + 
				"channel set_x_CB1: ID_CB1.countSet_CB1.int_CB1\n" + 
				"channel get_y_CB1: ID_CB1.countGet_CB1.int_CB1\n" + 
				"channel set_y_CB1: ID_CB1.countSet_CB1.int_CB1\n" + 
				"channel get_x_act1_CB1: ID_CB1.countGet_CB1.int_CB1\n" + 
				"channel set_x_act1_CB1: ID_CB1.countSet_CB1.int_CB1\n" + 
				"channel get_int_y_CB1: ID_CB1.countGet_CB1.int_CB1\n" + 
				"channel set_int_y_CB1: ID_CB1.countSet_CB1.int_CB1\n" + 
				"channel get_a_act1_CB1: ID_CB1.countGet_CB1.int_CB1\n" + 
				"channel set_a_act1_CB1: ID_CB1.countSet_CB1.int_CB1\n" + 
				"channel oe_int_CB1: ID_CB1.countOe_CB1.int_CB1\n" + 
				"channel clear_CB1: ID_CB1.countClear_CB1\n" + 
				"channel update_CB1: ID_CB1.countUpdate_CB1.limiteUpdate_CB1\n" + 
				"channel endDiagram_CB1: ID_CB1\n" + 
				"channel event_act1_CB1: ID_CB1\n" + 
				"END_DIAGRAM_CB1(id) = endDiagram_CB1.id -> SKIP\n" + 
				"CB1(ID_CB1) = ((Internal_CB1(ID_CB1) [|{|update_CB1,clear_CB1,endDiagram_CB1|}|] TokenManager_CB1_t(ID_CB1,0,0)) [|{|get_b_CB1,set_b_CB1,get_x_CB1,set_x_CB1,get_y_CB1,set_y_CB1,endActivity_CB1|}|] Mem_CB1(ID_CB1)) \\{|get_b_CB1,set_b_CB1,get_x_CB1,set_x_CB1,get_y_CB1,set_y_CB1|}\n" + 
				"Internal_CB1(id) = StartActivity_CB1(id); Node_CB1(id); EndActivity_CB1(id)\n" + 
				"StartActivity_CB1(id) = startActivity_CB1.id?b?x -> set_b_CB1.id.6!b -> set_x_CB1.id.7!x -> SKIP\n" + 
				"EndActivity_CB1(id) = get_y_CB1.id.6?y -> endActivity_CB1.id!y -> SKIP\n" + 
				"AlphabetDiagram_CB1(id,init_CB1_t_alphabet) = {|update_CB1.id.1,get_x_CB1.id.1,oe_int_CB1.id.1,update_CB1.id.3,get_b_CB1.id.3,oe_int_CB1.id.4,endDiagram_CB1.id|}\n" + 
				"AlphabetDiagram_CB1(id,act1_CB1_t_alphabet) = {|oe_int_CB1.id.1,set_x_act1_CB1.id.4,oe_int_CB1.id.4,set_a_act1_CB1.id.5,event_act1_CB1.id,get_x_act1_CB1.id.4,get_a_act1_CB1.id.5,oe_int_CB1.id.2,oe_int_CB1.id.3,endDiagram_CB1.id|}\n" + 
				"AlphabetDiagram_CB1(id,parameter_y_CB1_t_alphabet) = {|oe_int_CB1.id.3,set_int_y_CB1.id.1,oe_int_CB1.id.2,set_int_y_CB1.id.2,get_int_y_CB1.id.2,set_y_CB1.id.3,update_CB1.id.2,endDiagram_CB1.id|}\n" + 
				"AlphabetDiagram_CB1_t(id) = union(union(AlphabetDiagram_CB1(id,init_CB1_t_alphabet),AlphabetDiagram_CB1(id,act1_CB1_t_alphabet)),AlphabetDiagram_CB1(id,parameter_y_CB1_t_alphabet))\n" + 
				"\n" + 
				"ProcessDiagram_CB1(id,init_CB1_t_alphabet) = normal(init_CB1_t(id))\n" + 
				"ProcessDiagram_CB1(id,act1_CB1_t_alphabet) = normal(act1_CB1_t(id))\n" + 
				"ProcessDiagram_CB1(id,parameter_y_CB1_t_alphabet) = normal(parameter_y_CB1_t(id))\n" + 
				"Node_CB1(id) = || x:alphabet_CB1 @ [AlphabetDiagram_CB1(id,x)] ProcessDiagram_CB1(id,x)\n" + 
				"parameter_x_CB1_t(id) = update_CB1.id.1!(1-0) -> get_x_CB1.id.1?x -> ((oe_int_CB1.id.1!x -> SKIP))\n" + 
				"parameter_y_CB1(id) = ((oe_int_CB1.id.3?int -> set_int_y_CB1.id.1!int -> SKIP) [] (oe_int_CB1.id.2?int -> set_int_y_CB1.id.2!int -> SKIP)); get_int_y_CB1.id.2?int -> set_y_CB1.id.3!int -> update_CB1.id.2!(0-1) -> parameter_y_CB1(id)\n" + 
				"parameter_y_CB1_t(id) = ((parameter_y_CB1(id) /\\ END_DIAGRAM_CB1(id)) [|{|get_int_y_CB1.id,set_int_y_CB1.id,endDiagram_CB1.id|}|] Mem_y_CB1_int_t(id,0)) \\{|get_int_y_CB1.id,set_int_y_CB1.id|}\n" + 
				"parameter_b_CB1_t(id) = update_CB1.id.3!(1-0) -> get_b_CB1.id.3?b -> ((oe_int_CB1.id.4!b -> SKIP))\n" + 
				"act1_CB1(id) = ((oe_int_CB1.id.1?x -> set_x_act1_CB1.id.4!x -> SKIP) ||| (oe_int_CB1.id.4?a -> set_a_act1_CB1.id.5!a -> SKIP)); event_act1_CB1.id -> get_x_act1_CB1.id.4?x -> get_a_act1_CB1.id.5?a -> ((((a) >= 0 and (a) <= 1) & oe_int_CB1.id.2!(a) -> SKIP) ||| (((x) >= 0 and (x) <= 1) & oe_int_CB1.id.3!(x) -> SKIP)); act1_CB1(id)\n" + 
				"act1_CB1_t(id) = (((act1_CB1(id) /\\ END_DIAGRAM_CB1(id)) [|{|get_x_act1_CB1.id,set_x_act1_CB1.id,endDiagram_CB1.id|}|] Mem_act1_CB1_x_t(id,0)) [|{|get_a_act1_CB1.id,set_a_act1_CB1.id,endDiagram_CB1.id|}|] Mem_act1_CB1_a_t(id,0)) \\{|get_x_act1_CB1.id,set_x_act1_CB1.id,get_a_act1_CB1.id,set_a_act1_CB1.id|}\n" + 
				"init_CB1_t(id) = (parameter_x_CB1_t(id) ||| parameter_b_CB1_t(id)) /\\ END_DIAGRAM_CB1(id)\n" + 
				"Mem_act1_CB1_x(id,x) = get_x_act1_CB1.id?c!x -> Mem_act1_CB1_x(id,x) [] set_x_act1_CB1.id?c?x -> Mem_act1_CB1_x(id,x)\n" + 
				"Mem_act1_CB1_x_t(id,x) = Mem_act1_CB1_x(id,x) /\\ END_DIAGRAM_CB1(id)\n" + 
				"Mem_y_CB1_int(id,int) = get_int_y_CB1.id?c!int -> Mem_y_CB1_int(id,int) [] set_int_y_CB1.id?c?int -> Mem_y_CB1_int(id,int)\n" + 
				"Mem_y_CB1_int_t(id,int) = Mem_y_CB1_int(id,int) /\\ END_DIAGRAM_CB1(id)\n" + 
				"Mem_act1_CB1_a(id,a) = get_a_act1_CB1.id?c!a -> Mem_act1_CB1_a(id,a) [] set_a_act1_CB1.id?c?a -> Mem_act1_CB1_a(id,a)\n" + 
				"Mem_act1_CB1_a_t(id,a) = Mem_act1_CB1_a(id,a) /\\ END_DIAGRAM_CB1(id)\n" + 
				"Mem_act1_CB1(id) = Mem_act1_CB1_x_t(id,0) ||| Mem_y_CB1_int_t(id,0) ||| Mem_act1_CB1_a_t(id,0) \n" + 
				"Mem_CB1_b(id,b) = get_b_CB1.id?c!b -> Mem_CB1_b(id,b) [] set_b_CB1.id?c?b -> Mem_CB1_b(id,b)\n" + 
				"Mem_CB1_b_t(id,b) = Mem_CB1_b(id,b) /\\ (endActivity_CB1.id?b -> SKIP)\n" + 
				"Mem_CB1_x(id,x) = get_x_CB1.id?c!x -> Mem_CB1_x(id,x) [] set_x_CB1.id?c?x -> Mem_CB1_x(id,x)\n" + 
				"Mem_CB1_x_t(id,x) = Mem_CB1_x(id,x) /\\ (endActivity_CB1.id?x -> SKIP)\n" + 
				"Mem_CB1_y(id,y) = get_y_CB1.id?c!y -> Mem_CB1_y(id,y) [] set_y_CB1.id?c?y -> Mem_CB1_y(id,y)\n" + 
				"Mem_CB1_y_t(id,y) = Mem_CB1_y(id,y) /\\ (endActivity_CB1.id?y -> SKIP)\n" + 
				"Mem_CB1(id) = ((Mem_CB1_b_t(id,0) [|{|endActivity_CB1|}|] Mem_CB1_x_t(id,0)) [|{|endActivity_CB1|}|] Mem_CB1_y_t(id,0))\n" + 
				"TokenManager_CB1(id,x,init) = update_CB1.id?c?y:limiteUpdate_CB1 -> x+y < 10 & x+y > -10 & TokenManager_CB1(id,x+y,1) [] clear_CB1.id?c -> endDiagram_CB1.id -> SKIP [] x == 0 & init == 1 & endDiagram_CB1.id -> SKIP\n" + 
				"TokenManager_CB1_t(id,x,init) = TokenManager_CB1(id,x,init)\n" + 
				"\n" + 
				"assert MAIN :[deadlock free]\n" + 
				"assert MAIN :[divergence free]\n" + 
				"assert MAIN :[deterministic]");

		assertEquals(expected.toString(), actual);
		projectAccessor5.close();

	}
	
	/*
	 * Teste de Tradução do call behavior
	 * */
	@Test
	public void TestNodesBehavior6() throws ParsingException, ClassNotFoundException, LicenseNotFoundException, ProjectNotFoundException, NonCompatibleException, IOException, ProjectLockedException {
		projectAccessor6 = AstahAPI.getAstahAPI().getProjectAccessor();
		projectAccessor6.open("src/test/resources/activityDiagram/behavior6.asta");
		INamedElement[] findElements = findElements(projectAccessor6);
		findElements = findElements(projectAccessor6);

		for (int i = 0; i < findElements.length; i++) {
			if (findElements[i].getName().equals("behavior6")) {
				ad = (IActivityDiagram) findElements[i];
			}
		}
		
		parser6 = new ADParser(ad.getActivity(), ad.getName(), ad);
		
		parser6.clearBuffer();
		String actual = parser6.parserDiagram();
		StringBuffer expected = new StringBuffer();
		expected.append("transparent normal\n" + 
				"ID_behavior6 = {1..1}\n" + 
				"ID_CB1 = {1..1}\n" + 
				"datatype alphabet_behavior6 = init_behavior6_t_alphabet | CB1_behavior6_t_alphabet| DecisionNode_MergeNode0_behavior6_t_alphabet\n" + 
				"int_behavior6 = {0..1}\n" + 
				"countCe_behavior6 = {1..3}\n" + 
				"countUpdate_behavior6 = {1..1}\n" + 
				"countClear_behavior6 = {1..0}\n" + 
				"limiteUpdate_behavior6 = {(1)..(1)}\n" + 
				"channel startActivity_behavior6: ID_behavior6\n" + 
				"channel endActivity_behavior6: ID_behavior6\n" + 
				"channel ce_behavior6: ID_behavior6.countCe_behavior6\n" + 
				"channel clear_behavior6: ID_behavior6.countClear_behavior6\n" + 
				"channel update_behavior6: ID_behavior6.countUpdate_behavior6.limiteUpdate_behavior6\n" + 
				"channel endDiagram_behavior6: ID_behavior6\n" + 
				"channel loop\n" + 
				"channel dc\n" + 
				"MAIN = normal(behavior6(1)); LOOP\n" + 
				"LOOP = loop -> LOOP\n" + 
				"END_DIAGRAM_behavior6(id) = endDiagram_behavior6.id -> SKIP\n" + 
				"behavior6(ID_behavior6) = (Internal_behavior6(ID_behavior6) [|{|update_behavior6,clear_behavior6,endDiagram_behavior6|}|] TokenManager_behavior6_t(ID_behavior6,0,0))\n" + 
				"Internal_behavior6(id) = StartActivity_behavior6(id); Node_behavior6(id); EndActivity_behavior6(id)\n" + 
				"StartActivity_behavior6(id) = startActivity_behavior6.id -> SKIP\n" + 
				"EndActivity_behavior6(id) = endActivity_behavior6.id -> SKIP\n" + 
				"AlphabetDiagram_behavior6(id,init_behavior6_t_alphabet) = {|update_behavior6.id.1,ce_behavior6.id.1,endDiagram_behavior6.id|}\n" + 
				"AlphabetDiagram_behavior6(id,CB1_behavior6_t_alphabet) = union({|ce_behavior6.id.2,startActivity_CB1.1,endActivity_CB1.1,ce_behavior6.id.3,endDiagram_behavior6.id|},AlphabetDiagram_CB1_t(1))\n" + 
				"AlphabetDiagram_behavior6(id,DecisionNode_MergeNode0_behavior6_t_alphabet) = {|ce_behavior6.id.3,ce_behavior6.id.1,ce_behavior6.id.2,endDiagram_behavior6.id|}\n" + 
				"AlphabetDiagram_behavior6_t(id) = union(union(AlphabetDiagram_behavior6(id,init_behavior6_t_alphabet),AlphabetDiagram_behavior6(id,CB1_behavior6_t_alphabet)),AlphabetDiagram_behavior6(id,DecisionNode_MergeNode0_behavior6_t_alphabet))\n" + 
				"\n" + 
				"ProcessDiagram_behavior6(id,init_behavior6_t_alphabet) = normal(init_behavior6_t(id))\n" + 
				"ProcessDiagram_behavior6(id,CB1_behavior6_t_alphabet) = normal(CB1_behavior6_t(id))\n" + 
				"ProcessDiagram_behavior6(id,DecisionNode_MergeNode0_behavior6_t_alphabet) = normal(DecisionNode_MergeNode0_behavior6_t(id))\n" + 
				"Node_behavior6(id) = || x:alphabet_behavior6 @ [AlphabetDiagram_behavior6(id,x)] ProcessDiagram_behavior6(id,x)\n" + 
				"InitialNode1_behavior6_t(id) = update_behavior6.id.1!(1-0) -> ((ce_behavior6.id.1 -> SKIP))\n" + 
				"CB1_behavior6(id) = ((ce_behavior6.id.2 -> SKIP)); normal(CB1(1));((ce_behavior6.id.3 -> SKIP)); CB1_behavior6(id)\n" + 
				"CB1_behavior6_t(id) = CB1_behavior6(id) /\\ END_DIAGRAM_behavior6(id)\n" + 
				"DecisionNode_MergeNode0_behavior6(id) = ((ce_behavior6.id.3 -> SKIP) [] (ce_behavior6.id.1 -> SKIP)); ce_behavior6.id.2 -> DecisionNode_MergeNode0_behavior6(id)\n" + 
				"DecisionNode_MergeNode0_behavior6_t(id) = DecisionNode_MergeNode0_behavior6(id) /\\ END_DIAGRAM_behavior6(id)\n" + 
				"init_behavior6_t(id) = (InitialNode1_behavior6_t(id)) /\\ END_DIAGRAM_behavior6(id)\n" + 
				"\n" + 
				"\n" + 
				"AlphabetMemCB1(id) = {|endDiagram_behavior6.id|}\n" + 
				"\n" + 
				"TokenManager_behavior6(id,x,init) = update_behavior6.id?c?y:limiteUpdate_behavior6 -> x+y < 10 & x+y > -10 & TokenManager_behavior6(id,x+y,1) [] clear_behavior6.id?c -> endDiagram_behavior6.id -> SKIP [] x == 0 & init == 1 & endDiagram_behavior6.id -> SKIP\n" + 
				"TokenManager_behavior6_t(id,x,init) = TokenManager_behavior6(id,x,init)\n" + 
				"\n" + 
				"AlphabetPool = {|endDiagram_behavior6|}\n" + 
				"\n" + 
				"datatype alphabet_CB1 = init_CB1_t_alphabet | act1_CB1_t_alphabet| ActivityFinal0_CB1_t_alphabet\n" + 
				"int_CB1 = {0..1}\n" + 
				"countCe_CB1 = {1..2}\n" + 
				"countUpdate_CB1 = {1..1}\n" + 
				"countClear_CB1 = {1..1}\n" + 
				"limiteUpdate_CB1 = {(1)..(1)}\n" + 
				"channel startActivity_CB1: ID_CB1\n" + 
				"channel endActivity_CB1: ID_CB1\n" + 
				"channel ce_CB1: ID_CB1.countCe_CB1\n" + 
				"channel clear_CB1: ID_CB1.countClear_CB1\n" + 
				"channel update_CB1: ID_CB1.countUpdate_CB1.limiteUpdate_CB1\n" + 
				"channel endDiagram_CB1: ID_CB1\n" + 
				"channel event_act1_CB1: ID_CB1\n" + 
				"END_DIAGRAM_CB1(id) = endDiagram_CB1.id -> SKIP\n" + 
				"CB1(ID_CB1) = (Internal_CB1(ID_CB1) [|{|update_CB1,clear_CB1,endDiagram_CB1|}|] TokenManager_CB1_t(ID_CB1,0,0))\n" + 
				"Internal_CB1(id) = StartActivity_CB1(id); Node_CB1(id); EndActivity_CB1(id)\n" + 
				"StartActivity_CB1(id) = startActivity_CB1.id -> SKIP\n" + 
				"EndActivity_CB1(id) = endActivity_CB1.id -> SKIP\n" + 
				"AlphabetDiagram_CB1(id,init_CB1_t_alphabet) = {|update_CB1.id.1,ce_CB1.id.1,endDiagram_CB1.id|}\n" + 
				"AlphabetDiagram_CB1(id,act1_CB1_t_alphabet) = {|ce_CB1.id.1,event_act1_CB1.id,ce_CB1.id.2,endDiagram_CB1.id|}\n" + 
				"AlphabetDiagram_CB1(id,ActivityFinal0_CB1_t_alphabet) = {|ce_CB1.id.2,clear_CB1.id.1,endDiagram_CB1.id|}\n" + 
				"AlphabetDiagram_CB1_t(id) = union(union(AlphabetDiagram_CB1(id,init_CB1_t_alphabet),AlphabetDiagram_CB1(id,act1_CB1_t_alphabet)),AlphabetDiagram_CB1(id,ActivityFinal0_CB1_t_alphabet))\n" + 
				"\n" + 
				"ProcessDiagram_CB1(id,init_CB1_t_alphabet) = normal(init_CB1_t(id))\n" + 
				"ProcessDiagram_CB1(id,act1_CB1_t_alphabet) = normal(act1_CB1_t(id))\n" + 
				"ProcessDiagram_CB1(id,ActivityFinal0_CB1_t_alphabet) = normal(ActivityFinal0_CB1_t(id))\n" + 
				"Node_CB1(id) = || x:alphabet_CB1 @ [AlphabetDiagram_CB1(id,x)] ProcessDiagram_CB1(id,x)\n" + 
				"InitialNode0_CB1_t(id) = update_CB1.id.1!(1-0) -> ((ce_CB1.id.1 -> SKIP))\n" + 
				"act1_CB1(id) = ((ce_CB1.id.1 -> SKIP)); event_act1_CB1.id -> ((ce_CB1.id.2 -> SKIP)); act1_CB1(id)\n" + 
				"act1_CB1_t(id) = act1_CB1(id) /\\ END_DIAGRAM_CB1(id)\n" + 
				"ActivityFinal0_CB1(id) = ((ce_CB1.id.2 -> SKIP)); clear_CB1.id.1 -> SKIP\n" + 
				"ActivityFinal0_CB1_t(id) = ActivityFinal0_CB1(id) /\\ END_DIAGRAM_CB1(id)\n" + 
				"init_CB1_t(id) = (InitialNode0_CB1_t(id)) /\\ END_DIAGRAM_CB1(id)\n" + 
				"\n" + 
				"\n" + 
				"TokenManager_CB1(id,x,init) = update_CB1.id?c?y:limiteUpdate_CB1 -> x+y < 10 & x+y > -10 & TokenManager_CB1(id,x+y,1) [] clear_CB1.id?c -> endDiagram_CB1.id -> SKIP [] x == 0 & init == 1 & endDiagram_CB1.id -> SKIP\n" + 
				"TokenManager_CB1_t(id,x,init) = TokenManager_CB1(id,x,init)\n" + 
				"\n" + 
				"assert MAIN :[deadlock free]\n" + 
				"assert MAIN :[divergence free]\n" + 
				"assert MAIN :[deterministic]");

		assertEquals(expected.toString(), actual);
		projectAccessor6.close();

	}
	
	@Test
	public void TestNodesBehavior8() throws ParsingException, ClassNotFoundException, LicenseNotFoundException, ProjectNotFoundException, NonCompatibleException, IOException, ProjectLockedException {
		projectAccessor8 = AstahAPI.getAstahAPI().getProjectAccessor();
		projectAccessor8.open("src/test/resources/activityDiagram/behavior8.asta");
		INamedElement[] findElements = findElements(projectAccessor8);
		findElements = findElements(projectAccessor8);

		for (int i = 0; i < findElements.length; i++) {
			if (findElements[i].getName().equals("behavior8")) {
				ad = (IActivityDiagram) findElements[i];
			}
		}
		
		parser8 = new ADParser(ad.getActivity(), ad.getName(), ad);
		
		parser8.clearBuffer();
		String actual = parser8.parserDiagram();
		StringBuffer expected = new StringBuffer();
		expected.append("transparent normal\n" + 
				"ID_behavior8 = {1..1}\n" + 
				"ID_CB2 = {1..1}\n" + 
				"ID_CB1 = {1..2}\n" + 
				"datatype alphabet_behavior8 = init_behavior8_t_alphabet | CB2_behavior8_t_alphabet| CB1_behavior8_t_alphabet| ActivityFinal0_behavior8_t_alphabet| ForkNode0_behavior8_t_alphabet| JoinNode0_behavior8_t_alphabet\n" + 
				"countCe_behavior8 = {1..6}\n" + 
				"countUpdate_behavior8 = {1..3}\n" + 
				"countClear_behavior8 = {1..1}\n" + 
				"limiteUpdate_behavior8 = {(-1)..(1)}\n" + 
				"channel startActivity_behavior8: ID_behavior8\n" + 
				"channel endActivity_behavior8: ID_behavior8\n" + 
				"channel ce_behavior8: ID_behavior8.countCe_behavior8\n" + 
				"channel clear_behavior8: ID_behavior8.countClear_behavior8\n" + 
				"channel update_behavior8: ID_behavior8.countUpdate_behavior8.limiteUpdate_behavior8\n" + 
				"channel endDiagram_behavior8: ID_behavior8\n" + 
				"channel loop\n" + 
				"channel dc\n" + 
				"MAIN = normal(behavior8(1)); LOOP\n" + 
				"LOOP = loop -> LOOP\n" + 
				"END_DIAGRAM_behavior8(id) = endDiagram_behavior8.id -> SKIP\n" + 
				"behavior8(ID_behavior8) = (Internal_behavior8(ID_behavior8) [|{|update_behavior8,clear_behavior8,endDiagram_behavior8|}|] TokenManager_behavior8_t(ID_behavior8,0,0))\n" + 
				"Internal_behavior8(id) = StartActivity_behavior8(id); Node_behavior8(id); EndActivity_behavior8(id)\n" + 
				"StartActivity_behavior8(id) = startActivity_behavior8.id -> SKIP\n" + 
				"EndActivity_behavior8(id) = endActivity_behavior8.id -> SKIP\n" + 
				"AlphabetDiagram_behavior8(id,init_behavior8_t_alphabet) = {|update_behavior8.id.1,ce_behavior8.id.1,endDiagram_behavior8.id|}\n" + 
				"AlphabetDiagram_behavior8(id,CB2_behavior8_t_alphabet) = union({|ce_behavior8.id.3,startActivity_CB2.1,endActivity_CB2.1,ce_behavior8.id.6,endDiagram_behavior8.id|},AlphabetDiagram_CB2_t(1))\n" + 
				"AlphabetDiagram_behavior8(id,CB1_behavior8_t_alphabet) = union({|ce_behavior8.id.2,startActivity_CB1.1,endActivity_CB1.1,ce_behavior8.id.4,endDiagram_behavior8.id|},AlphabetDiagram_CB1_t(1))\n" + 
				"AlphabetDiagram_behavior8(id,ActivityFinal0_behavior8_t_alphabet) = {|ce_behavior8.id.5,clear_behavior8.id.1,endDiagram_behavior8.id|}\n" + 
				"AlphabetDiagram_behavior8(id,ForkNode0_behavior8_t_alphabet) = {|ce_behavior8.id.1,update_behavior8.id.2,ce_behavior8.id.2,ce_behavior8.id.3,endDiagram_behavior8.id|}\n" + 
				"AlphabetDiagram_behavior8(id,JoinNode0_behavior8_t_alphabet) = {|ce_behavior8.id.4,ce_behavior8.id.6,update_behavior8.id.3,ce_behavior8.id.5,endDiagram_behavior8.id|}\n" + 
				"AlphabetDiagram_behavior8_t(id) = union(union(union(union(union(AlphabetDiagram_behavior8(id,init_behavior8_t_alphabet),AlphabetDiagram_behavior8(id,CB2_behavior8_t_alphabet)),AlphabetDiagram_behavior8(id,CB1_behavior8_t_alphabet)),AlphabetDiagram_behavior8(id,ActivityFinal0_behavior8_t_alphabet)),AlphabetDiagram_behavior8(id,ForkNode0_behavior8_t_alphabet)),AlphabetDiagram_behavior8(id,JoinNode0_behavior8_t_alphabet))\n" + 
				"\n" + 
				"ProcessDiagram_behavior8(id,init_behavior8_t_alphabet) = normal(init_behavior8_t(id))\n" + 
				"ProcessDiagram_behavior8(id,CB2_behavior8_t_alphabet) = normal(CB2_behavior8_t(id))\n" + 
				"ProcessDiagram_behavior8(id,CB1_behavior8_t_alphabet) = normal(CB1_behavior8_t(id))\n" + 
				"ProcessDiagram_behavior8(id,ActivityFinal0_behavior8_t_alphabet) = normal(ActivityFinal0_behavior8_t(id))\n" + 
				"ProcessDiagram_behavior8(id,ForkNode0_behavior8_t_alphabet) = normal(ForkNode0_behavior8_t(id))\n" + 
				"ProcessDiagram_behavior8(id,JoinNode0_behavior8_t_alphabet) = normal(JoinNode0_behavior8_t(id))\n" + 
				"Node_behavior8(id) = || x:alphabet_behavior8 @ [AlphabetDiagram_behavior8(id,x)] ProcessDiagram_behavior8(id,x)\n" + 
				"InitialNode0_behavior8_t(id) = update_behavior8.id.1!(1-0) -> ((ce_behavior8.id.1 -> SKIP))\n" + 
				"ForkNode0_behavior8(id) = ce_behavior8.id.1 -> update_behavior8.id.2!(2-1) -> ((ce_behavior8.id.2 -> SKIP) ||| (ce_behavior8.id.3 -> SKIP)); ForkNode0_behavior8(id)\n" + 
				"ForkNode0_behavior8_t(id) = ForkNode0_behavior8(id) /\\ END_DIAGRAM_behavior8(id)\n" + 
				"CB1_behavior8(id) = ((ce_behavior8.id.2 -> SKIP)); normal(CB1(1));((ce_behavior8.id.4 -> SKIP)); CB1_behavior8(id)\n" + 
				"CB1_behavior8_t(id) = CB1_behavior8(id) /\\ END_DIAGRAM_behavior8(id)\n" + 
				"ActivityFinal0_behavior8(id) = ((ce_behavior8.id.5 -> SKIP)); clear_behavior8.id.1 -> SKIP\n" + 
				"ActivityFinal0_behavior8_t(id) = ActivityFinal0_behavior8(id) /\\ END_DIAGRAM_behavior8(id)\n" + 
				"CB2_behavior8(id) = ((ce_behavior8.id.3 -> SKIP)); normal(CB2(1));((ce_behavior8.id.6 -> SKIP)); CB2_behavior8(id)\n" + 
				"CB2_behavior8_t(id) = CB2_behavior8(id) /\\ END_DIAGRAM_behavior8(id)\n" + 
				"JoinNode0_behavior8(id) = ((ce_behavior8.id.4 -> SKIP) ||| (ce_behavior8.id.6 -> SKIP)); update_behavior8.id.3!(1-2) -> ((ce_behavior8.id.5 -> SKIP)); JoinNode0_behavior8(id)\n" + 
				"JoinNode0_behavior8_t(id) = (JoinNode0_behavior8(id) /\\ END_DIAGRAM_behavior8(id))\n" + 
				"init_behavior8_t(id) = (InitialNode0_behavior8_t(id)) /\\ END_DIAGRAM_behavior8(id)\n" + 
				"\n" + 
				"\n" + 
				"AlphabetMemCB2(id) = {|endDiagram_behavior8.id|}\n" + 
				"\n" + 
				"TokenManager_behavior8(id,x,init) = update_behavior8.id?c?y:limiteUpdate_behavior8 -> x+y < 10 & x+y > -10 & TokenManager_behavior8(id,x+y,1) [] clear_behavior8.id?c -> endDiagram_behavior8.id -> SKIP [] x == 0 & init == 1 & endDiagram_behavior8.id -> SKIP\n" + 
				"TokenManager_behavior8_t(id,x,init) = TokenManager_behavior8(id,x,init)\n" + 
				"\n" + 
				"AlphabetPool = {|endDiagram_behavior8|}\n" + 
				"\n" + 
				"datatype alphabet_CB1 = init_CB1_t_alphabet | Action0_CB1_t_alphabet| ActivityFinal1_CB1_t_alphabet\n" + 
				"countCe_CB1 = {1..2}\n" + 
				"countUpdate_CB1 = {1..1}\n" + 
				"countClear_CB1 = {1..1}\n" + 
				"limiteUpdate_CB1 = {(1)..(1)}\n" + 
				"channel startActivity_CB1: ID_CB1\n" + 
				"channel endActivity_CB1: ID_CB1\n" + 
				"channel ce_CB1: ID_CB1.countCe_CB1\n" + 
				"channel clear_CB1: ID_CB1.countClear_CB1\n" + 
				"channel update_CB1: ID_CB1.countUpdate_CB1.limiteUpdate_CB1\n" + 
				"channel endDiagram_CB1: ID_CB1\n" + 
				"channel event_Action0_CB1: ID_CB1\n" + 
				"END_DIAGRAM_CB1(id) = endDiagram_CB1.id -> SKIP\n" + 
				"CB1(ID_CB1) = (Internal_CB1(ID_CB1) [|{|update_CB1,clear_CB1,endDiagram_CB1|}|] TokenManager_CB1_t(ID_CB1,0,0))\n" + 
				"Internal_CB1(id) = StartActivity_CB1(id); Node_CB1(id); EndActivity_CB1(id)\n" + 
				"StartActivity_CB1(id) = startActivity_CB1.id -> SKIP\n" + 
				"EndActivity_CB1(id) = endActivity_CB1.id -> SKIP\n" + 
				"AlphabetDiagram_CB1(id,init_CB1_t_alphabet) = {|update_CB1.id.1,ce_CB1.id.1,endDiagram_CB1.id|}\n" + 
				"AlphabetDiagram_CB1(id,Action0_CB1_t_alphabet) = {|ce_CB1.id.1,event_Action0_CB1.id,ce_CB1.id.2,endDiagram_CB1.id|}\n" + 
				"AlphabetDiagram_CB1(id,ActivityFinal1_CB1_t_alphabet) = {|ce_CB1.id.2,clear_CB1.id.1,endDiagram_CB1.id|}\n" + 
				"AlphabetDiagram_CB1_t(id) = union(union(AlphabetDiagram_CB1(id,init_CB1_t_alphabet),AlphabetDiagram_CB1(id,Action0_CB1_t_alphabet)),AlphabetDiagram_CB1(id,ActivityFinal1_CB1_t_alphabet))\n" + 
				"\n" + 
				"ProcessDiagram_CB1(id,init_CB1_t_alphabet) = normal(init_CB1_t(id))\n" + 
				"ProcessDiagram_CB1(id,Action0_CB1_t_alphabet) = normal(Action0_CB1_t(id))\n" + 
				"ProcessDiagram_CB1(id,ActivityFinal1_CB1_t_alphabet) = normal(ActivityFinal1_CB1_t(id))\n" + 
				"Node_CB1(id) = || x:alphabet_CB1 @ [AlphabetDiagram_CB1(id,x)] ProcessDiagram_CB1(id,x)\n" + 
				"InitialNode1_CB1_t(id) = update_CB1.id.1!(1-0) -> ((ce_CB1.id.1 -> SKIP))\n" + 
				"Action0_CB1(id) = ((ce_CB1.id.1 -> SKIP)); event_Action0_CB1.id -> ((ce_CB1.id.2 -> SKIP)); Action0_CB1(id)\n" + 
				"Action0_CB1_t(id) = Action0_CB1(id) /\\ END_DIAGRAM_CB1(id)\n" + 
				"ActivityFinal1_CB1(id) = ((ce_CB1.id.2 -> SKIP)); clear_CB1.id.1 -> SKIP\n" + 
				"ActivityFinal1_CB1_t(id) = ActivityFinal1_CB1(id) /\\ END_DIAGRAM_CB1(id)\n" + 
				"init_CB1_t(id) = (InitialNode1_CB1_t(id)) /\\ END_DIAGRAM_CB1(id)\n" + 
				"\n" + 
				"\n" + 
				"TokenManager_CB1(id,x,init) = update_CB1.id?c?y:limiteUpdate_CB1 -> x+y < 10 & x+y > -10 & TokenManager_CB1(id,x+y,1) [] clear_CB1.id?c -> endDiagram_CB1.id -> SKIP [] x == 0 & init == 1 & endDiagram_CB1.id -> SKIP\n" + 
				"TokenManager_CB1_t(id,x,init) = TokenManager_CB1(id,x,init)\n" + 
				"\n" + 
				"datatype alphabet_CB2 = init_CB2_t_alphabet | CB1_CB2_t_alphabet| ActivityFinal2_CB2_t_alphabet\n" + 
				"countCe_CB2 = {1..2}\n" + 
				"countUpdate_CB2 = {1..1}\n" + 
				"countClear_CB2 = {1..1}\n" + 
				"limiteUpdate_CB2 = {(1)..(1)}\n" + 
				"channel startActivity_CB2: ID_CB2\n" + 
				"channel endActivity_CB2: ID_CB2\n" + 
				"channel ce_CB2: ID_CB2.countCe_CB2\n" + 
				"channel clear_CB2: ID_CB2.countClear_CB2\n" + 
				"channel update_CB2: ID_CB2.countUpdate_CB2.limiteUpdate_CB2\n" + 
				"channel endDiagram_CB2: ID_CB2\n" + 
				"END_DIAGRAM_CB2(id) = endDiagram_CB2.id -> SKIP\n" + 
				"CB2(ID_CB2) = (Internal_CB2(ID_CB2) [|{|update_CB2,clear_CB2,endDiagram_CB2|}|] TokenManager_CB2_t(ID_CB2,0,0))\n" + 
				"Internal_CB2(id) = StartActivity_CB2(id); Node_CB2(id); EndActivity_CB2(id)\n" + 
				"StartActivity_CB2(id) = startActivity_CB2.id -> SKIP\n" + 
				"EndActivity_CB2(id) = endActivity_CB2.id -> SKIP\n" + 
				"AlphabetDiagram_CB2(id,init_CB2_t_alphabet) = {|update_CB2.id.1,ce_CB2.id.1,endDiagram_CB2.id|}\n" + 
				"AlphabetDiagram_CB2(id,CB1_CB2_t_alphabet) = union({|ce_CB2.id.1,startActivity_CB1.2,endActivity_CB1.2,ce_CB2.id.2,endDiagram_CB2.id|},AlphabetDiagram_CB1_t(2))\n" + 
				"AlphabetDiagram_CB2(id,ActivityFinal2_CB2_t_alphabet) = {|ce_CB2.id.2,clear_CB2.id.1,endDiagram_CB2.id|}\n" + 
				"AlphabetDiagram_CB2_t(id) = union(union(AlphabetDiagram_CB2(id,init_CB2_t_alphabet),AlphabetDiagram_CB2(id,CB1_CB2_t_alphabet)),AlphabetDiagram_CB2(id,ActivityFinal2_CB2_t_alphabet))\n" + 
				"\n" + 
				"ProcessDiagram_CB2(id,init_CB2_t_alphabet) = normal(init_CB2_t(id))\n" + 
				"ProcessDiagram_CB2(id,CB1_CB2_t_alphabet) = normal(CB1_CB2_t(id))\n" + 
				"ProcessDiagram_CB2(id,ActivityFinal2_CB2_t_alphabet) = normal(ActivityFinal2_CB2_t(id))\n" + 
				"Node_CB2(id) = || x:alphabet_CB2 @ [AlphabetDiagram_CB2(id,x)] ProcessDiagram_CB2(id,x)\n" + 
				"InitialNode2_CB2_t(id) = update_CB2.id.1!(1-0) -> ((ce_CB2.id.1 -> SKIP))\n" + 
				"CB1_CB2(id) = ((ce_CB2.id.1 -> SKIP)); normal(CB1(2));((ce_CB2.id.2 -> SKIP)); CB1_CB2(id)\n" + 
				"CB1_CB2_t(id) = CB1_CB2(id) /\\ END_DIAGRAM_CB2(id)\n" + 
				"ActivityFinal2_CB2(id) = ((ce_CB2.id.2 -> SKIP)); clear_CB2.id.1 -> SKIP\n" + 
				"ActivityFinal2_CB2_t(id) = ActivityFinal2_CB2(id) /\\ END_DIAGRAM_CB2(id)\n" + 
				"init_CB2_t(id) = (InitialNode2_CB2_t(id)) /\\ END_DIAGRAM_CB2(id)\n" + 
				"\n" + 
				"\n" + 
				"AlphabetMemCB1(id) = {|endDiagram_CB2.id|}\n" + 
				"\n" + 
				"TokenManager_CB2(id,x,init) = update_CB2.id?c?y:limiteUpdate_CB2 -> x+y < 10 & x+y > -10 & TokenManager_CB2(id,x+y,1) [] clear_CB2.id?c -> endDiagram_CB2.id -> SKIP [] x == 0 & init == 1 & endDiagram_CB2.id -> SKIP\n" + 
				"TokenManager_CB2_t(id,x,init) = TokenManager_CB2(id,x,init)\n" + 
				"\n" + 
				"assert MAIN :[deadlock free]\n" + 
				"assert MAIN :[divergence free]\n" + 
				"assert MAIN :[deterministic]");

		assertEquals(expected.toString(), actual);
		projectAccessor8.close();

	}
}
