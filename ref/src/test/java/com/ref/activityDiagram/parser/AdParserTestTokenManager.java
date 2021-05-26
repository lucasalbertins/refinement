package com.ref.activityDiagram.parser;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
//import com.change_vision.jude.api.inf.model.IActivityDiagram;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.project.ModelFinder;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.ref.exceptions.ParsingException;
import com.ref.parser.activityDiagram.ADParser;
import com.ref.interfaces.activityDiagram.IActivityDiagram;

public class AdParserTestTokenManager {

	public static IActivityDiagram ad;
	private static ADParser parser1;
	private static ADParser parser2;
	
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
	}
	
	@AfterClass
	public static void CloseProject() throws Exception {
		AstahAPI.getAstahAPI().getProjectAccessor().close();
	}
	
	/*
	 * Teste de Tradução TokenManager
	 * */
	@Test
	public void TestNodesTokenManager1() throws ParsingException {
		parser1.defineNodesActionAndControl();
		String actual = parser1.defineTokenManager();
		StringBuffer expected = new StringBuffer();
		expected.append("TokenManager_action1(x,init) = update_action1?c?y:limiteUpdate_action1 -> x+y < 10 & x+y > -10 & TokenManager_action1(x+y,1) [] clear_action1?c -> endDiagram_action1 -> SKIP [] x == 0 & init == 1 & endDiagram_action1 -> SKIP\n" + 
				"TokenManager_action1_t(x,init) = TokenManager_action1(x,init)\n");
		
		assertEquals(expected.toString(), actual);
	}
	
	/*
	 * Teste de Tradução TokenManager
	 * */
	@Test
	public void TestNodesTokenManager2() throws ParsingException {
		parser2.defineNodesActionAndControl();
		String actual = parser2.defineTokenManager();
		StringBuffer expected = new StringBuffer();
		expected.append("TokenManager_decision1(x,init) = update_decision1?c?y:limiteUpdate_decision1 -> x+y < 10 & x+y > -10 & TokenManager_decision1(x+y,1) [] clear_decision1?c -> endDiagram_decision1 -> SKIP [] x == 0 & init == 1 & endDiagram_decision1 -> SKIP\n" + 
				"TokenManager_decision1_t(x,init) = TokenManager_decision1(x,init)\n");
		
		assertEquals(expected.toString(), actual);
	}
	
}
