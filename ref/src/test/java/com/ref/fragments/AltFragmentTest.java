package com.ref.fragments;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.ISequenceDiagram;
import com.change_vision.jude.api.inf.project.ModelFinder;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.ref.parser.SDParser;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class AltFragmentTest {

    private static SDParser parser;
    private static ISequenceDiagram seq1;
    private static ISequenceDiagram seq2;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        try {

            ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
            projectAccessor.open("src/test/resources/oldTest.asta");
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

    @Test
    public void channelsWithFragment() {
        String actual = parser.getChannels();

        String expected = "Hidden = {|alt2, alt1|}\nchannel beginInteraction,endInteraction\nchannel A_mSIG: COM.ID.ID.A_SIG\nchannel B_mOP: COM.ID.ID.B_OPS\nchannel B_mSIG: COM.ID.ID.B_SIG\nchannel alt1: Bool.Bool\nchannel alt2: Bool.Bool\n";
        assertEquals(expected, actual);
    }

    @Test
    public void testParseSD1(){
        String actual = parser.getSd1Parse();
        StringBuilder expected1 = new StringBuilder();
        expected1.append("Seq0_t_A(sd_id,lf1_id,lf2_id) =(B_mOP.s!lf1_id!lf2_id.m0_I -> SKIP);");
        expected1.append("(B_mOP.r!lf2_id!lf1_id?out:{x | x <-B_OPS,(get_id(x) == m0_O)} -> SKIP);\n");
        expected1.append("alt1?g1?g2 -> (g1 & (A_mSIG.r!lf2_id!lf1_id?signal:{x | x <- A_SIG,(get_id(x) == m1)} -> SKIP)\n");
        expected1.append("[]\n");
        expected1.append("g2 & (B_mOP.s!lf1_id!lf2_id.m2_I -> SKIP);(B_mOP.r!lf2_id!lf1_id?out:{x | x <-B_OPS,(get_id(x) == m2_O)} -> SKIP))\n");
        expected1.append("Seq0_u_B(sd_id,lf1_id,lf2_id) =(B_mOP.r!lf1_id!lf2_id?oper:{x | x <- B_OPS,(get_id(x) == m0_I)} -> SKIP);");
        expected1.append("(B_mOP.s!lf2_id!lf1_id.m0_O -> SKIP);\n");
        expected1.append("alt1?g1?g2 -> (g1 & (A_mSIG.s!lf2_id!lf1_id.m1 -> SKIP)\n");
        expected1.append("[]\n");
        expected1.append("g2 & (B_mOP.r!lf1_id!lf2_id?oper:{x | x <- B_OPS,(get_id(x) == m2_I)} -> SKIP);");
        expected1.append("(B_mOP.s!lf2_id!lf1_id.m2_O -> SKIP))\n");
        expected1.append("Seq0_t_A_u_B_m0(sd_id,lf1_id,lf2_id) = B_mOP.s.lf1_id.lf2_id?x:{x | x<-B_OPS,get_id(x) == m0_I}");
        expected1.append(" -> B_mOP.r.lf1_id.lf2_id!x -> Seq0_t_A_u_B_m0(sd_id,lf1_id,lf2_id)\n");
        expected1.append("Seq0_u_B_t_A_m0_r(sd_id,lf2_id,lf1_id) = B_mOP.s.lf2_id.lf1_id?x:{x | x<-B_OPS,get_id(x) == m0_O}");
        expected1.append(" -> B_mOP.r.lf2_id.lf1_id!x -> Seq0_u_B_t_A_m0_r(sd_id,lf2_id,lf1_id)\n");
        expected1.append("Seq0_t_A_u_B_m2(sd_id,lf1_id,lf2_id) = B_mOP.s.lf1_id.lf2_id?x:{x | x<-B_OPS,get_id(x) == m2_I}");
        expected1.append(" -> B_mOP.r.lf1_id.lf2_id!x -> Seq0_t_A_u_B_m2(sd_id,lf1_id,lf2_id)\n");
        expected1.append("Seq0_u_B_t_A_m2_r(sd_id,lf2_id,lf1_id) = B_mOP.s.lf2_id.lf1_id?x:{x | x<-B_OPS,get_id(x) == m2_O}");
        expected1.append(" -> B_mOP.r.lf2_id.lf1_id!x -> Seq0_u_B_t_A_m2_r(sd_id,lf2_id,lf1_id)\n");
        expected1.append("Seq0_u_B_t_A_m1(sd_id,lf2_id,lf1_id) = A_mSIG.s.lf2_id.lf1_id?x:{x | x<-A_SIG,get_id(x) == m1}");
        expected1.append(" -> A_mSIG.r.lf2_id.lf1_id!x -> Seq0_u_B_t_A_m1(sd_id,lf2_id,lf1_id)\n");
        expected1.append("Seq0_MessagesBuffer(sd_id,lf1_id,lf2_id) = (Seq0_t_A_u_B_m0(sd_id,lf1_id,lf2_id) ||| Seq0_u_B_t_A_m0_r(sd_id,lf2_id,lf1_id)");
        expected1.append(" ||| Seq0_t_A_u_B_m2(sd_id,lf1_id,lf2_id) ||| Seq0_u_B_t_A_m2_r(sd_id,lf2_id,lf1_id)");
        expected1.append(" ||| Seq0_u_B_t_A_m1(sd_id,lf2_id,lf1_id))/\\endInteraction -> SKIP\n");
        expected1.append("Seq0Parallel(sd_id,lf1_id,lf2_id) = Seq0_t_A(sd_id,lf1_id,lf2_id)");
        expected1.append("[ {|alt1,B_mOP.s.lf1_id.lf2_id.m0_I, B_mOP.r.lf2_id.lf1_id.m0_O, B_mOP.s.lf1_id.lf2_id.m2_I, B_mOP.r.lf2_id.lf1_id.m2_O, A_mSIG.r.lf2_id.lf1_id.m1|}");
        expected1.append(" || {|alt1,B_mOP.r.lf1_id.lf2_id.m0_I, B_mOP.s.lf2_id.lf1_id.m0_O, B_mOP.r.lf1_id.lf2_id.m2_I, B_mOP.s.lf2_id.lf1_id.m2_O, A_mSIG.s.lf2_id.lf1_id.m1|} ]Seq0_u_B(sd_id,lf1_id,lf2_id)\\Hidden\n");
        expected1.append("SD_Seq0(sd_id,lf1_id,lf2_id) = beginInteraction ->((Seq0Parallel(sd_id,lf1_id,lf2_id); endInteraction -> SKIP)[|{|B_mOP.s.lf1_id.lf2_id.m0_I,B_mOP.r.lf1_id.lf2_id.m0_I,B_mOP.s.lf2_id.lf1_id.m0_O,");
        expected1.append("B_mOP.r.lf2_id.lf1_id.m0_O,B_mOP.s.lf1_id.lf2_id.m2_I,B_mOP.r.lf1_id.lf2_id.m2_I,B_mOP.s.lf2_id.lf1_id.m2_O,B_mOP.r.lf2_id.lf1_id.m2_O,A_mSIG.s.lf2_id.lf1_id.m1,A_mSIG.r.lf2_id.lf1_id.m1,endInteraction|}|]Seq0_MessagesBuffer(sd_id,lf1_id,lf2_id))");
//

        assertEquals(expected1.toString(),actual);
    }
    @Test
    public void testParseSD2(){
        String actual = parser.getSd2Parse();
        StringBuilder expected1 = new StringBuilder();
        expected1.append("Seq1_x_A(sd_id,lf1_id,lf2_id) =(B_mOP.s!lf1_id!lf2_id.m0_I -> SKIP);");
        expected1.append("(B_mOP.r!lf2_id!lf1_id?out:{x | x <-B_OPS,(get_id(x) == m0_O)} -> SKIP);\n");
        expected1.append("alt2?g1?g2 -> (g1 & (A_mSIG.r!lf2_id!lf1_id?signal:{x | x <- A_SIG,(get_id(x) == m1)} -> SKIP)\n");
        expected1.append("[]\n");
        expected1.append("g2 & (B_mOP.s!lf1_id!lf2_id.m2_I -> SKIP);(B_mOP.r!lf2_id!lf1_id?out:{x | x <-B_OPS,(get_id(x) == m2_O)} -> SKIP));");
        expected1.append("(B_mSIG.s!lf1_id!lf2_id.m3 -> SKIP)\n");
        expected1.append("Seq1_y_B(sd_id,lf1_id,lf2_id) =(B_mOP.r!lf1_id!lf2_id?oper:{x | x <- B_OPS,(get_id(x) == m0_I)} -> SKIP);(B_mOP.s!lf2_id!lf1_id.m0_O -> SKIP);\n");
        expected1.append("alt2?g1?g2 -> (g1 & (A_mSIG.s!lf2_id!lf1_id.m1 -> SKIP)\n");
        expected1.append("[]\n");
        expected1.append("g2 & (B_mOP.r!lf1_id!lf2_id?oper:{x | x <- B_OPS,(get_id(x) == m2_I)} -> SKIP);(B_mOP.s!lf2_id!lf1_id.m2_O -> SKIP));");
        expected1.append("(B_mSIG.r!lf1_id!lf2_id?signal:{x | x <- B_SIG,(get_id(x) == m3)} -> SKIP)\n");
        expected1.append("Seq1_x_A_y_B_m0(sd_id,lf1_id,lf2_id) = B_mOP.s.lf1_id.lf2_id?x:{x | x<-B_OPS,get_id(x) == m0_I} -> ");
        expected1.append("B_mOP.r.lf1_id.lf2_id!x -> Seq1_x_A_y_B_m0(sd_id,lf1_id,lf2_id)\n");
        expected1.append("Seq1_y_B_x_A_m0_r(sd_id,lf2_id,lf1_id) = B_mOP.s.lf2_id.lf1_id?x:{x | x<-B_OPS,get_id(x) == m0_O} -> ");
        expected1.append("B_mOP.r.lf2_id.lf1_id!x -> Seq1_y_B_x_A_m0_r(sd_id,lf2_id,lf1_id)\n");
        expected1.append("Seq1_x_A_y_B_m3(sd_id,lf1_id,lf2_id) = B_mSIG.s.lf1_id.lf2_id?x:{x | x<-B_SIG,get_id(x) == m3} -> ");
        expected1.append("B_mSIG.r.lf1_id.lf2_id!x -> Seq1_x_A_y_B_m3(sd_id,lf1_id,lf2_id)\n");
        expected1.append("Seq1_x_A_y_B_m2(sd_id,lf1_id,lf2_id) = B_mOP.s.lf1_id.lf2_id?x:{x | x<-B_OPS,get_id(x) == m2_I} -> ");
        expected1.append("B_mOP.r.lf1_id.lf2_id!x -> Seq1_x_A_y_B_m2(sd_id,lf1_id,lf2_id)\n");
        expected1.append("Seq1_y_B_x_A_m2_r(sd_id,lf2_id,lf1_id) = B_mOP.s.lf2_id.lf1_id?x:{x | x<-B_OPS,get_id(x) == m2_O} -> ");
        expected1.append("B_mOP.r.lf2_id.lf1_id!x -> Seq1_y_B_x_A_m2_r(sd_id,lf2_id,lf1_id)\n");
        expected1.append("Seq1_y_B_x_A_m1(sd_id,lf2_id,lf1_id) = A_mSIG.s.lf2_id.lf1_id?x:{x | x<-A_SIG,get_id(x) == m1} -> ");
        expected1.append("A_mSIG.r.lf2_id.lf1_id!x -> Seq1_y_B_x_A_m1(sd_id,lf2_id,lf1_id)\n");
        expected1.append("Seq1_MessagesBuffer(sd_id,lf1_id,lf2_id) = (Seq1_x_A_y_B_m0(sd_id,lf1_id,lf2_id) ||| Seq1_y_B_x_A_m0_r(sd_id,lf2_id,lf1_id) ");
        expected1.append("||| Seq1_x_A_y_B_m3(sd_id,lf1_id,lf2_id) ||| Seq1_x_A_y_B_m2(sd_id,lf1_id,lf2_id) ");
        expected1.append("||| Seq1_y_B_x_A_m2_r(sd_id,lf2_id,lf1_id) ||| Seq1_y_B_x_A_m1(sd_id,lf2_id,lf1_id))/\\endInteraction -> SKIP\n");
        expected1.append("Seq1Parallel(sd_id,lf1_id,lf2_id) = Seq1_x_A(sd_id,lf1_id,lf2_id)");
        expected1.append("[ {|alt2,B_mOP.s.lf1_id.lf2_id.m0_I, B_mOP.r.lf2_id.lf1_id.m0_O, B_mSIG.s.lf1_id.lf2_id.m3, ");
        expected1.append("B_mOP.s.lf1_id.lf2_id.m2_I, B_mOP.r.lf2_id.lf1_id.m2_O, A_mSIG.r.lf2_id.lf1_id.m1|} || ");
        expected1.append("{|alt2,B_mOP.r.lf1_id.lf2_id.m0_I, B_mOP.s.lf2_id.lf1_id.m0_O, B_mSIG.r.lf1_id.lf2_id.m3, B_mOP.r.lf1_id.lf2_id.m2_I, ");
        expected1.append("B_mOP.s.lf2_id.lf1_id.m2_O, A_mSIG.s.lf2_id.lf1_id.m1|} ]Seq1_y_B(sd_id,lf1_id,lf2_id)\\Hidden\n");
        expected1.append("SD_Seq1(sd_id,lf1_id,lf2_id) = beginInteraction ->((Seq1Parallel(sd_id,lf1_id,lf2_id); endInteraction -> SKIP)");
        expected1.append("[|{|B_mOP.s.lf1_id.lf2_id.m0_I,B_mOP.r.lf1_id.lf2_id.m0_I,B_mOP.s.lf2_id.lf1_id.m0_O,B_mOP.r.lf2_id.lf1_id.m0_O,");
        expected1.append("B_mSIG.s.lf1_id.lf2_id.m3,B_mSIG.r.lf1_id.lf2_id.m3,B_mOP.s.lf1_id.lf2_id.m2_I,B_mOP.r.lf1_id.lf2_id.m2_I,");
        expected1.append("B_mOP.s.lf2_id.lf1_id.m2_O,B_mOP.r.lf2_id.lf1_id.m2_O,A_mSIG.s.lf2_id.lf1_id.m1,A_mSIG.r.lf2_id.lf1_id.m1,");
        expected1.append("endInteraction|}|]Seq1_MessagesBuffer(sd_id,lf1_id,lf2_id))");

        assertEquals(expected1.toString(),actual);
    }

    private static INamedElement[] findSequence(ProjectAccessor projectAccessor) throws ProjectNotFoundException {
        INamedElement[] foundElements = projectAccessor.findElements(new ModelFinder() {
            public boolean isTarget(INamedElement namedElement) {
                return namedElement instanceof ISequenceDiagram;
            }
        });
        return foundElements;
    }

}