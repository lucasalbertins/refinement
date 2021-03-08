package com.ref.parser.activityDiagram;

import com.change_vision.jude.api.inf.model.IActivity;
import com.change_vision.jude.api.inf.model.IActivityNode;
import com.change_vision.jude.api.inf.model.IFlow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ADDefineSignal {

    private IActivity ad;

    private HashMap<Pair<IActivity, String>, ArrayList<String>> alphabetNode;
    private HashMap<Pair<IActivity, String>, String> syncChannelsEdge;
    private List<IActivityNode> queueNode;
    private List<Pair<String, Integer>> countSignal;
    //private List<Pair<String, Integer>> countAccept;
    private List<String> createdSignal;
    //private List<String> createdAccept;
    private ADUtils adUtils;

    public ADDefineSignal(IActivity ad, HashMap<Pair<IActivity, String>, ArrayList<String>> alphabetNode2, HashMap<Pair<IActivity, String>, String> syncChannelsEdge2,
                          List<IActivityNode> queueNode, List<Pair<String, Integer>> countSignal, List<Pair<String, Integer>> countAccept,
                          List<String> createdSignal, List<String> createdAccept, ADUtils adUtils) {
        this.ad = ad;
        this.alphabetNode = alphabetNode2;
        this.syncChannelsEdge = syncChannelsEdge2;
        this.queueNode = queueNode;
        this.countSignal = countSignal;
        //this.countAccept = countAccept;
        this.createdSignal = createdSignal;
        //this.createdAccept = createdAccept;
        this.adUtils = adUtils;
    }

    public IActivityNode defineSignal(IActivityNode activityNode, StringBuilder nodes, int code) {
        StringBuilder signal = new StringBuilder();
        ArrayList<String> alphabet = new ArrayList<>();
        String endDiagram = "END_DIAGRAM_" + adUtils.nameDiagramResolver(ad.getName());
        IFlow[] outFlows = activityNode.getOutgoings();
        IFlow[] inFlows = activityNode.getIncomings();
        
////////////////////////////////////////////////////////////////////////////////////////
        int idSignal = 1;
        for (int i = 0; i < countSignal.size(); i++) {
        	String nSignal = adUtils.nameRobochartResolver(activityNode.getName(), ".out");
            if (countSignal.get(i).getKey().equals(nSignal)) {
                idSignal = countSignal.get(i).getValue();
                break;
            }
        }
     
        String nameSignal = adUtils.nameDiagramResolver("signal_" + activityNode.getName()) + "_" + idSignal + "_" + adUtils.nameDiagramResolver(ad.getName());
        String nameSignalTermination = adUtils.nameDiagramResolver("signal_" + activityNode.getName()) + "_" + idSignal + "_" + adUtils.nameDiagramResolver(ad.getName()) + "_t";
////////////////////////////////////////////////////////////////////////////////////////
        if (code == 0) {
            signal.append(nameSignal + "(id) = ");

            signal.append("(");
            for (int i = 0; i < inFlows.length; i++) {
            	 Pair<IActivity,String> key = new Pair<IActivity, String>(ad,inFlows[i].getId());
                if (syncChannelsEdge.containsKey(key)) {
                    String ceIn = syncChannelsEdge.get(key);

                    signal.append("(");
                    if (i >= 0 && (i < inFlows.length - 1)) {
                        adUtils.ce(alphabet, signal, ceIn, " -> SKIP) ||| ");
                    } else {
                        adUtils.ce(alphabet, signal, ceIn, " -> SKIP)");
                    }
                }
            }

            signal.append("); ");
////////////////////////////////////////////////////////////////////////////////////////            
            if (inFlows.length == 1 && inFlows[0].getStereotypes().length > 0 && inFlows[0].getStereotypes()[0].equals("UNTIL")) {
//            	adUtils.until(alphabet, signal, adUtils.nameDiagramResolver(activityNode.getName()) + ".out", " -> SKIP; ");
    			adUtils.until(alphabet, signal, adUtils.nameRobochartResolver(activityNode.getName(), ".out"), " -> SKIP; ");
            } else {
//            	adUtils.signal(alphabet ,adUtils.nameDiagramResolver(activityNode.getName()), signal);
            	adUtils.signal(alphabet ,adUtils.nameRobochartResolver(activityNode.getName(), ".out"), signal);
            }
////////////////////////////////////////////////////////////////////////////////////////            
//            if (inFlows.length > 0) {
//                for (int i = 0; i < inFlows.length; i++) {
//                	Pair<IActivity,String> key = new Pair<IActivity, String>(ad,inFlows[i].getId());
//                    if (syncChannelsEdge.containsKey(key)) {
//                    	for (int j = 0; j < inFlows.length; j++) {
//                        	String untilIn = inFlows[i].getStereotypes()[j];							
//                        	if (i >= 0 && (i < inFlows.length - 1)) {
//                        		adUtils.until(alphabet, signal, untilIn, " -> SKIP; ||| ");
//                        	} else {
//                        		adUtils.until(alphabet, signal, untilIn, " -> SKIP; ");
//                        	}
//                        }
//                        
//                    }
//                }
//            }

//            adUtils.signal(alphabet, adUtils.nameDiagramResolver(activityNode.getName()), signal);

            adUtils.update(alphabet, signal, inFlows.length, outFlows.length, false);

            if (outFlows.length > 0) {
                signal.append("(");

                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String ce = adUtils.createCE();
                    Pair<IActivity,String> key = new Pair<IActivity, String>(ad,outFlows[i].getId());
                    syncChannelsEdge.put(key, ce);

                    signal.append("(");

                    if (i >= 0 && (i < outFlows.length - 1)) {
                        adUtils.ce(alphabet, signal, ce, " -> SKIP) ||| ");
                    } else {
                        adUtils.ce(alphabet, signal, ce, " -> SKIP)");
                    }
                }

                signal.append("); ");
            }

            signal.append(nameSignal + "(id)\n");

            signal.append(nameSignalTermination + "(id) = ");
            signal.append(nameSignal + "(id) /\\ " + endDiagram + "(id)\n");

            alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName())+".id");
            Pair<IActivity,String> key = new Pair<IActivity, String>(ad,adUtils.nameDiagramResolver("signal_" + activityNode.getName() + "_" + idSignal));
            alphabetNode.put(key, alphabet);
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
                Pair<IActivity,String> key = new Pair<IActivity, String>(ad,outFlows[i].getId());
                syncChannelsEdge.put(key, ce);

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

            signal.append(nameSignal + "(id) = ");

            signal.append("(");
            for (int i = 0; i < inFlows.length; i++) {
            	Pair<IActivity,String> key = new Pair<IActivity, String>(ad,inFlows[i].getId());
                if (syncChannelsEdge.containsKey(key)) {
                    String ceIn = syncChannelsEdge.get(key);

                    signal.append("(");
                    if (i >= 0 && (i < inFlows.length - 1)) {
                        adUtils.ce(alphabet, signal, ceIn, " -> SKIP) ||| ");
                    } else {
                        adUtils.ce(alphabet, signal, ceIn, " -> SKIP)");
                    }
                }
            }

            signal.append("); ");
////////////////////////////////////////////////////////////////////////////////////////
//            adUtils.signal(alphabet, adUtils.nameDiagramResolver(activityNode.getName()), signal);
            adUtils.signal(alphabet, adUtils.nameRobochartResolver(activityNode.getName(), ".out"), signal);
////////////////////////////////////////////////////////////////////////////////////////
            adUtils.update(alphabet, signal, inFlows.length, outFlows.length, false);

            if (outFlows.length > 0) {
                signal.append("(");

                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                	 Pair<IActivity,String> key = new Pair<IActivity, String>(ad,outFlows[i].getId());
                    String ce = syncChannelsEdge.get(key);

                    signal.append("(");

                    if (i >= 0 && (i < outFlows.length - 1)) {
                        adUtils.ce(alphabet, signal, ce, " -> SKIP) ||| ");
                    } else {
                        adUtils.ce(alphabet, signal, ce, " -> SKIP)");
                    }
                }

                signal.append("); ");
            }

            signal.append(nameSignal + "(id)\n");

            signal.append(nameSignalTermination + "(id) = ");
            signal.append(nameSignal + "(id) /\\ " + endDiagram + "(id)\n");

            alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName())+".id");
            Pair<IActivity,String> key = new Pair<IActivity, String>(ad,adUtils.nameDiagramResolver("signal_" + activityNode.getName() + "_" + idSignal));
            alphabetNode.put(key, alphabet);
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
