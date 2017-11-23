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
			projectAccessor.open("src/test/resources/testRef3.asta");
			INamedElement[] findSequence = findSequence(projectAccessor);
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
		expected.append("datatype ID = lf1id|lf2id|lf3id\n");
		expected.append("datatype ID_SD = sd1id|sd2id\n");
		expected.append("MyInteger ={0,1,2,3,4,5,6,7,8,9}\n");
		expected.append("MyString ={\"teste\"}\n");
		expected.append("IntParams = {3}\n");
		expected.append("DoubleParams = {2.5}\n");
		expected.append("CharParams = {'a'}\n");
		expected.append("datatype MSG = m1|m0_I|m0_O|m2\n");
		expected.append("subtype A_SIG = m1\n");	
		expected.append("subtype B_OPS = m0_I | m0_O\n");
		expected.append("subtype C_SIG = m2\n");
		expected.append("get_id(m1) = m1\n");
		expected.append("get_id(m0_I) = m0_I\n");
		expected.append("get_id(m0_O) = m0_O\n");
		expected.append("get_id(m2) = m2\n");
		System.out.println(actual);
		//System.out.println(expected);
		assertEquals(expected.toString(),actual);
		//System.out.println(expected.toString());
		//System.out.println(actual);
	}

	@Test
	public void testParseChannels() {
		String actual = parser.parseChannels();
		System.out.println(actual);
		StringBuilder expected = new StringBuilder();
		expected.append("channel beginInteraction,endInteraction\n");
		expected.append("channel A_mSIG: COM.ID.ID.A_SIG\n");
		expected.append("channel B_mOP: COM.ID.ID.B_OPS\n");
		expected.append("channel C_mSIG: COM.ID.ID.C_SIG\n");
		assertEquals(expected.toString(), actual);
	}

	@Test
	public void testParseSD1() {
		String actual = parser.parseSD1();
		System.out.println(actual);
		StringBuilder expected = new StringBuilder();
		expected.append("Seq0_A(sd_id,lf1_id,lf2_id) =(B_mOP.s!lf1_id!lf2_id.m0_I -> SKIP);");
		expected.append("(B_mOP.r!lf2_id!lf1_id?out:{x | x <-B_OPS,(get_id(x) == m0_O)} -> SKIP)\n");

		expected.append("Seq0_B(sd_id,lf1_id,lf2_id,lf3_id) =(B_mOP.r!lf1_id!lf2_id?oper:{x | x <- B_OPS,(get_id(x) == m0_I)} -> SKIP);");
		expected.append("(B_mOP.s!lf2_id!lf1_id.m0_O -> SKIP);(C_mSIG.s!lf2_id!lf3_id.m2 -> SKIP)\n");

		expected.append("Seq0_C(sd_id,lf2_id,lf3_id) =(C_mSIG.r!lf2_id!lf3_id?signal:{x | x <- C_SIG,(get_id(x) == m2)} -> SKIP)\n");

		expected.append("Seq0_m0(sd_id,lf1_id,lf2_id) =B_mOP.s.lf1_id.lf2_id?x:{x | x<-B_OPS,get_id(x) == m0_I} -> B_mOP.r.lf1_id.lf2_id!x -> Seq0_m0(sd_id,lf1_id,lf2_id)\n");

		expected.append("Seq0_m0_r(sd_id,lf2_id,lf1_id) = B_mOP.s.lf2_id.lf1_id?x:{x | x<-B_OPS,get_id(x) == m0_O} -> B_mOP.r.lf2_id.lf1_id!x -> Seq0_m0_r(sd_id,lf2_id,lf1_id)\n");

		expected.append("Seq0_m2(sd_id,lf2_id,lf3_id) = C_mSIG.s.lf2_id.lf3_id?x:{x | x<-C_SIG,get_id(x) == m2} -> C_mSIG.r.lf2_id.lf3_id!x -> Seq0_m2(sd_id,lf2_id,lf3_id)\n");

		expected.append("Seq0_MessagesBuffer(sd_id,lf1_id,lf2_id,lf3_id) = (Seq0_m0(sd_id,lf1_id,lf2_id) ||| Seq0_m0_r(sd_id,lf2_id,lf1_id) ||| Seq0_m2(sd_id,lf2_id,lf3_id))/\\endInteraction -> SKIP\n");

		expected.append("Seq0Parallel(sd_id,lf1_id,lf2_id,lf3_id) = (Seq0_A(sd_id,lf1_id,lf2_id)[ {|B_mOP.s.lf1_id.lf2_id, B_mOP.r.lf2_id.lf1_id|}");
		expected.append(" || {|B_mOP.r.lf1_id.lf2_id, B_mOP.s.lf2_id.lf1_id, C_mSIG.s.lf2_id.lf3_id|} ]Seq0_B(sd_id,lf1_id,lf2_id,lf3_id))");
		expected.append("[ {|B_mOP.s.lf1_id.lf2_id, B_mOP.r.lf2_id.lf1_id, B_mOP.r.lf1_id.lf2_id, B_mOP.s.lf2_id.lf1_id, C_mSIG.s.lf2_id.lf3_id|}");
		expected.append(" || {|C_mSIG.r.lf2_id.lf3_id|} ]Seq0_C(sd_id,lf2_id,lf3_id)\n");

		expected.append("SD(sd_id,lf1_id,lf2_id,lf3_id) = beginInteraction ->((Seq0Parallel(sd_id,lf1_id,lf2_id,lf3_id); endInteraction -> SKIP)");
		expected.append("[|{|B_mOP.s.lf1_id.lf2_id,B_mOP.r.lf1_id.lf2_id,B_mOP.s.lf2_id.lf1_id,B_mOP.r.lf2_id.lf1_id,C_mSIG.s.lf2_id.lf3_id,C_mSIG.r.lf2_id.lf3_id,endInteraction|}|]Seq0_MessagesBuffer(sd_id,lf1_id,lf2_id,lf3_id))");
		assertEquals(expected.toString(), actual);
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
