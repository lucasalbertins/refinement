package com.ref.parser.activityDiagram;

import com.ref.exceptions.ParsingException;
import com.ref.interfaces.activityDiagram.IAction;
import com.ref.interfaces.activityDiagram.IActivity;
import com.ref.interfaces.activityDiagram.IActivityNode;
import com.ref.interfaces.activityDiagram.IFlow;
import com.ref.interfaces.activityDiagram.IInputPin;
import com.ref.interfaces.activityDiagram.IOutputPin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ADDefineAction {

    private IActivity ad;

    private HashMap<Pair<IActivity,String>, ArrayList<String>> alphabetNode;
    private ADUtils adUtils;

    public ADDefineAction(IActivity ad, HashMap<Pair<IActivity, String>, ArrayList<String>> alphabetNode2, 
    		ADUtils adUtils) {
        this.ad = ad;
        this.alphabetNode = alphabetNode2;
        this.adUtils = adUtils;
    }
    
    
    public String defineAction(IActivityNode activityNode) throws ParsingException {
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
        if(Character.isDigit(nameAction.charAt(0))) {
        	throw new ParsingException("The node name "+adUtils.nameDiagramResolver(activityNode.getName())+" cannot start with a number\n");
        }
        
        
        //if node has opaque expressions
        String definition = activityNode.getDefinition();
        String[] definitionFinal = new String[0];

        if (definition != null && !(definition.equals(""))) {
            definitionFinal = definition.replace(" ", "").split(";");
        }

        //name of the csp process
        action.append(nameAction + "(id) = ");

        //inputs of the action
        adUtils.incomingEdges(activityNode, action, alphabet, inFlows, inPins, namesMemoryLocal, typeMemoryLocal);
        
        
        //defining the event of the action 
        adUtils.event(alphabet, nameAction, action);//TODO

        //treating expressions inside opaque actions
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
        
        //count input flows and output flows to calculate the update channel
        for (int i = 0; i < inPins.length; i++) {
            countInFlowPin += inPins[i].getIncomings().length;
        }
        for (int i = 0; i < outPins.length; i++) {
            countOutFlowPin += outPins[i].getOutgoings().length;
        }
        adUtils.update(alphabet, action, inFlows.length + countInFlowPin, outFlows.length + countOutFlowPin, false);

        // get any local data calculated in the actions to be sent in outgoing edges
        for (String nameObj : namesMemoryLocal) {
            adUtils.getLocal(alphabet, action, nameObj, adUtils.nameDiagramResolver(activityNode.getName()), nameObj,typeMemoryLocal.get(nameObj));
        }

        
        adUtils.outgoingEdges(action, alphabet, outFlows, outPins, definitionFinal);

        // defining the recursion
        action.append(nameAction + "(id)\n");

        // defining the terminating process for this action
        action.append(nameActionTermination + "(id) = ");

        if (namesMemoryLocal.size() > 0) {
            for (int i = 0; i < namesMemoryLocal.size(); i++) {
                action.append("(");
            }
            action.append("(" + nameAction + "(id) /\\ " + endDiagram + "(id)) ");

            // defining the parallelism with memory process
            for (int i = 0; i < namesMemoryLocal.size(); i++) {
                action.append("[|{|");
                action.append("get_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ".id,");
                action.append("set_" + namesMemoryLocal.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ".id,");
                action.append("endDiagram_" + adUtils.nameDiagramResolver(ad.getName())+".id");
                action.append("|}|] ");

                String typeObj = typeMemoryLocal.get(namesMemoryLocal.get(i));

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

        return action.toString();
    }
    /*
    public IActivityNode defineAction(IActivityNode activityNode, StringBuilder nodes, int code) throws ParsingException {
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
        if(Character.isDigit(nameAction.charAt(0))) {
        	throw new ParsingException("The node name "+adUtils.nameDiagramResolver(activityNode.getName())+" cannot start with a number\n");
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

            adUtils.event(alphabet, nameAction, action);//TODO

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

            for (int i = 0; i < outPins.length; i++) {    //creates the parallel output channels
                IFlow[] outFlowPin = outPins[i].getOutgoings();

                for (int x = 0; x < outFlowPin.length; x++) {
                    
                	try {
						nameObject = outPins[i].getBase().getName();
					} catch (Exception e) {
						throw new ParsingException("Pin "+outPins[i].getName()+" without base class\n");
					}

                    String oe = adUtils.createOE();
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

                    String typeObj = typeMemoryLocal.get(namesMemoryLocal.get(i));

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
            
            for (int i = 0; i < outPins.length; i++) {    //creates the parallel output channels
                IFlow[] outFlowPin = outPins[i].getOutgoings();

                for (int x = 0; x < outFlowPin.length; x++) {
                    nameObject = outPins[i].getBase().getName();

                    String oe = adUtils.createOE();
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

            adUtils.event(alphabet, nameAction, action);

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

                    String typeObj = typeMemoryLocal.get(namesMemoryLocal.get(i));
                    
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
                action.append(nameAction + "(id) /\\ " + endDiagram + "(id)\n");
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
    */


	


	
}
