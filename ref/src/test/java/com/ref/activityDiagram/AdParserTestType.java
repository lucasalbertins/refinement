package com.ref.activityDiagram;

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
import com.ref.parser.activityDiagram.ADParser;

public class AdParserTestType {

	public static IActivityDiagram ad;
	private static ADParser parser1;
	private static ADParser parser2;
	private static ADParser parser3;
	private static ADParser parser4;
	
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
			projectAccessor.open("src/test/resources/activityDiagram/join1.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];

			parser3 = new ADParser(ad.getActivity(), ad.getName(), ad);

			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/decision3.asta");
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
	
//	@Before
//	public void clearBuffer() {
//		parser1.clearBuffer();
//		parser2.clearBuffer();
//	}
	
	@AfterClass
	public static void CloseProject() throws Exception {
		AstahAPI.getAstahAPI().getProjectAccessor().close();
	}
	
	/* Teste de Tradução dos tipos de dados
	 */
	@Test
	public void TestDefineTypes1() {
		parser1.clearBuffer();
		parser1.checkCountCallInitial();
		parser1.defineNodesActionAndControl();
		parser1.defineChannels();
		parser1.defineMainNodes();
		String actual = parser1.defineTypes();
		StringBuffer expected = new StringBuffer();
		expected.append("ID_action1 = {1..1}\n" +
				"datatype T = lock | unlock\n" +
				"countCe_action1 = {1..2}\n" +
				"countUpdate_action1 = {1..1}\n" +
				"countClear_action1 = {1..1}\n" +
				"limiteUpdate_action1 = {(1)..(1)}\n");

		assertEquals(expected.toString(), actual);
	}
	
	/* Teste de Tradução dos tipos de dados
	 */
	@Test
	public void TestDefineTypes2() {
		parser2.clearBuffer();
		parser2.checkCountCallInitial();
		parser2.defineNodesActionAndControl();
		parser2.defineChannels();
		parser2.defineMainNodes();
		String actual = parser2.defineTypes();
		StringBuffer expected = new StringBuffer();
		expected.append("ID_decision1 = {1..1}\n" +
				"datatype T = lock | unlock\n" +
				"x_decision1 = {0..1}\n" +
				"countGet_decision1 = {1..2}\n" +
				"countSet_decision1 = {1..2}\n" +
				"countCe_decision1 = {1..5}\n" +
				"countOe_decision1 = {1..1}\n" +
				"countUpdate_decision1 = {1..3}\n" +
				"countClear_decision1 = {1..1}\n" +
				"limiteUpdate_decision1 = {(-1)..(1)}\n");

		assertEquals(expected.toString(), actual);
	}
	
	/* Teste de Tradução dos tipos de dados
	 */
	@Test
	public void TestDefineTypes3() {
		parser3.clearBuffer();
		parser3.checkCountCallInitial();
		parser3.defineNodesActionAndControl();
		parser3.defineChannels();
		parser3.defineMainNodes();
		String actual = parser3.defineTypes();
		StringBuffer expected = new StringBuffer();
		expected.append("ID_join1 = {1..1}\n" +
				"datatype T = lock | unlock\n" +
				"countCe_join1 = {1..5}\n" +
				"countUpdate_join1 = {1..2}\n" +
				"countClear_join1 = {1..1}\n" +
				"limiteUpdate_join1 = {(-1)..(2)}\n");

		assertEquals(expected.toString(), actual);
	}
	
	/* Teste de Tradução dos tipos de dados
	 */
	@Test
	public void TestDefineTypes4() {
		parser4.clearBuffer();
		parser4.checkCountCallInitial();
		parser4.defineNodesActionAndControl();
		parser4.defineChannels();
		parser4.defineMainNodes();
		String actual = parser4.defineTypes();
		StringBuffer expected = new StringBuffer();
		expected.append("ID_decision3 = {1..1}\n" +
				"datatype T = lock | unlock\n" +
				"z_decision3 = {0..1}\n" +
				"countGet_decision3 = {1..3}\n" +
				"countSet_decision3 = {1..3}\n" +
				"countOe_decision3 = {1..5}\n" +
				"countUpdate_decision3 = {1..1}\n" +
				"countClear_decision3 = {1..1}\n" +
				"limiteUpdate_decision3 = {(1)..(1)}\n");

		assertEquals(expected.toString(), actual);
	}
	
}
