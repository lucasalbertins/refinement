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

        for(String node : alphabetNode.keySet()) {
            ArrayList<String> alphabet = alphabetNode.get(node);
            processSync.append("AlphabetDiagram_" + nameDiagram + "(" + node + terminationAlphabet + ") = {|");
            for (int i = 0; i < alphabet.size(); i++) {
                processSync.append(alphabet.get(i));
                if (i < alphabet.size() - 1) {
                    processSync.append(",");
                }
            }
            processSync.append("|}\n");
        }

        for(String node : alphabetNode.keySet()) {
            processSync.append("ProcessDiagram_" + nameDiagram + "(" + node + terminationAlphabet + ") = ");
            processSync.append(node + termination + "\n");
        }

        processSync.append("Node_" + nameDiagram + " = || x:alphabet_" + nameDiagram + " @ [AlphabetDiagram_" + nameDiagram + "(x)] ProcessDiagram_" + nameDiagram + "(x)\n");

        return processSync.toString();
    }
}
