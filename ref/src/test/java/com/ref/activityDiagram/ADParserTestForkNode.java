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

public class ADParserTestForkNode {

	public static IActivityDiagram ad;
	private static ADParser parser1;
	
	@BeforeClass
	public static void GetDiagram() throws Exception {
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/fork1.asta");
			INamedElement[] findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser1 = new ADParser(ad.getActivity(), ad.getName());
			
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
	}
	
	@AfterClass
	public static void CloseProject() throws Exception {
		AstahAPI.getAstahAPI().getProjectAccessor().close();
	}
	
	/*
	 * Teste de Tradução Fork Node
	 * */
	@Test
	public void TestNodesFork1() {
		String actual = parser1.defineNodesActionAndControl();
		StringBuffer expected = new StringBuffer();
		expected.append("init1_fork1_t = update_fork1.1!(1-0) -> ((ce_fork1.1 -> SKIP))\n" + 
				"fork1_fork1 = ce_fork1.1 -> update_fork1.2!(2-1) -> ((ce_fork1.2 -> SKIP) ||| (ce_fork1.3 -> SKIP)); fork1_fork1\n" + 
				"fork1_fork1_t = fork1_fork1 /\\ END_DIAGRAM_fork1\n" + 
				"act1_fork1 = ce_fork1.2 -> lock_act1_fork1.lock -> event_act1_fork1 -> lock_act1_fork1.unlock -> update_fork1.3!(1-1) -> ce_fork1.4 -> act1_fork1\n" + 
				"act1_fork1_t = act1_fork1 /\\ END_DIAGRAM_fork1\n" + 
				"act2_fork1 = ce_fork1.3 -> lock_act2_fork1.lock -> event_act2_fork1 -> lock_act2_fork1.unlock -> update_fork1.4!(1-1) -> ce_fork1.5 -> act2_fork1\n" + 
				"act2_fork1_t = act2_fork1 /\\ END_DIAGRAM_fork1\n" + 
				"fin1_fork1 = ((ce_fork1.5 -> SKIP) [] (ce_fork1.4 -> SKIP)); clear_fork1.1 -> SKIP\n" + 
				"fin1_fork1_t = fin1_fork1 /\\ END_DIAGRAM_fork1\n" + 
				"init_fork1_t = (init1_fork1_t) /\\ END_DIAGRAM_fork1");
		
		assertEquals(expected.toString(), actual);
	}
	
}
