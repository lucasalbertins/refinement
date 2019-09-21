package com.ref.refinement;

import com.change_vision.jude.api.inf.editor.SequenceDiagramEditor;
import com.change_vision.jude.api.inf.editor.TransactionManager;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.model.*;
import com.change_vision.jude.api.inf.presentation.ILinkPresentation;
import com.change_vision.jude.api.inf.presentation.INodePresentation;
import com.change_vision.jude.api.inf.presentation.IPresentation;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.ref.parser.ParserHelper;

import java.io.IOException;
import java.util.*;

public class CounterExampleDescriptor2 {

    private ISequenceDiagram referenceDiagram;
    private Map<ILifeline, INodePresentation> lifelinesMap;

    public CounterExampleDescriptor2(ISequenceDiagram reference) {
        this.referenceDiagram = reference;
        this.lifelinesMap = new HashMap<>();
    }

    public void buildCounterExample(ProjectAccessor projectAccessor, List<String> trace) throws ClassNotFoundException, IOException {

        try {
            TransactionManager.beginTransaction();
//            List<String> nameTrace = preProcess(trace);
            String error = getErrorMsgName(trace);
            IMessage[] msgs = referenceDiagram.getInteraction().getMessages();

            System.out.println("Error :" + error);

            for (IMessage msg : msgs) {
                if (msg.getName().equals(error)) {
                    IPresentation[] presentations = msg.getPresentations();
                    for (IPresentation p : presentations) {
                        if (p instanceof ILinkPresentation) {
                            p.setProperty("line.color", "#FF0000");
                        }
                    }
                }
            }

            TransactionManager.endTransaction();
            projectAccessor.save();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            TransactionManager.abortTransaction();
            // projectAccessor.close();
        }
    }

    private String getErrorMsgName(List<String> msgs) {
        String errorMsg = "";
        String error = msgs.get(0);
        String[] splitPoint = error.split("\\.");
        String lastSplitPoint = splitPoint[splitPoint.length - 1];
        String[] splitUnderScore = lastSplitPoint.split("_");
        boolean isSendMsg = (splitUnderScore.length == 1 || splitUnderScore[1].equals("I"));
        if (splitPoint[1].equals("s") && isSendMsg) {
            errorMsg = splitUnderScore[0];
        }

        return errorMsg;
    }

//    private List<String> preProcess(List<String> entrada) {
//        List<String> result = new ArrayList<String>();
//        for (String trace : entrada) {
//            String newtrace = trace.replaceAll("beginInteraction, ", "");
//            newtrace = newtrace.replaceAll(", endInteraction", "");
//            String[] msgs = newtrace.split(",");
//            for (String msg : msgs) {
//                String[] splitPoint = msg.split("\\.");
//                String lastSplitPoint = splitPoint[splitPoint.length - 1];
//                String[] splitUnderScore = lastSplitPoint.split("_");
//                boolean isSendMsg = (splitUnderScore.length == 1 || splitUnderScore[1].equals("I"));
//                if (splitPoint[1].equals("s") && isSendMsg) {
//                    result.add(splitUnderScore[0]);
//                }
//            }
//        }
//        return result;
//    }
}
