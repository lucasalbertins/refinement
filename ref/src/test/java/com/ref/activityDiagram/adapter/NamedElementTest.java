package com.ref.activityDiagram.adapter;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IActivityDiagram;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.ref.astah.adapter.NamedElement;

public class NamedElementTest {
	public static com.change_vision.jude.api.inf.model.IActivityDiagram ad;
	public static com.change_vision.jude.api.inf.model.INamedElement namedElement;
	
	@BeforeClass
	public static void GetDiagram() throws Exception {
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/action1.asta");
			INamedElement[] findElements = TestUtils.findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			namedElement = (com.change_vision.jude.api.inf.model.INamedElement) ad.getActivity().getActivityNodes()[1];
			
		} catch (ProjectNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@Test
	public void testGetId() {
		NamedElement namedElement = new NamedElement(NamedElementTest.namedElement);
		assertEquals(namedElement.getId(), NamedElementTest.namedElement.getId());
	}
	
	@Test
	public void testGetName() {
		NamedElement namedElement = new NamedElement(NamedElementTest.namedElement);
		assertEquals(namedElement.getName(), NamedElementTest.namedElement.getName());
	}
	
	@Test
	public void testGetDefinition() {
		NamedElement namedElement = new NamedElement(NamedElementTest.namedElement);
		assertEquals(namedElement.getDefinition(), NamedElementTest.namedElement.getDefinition());
	}
	
	@Test
	public void testGetStereotypes() {
		NamedElement namedElement = new NamedElement(NamedElementTest.namedElement);
		String[] stereotypes = NamedElementTest.namedElement.getStereotypes();
		for (int i = 0; i < stereotypes.length; i++) {
			assertEquals(namedElement.getStereotypes()[i], stereotypes[i]);
		}
	}
	
}
