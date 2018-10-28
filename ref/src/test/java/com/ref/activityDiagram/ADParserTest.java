package com.ref.activityDiagram;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IActivityDiagram;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.project.ModelFinder;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.ref.parser.activityDiagram.ADParser;

public class ADParserTest {
	
	/*
	 * Criar teste para cada parte do diagrama de atividades
	 * */
	
	public static IActivityDiagram ad;
	private static ADParser parser;
	
	@BeforeClass
	public static void GetDiagram() throws Exception {
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/ad2.asta");
			INamedElement[] findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser = new ADParser(ad.getActivity(), ad.getName());
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
	
	/*
	 * Teste de Tradução dos tipos de elementos
	 * */
	@Ignore
	@Test
	public void TestNodesActionAndcountrol() {
		String actual = parser.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("init1_ad2_1 = update_ad2_1.1!(1-0) -> cn_ad2_1.1 -> SKIP\n" + 
				"act1_ad2_1 = cn_ad2_1.1 -> event_act1_ad2_1 -> update_ad2_1.2!(1-1) -> cn_ad2_1.2 -> act1_ad2_1\n" + 
				"act1_ad2_t_1 = act1_ad2_1 /\\ END_DIAGRAM_ad2_1\n" + 
				"fin1_ad2_1 = cn_ad2_1.2 -> clear_ad2_1.1 -> SKIP\n" + 
				"fin1_ad2_t_1 = fin1_ad2_1 /\\ END_DIAGRAM_ad2_1\n");
		
		assertEquals(expected.toString(), actual);
	}
	
	
	 /* Teste de Tradução do processo Node_"getname"
	 */
	@Ignore
	@Test
	public void TestDefineProcessNode() {
		String actual = "";
		StringBuffer expected = new StringBuffer();
		expected.append("Node_ad2_1 = ((init1_ad2_1 [{|cn_ad2_1.1,update_ad2_1.1|}||{|cn_ad2_1.1,cn_ad2_1.2,update_ad2_1.2,endDiagram_ad2_1,event_act1_ad2_1|}] act1_ad2_t_1) [{|cn_ad2_1.1,cn_ad2_1.2,update_ad2_1.1,update_ad2_1.2,endDiagram_ad2_1,event_act1_ad2_1|}||{|cn_ad2_1.2,clear_ad2_1.1,endDiagram_ad2_1|}] fin1_ad2_t_1)\n");
	
		assertEquals(expected.toString(), actual);
	}
	
	
	
	 /* Teste de Tradução do processo de memória
	 */ 
	@Ignore
	@Test
	public void TestDefineProcessMemory() {
		
	}
	
	
	
	 /* Teste de Tradução do processo TokenManager
	 */ 
	@Ignore
	@Test
	public void TestDefineProcessTokenManager() {
		String actual = "";
		StringBuffer expected = new StringBuffer();
		expected.append("TokenManager_ad2_1(x,init) = update_ad2_1?c?y:limiteUpdate_ad2_1 -> x+y < 10 & x+y > -10 & TokenManager_ad2_1(x+y,1) [] clear_ad2_1?c -> endDiagram_ad2_1 -> SKIP [] x == 0 & init == 1 & endDiagram_ad2_1 -> SKIP\n" + 
				"TokenManager_ad2_t_1(x,init) = TokenManager_ad2_1(x,init)\n");
		
		assertEquals(expected.toString(), actual);
	}
	
	
	 /* Teste de Tradução do processo Lock
	 */ 
//	@Ignore
//	@Test
//	public void TestDefineProcessLock() {
//		String actual = "";
//		StringBuffer expected = new StringBuffer();
//		expected.append("");
//		
//		assertEquals(expected.toString(), actual);
//	}

	
	
	 /* Teste de Tradução do processo principal
	 */ 
	@Ignore
	@Test
	public void TestDefineProcessMain() {
		
		String actual = "";
		StringBuffer expected = new StringBuffer();
		expected.append("MAIN = ad2_1; LOOP\n" + 
				"LOOP = loop -> LOOP\n" + 
				"END_DIAGRAM_ad2_1 = endDiagram_ad2_1 -> SKIP\n" + 
				"ad2_1 = ((Internal_ad2_1 [|{|update_ad2_1,clear_ad2_1,endDiagram_ad2_1|}|] TokenManager_ad2_t_1(0,0)))\n" + 
				"Internal_ad2_1 = StartActivity_ad2_1; Node_ad2_1; EndActivity_ad2_1\n" + 
				"StartActivity_ad2_1 = startActivity_ad2_1 -> SKIP\n" + 
				"EndActivity_ad2_1 = endActivity_ad2_1 -> SKIP\n");
		
		assertEquals(expected.toString(), actual);
	}
	
	
	
	 /* Teste de Tradução dos tipos de dados
	 */
	@Ignore
	@Test
	public void TestDefineTypes() {
		parser.defineNodesActionAndControl();
		String actual = parser.defineTypes();
		StringBuffer expected = new StringBuffer();
		expected.append("countCn_ad2_1 = {1..2}\n" + 
				"countUpdate_ad2_1 = {1..2}\n" + 
				"countClear_ad2_1 = {1..1}\n" + 
				"limiteUpdate_ad2_1 = {(-2)..2}\n");

		assertEquals(expected.toString(), actual);
	}
	
	
	/* Teste de Tradução dos canais
	 */
	
	@Test
	public void TestDefineChannels() {
		parser.defineNodesActionAndControl();
		parser.defineTypes();
		String actual = parser.defineChannels();
		StringBuffer expected = new StringBuffer();
		expected.append("channel startActivity_ad2_1\n" + 
				"channel endActivity_ad2_1\n" + 
				"channel cn_ad2_1: countCn_ad2_1\n" + 
				"channel clear_ad2_1: countClear_ad2_1\n" + 
				"channel update_ad2_1: countUpdate_ad2_1.limiteUpdate_ad2_1\n" + 
				"channel endDiagram_ad2_1\n" + 
				"channel event_act1_ad2_1\n" + 
				"channel loop\n");

		assertEquals(expected.toString(), actual);
	}
	
	@AfterClass
	public static void CloseProject() throws Exception {
		AstahAPI.getAstahAPI().getProjectAccessor().close();
	}
	
}
