package com.ref.parser.activityDiagram;

import com.change_vision.jude.api.inf.model.*;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ADDefineDecision {

    private IActivity ad;

    private HashMap<String, ArrayList<String>> alphabetNode;
    private HashMap<String, String> syncChannelsEdge;
    private HashMap<String, String> syncObjectsEdge;
    private HashMap<String, String> objectEdges;
    private List<IActivityNode> queueNode;
    private HashMap<String, String> parameterNodesInput;
    private ADUtils adUtils;

    public ADDefineDecision(IActivity ad, HashMap<String, ArrayList<String>> alphabetNode, HashMap<String, String> syncChannelsEdge,
                            HashMap<String, String> syncObjectsEdge, HashMap<String, String> objectEdges, List<IActivityNode> queueNode,
                            HashMap<String, String> parameterNodesInput, ADUtils adUtils) {
        this.ad = ad;
        this.alphabetNode = alphabetNode;
        this.syncChannelsEdge = syncChannelsEdge;
        this.syncObjectsEdge = syncObjectsEdge;
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
        String decisionInputFlow = null;

        if (code == 0) {
            for (int i = 0; i < inFlows.length; i++) {

                String[] stereotype = inFlows[i].getStereotypes();

                for (int j = 0; j < stereotype.length; j++) {
                    if (stereotype[j].equals("decisionInputFlow")) {
                        decisionInputFlow = objectEdges.get(syncObjectsEdge.get(inFlows[i].getId()));
                    }
                }
            }

            if (decisionInputFlow != null && inFlows.length == 1) {    //just object
                decision.append(nameDecision + " = ");

                for (int i = 0; i < inFlows.length; i++) {
                    if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
                        String ceIn = syncObjectsEdge.get(inFlows[i].getId());
                        adUtils.oe(alphabet, decision, ceIn, "?" + decisionInputFlow, " -> ");
                    }
                }

                adUtils.update(alphabet, decision, 1, 1, false);

                decision.append("(");

                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String oe = adUtils.createOE(decisionInputFlow);
                    syncObjectsEdge.put(outFlows[i].getId(), oe);
                    objectEdges.put(oe, decisionInputFlow);

                    decision.append(outFlows[i].getGuard() + " & (dc -> ");
                    if (!alphabet.contains("dc")) {
                        alphabet.add("dc");
                    }

                    if (i >= 0 && i < outFlows.length - 1) {
                        adUtils.oe(alphabet, decision, oe, "!" + decisionInputFlow, " -> SKIP) [] ");
                    } else {
                        adUtils.oe(alphabet, decision, oe, "!" + decisionInputFlow, " -> SKIP)");
                    }
                }

                decision.append("); ");

                decision.append(nameDecision + "\n");

                decision.append(nameDecisionTermination + " = ");
                decision.append("(" + nameDecision + " /\\ " + endDiagram + ") \\{|dc|}\n");

                alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()));
                alphabetNode.put(adUtils.nameDiagramResolver(activityNode.getName()), alphabet);

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
            } else if (decisionInputFlow != null && inFlows.length > 1) {                    //object and control
                decision.append(nameDecision + " = ");

                String sync2 = "";
                String sync = "";

                for (int i = 0; i < inFlows.length; i++) {
                    if (syncChannelsEdge.containsKey(inFlows[i].getId())) {
                        sync2 = inFlows[i].getId();
                    }

                    if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
                        sync = inFlows[i].getId();
                    }
                }


                String ceIn2 = syncChannelsEdge.get(sync2);
                String ceIn = syncObjectsEdge.get(sync);

                decision.append("((");
                adUtils.ce(alphabet, decision, ceIn2, " -> SKIP");

                decision.append(") ||| (");
                adUtils.oe(alphabet, decision, ceIn, "?" + decisionInputFlow, " -> ");

                adUtils.setLocal(alphabet, decision, decisionInputFlow, adUtils.nameDiagramResolver(activityNode.getName()), decisionInputFlow);
                decision.append("SKIP)); ");

                adUtils.update(alphabet, decision, 2, 1, false);
                adUtils.getLocal(alphabet, decision, decisionInputFlow, adUtils.nameDiagramResolver(activityNode.getName()), decisionInputFlow);

                decision.append("(");

                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String ce = adUtils.createCE();
                    syncChannelsEdge.put(outFlows[i].getId(), ce);

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

                decision.append(nameDecision + "\n");

                decision.append(nameDecisionTermination + " = ");
                decision.append("((" + nameDecision + " /\\ " + endDiagram + ") ");

                decision.append("[|{|");
                decision.append("get_" + decisionInputFlow + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                decision.append("set_" + decisionInputFlow + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                decision.append("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()));
                decision.append("|}|] ");
                decision.append("Mem_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + "_" + decisionInputFlow + "_t(" + adUtils.getDefaultValue(parameterNodesInput.get(decisionInputFlow)) + ")) ");

                decision.append("\\{|");
                decision.append("get_" + decisionInputFlow + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                decision.append("set_" + decisionInputFlow + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                decision.append("dc");
                decision.append("|}\n");

                alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()));
                alphabetNode.put(adUtils.nameDiagramResolver(activityNode.getName()), alphabet);

                activityNode = outFlows[0].getTarget();    //set next action or control node

                for (int i = 1; i < outFlows.length; i++) {    //puts the remaining nodes in the queue
                    if (!queueNode.contains(outFlows[i].getTarget())) {
                        queueNode.add(outFlows[i].getTarget());
                    }
                }

                nodes.append(decision.toString());
            } else {        //just control
                decision.append(nameDecision + " = ");

                String sync = "";

                sync = inFlows[0].getId();

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
                    syncChannelsEdge.put(outFlows[i].getId(), ce);

                    // tratamento de guarda
                    if (outFlows[i].getGuard().length() == 0) {
                        decision.append("(");
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

                decision.append(nameDecision + "\n");

                decision.append(nameDecisionTermination + " = ");
                decision.append(nameDecision + " /\\ " + endDiagram + "\n");

                alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()));
                alphabetNode.put(adUtils.nameDiagramResolver(activityNode.getName()), alphabet);

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
                        decisionInputFlow = objectEdges.get(syncObjectsEdge.get(inFlows[i].getId()));
                    }
                }
            }

            if (decisionInputFlow != null && inFlows.length == 1) {    //just object
                decision.append(nameDecision + " = ");

                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String oe = adUtils.createOE(decisionInputFlow);
                    syncObjectsEdge.put(outFlows[i].getId(), oe);
                    objectEdges.put(oe, decisionInputFlow);

                    decision.append(outFlows[i].getGuard() + " & (dc -> ");
                    if (!alphabet.contains("dc")) {
                        alphabet.add("dc");
                    }

                    if (i >= 0 && i < outFlows.length - 1) {
                        adUtils.oe(alphabet, decision, oe, "!" + decisionInputFlow, " -> SKIP) [] ");
                    } else {
                        adUtils.oe(alphabet, decision, oe, "!" + decisionInputFlow, " -> SKIP)");
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

            } else if (decisionInputFlow != null && inFlows.length > 1) {                    //object and control
                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String ce = adUtils.createCE();
                    syncChannelsEdge.put(outFlows[i].getId(), ce);

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
                    syncChannelsEdge.put(outFlows[i].getId(), ce);

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
            }
        } else if (code == 2) {
            for (int i = 0; i < inFlows.length; i++) {

                String[] stereotype = inFlows[i].getStereotypes();

                for (int j = 0; j < stereotype.length; j++) {
                    if (stereotype[j].equals("decisionInputFlow")) {
                        //decisionInputFlow = inFlows[i].getSource().getName();
                        decisionInputFlow = objectEdges.get(syncObjectsEdge.get(inFlows[i].getId()));
                    }
                }
            }

            if (decisionInputFlow != null && inFlows.length == 1) {    //just object
                decision.append(nameDecision + " = ");

                for (int i = 0; i < inFlows.length; i++) {
                    if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
                        String ceIn = syncObjectsEdge.get(inFlows[i].getId());
                        adUtils.oe(alphabet, decision, ceIn, "?" + decisionInputFlow, " -> ");
                    }
                }

                adUtils.update(alphabet, decision, 1, 1, false);

                decision.append("(");

                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String oe = syncObjectsEdge.get(outFlows[i].getId());

                    decision.append(outFlows[i].getGuard() + " & (dc -> ");
                    if (!alphabet.contains("dc")) {
                        alphabet.add("dc");
                    }

                    if (i >= 0 && i < outFlows.length - 1) {
                        adUtils.oe(alphabet, decision, oe, "!" + decisionInputFlow, " -> SKIP) [] ");
                    } else {
                        adUtils.oe(alphabet, decision, oe, "!" + decisionInputFlow, " -> SKIP)");
                    }
                }

                decision.append("); ");

                decision.append(nameDecision + "\n");

                decision.append(nameDecisionTermination + " = ");
                decision.append("(" + nameDecision + " /\\ " + endDiagram + ") \\{|dc|}\n");

                alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()));
                alphabetNode.put(adUtils.nameDiagramResolver(activityNode.getName()), alphabet);

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
            } else if (decisionInputFlow != null && inFlows.length > 1) {                    //object and control
                decision.append(nameDecision + " = ");

                String sync2 = "";
                String sync = "";

                for (int i = 0; i < inFlows.length; i++) {
                    if (syncChannelsEdge.containsKey(inFlows[i].getId())) {
                        sync2 = inFlows[i].getId();
                    }

                    if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
                        sync = inFlows[i].getId();
                    }
                }


                String ceIn2 = syncChannelsEdge.get(sync2);
                String ceIn = syncObjectsEdge.get(sync);

                decision.append("((");
                adUtils.ce(alphabet, decision, ceIn2, " -> SKIP");

                decision.append(") ||| (");
                adUtils.oe(alphabet, decision, ceIn, "?" + decisionInputFlow, " -> ");

                adUtils.setLocal(alphabet, decision, decisionInputFlow, adUtils.nameDiagramResolver(activityNode.getName()), decisionInputFlow);
                decision.append("SKIP)); ");

                adUtils.update(alphabet, decision, 2, 1, false);
                adUtils.getLocal(alphabet, decision, decisionInputFlow, adUtils.nameDiagramResolver(activityNode.getName()), decisionInputFlow);

                decision.append("(");

                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String ce = syncChannelsEdge.get(outFlows[i].getId());

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

                decision.append(nameDecision + "\n");

                decision.append(nameDecisionTermination + " = ");
                decision.append("((" + nameDecision + " /\\ " + endDiagram + ") ");

                decision.append("[|{|");
                decision.append("get_" + decisionInputFlow + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                decision.append("set_" + decisionInputFlow + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                decision.append("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()));
                decision.append("|}|] ");
                decision.append("Mem_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + "_" + decisionInputFlow + "_t(" + adUtils.getDefaultValue(parameterNodesInput.get(decisionInputFlow)) + ")) ");

                decision.append("\\{|");
                decision.append("get_" + decisionInputFlow + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                decision.append("set_" + decisionInputFlow + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                decision.append("dc");
                decision.append("|}\n");

                alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()));
                alphabetNode.put(adUtils.nameDiagramResolver(activityNode.getName()), alphabet);

                activityNode = outFlows[0].getTarget();    //set next action or control node

                for (int i = 1; i < outFlows.length; i++) {    //puts the remaining nodes in the queue
                    if (!queueNode.contains(outFlows[i].getTarget())) {
                        queueNode.add(outFlows[i].getTarget());
                    }
                }

                nodes.append(decision.toString());
            } else {        //just control
                decision.append(nameDecision + " = ");

                String sync = "";

                sync = inFlows[0].getId();

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
                    String ce = syncChannelsEdge.get(outFlows[i].getId());

                    // tratamento de guarda
                    if (outFlows[i].getGuard().length() == 0) {
                        decision.append("(");
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

                decision.append(nameDecision + "\n");

                decision.append(nameDecisionTermination + " = ");
                decision.append(nameDecision + " /\\ " + endDiagram + "\n");

                alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()));
                alphabetNode.put(adUtils.nameDiagramResolver(activityNode.getName()), alphabet);

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