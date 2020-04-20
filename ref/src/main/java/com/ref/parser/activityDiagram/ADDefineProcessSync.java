package com.ref.parser.activityDiagram;

import com.change_vision.jude.api.inf.model.IAction;
import com.change_vision.jude.api.inf.model.IActivity;
import com.change_vision.jude.api.inf.model.IActivityNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


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
            /*for(IActivity CBAs:ADParser.callBehaviourList) {//TODO resolver hashmap
            	if(ADUtils.nameResolver(CBAs.getName()).equals(node)) {//se for um cba
            		ehCBA = true;
            	}
            }*/
            IActivityNode Activitynode = findCBANode(node);
            if(Activitynode != null) {
        		processSync.append("AlphabetDiagram_" + nameDiagram + "(id," + node + terminationAlphabet + ") = union({|");
                alphabetDiagram.append("AlphabetDiagram_" + nameDiagram + "(id," + node + terminationAlphabet + ")"+"SUB");
                for (int i = 0; i < alphabet.size(); i++) {
                    processSync.append(alphabet.get(i));
                    if (i < alphabet.size() - 1) {
                        processSync.append(",");
                    }
                }
                List<Pair<String,String>> CBAList = ADParser.countcallBehavior.get(((IAction) Activitynode).getCallingActivity().getId());//pega a list com todos os nos que chamam esse cba
            	int index = 1;
            	for(int i=0;i<CBAList.size();i++) {//varre a lista atrás do indice desse nó
            		if(Activitynode.getId().equals(CBAList.get(i).getKey())) {
            			index = i+1;
            		}
            	}
                processSync.append("|},AlphabetDiagram_"+((IAction)Activitynode).getCallingActivity().getName()+"_t("+index+"))\n");
        	}else {
        		processSync.append("AlphabetDiagram_" + nameDiagram + "(id," + node + terminationAlphabet + ") = {|");
                alphabetDiagram.append("AlphabetDiagram_" + nameDiagram + "(id," + node + terminationAlphabet + ")"+"SUB");
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
   
        processSync.append("AlphabetDiagram_" + nameDiagram +"_t(id) = ");
        for(int i = 1; i< alphabetNode.size();i++) {
        	processSync.append("union(");
        }
        alphabetDiagram.replace(alphabetDiagram.indexOf("SUB"), alphabetDiagram.indexOf("SUB")+3,",");
        alphabetDiagram.replace(alphabetDiagram.lastIndexOf("SUB"), alphabetDiagram.lastIndexOf("SUB")+3,")\n\n");
        String aux = alphabetDiagram.toString().replaceAll("SUB", "),");
        processSync.append(aux);
        
        
        for(String node : alphabetNode.keySet()) {
            processSync.append("ProcessDiagram_" + nameDiagram + "(id," + node + terminationAlphabet + ") = normal(");
            processSync.append(node + termination + "(id))\n");
        }

        processSync.append("Node_" + nameDiagram + "(id) = || x:alphabet_" + nameDiagram + " @ [AlphabetDiagram_" + nameDiagram + "(id,x)] ProcessDiagram_" + nameDiagram + "(id,x)\n");

        return processSync.toString();
    }
    
    public IActivityNode findCBANode(String nodeName) {
    	IActivityNode[] nodes = ad.getActivityNodes();//pega todos os nós do diagrama
    	IAction nodeFound;
		for(int i=0; i<nodes.length;i++) {//varre os nós
			if(nodes[i].getName().equals(nodeName) && nodes[i] instanceof IAction) {
				nodeFound = (IAction) nodes[i];
				if(nodeFound.isCallBehaviorAction()) {
					return nodeFound;
				}
			}
		}
		return null;
	}
}
