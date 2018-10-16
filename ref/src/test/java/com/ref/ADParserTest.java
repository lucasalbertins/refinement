package com.ref;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IActivityDiagram;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.project.ModelFinder;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

public class ADParserTest {
	
	/*
	 * Criar teste para cada parte do diagrama de atividades
	 * */
	
	public static IActivityDiagram ad;
	
	@BeforeClass
	public static void GetDiagram() throws Exception {
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/ad2.asta");
			INamedElement[] findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			//parser = new ADParser(ad);
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
	@Test
	public void TestDefineTypesElements() {
		String actual = "";
		StringBuffer expected = new StringBuffer();
		expected.append("init1_ad2 = cn_ad2.1 -> SKIP\n" + 
				"act1_ad2 = cn_ad2.1 -> lock_ad2.1.1 -> cn_ad2.2 -> update_ad2.2!(1-1) -> lock_ad2.1.0 -> act1_ad2\n" + 
				"act1_ad2_t = act1_ad2 /\\ END_DIAGRAM_ad2\n" + 
				"fin1_ad2 = cn_ad2.2 -> lock_ad2.2.1 -> clear_ad2.1 -> lock_ad2.2.0 -> SKIP\n" + 
				"fin1_ad2_t = fin1_ad2 /\\ END_DIAGRAM_ad2\n");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução do processo Node_"getname"
	 * */
	@Test
	public void TestDefineProcessNode() {
		String actual = "";
		StringBuffer expected = new StringBuffer();
		expected.append("Node_ad2 = ((init1_ad2 [{|cn_ad2.1|}||{|cn_ad2.1,lock_ad2.1,cn_ad2.2,update_ad2.2,endDiagram_ad2|}] act1_ad2_t) [{|cn_ad2.1,lock_ad2.1,cn_ad2.2,update_ad2.2,endDiagram_ad2|}||{|cn_ad2.2,lock_ad2.2,clear_ad2.1,endDiagram_ad2|}] fin1_ad2_t)\n");
	
		assertEquals(expected.toString(), actual);
	}
	
	
	/*
	 * Teste de Tradução do processo de memória
	 * */
	@Test
	public void TestDefineProcessMemory() {
		/*ad2 não contém*/
	}
	
	
	/*
	 * Teste de Tradução do processo TokenManager
	 * */
	@Test
	public void TestDefineProcessTokenManager() {
		String actual = "";
		StringBuffer expected = new StringBuffer();
		expected.append("TokenManager_ad2(l,a,cont) = update_ad2.head(l)?b:limiteUpdate_ad2 -> a+b < 10 & a+b > -10 & TokenManager_ad2(l, a+b, 1) [] clear_ad2.head(l) -> endDiagram_ad2 -> SKIP [] a == 0 & get_value_ad2?x -> x == 1 & endDiagram_ad2 -> SKIP [] cont < length(l) & TokenManager_ad2((tail(l)^<head(l)>), a, cont+1)\n" + 
				"TokenManager_ad2_t(l,a,cont) = TokenManager_ad2(l, a, cont)\n");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução do processo Lock
	 * */
	@Test
	public void TestDefineProcessLock() {
		String actual = "";
		StringBuffer expected = new StringBuffer();
		expected.append("Lock_ad2(l,x,cont) = x == 1 & lock_ad2.head(l).1 -> Lock_ad2(l,0,1) [] x == 0 & lock_ad2.head(l).0 -> Lock_ad2(l,1,1) [] get_value_ad2!x -> Lock_ad2(l,x,1) [] cont < length(l) & Lock_ad2((tail(l)^<head(l)>), x, cont+1)\n" + 
				"Lock_ad2_t(l,x,cont) = Lock_ad2(l,x,cont) /\\ END_DIAGRAM_ad2\n");
		
		assertEquals(expected.toString(), actual);
	}

	
	/*
	 * Teste de Tradução do processo principal
	 * */
	@Test
	public void TestDefineProcessMain() {
		/*
		 * ad process
		 * internal_ad process
		 * StartActivity_ad process
		 * EndActivity_ad process
		 * END_DIAGRAM_ad process
		 * MAIN process
		 * LOOP process
		 * */
		
		String actual = "";
		StringBuffer expected = new StringBuffer();
		expected.append("MAIN = ad2; LOOP\n" + 
				"LOOP = loop -> LOOP\n" + 
				"END_DIAGRAM_ad2 = endDiagram_ad2 -> SKIP\n" + 
				"ad2 = ((Internal_ad2 [|{|update_ad2,clear_ad2,get_value_ad2,endDiagram_ad2|}|] TokenManager_ad2_t(contTokenList_ad2,0,1)) [|{|lock_ad2,get_value_ad2,endDiagram_ad2|}|] Lock_ad2_t(contLockList_ad2,1,1))\n" + 
				"Internal_ad2 = StartActivity_ad2; Node_ad2; EndActivity_ad2\n" + 
				"StartActivity_ad2 = startActivity_ad2 -> update_ad2.1!1 -> SKIP\n" + 
				"EndActivity_ad2 = endActivity_ad2 -> SKIP\n");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução dos canais e dados
	 * */
	@Test
	public void TestDefineChannels() {
		String actual = "";
		StringBuffer expected = new StringBuffer();
		expected.append("contCn_ad2 = {1..2}\n" + 
				"contUpdate_ad2 = {1..2}\n" + 
				"contClear_ad2 = {1..2}\n" + 
				"limiteUpdate_ad2 = {(-2)..2}\n" + 
				"contLock_ad2 = {1..2}\n" + 
				"contLockList_ad2 = <1..2>\n" + 
				"contTokenList_ad2 = <1..2>\n" + 
				"channel startActivity_ad2\n" + 
				"channel endActivity_ad2\n" + 
				"channel cn_ad2: contCn_ad2\n" + 
				"channel clear_ad2: contClear_ad2\n" + 
				"channel update_ad2: contUpdate_ad2.limiteUpdate_ad2\n" + 
				"channel endDiagram_ad2\n" + 
				"channel lock_ad2: contLock_ad2.{0,1}\n" + 
				"channel get_value_ad2: {0,1}\n" + 
				"channel loop\n");

		assertEquals(expected.toString(), actual);
	}
	
	@AfterClass
	public static void CloseProject() throws Exception {
		AstahAPI.getAstahAPI().getProjectAccessor().close();
	}
	
}
