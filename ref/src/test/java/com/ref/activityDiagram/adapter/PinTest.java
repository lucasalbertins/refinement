package com.ref.activityDiagram.adapter;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
//import com.change_vision.jude.api.inf.model.IAction;
import com.change_vision.jude.api.inf.model.IActivityDiagram;
import com.change_vision.jude.api.inf.model.IInputPin;
//import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IPin;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.ref.astah.adapter.InputPin;
import com.ref.exceptions.WellFormedException;
import com.ref.interfaces.activityDiagram.IAction;
import com.ref.interfaces.activityDiagram.IFlow;
import com.ref.interfaces.activityDiagram.INamedElement;

public class PinTest {
	public static com.change_vision.jude.api.inf.model.IActivityDiagram ad;
	private static IPin pin;

	
	@BeforeClass
	public static void GetDiagram() throws Exception {
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/action5.asta");
			INamedElement[] findElements = TestUtils.findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			pin = ((com.change_vision.jude.api.inf.model.IAction) ad.getActivity().getActivityNodes()[4]).getInputs()[0];
			
		} catch (ProjectNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@Test
	public void testGetIncomings() throws WellFormedException {
		InputPin pin = new InputPin((IInputPin) PinTest.pin);
		IFlow[] incomings = pin.getIncomings();
		for (int i = 0; i < incomings.length; i++) {
			incomings[i].getId();
			assertEquals(incomings[i].getId(), PinTest.pin.getIncomings()[i].getId());
		}
	}
	
	@Test
	public void testGetOutGoings() throws WellFormedException {
		InputPin pin = new InputPin((IInputPin) PinTest.pin);
		IFlow[] outgoings = pin.getOutgoings();
		for (int i = 0; i < outgoings.length; i++) {
			assertEquals(outgoings[i].getId(), pin.getOutgoings()[i].getId());
		}
	}
	
	@Test
	public void testGetId() throws WellFormedException {
		InputPin pin = new InputPin((IInputPin) PinTest.pin);
		assertEquals(pin.getId(), PinTest.pin.getId());
	}
	
	@Test
	public void testGetDefinition() throws WellFormedException {
		InputPin pin = new InputPin((IInputPin) PinTest.pin);
		assertEquals(pin.getDefinition(), PinTest.pin.getDefinition());
	}
	
	@Test
	public void testGetName() throws WellFormedException {
		InputPin pin = new InputPin((IInputPin) PinTest.pin);
		assertEquals(pin.getName(), PinTest.pin.getName());
	}
	
	@Test
	public void testGetStereotypes() throws WellFormedException {
		InputPin pin = new InputPin((IInputPin) PinTest.pin);
		String[] stereotypes = pin.getStereotypes();
		for (int i = 0; i < stereotypes.length; i++) {
			assertEquals(stereotypes[i], PinTest.pin.getStereotypes()[i]);
		}
	}
	
	@Test
	public void testGetBase() throws WellFormedException {
		InputPin pin = new InputPin((IInputPin) PinTest.pin);
		assertEquals(pin.getBase().getId(), PinTest.pin.getBase().getId());
	}
	
	@Test
	public void testGetOwner() throws WellFormedException {
		InputPin pin = new InputPin((IInputPin) PinTest.pin);
		pin.setOwner((IAction) PinTest.pin.getOwner());
		assertEquals(pin.getOwner().getId(), PinTest.pin.getOwner().getId());
	}
}
