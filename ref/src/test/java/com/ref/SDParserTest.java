package com.ref;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.ILifeline;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.ISequenceDiagram;
import com.change_vision.jude.api.inf.project.ModelFinder;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.ref.parser.SDParser;

public class SDParserTest {

	private static SDParser parser;

	private static ISequenceDiagram seq1;
	private static ISequenceDiagram seq2;

	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {

			ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			projectAccessor.open("src/test/resources/testRef.asta");
			INamedElement[] findSequence = findSequence(projectAccessor);
			if (((ISequenceDiagram) findSequence[0]).getName().equals("Seq0")) {
				seq1 = (ISequenceDiagram) findSequence[0];
				seq2 = (ISequenceDiagram) findSequence[1];
			} else {
				seq1 = (ISequenceDiagram) findSequence[1];
				seq2 = (ISequenceDiagram) findSequence[0];
			}
			parser = new SDParser(seq1, seq2);
		} catch (ProjectNotFoundException e) {
			System.out.println("aqui");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

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
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	
	@Test 
	public void testDefineTypes() throws InvalidEditingException {
		String actual = parser.defineTypes();
		StringBuilder expected = new StringBuilder();
		//expected.append("SDnat = {1,2,1r}\n");
		expected.append("datatype COM = s | r\n");
		expected.append("ID1 = {");
		for (ILifeline lif : seq1.getInteraction().getLifelines()) {
			expected.append(lif.getId()).append(",");
		}
		expected.deleteCharAt(expected.length() - 1);
		expected.append("}\n");
		expected.append("ID2 = {");
		for (ILifeline lif : seq2.getInteraction().getLifelines()) {
			expected.append(lif.getId()).append(",");
		}
		expected.deleteCharAt(expected.length() - 1);
		expected.append("}\n");
		expected.append("ID_SD = {<").append(seq1.getId()).append(">,<");
		expected.append(seq2.getId()).append(">}\n");
		expected.append("MyInteger ={0,1,2,3,4,5,6,7,8,9}\n");
		expected.append("MyString ={\"teste\"}\n");
		expected.append("IntParams = {3}\n");
		expected.append("DoubleParams = {2.5}\n");
		expected.append("CharParams = {'a'}\n");
		expected.append("datatype A_SIG = m1\n");
		expected.append("datatype B_OPS = m0_I.MyInteger.MyString.IntParams.DoubleParams.CharParams | m0_O\n");
		expected.append("get_id(m0_I._._._._._) = m0_I\n");
		expected.append("get_id(m0_O) = m0_O\n");
		System.out.println(actual);
		//System.out.println(expected);
		assertEquals(expected.toString(),actual);
		//System.out.println(expected.toString());
		//System.out.println(actual);
	}

	@Ignore
	@Test
	public void testParseChannels() {
		String actual = parser.parseChannels();
		StringBuilder expected = new StringBuilder();
		expected.append("channel beginInteration,endInteraction\n");
		expected.append("channel B_mOP: COM.ID.ID.B_OPS\n");
		expected.append("channel A_mSIG: COM.ID.ID.A_SIG\n");
		assertEquals(expected.toString(), actual);
		System.out.println(actual);
	}
	
	@Test
	public void testParseSD1() {
		String actual = parser.parseSD1();
		System.out.println(actual);
		ILifeline lif1 = seq1.getInteraction().getLifelines()[0];
		ILifeline lif2 = seq1.getInteraction().getLifelines()[1];

		StringBuilder expected = new StringBuilder();
		// Lifeline A
		expected.append("Seq1_A(sd_id) = ");
		expected.append("(B_mOP.s.1." + lif1.getId() + "." + lif2.getId() + ".m0_I -> SKIP);");
		expected.append(
				"(B_mOP.r.1." + lif1.getId() + "." + lif2.getId() + "?out:{x | x <-B_OPS,(x == m0_O)} -> SKIP);");
		expected.append(
				"(A_mSIG.r.2." + lif2.getId() + "." + lif1.getId() + "?signal:{x | x <- A_SIG,(x == m1_S)} -> SKIP)\n");
		// Lifeline B
		expected.append("Seq1_B(sd_id) = ");
		expected.append(
				"(B_mOP.r.1." + lif1.getId() + "." + lif2.getId() + "?oper:{x | x <- B_OPS,(x == m0_I)} -> SKIP);");
		expected.append("(B_mOP.s.1." + lif1.getId() + "." + lif2.getId() + ".m0_O -> SKIP);");
		expected.append("(A_mSIG.s.2." + lif2.getId() + "." + lif1.getId() + ".m1_S -> SKIP)\n");
		// Message m0
		expected.append("Seq1_m0 = B_mOP.s.1." + lif1.getId() + "." + lif2.getId() + "?x -> ");
		expected.append("B_mOP.r.1." + lif1.getId() + "." + lif2.getId() + "!x -> Seq1_m0\n");
		// Reply of m0
		expected.append("Seq1_m0_r = B_mOP.s.1." + lif1.getId() + "." + lif2.getId() + "?x -> ");
		expected.append("B_mOP.r.1." + lif1.getId() + "." + lif2.getId() + "!x -> Seq1_m0_r\n");
		// Message m1
		expected.append("Seq1_m1 = A_mSIG.s.2." + lif2.getId() + "." + lif1.getId() + "?x -> ");
		expected.append("A_mSIG.r.2." + lif2.getId() + "." + lif1.getId() + "!x -> Seq1_m0\n");
		// MessagesBuffer
		expected.append("Seq1_MessagesBuffer = (Seq1_m0 ||| Seq1_m0_r ||| Seq1_m1) /\\ endInteraction -> SKIP\n");
		// Sequence Diagram Process
		expected.append("Seq1(sd_id) = (((beginInteraction -> ((");
		expected.append("Seq1_A(sd_id) [{|B_mOP.s.1." + lif1.getId() + "." + lif2.getId() + ".m0_I,");
		expected.append("B_mOP.r.1." + lif1.getId() + "." + lif2.getId() + ".m0_O,");
		expected.append("A_mSIG.r.2." + lif2.getId() + "." + lif1.getId() + ".m1_S|} || ");
		expected.append("{|B_mOP.r.1." + lif1.getId() + "." + lif2.getId() + ".m0_I,");
		expected.append("B_mOP.s.1." + lif1.getId() + "." + lif2.getId() + ".m0_O,");
		expected.append("A_mSIG.s.2." + lif2.getId() + "." + lif1.getId() + ".m1_S|}] Seq1_B(sd_id)");
		expected.append(");endInteraction -> SKIP [|{|endInteraction,B_mOP,A_mSIG|}|] Seq1_MessagesBuffer))))");
		//System.out.println(expected.toString());
		//assertEquals(expected.toString(), actual);
	}

	@Ignore
	@Test
	public void testParseSD2() {
		String actual = parser.parseSD1();
		ILifeline lif1 = seq1.getInteraction().getLifelines()[0];
		ILifeline lif2 = seq1.getInteraction().getLifelines()[1];

		StringBuilder expected = new StringBuilder();
		expected.append("Seq1_A(sd_id) = ");
		expected.append("(B_mOP.s.1." + lif1.getId() + "." + lif2.getId() + ".m0_I -> SKIP);");
		expected.append(
				"(B_mOP.r.1." + lif1.getId() + "." + lif2.getId() + "?out:{x | x <-B_OPS,(x == m0_O)} -> SKIP);");
		expected.append(
				"(A_mSIG.r.2." + lif2.getId() + "." + lif1.getId() + "?signal:{x | x <- A_SIG,(x == m1_S)} -> SKIP)\n");
		expected.append("Seq1_B(sd_id) = ");
		expected.append(
				"(B_mOP.r.1." + lif1.getId() + "." + lif2.getId() + "?oper:{x | x <- B_OPS,(x == m0_I)} -> SKIP);");
		expected.append("(B_mOP.s.1." + lif1.getId() + "." + lif2.getId() + ".m0_O -> SKIP);");
		expected.append("(A_mSIG.s.2." + lif2.getId() + "." + lif1.getId() + ".m1_S -> SKIP)\n");

		assertEquals(expected.toString(), actual);
	}

}
