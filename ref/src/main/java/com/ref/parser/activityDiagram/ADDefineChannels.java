package com.ref.parser.activityDiagram;

import com.change_vision.jude.api.inf.model.IActivity;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ADDefineChannels {

    private HashMap<String,Integer> allGuards;
    private IActivity ad;
    private HashMap<String, String> parameterNodesInput;        //name; type
    private HashMap<String, String> parameterNodesOutput;
    private List<Pair<String, String>> memoryLocal;             //nameNode, nameObject
    private HashMap<String, String> parameterNodesOutputObject; //name; object
    private HashMap<String, String> syncObjectsEdge;
    private HashMap<String, String> objectEdges;                //channel; name
    private List<String> eventChannel;
    private List<String> lockChannel;
    private String firstDiagram;
    private List<String> signalChannels;
    private ADUtils adUtils;
    private ADParser adParser;

    public ADDefineChannels(HashMap allGuards, IActivity ad, HashMap parameterNodesInput, HashMap parameterNodesOutput,
                            List memoryLocal, HashMap parameterNodesOutputObject, HashMap syncObjectsEdge,
                            HashMap objectEdges, List eventChannel, List lockChannel, String firstDiagram, List signalChannels,
                            ADUtils adUtils, ADParser adParser) {
        this.allGuards = allGuards;
        this.ad = ad;
        this.parameterNodesInput = parameterNodesInput;
        this.parameterNodesOutput = parameterNodesOutput;
        this.memoryLocal = memoryLocal;
        this.parameterNodesOutputObject = parameterNodesOutputObject;
        this.syncObjectsEdge = syncObjectsEdge;
        this.objectEdges = objectEdges;
        this.eventChannel = eventChannel;
        this.lockChannel = lockChannel;
        this.firstDiagram = firstDiagram;
        this.signalChannels = signalChannels;
        this.adUtils = adUtils;
        this.adParser = adParser;
    }

    public String defineChannels() {
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

            for (String input : parameterNodesInput.keySet()) {
                channels.append("." + input + "_" + nameDiagram);
            }

            channels.append("\n");

        } else {
            channels.append("channel startActivity_" + nameDiagram + ": ID_" + nameDiagram + "\n");
        }

        if (parameterNodesOutput.size() > 0) {
            channels.append("channel endActivity_" + nameDiagram + ": ID_" + nameDiagram);

            for (String output : parameterNodesOutput.keySet()) {
                channels.append("." + output + "_" + nameDiagram);
            }

            channels.append("\n");

        } else {
            channels.append("channel endActivity_" + nameDiagram + ": ID_" + nameDiagram + "\n");
        }

        if (parameterNodesInput.size() > 0 || parameterNodesOutput.size() > 0 || memoryLocal.size() > 0) {

            for (String get : parameterNodesInput.keySet()) {
                channels.append("channel get_" + get + "_" + nameDiagram + ": countGet_" + nameDiagram + "." + get + "_" + nameDiagram + "\n");
            }

            for (String get : parameterNodesOutput.keySet()) {
                String object = parameterNodesOutputObject.get(get);

                if (object == null) {
                    object = get;
                }

                channels.append("channel get_" + get + "_" + nameDiagram + ": countGet_" + nameDiagram + "." + object + "_" + nameDiagram + "\n");
            }

            for (Pair<String, String> pair : memoryLocal) {
                channels.append("channel get_" + pair.getValue() + "_" + pair.getKey() + "_" + nameDiagram + ": countGet_" + nameDiagram + "." + pair.getValue() + "_" + nameDiagram + "\n");
            }

            for (String set : parameterNodesInput.keySet()) {
                channels.append("channel set_" + set + "_" + nameDiagram + ": countSet_" + nameDiagram + "." + set + "_" + nameDiagram + "\n");
            }

            for (String set : parameterNodesOutput.keySet()) {
                String object = parameterNodesOutputObject.get(set);

                if (object == null) {
                    object = set;
                }
                channels.append("channel set_" + set + "_" + nameDiagram + ": countSet_" + nameDiagram + "." + object + "_" + nameDiagram + "\n");
            }

            for (Pair<String, String> pair : memoryLocal) {
                channels.append("channel set_" + pair.getValue() + "_" + pair.getKey() + "_" + nameDiagram + ": countSet_" + nameDiagram + "." + pair.getValue() + "_" + nameDiagram + "\n");
            }

        }

        if (adParser.countCe_ad > 1) {
            channels.append("channel ce_" + nameDiagram + ": countCe_" + nameDiagram + "\n");
        }

        if (syncObjectsEdge.size() > 0) {
            ArrayList<String> allObjectEdges = new ArrayList<>();
            for (String objectEdge : syncObjectsEdge.values()) {    //get sync channel
                String nameParamater = objectEdges.get(objectEdge);

                if (!allObjectEdges.contains(nameParamater)) {
                    allObjectEdges.add(nameParamater);
                    channels.append("channel oe_" + nameParamater + "_" + nameDiagram + ": countOe_" + nameDiagram + "." + nameParamater + "_" + nameDiagram + "\n");
                }
            }

        }

        channels.append("channel clear_" + nameDiagram + ": countClear_" + nameDiagram + "\n");

        channels.append("channel update_" + nameDiagram + ": countUpdate_" + nameDiagram + ".limiteUpdate_" + nameDiagram + "\n");

        channels.append("channel endDiagram_" + nameDiagram + "\n");

        if (eventChannel.size() > 0) {
            channels.append("channel ");

            for (int i = 0; i < eventChannel.size(); i++) {
                channels.append(eventChannel.get(i));

                if ((i + 1) < eventChannel.size()) {
                    channels.append(",");
                }
            }

            channels.append("\n");
        }

        if (lockChannel.size() > 0) {
            channels.append("channel ");

            for (int i = 0; i < lockChannel.size(); i++) {
                channels.append("lock_" + lockChannel.get(i));

                if ((i + 1) < lockChannel.size()) {
                    channels.append(",");
                }
            }

            channels.append(": T\n");
        }

        if (firstDiagram.equals(ad.getId())) {

            for (String signalChannel : signalChannels) {
                channels.append("channel " + signalChannel + ": countSignal_" + signalChannel + ".countAccept_" + signalChannel + "\n");
            }

            channels.append("channel loop\n");
            channels.append("channel dc\n");
        }

        return channels.toString();
    }
}
