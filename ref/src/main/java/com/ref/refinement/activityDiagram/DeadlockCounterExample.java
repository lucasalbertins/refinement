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
import com.change_vision.jude.api.inf.presentation.IPresentation;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.ref.parser.activityDiagram.ADParser;
import jdk.nashorn.internal.ir.IfNode;

import javax.swing.*;
import java.awt.geom.Point2D;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DeadlockCounterExample {
    private static HashMap<String, INodePresentation> nodeAdded;
    private static HashMap<String, INodePresentation> objPresent;
    private static List<String> trace;
    private static ADParser parser;
    private static IPackage packageCounterExample;
    private static IActivityDiagram ad;

    public static void createDeadlockCounterExample(List<String> traceList, ADParser parserParam) {
        try {
            Date hoje = new Date();
            SimpleDateFormat df;
            df = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");
            String data = df.format(hoje);

            nodeAdded = new HashMap<>();
            objPresent = new HashMap<>();
            trace = new ArrayList<>();

            for (String objTrace : traceList) {
                String objTracePartition[] = objTrace.split("\\.");
                if (objTracePartition.length > 2) {
                    trace.add(objTracePartition[0] + "." + objTracePartition[1]);
                } else {
                    trace.add(objTrace);
                }
            }

            parser = parserParam;

            IDiagram diagram = AstahAPI.getAstahAPI().getViewManager().getDiagramViewManager().getCurrentDiagram();

            ProjectAccessor prjAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
            IModel project = prjAccessor.getProject();
            BasicModelEditor basicModelEditor = ModelEditorFactory.getBasicModelEditor();

            TransactionManager.beginTransaction();
            createPackage(basicModelEditor, project);
            ActivityDiagramEditor adEditor = prjAccessor.getDiagramEditorFactory().getActivityDiagramEditor();
            ad = adEditor.createActivityDiagram(packageCounterExample, diagram.getName() + "#" + data);

            for (IActivityNode node : ((IActivityDiagram) diagram).getActivity().getActivityNodes()) {
                createNode(node, adEditor);
            }

            TransactionManager.endTransaction();
        } catch (Exception e) {
            TransactionManager.abortTransaction();
        }
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
                if (((INodePresentation) actNode.getPresentations()[0]) == nodePresent) {
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
                        if (((ILinkPresentation)flow.getPresentations()[0]) == flowPresent) {
                            result = flow;
                            break;
                        }
                    }

                    //outPins
                    IOutputPin[] outPins = ((IAction) actNode).getOutputs();
                    for (IOutputPin outPin : outPins) {
                        outflows = outPin.getOutgoings();
                        for (IFlow flow : outflows){
                            if (((ILinkPresentation)flow.getPresentations()[0]) == flowPresent) {
                                result = flow;
                                break;
                            }
                        }
                    }
                } else {
                    IFlow[] outflows = actNode.getOutgoings();
                    for (IFlow flow : outflows){
                        if (((ILinkPresentation)flow.getPresentations()[0]) == flowPresent) {
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

    private static String nameResolver(String name) {
        return name.replace(" ", "").replace("!", "_").replace("@", "_")
                .replace("%", "_").replace("&", "_").replace("*", "_")
                .replace("(", "_").replace(")", "_").replace("+", "_")
                .replace("-", "_").replace("=", "_").replace("?", "_")
                .replace(":", "_").replace("/", "_").replace(";", "_")
                .replace(">", "_").replace("<", "_").replace(",", "_")
                .replace("{", "_").replace("}", "_");
    }

    private static INodePresentation createNode(IActivityNode node, ActivityDiagramEditor adEditor) {
        INodePresentation nodePresent = null;

        if (!nodeAdded.containsKey(node.getId())) {
            if (node instanceof IAction) {
                nodePresent = createAction(node, adEditor);
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
            }
        } else {
            nodePresent = nodeAdded.get(node.getId());
        }

        return nodePresent;
    }

    private static INodePresentation createAction(IActivityNode node, ActivityDiagramEditor adEditor) {
        IFlow outFlows[] = node.getOutgoings();
        IInputPin inPins[] = ((IAction) node).getInputs();
        IOutputPin outPins[] = ((IAction) node).getOutputs();
        INodePresentation actionNode = null;

        try {
            actionNode = adEditor.createAction(nameResolver(node.getName()), ((INodePresentation) node.getPresentations()[0]).getLocation());

            IActivityNode actNode = getIActivityNode(actionNode);
            actNode.setDefinition(node.getDefinition());

            if (parser.alphabetNode.containsKey(nameResolver(node.getName()))) {
                List<String> allflowsNode =  parser.alphabetNode.get(nameResolver(node.getName()));

                for (String objTrace : trace) {
                    if (allflowsNode.contains(objTrace)) {
                        actionNode.setProperty("fill.color", "#FF0000");
                    }
                }
            }

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

                if (parser.syncChannelsEdge.containsKey(outFlows[i].getId()) || parser.syncObjectsEdge.containsKey(outFlows[i].getId())) {
                    String channel = parser.syncChannelsEdge.get(outFlows[i].getId());
                    String channelObj = parser.syncObjectsEdge.get(outFlows[i].getId());

                    if (channel != null && trace.contains(channel)) {
                        flow.setProperty("line.color", "#FF0000");
                    } else if (channelObj != null && trace.contains(channelObj)) {
                        flow.setProperty("line.color", "#FF0000");
                    }
                }

            }

            for (int i = 0; i < outPins.length; i++) {
                IFlow targetOutFlows[] = outPins[i].getOutgoings();
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

                        if (parser.syncChannelsEdge.containsKey(targetOutFlows[x].getId()) || parser.syncObjectsEdge.containsKey(targetOutFlows[x].getId())) {
                            String channel = parser.syncChannelsEdge.get(targetOutFlows[x].getId());
                            String channelObj = parser.syncObjectsEdge.get(targetOutFlows[x].getId());

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

                        if (parser.syncChannelsEdge.containsKey(targetOutFlows[x].getId()) || parser.syncObjectsEdge.containsKey(targetOutFlows[x].getId())) {
                            String channel = parser.syncChannelsEdge.get(targetOutFlows[x].getId());
                            String channelObj = parser.syncObjectsEdge.get(targetOutFlows[x].getId());

                            if (channel != null && trace.contains(channel)) {
                                flow.setProperty("line.color", "#FF0000");
                                pinPresent.setProperty("fill.color", "#FF0000");
                            } else if (channelObj != null && trace.contains(channelObj)) {
                                flow.setProperty("line.color", "#FF0000");
                                pinPresent.setProperty("fill.color", "#FF0000");
                            }
                        }

                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return actionNode;

    }

    private static INodePresentation createInitial(IActivityNode node, ActivityDiagramEditor adEditor) {
        IFlow outFlows[] = node.getOutgoings();
        INodePresentation initialNode = null;

        try {
            initialNode = adEditor.createInitialNode(nameResolver(node.getName()), ((INodePresentation) node.getPresentations()[0]).getLocation());

            if (parser.alphabetNode.containsKey(nameResolver(node.getName()))) {
                List<String> allflowsNode =  parser.alphabetNode.get(nameResolver(node.getName()));

                for (String objTrace : trace) {
                    if (allflowsNode.contains(objTrace)) {
                        initialNode.setProperty("fill.color", "#FF0000");
                    }
                }
            }

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

                if (parser.syncChannelsEdge.containsKey(outFlows[i].getId()) || parser.syncObjectsEdge.containsKey(outFlows[i].getId())) {
                    String channel = parser.syncChannelsEdge.get(outFlows[i].getId());
                    String channelObj = parser.syncObjectsEdge.get(outFlows[i].getId());

                    if (channel != null && trace.contains(channel)) {
                        flow.setProperty("line.color", "#FF0000");
                    } else if (channelObj != null && trace.contains(channelObj)) {
                        flow.setProperty("line.color", "#FF0000");
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return initialNode;
    }

    private static INodePresentation createParameter(IActivityNode node, ActivityDiagramEditor adEditor) {
        IFlow outFlows[] = node.getOutgoings();
        INodePresentation parameterNode = null;

        try {
            parameterNode = adEditor.createActivityParameterNode(nameResolver(node.getName()), ((IActivityParameterNode) node).getBase(), ((INodePresentation) node.getPresentations()[0]).getLocation());

            if (parser.parameterAlphabetNode.containsKey(nameResolver(node.getName()))) {
                List<String> allflowsNode =  parser.parameterAlphabetNode.get(nameResolver(node.getName()));

                for (String objTrace : trace) {
                    if (allflowsNode.contains(objTrace)) {
                        parameterNode.setProperty("fill.color", "#FF0000");
                    }
                }
            }

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

                    if (parser.syncChannelsEdge.containsKey(outFlows[i].getId()) || parser.syncObjectsEdge.containsKey(outFlows[i].getId())) {
                        String channel = parser.syncChannelsEdge.get(outFlows[i].getId());
                        String channelObj = parser.syncObjectsEdge.get(outFlows[i].getId());

                        if (channel != null && trace.contains(channel)) {
                            flow.setProperty("line.color", "#FF0000");
                            targetPresent.setProperty("fill.color", "#FF0000");
                        } else if (channelObj != null && trace.contains(channelObj)) {
                            flow.setProperty("line.color", "#FF0000");
                            targetPresent.setProperty("fill.color", "#FF0000");
                        }
                    }

                } else {
                    INodePresentation targetPresent = createNode(outFlows[i].getTarget(), adEditor);
                    ILinkPresentation flow = adEditor.createFlow(parameterNode, targetPresent);
                    flow.setLabel(outFlows[i].getGuard());

                    IFlow flowPresent = getIFlow(flow);
                    for (String stereotype : outFlows[i].getStereotypes()) {
                        flowPresent.addStereotype(stereotype);
                    }

                    setFlowPoints(flow, outFlows[i]);

                    if (parser.syncChannelsEdge.containsKey(outFlows[i].getId()) || parser.syncObjectsEdge.containsKey(outFlows[i].getId())) {
                        String channel = parser.syncChannelsEdge.get(outFlows[i].getId());
                        String channelObj = parser.syncObjectsEdge.get(outFlows[i].getId());

                        if (channel != null && trace.contains(channel)) {
                            flow.setProperty("line.color", "#FF0000");
                        } else if (channelObj != null && trace.contains(channelObj)) {
                            System.out.println("aqui");
                            flow.setProperty("line.color", "#FF0000");
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return parameterNode;
    }

    private static INodePresentation createDecisionAndMerge(IActivityNode node, ActivityDiagramEditor adEditor) {
        IFlow outFlows[] = node.getOutgoings();
        INodePresentation decisionNode = null;

        try {
            decisionNode = adEditor.createDecisionMergeNode(null, ((INodePresentation) node.getPresentations()[0]).getLocation());
            decisionNode.setLabel(nameResolver(node.getName()));

            if (parser.alphabetNode.containsKey(nameResolver(node.getName()))) {
                List<String> allflowsNode =  parser.alphabetNode.get(nameResolver(node.getName()));

                for (String objTrace : trace) {
                    if (allflowsNode.contains(objTrace)) {
                        decisionNode.setProperty("fill.color", "#FF0000");
                    }
                }
            }

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

                    if (parser.syncChannelsEdge.containsKey(outFlows[i].getId()) || parser.syncObjectsEdge.containsKey(outFlows[i].getId())) {
                        String channel = parser.syncChannelsEdge.get(outFlows[i].getId());
                        String channelObj = parser.syncObjectsEdge.get(outFlows[i].getId());

                        if (channel != null && trace.contains(channel)) {
                            flow.setProperty("line.color", "#FF0000");
                            targetPresent.setProperty("fill.color", "#FF0000");
                        } else if (channelObj != null && trace.contains(channelObj)) {
                            flow.setProperty("line.color", "#FF0000");
                            targetPresent.setProperty("fill.color", "#FF0000");
                        }
                    }

                } else {
                    INodePresentation targetPresent = createNode(outFlows[i].getTarget(), adEditor);
                    ILinkPresentation flow = adEditor.createFlow(decisionNode, targetPresent);
                    flow.setLabel(outFlows[i].getGuard());

                    IFlow flowPresent = getIFlow(flow);
                    for (String stereotype : outFlows[i].getStereotypes()) {
                        flowPresent.addStereotype(stereotype);
                    }

                    setFlowPoints(flow, outFlows[i]);

                    if (parser.syncChannelsEdge.containsKey(outFlows[i].getId()) || parser.syncObjectsEdge.containsKey(outFlows[i].getId())) {
                        String channel = parser.syncChannelsEdge.get(outFlows[i].getId());
                        String channelObj = parser.syncObjectsEdge.get(outFlows[i].getId());

                        if (channel != null && trace.contains(channel)) {
                            flow.setProperty("line.color", "#FF0000");
                        } else if (channelObj != null && trace.contains(channelObj)) {
                            flow.setProperty("line.color", "#FF0000");
                        }
                    }

                }
            }

        } catch (Exception e) {
           e.printStackTrace();
        }

        return decisionNode;
    }

    private static INodePresentation createFork(IActivityNode node, ActivityDiagramEditor adEditor) {
        IFlow outFlows[] = node.getOutgoings();
        INodePresentation forkNode = null;

        try {
            forkNode = adEditor.createForkNode(null, ((INodePresentation) node.getPresentations()[0]).getLocation(),
                    ((INodePresentation) node.getPresentations()[0]).getWidth(), ((INodePresentation) node.getPresentations()[0]).getHeight());
            forkNode.setLabel(nameResolver(node.getName()));

            if (parser.alphabetNode.containsKey(nameResolver(node.getName()))) {
                List<String> allflowsNode =  parser.alphabetNode.get(nameResolver(node.getName()));

                for (String objTrace : trace) {
                    if (allflowsNode.contains(objTrace)) {
                        forkNode.setProperty("fill.color", "#FF0000");
                    }
                }
            }

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

                    if (parser.syncChannelsEdge.containsKey(outFlows[i].getId()) || parser.syncObjectsEdge.containsKey(outFlows[i].getId())) {
                        String channel = parser.syncChannelsEdge.get(outFlows[i].getId());
                        String channelObj = parser.syncObjectsEdge.get(outFlows[i].getId());

                        if (channel != null && trace.contains(channel)) {
                            flow.setProperty("line.color", "#FF0000");
                            targetPresent.setProperty("fill.color", "#FF0000");
                        } else if (channelObj != null && trace.contains(channelObj)) {
                            flow.setProperty("line.color", "#FF0000");
                            targetPresent.setProperty("fill.color", "#FF0000");
                        }
                    }

                } else {
                    INodePresentation targetPresent = createNode(outFlows[i].getTarget(), adEditor);
                    ILinkPresentation flow = adEditor.createFlow(forkNode, targetPresent);
                    flow.setLabel(outFlows[i].getGuard());

                    IFlow flowPresent = getIFlow(flow);
                    for (String stereotype : outFlows[i].getStereotypes()) {
                        flowPresent.addStereotype(stereotype);
                    }

                    setFlowPoints(flow, outFlows[i]);

                    if (parser.syncChannelsEdge.containsKey(outFlows[i].getId()) || parser.syncObjectsEdge.containsKey(outFlows[i].getId())) {
                        String channel = parser.syncChannelsEdge.get(outFlows[i].getId());
                        String channelObj = parser.syncObjectsEdge.get(outFlows[i].getId());

                        if (channel != null && trace.contains(channel)) {
                            flow.setProperty("line.color", "#FF0000");
                        } else if (channelObj != null && trace.contains(channelObj)) {
                            flow.setProperty("line.color", "#FF0000");
                        }
                    }

                }
            }

        } catch (Exception e) {
           e.printStackTrace();
        }

        return forkNode;
    }

    private static INodePresentation createJoin(IActivityNode node, ActivityDiagramEditor adEditor) {
        IFlow outFlows[] = node.getOutgoings();
        INodePresentation joinNode = null;

        try {
            joinNode = adEditor.createJoinNode(null, ((INodePresentation) node.getPresentations()[0]).getLocation(),
                    ((INodePresentation) node.getPresentations()[0]).getWidth(), ((INodePresentation) node.getPresentations()[0]).getHeight());
            joinNode.setLabel(nameResolver(node.getName()));

            if (parser.alphabetNode.containsKey(nameResolver(node.getName()))) {
                List<String> allflowsNode =  parser.alphabetNode.get(nameResolver(node.getName()));

                for (String objTrace : trace) {
                    if (allflowsNode.contains(objTrace)) {
                        joinNode.setProperty("fill.color", "#FF0000");
                    }
                }
            }

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

                    if (parser.syncChannelsEdge.containsKey(outFlows[i].getId()) || parser.syncObjectsEdge.containsKey(outFlows[i].getId())) {
                        String channel = parser.syncChannelsEdge.get(outFlows[i].getId());
                        String channelObj = parser.syncObjectsEdge.get(outFlows[i].getId());

                        if (channel != null && trace.contains(channel)) {
                            flow.setProperty("line.color", "#FF0000");
                            targetPresent.setProperty("fill.color", "#FF0000");
                        } else if (channelObj != null && trace.contains(channelObj)) {
                            flow.setProperty("line.color", "#FF0000");
                            targetPresent.setProperty("fill.color", "#FF0000");
                        }
                    }

                } else {
                    INodePresentation targetPresent = createNode(outFlows[i].getTarget(), adEditor);
                    ILinkPresentation flow = adEditor.createFlow(joinNode, targetPresent);
                    flow.setLabel(outFlows[i].getGuard());

                    IFlow flowPresent = getIFlow(flow);
                    for (String stereotype : outFlows[i].getStereotypes()) {
                        flowPresent.addStereotype(stereotype);
                    }

                    setFlowPoints(flow, outFlows[i]);

                    if (parser.syncChannelsEdge.containsKey(outFlows[i].getId()) || parser.syncObjectsEdge.containsKey(outFlows[i].getId())) {
                        String channel = parser.syncChannelsEdge.get(outFlows[i].getId());
                        String channelObj = parser.syncObjectsEdge.get(outFlows[i].getId());

                        if (channel != null && trace.contains(channel)) {
                            flow.setProperty("line.color", "#FF0000");
                        } else if (channelObj != null && trace.contains(channelObj)) {
                            flow.setProperty("line.color", "#FF0000");
                        }
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return joinNode;
    }

    private static INodePresentation createFinal(IActivityNode node, ActivityDiagramEditor adEditor) {
        IFlow outFlows[] = node.getOutgoings();
        INodePresentation FinalNode = null;

        try {
            FinalNode = adEditor.createFinalNode(nameResolver(node.getName()), ((INodePresentation) node.getPresentations()[0]).getLocation());

            nodeAdded.put(node.getId(), FinalNode);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return FinalNode;
    }

    private static INodePresentation createFlowFinal(IActivityNode node, ActivityDiagramEditor adEditor) {
        IFlow outFlows[] = node.getOutgoings();
        INodePresentation flowFinalNode = null;

        try {
            flowFinalNode = adEditor.createFlowFinalNode(nameResolver(node.getName()), ((INodePresentation) node.getPresentations()[0]).getLocation());

            if (parser.alphabetNode.containsKey(nameResolver(node.getName()))) {
                List<String> allflowsNode =  parser.alphabetNode.get(nameResolver(node.getName()));

                for (String objTrace : trace) {
                    if (allflowsNode.contains(objTrace)) {
                        flowFinalNode.setProperty("fill.color", "#FF0000");
                    }
                }
            }

            nodeAdded.put(node.getId(), flowFinalNode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return flowFinalNode;
    }

    private static INodePresentation createInputPin(IActivityNode node, ActivityDiagramEditor adEditor, INodePresentation actionNode, IInputPin pin) {
        IFlow outFlows[] = node.getOutgoings();
        IInputPin inPins[] = ((IAction) node).getInputs();
        IOutputPin outPins[] = ((IAction) node).getOutputs();
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
        IFlow outFlows[] = node.getOutgoings();
        IInputPin inPins[] = ((IAction) node).getInputs();
        IOutputPin outPins[] = ((IAction) node).getOutputs();
        INodePresentation targetPresent = null;

        try {
            targetPresent = adEditor.createPin(pin.getName(), null, false, actionNode, ((INodePresentation) pin.getPresentations()[0]).getLocation());
        } catch (Exception e) {
            e.printStackTrace();
        }

        objPresent.put(pin.getId(), targetPresent);

        return targetPresent;
    }

}
