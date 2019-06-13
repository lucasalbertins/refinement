package com.ref.parser.activityDiagram;

import com.change_vision.jude.api.inf.model.IActivity;
import com.change_vision.jude.api.inf.model.IActivityNode;
import com.change_vision.jude.api.inf.model.IFlow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ADDefineInitialNode {

    private IActivity ad;
    private List<String> allInitial;
    private ArrayList<String> alphabetAllInitialAndParameter;
    private List<IActivityNode> queueNode;
    private HashMap<String, String> syncChannelsEdge;
    private ADUtils adUtils;

    public ADDefineInitialNode(IActivity ad, List<String> allInitial, ArrayList<String> alphabetAllInitialAndParameter,
                               List<IActivityNode> queueNode, HashMap<String, String> syncChannelsEdge, ADUtils adUtils) {
        this.ad = ad;
        this.allInitial = allInitial;
        this.alphabetAllInitialAndParameter = alphabetAllInitialAndParameter;
        this.queueNode = queueNode;
        this.syncChannelsEdge = syncChannelsEdge;
        this.adUtils = adUtils;
    }

    public IActivityNode defineInitialNode(IActivityNode activityNode, StringBuilder nodes) {
        StringBuilder initialNode = new StringBuilder();
        ArrayList<String> alphabet = new ArrayList<>();
        String nameInitialNode = adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + "_t";
        IFlow[] outFlows = activityNode.getOutgoings();
        IFlow[] inFlows = activityNode.getIncomings();

        initialNode.append(nameInitialNode + " = ");

        adUtils.update(alphabet, initialNode, inFlows.length, outFlows.length, false);

        initialNode.append("(");

        for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
            String ce = adUtils.createCE();
            syncChannelsEdge.put(outFlows[i].getId(), ce);
            initialNode.append("(");

            if (i >= 0 && i < outFlows.length - 1) {
                adUtils.ce(alphabet, initialNode, ce, " -> SKIP) ||| ");
            } else {
                adUtils.ce(alphabet, initialNode, ce, " -> SKIP)");
            }
        }

        initialNode.append(")\n");

        allInitial.add(nameInitialNode);
        for (String channel : alphabet) {
            if (!alphabetAllInitialAndParameter.contains(channel)) {
                alphabetAllInitialAndParameter.add(channel);
            }
        }

        activityNode = outFlows[0].getTarget();    //set next action or control node

        for (int i = 1; i < outFlows.length; i++) {    //puts the remaining nodes in the queue
            if (!queueNode.contains(outFlows[i].getTarget())) {
                queueNode.add(outFlows[i].getTarget());
            }
        }

        nodes.append(initialNode.toString());

        return activityNode;
    }
}
