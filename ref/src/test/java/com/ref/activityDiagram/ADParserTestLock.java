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

public class ADParserTestLock {

	public static IActivityDiagram ad;
	private static ADParser parser1;
	private static ADParser parser2;
	private static ADParser parser3;
	
	@BeforeClass
	public static void GetDiagram() throws Exception {
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/action1.asta");
			INamedElement[] findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser1 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/decision1.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser2 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/action2.asta");
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
	
	/* 
	 * Teste de Tradução do processo Lock
	 */ 
	@Test
	public void TestDefineProcessLock1() {
		parser1.defineNodesActionAndControl();
		String actual = parser1.defineLock();
		StringBuffer expected = new StringBuffer();
		expected.append("Lock_act1_action1 = lock_act1_action1.lock -> lock_act1_action1.unlock -> Lock_act1_action1 [] endDiagram_action1 -> SKIP\n" + 
				"Lock_action1 = Lock_act1_action1\n");
		
		assertEquals(expected.toString(), actual);
	}
	
	/* 
	 * Teste de Tradução do processo Lock
	 */ 
	@Test
	public void TestDefineProcessLock2() {
		parser2.defineNodesActionAndControl();
		String actual = parser2.defineLock();
		StringBuffer expected = new StringBuffer();
		expected.append("Lock_act1_decision1 = lock_act1_decision1.lock -> lock_act1_decision1.unlock -> Lock_act1_decision1 [] endDiagram_decision1 -> SKIP\n" + 
				"Lock_act2_decision1 = lock_act2_decision1.lock -> lock_act2_decision1.unlock -> Lock_act2_decision1 [] endDiagram_decision1 -> SKIP\n" + 
				"Lock_decision1 = (Lock_act1_decision1 [|{|endDiagram_decision1|}|] Lock_act2_decision1)\n");
		
		assertEquals(expected.toString(), actual);
	}
	
	@Test
	public void TestDefineProcessLock3() {
		parser3.defineNodesActionAndControl();
		String actual = parser3.defineLock();
		StringBuffer expected = new StringBuffer();
		expected.append("Lock_act1_action2 = lock_act1_action2.lock -> lock_act1_action2.unlock -> Lock_act1_action2 [] endDiagram_action2 -> SKIP\n" + 
				"Lock_act2_action2 = lock_act2_action2.lock -> lock_act2_action2.unlock -> Lock_act2_action2 [] endDiagram_action2 -> SKIP\n" + 
				"Lock_act3_action2 = lock_act3_action2.lock -> lock_act3_action2.unlock -> Lock_act3_action2 [] endDiagram_action2 -> SKIP\n" + 
				"Lock_action2 = ((Lock_act1_action2 [|{|endDiagram_action2|}|] Lock_act2_action2) [|{|endDiagram_action2|}|] Lock_act3_action2)\n");
		
		assertEquals(expected.toString(), actual);
	}
}
