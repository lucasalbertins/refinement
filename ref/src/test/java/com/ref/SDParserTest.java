package com.ref;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.hooks.service.FindHook;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IModel;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.ISequenceDiagram;
import com.change_vision.jude.api.inf.project.ModelFinder;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate.UnExpectedException;

import JP.co.esm.caddies.jomt.jview.ex;

public class SDParserTest {

	private static ISequenceDiagram seq1;
	private static ISequenceDiagram seq2;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			
            ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
            projectAccessor.open("src/test/resources/testRef.asta");
            INamedElement[] findSequence = findSequence(projectAccessor);
            seq1 = (ISequenceDiagram)findSequence[0];
            seq2 = (ISequenceDiagram)findSequence[1];
        } catch (ProjectNotFoundException e) {
        		System.out.println("aqui");
            e.printStackTrace();
        } catch (Exception e) {
        		e.printStackTrace();
        }
        
	}
	
	private static INamedElement[] findSequence(ProjectAccessor projectAccessor) throws ProjectNotFoundException {
		INamedElement[] foundElements = projectAccessor
				.findElements(new ModelFinder() {
					public boolean isTarget(INamedElement namedElement) {
						return namedElement instanceof ISequenceDiagram;
					}
				});
		return foundElements;
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		AstahAPI.getAstahAPI().getProjectAccessor().close();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test 
	public void testDefineTypes() {
		SDParser parser = new SDParser(seq1, seq2);
		String process = "";
		String parsed = parser.defineTypes();
		StringBuilder expected = new StringBuilder();
		expected.append("SDnat = {0,1}\n");
		expected.append("datatype COM = s | r\n");
		expected.append("ID = {<id_A>, <id_B>} \n");
		
	}

	@Test
	public void testParseHeader() {
		SDParser parser = new SDParser(seq1, seq2);
		String process = "";
		parser.parseHeader();
		StringBuilder expected = new StringBuilder();
		expected.append("SDnat = {0}\n");
		expected.append("channel\n");
		expected.append("\tjoin, break, interrupt: SDnat\n");
		expected.append("\tjoin, break, interrupt: SDnat\n");
	}

}
