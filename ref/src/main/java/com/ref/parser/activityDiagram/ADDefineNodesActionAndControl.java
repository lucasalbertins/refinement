package com.ref.parser.activityDiagram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.ref.exceptions.ParsingException;
import com.ref.interfaces.activityDiagram.IAction;
import com.ref.interfaces.activityDiagram.IActivity;
import com.ref.interfaces.activityDiagram.IActivityDiagram;
import com.ref.interfaces.activityDiagram.IActivityNode;
import com.ref.interfaces.activityDiagram.IActivityParameterNode;
import com.ref.interfaces.activityDiagram.IControlNode;
import com.ref.interfaces.activityDiagram.IFlow;
import com.ref.interfaces.activityDiagram.IObjectNode;

public class ADDefineNodesActionAndControl {

    private IActivity ad;
    private IActivityDiagram adDiagram;

    private HashMap<String, Integer> countCall;
    private HashMap<Pair<IActivity, String>, ArrayList<String>> alphabetNode;
    private HashMap<Pair<IActivity, String>, ArrayList<String>> parameterAlphabetNode;
    private HashMap<Pair<IActivity, String>, String> syncChannelsEdge;
    private HashMap<Pair<IActivity, String>, String> syncObjectsEdge;
    private HashMap<String, String> objectEdges;
    private List<IActivityNode> queueNode;
    private List<IActivityNode> queueRecreateNode;
    private List<IActivity> callBehaviourList;
    private List<String> eventChannel;
    private List<String> lockChannel;
    private List<String> allInitial;
    private ArrayList<String> alphabetAllInitialAndParameter;
    private HashMap<String, String> parameterNodesInput;
    private HashMap<String, String> parameterNodesOutput;
    private HashMap<String, String> parameterNodesOutputObject;
    private List<Pair<String, Integer>> callBehaviourNumber;
    private Map<Pair<String, String>,String> memoryLocal;
    private List<Pair<String, String>> memoryLocalChannel;
    private List<ArrayList<String>> unionList;
    private HashMap<String, String> typeUnionList;
    private HashMap<String, List<String>> callBehaviourInputs;
    private HashMap<String, List<String>> callBehaviourOutputs;
    private List<Pair<String, Integer>> countSignal;
    private List<Pair<String, Integer>> countAccept;
    private HashMap<String, List<IActivity>> signalChannels;
    private List<String> signalChannelsLocal;
    private List<String> localSignalChannelsSync;
    private List<String> createdSignal;
    private List<String> createdAccept;
    private HashMap<String,Integer> allGuards;
    private ADUtils adUtils;
    private ADParser adParser;
    private ADDefineAction dAction;
    private ADDefineFinalNode dFinalNode;
    private ADDefineInitialNode dInitialNode;
    private ADDefineCallBehavior dCallBehavior;
    private ADDefineFork dFork;
    private ADDefineJoin dJoin;
    private ADDefineMerge dMerge;
    private ADDefineDecision dDecision;
    private ADDefineFlowFinal dFlowFinal;
    private ADDefineInputParameterNode dInputParameterNode;
    private ADDefineOutputParameterNode dOutputParameterNode;
    private ADDefineObjectNode dObjectNode;
    private ADDefineSignal dSignal;
    private ADDefineAccept dAccept;

    public ADDefineNodesActionAndControl(IActivity ad, IActivityDiagram adDiagram, HashMap<String, Integer> countCall, HashMap<Pair<IActivity, String>, ArrayList<String>> alphabetNode2,
                                         HashMap<Pair<IActivity, String>, ArrayList<String>> parameterAlphabetNode2, HashMap<Pair<IActivity, String>, String> syncChannelsEdge2,
                                         HashMap<Pair<IActivity, String>, String> syncObjectsEdge2, HashMap<String, String> objectEdges, List<IActivityNode> queueNode,
                                         List<IActivityNode> queueRecreateNode, List<IActivity> callBehaviourList, List<String> eventChannel, List<String> lockChannel,
                                         List<String> allInitial, ArrayList<String> alphabetAllInitialAndParameter, HashMap<String, String> parameterNodesInput,
                                         HashMap<String, String> parameterNodesOutput, HashMap<String, String> parameterNodesOutputObject, List<Pair<String, Integer>> callBehaviourNumber,
                                         Map<Pair<String, String>,String> memoryLocal, List<Pair<String, String>> memoryLocalChannel, List<ArrayList<String>> unionList, HashMap<String, String> typeUnionList,
                                         HashMap<String, List<String>> callBehaviourInputs, HashMap<String, List<String>> callBehaviourOutputs, List<Pair<String, Integer>> countSignal,
                                         List<Pair<String, Integer>> countAccept, HashMap<String,List<IActivity>> signalChannels, List<String> localSignalChannelsSync, List<String> createdSignal, List<String> createdAccept,
                                         HashMap<String, Integer> allGuards, List<String> signalChannelsLocal, ADUtils adUtils, ADParser adParser) {
        this.ad = ad;
        this.adDiagram = adDiagram;
        this.countCall = countCall;
        this.alphabetNode = alphabetNode2;
        this.parameterAlphabetNode = parameterAlphabetNode2;
        this.syncChannelsEdge = syncChannelsEdge2;
        this.syncObjectsEdge = syncObjectsEdge2;
        this.objectEdges = objectEdges;
        this.queueNode = queueNode;
        this.queueRecreateNode = queueRecreateNode;
        this.callBehaviourList = callBehaviourList;
        this.eventChannel = eventChannel;
        this.lockChannel = lockChannel;
        this.allInitial = allInitial;
        this.alphabetAllInitialAndParameter = alphabetAllInitialAndParameter;
        this.parameterNodesInput = parameterNodesInput;
        this.parameterNodesOutput = parameterNodesOutput;
        this.parameterNodesOutputObject = parameterNodesOutputObject;
        this.callBehaviourNumber = callBehaviourNumber;
        this.memoryLocal = memoryLocal;
        this.memoryLocalChannel = memoryLocalChannel;
        this.unionList = unionList;
        this.typeUnionList = typeUnionList;
        this.callBehaviourInputs = callBehaviourInputs;
        this.callBehaviourOutputs = callBehaviourOutputs;
        this.countSignal = countSignal;
        this.countAccept = countAccept;
        this.signalChannels = signalChannels;
        this.localSignalChannelsSync = localSignalChannelsSync;
        this.createdSignal = createdSignal;
        this.createdAccept = createdAccept;
        this.allGuards = allGuards;
        this.signalChannelsLocal = signalChannelsLocal;
        this.adUtils = adUtils;
        this.adParser = adParser;
    }
    
    
    
    public String defineNodes() throws ParsingException {
    	StringBuilder nodes = new StringBuilder();
    	
    	for (IActivityNode activityNode : ad.getActivityNodes()) {
    		 if (activityNode instanceof IAction) {
                 if (((IAction) activityNode).isCallBehaviorAction()) {
                     nodes.append(defineCallBehaviour(activityNode));
                 } else if (((IAction) activityNode).isSendSignalAction()) {
                     nodes.append(defineSignal(activityNode));
                 } else if (((IAction) activityNode).isAcceptEventAction()) {
                     nodes.append(defineAccept(activityNode));
                 } else {//TODO else if value specification(classe nova)
                     nodes.append(defineAction(activityNode));    // create action node and set next action node
                 }
             } else if (activityNode instanceof IControlNode) {
                 if (((IControlNode) activityNode).isFinalNode()) {
                     nodes.append(defineFinalNode(activityNode)); // create final node and set next action node
                 } else if (((IControlNode) activityNode).isFlowFinalNode()) {
                     nodes.append(defineFlowFinal(activityNode)); // create flow final and set next action node
                 } else if (((IControlNode) activityNode).isInitialNode()) {
                     nodes.append(defineInitialNode(activityNode)); // create initial node and set next action node
                 } else if (((IControlNode) activityNode).isForkNode()) {
                	 nodes.append(defineFork(activityNode)); // create fork node and set next action node
                 } else if (((IControlNode) activityNode).isJoinNode()) {
                	 nodes.append(defineJoin(activityNode)); // create join node and set next action node
                 } else if (((IControlNode) activityNode).isDecisionNode()) {
                	 nodes.append(defineDecision(activityNode)); // create decision node and set next action node                          
                 }else if(((IControlNode) activityNode).isMergeNode()){
                 	activityNode = defineMerge(activityNode, nodes, 0); // create merge node and set next action node
                 }
             } else if (activityNode instanceof IActivityParameterNode) {
                 if (activityNode.getOutgoings().length > 0) {
                     activityNode = defineInputParameterNode(activityNode, nodes);
                 } else if (activityNode.getIncomings().length > 0) {
                     activityNode = defineOutputParameterNode(activityNode, nodes);
                 } else {
                     activityNode = null;
                 }

             } else if (activityNode instanceof IObjectNode) {
                 activityNode = defineObjectNode(activityNode, nodes, 0);
             }
		}
    	
    	
    	
    	
    	
    	return nodes.toString();
    }
    /*
    public String defineNodesActionAndControl() throws ParsingException {
        for (IActivityNode activityNode : ad.getActivityNodes()) {
            if (activityNode instanceof IActivityParameterNode && activityNode.getOutgoings().length > 0) {
                try {
					parameterNodesInput.put(adUtils.nameDiagramResolver(activityNode.getName()), ((IActivityParameterNode) activityNode).getBase().getName());
				} catch (Exception e) {
					throw new ParsingException("Parameter node "+activityNode.getName()+" without base type\n");
				}
            }

            if (activityNode instanceof IActivityParameterNode && activityNode.getIncomings().length > 0) {
                try {
					parameterNodesOutput.put(adUtils.nameDiagramResolver(activityNode.getName()), ((IActivityParameterNode) activityNode).getBase().getName());
				} catch (Exception e) {
					throw new ParsingException("Parameter node "+activityNode.getName()+" without base type\n");
				}
            }

            if (!ADParser.containsCallBehavior &&  activityNode instanceof IAction && ((IAction) activityNode).isCallBehaviorAction()) {
                ADParser.containsCallBehavior = true;
            }
        }

        StringBuilder nodes = new StringBuilder();

        for (IActivityNode activityNode : ad.getActivityNodes()) {
            if (((activityNode instanceof IControlNode && ((IControlNode) activityNode).isInitialNode()) ||
                    (activityNode instanceof IAction && (((IAction) activityNode).isSendSignalAction() || ((IAction) activityNode).isAcceptEventAction())) ||
                    (activityNode instanceof IActivityParameterNode && activityNode.getIncomings().length == 0)) &&
                    !queueNode.contains(activityNode)) {

                queueNode.add(activityNode);
            }
        }

        int input = 0;
        int expectedInput = 0;

        while (queueNode.size() != 0) {
            IActivityNode activityNode = queueNode.get(0);
            queueNode.remove(0);

            input = adUtils.countAmount(activityNode);
            if (activityNode != null) {
                if (activityNode instanceof IAction) {
                    expectedInput = activityNode.getIncomings().length + ((IAction) activityNode).getInputs().length;
                } else {
                    expectedInput = activityNode.getIncomings().length;
                }
            }

            String name = activityNode.getName();
            if (activityNode instanceof IActivityParameterNode) {
                name = "parameter_" + activityNode.getName();
            }
            Pair<IActivity,String> key = new Pair<IActivity, String>(ad,adUtils.nameDiagramResolver(name));
            while (activityNode != null && !(alphabetNode.containsKey(key) || isSignal(activityNode))
                    && !queueRecreateNode.contains(activityNode)) {

                if (input == expectedInput) {
                    if (activityNode instanceof IAction) {
                        if (((IAction) activityNode).isCallBehaviorAction()) {
                            activityNode = defineCallBehaviour(activityNode, nodes, 0);
                        } else if (((IAction) activityNode).isSendSignalAction()) {
                            activityNode = defineSignal(activityNode, nodes, 0);
                        } else if (((IAction) activityNode).isAcceptEventAction()) {
                            activityNode = defineAccept(activityNode, nodes, 0);
                        } else {//TODO else if value specification(classe nova)
                            activityNode = defineAction(activityNode, nodes, 0);    // create action node and set next action node
                        }
                    } else if (activityNode instanceof IControlNode) {
                        if (((IControlNode) activityNode).isFinalNode()) {
                            activityNode = defineFinalNode(activityNode, nodes); // create final node and set next action node
                        } else if (((IControlNode) activityNode).isFlowFinalNode()) {
                            activityNode = defineFlowFinal(activityNode, nodes); // create flow final and set next action node
                        } else if (((IControlNode) activityNode).isInitialNode()) {
                            activityNode = defineInitialNode(activityNode, nodes); // create initial node and set next action node
                        } else if (((IControlNode) activityNode).isForkNode()) {
                            activityNode = defineFork(activityNode, nodes, 0); // create fork node and set next action node
                        } else if (((IControlNode) activityNode).isJoinNode()) {
                            activityNode = defineJoin(activityNode, nodes, 0); // create join node and set next action node
                        } else if (((IControlNode) activityNode).isDecisionNode()) {
                        	activityNode = defineDecision(activityNode, nodes, 0); // create decision node and set next action node                          
                        }else if(((IControlNode) activityNode).isMergeNode()){
                        	activityNode = defineMerge(activityNode, nodes, 0); // create merge node and set next action node
                        }
                    } else if (activityNode instanceof IActivityParameterNode) {
                        if (activityNode.getOutgoings().length > 0) {
                            activityNode = defineInputParameterNode(activityNode, nodes);
                        } else if (activityNode.getIncomings().length > 0) {
                            activityNode = defineOutputParameterNode(activityNode, nodes);
                        } else {
                            activityNode = null;
                        }

                    } else if (activityNode instanceof IObjectNode) {
                        activityNode = defineObjectNode(activityNode, nodes, 0);
                    }
                    input = adUtils.countAmount(activityNode);

                    if (activityNode != null) {
                        if (activityNode instanceof IAction) {
                            expectedInput = activityNode.getIncomings().length + ((IAction) activityNode).getInputs().length;
                        } else {
                            expectedInput = activityNode.getIncomings().length;
                        }

                        name = activityNode.getName();
                        key = new Pair<IActivity, String>(ad,adUtils.nameDiagramResolver(name));
                        if (activityNode instanceof IActivityParameterNode) {
                            name = "parameter_" + activityNode.getName();
                        }
                    }
                } else {
                    if (activityNode instanceof IAction) {
                        if (((IAction) activityNode).isCallBehaviorAction()) {
                            queueRecreateNode.add(activityNode);
                            activityNode = defineCallBehaviour(activityNode, nodes, 1);
                        } else if (((IAction) activityNode).isSendSignalAction()) {
                            queueRecreateNode.add(activityNode);
                            activityNode = defineSignal(activityNode, nodes, 1);
                        } else if (((IAction) activityNode).isAcceptEventAction()) {
                            queueRecreateNode.add(activityNode);
                            activityNode = defineAccept(activityNode, nodes, 1);
                        } else {
                            queueRecreateNode.add(activityNode);
                            activityNode = defineAction(activityNode, nodes, 1);    // create action node and set next action node
                        }
                    } else if (activityNode instanceof IControlNode) {
                        if (((IControlNode) activityNode).isFinalNode()) {
                            queueRecreateNode.add(activityNode);
                            activityNode = null;
                        } else if (((IControlNode) activityNode).isFlowFinalNode()) {
                            queueRecreateNode.add(activityNode);
                            activityNode = null;
                        } else if (((IControlNode) activityNode).isForkNode()) {
                            queueRecreateNode.add(activityNode);
                            activityNode = defineFork(activityNode, nodes, 1); // create fork node and set next action node
                        } else if (((IControlNode) activityNode).isJoinNode()) {
                            queueRecreateNode.add(activityNode);
                            activityNode = defineJoin(activityNode, nodes, 1); // create join node and set next action node
                        } else if (((IControlNode) activityNode).isDecisionMergeNode()) {
                            queueRecreateNode.add(activityNode);
                            if (activityNode.getOutgoings().length > 1) {
                                activityNode = defineDecision(activityNode, nodes, 1); // create decision node and set next action node
                            } else {
                                IFlow[] flows = activityNode.getIncomings();
                                boolean decision = false;
                                for (int i = 0; i < flows.length; i++) {

                                    String[] stereotype = flows[i].getStereotypes();

                                    for (int j = 0; j < stereotype.length; j++) {
                                        if (stereotype[j].equals("decisionInputFlow")) {
                                            decision = true;
                                        }
                                    }
                                }

                                if (decision) {
                                    activityNode = defineDecision(activityNode, nodes, 1); // create decision node and set next action node
                                } else {
                                    activityNode = defineMerge(activityNode, nodes, 1); // create merge node and set next action node
                                }
                            }
                        }
                    } else if (activityNode instanceof IObjectNode) {
                        queueRecreateNode.add(activityNode);
                        activityNode = defineObjectNode(activityNode, nodes, 1);
                    }

                    input = adUtils.countAmount(activityNode);

                    if (activityNode != null) {
                        if (activityNode instanceof IAction) {
                            expectedInput = activityNode.getIncomings().length + ((IAction) activityNode).getInputs().length;
                        } else {
                            expectedInput = activityNode.getIncomings().length;
                        }

                        name = activityNode.getName();
                        key = new Pair<IActivity, String>(ad,adUtils.nameDiagramResolver(name));
                        if (activityNode instanceof IActivityParameterNode) {
                            name = "parameter_" + activityNode.getName();
                        }
                    }
                }
            }
        }

         while (queueRecreateNode.size() != 0) {
            IActivityNode activityNode = queueRecreateNode.get(0);
            queueRecreateNode.remove(0);

            input = adUtils.countAmount(activityNode);
            if (activityNode != null) {
                if (activityNode instanceof IAction) {
                    expectedInput = activityNode.getIncomings().length + ((IAction) activityNode).getInputs().length;
                } else {
                    expectedInput = activityNode.getIncomings().length;
                }
            }

            String name = activityNode.getName();
            
            if (activityNode instanceof IActivityParameterNode) {
                name = "parameter_" + activityNode.getName();
            }
            Pair<IActivity,String> key = new Pair<IActivity, String>(ad,adUtils.nameDiagramResolver(name));
            while (activityNode != null && !(alphabetNode.containsKey(key) || isSignal(activityNode))) {    // Verifica se nó é nulo, se nó já foi criado e se todos os nós de entrada dele já foram criados
                if (activityNode instanceof IAction) {
                    if (((IAction) activityNode).isCallBehaviorAction()) {
                        activityNode = defineCallBehaviour(activityNode, nodes, 2);
                    } else if (((IAction) activityNode).isSendSignalAction()) {
                        activityNode = defineSignal(activityNode, nodes, 2);
                    } else if (((IAction) activityNode).isAcceptEventAction()) {
                        activityNode = defineAccept(activityNode, nodes, 2);
                    } else {
                        activityNode = defineAction(activityNode, nodes, 2);    // create action node and set next action node
                    }
                } else if (activityNode instanceof IControlNode) {
                    if (((IControlNode) activityNode).isFinalNode()) {
                        activityNode = defineFinalNode(activityNode, nodes); // create final node and set next action node
                    } else if (((IControlNode) activityNode).isFlowFinalNode()) {
                        activityNode = defineFlowFinal(activityNode, nodes); // create flow final and set next action node
                    } else if (((IControlNode) activityNode).isForkNode()) {
                        activityNode = defineFork(activityNode, nodes, 2); // create fork node and set next action node
                    } else if (((IControlNode) activityNode).isJoinNode()) {
                        activityNode = defineJoin(activityNode, nodes, 2); // create join node and set next action node
                    } else if (((IControlNode) activityNode).isDecisionMergeNode()) {

                        if (activityNode.getOutgoings().length > 1) {
                            activityNode = defineDecision(activityNode, nodes, 2); // create decision node and set next action node
                        } else {
                            IFlow[] flows = activityNode.getIncomings();
                            boolean decision = false;
                            for (int i = 0; i < flows.length; i++) {

                                String[] stereotype = flows[i].getStereotypes();

                                for (int j = 0; j < stereotype.length; j++) {
                                    if (stereotype[j].equals("decisionInputFlow")) {
                                        decision = true;
                                    }
                                }
                            }

                            if (decision) {
                                activityNode = defineDecision(activityNode, nodes, 2); // create decision node and set next action node
                            } else {
                                activityNode = defineMerge(activityNode, nodes, 2); // create merge node and set next action node
                            }
                        }
                    }
                } else if (activityNode instanceof IActivityParameterNode) {
                    if (activityNode.getOutgoings().length > 0) {
                        activityNode = defineInputParameterNode(activityNode, nodes);
                    } else if (activityNode.getIncomings().length > 0) {
                        activityNode = defineOutputParameterNode(activityNode, nodes);
                    } else {
                        activityNode = null;
                    }

                    
                } else if (activityNode instanceof IObjectNode) {
                    activityNode = defineObjectNode(activityNode, nodes, 2);
                }

                input = adUtils.countAmount(activityNode);

                if (activityNode != null) {
                    if (activityNode instanceof IAction) {
                        expectedInput = activityNode.getIncomings().length + ((IAction) activityNode).getInputs().length;
                    } else {
                        expectedInput = activityNode.getIncomings().length;
                    }                    
                    name = activityNode.getName();
                    key = new Pair<IActivity, String>(ad,adUtils.nameDiagramResolver(name));
                    if (activityNode instanceof IActivityParameterNode) {
                        name = "parameter_" + activityNode.getName();
                    }
                }
            }
        }

        nodes.append("\n");

        return nodes.toString();
    }*/

    private boolean isSignal(IActivityNode activityNode) {
        return (activityNode instanceof IAction &&
                ((((IAction) activityNode).isSendSignalAction() && createdSignal.contains(activityNode.getId())) ||
                        (((IAction) activityNode).isAcceptEventAction() && createdAccept.contains(activityNode.getId()))));
    }

    private String defineAction(IActivityNode activityNode) throws ParsingException {
        ADUtils adUtils = defineADUtils();

        dAction = new ADDefineAction(ad, alphabetNode, adUtils);

        return dAction.defineAction(activityNode);
    }

    private String defineFinalNode(IActivityNode activityNode) {
        ADUtils adUtils = defineADUtils();

        dFinalNode = new ADDefineFinalNode(ad, alphabetNode, syncChannelsEdge, syncObjectsEdge, objectEdges, adUtils);

        return dFinalNode.defineFinalNode(activityNode);
    }

    private String defineInitialNode(IActivityNode activityNode) {
        ADUtils adUtils = defineADUtils();

        dInitialNode = new ADDefineInitialNode(ad, allInitial, alphabetAllInitialAndParameter, queueNode, syncChannelsEdge, adUtils,alphabetNode);

        return dInitialNode.defineInitialNode(activityNode);
    }

    private String defineCallBehaviour(IActivityNode activityNode) throws ParsingException {
        ADUtils adUtils = defineADUtils();

        dCallBehavior = new ADDefineCallBehavior(ad, alphabetNode, adUtils);

        return dCallBehavior.defineCallBehaviour(activityNode);
    }

    private String defineFork(IActivityNode activityNode) throws ParsingException {
        ADUtils adUtils = defineADUtils();

        dFork = new  ADDefineFork(ad, alphabetNode, syncChannelsEdge, syncObjectsEdge, objectEdges, adUtils);

        return dFork.defineFork(activityNode);
    }

    private String defineJoin(IActivityNode activityNode) throws ParsingException {
        ADUtils adUtils = defineADUtils();

        dJoin = new ADDefineJoin(ad, alphabetNode, syncChannelsEdge, syncObjectsEdge,  objectEdges, adUtils);

        return dJoin.defineJoin(activityNode);
    }

    private IActivityNode defineMerge(IActivityNode activityNode, StringBuilder nodes, int code) {
        ADUtils adUtils = defineADUtils();

        dMerge = new ADDefineMerge(ad, alphabetNode, syncChannelsEdge, syncObjectsEdge, objectEdges, parameterNodesInput,
                unionList, typeUnionList, adUtils);

        return dMerge.defineMerge(activityNode, nodes, code);
    }

    private String defineDecision(IActivityNode activityNode) throws ParsingException {
        ADUtils adUtils = defineADUtils();

        dDecision = new ADDefineDecision(ad, alphabetNode, syncChannelsEdge, syncObjectsEdge, objectEdges, adUtils);

        return dDecision.defineDecision(activityNode);
    }

    private String defineFlowFinal(IActivityNode activityNode) throws ParsingException {
        ADUtils adUtils = defineADUtils();

        dFlowFinal = new ADDefineFlowFinal(ad, alphabetNode, syncChannelsEdge, syncObjectsEdge, objectEdges, adUtils);

        return dFlowFinal.defineFlowFinal(activityNode);
    }

    private IActivityNode defineInputParameterNode(IActivityNode activityNode, StringBuilder nodes) {
        ADUtils adUtils = defineADUtils();

        dInputParameterNode = new ADDefineInputParameterNode(ad, parameterAlphabetNode, syncObjectsEdge, objectEdges,
                queueNode, allInitial, alphabetAllInitialAndParameter, adUtils,alphabetNode);

        return dInputParameterNode.defineInputParameterNode(activityNode, nodes);
    }

    private IActivityNode defineOutputParameterNode(IActivityNode activityNode, StringBuilder nodes) {
        ADUtils adUtils = defineADUtils();

        dOutputParameterNode = new ADDefineOutputParameterNode(ad, alphabetNode, syncChannelsEdge, syncObjectsEdge,
                objectEdges,  parameterNodesInput, typeUnionList, adUtils);

        return dOutputParameterNode.defineOutputParameterNode(activityNode, nodes);
    }

    private IActivityNode defineObjectNode(IActivityNode activityNode, StringBuilder nodes, int code) {
        ADUtils adUtils = defineADUtils();

        dObjectNode = new ADDefineObjectNode(ad, alphabetNode, syncChannelsEdge, syncObjectsEdge, objectEdges, queueNode,
                parameterNodesInput, unionList, typeUnionList, adUtils);

        return dObjectNode.defineObjectNode(activityNode, nodes, code);
    }

    private String defineSignal(IActivityNode activityNode) throws ParsingException {
        ADUtils adUtils = defineADUtils();

        dSignal = new ADDefineSignal(ad, alphabetNode, countSignal, createdSignal,adUtils);

        return dSignal.defineSignal(activityNode);
    }

    private String defineAccept(IActivityNode activityNode) throws ParsingException {
        ADUtils adUtils = defineADUtils();

        dAccept = new ADDefineAccept(ad, alphabetNode, countAccept, createdAccept, adUtils);

        return dAccept.defineAccept(activityNode);
    }

    private ADUtils defineADUtils() {
        ADUtils adUtils = new ADUtils(ad, adDiagram, countCall, eventChannel, lockChannel, parameterNodesOutputObject, callBehaviourNumber,
                memoryLocal,  memoryLocalChannel, callBehaviourInputs, callBehaviourOutputs, countSignal, countAccept,
                signalChannels, localSignalChannelsSync, allGuards, createdSignal, createdAccept, syncChannelsEdge, syncObjectsEdge, objectEdges,
                signalChannelsLocal, adParser);
        return adUtils;
    }
}
