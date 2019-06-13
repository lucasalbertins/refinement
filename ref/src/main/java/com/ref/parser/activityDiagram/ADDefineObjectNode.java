package com.ref.parser.activityDiagram;

import com.change_vision.jude.api.inf.model.*;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ADDefineObjectNode {

    private IActivity ad;

    private HashMap<String, ArrayList<String>> alphabetNode;
    private HashMap<String, String> syncChannelsEdge;
    private HashMap<String, String> syncObjectsEdge;
    private HashMap<String, String> objectEdges;
    private List<IActivityNode> queueNode;
    private HashMap<String, String> parameterNodesInput;
    private List<ArrayList<String>> unionList;
    private HashMap<String, String> typeUnionList;
    private ADUtils adUtils;

    public ADDefineObjectNode(IActivity ad, HashMap<String, ArrayList<String>> alphabetNode, HashMap<String, String> syncChannelsEdge,
                              HashMap<String, String> syncObjectsEdge, HashMap<String, String> objectEdges, List<IActivityNode> queueNode,
                              HashMap<String, String> parameterNodesInput, List<ArrayList<String>> unionList, HashMap<String, String> typeUnionList,
                              ADUtils adUtils) {
        this.ad = ad;
        this.alphabetNode = alphabetNode;
        this.syncChannelsEdge = syncChannelsEdge;
        this.syncObjectsEdge = syncObjectsEdge;
        this.objectEdges = objectEdges;
        this.queueNode = queueNode;
        this.parameterNodesInput = parameterNodesInput;
        this.unionList = unionList;
        this.typeUnionList = typeUnionList;
        this.adUtils = adUtils;
    }

    public IActivityNode defineObjectNode(IActivityNode activityNode, StringBuilder nodes, int code) {
        StringBuilder objectNode = new StringBuilder();
        ArrayList<String> alphabet = new ArrayList<>();
        String nameObjectNode = adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName());
        String nameObjectNodeTermination = adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + "_t";
        String endDiagram = "END_DIAGRAM_" + adUtils.nameDiagramResolver(ad.getName());
        IFlow[] outFlows = activityNode.getOutgoings();
        IFlow[] inFlows = activityNode.getIncomings();
        String nameObject = null;
        String nameObjectUnique = "";
        List<String> nameObjectAdded = new ArrayList<>();
        HashMap<String, String> nameObjects = new HashMap<>();
        List<String> namesMemoryLocal = new ArrayList<>();
        String typeMemoryLocal = null;

        if (code == 0) {
            ArrayList<String> ceInitials = new ArrayList<>();
            for (int i = 0; i < inFlows.length; i++) {
                ceInitials.add(inFlows[i].getId());

                if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
                    String ceIn2 = syncObjectsEdge.get(inFlows[i].getId());
                    nameObjects.put(inFlows[i].getId(), objectEdges.get(ceIn2));
                }
            }

            objectNode.append(nameObjectNode + " = ");

            objectNode.append("(");


            for (int i = 0; i < ceInitials.size(); i++) {        //get unique channel
                if (nameObjects.get(ceInitials.get(i)) != null) {
                    if (!nameObjectAdded.contains(nameObjects.get(ceInitials.get(i)))) {
                        nameObjectAdded.add(nameObjects.get(ceInitials.get(i)));
                        nameObjectUnique += nameObjects.get(ceInitials.get(i));
                        typeMemoryLocal = nameObjects.get(ceInitials.get(i));
                    }
                }
            }

            if (!nameObjectUnique.equals("")) {
                namesMemoryLocal.add(nameObjectUnique);
            }

            for (int i = 0; i < ceInitials.size(); i++) {
                String oeIn = syncObjectsEdge.get(ceInitials.get(i)); //get the parallel input channels

                nameObject = nameObjects.get(ceInitials.get(i));
                objectNode.append("(");

                if (i >= 0 && i < ceInitials.size() - 1) {
                    adUtils.ce(alphabet, objectNode, oeIn, "?" + nameObject + " -> ");
                    adUtils.setLocalInput(alphabet, objectNode, nameObjectUnique, adUtils.nameDiagramResolver(activityNode.getName()), nameObject, oeIn);
                    objectNode.append("SKIP) [] ");
                } else {
                    adUtils.ce(alphabet, objectNode, oeIn, "?" + nameObject + " -> ");
                    adUtils.setLocalInput(alphabet, objectNode, nameObjectUnique, adUtils.nameDiagramResolver(activityNode.getName()), nameObject, oeIn);
                    objectNode.append("SKIP)");
                }
            }

            objectNode.append("); ");

            adUtils.update(alphabet, objectNode, 1, activityNode.getOutgoings().length, false);

            adUtils.getLocal(alphabet, objectNode, nameObjectUnique, adUtils.nameDiagramResolver(activityNode.getName()), nameObjectUnique);

            objectNode.append("(");

            for (int i = 0; i < outFlows.length; i++) {
                String oe = adUtils.createOE(nameObjectUnique); //creates output channels
                syncObjectsEdge.put(outFlows[i].getId(), oe);
                objectEdges.put(oe, nameObjectUnique);
                objectNode.append("(");

                if (i >= 0 && (i < outFlows.length - 1)) {
                    adUtils.oe(alphabet, objectNode, oe, "!" + nameObjectUnique, " -> SKIP) ||| ");
                } else {
                    adUtils.oe(alphabet, objectNode, oe, "!" + nameObjectUnique, " -> SKIP)");
                }
            }

            objectNode.append("); ");

            objectNode.append(nameObjectNode + "\n");
            objectNode.append(nameObjectNodeTermination + " = ");

            objectNode.append("((" + nameObjectNode + " /\\ " + endDiagram + ") ");

            objectNode.append("[|{|");
            objectNode.append("get_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
            objectNode.append("set_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
            objectNode.append("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()));
            objectNode.append("|}|] ");
            objectNode.append("Mem_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + "_" + nameObjectUnique + "_t(" + adUtils.getDefaultValue(parameterNodesInput.get(typeMemoryLocal)) + ")) ");

            objectNode.append("\\{|");
            objectNode.append("get_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
            objectNode.append("set_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()));
            objectNode.append("|}\n");

            //

            alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()));
            alphabetNode.put(adUtils.nameDiagramResolver(activityNode.getName()), alphabet);

            if (outFlows[0].getTarget() instanceof IInputPin) {
                for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                    if (activityNodeSearch instanceof IAction) {
                        IInputPin[] inPins = ((IAction) activityNodeSearch).getInputs();
                        for (int y = 0; y < inPins.length; y++) {
                            if (inPins[y].getId().equals(outFlows[0].getTarget().getId())) {
                                activityNode = activityNodeSearch;
                            }
                        }
                    }
                }
            } else {
                activityNode = outFlows[0].getTarget();
            }

            for (int i = 1; i < outFlows.length; i++) {    //creates the parallel output channels
                if (outFlows[i].getTarget() instanceof IInputPin) {
                    for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                        if (activityNodeSearch instanceof IAction) {
                            IInputPin[] inFlowPin = ((IAction) activityNodeSearch).getInputs();
                            for (int y = 0; y < inFlowPin.length; y++) {
                                if (inFlowPin[y].getId().equals(outFlows[i].getTarget().getId())) {
                                    if (!queueNode.contains(activityNodeSearch)) {
                                        queueNode.add(activityNodeSearch);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (!queueNode.contains(outFlows[i].getTarget())) {
                        queueNode.add(outFlows[i].getTarget());
                    }
                }
            }

            nodes.append(objectNode.toString());
        } else if (code == 1) {
            ArrayList<String> union = new ArrayList<>();
            List<String> nameObjs = new ArrayList<>();
            List<String> nodesAdded = new ArrayList<>();

            for (int i = 0; i < inFlows.length; i++) {
                nameObjs.addAll(adUtils.getObjects(inFlows[i], nodesAdded));
            }

            for (String nameObj : nameObjs) {
                if (!union.contains(nameObj)) {
                    nameObjectUnique += nameObj;
                    union.add(nameObj);
                    typeMemoryLocal = nameObj;
                }
            }

            if (union.size() > 1) {
                unionList.add(union);
                typeUnionList.put(nameObjectUnique, parameterNodesInput.get(typeMemoryLocal));
            }

            if (!nameObjectUnique.equals("")) {
                namesMemoryLocal.add(nameObjectUnique);
            }

            for (int i = 0; i < outFlows.length; i++) {
                String oe = adUtils.createOE(nameObjectUnique); //creates output channels
                syncObjectsEdge.put(outFlows[i].getId(), oe);
                objectEdges.put(oe, nameObjectUnique);

                if (i >= 0 && (i < outFlows.length - 1)) {
                    adUtils.oe(alphabet, objectNode, oe, "!" + nameObjectUnique, " -> SKIP) ||| ");
                } else {
                    adUtils.oe(alphabet, objectNode, oe, "!" + nameObjectUnique, " -> SKIP)");
                }
            }

            if (outFlows[0].getTarget() instanceof IInputPin) {
                for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                    if (activityNodeSearch instanceof IAction) {
                        IInputPin[] inPins = ((IAction) activityNodeSearch).getInputs();
                        for (int y = 0; y < inPins.length; y++) {
                            if (inPins[y].getId().equals(outFlows[0].getTarget().getId())) {
                                activityNode = activityNodeSearch;
                            }
                        }
                    }
                }
            } else {
                activityNode = outFlows[0].getTarget();
            }

            for (int i = 1; i < outFlows.length; i++) {    //creates the parallel output channels
                if (outFlows[i].getTarget() instanceof IInputPin) {
                    for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                        if (activityNodeSearch instanceof IAction) {
                            IInputPin[] inFlowPin = ((IAction) activityNodeSearch).getInputs();
                            for (int y = 0; y < inFlowPin.length; y++) {
                                if (inFlowPin[y].getId().equals(outFlows[i].getTarget().getId())) {
                                    if (!queueNode.contains(activityNodeSearch)) {
                                        queueNode.add(activityNodeSearch);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (!queueNode.contains(outFlows[i].getTarget())) {
                        queueNode.add(outFlows[i].getTarget());
                    }
                }
            }

        } else if (code == 2) {
            ArrayList<String> ceInitials = new ArrayList<>();
            for (int i = 0; i < inFlows.length; i++) {
                ceInitials.add(inFlows[i].getId());

                if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
                    String ceIn2 = syncObjectsEdge.get(inFlows[i].getId());
                    nameObjects.put(inFlows[i].getId(), objectEdges.get(ceIn2));
                }
            }

            objectNode.append(nameObjectNode + " = ");

            objectNode.append("(");


            for (int i = 0; i < ceInitials.size(); i++) {        //get unique channel
                if (nameObjects.get(ceInitials.get(i)) != null) {
                    if (!nameObjectAdded.contains(nameObjects.get(ceInitials.get(i)))) {
                        nameObjectAdded.add(nameObjects.get(ceInitials.get(i)));
                        nameObjectUnique += nameObjects.get(ceInitials.get(i));
                        typeMemoryLocal = nameObjects.get(ceInitials.get(i));
                    }
                }
            }

            if (!nameObjectUnique.equals("")) {
                namesMemoryLocal.add(nameObjectUnique);
            }

            for (int i = 0; i < ceInitials.size(); i++) {
                String ceIn = syncChannelsEdge.get(ceInitials.get(i));    //get the parallel input channels
                String oeIn = syncObjectsEdge.get(ceInitials.get(i));

                if (ceIn != null) {
                    objectNode.append("(");

                    if (i >= 0 && i < ceInitials.size() - 1) {
                        adUtils.ce(alphabet, objectNode, ceIn, " -> SKIP) [] ");
                    } else {
                        adUtils.ce(alphabet, objectNode, ceIn, " -> SKIP)");
                    }
                } else {

                    nameObject = nameObjects.get(ceInitials.get(i));
                    objectNode.append("(");

                    if (i >= 0 && i < ceInitials.size() - 1) {
                        adUtils.ce(alphabet, objectNode, oeIn, "?" + nameObject + " -> ");
                        adUtils.setLocalInput(alphabet, objectNode, nameObjectUnique, adUtils.nameDiagramResolver(activityNode.getName()), nameObject, oeIn);
                        objectNode.append("SKIP) [] ");
                    } else {
                        adUtils.ce(alphabet, objectNode, oeIn, "?" + nameObject + " -> ");
                        adUtils.setLocalInput(alphabet, objectNode, nameObjectUnique, adUtils.nameDiagramResolver(activityNode.getName()), nameObject, oeIn);
                        objectNode.append("SKIP)");
                    }
                }
            }

            objectNode.append("); ");

            adUtils.update(alphabet, objectNode, 1, outFlows.length, false);

            adUtils.getLocal(alphabet, objectNode, nameObjectUnique, adUtils.nameDiagramResolver(activityNode.getName()), nameObjectUnique);

            objectNode.append("(");

            for (int i = 0; i < outFlows.length; i++) {
                String oe = syncObjectsEdge.get(outFlows[i].getId());
                objectNode.append("(");

                if (i >= 0 && (i < outFlows.length - 1)) {
                    adUtils.oe(alphabet, objectNode, oe, "!" + nameObjectUnique, " -> SKIP) ||| ");
                } else {
                    adUtils.oe(alphabet, objectNode, oe, "!" + nameObjectUnique, " -> SKIP)");
                }
            }

            objectNode.append("); ");

            objectNode.append(nameObjectNode + "\n");
            objectNode.append(nameObjectNodeTermination + " = ");

            objectNode.append("((" + nameObjectNode + " /\\ " + endDiagram + ") ");

            objectNode.append("[|{|");
            objectNode.append("get_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
            objectNode.append("set_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
            objectNode.append("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()));
            objectNode.append("|}|] ");
            objectNode.append("Mem_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + "_" + nameObjectUnique + "_t(" + adUtils.getDefaultValue(parameterNodesInput.get(typeMemoryLocal)) + ")) ");

            objectNode.append("\\{|");
            objectNode.append("get_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
            objectNode.append("set_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()));
            objectNode.append("|}\n");

            //

            alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()));
            alphabetNode.put(adUtils.nameDiagramResolver(activityNode.getName()), alphabet);

            if (outFlows[0].getTarget() instanceof IInputPin) {
                for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                    if (activityNodeSearch instanceof IAction) {
                        IInputPin[] inPins = ((IAction) activityNodeSearch).getInputs();
                        for (int y = 0; y < inPins.length; y++) {
                            if (inPins[y].getId().equals(outFlows[0].getTarget().getId())) {
                                activityNode = activityNodeSearch;
                            }
                        }
                    }
                }
            } else {
                activityNode = outFlows[0].getTarget();
            }

            for (int i = 1; i < outFlows.length; i++) {    //creates the parallel output channels
                if (outFlows[i].getTarget() instanceof IInputPin) {
                    for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                        if (activityNodeSearch instanceof IAction) {
                            IInputPin[] inFlowPin = ((IAction) activityNodeSearch).getInputs();
                            for (int y = 0; y < inFlowPin.length; y++) {
                                if (inFlowPin[y].getId().equals(outFlows[i].getTarget().getId())) {
                                    if (!queueNode.contains(activityNodeSearch)) {
                                        queueNode.add(activityNodeSearch);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (!queueNode.contains(outFlows[i].getTarget())) {
                        queueNode.add(outFlows[i].getTarget());
                    }
                }
            }

            nodes.append(objectNode.toString());
        }

        return activityNode;
    }
}
