package com.ref.refinement.activityDiagram;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.ActivityDiagramEditor;
import com.change_vision.jude.api.inf.editor.BasicModelEditor;
import com.change_vision.jude.api.inf.editor.ModelEditorFactory;
import com.change_vision.jude.api.inf.editor.TransactionManager;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.*;
import com.change_vision.jude.api.inf.presentation.ILinkPresentation;
import com.change_vision.jude.api.inf.presentation.INodePresentation;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.ref.parser.activityDiagram.ADAlphabet;
import com.ref.parser.activityDiagram.ADCompositeAlphabet;
import com.ref.parser.activityDiagram.Pair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DeadlockCounterExample {
    private static HashMap<String, INodePresentation> nodeAdded;
    private static HashMap<String, INodePresentation> objPresent;
    private static List<String> trace;
    private static IPackage packageCounterExample;
    private static IActivityDiagram ad;
    private static ADAlphabet alphabet;
    public static List<IActivity> callBehaviourList = new ArrayList<>();
    
	public static void createDeadlockCounterExample(List<String> traceList, ADAlphabet alphabetAD) {
        try {
        	alphabet = alphabetAD;
        	
            Date hoje = new Date();
            SimpleDateFormat df;
            df = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");
            String data = df.format(hoje);

            nodeAdded = new HashMap<>();
            objPresent = new HashMap<>();
            trace = new ArrayList<>();

            for (String objTrace : traceList) {
                String[] objTracePartition = objTrace.split("\\.");
                if (objTracePartition.length > 2) {
                	String aux = objTracePartition[0] + ".id";
                	for(int i=2;i<objTracePartition.length;i++) {
                		aux+="."+objTracePartition[i];
                	}
                    trace.add(aux);
                } else {
                    trace.add(objTrace);
                }
            }

            
            IDiagram[] diagrams = AstahAPI.getAstahAPI().getViewManager().getDiagramViewManager().getOpenDiagrams();
            IDiagram diagram = AstahAPI.getAstahAPI().getViewManager().getDiagramViewManager().getCurrentDiagram();
            
            ProjectAccessor prjAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
            IModel project = prjAccessor.getProject();
            BasicModelEditor basicModelEditor = ModelEditorFactory.getBasicModelEditor();

            TransactionManager.beginTransaction();
            createPackage(basicModelEditor, project);
            ActivityDiagramEditor adEditor = prjAccessor.getDiagramEditorFactory().getActivityDiagramEditor();
            for(IDiagram ADdiagrams: diagrams) {
            	IActivity activity = ((IActivityDiagram) ADdiagrams).getActivity();
            	if(ADdiagrams == diagram || 
        			(ADdiagrams instanceof IActivityDiagram &&  
        					callBehaviourList.contains(activity))) {
	            	ad = adEditor.createActivityDiagram(packageCounterExample, ADdiagrams.getName()+ "#" + data);
	            	for (IActivityNode node : ((IActivityDiagram) ADdiagrams).getActivity().getActivityNodes()) {
	                    createNode(node, adEditor);
	                }
            	}
            }
            callBehaviourList = new ArrayList<>();
            TransactionManager.endTransaction();
        } catch (Exception e) {
            TransactionManager.abortTransaction();
        }
    }

    private static String nameNodeResolver(String name) {
        return name.replace(" ", "").replace("!", "_").replace("@", "_")
                .replace("%", "_").replace("&", "_").replace("*", "_")
                .replace("(", "_").replace(")", "_").replace("+", "_")
                .replace("-", "_").replace("=", "_").replace("?", "_")
                .replace(":", "_").replace("/", "_").replace(";", "_")
                .replace(">", "_").replace("<", "_").replace(",", "_")
                .replace("{", "_").replace("}", "_").replace("|", "_")
                .replace("\\", "_");
    }

    private static void createPackage(BasicModelEditor basicModelEditor, IModel project) {
        try {
            packageCounterExample = basicModelEditor.createPackage(project, "DeadlockCounterExample");
        } catch (InvalidEditingException e) {
            INamedElement[] objects = project.getOwnedElements();

            for (INamedElement object : objects) {
                if (object.getName().equals("DeadlockCounterExample")) {
                    packageCounterExample = (IPackage) object;
                }
            }
        }
    }

    private static IActivityNode getIActivityNode(INodePresentation nodePresent) {
        IActivityNode result = null;
        try {
            for (IActivityNode actNode : ad.getActivity().getActivityNodes()) {
                if (actNode.getPresentations()[0] == nodePresent) {
                    result = actNode;
                    break;
                }
            }
        } catch (InvalidUsingException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static IFlow getIFlow(ILinkPresentation flowPresent) {
        IFlow result = null;
        try {
            for (IActivityNode actNode : ad.getActivity().getActivityNodes()) {
                if (actNode instanceof IAction) {
                    IFlow[] outflows = actNode.getOutgoings();
                    for (IFlow flow : outflows){
                        if (flow.getPresentations()[0] == flowPresent) {
                            result = flow;
                            break;
                        }
                    }

                    //outPins
                    IOutputPin[] outPins = ((IAction) actNode).getOutputs();
                    for (IOutputPin outPin : outPins) {
                        outflows = outPin.getOutgoings();
                        for (IFlow flow : outflows){
                            if (flow.getPresentations()[0] == flowPresent) {
                                result = flow;
                                break;
                            }
                        }
                    }
                } else {
                    IFlow[] outflows = actNode.getOutgoings();
                    for (IFlow flow : outflows){
                        if (flow.getPresentations()[0] == flowPresent) {
                            result = flow;
                            break;
                        }
                    }
                }
            }
        } catch (InvalidUsingException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static void setFlowPoints(ILinkPresentation flow, IFlow targetOutFlow) {
        try {
            flow.setAllPoints(((ILinkPresentation) targetOutFlow.getPresentations()[0]).getAllPoints());
        } catch (Exception e) { }
    }

    private static INodePresentation createNode(IActivityNode node, ActivityDiagramEditor adEditor) {
        INodePresentation nodePresent = null;

        if (!nodeAdded.containsKey(node.getId())) {
            if (node instanceof IAction) {
                if (((IAction) node).isCallBehaviorAction()) {
                    nodePresent = createAction(node, adEditor, true);
                } else {
                    nodePresent = createAction(node, adEditor, false);
                }
            } else if (node instanceof IControlNode) {
                if (((IControlNode) node).isFinalNode()) {
                    nodePresent = createFinal(node, adEditor);
                } else if (((IControlNode) node).isFlowFinalNode()) {
                    nodePresent = createFlowFinal(node, adEditor);
                } else if (((IControlNode) node).isInitialNode()) {
                    nodePresent = createInitial(node, adEditor);
                } else if (((IControlNode) node).isForkNode()) {
                    nodePresent = createFork(node, adEditor);
                } else if (((IControlNode) node).isJoinNode()) {
                    nodePresent = createJoin(node, adEditor);
                } else if (((IControlNode) node).isDecisionMergeNode()) {
                    nodePresent = createDecisionAndMerge(node, adEditor);
                }
            } else if (node instanceof IActivityParameterNode) {
                nodePresent = createParameter(node, adEditor);
            } else if (node instanceof IObjectNode && !(node instanceof IPin)) {
                nodePresent = createObjectNode(node, adEditor);
            }
        } else {
            nodePresent = nodeAdded.get(node.getId());
        }

        return nodePresent;
    }

    private static INodePresentation createAction(IActivityNode node, ActivityDiagramEditor adEditor, boolean callBehaviour) {
        IFlow[] outFlows = node.getOutgoings();
        IInputPin[] inPins = ((IAction) node).getInputs();
        IOutputPin[] outPins = ((IAction) node).getOutputs();
        INodePresentation actionNode = null;

        try {
            if (callBehaviour) {
                actionNode = adEditor.createCallBehaviorAction(node.getName(), null, ((INodePresentation) node.getPresentations()[0]).getLocation());
            } else {
            	if(((IAction) node).isAcceptEventAction()) {
            		actionNode = adEditor.createAcceptEventAction(node.getName(), ((INodePresentation) node.getPresentations()[0]).getLocation());
            	}else if(((IAction) node).isSendSignalAction()) {
            		actionNode = adEditor.createSendSignalAction(node.getName(), ((INodePresentation) node.getPresentations()[0]).getLocation());
            	}else {
            		actionNode = adEditor.createAction(node.getName(), ((INodePresentation) node.getPresentations()[0]).getLocation());
            	}
            }

            IActivityNode actNode = getIActivityNode(actionNode);
            actNode.setDefinition(node.getDefinition());
            paintNodes(node, actionNode);
            /*if (alphabet.getAlphabetAD().containsKey(nameNodeResolver(node.getName()))) {
                List<String> allflowsNode =  alphabet.getAlphabetAD().get(nameNodeResolver(node.getName()));

                for (String objTrace : trace) {
                    if (allflowsNode.contains(objTrace)) {
                        actionNode.setProperty("fill.color", "#FF0000");
                    }
                }
            }*/

            nodeAdded.put(node.getId(), actionNode);

            for (int i = 0; i < inPins.length; i++) {
                createInputPin(node, adEditor, actionNode, inPins[i]);
            }

            for (int i = 0; i < outPins.length; i++) {
                createOutputPin(node, adEditor, actionNode, outPins[i]);
            }

            for (int i = 0; i < outFlows.length; i++) {
                INodePresentation targetPresent = createNode(outFlows[i].getTarget(), adEditor);
                ILinkPresentation flow = adEditor.createFlow(actionNode, targetPresent);
                flow.setLabel(outFlows[i].getGuard());

                IFlow flowPresent = getIFlow(flow);
                for (String stereotype : outFlows[i].getStereotypes()) {
                    flowPresent.addStereotype(stereotype);
                }

                setFlowPoints(flow, outFlows[i]);
     
                flowSP(outFlows, i, flow);
            }

            for (int i = 0; i < outPins.length; i++) {
                IFlow[] targetOutFlows = outPins[i].getOutgoings();
                for (int x = 0; x < targetOutFlows.length; x++) {
                    if (targetOutFlows[x].getTarget() instanceof IInputPin) {
                        createNode((IActivityNode) targetOutFlows[x].getTarget().getOwner(), adEditor);
                        INodePresentation targetPresent = objPresent.get(targetOutFlows[x].getTarget().getId());
                        INodePresentation pinPresent = objPresent.get(outPins[i].getId());
                        ILinkPresentation flow = adEditor.createFlow(pinPresent, targetPresent);
                        flow.setLabel(targetOutFlows[x].getGuard());

                        IFlow flowPresent = getIFlow(flow);
                        for (String stereotype : targetOutFlows[x].getStereotypes()) {
                            flowPresent.addStereotype(stereotype);
                        }

                        setFlowPoints(flow, targetOutFlows[x]);
                        
                        flowPinTargetSP(outFlows, targetOutFlows, x, targetPresent, pinPresent, flow);
                        
                        /*if (alphabet.getSyncChannelsEdge().containsKey(targetOutFlows[x].getId()) ||alphabet.getSyncObjectsEdge().containsKey(targetOutFlows[x].getId())) {
                            String channel = alphabet.getSyncChannelsEdge().get(targetOutFlows[x].getId());
                            String channelObj = alphabet.getSyncObjectsEdge().get(targetOutFlows[x].getId());

                            if (channel != null && trace.contains(channel)) {
                                flow.setProperty("line.color", "#FF0000");
                                pinPresent.setProperty("fill.color", "#FF0000");
                                targetPresent.setProperty("fill.color", "#FF0000");
                            } else if (channelObj != null && trace.contains(channelObj)) {
                                flow.setProperty("line.color", "#FF0000");
                                pinPresent.setProperty("fill.color", "#FF0000");
                                targetPresent.setProperty("fill.color", "#FF0000");
                            }
                        }*/

                    } else {
                        INodePresentation targetPresent = createNode(targetOutFlows[x].getTarget(), adEditor);
                        INodePresentation pinPresent = objPresent.get(outPins[i].getId());
                        ILinkPresentation flow = adEditor.createFlow(pinPresent, targetPresent);
                        flow.setLabel(targetOutFlows[x].getGuard());

                        IFlow flowPresent = getIFlow(flow);
                        for (String stereotype : targetOutFlows[x].getStereotypes()) {
                            flowPresent.addStereotype(stereotype);
                        }

                        setFlowPoints(flow, targetOutFlows[x]);
                        
                        //flowPinTargetSP(outFlows, targetOutFlows, x, targetPresent, pinPresent, flow);
                     
                        flowPinSP(outFlows, targetOutFlows, x, targetPresent, pinPresent, flow);
                        /*if (alphabet.getSyncChannelsEdge().containsKey(targetOutFlows[x].getId()) || alphabet.getSyncObjectsEdge().containsKey(targetOutFlows[x].getId())) {
                            String channel = alphabet.getSyncChannelsEdge().get(targetOutFlows[x].getId());
                            String channelObj = alphabet.getSyncObjectsEdge().get(targetOutFlows[x].getId());

                            if (channel != null && trace.contains(channel)) {
                                flow.setProperty("line.color", "#FF0000");
                                pinPresent.setProperty("fill.color", "#FF0000");
                            } else if (channelObj != null && trace.contains(channelObj)) {
                                flow.setProperty("line.color", "#FF0000");
                                pinPresent.setProperty("fill.color", "#FF0000");
                            }
                        }*/

                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return actionNode;

    }
    
    private static INodePresentation createInitial(IActivityNode node, ActivityDiagramEditor adEditor) {
        IFlow[] outFlows = node.getOutgoings();
        INodePresentation initialNode = null;

        try {
            initialNode = adEditor.createInitialNode(node.getName(), ((INodePresentation) node.getPresentations()[0]).getLocation());

            paintNodes(node, initialNode);
            
            nodeAdded.put(node.getId(), initialNode);

            for (int i = 0; i < outFlows.length; i++) {
                INodePresentation targetPresent = createNode(outFlows[i].getTarget(), adEditor);
                ILinkPresentation flow = adEditor.createFlow(initialNode, targetPresent);
                flow.setLabel(outFlows[i].getGuard());

                IFlow flowPresent = getIFlow(flow);
                for (String stereotype : outFlows[i].getStereotypes()) {
                    flowPresent.addStereotype(stereotype);
                }

                setFlowPoints(flow, outFlows[i]);
                
                flowSP(outFlows, i, flow);
                
                /*if (alphabet.getSyncChannelsEdge().containsKey(outFlows[i].getId()) || alphabet.getSyncObjectsEdge().containsKey(outFlows[i].getId())) {
                    String channel = alphabet.getSyncChannelsEdge().get(outFlows[i].getId());
                    String channelObj = alphabet.getSyncObjectsEdge().get(outFlows[i].getId());

                    if (channel != null && trace.contains(channel)) {
                        flow.setProperty("line.color", "#FF0000");
                    } else if (channelObj != null && trace.contains(channelObj)) {
                        flow.setProperty("line.color", "#FF0000");
                    }
                }*/

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return initialNode;
    }

    private static INodePresentation createParameter(IActivityNode node, ActivityDiagramEditor adEditor) {
        IFlow[] outFlows = node.getOutgoings();
        INodePresentation parameterNode = null;

        try {
            parameterNode = adEditor.createActivityParameterNode(node.getName(), ((IActivityParameterNode) node).getBase(), ((INodePresentation) node.getPresentations()[0]).getLocation());
            
            paintNodes(node, parameterNode);
            /*if (alphabet.getParameterAlphabetNode().containsKey(nameNodeResolver(node.getName()))) {
                List<String> allflowsNode =  alphabet.getParameterAlphabetNode().get(nameNodeResolver(node.getName()));

                for (String objTrace : trace) {
                    if (allflowsNode.contains(objTrace)) {
                        parameterNode.setProperty("fill.color", "#FF0000");
                    }
                }
            }*/

            nodeAdded.put(node.getId(), parameterNode);

            for (int i = 0; i < outFlows.length; i++) {
                if (outFlows[i].getTarget() instanceof IInputPin) {
                    createNode((IActivityNode) outFlows[i].getTarget().getOwner(), adEditor);
                    INodePresentation targetPresent = objPresent.get(outFlows[i].getTarget().getId());
                    ILinkPresentation flow = adEditor.createFlow(parameterNode, targetPresent);
                    flow.setLabel(outFlows[i].getGuard());

                    IFlow flowPresent = getIFlow(flow);
                    for (String stereotype : outFlows[i].getStereotypes()) {
                        flowPresent.addStereotype(stereotype);
                    }

                    setFlowPoints(flow, outFlows[i]);

                    flowTargetSP(outFlows, i, targetPresent, flow);

                } else {
                    INodePresentation targetPresent = createNode(outFlows[i].getTarget(), adEditor);
                    ILinkPresentation flow = adEditor.createFlow(parameterNode, targetPresent);
                    flow.setLabel(outFlows[i].getGuard());

                    IFlow flowPresent = getIFlow(flow);
                    for (String stereotype : outFlows[i].getStereotypes()) {
                        flowPresent.addStereotype(stereotype);
                    }

                    setFlowPoints(flow, outFlows[i]);

                    flowSP(outFlows, i, flow);
                    
                    /*if (alphabet.getSyncChannelsEdge().containsKey(outFlows[i].getId()) || alphabet.getSyncObjectsEdge().containsKey(outFlows[i].getId())) {
                        String channel = alphabet.getSyncChannelsEdge().get(outFlows[i].getId());
                        String channelObj = alphabet.getSyncObjectsEdge().get(outFlows[i].getId());

                        if (channel != null && trace.contains(channel)) {
                            flow.setProperty("line.color", "#FF0000");
                        } else if (channelObj != null && trace.contains(channelObj)) {
                            System.out.println("aqui");
                            flow.setProperty("line.color", "#FF0000");
                        }
                    }*/
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return parameterNode;
    }

    private static INodePresentation createDecisionAndMerge(IActivityNode node, ActivityDiagramEditor adEditor) {
        IFlow[] outFlows = node.getOutgoings();
        INodePresentation decisionNode = null;

        try {
            decisionNode = adEditor.createDecisionMergeNode(null, ((INodePresentation) node.getPresentations()[0]).getLocation());
            decisionNode.setLabel(node.getName());
            
            paintNodes(node, decisionNode);
            /*if (alphabet.getAlphabetAD().containsKey(nameNodeResolver(node.getName()))) {
                List<String> allflowsNode =  alphabet.getAlphabetAD().get(nameNodeResolver(node.getName()));

                for (String objTrace : trace) {
                    if (allflowsNode.contains(objTrace)) {
                        decisionNode.setProperty("fill.color", "#FF0000");
                    }
                }
            }*/

            nodeAdded.put(node.getId(), decisionNode);

            for (int i = 0; i < outFlows.length; i++) {
                if (outFlows[i].getTarget() instanceof IInputPin) {
                    createNode((IActivityNode) outFlows[i].getTarget().getOwner(), adEditor);
                    INodePresentation targetPresent = objPresent.get(outFlows[i].getTarget().getId());
                    ILinkPresentation flow = adEditor.createFlow(decisionNode, targetPresent);
                    flow.setLabel(outFlows[i].getGuard());

                    IFlow flowPresent = getIFlow(flow);
                    for (String stereotype : outFlows[i].getStereotypes()) {
                        flowPresent.addStereotype(stereotype);
                    }

                    setFlowPoints(flow, outFlows[i]);

                    flowTargetSP(outFlows, i, targetPresent, flow);
                    
                    /*if (alphabet.getSyncChannelsEdge().containsKey(outFlows[i].getId()) || alphabet.getSyncObjectsEdge().containsKey(outFlows[i].getId())) {
                        String channel = alphabet.getSyncChannelsEdge().get(outFlows[i].getId());
                        String channelObj = alphabet.getSyncObjectsEdge().get(outFlows[i].getId());

                        if (channel != null && trace.contains(channel)) {
                            flow.setProperty("line.color", "#FF0000");
                            targetPresent.setProperty("fill.color", "#FF0000");
                        } else if (channelObj != null && trace.contains(channelObj)) {
                            flow.setProperty("line.color", "#FF0000");
                            targetPresent.setProperty("fill.color", "#FF0000");
                        }
                    }*/

                } else {
                    INodePresentation targetPresent = createNode(outFlows[i].getTarget(), adEditor);
                    ILinkPresentation flow = adEditor.createFlow(decisionNode, targetPresent);
                    flow.setLabel(outFlows[i].getGuard());

                    IFlow flowPresent = getIFlow(flow);
                    for (String stereotype : outFlows[i].getStereotypes()) {
                        flowPresent.addStereotype(stereotype);
                    }

                    setFlowPoints(flow, outFlows[i]);
                    
                    flowTargetSP(outFlows, i, targetPresent, flow);
                    
                    /*if (alphabet.getSyncChannelsEdge().containsKey(outFlows[i].getId()) || alphabet.getSyncObjectsEdge().containsKey(outFlows[i].getId())) {
                        String channel = alphabet.getSyncChannelsEdge().get(outFlows[i].getId());
                        String channelObj = alphabet.getSyncObjectsEdge().get(outFlows[i].getId());

                        if (channel != null && trace.contains(channel)) {
                            flow.setProperty("line.color", "#FF0000");
                        } else if (channelObj != null && trace.contains(channelObj)) {
                            flow.setProperty("line.color", "#FF0000");
                        }
                    }*/

                }
            }

        } catch (Exception e) {
           e.printStackTrace();
        }

        return decisionNode;
    }

    private static INodePresentation createFork(IActivityNode node, ActivityDiagramEditor adEditor) {
        IFlow[] outFlows = node.getOutgoings();
        INodePresentation forkNode = null;

        try {
            forkNode = adEditor.createForkNode(null, ((INodePresentation) node.getPresentations()[0]).getLocation(),
                    ((INodePresentation) node.getPresentations()[0]).getWidth(), ((INodePresentation) node.getPresentations()[0]).getHeight());
            forkNode.setLabel(node.getName());

            paintNodes(node, forkNode);
            /*if (alphabet.getAlphabetAD().containsKey(nameNodeResolver(node.getName()))) {
                List<String> allflowsNode =  alphabet.getAlphabetAD().get(nameNodeResolver(node.getName()));

                for (String objTrace : trace) {
                    if (allflowsNode.contains(objTrace)) {
                        forkNode.setProperty("fill.color", "#FF0000");
                    }
                }
            }*/

            nodeAdded.put(node.getId(), forkNode);

            for (int i = 0; i < outFlows.length; i++) {
                if (outFlows[i].getTarget() instanceof IInputPin) {
                    createNode((IActivityNode) outFlows[i].getTarget().getOwner(), adEditor);
                    INodePresentation targetPresent = objPresent.get(outFlows[i].getTarget().getId());
                    ILinkPresentation flow = adEditor.createFlow(forkNode, targetPresent);
                    flow.setLabel(outFlows[i].getGuard());

                    IFlow flowPresent = getIFlow(flow);
                    for (String stereotype : outFlows[i].getStereotypes()) {
                        flowPresent.addStereotype(stereotype);
                    }

                    setFlowPoints(flow, outFlows[i]);
                    flowTargetSP(outFlows, i, targetPresent, flow);
                    
                    /*if (alphabet.getSyncChannelsEdge().containsKey(outFlows[i].getId()) || alphabet.getSyncObjectsEdge().containsKey(outFlows[i].getId())) {
                        String channel = alphabet.getSyncChannelsEdge().get(outFlows[i].getId());
                        String channelObj = alphabet.getSyncObjectsEdge().get(outFlows[i].getId());

                        if (channel != null && trace.contains(channel)) {
                            flow.setProperty("line.color", "#FF0000");
                            targetPresent.setProperty("fill.color", "#FF0000");
                        } else if (channelObj != null && trace.contains(channelObj)) {
                            flow.setProperty("line.color", "#FF0000");
                            targetPresent.setProperty("fill.color", "#FF0000");
                        }
                    }*/

                } else {
                    INodePresentation targetPresent = createNode(outFlows[i].getTarget(), adEditor);
                    ILinkPresentation flow = adEditor.createFlow(forkNode, targetPresent);
                    flow.setLabel(outFlows[i].getGuard());

                    IFlow flowPresent = getIFlow(flow);
                    for (String stereotype : outFlows[i].getStereotypes()) {
                        flowPresent.addStereotype(stereotype);
                    }

                    setFlowPoints(flow, outFlows[i]);
                    flowTargetSP(outFlows, i, targetPresent, flow);
                    
                    /*if (alphabet.getSyncChannelsEdge().containsKey(outFlows[i].getId()) || alphabet.getSyncObjectsEdge().containsKey(outFlows[i].getId())) {
                        String channel = alphabet.getSyncChannelsEdge().get(outFlows[i].getId());
                        String channelObj = alphabet.getSyncObjectsEdge().get(outFlows[i].getId());

                        if (channel != null && trace.contains(channel)) {
                            flow.setProperty("line.color", "#FF0000");
                        } else if (channelObj != null && trace.contains(channelObj)) {
                            flow.setProperty("line.color", "#FF0000");
                        }
                    }*/

                }
            }

        } catch (Exception e) {
           e.printStackTrace();
        }

        return forkNode;
    }

    private static INodePresentation createJoin(IActivityNode node, ActivityDiagramEditor adEditor) {
        IFlow[] outFlows = node.getOutgoings();
        INodePresentation joinNode = null;

        try {
            joinNode = adEditor.createJoinNode(null, ((INodePresentation) node.getPresentations()[0]).getLocation(),
                    ((INodePresentation) node.getPresentations()[0]).getWidth(), ((INodePresentation) node.getPresentations()[0]).getHeight());
            joinNode.setLabel(node.getName());

            paintNodes(node, joinNode);
            /*if (alphabet.getAlphabetAD().containsKey(nameNodeResolver(node.getName()))) {
                List<String> allflowsNode =  alphabet.getAlphabetAD().get(nameNodeResolver(node.getName()));

                for (String objTrace : trace) {
                    if (allflowsNode.contains(objTrace)) {
                        joinNode.setProperty("fill.color", "#FF0000");
                    }
                }
            }*/

            nodeAdded.put(node.getId(), joinNode);

            for (int i = 0; i < outFlows.length; i++) {
                if (outFlows[i].getTarget() instanceof IInputPin) {
                    createNode((IActivityNode) outFlows[i].getTarget().getOwner(), adEditor);
                    INodePresentation targetPresent = objPresent.get(outFlows[i].getTarget().getId());
                    ILinkPresentation flow = adEditor.createFlow(joinNode, targetPresent);
                    flow.setLabel(outFlows[i].getGuard());

                    IFlow flowPresent = getIFlow(flow);
                    for (String stereotype : outFlows[i].getStereotypes()) {
                        flowPresent.addStereotype(stereotype);
                    }

                    setFlowPoints(flow, outFlows[i]);

                    flowTargetSP(outFlows, i, targetPresent, flow);

                } else {
                    INodePresentation targetPresent = createNode(outFlows[i].getTarget(), adEditor);
                    ILinkPresentation flow = adEditor.createFlow(joinNode, targetPresent);
                    flow.setLabel(outFlows[i].getGuard());

                    IFlow flowPresent = getIFlow(flow);
                    for (String stereotype : outFlows[i].getStereotypes()) {
                        flowPresent.addStereotype(stereotype);
                    }

                    setFlowPoints(flow, outFlows[i]);

                    flowSP(outFlows, i, flow);
                    
                    /*if (alphabet.getSyncChannelsEdge().containsKey(outFlows[i].getId()) || alphabet.getSyncObjectsEdge().containsKey(outFlows[i].getId())) {
                        String channel = alphabet.getSyncChannelsEdge().get(outFlows[i].getId());
                        String channelObj = alphabet.getSyncObjectsEdge().get(outFlows[i].getId());

                        if (channel != null && trace.contains(channel)) {
                            flow.setProperty("line.color", "#FF0000");
                        } else if (channelObj != null && trace.contains(channelObj)) {
                            flow.setProperty("line.color", "#FF0000");
                        }
                    }*/

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return joinNode;
    }

    private static INodePresentation createFinal(IActivityNode node, ActivityDiagramEditor adEditor) {
        //IFlow[] outFlows = node.getOutgoings();
        INodePresentation FinalNode = null;

        try {
            FinalNode = adEditor.createFinalNode(node.getName(), ((INodePresentation) node.getPresentations()[0]).getLocation());

            nodeAdded.put(node.getId(), FinalNode);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return FinalNode;
    }

    private static INodePresentation createFlowFinal(IActivityNode node, ActivityDiagramEditor adEditor) {
        //IFlow[] outFlows = node.getOutgoings();
        INodePresentation flowFinalNode = null;

        try {
            flowFinalNode = adEditor.createFlowFinalNode(node.getName(), ((INodePresentation) node.getPresentations()[0]).getLocation());
            
            paintNodes(node, flowFinalNode);
            /*if (alphabet.getAlphabetAD().containsKey(nameNodeResolver(node.getName()))) {
                List<String> allflowsNode =  alphabet.getAlphabetAD().get(nameNodeResolver(node.getName()));

                for (String objTrace : trace) {
                    if (allflowsNode.contains(objTrace)) {
                        flowFinalNode.setProperty("fill.color", "#FF0000");
                    }
                }
            }*/

            nodeAdded.put(node.getId(), flowFinalNode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return flowFinalNode;
    }

    private static INodePresentation createInputPin(IActivityNode node, ActivityDiagramEditor adEditor, INodePresentation actionNode, IInputPin pin) {
        //IFlow[] outFlows = node.getOutgoings();
        //IInputPin[] inPins = ((IAction) node).getInputs();
        //IOutputPin[] outPins = ((IAction) node).getOutputs();
        INodePresentation targetPresent = null;

        try{
            targetPresent = adEditor.createPin(pin.getName(), null, true, actionNode, ((INodePresentation) pin.getPresentations()[0]).getLocation());
        } catch (Exception e) {
            e.printStackTrace();
        }

        objPresent.put(pin.getId(), targetPresent);

        return targetPresent;
    }

    private static INodePresentation createOutputPin(IActivityNode node, ActivityDiagramEditor adEditor, INodePresentation actionNode, IOutputPin pin) {
        //IFlow[] outFlows = node.getOutgoings();
        //IInputPin[] inPins = ((IAction) node).getInputs();
        //IOutputPin[] outPins = ((IAction) node).getOutputs();
        INodePresentation targetPresent = null;

        try {
            targetPresent = adEditor.createPin(pin.getName(), null, false, actionNode, ((INodePresentation) pin.getPresentations()[0]).getLocation());
        } catch (Exception e) {
            e.printStackTrace();
        }

        objPresent.put(pin.getId(), targetPresent);

        return targetPresent;
    }

    private static INodePresentation createObjectNode(IActivityNode node, ActivityDiagramEditor adEditor) {
        IFlow[] outFlows = node.getOutgoings();
        INodePresentation objectNode = null;

        try {
            objectNode = adEditor.createObjectNode(node.getName(), null, ((INodePresentation) node.getPresentations()[0]).getLocation());
            
            paintNodes(node, objectNode);
            /*if (alphabet.getAlphabetAD().containsKey(nameNodeResolver(node.getName()))) {
                List<String> allflowsNode =  alphabet.getAlphabetAD().get(nameNodeResolver(node.getName()));

                for (String objTrace : trace) {
                    if (allflowsNode.contains(objTrace)) {
                        objectNode.setProperty("fill.color", "#FF0000");
                    }
                }
            }*/

            nodeAdded.put(node.getId(), objectNode);

            for (int i = 0; i < outFlows.length; i++) {
                if (outFlows[i].getTarget() instanceof IInputPin) {
                    createNode((IActivityNode) outFlows[i].getTarget().getOwner(), adEditor);
                    INodePresentation targetPresent = objPresent.get(outFlows[i].getTarget().getId());
                    ILinkPresentation flow = adEditor.createFlow(objectNode, targetPresent);
                    flow.setLabel(outFlows[i].getGuard());

                    IFlow flowPresent = getIFlow(flow);
                    for (String stereotype : outFlows[i].getStereotypes()) {
                        flowPresent.addStereotype(stereotype);
                    }

                    setFlowPoints(flow, outFlows[i]);
                    
                    flowTargetSP(outFlows, i, targetPresent, flow);
                    
                    /*if (alphabet.getSyncChannelsEdge().containsKey(outFlows[i].getId()) || alphabet.getSyncObjectsEdge().containsKey(outFlows[i].getId())) {
                        String channel = alphabet.getSyncChannelsEdge().get(outFlows[i].getId());
                        String channelObj = alphabet.getSyncObjectsEdge().get(outFlows[i].getId());

                        if (channel != null && trace.contains(channel)) {
                            flow.setProperty("line.color", "#FF0000");
                            targetPresent.setProperty("fill.color", "#FF0000");
                        } else if (channelObj != null && trace.contains(channelObj)) {
                            flow.setProperty("line.color", "#FF0000");
                            targetPresent.setProperty("fill.color", "#FF0000");
                        }
                    }*/

                } else {
                    INodePresentation targetPresent = createNode(outFlows[i].getTarget(), adEditor);
                    ILinkPresentation flow = adEditor.createFlow(objectNode, targetPresent);
                    flow.setLabel(outFlows[i].getGuard());

                    IFlow flowPresent = getIFlow(flow);
                    for (String stereotype : outFlows[i].getStereotypes()) {
                        flowPresent.addStereotype(stereotype);
                    }

                    setFlowPoints(flow, outFlows[i]);
                    
                    flowSP(outFlows,i,flow);
                    
                    /*if (alphabet.getSyncChannelsEdge().containsKey(outFlows[i].getId()) || alphabet.getSyncObjectsEdge().containsKey(outFlows[i].getId())) {
                        String channel = alphabet.getSyncChannelsEdge().get(outFlows[i].getId());
                        String channelObj = alphabet.getSyncObjectsEdge().get(outFlows[i].getId());

                        if (channel != null && trace.contains(channel)) {
                            flow.setProperty("line.color", "#FF0000");
                        } else if (channelObj != null && trace.contains(channelObj)) {
                            flow.setProperty("line.color", "#FF0000");
                        }
                    }*/

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return objectNode;
    }
  
    private static void paintNodes(IActivityNode node, INodePresentation actionNode) throws InvalidEditingException {
    	Pair<IActivity, String> key = new Pair<IActivity, String>(ad.getActivity(), nameNodeResolver(node.getName()));
    	if(alphabet instanceof ADCompositeAlphabet) {
			HashMap<Pair<IActivity, String>, ArrayList<String>> aux = new HashMap<>();
			aux =((ADCompositeAlphabet) alphabet).getAllAlphabetNodes();		
			if(aux.containsKey(key)) {
				List<String> allflowsNode =  aux.get(key);

		        for (String objTrace : trace) {
		            if (allflowsNode.contains(objTrace)) {
		                actionNode.setProperty("fill.color", "#FF0000");
		            }
		        }	
			}
		}else {
			HashMap<Pair<IActivity, String>, ArrayList<String>> aux = new HashMap<>();
			aux = alphabet.getAlphabetAD();			
			List<String> allflowsNode =  aux.get(key);
		    for (String objTrace : trace) {
		        if (allflowsNode!= null && allflowsNode.contains(objTrace)) {
		            actionNode.setProperty("fill.color", "#FF0000");
		        }
		    }
		}
	}

    private static void flowSP(IFlow[] outFlows, int i, ILinkPresentation flow) throws InvalidEditingException {
    	String outFlowID = outFlows[i].getId();
    	Pair<IActivity, String> key = new Pair<IActivity, String>(ad.getActivity(),outFlowID);
    	if(alphabet instanceof ADCompositeAlphabet){
			if(((ADCompositeAlphabet) alphabet).getAllsyncChannelsEdge().containsKey(key) ||
					((ADCompositeAlphabet) alphabet).getAllsyncObjectsEdge().containsKey(key)) {
				String channel = ((ADCompositeAlphabet) alphabet).getAllsyncChannelsEdge().get(key);
		        String channelObj = ((ADCompositeAlphabet) alphabet).getAllsyncObjectsEdge().get(key);

		        if (channel != null && trace.contains(channel)) {
		            flow.setProperty("line.color", "#FF0000");
		        } else if (channelObj != null && trace.contains(channelObj)) {
		            flow.setProperty("line.color", "#FF0000");
		        }
			}
		}
		else {
			if (alphabet.getSyncChannelsEdge().containsKey(key) ||
				alphabet.getSyncObjectsEdge().containsKey(key)) {
		        String channel = alphabet.getSyncChannelsEdge().get(key);
		        String channelObj = alphabet.getSyncObjectsEdge().get(key);

		        if (channel != null && trace.contains(channel)) {
		            flow.setProperty("line.color", "#FF0000");
		        } else if (channelObj != null && trace.contains(channelObj)) {
		            flow.setProperty("line.color", "#FF0000");
		        }
		    }
		}
	}

    private static void flowTargetSP(IFlow[] outFlows, int i, INodePresentation targetPresent, ILinkPresentation flow)
			throws InvalidEditingException {
    	String outFlowID = outFlows[i].getId();
    	Pair<IActivity, String> key = new Pair<IActivity, String>(ad.getActivity(),outFlowID);
		if(alphabet instanceof ADCompositeAlphabet){
			if(((ADCompositeAlphabet) alphabet).getAllsyncChannelsEdge().containsKey(key) ||
					((ADCompositeAlphabet) alphabet).getAllsyncObjectsEdge().containsKey(key)) {
				String channel = ((ADCompositeAlphabet) alphabet).getAllsyncChannelsEdge().get(key);
		        String channelObj = ((ADCompositeAlphabet) alphabet).getAllsyncObjectsEdge().get(key);

		        if (channel != null && trace.contains(channel)) {
		            flow.setProperty("line.color", "#FF0000");
		            targetPresent.setProperty("fill.color", "#FF0000");
		        } else if (channelObj != null && trace.contains(channelObj)) {
		            flow.setProperty("line.color", "#FF0000");
		           
		            targetPresent.setProperty("fill.color", "#FF0000");
		        }
			}
		}
		else {
			if (alphabet.getSyncChannelsEdge().containsKey(key) || 
				alphabet.getSyncObjectsEdge().containsKey(key)) {
		        String channel = alphabet.getSyncChannelsEdge().get(key);
		        String channelObj = alphabet.getSyncObjectsEdge().get(key);

		        if (channel != null && trace.contains(channel)) {
		            flow.setProperty("line.color", "#FF0000");
		            targetPresent.setProperty("fill.color", "#FF0000");
		            
		        } else if (channelObj != null && trace.contains(channelObj)) {
		            flow.setProperty("line.color", "#FF0000");
		            targetPresent.setProperty("fill.color", "#FF0000");
		        }
		    }
		}
	}
    
    private static void flowPinSP(IFlow[] outFlows, IFlow[] targetOutFlows, int x, INodePresentation targetPresent,
			INodePresentation pinPresent, ILinkPresentation flow) throws InvalidEditingException {
		if(alphabet instanceof ADCompositeAlphabet){
			String outFlowID = outFlows[x].getId();
			Pair<IActivity, String> key = new Pair<IActivity, String>(ad.getActivity(),outFlowID);
			if(((ADCompositeAlphabet) alphabet).getAllsyncChannelsEdge().containsKey(key) ||
					((ADCompositeAlphabet) alphabet).getAllsyncObjectsEdge().containsKey(key)) {
				String channel = ((ADCompositeAlphabet) alphabet).getAllsyncChannelsEdge().get(key);
		        String channelObj = ((ADCompositeAlphabet) alphabet).getAllsyncObjectsEdge().get(key);

		        if (channel != null && trace.contains(channel)) {
		            flow.setProperty("line.color", "#FF0000");
		            pinPresent.setProperty("fill.color", "#FF0000");
		            targetPresent.setProperty("fill.color", "#FF0000");
		        } else if (channelObj != null && trace.contains(channelObj)) {
		            flow.setProperty("line.color", "#FF0000");
		            pinPresent.setProperty("fill.color", "#FF0000");              		           
		        }
			}
		}
		else {
			String targetOutFlowID = targetOutFlows[x].getId();
			Pair<IActivity, String> key = new Pair<IActivity, String>(ad.getActivity(),targetOutFlowID);
			if (alphabet.getSyncChannelsEdge().containsKey(key) ||
				alphabet.getSyncObjectsEdge().containsKey(key)) {
		        String channel = alphabet.getSyncChannelsEdge().get(key);
		        String channelObj = alphabet.getSyncObjectsEdge().get(key);

		        if (channel != null && trace.contains(channel)) {
		            flow.setProperty("line.color", "#FF0000");
		            pinPresent.setProperty("fill.color", "#FF0000");
		            targetPresent.setProperty("fill.color", "#FF0000");
		        } else if (channelObj != null && trace.contains(channelObj)) {
		            flow.setProperty("line.color", "#FF0000");
		            pinPresent.setProperty("fill.color", "#FF0000");                		          
		        }
		    }
		}
	}

    private static void flowPinTargetSP(IFlow[] outFlows, IFlow[] targetOutFlows, int x,
			INodePresentation targetPresent, INodePresentation pinPresent, ILinkPresentation flow)
			throws InvalidEditingException {
		if(alphabet instanceof ADCompositeAlphabet){
			String outFlowID = outFlows[x].getId();
			Pair<IActivity, String> key = new Pair<IActivity, String>(ad.getActivity(),outFlowID);
			if(((ADCompositeAlphabet) alphabet).getAllsyncChannelsEdge().containsKey(key) ||
					((ADCompositeAlphabet) alphabet).getAllsyncObjectsEdge().containsKey(key)) {
				String channel = ((ADCompositeAlphabet) alphabet).getAllsyncChannelsEdge().get(key);
		        String channelObj = ((ADCompositeAlphabet) alphabet).getAllsyncObjectsEdge().get(key);

		        if (channel != null && trace.contains(channel)) {
		            flow.setProperty("line.color", "#FF0000");
		            pinPresent.setProperty("fill.color", "#FF0000");
		            targetPresent.setProperty("fill.color", "#FF0000");
		        } else if (channelObj != null && trace.contains(channelObj)) {
		            flow.setProperty("line.color", "#FF0000");
		            pinPresent.setProperty("fill.color", "#FF0000");
		            targetPresent.setProperty("fill.color", "#FF0000");
		        }
			}
		}
		else {
			String targetOutFlowID = targetOutFlows[x].getId();
			Pair<IActivity, String> key = new Pair<IActivity, String>(ad.getActivity(),targetOutFlowID);
			if (alphabet.getSyncChannelsEdge().containsKey(key) || 
				alphabet.getSyncObjectsEdge().containsKey(key)) {
		        String channel = alphabet.getSyncChannelsEdge().get(key);
		        String channelObj = alphabet.getSyncObjectsEdge().get(key);

		        if (channel != null && trace.contains(channel)) {
		            flow.setProperty("line.color", "#FF0000");
		            pinPresent.setProperty("fill.color", "#FF0000");
		            targetPresent.setProperty("fill.color", "#FF0000");
		        } else if (channelObj != null && trace.contains(channelObj)) {
		            flow.setProperty("line.color", "#FF0000");
		            pinPresent.setProperty("fill.color", "#FF0000");
		            targetPresent.setProperty("fill.color", "#FF0000");
		        }
		    }
		}
	}

}