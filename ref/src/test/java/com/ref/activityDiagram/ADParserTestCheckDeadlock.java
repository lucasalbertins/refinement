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
import com.ref.ui.FDR3LocationDialog;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import javax.swing.JFrame;

import static org.junit.Assert.assertEquals;

public class ADParserTestCheckDeadlock {

	public static IActivityDiagram ad;
	private static ADParser parser1;
	private static ADParser parser2;
	private static ADParser parser3;
	private static ADParser parser4;
	private static ADParser parser5;
	private static ADParser parser6;
	public static boolean loadClassFDR = false;
	
	@BeforeClass
	public static void GetDiagram() throws Exception {
		try {
			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/deadlock1.asta");
			INamedElement[] findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser1 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/deadlockFree1.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser2 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/deadlockFree2.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser3 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/deadlock2.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser4 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/deadlock3.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser5 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/activityDiagram/compilationFailed1.asta");
			findElements = findElements(projectAccessor);

			ad = (IActivityDiagram) findElements[0];
			
			parser6 = new ADParser(ad.getActivity(), ad.getName(), ad);
			
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
		if (!ADParserTestCheckDeterminism.loadClassFDR) {
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
	public void TestCheckDeadlock1() throws ParsingException {
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
			progressBar.setNewTitle("Checking deadlock");
			progressBar.setAssertion(0);
            actual = FdrWrapper.getInstance().checkDeadlock(uh + fs + "TempAstah" + fs + "teste1.csp", parser1, "deadlock1", progressBar);
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
	public void TestCheckDeadlock2() throws ParsingException {
		parser4.clearBuffer();
		String diagramCSP = parser4.parserDiagram();
		
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
			progressBar.setNewTitle("Checking deadlock");
			progressBar.setAssertion(0);
            actual = FdrWrapper.getInstance().checkDeadlock(uh + fs + "TempAstah" + fs + "teste2.csp", parser2, "deadlock2", progressBar);
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
	public void TestCheckDeadlock3() throws ParsingException {
		parser5.clearBuffer();
		String diagramCSP = parser5.parserDiagram();
		
		String fs = System.getProperty("file.separator");
		String uh = System.getProperty("user.home");
		File directory = new File(uh+fs+"TempAstah");
		directory.mkdirs();

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(uh + fs + "TempAstah" + fs + "teste3.csp", "UTF-8");
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
			progressBar.setNewTitle("Checking deadlock");
			progressBar.setAssertion(0);
            actual = FdrWrapper.getInstance().checkDeadlock(uh + fs + "TempAstah" + fs + "teste3.csp", parser5, "deadlock3", progressBar);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int expected = 2;

		assertEquals(expected, actual);
	}
	
	/*
	 * Teste de Check Deadlock Free
	 * */
	@Test
	public void TestCheckDeadlockFree1() throws ParsingException {
		parser2.clearBuffer();
		String diagramCSP = parser2.parserDiagram();
		
		String fs = System.getProperty("file.separator");
		String uh = System.getProperty("user.home");
		File directory = new File(uh+fs+"TempAstah");
		directory.mkdirs();

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(uh + fs + "TempAstah" + fs + "teste4.csp", "UTF-8");
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
			progressBar.setNewTitle("Checking deadlock");
			progressBar.setAssertion(0);
            actual = FdrWrapper.getInstance().checkDeadlock(uh + fs + "TempAstah" + fs + "teste4.csp", parser2, "deadlockFree1", progressBar);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int expected = 1;

		assertEquals(expected, actual);
	}
	
	/*
	 * Teste de Check Deadlock Free
	 * */
	@Test
	public void TestCheckDeadlockFree2() throws ParsingException {
		parser3.clearBuffer();
		String diagramCSP = parser3.parserDiagram();
		
		String fs = System.getProperty("file.separator");
		String uh = System.getProperty("user.home");
		File directory = new File(uh+fs+"TempAstah");
		directory.mkdirs();

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(uh + fs + "TempAstah" + fs + "teste5.csp", "UTF-8");
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
			progressBar.setNewTitle("Checking deadlock");
			progressBar.setAssertion(0);
            actual = FdrWrapper.getInstance().checkDeadlock(uh + fs + "TempAstah" + fs + "teste5.csp", parser3, "deadlockFree2", progressBar);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int expected = 1;

		assertEquals(expected, actual);
	}
	
	/*
	 * Teste de Check Failed Compilation
	 * */
	@Test
	public void TestCheckFailedCompilation1() throws ParsingException {
		parser6.clearBuffer();
		String diagramCSP = parser6.parserDiagram();
		
		String fs = System.getProperty("file.separator");
		String uh = System.getProperty("user.home");
		File directory = new File(uh+fs+"TempAstah");
		directory.mkdirs();

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(uh + fs + "TempAstah" + fs + "teste6.csp", "UTF-8");
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
			progressBar.setNewTitle("Checking deadlock");
			progressBar.setAssertion(0);
            actual = FdrWrapper.getInstance().checkDeadlock(uh + fs + "TempAstah" + fs + "teste6.csp", parser6, "FailedCompilation1", progressBar);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int expected = 3;

		assertEquals(expected, actual);
	}

}
