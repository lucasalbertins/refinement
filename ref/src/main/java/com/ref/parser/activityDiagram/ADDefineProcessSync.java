package com.ref.parser.activityDiagram;

import com.change_vision.jude.api.inf.model.IActivity;

import java.util.ArrayList;
import java.util.HashMap;


public class ADDefineProcessSync {

    private IActivity ad;
    private HashMap<String, ArrayList<String>> alphabetNode;
    private ADUtils adUtils;

    public ADDefineProcessSync(IActivity ad, HashMap<String, ArrayList<String>> alphabetNode, ADUtils adUtils) {
        this.ad = ad;
        this.alphabetNode = alphabetNode;
        this.adUtils = adUtils;
    }

    public String defineProcessSync() {
        StringBuilder processSync = new StringBuilder();
        String nameDiagram = adUtils.nameDiagramResolver(ad.getName());
        String termination = "_" + nameDiagram + "_t";
        String terminationAlphabet = "_" + nameDiagram + "_t_alphabet";
        StringBuilder alphabetDiagram = new StringBuilder();
        Boolean ehCBA = false;
        
        for(String node : alphabetNode.keySet()) {
            ArrayList<String> alphabet = alphabetNode.get(node);
            for(IActivity CBAs:ADParser.callBehaviourList) {
            	if(ADUtils.nameResolver(CBAs.getName()).equals(node)) {//se for um cba
            		ehCBA = true;
            	}
            }
            if(ehCBA) {//se for um cba
        		processSync.append("AlphabetDiagram_" + nameDiagram + "(" + node + terminationAlphabet + ") = union({|");
                alphabetDiagram.append("AlphabetDiagram_" + nameDiagram + "(" + node + terminationAlphabet + ")"+"SUB");
                for (int i = 0; i < alphabet.size(); i++) {
                    processSync.append(alphabet.get(i));
                    if (i < alphabet.size() - 1) {
                        processSync.append(",");
                    }
                }
                processSync.append("|},AlphabetDiagram_"+node+"_t)\n");
        	}else {
        		processSync.append("AlphabetDiagram_" + nameDiagram + "(" + node + terminationAlphabet + ") = {|");
                alphabetDiagram.append("AlphabetDiagram_" + nameDiagram + "(" + node + terminationAlphabet + ")"+"SUB");
                for (int i = 0; i < alphabet.size(); i++) {
                    processSync.append(alphabet.get(i));
                    if (i < alphabet.size() - 1) {
                        processSync.append(",");
                    }
                }
        		processSync.append("|}\n");
        	}           
         
            ehCBA = false;        
        }
   
        processSync.append("AlphabetDiagram_" + nameDiagram +"_t = ");
        for(int i = 1; i< alphabetNode.size();i++) {
        	processSync.append("union(");
        }
        alphabetDiagram.replace(alphabetDiagram.indexOf("SUB"), alphabetDiagram.indexOf("SUB")+3,",");
        alphabetDiagram.replace(alphabetDiagram.lastIndexOf("SUB"), alphabetDiagram.lastIndexOf("SUB")+3,")\n\n");
        String aux = alphabetDiagram.toString().replaceAll("SUB", "),");
        processSync.append(aux);
        
        
        for(String node : alphabetNode.keySet()) {
            processSync.append("ProcessDiagram_" + nameDiagram + "(" + node + terminationAlphabet + ") = normal(");//TODO realmente precisa de tudo isso?
            processSync.append(node + termination + ")\n");
        }

        processSync.append("Node_" + nameDiagram + " = || x:alphabet_" + nameDiagram + " @ [AlphabetDiagram_" + nameDiagram + "(x)] ProcessDiagram_" + nameDiagram + "(x)\n");

        return processSync.toString();
    }
}
