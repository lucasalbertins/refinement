package com.ref.refinement;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.*;

import com.change_vision.jude.api.inf.editor.BasicModelEditor;
import com.change_vision.jude.api.inf.editor.ModelEditorFactory;
import com.change_vision.jude.api.inf.editor.SequenceDiagramEditor;
import com.change_vision.jude.api.inf.editor.TransactionManager;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.exception.LicenseNotFoundException;
import com.change_vision.jude.api.inf.exception.ProjectLockedException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.*;
import com.change_vision.jude.api.inf.presentation.ILinkPresentation;
import com.change_vision.jude.api.inf.presentation.INodePresentation;
import com.change_vision.jude.api.inf.presentation.IPresentation;
import com.change_vision.jude.api.inf.project.ModelFinder;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;
import com.ref.parser.ParserHelper;
import com.ref.parser.process.parsers.FragmentInfo;

public class CounterexampleDescriptor {

    private Map<String, String> lifelinesMap;
    private List<String> lifelineBases;
    private ISequenceDiagram cloneDiagram;
    private static final int SPACE_BETWEEN_MSGS = 50;
    private static final int SPACE_BETWEEN_LFS = 200;

    /*
        The counterExampleDescriptor needs a map of all lifelines contained in the current project.
        The map entries have the format < lf(int)id, (lf base)_(lf instance) >
        and are defined in the lifelines3 attribute of SDParser class
     */
    public CounterexampleDescriptor(Map<String, String> lifelinesMap, ISequenceDiagram seq) {
        this.lifelineBases = new ArrayList<>(lifelinesMap.values());
    }
    /*
        This is the method that creates a counter example sequence diagram in Astah.
        It receives a list of events as parameter, this list is created by the FdrWrapper
        class as a result of the method getCounterExamples()
     */
    public void buildCounterExample(String name, List<String> entrada, ProjectAccessor projectAccessor)
            throws ClassNotFoundException, IOException {

        List<String> events = preProcess(entrada);
        try {
            TransactionManager.beginTransaction();
            createSequenceDiagram(events, projectAccessor);
            TransactionManager.endTransaction();
            projectAccessor.save();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            TransactionManager.abortTransaction();
            // projectAccessor.close();
        }
    }

    /*
        This method removes events from the counterExample event list. It removes events that are not used in
        the counter Example SD, such as beginInteraction and endInteraction
     */
    private List<String> preProcess(List<String> entrada) {
        List<String> result = new ArrayList<String>();

        for (String trace : entrada) {
            String newtrace = trace.replace("beginInteraction, ", "");
            newtrace = newtrace.replaceAll(", endInteraction", "");
            result.add(newtrace);
        }
        return result;
    }

    private void createSequenceDiagram(List<String> events, ProjectAccessor projectAccessor) throws Exception {

        IModel project = projectAccessor.getProject();
        // create sequence diagram
        SequenceDiagramEditor de = projectAccessor.getDiagramEditorFactory().getSequenceDiagramEditor();
        ISequenceDiagram newDgm = de.createSequenceDiagram(project, "CounterExample");
        newDgm.setProperties(cloneDiagram.getProperties());



        // Creates the lifelines and position them properly in the sequence diagram
        List<INodePresentation> myLifelines = CreateLifelines(project, de);
        // create messages, combinedFragment, interactionUse, stateInvariant
        CreateMessages(events, de, myLifelines);
    }

    private List<INodePresentation> CreateLifelines(IModel project, SequenceDiagramEditor de) throws InvalidEditingException {
        List<INodePresentation> myLifelines = new ArrayList<INodePresentation>();
        double position = 0;
        for (String lf : lifelineBases) {
            String[] split = lf.split("_");
            IClass boundary = findNamedElement(project.getOwnedElements(), split[0], IClass.class);
            INodePresentation objPs1 = de.createLifeline(split[1], position);
            ILifeline lifeline1 = (ILifeline) objPs1.getModel();
            lifeline1.setBase(boundary);
            position = position + 200;
            myLifelines.add(objPs1);
        }
        return myLifelines;
    }

    private void CreateFragment(String frag, int position ,SequenceDiagramEditor de, List<INodePresentation> myLifelines) throws InvalidEditingException {

        Map<IClass, Double> lifelinesPos = new HashMap<>();
        for (INodePresentation lf : myLifelines) {
            lifelinesPos.put(((ILifeline) lf.getModel()).getBase(), lf.getLocation().getX());
        }
        System.out.println("LifelinesPos :" + lifelinesPos.toString());

        List<ILifeline> lfbases = ParserHelper.getInstance().getLifelinesByFrag(frag);
        double[] bounds = getFragmentBoundaries(lfbases, lifelinesPos);
        FragmentInfo fragInfo = ParserHelper.getInstance().getFragmentInfo(frag);

        System.out.println("Number of operands: " + fragInfo.getNumberOfOperands());
        Point2D point = new Point2D.Double(bounds[0] + 28, position);
        INodePresentation fragment = de.createCombinedFragment(fragInfo.getName(), fragInfo.getType(), point, bounds[1] + 56 - bounds[0], 50 + fragInfo.getNumberOfMessages() * SPACE_BETWEEN_MSGS);
        Map<String, String> props = new HashMap<>();

        ICombinedFragment fragment1 = (ICombinedFragment) fragment.getModel();
        for (int i = 0 ; i < fragInfo.getNumberOfOperands() - 1; i++ ){
           fragment1.addInteractionOperand ("g" + i, "g" + i);
           IInteractionOperand[] operands= fragment1.getInteractionOperands();
        }
//        IInteractionOperand[] operands = fragment1.getInteractionOperands();
//        for (int j = 0 ; j < operands.length; j++){
//            operands[j].setGuard(fragInfo.getType() + (j+1));
//        }
    }

    private double[] getFragmentBoundaries(List<ILifeline> lifelines, Map<IClass, Double> lifelinesPos) {
        double[] bounds = new double[2];
        double minBound = Double.MAX_VALUE;
        double maxBoud = Double.MIN_VALUE;
        for (ILifeline lf : lifelines) {
            System.out.println("Base :" + lf.getBase());
            double pos = lifelinesPos.get(lf.getBase());
            if (pos < minBound)
                minBound = pos;
            if (pos > maxBoud)
                maxBoud = pos;
        }
        bounds[0] = minBound;
        bounds[1] = maxBoud;

        return bounds;
    }

    private void CreateMessages(List<String> events, SequenceDiagramEditor de, List<INodePresentation> myLifelines) throws InvalidEditingException {
        List<ILinkPresentation> msgs = new ArrayList<ILinkPresentation>();

//        System.out.println("Events :" + events.toString());

        List<String> msgsSpecification = getMessages(events.get(1));
        List<String> msgsImplementation;

        if (events.get(0).equals("endInteraction")) {
            msgsImplementation = getMessages(events.get(2));
        } else {
            msgsSpecification.add(events.get(0));
            msgsImplementation = null;
        }

        int msgType; // pode ser substituido por um boolean "isAsync"
        int msgPosition = 160;

        System.out.println("MsgSpecification: " +msgsSpecification.toString());
        int size = msgsSpecification.size();
        System.out.println("MsgsInfo :" + ParserHelper.getInstance().getCounterExampleMsgs(msgsSpecification.get(0),msgsSpecification.get(size - 1)));
        for (int i = 0; i < msgsSpecification.size(); i++) {
            String[] msgSplit = msgsSpecification.get(i).split("\\.");

            if (msgSplit[0].contains("opt") || msgSplit[0].contains("alt")) {
                CreateFragment(msgSplit[0], msgPosition - SPACE_BETWEEN_MSGS, de, myLifelines);
                continue;
            }
            // Checks if the message is asynchronous(SIG) or synchronous(OP)
            else if (msgSplit[0].contains("SIG")) {
                msgType = 1;
            } else {
                msgType = 0;
            }
            msgPosition = BuildMessage(msgPosition, events, de, myLifelines, msgs, msgsSpecification, msgsImplementation, msgType, i, msgSplit);
        }
    }

    private int BuildMessage(int msgPosition, List<String> events, SequenceDiagramEditor de, List<INodePresentation> myLifelines, List<ILinkPresentation> msgs, List<String> msgsSpecification, List<String> msgsImplementation, int msgType, int i, String[] msgSplit) throws InvalidEditingException {

        int[] ids;
        // Just consider messages that are not return messages
        if (!msgSplit[1].equals("r")) {
            ids = findLifeline(msgSplit[2], msgSplit[3], myLifelines);
            if (ids[0] != -1 && ids[1] != -1) {
                String[] msgName = msgSplit[4].split("_");
                if (msgName.length >= 1 && !msgSplit[4].contains("_O")) {
                    ILinkPresentation msg = de.createMessage(msgName[0], myLifelines.get(ids[0]),
                            myLifelines.get(ids[1]), msgPosition);

                    if (events.get(0).equals("endInteraction") && !msgsImplementation.get(i).equals(msgsSpecification.get(i))) {
                        msg.setProperty("line.color", "#FF0000");
                    }

                    if (!events.get(0).equals("endInteraction") && i == msgsSpecification.size() - 1) {
                        msg.setProperty("line.color", "#FF0000");
                    }

                    if (msgType == 1) {
                        IMessage m = (IMessage) msg.getModel();
                        m.setAsynchronous(true);
                    } else {
                        msgs.add(msg);
                    }

                    msgPosition = msgPosition + 50;
                } else {
                    IMessage message;
                    for (ILinkPresentation msg : msgs) {
                        message = (IMessage) msg.getModel();
                        if (msgName[0].equals(message.getName())) {
                            de.createReturnMessage("", msg);
                            msgPosition = msgPosition + 50;
                            break;
                        }
                    }
                }
            }
        }
        return msgPosition;
    }

    private List<String> getMessages(String string) {
        List<String> msgs = new ArrayList<String>();
        String[] split = string.split(", ");
        for (int i = 0; i < split.length; i++) {
            if (!split[i].equals(""))
                msgs.add(split[i]);
        }
        return msgs;
    }

    private int[] findLifeline(String lfsrcID, String lfdestID, List<INodePresentation> myLifelines) {
        String lfName1 = lifelinesMap.get(lfsrcID);
        String lfName2 = lifelinesMap.get(lfdestID);
        String[] split1 = lfName1.split("_");
        String[] split2 = lfName2.split("_");
        int id = 0;
        int[] result = {-1, -1};
        ILifeline life;

        for (INodePresentation lf : myLifelines) {
            life = (ILifeline) lf.getModel();
            if (life.getName().equals(split1[1])) {
                result[0] = id;
            }
            if (life.getName().equals(split2[1])) {
                result[1] = id;
            }
            id++;
        }
        return result;
    }

    private <T extends INamedElement> T findNamedElement(INamedElement[] children, String name, Class<T> clazz) {
        for (INamedElement child : children) {
            if (clazz.isInstance(child) && child.getName().equals(name)) {
                return clazz.cast(child);
            }
        }
        return null;
    }

//    private IPresentation findPresentationByType(ISequenceDiagram dgm, String type) throws InvalidUsingException {
//        for (IPresentation ps : dgm.getPresentations()) {
//            if (ps.getType().equals(type)) {
//                return ps;
//            }
//        }
//        return null;
//    }
//
//    private static INamedElement[] findSequence(ProjectAccessor projectAccessor) throws ProjectNotFoundException {
//        INamedElement[] foundElements = projectAccessor.findElements(new ModelFinder() {
//            public boolean isTarget(INamedElement namedElement) {
//                return namedElement instanceof ISequenceDiagram;
//            }
//        });
//        return foundElements;
//    }

}
