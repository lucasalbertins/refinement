package com.ref.parser.activityDiagram;

import com.change_vision.jude.api.inf.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ADDefineInputParameterNode {

    private IActivity ad;

    private HashMap<Pair<IActivity, String>, ArrayList<String>> parameterAlphabetNode;
    private HashMap<Pair<IActivity, String>, String> syncObjectsEdge;
    private HashMap<String, String> objectEdges;
    private List<IActivityNode> queueNode;
    private List<String> allInitial;
    private ArrayList<String> alphabetAllInitialAndParameter;
    private ADUtils adUtils;

    public ADDefineInputParameterNode(IActivity ad, HashMap<Pair<IActivity, String>, ArrayList<String>> parameterAlphabetNode2, HashMap<Pair<IActivity, String>, String> syncObjectsEdge2,
                                      HashMap<String, String> objectEdges, List<IActivityNode> queueNode, List<String> allInitial,
                                      ArrayList<String> alphabetAllInitialAndParameter, ADUtils adUtils) {
        this.ad = ad;
        this.parameterAlphabetNode = parameterAlphabetNode2;
        this.syncObjectsEdge = syncObjectsEdge2;
        this.objectEdges = objectEdges;
        this.queueNode = queueNode;
        this.allInitial = allInitial;
        this.alphabetAllInitialAndParameter = alphabetAllInitialAndParameter;
        this.adUtils = adUtils;
    }

    public IActivityNode defineInputParameterNode(IActivityNode activityNode, StringBuilder nodes) {
        StringBuilder parameterNode = new StringBuilder();
        ArrayList<String> alphabet = new ArrayList<>();
        String nameParameterNode = "parameter_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + "_t";
        IFlow[] outFlows = activityNode.getOutgoings();
        IFlow[] inFlows = activityNode.getIncomings();

        parameterNode.append(nameParameterNode + "(id) = ");

        adUtils.update(alphabet, parameterNode, inFlows.length, outFlows.length, false);
        adUtils.get(alphabet, parameterNode, adUtils.nameDiagramResolver(activityNode.getName()));

        parameterNode.append("(");

        for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
            //String oe = adUtils.createOE(adUtils.nameDiagramResolver(activityNode.getName()));
        	String typeObject = ((IActivityParameterNode)activityNode).getBase().getName();
        	String oe = adUtils.createOE(typeObject);
        	Pair<IActivity,String> key = new Pair<IActivity, String>(ad,outFlows[i].getId());
            syncObjectsEdge.put(key, oe);
            objectEdges.put(oe, typeObject);

            parameterNode.append("(");

            if (i >= 0 && i < outFlows.length - 1) {
                adUtils.oe(alphabet, parameterNode, oe, "!" + adUtils.nameDiagramResolver(activityNode.getName()), " -> SKIP) ||| ");
            } else {
                adUtils.oe(alphabet, parameterNode, oe, "!" + adUtils.nameDiagramResolver(activityNode.getName()), " -> SKIP)");
            }
        }

        parameterNode.append(")\n");
        Pair<IActivity,String> key = new Pair<IActivity, String>(ad,adUtils.nameDiagramResolver(activityNode.getName()));
        parameterAlphabetNode.put(key, alphabet);
        allInitial.add(nameParameterNode);
        for (String channel : alphabet) {
            if (!alphabetAllInitialAndParameter.contains(channel)) {
                alphabetAllInitialAndParameter.add(channel);
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


        for (int i = 1; i < outFlows.length; i++) {    //creates the parallel output channels
            if (outFlows[i].getTarget() instanceof IInputPin) {
                for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                    if (activityNodeSearch instanceof IAction) {
                        IInputPin[] inFlowPin = ((IAction) activityNodeSearch).getInputs();
                        for (int y = 0; y < inFlowPin.length; y++) {
                            if (inFlowPin[y].getId().equals(outFlows[i].getTarget().getId())) {
                                if (!queueNode.contains(activityNodeSearch)) {
                                    queueNode.add(activityNodeSearch);
                                }
                            }
                        }
                    }
                }
            } else {
                if (!queueNode.contains(outFlows[i].getTarget())) {
                    queueNode.add(outFlows[i].getTarget());
                }
            }
        }


        nodes.append(parameterNode.toString());

        return activityNode;
    }
}
