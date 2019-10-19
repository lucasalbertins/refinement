package com.ref.parser.activityDiagram;

import com.change_vision.jude.api.inf.model.IActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ADDefineMainNodes {

    private IActivity ad;

    private String firstDiagram;
    private List<String> lockChannel;
    private HashMap<String, String> parameterNodesInput;
    private HashMap<String, String> parameterNodesOutput;
    private List<Pair<String, Integer>> callBehaviourNumber;
    private HashMap<String, List<String>> callBehaviourInputs;
    private List<String> localSignalChannelsSync;
    private List<String> signalChannelsLocal;
    private ADUtils adUtils;
    private ADParser adParser;

    public ADDefineMainNodes(IActivity ad, String firstDiagram, List<String> lockChannel, HashMap<String, String> parameterNodesInput, HashMap<String, String> parameterNodesOutput,
                             List<Pair<String, Integer>> callBehaviourNumber, HashMap<String, List<String>> callBehaviourInputs,
                             List<String> localSignalChannelsSync, List<String> signalChannelsLocal, ADUtils adUtils, ADParser adParser) {
        this.ad = ad;
        this.firstDiagram = firstDiagram;
        this.lockChannel = lockChannel;
        this.parameterNodesInput = parameterNodesInput;
        this.parameterNodesOutput = parameterNodesOutput;
        this.callBehaviourNumber = callBehaviourNumber;
        this.callBehaviourInputs = callBehaviourInputs;
        this.localSignalChannelsSync = localSignalChannelsSync;
        this.signalChannelsLocal = signalChannelsLocal;
        this.adUtils = adUtils;
        this.adParser = adParser;
    }

    public String defineMainNodes() {
        StringBuilder mainNode = new StringBuilder();
        String nameDiagram = adUtils.nameDiagramResolver(ad.getName());
        ArrayList<String> alphabet = new ArrayList<>();

        if (firstDiagram.equals(ad.getId())) {
            mainNode.append("MAIN = " + nameDiagram + "(1); LOOP\n");
            mainNode.append("LOOP = loop -> LOOP\n");
        }

        mainNode.append("END_DIAGRAM_" + nameDiagram + " = endDiagram_" + nameDiagram + " -> SKIP\n");
        mainNode.append(nameDiagram + "(ID_" + nameDiagram + ") = ");

        if (parameterNodesInput.size() + parameterNodesOutput.size() > 0) {
            mainNode.append("(");
        }

        if (adParser.countUpdate_ad > 0) {
            mainNode.append("(");
        }

        if (lockChannel.size() > 0) {
            mainNode.append("(");
        }

        for (int i = 0; i < callBehaviourNumber.size(); i++) {
            mainNode.append("(");
        }

        for (int i = 0; i < signalChannelsLocal.size(); i++) {
            mainNode.append("(");
        }

        mainNode.append("Internal_" + nameDiagram + "(ID_" + nameDiagram + ")");

        for (Pair<String, Integer> callBehaviourAD : callBehaviourNumber) {
            mainNode.append(" [|{|startActivity_" + callBehaviourAD.getKey() + "." + callBehaviourAD.getValue() +
                    ",endActivity_" + callBehaviourAD.getKey() + "." + callBehaviourAD.getValue());

            mainNode.append("|}|] ");

            mainNode.append(callBehaviourAD.getKey() + "(" + callBehaviourAD.getValue() + "))");
        }

        mainNode.append(" [|{|update_" + nameDiagram + ",clear_" + nameDiagram + ",endDiagram_" + nameDiagram + "|}|] ");
        mainNode.append("TokenManager_" + nameDiagram + "_t(0,0))");

        for (String signal: signalChannelsLocal) {
            mainNode.append(" [|{|");
            mainNode.append("signal_" + signal + "," + "accept_" + signal + "," + "endDiagram_" + nameDiagram);
            mainNode.append("|}|] pool_" + signal + "_t(<>))");
        }

        if (lockChannel.size() > 0) {
            mainNode.append(" [|{|");
            for (String lock : lockChannel) {
                mainNode.append("lock_" + lock + ",");
            }
            mainNode.append("endDiagram_" + nameDiagram + "|}|] ");
        }

        if (parameterNodesInput.size() + parameterNodesOutput.size() > 0) {
            if (lockChannel.size() > 0) {
                mainNode.append("Lock_" + nameDiagram + ")");
            }

            mainNode.append(" [|{|");

            for (String input : parameterNodesInput.keySet()) {
                mainNode.append("get_" + input + "_" + nameDiagram + ",");
                mainNode.append("set_" + input + "_" + nameDiagram + ",");
            }

            for (String output : parameterNodesOutput.keySet()) {
                mainNode.append("get_" + output + "_" + nameDiagram + ",");
                mainNode.append("set_" + output + "_" + nameDiagram + ",");
            }

            mainNode.append("endActivity_" + nameDiagram + "|}|] ");

            mainNode.append("Mem_" + nameDiagram + ")\n");
        } else if (lockChannel.size() > 0) {
            mainNode.append("Lock_" + nameDiagram + ")\n");
        } else {
            mainNode.append("\n");
        }

        mainNode.append("Internal_" + nameDiagram + "(ID_" + nameDiagram + ") = ");
        mainNode.append("StartActivity_" + nameDiagram + "(ID_" + nameDiagram + "); Node_" + nameDiagram + "; EndActivity_" + nameDiagram + "(ID_" + nameDiagram + ")\n");


        mainNode.append("StartActivity_" + nameDiagram + "(ID_" + nameDiagram + ") = ");
        mainNode.append("startActivity_" + nameDiagram + ".ID_" + nameDiagram);

        if (parameterNodesInput.size() > 0) {
            if (callBehaviourInputs.containsKey(adUtils.nameDiagramResolver(ad.getName()))) {
                for (String input : callBehaviourInputs.get(adUtils.nameDiagramResolver(ad.getName()))) {
                    mainNode.append("?" + input);
                }
            } else {
                for (String input : parameterNodesInput.keySet()) {
                    mainNode.append("?" + input);
                }
            }


            mainNode.append(" -> ");

            if (callBehaviourInputs.containsKey(adUtils.nameDiagramResolver(ad.getName()))) {
                for (String input : callBehaviourInputs.get(adUtils.nameDiagramResolver(ad.getName()))) {
                    adUtils.set(alphabet, mainNode, input, input);
                }
            } else {
                for (String input : parameterNodesInput.keySet()) {
                    adUtils.set(alphabet, mainNode, input, input);
                }
            }

            mainNode.append("SKIP\n");
        } else {
            mainNode.append(" -> SKIP\n");
        }


        mainNode.append("EndActivity_" + nameDiagram + "(ID_" + nameDiagram + ") = ");

        if (parameterNodesOutput.size() > 0) {
            for (String input : parameterNodesOutput.keySet()) {
                adUtils.get(alphabet, mainNode, input);
            }

            mainNode.append("endActivity_" + nameDiagram + ".ID_" + nameDiagram);

            for (String output : parameterNodesOutput.keySet()) {
                mainNode.append("!" + output);
            }

            mainNode.append(" -> SKIP");
        } else {
            mainNode.append("endActivity_" + nameDiagram + ".ID_" + nameDiagram + " -> SKIP");
        }

        mainNode.append("\n");
        return mainNode.toString();
    }
}
