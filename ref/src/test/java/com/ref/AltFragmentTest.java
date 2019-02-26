package com.ref;

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
            projectAccessor.open("src/test/resources/testRef4.asta");
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
        String expected = "channel beginInteraction,endInteraction\nchannel A_mSIG: COM.ID.ID.A_SIG\nchannel B_mOP: COM.ID.ID.B_OPS\nchannel B_mSIG: COM.ID.ID.B_SIG\nchannel alt1: Bool.Bool\nchannel alt2: Bool.Bool\n";
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
        expected1.append("g2 & (A_mSIG.s!lf1_id!lf2_id.m2 -> SKIP)");
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
