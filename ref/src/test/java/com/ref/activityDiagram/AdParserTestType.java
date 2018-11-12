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
	
	@BeforeClass
	public static void GetDiagram() throws Exception {
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/action1.asta");
			INamedElement[] findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
	
			parser1 = new ADParser(ad.getActivity(), ad.getName());
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/decision1.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];

			parser2 = new ADParser(ad.getActivity(), ad.getName());
			
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
		parser1.defineNodesActionAndControl();
		String actual = parser1.defineTypes();
		StringBuffer expected = new StringBuffer();
		expected.append("ID_action1 = {1..1}\n" + 
				"datatype T = lock | unlock\n" + 
				"countCn_action1 = {1..2}\n" + 
				"countUpdate_action1 = {1..2}\n" + 
				"countClear_action1 = {1..1}\n" + 
				"limiteUpdate_action1 = {(-2)..2}\n");

		assertEquals(expected.toString(), actual);
	}
	
	/* Teste de Tradução dos tipos de dados
	 */
	@Test
	public void TestDefineTypes2() {
		parser2.clearBuffer();
		parser2.defineNodesActionAndControl();
		String actual = parser2.defineTypes();
		StringBuffer expected = new StringBuffer();
		expected.append("ID_decision1 = {1..1}\n" + 
				"datatype T = lock | unlock\n" + 
				"typeIn_decision1 = {0..1}\n" + 
				"countGet_decision1 = {1..1}\n" + 
				"countSet_decision1 = {1..1}\n" + 
				"countCn_decision1 = {1..5}\n" + 
				"countUpdate_decision1 = {1..4}\n" + 
				"countClear_decision1 = {1..1}\n" + 
				"limiteUpdate_decision1 = {(-2)..2}\n");

		assertEquals(expected.toString(), actual);
	}
	
}
