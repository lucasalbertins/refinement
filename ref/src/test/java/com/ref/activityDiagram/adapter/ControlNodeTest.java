package com.ref.activityDiagram.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IActivityDiagram;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.ref.adapter.astah.ControlNode;
import com.ref.exceptions.WellFormedException;
import com.ref.interfaces.activityDiagram.IFlow;

public class ControlNodeTest {
	public static com.change_vision.jude.api.inf.model.IActivityDiagram ad;
	public static com.change_vision.jude.api.inf.model.IControlNode controlNode1;
	public static com.change_vision.jude.api.inf.model.IControlNode controlNode2;
	public static com.change_vision.jude.api.inf.model.IControlNode controlNode3;
	public static com.change_vision.jude.api.inf.model.IControlNode controlNode4;
	public static com.change_vision.jude.api.inf.model.IControlNode controlNode5;
	public static com.change_vision.jude.api.inf.model.IControlNode controlNode6;
	public static com.change_vision.jude.api.inf.model.IControlNode controlNode7;

	@BeforeClass
	public static void GetDiagram() throws Exception {
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/decision1.asta");
			INamedElement[] findElements = TestUtils.findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			controlNode1 = (com.change_vision.jude.api.inf.model.IControlNode) ad.getActivity().getActivityNodes()[2];
			controlNode2 = (com.change_vision.jude.api.inf.model.IControlNode) ad.getActivity().getActivityNodes()[3];
			controlNode3 = (com.change_vision.jude.api.inf.model.IControlNode) ad.getActivity().getActivityNodes()[4];
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/flowFinal1.asta");
			findElements = TestUtils.findElements(projectAccessor);
			ad = (IActivityDiagram) findElements[0];

			controlNode4 = (com.change_vision.jude.api.inf.model.IControlNode) ad.getActivity().getActivityNodes()[2];
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/fork1.asta");
			findElements = TestUtils.findElements(projectAccessor);
			ad = (IActivityDiagram) findElements[0];

			controlNode5 = (com.change_vision.jude.api.inf.model.IControlNode) ad.getActivity().getActivityNodes()[1];
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/merge1.asta");
			findElements = TestUtils.findElements(projectAccessor);
			ad = (IActivityDiagram) findElements[0];

			controlNode6 = (com.change_vision.jude.api.inf.model.IControlNode) ad.getActivity().getActivityNodes()[3];
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/join1.asta");
			findElements = TestUtils.findElements(projectAccessor);
			ad = (IActivityDiagram) findElements[0];

			controlNode7 = (com.change_vision.jude.api.inf.model.IControlNode) ad.getActivity().getActivityNodes()[3];			
			
		} catch (ProjectNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@Test
	public void testGetIncomings() throws WellFormedException {
		ControlNode controlNode = new ControlNode(ControlNodeTest.controlNode1);
		IFlow[] incomings = controlNode.getIncomings();
		for (int i = 0; i < incomings.length; i++) {
			assertEquals(incomings[i].getId(), ControlNodeTest.controlNode1.getIncomings()[i].getId());
		}
	}
	
	@Test
	public void testGetOutGoings() throws WellFormedException {
		ControlNode controlNode = new ControlNode(ControlNodeTest.controlNode1);
		IFlow[] outgoings = controlNode.getOutgoings();
		for (int i = 0; i < outgoings.length; i++) {
			assertEquals(outgoings[i].getId(), controlNode.getOutgoings()[i].getId());
		}
	}
	
	@Test
	public void testGetId() throws WellFormedException {
		ControlNode controlNode = new ControlNode(ControlNodeTest.controlNode1);
		assertEquals(controlNode.getId(), ControlNodeTest.controlNode1.getId());
	}
	
	@Test
	public void testGetName() throws WellFormedException {
		ControlNode controlNode = new ControlNode(ControlNodeTest.controlNode1);
		assertEquals(controlNode.getName(), ControlNodeTest.controlNode1.getName());
	}
	
	@Test
	public void testGetDefinition() throws WellFormedException {
		ControlNode controlNode = new ControlNode(ControlNodeTest.controlNode1);
		assertEquals(controlNode.getDefinition(), ControlNodeTest.controlNode1.getDefinition());
	}
	
	@Test
	public void testGetStereotypes() throws WellFormedException {
		ControlNode controlNode = new ControlNode(ControlNodeTest.controlNode1);
		String[] stereotypes = controlNode.getStereotypes();
		for (int i = 0; i < stereotypes.length; i++) {
			assertEquals(stereotypes[i], ControlNodeTest.controlNode1.getStereotypes()[i]);
		}
	}
	
	@Test
	public void testInitialNode() throws WellFormedException {
		ControlNode controlNode = new ControlNode(ControlNodeTest.controlNode1);
		//controlNode.getName();
		assertTrue(controlNode.isInitialNode());
		assertFalse(controlNode.isFlowFinalNode());
		assertFalse(controlNode.isFinalNode());
		assertFalse(controlNode.isForkNode());
		assertFalse(controlNode.isJoinNode());
		assertFalse(controlNode.isMergeNode());
		assertFalse(controlNode.isDecisionNode());
	}
	
	@Test
	public void testFlowFinalNode() throws WellFormedException {
		ControlNode controlNode = new ControlNode(ControlNodeTest.controlNode4);
		//controlNode.getName();
		assertFalse(controlNode.isInitialNode());
		assertTrue(controlNode.isFlowFinalNode());
		assertFalse(controlNode.isFinalNode());
		assertFalse(controlNode.isForkNode());
		assertFalse(controlNode.isJoinNode());
		assertFalse(controlNode.isMergeNode());
		assertFalse(controlNode.isDecisionNode());
	}
	
	@Test
	public void testFinalNode() throws WellFormedException {
		ControlNode controlNode = new ControlNode(ControlNodeTest.controlNode3);
		//controlNode.getName();
		assertFalse(controlNode.isInitialNode());
		assertFalse(controlNode.isFlowFinalNode());
		assertTrue(controlNode.isFinalNode());
		assertFalse(controlNode.isForkNode());
		assertFalse(controlNode.isJoinNode());
		assertFalse(controlNode.isMergeNode());
		assertFalse(controlNode.isDecisionNode());
	}
	
	@Test
	public void testForkNode() throws WellFormedException {
		ControlNode controlNode = new ControlNode(ControlNodeTest.controlNode5);
		controlNode.getName();
		assertFalse(controlNode.isInitialNode());
		assertFalse(controlNode.isFlowFinalNode());
		assertFalse(controlNode.isFinalNode());
		assertTrue(controlNode.isForkNode());
		assertFalse(controlNode.isJoinNode());
		assertFalse(controlNode.isMergeNode());
		assertFalse(controlNode.isDecisionNode());
	}
	
	@Test
	public void testJoinNode() throws WellFormedException {
		ControlNode controlNode = new ControlNode(ControlNodeTest.controlNode7);
		controlNode.getName();
		assertFalse(controlNode.isInitialNode());
		assertFalse(controlNode.isFlowFinalNode());
		assertFalse(controlNode.isFinalNode());
		assertFalse(controlNode.isForkNode());
		assertTrue(controlNode.isJoinNode());
		assertFalse(controlNode.isMergeNode());
		assertFalse(controlNode.isDecisionNode());
	}
	
	@Test
	public void testMergeNode() throws WellFormedException {
		ControlNode controlNode = new ControlNode(ControlNodeTest.controlNode6);
		//controlNode.getName();
		assertFalse(controlNode.isInitialNode());
		assertFalse(controlNode.isFlowFinalNode());
		assertFalse(controlNode.isFinalNode());
		assertFalse(controlNode.isForkNode());
		assertFalse(controlNode.isJoinNode());
		assertTrue(controlNode.isMergeNode());
		assertFalse(controlNode.isDecisionNode());
	}
	
	@Test
	public void testDecisionNode() throws WellFormedException {
		ControlNode controlNode = new ControlNode(ControlNodeTest.controlNode2);
		controlNode.getName();
		assertFalse(controlNode.isInitialNode());
		assertFalse(controlNode.isFlowFinalNode());
		assertFalse(controlNode.isFinalNode());
		assertFalse(controlNode.isForkNode());
		assertFalse(controlNode.isJoinNode());
		assertFalse(controlNode.isMergeNode());
		assertTrue(controlNode.isDecisionNode());
	}
}
