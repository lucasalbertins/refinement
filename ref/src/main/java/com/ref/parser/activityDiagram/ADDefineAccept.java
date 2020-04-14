package com.ref.parser.activityDiagram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.change_vision.jude.api.inf.model.IActivity;
import com.change_vision.jude.api.inf.model.IActivityNode;
import com.change_vision.jude.api.inf.model.IFlow;


public class ADDefineAccept {

    private IActivity ad;

    private HashMap<String, ArrayList<String>> alphabetNode;
    private HashMap<String, String> syncChannelsEdge;
    private List<IActivityNode> queueNode;
	private List<Pair<String, Integer>> countAccept;
    private List<String> createdAccept;
    private ADUtils adUtils;

    public ADDefineAccept(IActivity ad, HashMap<String, ArrayList<String>> alphabetNode, HashMap<String, String> syncChannelsEdge,
                          List<IActivityNode> queueNode, List<Pair<String, Integer>> countAccept, List<String> createdAccept, ADUtils adUtils) {
        this.ad = ad;
        this.alphabetNode = alphabetNode;
        this.syncChannelsEdge = syncChannelsEdge;
        this.queueNode = queueNode;
        this.countAccept = countAccept;
        this.createdAccept = createdAccept;
        this.adUtils = adUtils;
    }

    public IActivityNode defineAccept(IActivityNode activityNode, StringBuilder nodes, int code) {
        StringBuilder accept = new StringBuilder();
        ArrayList<String> alphabet = new ArrayList<>();
        String endDiagram = "END_DIAGRAM_" + adUtils.nameDiagramResolver(ad.getName());
        IFlow[] outFlows = activityNode.getOutgoings();
        IFlow[] inFlows = activityNode.getIncomings();

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
            accept.append(nameAccept + "(id) = ");

            if (inFlows.length > 0) {
                accept.append("(");
                for (int i = 0; i < inFlows.length; i++) {
                    if (syncChannelsEdge.containsKey(inFlows[i].getId())) {
                        String ceIn = syncChannelsEdge.get(inFlows[i].getId());

                        accept.append("(");
                        if (i >= 0 && (i < inFlows.length - 1)) {
                            adUtils.ce(alphabet, accept, ceIn, " -> SKIP) ||| ");
                        } else {
                            adUtils.ce(alphabet, accept, ceIn, " -> SKIP)");
                        }
                    }
                }

                accept.append("); ");
            }

            adUtils.accept(alphabet ,adUtils.nameDiagramResolver(activityNode.getName()), accept);

            if (inFlows.length == 0) {
                adUtils.update(alphabet, accept, 1, outFlows.length, false); // outFlows - 1
            } else {
                adUtils.update(alphabet, accept, inFlows.length, outFlows.length, false);
            }

            if (outFlows.length > 0) {
                accept.append("(");

                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String ce = adUtils.createCE();
                    syncChannelsEdge.put(outFlows[i].getId(), ce);

                    accept.append("(");

                    if (i >= 0 && (i < outFlows.length - 1)) {
                        adUtils.ce(alphabet, accept, ce, " -> SKIP) ||| ");
                    } else {
                        adUtils.ce(alphabet, accept, ce, " -> SKIP)");
                    }
                }

                accept.append("); ");
            }

            accept.append(nameAccept + "(id)\n");

            accept.append(nameAcceptTermination + "(id) = ");
            accept.append(nameAccept + "(id) /\\ " + endDiagram + "(id)\n");

            alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName())+".id");
            alphabetNode.put(adUtils.nameDiagramResolver("accept_" + activityNode.getName() + "_" + idAccept), alphabet);
            createdAccept.add(activityNode.getId());

            if (outFlows.length > 0) {
                activityNode = outFlows[0].getTarget();    //set next action or control node

                for (int i = 1; i < outFlows.length; i++) {    //puts the remaining nodes in the queue
                    if (!queueNode.contains(outFlows[i].getTarget())) {
                        queueNode.add(outFlows[i].getTarget());
                    }
                }
            } else {
                activityNode = null;
            }

            nodes.append(accept.toString());

        } else if (code == 1) {

            for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                String ce = adUtils.createCE();
                syncChannelsEdge.put(outFlows[i].getId(), ce);

                accept.append("(");

                if (i >= 0 && (i < outFlows.length - 1)) {
                    adUtils.ce(alphabet, accept, ce, " -> SKIP) ||| ");
                } else {
                    adUtils.ce(alphabet, accept, ce, " -> SKIP)");
                }
            }

            if (outFlows.length > 0) {
                activityNode = outFlows[0].getTarget();    //set next action or control node

                for (int i = 1; i < outFlows.length; i++) {    //puts the remaining nodes in the queue
                    if (!queueNode.contains(outFlows[i].getTarget())) {
                        queueNode.add(outFlows[i].getTarget());
                    }
                }

            } else {
                activityNode = null;
            }

        } else if (code == 2) {

            accept.append(nameAccept + "(id) = ");

            if (inFlows.length > 0) {
                accept.append("(");

                for (int i = 0; i < inFlows.length; i++) {
                    if (syncChannelsEdge.containsKey(inFlows[i].getId())) {
                        String ceIn = syncChannelsEdge.get(inFlows[i].getId());

                        accept.append("(");
                        if (i >= 0 && (i < inFlows.length - 1)) {
                            adUtils.ce(alphabet, accept, ceIn, " -> SKIP) ||| ");
                        } else {
                            adUtils.ce(alphabet, accept, ceIn, " -> SKIP)");
                        }
                    }
                }

                accept.append("); ");
            }

            adUtils.accept(alphabet, adUtils.nameDiagramResolver(activityNode.getName()), accept);

            if (inFlows.length == 0) {
                adUtils.update(alphabet, accept, 1, outFlows.length, false); // outFlows - 1
            } else {
                adUtils.update(alphabet, accept, inFlows.length, outFlows.length, false);
            }

            if (outFlows.length > 0) {
                accept.append("(");

                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String ce = syncChannelsEdge.get(outFlows[i].getId());

                    accept.append("(");

                    if (i >= 0 && (i < outFlows.length - 1)) {
                        adUtils.ce(alphabet, accept, ce, " -> SKIP) ||| ");
                    } else {
                        adUtils.ce(alphabet, accept, ce, " -> SKIP)");
                    }
                }

                accept.append("); ");
            }

            accept.append(nameAccept + "(id)\n");

            accept.append(nameAcceptTermination + "(id) = ");
            accept.append(nameAccept + "(id) /\\ " + endDiagram + "(id)\n");

            alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName())+".id");
            alphabetNode.put(adUtils.nameDiagramResolver("accept_" + activityNode.getName() + "_" + idAccept), alphabet);
            createdAccept.add(activityNode.getId());

            if (outFlows.length > 0) {
                activityNode = outFlows[0].getTarget();    //set next action or control node

                for (int i = 1; i < outFlows.length; i++) {    //puts the remaining nodes in the queue
                    if (!queueNode.contains(outFlows[i].getTarget())) {
                        queueNode.add(outFlows[i].getTarget());
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
