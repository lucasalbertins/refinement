package com.ref.parser.activityDiagram;

import java.util.ArrayList;
import java.util.HashMap;

import com.ref.exceptions.ParsingException;
import com.ref.interfaces.activityDiagram.IActivity;
import com.ref.interfaces.activityDiagram.IActivityNode;
import com.ref.interfaces.activityDiagram.IFlow;
import com.ref.interfaces.activityDiagram.IObjectFlow;

public class ADDefineFork {

	private IActivity ad;

	private HashMap<Pair<IActivity, String>, ArrayList<String>> alphabetNode;
	private HashMap<Pair<IActivity, String>, String> syncChannelsEdge;
	private HashMap<Pair<IActivity, String>, String> syncObjectsEdge;
	private HashMap<String, String> objectEdges;
	private ADUtils adUtils;

	public ADDefineFork(IActivity ad, HashMap<Pair<IActivity, String>, ArrayList<String>> alphabetNode2,
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

	public String defineFork(IActivityNode activityNode) throws ParsingException {
		StringBuilder forkNode = new StringBuilder();
		ArrayList<String> alphabet = new ArrayList<>();
		String nameFork = adUtils.nameDiagramResolver(activityNode.getName()) + "_"
				+ adUtils.nameDiagramResolver(ad.getName());
		String nameForkTermination = adUtils.nameDiagramResolver(activityNode.getName()) + "_"
				+ adUtils.nameDiagramResolver(ad.getName()) + "_t";
		String endDiagram = "END_DIAGRAM_" + adUtils.nameDiagramResolver(ad.getName());
		IFlow[] outFlows = activityNode.getOutgoings();
		IFlow[] inFlows = activityNode.getIncomings();


		if (inFlows.length != 1) {
			throw new ParsingException("Fork node must have exactly one incoming edge.");
		}

		forkNode.append(nameFork + "(id) = ");

		IFlow inEdge = inFlows[0];
		Pair<IActivity, String> key = new Pair<IActivity, String>(ad, inEdge.getId());

		// case input is object
		if (inEdge instanceof IObjectFlow) {
			String oeIn;
        	String typeObject;
        	try {
				typeObject = ((IObjectFlow)inEdge).getBase().getName();
			} catch (NullPointerException e) {
				throw new ParsingException("Object flow does not have a type.");
			}
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
			
        	adUtils.oe(alphabet, forkNode, oeIn, "?x", " -> ");			

        	adUtils.update(alphabet, forkNode, inFlows.length, outFlows.length, false);

			forkNode.append("(");
			
			for (int i = 0; i < outFlows.length; i++) { // creates the parallel output channels
				
				if (!(outFlows[i] instanceof IObjectFlow)) {
					throw new ParsingException("If the incoming edge of fork node "+activityNode.getName()+" is a ObjectFlow, then\r\n" + 
							"all outgoing edges shall be ObjectFlows");
				}
				key = new Pair<IActivity, String>(ad, outFlows[i].getId());
				String oe;
	        	try {
					typeObject = ((IObjectFlow)outFlows[i]).getBase().getName();
				} catch (NullPointerException e) {
					throw new ParsingException("Object flow does not have a type.");
				}
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
				
				forkNode.append("(");

				if (i >= 0 && i < outFlows.length - 1) {
					adUtils.oe(alphabet, forkNode, oe, "!x" , " -> SKIP) ||| ");
				} else {
					adUtils.oe(alphabet, forkNode, oe, "!x" , " -> SKIP)");
				}
			}
		} else { // case is control
			String ceIn;

			if (syncChannelsEdge.containsKey(key)) {
				ceIn = syncChannelsEdge.get(key);
			} else {
				ceIn = adUtils.createCE();
				syncChannelsEdge.put(key, ceIn);
			}
			adUtils.ce(alphabet, forkNode, ceIn, " -> ");
			
			adUtils.update(alphabet, forkNode, inFlows.length, outFlows.length, false);

			forkNode.append("(");
			
			for (int i = 0; i < outFlows.length; i++) { // creates the parallel output channels
				if (outFlows[i] instanceof IObjectFlow) {
					throw new ParsingException("If the incoming edge of fork node "+activityNode.getName()+" is a ControlFlow, then\r\n" + 
							"all outgoing edges shall be ControlFlows");
				}
				key = new Pair<IActivity, String>(ad, outFlows[i].getId());
				String ce;
				if (syncChannelsEdge.containsKey(key)) {
					ce = syncChannelsEdge.get(key);
				} else {
					ce = adUtils.createCE();
					syncChannelsEdge.put(key, ce);
				}

				forkNode.append("(");

				if (i >= 0 && i < outFlows.length - 1) {
					adUtils.ce(alphabet, forkNode, ce, " -> SKIP) ||| ");
				} else {
					adUtils.ce(alphabet, forkNode, ce, " -> SKIP)");
				}
			}
		}
		forkNode.append("); ");

		forkNode.append(nameFork + "(id)\n");

		forkNode.append(nameForkTermination + "(id) = ");
		forkNode.append(nameFork + "(id) /\\ " + endDiagram + "(id)\n");

		alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()) + ".id");
		key = new Pair<IActivity, String>(ad, adUtils.nameDiagramResolver(activityNode.getName()));
		alphabetNode.put(key, alphabet);

		return forkNode.toString();
	}
/*
	public IActivityNode defineFork(IActivityNode activityNode, StringBuilder nodes, int code) {
		StringBuilder forkNode = new StringBuilder();
		ArrayList<String> alphabet = new ArrayList<>();
		String nameFork = adUtils.nameDiagramResolver(activityNode.getName()) + "_"
				+ adUtils.nameDiagramResolver(ad.getName());
		String nameForkTermination = adUtils.nameDiagramResolver(activityNode.getName()) + "_"
				+ adUtils.nameDiagramResolver(ad.getName()) + "_t";
		String endDiagram = "END_DIAGRAM_" + adUtils.nameDiagramResolver(ad.getName());
		IFlow[] outFlows = activityNode.getOutgoings();
		IFlow[] inFlows = activityNode.getIncomings();
		boolean syncBool = false;
		boolean sync2Bool = false;
		String nameObject = null;

		if (code == 0) {
			forkNode.append(nameFork + "(id) = ");

			for (int i = 0; i < inFlows.length; i++) {
				Pair<IActivity, String> key = new Pair<IActivity, String>(ad, inFlows[i].getId());
				if (syncChannelsEdge.containsKey(key)) {
					String ceIn = syncChannelsEdge.get(key);
					adUtils.ce(alphabet, forkNode, ceIn, " -> ");
					syncBool = true;
				}
			}

			for (int i = 0; i < inFlows.length; i++) {
				Pair<IActivity, String> key = new Pair<IActivity, String>(ad, inFlows[i].getId());
				if (syncObjectsEdge.containsKey(key)) {
					String oeIn = syncObjectsEdge.get(key);
					nameObject = objectEdges.get(oeIn);
					adUtils.oe(alphabet, forkNode, oeIn, "?" + nameObject, " -> ");
					sync2Bool = true;
				}
			}

			adUtils.update(alphabet, forkNode, inFlows.length, outFlows.length, false);

			forkNode.append("(");

			if (syncBool) {
				for (int i = 0; i < outFlows.length; i++) { // creates the parallel output channels
					String ce = adUtils.createCE();
					Pair<IActivity, String> key = new Pair<IActivity, String>(ad, outFlows[i].getId());
					syncChannelsEdge.put(key, ce);

					forkNode.append("(");

					if (i >= 0 && i < outFlows.length - 1) {
						adUtils.ce(alphabet, forkNode, ce, " -> SKIP) ||| ");
					} else {
						adUtils.ce(alphabet, forkNode, ce, " -> SKIP)");
					}
				}
			} else if (sync2Bool) {
				for (int i = 0; i < outFlows.length; i++) { // creates the parallel output channels
					String oe = adUtils.createOE(nameObject);
					Pair<IActivity, String> key = new Pair<IActivity, String>(ad, outFlows[i].getId());
					syncObjectsEdge.put(key, oe);
					objectEdges.put(oe, nameObject);
					forkNode.append("(");

					if (i >= 0 && i < outFlows.length - 1) {
						adUtils.ce(alphabet, forkNode, oe, "!" + nameObject + " -> SKIP) ||| ");
					} else {
						adUtils.ce(alphabet, forkNode, oe, "!" + nameObject + " -> SKIP)");
					}
				}
			}

			forkNode.append("); ");

			forkNode.append(nameFork + "(id)\n");

			forkNode.append(nameForkTermination + "(id) = ");
			forkNode.append(nameFork + "(id) /\\ " + endDiagram + "(id)\n");

			alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()) + ".id");
			Pair<IActivity, String> key = new Pair<IActivity, String>(ad,
					adUtils.nameDiagramResolver(activityNode.getName()));
			alphabetNode.put(key, alphabet);

			if (outFlows[0].getTarget() instanceof IInputPin) {
				for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
					if (activityNodeSearch instanceof IAction) {
						IInputPin[] inFlowPin = ((IAction) activityNodeSearch).getInputs();
						for (int y = 0; y < inFlowPin.length; y++) {
							if (inFlowPin[y].getId().equals(outFlows[0].getTarget().getId())) {
								activityNode = activityNodeSearch;
							}
						}
					}
				}
			} else {
				activityNode = outFlows[0].getTarget();
			}

			for (int x = 1; x < outFlows.length; x++) {
				if (outFlows[x].getTarget() instanceof IInputPin) {
					for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
						if (activityNodeSearch instanceof IAction) {
							IInputPin[] inFlowPin = ((IAction) activityNodeSearch).getInputs();
							for (int y = 0; y < inFlowPin.length; y++) {
								if (inFlowPin[y].getId().equals(outFlows[x].getTarget().getId())) {
									if (!queueNode.contains(activityNodeSearch)) {
										queueNode.add(activityNodeSearch);
									}
								}
							}
						}
					}
				} else {
					if (!queueNode.contains(outFlows[x].getTarget())) {
						queueNode.add(outFlows[x].getTarget());
					}
				}
			}

			nodes.append(forkNode.toString());
		} else if (code == 1) {
			if (syncBool) {
				for (int i = 0; i < outFlows.length; i++) { // creates the parallel output channels
					String ce = adUtils.createCE();
					Pair<IActivity, String> key = new Pair<IActivity, String>(ad, outFlows[i].getId());
					syncChannelsEdge.put(key, ce);

					forkNode.append("(");

					if (i >= 0 && i < outFlows.length - 1) {
						adUtils.ce(alphabet, forkNode, ce, " -> SKIP) ||| ");
					} else {
						adUtils.ce(alphabet, forkNode, ce, " -> SKIP)");
					}
				}
			} else if (sync2Bool) {
				for (int i = 0; i < outFlows.length; i++) { // creates the parallel output channels
					String oe = adUtils.createOE(nameObject);
					Pair<IActivity, String> key = new Pair<IActivity, String>(ad, outFlows[i].getId());
					syncObjectsEdge.put(key, oe);
					objectEdges.put(oe, nameObject);
					forkNode.append("(");

					if (i >= 0 && i < outFlows.length - 1) {
						adUtils.ce(alphabet, forkNode, oe, "!" + nameObject + " -> SKIP) ||| ");
					} else {
						adUtils.ce(alphabet, forkNode, oe, "!" + nameObject + " -> SKIP)");
					}
				}
			}

			if (outFlows[0].getTarget() instanceof IInputPin) {
				for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
					if (activityNodeSearch instanceof IAction) {
						IInputPin[] inFlowPin = ((IAction) activityNodeSearch).getInputs();
						for (int y = 0; y < inFlowPin.length; y++) {
							if (inFlowPin[y].getId().equals(outFlows[0].getTarget().getId())) {
								activityNode = activityNodeSearch;
							}
						}
					}
				}
			} else {
				activityNode = outFlows[0].getTarget();
			}

			for (int x = 1; x < outFlows.length; x++) {
				if (outFlows[x].getTarget() instanceof IInputPin) {
					for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
						if (activityNodeSearch instanceof IAction) {
							IInputPin[] inFlowPin = ((IAction) activityNodeSearch).getInputs();
							for (int y = 0; y < inFlowPin.length; y++) {
								if (inFlowPin[y].getId().equals(outFlows[x].getTarget().getId())) {
									if (!queueNode.contains(activityNodeSearch)) {
										queueNode.add(activityNodeSearch);
									}
								}
							}
						}
					}
				} else {
					if (!queueNode.contains(outFlows[x].getTarget())) {
						queueNode.add(outFlows[x].getTarget());
					}
				}
			}

			nodes.append(forkNode.toString());
		} else if (code == 1) {
			for (int i = 0; i < inFlows.length; i++) {
				Pair<IActivity, String> key = new Pair<IActivity, String>(ad, inFlows[i].getId());
				if (syncChannelsEdge.containsKey(key)) {
					syncBool = true;
				}
			}

			for (int i = 0; i < inFlows.length; i++) {
				Pair<IActivity, String> key = new Pair<IActivity, String>(ad, inFlows[i].getId());
				if (syncObjectsEdge.containsKey(key)) {
					String oeIn = syncObjectsEdge.get(key);
					nameObject = objectEdges.get(oeIn);
					sync2Bool = true;
				}
			}

			if (syncBool) {
				for (int i = 0; i < outFlows.length; i++) { // creates the parallel output channels
					String ce = adUtils.createCE();
					Pair<IActivity, String> key = new Pair<IActivity, String>(ad, outFlows[i].getId());
					syncChannelsEdge.put(key, ce);

					forkNode.append("(");

					if (i >= 0 && i < outFlows.length - 1) {
						adUtils.ce(alphabet, forkNode, ce, " -> SKIP) ||| ");
					} else {
						adUtils.ce(alphabet, forkNode, ce, " -> SKIP)");
					}
				}
			} else if (sync2Bool) {
				for (int i = 0; i < outFlows.length; i++) { // creates the parallel output channels
					String oe = adUtils.createOE(nameObject);
					Pair<IActivity, String> key = new Pair<IActivity, String>(ad, outFlows[i].getId());
					syncObjectsEdge.put(key, oe);
					objectEdges.put(oe, nameObject);
					forkNode.append("(");

					if (i >= 0 && i < outFlows.length - 1) {
						adUtils.ce(alphabet, forkNode, oe, "!" + nameObject + " -> SKIP) ||| ");
					} else {
						adUtils.ce(alphabet, forkNode, oe, "!" + nameObject + " -> SKIP)");
					}
				}
			}

		} else if (code == 2) {
			forkNode.append(nameFork + "(id) = ");

			for (int i = 0; i < inFlows.length; i++) {
				Pair<IActivity, String> key = new Pair<IActivity, String>(ad, inFlows[i].getId());
				if (syncChannelsEdge.containsKey(key)) {
					String ceIn = syncChannelsEdge.get(key);
					adUtils.ce(alphabet, forkNode, ceIn, " -> ");
					syncBool = true;
				}
			}

			for (int i = 0; i < inFlows.length; i++) {
				Pair<IActivity, String> key = new Pair<IActivity, String>(ad, inFlows[i].getId());
				if (syncObjectsEdge.containsKey(key)) {
					String oeIn = syncObjectsEdge.get(key);
					nameObject = objectEdges.get(oeIn);
					adUtils.oe(alphabet, forkNode, oeIn, "?" + nameObject, " -> ");
					sync2Bool = true;
				}
			}

			adUtils.update(alphabet, forkNode, inFlows.length, outFlows.length, false);

			forkNode.append("(");

			if (syncBool) {
				for (int i = 0; i < outFlows.length; i++) { // creates the parallel output channels
					Pair<IActivity, String> key = new Pair<IActivity, String>(ad, outFlows[i].getId());
					String ce = syncChannelsEdge.get(key);

					forkNode.append("(");

					if (i >= 0 && i < outFlows.length - 1) {
						adUtils.ce(alphabet, forkNode, ce, " -> SKIP) ||| ");
					} else {
						adUtils.ce(alphabet, forkNode, ce, " -> SKIP)");
					}
				}
			} else if (sync2Bool) {
				for (int i = 0; i < outFlows.length; i++) { // creates the parallel output channels
					Pair<IActivity, String> key = new Pair<IActivity, String>(ad, outFlows[i].getId());
					String oe = syncObjectsEdge.get(key);

					forkNode.append("(");

					if (i >= 0 && i < outFlows.length - 1) {
						adUtils.ce(alphabet, forkNode, oe, "!" + nameObject + " -> SKIP) ||| ");
					} else {
						adUtils.ce(alphabet, forkNode, oe, "!" + nameObject + " -> SKIP)");
					}
				}
			}

			forkNode.append("); ");

			forkNode.append(nameFork + "(id)\n");

			forkNode.append(nameForkTermination + "(id) = ");
			forkNode.append(nameFork + "(id) /\\ " + endDiagram + "(id)\n");

			alphabet.add("endDiagram_" + adUtils.nameDiagramResolver(ad.getName()) + ".id");
			Pair<IActivity, String> key = new Pair<IActivity, String>(ad,
					adUtils.nameDiagramResolver(activityNode.getName()));
			alphabetNode.put(key, alphabet);

			if (outFlows[0].getTarget() instanceof IInputPin) {
				for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
					if (activityNodeSearch instanceof IAction) {
						IInputPin[] inFlowPin = ((IAction) activityNodeSearch).getInputs();
						for (int y = 0; y < inFlowPin.length; y++) {
							if (inFlowPin[y].getId().equals(outFlows[0].getTarget().getId())) {
								activityNode = activityNodeSearch;
							}
						}
					}
				}
			} else {
				activityNode = outFlows[0].getTarget();
			}

			for (int x = 1; x < outFlows.length; x++) {
				if (outFlows[x].getTarget() instanceof IInputPin) {
					for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
						if (activityNodeSearch instanceof IAction) {
							IInputPin[] inFlowPin = ((IAction) activityNodeSearch).getInputs();
							for (int y = 0; y < inFlowPin.length; y++) {
								if (inFlowPin[y].getId().equals(outFlows[x].getTarget().getId())) {
									if (!queueNode.contains(activityNodeSearch)) {
										queueNode.add(activityNodeSearch);
									}
								}
							}
						}
					}
				} else {
					if (!queueNode.contains(outFlows[x].getTarget())) {
						queueNode.add(outFlows[x].getTarget());
					}
				}
			}

			nodes.append(forkNode.toString());
		}

		return activityNode;
	}*/
}
