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
    public void  channelsWithFragment(){
        System.out.println(parser.getChannels());
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
