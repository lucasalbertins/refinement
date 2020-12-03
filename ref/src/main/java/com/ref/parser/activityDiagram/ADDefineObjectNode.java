package com.ref.parser.activityDiagram;

import java.util.ArrayList;
import java.util.HashMap;

import com.ref.exceptions.ParsingException;
import com.ref.interfaces.activityDiagram.IActivity;
import com.ref.interfaces.activityDiagram.IActivityNode;
import com.ref.interfaces.activityDiagram.IActivityParameterNode;
import com.ref.interfaces.activityDiagram.IFlow;
import com.ref.interfaces.activityDiagram.IObjectFlow;

public class ADDefineObjectNode {

	private IActivity ad;

	private HashMap<Pair<IActivity, String>, ArrayList<String>> alphabetNode;
	private HashMap<Pair<IActivity, String>, String> syncChannelsEdge;
	private HashMap<Pair<IActivity, String>, String> syncObjectsEdge;
	private HashMap<String, String> objectEdges;
	private ADUtils adUtils;

	public ADDefineObjectNode(IActivity ad, HashMap<Pair<IActivity, String>, ArrayList<String>> alphabetNode2,
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

	public String defineObjectNode(IActivityNode activityNode) throws ParsingException {
		StringBuilder objectNode = new StringBuilder();
		ArrayList<String> alphabet = new ArrayList<>();
		String nameObjectNode = adUtils.nameDiagramResolver(activityNode.getName()) + "_"
				+ adUtils.nameDiagramResolver(ad.getName());
		String nameObjectNodeTermination = adUtils.nameDiagramResolver(activityNode.getName()) + "_"
				+ adUtils.nameDiagramResolver(ad.getName()) + "_t";
		String endDiagram = "END_DIAGRAM_" + adUtils.nameDiagramResolver(ad.getName());
		IFlow[] outFlows = activityNode.getOutgoings();
		IFlow[] inFlows = activityNode.getIncomings();
		String nameObjectUnique = "";
		String parameterType = ((IActivityParameterNode) activityNode).getBase().getName();

		objectNode.append(nameObjectNode + "(id) = (");

		for (int i = 0; i < inFlows.length; i++) {
			String typeObject;
			Pair<IActivity, String> key = new Pair<IActivity, String>(ad, inFlows[i].getId());
			try {
				typeObject = ((IObjectFlow) inFlows[i]).getBase().getName();
			} catch (NullPointerException e) {
				throw new ParsingException("Object flow does not have a type.");
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
			nameObjectUnique = "objMem";
			objectNode.append("(");
			adUtils.oe(alphabet, objectNode, oeIn, "?oe" + i, " -> ");
			adUtils.setLocalInput(alphabet, objectNode, nameObjectUnique,
					adUtils.nameDiagramResolver(activityNode.getName()), "oe" + i, oeIn, typeObject);
			if (i >= 0 && i < inFlows.length - 1) {
				objectNode.append("SKIP) [] ");
			} else {
				objectNode.append("SKIP)");
			}
		}
		
		objectNode.append("); ");

		adUtils.update(alphabet, objectNode, 1, activityNode.getOutgoings().length, false);
		
		adUtils.getLocal(alphabet, objectNode, nameObjectUnique,
				adUtils.nameDiagramResolver(activityNode.getName()), nameObjectUnique, parameterType);

		objectNode.append("(");
		
		for (int i = 0; i < outFlows.length; i++) {
			Pair<IActivity, String> key = new Pair<IActivity, String>(ad, outFlows[i].getId());
			String typeObject = ((IObjectFlow) outFlows[i]).getBase().getName();
			String oe; // creates output channels
			if (syncObjectsEdge.containsKey(key)) {
				oe = syncObjectsEdge.get(key);
				if (!objectEdges.containsKey(oe)) {
					objectEdges.put(oe, typeObject);
				}
			} else {
				oe = adUtils.createOE();
				syncObjectsEdge.put(key, oe);
				objectEdges.put(oe, typeObject);
			}
			objectNode.append("(");

			if (i >= 0 && (i < outFlows.length - 1)) {
				adUtils.oe(alphabet, objectNode, oe, "!" + nameObjectUnique, " -> SKIP) ||| ");
			} else {
				adUtils.oe(alphabet, objectNode, oe, "!" + nameObjectUnique, " -> SKIP)");
			}
		}

		objectNode.append("); ");

		objectNode.append(nameObjectNode + "(id)\n");
		objectNode.append(nameObjectNodeTermination + "(id) = ");

		objectNode.append("((" + nameObjectNode + "(id) /\\ " + endDiagram + "(id)) ");

		objectNode.append("[|{|");
		objectNode.append("get_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName())
				+ "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
		objectNode.append("set_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName())
				+ "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
		objectNode.append("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()));
		objectNode.append("|}|] ");
		objectNode.append("Mem_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_"
				+ adUtils.nameDiagramResolver(ad.getName()) + "_" + nameObjectUnique + "_t(id,"
				+ adUtils.getDefaultValue(parameterType) + ")) ");

		objectNode.append("\\{|");
		objectNode.append("get_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName())
				+ "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
		objectNode.append("set_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName())
				+ "_" + adUtils.nameDiagramResolver(ad.getName()));
		objectNode.append("|}\n");



		alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()) + ".id");
		Pair<IActivity, String> key = new Pair<IActivity, String>(ad,
				adUtils.nameDiagramResolver(activityNode.getName()));
		alphabetNode.put(key, alphabet);

		return objectNode.toString();

	}
/*
	public IActivityNode defineObjectNode(IActivityNode activityNode, StringBuilder nodes, int code) {
		StringBuilder objectNode = new StringBuilder();
		ArrayList<String> alphabet = new ArrayList<>();
		String nameObjectNode = adUtils.nameDiagramResolver(activityNode.getName()) + "_"
				+ adUtils.nameDiagramResolver(ad.getName());
		String nameObjectNodeTermination = adUtils.nameDiagramResolver(activityNode.getName()) + "_"
				+ adUtils.nameDiagramResolver(ad.getName()) + "_t";
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
				Pair<IActivity, String> key = new Pair<IActivity, String>(ad, inFlows[i].getId());
				if (syncObjectsEdge.containsKey(key)) {
					String ceIn2 = syncObjectsEdge.get(key);
					nameObjects.put(inFlows[i].getId(), objectEdges.get(ceIn2));
				}
			}

			objectNode.append(nameObjectNode + "(id) = ");

			objectNode.append("(");

			for (int i = 0; i < ceInitials.size(); i++) { // get unique channel
				if (nameObjects.get(ceInitials.get(i)) != null) {
					if (!nameObjectAdded.contains(nameObjects.get(ceInitials.get(i)))) {
						nameObjectAdded.add(nameObjects.get(ceInitials.get(i)));
						nameObjectUnique += nameObjects.get(ceInitials.get(i));
						typeMemoryLocal = nameObjects.get(ceInitials.get(i));
					}
				}
			}

			if (!nameObjectUnique.equals("")) {
				namesMemoryLocal.add(nameObjectUnique);
			}

			for (int i = 0; i < ceInitials.size(); i++) {
				Pair<IActivity, String> key = new Pair<IActivity, String>(ad, ceInitials.get(i));
				String oeIn = syncObjectsEdge.get(key); // get the parallel input channels

				nameObject = nameObjects.get(ceInitials.get(i));
				objectNode.append("(");

				if (i >= 0 && i < ceInitials.size() - 1) {
					adUtils.ce(alphabet, objectNode, oeIn, "?" + nameObject + " -> ");
					adUtils.setLocalInput(alphabet, objectNode, nameObjectUnique,
							adUtils.nameDiagramResolver(activityNode.getName()), nameObject, oeIn, nameObjectUnique);
					objectNode.append("SKIP) [] ");
				} else {
					adUtils.ce(alphabet, objectNode, oeIn, "?" + nameObject + " -> ");
					adUtils.setLocalInput(alphabet, objectNode, nameObjectUnique,
							adUtils.nameDiagramResolver(activityNode.getName()), nameObject, oeIn, nameObjectUnique);
					objectNode.append("SKIP)");
				}
			}

			objectNode.append("); ");

			adUtils.update(alphabet, objectNode, 1, activityNode.getOutgoings().length, false);

			adUtils.getLocal(alphabet, objectNode, nameObjectUnique,
					adUtils.nameDiagramResolver(activityNode.getName()), nameObjectUnique, nameObjectUnique);

			objectNode.append("(");

			for (int i = 0; i < outFlows.length; i++) {
				String oe = adUtils.createOE(nameObjectUnique); // creates output channels
				Pair<IActivity, String> key = new Pair<IActivity, String>(ad, outFlows[i].getId());
				syncObjectsEdge.put(key, oe);
				objectEdges.put(oe, nameObjectUnique);
				objectNode.append("(");

				if (i >= 0 && (i < outFlows.length - 1)) {
					adUtils.oe(alphabet, objectNode, oe, "!" + nameObjectUnique, " -> SKIP) ||| ");
				} else {
					adUtils.oe(alphabet, objectNode, oe, "!" + nameObjectUnique, " -> SKIP)");
				}
			}

			objectNode.append("); ");

			objectNode.append(nameObjectNode + "(id)\n");
			objectNode.append(nameObjectNodeTermination + "(id) = ");

			objectNode.append("((" + nameObjectNode + "(id) /\\ " + endDiagram + "(id)) ");

			objectNode.append("[|{|");
			objectNode.append("get_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName())
					+ "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
			objectNode.append("set_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName())
					+ "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
			objectNode.append("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()));
			objectNode.append("|}|] ");
			objectNode.append("Mem_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_"
					+ adUtils.nameDiagramResolver(ad.getName()) + "_" + nameObjectUnique + "_t(id,"
					+ adUtils.getDefaultValue(typeMemoryLocal) + ")) ");

			objectNode.append("\\{|");
			objectNode.append("get_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName())
					+ "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
			objectNode.append("set_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName())
					+ "_" + adUtils.nameDiagramResolver(ad.getName()));
			objectNode.append("|}\n");

			//

			alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()) + ".id");
			Pair<IActivity, String> key = new Pair<IActivity, String>(ad,
					adUtils.nameDiagramResolver(activityNode.getName()));
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

			for (int i = 1; i < outFlows.length; i++) { // creates the parallel output channels
				if (outFlows[i].getTarget() instanceof IInputPin) {
					for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
						if (activityNodeSearch instanceof IAction) {
							IInputPin[] inFlowPin = ((IAction) activityNodeSearch).getInputs();
							for (int y = 0; y < inFlowPin.length; y++) {
								if (inFlowPin[y].getId().equals(outFlows[i].getTarget().getId())) {
									if (!queueNode.contains(activityNodeSearch)) {
										queueNode.add(activityNodeSearch);
									}
								}
							}
						}
					}
				} else {
					if (!queueNode.contains(outFlows[i].getTarget())) {
						queueNode.add(outFlows[i].getTarget());
					}
				}
			}

			nodes.append(objectNode.toString());
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
					typeMemoryLocal = nameObj;
				}
			}

			if (union.size() > 1) {
				unionList.add(union);
				typeUnionList.put(nameObjectUnique, parameterNodesInput.get(typeMemoryLocal));
			}

			if (!nameObjectUnique.equals("")) {
				namesMemoryLocal.add(nameObjectUnique);
			}

			for (int i = 0; i < outFlows.length; i++) {
				String oe = adUtils.createOE(nameObjectUnique); // creates output channels
				Pair<IActivity, String> key = new Pair<IActivity, String>(ad, outFlows[i].getId());
				syncObjectsEdge.put(key, oe);
				objectEdges.put(oe, nameObjectUnique);

				if (i >= 0 && (i < outFlows.length - 1)) {
					adUtils.oe(alphabet, objectNode, oe, "!" + nameObjectUnique, " -> SKIP) ||| ");
				} else {
					adUtils.oe(alphabet, objectNode, oe, "!" + nameObjectUnique, " -> SKIP)");
				}
			}
			if (outFlows.length == 0) {
				activityNode = null;
			} else {
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
			}

			for (int i = 1; i < outFlows.length; i++) { // creates the parallel output channels
				if (outFlows[i].getTarget() instanceof IInputPin) {
					for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
						if (activityNodeSearch instanceof IAction) {
							IInputPin[] inFlowPin = ((IAction) activityNodeSearch).getInputs();
							for (int y = 0; y < inFlowPin.length; y++) {
								if (inFlowPin[y].getId().equals(outFlows[i].getTarget().getId())) {
									if (!queueNode.contains(activityNodeSearch)) {
										queueNode.add(activityNodeSearch);
									}
								}
							}
						}
					}
				} else {
					if (!queueNode.contains(outFlows[i].getTarget())) {
						queueNode.add(outFlows[i].getTarget());
					}
				}
			}

		} else if (code == 2) {
			ArrayList<String> ceInitials = new ArrayList<>();
			for (int i = 0; i < inFlows.length; i++) {
				ceInitials.add(inFlows[i].getId());
				Pair<IActivity, String> key = new Pair<IActivity, String>(ad, inFlows[i].getId());
				if (syncObjectsEdge.containsKey(key)) {
					String ceIn2 = syncObjectsEdge.get(key);
					nameObjects.put(inFlows[i].getId(), objectEdges.get(ceIn2));
				}
			}

			objectNode.append(nameObjectNode + "(id) = ");

			objectNode.append("(");

			for (int i = 0; i < ceInitials.size(); i++) { // get unique channel
				if (nameObjects.get(ceInitials.get(i)) != null) {
					if (!nameObjectAdded.contains(nameObjects.get(ceInitials.get(i)))) {
						nameObjectAdded.add(nameObjects.get(ceInitials.get(i)));
						nameObjectUnique += nameObjects.get(ceInitials.get(i));
						typeMemoryLocal = nameObjects.get(ceInitials.get(i));
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
					objectNode.append("(");

					if (i >= 0 && i < ceInitials.size() - 1) {
						adUtils.ce(alphabet, objectNode, ceIn, " -> SKIP) [] ");
					} else {
						adUtils.ce(alphabet, objectNode, ceIn, " -> SKIP)");
					}
				} else {

					nameObject = nameObjects.get(ceInitials.get(i));
					objectNode.append("(");

					if (i >= 0 && i < ceInitials.size() - 1) {
						adUtils.ce(alphabet, objectNode, oeIn, "?" + nameObject + " -> ");
						adUtils.setLocalInput(alphabet, objectNode, nameObjectUnique,
								adUtils.nameDiagramResolver(activityNode.getName()), nameObject, oeIn,
								nameObjectUnique);
						objectNode.append("SKIP) [] ");
					} else {
						adUtils.ce(alphabet, objectNode, oeIn, "?" + nameObject + " -> ");
						adUtils.setLocalInput(alphabet, objectNode, nameObjectUnique,
								adUtils.nameDiagramResolver(activityNode.getName()), nameObject, oeIn,
								nameObjectUnique);
						objectNode.append("SKIP)");
					}
				}
			}

			objectNode.append("); ");

			adUtils.update(alphabet, objectNode, 1, outFlows.length, false);

			adUtils.getLocal(alphabet, objectNode, nameObjectUnique,
					adUtils.nameDiagramResolver(activityNode.getName()), nameObjectUnique, nameObjectUnique);

			objectNode.append("(");

			for (int i = 0; i < outFlows.length; i++) {
				Pair<IActivity, String> key = new Pair<IActivity, String>(ad, outFlows[i].getId());
				String oe = syncObjectsEdge.get(key);
				objectNode.append("(");

				if (i >= 0 && (i < outFlows.length - 1)) {
					adUtils.oe(alphabet, objectNode, oe, "!" + nameObjectUnique, " -> SKIP) ||| ");
				} else {
					adUtils.oe(alphabet, objectNode, oe, "!" + nameObjectUnique, " -> SKIP)");
				}
			}

			objectNode.append("); ");

			objectNode.append(nameObjectNode + "(id)\n");
			objectNode.append(nameObjectNodeTermination + "(id) = ");

			objectNode.append("((" + nameObjectNode + "(id) /\\ " + endDiagram + "(id)) ");

			objectNode.append("[|{|");
			objectNode.append("get_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName())
					+ "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
			objectNode.append("set_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName())
					+ "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
			objectNode.append("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()));
			objectNode.append("|}|] ");
			objectNode.append("Mem_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_"
					+ adUtils.nameDiagramResolver(ad.getName()) + "_" + nameObjectUnique + "_t(id,"
					+ adUtils.getDefaultValue(parameterNodesInput.get(typeMemoryLocal)) + ")) ");

			objectNode.append("\\{|");
			objectNode.append("get_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName())
					+ "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
			objectNode.append("set_" + nameObjectUnique + "_" + adUtils.nameDiagramResolver(activityNode.getName())
					+ "_" + adUtils.nameDiagramResolver(ad.getName()));
			objectNode.append("|}\n");

			//

			alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()) + ".id");
			Pair<IActivity, String> key = new Pair<IActivity, String>(ad,
					adUtils.nameDiagramResolver(activityNode.getName()));
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

			for (int i = 1; i < outFlows.length; i++) { // creates the parallel output channels
				if (outFlows[i].getTarget() instanceof IInputPin) {
					for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
						if (activityNodeSearch instanceof IAction) {
							IInputPin[] inFlowPin = ((IAction) activityNodeSearch).getInputs();
							for (int y = 0; y < inFlowPin.length; y++) {
								if (inFlowPin[y].getId().equals(outFlows[i].getTarget().getId())) {
									if (!queueNode.contains(activityNodeSearch)) {
										queueNode.add(activityNodeSearch);
									}
								}
							}
						}
					}
				} else {
					if (!queueNode.contains(outFlows[i].getTarget())) {
						queueNode.add(outFlows[i].getTarget());
					}
				}
			}

			nodes.append(objectNode.toString());
		}

		return activityNode;
	}*/
}
