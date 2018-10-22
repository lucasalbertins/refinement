package com.ref;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.*;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.BasicModelEditor;
import com.change_vision.jude.api.inf.editor.ModelEditorFactory;
import com.change_vision.jude.api.inf.editor.SequenceDiagramEditor;
import com.change_vision.jude.api.inf.editor.TransactionManager;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.exception.LicenseNotFoundException;
import com.change_vision.jude.api.inf.exception.ProjectLockedException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IMessage;
import com.change_vision.jude.api.inf.model.IModel;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IOperation;
import com.change_vision.jude.api.inf.model.ISequenceDiagram;
import com.change_vision.jude.api.inf.presentation.ILinkPresentation;
import com.change_vision.jude.api.inf.presentation.INodePresentation;
import com.change_vision.jude.api.inf.presentation.IPresentation;
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
			// buildCounterExample(projectAccessor);

			if ((findSequence[0]).getName().equals("Seq0")) {
				seq1 = (ISequenceDiagram) findSequence[0];
				seq2 = (ISequenceDiagram) findSequence[1];
			} else {
				seq1 = (ISequenceDiagram) findSequence[1];
				seq2 = (ISequenceDiagram) findSequence[0];
			}

			parser = new SDParser(seq1, seq2);
			parser.parseSDs();

		} catch (ProjectNotFoundException e) {
			//System.out.println("aqui");
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
	public void testDefineTypes() throws InvalidEditingException, IOException {
		String actual = parser.getDefinedTypes();
		//System.out.println(actual);
		StringBuilder expected = new StringBuilder();
		// expected.append("SDnat = {1,2,1r}\n");
		expected.append("datatype COM = s | r\n");
		expected.append("datatype ID = lf1id|lf2id\n");
		expected.append("datatype ID_SD = sd1id|sd2id\n");
		//expected.append("MyInteger ={0,1,2,3,4,5,6,7,8,9}\n");
		//expected.append("MyString ={\"teste\"}\n");
		//expected.append("IntParams = {3}\n");
		//expected.append("DoubleParams = {2.5}\n");
		//expected.append("CharParams = {'a'}\n");
		expected.append("datatype MSG = m1|m0_I|m0_O\n");
		expected.append("subtype A_SIG = m1\n");
		expected.append("subtype B_OPS = m0_I | m0_O\n");
		//expected.append("subtype C_SIG = m2\n");
		expected.append("get_id(m1) = m1\n");
		expected.append("get_id(m0_I) = m0_I\n");
		expected.append("get_id(m0_O) = m0_O\n");
		//expected.append("get_id(m2) = m2\n");
		assertEquals(expected.toString(), actual);
	}

	@Test
	public void testParseChannels() throws IOException {
		String actual = parser.getChannels();
		//System.out.println(actual);
		StringBuilder expected = new StringBuilder();
		expected.append("channel beginInteraction,endInteraction\n");
		expected.append("channel A_mSIG: COM.ID.ID.A_SIG\n");
		expected.append("channel B_mOP: COM.ID.ID.B_OPS\n");
		//expected.append("channel C_mSIG: COM.ID.ID.C_SIG\n");
		assertEquals(expected.toString(), actual);
	}


	@Test
	public void testParseSD1() throws IOException {
		String actual = parser.getSd1Parse();
		//System.out.println(actual);
		StringBuilder expected = new StringBuilder();
		expected.append("Seq0_t_A(sd_id,lf1_id,lf2_id) =(B_mOP.s!lf1_id!lf2_id.m0_I -> SKIP);");
		expected.append("(B_mOP.r!lf2_id!lf1_id?out:{x | x <-B_OPS,(get_id(x) == m0_O)} -> SKIP)\n");

		expected.append(
				"Seq0_u_B(sd_id,lf1_id,lf2_id) =(B_mOP.r!lf1_id!lf2_id?oper:{x | x <- B_OPS,(get_id(x) == m0_I)} -> SKIP);");
		expected.append("(B_mOP.s!lf2_id!lf1_id.m0_O -> SKIP)\n");

		//expected.append(
		//		"Seq0_C(sd_id,lf2_id,lf3_id) =(C_mSIG.r!lf2_id!lf3_id?signal:{x | x <- C_SIG,(get_id(x) == m2)} -> SKIP)\n");

		expected.append(
				"Seq0_t_A_u_B_m0(sd_id,lf1_id,lf2_id) =B_mOP.s.lf1_id.lf2_id?x:{x | x<-B_OPS,get_id(x) == m0_I} -> B_mOP.r.lf1_id.lf2_id!x -> Seq0_t_A_u_B_m0(sd_id,lf1_id,lf2_id)\n");

		expected.append(
				"Seq0_u_B_t_A_m0_r(sd_id,lf2_id,lf1_id) = B_mOP.s.lf2_id.lf1_id?x:{x | x<-B_OPS,get_id(x) == m0_O} -> B_mOP.r.lf2_id.lf1_id!x -> Seq0_u_B_t_A_m0_r(sd_id,lf2_id,lf1_id)\n");

		//expected.append(
		//		"Seq0_m2(sd_id,lf2_id,lf3_id) = C_mSIG.s.lf2_id.lf3_id?x:{x | x<-C_SIG,get_id(x) == m2} -> C_mSIG.r.lf2_id.lf3_id!x -> Seq0_m2(sd_id,lf2_id,lf3_id)\n");

		expected.append(
				"Seq0_MessagesBuffer(sd_id,lf1_id,lf2_id) = (Seq0_t_A_u_B_m0(sd_id,lf1_id,lf2_id) ||| Seq0_u_B_t_A_m0_r(sd_id,lf2_id,lf1_id))/\\endInteraction -> SKIP\n");

		expected.append(
				"Seq0Parallel(sd_id,lf1_id,lf2_id) = Seq0_t_A(sd_id,lf1_id,lf2_id)[ {|B_mOP.s.lf1_id.lf2_id.m0_I, B_mOP.r.lf2_id.lf1_id.m0_O|}");
		expected.append(
				" || {|B_mOP.r.lf1_id.lf2_id.m0_I, B_mOP.s.lf2_id.lf1_id.m0_O|} ]Seq0_u_B(sd_id,lf1_id,lf2_id)");
//		expected.append(
//				"[ {|B_mOP.s.lf1_id.lf2_id.m0_I, B_mOP.r.lf2_id.lf1_id.m0_O, B_mOP.r.lf1_id.lf2_id.m0_I, B_mOP.s.lf2_id.lf1_id.m0_O|}");
		expected.append("\n");

		expected.append(
				"SD_Seq0(sd_id,lf1_id,lf2_id) = beginInteraction ->((Seq0Parallel(sd_id,lf1_id,lf2_id); endInteraction -> SKIP)");
		expected.append(
				"[|{|B_mOP.s.lf1_id.lf2_id.m0_I,B_mOP.r.lf1_id.lf2_id.m0_I,B_mOP.s.lf2_id.lf1_id.m0_O,B_mOP.r.lf2_id.lf1_id.m0_O,endInteraction|}|]Seq0_MessagesBuffer(sd_id,lf1_id,lf2_id))");
		assertEquals(expected.toString(), actual);
	}

	@Test
	public void testParseSD2() throws IOException {
		String actual = parser.getSd2Parse();
		//System.out.println(actual);
		StringBuilder expected = new StringBuilder();
		expected.append("Seq1_x_A(sd_id,lf1_id,lf2_id) =(B_mOP.s!lf1_id!lf2_id.m0_I -> SKIP);");
		expected.append(
				"(B_mOP.r!lf2_id!lf1_id?out:{x | x <-B_OPS,(get_id(x) == m0_O)} -> SKIP);(B_mOP.s!lf1_id!lf2_id.m0_I -> SKIP);(A_mSIG.r!lf2_id!lf1_id?signal:{x | x <- A_SIG,(get_id(x) == m1)} -> SKIP)\n");

		expected.append(
				"Seq1_y_B(sd_id,lf1_id,lf2_id) =(B_mOP.r!lf1_id!lf2_id?oper:{x | x <- B_OPS,(get_id(x) == m0_I)} -> SKIP);");
		expected.append("(B_mOP.s!lf2_id!lf1_id.m0_O -> SKIP);(B_mOP.r!lf1_id!lf2_id?oper:{x | x <- B_OPS,(get_id(x) == m0_I)} -> SKIP);(A_mSIG.s!lf2_id!lf1_id.m1 -> SKIP)\n");

		expected.append(
				"Seq1_x_A_y_B_m0(sd_id,lf1_id,lf2_id) =B_mOP.s.lf1_id.lf2_id?x:{x | x<-B_OPS,get_id(x) == m0_I} -> B_mOP.r.lf1_id.lf2_id!x -> Seq1_x_A_y_B_m0(sd_id,lf1_id,lf2_id)\n");

		expected.append(
				"Seq1_y_B_x_A_m1(sd_id,lf2_id,lf1_id) = A_mSIG.s.lf2_id.lf1_id?x:{x | x<-A_SIG,get_id(x) == m1} -> A_mSIG.r.lf2_id.lf1_id!x -> Seq1_y_B_x_A_m1(sd_id,lf2_id,lf1_id)\n");

		expected.append(
				"Seq1_y_B_x_A_m0_r(sd_id,lf2_id,lf1_id) = B_mOP.s.lf2_id.lf1_id?x:{x | x<-B_OPS,get_id(x) == m0_O} -> B_mOP.r.lf2_id.lf1_id!x -> Seq1_y_B_x_A_m0_r(sd_id,lf2_id,lf1_id)\n");

		expected.append(
				"Seq1_MessagesBuffer(sd_id,lf1_id,lf2_id) = (Seq1_x_A_y_B_m0(sd_id,lf1_id,lf2_id) ||| Seq1_y_B_x_A_m1(sd_id,lf2_id,lf1_id) ||| Seq1_y_B_x_A_m0_r(sd_id,lf2_id,lf1_id))/\\endInteraction -> SKIP\n");

		expected.append(
				"Seq1Parallel(sd_id,lf1_id,lf2_id) = Seq1_x_A(sd_id,lf1_id,lf2_id)[ {|B_mOP.s.lf1_id.lf2_id.m0_I, A_mSIG.r.lf2_id.lf1_id.m1, B_mOP.r.lf2_id.lf1_id.m0_O|}");
		expected.append(
				" || {|B_mOP.r.lf1_id.lf2_id.m0_I, A_mSIG.s.lf2_id.lf1_id.m1, B_mOP.s.lf2_id.lf1_id.m0_O|} ]Seq1_y_B(sd_id,lf1_id,lf2_id)\n");

		expected.append(
				"SD_Seq1(sd_id,lf1_id,lf2_id) = beginInteraction ->((Seq1Parallel(sd_id,lf1_id,lf2_id); endInteraction -> SKIP)");
		expected.append(
				"[|{|B_mOP.s.lf1_id.lf2_id.m0_I,B_mOP.r.lf1_id.lf2_id.m0_I,A_mSIG.s.lf2_id.lf1_id.m1,A_mSIG.r.lf2_id.lf1_id.m1,B_mOP.s.lf2_id.lf1_id.m0_O,B_mOP.r.lf2_id.lf1_id.m0_O,endInteraction|}|]Seq1_MessagesBuffer(sd_id,lf1_id,lf2_id))");
		assertEquals(expected.toString(), actual);
	}

}
