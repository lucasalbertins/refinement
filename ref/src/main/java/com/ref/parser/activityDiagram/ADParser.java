package com.ref.parser.activityDiagram;

import java.util.ArrayList;
import java.util.HashMap;

import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.model.IAction;
import com.change_vision.jude.api.inf.model.IActivity;
import com.change_vision.jude.api.inf.model.IActivityNode;
import com.change_vision.jude.api.inf.model.IActivityParameterNode;
import com.change_vision.jude.api.inf.model.IControlNode;
import com.change_vision.jude.api.inf.model.IFlow;
import com.change_vision.jude.api.inf.model.IInputPin;
import com.change_vision.jude.api.inf.model.IOutputPin;

import javafx.util.Pair;

public class ADParser {

	private IActivity ad;
	
	private int countGet_ad;
	private int countSet_ad;
	private int countCe_ad;
	private int countOe_ad;
	private int countUpdate_ad;
	private int countClear_ad;
	private int limiteInf;
	private int limiteSup;
	
	private static HashMap<String, Integer> countCall;
	private HashMap<String, ArrayList<String>> alphabetNode;
	private HashMap<Pair<String, String>, String> syncChannelsEdge;
	private HashMap<Pair<String, String>, String> syncObjectsEdge;
	private HashMap<String, String> objectEdges;				//channel; name
	private ArrayList<IActivityNode> queueNode;
	private ArrayList<IActivity> callBehaviorList;
	private ArrayList<String> eventChannel;
	private ArrayList<String> lockChannel;
	private ArrayList<String> allInitial;
	private ArrayList<String> alphabetAllInitialAndParameter;
	private HashMap<String, String> parameterNodesInput;		//name; type
	private HashMap<String, String> parameterNodesOutput;
	
	public ADParser(IActivity ad, String nameAD) {
		this.ad = ad;
		setName(nameAD);
		this.countGet_ad = 1;
		this.countSet_ad = 1;
		this.countCe_ad = 1;
		this.countOe_ad = 1;
		this.countUpdate_ad = 1;
		this.countClear_ad = 1;
		this.limiteInf = 99;
		this.limiteSup = -99;
		this.alphabetNode = new HashMap<>();
		countCall = new HashMap<>();
		//addCountCall(); //comentado durante os testes
		syncChannelsEdge = new HashMap<>();
		syncObjectsEdge = new HashMap<>();
		objectEdges = new HashMap<>();
		queueNode = new ArrayList<>();
		callBehaviorList = new ArrayList<>();
		eventChannel = new ArrayList<>();
		lockChannel = new ArrayList<>();
		allInitial = new ArrayList<>();
		alphabetAllInitialAndParameter = new ArrayList<>();
		parameterNodesInput = new HashMap<>();
		parameterNodesOutput = new HashMap<>();
	}
	
	public void clearBuffer() {
		this.countGet_ad = 1;
		this.countSet_ad = 1;
		this.countCe_ad = 1;
		this.countOe_ad = 1;
		this.countUpdate_ad = 1;
		this.countClear_ad = 1;
		this.limiteInf = 99;
		this.limiteSup = -99;
		this.alphabetNode = new HashMap<>();
		countCall.clear();
		addCountCall();
		syncChannelsEdge = new HashMap<>();
		syncObjectsEdge = new HashMap<>();
		objectEdges = new HashMap<>();
		queueNode = new ArrayList<>();
		callBehaviorList = new ArrayList<>();
		eventChannel = new ArrayList<>();
		lockChannel = new ArrayList<>();
		allInitial = new ArrayList<>();
		alphabetAllInitialAndParameter = new ArrayList<>();
		parameterNodesInput = new HashMap<>();
		parameterNodesOutput = new HashMap<>();
	}
	
	private void setName(String nameAD) {
		try {
			this.ad.setName(nameAD);
		} catch (InvalidEditingException e) {
			e.printStackTrace();
		}
	}
	
	private void addCountCall() {
		if (countCall.containsKey(ad.getName())) {
			int i = countCall.get(ad.getName());			
			countCall.put(ad.getName(), ++i);
		} else {
			countCall.put(ad.getName(), 1);
		}
	}
	
	public String defineChannels() {
		StringBuilder channels = new StringBuilder();
		IActivityNode nodes[] =  ad.getActivityNodes();
		String nameDiagram = ad.getName();
		
		for (IActivityNode activityNode : nodes) {
			if (activityNode instanceof IActivityParameterNode && activityNode.getOutgoings().length > 0) {	
				parameterNodesInput.put(activityNode.getName(), ((IActivityParameterNode) activityNode).getBase().getName());
			}
			
			if (activityNode instanceof IActivityParameterNode && activityNode.getIncomings().length > 0) {	
				parameterNodesOutput.put(activityNode.getName(), ((IActivityParameterNode) activityNode).getBase().getName());
			}
		}
		
		if (parameterNodesInput.size() > 0) {
			channels.append("channel startActivity_" + nameDiagram + ": ID_" + nameDiagram);
			
			for (String input : parameterNodesInput.keySet()) {
				channels.append("." + input + "_" + nameDiagram);
			}
			
			channels.append("\n");
			
		} else {
			channels.append("channel startActivity_" + nameDiagram + ": ID_" + nameDiagram + "\n");
		}
		
		if (parameterNodesOutput.size() > 0) {
			channels.append("channel endActivity_" + nameDiagram + ": ID_" + nameDiagram);
			
			for (String output : parameterNodesOutput.keySet()) {
				channels.append("." + output + "_" + nameDiagram);
			}
			
			channels.append("\n");
			
		} else {
			channels.append("channel endActivity_" + nameDiagram + ": ID_" + nameDiagram + "\n");
		}
		
		if (parameterNodesInput.size() > 0 || parameterNodesOutput.size() > 0) {
			
			for (String get : parameterNodesInput.keySet()) {
				channels.append("channel get_" + get + "_" + nameDiagram + ": countGet_" + nameDiagram + "." + get + "_" + nameDiagram + "\n");
			}
			
			for (String get : parameterNodesOutput.keySet()) {
				channels.append("channel get_" + get + "_" + nameDiagram + ": countGet_" + nameDiagram + "." + get + "_" + nameDiagram + "\n");
			}
			
			for (String set : parameterNodesInput.keySet()) {
				channels.append("channel set_" + set + "_" + nameDiagram + ": countSet_" + nameDiagram + "." + set + "_" + nameDiagram + "\n");
			}
			
			for (String set : parameterNodesOutput.keySet()) {
				channels.append("channel set_" + set + "_" + nameDiagram + ": countSet_" + nameDiagram + "." + set + "_" + nameDiagram + "\n");
			}
			
		}
		
		if (countCe_ad > 1) {
			channels.append("channel ce_" + nameDiagram + ": countCe_" + nameDiagram + "\n");
		}
		
		if (syncObjectsEdge.size() > 0) {
			ArrayList<String> allObjectEdges = new ArrayList<>();
			for (Pair<String, String> tupla : syncObjectsEdge.keySet()) {	//get sync channel
				String objectEdge = syncObjectsEdge.get(tupla);
				String nameParamater = objectEdges.get(objectEdge);
				
				if (!allObjectEdges.contains(nameParamater)) {
					allObjectEdges.add(nameParamater);
					channels.append("channel oe_" + nameParamater + "_" + nameDiagram + ": countOe_" + nameDiagram + "." + nameParamater + "_" + nameDiagram + "\n");
				}
			}
			
		}

		if (countClear_ad > 1) {
			channels.append("channel clear_" + nameDiagram + ": countClear_" + nameDiagram + "\n");
		}
		
		channels.append("channel update_" + nameDiagram + ": countUpdate_" + nameDiagram + ".limiteUpdate_" + nameDiagram + "\n");
		
		channels.append("channel endDiagram_" + nameDiagram + "\n");
		
		if (eventChannel.size() > 0) {
			channels.append("channel ");
			
			for (int i = 0; i < eventChannel.size(); i++) {
				channels.append(eventChannel.get(i));
				
				if ((i + 1) < eventChannel.size()) {
					channels.append(",");
				}
			}
			
			channels.append("\n");
		}
		
		if (lockChannel.size() > 0) {
			channels.append("channel ");
			
			for (int i = 0; i < lockChannel.size(); i++) {
				channels.append("lock_" + lockChannel.get(i));
				
				if ((i + 1) < lockChannel.size()) {
					channels.append(",");
				}
			}
			
			channels.append(": T\n");
		}
		
		channels.append("channel loop\n");
		
		System.out.println(channels);
		
		return channels.toString();
	}
	
	public String defineTypes() {
		StringBuilder types = new StringBuilder();
		String nameDiagram = ad.getName();
		
		if (countCall.size() > 0) {		//Provavelmente deve ser criado por ultimo
			for (String nameDiagram2 : countCall.keySet()) {
				types.append("ID_" + nameDiagram2 + " = {1.." + countCall.get(nameDiagram2) + "}\n");
			}
		}
		
		types.append("datatype T = lock | unlock\n");
		
		if (parameterNodesInput.size() > 0 || parameterNodesOutput.size() > 0) {
			for (String input : parameterNodesInput.keySet()) {
				types.append(input + "_" + nameDiagram + " = ");
				
				if (parameterNodesInput.get(input).equals("int")) {
					types.append("{0..1}\n"); //Verificar se possivel usar o campo definition para definir o intervalo
				}
				
			}
			
			for (String output : parameterNodesOutput.keySet()) {
				types.append(output + "_" + nameDiagram + " = ");
				
				if (parameterNodesOutput.get(output).equals("int")) {
					types.append("{0..1}\n"); //Verificar se possivel usar o campo definition para definir o intervalo
				}
				
			}
			
		}
		
		if (countGet_ad > 1 || countSet_ad > 1) {
			if (countGet_ad == 1) {
				types.append("countGet_" + nameDiagram + " = {1.." + countGet_ad + "}\n");
			} else {
				types.append("countGet_" + nameDiagram + " = {1.." + (countGet_ad - 1) + "}\n");
			}
			
			if (countSet_ad == 1) {
				types.append("countSet_" + nameDiagram + " = {1.." + countSet_ad + "}\n");
			} else {
				types.append("countSet_" + nameDiagram + " = {1.." + (countSet_ad - 1) + "}\n");
			}
		}

		if (countCe_ad > 1) {
			types.append("countCe_" + nameDiagram + " = {1.." + (countCe_ad - 1) + "}\n");
		}
		
		if (countOe_ad > 1) {
			types.append("countOe_" + nameDiagram + " = {1.." + (countOe_ad - 1) + "}\n");
		}
		
		types.append("countUpdate_" + nameDiagram + " = {1.." + (countUpdate_ad - 1) + "}\n");

		if (countClear_ad > 1) {
			types.append("countClear_" + nameDiagram + " = {1.." + (countClear_ad - 1) + "}\n");
		}
		
		types.append("limiteUpdate_" + nameDiagram + " = {(" + limiteInf + ")..(" + limiteSup + ")}\n");
		
		System.out.println(types);
		
		return types.toString();
	}
	
	public String defineNodesActionAndControl() {
		StringBuilder nodes = new StringBuilder();
		
		for (IActivityNode activityNode : ad.getActivityNodes()) {
			if (((activityNode instanceof IControlNode && ((IControlNode) activityNode).isInitialNode()) || 
					activityNode instanceof IActivityParameterNode) && !queueNode.contains(activityNode)) {
				
				queueNode.add(activityNode);
			}
		}
		
		int input = 0;
		
		while (queueNode.size() != 0) {
			IActivityNode activityNode = queueNode.get(0);
			queueNode.remove(0);
			
			input = countAmount(activityNode);
			
			while (activityNode != null && !alphabetNode.containsKey(activityNode.getName())	// Verifica se nó é nulo, se nó já foi criado e se todos os nós de entrada dele já foram criados
					&& input == activityNode.getIncomings().length ) {
				
				if (activityNode instanceof IAction) {
					if (((IAction) activityNode).isCallBehaviorAction()) {
						activityNode = defineCallBehavior(activityNode, nodes);
					} else {
						activityNode = defineAction(activityNode, nodes);	// create action node and set next action node
					}
				} else if (activityNode instanceof IControlNode) {
					if (((IControlNode) activityNode).isFinalNode()) {
						activityNode = defineFinalNode(activityNode, nodes); // create final node and set next action node
					} else if (((IControlNode) activityNode).isFlowFinalNode()) {
						activityNode = defineFlowFinal(activityNode, nodes); // create flow final and set next action node
					} else if (((IControlNode) activityNode).isInitialNode()) {
						activityNode = defineInitialNode(activityNode, nodes); // create initial node and set next action node
					} else if (((IControlNode) activityNode).isForkNode()) {
						activityNode = defineFork(activityNode, nodes); // create fork node and set next action node
					} else if (((IControlNode) activityNode).isJoinNode()) {
						activityNode = defineJoin(activityNode, nodes); // create join node and set next action node
					} else if (((IControlNode) activityNode).isDecisionMergeNode()) {
						
						if (activityNode.getOutgoings().length > 1) {
							activityNode = defineDecision(activityNode, nodes); // create decision node and set next action node
						} else {
							IFlow flows[] = activityNode.getOutgoings();
							boolean decision = false;
							for (int i = 0; i < flows.length; i++) {
								
								String stereotype[] = flows[i].getStereotypes();
								
								for (int j = 0; j < stereotype.length; j++) {
									if (stereotype[j].equals("decisionInputFlow")) {
										decision = true;
									}
								}
								
								
							}
						
							if (decision) {
								activityNode = defineDecision(activityNode, nodes); // create decision node and set next action node
							} else {
								activityNode = defineMerge(activityNode, nodes); // create merge node and set next action node
							}	
						}
					} 
				} else if (activityNode instanceof IActivityParameterNode) {
					activityNode = defineParameterNode(activityNode, nodes);
				}

				input = countAmount(activityNode);

			}	
		}
		
		
		//add initial central
		if (allInitial.size() > 0) {
			nodes.append("init_" + ad.getName() + "_t = (" + allInitial.get(0));
			for (int i = 1; i < allInitial.size(); i++) {
				nodes.append(" ||| " + allInitial.get(i));
			}
		}
		
		nodes.append(") /\\ END_DIAGRAM_" + ad.getName());
		alphabetAllInitialAndParameter.add("endDiagram_" + ad.getName());
		
		alphabetNode.put("init", alphabetAllInitialAndParameter);
		
		System.out.println(nodes);

		return nodes.toString();
	}
	
	public String defineLock() {
		StringBuilder locks = new StringBuilder();
		String nameDiagram = ad.getName();
		
		if (lockChannel.size() > 0) {
			for (String lock : lockChannel) {
				locks.append("Lock_" + lock + " = lock_" + lock + ".lock -> lock_" + lock + ".unlock -> Lock_" + lock + " [] endDiagram_" + nameDiagram + " -> SKIP\n");
			}
			
			locks.append("Lock_" + nameDiagram + " = ");
			
			if (lockChannel.size() == 1) {
				locks.append("Lock_" + lockChannel.get(0) + "\n");
			} else {
				for (int i = 0; i < lockChannel.size() - 1; i++) {
					locks.append("(");
				}
				
				locks.append("Lock_" + lockChannel.get(0));
				
				for (int i = 1; i < lockChannel.size(); i++) {
					locks.append(" [|{|endDiagram_" + nameDiagram + "|}|] Lock_" + lockChannel.get(i) + ")");
				}
				
				locks.append("\n");
			}
		}
		
		System.out.println(locks);
		
		return locks.toString();
	}
	
	public String defineTokenManager() {
		StringBuilder tokenManager = new StringBuilder();
		String nameDiagram = ad.getName();
		
		tokenManager.append("TokenManager_" + nameDiagram + "(x,init) = update_" + nameDiagram 	+ "?c?y:limiteUpdate_" + nameDiagram
				+ " -> x+y < 10 & x+y > -10 & TokenManager_" + nameDiagram + "(x+y,1) [] clear_" + nameDiagram + "?c -> endDiagram_" + nameDiagram
				+ " -> SKIP [] x == 0 & init == 1 & endDiagram_" + nameDiagram + " -> SKIP\n");
		tokenManager.append("TokenManager_" + ad.getName() + "_t(x,init) = TokenManager_" + nameDiagram + "(x,init)\n");

		System.out.println(tokenManager.toString());
		
		return tokenManager.toString();
	}
	
	public String defineProcessSync() {
		StringBuilder processSync = new StringBuilder();
		String termination = "_" + ad.getName() + "_t";
		
		processSync.append("Node_" + ad.getName() + " = ");
		
		if (alphabetNode.size() == 1) {
			for (String node : alphabetNode.keySet()) {
				processSync.append(node + termination +  "\n");
			}
		} else {
			for (int i = 0; i < alphabetNode.size() - 1; i++) {
				processSync.append("(");
			}
			
			ArrayList<String> set = null; 	// total set
			
			int add = 1;
			for (String node : alphabetNode.keySet()) {		//add first and second
				if (add == 1) {
					ArrayList<String> alphabet = alphabetNode.get(node);
					processSync.append(node + termination + " [{|");
					
					if (alphabet.size() == 1) {
						processSync.append(alphabet.get(0) + "|}||");
					} else {
						
						processSync.append(alphabet.get(0));
						
						for (int i = 1; i < alphabet.size(); i++) {
							processSync.append("," + alphabet.get(i));
						}
						
						processSync.append("|}||");
					}
					
					set = new ArrayList<>(alphabet);
				}
				
				if (add == 2) {
					ArrayList<String> alphabet = alphabetNode.get(node);
					processSync.append("{|");
					
					if (alphabet.size() == 1) {
						processSync.append(alphabet.get(0) + "|}]");
					} else {
						
						processSync.append(alphabet.get(0));
						
						for (int i = 1; i < alphabet.size(); i++) {
							processSync.append("," + alphabet.get(i));
						}
						
						processSync.append("|}] " + node + termination + ")");
					}
					
					for (String channel : alphabet) {		//add channels 
						if (!set.contains(channel)) {
							set.add(channel);
						}
					}
				}
				
				add++;
			}
			
			add = 1;
			for (String node : alphabetNode.keySet()) {		//add first and second
				if (add > 2) {
					processSync.append(" [{|");
					
					if (set.size() == 1) {
						processSync.append(set.get(0) + "|}||");
					} else {
						
						processSync.append(set.get(0));
						
						for (int i = 1; i < set.size(); i++) {
							processSync.append("," + set.get(i));
						}
						
						processSync.append("|}||");
					}
				
			
					ArrayList<String> alphabet = alphabetNode.get(node);
					processSync.append("{|");
					
					if (alphabet.size() == 1) {
						processSync.append(alphabet.get(0) + "|}]");
					} else {
						
						processSync.append(alphabet.get(0));
						
						for (int i = 1; i < alphabet.size(); i++) {
							processSync.append("," + alphabet.get(i));
						}
						
						processSync.append("|}] " + node + termination + ")");
					}
					
					for (String channel : alphabet) {		//add channels 
						if (!set.contains(channel)) {
							set.add(channel);
						}
					}
				}
				
				add++;
			}
			
		}
		
		processSync.append("\n");
		
		System.out.println(processSync.toString());
		
		return processSync.toString();
	}
	
	private int countAmount(IActivityNode activityNode) {
		int input = 0;
		if (activityNode != null) {
			input = 0;
			
			for (Pair<String, String> tupla : syncChannelsEdge.keySet()) {	//get amount input
				if (tupla.getValue().equals(activityNode.getName())) {
					input++;
				}
			}
			
			for (Pair<String, String> tupla : syncObjectsEdge.keySet()) {	//get amount input
				if (tupla.getValue().equals(activityNode.getName())) {
					input++;
				}
			}
			
		}
		
		return input;
	}
	
	private IActivityNode defineAction(IActivityNode activityNode, StringBuilder nodes) {
		StringBuilder action = new StringBuilder();
		ArrayList<String> alphabet = new ArrayList<>();
		String nameAction = activityNode.getName() + "_" + ad.getName();
		String nameActionTermination = activityNode.getName() + "_" + ad.getName() + "_t";
		String endDiagram = "END_DIAGRAM_" + ad.getName();
		IFlow outFlows[] = activityNode.getOutgoings();
		IFlow inFlows[] = activityNode.getIncomings();
		String nameObject = null;
		boolean syncBool = false;
		boolean sync2Bool = false;
		
		action.append(nameAction + " = ");
		
		for (Pair<String, String> tupla : syncChannelsEdge.keySet()) {	//get sync channel
			if (tupla.getValue().equals(activityNode.getName())) {
				String ceIn = syncChannelsEdge.get(tupla);
				ce(alphabet, action, ceIn, " -> ");
				syncBool = true;
			}
		}
		
		for (Pair<String, String> tupla : syncObjectsEdge.keySet()) {	//get sync channel
			if (tupla.getValue().equals(activityNode.getName())) {
				String ceIn2 = syncObjectsEdge.get(tupla);
				nameObject = objectEdges.get(ceIn2);
				oe(alphabet, action, ceIn2, "?" + nameObject + " -> ");
				sync2Bool = true;
			}
		}
	
		lock(alphabet, action, 0, nameAction);
		event(alphabet, nameAction, action);
		lock(alphabet, action, 1, nameAction);
		update(alphabet, action, inFlows.length, outFlows.length);
	
		action.append("(");
		
		if (syncBool) {
			for (int i = 0; i <  outFlows.length; i++) {	//creates the parallel output channels
				String ce = createCE();
				syncChannelsEdge.put(new Pair<String, String>(activityNode.getName(), outFlows[i].getTarget().getName()), ce);
				
				action.append("(");
				
				if (i >= 0 && i < outFlows.length - 1) {
					ce(alphabet, action, ce, " -> SKIP) ||| ");
				} else {
					ce(alphabet, action, ce, " -> SKIP)");
				}
			}	
		} else if (sync2Bool) {	
			for (int i = 0; i <  outFlows.length; i++) {	//creates the parallel output channels
				String oe = createOE(nameObject);
				syncObjectsEdge.put(new Pair<String, String>(activityNode.getName(), outFlows[i].getTarget().getName()), oe);
				objectEdges.put(oe, nameObject);
				action.append("(");
				
				if (i >= 0 && i < outFlows.length - 1) {
					ce(alphabet, action, oe, "!" + nameObject + " -> SKIP) ||| ");
				} else {
					ce(alphabet, action, oe, "!" + nameObject + " -> SKIP)");
				}
			}
		}
		
		action.append("); ");
		
		action.append(nameAction + "\n");
		
		action.append(nameActionTermination + " = ");
		action.append(nameAction + " /\\ " + endDiagram + "\n");

		alphabet.add("endDiagram_" + ad.getName());
		alphabetNode.put(activityNode.getName(), alphabet);
		
		activityNode = outFlows[0].getTarget();	//set next action or control node
		
		for (int i = 1; i < outFlows.length; i++) {	//puts the remaining nodes in the queue
			if (!queueNode.contains(outFlows[i].getTarget())) {
				queueNode.add(outFlows[i].getTarget());
			}
		}
		
		nodes.append(action.toString());
		
		return activityNode;
	}
	
	private IActivityNode defineFinalNode (IActivityNode activityNode, StringBuilder nodes) {
		StringBuilder finalNode = new StringBuilder();
		ArrayList<String> alphabet = new ArrayList<>();
		String nameFinalNode = activityNode.getName() + "_" + ad.getName();
		String nameFinalNodeTermination = activityNode.getName() + "_" + ad.getName() + "_t";
		String endDiagram = "END_DIAGRAM_" + ad.getName();
		HashMap<Pair<String, String>, String> nameObjects = new HashMap<>();
		
		finalNode.append(nameFinalNode + " = ");

		ArrayList<Pair<String, String>> ceInitials = new ArrayList<>();
		for (Pair<String, String> tupla : syncChannelsEdge.keySet()) {	//get all sync channels
			if (tupla.getValue().equals(activityNode.getName())) {
				ceInitials.add(tupla);
			}
		}
		
		for (Pair<String, String> tupla : syncObjectsEdge.keySet()) {	//get all sync channels
			if (tupla.getValue().equals(activityNode.getName())) {
				String ceIn2 = syncObjectsEdge.get(tupla);
				ceInitials.add(tupla);
				nameObjects.put(tupla, objectEdges.get(ceIn2));
			}
		}
		
		
		finalNode.append("(");
		for (int i = 0; i < ceInitials.size(); i++) {
			String ceIn = syncChannelsEdge.get(ceInitials.get(i));	//get the parallel input channels
			String oeIn = syncObjectsEdge.get(ceInitials.get(i));
			
			if (ceIn != null) {
				finalNode.append("(");
				
				if (i >= 0 && i < ceInitials.size() - 1) {
					ce(alphabet, finalNode, ceIn, " -> SKIP) [] ");
				} else {
					ce(alphabet, finalNode, ceIn, " -> SKIP)");
				}
			} else {
				
				String nameObject = nameObjects.get(ceInitials.get(i));
				
				finalNode.append("(");
				
				if (i >= 0 && i < ceInitials.size() - 1) {
					ce(alphabet, finalNode, oeIn, "?" + nameObject + " -> SKIP) [] ");
				} else {
					ce(alphabet, finalNode, oeIn, "?" + nameObject + " -> SKIP)");
				}
			}
			
		}
		
		finalNode.append("); ");
		
		clear(alphabet, finalNode);
		
		finalNode.append("SKIP\n");
		
		finalNode.append(nameFinalNodeTermination + " = ");
		finalNode.append(nameFinalNode + " /\\ " + endDiagram + "\n");

		alphabet.add("endDiagram_" + ad.getName());
		alphabetNode.put(activityNode.getName(), alphabet);

		activityNode = null;	

		nodes.append(finalNode.toString());
		
		return activityNode;
	}
	
	private IActivityNode defineInitialNode (IActivityNode activityNode, StringBuilder nodes) {
		StringBuilder initialNode = new StringBuilder();
		ArrayList<String> alphabet = new ArrayList<>();
		String nameInitialNode = activityNode.getName() + "_" + ad.getName() + "_t";
		IFlow outFlows[] = activityNode.getOutgoings();
		IFlow inFlows[] = activityNode.getIncomings();
		
		initialNode.append(nameInitialNode + " = ");
		
		update(alphabet, initialNode, inFlows.length, outFlows.length);
		
		initialNode.append("(");
		
		for (int i = 0; i <  outFlows.length; i++) {	//creates the parallel output channels
			String ce = createCE();
			syncChannelsEdge.put(new Pair<String, String>(activityNode.getName(), outFlows[i].getTarget().getName()), ce);
			
			initialNode.append("(");
			
			if (i >= 0 && i < outFlows.length - 1) {
				ce(alphabet, initialNode, ce, " -> SKIP) ||| ");
			} else {
				ce(alphabet, initialNode, ce, " -> SKIP)");
			}
		}
		
		initialNode.append(")\n");
		
		
//		for (IFlow flow : outFlows) {	//creates output channels
//			String ce = createCN();
//			syncChannels.put(new Pair<String, String>(activityNode.getName(), flow.getTarget().getName()), ce);
//			ce(alphabet, initialNode, ce, " -> ");
//		}
//		
//		initialNode.append("SKIP\n");

		allInitial.add(nameInitialNode);	
		for (String channel : alphabet) {
			if (!alphabetAllInitialAndParameter.contains(channel)) {
				alphabetAllInitialAndParameter.add(channel);
			}
		}

		activityNode = outFlows[0].getTarget();	//set next action or control node
		
		for (int i = 1; i < outFlows.length; i++) {	//puts the remaining nodes in the queue
			if (!queueNode.contains(outFlows[i].getTarget())) {
				queueNode.add(outFlows[i].getTarget());
			}
		}
		
		nodes.append(initialNode.toString());
		
		return activityNode;
	} 
	
	private IActivityNode defineCallBehavior(IActivityNode activityNode, StringBuilder nodes) {	//Ainda nao testado
		StringBuilder callBehavior = new StringBuilder();
		ArrayList<String> alphabet = new ArrayList<>();
		String nameCallBehavior = activityNode.getName() + "_" + ad.getName();
		String nameCallBehaviorTermination = activityNode.getName() + "_" + ad.getName() + "_t";
		String endDiagram = "END_DIAGRAM_" + ad.getName();
		IFlow outFlows[] = activityNode.getOutgoings();
		IFlow inFlows[] = activityNode.getIncomings();
		ArrayList<String> inputPins = new ArrayList<>();
		ArrayList<String> outputPins = new ArrayList<>();
		
		callBehavior.append(nameCallBehavior + " = ");
		
		Pair<String, String> sync = new Pair<String, String>("", "");
		for (Pair<String, String> tupla : syncChannelsEdge.keySet()) {	//get sync channel
			if (tupla.getValue().equals(activityNode.getName())) {
				sync = tupla;
			}
		}
		
		String ceIn = syncChannelsEdge.get(sync);
		
		ce(alphabet, callBehavior, ceIn, " -> ");
		
		for (IFlow pinInput : activityNode.getIncomings()) {
			
			IActivityNode pin = pinInput.getSource();
			
			if (pin instanceof IInputPin) {
				get(alphabet, callBehavior, pin.getName());
				inputPins.add(pin.getName());
			}
		}
		
		
		startActivity(alphabet, callBehavior, inputPins);
		endActivity(alphabet, callBehavior, outputPins);
		
		for (IFlow pinOutput : activityNode.getOutgoings()) {
			
			IActivityNode pin = pinOutput.getTarget();
			
			if (pin instanceof IOutputPin) {
				set(alphabet, callBehavior, pin.getName());
				outputPins.add(pin.getName());
			}
		}
		
		update(alphabet, callBehavior, inFlows.length, outFlows.length);
		
		for (IFlow flow : outFlows) {	//creates output channels
			String ce = createCE();
			syncChannelsEdge.put(new Pair<String, String>(activityNode.getName(), flow.getTarget().getName()), ce);
			ce(alphabet, callBehavior, ce, " -> ");
		}
		
		callBehavior.append(nameCallBehavior + "\n");
		
		callBehavior.append(nameCallBehaviorTermination + " = ");
		callBehavior.append(nameCallBehavior + " /\\ " + endDiagram + "\n");

		alphabet.add("endDiagram_" + ad.getName());
		alphabetNode.put(activityNode.getName(), alphabet);
		
		callBehaviorList.add(((IAction) activityNode).getCallingActivity()); 	// add activity call behavior
		
		IFlow flow[] = outFlows;
		activityNode = flow[0].getTarget();	//set next action or control node
		
		nodes.append(callBehavior.toString());
		
		return activityNode;
	}
	
	private IActivityNode defineFork(IActivityNode activityNode, StringBuilder nodes) {
		StringBuilder forkNode = new StringBuilder();
		ArrayList<String> alphabet = new ArrayList<>();
		String nameFork = activityNode.getName() + "_" + ad.getName();
		String nameForkTermination = activityNode.getName() + "_" + ad.getName() + "_t";
		String endDiagram = "END_DIAGRAM_" + ad.getName();
		IFlow outFlows[] = activityNode.getOutgoings();
		IFlow inFlows[] = activityNode.getIncomings();
		boolean syncBool = false;
		boolean sync2Bool = false;
		String nameObject = null;
		
		forkNode.append(nameFork + " = ");
		
		for (Pair<String, String> tupla : syncChannelsEdge.keySet()) {	//get sync channel
			if (tupla.getValue().equals(activityNode.getName())) {
				String ceIn = syncChannelsEdge.get(tupla);
				ce(alphabet, forkNode, ceIn, " -> ");
				syncBool = true;
			}
		}
		
		for (Pair<String, String> tupla : syncObjectsEdge.keySet()) {	//get sync channel
			if (tupla.getValue().equals(activityNode.getName())) {
				String oeIn = syncObjectsEdge.get(tupla);
				nameObject = objectEdges.get(oeIn);
				ce(alphabet, forkNode, oeIn, "?" + nameObject + " -> ");
				sync2Bool = true;
			}
		}
		
		update(alphabet, forkNode, inFlows.length, outFlows.length);
		
		forkNode.append("(");
		
		if (syncBool) {
			for (int i = 0; i <  outFlows.length; i++) {	//creates the parallel output channels
				String ce = createCE();
				syncChannelsEdge.put(new Pair<String, String>(activityNode.getName(), outFlows[i].getTarget().getName()), ce);
				
				forkNode.append("(");
				
				if (i >= 0 && i < outFlows.length - 1) {
					ce(alphabet, forkNode, ce, " -> SKIP) ||| ");
				} else {
					ce(alphabet, forkNode, ce, " -> SKIP)");
				}
			}	
		} else if (sync2Bool) {	
			for (int i = 0; i <  outFlows.length; i++) {	//creates the parallel output channels
				String oe = createOE(nameObject);
				syncObjectsEdge.put(new Pair<String, String>(activityNode.getName(), outFlows[i].getTarget().getName()), oe);
				objectEdges.put(oe, nameObject);
				forkNode.append("(");
				
				if (i >= 0 && i < outFlows.length - 1) {
					ce(alphabet, forkNode, oe, "!" + nameObject + " -> SKIP) ||| ");
				} else {
					ce(alphabet, forkNode, oe, "!" + nameObject + " -> SKIP)");
				}
			}
		}
		
		forkNode.append("); ");
		
		forkNode.append(nameFork + "\n");
		
		forkNode.append(nameForkTermination + " = ");
		forkNode.append(nameFork + " /\\ " + endDiagram + "\n");

		alphabet.add("endDiagram_" + ad.getName());
		alphabetNode.put(activityNode.getName(), alphabet);
		
		activityNode = outFlows[0].getTarget();	//set next action or control node
		
		for (int i = 1; i < outFlows.length; i++) {	//puts the remaining nodes in the queue
			if (!queueNode.contains(outFlows[i].getTarget())) {
				queueNode.add(outFlows[i].getTarget());
			}
		}
		
		nodes.append(forkNode.toString());
		
		return activityNode;
	}
	
	private IActivityNode defineJoin(IActivityNode activityNode, StringBuilder nodes) {
		StringBuilder joinNode = new StringBuilder();
		ArrayList<String> alphabet = new ArrayList<>();
		String nameJoin = activityNode.getName() + "_" + ad.getName();
		String nameJoinTermination = activityNode.getName() + "_" + ad.getName() + "_t";
		String endDiagram = "END_DIAGRAM_" + ad.getName();
		IFlow outFlows[] = activityNode.getOutgoings();
		IFlow inFlows[] = activityNode.getIncomings();
		HashMap<Pair<String, String>, String> nameObjects = new HashMap<>();
		String nameObject = null;
		boolean syncBool = false;
		boolean sync2Bool = false;
		
		ArrayList<Pair<String, String>> ceInitials = new ArrayList<>();
		for (Pair<String, String> tupla : syncChannelsEdge.keySet()) {	//get all sync channels
			if (tupla.getValue().equals(activityNode.getName())) {
				ceInitials.add(tupla);
				syncBool = true;
			}
		}
		
		for (Pair<String, String> tupla : syncObjectsEdge.keySet()) {	//get all sync channels
			if (tupla.getValue().equals(activityNode.getName())) {
				String ceIn2 = syncObjectsEdge.get(tupla);
				nameObject = objectEdges.get(ceIn2);
				ceInitials.add(tupla);
				nameObjects.put(tupla, nameObject);
				sync2Bool = true;
			}
		}

		if (sync2Bool) {
			joinNode.append(nameJoin + "(" + nameObject + ") = ");
		} else {
			joinNode.append(nameJoin + " = ");
		}
		
		joinNode.append("(");
		
		for (int i = 0; i < ceInitials.size(); i++) {
			String ceIn = syncChannelsEdge.get(ceInitials.get(i));	//get the parallel input channels
			String oeIn = syncObjectsEdge.get(ceInitials.get(i));
			
			if (ceIn != null) {
				joinNode.append("(");
				
				if (i >= 0 && i < ceInitials.size() - 1) {
					ce(alphabet, joinNode, ceIn, " -> SKIP) ||| ");
				} else {
					ce(alphabet, joinNode, ceIn, " -> SKIP)");
				}
			} else {
				
				nameObject = nameObjects.get(ceInitials.get(i));
				
				joinNode.append("(");
				
				if (i >= 0 && i < ceInitials.size() - 1) {
					ce(alphabet, joinNode, oeIn, "?" + nameObject + " -> SKIP) ||| ");
				} else {
					ce(alphabet, joinNode, oeIn, "?" + nameObject + " -> SKIP)");
				}
			}
			
		}
		
		joinNode.append("); ");

		update(alphabet, joinNode, inFlows.length, outFlows.length);
		
		joinNode.append("(");
		
		if (syncBool) {
			for (int i = 0; i <  outFlows.length; i++) {	//creates the parallel output channels
				String ce = createCE();
				syncChannelsEdge.put(new Pair<String, String>(activityNode.getName(), outFlows[i].getTarget().getName()), ce);
				
				joinNode.append("(");
				
				if (i >= 0 && i < outFlows.length - 1) {
					ce(alphabet, joinNode, ce, " -> SKIP) ||| ");
				} else {
					ce(alphabet, joinNode, ce, " -> SKIP)");
				}
			}	
		} else if (sync2Bool) {	
			for (int i = 0; i <  outFlows.length; i++) {	//creates the parallel output channels
				String oe = createOE(nameObject);
				syncObjectsEdge.put(new Pair<String, String>(activityNode.getName(), outFlows[i].getTarget().getName()), oe);
				objectEdges.put(oe, nameObject);
				joinNode.append("(");
				
				if (i >= 0 && i < outFlows.length - 1) {
					ce(alphabet, joinNode, oe, "!" + nameObject + " -> SKIP) ||| ");
				} else {
					ce(alphabet, joinNode, oe, "!" + nameObject + " -> SKIP)");
				}
			}
		}
		
		joinNode.append("); ");
		
		if (sync2Bool) {
			joinNode.append(nameJoin + "(" + nameObject + ")\n");
		} else {
			joinNode.append(nameJoin + "\n");
		}
	
		joinNode.append(nameJoinTermination + " = ");
		
		if (sync2Bool) {
			joinNode.append(nameJoin + "(0) /\\ " + endDiagram + "\n");
		} else {
			joinNode.append(nameJoin + " /\\ " + endDiagram + "\n");
		}

		alphabet.add("endDiagram_" + ad.getName());
		alphabetNode.put(activityNode.getName(), alphabet);
		
		IFlow flow[] = outFlows;
		activityNode = flow[0].getTarget();	//set next action or control node
		
		nodes.append(joinNode.toString());
		
		return activityNode;
	}
	
	private IActivityNode defineMerge(IActivityNode activityNode, StringBuilder nodes) {
		StringBuilder merge = new StringBuilder();
		ArrayList<String> alphabet = new ArrayList<>();
		String nameMerge = activityNode.getName() + "_" + ad.getName();
		String nameMergeTermination = activityNode.getName() + "_" + ad.getName() + "_t";
		String endDiagram = "END_DIAGRAM_" + ad.getName();
		IFlow outFlows[] = activityNode.getOutgoings();
		//IFlow inFlows[] = activityNode.getIncomings();
		String nameObject = null;
		HashMap<Pair<String, String>, String> nameObjects = new HashMap<>();
		
		ArrayList<Pair<String, String>> ceInitials = new ArrayList<>();
		for (Pair<String, String> tupla : syncChannelsEdge.keySet()) {	//get all sync channels
			if (tupla.getValue().equals(activityNode.getName())) {
				ceInitials.add(tupla);
			}
		}
		
		for (Pair<String, String> tupla : syncObjectsEdge.keySet()) {	//get all sync channels
			if (tupla.getValue().equals(activityNode.getName())) {
				String ceIn2 = syncObjectsEdge.get(tupla);
				nameObject = objectEdges.get(ceIn2);
				ceInitials.add(tupla);
				nameObjects.put(tupla, nameObject);
			}
		}

		if (nameObjects.size() > 0) {
			merge.append(nameMerge + "(" + nameObject + ") = ");
		} else {
			merge.append(nameMerge + " = ");
		}
		
		merge.append("(");
		
		for (int i = 0; i < ceInitials.size(); i++) {
			String ceIn = syncChannelsEdge.get(ceInitials.get(i));	//get the parallel input channels
			String oeIn = syncObjectsEdge.get(ceInitials.get(i));
			
			if (ceIn != null) {
				merge.append("(");
				
				if (i >= 0 && i < ceInitials.size() - 1) {
					ce(alphabet, merge, ceIn, " -> SKIP) [] ");
				} else {
					ce(alphabet, merge, ceIn, " -> SKIP)");
				}
			} else {
				
				nameObject = nameObjects.get(ceInitials.get(i));
				
				merge.append("(");
				
				if (i >= 0 && i < ceInitials.size() - 1) {
					ce(alphabet, merge, oeIn, "?" + nameObject + " -> SKIP) [] ");
				} else {
					ce(alphabet, merge, oeIn, "?" + nameObject + " -> SKIP)");
				}
			}
			
		}
		
		merge.append("); ");
		
		update(alphabet, merge, 1, 1);
		
//		merge.append("(");
//		
//		if (syncBool) {
//			for (int i = 0; i <  outFlows.length; i++) {	//creates the parallel output channels
//				String ce = createCE();
//				syncChannelsEdge.put(new Pair<String, String>(activityNode.getName(), outFlows[i].getTarget().getName()), ce);
//				
//				merge.append("(");
//				
//				if (i >= 0 && i < outFlows.length - 1) {
//					ce(alphabet, merge, ce, " -> SKIP) ||| ");
//				} else {
//					ce(alphabet, merge, ce, " -> SKIP)");
//				}
//			}	
//		} else if (sync2Bool) {	
//			for (int i = 0; i <  outFlows.length; i++) {	//creates the parallel output channels
//				String oe = createOE(nameObject);
//				syncObjectsEdge.put(new Pair<String, String>(activityNode.getName(), outFlows[i].getTarget().getName()), oe);
//				objectEdges.put(oe, nameObject);
//				merge.append("(");
//				
//				if (i >= 0 && i < outFlows.length - 1) {
//					ce(alphabet, merge, oe, "!" + nameObject + " -> SKIP) ||| ");
//				} else {
//					ce(alphabet, merge, oe, "!" + nameObject + " -> SKIP)");
//				}
//			}
//		}
//		
//		merge.append("); ");
		
		if (nameObjects.size() > 0) {
			for (IFlow flow : outFlows) {	//creates output channels
				String oe = createOE(nameObject);
				syncObjectsEdge.put(new Pair<String, String>(activityNode.getName(), flow.getTarget().getName()), oe);
				objectEdges.put(oe, nameObject);
				ce(alphabet, merge, oe, "!" + nameObject + " -> ");
				
				merge.append(nameMerge + "(" + nameObject + ")\n");
				merge.append(nameMergeTermination + " = ");
				merge.append(nameMerge + "(0) /\\ " + endDiagram + "\n");
			}
		} else {
			for (IFlow flow : outFlows) {	//creates output channels
				String ce = createCE();
				syncChannelsEdge.put(new Pair<String, String>(activityNode.getName(), flow.getTarget().getName()), ce);
				ce(alphabet, merge, ce, " -> ");
				
				merge.append(nameMerge + "\n");
				merge.append(nameMergeTermination + " = ");
				merge.append(nameMerge + " /\\ " + endDiagram + "\n");
			}
		}

		alphabet.add("endDiagram_" + ad.getName());
		alphabetNode.put(activityNode.getName(), alphabet);
		
		IFlow flow[] = outFlows;
		activityNode = flow[0].getTarget();	//set next action or control node
		
		nodes.append(merge.toString());
		
		return activityNode;
	}
	
	private IActivityNode defineDecision(IActivityNode activityNode, StringBuilder nodes) {
		StringBuilder decision = new StringBuilder();
		ArrayList<String> alphabet = new ArrayList<>();
		String nameDecision = activityNode.getName() + "_" + ad.getName();
		String nameDecisionTermination = activityNode.getName() + "_" + ad.getName() + "_t";
		String endDiagram = "END_DIAGRAM_" + ad.getName();
		IFlow outFlows[] = activityNode.getOutgoings();
		IFlow inFlows[] = activityNode.getIncomings();
		String decisionInputFlow = null;
		
		for (int i = 0; i < inFlows.length; i++) {	
			
			String stereotype[] = inFlows[i].getStereotypes();
		
			for (int j = 0; j < stereotype.length; j++) {
				if (stereotype[j].equals("decisionInputFlow")) {
					decisionInputFlow = inFlows[i].getSource().getName();
				}
			}
		}
		
		if (decisionInputFlow != null && inFlows.length == 1) {
			decision.append(nameDecision + " = ");
			
			Pair<String, String> sync = new Pair<String, String>("", "");
			for (Pair<String, String> tupla : syncObjectsEdge.keySet()) {	//get sync channel
				if (tupla.getValue().equals(activityNode.getName())) {
					sync = tupla;
				}
			}
			
			String ceIn = syncObjectsEdge.get(sync);
			
			oe(alphabet, decision, ceIn, "?" + decisionInputFlow + " -> ");
			update(alphabet, decision, 1, 1);
			
			decision.append("(");
			
			for (int i = 0; i < outFlows.length; i++) {	//creates the parallel output channels
				String oe = createOE(decisionInputFlow);
				syncObjectsEdge.put(new Pair<String, String>(activityNode.getName(), outFlows[i].getTarget().getName()), oe);
				objectEdges.put(oe, decisionInputFlow);
				
				decision.append(outFlows[i].getGuard() + " & (");
				
				if (i >= 0 && i < outFlows.length - 1) {
					oe(alphabet, decision, oe, "!" + decisionInputFlow + " -> SKIP) [] ");
				} else {
					oe(alphabet, decision, oe, "!" + decisionInputFlow + " -> SKIP)");
				}
			}
			
			decision.append("); ");
			
			decision.append(nameDecision + "\n");
			
			decision.append(nameDecisionTermination + " = ");
			decision.append(nameDecision + " /\\ " + endDiagram + "\n");

			alphabet.add("endDiagram_" + ad.getName());
			alphabetNode.put(activityNode.getName(), alphabet);
			
			activityNode = outFlows[0].getTarget();	//set next action or control node
			
			for (int i = 1; i < outFlows.length; i++) {	//puts the remaining nodes in the queue
				if (!queueNode.contains(outFlows[i].getTarget())) {
					queueNode.add(outFlows[i].getTarget());
				}
			}
			
			nodes.append(decision.toString());
		} else if (decisionInputFlow != null && inFlows.length > 1) {
			decision.append(nameDecision + "(" + decisionInputFlow + ") = ");
			
			Pair<String, String> sync2 = new Pair<String, String>("", "");
			for (Pair<String, String> tupla : syncChannelsEdge.keySet()) {	//get sync channel
				if (tupla.getValue().equals(activityNode.getName())) {
					sync2 = tupla;
				}
			}
			
			Pair<String, String> sync = new Pair<String, String>("", "");
			for (Pair<String, String> tupla : syncObjectsEdge.keySet()) {	//get sync channel
				if (tupla.getValue().equals(activityNode.getName())) {
					sync = tupla;
				}
			}
			
			String ceIn2 = syncChannelsEdge.get(sync2);
			String ceIn = syncObjectsEdge.get(sync);
			
			decision.append("((");
			ce(alphabet, decision, ceIn2, " -> SKIP");
			
			decision.append(") ||| (");
			oe(alphabet, decision, ceIn, "?" + decisionInputFlow + " -> SKIP");
			decision.append(")); ");
			
			update(alphabet, decision, 2, 1);
			
			decision.append("(");
			
			for (int i = 0; i < outFlows.length; i++) {	//creates the parallel output channels
				String ce = createCE();
				syncChannelsEdge.put(new Pair<String, String>(activityNode.getName(), outFlows[i].getTarget().getName()), ce);
				
				decision.append(outFlows[i].getGuard() + " & (");
				
				if (i >= 0 && i < outFlows.length - 1) {
					ce(alphabet, decision, ce, " -> SKIP) [] ");
				} else {
					ce(alphabet, decision, ce, " -> SKIP)");
				}
			}
			
			decision.append("); ");
			
			decision.append(nameDecision + "(" + decisionInputFlow + ")\n");
			
			decision.append(nameDecisionTermination + " = ");
			decision.append(nameDecision + "(0) /\\ " + endDiagram + "\n");

			alphabet.add("endDiagram_" + ad.getName());
			alphabetNode.put(activityNode.getName(), alphabet);
			
			activityNode = outFlows[0].getTarget();	//set next action or control node
			
			for (int i = 1; i < outFlows.length; i++) {	//puts the remaining nodes in the queue
				if (!queueNode.contains(outFlows[i].getTarget())) {
					queueNode.add(outFlows[i].getTarget());
				}
			}
			
			nodes.append(decision.toString());
		} else {
			decision.append(nameDecision + " = ");
			
			Pair<String, String> sync = new Pair<String, String>("", "");
			for (Pair<String, String> tupla : syncChannelsEdge.keySet()) {	//get sync channel
				if (tupla.getValue().equals(activityNode.getName())) {
					sync = tupla;
				}
			}
			
			String ceIn = syncChannelsEdge.get(sync);
			
			ce(alphabet, decision, ceIn, " -> ");
			update(alphabet, decision, 1, 1);
			
			decision.append("(");
			
			for (int i = 0; i <  outFlows.length; i++) {	//creates the parallel output channels
				String ce = createCE();
				syncChannelsEdge.put(new Pair<String, String>(activityNode.getName(), outFlows[i].getTarget().getName()), ce);
				
				decision.append("(");
				
				if (i >= 0 && i < outFlows.length - 1) {
					ce(alphabet, decision, ce, " -> SKIP) [] ");
				} else {
					ce(alphabet, decision, ce, " -> SKIP)");
				}
			}
			
			decision.append("); ");
			
			decision.append(nameDecision + "\n");
			
			decision.append(nameDecisionTermination + " = ");
			decision.append(nameDecision + " /\\ " + endDiagram + "\n");

			alphabet.add("endDiagram_" + ad.getName());
			alphabetNode.put(activityNode.getName(), alphabet);
			
			activityNode = outFlows[0].getTarget();	//set next action or control node
			
			for (int i = 1; i < outFlows.length; i++) {	//puts the remaining nodes in the queue
				if (!queueNode.contains(outFlows[i].getTarget())) {
					queueNode.add(outFlows[i].getTarget());
				}
			}
			
			nodes.append(decision.toString());
		}
		
		return activityNode;
	}
	
	private IActivityNode defineFlowFinal (IActivityNode activityNode, StringBuilder nodes) {
		StringBuilder flowFinal = new StringBuilder();
		ArrayList<String> alphabet = new ArrayList<>();
		String nameFlowFinal = activityNode.getName() + "_" + ad.getName();
		String nameFlowFinalTermination = activityNode.getName() + "_" + ad.getName() + "_t" ;
		String endDiagram = "END_DIAGRAM_" + ad.getName();
		HashMap<Pair<String, String>, String> nameObjects = new HashMap<>();
		
		flowFinal.append(nameFlowFinal + " = ");

		ArrayList<Pair<String, String>> ceInitials = new ArrayList<>();
		for (Pair<String, String> tupla : syncChannelsEdge.keySet()) {	//get all sync channels
			if (tupla.getValue().equals(activityNode.getName())) {
				ceInitials.add(tupla);
			}
		}

		for (Pair<String, String> tupla : syncObjectsEdge.keySet()) {	//get all sync channels
			if (tupla.getValue().equals(activityNode.getName())) {
				String ceIn2 = syncObjectsEdge.get(tupla);
				nameObjects.put(tupla, objectEdges.get(ceIn2));
				ceInitials.add(tupla);
			}
		}
		
		flowFinal.append("(");
		for (int i = 0; i < ceInitials.size(); i++) {
			String ceIn = syncChannelsEdge.get(ceInitials.get(i));	//get the parallel input channels
			String oeIn = syncObjectsEdge.get(ceInitials.get(i));
			
			if (ceIn != null) {
				flowFinal.append("(");
				
				if (i >= 0 && i < ceInitials.size() - 1) {
					ce(alphabet, flowFinal, ceIn, " -> SKIP) [] ");
				} else {
					ce(alphabet, flowFinal, ceIn, " -> SKIP)");
				}
			} else {
				
				String nameObject = nameObjects.get(ceInitials.get(i));
				
				flowFinal.append("(");
				
				if (i >= 0 && i < ceInitials.size() - 1) {
					ce(alphabet, flowFinal, oeIn, "?" + nameObject + " -> SKIP) [] ");
				} else {
					ce(alphabet, flowFinal, oeIn, "?" + nameObject + " -> SKIP)");
				}
			}
			
		}
		
		flowFinal.append("); ");
		
		update(alphabet, flowFinal, 1, 0);
		
		flowFinal.append(nameFlowFinal + "\n");
		
		flowFinal.append(nameFlowFinalTermination + " = ");
		flowFinal.append(nameFlowFinal + " /\\ " + endDiagram + "\n");

		alphabet.add("endDiagram_" + ad.getName() );
		alphabetNode.put(activityNode.getName(), alphabet);

		activityNode = null;	

		nodes.append(flowFinal.toString());
		
		return activityNode;
	}
	
	private IActivityNode defineParameterNode (IActivityNode activityNode, StringBuilder nodes) {
		StringBuilder parameterNode = new StringBuilder();
		ArrayList<String> alphabet = new ArrayList<>();
		String nameParameterNode =  "parameter_" + activityNode.getName() + "_t";
		IFlow outFlows[] = activityNode.getOutgoings();
		IFlow inFlows[] = activityNode.getIncomings();
		
		parameterNode.append(nameParameterNode + " = ");
		
		update(alphabet, parameterNode, inFlows.length, outFlows.length);
		get(alphabet, parameterNode, activityNode.getName());
		
		parameterNode.append("(");
		
		for (int i = 0; i <  outFlows.length; i++) {	//creates the parallel output channels
			String oe = createOE(activityNode.getName());
			syncObjectsEdge.put(new Pair<String, String>(activityNode.getName(), outFlows[i].getTarget().getName()), oe);
			objectEdges.put(oe, activityNode.getName());
			
			parameterNode.append("(");
			
			if (i >= 0 && i < outFlows.length - 1) {
				oe(alphabet, parameterNode, oe, "!" + activityNode.getName() + " -> SKIP) ||| ");
			} else {
				oe(alphabet, parameterNode, oe, "!" + activityNode.getName() + " -> SKIP)");
			}
		}
		
		parameterNode.append(")\n");

		allInitial.add(nameParameterNode);	
		for (String channel : alphabet) {
			if (!alphabetAllInitialAndParameter.contains(channel)) {
				alphabetAllInitialAndParameter.add(channel);
			}
		}

		activityNode = outFlows[0].getTarget();	//set next action or control node
		System.out.println(outFlows.length);
		for (int i = 1; i < outFlows.length; i++) {	//puts the remaining nodes in the queue
			if (!queueNode.contains(outFlows[i].getTarget())) {
				queueNode.add(outFlows[i].getTarget());
			}
		}
		
		nodes.append(parameterNode.toString());
		
		return activityNode;
	} 
	
	private String createCE() {
		return "ce_" + ad.getName() + "." + countCe_ad++;
	}
	
	private String createOE(String nameObject) {
		return "oe_" + nameObject + "_" + ad.getName() + "." + countOe_ad++;
	}
	
	private void startActivity(ArrayList<String> alphabetNode, StringBuilder action, ArrayList<String> inputPins) {
		String startActivity = "startActivity_" + ad.getName();	
		alphabetNode.add(startActivity);
		
		for (String pin : inputPins) {
			startActivity += "!" + pin;
		}
		
		action.append(startActivity + " -> ");
	}
	
	private void endActivity(ArrayList<String> alphabetNode, StringBuilder action, ArrayList<String> outputPins) {
		String endActivity = "endActivity_" + ad.getName();
		alphabetNode.add(endActivity);
		
		for (String pin : outputPins) {
			endActivity += "?" + pin;
		}
		
		action.append(endActivity + " -> ");
	}
	
	private void get(ArrayList<String> alphabetNode, StringBuilder action, String nameObject) {
		String get = "get_" + nameObject + "_" + ad.getName() + "." + countGet_ad++;
		alphabetNode.add(get);
		action.append(get +"?" + nameObject + " -> ");
	}
	
	private void set(ArrayList<String> alphabetNode, StringBuilder action, String nameObject) {
		String set = "set_" + nameObject + "_" + ad.getName() + "." + countSet_ad++;
		alphabetNode.add(set);
		action.append(set +"!" + nameObject + " -> ");
	}
	
	private void lock(ArrayList<String> alphabetNode, StringBuilder action, int inOut, String nameNode) {
		if (inOut == 0) {
			String lock = "lock_" + nameNode;
			alphabetNode.add(lock);
			lockChannel.add(nameNode);
			action.append(lock + ".lock -> ");
		} else {
			String lock = "lock_" + nameNode;
			action.append(lock + ".unlock -> ");
		}
	}
	
	private void event(ArrayList<String> alphabet, String nameAction, StringBuilder action) {
		alphabet.add("event_" + nameAction);
		eventChannel.add("event_" + nameAction);
		action.append("event_" + nameAction + " -> ");
	}
	
	private void ce(ArrayList<String> alphabetNode, StringBuilder action, String ce, String posCe) {
		alphabetNode.add(ce);
		action.append(ce + posCe);
	}
	
	private void oe(ArrayList<String> alphabetNode, StringBuilder action, String oe, String posOe) {
		alphabetNode.add(oe);
		action.append(oe + posOe);
	}
	
	private void update(ArrayList<String> alphabetNode, StringBuilder action, int countInFlows, int countOutFlows) {
		String update = "update_" + ad.getName() + "." + countUpdate_ad++;
		alphabetNode.add(update);
		action.append(update + "!(" + countOutFlows + "-" + countInFlows + ") -> ");
		
		int result = countOutFlows - countInFlows;
		
		if (result < limiteInf) {
			limiteInf = result;
			
			if (limiteSup == -99) {
				limiteSup = result;
			}
			
		}
		
		if (result > limiteSup) {
			limiteSup = result;
			
			if (limiteInf == 99) {
				limiteInf = result;
			}
			
		}
		
	}

	private void clear(ArrayList<String> alphabetNode, StringBuilder action) {
		String update = "clear_" + ad.getName() + "." + countClear_ad++;
		alphabetNode.add(update);
		action.append(update + " -> ");
	}
	
}
