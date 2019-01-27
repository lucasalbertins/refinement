package com.ref.activityDiagram;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IActivityDiagram;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.project.ModelFinder;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.ref.fdr.FdrWrapper;
import com.ref.parser.activityDiagram.ADParser;
import com.ref.ui.FDR3LocationDialog;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import javax.swing.JFrame;

import static org.junit.Assert.assertEquals;

public class ADParserTestCheckDeadlock {

	public static IActivityDiagram ad;
	private static ADParser parser1;
	private static ADParser parser2;
	
	@BeforeClass
	public static void GetDiagram() throws Exception {
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/deadlock1.asta");
			INamedElement[] findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser1 = new ADParser(ad.getActivity(), ad.getName());
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/deadklockFree1.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser2 = new ADParser(ad.getActivity(), ad.getName());
			
		} catch (ProjectNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private static INamedElement[] findElements(ProjectAccessor projectAccessor) throws ProjectNotFoundException {
		INamedElement[] foundElements = projectAccessor.findElements(new ModelFinder() {
			public boolean isTarget(INamedElement namedElement) {
				return namedElement instanceof IActivityDiagram;
			}
		});
		return foundElements;
	}
	
	@Before
	public void clearBuffer() {
		
		
	}
	
	@AfterClass
	public static void CloseProject() throws Exception {
		AstahAPI.getAstahAPI().getProjectAccessor().close();
	}
	
	/*
	 * Teste de Tradução TokenManager
	 * */
	@Test
	public void TestCheckDeadlock1() {
		parser1.clearBuffer();
		String diagramCSP = parser1.parserDiagram();
		
		FdrWrapper wrapper = FdrWrapper.getInstance();
		wrapper.loadFDR("C:\\Program Files\\FDR\\bin\\fdr.jar");
		try {
			wrapper.loadClasses();
		} catch (MalformedURLException | ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String fs = System.getProperty("file.separator");
		String uh = System.getProperty("user.home");
		File directory = new File(uh+fs+"TempAstah");
		directory.mkdirs();

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(uh + fs + "TempAstah" + fs + "teste1.csp", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        writer.print(diagramCSP);
		writer.flush();
		writer.close();

        int actual = -1;
        try {
            actual = FdrWrapper.getInstance().checkDeadlock(uh + fs + "TempAstah" + fs + "teste1.csp", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
		int expected = 1;

		assertEquals(expected, actual);
	}
	
	/*
	 * Teste de Tradução TokenManager
	 * */
	@Test
	public void TestCheckDeadlock2() {
		parser2.clearBuffer();
		String diagramCSP = parser2.parserDiagram();

		FdrWrapper wrapper = FdrWrapper.getInstance();
		wrapper.loadFDR("C:\\Program Files\\FDR\\bin\\fdr.jar");
		try {
			wrapper.loadClasses();
		} catch (MalformedURLException | ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String fs = System.getProperty("file.separator");
		String uh = System.getProperty("user.home");
		File directory = new File(uh+fs+"TempAstah");
		directory.mkdirs();

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(uh + fs + "TempAstah" + fs + "teste2.csp", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        writer.print(diagramCSP);
		writer.flush();
		writer.close();

        int actual = -1;
        try {
            actual = FdrWrapper.getInstance().checkDeadlock(uh + fs + "TempAstah" + fs + "teste2.csp", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int expected = 0;

		assertEquals(expected, actual);
	}
	
}
