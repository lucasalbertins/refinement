package com.ref.parser.activityDiagram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ref.exceptions.ParsingException;
import com.ref.interfaces.activityDiagram.IAction;
import com.ref.interfaces.activityDiagram.IActivity;
import com.ref.interfaces.activityDiagram.IActivityNode;
import com.ref.interfaces.activityDiagram.IFlow;
import com.ref.interfaces.activityDiagram.IInputPin;
import com.ref.interfaces.activityDiagram.IOutputPin;

public class ADDefineAccept {

    private IActivity ad;

    private HashMap<Pair<IActivity, String>, ArrayList<String>> alphabetNode;
    private HashMap<Pair<IActivity, String>, String> syncChannelsEdge;
	private HashMap<Pair<IActivity, String>, String> syncObjectsEdge;
    private List<IActivityNode> queueNode;
	private List<Pair<String, Integer>> countAccept;
    private List<String> createdAccept;
    private ADUtils adUtils;
    private HashMap<String, String> objectEdges;

    public ADDefineAccept(IActivity ad, HashMap<Pair<IActivity, String>, ArrayList<String>> alphabetNode2, HashMap<Pair<IActivity, String>, String> syncChannelsEdge2,
                          List<IActivityNode> queueNode, List<Pair<String, Integer>> countAccept, List<String> createdAccept, ADUtils adUtils,HashMap<Pair<IActivity, String>,
                          String> syncObjectsEdge, HashMap<String, String> objectEdges) {
        this.ad = ad;
        this.alphabetNode = alphabetNode2;
        this.syncChannelsEdge = syncChannelsEdge2;
        this.queueNode = queueNode;
        this.countAccept = countAccept;
        this.createdAccept = createdAccept;
        this.adUtils = adUtils;
        this.syncObjectsEdge = syncObjectsEdge;
        this.objectEdges = objectEdges;
    }

    public IActivityNode defineAccept(IActivityNode activityNode, StringBuilder nodes, int code) throws ParsingException {
        StringBuilder accept = new StringBuilder();
        ArrayList<String> alphabet = new ArrayList<>();
        String endDiagram = "END_DIAGRAM_" + adUtils.nameDiagramResolver(ad.getName());
        IFlow[] outFlows = activityNode.getOutgoings();
        IFlow[] inFlows = activityNode.getIncomings();
        IOutputPin[] outPins = ((IAction) activityNode).getOutputs();
        IInputPin[] inPins = ((IAction) activityNode).getInputs();
        List<String> namesMemoryLocal = new ArrayList<>();
        HashMap<String, String> typeMemoryLocal = new HashMap<>();
        int countInFlowPin = 0;
        int countOutFlowPin = 0;
        
        int idAccept = 1;
        for (int i = 0; i < countAccept.size(); i++) {
            if (countAccept.get(i).getKey().equals(adUtils.nameDiagramResolver(activityNode.getName()))) {
                idAccept = countAccept.get(i).getValue();
                break;
            }
        }

        String nameAccept = adUtils.nameDiagramResolver("accept_" + activityNode.getName()) + "_" + idAccept + "_" + adUtils.nameDiagramResolver(ad.getName());
        String nameAcceptTermination = adUtils.nameDiagramResolver("accept_" + activityNode.getName()) + "_" + idAccept + "_" + adUtils.nameDiagramResolver(ad.getName()) + "_t";

        if (code == 0) {
        	String definition = activityNode.getDefinition();
            String[] definitionFinal = new String[0];

            if (definition != null && !(definition.equals(""))) {
                definitionFinal = definition.replace(" ", "").split(";");
            }
            
            accept.append(nameAccept + "(id) = ");

            if (inFlows.length > 0) {
                accept.append("(");
                for (int i = 0; i < inFlows.length; i++) {
                	Pair<IActivity,String> key = new Pair<IActivity, String>(ad,inFlows[i].getId());
                    if (syncChannelsEdge.containsKey(key)) {
                        String ceIn = syncChannelsEdge.get(key);

                        accept.append("(");
                        if (i >= 0 && (i < inFlows.length - 1)) {
                            adUtils.ce(alphabet, accept, ceIn, " -> SKIP) ||| ");
                        } else {
                            adUtils.ce(alphabet, accept, ceIn, " -> SKIP)");
                        }
                    }
                }
            }
            
            for (int i = 0; i < inPins.length; i++) {
                IFlow[] inFlowPin = inPins[i].getIncomings();
                for (int x = 0; x < inFlowPin.length; x++) {
                	Pair<IActivity,String> key = new Pair<IActivity, String>(ad, inFlowPin[x].getId());
                    if (syncObjectsEdge.containsKey(key)) {
                        String oeIn = syncObjectsEdge.get(key);
                        String nameObject = inPins[i].getName();

                        accept.append("(");
                        if (i >= 0 && (i < inPins.length - 1 || x < inFlowPin.length - 1)) {
                            adUtils.oe(alphabet, accept, oeIn, "?" + nameObject, " -> ");
                            try {
								adUtils.setLocalInput(alphabet, accept, inPins[i].getName(), adUtils.nameDiagramResolver(activityNode.getName()), nameObject, oeIn,inPins[i].getBase().getName());
							} catch (Exception e) {
								throw new ParsingException("Pin node "+inPins[i].getName()+" without base type\n");//TODO fix the type of exception
							}
                            accept.append("SKIP) ||| ");
                        } else {
                            adUtils.oe(alphabet, accept, oeIn, "?" + nameObject, " -> ");
                            try {
								adUtils.setLocalInput(alphabet, accept, inPins[i].getName(), adUtils.nameDiagramResolver(activityNode.getName()), nameObject, oeIn,inPins[i].getBase().getName());
							} catch (Exception e) {
								throw new ParsingException("Pin node "+inPins[i].getName()+" without base type\n");//TODO fix the type of exception
							}
                            accept.append("SKIP)");
                        }

                        if (!namesMemoryLocal.contains(nameObject)) {
                            namesMemoryLocal.add(nameObject);
                            typeMemoryLocal.put(nameObject, inPins[i].getBase().getName());
                        }
                    }
                }
            }

                accept.append("); ");
                      

            adUtils.accept(alphabet ,adUtils.nameDiagramResolver(activityNode.getName()), accept,activityNode);

            for (int i = 0; i < namesMemoryLocal.size(); i++) {
                for (int j = 0; j < definitionFinal.length; j++) {
                    String[] expression = definitionFinal[j].split("=");
                    if (expression[0].equals(namesMemoryLocal.get(i))) {
                        List<String> expReplaced = adUtils.replaceExpression(expression[1]);    //get expression replace '+','-','*','/'
                        for (String value : expReplaced) {                //get all parts
                            for (int x = 0; x < namesMemoryLocal.size(); x++) {
                                if (value.equals(namesMemoryLocal.get(x))) {
                                    adUtils.getLocal(alphabet, accept, namesMemoryLocal.get(x), adUtils.nameDiagramResolver(activityNode.getName()), namesMemoryLocal.get(x),typeMemoryLocal.get(namesMemoryLocal.get(x)));
                                }
                            }
                        }

                        adUtils.setLocal(alphabet, accept, expression[0], adUtils.nameDiagramResolver(activityNode.getName()), "(" + expression[1] + ")",expression[0]);

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
            
            if (inFlows.length == 0) {
                adUtils.update(alphabet, accept, 1, outFlows.length, false); // outFlows - 1
            } else {
                adUtils.update(alphabet, accept, inFlows.length, outFlows.length, false);
            }
            
            for (String nameObj : namesMemoryLocal) {
                adUtils.getLocal(alphabet, accept, nameObj, adUtils.nameDiagramResolver(activityNode.getName()), nameObj,typeMemoryLocal.get(nameObj));
            }

            if (outFlows.length > 0 || outPins.length > 0) {	
                accept.append("(");
            }
            
            for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                String ce = adUtils.createCE();
                Pair<IActivity,String> key = new Pair<IActivity, String>(ad,outFlows[i].getId());
                syncChannelsEdge.put(key, ce);

                accept.append("(");

                if (i >= 0 && (i < outFlows.length - 1)) {
                    adUtils.ce(alphabet, accept, ce, " -> SKIP) ||| ");
                } else {
                    adUtils.ce(alphabet, accept, ce, " -> SKIP)");
                }
            }

            //accept.append("); ");
            
            
            String nameObject = "";

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

                    String typeObj = nameObject;


                    Pair<String, String> initialAndFinalParameterValue = adUtils.getInitialAndFinalParameterValue(typeObj);

                    if ((value != null && !value.equals("")) && ADUtils.isInteger(initialAndFinalParameterValue.getKey())) {
                        accept.append("((");
                        accept.append("(" + value + ") >= " + initialAndFinalParameterValue.getKey() + " and (" + value + ") <= "  + initialAndFinalParameterValue.getValue() + ") & ");
                    } else {
                        accept.append("(");
                    }
                    if(value !=null && !value.equals("")) {
	                    if (i >= 0 && (i < outPins.length - 1 || x < outFlowPin.length - 1)) {
	                        adUtils.oe(alphabet, accept, oe, "!(" + value + ")", " -> SKIP) ||| ");
	                    } else {
	                        adUtils.oe(alphabet, accept, oe, "!(" + value + ")", " -> SKIP)");
	                    }
                    }
                    else {
                    	 if (i >= 0 && (i < outPins.length - 1 || x < outFlowPin.length - 1)) {
 	                        adUtils.oe(alphabet, accept, oe, "?"+nameAccept+i, " -> SKIP) ||| ");
 	                    } else {
 	                        adUtils.oe(alphabet, accept, oe, "?"+nameAccept+i, " -> SKIP)");
 	                    }
                    }

                }
            }

            if (outFlows.length > 0 || outPins.length > 0) {
                accept.append("); ");
            }
            
            accept.append(nameAccept + "(id)\n");

            accept.append(nameAcceptTermination + "(id) = ");
            
            if (namesMemoryLocal.size() > 0) {
                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                	accept.append("(");
                }
                accept.append("(" + nameAccept + "(id) /\\ " + endDiagram + "(id)) ");

                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                	accept.append("[|{|");
                    accept.append("get_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ".id,");
                    accept.append("set_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ".id,");
                    accept.append("endDiagram_" + adUtils.nameDiagramResolver(ad.getName())+".id");
                    accept.append("|}|] ");

                    String typeObj = typeMemoryLocal.get(namesMemoryLocal.get(i));                   

                    accept.append("Mem_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + "_" + namesMemoryLocal.get(i) + "_t(id," + adUtils.getDefaultValue(typeObj) + ")) ");
                }

                accept.append("\\{|");

                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                    if (i == namesMemoryLocal.size() - 1) {
                    	accept.append("get_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ".id,");
                        accept.append("set_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) +".id");
                    } else {
                    	accept.append("get_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ".id,");
                        accept.append("set_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ".id,");
                    }
                }

                accept.append("|}\n");

            } else {
            	accept.append(nameAccept + "(id) /\\ " + endDiagram + "(id)\n");
            }
            

            alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName())+".id");
            Pair<IActivity,String> key = new Pair<IActivity, String>(ad,adUtils.nameDiagramResolver("accept_" + activityNode.getName() + "_" + idAccept));
            alphabetNode.put(key, alphabet);
            createdAccept.add(activityNode.getId());

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

            nodes.append(accept.toString());

        } else if (code == 1) {
            String[] definitionFinal = new String[0];

        	if (outFlows.length > 0 || outPins.length > 0) {
                accept.append("(");
            }
        	
            for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                String ce = adUtils.createCE();
                Pair<IActivity,String> key = new Pair<IActivity, String>(ad,outFlows[i].getId());
                syncChannelsEdge.put(key, ce);

                accept.append("(");

                if (i >= 0 && (i < outFlows.length - 1)) {
                    adUtils.ce(alphabet, accept, ce, " -> SKIP) ||| ");
                } else {
                    adUtils.ce(alphabet, accept, ce, " -> SKIP)");
                }
            }

            String nameObject = "";
            
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
                    accept.append("(");
                    if(value !=null && !value.equals("")) {
	                    if (i >= 0 && (i < outPins.length - 1 || x < outFlowPin.length - 1)) {
	                        adUtils.oe(alphabet, accept, oe, "!(" + value + ")", " -> SKIP) ||| ");
	                    } else {
	                        adUtils.oe(alphabet, accept, oe, "!(" + value + ")", " -> SKIP)");
	                    }
                    }
                    else {
                    	 if (i >= 0 && (i < outPins.length - 1 || x < outFlowPin.length - 1)) {
 	                        adUtils.oe(alphabet, accept, oe, "?"+nameAccept+i, " -> SKIP) ||| ");
 	                    } else {
 	                        adUtils.oe(alphabet, accept, oe, "?"+nameAccept+i, " -> SKIP)");
 	                    }
                    }

                }
            }
            
            if (outFlows.length > 0 || outPins.length > 0) {
                accept.append("); ");
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

            accept.append(nameAccept + "(id) = ");

            if (inFlows.length > 0) {
                accept.append("(");

                for (int i = 0; i < inFlows.length; i++) {
                	Pair<IActivity,String> key = new Pair<IActivity, String>(ad,inFlows[i].getId());
                    if (syncChannelsEdge.containsKey(key)) {
                        String ceIn = syncChannelsEdge.get(key);

                        accept.append("(");
                        if (i >= 0 && (i < inFlows.length - 1)) {
                            adUtils.ce(alphabet, accept, ceIn, " -> SKIP) ||| ");
                        } else {
                            adUtils.ce(alphabet, accept, ceIn, " -> SKIP)");
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

                            accept.append("(");
                            if (i >= 0 && (i < inPins.length - 1 || x < inFlowPin.length - 1)) {
                                adUtils.oe(alphabet, accept, oeIn, "?" + nameObject, " -> ");
                                adUtils.setLocalInput(alphabet, accept, inPins[i].getName(), adUtils.nameDiagramResolver(activityNode.getName()), nameObject, oeIn,inPins[i].getBase().getName());
                                accept.append("SKIP) ||| ");
                            } else {
                                adUtils.oe(alphabet, accept, oeIn, "?" + nameObject, " -> ");
                                adUtils.setLocalInput(alphabet, accept, inPins[i].getName(), adUtils.nameDiagramResolver(activityNode.getName()), nameObject, oeIn,inPins[i].getBase().getName());
                                accept.append("SKIP)");
                            }

                            if (!namesMemoryLocal.contains(nameObject)) {
                                namesMemoryLocal.add(nameObject);
                                typeMemoryLocal.put(nameObject, inPins[i].getBase().getName());
                            }
                        }
                    }
                }
                
                accept.append("); ");
            }

            adUtils.accept(alphabet, adUtils.nameDiagramResolver(activityNode.getName()), accept,activityNode);
            
            for (int i = 0; i < namesMemoryLocal.size(); i++) {
                for (int j = 0; j < definitionFinal.length; j++) {
                    String[] expression = definitionFinal[j].split("=");
                    if (expression[0].equals(namesMemoryLocal.get(i))) {
                        List<String> expReplaced = adUtils.replaceExpression(expression[1]);    //get expression replace '+','-','*','/'
                        for (String value : expReplaced) {                //get all parts
                            for (int x = 0; x < namesMemoryLocal.size(); x++) {
                                if (value.equals(namesMemoryLocal.get(x))) {
                                    adUtils.getLocal(alphabet, accept, namesMemoryLocal.get(x), adUtils.nameDiagramResolver(activityNode.getName()), namesMemoryLocal.get(x),typeMemoryLocal.get(namesMemoryLocal.get(x)));
                                }
                            }
                        }

                        adUtils.setLocal(alphabet, accept, expression[0], adUtils.nameDiagramResolver(activityNode.getName()), "(" + expression[1] + ")",expression[0]);

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
            
            if (inFlows.length == 0) {
                adUtils.update(alphabet, accept, 1, outFlows.length, false); // outFlows - 1
            } else {
                adUtils.update(alphabet, accept, inFlows.length, outFlows.length, false);
            }
            
            for (String nameObj : namesMemoryLocal) {
                adUtils.getLocal(alphabet, accept, nameObj, adUtils.nameDiagramResolver(activityNode.getName()), nameObj,typeMemoryLocal.get(nameObj));
            }

            if (outFlows.length > 0 || outPins.length > 0) {
                accept.append("(");
            }

            if (outFlows.length > 0) {
                accept.append("(");

                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                	Pair<IActivity,String> key = new Pair<IActivity, String>(ad,outFlows[i].getId());
                    String ce = syncChannelsEdge.get(key);

                    accept.append("(");

                    if (i >= 0 && (i < outFlows.length - 1)) {
                        adUtils.ce(alphabet, accept, ce, " -> SKIP) ||| ");
                    } else {
                        adUtils.ce(alphabet, accept, ce, " -> SKIP)");
                    }
                }
                
                String nameObject = "";

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
                    
                        String typeObj = nameObject;


                        Pair<String, String> initialAndFinalParameterValue = adUtils.getInitialAndFinalParameterValue(typeObj);

                        if ((value != null && !value.equals("")) && ADUtils.isInteger(initialAndFinalParameterValue.getKey())) {
                            accept.append("((");
                            accept.append("(" + value + ") >= " + initialAndFinalParameterValue.getKey() + " and (" + value + ") <= "  + initialAndFinalParameterValue.getValue() + ") & ");
                        } else {
                        	accept.append("(");
                        }
                        if(value !=null && !value.equals("")) {
    	                    if (i >= 0 && (i < outPins.length - 1 || x < outFlowPin.length - 1)) {
    	                        adUtils.oe(alphabet, accept, oe, "!(" + value + ")", " -> SKIP) ||| ");
    	                    } else {
    	                        adUtils.oe(alphabet, accept, oe, "!(" + value + ")", " -> SKIP)");
    	                    }
                        }
                        else {
                        	 if (i >= 0 && (i < outPins.length - 1 || x < outFlowPin.length - 1)) {
     	                        adUtils.oe(alphabet, accept, oe, "?"+nameAccept+i, " -> SKIP) ||| ");
     	                    } else {
     	                        adUtils.oe(alphabet, accept, oe, "?"+nameAccept+i, " -> SKIP)");
     	                    }
                        }
                    }
                }
                
                accept.append("); ");
            }
            
            if (outFlows.length > 0 || outPins.length > 0) {
                accept.append("); ");
            }

            accept.append(nameAccept + "(id)\n");

            accept.append(nameAcceptTermination + "(id) = ");
            
            if (namesMemoryLocal.size() > 0) {
                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                	accept.append("(");
                }
                accept.append("(" + nameAccept + "(id) /\\ " + endDiagram + "(id)) ");

                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                	accept.append("[|{|");
                    accept.append("get_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ".id,");
                    accept.append("set_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ".id,");
                    accept.append("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()) + ".id");
                    accept.append("|}|] ");

                    String typeObj = typeMemoryLocal.get(namesMemoryLocal.get(i));
                    
                    accept.append("Mem_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + "_" + namesMemoryLocal.get(i) + "_t(id," + adUtils.getDefaultValue(typeObj) + ")) ");
                }

                accept.append("\\{|");

                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                    if (i == namesMemoryLocal.size() - 1) {
                    	accept.append("get_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ".id,");
                        accept.append("set_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ".id");
                    } else {
                    	accept.append("get_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ".id,");
                        accept.append("set_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ".id,");
                    }
                }

                accept.append("|}\n");

            } else {
            	accept.append(nameAccept + "(id) /\\ " + endDiagram + "(id)\n");
            }
            

            alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName())+".id");
            Pair<IActivity,String> key = new Pair<IActivity, String>(ad,adUtils.nameDiagramResolver("accept_" + activityNode.getName() + "_" + idAccept));
            alphabetNode.put(key, alphabet);
            createdAccept.add(activityNode.getId());

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

            nodes.append(accept.toString());
        }

        return activityNode;
    }
}
