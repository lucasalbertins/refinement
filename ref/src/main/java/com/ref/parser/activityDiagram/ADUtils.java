package com.ref.parser.activityDiagram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ref.astah.adapter.ActivityNode;
import com.ref.exceptions.ParsingException;
import com.ref.interfaces.activityDiagram.IAction;
import com.ref.interfaces.activityDiagram.IActivity;
import com.ref.interfaces.activityDiagram.IActivityDiagram;
import com.ref.interfaces.activityDiagram.IActivityNode;
import com.ref.interfaces.activityDiagram.IActivityParameterNode;
import com.ref.interfaces.activityDiagram.IControlFlow;
import com.ref.interfaces.activityDiagram.IFlow;
import com.ref.interfaces.activityDiagram.IInputPin;
import com.ref.interfaces.activityDiagram.IObjectFlow;
import com.ref.interfaces.activityDiagram.IOutputPin;
import com.sun.tools.doclets.internal.toolkit.util.DocFinder.Output;

public class ADUtils {

	private IActivity ad;
	private IActivityDiagram adDiagram;

	public HashMap<String, Integer> countCall;
	private List<String> eventChannel;
	private List<String> lockChannel;
	private HashMap<String, String> parameterNodesOutputObject;
	private List<Pair<String, Integer>> callBehaviourNumber;
	private Map<Pair<String, String>,String> memoryLocal;
	private List<Pair<String, String>> memoryLocalChannel;
	private HashMap<String, List<String>> callBehaviourInputs;
	private HashMap<String, List<String>> callBehaviourOutputs;
	private List<Pair<String, Integer>> countSignal;
	private List<Pair<String, Integer>> countAccept;
	private List<Pair<String, Integer>> countAction;
	private HashMap<String, List<IActivity>> signalChannels;
	private List<String> signalChannelsLocal;
	private List<String> localSignalChannelsSync;
	private HashMap<String,Integer> allGuards;
	public HashMap<Pair<IActivity,String>, String> syncChannelsEdge;
	public HashMap<Pair<IActivity,String>, String> syncObjectsEdge;
	private HashMap<String, String> objectEdges;
	private ADParser adParser;
	public List<String> robo;
	public List<String> untilEvents;
	public HashMap<String, String> untilList;
	//private HashMap<Pair<String,String>, String> memoryPinLocal;
	private List<String> waitAccept;
	private boolean hasPins = false;

	public ADUtils(IActivity ad, IActivityDiagram adDiagram, HashMap<String, Integer> countCall, List<String> eventChannel,
			List<String> lockChannel, HashMap<String, String> parameterNodesOutputObject, List<Pair<String, Integer>> callBehaviourNumber,
			Map<Pair<String, String>,String> memoryLocal, List<Pair<String, String>> memoryLocalChannel, HashMap<String, List<String>> callBehaviourInputs,
			HashMap<String, List<String>> callBehaviourOutputs, List<Pair<String, Integer>> countSignal, List<Pair<String, Integer>> countAccept,
			HashMap<String, List<IActivity>> signalChannels2, List<String> localSignalChannelsSync, HashMap<String, Integer> allGuards,
			List<String> createdSignal, List<String> createdAccept, HashMap<Pair<IActivity, String>, String> syncChannelsEdge2,
			HashMap<Pair<IActivity, String>, String> syncObjectsEdge2, HashMap<String, String> objectEdges2, List<String> signalChannelsLocal, ADParser adParser,
			List<String> robo, List<String> untilEvents, HashMap<String, String> untilList, List<Pair<String, Integer>> countAction, List<String> createdAction,
			List<String> waitAccept) {

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
		this.countSignal = countSignal;
		this.countAccept = countAccept;
		this.countAction = countAction;
		this.signalChannels = signalChannels2;
		this.localSignalChannelsSync = localSignalChannelsSync;
		this.allGuards = allGuards;
		this.syncChannelsEdge = syncChannelsEdge2;
		this.syncObjectsEdge = syncObjectsEdge2;
		this.signalChannelsLocal = signalChannelsLocal;
		this.objectEdges = objectEdges2;
		this.adParser = adParser;
		this.robo = robo;
		this.untilEvents = untilEvents;
		this.untilList = untilList;
		//this.memoryPinLocal = memoryPinLocal;
		this.waitAccept = waitAccept;
	}

	public String createCE() {
		return "ce_" + nameDiagramResolver(ad.getName()) + ".id." + adParser.countCe_ad++;
	}

	public String createOE() {
		return "oe_" + adParser.countOe_ad++ + "_" + nameDiagramResolver(ad.getName()) + ".id" ;
	}

	public int startActivity(ArrayList<String> alphabetNode, StringBuilder action, String nameAD, List<String> inputPins) {
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

	public void endActivity(ArrayList<String> alphabetNode, StringBuilder action, String nameAD, List<String> outputPins, int count) {
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
		String get = "get_" + nameObject + "_" + nameDiagramResolver(ad.getName()) + ".id." + adParser.countGet_ad++;
		//        alphabetNode.add(get);
		action.append(get + "?" + nameObject + " -> ");
	}

	public void set(ArrayList<String> alphabetNode, StringBuilder action, String nameMemory, String nameObject) {
		String set = "set_" + nameMemory + "_" + nameDiagramResolver(ad.getName()) + ".id." + adParser.countSet_ad++;
		//        alphabetNode.add(set);
		action.append(set +"!" + nameObject + " -> ");
		parameterNodesOutputObject.put(nameMemory, nameObject);
	}

	public void setLocal(ArrayList<String> alphabetNode, StringBuilder action, String nameObject, String nameNode, String data, String datatype) {
		String set = "set_" + nameObject + "_" + nameNode + "_" + nameDiagramResolver(ad.getName()) + ".id." + adParser.countSet_ad++;
		//        alphabetNode.add(set);
		action.append(set + "!" + data + " -> ");
		Pair<String, String> memoryLocalPair = new Pair<String, String>(nameNode, nameObject);
		if (!memoryLocal.keySet().contains(memoryLocalPair)) {
			memoryLocal.put(memoryLocalPair,datatype);
		}
	}

	public void getLocal(ArrayList<String> alphabetNode, StringBuilder action, String nameObject, String nameNode, String data, String datatype) {
		String get = "get_" + nameObject + "_" + nameNode + "_" + nameDiagramResolver(ad.getName()) + ".id." + adParser.countGet_ad++;
		//        alphabetNode.add(get);
		action.append(get + "?" + data + " -> ");
		Pair<String, String> memoryLocalPair = new Pair<String, String>(nameNode, nameObject);
		if (!memoryLocal.keySet().contains(memoryLocalPair)) {
			memoryLocal.put(memoryLocalPair,datatype);
		}
	}

	public void setLocalInput(ArrayList<String> alphabetNode, StringBuilder action, String nameObject, String nameNode, String data, String oeIn, String datatype) {
		String set = "set_" + nameObject + "_" + nameNode + "_" + nameDiagramResolver(ad.getName()) + ".id." + adParser.countSet_ad++;
		//        alphabetNode.add(set);
		action.append(set + "!" + data + " -> ");
		Pair<String, String> memoryLocalPair = new Pair<String, String>(nameNode, nameObject);
		memoryLocalChannel.add(new Pair<String, String>(oeIn, nameObject));

		if (!memoryLocal.keySet().contains(memoryLocalPair)) {
			memoryLocal.put(memoryLocalPair,datatype);
		}
	}

	public void lock(ArrayList<String> alphabetNode, StringBuilder action, int inOut, String nameNode) {
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
	////////////////////////////////////////////////////////////////////////////////////////
	public void event(ArrayList<String> alphabet, String nameAction, StringBuilder action)
			throws ParsingException {
		String partitionName;
		try {
			partitionName = this.ad.getPartitions()[0].getSubPartitions()[0].getName();
		} catch (Exception e) {
			throw new ParsingException(
					"The module should have a partition. \n Please, insert and try again.");
		}

		//		eventChannel.add(partitionName + "::" + nameAction);
		action.append(partitionName + "::" + nameAction + " -> ");
		robo.add(partitionName + "::" + nameAction);
	}
	////////////////////////////////////////////////////////////////////////////////////////
	//    public void event(ArrayList<String> alphabet, String nameAction, StringBuilder action) {
	//        alphabet.add("event_" + nameAction+".id");
	//        eventChannel.add("event_" + nameAction);
	//        action.append("event_" + nameAction + ".id -> ");
	//    }

	public void ce(ArrayList<String> alphabetNode, StringBuilder action, String ce, String posCe) {
		alphabetNode.add(ce);
		action.append(ce + posCe);
	}

	public void oe(ArrayList<String> alphabetNode, StringBuilder action, String oe, String data, String posOe) {
		alphabetNode.add(oe);
		action.append(oe + data + posOe);
	}

	////////////////////////////////////////////////////////////////////////////////////////
	public void until(ArrayList<String> alphabetNode, StringBuilder action, String eventName,
			String posUntil) {
		String partitionName;
		partitionName = this.ad.getPartitions()[0].getSubPartitions()[0].getName();

		adParser.countUntil_ad++;
//		if (!hasPins) {
//			 alphabetNode.add("begin." + adParser.countUntil_ad + ",end." + adParser.countUntil_ad);//TODO olhar
//		}
		action.append("begin." + adParser.countUntil_ad + " -> end." + adParser.countUntil_ad + posUntil);
		robo.add(partitionName + "::" + eventName);
		untilEvents.add(partitionName + "::" + eventName);
		untilList.put("" + adParser.countUntil_ad, partitionName + "::" + eventName);
	}

	////////////////////////////////////////////////////////////////////////////////////////
	public void untilWithPins(ArrayList<String> alphabet, StringBuilder accept,
			IActivityNode activityNode, IOutputPin[] outPins) {
		String partitionName;
		partitionName = this.ad.getPartitions()[0].getSubPartitions()[0].getName();
		hasPins = true;
		
		int idAccept = 1;
		String nAccept = nameRobochartResolver(activityNode.getName(), ".in");
		for (int i = 0; i < countAccept.size(); i++) {
			if (countAccept.get(i).getKey().equals(nAccept)) {
				idAccept = countAccept.get(i).getValue();
				break;
			}
		}

		//		waitAccept.add(partitionName + "::" + nameRobochartResolver(activityNode.getName(), ".in"));

		// WAIT_accept_ultrasonic_1(alphabet) = 
		waitAccept.add("WAIT_accept_" + nameRobochartResolver(activityNode.getName()) + "_" + idAccept + "(id, alphabet) = \n");
		// NRecurse(diff(alphabet, {| PathPlanningSM::ultrasonic.in |}), WAIT_accept_ultrasonic_1(alphabet)) 
		waitAccept.add("NRecurse(diff(alphabet, {|" + partitionName + "::" + nameRobochartResolver(activityNode.getName(), ".in") + "|}), WAIT_accept_" + nameDiagramResolver(activityNode.getName()) + "_" + idAccept + "(id, alphabet))\n |~| \n");


		accept.append("WAIT_accept_" + nameDiagramResolver(activityNode.getName())+"_" + idAccept+"(id, alphabet_robochart_"+ 
				nameDiagramResolver(ad.getName()) + ") [| {|");
		int c = 0;
		for (int i = 0; i < outPins.length; i++) {
			// set_u_ultrasonic_P_Teste.id
			accept.append("set_"+nameDiagramResolver(outPins[i].getName())+"_"+nameDiagramResolver(activityNode.getName())+
					"_"+nameDiagramResolver(ad.getName())+".id"+",");
			// PathPlanningSM::ultrasonic.in?u -> set_u_ultrasonic_P_Teste.id?c!u -> SKIP
			waitAccept.add(partitionName + "::" + nAccept + "?" + nameDiagramResolver(outPins[i].getName()) + " -> set_" + nameDiagramResolver(outPins[i].getName()) 
			+ "_" + nameDiagramResolver(activityNode.getName()) + "_" + nameDiagramResolver(ad.getName()) + ".id?c!" + outPins[i].getName() + " -> SKIP\n\n");
		}
		accept.setCharAt(accept.length()-1, ' ');
		accept.append("|} |> ");

	}

	public void getMemoryPin(ArrayList<String> alphabetNode, StringBuilder action, String nameObject, String nameNode, String data, String datatype) {
		//get_accept_ultrasonic_u_P_Teste?u
		String get = "get_" + nameObject + "_" + nameNode + "_" + nameDiagramResolver(ad.getName())+"_" + data+ "_" +
				nameDiagramResolver(ad.getName());
		//        alphabetNode.add(get);
		action.append(get + "?" + data + " -> ");
		Pair<String, String> memoryLocalPair = new Pair<String, String>(nameObject+"_"+nameNode, data);
		//        if (!memoryPinLocal.keySet().contains(memoryLocalPair)) {
		//        	memoryPinLocal.put(memoryLocalPair,datatype);
		//        }

	}

	////////////////////////////////////////////////////////////////////////////////////////

	public void chaos(ArrayList<String> alphabetNode, StringBuilder action, String eventName,
			String posChaos) {
		String partitionName;
		partitionName = this.ad.getPartitions()[0].getSubPartitions()[0].getName();

		adParser.countUntil_ad++;
		//		alphabetNode.add("chaos." + adParser.callBehaviourList);// TODO olhar
		action.append("chaos." + adParser.callBehaviourList + " -> CHAOS(Events)");
		// robo.add(partitionName + "::" + eventName);
		// untilEvents.add(partitionName + "::" + eventName);
		// untilList.put("" + adParser.countUntil_ad, partitionName + "::" + eventName);
	}
	////////////////////////////////////////////////////////////////////////////////////////

	public void update(ArrayList<String> alphabetNode, StringBuilder action, int countInFlows, int countOutFlows, boolean canBeNegative) {
		int result = countOutFlows - countInFlows;

		if (result != 0) {
			if (countOutFlows == 0 && canBeNegative || countOutFlows > 0) {
				String update = "update_" + nameDiagramResolver(ad.getName()) + ".id." + adParser.countUpdate_ad++;
				//                alphabetNode.add(update);
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
		String clear = "clear_" + nameDiagramResolver(ad.getName()) + ".id." + adParser.countClear_ad++;
		//alphabetNode.add(clear);
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

		if (!value.equals("")) {    //add last value
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
				IInputPin[] inPins = ((IAction) ((IOutputPin)flow.getSource()).getOwner()).getInputs();
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

	public void signal(ArrayList<String> alphabet, String nameSignal, StringBuilder signal, IInputPin[] inPins) {
		String partitionName;
		partitionName = this.ad.getPartitions()[0].getSubPartitions()[0].getName();

		if (!localSignalChannelsSync.contains(partitionName + "::" + nameSignal)) {
			localSignalChannelsSync.add(partitionName + "::" + nameSignal);
		}

		if (!signalChannels.containsKey(nameSignal)) {// TODO local onde modifica o signal channels
			List<IActivity> list = new ArrayList<>();
			list.add(ad);
			signalChannels.put(nameSignal, list);
		}

		if (!signalChannelsLocal.contains(nameSignal)) {
			signalChannelsLocal.add(nameSignal);
		}

		int idSignal = 1;
		int index = -1;

		for (int i = 0; i < countSignal.size(); i++) {
			if (countSignal.get(i).getKey().equals(nameSignal)) {
				idSignal = countSignal.get(i).getValue();
				index = i;
				break;
			}
		}

		//		signal.append(partitionName + "::" + nameSignal + " -> ");		
		signal.append(partitionName + "::" + nameSignal);
		for (int i = 0; i < inPins.length; i++) {
			signal.append("?"+inPins[i].getName());
		}
		signal.append(" -> ");

		robo.add(partitionName + "::" + nameSignal);

		if (index >= 0) {
			countSignal.set(index, new Pair<String, Integer>(nameSignal, idSignal + 1));
			//			ADParser.IdSignals.put(activityNode.getId(),idSignal);
		} else {
			countSignal.add(new Pair<String, Integer>(nameSignal, idSignal + 1));
			//			ADParser.IdSignals.put(activityNode.getId(),idSignal);
		}

	}

	public void accept(ArrayList<String> alphabet, String nameAccept, StringBuilder accept, IOutputPin[] outPins) {
		String partitionName;
		partitionName = this.ad.getPartitions()[0].getSubPartitions()[0].getName();

		if (!localSignalChannelsSync.contains(partitionName + "::" + nameAccept)) {
			localSignalChannelsSync.add(partitionName + "::" + nameAccept);
		}

		if (!signalChannels.containsKey(nameAccept)) {
			List<IActivity> list = new ArrayList<>();
			list.add(ad);
			signalChannels.put(nameAccept, list);
		}

		int idAccept = 1;
		int index = -1;

		for (int i = 0; i < countAccept.size(); i++) {
			if (countAccept.get(i).getKey().equals(nameAccept)) {
				idAccept = countAccept.get(i).getValue();
				index = i;
				break;
			}
		}
		//		accept.append(partitionName + "::" + nameAccept + " -> ");
		accept.append(partitionName + "::" + nameAccept);
		for (int i = 0; i < outPins.length; i++) {
			accept.append("?"+outPins[i].getName());
		}
		accept.append(" -> ");

		robo.add(partitionName + "::" + nameAccept);

		if (index >= 0) {
			countAccept.set(index, new Pair<String, Integer>(nameAccept, idAccept + 1));
			//			ADParser.IdSignals.put(activityNode.getId(),idAccept);
		} else {
			countAccept.add(new Pair<String, Integer>(nameAccept, idAccept + 1));
			//			ADParser.IdSignals.put(activityNode.getId(),idAccept);
		}
	}

	public String nameDiagramResolver(String name) {
		return name.replace(" ", "").replace("!", "_").replace("@", "_")
				.replace("%", "_").replace("&", "_").replace("*", "_")
				.replace("(", "_").replace(")", "_").replace("+", "_")
				.replace("-", "_").replace("=", "_").replace("?", "_")
				.replace(":", "_").replace("/", "_").replace(";", "_")
				.replace(">", "_").replace("<", "_").replace(",", "_")
				.replace("{", "_").replace("}", "_").replace("|", "_")
				.replace("\\", "_").replace("\n", "_");
	}

	public static String nameResolver(String name) {
		return name.replace(" ", "").replace("!", "_").replace("@", "_")
				.replace("%", "_").replace("&", "_").replace("*", "_")
				.replace("(", "_").replace(")", "_").replace("+", "_")
				.replace("-", "_").replace("=", "_").replace("?", "_")
				.replace(":", "_").replace("/", "_").replace(";", "_")
				.replace(">", "_").replace("<", "_").replace(",", "_")
				.replace("{", "_").replace("}", "_").replace("|", "_")
				.replace("\\", "_").replace("\n", "_");
	}

	////////////////////////////////////////////////////////////////////////////////////////
	public String nameRobochartResolver(String name) {
		return name.replace(" ", "").replace("!", "_").replace("@", "_").replace("%", "_")
				.replace("&", "_").replace("*", "_").replace("(", "Call.").replace(")", "")
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
	////////////////////////////////////////////////////////////////////////////////////////

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

		String defaultValue = typesParameter.get(type).replace("{", "").replace("}", "").replace("(", "")
				.replace(")", "").split(",")[0];
		String defaultValueFinal = defaultValue.split("\\.\\.")[0];

		return defaultValueFinal;
	}

	public Pair<String, String> getInitialAndFinalParameterValue(String type) {
		Pair<String, String> initialAndFinalParameterValue;
		String[] allValues;
		String firstValue;
		String secondValue;
		HashMap<String, String> typesParameter = getParameterValueDiagram(type);

		String ListValue = typesParameter.get(type).replace("{", "").replace("}", "").replace("(", "")
				.replace(")", "");

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
		String[] definition = adDiagram.getDefinition().replace("\n", "").replace(" ", "").split(";");

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

	public int countAmount(IActivityNode activityNode) {
		int input = 0;
		if (activityNode != null) {
			input = 0;
			IFlow[] inFlow = activityNode.getIncomings();

			for (int i = 0; i < inFlow.length; i++) {
				Pair<IActivity,String> key = new Pair<IActivity, String>(ad,inFlow[i].getId());
				if (syncChannelsEdge.containsKey(key)) {
					input++;
				}
			}


			if (activityNode instanceof IAction) {
				IInputPin[] inPin = ((IAction) activityNode).getInputs();

				for (int i = 0; i < inPin.length; i++) {
					IFlow[] inFlowPin = inPin[i].getIncomings();
					for (int x = 0; x < inFlowPin.length; x++) {
						Pair<IActivity,String> key = new Pair<IActivity, String>(ad,inFlowPin[x].getId());
						if (syncObjectsEdge.containsKey(key)) {
							input++;
						}
					}
				}

			} else {
				for (int i = 0; i < inFlow.length; i++) {
					Pair<IActivity,String> key = new Pair<IActivity, String>(ad,inFlow[i].getId());
					if (syncObjectsEdge.containsKey(key)) {
						input++;
					}
				}
			}
		}

		return input;
	}

	public static boolean isInteger(String s) {
		return isInteger(s,10);
	}

	private static boolean isInteger(String s, int radix) {
		if(s.isEmpty()) return false;
		for(int i = 0; i < s.length(); i++) {
			if(i == 0 && s.charAt(i) == '-') {
				if(s.length() == 1) return false;
				else continue;
			}
			if(Character.digit(s.charAt(i),radix) < 0) return false;
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

	public void incomingEdges(IActivityNode activityNode, StringBuilder action, ArrayList<String> alphabet, IFlow[] inFlows,
			IInputPin[] inPins, List<String> namesMemoryLocal, HashMap<String, String> typeMemoryLocal)
					throws ParsingException {
		if (inFlows.length > 0 || inPins.length > 0) {
			action.append("(");
			for (int i = 0; i < inFlows.length; i++) {
				// first control flows
				Pair<IActivity,String> key = new Pair<IActivity, String>(ad, inFlows[i].getId());
				if (inFlows[i] instanceof IControlFlow) {
					String ceIn;
					if (syncChannelsEdge.containsKey(key)) {
						ceIn = syncChannelsEdge.get(key);

					} else {
						ceIn = createCE();
						Pair<IActivity,String> pair = new Pair<IActivity, String>(ad, inFlows[i].getId());
						syncChannelsEdge.put(pair, ceIn);
					}
					action.append("(");
					// AJUSTE Signal para remover ||| CE
					///////////////////////////////////////////////////////////////////////////
					boolean pinswithedges = false;
					for (IInputPin pin : inPins) {
						if (pin.getIncomings().length > 0) {
							pinswithedges = true;
							break;
						}
					}

					if (i < inFlows.length - 1 || pinswithedges) {
						ce(alphabet, action, ceIn, " -> SKIP) ||| ");
					} else {
						ce(alphabet, action, ceIn, " -> SKIP)");
					}
					///////////////////////////////////////////////////////////////////////////
				} else {// then object flows, which are discarded as they are not sent to pins
					String oeIn; 
					String typeObject;
					try {
						typeObject = ((IObjectFlow)inFlows[i]).getBase().getName();
					} catch (NullPointerException e) {
						throw new ParsingException("Object flow does not have a type.");
					}

					if (syncObjectsEdge.containsKey(key)) {
						oeIn = syncObjectsEdge.get(key);
						if (!objectEdges.containsKey(oeIn)) {
							objectEdges.put(oeIn, typeObject);
						}
					} else {
						oeIn = createOE();
						Pair<IActivity,String> pair = new Pair<IActivity, String>(ad,inFlows[i].getId());
						syncObjectsEdge.put(pair, oeIn);
						objectEdges.put(oeIn, typeObject);
					}

					action.append("(");
					// AJUSTE Signal para remover ||| CE
					///////////////////////////////////////////////////////////////////////////
					boolean pinswithedges = false;
					for (IInputPin pin : inPins) {
						if (pin.getIncomings().length > 0) {
							pinswithedges = true;
							break;
						}
					}

					if (i < inFlows.length - 1 || pinswithedges) {
						oe(alphabet, action, oeIn, "?x", " -> ");
						action.append("SKIP) ||| ");
					} else {
						oe(alphabet, action, oeIn, "?x" , " -> ");
						action.append("SKIP)");
					}
					///////////////////////////////////////////////////////////////////////////
				}

			}

			//then object flows
			for (int i = 0; i < inPins.length; i++) {
				IFlow[] inFlowPin = inPins[i].getIncomings();
				for (int x = 0; x < inFlowPin.length; x++) {
					String type = ((IObjectFlow)inFlowPin[x]).getBase().getName();

					if (!type.equals(inPins[i].getBase().getName())) {
						throw new ParsingException("Pin "+ inPins[i].getName() + " and object flow have incompatible types!");
					}

					Pair<IActivity,String> key = new Pair<IActivity, String>(ad, inFlowPin[x].getId());
					String nameObject = inPins[i].getName();
					String oeIn;
					if (syncObjectsEdge.containsKey(key)) {
						oeIn = syncObjectsEdge.get(key);
						if (!objectEdges.containsKey(oeIn)) {
							objectEdges.put(oeIn, type);
						}
					} else {
						oeIn = createOE();
						Pair<IActivity,String> pair = new Pair<IActivity, String>(ad,inFlowPin[x].getId());
						syncObjectsEdge.put(pair, oeIn);
						objectEdges.put(oeIn, type);
					}

					action.append("(");
					if (i < inPins.length - 1 || x < inFlowPin.length - 1) {
						oe(alphabet, action, oeIn, "?" + nameObject, " -> ");
						try {
							setLocalInput(alphabet, action, inPins[i].getName(), nameDiagramResolver(activityNode.getName()), nameObject, oeIn,inPins[i].getBase().getName());
						} catch (Exception e) {
							throw new ParsingException("InputPin node "+inPins[i].getName()+" without base type\n");//TODO fix the type of exception
						}
						action.append("SKIP) ||| ");
					} else {
						oe(alphabet, action, oeIn, "?" + nameObject, " -> ");
						try {
							setLocalInput(alphabet, action, inPins[i].getName(), nameDiagramResolver(activityNode.getName()), nameObject, oeIn,inPins[i].getBase().getName());
						} catch (Exception e) {
							throw new ParsingException("Pin node "+inPins[i].getName()+" without base type\n");//TODO fix the type of exception
						}
						action.append("SKIP)");
					}

					if (!namesMemoryLocal.contains(nameObject)) {
						namesMemoryLocal.add(nameObject);
						typeMemoryLocal.put(nameObject, inPins[i].getBase().getName());
					}
				}
			}
			action.append("); ");
		}

	}

	public void outgoingEdges(StringBuilder action, ArrayList<String> alphabet, IFlow[] outFlows,
			IOutputPin[] outPins, String[] definitionFinal, boolean isAccept) throws ParsingException {
		// defining outgoing edges
		if (outFlows.length > 0 || outPins.length > 0) {
			action.append("(");
		}

		// creates the outgoing control edges events
		for (int i = 0; i < outFlows.length; i++) {    
			Pair<IActivity,String> key = new Pair<IActivity, String>(ad, outFlows[i].getId());
			String ceOut;
			if (syncChannelsEdge.containsKey(key)) {
				ceOut = syncChannelsEdge.get(key);

			} else {
				ceOut = createCE();
				Pair<IActivity,String> pair = new Pair<IActivity, String>(ad, outFlows[i].getId());
				syncChannelsEdge.put(pair, ceOut);
			}

			action.append("(");

			// AJUSTE Accept para remover ||| OE
			///////////////////////////////////////////////////////////////////////////
			boolean pinswithedges = false;
			for (IOutputPin pin : outPins) {
				if (pin.getOutgoings().length > 0) {
					pinswithedges = true;
					break;
				}
			}
			if (i >= 0 && (i < outFlows.length - 1 || pinswithedges)) {
				ce(alphabet, action, ceOut, " -> SKIP) ||| ");
			} else {
				ce(alphabet, action, ceOut, " -> SKIP)");
			}
			///////////////////////////////////////////////////////////////////////////
		}


		// creates the outgoing object edges events

		for (int i = 0; i < outPins.length; i++) {    
			IFlow[] outFlowPin = outPins[i].getOutgoings();

			for (int x = 0; x < outFlowPin.length; x++) {
				String nameObject;
				String type;
				try {
					nameObject = outPins[i].getBase().getName();
					type = ((IObjectFlow)outFlowPin[x]).getBase().getName();

					if (!type.equals(outPins[i].getBase().getName())) {
						throw new ParsingException("OutputPin "+ outPins[i].getName() + " and object flow have incompatible types!");
					}
				} catch (NullPointerException e) {
					throw new ParsingException("Pin "+outPins[i].getName()+" without base class\n");
				}

				Pair<IActivity,String> pair = new Pair<IActivity, String>(ad,outFlowPin[x].getId());
				String oe;
				if (syncObjectsEdge.containsKey(pair)) {
					oe = syncObjectsEdge.get(pair);
					if (!objectEdges.containsKey(oe)) {
						objectEdges.put(oe, type);
					}
				} else {
					oe = createOE();
					syncObjectsEdge.put(pair, oe);
					objectEdges.put(oe, type);
				}


				if (isAccept) {
					action.append("(");
					if (i >= 0 && (i < outPins.length - 1 || x < outFlowPin.length - 1)) {
						oe(alphabet, action, oe, "!"+outPins[i].getName(), " -> SKIP) ||| ");
					} else {
						oe(alphabet, action, oe, "!"+outPins[i].getName(), " -> SKIP)");
					}
				} else if (definitionFinal != null) {//not call behavior
					String value = "";
					for (int j = 0; j < definitionFinal.length; j++) {
						String[] expression = definitionFinal[j].split("=");
						if (expression[0].equals(outPins[i].getName())) {
							value = expression[1];
						}
					}

					String typeObj = nameObject;

					// defining bounds for model checking
					Pair<String, String> initialAndFinalParameterValue = getInitialAndFinalParameterValue(typeObj);

					if ((value != null && !value.equals("")) && ADUtils.isInteger(initialAndFinalParameterValue.getKey())) {
						action.append("((");
						action.append("(" + value + ") >= " + initialAndFinalParameterValue.getKey() + " and (" + value + ") <= "  + initialAndFinalParameterValue.getValue() + ") & ");
					} else {
						action.append("(");
					}
					if(value !=null && !value.equals("")) {
						if (i >= 0 && (i < outPins.length - 1 || x < outFlowPin.length - 1)) {
							oe(alphabet, action, oe, "!(" + value + ")", " -> SKIP) ||| ");
						} else {
							oe(alphabet, action, oe, "!(" + value + ")", " -> SKIP)");
						}
					}
					else {
						if (i >= 0 && (i < outPins.length - 1 || x < outFlowPin.length - 1)) {
							oe(alphabet, action, oe, "?unknown"+i, " -> SKIP) ||| ");
						} else {
							oe(alphabet, action, oe, "?unknown"+i, " -> SKIP)");
						}
					}

				} else {// node is a call behavior
					action.append("(");
					// AJUSTE Accept para remover ||| OE
					///////////////////////////////////////////////////////////////////////////
					boolean pinswithedges = false;
					for (IOutputPin pin : outPins) {
						if (pin.getOutgoings().length > 0) {
							pinswithedges = true;
							break;
						}
					}
					if (i >= 0 && (i < outFlows.length - 1 || pinswithedges)) {
					//	if (i >= 0 && (i < outPins.length - 1 || x < outFlowPin.length - 1)) {
						getLocal(alphabet, action, nameResolver(outPins[i].getName()), nameResolver(outPins[i].getOwner().getName()), nameResolver(outPins[i].getName()),type);
						oe(alphabet, action, oe, "!(" + outPins[i].getName() + ")", " -> SKIP) ||| ");
					} else {
						getLocal(alphabet, action, nameResolver(outPins[i].getName()), nameResolver(outPins[i].getOwner().getName()), nameResolver(outPins[i].getName()),type);
						oe(alphabet, action, oe, "!(" + outPins[i].getName() + ")", " -> SKIP)");
					}
					///////////////////////////////////////////////////////////////////////////
				}

			}
		}

		if (outFlows.length > 0 || outPins.length > 0) {
			action.append("); ");
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////
	public String alphabetRobo(String alphabet) {
		StringBuilder channels = new StringBuilder();
		channels.append("alphabet_robochart_" + ADUtils.nameResolver(ad.getName()) + " = ");
		channels.append(alphabet);
		return channels.toString();
	}

	public String alphabetUntil() {
		StringBuilder channels = new StringBuilder();

		channels.append("Wait_" + ad.getName() + " = WAIT(alphabet_robochart_"
				+ ADUtils.nameResolver(ad.getName()) + ", ");
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
			channels.append("Wait_" + ADUtils.nameResolver(ad.getName()) + "_" + i
					+ " = WAIT(alphabet_robochart_" + ADUtils.nameResolver(ad.getName()) + ", "
					+ untilList.get(i) + ")\n\n");
			channels.append("Wait_" + ADUtils.nameResolver(ad.getName()) + "_control_" + i
					+ " = begin." + i + " -> Wait_" + ADUtils.nameResolver(ad.getName()) + "_" + i
					+ "; end." + i + " -> Wait_" + ADUtils.nameResolver(ad.getName()) + "_control_"
					+ i + "\n\n");
		}
		return channels.toString();
	}	
	//-----------------------------------------------
	public String printUntilWithPins() {
		StringBuilder channels = new StringBuilder();
		int c = 0;
		for (String i : waitAccept) {
			channels.append(i);
			if ((c + 1) <  waitAccept.size()) {
				channels.append(",");
			}
			c++;
		}
		//		channels.substring(0, channels.length()-2);

		return channels.toString();
	}
	public String printUntilWithPins2() {
		StringBuilder channels = new StringBuilder();
		for (String i : waitAccept) {
			channels.append(i);
		}
		//		channels.append("WAIT_accept_" + nameDiagramResolver(activityNode.getName()) + "_" + "idAccept" + "(alphabet) = \n");
		//		channels.append("NRecurse(diff(alphabet, " + printUntilWithPins() + ")," + "WAIT_accept_" + nameDiagramResolver(activityNode.getName()) + "_" + "idAccept" + "(alphabet))\n |~| \n");
		//		channels.append(printUntilWithPins() + "?u -> set_accept_ultrasonic_u_P_Teste.u -> SKIP\n");	
		return channels.toString();
	}
	//-----------------------------------------------
	public String printAny() {
		StringBuilder channels = new StringBuilder();
		for (int i = 1; i <= adParser.countAny_ad; i++) {
			channels.append("Wait_" + ADUtils.nameResolver(ad.getName()) + "_chaos_" + i
					+ " = chaos." + i + " -> CHAOS(alphabet_robochart_" + ad.getName() + ")\n\n");
		}
		return channels.toString();
	}

	public String printControlProcesses() {
		StringBuilder channels = new StringBuilder();
		// channels.append("Wait_control_processes = {Wait_" + ad.getName() + "_control_" + i +
		// "}\n");
		channels.append("Wait_control_processes_" + ADUtils.nameResolver(ad.getName()) + " = {");
		int c = 0;		
		for (int i = 1; i <= adParser.countUntil_ad; i++) {
			channels.append("Wait_" + ADUtils.nameResolver(ad.getName()) + "_control_" + i);
			if ((c + 1) < adParser.countUntil_ad || adParser.countAny_ad > 0) {
				channels.append(", ");
			}
			c++;
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
		//		alphabet.add("chaos." + index);
		callBehaviour.append("chaos." + index + " -> SKIP;");
	}

	public boolean hasPins() {
		return hasPins;
	}
}
