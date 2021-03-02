package com.ref.parser.activityDiagram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.change_vision.jude.api.inf.model.IAction;
import com.change_vision.jude.api.inf.model.IActivity;
import com.change_vision.jude.api.inf.model.IActivityDiagram;
import com.change_vision.jude.api.inf.model.IActivityNode;
import com.change_vision.jude.api.inf.model.IActivityParameterNode;
import com.change_vision.jude.api.inf.model.IFlow;
import com.change_vision.jude.api.inf.model.IInputPin;
import com.change_vision.jude.api.inf.model.IOutputPin;
import com.ref.exceptions.ParsingException;

public class ADUtils {

	private IActivity ad;
	private IActivityDiagram adDiagram;

	public HashMap<String, Integer> countCall;
	private List<String> eventChannel;
	private List<String> lockChannel;
	private HashMap<String, String> parameterNodesOutputObject;
	private List<Pair<String, Integer>> callBehaviourNumber;
	private Map<Pair<String, String>, String> memoryLocal;
	private List<Pair<String, String>> memoryLocalChannel;
	private HashMap<String, List<String>> callBehaviourInputs;
	private HashMap<String, List<String>> callBehaviourOutputs;
	private List<Pair<String, Integer>> countAction;
	private List<Pair<String, Integer>> countSignal;
	private List<Pair<String, Integer>> countAccept;
	private HashMap<String, List<IActivity>> signalChannels;
	private List<String> signalChannelsLocal;
	private List<String> localSignalChannelsSync;
	// private List<String> createdSignal;
	// private List<String> createdAccept;
	private HashMap<String, Integer> allGuards;
	public HashMap<Pair<IActivity, String>, String> syncChannelsEdge;
	public HashMap<Pair<IActivity, String>, String> syncObjectsEdge;
	private ADParser adParser;
	
	//----------------------------------------------------------------------   
	public List<String> robo;
	public List<String> untilEvents;
	public HashMap<String, String> untilList;

	// ----------------------------------------------------------------------

	public ADUtils(IActivity ad, IActivityDiagram adDiagram, HashMap<String, Integer> countCall, List<String> eventChannel,
			List<String> lockChannel, HashMap<String, String> parameterNodesOutputObject, List<Pair<String, Integer>> callBehaviourNumber,
			Map<Pair<String, String>,String> memoryLocal, List<Pair<String, String>> memoryLocalChannel, HashMap<String, List<String>> callBehaviourInputs,
			HashMap<String, List<String>> callBehaviourOutputs, List<Pair<String, Integer>> countSignal, List<Pair<String, Integer>> countAccept,
			HashMap<String, List<IActivity>> signalChannels2, List<String> localSignalChannelsSync, HashMap<String, Integer> allGuards,
			List<String> createdSignal, List<String> createdAccept, HashMap<Pair<IActivity, String>, String> syncChannelsEdge2,
			HashMap<Pair<IActivity, String>, String> syncObjectsEdge2, List<String> signalChannelsLocal, ADParser adParser, List<String> robo, List<String> untilEvents, 
			HashMap<String, String> untilList) {

		this.ad = ad;
		this.adDiagram = adDiagram;
		this.countCall = countCall;
		this.eventChannel = eventChannel;
		this.lockChannel = lockChannel;
		this.parameterNodesOutputObject = parameterNodesOutputObject;
		this.callBehaviourNumber = callBehaviourNumber;
		this.memoryLocal = memoryLocal;
		this.memoryLocalChannel = memoryLocalChannel;
		this.callBehaviourInputs = callBehaviourInputs;
		this.callBehaviourOutputs = callBehaviourOutputs;
		this.countAction = countAction;
		this.countSignal = countSignal;
		this.countAccept = countAccept;
		this.signalChannels = signalChannels2;
		this.localSignalChannelsSync = localSignalChannelsSync;
		this.allGuards = allGuards;
		// this.createdSignal = createdSignal;
		// this.createdAccept = createdAccept;
		this.syncChannelsEdge = syncChannelsEdge2;
		this.syncObjectsEdge = syncObjectsEdge2;
		this.signalChannelsLocal = signalChannelsLocal;
		this.adParser = adParser;
		this.robo = robo;
		this.untilEvents = untilEvents;
		this.untilList = untilList;
	}

	public String createCE() {
		return "ce_" + nameDiagramResolver(ad.getName()) + ".id." + adParser.countCe_ad++;
	}

	public String createOE(String nameObject) {
		return "oe_" + nameObject + "_" + nameDiagramResolver(ad.getName()) + ".id."
				+ adParser.countOe_ad++;
	}

	//// ------------------------------------------------------------------------------------
	// public String createUntil() {
	// return "until_" + nameDiagramResolver(ad.getName()) + ".id." + adParser.countUntil_ad++;
	// }
	//// ------------------------------------------------------------------------------------
	public int startActivity(ArrayList<String> alphabetNode, StringBuilder action, String nameAD,
			List<String> inputPins) {
		int count = 0;
		count = addCountCall(nameDiagramResolver(nameAD));
		String startActivity = "startActivity_" + nameDiagramResolver(nameAD) + "." + count;
		alphabetNode.add(startActivity);
		callBehaviourNumber.add(new Pair<>(nameDiagramResolver(nameAD), count));

		List<String> outputPinsUsed = callBehaviourInputs.get(nameDiagramResolver(nameAD));
		if (outputPinsUsed == null) {
			outputPinsUsed = inputPins;
			callBehaviourInputs.put(nameAD, inputPins);
		}

		for (String pin : outputPinsUsed) {
			startActivity += "!" + pin;
		}

		action.append(startActivity + " -> ");

		return count;
	}

	public void endActivity(ArrayList<String> alphabetNode, StringBuilder action, String nameAD,
			List<String> outputPins, int count) {
		String endActivity = "endActivity_" + nameDiagramResolver(nameAD) + "." + count;
		alphabetNode.add(endActivity);

		List<String> outputPinsUsed = callBehaviourOutputs.get(nameDiagramResolver(nameAD));
		if (outputPinsUsed == null) {
			outputPinsUsed = outputPins;
			callBehaviourOutputs.put(nameAD, outputPins);
		}

		for (String pin : outputPinsUsed) {
			endActivity += "?" + pin;
		}

		action.append(endActivity + " -> ");
	}

	public void get(ArrayList<String> alphabetNode, StringBuilder action, String nameObject) {
		String get = "get_" + nameObject + "_" + nameDiagramResolver(ad.getName()) + ".id."
				+ adParser.countGet_ad++;
		alphabetNode.add(get);
		action.append(get + "?" + nameObject + " -> ");
	}

	public void set(ArrayList<String> alphabetNode, StringBuilder action, String nameMemory,
			String nameObject) {
		String set = "set_" + nameMemory + "_" + nameDiagramResolver(ad.getName()) + ".id."
				+ adParser.countSet_ad++;
		alphabetNode.add(set);
		action.append(set + "!" + nameObject + " -> ");
		parameterNodesOutputObject.put(nameMemory, nameObject);
	}

	public void setLocal(ArrayList<String> alphabetNode, StringBuilder action, String nameObject,
			String nameNode, String data, String datatype) {
		String set = "set_" + nameObject + "_" + nameNode + "_" + nameDiagramResolver(ad.getName())
				+ ".id." + adParser.countSet_ad++;
		alphabetNode.add(set);
		action.append(set + "!" + data + " -> ");
		Pair<String, String> memoryLocalPair = new Pair<String, String>(nameNode, nameObject);
		if (!memoryLocal.keySet().contains(memoryLocalPair)) {
			memoryLocal.put(memoryLocalPair, datatype);
		}
	}

	public void getLocal(ArrayList<String> alphabetNode, StringBuilder action, String nameObject,
			String nameNode, String data, String datatype) {
		String get = "get_" + nameObject + "_" + nameNode + "_" + nameDiagramResolver(ad.getName())
				+ ".id." + adParser.countGet_ad++;
		alphabetNode.add(get);
		action.append(get + "?" + data + " -> ");
		Pair<String, String> memoryLocalPair = new Pair<String, String>(nameNode, nameObject);
		if (!memoryLocal.keySet().contains(memoryLocalPair)) {
			memoryLocal.put(memoryLocalPair, datatype);
		}
	}

	public void setLocalInput(ArrayList<String> alphabetNode, StringBuilder action,
			String nameObject, String nameNode, String data, String oeIn, String datatype) {
		String set = "set_" + nameObject + "_" + nameNode + "_" + nameDiagramResolver(ad.getName())
				+ ".id." + adParser.countSet_ad++;
		alphabetNode.add(set);
		action.append(set + "!" + data + " -> ");
		Pair<String, String> memoryLocalPair = new Pair<String, String>(nameNode, nameObject);
		memoryLocalChannel.add(new Pair<String, String>(oeIn, nameObject));

		if (!memoryLocal.keySet().contains(memoryLocalPair)) {
			memoryLocal.put(memoryLocalPair, datatype);
		}
	}

	public void lock(ArrayList<String> alphabetNode, StringBuilder action, int inOut,
			String nameNode) {
		if (ADParser.containsCallBehavior) {
			if (inOut == 0) {
				String lock = "lock_" + nameNode;
				alphabetNode.add(lock);
				lockChannel.add(nameNode);
				action.append(lock + ".id.lock -> ");
			} else {
				String lock = "lock_" + nameNode;
				action.append(lock + ".id.unlock -> ");
			}
		}
	}

	public void event(ArrayList<String> alphabet, String nameAction, StringBuilder action)
			throws ParsingException {
		String partitionName;
		try {
			partitionName = this.ad.getPartitions()[0].getSubPartitions()[0].getName();
		} catch (Exception e) {
			throw new ParsingException(
					"The module should have a partition. \n Please, insert and try again.");
		}

//		alphabet.add(partitionName + "::" + nameAction);
		eventChannel.add(partitionName + "::" + nameAction);
		action.append(partitionName + "::" + nameAction + " -> "); 
		robo.add(partitionName + "::" + nameAction );
		
//		int idAction = 1;
//		int index = -1;
//
//		for (int i = 0; i < countAction.size(); i++) {
//			if (countAction.get(i).getKey().equals(nameAction)) {
//				idAction = countAction.get(i).getValue();
//				index = i;
//				break;
//			}
//		}
//
//		if (index >= 0) {
//			countAction.set(index, new Pair<String, Integer>(nameAction, idAction + 1));
//		} else {
//			countAction.add(new Pair<String, Integer>(nameAction, idAction + 1));
//		}
	}

	//    public void event(ArrayList<String> alphabet, String nameAction, StringBuilder action) {
	//        alphabet.add("event_" + nameAction+".id");
	//        eventChannel.add("event_" + nameAction);
	//        action.append("event_" + nameAction + ".id -> ");
	//    }

	public void ce(ArrayList<String> alphabetNode, StringBuilder action, String ce, String posCe) {
		alphabetNode.add(ce);// TODO olhar
		action.append(ce + posCe);
	}

	public void oe(ArrayList<String> alphabetNode, StringBuilder action, String oe, String data,
			String posOe) {
		alphabetNode.add(oe);// TODO olhar2
		action.append(oe + data + posOe);
	}

	public void until(ArrayList<String> alphabetNode, StringBuilder action, String eventName,
			String posUntil) {
		String partitionName;
		partitionName = this.ad.getPartitions()[0].getSubPartitions()[0].getName();

		adParser.countUntil_ad++;
//		alphabetNode.add("begin." + adParser.countUntil_ad + ",end." + adParser.countUntil_ad);//TODO olhar
		action.append("begin." + adParser.countUntil_ad + " -> end." + adParser.countUntil_ad + posUntil);
		robo.add(partitionName + "::" + eventName);
		untilEvents.add(partitionName + "::" + eventName);
		untilList.put("" + adParser.countUntil_ad, partitionName + "::" + eventName);
	}

	public void chaos(ArrayList<String> alphabetNode, StringBuilder action, String eventName,
			String posChaos) {
		String partitionName;
		partitionName = this.ad.getPartitions()[0].getSubPartitions()[0].getName();

		adParser.countUntil_ad++;
		alphabetNode.add("chaos." + adParser.callBehaviourList);// TODO olhar
		action.append("chaos." + adParser.callBehaviourList + " -> CHAOS(Events)");
		// robo.add(partitionName + "::" + eventName);
		// untilEvents.add(partitionName + "::" + eventName);
		// untilList.put("" + adParser.countUntil_ad, partitionName + "::" + eventName);
	}

	public void update(ArrayList<String> alphabetNode, StringBuilder action, int countInFlows,
			int countOutFlows, boolean canBeNegative) {
		int result = countOutFlows - countInFlows;

		if (result != 0) {
			if (countOutFlows == 0 && canBeNegative || countOutFlows > 0) {
				String update = "update_" + nameDiagramResolver(ad.getName()) + ".id."
						+ adParser.countUpdate_ad++;
//				alphabetNode.add(update);
				action.append(update + "!(" + countOutFlows + "-" + countInFlows + ") -> ");

				if (result < adParser.limiteInf) {
					adParser.limiteInf = result;

					if (adParser.limiteSup == -99) {
						adParser.limiteSup = result;
					}

				}

				if (result > adParser.limiteSup) {
					adParser.limiteSup = result;

					if (adParser.limiteInf == 99) {
						adParser.limiteInf = result;
					}

				}
			}
		}

	}

	public void clear(ArrayList<String> alphabetNode, StringBuilder action) {
		String clear = "clear_" + nameDiagramResolver(ad.getName()) + ".id."
				+ adParser.countClear_ad++;
		alphabetNode.add(clear);
		action.append(clear + " -> ");
	}

	public List<String> replaceExpression(String expression) {
		String value = "";
		char[] expChar = expression.toCharArray();
		List<String> expReplaced = new ArrayList<>();
		for (int i = 0; i < expChar.length; i++) {
			if (expChar[i] == '+' || expChar[i] == '-' || expChar[i] == '*' || expChar[i] == '/') {
				expReplaced.add(value);
				value = "";
			} else {
				value += expChar[i];
			}
		}

		if (!value.equals("")) { // add last value
			expReplaced.add(value);
		}

		return expReplaced;
	}

	public List<String> getObjects(IFlow flow, List<String> nodes) {
		List<String> objects = new ArrayList<>();

		if (!nodes.contains(flow.getSource().getId())) {
			nodes.add(flow.getSource().getId());
			if (flow.getSource() instanceof IActivityParameterNode) {
				objects.add(flow.getSource().getName());
			} else if (flow.getSource() instanceof IOutputPin) {
				IInputPin[] inPins = ((IAction) flow.getSource().getOwner()).getInputs();
				for (int x = 0; x < inPins.length; x++) {
					for (IFlow flowNode : inPins[x].getIncomings()) {
						objects.addAll(getObjects(flowNode, nodes));
					}
				}
			} else {
				for (IFlow flowNode : flow.getSource().getIncomings()) {
					objects.addAll(getObjects(flowNode, nodes));
				}
			}
		}

		return objects;
	}

	public void signal(ArrayList<String> alphabet, String nameSignal, StringBuilder signal) {
		String partitionName;
		partitionName = this.ad.getPartitions()[0].getSubPartitions()[0].getName();

		if (!localSignalChannelsSync.contains(partitionName + "::" + nameSignal)) {
			localSignalChannelsSync.add(partitionName + "::" + nameSignal);
		}

		//        if (!localSignalChannelsSync.contains("signal_" + nameSignal)) {
		//            localSignalChannelsSync.add("signal_" + nameSignal);
		//        }

		if (!signalChannels.containsKey(nameSignal)) {// TODO local onde modifica o signalchannels
			// Pair<IActivity,Integer> pair = new Pair<>(ad,1);
			List<IActivity> list = new ArrayList<>();
			list.add(ad);
			signalChannels.put(nameSignal, list);
		}
		/*
		 * else { List<Pair<IActivity,Integer>> list = new ArrayList<>(); list =
		 * signalChannels.get(nameSignal); Pair<IActivity,Integer> pair = null; int i=0;
		 * for(Pair<IActivity,Integer> aux : list) { if(aux.getKey().getId() == ad.getId()) { pair =
		 * new Pair<>(aux.getKey(),aux.getValue()+1); break; } i++; } if(pair != null) {
		 * list.remove(i); list.add(pair); } }
		 */

//		if (!signalChannelsLocal.contains(nameSignal)) {
//			signalChannelsLocal.add(nameSignal);
//		}

		int idSignal = 1;
		int index = -1;

		for (int i = 0; i < countSignal.size(); i++) {
//			if (countSignal.get(i).getKey().equals(nameCountResolver(nameSignal))) {
			if (countSignal.get(i).getKey().equals(nameSignal)) {
				idSignal = countSignal.get(i).getValue();
				index = i;
				break;
			}
		}

//		alphabet.add(partitionName + "::" + nameSignal + ".out");
		signal.append(partitionName + "::" + nameSignal + " -> ");
		robo.add(partitionName + "::" + nameSignal);

		//        alphabet.add("signal_" + nameSignal + ".id." + idSignal);
		//        signal.append("signal_" + nameSignal + ".id!" + idSignal + " -> ");

		if (index >= 0) {
//			countSignal.set(index, new Pair<String, Integer>(nameCountResolver(nameSignal), idSignal + 1));
			countSignal.set(index, new Pair<String, Integer>(nameSignal, idSignal + 1));
		} else {
//			countSignal.add(new Pair<String, Integer>(nameCountResolver(nameSignal), idSignal + 1));
			countSignal.add(new Pair<String, Integer>(nameSignal, idSignal + 1));
		}

	}

	public void accept(ArrayList<String> alphabet, String nameAccept, StringBuilder accept) {
		String partitionName;
		partitionName = this.ad.getPartitions()[0].getSubPartitions()[0].getName();
		// String nameAcceptResolved = partitionName + "::" + treatRobochartEventName(nameAccept);
		// if (!localSignalChannelsSync.contains(partitionName + "::" + nameAccept + ".in")) {
		// localSignalChannelsSync.add(partitionName + "::" + nameAccept + ".in");
		// }

		if (!localSignalChannelsSync.contains(partitionName + "::" + nameAccept)) {
			localSignalChannelsSync.add(partitionName + "::" + nameAccept);
		}

		if (!signalChannels.containsKey(nameAccept)) {
			// Pair<IActivity,Integer> pair = new Pair<>(ad,1);
			List<IActivity> list = new ArrayList<>();
			list.add(ad);
			signalChannels.put(nameAccept, list);
		}

		int idAccept = 1;
		int index = -1;

		for (int i = 0; i < countAccept.size(); i++) {
//			if (countAccept.get(i).getKey().equals(nameCountResolver(nameAccept))) {
			if (countAccept.get(i).getKey().equals(nameAccept)) {
				idAccept = countAccept.get(i).getValue();
				index = i;
				break;
			}
		}

//		alphabet.add(partitionName + "::" + nameAccept + ".in");
		accept.append(partitionName + "::" + nameAccept + " -> ");
		robo.add(partitionName + "::" + nameAccept);

//		alphabet.add("accept_" + nameAccept + ".id." + idAccept);
//		accept.append("accept_" + nameAccept + ".id." + idAccept + "?x -> ");

//		robo.put("accept_" + nameAccept + ".id." + idAccept, partitionName + "::" + nameAccept);

		if (index >= 0) {
//			countAccept.set(index, new Pair<String, Integer>(nameCountResolver(nameAccept), idAccept + 1));
			countAccept.set(index, new Pair<String, Integer>(nameAccept, idAccept + 1));
		} else {
//			countAccept.add(new Pair<String, Integer>(nameCountResolver(nameAccept), idAccept + 1));
			countAccept.add(new Pair<String, Integer>(nameAccept, idAccept + 1));
		}
	}

	public String nameDiagramResolver(String name) {
		return name.replace(" ", "").replace("!", "_").replace("@", "_").replace("%", "_")
				.replace("&", "_").replace("*", "_").replace("(", "_").replace(")", "_")
				.replace("+", "_").replace("-", "_").replace("=", "_").replace("?", "_")
				.replace(":", "_").replace("/", "_").replace(";", "_").replace(">", "_")
				.replace("<", "_").replace(",", "_").replace("{", "_").replace("}", "_")
				.replace("|", "_").replace("\\", "_").replace("\n", "_");
	}

	public static String nameResolver(String name) {
		return name.replace(" ", "").replace("!", "_").replace("@", "_").replace("%", "_")
				.replace("&", "_").replace("*", "_").replace("(", "_").replace(")", "_")
				.replace("+", "_").replace("-", "_").replace("=", "_").replace("?", "_")
				.replace(":", "_").replace("/", "_").replace(";", "_").replace(">", "_")
				.replace("<", "_").replace(",", "_").replace("{", "_").replace("}", "_")
				.replace("|", "_").replace("\\", "_").replace("\n", "_");
	}

	public String nameRobochartResolver(String name) {
		return name.replace(" ", "").replace("!", "_").replace("@", "_").replace("%", "_")
				.replace("&", "_").replace("*", "_").replace("(", ".").replace(")", "")
				.replace("+", "_").replace("-", "_").replace("=", "_").replace("?", "_")
				.replace(":", "_").replace("/", "_").replace(";", "_").replace(">", "_")
				.replace("<", "_").replace(",", ".").replace("{", "_").replace("}", "_")
				.replace("|", "_").replace("\\", "_").replace("\n", "_");
	}

	public String nameRobochartResolver(String name, String inOut) {

		if (NameRobochartResolverVerify(name)) {
			return name.replace(" ", "").replace("(", inOut + ".").replace(")", "");
		} else {
			return name + inOut;
		}

	}

	public boolean NameRobochartResolverVerify(String name) {

		for (int i = 0; i < name.length(); i++) {
			int ocorr = name.indexOf("(", i);
			if (ocorr > -1) {
				return true;
			}
		}
		return false;
	}
	
	public String nameCountResolver(String name) {
		return name.replace(".in", "").replace(".out", "");
	}

	public int addCountCall(String name) {
		int i = 1;
		if (countCall.containsKey(name)) {
			i = countCall.get(name);
			countCall.put(name, ++i);
		} else {
			countCall.put(name, i);
		}
		return i;
	}

	public int addCountGuard(String guard) {
		int i = 1;
		if (allGuards.containsKey(guard)) {
			i = allGuards.get(guard);
			allGuards.put(guard, ++i);
		} else {
			allGuards.put(guard, i);
		}

		return i;
	}

	public String getDefaultValue(String type) {
		HashMap<String, String> typesParameter = getParameterValueDiagram(type);

		String defaultValue = typesParameter.get(type).replace("{", "").replace("}", "")
				.replace("(", "").replace(")", "").split(",")[0];
		String defaultValueFinal = defaultValue.split("\\.\\.")[0];

		return defaultValueFinal;
	}

	public Pair<String, String> getInitialAndFinalParameterValue(String type) {
		Pair<String, String> initialAndFinalParameterValue;
		String[] allValues;
		String firstValue;
		String secondValue;
		HashMap<String, String> typesParameter = getParameterValueDiagram(type);

		String ListValue = typesParameter.get(type).replace("{", "").replace("}", "")
				.replace("(", "").replace(")", "");

		if (ListValue.contains("..")) {
			allValues = ListValue.split("\\.\\.");
			firstValue = allValues[0];
			secondValue = allValues[1];
		} else {
			allValues = ListValue.split(",");
			firstValue = allValues[0];
			secondValue = allValues[allValues.length - 1];
		}

		initialAndFinalParameterValue = new Pair<>(firstValue, secondValue);

		return initialAndFinalParameterValue;
	}

	public HashMap<String, String> getParameterValueDiagram(String type) {
		HashMap<String, String> typesParameter = new HashMap<>();
		String[] definition = adDiagram.getDefinition().replace("\n", "").replace(" ", "")
				.split(";");

		for (String def : definition) {
			String[] keyValue = def.split("=");

			if (keyValue.length == 1) {
				typesParameter.put(keyValue[0], keyValue[0]);
			} else {
				typesParameter.put(keyValue[0], keyValue[1]);
			}


		}

		return typesParameter;
	}

	/*
	 * private boolean isSignal(IActivityNode activityNode) { return (activityNode instanceof
	 * IAction && ((((IAction) activityNode).isSendSignalAction() &&
	 * createdSignal.contains(activityNode.getId())) || (((IAction)
	 * activityNode).isAcceptEventAction() && createdAccept.contains(activityNode.getId())))); }
	 */

	public int countAmount(IActivityNode activityNode) {
		int input = 0;
		if (activityNode != null) {
			input = 0;
			IFlow[] inFlow = activityNode.getIncomings();

			for (int i = 0; i < inFlow.length; i++) {
				Pair<IActivity, String> key = new Pair<IActivity, String>(ad, inFlow[i].getId());
				if (syncChannelsEdge.containsKey(key)) {
					input++;
				}
			}

			if (activityNode instanceof IAction) {
				IInputPin[] inPin = ((IAction) activityNode).getInputs();

				for (int i = 0; i < inPin.length; i++) {
					IFlow[] inFlowPin = inPin[i].getIncomings();
					for (int x = 0; x < inFlowPin.length; x++) {
						Pair<IActivity, String> key = new Pair<IActivity, String>(ad,
								inFlowPin[x].getId());
						if (syncObjectsEdge.containsKey(key)) {
							input++;
						}
					}
				}

			} else {
				for (int i = 0; i < inFlow.length; i++) {
					Pair<IActivity, String> key = new Pair<IActivity, String>(ad,
							inFlow[i].getId());
					if (syncObjectsEdge.containsKey(key)) {
						input++;
					}
				}
			}
		}

		return input;
	}

	public static boolean isInteger(String s) {
		return isInteger(s, 10);
	}

	private static boolean isInteger(String s, int radix) {
		if (s.isEmpty())
			return false;
		for (int i = 0; i < s.length(); i++) {
			if (i == 0 && s.charAt(i) == '-') {
				if (s.length() == 1)
					return false;
				else
					continue;
			}
			if (Character.digit(s.charAt(i), radix) < 0)
				return false;
		}
		return true;
	}

	public List<Pair<String, Integer>> getCallBehaviourNumber() {
		return callBehaviourNumber;
	}

	public void setCallBehaviourNumber(List<Pair<String, Integer>> callBehaviourNumber) {
		this.callBehaviourNumber = callBehaviourNumber;
	}

	public HashMap<String, List<String>> getCallBehaviourInputs() {
		return callBehaviourInputs;
	}

	public void setCallBehaviourInputs(HashMap<String, List<String>> callBehaviourInputs) {
		this.callBehaviourInputs = callBehaviourInputs;
	}

	public HashMap<String, List<String>> getCallBehaviourOutputs() {
		return callBehaviourOutputs;
	}

	public void setCallBehaviourOutputs(HashMap<String, List<String>> callBehaviourOutputs) {
		this.callBehaviourOutputs = callBehaviourOutputs;
	}

	public String alphabetRobo(String alphabet) {
		StringBuilder channels = new StringBuilder();
		channels.append("alphabet_robochart_" + ADUtils.nameResolver(ad.getName()) + " = ");
		channels.append(alphabet);
		return channels.toString();
	}

	// public String alphabetRobo2() {
	// StringBuilder channels = new StringBuilder();
	// channels.append("alphabet_robochart_" + ad.getName() + " = {| ");
	//// channels.append("alphabet_robochart_" + ad.getName() + " = ");
	// for (int i = 0; i < robo.size(); i++) {
	// channels.append(robo.get(i));
	//
	// if ((i + 1) < robo.size()) {
	// channels.append(", ");
	// } else {
	// channels.append(" |}\n");
	// }
	// }
	//// channels.append(alphabet);
	// return channels.toString();
	// }

	public String alphabetUntil() {
		StringBuilder channels = new StringBuilder();

		channels.append(
				"Wait_" + ad.getName() + " = WAIT(alphabet_robochart_" + ADUtils.nameResolver(ad.getName()) + ", ");
		for (int i = 0; i < untilEvents.size(); i++) {
			channels.append(untilEvents.get(i));

			if ((i + 1) < untilEvents.size()) {
				channels.append(", ");
			} else {
				channels.append(")\n");
			}
		}

		return channels.toString();
	}

	public String printUntils() {
		StringBuilder channels = new StringBuilder();
		for (String i : untilList.keySet()) {
			channels.append("Wait_" + ADUtils.nameResolver(ad.getName()) + "_" + i + " = WAIT(alphabet_robochart_"
					 + ADUtils.nameResolver(ad.getName()) + ", " + untilList.get(i) + ")\n\n");
			channels.append("Wait_" + ADUtils.nameResolver(ad.getName()) + "_control_" + i + " = begin." + i + " -> Wait_"
					 + ADUtils.nameResolver(ad.getName()) +  "_" + i + "; end." + i + " -> Wait_" + ADUtils.nameResolver(ad.getName())
					+ "_control_" + i + "\n\n");
		}
		return channels.toString();
	}

	public String printAny() {
		StringBuilder channels = new StringBuilder();
		for (int i = 1; i <= adParser.countAny_ad; i++) {
			channels.append("Wait_" + ADUtils.nameResolver(ad.getName()) + "_chaos_" + i + " = chaos." + i
					+ " -> CHAOS(alphabet_robochart_" + ad.getName() + ")\n\n");
		}
		return channels.toString();
	}

	public String printControlProcesses() {
		StringBuilder channels = new StringBuilder();
		// channels.append("Wait_control_processes = {Wait_" + ad.getName() + "_control_" + i +
		// "}\n");
		channels.append("Wait_control_processes = {");
		int c = 0;

		for (String i : untilList.keySet()) {
			channels.append("Wait_" + ADUtils.nameResolver(ad.getName()) + "_control_" + i);

			if ((c + 1) < untilList.size()) {
				channels.append(", ");
			}
			// else {
			// channels.append("}\n");
			// }
			c++;
		}

		if (adParser.countAny_ad > 0 && untilList.size() > 0) {
			channels.append(", ");
		}

		c = 0;
		for (int i = 1; i <= adParser.countAny_ad; i++) {
			channels.append("Wait_" + ADUtils.nameResolver(ad.getName()) + "_chaos_" + i);
			if ((c + 1) < adParser.countAny_ad) {
				channels.append(", ");
			}
			c++;
		}

		channels.append("}\n");
		return channels.toString();
	}

	public void createAny(ArrayList<String> alphabet, StringBuilder callBehaviour) {
		int index = ++adParser.countAny_ad;
		alphabet.add("chaos." + index);
		callBehaviour.append("chaos." + index + " -> SKIP;");

	}
	
//	public String mapEvents() {
//		StringBuilder channels = new StringBuilder();
//		channels.append("Valores mapeados = {");
//		int c = 0;
//		for (String i : robo.values()) {
//			channels.append(i);
//			if ((c + 1) < robo.size()) {
//				channels.append(", ");
//			}
//			c++;
//		}
//		channels.append("}\n");
//
//		channels.append("Chaves mapeadas = {");
//		c = 0;
//		for (String i : robo.keySet()) {
//			channels.append(i);
//			if ((c + 1) < robo.size()) {
//				channels.append(", ");
//			}
//			c++;
//		}
//		
//		channels.append("}\n");
//		return channels.toString();
//	}

}
