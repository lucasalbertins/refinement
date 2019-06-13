package com.ref.parser.activityDiagram;

import com.change_vision.jude.api.inf.model.IActivity;
import com.change_vision.jude.api.inf.model.IActivityDiagram;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ADDefineTypes {

    private IActivity ad;
    private IActivityDiagram adDiagram;

    private String firstDiagram;

    private HashMap<String, Integer> countCall;
    private HashMap<String, ArrayList<String>> alphabetNode;
    private HashMap<String, String> objectEdges;
    private HashMap<String, String> parameterNodesInput;
    private HashMap<String, String> parameterNodesOutput;
    private List<Pair<String, String>> memoryLocalChannel;
    private List<ArrayList<String>> unionList;
    private HashMap<String, String> typeUnionList;
    private List<Pair<String, Integer>> countSignal;
    private List<Pair<String, Integer>> countAccept;
    private ADUtils adUtils;
    private ADParser adParser;

    public ADDefineTypes(IActivity ad, IActivityDiagram adDiagram, String firstDiagram, HashMap<String, Integer> countCall, HashMap<String, ArrayList<String>> alphabetNode,
                         HashMap<String, String> objectEdges, HashMap<String, String> parameterNodesInput, HashMap<String, String> parameterNodesOutput,
                         List<Pair<String, String>> memoryLocalChannel, List<ArrayList<String>> unionList, HashMap<String, String> typeUnionList,
                         List<Pair<String, Integer>> countSignal, List<Pair<String, Integer>> countAccept, ADUtils adUtils, ADParser adParser) {
        this.ad = ad;
        this.adDiagram = adDiagram;
        this.firstDiagram = firstDiagram;
        this.countCall = countCall;
        this.alphabetNode = alphabetNode;
        this.objectEdges = objectEdges;
        this.parameterNodesInput = parameterNodesInput;
        this.parameterNodesOutput = parameterNodesOutput;
        this.memoryLocalChannel = memoryLocalChannel;
        this.unionList = unionList;
        this.typeUnionList = typeUnionList;
        this.countSignal = countSignal;
        this.countAccept = countAccept;
        this.adUtils = adUtils;
        this.adParser = adParser;
    }

    public String defineTypes() {
        StringBuilder types = new StringBuilder();
        String nameDiagram = adUtils.nameDiagramResolver(ad.getName());

        if (firstDiagram.equals(ad.getId())) { // igual a primeira ocorrencia

            for (String id : countCall.keySet()) {
                types.append("ID_" + id + " = {1.." + countCall.get(id) + "}\n");
            }

            types.append("datatype T = lock | unlock\n");

            for (Pair<String, Integer> signal : countSignal) {
                types.append("countSignal_signal_" + signal.getKey() + " = {1.." + (signal.getValue() - 1) + "}\n");
            }

            for (Pair<String, Integer> accept : countAccept) {
                types.append("countAccept_signal_" + accept.getKey() + " = {1.." + (accept.getValue() - 1) + "}\n");
            }

        }

        if (alphabetNode.size() > 0) {
            String termination = "_" + nameDiagram + "_t_alphabet";
            types.append("datatype alphabet_" + nameDiagram + " = ");
            boolean first = true;

            for (String node : alphabetNode.keySet()) {
                if (first) {
                    types.append(node + termination + " ");
                    first = false;
                } else {
                    types.append("| " + node + termination);
                }
            }
            types.append("\n");
        }

        if (parameterNodesInput.size() > 0 || parameterNodesOutput.size() > 0) {
            HashMap<String, String> typesParameter = new HashMap<>();
            String[] definition = adDiagram.getDefinition().replace(" ", "").split(";");

            for (String def : definition) {
                String[] keyValue = def.split("=");
                typesParameter.put(keyValue[0], keyValue[1]);
            }

            for (String input : parameterNodesInput.keySet()) {
                types.append(input + "_" + nameDiagram + " = ");

                types.append(typesParameter.get(parameterNodesInput.get(input)) + "\n"); //Verificar se possivel usar o campo definition para definir o intervalo

            }

            for (String output : parameterNodesOutput.keySet()) {
                types.append(output + "_" + nameDiagram + " = ");

                types.append(typesParameter.get(parameterNodesOutput.get(output)) + "\n"); //Verificar se possivel usar o campo definition para definir o intervalo

            }

            List<String> buffer = new ArrayList<>();

            for (ArrayList<String> union : unionList) {
                String objectUnion = "";
                String nameLast = "";
                for (int i = 0; i < union.size(); i++) {
                    objectUnion += union.get(i);
                    nameLast = union.get(i);
                }

                if (!buffer.contains(objectUnion)) {
                    types.append(objectUnion + "_" + nameDiagram + " = ");
                    types.append(typesParameter.get(parameterNodesInput.get(nameLast)) + "\n");
                    buffer.add(objectUnion);
                }

            }

            for (Pair<String, String> pair : memoryLocalChannel) {
                if (!parameterNodesInput.containsKey(pair.getValue()) && !parameterNodesOutput.containsKey(pair.getValue()) && !buffer.contains(pair.getValue())) {
                    types.append(pair.getValue() + "_" + nameDiagram + " = ");
                    if (objectEdges.containsKey(pair.getKey())) {
                        if (typeUnionList.containsKey(objectEdges.get(pair.getKey()))) {
                            types.append(typesParameter.get(typeUnionList.get(objectEdges.get(pair.getKey()))) + "\n");
                        } else {
                            types.append(typesParameter.get(parameterNodesInput.get(objectEdges.get(pair.getKey()))) + "\n");
                        }
                    } else {
                        types.append(typesParameter.get(parameterNodesInput.get(pair.getValue())) + "\n");
                    }

                    buffer.add(pair.getValue());
                }

            }


        }

        if (adParser.countGet_ad > 1 || adParser.countSet_ad > 1) {
            if (adParser.countGet_ad == 1) {
                types.append("countGet_" + nameDiagram + " = {1.." + adParser.countGet_ad + "}\n");
            } else {
                types.append("countGet_" + nameDiagram + " = {1.." + (adParser.countGet_ad - 1) + "}\n");
            }

            if (adParser.countSet_ad == 1) {
                types.append("countSet_" + nameDiagram + " = {1.." + adParser.countSet_ad + "}\n");
            } else {
                types.append("countSet_" + nameDiagram + " = {1.." + (adParser.countSet_ad - 1) + "}\n");
            }
        }

        if (adParser.countCe_ad > 1) {
            types.append("countCe_" + nameDiagram + " = {1.." + (adParser.countCe_ad - 1) + "}\n");
        }

        if (adParser.countOe_ad > 1) {
            types.append("countOe_" + nameDiagram + " = {1.." + (adParser.countOe_ad - 1) + "}\n");
        }

        types.append("countUpdate_" + nameDiagram + " = {1.." + (adParser.countUpdate_ad - 1) + "}\n");

        types.append("countClear_" + nameDiagram + " = {1.." + (adParser.countClear_ad - 1) + "}\n");

        types.append("limiteUpdate_" + nameDiagram + " = {(" + adParser.limiteInf + ")..(" + adParser.limiteSup + ")}\n");

        return types.toString();
    }
}
