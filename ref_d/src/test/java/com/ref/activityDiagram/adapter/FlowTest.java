package com.ref.activityDiagram.adapter;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IAction;
import com.change_vision.jude.api.inf.model.IActivityDiagram;
import com.change_vision.jude.api.inf.model.IControlNode;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.ref.astah.adapter.Action;
import com.ref.astah.adapter.ControlFlow;
import com.ref.astah.adapter.ControlNode;
import com.ref.exceptions.WellFormedException;
import com.ref.interfaces.activityDiagram.IFlow;

public class FlowTest {
	public static com.change_vision.jude.api.inf.model.IActivityDiagram ad;
	public static com.change_vision.jude.api.inf.model.IFlow flow;
	public static com.change_vision.jude.api.inf.model.IFlow flow2;

	@BeforeClass
	public static void GetDiagram() throws Exception {
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/action1.asta");
			INamedElement[] findElements = TestUtils.findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			flow = ad.getActivity().getFlows()[1];
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/decision1.asta");
			findElements = TestUtils.findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			flow2 = ad.getActivity().getFlows()[1];
			
			
		} catch (ProjectNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@Test
	public void testGetId() {
		IFlow flow = new ControlFlow(FlowTest.flow);
		assertEquals(flow.getId(), FlowTest.flow.getId());
	}
	
	@Test
	public void testGetName() {
		IFlow flow = new ControlFlow(FlowTest.flow);
		assertEquals(flow.getName(), FlowTest.flow.getName());
	}
	
	@Test
	public void testGetDefinition() {
		IFlow flow = new ControlFlow(FlowTest.flow);
		assertEquals(flow.getDefinition(), FlowTest.flow.getDefinition());
	}
	
	@Test
	public void testGetStereotypes() {
		IFlow flow = new ControlFlow(FlowTest.flow);
		String[] stereotypes = flow.getStereotypes();
		for (int i = 0; i < stereotypes.length; i++) {
			assertEquals(stereotypes[i], FlowTest.flow.getStereotypes()[i]);
		}
	}
	
	@Test
	public void testGetTarget() throws WellFormedException {
		IFlow flow = new ControlFlow(FlowTest.flow);
		flow.setTarget(new ControlNode((IControlNode) FlowTest.flow.getTarget()));
		assertEquals(flow.getTarget().getId(), FlowTest.flow.getTarget().getId());
	}
	
	@Test
	public void testGetSource() throws WellFormedException {
		IFlow flow = new ControlFlow(FlowTest.flow);
		flow.setSource(new Action((IAction) FlowTest.flow.getSource()));
		assertEquals(flow.getSource().getId(), FlowTest.flow.getSource().getId());
	}
	
	@Test
	public void testGetGuard() {
		IFlow flow = new ControlFlow(FlowTest.flow2);
		assertEquals(flow.getGuard(), FlowTest.flow2.getGuard());
	}
	
}
