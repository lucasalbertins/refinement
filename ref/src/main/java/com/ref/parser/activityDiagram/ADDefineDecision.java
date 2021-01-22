package com.ref.parser.activityDiagram;

import com.change_vision.jude.api.inf.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ADDefineDecision {

    private IActivity ad;

    private HashMap<Pair<IActivity, String>, ArrayList<String>> alphabetNode;
    private HashMap<Pair<IActivity, String>, String> syncChannelsEdge;
    private HashMap<Pair<IActivity, String>, String> syncObjectsEdge;
    private HashMap<String, String> objectEdges;
    private List<IActivityNode> queueNode;
    private HashMap<String, String> parameterNodesInput;
    private ADUtils adUtils;

    public ADDefineDecision(IActivity ad, HashMap<Pair<IActivity, String>, ArrayList<String>> alphabetNode2, HashMap<Pair<IActivity, String>, String> syncChannelsEdge2,
                            HashMap<Pair<IActivity, String>, String> syncObjectsEdge2, HashMap<String, String> objectEdges, List<IActivityNode> queueNode,
                            HashMap<String, String> parameterNodesInput, ADUtils adUtils) {
        this.ad = ad;
        this.alphabetNode = alphabetNode2;
        this.syncChannelsEdge = syncChannelsEdge2;
        this.syncObjectsEdge = syncObjectsEdge2;
        this.objectEdges = objectEdges;
        this.queueNode = queueNode;
        this.parameterNodesInput = parameterNodesInput;
        this.adUtils = adUtils;
    }

    public IActivityNode defineDecision(IActivityNode activityNode, StringBuilder nodes, int code) {
        StringBuilder decision = new StringBuilder();
        ArrayList<String> alphabet = new ArrayList<>();
        String nameDecision = adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName());
        String nameDecisionTermination = adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + "_t";
        String endDiagram = "END_DIAGRAM_" + adUtils.nameDiagramResolver(ad.getName());
        IFlow[] outFlows = activityNode.getOutgoings();
        IFlow[] inFlows = activityNode.getIncomings();
        String decisionInputType = null;
        String decisionInputFlow = null;

        if (code == 0) {
            for (int i = 0; i < inFlows.length; i++) {

                String[] stereotype = inFlows[i].getStereotypes();

                for (int j = 0; j < stereotype.length; j++) {
                	Pair<IActivity,String> key = new Pair<IActivity, String>(ad,inFlows[i].getId());
                    if (stereotype[j].equals("decisionInputFlow")) {
                        decisionInputType = objectEdges.get(syncObjectsEdge.get(key));
                        decisionInputFlow = inFlows[i].getSource().getName();
                    }
                }
            }

            if (decisionInputType != null && inFlows.length == 1) {    //just object
                decision.append(nameDecision + "(id) = ");

                for (int i = 0; i < inFlows.length; i++) {
                	Pair<IActivity,String> key = new Pair<IActivity, String>(ad,inFlows[i].getId());
                    if (syncObjectsEdge.containsKey(key)) {
                        String ceIn = syncObjectsEdge.get(key);
                        adUtils.oe(alphabet, decision, ceIn, "?" + decisionInputFlow, " -> ");
                    }
                }

                adUtils.update(alphabet, decision, 1, 1, false);

                decision.append("(");
                
                List<String> prevGuard = new ArrayList<>();
                
                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String oe = "";
                    String ce = "";
                    
                    Pair<IActivity,String> key = new Pair<IActivity, String>(ad,outFlows[i].getId());
                    if (outFlows[i].getTarget() instanceof IPin) {
                        oe = adUtils.createOE(decisionInputType);
                        syncObjectsEdge.put(key, oe);

                        objectEdges.put(oe, decisionInputType);
                    } else {
                        ce = adUtils.createCE();
                        syncChannelsEdge.put(key, ce);
                    }
                    if(!adUtils.nameDiagramResolver(outFlows[i].getGuard()).equalsIgnoreCase("else")) {// se a guarda não for else
                    	decision.append(outFlows[i].getGuard() == "" ? "true & (dc -> ": (outFlows[i].getGuard() + " & (dc -> "));//se a guarda for vazia então assume-se true
                    	prevGuard.add(outFlows[i].getGuard()); //salva a guarda para o proximo else
                    }else {
                    	decision.append("not "+prevGuard.get(prevGuard.size()-1) + " & (dc -> ");
                    	prevGuard.remove(prevGuard.size()-1);
                    }
                    
                    if (!alphabet.contains("dc")) {
                        alphabet.add("dc");
                    }             

                    if (outFlows[i].getTarget() instanceof IPin) {
                        if (i >= 0 && i < outFlows.length - 1) {
                            adUtils.oe(alphabet, decision, oe, "!" + decisionInputFlow, " -> SKIP) [] ");
                        } else {
                            adUtils.oe(alphabet, decision, oe, "!" + decisionInputFlow, " -> SKIP)");
                        }
                    } else {
                        if (i >= 0 && i < outFlows.length - 1) {
                            adUtils.ce(alphabet, decision, ce, " -> SKIP) [] ");
                        } else {
                            adUtils.ce(alphabet, decision, ce, " -> SKIP)");
                        }
                    }
                }

                decision.append("); ");

                decision.append(nameDecision + "(id)\n");

                decision.append(nameDecisionTermination + "(id) = ");
                decision.append("(" + nameDecision + "(id) /\\ " + endDiagram + "(id)) \\{|dc|}\n");

                alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName())+".id");
                Pair<IActivity,String> key = new Pair<IActivity, String>(ad,adUtils.nameDiagramResolver(activityNode.getName()));
                alphabetNode.put(key, alphabet);

                if (outFlows[0].getTarget() instanceof IInputPin) {
                    for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                        if (activityNodeSearch instanceof IAction) {
                            IInputPin[] inFlowPin = ((IAction) activityNodeSearch).getInputs();
                            for (int y = 0; y < inFlowPin.length; y++) {
                                if (inFlowPin[y].getId().equals(outFlows[0].getTarget().getId())) {
                                    activityNode = activityNodeSearch;
                                }
                            }
                        }
                    }
                } else {
                    activityNode = outFlows[0].getTarget();
                }

                for (int x = 1; x < outFlows.length; x++) {
                    if (outFlows[x].getTarget() instanceof IInputPin) {
                        for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                            if (activityNodeSearch instanceof IAction) {
                                IInputPin[] inFlowPin = ((IAction) activityNodeSearch).getInputs();
                                for (int y = 0; y < inFlowPin.length; y++) {
                                    if (inFlowPin[y].getId().equals(outFlows[x].getTarget().getId())) {
                                        if (!queueNode.contains(activityNodeSearch)) {
                                            queueNode.add(activityNodeSearch);
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if (!queueNode.contains(outFlows[x].getTarget())) {
                            queueNode.add(outFlows[x].getTarget());
                        }
                    }
                }

                nodes.append(decision.toString());
            } else if (decisionInputType != null && inFlows.length > 1) {                    //object and control
                decision.append(nameDecision + "(id) = ");

                Pair<IActivity,String> sync2 = new Pair<IActivity, String>(ad, "");
                Pair<IActivity,String> sync = new Pair<IActivity, String>(ad,"");

                for (int i = 0; i < inFlows.length; i++) {
                	Pair<IActivity,String> key = new Pair<IActivity, String>(ad,inFlows[i].getId());
                    if (syncChannelsEdge.containsKey(key)) {
                        sync2 = new Pair<IActivity, String>(ad, inFlows[i].getId());
                    }

                    if (syncObjectsEdge.containsKey(key)) {
                    	sync = new Pair<IActivity, String>(ad, inFlows[i].getId());
                    }
                }


                String ceIn2 = syncChannelsEdge.get(sync2);
                String ceIn = syncObjectsEdge.get(sync);

                decision.append("((");
                adUtils.ce(alphabet, decision, ceIn2, " -> SKIP");

                decision.append(") ||| (");
                adUtils.oe(alphabet, decision, ceIn, "?" + decisionInputType, " -> ");

                adUtils.setLocal(alphabet, decision, decisionInputFlow, adUtils.nameDiagramResolver(activityNode.getName()), decisionInputFlow,decisionInputType);
                decision.append("SKIP)); ");

                adUtils.update(alphabet, decision, 2, 1, false);
                adUtils.getLocal(alphabet, decision, decisionInputFlow, adUtils.nameDiagramResolver(activityNode.getName()), decisionInputFlow,decisionInputType);

                decision.append("(");
                
                List<String> prevGuard = new ArrayList<>();
                
                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String ce = adUtils.createCE();
                    Pair<IActivity,String> key = new Pair<IActivity, String>(ad,outFlows[i].getId());
                    syncChannelsEdge.put(key, ce);

                    if(!adUtils.nameDiagramResolver(outFlows[i].getGuard()).equalsIgnoreCase("else")) {// se a guarda não for else
                    	decision.append(outFlows[i].getGuard() == "" ? "true & (dc -> ": (outFlows[i].getGuard() + " & (dc -> "));//se a guarda for vazia então assume-se true
                    	prevGuard.add(outFlows[i].getGuard()); //salva a guarda para o proximo else
                    }else {
                    	decision.append("not "+prevGuard.get(prevGuard.size()-1) + " & (dc -> ");
                    	prevGuard.remove(prevGuard.size()-1);
                    }
                    
                    if (!alphabet.contains("dc")) {
                        alphabet.add("dc");
                    }

                    if (i >= 0 && i < outFlows.length - 1) {
                        adUtils.ce(alphabet, decision, ce, " -> SKIP) [] ");
                    } else {
                        adUtils.ce(alphabet, decision, ce, " -> SKIP)");
                    }
                }

                decision.append("); ");

                decision.append(nameDecision + "(id)\n");

                decision.append(nameDecisionTermination + "(id) = ");
                decision.append("((" + nameDecision + "(id) /\\ " + endDiagram + "(id)) ");

                decision.append("[|{|");
                decision.append("get_" + decisionInputFlow + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                decision.append("set_" + decisionInputFlow + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                decision.append("endDiagram_" + adUtils.nameDiagramResolver(ad.getName())+".id");
                decision.append("|}|] ");
                decision.append("Mem_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + "_t(id," + adUtils.getDefaultValue(parameterNodesInput.get(decisionInputFlow)) + ")) ");

                decision.append("\\{|");
                decision.append("get_" + decisionInputFlow + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                decision.append("set_" + decisionInputFlow + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                decision.append("dc");
                decision.append("|}\n");

                alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName())+".id");
                Pair<IActivity,String> key = new Pair<IActivity, String>(ad,adUtils.nameDiagramResolver(activityNode.getName()));
                alphabetNode.put(key, alphabet);

                activityNode = outFlows[0].getTarget();    //set next action or control node

                for (int i = 1; i < outFlows.length; i++) {    //puts the remaining nodes in the queue
                    if (!queueNode.contains(outFlows[i].getTarget())) {
                        queueNode.add(outFlows[i].getTarget());
                    }
                }

                nodes.append(decision.toString());
            } else {        //just control
                decision.append(nameDecision + "(id) = ");

                Pair<IActivity, String> sync = new Pair<IActivity, String>(ad, inFlows[0].getId());

                String ceIn = syncChannelsEdge.get(sync);

                adUtils.ce(alphabet, decision, ceIn, " -> ");
                adUtils.update(alphabet, decision, 1, 1, false);

                String allGuards = "";
                int countGuards = 0;

                for (int i = 0; i < outFlows.length; i++) {
                    if (outFlows[i].getGuard().length() > 0 &&
                            !adUtils.nameDiagramResolver(outFlows[i].getGuard()).equalsIgnoreCase("else")) {
                        countGuards = adUtils.addCountGuard(nameDecision + "_guard");
                        allGuards += "?" + adUtils.nameDiagramResolver(outFlows[i].getGuard());
                    }
                }

                if (countGuards > 0) {
                    decision.append(nameDecision + "_guard" + allGuards + " -> ");
                    alphabet.add(nameDecision + "_guard");
                }

                decision.append("(");

                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String ce = adUtils.createCE();
                    Pair<IActivity,String> key = new Pair<IActivity, String>(ad,outFlows[i].getId());
                    syncChannelsEdge.put(key, ce);

                    // tratamento de guarda
                    if (outFlows[i].getGuard().length() == 0) {
                        decision.append("(dc -> ");
                        if (!alphabet.contains("dc")) {
                            alphabet.add("dc");
                        }
                    } else if (adUtils.nameDiagramResolver(outFlows[i].getGuard()).equalsIgnoreCase("else")) {
                        boolean first = true;
                        for (int x = 0; x < outFlows.length; x++) {
                            if (!adUtils.nameDiagramResolver(outFlows[x].getGuard()).equalsIgnoreCase("else")) {
                                if (first) {
                                    decision.append("not(" + adUtils.nameDiagramResolver(outFlows[x].getGuard()) + ") ");
                                    first = false;
                                } else {
                                    decision.append("and not(" + adUtils.nameDiagramResolver(outFlows[x].getGuard()) + ") ");
                                }
                            }
                        }

                        decision.append("& (");
                    } else {
                        decision.append(adUtils.nameDiagramResolver(outFlows[i].getGuard()) + " & (");
                    }

                    if (i >= 0 && i < outFlows.length - 1) {
                        adUtils.ce(alphabet, decision, ce, " -> SKIP) [] ");
                    } else {
                        adUtils.ce(alphabet, decision, ce, " -> SKIP)");
                    }
                }

                decision.append("); ");

                decision.append(nameDecision + "(id)\n");

                decision.append(nameDecisionTermination + "(id) = ");
                decision.append(nameDecision + "(id) /\\ " + endDiagram + "(id) \\{|dc|}\n");

                alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName())+".id");
                Pair<IActivity,String> key = new Pair<IActivity, String>(ad,adUtils.nameDiagramResolver(activityNode.getName()));
                alphabetNode.put(key, alphabet);

                activityNode = outFlows[0].getTarget();    //set next action or control node

                for (int i = 1; i < outFlows.length; i++) {    //puts the remaining nodes in the queue
                    if (!queueNode.contains(outFlows[i].getTarget())) {
                        queueNode.add(outFlows[i].getTarget());
                    }
                }

                nodes.append(decision.toString());
            }
        } else if (code == 1) {
            for (int i = 0; i < inFlows.length; i++) {

                String[] stereotype = inFlows[i].getStereotypes();

                for (int j = 0; j < stereotype.length; j++) {
                    if (stereotype[j].equals("decisionInputFlow")) {
                    	Pair<IActivity, String> key = new Pair<IActivity, String>(ad, inFlows[i].getId());
                        decisionInputType = objectEdges.get(syncObjectsEdge.get(key));
                        decisionInputFlow = inFlows[i].getSource().getName();
                    }
                }
            }

            if (decisionInputType != null && inFlows.length == 1) {    //just object
                decision.append(nameDecision + "(id) = ");

                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String oe = "";
                    String ce = "";
                    Pair<IActivity,String> key = new Pair<IActivity, String>(ad,outFlows[i].getId());
                    if (outFlows[i].getTarget() instanceof IPin) {
                        oe = adUtils.createOE(decisionInputType);
                        syncObjectsEdge.put(key, oe);

                        objectEdges.put(oe, decisionInputType);
                    } else {
                        ce = adUtils.createCE();
                        syncChannelsEdge.put(key, ce);
                    }

                    decision.append(outFlows[i].getGuard() + " & (dc -> ");
                    if (!alphabet.contains("dc")) {
                        alphabet.add("dc");
                    }

                    if (outFlows[i].getTarget() instanceof IPin) {
                        if (i >= 0 && i < outFlows.length - 1) {
                            adUtils.oe(alphabet, decision, oe, "!" + decisionInputFlow, " -> SKIP) [] ");
                        } else {
                            adUtils.oe(alphabet, decision, oe, "!" + decisionInputFlow, " -> SKIP)");
                        }
                    } else {
                        if (i >= 0 && i < outFlows.length - 1) {
                            adUtils.ce(alphabet, decision, ce, " -> SKIP) [] ");
                        } else {
                            adUtils.ce(alphabet, decision, ce, " -> SKIP)");
                        }
                    }
                }

                if (outFlows[0].getTarget() instanceof IInputPin) {
                    for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                        if (activityNodeSearch instanceof IAction) {
                            IInputPin[] inFlowPin = ((IAction) activityNodeSearch).getInputs();
                            for (int y = 0; y < inFlowPin.length; y++) {
                                if (inFlowPin[y].getId().equals(outFlows[0].getTarget().getId())) {
                                    activityNode = activityNodeSearch;
                                }
                            }
                        }
                    }
                } else {
                    activityNode = outFlows[0].getTarget();
                }

                for (int x = 1; x < outFlows.length; x++) {
                    if (outFlows[x].getTarget() instanceof IInputPin) {
                        for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                            if (activityNodeSearch instanceof IAction) {
                                IInputPin[] inFlowPin = ((IAction) activityNodeSearch).getInputs();
                                for (int y = 0; y < inFlowPin.length; y++) {
                                    if (inFlowPin[y].getId().equals(outFlows[x].getTarget().getId())) {
                                        if (!queueNode.contains(activityNodeSearch)) {
                                            queueNode.add(activityNodeSearch);
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if (!queueNode.contains(outFlows[x].getTarget())) {
                            queueNode.add(outFlows[x].getTarget());
                        }
                    }
                }

            } else if (decisionInputType != null && inFlows.length > 1) {                    //object and control
                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String ce = adUtils.createCE();
                    Pair<IActivity,String> key = new Pair<IActivity, String>(ad,outFlows[i].getId());
                    syncChannelsEdge.put(key, ce);

                    decision.append(outFlows[i].getGuard() + " & (dc -> ");
                    if (!alphabet.contains("dc")) {
                        alphabet.add("dc");
                    }

                    if (i >= 0 && i < outFlows.length - 1) {
                        adUtils.ce(alphabet, decision, ce, " -> SKIP) [] ");
                    } else {
                        adUtils.ce(alphabet, decision, ce, " -> SKIP)");
                    }
                }

                activityNode = outFlows[0].getTarget();    //set next action or control node

                for (int i = 1; i < outFlows.length; i++) {    //puts the remaining nodes in the queue
                    if (!queueNode.contains(outFlows[i].getTarget())) {
                        queueNode.add(outFlows[i].getTarget());
                    }
                }

            } else {        //just control
                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String ce = adUtils.createCE();
                    Pair<IActivity,String> key = new Pair<IActivity, String>(ad,outFlows[i].getId());
                    syncChannelsEdge.put(key, ce);

//                    if (!alphabet.contains("dc")) {
//                        alphabet.add("dc");
//                    }

                    if (i >= 0 && i < outFlows.length - 1) {
                        adUtils.ce(alphabet, decision, ce, " -> SKIP) [] ");
                    } else {
                        adUtils.ce(alphabet, decision, ce, " -> SKIP)");
                    }
                }

                activityNode = outFlows[0].getTarget();    //set next action or control node

                for (int i = 1; i < outFlows.length; i++) {    //puts the remaining nodes in the queue
                    if (!queueNode.contains(outFlows[i].getTarget())) {
                        queueNode.add(outFlows[i].getTarget());
                    }
                }
            }
        } else if (code == 2) {
            for (int i = 0; i < inFlows.length; i++) {

                String[] stereotype = inFlows[i].getStereotypes();

                for (int j = 0; j < stereotype.length; j++) {
                    if (stereotype[j].equals("decisionInputFlow")) {
                    	Pair<IActivity, String> key = new Pair<IActivity, String>(ad, inFlows[i].getId());
                        decisionInputType = objectEdges.get(syncObjectsEdge.get(key));
                        decisionInputFlow = inFlows[i].getSource().getName();
                    }
                }
            }

            if (decisionInputType != null && inFlows.length == 1) {    //just object
                decision.append(nameDecision + "(id) = ");

                for (int i = 0; i < inFlows.length; i++) {
                	Pair<IActivity, String> key = new Pair<IActivity, String>(ad, inFlows[i].getId());
                    if (syncObjectsEdge.containsKey(key)) {
                        String ceIn = syncObjectsEdge.get(key);
                        adUtils.oe(alphabet, decision, ceIn, "?" + decisionInputFlow, " -> ");
                    }
                }

                adUtils.update(alphabet, decision, 1, 1, false);

                decision.append("(");

                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String oe = "";
                    String ce = "";
                    Pair<IActivity,String> key = new Pair<IActivity, String>(ad,outFlows[i].getId());
                    if (outFlows[i].getTarget() instanceof IPin) {
                        oe = syncObjectsEdge.get(key);
                        syncObjectsEdge.put(key, oe);

                        objectEdges.put(oe, decisionInputType);
                    } else {
                        ce = syncChannelsEdge.get(key);
                        syncChannelsEdge.put(key, ce);
                    }

                    decision.append(outFlows[i].getGuard() + " & (dc -> ");
                    if (!alphabet.contains("dc")) {
                        alphabet.add("dc");
                    }

                    if (outFlows[i].getTarget() instanceof IPin) {
                        if (i >= 0 && i < outFlows.length - 1) {
                            adUtils.oe(alphabet, decision, oe, "!" + decisionInputFlow, " -> SKIP) [] ");
                        } else {
                            adUtils.oe(alphabet, decision, oe, "!" + decisionInputFlow, " -> SKIP)");
                        }
                    } else {
                        if (i >= 0 && i < outFlows.length - 1) {
                            adUtils.ce(alphabet, decision, ce, " -> SKIP) [] ");
                        } else {
                            adUtils.ce(alphabet, decision, ce, " -> SKIP)");
                        }
                    }
                }

                decision.append("); ");

                decision.append(nameDecision + "(id)\n");

                decision.append(nameDecisionTermination + "(id) = ");
                decision.append("(" + nameDecision + "(id) /\\ " + endDiagram + "(id)) \\{|dc|}\n");

                alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName())+".id");
                Pair<IActivity,String> key = new Pair<IActivity, String>(ad,adUtils.nameDiagramResolver(activityNode.getName()));
                alphabetNode.put(key, alphabet);

                if (outFlows[0].getTarget() instanceof IInputPin) {
                    for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                        if (activityNodeSearch instanceof IAction) {
                            IInputPin[] inFlowPin = ((IAction) activityNodeSearch).getInputs();
                            for (int y = 0; y < inFlowPin.length; y++) {
                                if (inFlowPin[y].getId().equals(outFlows[0].getTarget().getId())) {
                                    activityNode = activityNodeSearch;
                                }
                            }
                        }
                    }
                } else {
                    activityNode = outFlows[0].getTarget();
                }

                for (int x = 1; x < outFlows.length; x++) {
                    if (outFlows[x].getTarget() instanceof IInputPin) {
                        for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                            if (activityNodeSearch instanceof IAction) {
                                IInputPin[] inFlowPin = ((IAction) activityNodeSearch).getInputs();
                                for (int y = 0; y < inFlowPin.length; y++) {
                                    if (inFlowPin[y].getId().equals(outFlows[x].getTarget().getId())) {
                                        if (!queueNode.contains(activityNodeSearch)) {
                                            queueNode.add(activityNodeSearch);
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if (!queueNode.contains(outFlows[x].getTarget())) {
                            queueNode.add(outFlows[x].getTarget());
                        }
                    }
                }

                nodes.append(decision.toString());
            } else if (decisionInputType != null && inFlows.length > 1) {                    //object and control
                decision.append(nameDecision + "(id) = ");

                Pair<IActivity, String> sync2 = new Pair<IActivity, String>(ad,"");
                Pair<IActivity, String> sync = new Pair<IActivity, String>(ad,"");

                for (int i = 0; i < inFlows.length; i++) {
                	Pair<IActivity, String> key = new Pair<IActivity, String>(ad, inFlows[i].getId());
                    if (syncChannelsEdge.containsKey(key)) {
                        sync2 = key;
                    }

                    if (syncObjectsEdge.containsKey(key)) {
                        sync = key;
                    }
                }


                String ceIn2 = syncChannelsEdge.get(sync2);
                String ceIn = syncObjectsEdge.get(sync);

                decision.append("((");
                adUtils.ce(alphabet, decision, ceIn2, " -> SKIP");

                decision.append(") ||| (");
                adUtils.oe(alphabet, decision, ceIn, "?" + decisionInputFlow, " -> ");

                adUtils.setLocal(alphabet, decision, decisionInputFlow, adUtils.nameDiagramResolver(activityNode.getName()), decisionInputFlow,decisionInputType);
                decision.append("SKIP)); ");

                adUtils.update(alphabet, decision, 2, 1, false);
                adUtils.getLocal(alphabet, decision, decisionInputFlow, adUtils.nameDiagramResolver(activityNode.getName()), decisionInputFlow,decisionInputType);

                decision.append("(");

                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                	Pair<IActivity, String> key = new Pair<IActivity, String>(ad, outFlows[i].getId());
                	String ce = syncChannelsEdge.get(key);

                    decision.append(outFlows[i].getGuard() + " & (dc -> ");
                    if (!alphabet.contains("dc")) {
                        alphabet.add("dc");
                    }

                    if (i >= 0 && i < outFlows.length - 1) {
                        adUtils.ce(alphabet, decision, ce, " -> SKIP) [] ");
                    } else {
                        adUtils.ce(alphabet, decision, ce, " -> SKIP)");
                    }
                }

                decision.append("); ");

                decision.append(nameDecision + "(id)\n");

                decision.append(nameDecisionTermination + "(id) = ");
                decision.append("((" + nameDecision + "(id) /\\ " + endDiagram + "(id)) ");

                decision.append("[|{|");
                decision.append("get_" + decisionInputFlow + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                decision.append("set_" + decisionInputFlow + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                decision.append("endDiagram_" + adUtils.nameDiagramResolver(ad.getName())+".id");
                decision.append("|}|] ");
                decision.append("Mem_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + "_t(id," + adUtils.getDefaultValue(parameterNodesInput.get(decisionInputFlow)) + ")) ");
                decision.append("\\{|");
                decision.append("get_" + decisionInputFlow + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                decision.append("set_" + decisionInputFlow + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                decision.append("dc");
                decision.append("|}\n");

                alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName())+".id");
                Pair<IActivity,String> key = new Pair<IActivity, String>(ad,adUtils.nameDiagramResolver(activityNode.getName()));
                alphabetNode.put(key, alphabet);

                activityNode = outFlows[0].getTarget();    //set next action or control node

                for (int i = 1; i < outFlows.length; i++) {    //puts the remaining nodes in the queue
                    if (!queueNode.contains(outFlows[i].getTarget())) {
                        queueNode.add(outFlows[i].getTarget());
                    }
                }

                nodes.append(decision.toString());
            } else {        //just control
                decision.append(nameDecision + "(id) = ");

                Pair<IActivity, String> sync = new Pair<IActivity, String>(ad,inFlows[0].getId());

                String ceIn = syncChannelsEdge.get(sync);

                adUtils.ce(alphabet, decision, ceIn, " -> ");
                adUtils.update(alphabet, decision, 1, 1, false);

                String allGuards = "";
                int countGuards = 0;

                for (int i = 0; i < outFlows.length; i++) {
                    if (outFlows[i].getGuard().length() > 0 &&
                            !adUtils.nameDiagramResolver(outFlows[i].getGuard()).equalsIgnoreCase("else")) {
                        countGuards = adUtils.addCountGuard(nameDecision + "_guard");
                        allGuards += "?" + adUtils.nameDiagramResolver(outFlows[i].getGuard());
                    }
                }

                if (countGuards > 0) {
                    decision.append(nameDecision + "_guard" + allGuards + " -> ");
                    alphabet.add(nameDecision + "_guard");
                }

                decision.append("(");

                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                	Pair<IActivity, String> key = new Pair<IActivity, String>(ad, outFlows[i].getId());
                    String ce = syncChannelsEdge.get(key);

                    // tratamento de guarda
                    if (outFlows[i].getGuard().length() == 0) {
                        decision.append("(dc -> ");
                        if (!alphabet.contains("dc")) {
                            alphabet.add("dc");
                        }
                    } else if (adUtils.nameDiagramResolver(outFlows[i].getGuard()).equalsIgnoreCase("else")) {
                        boolean first = true;
                        for (int x = 0; x < outFlows.length; x++) {
                            if (!adUtils.nameDiagramResolver(outFlows[x].getGuard()).equalsIgnoreCase("else")) {
                                if (first) {
                                    decision.append("not(" + adUtils.nameDiagramResolver(outFlows[x].getGuard()) + ") ");
                                    first = false;
                                } else {
                                    decision.append("and not(" + adUtils.nameDiagramResolver(outFlows[x].getGuard()) + ") ");
                                }
                            }
                        }

                        decision.append("& (");
                    } else {
                        decision.append(adUtils.nameDiagramResolver(outFlows[i].getGuard()) + " & (");
                    }

                    if (i >= 0 && i < outFlows.length - 1) {
                        adUtils.ce(alphabet, decision, ce, " -> SKIP) [] ");
                    } else {
                        adUtils.ce(alphabet, decision, ce, " -> SKIP)");
                    }
                }

                decision.append("); ");

                decision.append(nameDecision + "(id)\n");

                decision.append(nameDecisionTermination + "(id) = ");
                decision.append(nameDecision + "(id) /\\ " + endDiagram + "(id) \\{|dc|}\n");

                alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName())+".id");
                Pair<IActivity,String> key = new Pair<IActivity, String>(ad,adUtils.nameDiagramResolver(activityNode.getName()));
                alphabetNode.put(key, alphabet);

                activityNode = outFlows[0].getTarget();    //set next action or control node

                for (int i = 1; i < outFlows.length; i++) {    //puts the remaining nodes in the queue
                    if (!queueNode.contains(outFlows[i].getTarget())) {
                        queueNode.add(outFlows[i].getTarget());
                    }
                }

                nodes.append(decision.toString());
            }
        }

        return activityNode;
    }
}

