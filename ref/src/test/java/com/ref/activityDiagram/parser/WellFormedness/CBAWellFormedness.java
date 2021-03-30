package com.ref.activityDiagram.parser.WellFormedness;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IActivityDiagram;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.project.ModelFinder;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.ref.exceptions.FDRException;
import com.ref.exceptions.ParsingException;
import com.ref.exceptions.WellFormedException;
import com.ref.traceability.activityDiagram.ActivityController;
import com.ref.traceability.activityDiagram.ActivityController.VerificationType;
import com.ref.ui.CheckingProgressBar;

public class CBAWellFormedness {
	public static IActivityDiagram ad,ad3;
	 
	@BeforeClass
	public static void getDiagram() throws  Exception,ProjectNotFoundException {
		ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
		
		projectAccessor.open("src/test/resources/activityDiagram/behavior1.asta");
		INamedElement[] findElements = findElements(projectAccessor);
		ad = (IActivityDiagram) findElements[0];
	
		projectAccessor.open("src/test/resources/activityDiagram/behavior1UnspecifiedCBA.asta");
		INamedElement[] findElements3 = findElements(projectAccessor);
		ad3 = (IActivityDiagram) findElements3[0];
	}
	private static INamedElement[] findElements(ProjectAccessor projectAccessor) throws ProjectNotFoundException {
		INamedElement[] foundElements = projectAccessor.findElements(new ModelFinder() {
			public boolean isTarget(INamedElement namedElement) {
				return namedElement instanceof IActivityDiagram;
			}
		});
		return foundElements;
	}
	@Test
	public void testControl() throws FileNotFoundException, UnsupportedEncodingException, FDRException, ParsingException, IOException {
		try {
			CheckingProgressBar progressBar = new CheckingProgressBar();
			progressBar.setNewTitle("Checking deadlock");
			progressBar.setAssertion(0);
			
			ActivityController.getInstance().AstahInvocation(ad, VerificationType.DEADLOCK, progressBar);
			
		}  catch (WellFormedException e) {
			fail("should not throw WellFormed Exception");		
		}
		
	}

	@Test
	public void testUnspecifiedCBA() throws FileNotFoundException, UnsupportedEncodingException, FDRException, ParsingException, IOException {
		try {
			CheckingProgressBar progressBar = new CheckingProgressBar();
			progressBar.setNewTitle("Checking deadlock");
			progressBar.setAssertion(0);
			
			ActivityController.getInstance().AstahInvocation(ad3, VerificationType.DEADLOCK, progressBar);
		}
		 catch (WellFormedException e) {
			assertEquals(e.getMessage(),"There's no Activity related to the Call Behaviour Action");
		} 
	}
	
}
