package com.ref.parser.activityDiagram;

import com.change_vision.jude.api.inf.model.IActivity;
import com.change_vision.jude.api.inf.model.IActivityNode;
import com.change_vision.jude.api.inf.model.IFlow;

import java.util.ArrayList;
import java.util.HashMap;

public class ADDefineFinalNode {

    private IActivity ad;
    private HashMap<String, ArrayList<String>> alphabetNode;
    private HashMap<String, String> syncChannelsEdge;
    private HashMap<String, String> syncObjectsEdge;
    private HashMap<String, String> objectEdges;
    private ADUtils adUtils;

    public ADDefineFinalNode(IActivity ad, HashMap<String, ArrayList<String>> alphabetNode, HashMap<String, String> syncChannelsEdge,
                             HashMap<String, String> syncObjectsEdge, HashMap<String, String> objectEdges, ADUtils adUtils) {
        this.ad = ad;
        this.alphabetNode = alphabetNode;
        this.syncChannelsEdge = syncChannelsEdge;
        this.syncObjectsEdge = syncObjectsEdge;
        this.objectEdges = objectEdges;
        this.adUtils = adUtils;
    }

    public IActivityNode defineFinalNode(IActivityNode activityNode, StringBuilder nodes) {
        StringBuilder finalNode = new StringBuilder();
        ArrayList<String> alphabet = new ArrayList<>();
        String nameFinalNode = adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName());
        String nameFinalNodeTermination = adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + "_t";
        String endDiagram = "END_DIAGRAM_" + adUtils.nameDiagramResolver(ad.getName());
        HashMap<String, String> nameObjects = new HashMap<>();
        IFlow[] inFlows = activityNode.getIncomings();

        finalNode.append(nameFinalNode + "(id) = ");

        ArrayList<String> ceInitials = new ArrayList<>();
        for (int i = 0; i < inFlows.length; i++) {
            ceInitials.add(inFlows[i].getId());

            if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
                String ceIn2 = syncObjectsEdge.get(inFlows[i].getId());
                nameObjects.put(inFlows[i].getId(), objectEdges.get(ceIn2));
            }

        }

        finalNode.append("(");
        for (int i = 0; i < ceInitials.size(); i++) {
            String ceIn = syncChannelsEdge.get(ceInitials.get(i));    //get the parallel input channels
            String oeIn = syncObjectsEdge.get(ceInitials.get(i));

            if (ceIn != null) {
                finalNode.append("(");

                if (i >= 0 && i < ceInitials.size() - 1) {
                    adUtils.ce(alphabet, finalNode, ceIn, " -> SKIP) [] ");
                } else {
                    adUtils.ce(alphabet, finalNode, ceIn, " -> SKIP)");
                }
            } else {

                String nameObject = nameObjects.get(ceInitials.get(i));

                finalNode.append("(");

                if (i >= 0 && i < ceInitials.size() - 1) {
                    adUtils.ce(alphabet, finalNode, oeIn, "?" + nameObject + " -> SKIP) [] ");
                } else {
                    adUtils.ce(alphabet, finalNode, oeIn, "?" + nameObject + " -> SKIP)");
                }
            }

        }

        finalNode.append("); ");

        adUtils.clear(alphabet, finalNode);

        finalNode.append("SKIP\n");

        finalNode.append(nameFinalNodeTermination + "(id) = ");
        finalNode.append(nameFinalNode + "(id) /\\ " + endDiagram + "(id)\n");

        alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName())+".id");
        alphabetNode.put(adUtils.nameDiagramResolver(activityNode.getName()), alphabet);

        activityNode = null;

        nodes.append(finalNode.toString());

        return activityNode;
    }
}
