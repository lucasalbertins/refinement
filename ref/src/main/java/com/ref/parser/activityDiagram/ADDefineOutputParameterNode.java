package com.ref.parser.activityDiagram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.change_vision.jude.api.inf.model.IActivityParameterNode;
import com.ref.exceptions.ParsingException;
import com.ref.interfaces.activityDiagram.IActivity;
import com.ref.interfaces.activityDiagram.IActivityNode;
import com.ref.interfaces.activityDiagram.IFlow;
import com.ref.interfaces.activityDiagram.IObjectFlow;

public class ADDefineOutputParameterNode {

	private IActivity ad;

	private HashMap<Pair<IActivity, String>, ArrayList<String>> alphabetNode;
	private HashMap<Pair<IActivity, String>, String> syncChannelsEdge;
	private HashMap<Pair<IActivity, String>, String> syncObjectsEdge;
	private HashMap<String, String> objectEdges;
	private ADUtils adUtils;

	public ADDefineOutputParameterNode(IActivity ad, HashMap<Pair<IActivity, String>, ArrayList<String>> alphabetNode2,
			HashMap<Pair<IActivity, String>, String> syncChannelsEdge2,
			HashMap<Pair<IActivity, String>, String> syncObjectsEdge2, HashMap<String, String> objectEdges,
			ADUtils adUtils) {
		this.ad = ad;
		this.alphabetNode = alphabetNode2;
		this.syncChannelsEdge = syncChannelsEdge2;
		this.syncObjectsEdge = syncObjectsEdge2;
		this.objectEdges = objectEdges;
		this.adUtils = adUtils;
	}

	public String defineOutputParameterNode(IActivityNode activityNode) throws ParsingException {
		StringBuilder outParameter = new StringBuilder();
		ArrayList<String> alphabet = new ArrayList<>();
		String nameOutParameter = "parameter_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_"
				+ adUtils.nameDiagramResolver(ad.getName());
		String nameOutParameterTermination = nameOutParameter + "_t";
		String endDiagram = "END_DIAGRAM_" + adUtils.nameDiagramResolver(ad.getName());
		IFlow[] inFlows = activityNode.getIncomings();
		String nameObjectUnique = "outParam";
		String parameterType = ((IActivityParameterNode) activityNode).getBase().getName();

		outParameter.append(nameOutParameter + "(id) = ");

		outParameter.append("(");

		for (int i = 0; i < inFlows.length; i++) {
			Pair<IActivity, String> key = new Pair<IActivity, String>(ad, inFlows[i].getId());
			String typeObject;
			try {
				typeObject = ((IObjectFlow) inFlows[i]).getBase().getName();
			} catch (NullPointerException e) {
				throw new ParsingException("Object flow does not have a type.");
			}
			if (!typeObject.equals(parameterType)) {
				throw new ParsingException("Out Parameter " + activityNode.getName()
						+ ": incompatible types of incoming edge and parameter.");
			}
			String oeIn;
			if (syncObjectsEdge.containsKey(key)) {
				oeIn = syncObjectsEdge.get(key);
				if (!objectEdges.containsKey(oeIn)) {
					objectEdges.put(oeIn, typeObject);
				}
			} else {
				oeIn = adUtils.createOE();
				syncObjectsEdge.put(key, oeIn);
				objectEdges.put(oeIn, typeObject);
			}

			outParameter.append("(");

			adUtils.oe(alphabet, outParameter, oeIn, "?oe" + i, " -> ");
			adUtils.setLocalInput(alphabet, outParameter, nameObjectUnique,
					adUtils.nameDiagramResolver(activityNode.getName()), "oe"+i, oeIn, typeObject);
			if (i >= 0 && i < inFlows.length - 1) {
				outParameter.append("SKIP) [] ");
			} else {
				outParameter.append("SKIP)");
			}
		}

		outParameter.append("); ");

		adUtils.getLocal(alphabet, outParameter, nameObjectUnique, adUtils.nameDiagramResolver(activityNode.getName()),
				nameObjectUnique, parameterType);
		adUtils.set(alphabet, outParameter, activityNode.getName(), nameObjectUnique);

		adUtils.update(alphabet, outParameter, 1, 0, true);


		outParameter.append(nameOutParameter + "(id)\n");
		outParameter.append(nameOutParameterTermination + "(id) = ");

		outParameter.append("((" + nameOutParameter + "(id) /\\ " + endDiagram + "(id)) ");

		outParameter.append("[|{|");
		outParameter.append("get_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_"
				+ adUtils.nameDiagramResolver(ad.getName()) + ".id,");
		outParameter.append("set_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_"
				+ adUtils.nameDiagramResolver(ad.getName()) + ".id,");
		outParameter.append("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()) + ".id");
		outParameter.append("|}|] ");
		outParameter.append("Mem_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_"
				+ adUtils.nameDiagramResolver(ad.getName()) + "_" + nameObjectUnique + "_t(id,"
				+ adUtils.getDefaultValue(parameterType) + ")) ");

		outParameter.append("\\{|");
		outParameter.append("get_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_"
				+ adUtils.nameDiagramResolver(ad.getName()) + ".id,");
		outParameter.append("set_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_"
				+ adUtils.nameDiagramResolver(ad.getName()) + ".id");
		outParameter.append("|}\n");

		alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()) + ".id");
		Pair<IActivity, String> key = new Pair<IActivity, String>(ad,
				adUtils.nameDiagramResolver("parameter_" + activityNode.getName()));
		alphabetNode.put(key, alphabet);

		return outParameter.toString();

	}
/*
	public IActivityNode defineOutputParameterNode(IActivityNode activityNode, StringBuilder nodes) {
		StringBuilder outParameter = new StringBuilder();
		ArrayList<String> alphabet = new ArrayList<>();
		String nameOutParameter = "parameter_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_"
				+ adUtils.nameDiagramResolver(ad.getName());
		String nameOutParameterTermination = nameOutParameter + "_t";
		String endDiagram = "END_DIAGRAM_" + adUtils.nameDiagramResolver(ad.getName());
		IFlow[] inFlows = activityNode.getIncomings();
		String nameObject = null;
		String nameObjectUnique = "";
		List<String> nameObjectAdded = new ArrayList<>();
		HashMap<String, String> nameObjects = new HashMap<>();
		List<String> namesMemoryLocal = new ArrayList<>();
		// String typeMemoryLocal = null;
		String parameterType = ((IActivityParameterNode) activityNode).getBase().getName();

		ArrayList<String> ceInitials = new ArrayList<>();
		for (int i = 0; i < inFlows.length; i++) {
			ceInitials.add(inFlows[i].getId());
			Pair<IActivity, String> key = new Pair<IActivity, String>(ad, inFlows[i].getId());
			if (syncObjectsEdge.containsKey(key)) {
				String ceIn2 = syncObjectsEdge.get(key);
				nameObjects.put(inFlows[i].getId(), objectEdges.get(ceIn2));
			}
		}

		outParameter.append(nameOutParameter + "(id) = ");

		outParameter.append("(");

		for (int i = 0; i < ceInitials.size(); i++) { // get unique channel
			if (nameObjects.get(ceInitials.get(i)) != null) {
				if (!nameObjectAdded.contains(nameObjects.get(ceInitials.get(i)))) {
					nameObjectAdded.add(nameObjects.get(ceInitials.get(i)));
					nameObjectUnique += nameObjects.get(ceInitials.get(i));
					// typeMemoryLocal = nameObjects.get(ceInitials.get(i));
				}
			}
		}

		if (!nameObjectUnique.equals("")) {
			namesMemoryLocal.add(nameObjectUnique);
		}

		for (int i = 0; i < ceInitials.size(); i++) {
			Pair<IActivity, String> key = new Pair<IActivity, String>(ad, ceInitials.get(i));
			String ceIn = syncChannelsEdge.get(key); // get the parallel input channels
			String oeIn = syncObjectsEdge.get(key);

			if (ceIn != null) {
				outParameter.append("(");

				if (i >= 0 && i < ceInitials.size() - 1) {
					adUtils.ce(alphabet, outParameter, ceIn, " -> SKIP) [] ");
				} else {
					adUtils.ce(alphabet, outParameter, ceIn, " -> SKIP)");
				}
			} else {

				nameObject = nameObjects.get(ceInitials.get(i));
				outParameter.append("(");

				if (i >= 0 && i < ceInitials.size() - 1) {
					adUtils.ce(alphabet, outParameter, oeIn, "?" + nameObject + " -> ");
					adUtils.setLocalInput(alphabet, outParameter, nameObjectUnique,
							adUtils.nameDiagramResolver(activityNode.getName()), nameObject, oeIn, parameterType);
					outParameter.append("SKIP) [] ");
				} else {
					adUtils.ce(alphabet, outParameter, oeIn, "?" + nameObject + " -> ");
					adUtils.setLocalInput(alphabet, outParameter, nameObjectUnique,
							adUtils.nameDiagramResolver(activityNode.getName()), nameObject, oeIn, parameterType);
					outParameter.append("SKIP)");
				}
			}
		}

		outParameter.append("); ");

		adUtils.getLocal(alphabet, outParameter, nameObjectUnique, adUtils.nameDiagramResolver(activityNode.getName()),
				nameObjectUnique, parameterType);
		adUtils.set(alphabet, outParameter, activityNode.getName(), nameObjectUnique);

		adUtils.update(alphabet, outParameter, 1, 0, true);

		String nameObjectReal = ((IActivityParameterNode) activityNode).getBase().getName();

//        if (nameObjectReal == null) {
//            nameObjectReal = typeUnionList.get(typeMemoryLocal);
//        }

		outParameter.append(nameOutParameter + "(id)\n");
		outParameter.append(nameOutParameterTermination + "(id) = ");

		outParameter.append("((" + nameOutParameter + "(id) /\\ " + endDiagram + "(id)) ");

		outParameter.append("[|{|");
		outParameter.append("get_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_"
				+ adUtils.nameDiagramResolver(ad.getName()) + ".id,");
		outParameter.append("set_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_"
				+ adUtils.nameDiagramResolver(ad.getName()) + ".id,");
		outParameter.append("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()) + ".id");
		outParameter.append("|}|] ");
		outParameter.append("Mem_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_"
				+ adUtils.nameDiagramResolver(ad.getName()) + "_" + nameObjectUnique + "_t(id,"
				+ adUtils.getDefaultValue(nameObjectReal) + ")) ");

		outParameter.append("\\{|");
		outParameter.append("get_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_"
				+ adUtils.nameDiagramResolver(ad.getName()) + ".id,");
		outParameter.append("set_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_"
				+ adUtils.nameDiagramResolver(ad.getName()) + ".id");
		outParameter.append("|}\n");

		alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()) + ".id");
		Pair<IActivity, String> key = new Pair<IActivity, String>(ad,
				adUtils.nameDiagramResolver("parameter_" + activityNode.getName()));
		alphabetNode.put(key, alphabet);

		activityNode = null;

		nodes.append(outParameter.toString());

		return activityNode;
	}*/
}
