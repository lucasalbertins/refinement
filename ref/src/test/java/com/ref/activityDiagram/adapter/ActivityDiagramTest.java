package com.ref.activityDiagram.adapter;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IActivityDiagram;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.ref.adapter.astah.ActivityDiagram;
import com.ref.exceptions.WellFormedException;

public class ActivityDiagramTest {
	public static com.change_vision.jude.api.inf.model.IActivityDiagram ad;
	
	@BeforeClass
	public static void GetDiagram() throws Exception {
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/action1.asta");
			INamedElement[] findElements = TestUtils.findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
		} catch (ProjectNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@Test
	public void testGetId() throws WellFormedException {
		ActivityDiagram activityDiagram = new ActivityDiagram(ad);
		assertEquals(activityDiagram.getId(), ActivityDiagramTest.ad.getId());
	}
	
	@Test
	public void testGetName() throws WellFormedException {
		ActivityDiagram activityDiagram = new ActivityDiagram(ad);
		assertEquals(activityDiagram.getName(), ActivityDiagramTest.ad.getName());
	}
	
	@Test
	public void testGetDefinition() throws WellFormedException {
		ActivityDiagram activityDiagram = new ActivityDiagram(ad);
		assertEquals(activityDiagram.getDefinition(), ActivityDiagramTest.ad.getDefinition());
	}
	
	@Test
	public void testGetStereotypes() throws WellFormedException {
		ActivityDiagram activityDiagram = new ActivityDiagram(ad);
		String[] stereotypes = activityDiagram.getStereotypes();
		for (int i = 0; i < stereotypes.length; i++) {
			assertEquals(stereotypes[i], ActivityDiagramTest.ad.getStereotypes()[i]);
		}
	}
	
	@Test
	public void testGetActivity() throws WellFormedException {
		ActivityDiagram activityDiagram = new ActivityDiagram(ad);
		assertEquals(activityDiagram.getActivity().getId(), ActivityDiagramTest.ad.getActivity().getId());
	}
}
