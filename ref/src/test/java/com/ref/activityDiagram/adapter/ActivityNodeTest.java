package com.ref.activityDiagram.adapter;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IAction;
import com.change_vision.jude.api.inf.model.IActivityDiagram;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.ref.astah.adapter.Action;
import com.ref.exceptions.WellFormedException;
import com.ref.interfaces.activityDiagram.IActivityNode;
import com.ref.interfaces.activityDiagram.IFlow;

public class ActivityNodeTest {
	public static com.change_vision.jude.api.inf.model.IActivityDiagram ad;
	public static com.change_vision.jude.api.inf.model.IActivityNode activityNode;
	@BeforeClass
	public static void GetDiagram() throws Exception {
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/action1.asta");
			INamedElement[] findElements = TestUtils.findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			activityNode = (com.change_vision.jude.api.inf.model.IAction) ad.getActivity().getActivityNodes()[1];
			
		} catch (ProjectNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@Test
	public void testGetIncomings() throws WellFormedException {
		//ActivityNode activityNode = new ActivityNode(ActivityNodeTest.activityNode);
		IActivityNode activityNode = new Action((IAction) ActivityNodeTest.activityNode);
		IFlow[] incomings = activityNode.getIncomings();
		for (int i = 0; i < incomings.length; i++) {
			assertEquals(incomings[i].getId(), ActivityNodeTest.activityNode.getIncomings()[i].getId());
		}
	}
	
	@Test
	public void testGetOutGoings() throws WellFormedException {
		IActivityNode activityNode = new Action((IAction) ActivityNodeTest.activityNode);
		IFlow[] outgoings = activityNode.getOutgoings();
		for (int i = 0; i < outgoings.length; i++) {
			assertEquals(outgoings[i].getId(), activityNode.getOutgoings()[i].getId());
		}
	}
	
	@Test
	public void testGetId() throws WellFormedException {
		IActivityNode activityNode = new Action((IAction) ActivityNodeTest.activityNode);
		assertEquals(activityNode.getId(), ActivityNodeTest.activityNode.getId());
	}
	
	@Test
	public void testGetDefinition() throws WellFormedException {
		IActivityNode activityNode = new Action((IAction) ActivityNodeTest.activityNode);
		assertEquals(activityNode.getDefinition(), ActivityNodeTest.activityNode.getDefinition());
	}
	
	@Test
	public void testGetName() throws WellFormedException {
		IActivityNode activityNode = new Action((IAction) ActivityNodeTest.activityNode);
		assertEquals(activityNode.getName(), ActivityNodeTest.activityNode.getName());
	}
	
	@Test
	public void testGetStereotypes() throws WellFormedException {
		IActivityNode activityNode = new Action((IAction) ActivityNodeTest.activityNode);
		String[] stereotypes = activityNode.getStereotypes();
		for (int i = 0; i < stereotypes.length; i++) {
			assertEquals(stereotypes[i], ActivityNodeTest.activityNode.getStereotypes()[i]);
		}
	}
}
