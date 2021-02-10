package com.ref.parser.activityDiagram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.mail.internet.ParseException;

import com.change_vision.jude.api.inf.model.IActivity;
import com.change_vision.jude.api.inf.model.IActivityNode;
import com.change_vision.jude.api.inf.model.IFlow;
import com.sun.mail.iap.ParsingException;


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

    public IActivityNode defineAccept(IActivityNode activityNode, StringBuilder nodes, int code) throws com.ref.exceptions.ParsingException {
    	StringBuilder accept = new StringBuilder();
        ArrayList<String> alphabet = new ArrayList<>();
        String endDiagram = "END_DIAGRAM_" + adUtils.nameDiagramResolver(ad.getName());
        IFlow[] outFlows = activityNode.getOutgoings();
        IFlow[] inFlows = activityNode.getIncomings();

        int idAccept = 1;
        for (int i = 0; i < countAccept.size(); i++) {
//        	String nAccept = adUtils.nameDiagramResolver(activityNode.getName());
        	String nAccept = adUtils.nameRobochartResolver(activityNode.getName(), ".in");
            if (countAccept.get(i).getKey().equals(nAccept)) {
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
//                	System.out.println(inFlows[i].getStereotypes()[0]); //-> RETORNA Until
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
            
            if (inFlows.length == 1 && inFlows[0].getStereotypes().length > 0 && inFlows[0].getStereotypes()[0].equals("UNTIL")) {
//    			adUtils.until(alphabet, accept, adUtils.nameDiagramResolver(activityNode.getName()) + ".in", " -> SKIP; "); 
    			adUtils.until(alphabet, accept, adUtils.nameRobochartResolver(activityNode.getName(), ".in"), " -> SKIP; ");
            } else {
//            	adUtils.accept(alphabet ,adUtils.nameDiagramResolver(activityNode.getName()), accept);
            	adUtils.accept(alphabet ,adUtils.nameRobochartResolver(activityNode.getName(), ".in"), accept);
            }
            
//------------------------------------------------------------------            
//            String untilIn = inFlows[i].getStereotypes()[0];							
//        	if (untilIn.equals("UNTIL")) {
//        		if (inFlows.length > 1) {
//					throw new com.ref.exceptions.ParsingException("When using UNTIL stereotype only one edge is allowed ( see " + activityNode.getName() + ").");
//				}
//        		if (i >= 0 && (i < inFlows.length - 1)) {
//        			adUtils.until(alphabet, accept, untilIn, " -> SKIP; ||| ");
//        		} else {
//        			adUtils.until(alphabet, accept, untilIn, " -> SKIP; ");
//        		}
//        	}  
//------------------------------------------------------------------
//            if (inFlows.length > 0) {
//                for (int i = 0; i < inFlows.length; i++) {
//                	Pair<IActivity,String> key = new Pair<IActivity, String>(ad,inFlows[i].getId());
//                    if (syncChannelsEdge.containsKey(key)) {
//                    	for (int j = 0; j < inFlows.length; j++) {
//                    		if (inFlows[i].getStereotypes()[j] == "UNTIL") {
//                    			String untilIn = inFlows[i].getStereotypes()[j];							
//                    			if (i >= 0 && (i < inFlows.length - 1)) {
//                    				adUtils.until(alphabet, accept, untilIn, " -> SKIP; ||| ");
//                    			} else {
//                    				adUtils.until(alphabet, accept, untilIn, " -> SKIP; ");
//                    			}								
//							}
//                        }
//                        
//                    }
//                }
//            }

//            adUtils.accept(alphabet ,adUtils.nameDiagramResolver(activityNode.getName()), accept);

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

//            adUtils.accept(alphabet, adUtils.nameDiagramResolver(activityNode.getName()), accept);
            adUtils.accept(alphabet, adUtils.nameRobochartResolver(activityNode.getName(), ".in"), accept);

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
