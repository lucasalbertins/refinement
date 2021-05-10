/**
 * 
 */
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

/**
 * @author gabri
 *
 */

public class ObjectFlowWellFormedness {
	 public static IActivityDiagram ad,ad2,ad3;
	 
	@BeforeClass
	public static void getDiagram() throws  Exception,ProjectNotFoundException {
		ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
		projectAccessor.open("src/test/resources/activityDiagram/submitData2.asta");
		INamedElement[] findElements = findElements(projectAccessor);
		ad = (IActivityDiagram) findElements[0];
		projectAccessor.open("src/test/resources/activityDiagram/submitData2controlNodeTest.asta");
		INamedElement[] findElements2 = findElements(projectAccessor);
		ad2 = (IActivityDiagram) findElements2[0];
		projectAccessor.open("src/test/resources/activityDiagram/submitData2ConflictingDataType.asta");
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
	public void testControlNode() throws FileNotFoundException, UnsupportedEncodingException, FDRException, ParsingException, IOException {
		try {
			CheckingProgressBar progressBar = new CheckingProgressBar();
			progressBar.setNewTitle("Checking deadlock");
			progressBar.setAssertion(0);
			
			ActivityController.getInstance().AstahInvocation(ad2, VerificationType.DEADLOCK, progressBar);
			
		} catch (WellFormedException e) {
			fail("should not throw WellFormed Exception"+e.getMessage());		
		}
		
	}
	@Test
	public void testControl() throws FileNotFoundException, UnsupportedEncodingException, FDRException, ParsingException, IOException {
		try {
			CheckingProgressBar progressBar = new CheckingProgressBar();
			progressBar.setNewTitle("Checking deadlock");
			progressBar.setAssertion(0);
			
			ActivityController.getInstance().AstahInvocation(ad, VerificationType.DEADLOCK, progressBar);
			
		} catch (WellFormedException e) {
			fail("should not throw WellFormed Exception");		
		}
		
	}

	@Test
	public void testConflictingType() throws FileNotFoundException, UnsupportedEncodingException, FDRException, ParsingException, IOException {
		try {
			CheckingProgressBar progressBar = new CheckingProgressBar();
			progressBar.setNewTitle("Checking deadlock");
			progressBar.setAssertion(0);
			
			ActivityController.getInstance().AstahInvocation(ad3, VerificationType.DEADLOCK, progressBar);
			
		}catch (WellFormedException e) {
			assertEquals(e.getMessage(),"The source node DataSize and the target size have conflicting data types");
		} 
		
	}
}
