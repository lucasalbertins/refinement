package com.ref.activityDiagram;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IActivityDiagram;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.project.ModelFinder;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.ref.exceptions.ParsingException;
import com.ref.fdr.FdrWrapper;
import com.ref.parser.activityDiagram.ADParser;
import com.ref.ui.CheckingProgressBar;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.net.MalformedURLException;

import static org.junit.Assert.assertEquals;

public class ADParserTestCheckDeterminism {

	public static IActivityDiagram ad;
	private static ADParser parser1;
	private static ADParser parser2;
	public static boolean loadClassFDR = false;
	
	@BeforeClass
	public static void GetDiagram() throws Exception {
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/nonDeterminism1.asta");
			INamedElement[] findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser1 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/nonDeterminism2.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser2 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
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
		String fs = System.getProperty("file.separator");
		String uh = System.getProperty("user.home");
		File directory = new File(uh+fs+"TempAstah");
		delete(directory);
		AstahAPI.getAstahAPI().getProjectAccessor().close();
	}
	
	@BeforeClass
	public static void loadClassesFDR() {
		if (!ADParserTestCheckDeadlock.loadClassFDR) {
			FdrWrapper wrapper = FdrWrapper.getInstance();
			String os = System.getProperty("os.name").toLowerCase();
			if (os.indexOf("win") >= 0) {
				wrapper.loadFDR("C:\\Program Files\\FDR\\bin\\fdr.jar");
			} else if (os.indexOf("mac") >= 0) {
				wrapper.loadFDR("/Applications/FDR4.app/Contents/Frameworks/fdr.jar");
			}
			try {
				wrapper.loadClasses();
			} catch (MalformedURLException | ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			loadClassFDR = true;
		}
	}
	
	public static void delete(File f) throws IOException {
		  if (f.isDirectory()) {
		    for (File c : f.listFiles())
		      delete(c);
		  }
		  if (!f.delete())
		    throw new FileNotFoundException("Failed to delete file: " + f);
	}
	
	
	/*
	 * Teste de Check Deadlock
	 * */
	@Test
	public void TestCheckDeterminism1() throws ParsingException {
		parser1.clearBuffer();
		String diagramCSP = parser1.parserDiagram();
		
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
			CheckingProgressBar progressBar = new CheckingProgressBar();
			progressBar.setNewTitle("Checking non-determinism");
			progressBar.setAssertion(0);
            actual = FdrWrapper.getInstance().checkDeterminism(uh + fs + "TempAstah" + fs + "teste1.csp", parser1, "Determinism1", progressBar);
        } catch (Exception e) {
            e.printStackTrace();
        }
		int expected = 2;

		assertEquals(expected, actual);
	}
	
	/*
	 * Teste de Check Deadlock
	 * */
	@Test
	public void TestCheckDeterminism2() throws ParsingException {
		parser2.clearBuffer();
		String diagramCSP = parser2.parserDiagram();
		
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
			CheckingProgressBar progressBar = new CheckingProgressBar();
			progressBar.setNewTitle("Checking non-determinism");
			progressBar.setAssertion(0);
            actual = FdrWrapper.getInstance().checkDeterminism(uh + fs + "TempAstah" + fs + "teste2.csp", parser2, "Determinism2", progressBar);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int expected = 2;

		assertEquals(expected, actual);
	}
	
}
