package com.ref;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.plaf.FileChooserUI;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.ISequenceDiagram;
import com.change_vision.jude.api.inf.project.ModelFinder;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.ref.parser.SDParser;

public class FdrTest {

	private static SDParser parser;
	private static ISequenceDiagram seq1;
	private static ISequenceDiagram seq2;
	private static BufferedWriter bw;
	private static FileWriter fw;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			fw = new FileWriter(new File("resultado.csp"));
			bw = new BufferedWriter(fw);

			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/testRef3.asta");
			INamedElement[] findSequence = findSequence(projectAccessor);
			// createSD(projectAccessor);

			if (((ISequenceDiagram) findSequence[0]).getName().equals("Seq0")) {
				seq1 = (ISequenceDiagram) findSequence[0];
				seq2 = (ISequenceDiagram) findSequence[1];
			} else {
				seq1 = (ISequenceDiagram) findSequence[1];
				seq2 = (ISequenceDiagram) findSequence[0];
			}
			parser = new SDParser(seq1, seq2);
			parser.carregaLifelines();
		} catch (ProjectNotFoundException e) {
			System.out.println("aqui");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void refinementAssertion() throws InvalidEditingException {
		parser.defineTypes();
		parser.parseChannels();
		parser.parseSDs();
		String actual = parser.refinementAssertion();
		String expected = "assert SD_Seq0(sd1id,lf1id,lf2id,lf3id) [T= SD_Seq1(sd2id,lf1id,lf2id)\\{}";
		assertEquals(expected,actual);
	}
	
	@Ignore
	@Test
	public void gerarArquivo() throws InvalidEditingException, IOException {

		String actual = parser.defineTypes();
		bw.write(actual);
		bw.newLine();
		actual = parser.parseChannels();
		bw.write(actual);
		bw.newLine();
		actual = parser.parseSD(seq1);
		bw.write(actual);
		bw.newLine();
		bw.newLine();
		actual = parser.parseSD(seq2);
		bw.write(actual);

		bw.close();
		fw.close();

		FileReader fr = new FileReader("resultado.csp");
		BufferedReader br = new BufferedReader(fr);
		StringBuilder sb1 = new StringBuilder();

		String linha = br.readLine();
		while (linha != null) {
			sb1.append(linha);
		}

		System.out.println(sb1.toString());

		br.close();
		fr.close();

		// assertEquals(sb2.toString(), sb1.toString());

	}

	@Ignore
	@Test
	public void fdrTest() {
		FdrManager fdr = new FdrManager();
		fdr.verify("result");
	}

	private static INamedElement[] findSequence(ProjectAccessor projectAccessor) throws ProjectNotFoundException {
		INamedElement[] foundElements = projectAccessor.findElements(new ModelFinder() {
			public boolean isTarget(INamedElement namedElement) {
				return namedElement instanceof ISequenceDiagram;
			}
		});
		return foundElements;
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		AstahAPI.getAstahAPI().getProjectAccessor().close();
		bw.close();
		fw.close();
	}

}
