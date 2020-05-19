package com.ref.parser.activityDiagram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.change_vision.jude.api.inf.model.IAction;
import com.change_vision.jude.api.inf.model.IActivity;
import com.change_vision.jude.api.inf.model.IActivityNode;

public class ADDefineMemories {

    private IActivity ad;

    private HashMap<String, String> parameterNodesInput;
    private HashMap<String, String> parameterNodesOutput;
    private Map<Pair<String, String>,String> memoryLocal;
    private ADUtils adUtils;
    public static HashMap<IActivityNode,IActivity> CBAMemAlphabet = new HashMap<>();
    private static List<IActivity> CBAMemAlphabetDone = new ArrayList<>();
    
    public ADDefineMemories(IActivity ad, HashMap<String, String> parameterNodesInput, HashMap<String, String> parameterNodesOutput,
                            Map<Pair<String, String>,String> memoryLocal, ADUtils adUtils) {
        this.ad = ad;
        this.parameterNodesInput = parameterNodesInput;
        this.parameterNodesOutput = parameterNodesOutput;
        this.memoryLocal = memoryLocal;
        this.adUtils = adUtils;
    }

    public String defineMemories() {
        StringBuilder memory = new StringBuilder();
        String nameDiagram = adUtils.nameDiagramResolver(ad.getName());
        IActivityNode[] nodes = ad.getActivityNodes();
        StringBuilder alphabetMemory = new StringBuilder();
        
        defineMemoryLocal(memory, nameDiagram,alphabetMemory);
        
        if (parameterNodesInput.size() > 0 || parameterNodesOutput.size() > 0) {
            defineMemoryGlobal(memory, nameDiagram, parameterNodesInput,parameterNodesOutput.size()>0);
            defineMemoryGlobal(memory, nameDiagram, parameterNodesOutput,parameterNodesOutput.size()>0);

            memory.append("Mem_" + nameDiagram + "(id) = ");

            for (int i = 0; i < parameterNodesInput.size() + parameterNodesOutput.size() - 1; i++) {
                memory.append("(");
            }

            int i = 0;

            for (String input : parameterNodesInput.keySet()) {
                if (i <= 1) {
                    memory.append("Mem_" + nameDiagram + "_" + input + "_t(id," + adUtils.getDefaultValue(parameterNodesInput.get(input)) + ")");
                    if (i % 2 == 0 && (i + 1 < parameterNodesInput.size() || parameterNodesOutput.size() > 0)) {
                        memory.append(" [|{|endActivity_" + nameDiagram + "|}|] ");
                    } else if (parameterNodesInput.size() + parameterNodesOutput.size() > 1) {
                        memory.append(")");
                    }
                } else {
                    memory.append(" [|{|endActivity_" + nameDiagram + "|}|] ");
                    memory.append("Mem_" + nameDiagram + "_" + input + "_t(id," + adUtils.getDefaultValue(parameterNodesInput.get(input)) + "))");
                }

                i++;
            }

            for (String output : parameterNodesOutput.keySet()) {
                if (i <= 1) {
                    memory.append("Mem_" + nameDiagram + "_" + output + "_t(id," + adUtils.getDefaultValue(parameterNodesOutput.get(output)) + ")");
                    if (i % 2 == 0 && parameterNodesOutput.size() > 1 &&
                            i < parameterNodesOutput.size()) {
                        memory.append(" [|{|endActivity_" + nameDiagram + "|}|] ");
                    } else if (parameterNodesInput.size() + parameterNodesOutput.size() > 1) {
                        memory.append(")");
                    }
                } else {
                    memory.append(" [|{|endActivity_" + nameDiagram + "|}|] ");
                    memory.append("Mem_" + nameDiagram + "_" + output + "_t(id," + adUtils.getDefaultValue(parameterNodesOutput.get(output)) + "))");
                }

                i++;
            }

        }
        
        for(IActivityNode node: nodes) {//TODO ver se ta certo mesmo
        	if(CBAMemAlphabet.containsKey(node) && !CBAMemAlphabetDone.contains(((IAction)node).getCallingActivity())) {
        		memory.append("\nAlphabetMem"+adUtils.nameDiagramResolver(node.getName())+"(id) = {|");
        		memory.append(alphabetMemory);
        		memory.append("endDiagram_"+nameDiagram+".id|}\n");
        		CBAMemAlphabetDone.add(((IAction)node).getCallingActivity());
    		}
        }
        
        memory.append("\n");
        return memory.toString();
    }

    private void defineMemoryLocal(StringBuilder memory, String nameDiagram, StringBuilder alphabetMemory) {
        StringBuilder aux = new StringBuilder();
        StringBuilder name = new StringBuilder(); 
    	for (Pair<String, String> pair : memoryLocal.keySet()) {
            memory.append("Mem_" + pair.getKey() + "_" + nameDiagram + "_" + pair.getValue() + "(id," + pair.getValue() + ") = ");
            memory.append("get_" + pair.getValue() + "_" + pair.getKey() + "_" + nameDiagram + ".id?c!" + pair.getValue() + " -> ");
            memory.append("Mem_" + pair.getKey() + "_" + nameDiagram + "_" + pair.getValue() + "(id," + pair.getValue() + ") [] ");
            memory.append("set_" + pair.getValue() + "_" + pair.getKey() + "_" + nameDiagram + ".id?c?" + pair.getValue() + " -> ");
            memory.append("Mem_" + pair.getKey() + "_" + nameDiagram + "_" + pair.getValue() + "(id," + pair.getValue() + ")\n");
            memory.append("Mem_" + pair.getKey() + "_" + nameDiagram + "_" + pair.getValue() + "_t" + "(id," + pair.getValue() + ") = ");
            memory.append("Mem_" + pair.getKey() + "_" + nameDiagram + "_" + pair.getValue() + "(id," + pair.getValue() + ") /\\ END_DIAGRAM_" + nameDiagram + "(id)\n");
            
            name.append(name.toString().equals("")?"Mem_" + pair.getKey() + "_" + nameDiagram + "(id) =":"");
            aux.append(" Mem_" + pair.getKey() + "_" + nameDiagram+ "_" + pair.getValue() +"_t(id,0) |||");
            alphabetMemory.append("get_" + pair.getValue() + "_" + pair.getKey() + "_" + nameDiagram + ".id,");
            alphabetMemory.append("set_" + pair.getValue() + "_" + pair.getKey() + "_" + nameDiagram + ".id,");
            
        }
    	if(aux.length()!=0) {
    		aux.replace(aux.lastIndexOf("|||"), aux.lastIndexOf("|||")+3, "");//remover ultimo |||
    	}
    	memory.append(name);
    	memory.append(aux+"\n");
    }

    private void defineMemoryGlobal(StringBuilder memory, String nameDiagram, HashMap<String, String> memoryGlobal, boolean outputParam) {
    	for (String value : memoryGlobal.keySet()) {
            memory.append("Mem_" + nameDiagram + "_" + value + "(id," + value + ") = ");
            memory.append("get_" + value + "_" + nameDiagram + ".id?c!" + value + " -> ");
            memory.append("Mem_" + nameDiagram + "_" + value + "(id," + value + ") [] ");
            memory.append("set_" + value + "_" + nameDiagram + ".id?c?" + value + " -> ");
            memory.append("Mem_" + nameDiagram + "_" + value + "(id," + value + ")\n");
            memory.append("Mem_" + nameDiagram + "_" + value + "_t" + "(id," + value + ") = ");
            
            memory.append("Mem_" + nameDiagram + "_" + value + "(id," + value + ") /\\ (endActivity_" + nameDiagram + ".id"+(outputParam?"?" + value:"") + " -> SKIP)\n");
            //TODO verificar os endActivity
        }
    }
}
