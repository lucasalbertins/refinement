package com.ref.parser.activityDiagram;

import com.change_vision.jude.api.inf.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ADDefineJoin {

    private IActivity ad;

    private HashMap<String, ArrayList<String>> alphabetNode;
    private HashMap<String, String> syncChannelsEdge;
    private HashMap<String, String> syncObjectsEdge;
    private HashMap<String, String> objectEdges;
    private HashMap<String, String> parameterNodesInput;
    private List<ArrayList<String>> unionList;
    private HashMap<String, String> typeUnionList;
    private ADUtils adUtils;
    private ADParser adParser;

    public ADDefineJoin(IActivity ad, HashMap<String, ArrayList<String>> alphabetNode, HashMap<String, String> syncChannelsEdge,
                        HashMap<String, String> syncObjectsEdge, HashMap<String, String> objectEdges, HashMap<String, String> parameterNodesInput,
                        List<ArrayList<String>> unionList, HashMap<String, String> typeUnionList, ADUtils adUtils, ADParser adParser) {
        this.ad = ad;
        this.alphabetNode = alphabetNode;
        this.syncChannelsEdge = syncChannelsEdge;
        this.syncObjectsEdge = syncObjectsEdge;
        this.objectEdges = objectEdges;
        this.parameterNodesInput = parameterNodesInput;
        this.unionList = unionList;
        this.typeUnionList = typeUnionList;
        this.adUtils = adUtils;
        this.adParser = adParser;
    }

    public IActivityNode defineJoin(IActivityNode activityNode, StringBuilder nodes, int code) {
        StringBuilder joinNode = new StringBuilder();
        ArrayList<String> alphabet = new ArrayList<>();
        String nameJoin = adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName());
        String nameJoinTermination = adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + "_t";
        String endDiagram = "END_DIAGRAM_" + adUtils.nameDiagramResolver(ad.getName());
        IFlow[] outFlows = activityNode.getOutgoings();
        IFlow[] inFlows = activityNode.getIncomings();
        HashMap<String, String> nameObjects = new HashMap<>();
        List<String> objects = new ArrayList<>();
        String typeObject = null;
        List<String> nameObjectAdded = new ArrayList<>();
        boolean syncBool = false;
        boolean sync2Bool = false;

        if (code == 0) {
            ArrayList<String> ceInitials = new ArrayList<>();
            for (int i = 0; i < inFlows.length; i++) {
                ceInitials.add(inFlows[i].getId());
                if (syncChannelsEdge.containsKey(inFlows[i].getId())) {
                    syncBool = true;
                }

                if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
                    String ceIn2 = syncObjectsEdge.get(inFlows[i].getId());
                    //nameObject = objectEdges.get(ceIn2);

                    typeObject = ((IObjectNode) inFlows[i].getSource()).getBase().getName();

                    nameObjects.put(inFlows[i].getId(), typeObject);
                    sync2Bool = true;
                }
            }

            joinNode.append(nameJoin + " = (");

            for (int i = 0; i < ceInitials.size(); i++) {
                String ceIn = syncChannelsEdge.get(ceInitials.get(i));    //get the parallel input channels
                String oeIn = syncObjectsEdge.get(ceInitials.get(i));

                if (ceIn != null) {
                    joinNode.append("(");

                    if (i >= 0 && i < ceInitials.size() - 1) {
                        adUtils.ce(alphabet, joinNode, ceIn, " -> SKIP) ||| ");
                    } else {
                        adUtils.ce(alphabet, joinNode, ceIn, " -> SKIP)");
                    }
                } else {

                    typeObject = nameObjects.get(ceInitials.get(i));

                    if (!objects.contains(typeObject)) {
                        objects.add(typeObject);
                    }

                    joinNode.append("(");

                    if (i >= 0 && i < ceInitials.size() - 1) {
                        adUtils.ce(alphabet, joinNode, oeIn, "?" + typeObject + " -> ");
                        adUtils.setLocalInput(alphabet, joinNode, typeObject, adUtils.nameDiagramResolver(activityNode.getName()), typeObject, oeIn);
                        joinNode.append("SKIP) ||| ");
                    } else {
                        adUtils.ce(alphabet, joinNode, oeIn, "?" + typeObject + " -> ");
                        adUtils.setLocalInput(alphabet, joinNode, typeObject, adUtils.nameDiagramResolver(activityNode.getName()), typeObject, oeIn);
                        joinNode.append("SKIP)");
                    }
                }

            }

            joinNode.append("); ");

            adUtils.update(alphabet, joinNode, inFlows.length, outFlows.length, false);

            if (sync2Bool) {
                for (String nameObjectOut : objects) {
                    adUtils.getLocal(alphabet, joinNode, nameObjectOut, adUtils.nameDiagramResolver(activityNode.getName()), nameObjectOut);
                }
            }

            joinNode.append("(");

            typeObject = "";

            for (int i = 0; i < inFlows.length; i++) {
                String channel = syncObjectsEdge.get(inFlows[i].getId());
                if (objectEdges.get(channel) != null && !nameObjectAdded.contains(objectEdges.get(channel))) {
                    nameObjectAdded.add(objectEdges.get(channel));
                    typeObject += objectEdges.get(channel);
                }
            }

            if (sync2Bool) {
                for (int i = 0; i < objects.size(); i++) {    //creates the parallel output channels
                    String oe = adUtils.createOE(typeObject);
                    syncObjectsEdge.put(outFlows[0].getId(), oe);    //just one output
                    objectEdges.put(oe, typeObject);
                    joinNode.append("(");

                    if (i >= 0 && i < objects.size() - 1) {
                        adUtils.ce(alphabet, joinNode, oe, "!" + objects.get(i) + " -> SKIP) |~| ");
                        adParser.countOe_ad--;
                    } else {
                        adUtils.ce(alphabet, joinNode, oe, "!" + objects.get(i) + " -> SKIP)");
                    }
                }
            } else if (syncBool) {
                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String ce = adUtils.createCE();
                    syncChannelsEdge.put(outFlows[i].getId(), ce);

                    joinNode.append("(");

                    if (i >= 0 && i < outFlows.length - 1) {
                        adUtils.ce(alphabet, joinNode, ce, " -> SKIP) ||| ");
                    } else {
                        adUtils.ce(alphabet, joinNode, ce, " -> SKIP)");
                    }
                }
            }

            joinNode.append("); ");

            joinNode.append(nameJoin + "\n");

            joinNode.append(nameJoinTermination + " = ");

            for (int i = 0; i < objects.size(); i++) {
                joinNode.append("(");
            }

            joinNode.append("(" + nameJoin + " /\\ " + endDiagram + ")");

            for (int i = 0; i < objects.size(); i++) {    //creates the parallel output channels
                joinNode.append(" [|{|");
                joinNode.append("get_" + objects.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                joinNode.append("set_" + objects.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                joinNode.append("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()) + "|}|] ");
                joinNode.append("Mem_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + "_" + objects.get(i) + "_t(" + adUtils.getDefaultValue(objects.get(i)) + "))");
            }

            if (objects.size() > 0) {
                joinNode.append(" \\{|");

                for (int i = 0; i < objects.size(); i++) {
                    joinNode.append("get_" + objects.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                    joinNode.append("set_" + objects.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()));
                    if (i < objects.size() - 1) {
                        joinNode.append(",");
                    }
                }

                joinNode.append("|}");

            }

            joinNode.append("\n");

            alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()));
            alphabetNode.put(adUtils.nameDiagramResolver(activityNode.getName()), alphabet);

            if (outFlows[0].getTarget() instanceof IInputPin) {
                for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                    if (activityNodeSearch instanceof IAction) {
                        IInputPin[] inPins = ((IAction) activityNodeSearch).getInputs();
                        for (int y = 0; y < inPins.length; y++) {
                            if (inPins[y].getId().equals(outFlows[0].getTarget().getId())) {
                                activityNode = activityNodeSearch;
                            }
                        }
                    }
                }
            } else {
                activityNode = outFlows[0].getTarget();
            }

            nodes.append(joinNode.toString());
        } else if (code == 1) {
            ArrayList<String> ceInitials = new ArrayList<>();
            ArrayList<String> obj = new ArrayList<>();
            for (int i = 0; i < inFlows.length; i++) {
                ceInitials.add(inFlows[i].getId());
                if (syncChannelsEdge.containsKey(inFlows[i].getId())) {
                    syncBool = true;
                }

                if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
                    //String ceIn2 = syncObjectsEdge.get(inFlows[i].getId());
                    //nameObject = objectEdges.get(ceIn2);

                    typeObject = ((IObjectNode) inFlows[i].getSource()).getBase().getName();

                    nameObjects.put(inFlows[i].getId(), typeObject);

                    if (!obj.contains(typeObject)) {
                        obj.add(typeObject);
                    }

                    sync2Bool = true;
                }
            }

            typeObject = "";
            List<String> nodesAdded = new ArrayList<>();

            List<String> nameObjs = new ArrayList<>();
            for (int i = 0; i < inFlows.length; i++) {
                nameObjs.addAll(adUtils.getObjects(inFlows[i], nodesAdded));
            }

            ArrayList<String> union = new ArrayList<>();
            String lastName = "";

            for (String nameObj : nameObjs) {
                if (!nameObjectAdded.contains(nameObj)) {
                    nameObjectAdded.add(nameObj);
                    typeObject += nameObj;
                    union.add(nameObj);
                    lastName = nameObj;
                }
            }

            if (union.size() > 1) {
                unionList.add(union);
                typeUnionList.put(typeObject, parameterNodesInput.get(lastName));
            }

            if (sync2Bool) {
                for (int i = 0; i < obj.size(); i++) {    //creates the parallel output channels
                    String oe = adUtils.createOE(typeObject);
                    syncObjectsEdge.put(outFlows[0].getId(), oe);    //just one output
                    objectEdges.put(oe, typeObject);
                    joinNode.append("(");

                    if (i >= 0 && i < obj.size() - 1) {
                        adUtils.ce(alphabet, joinNode, oe, "!" + obj.get(i) + " -> SKIP) |~| ");
                        adParser.countOe_ad--;
                    } else {
                        adUtils.ce(alphabet, joinNode, oe, "!" + obj.get(i) + " -> SKIP)");
                    }
                }
            } else if (syncBool) {
                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String ce = adUtils.createCE();
                    syncChannelsEdge.put(outFlows[i].getId(), ce);

                    joinNode.append("(");

                    if (i >= 0 && i < outFlows.length - 1) {
                        adUtils.ce(alphabet, joinNode, ce, " -> SKIP) ||| ");
                    } else {
                        adUtils.ce(alphabet, joinNode, ce, " -> SKIP)");
                    }
                }
            }

            if (outFlows[0].getTarget() instanceof IInputPin) {
                for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                    if (activityNodeSearch instanceof IAction) {
                        IInputPin[] inPins = ((IAction) activityNodeSearch).getInputs();
                        for (int y = 0; y < inPins.length; y++) {
                            if (inPins[y].getId().equals(outFlows[0].getTarget().getId())) {
                                activityNode = activityNodeSearch;
                            }
                        }
                    }
                }
            } else {
                activityNode = outFlows[0].getTarget();
            }

        } else if (code == 2) {
            ArrayList<String> ceInitials = new ArrayList<>();
            for (int i = 0; i < inFlows.length; i++) {
                ceInitials.add(inFlows[i].getId());
                if (syncChannelsEdge.containsKey(inFlows[i].getId())) {
                    syncBool = true;
                }

                if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
                    //String oeIn = syncObjectsEdge.get(inFlows[i].getId());
                    //nameObject = objectEdges.get(oeIn);

                    typeObject = ((IObjectNode) inFlows[i].getSource()).getBase().getName();

                    nameObjects.put(inFlows[i].getId(), typeObject);
                    sync2Bool = true;
                }
            }

            joinNode.append(nameJoin + " = (");

            for (int i = 0; i < ceInitials.size(); i++) {
                String ceIn = syncChannelsEdge.get(ceInitials.get(i));    //get the parallel input channels
                String oeIn = syncObjectsEdge.get(ceInitials.get(i));

                if (ceIn != null) {
                    joinNode.append("(");

                    if (i >= 0 && i < ceInitials.size() - 1) {
                        adUtils.ce(alphabet, joinNode, ceIn, " -> SKIP) ||| ");
                    } else {
                        adUtils.ce(alphabet, joinNode, ceIn, " -> SKIP)");
                    }
                } else {

                    typeObject = nameObjects.get(ceInitials.get(i));

                    if (!objects.contains(typeObject)) {
                        objects.add(typeObject);
                    }

                    joinNode.append("(");

                    if (i >= 0 && i < ceInitials.size() - 1) {
                        adUtils.ce(alphabet, joinNode, oeIn, "?" + typeObject + " -> ");
                        adUtils.setLocalInput(alphabet, joinNode, typeObject, adUtils.nameDiagramResolver(activityNode.getName()), typeObject, oeIn);
                        joinNode.append("SKIP) ||| ");
                    } else {
                        adUtils.ce(alphabet, joinNode, oeIn, "?" + typeObject + " -> ");
                        adUtils.setLocalInput(alphabet, joinNode, typeObject, adUtils.nameDiagramResolver(activityNode.getName()), typeObject, oeIn);
                        joinNode.append("SKIP)");
                    }
                }

            }

            joinNode.append("); ");

            adUtils.update(alphabet, joinNode, inFlows.length, outFlows.length, false);

            if (sync2Bool) {
                for (String nameObjectOut : objects) {
                    adUtils.getLocal(alphabet, joinNode, nameObjectOut, adUtils.nameDiagramResolver(activityNode.getName()), nameObjectOut);
                }
            }

            joinNode.append("(");

            if (sync2Bool) {
                for (int i = 0; i < objects.size(); i++) {    //creates the parallel output channels
                    String oe = syncObjectsEdge.get(outFlows[0].getId());    //just one output


                    joinNode.append("(");

                    if (i >= 0 && i < objects.size() - 1) {
                        adUtils.ce(alphabet, joinNode, oe, "!" + objects.get(i) + " -> SKIP) |~| ");
                        //countOe_ad--;
                    } else {
                        adUtils.ce(alphabet, joinNode, oe, "!" + objects.get(i) + " -> SKIP)");
                    }
                }
            } else if (syncBool) {
                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String ce = syncChannelsEdge.get(outFlows[i].getId());

                    joinNode.append("(");

                    if (i >= 0 && i < outFlows.length - 1) {
                        adUtils.ce(alphabet, joinNode, ce, " -> SKIP) ||| ");
                    } else {
                        adUtils.ce(alphabet, joinNode, ce, " -> SKIP)");
                    }
                }
            }

            joinNode.append("); ");

            joinNode.append(nameJoin + "\n");

            joinNode.append(nameJoinTermination + " = ");

            for (int i = 0; i < objects.size(); i++) {
                joinNode.append("(");
            }

            joinNode.append("(" + nameJoin + " /\\ " + endDiagram + ")");

            for (int i = 0; i < objects.size(); i++) {    //creates the parallel output channels
                joinNode.append(" [|{|");
                joinNode.append("get_" + objects.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                joinNode.append("set_" + objects.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                joinNode.append("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()) + "|}|] ");
                joinNode.append("Mem_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + "_" + objects.get(i) + "_t(" + adUtils.getDefaultValue(objects.get(i)) + "))");
            }

            if (objects.size() > 0) {
                joinNode.append(" \\{|");

                for (int i = 0; i < objects.size(); i++) {
                    joinNode.append("get_" + objects.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                    joinNode.append("set_" + objects.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()));
                    if (i < objects.size() - 1) {
                        joinNode.append(",");
                    }
                }

                joinNode.append("|}");

            }

            joinNode.append("\n");

            alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()));
            alphabetNode.put(adUtils.nameDiagramResolver(activityNode.getName()), alphabet);

            if (outFlows[0].getTarget() instanceof IInputPin) {
                for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                    if (activityNodeSearch instanceof IAction) {
                        IInputPin[] inPins = ((IAction) activityNodeSearch).getInputs();
                        for (int y = 0; y < inPins.length; y++) {
                            if (inPins[y].getId().equals(outFlows[0].getTarget().getId())) {
                                activityNode = activityNodeSearch;
                            }
                        }
                    }
                }
            } else {
                activityNode = outFlows[0].getTarget();
            }

            nodes.append(joinNode.toString());
        }

        return activityNode;
    }
}
