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

public class ADParserTestBehaviorCall {
	
	public static IActivityDiagram ad;
	private static ADParser parser1;
	private static ADParser parser2;
	private static ADParser parser3;
	
	@BeforeClass
	public static void GetDiagram() throws Exception {
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/behavior1.asta");
			INamedElement[] findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser1 = new ADParser(ad.getActivity(), ad.getName());
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/behavior2.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser2 = new ADParser(ad.getActivity(), ad.getName());
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/behavior3.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser3 = new ADParser(ad.getActivity(), ad.getName());
			
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
	 * Teste de Tradução do call behavior
	 * */
	@Test
	public void TestNodesBehavior1() {
		String actual = "";
		StringBuffer expected = new StringBuffer();
		expected.append("ID_behavior1 = {1..1}\n" + 
				"datatype T = lock | unlock\n" + 
				"countCe_behavior1 = {1..2}\n" + 
				"countUpdate_behavior1 = {1..2}\n" + 
				"countClear_behavior1 = {1..1}\n" + 
				"limiteUpdate_behavior1 = {(0)..(1)}\n" + 
				"channel startActivity_behavior1: ID_behavior1\n" + 
				"channel endActivity_behavior1: ID_behavior1\n" + 
				"channel ce_behavior1: countCe_behavior1\n" + 
				"channel clear_behavior1: countClear_behavior1\n" + 
				"channel update_behavior1: countUpdate_behavior1.limiteUpdate_behavior1\n" + 
				"channel endDiagram_behavior1\n" + 
				"channel lock_CB1_behavior1: T\n" + 
				"channel loop\n" + 
				"MAIN = behavior1(1); LOOP\n" + 
				"LOOP = loop -> LOOP\n" + 
				"END_DIAGRAM_behavior1 = endDiagram_behavior1 -> SKIP\n" + 
				"behavior1(ID_behavior1) = ((((Internal_behavior1(ID_behavior1) [|{|startActivity_CB1,endActivity_CB1|}|] CB1(1)) [|{|update_behavior1,clear_behavior1,endDiagram_behavior1|}|] TokenManager_behavior1_t(0,0))) [|{|lock_CB1_behavior1,endDiagram_behavior1|}|] Lock_behavior1)\n" + 
				"Internal_behavior1(ID_behavior1) = StartActivity_behavior1(ID_behavior1); Node_behavior1; EndActivity_behavior1(ID_behavior1)\n" + 
				"StartActivity_behavior1(ID_behavior1) = startActivity_behavior1.ID_behavior1 -> SKIP\n" + 
				"EndActivity_behavior1(ID_behavior1) = endActivity_behavior1.ID_behavior1 -> SKIP\n" + 
				"Node_behavior1 = ((CB1_behavior1_t [{|ce_behavior1.1,lock_CB1_behavior1,startActivity_CB1.1,endActivity_CB1.1,update_behavior1.2,ce_behavior1.2,endDiagram_behavior1|}||{|ce_behavior1.2,clear_behavior1.1,endDiagram_behavior1|}] fin1_behavior1_t) [{|ce_behavior1.1,lock_CB1_behavior1,startActivity_CB1.1,endActivity_CB1.1,update_behavior1.2,ce_behavior1.2,endDiagram_behavior1,clear_behavior1.1|}||{|update_behavior1.1,ce_behavior1.1,endDiagram_behavior1|}] init_behavior1_t)\n" + 
				"init1_behavior1_t = update_behavior1.1!(1-0) -> ce_behavior1.1 -> SKIP\n" + 
				"CB1_behavior1 = ce_behavior1.1 -> lock_CB1_behavior1.lock -> startActivity_CB1.1 -> endActivity_CB1.1 -> lock_CB1_behavior1.unlock -> update_behavior1.2!(1-1) -> ((ce_behavior1.2 -> SKIP)); CB1_behavior1\n" + 
				"CB1_behavior1_t = CB1_behavior1 /\\ END_DIAGRAM_behavior1\n" + 
				"fin1_behavior1 = ((ce_behavior1.2 -> SKIP)); clear_behavior1.1 -> SKIP\n" + 
				"fin1_behavior1_t = fin1_behavior1 /\\ END_DIAGRAM_behavior1\n" + 
				"init_behavior1_t = (init1_behavior1_t) /\\ END_DIAGRAM_behavior1\n" + 
				"TokenManager_behavior1(x,init) = update_behavior1?c?y:limiteUpdate_behavior1 -> x+y < 10 & x+y > -10 & TokenManager_behavior1(x+y,1) [] clear_behavior1?c -> endDiagram_behavior1 -> SKIP [] x == 0 & init == 1 & endDiagram_behavior1 -> SKIP\n" + 
				"TokenManager_behavior1_t(x,init) = TokenManager_behavior1(x,init)\n" + 
				"Lock_CB1_behavior1 = lock_CB1_behavior1.lock -> lock_CB1_behavior1.unlock -> Lock_CB1_behavior1 [] endDiagram_behavior1 -> SKIP\n" + 
				"Lock_behavior1 = Lock_CB1_behavior1\n" + 
				"\n" + 
				"ID_CB1 = {1..1}\n" + 
				"countCe_CB1 = {1..2}\n" + 
				"countUpdate_CB1 = {1..2}\n" + 
				"countClear_CB1 = {1..1}\n" + 
				"limiteUpdate_CB1 = {(0)..(1)}\n" + 
				"channel startActivity_CB1: ID_CB1\n" + 
				"channel endActivity_CB1: ID_CB1\n" + 
				"channel ce_CB1: countCe_CB1\n" + 
				"channel clear_CB1: countClear_CB1\n" + 
				"channel update_CB1: countUpdate_CB1.limiteUpdate_CB1\n" + 
				"channel endDiagram_CB1\n" + 
				"channel event_act1_CB1\n" + 
				"channel lock_act1_CB1: T\n" + 
				"END_DIAGRAM_CB1 = endDiagram_CB1 -> SKIP\n" + 
				"CB1(ID_CB1) = (((Internal_CB1(ID_CB1) [|{|update_CB1,clear_CB1,endDiagram_CB1|}|] TokenManager_CB1_t(0,0))) [|{|lock_act1_CB1,endDiagram_CB1|}|] Lock_CB1)\n" + 
				"Internal_CB1(ID_CB1) = StartActivity_CB1(ID_CB1); Node_CB1; EndActivity_CB1(ID_CB1)\n" + 
				"StartActivity_CB1(ID_CB1) = startActivity_CB1.ID_CB1 -> SKIP\n" + 
				"EndActivity_CB1(ID_CB1) = endActivity_CB1.ID_CB1 -> SKIP\n" + 
				"Node_CB1 = ((act1_CB1_t [{|ce_CB1.1,lock_act1_CB1,event_act1_CB1,update_CB1.2,ce_CB1.2,endDiagram_CB1|}||{|ce_CB1.2,clear_CB1.1,endDiagram_CB1|}] fin1_CB1_t) [{|ce_CB1.1,lock_act1_CB1,event_act1_CB1,update_CB1.2,ce_CB1.2,endDiagram_CB1,clear_CB1.1|}||{|update_CB1.1,ce_CB1.1,endDiagram_CB1|}] init_CB1_t)\n" + 
				"init1_CB1_t = update_CB1.1!(1-0) -> ce_CB1.1 -> SKIP\n" + 
				"act1_CB1 = ce_CB1.1 -> lock_act1_CB1.lock -> event_act1_CB1 -> lock_act1_CB1.unlock -> update_CB1.2!(1-1) -> ((ce_CB1.2 -> SKIP)); act1_CB1\n" + 
				"act1_CB1_t = act1_CB1 /\\ END_DIAGRAM_CB1\n" + 
				"fin1_CB1 = ((ce_CB1.2 -> SKIP)); clear_CB1.1 -> SKIP\n" + 
				"fin1_CB1_t = fin1_CB1 /\\ END_DIAGRAM_CB1\n" + 
				"init_CB1_t = (init1_CB1_t) /\\ END_DIAGRAM_CB1\n" + 
				"TokenManager_CB1(x,init) = update_CB1?c?y:limiteUpdate_CB1 -> x+y < 10 & x+y > -10 & TokenManager_CB1(x+y,1) [] clear_CB1?c -> endDiagram_CB1 -> SKIP [] x == 0 & init == 1 & endDiagram_CB1 -> SKIP\n" + 
				"TokenManager_CB1_t(x,init) = TokenManager_CB1(x,init)\n" + 
				"Lock_act1_CB1 = lock_act1_CB1.lock -> lock_act1_CB1.unlock -> Lock_act1_CB1 [] endDiagram_CB1 -> SKIP\n" + 
				"Lock_CB1 = Lock_act1_CB1");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução do call behavior
	 * */
	@Test
	public void TestNodesBehavior2() {
		String actual = "";
		StringBuffer expected = new StringBuffer();
		expected.append("ID_behavior1 = {1..1}\n" + 
				"datatype T = lock | unlock\n" + 
				"countCe_behavior1 = {1..3}\n" + 
				"countUpdate_behavior1 = {1..3}\n" + 
				"countClear_behavior1 = {1..1}\n" + 
				"limiteUpdate_behavior1 = {(0)..(1)}\n" + 
				"channel startActivity_behavior1: ID_behavior1\n" + 
				"channel endActivity_behavior1: ID_behavior1\n" + 
				"channel ce_behavior1: countCe_behavior1\n" + 
				"channel clear_behavior1: countClear_behavior1\n" + 
				"channel update_behavior1: countUpdate_behavior1.limiteUpdate_behavior1\n" + 
				"channel endDiagram_behavior1\n" + 
				"channel lock_CB1_behavior1,lock_CB2_behavior1: T\n" + 
				"channel loop\n" + 
				"MAIN = behavior1(1); LOOP\n" + 
				"LOOP = loop -> LOOP\n" + 
				"END_DIAGRAM_behavior1 = endDiagram_behavior1 -> SKIP\n" + 
				"behavior1(ID_behavior1) = (((((Internal_behavior1(ID_behavior1) [|{|startActivity_CB1.1,endActivity_CB1.1|}|] CB1(1)) [|{|startActivity_CB1.2,endActivity_CB1.2|}|] CB1(2)) [|{|update_behavior1,clear_behavior1,endDiagram_behavior1|}|] TokenManager_behavior1_t(0,0))) [|{|lock_CB1_behavior1,endDiagram_behavior1|}|] Lock_behavior1)\n" + 
				"Internal_behavior1(ID_behavior1) = StartActivity_behavior1(ID_behavior1); Node_behavior1; EndActivity_behavior1(ID_behavior1)\n" + 
				"StartActivity_behavior1(ID_behavior1) = startActivity_behavior1.ID_behavior1 -> SKIP\n" + 
				"EndActivity_behavior1(ID_behavior1) = endActivity_behavior1.ID_behavior1 -> SKIP\n" + 
				"Node_behavior1 = (((CB1_behavior1_t [{|ce_behavior1.1,lock_CB1_behavior1,startActivity_CB1.1,endActivity_CB1.1,update_behavior1.2,ce_behavior1.2,endDiagram_behavior1|}||{|ce_behavior1.3,clear_behavior1.1,endDiagram_behavior1|}] fin1_behavior1_t) [{|ce_behavior1.1,ce_behavior1.2,lock_CB1_behavior1,startActivity_CB1.1,endActivity_CB1.1,update_behavior1.2,ce_behavior1.3,endDiagram_behavior1,clear_behavior1.1|}||{|update_behavior1.1,ce_behavior1.1,endDiagram_behavior1|}] init_behavior1_t) [{|ce_behavior1.1,ce_behavior1.2,lock_CB1_behavior1,startActivity_CB1.1,endActivity_CB1.1,update_behavior1.2,ce_behavior1.3,endDiagram_behavior1,clear_behavior1.1,update_behavior1.1|}||{|ce_behavior1.2,lock_CB2_behavior1,startActivity_CB1.2,endActivity_CB1.2,update_behavior1.3,ce_behavior1.3,endDiagram_behavior1|}] CB2_behavior1_t)\n" + 
				"init1_behavior1_t = update_behavior1.1!(1-0) -> ce_behavior1.1 -> SKIP\n" + 
				"CB1_behavior1 = ce_behavior1.1 -> lock_CB1_behavior1.lock -> startActivity_CB1.1 -> endActivity_CB1.1 -> lock_CB1_behavior1.unlock -> update_behavior1.2!(1-1) -> ((ce_behavior1.2 -> SKIP)); CB1_behavior1\n" + 
				"CB1_behavior1_t = CB1_behavior1 /\\ END_DIAGRAM_behavior1\n" + 
				"CB2_behavior1 = ce_behavior1.2 -> lock_CB2_behavior1.lock -> startActivity_CB1.2 -> endActivity_CB1.2 -> lock_CB2_behavior1.unlock -> update_behavior1.3!(1-1) -> ((ce_behavior1.3 -> SKIP)); CB2_behavior1\n" + 
				"CB2_behavior1_t = CB2_behavior1 /\\ END_DIAGRAM_behavior1\n" + 
				"fin1_behavior1 = ((ce_behavior1.3 -> SKIP)); clear_behavior1.1 -> SKIP\n" + 
				"fin1_behavior1_t = fin1_behavior1 /\\ END_DIAGRAM_behavior1\n" + 
				"init_behavior1_t = (init1_behavior1_t) /\\ END_DIAGRAM_behavior1\n" + 
				"TokenManager_behavior1(x,init) = update_behavior1?c?y:limiteUpdate_behavior1 -> x+y < 10 & x+y > -10 & TokenManager_behavior1(x+y,1) [] clear_behavior1?c -> endDiagram_behavior1 -> SKIP [] x == 0 & init == 1 & endDiagram_behavior1 -> SKIP\n" + 
				"TokenManager_behavior1_t(x,init) = TokenManager_behavior1(x,init)\n" + 
				"Lock_CB1_behavior1 = lock_CB1_behavior1.lock -> lock_CB1_behavior1.unlock -> Lock_CB1_behavior1 [] endDiagram_behavior1 -> SKIP\n" + 
				"Lock_CB2_behavior1 = lock_CB2_behavior1.lock -> lock_CB2_behavior1.unlock -> Lock_CB2_behavior1 [] endDiagram_behavior1 -> SKIP\n" + 
				"Lock_behavior1 = (Lock_CB1_behavior1 [|{|endDiagram_behavior1|}|] Lock_CB2_behavior1)\n" + 
				"\n" + 
				"ID_CB1 = {1..2}\n" + 
				"countCe_CB1 = {1..2}\n" + 
				"countUpdate_CB1 = {1..2}\n" + 
				"countClear_CB1 = {1..1}\n" + 
				"limiteUpdate_CB1 = {(0)..(1)}\n" + 
				"channel startActivity_CB1: ID_CB1\n" + 
				"channel endActivity_CB1: ID_CB1\n" + 
				"channel ce_CB1: countCe_CB1\n" + 
				"channel clear_CB1: countClear_CB1\n" + 
				"channel update_CB1: countUpdate_CB1.limiteUpdate_CB1\n" + 
				"channel endDiagram_CB1\n" + 
				"channel event_act1_CB1\n" + 
				"channel lock_act1_CB1: T\n" + 
				"END_DIAGRAM_CB1 = endDiagram_CB1 -> SKIP\n" + 
				"CB1(ID_CB1) = (((Internal_CB1(ID_CB1) [|{|update_CB1,clear_CB1,endDiagram_CB1|}|] TokenManager_CB1_t(0,0))) [|{|lock_act1_CB1,endDiagram_CB1|}|] Lock_CB1)\n" + 
				"Internal_CB1(ID_CB1) = StartActivity_CB1(ID_CB1); Node_CB1; EndActivity_CB1(ID_CB1)\n" + 
				"StartActivity_CB1(ID_CB1) = startActivity_CB1.ID_CB1 -> SKIP\n" + 
				"EndActivity_CB1(ID_CB1) = endActivity_CB1.ID_CB1 -> SKIP\n" + 
				"Node_CB1 = ((act1_CB1_t [{|ce_CB1.1,lock_act1_CB1,event_act1_CB1,update_CB1.2,ce_CB1.2,endDiagram_CB1|}||{|ce_CB1.2,clear_CB1.1,endDiagram_CB1|}] fin1_CB1_t) [{|ce_CB1.1,lock_act1_CB1,event_act1_CB1,update_CB1.2,ce_CB1.2,endDiagram_CB1,clear_CB1.1|}||{|update_CB1.1,ce_CB1.1,endDiagram_CB1|}] init_CB1_t)\n" + 
				"init1_CB1_t = update_CB1.1!(1-0) -> ce_CB1.1 -> SKIP\n" + 
				"act1_CB1 = ce_CB1.1 -> lock_act1_CB1.lock -> event_act1_CB1 -> lock_act1_CB1.unlock -> update_CB1.2!(1-1) -> ((ce_CB1.2 -> SKIP)); act1_CB1\n" + 
				"act1_CB1_t = act1_CB1 /\\ END_DIAGRAM_CB1\n" + 
				"fin1_CB1 = ((ce_CB1.2 -> SKIP)); clear_CB1.1 -> SKIP\n" + 
				"fin1_CB1_t = fin1_CB1 /\\ END_DIAGRAM_CB1\n" + 
				"init_CB1_t = (init1_CB1_t) /\\ END_DIAGRAM_CB1\n" + 
				"TokenManager_CB1(x,init) = update_CB1?c?y:limiteUpdate_CB1 -> x+y < 10 & x+y > -10 & TokenManager_CB1(x+y,1) [] clear_CB1?c -> endDiagram_CB1 -> SKIP [] x == 0 & init == 1 & endDiagram_CB1 -> SKIP\n" + 
				"TokenManager_CB1_t(x,init) = TokenManager_CB1(x,init)\n" + 
				"Lock_act1_CB1 = lock_act1_CB1.lock -> lock_act1_CB1.unlock -> Lock_act1_CB1 [] endDiagram_CB1 -> SKIP\n" + 
				"Lock_CB1 = Lock_act1_CB1");
		
		assertEquals(expected.toString(), actual);
	}
	
	

	/*
	 * Teste de Tradução do call behavior
	 * */
	@Test
	public void TestNodesBehavior3() {
		String actual = "";
		StringBuffer expected = new StringBuffer();
		expected.append("ID_behavior1 = {1..1}\n" + 
				"datatype T = lock | unlock\n" + 
				"countCe_behavior1 = {1..3}\n" + 
				"countUpdate_behavior1 = {1..3}\n" + 
				"countClear_behavior1 = {1..1}\n" + 
				"limiteUpdate_behavior1 = {(0)..(1)}\n" + 
				"channel startActivity_behavior1: ID_behavior1\n" + 
				"channel endActivity_behavior1: ID_behavior1\n" + 
				"channel ce_behavior1: countCe_behavior1\n" + 
				"channel clear_behavior1: countClear_behavior1\n" + 
				"channel update_behavior1: countUpdate_behavior1.limiteUpdate_behavior1\n" + 
				"channel endDiagram_behavior1\n" + 
				"channel lock_CB1_behavior1,lock_CB2_behavior1: T\n" + 
				"channel loop\n" + 
				"MAIN = behavior1(1); LOOP\n" + 
				"LOOP = loop -> LOOP\n" + 
				"END_DIAGRAM_behavior1 = endDiagram_behavior1 -> SKIP\n" + 
				"behavior1(ID_behavior1) = (((((Internal_behavior1(ID_behavior1) [|{|startActivity_CB1.1,endActivity_CB1.1|}|] CB1(1)) [|{|startActivity_CB2.1,endActivity_CB2.1|}|] CB2(1)) [|{|update_behavior1,clear_behavior1,endDiagram_behavior1|}|] TokenManager_behavior1_t(0,0))) [|{|lock_CB1_behavior1,endDiagram_behavior1|}|] Lock_behavior1)\n" + 
				"Internal_behavior1(ID_behavior1) = StartActivity_behavior1(ID_behavior1); Node_behavior1; EndActivity_behavior1(ID_behavior1)\n" + 
				"StartActivity_behavior1(ID_behavior1) = startActivity_behavior1.ID_behavior1 -> SKIP\n" + 
				"EndActivity_behavior1(ID_behavior1) = endActivity_behavior1.ID_behavior1 -> SKIP\n" + 
				"Node_behavior1 = (((CB1_behavior1_t [{|ce_behavior1.1,lock_CB1_behavior1,startActivity_CB1.1,endActivity_CB1.1,update_behavior1.2,ce_behavior1.2,endDiagram_behavior1|}||{|ce_behavior1.3,clear_behavior1.1,endDiagram_behavior1|}] fin1_behavior1_t) [{|ce_behavior1.1,ce_behavior1.2,lock_CB1_behavior1,startActivity_CB1.1,endActivity_CB1.1,update_behavior1.2,ce_behavior1.3,endDiagram_behavior1,clear_behavior1.1|}||{|update_behavior1.1,ce_behavior1.1,endDiagram_behavior1|}] init_behavior1_t) [{|ce_behavior1.1,ce_behavior1.2,lock_CB1_behavior1,startActivity_CB1.1,endActivity_CB1.1,update_behavior1.2,ce_behavior1.3,endDiagram_behavior1,clear_behavior1.1,update_behavior1.1|}||{|ce_behavior1.2,lock_CB2_behavior1,startActivity_CB2.1,endActivity_CB2.1,update_behavior1.3,ce_behavior1.3,endDiagram_behavior1|}] CB2_behavior1_t)\n" + 
				"init1_behavior1_t = update_behavior1.1!(1-0) -> ce_behavior1.1 -> SKIP\n" + 
				"CB1_behavior1 = ce_behavior1.1 -> lock_CB1_behavior1.lock -> startActivity_CB1.1 -> endActivity_CB1.1 -> lock_CB1_behavior1.unlock -> update_behavior1.2!(1-1) -> ((ce_behavior1.2 -> SKIP)); CB1_behavior1\n" + 
				"CB1_behavior1_t = CB1_behavior1 /\\ END_DIAGRAM_behavior1\n" + 
				"CB2_behavior1 = ce_behavior1.2 -> lock_CB2_behavior1.lock -> startActivity_CB2.1 -> endActivity_CB2.1 -> lock_CB2_behavior1.unlock -> update_behavior1.3!(1-1) -> ((ce_behavior1.3 -> SKIP)); CB2_behavior1\n" + 
				"CB2_behavior1_t = CB2_behavior1 /\\ END_DIAGRAM_behavior1\n" + 
				"fin1_behavior1 = ((ce_behavior1.3 -> SKIP)); clear_behavior1.1 -> SKIP\n" + 
				"fin1_behavior1_t = fin1_behavior1 /\\ END_DIAGRAM_behavior1\n" + 
				"init_behavior1_t = (init1_behavior1_t) /\\ END_DIAGRAM_behavior1\n" + 
				"TokenManager_behavior1(x,init) = update_behavior1?c?y:limiteUpdate_behavior1 -> x+y < 10 & x+y > -10 & TokenManager_behavior1(x+y,1) [] clear_behavior1?c -> endDiagram_behavior1 -> SKIP [] x == 0 & init == 1 & endDiagram_behavior1 -> SKIP\n" + 
				"TokenManager_behavior1_t(x,init) = TokenManager_behavior1(x,init)\n" + 
				"Lock_CB1_behavior1 = lock_CB1_behavior1.lock -> lock_CB1_behavior1.unlock -> Lock_CB1_behavior1 [] endDiagram_behavior1 -> SKIP\n" + 
				"Lock_CB2_behavior1 = lock_CB2_behavior1.lock -> lock_CB2_behavior1.unlock -> Lock_CB2_behavior1 [] endDiagram_behavior1 -> SKIP\n" + 
				"Lock_behavior1 = (Lock_CB1_behavior1 [|{|endDiagram_behavior1|}|] Lock_CB2_behavior1)\n" + 
				"\n" + 
				"ID_CB1 = {1..1}\n" + 
				"countCe_CB1 = {1..2}\n" + 
				"countUpdate_CB1 = {1..2}\n" + 
				"countClear_CB1 = {1..1}\n" + 
				"limiteUpdate_CB1 = {(0)..(1)}\n" + 
				"channel startActivity_CB1: ID_CB1\n" + 
				"channel endActivity_CB1: ID_CB1\n" + 
				"channel ce_CB1: countCe_CB1\n" + 
				"channel clear_CB1: countClear_CB1\n" + 
				"channel update_CB1: countUpdate_CB1.limiteUpdate_CB1\n" + 
				"channel endDiagram_CB1\n" + 
				"channel event_act1_CB1\n" + 
				"channel lock_act1_CB1: T\n" + 
				"END_DIAGRAM_CB1 = endDiagram_CB1 -> SKIP\n" + 
				"CB1(ID_CB1) = (((Internal_CB1(ID_CB1) [|{|update_CB1,clear_CB1,endDiagram_CB1|}|] TokenManager_CB1_t(0,0))) [|{|lock_act1_CB1,endDiagram_CB1|}|] Lock_CB1)\n" + 
				"Internal_CB1(ID_CB1) = StartActivity_CB1(ID_CB1); Node_CB1; EndActivity_CB1(ID_CB1)\n" + 
				"StartActivity_CB1(ID_CB1) = startActivity_CB1.ID_CB1 -> SKIP\n" + 
				"EndActivity_CB1(ID_CB1) = endActivity_CB1.ID_CB1 -> SKIP\n" + 
				"Node_CB1 = ((act1_CB1_t [{|ce_CB1.1,lock_act1_CB1,event_act1_CB1,update_CB1.2,ce_CB1.2,endDiagram_CB1|}||{|ce_CB1.2,clear_CB1.1,endDiagram_CB1|}] fin1_CB1_t) [{|ce_CB1.1,lock_act1_CB1,event_act1_CB1,update_CB1.2,ce_CB1.2,endDiagram_CB1,clear_CB1.1|}||{|update_CB1.1,ce_CB1.1,endDiagram_CB1|}] init_CB1_t)\n" + 
				"init1_CB1_t = update_CB1.1!(1-0) -> ce_CB1.1 -> SKIP\n" + 
				"act1_CB1 = ce_CB1.1 -> lock_act1_CB1.lock -> event_act1_CB1 -> lock_act1_CB1.unlock -> update_CB1.2!(1-1) -> ((ce_CB1.2 -> SKIP)); act1_CB1\n" + 
				"act1_CB1_t = act1_CB1 /\\ END_DIAGRAM_CB1\n" + 
				"fin1_CB1 = ((ce_CB1.2 -> SKIP)); clear_CB1.1 -> SKIP\n" + 
				"fin1_CB1_t = fin1_CB1 /\\ END_DIAGRAM_CB1\n" + 
				"init_CB1_t = (init1_CB1_t) /\\ END_DIAGRAM_CB1\n" + 
				"TokenManager_CB1(x,init) = update_CB1?c?y:limiteUpdate_CB1 -> x+y < 10 & x+y > -10 & TokenManager_CB1(x+y,1) [] clear_CB1?c -> endDiagram_CB1 -> SKIP [] x == 0 & init == 1 & endDiagram_CB1 -> SKIP\n" + 
				"TokenManager_CB1_t(x,init) = TokenManager_CB1(x,init)\n" + 
				"Lock_act1_CB1 = lock_act1_CB1.lock -> lock_act1_CB1.unlock -> Lock_act1_CB1 [] endDiagram_CB1 -> SKIP\n" + 
				"Lock_CB1 = Lock_act1_CB1\n" + 
				"\n" + 
				"ID_CB2 = {1..1}\n" + 
				"countCe_CB2 = {1..2}\n" + 
				"countUpdate_CB2 = {1..2}\n" + 
				"countClear_CB2 = {1..1}\n" + 
				"limiteUpdate_CB2 = {(0)..(1)}\n" + 
				"channel startActivity_CB2: ID_CB2\n" + 
				"channel endActivity_CB2: ID_CB2\n" + 
				"channel ce_CB2: countCe_CB2\n" + 
				"channel clear_CB2: countClear_CB2\n" + 
				"channel update_CB2: countUpdate_CB2.limiteUpdate_CB2\n" + 
				"channel endDiagram_CB2\n" + 
				"channel event_act1_CB2\n" + 
				"channel lock_act1_CB2: T\n" + 
				"END_DIAGRAM_CB2 = endDiagram_CB2 -> SKIP\n" + 
				"CB2(ID_CB2) = (((Internal_CB2(ID_CB2) [|{|update_CB2,clear_CB2,endDiagram_CB2|}|] TokenManager_CB2_t(0,0))) [|{|lock_act1_CB2,endDiagram_CB2|}|] Lock_CB2)\n" + 
				"Internal_CB2(ID_CB2) = StartActivity_CB2(ID_CB2); Node_CB2; EndActivity_CB2(ID_CB2)\n" + 
				"StartActivity_CB2(ID_CB2) = startActivity_CB2.ID_CB2 -> SKIP\n" + 
				"EndActivity_CB2(ID_CB2) = endActivity_CB2.ID_CB2 -> SKIP\n" + 
				"Node_CB2 = ((act1_CB2_t [{|ce_CB2.1,lock_act1_CB2,event_act1_CB2,update_CB2.2,ce_CB2.2,endDiagram_CB2|}||{|ce_CB2.2,clear_CB2.1,endDiagram_CB2|}] fin1_CB2_t) [{|ce_CB2.1,lock_act1_CB2,event_act1_CB2,update_CB2.2,ce_CB2.2,endDiagram_CB2,clear_CB2.1|}||{|update_CB2.1,ce_CB2.1,endDiagram_CB2|}] init_CB2_t)\n" + 
				"init1_CB2_t = update_CB2.1!(1-0) -> ce_CB2.1 -> SKIP\n" + 
				"act1_CB2 = ce_CB2.1 -> lock_act1_CB2.lock -> event_act1_CB2 -> lock_act1_CB2.unlock -> update_CB2.2!(1-1) -> ((ce_CB2.2 -> SKIP)); act1_CB2\n" + 
				"act1_CB2_t = act1_CB2 /\\ END_DIAGRAM_CB2\n" + 
				"fin1_CB2 = ((ce_CB2.2 -> SKIP)); clear_CB2.1 -> SKIP\n" + 
				"fin1_CB2_t = fin1_CB2 /\\ END_DIAGRAM_CB2\n" + 
				"init_CB2_t = (init1_CB2_t) /\\ END_DIAGRAM_CB2\n" + 
				"TokenManager_CB2(x,init) = update_CB2?c?y:limiteUpdate_CB2 -> x+y < 10 & x+y > -10 & TokenManager_CB2(x+y,1) [] clear_CB2?c -> endDiagram_CB2 -> SKIP [] x == 0 & init == 1 & endDiagram_CB2 -> SKIP\n" + 
				"TokenManager_CB2_t(x,init) = TokenManager_CB2(x,init)\n" + 
				"Lock_act1_CB2 = lock_act1_CB2.lock -> lock_act1_CB2.unlock -> Lock_act1_CB2 [] endDiagram_CB2 -> SKIP\n" + 
				"Lock_CB2 = Lock_act1_CB2");
		
		assertEquals(expected.toString(), actual);
	}
}
