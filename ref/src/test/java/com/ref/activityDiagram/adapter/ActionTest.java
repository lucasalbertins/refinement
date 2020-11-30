package com.ref.activityDiagram.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IActivityDiagram;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.ref.adapter.astah.Action;
import com.ref.exceptions.WellFormedException;
import com.ref.interfaces.activityDiagram.IFlow;
import com.ref.interfaces.activityDiagram.IInputPin;
import com.ref.interfaces.activityDiagram.IOutputPin;

public class ActionTest {
	public static com.change_vision.jude.api.inf.model.IActivityDiagram ad;
	public static com.change_vision.jude.api.inf.model.IAction action;
	public static com.change_vision.jude.api.inf.model.IAction action2;
	public static com.change_vision.jude.api.inf.model.IAction action3;
	public static com.change_vision.jude.api.inf.model.IAction action4;

	@BeforeClass
	public static void GetDiagram() throws Exception {
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/action1.asta");
			INamedElement[] findElements = TestUtils.findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			action = (com.change_vision.jude.api.inf.model.IAction) ad.getActivity().getActivityNodes()[1];
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/behavior1.asta");
			findElements = TestUtils.findElements(projectAccessor);
			
			for (int i = 0; i < findElements.length; i++) {
				if (findElements[i].getName().equals("behavior1")) {
					ad = (IActivityDiagram) findElements[i];
				}
			}
			action2 = (com.change_vision.jude.api.inf.model.IAction) ad.getActivity().getActivityNodes()[1];
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/signal1.asta");
			findElements = TestUtils.findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			action3 = (com.change_vision.jude.api.inf.model.IAction) ad.getActivity().getActivityNodes()[1];
			action4 = (com.change_vision.jude.api.inf.model.IAction) ad.getActivity().getActivityNodes()[0];

		} catch (ProjectNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@Test
	public void testGetIncomings() throws WellFormedException {
		Action action = new Action(ActionTest.action);
		IFlow[] incomings = action.getIncomings();
		for (int i = 0; i < incomings.length; i++) {
			assertEquals(incomings[i].getId(), ActionTest.action.getIncomings()[i].getId());
		}
	}
	
	@Test
	public void testGetOutGoings() throws WellFormedException {
		Action action = new Action(ActionTest.action);
		IFlow[] outgoings = action.getOutgoings();
		for (int i = 0; i < outgoings.length; i++) {
			assertEquals(outgoings[i].getId(), action.getOutgoings()[i].getId());
		}
	}
	
	@Test
	public void testGetId() throws WellFormedException {
		Action action = new Action(ActionTest.action);
		assertEquals(action.getId(), ActionTest.action.getId());
	}
	
	@Test
	public void testGetDefinition() throws WellFormedException {
		Action action = new Action(ActionTest.action);
		assertEquals(action.getDefinition(), ActionTest.action.getDefinition());
	}
	
	@Test
	public void testGetInputs() throws WellFormedException {
		Action action = new Action(ActionTest.action);
		IInputPin[] inputs = action.getInputs();
		for (int i = 0; i < inputs.length; i++) {
			assertEquals(inputs[i].getId(), ActionTest.action.getInputs()[i].getId());
		}
	}
	
	@Test
	public void testGetOutputs() throws WellFormedException {
		Action action = new Action(ActionTest.action);
		IOutputPin[] outputs = action.getOutputs();
		for (int i = 0; i < outputs.length; i++) {
			assertEquals(outputs[i].getId(), ActionTest.action.getOutputs()[i].getId());
		}
	}
	
	@Test
	public void testNullGetCallingActivity() throws WellFormedException {
		Action action = new Action(ActionTest.action);
		assertNull(action.getCallingActivity());
	}
	
	@Test
	public void testGetCallingActivity() throws WellFormedException {
		Action action = new Action(ActionTest.action2);
		assertEquals(action.getCallingActivity().getId(),ActionTest.action2.getCallingActivity().getId());
	}

	@Test
	public void testFalseIsCallBehaviorAction() throws WellFormedException {
		Action action = new Action(ActionTest.action);
		assertFalse(action.isCallBehaviorAction());
	}
	
	@Test
	public void testIsCallBehaviorAction() throws WellFormedException {
		Action action = new Action(ActionTest.action2);
		assertTrue(action.isCallBehaviorAction());
	}
	
	@Test
	public void testFalseIsSendSignalAction() throws WellFormedException {
		Action action = new Action(ActionTest.action);
		assertFalse(action.isSendSignalAction());
	}

	@Test
	public void testIsSendSignalAction() throws WellFormedException {
		Action action = new Action(ActionTest.action4);
		action.getName();
		action3.isSendSignalAction();
		assertTrue(action.isSendSignalAction());
	}
	
	@Test
	public void testFalseIsAcceptEventAction() throws WellFormedException {
		Action action = new Action(ActionTest.action);
		assertFalse(action.isAcceptEventAction());
	}
	
	@Test
	public void testIsAcceptEventAction() throws WellFormedException {
		Action action = new Action(ActionTest.action3);
		action4.getName();
		assertTrue(action.isAcceptEventAction());
	}
	
	@Test
	public void testGetName() throws WellFormedException {
		Action action = new Action(ActionTest.action);
		assertEquals(action.getName(), ActionTest.action.getName());
	}
	
	@Test
	public void testGetStereotypes() throws WellFormedException {
		Action action = new Action(ActionTest.action);
		String[] stereotypes = action.getStereotypes();
		for (int i = 0; i < stereotypes.length; i++) {
			assertEquals(stereotypes[i], ActionTest.action.getStereotypes()[i]);
		}
	}
	
}
