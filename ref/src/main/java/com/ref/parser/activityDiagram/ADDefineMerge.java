package com.ref.parser.activityDiagram;

import com.change_vision.jude.api.inf.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ADDefineMerge {

    private IActivity ad;

    private HashMap<Pair<IActivity, String>, ArrayList<String>> alphabetNode;
    private HashMap<Pair<IActivity, String>, String> syncChannelsEdge;
    private HashMap<Pair<IActivity, String>, String> syncObjectsEdge;
    private HashMap<String, String> objectEdges;
    private HashMap<String, String> parameterNodesInput;
    private List<ArrayList<String>> unionList;
    private HashMap<String, String> typeUnionList;
    private ADUtils adUtils;

    public ADDefineMerge(IActivity ad, HashMap<Pair<IActivity, String>, ArrayList<String>> alphabetNode2, HashMap<Pair<IActivity, String>, String> syncChannelsEdge2,
                         HashMap<Pair<IActivity, String>, String> syncObjectsEdge2, HashMap<String, String> objectEdges, HashMap<String, String> parameterNodesInput,
                         List<ArrayList<String>> unionList, HashMap<String, String> typeUnionList, ADUtils adUtils) {
        this.ad = ad;
        this.alphabetNode = alphabetNode2;
        this.syncChannelsEdge = syncChannelsEdge2;
        this.syncObjectsEdge = syncObjectsEdge2;
        this.objectEdges = objectEdges;
        this.parameterNodesInput = parameterNodesInput;
        this.unionList = unionList;
        this.typeUnionList = typeUnionList;
        this.adUtils = adUtils;
    }

    public IActivityNode defineMerge(IActivityNode activityNode, StringBuilder nodes, int code) {
        StringBuilder merge = new StringBuilder();
        ArrayList<String> alphabet = new ArrayList<>();
        String nameMerge = adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName());
        String nameMergeTermination = adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + "_t";
        String endDiagram = "END_DIAGRAM_" + adUtils.nameDiagramResolver(ad.getName());
        IFlow[] outFlows = activityNode.getOutgoings();
        IFlow[] inFlows = activityNode.getIncomings();
        String nameObject = null;
        String nameObjectUnique = "";
        List<String> nameObjectAdded = new ArrayList<>();
        HashMap<String, String> nameObjects = new HashMap<>();
        List<String> namesMemoryLocal = new ArrayList<>();
        String typeMemoryLocal = null;

        if (code == 0) {
            ArrayList<String> ceInitials = new ArrayList<>();
            for (int i = 0; i < inFlows.length; i++) {
                ceInitials.add(inFlows[i].getId());
                Pair<IActivity,String> key = new Pair<IActivity, String>(ad,inFlows[i].getId());
                if (syncObjectsEdge.containsKey(key)) {
                    String ceIn2 = syncObjectsEdge.get(key);
                    nameObjects.put(inFlows[i].getId(), objectEdges.get(ceIn2));
                }
            }

            merge.append(nameMerge + "(id) = ");

            merge.append("(");
            String datatype = null;

            for (int i = 0; i < ceInitials.size(); i++) {        //get unique channel
                if (nameObjects.get(ceInitials.get(i)) != null) {
                    if (!nameObjectAdded.contains(nameObjects.get(ceInitials.get(i)))) {
                        nameObjectAdded.add(nameObjects.get(ceInitials.get(i)));
                        nameObjectUnique += nameObjects.get(ceInitials.get(i));

                        if (parameterNodesInput.containsKey(nameObjects.get(ceInitials.get(i)))) {
                            typeMemoryLocal = parameterNodesInput.get(nameObjects.get(ceInitials.get(i)));
                        } else {
                            typeMemoryLocal = nameObjects.get(ceInitials.get(i));
                        }
                    }
                }
            }

            if (!nameObjectUnique.equals("")) {
                namesMemoryLocal.add(nameObjectUnique);
            }

            for (int i = 0; i < ceInitials.size(); i++) {
            	Pair<IActivity,String> key = new Pair<IActivity, String>(ad,ceInitials.get(i));
                String ceIn = syncChannelsEdge.get(key);    //get the parallel input channels
                String oeIn = syncObjectsEdge.get(key);

                if (ceIn != null) {
                    merge.append("(");

                    if (i >= 0 && i < ceInitials.size() - 1) {
                        adUtils.ce(alphabet, merge, ceIn, " -> SKIP) [] ");
                    } else {
                        adUtils.ce(alphabet, merge, ceIn, " -> SKIP)");
                    }
                } else {

                    nameObject = nameObjects.get(ceInitials.get(i));
                    merge.append("(");
                    datatype = objectEdges.get(oeIn);
                    if (i >= 0 && i < ceInitials.size() - 1) {
                        adUtils.ce(alphabet, merge, oeIn, "?" + nameObject + " -> ");
                        adUtils.setLocalInput(alphabet, merge, nameObjectUnique, adUtils.nameDiagramResolver(activityNode.getName()), nameObject, oeIn,datatype);
                        merge.append("SKIP) [] ");
                    } else {
                        adUtils.ce(alphabet, merge, oeIn, "?" + nameObject + " -> ");
                        adUtils.setLocalInput(alphabet, merge, nameObjectUnique, adUtils.nameDiagramResolver(activityNode.getName()), nameObject, oeIn,datatype);
                        merge.append("SKIP)");
                    }
                }
            }

            merge.append("); ");

            adUtils.update(alphabet, merge, 1, 1, false);

            if (!nameObjectUnique.equals("")) {
                adUtils.getLocal(alphabet, merge, nameObjectUnique, adUtils.nameDiagramResolver(activityNode.getName()), nameObjectUnique, datatype);
                String oe = adUtils.createOE(nameObjectUnique); //creates output channels
            	Pair<IActivity,String> key = new Pair<IActivity, String>(ad,outFlows[0].getId());
                syncObjectsEdge.put(key, oe);
                objectEdges.put(oe, nameObjectUnique);
                adUtils.oe(alphabet, merge, oe, "!" + nameObjectUnique, " -> ");

                merge.append(nameMerge + "(id)\n");
                merge.append(nameMergeTermination + "(id) = ");

                merge.append("((" + nameMerge + "(id) /\\ " + endDiagram + "(id)) ");

                merge.append("[|{|");
                merge.append("get_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                merge.append("set_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                merge.append("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()));
                merge.append("|}|] ");
                merge.append("Mem_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + "_" + nameObjectUnique + "_t(id," + adUtils.getDefaultValue(typeMemoryLocal) + ")) ");

                merge.append("\\{|");
                merge.append("get_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                merge.append("set_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()));
                merge.append("|}\n");

            } else {
                String ce = adUtils.createCE(); //creates output channels
                Pair<IActivity,String> key = new Pair<IActivity, String>(ad,outFlows[0].getId());
                syncChannelsEdge.put(key, ce);
                adUtils.ce(alphabet, merge, ce, " -> ");

                merge.append(nameMerge + "(id)\n");
                merge.append(nameMergeTermination + "(id) = ");
                merge.append(nameMerge + "(id) /\\ " + endDiagram + "(id)\n");
            }

            alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()+".id"));
        	Pair<IActivity,String> key = new Pair<IActivity, String>(ad,adUtils.nameDiagramResolver(activityNode.getName()));
            alphabetNode.put(key, alphabet);

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

            nodes.append(merge.toString());
        } else if (code == 1) {

            ArrayList<String> union = new ArrayList<>();
            List<String> nameObjs = new ArrayList<>();
            List<String> nodesAdded = new ArrayList<>();

            for (int i = 0; i < inFlows.length; i++) {
                nameObjs.addAll(adUtils.getObjects(inFlows[i], nodesAdded));
            }
            

            for (String nameObj : nameObjs) {
                if (!union.contains(nameObj)) {
                    nameObjectUnique += nameObj;
                    union.add(nameObj);
                    typeMemoryLocal = parameterNodesInput.get(nameObj);
                }
            }

            if (union.size() > 1) {
                unionList.add(union);
                typeUnionList.put(nameObjectUnique, typeMemoryLocal);
            }

            if (!nameObjectUnique.equals("")) {
                namesMemoryLocal.add(nameObjectUnique);
            }

            if (!nameObjectUnique.equals("")) {
                String oe = adUtils.createOE(typeMemoryLocal); //creates output channels
                Pair<IActivity,String> key = new Pair<IActivity, String>(ad,outFlows[0].getId());
                syncObjectsEdge.put(key, oe);
                objectEdges.put(oe, typeMemoryLocal);
                adUtils.oe(alphabet, merge, oe, "!" + nameObjectUnique, " -> ");

                merge.append(nameMerge + "(id)\n");
                merge.append(nameMergeTermination + "(id) = ");

                merge.append("((" + nameMerge + "(id) /\\ " + endDiagram + "(id)) ");

                merge.append("[|{|");
                merge.append("get_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                merge.append("set_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                merge.append("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()));
                merge.append("|}|] ");
                merge.append("Mem_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + "_" + nameObjectUnique + "_t(" + adUtils.getDefaultValue(typeMemoryLocal) + "(id))) ");

                merge.append("\\{|");
                merge.append("get_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                merge.append("set_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()));
                merge.append("|}\n");

            } else {
                String ce = adUtils.createCE(); //creates output channels
                Pair<IActivity,String> key = new Pair<IActivity, String>(ad,outFlows[0].getId());
                syncChannelsEdge.put(key, ce);
                adUtils.ce(alphabet, merge, ce, " -> ");

                merge.append(nameMerge + "(id)\n");
                merge.append(nameMergeTermination + "(id) = ");
                merge.append(nameMerge + "(id) /\\ " + endDiagram + "(id)\n");
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
                Pair<IActivity,String> key = new Pair<IActivity, String>(ad,inFlows[i].getId());
                if (syncObjectsEdge.containsKey(key)) {
                    String ceIn2 = syncObjectsEdge.get(key);
                    nameObjects.put(inFlows[i].getId(), objectEdges.get(ceIn2));
                }
            }

            merge.append(nameMerge + "(id) = ");

            merge.append("(");
            String dataType = null;

            for (int i = 0; i < ceInitials.size(); i++) {        //get unique channel
                if (nameObjects.get(ceInitials.get(i)) != null) {
                    if (!nameObjectAdded.contains(nameObjects.get(ceInitials.get(i)))) {
                        nameObjectAdded.add(nameObjects.get(ceInitials.get(i)));
                        nameObjectUnique += nameObjects.get(ceInitials.get(i));

                        if (parameterNodesInput.containsKey(nameObjects.get(ceInitials.get(i)))) {
                            typeMemoryLocal = parameterNodesInput.get(nameObjects.get(ceInitials.get(i)));
                        } else {
                            typeMemoryLocal = nameObjects.get(ceInitials.get(i));
                        }
                    }
                }
            }


            if (!nameObjectUnique.equals("")) {
                namesMemoryLocal.add(nameObjectUnique);
            }

            for (int i = 0; i < ceInitials.size(); i++) {
            	Pair<IActivity,String> key = new Pair<IActivity, String>(ad,ceInitials.get(i));
                String ceIn = syncChannelsEdge.get(key);    //get the parallel input channels
                String oeIn = syncObjectsEdge.get(key);

                if (ceIn != null) {
                    merge.append("(");

                    if (i >= 0 && i < ceInitials.size() - 1) {
                        adUtils.ce(alphabet, merge, ceIn, " -> SKIP) [] ");
                    } else {
                        adUtils.ce(alphabet, merge, ceIn, " -> SKIP)");
                    }
                } else {

                    nameObject = nameObjects.get(ceInitials.get(i));
                    merge.append("(");
                    dataType = objectEdges.get(oeIn);
                    if (i >= 0 && i < ceInitials.size() - 1) {//TODO verificar aqui
                        adUtils.ce(alphabet, merge, oeIn, "?" + nameObject + " -> ");
                        adUtils.setLocalInput(alphabet, merge, nameObjectUnique, adUtils.nameDiagramResolver(activityNode.getName()), nameObject, oeIn, dataType);
                        merge.append("SKIP) [] ");
                    } else {
                        adUtils.ce(alphabet, merge, oeIn, "?" + nameObject + " -> ");
                        adUtils.setLocalInput(alphabet, merge, nameObjectUnique, adUtils.nameDiagramResolver(activityNode.getName()), nameObject, oeIn, dataType);
                        merge.append("SKIP)");
                    }
                }
            }

            merge.append("); ");

            adUtils.update(alphabet, merge, 1, 1, false);

            if (!nameObjectUnique.equals("")) {
                adUtils.getLocal(alphabet, merge, nameObjectUnique, adUtils.nameDiagramResolver(activityNode.getName()), nameObjectUnique, dataType);
                Pair<IActivity,String> key = new Pair<IActivity, String>(ad,outFlows[0].getId());
                String oe = syncObjectsEdge.get(key);//TODO da erro aqui (pq ele ta olhando a saida?)

                adUtils.oe(alphabet, merge, oe, "!" + nameObjectUnique, " -> ");

                merge.append(nameMerge + "(id)\n");
                merge.append(nameMergeTermination + "(id) = ");

                merge.append("((" + nameMerge + "(id) /\\ " + endDiagram + "(id)) ");

                merge.append("[|{|");
                merge.append("get_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                merge.append("set_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                merge.append("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()));
                merge.append("|}|] ");
                merge.append("Mem_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + "_" + nameObjectUnique + "_t(id," + adUtils.getDefaultValue(typeMemoryLocal) + ")) ");

                merge.append("\\{|");
                merge.append("get_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
                merge.append("set_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_" + adUtils.nameDiagramResolver(ad.getName()));
                merge.append("|}\n");

            } else {
            	Pair<IActivity,String> key = new Pair<IActivity, String>(ad,outFlows[0].getId());
                String ce = syncChannelsEdge.get(key);
                adUtils.ce(alphabet, merge, ce, " -> ");

                merge.append(nameMerge + "(id)\n");
                merge.append(nameMergeTermination + "(id) = ");
                merge.append(nameMerge + "(id) /\\ " + endDiagram + "(id)\n");
            }

            alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName())+".id");
            Pair<IActivity,String> key = new Pair<IActivity, String>(ad,adUtils.nameDiagramResolver(activityNode.getName()));
            alphabetNode.put(key, alphabet);

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

            nodes.append(merge.toString());
        }

        return activityNode;
    }
}
