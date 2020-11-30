package com.ref.parser.activityDiagram;

import java.util.ArrayList;
import java.util.HashMap;

import com.ref.exceptions.ParsingException;

import com.ref.interfaces.activityDiagram.IActivity;
import com.ref.interfaces.activityDiagram.IActivityNode;
import com.ref.interfaces.activityDiagram.IFlow;
import com.ref.interfaces.activityDiagram.IObjectFlow;

public class ADDefineJoin {

	private IActivity ad;

	private HashMap<Pair<IActivity, String>, ArrayList<String>> alphabetNode;
	private HashMap<Pair<IActivity, String>, String> syncChannelsEdge;
	private HashMap<Pair<IActivity, String>, String> syncObjectsEdge;
	private HashMap<String, String> objectEdges;
	private ADUtils adUtils;


	public ADDefineJoin(IActivity ad, HashMap<Pair<IActivity, String>, ArrayList<String>> alphabetNode2,
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

	public String defineJoin(IActivityNode activityNode) throws ParsingException {
		StringBuilder joinNode = new StringBuilder();
		ArrayList<String> alphabet = new ArrayList<>();
		String nameJoin = adUtils.nameDiagramResolver(activityNode.getName()) + "_"
				+ adUtils.nameDiagramResolver(ad.getName());
		String nameJoinTermination = adUtils.nameDiagramResolver(activityNode.getName()) + "_"
				+ adUtils.nameDiagramResolver(ad.getName()) + "_t";
		String endDiagram = "END_DIAGRAM_" + adUtils.nameDiagramResolver(ad.getName());
		IFlow[] outFlows = activityNode.getOutgoings();
		IFlow[] inFlows = activityNode.getIncomings();
		HashMap<String, String> nameObjects = new HashMap<>();
		HashMap<String, String> typeObjects = new HashMap<>();
		String typeObject = null;
		boolean sync2Bool = false;

		if (outFlows.length != 1) {
			throw new ParsingException("Join node must have exactly one outgoing edge.");
		}

		joinNode.append(nameJoin + "(id) = (");

		ArrayList<String> ceInitials = new ArrayList<>();
		for (int i = 0; i < inFlows.length; i++) {
			ceInitials.add(inFlows[i].getId());
			Pair<IActivity, String> key = new Pair<IActivity, String>(ad, inFlows[i].getId());
			if (inFlows[i] instanceof IObjectFlow) {
				try {
					typeObject = ((IObjectFlow) inFlows[i]).getBase().getName();
				} catch (NullPointerException e) {
					throw new ParsingException("Object flow does not have a type.");
				}
				nameObjects.put(inFlows[i].getId(), "oe" + i);
				typeObjects.put(inFlows[i].getId(), typeObject);

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
				joinNode.append("(");

				if (i >= 0 && i < inFlows.length - 1) {
					adUtils.oe(alphabet, joinNode, oeIn, "?oe" + i, " -> ");
					adUtils.setLocalInput(alphabet, joinNode, "oe" + i,
							adUtils.nameDiagramResolver(activityNode.getName()), "oe" + i, oeIn, typeObject);
					joinNode.append("SKIP) ||| ");
				} else {
					adUtils.oe(alphabet, joinNode, oeIn, "?oe" + i, " -> ");
					adUtils.setLocalInput(alphabet, joinNode, "oe" + i,
							adUtils.nameDiagramResolver(activityNode.getName()), "oe" + i, oeIn, typeObject);
					joinNode.append("SKIP)");
				}
				sync2Bool = true;

			} else {
				String ceIn;

				if (syncChannelsEdge.containsKey(key)) {
					ceIn = syncChannelsEdge.get(key);
				} else {
					ceIn = adUtils.createCE();
					syncChannelsEdge.put(key, ceIn);
				}

				if (i >= 0 && i < inFlows.length - 1) {
					adUtils.ce(alphabet, joinNode, ceIn, " -> SKIP) ||| ");
				} else {
					adUtils.ce(alphabet, joinNode, ceIn, " -> SKIP)");
				}
			}
		}

		joinNode.append("); ");

		adUtils.update(alphabet, joinNode, inFlows.length, outFlows.length, false);

		if (sync2Bool) {
			for (String key : nameObjects.keySet()) {
				adUtils.getLocal(alphabet, joinNode, nameObjects.get(key),
						adUtils.nameDiagramResolver(activityNode.getName()), nameObjects.get(key),
						typeObjects.get(key));
			}
		}

		joinNode.append("(");

		Pair<IActivity, String> key = new Pair<IActivity, String>(ad, outFlows[0].getId());
		if (sync2Bool) {
			String oeOut;

			try {
				typeObject = ((IObjectFlow) outFlows[0]).getBase().getName();
			} catch (NullPointerException e) {
				throw new ParsingException("Object flow does not have a type.");
			}
			if (syncObjectsEdge.containsKey(key)) {
				oeOut = syncObjectsEdge.get(key);
				if (!objectEdges.containsKey(oeOut)) {
					objectEdges.put(oeOut, typeObject);
				}
			} else {
				oeOut = adUtils.createOE();
				syncObjectsEdge.put(key, oeOut);
				objectEdges.put(oeOut, typeObject);
			}

			int i = 0;
			for (String id : nameObjects.keySet()) { // creates the parallel output channels

				if (!typeObjects.get(id).equals(typeObject)) {
					continue;
				}

				joinNode.append("(");

				if (i >= 0 && i < nameObjects.keySet().size() - 1) {
					adUtils.oe(alphabet, joinNode, oeOut, "!" + nameObjects.get(id), " -> SKIP) |~| ");
				} else {
					adUtils.oe(alphabet, joinNode, oeOut, "!" + nameObjects.get(id), " -> SKIP)");
				}
				i++;
			}
		} else {
			String ceOut;
			if (syncChannelsEdge.containsKey(key)) {
				ceOut = syncChannelsEdge.get(key);
			} else {
				ceOut = adUtils.createCE();
				syncChannelsEdge.put(key, ceOut);
			}

			joinNode.append("(");

			adUtils.ce(alphabet, joinNode, ceOut, " -> SKIP)");
		}

		joinNode.append("); ");

		joinNode.append(nameJoin + "(id)\n");

		joinNode.append(nameJoinTermination + "(id) = ");

		for (int i = 0; i < nameObjects.keySet().size(); i++) {
			joinNode.append("(");
		}

		joinNode.append("(" + nameJoin + "(id) /\\ " + endDiagram + "(id))");

		for (String id: nameObjects.keySet()) { // creates the parallel output channels
			joinNode.append(" [|{|");
			joinNode.append("get_" + nameObjects.get(id) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_"
					+ adUtils.nameDiagramResolver(ad.getName()) + ",");
			joinNode.append("set_" + nameObjects.get(id) + "_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_"
					+ adUtils.nameDiagramResolver(ad.getName()) + ",");
			joinNode.append("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()) + ".id|}|] ");
			joinNode.append("Mem_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_"
					+ adUtils.nameDiagramResolver(ad.getName()) + "_" + nameObjects.get(id) + "_t(id,"
					+ adUtils.getDefaultValue(typeObjects.get(id)) + "))");
		}

		if (nameObjects.keySet().size() > 0) {
			joinNode.append(" \\{|");
			int i = 0;
			for (String id: nameObjects.keySet()) {
				joinNode.append("get_" + nameObjects.get(id) + "_" + adUtils.nameDiagramResolver(activityNode.getName())
						+ "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
				joinNode.append("set_" + nameObjects.get(id) + "_" + adUtils.nameDiagramResolver(activityNode.getName())
						+ "_" + adUtils.nameDiagramResolver(ad.getName()));
				if (i < nameObjects.keySet().size() - 1) {
					joinNode.append(",");
				}
				i++;
			}

			joinNode.append("|}");

		}

		joinNode.append("\n");

		alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()) + ".id");
		key = new Pair<IActivity, String>(ad,
				adUtils.nameDiagramResolver(activityNode.getName()));
		alphabetNode.put(key, alphabet);

		return joinNode.toString();
	}
/*
	public IActivityNode defineJoin(IActivityNode activityNode, StringBuilder nodes, int code) {
		StringBuilder joinNode = new StringBuilder();
		ArrayList<String> alphabet = new ArrayList<>();
		String nameJoin = adUtils.nameDiagramResolver(activityNode.getName()) + "_"
				+ adUtils.nameDiagramResolver(ad.getName());
		String nameJoinTermination = adUtils.nameDiagramResolver(activityNode.getName()) + "_"
				+ adUtils.nameDiagramResolver(ad.getName()) + "_t";
		String endDiagram = "END_DIAGRAM_" + adUtils.nameDiagramResolver(ad.getName());
		IFlow[] outFlows = activityNode.getOutgoings();
		IFlow[] inFlows = activityNode.getIncomings();
		HashMap<String, String> nameObjects = new HashMap<>();
		List<String> objects = new ArrayList<>();
		String typeObject = null;
		List<String> nameObjectAdded = new ArrayList<>();
		boolean syncBool = false;
		boolean sync2Bool = false;

		joinNode.append(nameJoin + "(id) = (");

		if (code == 0) {
			ArrayList<String> ceInitials = new ArrayList<>();
			for (int i = 0; i < inFlows.length; i++) {
				ceInitials.add(inFlows[i].getId());
				Pair<IActivity, String> key = new Pair<IActivity, String>(ad, inFlows[i].getId());
				if (syncChannelsEdge.containsKey(key)) {
					syncBool = true;
				}

				if (syncObjectsEdge.containsKey(key)) {
					// String ceIn2 = syncObjectsEdge.get(key);
					// nameObject = objectEdges.get(ceIn2);

					typeObject = ((IObjectNode) inFlows[i].getSource()).getBase().getName();

					nameObjects.put(inFlows[i].getId(), typeObject);
					sync2Bool = true;
				}
			}

			joinNode.append(nameJoin + "(id) = (");

			for (int i = 0; i < ceInitials.size(); i++) {
				Pair<IActivity, String> key = new Pair<IActivity, String>(ad, ceInitials.get(i));
				String ceIn = syncChannelsEdge.get(key); // get the parallel input channels
				String oeIn = syncObjectsEdge.get(key);

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
						adUtils.setLocalInput(alphabet, joinNode, oeIn,
								adUtils.nameDiagramResolver(activityNode.getName()), typeObject, oeIn, typeObject);
						joinNode.append("SKIP) ||| ");
					} else {
						adUtils.ce(alphabet, joinNode, oeIn, "?" + typeObject + " -> ");
						adUtils.setLocalInput(alphabet, joinNode, oeIn,
								adUtils.nameDiagramResolver(activityNode.getName()), typeObject, oeIn, typeObject);
						joinNode.append("SKIP)");
					}
				}

			}

			joinNode.append("); ");

			adUtils.update(alphabet, joinNode, inFlows.length, outFlows.length, false);

			if (sync2Bool) {
				for (String nameObjectOut : objects) {
					adUtils.getLocal(alphabet, joinNode, nameObjectOut,
							adUtils.nameDiagramResolver(activityNode.getName()), nameObjectOut, typeObject);
				}
			}

			joinNode.append("(");

			typeObject = "";

			for (int i = 0; i < inFlows.length; i++) {
				Pair<IActivity, String> key = new Pair<IActivity, String>(ad, inFlows[i].getId());
				String channel = syncObjectsEdge.get(key);
				if (objectEdges.get(channel) != null && !nameObjectAdded.contains(objectEdges.get(channel))) {
					nameObjectAdded.add(objectEdges.get(channel));
					typeObject += objectEdges.get(channel);
				}
			}

			if (sync2Bool) {
				for (int i = 0; i < objects.size(); i++) { // creates the parallel output channels
					String oe = adUtils.createOE(typeObject);
					Pair<IActivity, String> key = new Pair<IActivity, String>(ad, outFlows[0].getId());
					syncObjectsEdge.put(key, oe); // just one output
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
				for (int i = 0; i < outFlows.length; i++) { // creates the parallel output channels
					String ce = adUtils.createCE();
					Pair<IActivity, String> key = new Pair<IActivity, String>(ad, outFlows[i].getId());
					syncChannelsEdge.put(key, ce);

					joinNode.append("(");

					if (i >= 0 && i < outFlows.length - 1) {
						adUtils.ce(alphabet, joinNode, ce, " -> SKIP) ||| ");
					} else {
						adUtils.ce(alphabet, joinNode, ce, " -> SKIP)");
					}
				}
			}

			joinNode.append("); ");

			joinNode.append(nameJoin + "(id)\n");

			joinNode.append(nameJoinTermination + "(id) = ");

			for (int i = 0; i < objects.size(); i++) {
				joinNode.append("(");
			}

			joinNode.append("(" + nameJoin + "(id) /\\ " + endDiagram + "(id))");

			for (int i = 0; i < objects.size(); i++) { // creates the parallel output channels
				joinNode.append(" [|{|");
				joinNode.append("get_" + objects.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName())
						+ "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
				joinNode.append("set_" + objects.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName())
						+ "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
				joinNode.append("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()) + ".id|}|] ");
				joinNode.append("Mem_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_"
						+ adUtils.nameDiagramResolver(ad.getName()) + "_" + objects.get(i) + "_t(id,"
						+ adUtils.getDefaultValue(objects.get(i)) + "))");
			}

			if (objects.size() > 0) {
				joinNode.append(" \\{|");

				for (int i = 0; i < objects.size(); i++) {
					joinNode.append("get_" + objects.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName())
							+ "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
					joinNode.append("set_" + objects.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName())
							+ "_" + adUtils.nameDiagramResolver(ad.getName()));
					if (i < objects.size() - 1) {
						joinNode.append(",");
					}
				}

				joinNode.append("|}");

			}

			joinNode.append("\n");

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

			nodes.append(joinNode.toString());
		} else if (code == 1) {
			ArrayList<String> ceInitials = new ArrayList<>();
			ArrayList<String> obj = new ArrayList<>();
			for (int i = 0; i < inFlows.length; i++) {
				Pair<IActivity, String> key = new Pair<IActivity, String>(ad, inFlows[i].getId());
				ceInitials.add(inFlows[i].getId());
				if (syncChannelsEdge.containsKey(key)) {
					syncBool = true;
				}

				if (syncObjectsEdge.containsKey(key)) {
					// String ceIn2 = syncObjectsEdge.get(inFlows[i].getId());
					// nameObject = objectEdges.get(ceIn2);

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
				for (int i = 0; i < obj.size(); i++) { // creates the parallel output channels
					String oe = adUtils.createOE(typeObject);
					Pair<IActivity, String> key = new Pair<IActivity, String>(ad, outFlows[0].getId());
					syncObjectsEdge.put(key, oe); // just one output
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
				for (int i = 0; i < outFlows.length; i++) { // creates the parallel output channels
					String ce = adUtils.createCE();
					Pair<IActivity, String> key = new Pair<IActivity, String>(ad, outFlows[i].getId());
					syncChannelsEdge.put(key, ce);

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
				Pair<IActivity, String> key = new Pair<IActivity, String>(ad, inFlows[i].getId());
				if (syncChannelsEdge.containsKey(key)) {
					syncBool = true;
				}

				if (syncObjectsEdge.containsKey(key)) {
					// String oeIn = syncObjectsEdge.get(inFlows[i].getId());
					// nameObject = objectEdges.get(oeIn);

					typeObject = ((IObjectNode) inFlows[i].getSource()).getBase().getName();

					nameObjects.put(inFlows[i].getId(), typeObject);
					sync2Bool = true;
				}
			}

			joinNode.append(nameJoin + "(id) = (");

			for (int i = 0; i < ceInitials.size(); i++) {
				Pair<IActivity, String> key = new Pair<IActivity, String>(ad, ceInitials.get(i));
				String ceIn = syncChannelsEdge.get(key); // get the parallel input channels
				String oeIn = syncObjectsEdge.get(key);

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
						adUtils.setLocalInput(alphabet, joinNode, oeIn,
								adUtils.nameDiagramResolver(activityNode.getName()), typeObject, oeIn, typeObject);
						joinNode.append("SKIP) ||| ");
					} else {
						adUtils.ce(alphabet, joinNode, oeIn, "?" + typeObject + " -> ");
						adUtils.setLocalInput(alphabet, joinNode, oeIn,
								adUtils.nameDiagramResolver(activityNode.getName()), typeObject, oeIn, typeObject);
						joinNode.append("SKIP)");
					}
				}

			}

			joinNode.append("); ");

			adUtils.update(alphabet, joinNode, inFlows.length, outFlows.length, false);

			if (sync2Bool) {
				for (String nameObjectOut : objects) {
					adUtils.getLocal(alphabet, joinNode, nameObjectOut,
							adUtils.nameDiagramResolver(activityNode.getName()), nameObjectOut, typeObject);
				}
			}

			joinNode.append("(");

			if (sync2Bool) {
				for (int i = 0; i < objects.size(); i++) { // creates the parallel output channels
					Pair<IActivity, String> key = new Pair<IActivity, String>(ad, outFlows[0].getId());
					String oe = syncObjectsEdge.get(key); // just one output

					joinNode.append("(");

					if (i >= 0 && i < objects.size() - 1) {
						adUtils.ce(alphabet, joinNode, oe, "!" + objects.get(i) + " -> SKIP) |~| ");
						// countOe_ad--;
					} else {
						adUtils.ce(alphabet, joinNode, oe, "!" + objects.get(i) + " -> SKIP)");
					}
				}
			} else if (syncBool) {
				for (int i = 0; i < outFlows.length; i++) { // creates the parallel output channels
					Pair<IActivity, String> key = new Pair<IActivity, String>(ad, outFlows[i].getId());
					String ce = syncChannelsEdge.get(key);

					joinNode.append("(");

					if (i >= 0 && i < outFlows.length - 1) {
						adUtils.ce(alphabet, joinNode, ce, " -> SKIP) ||| ");
					} else {
						adUtils.ce(alphabet, joinNode, ce, " -> SKIP)");
					}
				}
			}

			joinNode.append("); ");

			joinNode.append(nameJoin + "(id)\n");

			joinNode.append(nameJoinTermination + "(id) = ");

			for (int i = 0; i < objects.size(); i++) {
				joinNode.append("(");
			}

			joinNode.append("(" + nameJoin + "(id) /\\ " + endDiagram + "(id))");

			for (int i = 0; i < objects.size(); i++) { // creates the parallel output channels
				joinNode.append(" [|{|");
				joinNode.append("get_" + objects.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName())
						+ "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
				joinNode.append("set_" + objects.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName())
						+ "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
				joinNode.append("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()) + ".id|}|] ");
				joinNode.append("Mem_" + adUtils.nameDiagramResolver(activityNode.getName()) + "_"
						+ adUtils.nameDiagramResolver(ad.getName()) + "_" + objects.get(i) + "_t(id,"
						+ adUtils.getDefaultValue(objects.get(i)) + "))");
			}

			if (objects.size() > 0) {
				joinNode.append(" \\{|");

				for (int i = 0; i < objects.size(); i++) {
					joinNode.append("get_" + objects.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName())
							+ "_" + adUtils.nameDiagramResolver(ad.getName()) + ",");
					joinNode.append("set_" + objects.get(i) + "_" + adUtils.nameDiagramResolver(activityNode.getName())
							+ "_" + adUtils.nameDiagramResolver(ad.getName()));
					if (i < objects.size() - 1) {
						joinNode.append(",");
					}
				}

				joinNode.append("|}");

			}

			joinNode.append("\n");

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

			nodes.append(joinNode.toString());
		}

		return activityNode;
	}*/
}
