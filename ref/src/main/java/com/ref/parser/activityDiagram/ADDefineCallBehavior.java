package com.ref.parser.activityDiagram;

import com.change_vision.jude.api.inf.model.*;
import com.ref.exceptions.ParsingException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ADDefineCallBehavior {

    private IActivity ad;

    private HashMap<Pair<IActivity,String>, ArrayList<String>> alphabetNode;
    private HashMap<Pair<IActivity,String>, String> syncChannelsEdge;
    private HashMap<Pair<IActivity,String>, String> syncObjectsEdge;
    private HashMap<String, String> objectEdges;
    private List<IActivityNode> queueNode;
    /*private HashMap<String, String> parameterNodesInput;
    private List<ArrayList<String>> unionList;
    private HashMap<String, String> typeUnionList;*/
    private ADUtils adUtils;

    public ADDefineCallBehavior(IActivity ad, HashMap<Pair<IActivity, String>, ArrayList<String>> alphabetNode2, HashMap<Pair<IActivity, String>, String> syncChannelsEdge2,
                                HashMap<Pair<IActivity, String>, String> syncObjectsEdge2, HashMap<String, String> objectEdges, List<IActivityNode> queueNode,
                                List<IActivity> callBehaviourList, HashMap<String, String> parameterNodesInput, List<ArrayList<String>> unionList,
                                HashMap<String, String> typeUnionList, ADUtils adUtils) {
        this.ad = ad;
        this.alphabetNode = alphabetNode2;
        this.syncChannelsEdge = syncChannelsEdge2;
        this.syncObjectsEdge = syncObjectsEdge2;
        this.objectEdges = objectEdges;
        this.queueNode = queueNode;
        /*this.parameterNodesInput = parameterNodesInput;
        this.unionList = unionList;
        this.typeUnionList = typeUnionList;*/
        this.adUtils = adUtils;
    }

    public IActivityNode defineCallBehaviour(IActivityNode activityNode, StringBuilder nodes, int code) throws ParsingException {    //Ainda nao testado
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
    	
        ADDefineMemories.CBAMemAlphabet.put(activityNode,((IAction)activityNode).getCallingActivity());

        for (int i = 0; i < outPins.length; i++) {
            namesOutpins.add(outPins[i].getName());
        }


        if (code == 0) {
            /*String definition = activityNode.getDefinition();
            String[] definitionFinal = new String[0];

            if (definition != null && !(definition.equals(""))) {
                definitionFinal = definition.replace(" ", "").split(";");
            }*/


            callBehaviour.append(nameCallBehaviour + "(id) = ");


            callBehaviour.append("(");
            for (int i = 0; i < inFlows.length; i++) {
            	Pair<IActivity,String> key = new Pair<IActivity, String>(ad, inFlows[i].getId());
                if (syncChannelsEdge.containsKey(key)) {
                    String ceIn = syncChannelsEdge.get(key);

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
                	Pair<IActivity,String> key = new Pair<IActivity, String>(ad, inFlowPin[x].getId());
                    if (syncObjectsEdge.containsKey(key)) {
                        String oeIn = syncObjectsEdge.get(key);
                        //String typeNameObject = objectEdges.get(oeIn);
                        String typeNameObject = inPins[i].getBase().getName();
                        String nameObject = inPins[i].getName();

                        callBehaviour.append("(");
                        if (i >= 0 && (i < inPins.length - 1 || x < inFlowPin.length - 1)) {
                            adUtils.oe(alphabet, callBehaviour, oeIn, "?" + nameObject, " -> ");
                            adUtils.setLocalInput(alphabet, callBehaviour, inPins[i].getName(), adUtils.nameDiagramResolver(activityNode.getName()), nameObject, oeIn, inPins[i].getBase().getName());
                            callBehaviour.append("SKIP) ||| ");
                        } else {
                            adUtils.oe(alphabet, callBehaviour, oeIn, "?" + nameObject, " -> ");
                            adUtils.setLocalInput(alphabet, callBehaviour, inPins[i].getName(), adUtils.nameDiagramResolver(activityNode.getName()), nameObject, oeIn, inPins[i].getBase().getName());
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
                adUtils.getLocal(alphabet, callBehaviour, nameObj, adUtils.nameDiagramResolver(activityNode.getName()), nameObj, typeMemoryLocal.get(nameObj));
            }
           
            try {
                	((IAction) activityNode).getCallingActivity().getActivityDiagram().getName();
				} catch (Exception e) {
					throw new ParsingException("The Call Behavior Action "+activityNode.getName()+" is unlinked with other diagram\n");
				}
/*
        	String stereotype = activityNode.getStereotypes()[0];
        	
        	if (stereotype.equals("ANY")) {
//    			passar o conjunto de eventos do robo
//        		callBehaviour.append("nome_do_processo_CHAOS");
        		return null;
    		} 
*/
            callBehavior(alphabet, callBehaviour, ((IAction) activityNode).getCallingActivity().getActivityDiagram().getName(), namesMemoryLocal, namesOutpins,activityNode);
           
            adUtils.update(alphabet, callBehaviour, inFlows.length + countInFlowPin, outFlows.length + countOutFlowPin, false);

            if (outFlows.length > 0 || outPins.length > 0) {
                callBehaviour.append("(");
            }

            for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                String ce = adUtils.createCE();
                Pair<IActivity,String> key = new Pair<IActivity, String>(ad, outFlows[i].getId());
                syncChannelsEdge.put(key, ce);

                callBehaviour.append("(");

                if (i >= 0 && (i < outFlows.length - 1 || outPins.length > 0)) {
                    adUtils.ce(alphabet, callBehaviour, ce, " -> SKIP) ||| ");
                } else {
                    adUtils.ce(alphabet, callBehaviour, ce, " -> SKIP)");
                }
            }
            
            
            
            String typeObject = "";
            //String lastName = "";
            //ArrayList<String> union = new ArrayList<>();

//            for (int i = 0; i < inPins.length; i++) {
//                IFlow[] inFlowPin = inPins[i].getIncomings();
//                for (int x = 0; x < inFlowPin.length; x++) {
//                    String channel = syncObjectsEdge.get(inFlowPin[x].getId());
//                    nameObject += objectEdges.get(channel);
//                    //union.add(objectEdges.get(channel));
//                    //lastName = objectEdges.get(channel);
//                }
//            }
//
//            if (union.size() > 1) {
//                unionList.add(union);
//                typeUnionList.put(nameObject, parameterNodesInput.get(lastName));
//            }

            for (int i = 0; i < outPins.length; i++) {    //creates the parallel output channels
                IFlow[] outFlowPin = outPins[i].getOutgoings();

                for (int x = 0; x < outFlowPin.length; x++) {
                    typeObject = outPins[i].getBase().getName();              
                    String oe = adUtils.createOE(typeObject);
                    Pair<IActivity,String> key = new Pair<IActivity, String>(ad, outFlowPin[x].getId());
                    syncObjectsEdge.put(key, oe);

                    objectEdges.put(oe, typeObject);
//                    String value = "";
//                    for (int j = 0; j < definitionFinal.length; j++) {
//                        String[] expression = definitionFinal[j].split("=");
//                        if (expression[0].equals(outPins[i].getName())) {
//                            value = expression[1];
//                        }
//                    }

                    callBehaviour.append("(");
                    if (i >= 0 && (i < outPins.length - 1 || x < outFlowPin.length - 1)) {
                    	//TODO ver se esta certo mesmo
                    	adUtils.getLocal(alphabet, callBehaviour, outPins[i].getName(), activityNode.getName(), outPins[i].getName(),typeObject);
                    	//adUtils.get(alphabet, callBehaviour, outPins[i].getName());
                        adUtils.oe(alphabet, callBehaviour, oe, "!(" + outPins[i].getName() + ")", " -> SKIP) ||| ");
                    } else {
                    	//TODO ver se esta certo mesmo
                    	adUtils.getLocal(alphabet, callBehaviour, outPins[i].getName(), activityNode.getName(), outPins[i].getName(),typeObject);
                    	//adUtils.get(alphabet, callBehaviour, outPins[i].getName());
                        adUtils.oe(alphabet, callBehaviour, oe, "!(" + outPins[i].getName() + ")", " -> SKIP)");
                    }

                }
            }

            if (outFlows.length > 0 || outPins.length > 0) {
                callBehaviour.append("); ");
            }

            callBehaviour.append(nameCallBehaviour+"(id)\n");

            callBehaviour.append(namCallBehaviourTermination + "(id) = ");

            if (namesMemoryLocal.size() > 0) {
                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                    callBehaviour.append("(");
                }
                callBehaviour.append("("+nameCallBehaviour+"(id)) ");
                for(int i = 0; i < namesMemoryLocal.size(); i++) {
                	callBehaviour.append("[|AlphabetMem"+nameCallBehaviour+"(id)|] "
                						+"Mem_"+nameCallBehaviour+"(id)) \\diff(AlphabetMem"+nameCallBehaviour
                						+"(id),{|endDiagram_"+adUtils.nameDiagramResolver(ad.getName())+".id|}) /\\ "+ endDiagram+ "(id)\n");
                }
                /*callBehaviour.append("(" + nameCallBehaviour + "(id) /\\ " + endDiagram + "(id)) ");

                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                    callBehaviour.append("[|{|");
                    callBehaviour.append("get_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                    callBehaviour.append("set_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                    callBehaviour.append("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()));
                    callBehaviour.append("|}|] ");

                    String typeObj = typeMemoryLocal.get(namesMemoryLocal.get(i));
//                    if (typeObj == null) {
//                        typeObj = typeUnionList.get(typeMemoryLocal.get(namesMemoryLocal.get(i)));
//                    }

                    callBehaviour.append("Mem_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + "_" + namesMemoryLocal.get(i) + "_t(id," + adUtils.getDefaultValue(typeObj) + ")) ");
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

                callBehaviour.append("|}\n");*/

            } else {
                callBehaviour.append(nameCallBehaviour + "(id) /\\ " + endDiagram + "(id)\n");
            }

            alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName())+".id");
            Pair<IActivity,String> key = new Pair<IActivity, String>(ad,adUtils.nameDiagramResolver(activityNode.getName()));
            alphabetNode.put(key, alphabet);

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
            //String definition = activityNode.getDefinition();
            //String[] definitionFinal = new String[0];

            if (outFlows.length > 0 || outPins.length > 0) {
                callBehaviour.append("(");
            }

            for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                String ce = adUtils.createCE();
                Pair<IActivity,String> key = new Pair<IActivity, String>(ad, outFlows[i].getId());
                syncChannelsEdge.put(key, ce);

                callBehaviour.append("(");

                if (i >= 0 && (i < outFlows.length - 1 || outPins.length > 0)) {
                    adUtils.ce(alphabet, callBehaviour, ce, " -> SKIP) ||| ");
                } else {
                    adUtils.ce(alphabet, callBehaviour, ce, " -> SKIP)");
                }
            }

            String nameObject = "";
            //String lastName = "";

            //ArrayList<String> union = new ArrayList<>();
//            List<String> nameObjects = new ArrayList<>();
//            List<String> nodesAdded = new ArrayList<>();
//
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
//                    lastName = nameObj;
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
                    Pair<IActivity,String> key = new Pair<IActivity, String>(ad, outFlowPin[x].getId());
                    syncObjectsEdge.put(key, oe);

                    objectEdges.put(oe, nameObject);
//                    String value = "";
//                    for (int j = 0; j < definitionFinal.length; j++) {
//                        String[] expression = definitionFinal[j].split("=");
//                        if (expression[0].equals(outPins[i].getName())) {
//                            value = expression[1];
//                        }
//                    }

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
            //String definition = activityNode.getDefinition();
            //String[] definitionFinal = new String[0];

//            if (definition != null && !(definition.equals(""))) {
//                definitionFinal = definition.replace(" ", "").split(";");
//            }


            callBehaviour.append(nameCallBehaviour + "(id) = ");


            callBehaviour.append("(");
            for (int i = 0; i < inFlows.length; i++) {
            	Pair<IActivity,String> key = new Pair<IActivity, String>(ad, inFlows[i].getId());
                if (syncChannelsEdge.containsKey(key)) {
                    String ceIn = syncChannelsEdge.get(key);

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
                	Pair<IActivity,String> key = new Pair<IActivity, String>(ad, inFlowPin[x].getId());
                    if (syncObjectsEdge.containsKey(key)) {
                        String oeIn = syncObjectsEdge.get(key);
                        //String typeNameObject = objectEdges.get(oeIn);
                        String typeNameObject = inPins[i].getBase().getName();
                        String nameObject = inPins[i].getName();

                        callBehaviour.append("(");
                        if (i >= 0 && (i < inPins.length - 1 || x < inFlowPin.length - 1)) {
                            adUtils.oe(alphabet, callBehaviour, oeIn, "?" + nameObject, " -> ");
                            adUtils.setLocalInput(alphabet, callBehaviour, inPins[i].getName(), adUtils.nameDiagramResolver(activityNode.getName()), nameObject, oeIn,inPins[i].getBase().getName());
                            callBehaviour.append("SKIP) ||| ");
                        } else {
                            adUtils.oe(alphabet, callBehaviour, oeIn, "?" + nameObject, " -> ");
                            adUtils.setLocalInput(alphabet, callBehaviour, inPins[i].getName(), adUtils.nameDiagramResolver(activityNode.getName()), nameObject, oeIn,inPins[i].getBase().getName());
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
                adUtils.getLocal(alphabet, callBehaviour, nameObj, adUtils.nameDiagramResolver(activityNode.getName()), nameObj,typeMemoryLocal.get(nameObj));
            }

            int count = adUtils.startActivity(alphabet, callBehaviour, ((IAction) activityNode).getCallingActivity().getActivityDiagram().getName(), namesMemoryLocal);
            adUtils.endActivity(alphabet, callBehaviour, ((IAction) activityNode).getCallingActivity().getActivityDiagram().getName(), namesOutpins, count);

            adUtils.update(alphabet, callBehaviour, inFlows.length + countInFlowPin, outFlows.length + countOutFlowPin, false);

            if (outFlows.length > 0 || outPins.length > 0) {
                callBehaviour.append("(");
            }

            for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
            	Pair<IActivity,String> key = new Pair<IActivity, String>(ad, outFlows[i].getId());
            	String ce = syncChannelsEdge.get(key);

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
                	Pair<IActivity,String> key = new Pair<IActivity, String>(ad, outFlowPin[x].getId());
                    String oe = syncObjectsEdge.get(key);

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

            callBehaviour.append(nameCallBehaviour + "(id)\n");

            callBehaviour.append(namCallBehaviourTermination + "(id) = ");

            if (namesMemoryLocal.size() > 0) {
                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                    callBehaviour.append("(");
                }
                callBehaviour.append("(" + nameCallBehaviour + "(id) /\\ " + endDiagram + "(id)) ");

                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                    callBehaviour.append("[|{|");
                    callBehaviour.append("get_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                    callBehaviour.append("set_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                    callBehaviour.append("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()));
                    callBehaviour.append("|}|] ");

                    String typeObj = typeMemoryLocal.get(namesMemoryLocal.get(i));
//                    if (typeObj == null) {
//                        typeObj = typeUnionList.get(typeMemoryLocal.get(namesMemoryLocal.get(i)));
//                    }

                    callBehaviour.append("Mem_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + "_" + namesMemoryLocal.get(i) + "_t(id," + adUtils.getDefaultValue(typeObj) + ")) ");
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
            Pair<IActivity,String> key = new Pair<IActivity, String>(ad, adUtils.nameDiagramResolver(activityNode.getName()));
            alphabetNode.put(key, alphabet);

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
    
    private void callBehavior(ArrayList<String> alphabetNode, StringBuilder action, String nameAD, List<String> inputPins, List<String> outputPins,IActivityNode activityNode) {
    	
    	int count = 0;
    	count = adUtils.addCountCall(adUtils.nameDiagramResolver(nameAD));
    	String Activity = "";
    	//String getInput ="getInputParam"+nameDiagramResolver(nameAD); 
    	String setMem = "setMemOutParam"+adUtils.nameDiagramResolver(nameAD);	
    	String startAct = "startActivity_" + adUtils.nameDiagramResolver(nameAD) + "." + count;
    	String endAct = "endActivity_" + adUtils.nameDiagramResolver(nameAD) + "." + count;
    	String id = ((IAction) activityNode).getCallingActivity().getId();
    	List<Pair<String,String>> CBAList = ADParser.countcallBehavior.get(id);//pega a list com todos os nos que chamam esse cba
    	int index = 1;
    	for(int i=0;i<CBAList.size();i++) {//varre a lista atrás do indice desse nó
    		if(activityNode.getId().equals(CBAList.get(i).getKey())) {
    			index = i+1;
    		}
    	}
    	
    	alphabetNode.add(startAct);
    	alphabetNode.add(endAct);
    	adUtils.getCallBehaviourNumber().add(new Pair<>(adUtils.nameDiagramResolver(nameAD), count));
    	
    	List<String> outputPinsUsed = adUtils.getCallBehaviourOutputs().get(adUtils.nameDiagramResolver(nameAD));
        if (outputPinsUsed == null) {
            outputPinsUsed = inputPins;
            HashMap<String,List<String>> aux = adUtils.getCallBehaviourInputs();
            aux.put(nameAD, inputPins);
            adUtils.setCallBehaviourInputs(aux);
        }
        
        if(!outputPinsUsed.isEmpty()) {
        	//alphabetNode.add(getInput);
        	for (String pin : outputPinsUsed) {
            	//getInput += "?" + pin;
                Activity += "!" + pin;
            }
            //getInput += " -> ";
        	action.append(/*getInput+*/"(");
            action.append("normal("+adUtils.nameDiagramResolver(nameAD)+"("+index+")) [|{|"+startAct+","+endAct+"|}|] (");
        }else {
        	action.append("normal("+adUtils.nameDiagramResolver(nameAD)+"("+index+"))");
        }

        
        action.append((Activity != ""?startAct+Activity + " -> ":""));
        
        Activity = "";	
        
        outputPinsUsed = adUtils.getCallBehaviourOutputs().get(adUtils.nameDiagramResolver(nameAD));
        if (outputPinsUsed == null) {
            outputPinsUsed = outputPins;
        	HashMap<String,List<String>> aux =	adUtils.getCallBehaviourOutputs();
            aux.put(nameAD, outputPins);
            adUtils.setCallBehaviourOutputs(aux);
        }
        
        for (String pin : outputPinsUsed) {
            Activity += "?" + pin;
            setMem += "!" + pin;
        }
        
        action.append((Activity != ""?endAct+Activity + " -> ":""));
        for(IOutputPin pin : ((IAction)activityNode).getOutputs()) {
        	adUtils.setLocal(alphabetNode,action,pin.getName(),adUtils.nameDiagramResolver(activityNode.getName()),pin.getName(),pin.getBase().getName()); //TODO adicionar um setLocal que pegue aqui	
        }
        action.append(!setMem.equals("setMemOutParam"+adUtils.nameDiagramResolver(nameAD))?"SKIP));":";");
        
    }
}
