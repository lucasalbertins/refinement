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

public class ADParserTestChannel {

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
	
	/* Teste de Tradução dos canais
	 */
	@Test
	public void TestDefineChannels1() {
		parser1.clearBuffer();
		parser1.defineNodesActionAndControl();
		String actual = parser1.defineChannels();
		StringBuffer expected = new StringBuffer();
		expected.append("channel startActivity_action1: ID_action1\n" + 
				"channel endActivity_action1: ID_action1\n" + 
				"channel cn_action1: countCn_action1\n" + 
				"channel clear_action1: countClear_action1\n" + 
				"channel update_action1: countUpdate_action1.limiteUpdate_action1\n" + 
				"channel endDiagram_action1\n" + 
				"channel event_act1_action1\n" + 
				"channel lock_act1_action1: T\n" + 
				"channel loop\n");

		assertEquals(expected.toString(), actual);
	}
	
	/* Teste de Tradução dos canais
	 */
	@Test
	public void TestDefineChannels2() {
		parser2.clearBuffer();
		parser2.defineNodesActionAndControl();
		String actual = parser2.defineChannels();
		StringBuffer expected = new StringBuffer();
		expected.append("channel startActivity_decision1: ID_decision1.x_decision1\n" + 
				"channel endActivity_decision1: ID_decision1\n" + 
				"channel get_x_decision1: countGet_decision1.x_decision1\n" +
				"channel set_x_decision1: countSet_decision1.x_decision1\n" +  
				"channel cn_decision1: countCn_decision1\n" + 
				"channel clear_decision1: countClear_decision1\n" + 
				"channel update_decision1: countUpdate_decision1.limiteUpdate_decision1\n" + 
				"channel endDiagram_decision1\n" + 
				"channel event_act1_decision1,event_act2_decision1\n" + 
				"channel lock_act1_decision1,lock_act2_decision1: T\n" + 
				"channel loop\n");

		assertEquals(expected.toString(), actual);
	}
	
}
