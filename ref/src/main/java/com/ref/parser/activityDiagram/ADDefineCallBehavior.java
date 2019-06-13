package com.ref.parser.activityDiagram;

import com.change_vision.jude.api.inf.model.*;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ADDefineCallBehavior {

    private IActivity ad;

    private HashMap<String, ArrayList<String>> alphabetNode;
    private HashMap<String, String> syncChannelsEdge;
    private HashMap<String, String> syncObjectsEdge;
    private HashMap<String, String> objectEdges;
    private List<IActivityNode> queueNode;
    private List<IActivity> callBehaviourList;
    private HashMap<String, String> parameterNodesInput;
    private List<ArrayList<String>> unionList;
    private HashMap<String, String> typeUnionList;
    private ADUtils adUtils;

    public ADDefineCallBehavior(IActivity ad, HashMap<String, ArrayList<String>> alphabetNode, HashMap<String, String> syncChannelsEdge,
                                HashMap<String, String> syncObjectsEdge, HashMap<String, String> objectEdges, List<IActivityNode> queueNode,
                                List<IActivity> callBehaviourList, HashMap<String, String> parameterNodesInput, List<ArrayList<String>> unionList,
                                HashMap<String, String> typeUnionList, ADUtils adUtils) {
        this.ad = ad;
        this.alphabetNode = alphabetNode;
        this.syncChannelsEdge = syncChannelsEdge;
        this.syncObjectsEdge = syncObjectsEdge;
        this.objectEdges = objectEdges;
        this.queueNode = queueNode;
        this.callBehaviourList = callBehaviourList;
        this.parameterNodesInput = parameterNodesInput;
        this.unionList = unionList;
        this.typeUnionList = typeUnionList;
        this.adUtils = adUtils;
    }

    public IActivityNode defineCallBehaviour(IActivityNode activityNode, StringBuilder nodes, int code) {    //Ainda nao testado
        StringBuilder callBehaviour = new StringBuilder();
        ArrayList<String> alphabet = new ArrayList<>();
        String nameCallBehaviour = adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName());
        String namCallBehaviourTermination = adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + "_t";
        String endDiagram = "END_DIAGRAM_" + adUtils.nameDiagramResolver(ad.getName());
        IFlow[] outFlows = activityNode.getOutgoings();
        IFlow[] inFlows = activityNode.getIncomings();
        IOutputPin[] outPins = ((IAction) activityNode).getOutputs();
        IInputPin[] inPins = ((IAction) activityNode).getInputs();
        List<String> namesMemoryLocal = new ArrayList<>();
        List<String> namesOutpins = new ArrayList<>();
        HashMap<String, String> typeMemoryLocal = new HashMap<>();
        int countInFlowPin = 0;
        int countOutFlowPin = 0;
        callBehaviourList.add(((IAction) activityNode).getCallingActivity());

        for (int i = 0; i < outPins.length; i++) {
            namesOutpins.add(outPins[i].getName());
        }


        if (code == 0) {
            String definition = activityNode.getDefinition();
            String[] definitionFinal = new String[0];

            if (definition != null && !(definition.equals(""))) {
                definitionFinal = definition.replace(" ", "").split(";");
            }


            callBehaviour.append(nameCallBehaviour + " = ");


            callBehaviour.append("(");
            for (int i = 0; i < inFlows.length; i++) {
                if (syncChannelsEdge.containsKey(inFlows[i].getId())) {
                    String ceIn = syncChannelsEdge.get(inFlows[i].getId());

                    callBehaviour.append("(");
                    if (i >= 0 && (i < inFlows.length - 1 || inPins.length > 0)) {
                        adUtils.ce(alphabet, callBehaviour, ceIn, " -> SKIP) ||| ");
                    } else {
                        adUtils.ce(alphabet, callBehaviour, ceIn, " -> SKIP)");
                    }
                }
            }

            for (int i = 0; i < inPins.length; i++) {
                IFlow[] inFlowPin = inPins[i].getIncomings();
                for (int x = 0; x < inFlowPin.length; x++) {
                    if (syncObjectsEdge.containsKey(inFlowPin[x].getId())) {
                        String oeIn = syncObjectsEdge.get(inFlowPin[x].getId());
                        String typeNameObject = objectEdges.get(oeIn);
                        String nameObject = inPins[i].getName();

                        callBehaviour.append("(");
                        if (i >= 0 && (i < inPins.length - 1 || x < inFlowPin.length - 1)) {
                            adUtils.oe(alphabet, callBehaviour, oeIn, "?" + nameObject, " -> ");
                            adUtils.setLocalInput(alphabet, callBehaviour, nameObject, adUtils.nameDiagramResolver(activityNode.getName()), nameObject, oeIn);
                            callBehaviour.append("SKIP) ||| ");
                        } else {
                            adUtils.oe(alphabet, callBehaviour, oeIn, "?" + nameObject, " -> ");
                            adUtils.setLocalInput(alphabet, callBehaviour, nameObject, adUtils.nameDiagramResolver(activityNode.getName()), nameObject, oeIn);
                            callBehaviour.append("SKIP)");
                        }

                        if (!namesMemoryLocal.contains(nameObject)) {
                            namesMemoryLocal.add(nameObject);
                            typeMemoryLocal.put(nameObject, typeNameObject);
                        }
                    }
                }
            }

            callBehaviour.append("); ");

            //count outFlowsPin
            for (int i = 0; i < inPins.length; i++) {
                countInFlowPin += inPins[i].getIncomings().length;
            }

            for (int i = 0; i < outPins.length; i++) {
                countOutFlowPin += outPins[i].getOutgoings().length;
            }

            for (String nameObj : namesMemoryLocal) {
                adUtils.getLocal(alphabet, callBehaviour, nameObj, adUtils.nameDiagramResolver(activityNode.getName()), nameObj);
            }
            //call
            int count = adUtils.startActivity(alphabet, callBehaviour, ((IAction) activityNode).getCallingActivity().getActivityDiagram().getName(), namesMemoryLocal);
            adUtils.endActivity(alphabet, callBehaviour, ((IAction) activityNode).getCallingActivity().getActivityDiagram().getName(), namesOutpins, count);

            adUtils.update(alphabet, callBehaviour, inFlows.length + countInFlowPin, outFlows.length + countOutFlowPin, false);

            if (outFlows.length > 0 || outPins.length > 0) {
                callBehaviour.append("(");
            }

            for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                String ce = adUtils.createCE();
                syncChannelsEdge.put(outFlows[i].getId(), ce);

                callBehaviour.append("(");

                if (i >= 0 && (i < outFlows.length - 1 || outPins.length > 0)) {
                    adUtils.ce(alphabet, callBehaviour, ce, " -> SKIP) ||| ");
                } else {
                    adUtils.ce(alphabet, callBehaviour, ce, " -> SKIP)");
                }
            }

            String nameObject = "";
            String lastName = "";
            ArrayList<String> union = new ArrayList<>();

            for (int i = 0; i < inPins.length; i++) {
                IFlow[] inFlowPin = inPins[i].getIncomings();
                for (int x = 0; x < inFlowPin.length; x++) {
                    String channel = syncObjectsEdge.get(inFlowPin[x].getId());
                    nameObject += objectEdges.get(channel);
                    union.add(objectEdges.get(channel));
                    lastName = objectEdges.get(channel);
                }
            }

            if (union.size() > 1) {
                unionList.add(union);
                typeUnionList.put(nameObject, parameterNodesInput.get(lastName));
            }

            for (int i = 0; i < outPins.length; i++) {    //creates the parallel output channels
                IFlow[] outFlowPin = outPins[i].getOutgoings();

                for (int x = 0; x < outFlowPin.length; x++) {
                    String oe = adUtils.createOE(nameObject);
                    syncObjectsEdge.put(outFlowPin[x].getId(), oe);

                    objectEdges.put(oe, nameObject);
                    String value = "";
                    for (int j = 0; j < definitionFinal.length; j++) {
                        String[] expression = definitionFinal[j].split("=");
                        if (expression[0].equals(outPins[i].getName())) {
                            value = expression[1];
                        }
                    }

                    callBehaviour.append("(");
                    if (i >= 0 && (i < outPins.length - 1 || x < outFlowPin.length - 1)) {
                        adUtils.oe(alphabet, callBehaviour, oe, "!(" + outPins[i].getName() + ")", " -> SKIP) ||| ");
                    } else {
                        adUtils.oe(alphabet, callBehaviour, oe, "!(" + outPins[i].getName() + ")", " -> SKIP)");
                    }

                }
            }

            if (outFlows.length > 0 || outPins.length > 0) {
                callBehaviour.append("); ");
            }

            callBehaviour.append(nameCallBehaviour + "\n");

            callBehaviour.append(namCallBehaviourTermination + " = ");

            if (namesMemoryLocal.size() > 0) {
                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                    callBehaviour.append("(");
                }
                callBehaviour.append("(" + nameCallBehaviour + " /\\ " + endDiagram + ") ");

                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                    callBehaviour.append("[|{|");
                    callBehaviour.append("get_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                    callBehaviour.append("set_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                    callBehaviour.append("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()));
                    callBehaviour.append("|}|] ");

                    String typeObj = parameterNodesInput.get(typeMemoryLocal.get(namesMemoryLocal.get(i)));
                    if (typeObj == null) {
                        typeObj = typeUnionList.get(typeMemoryLocal.get(namesMemoryLocal.get(i)));
                    }

                    callBehaviour.append("Mem_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + "_" + namesMemoryLocal.get(i) + "_t(" + adUtils.getDefaultValue(typeObj) + ")) ");
                }

                callBehaviour.append("\\{|");

                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                    if (i == namesMemoryLocal.size() - 1) {
                        callBehaviour.append("get_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                        callBehaviour.append("set_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()));
                    } else {
                        callBehaviour.append("get_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                        callBehaviour.append("set_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                    }
                }

                callBehaviour.append("|}\n");

            } else {
                callBehaviour.append(nameCallBehaviour + " /\\ " + endDiagram + "\n");
            }

            alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()));
            alphabetNode.put(adUtils.nameDiagramResolver(activityNode.getName()), alphabet);

            if (outFlows.length > 0) {
                activityNode = outFlows[0].getTarget();    //set next action or control node

                for (int i = 1; i < outFlows.length; i++) {    //puts the remaining nodes in the queue
                    if (!queueNode.contains(outFlows[i].getTarget())) {
                        queueNode.add(outFlows[i].getTarget());
                    }
                }

                for (int i = 0; i < outPins.length; i++) {    //creates the parallel output channels
                    IFlow[] outFlowPin = outPins[i].getOutgoings();
                    for (int x = 0; x < outFlowPin.length; x++) {
                        if (outFlowPin[x].getTarget() instanceof IInputPin) {
                            for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                                if (activityNodeSearch instanceof IAction) {
                                    IInputPin[] inFlowPin = ((IAction) activityNodeSearch).getInputs();
                                    for (int y = 0; y < inFlowPin.length; y++) {
                                        if (inFlowPin[y].getId().equals(outFlowPin[x].getTarget().getId())) {
                                            if (!queueNode.contains(activityNodeSearch)) {
                                                queueNode.add(activityNodeSearch);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            if (!queueNode.contains(outFlowPin[x].getTarget())) {
                                queueNode.add(outFlowPin[x].getTarget());
                            }
                        }
                    }
                }

            } else if (outPins.length > 0) {

                IFlow[] outFlowOut = outPins[0].getOutgoings();
                if (outFlowOut[0].getTarget() instanceof IInputPin) {
                    for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                        if (activityNodeSearch instanceof IAction) {
                            IInputPin[] inFlowPin = ((IAction) activityNodeSearch).getInputs();
                            for (int y = 0; y < inFlowPin.length; y++) {
                                if (inFlowPin[y].getId().equals(outFlowOut[0].getTarget().getId())) {
                                    activityNode = activityNodeSearch;
                                }
                            }
                        }
                    }
                } else {
                    activityNode = outFlowOut[0].getTarget();
                }

                for (int i = 0; i < outPins.length; i++) {    //creates the parallel output channels
                    IFlow[] outFlowPin = outPins[i].getOutgoings();
                    for (int x = 0; x < outFlowPin.length; x++) {
                        if (outFlowPin[x].getTarget() instanceof IInputPin) {
                            for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                                if (activityNodeSearch instanceof IAction) {
                                    IInputPin[] inFlowPin = ((IAction) activityNodeSearch).getInputs();
                                    for (int y = 0; y < inFlowPin.length; y++) {
                                        if (inFlowPin[y].getId().equals(outFlowPin[x].getTarget().getId())) {
                                            if (!queueNode.contains(activityNodeSearch) && (i != 0 || x != 0)) {
                                                queueNode.add(activityNodeSearch);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            if (!queueNode.contains(outFlowPin[x].getTarget()) && (i != 0 || x != 0)) {
                                queueNode.add(outFlowPin[x].getTarget());
                            }
                        }
                    }
                }
            } else {
                activityNode = null;
            }

            nodes.append(callBehaviour.toString());
        } else if (code == 1) {
            String definition = activityNode.getDefinition();
            String[] definitionFinal = new String[0];

            if (outFlows.length > 0 || outPins.length > 0) {
                callBehaviour.append("(");
            }

            for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                String ce = adUtils.createCE();
                syncChannelsEdge.put(outFlows[i].getId(), ce);

                callBehaviour.append("(");

                if (i >= 0 && (i < outFlows.length - 1 || outPins.length > 0)) {
                    adUtils.ce(alphabet, callBehaviour, ce, " -> SKIP) ||| ");
                } else {
                    adUtils.ce(alphabet, callBehaviour, ce, " -> SKIP)");
                }
            }

            String nameObject = "";
            String lastName = "";

            ArrayList<String> union = new ArrayList<>();
            List<String> nameObjects = new ArrayList<>();
            List<String> nodesAdded = new ArrayList<>();

            for (int i = 0; i < inPins.length; i++) {
                IFlow[] inFlowPin = inPins[i].getIncomings();
                for (int x = 0; x < inFlowPin.length; x++) {

                    nameObjects.addAll(adUtils.getObjects(inFlowPin[x], nodesAdded));

                }
            }

            for (String nameObj : nameObjects) {
                if (!union.contains(nameObj)) {
                    nameObject += nameObj;
                    union.add(nameObj);
                    lastName = nameObj;
                }
            }

            if (union.size() > 1) {
                unionList.add(union);
                typeUnionList.put(nameObject, parameterNodesInput.get(lastName));
            }

            for (int i = 0; i < outPins.length; i++) {    //creates the parallel output channels
                IFlow[] outFlowPin = outPins[i].getOutgoings();

                for (int x = 0; x < outFlowPin.length; x++) {
                    String oe = adUtils.createOE(nameObject);
                    syncObjectsEdge.put(outFlowPin[x].getId(), oe);

                    objectEdges.put(oe, nameObject);
                    String value = "";
                    for (int j = 0; j < definitionFinal.length; j++) {
                        String[] expression = definitionFinal[j].split("=");
                        if (expression[0].equals(outPins[i].getName())) {
                            value = expression[1];
                        }
                    }

                    callBehaviour.append("(");
                    if (i >= 0 && (i < outPins.length - 1 || x < outFlowPin.length - 1)) {
                        adUtils.oe(alphabet, callBehaviour, oe, "!(" + value + ")", " -> SKIP) ||| ");
                    } else {
                        adUtils.oe(alphabet, callBehaviour, oe, "!(" + value + ")", " -> SKIP)");
                    }

                }
            }

            if (outFlows.length > 0 || outPins.length > 0) {
                callBehaviour.append("); ");
            }

            if (outFlows.length > 0) {
                activityNode = outFlows[0].getTarget();    //set next action or control node

                for (int i = 1; i < outFlows.length; i++) {    //puts the remaining nodes in the queue
                    if (!queueNode.contains(outFlows[i].getTarget())) {
                        queueNode.add(outFlows[i].getTarget());
                    }
                }

                for (int i = 0; i < outPins.length; i++) {    //creates the parallel output channels
                    IFlow[] outFlowPin = outPins[i].getOutgoings();
                    for (int x = 0; x < outFlowPin.length; x++) {
                        if (outFlowPin[x].getTarget() instanceof IInputPin) {
                            for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                                if (activityNodeSearch instanceof IAction) {
                                    IInputPin[] inFlowPin = ((IAction) activityNodeSearch).getInputs();
                                    for (int y = 0; y < inFlowPin.length; y++) {
                                        if (inFlowPin[y].getId().equals(outFlowPin[x].getTarget().getId())) {
                                            if (!queueNode.contains(activityNodeSearch)) {
                                                queueNode.add(activityNodeSearch);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            if (!queueNode.contains(outFlowPin[x].getTarget())) {
                                queueNode.add(outFlowPin[x].getTarget());
                            }
                        }
                    }
                }

            } else if (outPins.length > 0) {

                IFlow[] outFlowOut = outPins[0].getOutgoings();
                if (outFlowOut[0].getTarget() instanceof IInputPin) {
                    for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                        if (activityNodeSearch instanceof IAction) {
                            IInputPin[] inFlowPin = ((IAction) activityNodeSearch).getInputs();
                            for (int y = 0; y < inFlowPin.length; y++) {
                                if (inFlowPin[y].getId().equals(outFlowOut[0].getTarget().getId())) {
                                    activityNode = activityNodeSearch;
                                }
                            }
                        }
                    }
                } else {
                    activityNode = outFlowOut[0].getTarget();
                }

                for (int i = 0; i < outPins.length; i++) {    //creates the parallel output channels
                    IFlow[] outFlowPin = outPins[i].getOutgoings();
                    for (int x = 0; x < outFlowPin.length; x++) {
                        if (outFlowPin[x].getTarget() instanceof IInputPin) {
                            for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                                if (activityNodeSearch instanceof IAction) {
                                    IInputPin[] inFlowPin = ((IAction) activityNodeSearch).getInputs();
                                    for (int y = 0; y < inFlowPin.length; y++) {
                                        if (inFlowPin[y].getId().equals(outFlowPin[x].getTarget().getId())) {
                                            if (!queueNode.contains(activityNodeSearch) && (i != 0 || x != 0)) {
                                                queueNode.add(activityNodeSearch);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            if (!queueNode.contains(outFlowPin[x].getTarget()) && (i != 0 || x != 0)) {
                                queueNode.add(outFlowPin[x].getTarget());
                            }
                        }
                    }
                }
            } else {
                activityNode = null;
            }
        } else if (code == 2) {
            String definition = activityNode.getDefinition();
            String[] definitionFinal = new String[0];

            if (definition != null && !(definition.equals(""))) {
                definitionFinal = definition.replace(" ", "").split(";");
            }


            callBehaviour.append(nameCallBehaviour + " = ");


            callBehaviour.append("(");
            for (int i = 0; i < inFlows.length; i++) {
                if (syncChannelsEdge.containsKey(inFlows[i].getId())) {
                    String ceIn = syncChannelsEdge.get(inFlows[i].getId());

                    callBehaviour.append("(");
                    if (i >= 0 && (i < inFlows.length - 1 || inPins.length > 0)) {
                        adUtils.ce(alphabet, callBehaviour, ceIn, " -> SKIP) ||| ");
                    } else {
                        adUtils.ce(alphabet, callBehaviour, ceIn, " -> SKIP)");
                    }
                }
            }

            for (int i = 0; i < inPins.length; i++) {
                IFlow[] inFlowPin = inPins[i].getIncomings();
                for (int x = 0; x < inFlowPin.length; x++) {
                    if (syncObjectsEdge.containsKey(inFlowPin[x].getId())) {
                        String oeIn = syncObjectsEdge.get(inFlowPin[x].getId());
                        String typeNameObject = objectEdges.get(oeIn);
                        String nameObject = inPins[i].getName();

                        callBehaviour.append("(");
                        if (i >= 0 && (i < inPins.length - 1 || x < inFlowPin.length - 1)) {
                            adUtils.oe(alphabet, callBehaviour, oeIn, "?" + nameObject, " -> ");
                            adUtils.setLocalInput(alphabet, callBehaviour, nameObject, adUtils.nameDiagramResolver(activityNode.getName()), nameObject, oeIn);
                            callBehaviour.append("SKIP) ||| ");
                        } else {
                            adUtils.oe(alphabet, callBehaviour, oeIn, "?" + nameObject, " -> ");
                            adUtils.setLocalInput(alphabet, callBehaviour, nameObject, adUtils.nameDiagramResolver(activityNode.getName()), nameObject, oeIn);
                            callBehaviour.append("SKIP)");
                        }

                        if (!namesMemoryLocal.contains(nameObject)) {
                            namesMemoryLocal.add(nameObject);
                            typeMemoryLocal.put(nameObject, typeNameObject);
                        }
                    }
                }
            }

            callBehaviour.append("); ");

            //count outFlowsPin
            for (int i = 0; i < inPins.length; i++) {
                countInFlowPin += inPins[i].getIncomings().length;
            }

            for (int i = 0; i < outPins.length; i++) {
                countOutFlowPin += outPins[i].getOutgoings().length;
            }

            for (String nameObj : namesMemoryLocal) {
                adUtils.getLocal(alphabet, callBehaviour, nameObj, adUtils.nameDiagramResolver(activityNode.getName()), nameObj);
            }

            int count = adUtils.startActivity(alphabet, callBehaviour, ((IAction) activityNode).getCallingActivity().getActivityDiagram().getName(), namesMemoryLocal);
            adUtils.endActivity(alphabet, callBehaviour, ((IAction) activityNode).getCallingActivity().getActivityDiagram().getName(), namesOutpins, count);

            adUtils.update(alphabet, callBehaviour, inFlows.length + countInFlowPin, outFlows.length + countOutFlowPin, false);

            if (outFlows.length > 0 || outPins.length > 0) {
                callBehaviour.append("(");
            }

            for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                String ce = syncChannelsEdge.get(outFlows[i].getId());

                callBehaviour.append("(");

                if (i >= 0 && (i < outFlows.length - 1 || outPins.length > 0)) {
                    adUtils.ce(alphabet, callBehaviour, ce, " -> SKIP) ||| ");
                } else {
                    adUtils.ce(alphabet, callBehaviour, ce, " -> SKIP)");
                }
            }

            for (int i = 0; i < outPins.length; i++) {    //creates the parallel output channels
                IFlow[] outFlowPin = outPins[i].getOutgoings();

                for (int x = 0; x < outFlowPin.length; x++) {
                    String oe = syncObjectsEdge.get(outFlowPin[x].getId());

                    callBehaviour.append("(");
                    if (i >= 0 && (i < outPins.length - 1 || x < outFlowPin.length - 1)) {
                        adUtils.oe(alphabet, callBehaviour, oe, "!(" + outPins[i].getName() + ")", " -> SKIP) ||| ");
                    } else {
                        adUtils.oe(alphabet, callBehaviour, oe, "!(" + outPins[i].getName() + ")", " -> SKIP)");
                    }

                }
            }

            if (outFlows.length > 0 || outPins.length > 0) {
                callBehaviour.append("); ");
            }

            callBehaviour.append(nameCallBehaviour + "\n");

            callBehaviour.append(namCallBehaviourTermination + " = ");

            if (namesMemoryLocal.size() > 0) {
                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                    callBehaviour.append("(");
                }
                callBehaviour.append("(" + nameCallBehaviour + " /\\ " + endDiagram + ") ");

                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                    callBehaviour.append("[|{|");
                    callBehaviour.append("get_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                    callBehaviour.append("set_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                    callBehaviour.append("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()));
                    callBehaviour.append("|}|] ");

                    String typeObj = parameterNodesInput.get(typeMemoryLocal.get(namesMemoryLocal.get(i)));
                    if (typeObj == null) {
                        typeObj = typeUnionList.get(typeMemoryLocal.get(namesMemoryLocal.get(i)));
                    }

                    callBehaviour.append("Mem_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + "_" + namesMemoryLocal.get(i) + "_t(" + adUtils.getDefaultValue(typeObj) + ")) ");
                }

                callBehaviour.append("\\{|");

                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                    if (i == namesMemoryLocal.size() - 1) {
                        callBehaviour.append("get_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                        callBehaviour.append("set_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()));
                    } else {
                        callBehaviour.append("get_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                        callBehaviour.append("set_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                    }
                }

                callBehaviour.append("|}\n");

            } else {
                callBehaviour.append(nameCallBehaviour + " /\\ " + endDiagram + "\n");
            }

            alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()));
            alphabetNode.put(adUtils.nameDiagramResolver(activityNode.getName()), alphabet);

            if (outFlows.length > 0) {
                activityNode = outFlows[0].getTarget();    //set next action or control node

                for (int i = 1; i < outFlows.length; i++) {    //puts the remaining nodes in the queue
                    if (!queueNode.contains(outFlows[i].getTarget())) {
                        queueNode.add(outFlows[i].getTarget());
                    }
                }

                for (int i = 0; i < outPins.length; i++) {    //creates the parallel output channels
                    IFlow[] outFlowPin = outPins[i].getOutgoings();
                    for (int x = 0; x < outFlowPin.length; x++) {
                        if (outFlowPin[x].getTarget() instanceof IInputPin) {
                            for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                                if (activityNodeSearch instanceof IAction) {
                                    IInputPin[] inFlowPin = ((IAction) activityNodeSearch).getInputs();
                                    for (int y = 0; y < inFlowPin.length; y++) {
                                        if (inFlowPin[y].getId().equals(outFlowPin[x].getTarget().getId())) {
                                            if (!queueNode.contains(activityNodeSearch)) {
                                                queueNode.add(activityNodeSearch);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            if (!queueNode.contains(outFlowPin[x].getTarget())) {
                                queueNode.add(outFlowPin[x].getTarget());
                            }
                        }
                    }
                }

            } else if (outPins.length > 0) {

                IFlow[] outFlowOut = outPins[0].getOutgoings();
                if (outFlowOut[0].getTarget() instanceof IInputPin) {
                    for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                        if (activityNodeSearch instanceof IAction) {
                            IInputPin[] inFlowPin = ((IAction) activityNodeSearch).getInputs();
                            for (int y = 0; y < inFlowPin.length; y++) {
                                if (inFlowPin[y].getId().equals(outFlowOut[0].getTarget().getId())) {
                                    activityNode = activityNodeSearch;
                                }
                            }
                        }
                    }
                } else {
                    activityNode = outFlowOut[0].getTarget();
                }

                for (int i = 0; i < outPins.length; i++) {    //creates the parallel output channels
                    IFlow[] outFlowPin = outPins[i].getOutgoings();
                    for (int x = 0; x < outFlowPin.length; x++) {
                        if (outFlowPin[x].getTarget() instanceof IInputPin) {
                            for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                                if (activityNodeSearch instanceof IAction) {
                                    IInputPin[] inFlowPin = ((IAction) activityNodeSearch).getInputs();
                                    for (int y = 0; y < inFlowPin.length; y++) {
                                        if (inFlowPin[y].getId().equals(outFlowPin[x].getTarget().getId())) {
                                            if (!queueNode.contains(activityNodeSearch) && (i != 0 || x != 0)) {
                                                queueNode.add(activityNodeSearch);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            if (!queueNode.contains(outFlowPin[x].getTarget()) && (i != 0 || x != 0)) {
                                queueNode.add(outFlowPin[x].getTarget());
                            }
                        }
                    }
                }
            } else {
                activityNode = null;
            }

            nodes.append(callBehaviour.toString());
        }

        return activityNode;
    }
}
