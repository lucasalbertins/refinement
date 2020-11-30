package com.ref.parser.activityDiagram;



import java.util.ArrayList;
import java.util.HashMap;

import com.ref.interfaces.activityDiagram.IActivity;
import com.ref.interfaces.activityDiagram.IActivityNode;
import com.ref.interfaces.activityDiagram.IFlow;

public class ADDefineFinalNode {

    private IActivity ad;
    private HashMap<Pair<IActivity, String>, ArrayList<String>> alphabetNode;
    private HashMap<Pair<IActivity, String>, String> syncChannelsEdge;
    private HashMap<Pair<IActivity, String>, String> syncObjectsEdge;
    private ADUtils adUtils;

    public ADDefineFinalNode(IActivity ad, HashMap<Pair<IActivity, String>, ArrayList<String>> alphabetNode2, HashMap<Pair<IActivity, String>, String> syncChannelsEdge2,
                             HashMap<Pair<IActivity, String>, String> syncObjectsEdge2, HashMap<String, String> objectEdges, ADUtils adUtils) {
        this.ad = ad;
        this.alphabetNode = alphabetNode2;
        this.syncChannelsEdge = syncChannelsEdge2;
        this.syncObjectsEdge = syncObjectsEdge2;
        this.adUtils = adUtils;
    }

    public String defineFinalNode(IActivityNode activityNode) {
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
            Pair<IActivity,String> key = new Pair<IActivity, String>(ad, inFlows[i].getId());
            if (syncObjectsEdge.containsKey(key)) {
                //String ceIn2 = syncObjectsEdge.get(key);
                nameObjects.put(inFlows[i].getId(), inFlows[i].getSource().getName());
            }

        }

        finalNode.append("(");
        for (int i = 0; i < ceInitials.size(); i++) {
        	Pair<IActivity,String> key = new Pair<IActivity, String>(ad,ceInitials.get(i));
            String ceIn = syncChannelsEdge.get(key);    //get the parallel input channels
            String oeIn = syncObjectsEdge.get(key);

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
        Pair<IActivity,String> key = new Pair<IActivity, String>(ad,adUtils.nameDiagramResolver(activityNode.getName()));
        alphabetNode.put(key, alphabet);

        activityNode = null;

        return finalNode.toString();

    }
}
