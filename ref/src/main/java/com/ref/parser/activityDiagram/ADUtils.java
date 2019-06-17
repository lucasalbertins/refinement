package com.ref.parser.activityDiagram;

import com.change_vision.jude.api.inf.model.*;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ADUtils {

    private IActivity ad;
    private IActivityDiagram adDiagram;

    public HashMap<String, Integer> countCall;
    private List<String> eventChannel;
    private List<String> lockChannel;
    private HashMap<String, String> parameterNodesOutputObject;
    private List<Pair<String, Integer>> callBehaviourNumber;
    private List<Pair<String, String>> memoryLocal;
    private List<Pair<String, String>> memoryLocalChannel;
    private HashMap<String, List<String>> callBehaviourInputs;
    private HashMap<String, List<String>> callBehaviourOutputs;
    private List<Pair<String, Integer>> countSignal;
    private List<Pair<String, Integer>> countAccept;
    private List<String> signalChannels;
    private List<String> signalChannelsLocal;
    private List<String> localSignalChannelsSync;
    private List<String> createdSignal;
    private List<String> createdAccept;
    private HashMap<String,Integer> allGuards;
    public HashMap<String, String> syncChannelsEdge;
    public HashMap<String, String> syncObjectsEdge;
    private ADParser adParser;

    public ADUtils(IActivity ad, IActivityDiagram adDiagram, HashMap<String, Integer> countCall, List<String> eventChannel,
                   List<String> lockChannel, HashMap<String, String> parameterNodesOutputObject, List<Pair<String, Integer>> callBehaviourNumber,
                   List<Pair<String, String>> memoryLocal, List<Pair<String, String>> memoryLocalChannel, HashMap<String, List<String>> callBehaviourInputs,
                   HashMap<String, List<String>> callBehaviourOutputs, List<Pair<String, Integer>> countSignal, List<Pair<String, Integer>> countAccept,
                   List<String> signalChannels, List<String> localSignalChannelsSync, HashMap<String, Integer> allGuards,
                   List<String> createdSignal, List<String> createdAccept, HashMap<String, String> syncChannelsEdge,
                   HashMap<String, String> syncObjectsEdge, List<String> signalChannelsLocal, ADParser adParser) {

        this.ad = ad;
        this.adDiagram = adDiagram;
        this.countCall = countCall;
        this.eventChannel = eventChannel;
        this.lockChannel = lockChannel;
        this.parameterNodesOutputObject = parameterNodesOutputObject;
        this.callBehaviourNumber = callBehaviourNumber;
        this.memoryLocal = memoryLocal;
        this.memoryLocalChannel = memoryLocalChannel;
        this.callBehaviourInputs = callBehaviourInputs;
        this.callBehaviourOutputs = callBehaviourOutputs;
        this.countSignal = countSignal;
        this.countAccept = countAccept;
        this.signalChannels = signalChannels;
        this.localSignalChannelsSync = localSignalChannelsSync;
        this.allGuards = allGuards;
        this.createdSignal = createdSignal;
        this.createdAccept = createdAccept;
        this.syncChannelsEdge = syncChannelsEdge;
        this.syncObjectsEdge = syncObjectsEdge;
        this.signalChannelsLocal = signalChannelsLocal;
        this.adParser = adParser;
    }

    public String createCE() {
        return "ce_" + nameDiagramResolver(ad.getName()) + "." + adParser.countCe_ad++;
    }

    public String createOE(String nameObject) {
        return "oe_" + nameObject + "_" + nameDiagramResolver(ad.getName()) + "." + adParser.countOe_ad++;
    }

    public int startActivity(ArrayList<String> alphabetNode, StringBuilder action, String nameAD, List<String> inputPins) {
        int count = 0;
        count = addCountCall(nameDiagramResolver(nameAD));
        String startActivity = "startActivity_" + nameDiagramResolver(nameAD) + "." + count;
        alphabetNode.add(startActivity);
        callBehaviourNumber.add(new Pair<>(nameDiagramResolver(nameAD), count));

        List<String> outputPinsUsed = callBehaviourInputs.get(nameDiagramResolver(nameAD));
        if (outputPinsUsed == null) {
            outputPinsUsed = inputPins;
            callBehaviourInputs.put(nameAD, inputPins);
        }

        for (String pin : outputPinsUsed) {
            startActivity += "!" + pin;
        }

        action.append(startActivity + " -> ");

        return count;
    }

    public void endActivity(ArrayList<String> alphabetNode, StringBuilder action, String nameAD, List<String> outputPins, int count) {
        String endActivity = "endActivity_" + nameDiagramResolver(nameAD) + "." + count;
        alphabetNode.add(endActivity);

        List<String> outputPinsUsed = callBehaviourOutputs.get(nameDiagramResolver(nameAD));
        if (outputPinsUsed == null) {
            outputPinsUsed = outputPins;
            callBehaviourOutputs.put(nameAD, outputPins);
        }

        for (String pin : outputPinsUsed) {
            endActivity += "?" + pin;
        }

        action.append(endActivity + " -> ");
    }

    public void get(ArrayList<String> alphabetNode, StringBuilder action, String nameObject) {
        String get = "get_" + nameObject + "_" + nameDiagramResolver(ad.getName()) + "." + adParser.countGet_ad++;
        alphabetNode.add(get);
        action.append(get + "?" + nameObject + " -> ");
    }

    public void set(ArrayList<String> alphabetNode, StringBuilder action, String nameMemory, String nameObject) {
        String set = "set_" + nameMemory + "_" + nameDiagramResolver(ad.getName()) + "." + adParser.countSet_ad++;
        alphabetNode.add(set);
        action.append(set +"!" + nameObject + " -> ");
        parameterNodesOutputObject.put(nameMemory, nameObject);
    }

    public void setLocal(ArrayList<String> alphabetNode, StringBuilder action, String nameObject, String nameNode, String data) {
        String set = "set_" + nameObject + "_" + nameNode + "_" + nameDiagramResolver(ad.getName()) + "." + adParser.countSet_ad++;
        action.append(set + "!" + data + " -> ");
        Pair<String, String> memoryLocalPair = new Pair<String, String>(nameNode, nameObject);
        if (!memoryLocal.contains(memoryLocalPair)) {
            memoryLocal.add(memoryLocalPair);
        }
    }

    public void getLocal(ArrayList<String> alphabetNode, StringBuilder action, String nameObject, String nameNode, String data) {
        String get = "get_" + nameObject + "_" + nameNode + "_" + nameDiagramResolver(ad.getName()) + "." + adParser.countGet_ad++;
        action.append(get + "?" + data + " -> ");
        Pair<String, String> memoryLocalPair = new Pair<String, String>(nameNode, nameObject);
        if (!memoryLocal.contains(memoryLocalPair)) {
            memoryLocal.add(memoryLocalPair);
        }
    }

    public void setLocalInput(ArrayList<String> alphabetNode, StringBuilder action, String nameObject, String nameNode, String data, String oeIn) {
        String set = "set_" + nameObject + "_" + nameNode + "_" + nameDiagramResolver(ad.getName()) + "." + adParser.countSet_ad++;
        action.append(set + "!" + data + " -> ");
        Pair<String, String> memoryLocalPair = new Pair<String, String>(nameNode, nameObject);
        memoryLocalChannel.add(new Pair<String, String>(oeIn, nameObject));

        if (!memoryLocal.contains(memoryLocalPair)) {
            memoryLocal.add(memoryLocalPair);
        }
    }

    public void lock(ArrayList<String> alphabetNode, StringBuilder action, int inOut, String nameNode) {
        if (ADParser.containsCallBehavior) {
            if (inOut == 0) {
                String lock = "lock_" + nameNode;
                alphabetNode.add(lock);
                lockChannel.add(nameNode);
                action.append(lock + ".lock -> ");
            } else {
                String lock = "lock_" + nameNode;
                action.append(lock + ".unlock -> ");
            }
        }
    }

    public void event(ArrayList<String> alphabet, String nameAction, StringBuilder action) {
        alphabet.add("event_" + nameAction);
        eventChannel.add("event_" + nameAction);
        action.append("event_" + nameAction + " -> ");
    }

    public void ce(ArrayList<String> alphabetNode, StringBuilder action, String ce, String posCe) {
        alphabetNode.add(ce);
        action.append(ce + posCe);
    }

    public void oe(ArrayList<String> alphabetNode, StringBuilder action, String oe, String data, String posOe) {
        alphabetNode.add(oe);
        action.append(oe + data + posOe);
    }

    public void update(ArrayList<String> alphabetNode, StringBuilder action, int countInFlows, int countOutFlows, boolean canBeNegative) {
        int result = countOutFlows - countInFlows;

        if (result != 0) {
            if (countOutFlows == 0 && canBeNegative || countOutFlows > 0) {
                String update = "update_" + nameDiagramResolver(ad.getName()) + "." + adParser.countUpdate_ad++;
                alphabetNode.add(update);
                action.append(update + "!(" + countOutFlows + "-" + countInFlows + ") -> ");

                if (result < adParser.limiteInf) {
                    adParser.limiteInf = result;

                    if (adParser.limiteSup == -99) {
                        adParser.limiteSup = result;
                    }

                }

                if (result > adParser.limiteSup) {
                    adParser.limiteSup = result;

                    if (adParser.limiteInf == 99) {
                        adParser.limiteInf = result;
                    }

                }
            }
        }

    }

    public void clear(ArrayList<String> alphabetNode, StringBuilder action) {
        String clear = "clear_" + nameDiagramResolver(ad.getName()) + "." + adParser.countClear_ad++;
        alphabetNode.add(clear);
        action.append(clear + " -> ");
    }

    public List<String> replaceExpression(String expression) {
        String value = "";
        char[] expChar = expression.toCharArray();
        List<String> expReplaced = new ArrayList<>();
        for (int i = 0; i < expChar.length; i++) {
            if (expChar[i] == '+' || expChar[i] == '-' || expChar[i] == '*' || expChar[i] == '/') {
                expReplaced.add(value);
                value = "";
            } else {
                value += expChar[i];
            }
        }

        if (!value.equals("")) {    //add last value
            expReplaced.add(value);
        }

        return expReplaced;
    }

    public List<String> getObjects(IFlow flow, List<String> nodes) {
        List<String> objects = new ArrayList<>();

        if (!nodes.contains(flow.getSource().getId())) {
            nodes.add(flow.getSource().getId());
            if (flow.getSource() instanceof IActivityParameterNode) {
                objects.add(flow.getSource().getName());
            } else if (flow.getSource() instanceof IOutputPin) {
                IInputPin[] inPins = ((IAction) flow.getSource().getOwner()).getInputs();
                for (int x = 0; x < inPins.length; x++) {
                    for (IFlow flowNode : inPins[x].getIncomings()) {
                        objects.addAll(getObjects(flowNode, nodes));
                    }
                }
            } else {
                for (IFlow flowNode : flow.getSource().getIncomings()) {
                    objects.addAll(getObjects(flowNode, nodes));
                }
            }
        }

        return objects;
    }

    public void signal(ArrayList<String> alphabet, String nameSignal, StringBuilder signal) {
        if (!localSignalChannelsSync.contains("signal_" + nameSignal)) {
            localSignalChannelsSync.add("signal_" + nameSignal);
        }

        if (!signalChannels.contains(nameSignal)) {
            signalChannels.add(nameSignal);
        }

        if (!signalChannelsLocal.contains(nameSignal)) {
            signalChannelsLocal.add(nameSignal);
        }

        int idSignal = 1;
        int index = -1;

        for (int i = 0; i < countSignal.size(); i++) {
            if (countSignal.get(i).getKey().equals(nameSignal)) {
                idSignal = countSignal.get(i).getValue();
                index = i;
                break;
            }
        }

        alphabet.add("signal_" + nameSignal + "." + idSignal);
        signal.append("signal_" + nameSignal + "!" + idSignal + " -> ");

        if (index >= 0) {
            countSignal.set(index, new Pair<String, Integer>(nameSignal, idSignal + 1));
        } else {
            countSignal.add(new Pair<String, Integer>(nameSignal, idSignal + 1));
        }

    }

    public void accept(ArrayList<String> alphabet, String nameAccept, StringBuilder accept) {
        if (!localSignalChannelsSync.contains("signal_" + nameAccept)) {
            localSignalChannelsSync.add("signal_" + nameAccept);
        }

        if (!signalChannels.contains(nameAccept)) {
            signalChannels.add(nameAccept);
        }

        int idAccept = 1;
        int index = -1;

        for (int i = 0; i < countAccept.size(); i++) {
            if (countAccept.get(i).getKey().equals(nameAccept)) {
                idAccept = countAccept.get(i).getValue();
                index = i;
                break;
            }
        }

        alphabet.add("accept_" + nameAccept + "." + idAccept);
        accept.append("accept_" + nameAccept + "." + idAccept + "?x -> ");

        if (index >= 0) {
            countAccept.set(index, new Pair<String, Integer>(nameAccept, idAccept + 1));
        } else {
            countAccept.add(new Pair<String, Integer>(nameAccept, idAccept + 1));
        }
    }

    public String nameDiagramResolver(String name) {
        return name.replace(" ", "").replace("!", "_").replace("@", "_")
                .replace("%", "_").replace("&", "_").replace("*", "_")
                .replace("(", "_").replace(")", "_").replace("+", "_")
                .replace("-", "_").replace("=", "_").replace("?", "_")
                .replace(":", "_").replace("/", "_").replace(";", "_")
                .replace(">", "_").replace("<", "_").replace(",", "_")
                .replace("{", "_").replace("}", "_").replace("|", "_")
                .replace("\\", "_");
    }

    public int addCountCall(String name) {
        int i = 1;
        if (countCall.containsKey(name)) {
            i = countCall.get(name);
            countCall.put(name, ++i);
        } else {
            countCall.put(name, i);
        }
        return i;
    }

    public int addCountGuard(String guard) {
        int i = 1;
        if (allGuards.containsKey(guard)) {
            i = allGuards.get(guard);
            allGuards.put(guard, ++i);
        } else {
            allGuards.put(guard, i);
        }

        return i;
    }

    public String getDefaultValue(String type) {
        HashMap<String, String> typesParameter = new HashMap<>();
        String[] definition = adDiagram.getDefinition().replace(" ", "").split(";");

        for (String def : definition) {
            String[] keyValue = def.split("=");
            typesParameter.put(keyValue[0], keyValue[1]);
        }

        String defaultValue = typesParameter.get(type).replace("{", "").replace("}", "").replace("(", "")
                .replace(")", "").split(",")[0];
        String defaultValueFinal = defaultValue.split("\\.\\.")[0];
        return defaultValueFinal;
    }

    private boolean isSignal(IActivityNode activityNode) {
        return (activityNode instanceof IAction &&
                ((((IAction) activityNode).isSendSignalAction() && createdSignal.contains(activityNode.getId())) ||
                        (((IAction) activityNode).isAcceptEventAction() && createdAccept.contains(activityNode.getId()))));
    }

    public int countAmount(IActivityNode activityNode) {
        int input = 0;
        if (activityNode != null) {
            input = 0;
            IFlow[] inFlow = activityNode.getIncomings();

            for (int i = 0; i < inFlow.length; i++) {
                if (syncChannelsEdge.containsKey(inFlow[i].getId())) {
                    input++;
                }
            }


            if (activityNode instanceof IAction) {
                IInputPin[] inPin = ((IAction) activityNode).getInputs();

                for (int i = 0; i < inPin.length; i++) {
                    IFlow[] inFlowPin = inPin[i].getIncomings();
                    for (int x = 0; x < inFlowPin.length; x++) {
                        if (syncObjectsEdge.containsKey(inFlowPin[x].getId())) {
                            input++;
                        }
                    }
                }

            } else {
                for (int i = 0; i < inFlow.length; i++) {
                    if (syncObjectsEdge.containsKey(inFlow[i].getId())) {
                        input++;
                    }
                }
            }
        }

        return input;
    }
}
