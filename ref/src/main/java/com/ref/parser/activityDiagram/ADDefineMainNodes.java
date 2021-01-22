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
    //private List<Pair<String, Integer>> callBehaviourNumber;
    private HashMap<String, List<String>> callBehaviourInputs;
    //private List<String> localSignalChannelsSync;
    //private List<String> signalChannelsLocal;
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
        //this.callBehaviourNumber = callBehaviourNumber;
        this.callBehaviourInputs = callBehaviourInputs;
        //this.localSignalChannelsSync = localSignalChannelsSync;
        //this.signalChannelsLocal = signalChannelsLocal;
        this.adUtils = adUtils;
        this.adParser = adParser;
    }

    public String defineMainNodes() {
        StringBuilder mainNode = new StringBuilder();
        String nameDiagram = adUtils.nameDiagramResolver(ad.getName());
        ArrayList<String> alphabet = new ArrayList<>();

        if (firstDiagram.equals(ad.getId())) {
            mainNode.append("MAIN = normal(" + nameDiagram + "(1))\n");
        }
        
//        if (firstDiagram.equals(ad.getId())) {
//            mainNode.append("MAIN = normal(" + nameDiagram + "(1)); LOOP\n");
//            mainNode.append("LOOP = loop -> LOOP\n");
//        }

        mainNode.append("END_DIAGRAM_" + nameDiagram + "(id) = endDiagram_" + nameDiagram + ".id -> SKIP\n");
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

        /*for (int i = 0; i < callBehaviourNumber.size(); i++) {
            mainNode.append("(");
        }*/

        //for (int i = 0; i < signalChannelsLocal.size(); i++) {
//            if(firstDiagram.equals(ad.getId()) && ADParser.alphabetPool.size() > 0) {
//            	mainNode.append("(");
//            }
        //}

        mainNode.append("Internal_" + nameDiagram + "(ID_" + nameDiagram + ")");

        /*for (Pair<String, Integer> callBehaviourAD : callBehaviourNumber) {
            mainNode.append(" [|{|startActivity_" + callBehaviourAD.getKey() + "." + callBehaviourAD.getValue() +
                    ",endActivity_" + callBehaviourAD.getKey() + "." + callBehaviourAD.getValue());

            mainNode.append("|}|] ");

            mainNode.append(callBehaviourAD.getKey() + "(" + callBehaviourAD.getValue() + "))");
        }*/

        mainNode.append(" [|{|update_" + nameDiagram + ",clear_" + nameDiagram + ",endDiagram_" + nameDiagram + "|}|] ");
        mainNode.append("TokenManager_" + nameDiagram + "_t(ID_"+nameDiagram+",0,0))");
        
//        if(firstDiagram.equals(ad.getId()) && ADParser.alphabetPool.size() > 0) {//se for o 1 diagrama
//        	mainNode.append("[|AlphabetPool|]pools(ID_"+nameDiagram+"))");
//        }
        
        /*if(signalChannelsLocal.size()> 0) {
	        StringBuilder alfabetoPools = new StringBuilder();
	        for(String signal: signalChannelsLocal) {
	        	alfabetoPools.append("signal_"+signal+",accept_"+signal+",");
	        }
	        alfabetoPools.append("endDiagram_"+nameDiagram);
	        mainNode.append(" [|{|"+alfabetoPools+"|}|]pools)");
	        
        	
	        for(String signal: signalChannelsLocal) {
		        if (!ADParser.alphabetPool.contains(signal)) {
					ADParser.alphabetPool.add("signal_"+signal+",accept_"+signal+",");
					
				}
	        }
	        if(!ADParser.alphabetPool.contains("endDiagram_"+nameDiagram)) {
	        	ADParser.alphabetPool.add("endDiagram_"+nameDiagram);
	        }
        }*/
        
        /*for (String signal: signalChannelsLocal) {
            mainNode.append(" [|{|");
            mainNode.append("signal_" + signal + "," + "accept_" + signal + "," + "endDiagram_" + nameDiagram);
            mainNode.append("|}|] pool_" + signal + "_t(<>))");
        }*//////

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
            StringBuilder getSet = new StringBuilder();
            for (String input : parameterNodesInput.keySet()) {
            	getSet.append("get_" + input + "_" + nameDiagram + ",set_" + input +"_" + nameDiagram+",");
                mainNode.append("get_" + input + "_" + nameDiagram + ",");
                mainNode.append("set_" + input + "_" + nameDiagram + ",");
            }

            for (String output : parameterNodesOutput.keySet()) {
            	getSet.append("get_" + output + "_" + nameDiagram + ",set_" + output +"_" + nameDiagram+",");
                mainNode.append("get_" + output + "_" + nameDiagram + ",");
                mainNode.append("set_" + output + "_" + nameDiagram + ",");
            }       
        	mainNode.append("endActivity_" + nameDiagram + "|}|] ");          
            
            if(!parameterNodesInput.isEmpty() || !parameterNodesOutput.isEmpty()) {
            	getSet.replace(getSet.lastIndexOf(","), getSet.lastIndexOf(",")+1,"");//tira a ultima ,
            	mainNode.append("Mem_" + nameDiagram + "(ID_"+nameDiagram+")) \\{|"+getSet+"|}\n");
            	
            }else {
            	mainNode.append("Mem_" + nameDiagram + "(ID_"+nameDiagram+"))\n");
            }
        } else if (lockChannel.size() > 0) {
            mainNode.append("Lock_" + nameDiagram + ")\n");
        } else {
            mainNode.append("\n");
        }

        mainNode.append("Internal_" + nameDiagram + "(id) = ");
        mainNode.append("StartActivity_" + nameDiagram + "(id); Node_" + nameDiagram + "(id); EndActivity_" + nameDiagram + "(id)\n");


        mainNode.append("StartActivity_" + nameDiagram + "(id) = ");
        mainNode.append("startActivity_" + nameDiagram + ".id");

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


        mainNode.append("EndActivity_" + nameDiagram + "(id) = ");

        if (parameterNodesOutput.size() > 0) {
            for (String input : parameterNodesOutput.keySet()) {
                adUtils.get(alphabet, mainNode, input);
            }

            mainNode.append("endActivity_" + nameDiagram + ".id");

            for (String output : parameterNodesOutput.keySet()) {
                mainNode.append("!" + output);
            }

            mainNode.append(" -> SKIP");
        } else {
            mainNode.append("endActivity_" + nameDiagram + ".id -> SKIP");
        }

        mainNode.append("\n");
        return mainNode.toString();
    }
}
