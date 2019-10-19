package com.ref.parser.activityDiagram;

import com.change_vision.jude.api.inf.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ADDefineFork {

    private IActivity ad;

    private HashMap<String, ArrayList<String>> alphabetNode;
    private HashMap<String, String> syncChannelsEdge;
    private HashMap<String, String> syncObjectsEdge;
    private HashMap<String, String> objectEdges;
    private List<IActivityNode> queueNode;
    private ADUtils adUtils;

    public ADDefineFork(IActivity ad, HashMap<String, ArrayList<String>> alphabetNode, HashMap<String, String> syncChannelsEdge,
                        HashMap<String, String> syncObjectsEdge, HashMap<String, String> objectEdges, List<IActivityNode> queueNode,
                        ADUtils adUtils) {
        this.ad = ad;
        this.alphabetNode = alphabetNode;
        this.syncChannelsEdge = syncChannelsEdge;
        this.syncObjectsEdge = syncObjectsEdge;
        this.objectEdges = objectEdges;
        this.queueNode = queueNode;
        this.adUtils = adUtils;
    }

    public IActivityNode defineFork(IActivityNode activityNode, StringBuilder nodes, int code) {
        StringBuilder forkNode = new StringBuilder();
        ArrayList<String> alphabet = new ArrayList<>();
        String nameFork = adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName());
        String nameForkTermination = adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + "_t";
        String endDiagram = "END_DIAGRAM_" + adUtils.nameDiagramResolver(ad.getName());
        IFlow[] outFlows = activityNode.getOutgoings();
        IFlow[] inFlows = activityNode.getIncomings();
        boolean syncBool = false;
        boolean sync2Bool = false;
        String nameObject = null;

        if (code == 0) {
            forkNode.append(nameFork + " = ");

            for (int i = 0; i < inFlows.length; i++) {
                if (syncChannelsEdge.containsKey(inFlows[i].getId())) {
                    String ceIn = syncChannelsEdge.get(inFlows[i].getId());
                    adUtils.ce(alphabet, forkNode, ceIn, " -> ");
                    syncBool = true;
                }
            }

            for (int i = 0; i < inFlows.length; i++) {
                if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
                    String oeIn = syncObjectsEdge.get(inFlows[i].getId());
                    nameObject = objectEdges.get(oeIn);
                    adUtils.oe(alphabet, forkNode, oeIn, "?" + nameObject, " -> ");
                    sync2Bool = true;
                }
            }

            adUtils.update(alphabet, forkNode, inFlows.length, outFlows.length, false);

            forkNode.append("(");

            if (syncBool) {
                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String ce = adUtils.createCE();
                    syncChannelsEdge.put(outFlows[i].getId(), ce);

                    forkNode.append("(");

                    if (i >= 0 && i < outFlows.length - 1) {
                        adUtils.ce(alphabet, forkNode, ce, " -> SKIP) ||| ");
                    } else {
                        adUtils.ce(alphabet, forkNode, ce, " -> SKIP)");
                    }
                }
            } else if (sync2Bool) {
                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String oe = adUtils.createOE(nameObject);
                    syncObjectsEdge.put(outFlows[i].getId(), oe);
                    objectEdges.put(oe, nameObject);
                    forkNode.append("(");

                    if (i >= 0 && i < outFlows.length - 1) {
                        adUtils.ce(alphabet, forkNode, oe, "!" + nameObject + " -> SKIP) ||| ");
                    } else {
                        adUtils.ce(alphabet, forkNode, oe, "!" + nameObject + " -> SKIP)");
                    }
                }
            }

            forkNode.append("); ");

            forkNode.append(nameFork + "\n");

            forkNode.append(nameForkTermination + " = ");
            forkNode.append(nameFork + " /\\ " + endDiagram + "\n");

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

            nodes.append(forkNode.toString());
        } else if (code == 1) {
            if (syncBool) {
                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String ce = adUtils.createCE();
                    syncChannelsEdge.put(outFlows[i].getId(), ce);

                    forkNode.append("(");

                    if (i >= 0 && i < outFlows.length - 1) {
                        adUtils.ce(alphabet, forkNode, ce, " -> SKIP) ||| ");
                    } else {
                        adUtils.ce(alphabet, forkNode, ce, " -> SKIP)");
                    }
                }
            } else if (sync2Bool) {
                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String oe = adUtils.createOE(nameObject);
                    syncObjectsEdge.put(outFlows[i].getId(), oe);
                    objectEdges.put(oe, nameObject);
                    forkNode.append("(");

                    if (i >= 0 && i < outFlows.length - 1) {
                        adUtils.ce(alphabet, forkNode, oe, "!" + nameObject + " -> SKIP) ||| ");
                    } else {
                        adUtils.ce(alphabet, forkNode, oe, "!" + nameObject + " -> SKIP)");
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

            nodes.append(forkNode.toString());
        } else if (code == 1) {
            for (int i = 0; i < inFlows.length; i++) {
                if (syncChannelsEdge.containsKey(inFlows[i].getId())) {
                    syncBool = true;
                }
            }

            for (int i = 0; i < inFlows.length; i++) {
                if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
                    String oeIn = syncObjectsEdge.get(inFlows[i].getId());
                    nameObject = objectEdges.get(oeIn);
                    sync2Bool = true;
                }
            }

            if (syncBool) {
                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String ce = adUtils.createCE();
                    syncChannelsEdge.put(outFlows[i].getId(), ce);

                    forkNode.append("(");

                    if (i >= 0 && i < outFlows.length - 1) {
                        adUtils.ce(alphabet, forkNode, ce, " -> SKIP) ||| ");
                    } else {
                        adUtils.ce(alphabet, forkNode, ce, " -> SKIP)");
                    }
                }
            } else if (sync2Bool) {
                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String oe = adUtils.createOE(nameObject);
                    syncObjectsEdge.put(outFlows[i].getId(), oe);
                    objectEdges.put(oe, nameObject);
                    forkNode.append("(");

                    if (i >= 0 && i < outFlows.length - 1) {
                        adUtils.ce(alphabet, forkNode, oe, "!" + nameObject + " -> SKIP) ||| ");
                    } else {
                        adUtils.ce(alphabet, forkNode, oe, "!" + nameObject + " -> SKIP)");
                    }
                }
            }

        } else if (code == 2) {
            forkNode.append(nameFork + " = ");

            for (int i = 0; i < inFlows.length; i++) {
                if (syncChannelsEdge.containsKey(inFlows[i].getId())) {
                    String ceIn = syncChannelsEdge.get(inFlows[i].getId());
                    adUtils.ce(alphabet, forkNode, ceIn, " -> ");
                    syncBool = true;
                }
            }

            for (int i = 0; i < inFlows.length; i++) {
                if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
                    String oeIn = syncObjectsEdge.get(inFlows[i].getId());
                    nameObject = objectEdges.get(oeIn);
                    adUtils.oe(alphabet, forkNode, oeIn, "?" + nameObject, " -> ");
                    sync2Bool = true;
                }
            }

            adUtils.update(alphabet, forkNode, inFlows.length, outFlows.length, false);

            forkNode.append("(");

            if (syncBool) {
                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String ce = syncChannelsEdge.get(outFlows[i].getId());

                    forkNode.append("(");

                    if (i >= 0 && i < outFlows.length - 1) {
                        adUtils.ce(alphabet, forkNode, ce, " -> SKIP) ||| ");
                    } else {
                        adUtils.ce(alphabet, forkNode, ce, " -> SKIP)");
                    }
                }
            } else if (sync2Bool) {
                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String oe = syncObjectsEdge.get(outFlows[i].getId());

                    forkNode.append("(");

                    if (i >= 0 && i < outFlows.length - 1) {
                        adUtils.ce(alphabet, forkNode, oe, "!" + nameObject + " -> SKIP) ||| ");
                    } else {
                        adUtils.ce(alphabet, forkNode, oe, "!" + nameObject + " -> SKIP)");
                    }
                }
            }

            forkNode.append("); ");

            forkNode.append(nameFork + "\n");

            forkNode.append(nameForkTermination + " = ");
            forkNode.append(nameFork + " /\\ " + endDiagram + "\n");

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

            nodes.append(forkNode.toString());
        }

        return activityNode;
    }
}
