package com.ref.parser.activityDiagram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ref.exceptions.ParsingException;
import com.ref.interfaces.activityDiagram.IActivity;

public class ADDefineChannels {

    private HashMap<String,Integer> allGuards;
    private IActivity ad;
    private HashMap<String, String> parameterNodesInput;        //name; type
    private HashMap<String, String> parameterNodesOutput;
    private Map<Pair<String, String>,String> memoryLocal;             //nameNode, nameObject
    private HashMap<String, String> parameterNodesOutputObject; //name; object
    private HashMap<Pair<IActivity, String>, String> syncObjectsEdge;
    private HashMap<String, String> objectEdges;                //channel; name
    private List<String> eventChannel;
    private String firstDiagram;
    private HashMap<String, List<IActivity>> signalChannels;
    private ADUtils adUtils;
    private ADParser adParser;    
	////////////////////////////////////////////////////////////////////////////////////////
	private List<String> alphabetAstah = new ArrayList<String>();
	////////////////////////////////////////////////////////////////////////////////////////

    public ADDefineChannels(HashMap<String, Integer> allGuards, IActivity ad, HashMap<String, String> parameterNodesInput, HashMap<String, String> parameterNodesOutput,
                            Map<Pair<String, String>, String> memoryLocal, HashMap<String, String> parameterNodesOutputObject, HashMap<Pair<IActivity, String>, String> syncObjectsEdge2,
                            HashMap<String, String> objectEdges, List<String> eventChannel, List<?> lockChannel, String firstDiagram, HashMap<String, List<IActivity>> signalChannels2,
                            ADUtils adUtils, ADParser adParser) {
        this.allGuards = allGuards;
        this.ad = ad;
        this.parameterNodesInput = parameterNodesInput;
        this.parameterNodesOutput = parameterNodesOutput;
        this.memoryLocal = memoryLocal;
        this.parameterNodesOutputObject = parameterNodesOutputObject;
        this.syncObjectsEdge = syncObjectsEdge2;
        this.objectEdges = objectEdges;
        this.eventChannel = eventChannel;
        this.firstDiagram = firstDiagram;
        this.signalChannels = signalChannels2;
        this.adUtils = adUtils;
        this.adParser = adParser;
    }

    public String defineChannels() throws ParsingException {
        StringBuilder channels = new StringBuilder();
        String nameDiagram = adUtils.nameDiagramResolver(ad.getName());

        for (String guard : allGuards.keySet()) {
            channels.append("channel " + guard + ": ");
            for (int i = 0; i < allGuards.get(guard); i++) {
                if (i > 0) {
                    channels.append(".Bool");
                } else {
                    channels.append("Bool");
                }
            }
            channels.append("\n");
        }

        if (parameterNodesInput.size() > 0) {
            channels.append("channel startActivity_" + nameDiagram + ": ID_" + nameDiagram);

            for (String input : parameterNodesInput.values()) {
                channels.append("." + input + "_" + nameDiagram);
            }

            channels.append("\n");

        } else {
            channels.append("channel startActivity_" + nameDiagram + ": ID_" + nameDiagram + "\n");
        }
		////////////////////////////////////////////////////////////////////////////////////////
		alphabetAstah.add("startActivity_" + nameDiagram);
		////////////////////////////////////////////////////////////////////////////////////////

        if (parameterNodesOutput.size() > 0) {
            channels.append("channel endActivity_" + nameDiagram + ": ID_" + nameDiagram);

            for (String output : parameterNodesOutput.values()) {
                channels.append("." + output + "_" + nameDiagram);
            }

            channels.append("\n");

        } else {
            channels.append("channel endActivity_" + nameDiagram + ": ID_" + nameDiagram + "\n");
        }
		////////////////////////////////////////////////////////////////////////////////////////
		alphabetAstah.add("endActivity_" + nameDiagram);
		////////////////////////////////////////////////////////////////////////////////////////

        if (parameterNodesInput.size() > 0 || parameterNodesOutput.size() > 0 || memoryLocal.size() > 0) {

            for (String in : parameterNodesInput.keySet()) {
                channels.append("channel get_" + in + "_" + nameDiagram + ": ID_"+nameDiagram +".countGet_" + nameDiagram + "." + parameterNodesInput.get(in) + "_" + nameDiagram + "\n");
                channels.append("channel set_" + in + "_" + nameDiagram + ": ID_"+nameDiagram +".countSet_" + nameDiagram + "." + parameterNodesInput.get(in) + "_" + nameDiagram + "\n");
//                channels.append("channel get_" + in + "_" + nameDiagram + ": ID_"+nameDiagram +".countGet_" + nameDiagram + "." + parameterNodesInput.get(in) + "\n");
//                channels.append("channel set_" + in + "_" + nameDiagram + ": ID_"+nameDiagram +".countSet_" + nameDiagram + "." + parameterNodesInput.get(in) + "\n");
            }

            for (String out : parameterNodesOutput.keySet()) {
                String object = parameterNodesOutputObject.get(out);

                if (object == null) {
                    throw new ParsingException("Parameter node " + out + " is untyped.");
                }

                channels.append("channel get_" + out + "_" + nameDiagram + ": ID_"+nameDiagram +".countGet_" + nameDiagram + "." + object + "_" + nameDiagram + "\n");
                channels.append("channel set_" + out + "_" + nameDiagram + ": ID_"+nameDiagram +".countSet_" + nameDiagram + "." + object + "_" + nameDiagram + "\n");
//                channels.append("channel get_" + out + "_" + nameDiagram + ": ID_"+nameDiagram +".countGet_" + nameDiagram + "." + object + "\n");
//                channels.append("channel set_" + out + "_" + nameDiagram + ": ID_"+nameDiagram +".countSet_" + nameDiagram + "." + object + "\n");
            }

            for (Pair<String, String> pair : memoryLocal.keySet()) {
            	channels.append("channel get_" + pair.getValue() + "_" + pair.getKey() + "_" + nameDiagram + ": ID_"+nameDiagram +".countGet_" + nameDiagram + "." + memoryLocal.get(pair) + "_" + nameDiagram + "\n");
            	channels.append("channel set_" + pair.getValue() + "_" + pair.getKey() + "_" + nameDiagram + ": ID_"+nameDiagram +".countSet_" + nameDiagram + "." + memoryLocal.get(pair) + "_" + nameDiagram + "\n");
//                channels.append("channel get_" + pair.getValue() + "_" + pair.getKey() + "_" + nameDiagram + ": ID_"+nameDiagram +".countGet_" + nameDiagram + "." + memoryLocal.get(pair) + "\n");
//                channels.append("channel set_" + pair.getValue() + "_" + pair.getKey() + "_" + nameDiagram + ": ID_"+nameDiagram +".countSet_" + nameDiagram + "." + memoryLocal.get(pair) + "\n");
            }

        }

        if (adParser.countCe_ad > 1) {
            channels.append("channel ce_" + nameDiagram + ": ID_"+nameDiagram +".countCe_" + nameDiagram + "\n");
        }
        
		////////////////////////////////////////////////////////////////////////////////////////
		alphabetAstah.add("ce_" + nameDiagram);
		////////////////////////////////////////////////////////////////////////////////////////

        if (syncObjectsEdge.size() > 0) {
            ArrayList<String> allObjectEdges = new ArrayList<>();
            for (String objectEdge : syncObjectsEdge.values()) {    //get sync channel
                         	
            	
            	String type = objectEdges.get(objectEdge);
            	objectEdge = objectEdge.substring(0, objectEdge.length()-3);

                if (!allObjectEdges.contains(type)) {
                    allObjectEdges.add(type);
                }
                channels.append("channel " + objectEdge + ": ID_"+nameDiagram +"." + type + "_" + nameDiagram + "\n");
//                channels.append("channel " + objectEdge + ": ID_"+nameDiagram +"." + type + "\n");
                alphabetAstah.add(objectEdge);
            }

        }

        channels.append("channel clear_" + nameDiagram + ": ID_"+nameDiagram +".countClear_" + nameDiagram + "\n");

        channels.append("channel update_" + nameDiagram + ": ID_"+nameDiagram +".countUpdate_" + nameDiagram + ".limiteUpdate_" + nameDiagram + "\n");

        channels.append("channel endDiagram_" + nameDiagram +": ID_"+nameDiagram +"\n");
        
		////////////////////////////////////////////////////////////////////////////////////////
		alphabetAstah.add("clear_" + nameDiagram);
		alphabetAstah.add("update_" + nameDiagram);
		alphabetAstah.add("endDiagram_" + nameDiagram);  
		////////////////////////////////////////////////////////////////////////////////////////

        if (eventChannel.size() > 0) {
            channels.append("channel ");

            for (int i = 0; i < eventChannel.size(); i++) {
                channels.append(eventChannel.get(i));

                if ((i + 1) < eventChannel.size()) {
                    channels.append(",");
                }
            }

            channels.append(": ID_"+nameDiagram+"\n");
        }

        if (firstDiagram.equals(ad.getId())) {
        	List<String> keySignalChannels = new ArrayList<String>();
        	keySignalChannels.addAll(signalChannels.keySet());
        	
        	List<List<IActivity>> entry = new ArrayList<>();
        	entry.addAll(signalChannels.values());
        	String nameMax = nameDiagram;
        	int numMax = 0;
        	for(List<IActivity> valueList : entry) {
        		for(IActivity diagram : valueList) {
        			if(ADParser.countCall.get(ADUtils.nameResolver(diagram.getName()))>numMax) {
            			nameMax = ADUtils.nameResolver(diagram.getName());
            			numMax = ADParser.countCall.get(ADUtils.nameResolver(diagram.getName()));
            		}
        		}        		
        	}
        	
//            for (String signalChannel : keySignalChannels) {
//                channels.append("channel signal_" + signalChannel + ": ID_"+nameMax +". countSignal_" + signalChannel + "\n");
//                channels.append("channel accept_" + signalChannel + ": ID_"+nameMax +". countAccept_" + signalChannel + ".countSignal_" + signalChannel +"\n");
//            }

//            channels.append("channel loop\n");
            channels.append("channel dc\n");
    
////////////////////////////////////////////////////////////////////////////////////////
    	String partitionName;            
    	try {
    		partitionName = this.ad.getPartitions()[0].getSubPartitions()[0].getName();				
		} catch (Exception e) {
			throw new ParsingException("The module should have a partition. \n Please, insert and try again.");
		}            

    	alphabetAstah.add("dc");  

    	if (adParser.countUntil_ad > 0) {
    		channels.append("channel begin, end:  {1.." + adParser.countUntil_ad + "}\n");				
    	}
    	if (adParser.countAny_ad > 0) {
    		channels.append("channel chaos:  {1.." + adParser.countAny_ad + "}\n");				
		}
    }
//        if (firstDiagram.equals(ad.getId())) {
        	channels.append("\nalphabet_Astah_" + ADUtils.nameResolver(ad.getName()) + " = {| ");
        	for (int i = 0; i < alphabetAstah.size(); i++) {
        		channels.append(alphabetAstah.get(i));
        		
        		if ((i + 1) < alphabetAstah.size()) {
        			channels.append(", ");
        		} else {
        			channels.append(" |}\n\n");
        		}
        	}
//        }

////////////////////////////////////////////////////////////////////////////////////////
        return channels.toString();
    }
}