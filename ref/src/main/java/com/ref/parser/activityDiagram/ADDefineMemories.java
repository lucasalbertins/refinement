package com.ref.parser.activityDiagram;

import com.change_vision.jude.api.inf.model.IActivity;

import java.util.HashMap;
import java.util.List;

public class ADDefineMemories {

    private IActivity ad;

    private HashMap<String, String> parameterNodesInput;
    private HashMap<String, String> parameterNodesOutput;
    private List<Pair<String, String>> memoryLocal;
    private ADUtils adUtils;

    public ADDefineMemories(IActivity ad, HashMap<String, String> parameterNodesInput, HashMap<String, String> parameterNodesOutput,
                            List<Pair<String, String>> memoryLocal, ADUtils adUtils) {
        this.ad = ad;
        this.parameterNodesInput = parameterNodesInput;
        this.parameterNodesOutput = parameterNodesOutput;
        this.memoryLocal = memoryLocal;
        this.adUtils = adUtils;
    }

    public String defineMemories() {
        StringBuilder memory = new StringBuilder();
        String nameDiagram = adUtils.nameDiagramResolver(ad.getName());

        defineMemoryLocal(memory, nameDiagram);

        if (parameterNodesInput.size() > 0 || parameterNodesOutput.size() > 0) {
            defineMemoryGlobal(memory, nameDiagram, parameterNodesInput);
            defineMemoryGlobal(memory, nameDiagram, parameterNodesOutput);

            memory.append("Mem_" + nameDiagram + " = ");

            for (int i = 0; i < parameterNodesInput.size() + parameterNodesOutput.size() - 1; i++) {
                memory.append("(");
            }

            int i = 0;

            for (String input : parameterNodesInput.keySet()) {
                if (i <= 1) {
                    memory.append("Mem_" + nameDiagram + "_" + input + "_t(" + adUtils.getDefaultValue(parameterNodesInput.get(input)) + ")");
                    if (i % 2 == 0 && (i + 1 < parameterNodesInput.size() || parameterNodesOutput.size() > 0)) {
                        memory.append(" [|{|endActivity_" + nameDiagram + "|}|] ");
                    } else if (parameterNodesInput.size() + parameterNodesOutput.size() > 1) {
                        memory.append(")");
                    }
                } else {
                    memory.append(" [|{|endActivity_" + nameDiagram + "|}|] ");
                    memory.append("Mem_" + nameDiagram + "_" + input + "_t(" + adUtils.getDefaultValue(parameterNodesInput.get(input)) + "))");
                }

                i++;
            }

            for (String output : parameterNodesOutput.keySet()) {
                if (i <= 1) {
                    memory.append("Mem_" + nameDiagram + "_" + output + "_t(" + adUtils.getDefaultValue(parameterNodesOutput.get(output)) + ")");
                    if (i % 2 == 0 && parameterNodesOutput.size() > 1 &&
                            i < parameterNodesOutput.size()) {
                        memory.append(" [|{|endActivity_" + nameDiagram + "|}|] ");
                    } else if (parameterNodesInput.size() + parameterNodesOutput.size() > 1) {
                        memory.append(")");
                    }
                } else {
                    memory.append(" [|{|endActivity_" + nameDiagram + "|}|] ");
                    memory.append("Mem_" + nameDiagram + "_" + output + "_t(" + adUtils.getDefaultValue(parameterNodesOutput.get(output)) + "))");
                }

                i++;
            }

        }

        memory.append("\n");

        return memory.toString();
    }

    private void defineMemoryLocal(StringBuilder memory, String nameDiagram) {
        for (Pair<String, String> pair : memoryLocal) {
            memory.append("Mem_" + pair.getKey() + "_" + nameDiagram + "_" + pair.getValue() + "(" + pair.getValue() + ") = ");
            memory.append("get_" + pair.getValue() + "_" + pair.getKey() + "_" + nameDiagram + "?c!" + pair.getValue() + " -> ");
            memory.append("Mem_" + pair.getKey() + "_" + nameDiagram + "_" + pair.getValue() + "(" + pair.getValue() + ") [] ");
            memory.append("set_" + pair.getValue() + "_" + pair.getKey() + "_" + nameDiagram + "?c?" + pair.getValue() + " -> ");
            memory.append("Mem_" + pair.getKey() + "_" + nameDiagram + "_" + pair.getValue() + "(" + pair.getValue() + ")\n");

            memory.append("Mem_" + pair.getKey() + "_" + nameDiagram + "_" + pair.getValue() + "_t" + "(" + pair.getValue() + ") = ");
            memory.append("Mem_" + pair.getKey() + "_" + nameDiagram + "_" + pair.getValue() + "(" + pair.getValue() + ") /\\ END_DIAGRAM_" + nameDiagram + "\n");
        }
    }

    private void defineMemoryGlobal(StringBuilder memory, String nameDiagram, HashMap<String, String> memoryGlobal) {
        for (String value : memoryGlobal.keySet()) {
            memory.append("Mem_" + nameDiagram + "_" + value + "(" + value + ") = ");
            memory.append("get_" + value + "_" + nameDiagram + "?c!" + value + " -> ");
            memory.append("Mem_" + nameDiagram + "_" + value + "(" + value + ") [] ");
            memory.append("set_" + value + "_" + nameDiagram + "?c?" + value + " -> ");
            memory.append("Mem_" + nameDiagram + "_" + value + "(" + value + ")\n");

            memory.append("Mem_" + nameDiagram + "_" + value + "_t" + "(" + value + ") = ");
            memory.append("Mem_" + nameDiagram + "_" + value + "(" + value + ") /\\ (endActivity_" + nameDiagram + "?" + value + " -> SKIP)\n");
        }
    }
}
