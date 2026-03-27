package com.ref.activityDiagram.adapter;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IActivityDiagram;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IObjectNode;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.ref.astah.adapter.ObjectNode;
import com.ref.exceptions.WellFormedException;
import com.ref.interfaces.activityDiagram.IFlow;

public class ObjectNodeTest {
	public static com.change_vision.jude.api.inf.model.IActivityDiagram ad;
	private static IObjectNode objectNode;

	@BeforeClass
	public static void GetDiagram() throws Exception {
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/objectNode1.asta");
			INamedElement[] findElements = TestUtils.findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			objectNode = (IObjectNode) ad.getActivity().getActivityNodes()[1];
			
		} catch (ProjectNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@Test
	public void testGetIncomings() throws WellFormedException {
		ObjectNode objectNode = new ObjectNode(ObjectNodeTest.objectNode);
		IFlow[] incomings = objectNode.getIncomings();
		for (int i = 0; i < incomings.length; i++) {
			assertEquals(incomings[i].getId(), ObjectNodeTest.objectNode.getIncomings()[i].getId());
		}
	}
	
	@Test
	public void testGetOutGoings() throws WellFormedException {
		ObjectNode objectNode = new ObjectNode(ObjectNodeTest.objectNode);
		IFlow[] outgoings = objectNode.getOutgoings();
		for (int i = 0; i < outgoings.length; i++) {
			assertEquals(outgoings[i].getId(), objectNode.getOutgoings()[i].getId());
		}
	}
	
	@Test
	public void testGetId() throws WellFormedException {
		ObjectNode objectNode = new ObjectNode(ObjectNodeTest.objectNode);
		assertEquals(objectNode.getId(), ObjectNodeTest.objectNode.getId());
	}
	
	@Test
	public void testGetDefinition() throws WellFormedException {
		ObjectNode objectNode = new ObjectNode(ObjectNodeTest.objectNode);
		assertEquals(objectNode.getDefinition(), ObjectNodeTest.objectNode.getDefinition());
	}
	
	@Test
	public void testGetName() throws WellFormedException {
		ObjectNode objectNode = new ObjectNode(ObjectNodeTest.objectNode);
		assertEquals(objectNode.getName(), ObjectNodeTest.objectNode.getName());
	}
	
	@Test
	public void testGetStereotypes() throws WellFormedException {
		ObjectNode objectNode = new ObjectNode(ObjectNodeTest.objectNode);
		String[] stereotypes = objectNode.getStereotypes();
		for (int i = 0; i < stereotypes.length; i++) {
			assertEquals(stereotypes[i], ObjectNodeTest.objectNode.getStereotypes()[i]);
		}
	}
	
	@Test
	public void testGetBase() throws WellFormedException {
		ObjectNode objectNode = new ObjectNode(ObjectNodeTest.objectNode);
		assertEquals(objectNode.getBase().getId(), ObjectNodeTest.objectNode.getBase().getId());
	}
}
