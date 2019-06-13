package com.ref.parser.activityDiagram;

import com.change_vision.jude.api.inf.model.IActivity;
import com.change_vision.jude.api.inf.model.IActivityDiagram;
import com.change_vision.jude.api.inf.model.IActivityNode;
import com.change_vision.jude.api.inf.model.IFlow;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ADDefineSignal {

    private IActivity ad;

    private HashMap<String, ArrayList<String>> alphabetNode;
    private HashMap<String, String> syncChannelsEdge;
    private List<IActivityNode> queueNode;
    private List<Pair<String, Integer>> countSignal;
    private List<Pair<String, Integer>> countAccept;
    private List<String> createdSignal;
    private List<String> createdAccept;
    private ADUtils adUtils;

    public ADDefineSignal(IActivity ad, HashMap<String, ArrayList<String>> alphabetNode, HashMap<String, String> syncChannelsEdge,
                          List<IActivityNode> queueNode, List<Pair<String, Integer>> countSignal, List<Pair<String, Integer>> countAccept,
                          List<String> createdSignal, List<String> createdAccept, ADUtils adUtils) {
        this.ad = ad;
        this.alphabetNode = alphabetNode;
        this.syncChannelsEdge = syncChannelsEdge;
        this.queueNode = queueNode;
        this.countSignal = countSignal;
        this.countAccept = countAccept;
        this.createdSignal = createdSignal;
        this.createdAccept = createdAccept;
        this.adUtils = adUtils;
    }

    public IActivityNode defineSignal(IActivityNode activityNode, StringBuilder nodes, int code) {
        StringBuilder signal = new StringBuilder();
        ArrayList<String> alphabet = new ArrayList<>();
        String endDiagram = "END_DIAGRAM_" + adUtils.nameDiagramResolver(ad.getName());
        IFlow[] outFlows = activityNode.getOutgoings();
        IFlow[] inFlows = activityNode.getIncomings();

        int idSignal = 1;
        for (int i = 0; i < countSignal.size(); i++) {
            if (countSignal.get(i).getKey().equals(adUtils.nameDiagramResolver(activityNode.getName()))) {
                idSignal = countSignal.get(i).getValue();
                break;
            }
        }

        String nameSignal = adUtils.nameDiagramResolver("signal_" + activityNode.getName()) + "_" + idSignal + "_" + adUtils.nameDiagramResolver(ad.getName());
        String nameSignalTermination = adUtils.nameDiagramResolver("signal_" + activityNode.getName()) + "_" + idSignal + "_" + adUtils.nameDiagramResolver(ad.getName()) + "_t";

        if (code == 0) {
            signal.append(nameSignal + " = ");

            signal.append("(");
            for (int i = 0; i < inFlows.length; i++) {
                if (syncChannelsEdge.containsKey(inFlows[i].getId())) {
                    String ceIn = syncChannelsEdge.get(inFlows[i].getId());

                    signal.append("(");
                    if (i >= 0 && (i < inFlows.length - 1)) {
                        adUtils.ce(alphabet, signal, ceIn, " -> SKIP) ||| ");
                    } else {
                        adUtils.ce(alphabet, signal, ceIn, " -> SKIP)");
                    }
                }
            }

            signal.append("); ");

            adUtils.signal(alphabet, adUtils.nameDiagramResolver(activityNode.getName()), signal);

            adUtils.update(alphabet, signal, inFlows.length, outFlows.length, false);

            if (outFlows.length > 0) {
                signal.append("(");

                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String ce = adUtils.createCE();
                    syncChannelsEdge.put(outFlows[i].getId(), ce);

                    signal.append("(");

                    if (i >= 0 && (i < outFlows.length - 1)) {
                        adUtils.ce(alphabet, signal, ce, " -> SKIP) ||| ");
                    } else {
                        adUtils.ce(alphabet, signal, ce, " -> SKIP)");
                    }
                }

                signal.append("); ");
            }

            signal.append(nameSignal + "\n");

            signal.append(nameSignalTermination + " = ");
            signal.append(nameSignal + " /\\ " + endDiagram + "\n");

            alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()));
            alphabetNode.put(adUtils.nameDiagramResolver("signal_" + activityNode.getName() + "_" + idSignal), alphabet);
            createdSignal.add(activityNode.getId());

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

            nodes.append(signal.toString());

        } else if (code == 1) {

            for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                String ce = adUtils.createCE();
                syncChannelsEdge.put(outFlows[i].getId(), ce);

                signal.append("(");

                if (i >= 0 && (i < outFlows.length - 1)) {
                    adUtils.ce(alphabet, signal, ce, " -> SKIP) ||| ");
                } else {
                    adUtils.ce(alphabet, signal, ce, " -> SKIP)");
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

            signal.append(nameSignal + " = ");

            signal.append("(");
            for (int i = 0; i < inFlows.length; i++) {
                if (syncChannelsEdge.containsKey(inFlows[i].getId())) {
                    String ceIn = syncChannelsEdge.get(inFlows[i].getId());

                    signal.append("(");
                    if (i >= 0 && (i < inFlows.length - 1)) {
                        adUtils.ce(alphabet, signal, ceIn, " -> SKIP) ||| ");
                    } else {
                        adUtils.ce(alphabet, signal, ceIn, " -> SKIP)");
                    }
                }
            }

            signal.append("); ");

            adUtils.signal(alphabet, adUtils.nameDiagramResolver(activityNode.getName()), signal);

            adUtils.update(alphabet, signal, inFlows.length, outFlows.length, false);

            if (outFlows.length > 0) {
                signal.append("(");

                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String ce = syncChannelsEdge.get(outFlows[i].getId());

                    signal.append("(");

                    if (i >= 0 && (i < outFlows.length - 1)) {
                        adUtils.ce(alphabet, signal, ce, " -> SKIP) ||| ");
                    } else {
                        adUtils.ce(alphabet, signal, ce, " -> SKIP)");
                    }
                }

                signal.append("); ");
            }

            signal.append(nameSignal + "\n");

            signal.append(nameSignalTermination + " = ");
            signal.append(nameSignal + " /\\ " + endDiagram + "\n");

            alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()));
            alphabetNode.put(adUtils.nameDiagramResolver("signal_" + activityNode.getName() + "_" + idSignal), alphabet);
            createdSignal.add(activityNode.getId());

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

            nodes.append(signal.toString());
        }

        return activityNode;
    }
}
