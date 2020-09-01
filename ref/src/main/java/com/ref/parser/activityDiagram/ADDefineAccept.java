package com.ref.parser.activityDiagram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.change_vision.jude.api.inf.model.IActivity;
import com.change_vision.jude.api.inf.model.IActivityNode;
import com.change_vision.jude.api.inf.model.IFlow;


public class ADDefineAccept {

    private IActivity ad;

    private HashMap<Pair<IActivity, String>, ArrayList<String>> alphabetNode;
    private HashMap<Pair<IActivity, String>, String> syncChannelsEdge;
    private List<IActivityNode> queueNode;
	private List<Pair<String, Integer>> countAccept;
    private List<String> createdAccept;
    private ADUtils adUtils;

    public ADDefineAccept(IActivity ad, HashMap<Pair<IActivity, String>, ArrayList<String>> alphabetNode2, HashMap<Pair<IActivity, String>, String> syncChannelsEdge2,
                          List<IActivityNode> queueNode, List<Pair<String, Integer>> countAccept, List<String> createdAccept, ADUtils adUtils) {
        this.ad = ad;
        this.alphabetNode = alphabetNode2;
        this.syncChannelsEdge = syncChannelsEdge2;
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

                accept.append("); ");
            }

            adUtils.accept(alphabet ,adUtils.nameDiagramResolver(activityNode.getName()), accept,activityNode);

            if (inFlows.length == 0) {
                adUtils.update(alphabet, accept, 1, outFlows.length, false); // outFlows - 1
            } else {
                adUtils.update(alphabet, accept, inFlows.length, outFlows.length, false);
            }

            if (outFlows.length > 0) {
                accept.append("(");

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

                accept.append("); ");
            }

            accept.append(nameAccept + "(id)\n");

            accept.append(nameAcceptTermination + "(id) = ");
            accept.append(nameAccept + "(id) /\\ " + endDiagram + "(id)\n");

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
            } else {
                activityNode = null;
            }

            nodes.append(accept.toString());

        } else if (code == 1) {

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

                accept.append("); ");
            }

            adUtils.accept(alphabet, adUtils.nameDiagramResolver(activityNode.getName()), accept,activityNode);

            if (inFlows.length == 0) {
                adUtils.update(alphabet, accept, 1, outFlows.length, false); // outFlows - 1
            } else {
                adUtils.update(alphabet, accept, inFlows.length, outFlows.length, false);
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

                accept.append("); ");
            }

            accept.append(nameAccept + "(id)\n");

            accept.append(nameAcceptTermination + "(id) = ");
            accept.append(nameAccept + "(id) /\\ " + endDiagram + "(id)\n");

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
            } else {
                activityNode = null;
            }

            nodes.append(accept.toString());
        }

        return activityNode;
    }
}
