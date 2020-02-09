package com.ref.parser.activityDiagram;

import com.change_vision.jude.api.inf.model.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ADDefineAction {

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
    private ADParser adParser;

    public ADDefineAction(IActivity ad, HashMap<String, ArrayList<String>> alphabetNode, HashMap<String, String> syncChannelsEdge,
                          HashMap<String, String> syncObjectsEdge, HashMap<String, String> objectEdges, List<IActivityNode> queueNode,
                          HashMap<String, String> parameterNodesInput, List<ArrayList<String>> unionList, HashMap<String, String> typeUnionList,
                          ADUtils adUtils, ADParser adParser) {
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
        this.adParser = adParser;
    }

    public IActivityNode defineAction(IActivityNode activityNode, StringBuilder nodes, int code) {
        StringBuilder action = new StringBuilder();
        ArrayList<String> alphabet = new ArrayList<>();
        String nameAction = adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName());
        String nameActionTermination = adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + "_t";
        String endDiagram = "END_DIAGRAM_" + adUtils.nameDiagramResolver(ad.getName());
        IFlow[] outFlows = activityNode.getOutgoings();
        IFlow[] inFlows = activityNode.getIncomings();
        IOutputPin[] outPins = ((IAction) activityNode).getOutputs();
        IInputPin[] inPins = ((IAction) activityNode).getInputs();
        List<String> namesMemoryLocal = new ArrayList<>();
        HashMap<String, String> typeMemoryLocal = new HashMap<>();
        int countInFlowPin = 0;
        int countOutFlowPin = 0;

        if (code == 0) {
            String definition = activityNode.getDefinition();
            String[] definitionFinal = new String[0];

            if (definition != null && !(definition.equals(""))) {
                definitionFinal = definition.replace(" ", "").split(";");
            }

            action.append(nameAction + " = ");

            action.append("(");
            for (int i = 0; i < inFlows.length; i++) {
                if (syncChannelsEdge.containsKey(inFlows[i].getId())) {
                    String ceIn = syncChannelsEdge.get(inFlows[i].getId());

                    action.append("(");
                    if (i >= 0 && (i < inFlows.length - 1 || inPins.length > 0)) {
                        adUtils.ce(alphabet, action, ceIn, " -> SKIP) ||| ");
                    } else {
                        adUtils.ce(alphabet, action, ceIn, " -> SKIP)");
                    }
                }
            }

            for (int i = 0; i < inPins.length; i++) {
                IFlow[] inFlowPin = inPins[i].getIncomings();
                for (int x = 0; x < inFlowPin.length; x++) {
                    if (syncObjectsEdge.containsKey(inFlowPin[x].getId())) {
                        String oeIn = syncObjectsEdge.get(inFlowPin[x].getId());
                        //String typeNameObject = objectEdges.get(oeIn);
                        String nameObject = inPins[i].getName();

                        action.append("(");
                        if (i >= 0 && (i < inPins.length - 1 || x < inFlowPin.length - 1)) {
                            adUtils.oe(alphabet, action, oeIn, "?" + nameObject, " -> ");
                            adUtils.setLocalInput(alphabet, action, inPins[i].getBase().getName(), adUtils.nameDiagramResolver(activityNode.getName()), nameObject, oeIn);
                            action.append("SKIP) ||| ");
                        } else {
                            adUtils.oe(alphabet, action, oeIn, "?" + nameObject, " -> ");
                            adUtils.setLocalInput(alphabet, action, inPins[i].getBase().getName(), adUtils.nameDiagramResolver(activityNode.getName()), nameObject, oeIn);
                            action.append("SKIP)");
                        }

                        if (!namesMemoryLocal.contains(nameObject)) {
                            namesMemoryLocal.add(nameObject);
                            typeMemoryLocal.put(nameObject, inPins[i].getBase().getName());
                        }
                    }
                }
            }

            action.append("); ");

            //adUtils.lock(alphabet, action, 0, nameAction);
            adUtils.event(alphabet, nameAction, action);

            for (int i = 0; i < namesMemoryLocal.size(); i++) {
                for (int j = 0; j < definitionFinal.length; j++) {
                    String[] expression = definitionFinal[j].split("=");
                    if (expression[0].equals(namesMemoryLocal.get(i))) {
                        List<String> expReplaced = adUtils.replaceExpression(expression[1]);    //get expression replace '+','-','*','/'
                        for (String value : expReplaced) {                //get all parts
                            for (int x = 0; x < namesMemoryLocal.size(); x++) {
                                if (value.equals(namesMemoryLocal.get(x))) {
                                    adUtils.getLocal(alphabet, action, typeMemoryLocal.get(namesMemoryLocal.get(x)), adUtils.nameDiagramResolver(activityNode.getName()), namesMemoryLocal.get(x));
                                }
                            }
                        }

                        adUtils.setLocal(alphabet, action, expression[0], adUtils.nameDiagramResolver(activityNode.getName()), "(" + expression[1] + ")");

                    }
                }
            }

            //count outFlowsPin
            for (int i = 0; i < inPins.length; i++) {
                countInFlowPin += inPins[i].getIncomings().length;
            }

            for (int i = 0; i < outPins.length; i++) {
                countOutFlowPin += outPins[i].getOutgoings().length;
            }

            //adUtils.lock(alphabet, action, 1, nameAction);
            adUtils.update(alphabet, action, inFlows.length + countInFlowPin, outFlows.length + countOutFlowPin, false);

            for (String nameObj : namesMemoryLocal) {
                adUtils.getLocal(alphabet, action, typeMemoryLocal.get(nameObj), adUtils.nameDiagramResolver(activityNode.getName()), nameObj);
            }

            if (outFlows.length > 0 || outPins.length > 0) {
                action.append("(");
            }

            for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                String ce = adUtils.createCE();
                syncChannelsEdge.put(outFlows[i].getId(), ce);

                action.append("(");

                if (i >= 0 && (i < outFlows.length - 1 || outPins.length > 0)) {
                    adUtils.ce(alphabet, action, ce, " -> SKIP) ||| ");
                } else {
                    adUtils.ce(alphabet, action, ce, " -> SKIP)");
                }
            }

            String nameObject = "";
            //String lastName = "";
            //ArrayList<String> union = new ArrayList<>();
//
//            for (int i = 0; i < inPins.length; i++) {
//                IFlow[] inFlowPin = inPins[i].getIncomings();
//                for (int x = 0; x < inFlowPin.length; x++) {
//                    String channel = syncObjectsEdge.get(inFlowPin[x].getId());
//                    nameObject += objectEdges.get(channel);
//                    union.add(objectEdges.get(channel));
//                    //lastName = objectEdges.get(channel);
//                }
//            }

//            if (union.size() > 1) {
//                unionList.add(union);
//                typeUnionList.put(nameObject, parameterNodesInput.get(lastName));
//            }

            for (int i = 0; i < outPins.length; i++) {    //creates the parallel output channels
                IFlow[] outFlowPin = outPins[i].getOutgoings();

                for (int x = 0; x < outFlowPin.length; x++) {
                    nameObject = outPins[i].getBase().getName();

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

                    //String typeObj = parameterNodesInput.get(nameObject);
//                    if (typeObj == null) {
//                        typeObj = typeUnionList.get(nameObject);
//                    }

                    String typeObj = nameObject;


                    Pair<String, String> initialAndFinalParameterValue = adUtils.getInitialAndFinalParameterValue(typeObj);

                    if ((value != null && !value.equals("")) && ADUtils.isInteger(initialAndFinalParameterValue.getKey())) {
                        action.append("((");
                        action.append("(" + value + ") >= " + initialAndFinalParameterValue.getKey() + " and (" + value + ") <= "  + initialAndFinalParameterValue.getValue() + ") & ");
                    } else {
                        action.append("(");
                    }
                    if(value !=null && !value.equals("")) {
	                    if (i >= 0 && (i < outPins.length - 1 || x < outFlowPin.length - 1)) {
	                        adUtils.oe(alphabet, action, oe, "!(" + value + ")", " -> SKIP) ||| ");
	                    } else {
	                        adUtils.oe(alphabet, action, oe, "!(" + value + ")", " -> SKIP)");
	                    }
                    }
                    else {
                    	 if (i >= 0 && (i < outPins.length - 1 || x < outFlowPin.length - 1)) {
 	                        adUtils.oe(alphabet, action, oe, "?"+action+i, " -> SKIP) ||| ");
 	                    } else {
 	                        adUtils.oe(alphabet, action, oe, "?"+action+i, " -> SKIP)");
 	                    }
                    }

                }
            }

            if (outFlows.length > 0 || outPins.length > 0) {
                action.append("); ");
            }

            action.append(nameAction + "\n");

            action.append(nameActionTermination + " = ");

            if (namesMemoryLocal.size() > 0) {
                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                    action.append("(");
                }
                action.append("(" + nameAction + " /\\ " + endDiagram + ") ");

                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                    action.append("[|{|");
                    action.append("get_" + typeMemoryLocal.get(namesMemoryLocal.get(i)) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                    action.append("set_" + typeMemoryLocal.get(namesMemoryLocal.get(i)) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                    action.append("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()));
                    action.append("|}|] ");

                    //String typeObj = parameterNodesInput.get(typeMemoryLocal.get(namesMemoryLocal.get(i)));
                    String typeObj = typeMemoryLocal.get(namesMemoryLocal.get(i));
                    //String typeObj = null;
//                    if (typeObj == null) {
//                        typeObj = typeUnionList.get(typeMemoryLocal.get(namesMemoryLocal.get(i)));
//                    }

                    action.append("Mem_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + "_" + typeMemoryLocal.get(namesMemoryLocal.get(i)) + "_t(" + adUtils.getDefaultValue(typeObj) + ")) ");
                }

                action.append("\\{|");

                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                    if (i == namesMemoryLocal.size() - 1) {
                        action.append("get_" + typeMemoryLocal.get(namesMemoryLocal.get(i)) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                        action.append("set_" + typeMemoryLocal.get(namesMemoryLocal.get(i)) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()));
                    } else {
                        action.append("get_" + typeMemoryLocal.get(namesMemoryLocal.get(i)) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                        action.append("set_" + typeMemoryLocal.get(namesMemoryLocal.get(i)) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                    }
                }

                action.append("|}\n");

            } else {
                action.append(nameAction + " /\\ " + endDiagram + "\n");
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

            nodes.append(action.toString());
        } else if (code == 1) {
            String definition = activityNode.getDefinition();
            String[] definitionFinal = new String[0];

            if (outFlows.length > 0 || outPins.length > 0) {
                action.append("(");
            }

            for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                String ce = adUtils.createCE();
                syncChannelsEdge.put(outFlows[i].getId(), ce);

                action.append("(");

                if (i >= 0 && (i < outFlows.length - 1 || outPins.length > 0)) {
                    adUtils.ce(alphabet, action, ce, " -> SKIP) ||| ");
                } else {
                    adUtils.ce(alphabet, action, ce, " -> SKIP)");
                }
            }

            String nameObject = "";
            //String lastName = "";
//
//            ArrayList<String> union = new ArrayList<>();
//            List<String> nameObjects = new ArrayList<>();
//            List<String> nodesAdded = new ArrayList<>();

//            for (int i = 0; i < inPins.length; i++) {
//                IFlow[] inFlowPin = inPins[i].getIncomings();
//                for (int x = 0; x < inFlowPin.length; x++) {
//
//                    nameObjects.addAll(adUtils.getObjects(inFlowPin[x], nodesAdded));
//
//                }
//            }

//            for (String nameObj : nameObjects) {
//                if (!union.contains(nameObj)) {
//                    nameObject += nameObj;
//                    union.add(nameObj);
//                    //lastName = nameObj;
//                }
//            }

//            if (union.size() > 1) {
//                unionList.add(union);
//                typeUnionList.put(nameObject, parameterNodesInput.get(lastName));
//            }

            for (int i = 0; i < outPins.length; i++) {    //creates the parallel output channels
                IFlow[] outFlowPin = outPins[i].getOutgoings();

                for (int x = 0; x < outFlowPin.length; x++) {
                    nameObject = outPins[i].getBase().getName();

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
                    //TODO tentar corrigir
                    action.append("(");
                    if(value !=null && !value.equals("")) {
	                    if (i >= 0 && (i < outPins.length - 1 || x < outFlowPin.length - 1)) {
	                        adUtils.oe(alphabet, action, oe, "!(" + value + ")", " -> SKIP) ||| ");
	                    } else {
	                        adUtils.oe(alphabet, action, oe, "!(" + value + ")", " -> SKIP)");
	                    }
                    }
                    else {
                    	 if (i >= 0 && (i < outPins.length - 1 || x < outFlowPin.length - 1)) {
 	                        adUtils.oe(alphabet, action, oe, "?"+action+i, " -> SKIP) ||| ");
 	                    } else {
 	                        adUtils.oe(alphabet, action, oe, "?"+action+i, " -> SKIP)");
 	                    }
                    }

                }
            }

            if (outFlows.length > 0 || outPins.length > 0) {
                action.append("); ");
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


            action.append(nameAction + " = ");


            action.append("(");
            for (int i = 0; i < inFlows.length; i++) {
                if (syncChannelsEdge.containsKey(inFlows[i].getId())) {
                    String ceIn = syncChannelsEdge.get(inFlows[i].getId());

                    action.append("(");
                    if (i >= 0 && (i < inFlows.length - 1 || inPins.length > 0)) {
                        adUtils.ce(alphabet, action, ceIn, " -> SKIP) ||| ");
                    } else {
                        adUtils.ce(alphabet, action, ceIn, " -> SKIP)");
                    }
                }
            }

            for (int i = 0; i < inPins.length; i++) {
                IFlow[] inFlowPin = inPins[i].getIncomings();
                for (int x = 0; x < inFlowPin.length; x++) {
                    if (syncObjectsEdge.containsKey(inFlowPin[x].getId())) {
                        String oeIn = syncObjectsEdge.get(inFlowPin[x].getId());
                        //String typeNameObject = objectEdges.get(oeIn);
                        String nameObject = inPins[i].getName();

                        action.append("(");
                        if (i >= 0 && (i < inPins.length - 1 || x < inFlowPin.length - 1)) {
                            adUtils.oe(alphabet, action, oeIn, "?" + nameObject, " -> ");
                            adUtils.setLocalInput(alphabet, action, inPins[i].getBase().getName(), adUtils.nameDiagramResolver(activityNode.getName()), nameObject, oeIn);
                            action.append("SKIP) ||| ");
                        } else {
                            adUtils.oe(alphabet, action, oeIn, "?" + nameObject, " -> ");
                            adUtils.setLocalInput(alphabet, action, inPins[i].getBase().getName(), adUtils.nameDiagramResolver(activityNode.getName()), nameObject, oeIn);
                            action.append("SKIP)");
                        }

                        if (!namesMemoryLocal.contains(nameObject)) {
                            namesMemoryLocal.add(nameObject);
                            typeMemoryLocal.put(nameObject, inPins[i].getBase().getName());
                        }
                    }
                }
            }

            action.append("); ");

            //adUtils.lock(alphabet, action, 0, nameAction);
            adUtils.event(alphabet, nameAction, action);

            for (int i = 0; i < namesMemoryLocal.size(); i++) {
                for (int j = 0; j < definitionFinal.length; j++) {
                    String[] expression = definitionFinal[j].split("=");
                    if (expression[0].equals(namesMemoryLocal.get(i))) {
                        List<String> expReplaced = adUtils.replaceExpression(expression[1]);    //get expression replace '+','-','*','/'
                        for (String value : expReplaced) {                //get all parts
                            for (int x = 0; x < namesMemoryLocal.size(); x++) {
                                if (value.equals(namesMemoryLocal.get(x))) {
                                    adUtils.getLocal(alphabet, action, typeMemoryLocal.get(namesMemoryLocal.get(x)), adUtils.nameDiagramResolver(activityNode.getName()), namesMemoryLocal.get(x));
                                }
                            }
                        }

                        adUtils.setLocal(alphabet, action, expression[0], adUtils.nameDiagramResolver(activityNode.getName()), "(" + expression[1] + ")");

                    }
                }
            }

            //count outFlowsPin
            for (int i = 0; i < inPins.length; i++) {
                countInFlowPin += inPins[i].getIncomings().length;
            }

            for (int i = 0; i < outPins.length; i++) {
                countOutFlowPin += outPins[i].getOutgoings().length;
            }

            //adUtils.lock(alphabet, action, 1, nameAction);
            adUtils.update(alphabet, action, inFlows.length + countInFlowPin, outFlows.length + countOutFlowPin, false);

            for (String nameObj : namesMemoryLocal) {
                adUtils.getLocal(alphabet, action, typeMemoryLocal.get(nameObj), adUtils.nameDiagramResolver(activityNode.getName()), nameObj);
            }

            if (outFlows.length > 0 || outPins.length > 0) {
                action.append("(");
            }

            for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                String ce = syncChannelsEdge.get(outFlows[i].getId());

                action.append("(");

                if (i >= 0 && (i < outFlows.length - 1 || outPins.length > 0)) {
                    adUtils.ce(alphabet, action, ce, " -> SKIP) ||| ");
                } else {
                    adUtils.ce(alphabet, action, ce, " -> SKIP)");
                }
            }

            String nameObject = "";

//            for (int i = 0; i < inPins.length; i++) {
//                IFlow[] inFlowPin = inPins[i].getIncomings();
//                for (int x = 0; x < inFlowPin.length; x++) {
//                    String channel = syncObjectsEdge.get(inFlowPin[x].getId());
//                    nameObject += objectEdges.get(channel);
//                }
//            }

            for (int i = 0; i < outPins.length; i++) {    //creates the parallel output channels
                IFlow[] outFlowPin = outPins[i].getOutgoings();

                for (int x = 0; x < outFlowPin.length; x++) {
                    nameObject = outPins[i].getBase().getName();

                    String oe = syncObjectsEdge.get(outFlowPin[x].getId());

                    String value = "";
                    for (int j = 0; j < definitionFinal.length; j++) {
                        String[] expression = definitionFinal[j].split("=");
                        if (expression[0].equals(outPins[i].getName())) {
                            value = expression[1];
                        }
                    }

                    //String typeObj = parameterNodesInput.get(nameObject);
//                    if (typeObj == null) {
//                        typeObj = typeUnionList.get(nameObject);
//                    }

                    String typeObj = nameObject;


                    Pair<String, String> initialAndFinalParameterValue = adUtils.getInitialAndFinalParameterValue(typeObj);

                    if ((value != null && !value.equals("")) && ADUtils.isInteger(initialAndFinalParameterValue.getKey())) {
                        action.append("((");
                        action.append("(" + value + ") >= " + initialAndFinalParameterValue.getKey() + " and (" + value + ") <= "  + initialAndFinalParameterValue.getValue() + ") & ");
                    } else {
                        action.append("(");
                    }
                    if(value !=null && !value.equals("")) {
	                    if (i >= 0 && (i < outPins.length - 1 || x < outFlowPin.length - 1)) {
	                        adUtils.oe(alphabet, action, oe, "!(" + value + ")", " -> SKIP) ||| ");
	                    } else {
	                        adUtils.oe(alphabet, action, oe, "!(" + value + ")", " -> SKIP)");
	                    }
                    }
                    else {
                    	 if (i >= 0 && (i < outPins.length - 1 || x < outFlowPin.length - 1)) {
 	                        adUtils.oe(alphabet, action, oe, "?"+action+i, " -> SKIP) ||| ");
 	                    } else {
 	                        adUtils.oe(alphabet, action, oe, "?"+action+i, " -> SKIP)");
 	                    }
                    }

                }
            }

            if (outFlows.length > 0 || outPins.length > 0) {
                action.append("); ");
            }

            action.append(nameAction + "\n");

            action.append(nameActionTermination + " = ");

            if (namesMemoryLocal.size() > 0) {
                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                    action.append("(");
                }
                action.append("(" + nameAction + " /\\ " + endDiagram + ") ");

                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                    action.append("[|{|");
                    action.append("get_" + typeMemoryLocal.get(namesMemoryLocal.get(i)) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                    action.append("set_" + typeMemoryLocal.get(namesMemoryLocal.get(i)) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                    action.append("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()));
                    action.append("|}|] ");

                    //String typeObj = parameterNodesInput.get(typeMemoryLocal.get(namesMemoryLocal.get(i)));
                    String typeObj = typeMemoryLocal.get(namesMemoryLocal.get(i));
//                    if (typeObj == null) {
//                        typeObj = typeUnionList.get(typeMemoryLocal.get(namesMemoryLocal.get(i)));
//                    }

                    action.append("Mem_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + "_" + typeMemoryLocal.get(namesMemoryLocal.get(i)) + "_t(" + adUtils.getDefaultValue(typeObj) + ")) ");
                }

                action.append("\\{|");

                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                    if (i == namesMemoryLocal.size() - 1) {
                        action.append("get_" + typeMemoryLocal.get(namesMemoryLocal.get(i)) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                        action.append("set_" + typeMemoryLocal.get(namesMemoryLocal.get(i)) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()));
                    } else {
                        action.append("get_" + typeMemoryLocal.get(namesMemoryLocal.get(i)) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                        action.append("set_" + typeMemoryLocal.get(namesMemoryLocal.get(i)) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                    }
                }

                action.append("|}\n");

            } else {
                action.append(nameAction + " /\\ " + endDiagram + "\n");
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

            nodes.append(action.toString());
        }

        return activityNode;
    }
}
