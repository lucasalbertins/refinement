package com.ref.activityDiagram.adapter;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IActivityDiagram;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.ref.astah.adapter.Activity;
import com.ref.astah.adapter.ActivityDiagram;
import com.ref.exceptions.WellFormedException;
import com.ref.interfaces.activityDiagram.IActivityNode;
import com.ref.activityDiagram.adapter.TestUtils;

public class ActivityTest {
	public static com.change_vision.jude.api.inf.model.IActivityDiagram ad;
	public static com.change_vision.jude.api.inf.model.IActivity activity;

	@BeforeClass
	public static void GetDiagram() throws Exception {
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/action1.asta");
			INamedElement[] findElements = TestUtils.findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			activity = ad.getActivity();
			
			
		} catch (ProjectNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@Test
	public void testGetId() throws WellFormedException {
		Activity activity = new Activity(ad.getActivity());
		assertEquals(activity.getId(), ad.getActivity().getId());
	}
	
	@Test
	public void testGetName() throws WellFormedException {
		Activity activity = new Activity(ad.getActivity());
		assertEquals(activity.getName(), ActivityTest.activity.getName());
	}
	
	@Test
	public void testGetDefinition() throws WellFormedException {
		Activity activity = new Activity(ad.getActivity());
		assertEquals(activity.getDefinition(), ActivityTest.activity.getDefinition());
	}
	
	@Test
	public void testGetStereotypes() throws WellFormedException {
		Activity activity = new Activity(ad.getActivity());
		String[] stereotypes = activity.getStereotypes();
		for (int i = 0; i < stereotypes.length; i++) {
			assertEquals(stereotypes[i], ActivityTest.activity.getStereotypes()[i]);
		}
	}
	
	@Test
	public void testGetActivityDiagram() throws WellFormedException {
		Activity activity = new Activity(ad.getActivity());
		activity.setActivityDiagram(new ActivityDiagram(ad.getActivity().getActivityDiagram()));
		assertEquals(activity.getActivityDiagram().getId(), ActivityTest.activity.getActivityDiagram().getId());		
	}
	
	@Test
	public void testGetActivityNodes() throws WellFormedException {
		Activity activity = new Activity(ad.getActivity());
		IActivityNode[] activityNodes = activity.getActivityNodes();
		for (int i = 0; i < activityNodes.length; i++) {
			assertEquals(activityNodes[i].getId(), ActivityTest.activity.getActivityNodes()[i].getId());
		}
	}
}
