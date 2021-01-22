package com.ref.parser.activityDiagram;

import com.change_vision.jude.api.inf.model.*;
import com.ref.exceptions.ParsingException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ADDefineAction {

    private IActivity ad;

    private HashMap<Pair<IActivity,String>, ArrayList<String>> alphabetNode;
    private HashMap<Pair<IActivity,String>, String> syncChannelsEdge;
    private HashMap<Pair<IActivity,String>, String> syncObjectsEdge;
    private HashMap<String, String> objectEdges;
    private List<IActivityNode> queueNode;
    //private HashMap<String, String> parameterNodesInput;
    //private List<ArrayList<String>> unionList;
    //private HashMap<String, String> typeUnionList;
    private ADUtils adUtils;
    //private ADParser adParser;

    public ADDefineAction(IActivity ad, HashMap<Pair<IActivity, String>, ArrayList<String>> alphabetNode2, HashMap<Pair<IActivity, String>, String> syncChannelsEdge2,
                          HashMap<Pair<IActivity, String>, String> syncObjectsEdge2, HashMap<String, String> objectEdges, List<IActivityNode> queueNode,
                          HashMap<String, String> parameterNodesInput, List<ArrayList<String>> unionList, HashMap<String, String> typeUnionList,
                          ADUtils adUtils, ADParser adParser) {
        this.ad = ad;
        this.alphabetNode = alphabetNode2;
        this.syncChannelsEdge = syncChannelsEdge2;
        this.syncObjectsEdge = syncObjectsEdge2;
        this.objectEdges = objectEdges;
        this.queueNode = queueNode;
        //this.parameterNodesInput = parameterNodesInput;
        //this.unionList = unionList;
        //this.typeUnionList = typeUnionList;
        this.adUtils = adUtils;
        //this.adParser = adParser;
    }

    public IActivityNode defineAction(IActivityNode activityNode, StringBuilder nodes, int code) throws ParsingException { 	
        StringBuilder action = new StringBuilder();
        ArrayList<String> alphabet = new ArrayList<>();
        String nameEvent = adUtils.nameRobochartResolver(activityNode.getName());
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
        if(Character.isDigit(nameAction.charAt(0))) {//TODO ver se isso é certo mesmo
        	throw new ParsingException("The node name "+adUtils.nameDiagramResolver(activityNode.getName())+" starts with a number\n");
        }
        if (code == 0) {
            String definition = activityNode.getDefinition();
            String[] definitionFinal = new String[0];

            if (definition != null && !(definition.equals(""))) {
                definitionFinal = definition.replace(" ", "").split(";");
            }

            action.append(nameAction + "(id) = ");
            action.append("(");
            for (int i = 0; i < inFlows.length; i++) {  
//            	System.out.println(inFlows[i].getStereotypes()[0]);
                Pair<IActivity,String> key = new Pair<IActivity, String>(ad, inFlows[i].getId());
                if (syncChannelsEdge.containsKey(key)) {
                    String ceIn = syncChannelsEdge.get(key);//TODO

                    action.append("(");
                    if (i >= 0 && (i < inFlows.length - 1 || inPins.length > 0)) {
                        adUtils.ce(alphabet, action, ceIn, " -> SKIP) ||| ");
                    } else {
                        adUtils.ce(alphabet, action, ceIn, " -> SKIP)");
                    }
                }
            }
            
//            if (inFlows.length > 0) {
//                for (int i = 0; i < inFlows.length; i++) {
//            	Pair<IActivity,String> key = new Pair<IActivity, String>(ad,inFlows[i].getId());
//                    if (syncChannelsEdge.containsKey(key)) {
//	                    for (int j = 0; j < inFlows.length; j++) {
//	                         String untilIn = inFlows[i].getStereotypes()[j];							
//	                         if (i >= 0 && (i < inFlows.length - 1)) {
//	                               adUtils.until(alphabet, action, untilIn, " -> SKIP ; ||| ");
//	                         } else {
//	                               adUtils.until(alphabet, action, untilIn, " -> SKIP ; ");
//	                         }
//                     	}
//                                    
//                    }
//                }
//            }
            
            for (int i = 0; i < inPins.length; i++) {
                IFlow[] inFlowPin = inPins[i].getIncomings();
                for (int x = 0; x < inFlowPin.length; x++) {
                	Pair<IActivity,String> key = new Pair<IActivity, String>(ad, inFlowPin[x].getId());
                    if (syncObjectsEdge.containsKey(key)) {
                        String oeIn = syncObjectsEdge.get(key);
                        //String typeNameObject = objectEdges.get(oeIn);
                        String nameObject = inPins[i].getName();

                        action.append("(");
                        if (i >= 0 && (i < inPins.length - 1 || x < inFlowPin.length - 1)) {
                            adUtils.oe(alphabet, action, oeIn, "?" + nameObject, " -> ");
                            try {
								adUtils.setLocalInput(alphabet, action, inPins[i].getName(), adUtils.nameDiagramResolver(activityNode.getName()), nameObject, oeIn,inPins[i].getBase().getName());
							} catch (Exception e) {
								throw new ParsingException("Pin node "+inPins[i].getName()+" without base type\n");//TODO fix the type of exception
							}
                            action.append("SKIP) ||| ");
                        } else {
                            adUtils.oe(alphabet, action, oeIn, "?" + nameObject, " -> ");
                            try {
								adUtils.setLocalInput(alphabet, action, inPins[i].getName(), adUtils.nameDiagramResolver(activityNode.getName()), nameObject, oeIn,inPins[i].getBase().getName());
							} catch (Exception e) {
								throw new ParsingException("Pin node "+inPins[i].getName()+" without base type\n");//TODO fix the type of exception
							}
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
            
            if (inFlows.length == 1 && inFlows[0].getStereotypes().length > 0 && inFlows[0].getStereotypes()[0].equals("UNTIL")) {
    			adUtils.until(alphabet, action, nameEvent, " -> SKIP; ");
            } else {
            	adUtils.event(alphabet, nameEvent, action);//TODO
            }

            //adUtils.lock(alphabet, action, 0, nameAction);
//            adUtils.event(alphabet, nameEvent, action);//TODO
//            adUtils.event(alphabet, nameAction, action);//TODO

            for (int i = 0; i < namesMemoryLocal.size(); i++) {
                for (int j = 0; j < definitionFinal.length; j++) {
                    String[] expression = definitionFinal[j].split("=");
                    if (expression[0].equals(namesMemoryLocal.get(i))) {
                        List<String> expReplaced = adUtils.replaceExpression(expression[1]);    //get expression replace '+','-','*','/'
                        for (String value : expReplaced) {                //get all parts
                            for (int x = 0; x < namesMemoryLocal.size(); x++) {
                                if (value.equals(namesMemoryLocal.get(x))) {
                                    adUtils.getLocal(alphabet, action, namesMemoryLocal.get(x), adUtils.nameDiagramResolver(activityNode.getName()), namesMemoryLocal.get(x),typeMemoryLocal.get(namesMemoryLocal.get(x)));
                                }
                            }
                        }

                        adUtils.setLocal(alphabet, action, expression[0], adUtils.nameDiagramResolver(activityNode.getName()), "(" + expression[1] + ")",expression[0]);

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
                adUtils.getLocal(alphabet, action, nameObj, adUtils.nameDiagramResolver(activityNode.getName()), nameObj,typeMemoryLocal.get(nameObj));
            }

            if (outFlows.length > 0 || outPins.length > 0) {
                action.append("(");
            }

            for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                String ce = adUtils.createCE();
                Pair<IActivity,String> pair = new Pair<IActivity, String>(ad, outFlows[i].getId());
                syncChannelsEdge.put(pair, ce);

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
                    
                	try {
						nameObject = outPins[i].getBase().getName();
					} catch (Exception e) {
						throw new ParsingException("Pin "+outPins[i].getName()+" without base class\n");
					}

                    String oe = adUtils.createOE(nameObject);
                    Pair<IActivity,String> pair = new Pair<IActivity, String>(ad,outFlowPin[x].getId());
                    syncObjectsEdge.put(pair, oe);

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
 	                        adUtils.oe(alphabet, action, oe, "?"+nameAction+i, " -> SKIP) ||| ");
 	                    } else {
 	                        adUtils.oe(alphabet, action, oe, "?"+nameAction+i, " -> SKIP)");
 	                    }
                    }

                }
            }

            if (outFlows.length > 0 || outPins.length > 0) {
                action.append("); ");
            }

            action.append(nameAction + "(id)\n");

            action.append(nameActionTermination + "(id) = ");

            if (namesMemoryLocal.size() > 0) {
                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                    action.append("(");
                }
                action.append("(" + nameAction + "(id) /\\ " + endDiagram + "(id)) ");

                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                    action.append("[|{|");
                    action.append("get_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ".id,");
                    action.append("set_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ".id,");
                    action.append("endDiagram_" + adUtils.nameDiagramResolver(ad.getName())+".id");
                    action.append("|}|] ");

                    //String typeObj = parameterNodesInput.get(typeMemoryLocal.get(namesMemoryLocal.get(i)));
                    String typeObj = typeMemoryLocal.get(namesMemoryLocal.get(i));
                    //String typeObj = null;
//                    if (typeObj == null) {
//                        typeObj = typeUnionList.get(typeMemoryLocal.get(namesMemoryLocal.get(i)));
//                    }

                    action.append("Mem_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + "_" + namesMemoryLocal.get(i) + "_t(id," + adUtils.getDefaultValue(typeObj) + ")) ");
                }

                action.append("\\{|");

                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                    if (i == namesMemoryLocal.size() - 1) {
                        action.append("get_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ".id,");
                        action.append("set_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) +".id");
                    } else {
                        action.append("get_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ".id,");
                        action.append("set_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ".id,");
                    }
                }

                action.append("|}\n");

            } else {
                action.append(nameAction + "(id) /\\ " + endDiagram + "(id)\n");
            }

            alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()+".id"));
            Pair<IActivity,String> pair = new Pair<IActivity, String>(ad,adUtils.nameDiagramResolver(activityNode.getName()));
            alphabetNode.put(pair, alphabet);

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
            //String definition = activityNode.getDefinition();
            String[] definitionFinal = new String[0];

            if (outFlows.length > 0 || outPins.length > 0) {
                action.append("(");
            }

            for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                String ce = adUtils.createCE();
                Pair<IActivity,String> pair = new Pair<IActivity, String>(ad,outFlows[i].getId());
                syncChannelsEdge.put(pair, ce);

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
                    Pair<IActivity,String> pair = new Pair<IActivity, String>(ad,outFlowPin[x].getId());
                    syncObjectsEdge.put(pair, oe);

                    objectEdges.put(oe, nameObject);
                    String value = "";
                    for (int j = 0; j < definitionFinal.length; j++) {
                        String[] expression = definitionFinal[j].split("=");
                        if (expression[0].equals(outPins[i].getName())) {
                            value = expression[1];
                        }
                    }
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
 	                        adUtils.oe(alphabet, action, oe, "?"+nameAction+i, " -> SKIP) ||| ");
 	                    } else {
 	                        adUtils.oe(alphabet, action, oe, "?"+nameAction+i, " -> SKIP)");
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


            action.append(nameAction + "(id) = ");


            action.append("(");
            for (int i = 0; i < inFlows.length; i++) {
            	Pair<IActivity,String> key = new Pair<IActivity, String>(ad, inFlows[i].getId());
                if (syncChannelsEdge.containsKey(key)) {
                    String ceIn = syncChannelsEdge.get(key);

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
                	Pair<IActivity,String> key = new Pair<IActivity, String>(ad, inFlowPin[x].getId());
                    if (syncObjectsEdge.containsKey(key)) {
                        String oeIn = syncObjectsEdge.get(key);
                        //String typeNameObject = objectEdges.get(oeIn);
                        String nameObject = inPins[i].getName();

                        action.append("(");
                        if (i >= 0 && (i < inPins.length - 1 || x < inFlowPin.length - 1)) {
                            adUtils.oe(alphabet, action, oeIn, "?" + nameObject, " -> ");
                            adUtils.setLocalInput(alphabet, action, inPins[i].getName(), adUtils.nameDiagramResolver(activityNode.getName()), nameObject, oeIn,inPins[i].getBase().getName());
                            action.append("SKIP) ||| ");
                        } else {
                            adUtils.oe(alphabet, action, oeIn, "?" + nameObject, " -> ");
                            adUtils.setLocalInput(alphabet, action, inPins[i].getName(), adUtils.nameDiagramResolver(activityNode.getName()), nameObject, oeIn,inPins[i].getBase().getName());
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
            adUtils.event(alphabet, nameEvent, action);
//            adUtils.event(alphabet, nameAction, action);

            for (int i = 0; i < namesMemoryLocal.size(); i++) {
                for (int j = 0; j < definitionFinal.length; j++) {
                    String[] expression = definitionFinal[j].split("=");
                    if (expression[0].equals(namesMemoryLocal.get(i))) {
                        List<String> expReplaced = adUtils.replaceExpression(expression[1]);    //get expression replace '+','-','*','/'
                        for (String value : expReplaced) {                //get all parts
                            for (int x = 0; x < namesMemoryLocal.size(); x++) {
                                if (value.equals(namesMemoryLocal.get(x))) {
                                    adUtils.getLocal(alphabet, action, namesMemoryLocal.get(x), adUtils.nameDiagramResolver(activityNode.getName()), namesMemoryLocal.get(x),typeMemoryLocal.get(namesMemoryLocal.get(x)));
                                }
                            }
                        }

                        adUtils.setLocal(alphabet, action, expression[0], adUtils.nameDiagramResolver(activityNode.getName()), "(" + expression[1] + ")",expression[0]);

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
                adUtils.getLocal(alphabet, action, nameObj, adUtils.nameDiagramResolver(activityNode.getName()), nameObj,typeMemoryLocal.get(nameObj));
            }

            if (outFlows.length > 0 || outPins.length > 0) {
                action.append("(");
            }

            for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
            	Pair<IActivity,String> key = new Pair<IActivity, String>(ad, outFlows[i].getId());
                String ce = syncChannelsEdge.get(key);

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
                    Pair<IActivity,String> key = new Pair<IActivity, String>(ad, outFlowPin[x].getId());
                    String oe = syncObjectsEdge.get(key);

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
 	                        adUtils.oe(alphabet, action, oe, "?"+nameAction+i, " -> SKIP) ||| ");
 	                    } else {
 	                        adUtils.oe(alphabet, action, oe, "?"+nameAction+i, " -> SKIP)");
 	                    }
                    }
                }
            }

            if (outFlows.length > 0 || outPins.length > 0) {
                action.append("); ");
            }

            action.append(nameAction + "(id)\n");

            action.append(nameActionTermination + "(id) = ");

            if (namesMemoryLocal.size() > 0) {
                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                    action.append("(");
                }
                action.append("(" + nameAction + "(id) /\\ " + endDiagram + "(id)) ");

                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                    action.append("[|{|");
                    action.append("get_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ".id,");
                    action.append("set_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ".id,");
                    action.append("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()) + ".id");
                    action.append("|}|] ");

                    //String typeObj = parameterNodesInput.get(typeMemoryLocal.get(namesMemoryLocal.get(i)));
                    String typeObj = typeMemoryLocal.get(namesMemoryLocal.get(i));
//                    if (typeObj == null) {
//                        typeObj = typeUnionList.get(typeMemoryLocal.get(namesMemoryLocal.get(i)));
//                    }

                    action.append("Mem_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + "_" + namesMemoryLocal.get(i) + "_t(id," + adUtils.getDefaultValue(typeObj) + ")) ");
                }

                action.append("\\{|");

                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                    if (i == namesMemoryLocal.size() - 1) {
                        action.append("get_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ".id,");
                        action.append("set_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ".id");
                    } else {
                        action.append("get_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ".id,");
                        action.append("set_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ".id,");
                    }
                }

                action.append("|}\n");

            } else {
                action.append(nameAction + " /\\ " + endDiagram + "\n");
            }

            alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName())+ ".id");
            Pair<IActivity,String> pair = new Pair<IActivity, String>(ad,adUtils.nameDiagramResolver(activityNode.getName()));
            alphabetNode.put(pair, alphabet);

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
