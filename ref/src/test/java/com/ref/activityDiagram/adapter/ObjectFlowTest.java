package com.ref.activityDiagram.adapter;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IActivityDiagram;
import com.change_vision.jude.api.inf.model.IControlNode;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IOutputPin;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.ref.astah.adapter.ControlNode;
import com.ref.astah.adapter.ObjectFlow;
import com.ref.astah.adapter.OutputPin;
import com.ref.exceptions.WellFormedException;
import com.ref.interfaces.activityDiagram.IFlow;

public class ObjectFlowTest {
	public static com.change_vision.jude.api.inf.model.IActivityDiagram ad;
	public static com.change_vision.jude.api.inf.model.IFlow objFlow;
	public static com.change_vision.jude.api.inf.model.IFlow objFlow2;

	@BeforeClass
	public static void GetDiagram() throws Exception {
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/action5.asta");
			INamedElement[] findElements = TestUtils.findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			objFlow = ad.getActivity().getFlows()[1];
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/decision1.asta");
			findElements = TestUtils.findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			objFlow2 = ad.getActivity().getFlows()[1];
			
			/*List<com.change_vision.jude.api.inf.model.IClass> aux = new ArrayList<>();
			aux.add(((IObjectNode)objFlow2.getSource()).getBase());*/
			
		} catch (ProjectNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@Test
	public void testGetId() throws WellFormedException {
		ObjectFlow objFlow = new ObjectFlow(ObjectFlowTest.objFlow);
		assertEquals(objFlow.getId(), ObjectFlowTest.objFlow.getId());
	}
	
	@Test
	public void testGetName() throws WellFormedException {
		ObjectFlow objFlow = new ObjectFlow(ObjectFlowTest.objFlow);
		assertEquals(objFlow.getName(), ObjectFlowTest.objFlow.getName());
	}
	
	@Test
	public void testGetDefinition() throws WellFormedException {
		ObjectFlow objFlow = new ObjectFlow(ObjectFlowTest.objFlow);
		assertEquals(objFlow.getDefinition(), ObjectFlowTest.objFlow.getDefinition());
	}
	
	//TODO verificar esse teste
	@Test
	public void testGetBase() throws WellFormedException {
		ObjectFlow objFlow = new ObjectFlow(ObjectFlowTest.objFlow);
		String base = "int";
		assertEquals(objFlow.getBase().getName(), base);
	}
	
	@Test
	public void testGetStereotypes() throws WellFormedException {
		IFlow objFlow = new ObjectFlow(ObjectFlowTest.objFlow);
		String[] stereotypes = objFlow.getStereotypes();
		for (int i = 0; i < stereotypes.length; i++) {
			assertEquals(stereotypes[i], ObjectFlowTest.objFlow.getStereotypes()[i]);
		}
	}
	
	@Test
	public void testGetTarget() throws WellFormedException {
		IFlow objFlow = new ObjectFlow(ObjectFlowTest.objFlow);
		objFlow.setTarget(new ControlNode((IControlNode) ObjectFlowTest.objFlow.getTarget()));
		assertEquals(objFlow.getTarget().getId(), ObjectFlowTest.objFlow.getTarget().getId());
	}
	
	@Test
	public void testGetSource() throws WellFormedException {
		IFlow objFlow = new ObjectFlow(ObjectFlowTest.objFlow);
		objFlow.setSource(new OutputPin((IOutputPin) ObjectFlowTest.objFlow.getSource()));
		assertEquals(objFlow.getSource().getId(), ObjectFlowTest.objFlow.getSource().getId());
	}
	
	@Test
	public void testGetGuard() throws WellFormedException {
		IFlow objFlow = new ObjectFlow(ObjectFlowTest.objFlow2); //TODO verificar o diagrama
		assertEquals(objFlow.getGuard(), ObjectFlowTest.objFlow2.getGuard());
	}
}
