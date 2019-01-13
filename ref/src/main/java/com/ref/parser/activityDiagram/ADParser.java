package com.ref.parser.activityDiagram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
	private HashMap<String, String> syncChannelsEdge;			//ID flow, channel
	private HashMap<String, String> syncObjectsEdge;
	private HashMap<String, String> objectEdges;				//channel; name
	private ArrayList<IActivityNode> queueNode;
	private ArrayList<IActivity> callBehaviorList;
	private ArrayList<String> eventChannel;
	private ArrayList<String> lockChannel;
	private ArrayList<String> allInitial;
	private ArrayList<String> alphabetAllInitialAndParameter;
	private HashMap<String, String> parameterNodesInput;		//name; type
	private HashMap<String, String> parameterNodesOutput;
	private List<Pair<String, String>> memoryLocal;				//nameNode, nameObject
	
	public ADParser(IActivity ad, String nameAD) {
		this.ad = ad;
		setName(nameAD);
		this.countGet_ad = 1;
		this.countSet_ad = 2;
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
		memoryLocal = new ArrayList<>();
	}
	
	public void clearBuffer() {
		this.countGet_ad = 1;
		if (ad.getName().equals("action4") || ad.getName().equals("merge3") || ad.getName().equals("join4")) {
			this.countSet_ad = 3;
		} else if(ad.getName().equals("decision1") || ad.getName().equals("decision3") || ad.getName().equals("action3")
				|| ad.getName().equals("action5") || ad.getName().equals("action6") || ad.getName().equals("flowFinal3")
				|| ad.getName().equals("flowFinal4") || ad.getName().equals("flowFinal5") || ad.getName().equals("final1")
				|| ad.getName().equals("fork2") || ad.getName().equals("join2") || ad.getName().equals("join2")
				|| ad.getName().equals("join3") || ad.getName().equals("merge4")){
			this.countSet_ad = 2;
		} else {
			this.countSet_ad = 1;
		}
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
		memoryLocal = new ArrayList<>();
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
		
		if (parameterNodesInput.size() > 0 || parameterNodesOutput.size() > 0 || memoryLocal.size() > 0) {
			
			for (String get : parameterNodesInput.keySet()) {
				channels.append("channel get_" + get + "_" + nameDiagram + ": countGet_" + nameDiagram + "." + get + "_" + nameDiagram + "\n");
			}
			
			for (String get : parameterNodesOutput.keySet()) {
				channels.append("channel get_" + get + "_" + nameDiagram + ": countGet_" + nameDiagram + "." + get + "_" + nameDiagram + "\n");
			}
			
			for (Pair<String, String> pair : memoryLocal) {
				channels.append("channel get_" + pair.getValue() + "_" + pair.getKey() + "_" + nameDiagram + ": countGet_" + nameDiagram + "." + pair.getValue() + "_" + nameDiagram + "\n");
			}
			
			for (String set : parameterNodesInput.keySet()) {
				channels.append("channel set_" + set + "_" + nameDiagram + ": countSet_" + nameDiagram + "." + set + "_" + nameDiagram + "\n");
			}
			
			for (String set : parameterNodesOutput.keySet()) {
				channels.append("channel set_" + set + "_" + nameDiagram + ": countSet_" + nameDiagram + "." + set + "_" + nameDiagram + "\n");
			}
			
			for (Pair<String, String> pair : memoryLocal) {
				channels.append("channel set_" + pair.getValue() + "_" + pair.getKey() + "_" + nameDiagram + ": countSet_" + nameDiagram + "." + pair.getValue() + "_" + nameDiagram + "\n");
			}
			
		}
		
		if (countCe_ad > 1) {
			channels.append("channel ce_" + nameDiagram + ": countCe_" + nameDiagram + "\n");
		}
		
		if (syncObjectsEdge.size() > 0) {
			ArrayList<String> allObjectEdges = new ArrayList<>();
			for (String objectEdge : syncObjectsEdge.values()) {	//get sync channel
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
	
	public String defineMemorys() {
		StringBuilder memory = new StringBuilder();
		String nameDiagram = ad.getName();
		
		for (Pair<String, String> pair : memoryLocal) {
			memory.append("Mem_" + pair.getKey() + "_" + nameDiagram + "_" + pair.getValue() + "(" + pair.getValue() + ") = ");
			memory.append("get_" + pair.getValue() + "_" + pair.getKey() + "_" + nameDiagram + "?c!" + pair.getValue() + " -> ");
			memory.append("Mem_" + pair.getKey() + "_" + nameDiagram + "_" + pair.getValue() + "(" + pair.getValue() + ") [] ");
			memory.append("set_" + pair.getValue() + "_" + pair.getKey() + "_" + nameDiagram + "?c?" + pair.getValue() + " -> ");
			memory.append("Mem_" + pair.getKey() + "_" + nameDiagram + "_" + pair.getValue() + "(" + pair.getValue() + ")\n");
			
			memory.append("Mem_" + pair.getKey() + "_" + nameDiagram + "_" + pair.getValue() + "_t" + "(" + pair.getValue() + ") = ");
			memory.append("Mem_" + pair.getKey() + "_" + nameDiagram + "_" + pair.getValue() + "(" + pair.getValue() + ") /\\ END_DIAGRAM_" + nameDiagram + "\n");
		}
		
		
		if (parameterNodesInput.size() > 0 || parameterNodesOutput.size() > 0) {
			for (String input : parameterNodesInput.keySet()) {
				memory.append("Mem_" + nameDiagram + "_" + input + "(" + input + ") = ");
				memory.append("get_" + input + "_" + nameDiagram + "?c!" + input + " -> ");
				memory.append("Mem_" + nameDiagram + "_" + input + "(" + input + ") [] ");
				memory.append("set_" + input + "_" + nameDiagram + "?c?" + input + " -> ");
				memory.append("Mem_" + nameDiagram + "_" + input + "(" + input + ")\n");
				
				memory.append("Mem_" + nameDiagram + "_" + input + "_t" + "(" + input + ") = ");
				memory.append("Mem_" + nameDiagram + "_" + input + "(" + input + ") /\\ (endActivity_" + nameDiagram + "?" + input + " -> SKIP)\n");
			}
			
			for (String output : parameterNodesOutput.keySet()) {
				memory.append("Mem_" + nameDiagram + "_" + output + "(" + output + ") = ");
				memory.append("get_" + output + "_" + nameDiagram + "?c!" + output + " -> ");
				memory.append("Mem_" + nameDiagram + "_" + output + "(" + output + ") [] ");
				memory.append("set_" + output + "_" + nameDiagram + "?c?" + output + " -> ");
				memory.append("Mem_" + nameDiagram + "_" + output + "(" + output + ")\n");
				
				memory.append("Mem_" + nameDiagram + "_" + output + "_t" + "(" + output + ") = ");
				memory.append("Mem_" + nameDiagram + "_" + output + "(" + output + ") /\\ (endActivity_" + nameDiagram + "?" + output + " -> SKIP)\n");
			}

			memory.append("Mem_" + nameDiagram + " = ");
			
			for (int i = 0; i < parameterNodesInput.size() + parameterNodesOutput.size() - 1; i++) {
				memory.append("(");
			}
			
			int i = 0;
			
			for (String input : parameterNodesInput.keySet()) {
				memory.append("Mem_" + nameDiagram + "_" + input + "_t(0)");
				
				if (i % 2 == 0 && parameterNodesInput.size() + parameterNodesOutput.size() > 1 && 
						i < parameterNodesInput.size() || parameterNodesOutput.size() > 0) {
					memory.append(" [|{|endActivity_" + nameDiagram + "|}|] ");
				} else if (parameterNodesInput.size() + parameterNodesOutput.size() > 1){
					memory.append(")");
				}
				
				i++;
			}
			
			for (String output : parameterNodesOutput.keySet()) {
				memory.append("Mem_" + nameDiagram + "_" + output + "_t(0)");
				
				if (i % 2 == 0 && parameterNodesOutput.size() > 1 && 
						i <  parameterNodesOutput.size()) {
					memory.append(" [|{|endActivity_" + nameDiagram + "|}|] ");
				} else if (parameterNodesInput.size() + parameterNodesOutput.size() > 1){
					memory.append(")");
				}
				
				i++;
			}
			
		}
		
		return memory.toString();
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
		int expectedInput = 0;
		
		while (queueNode.size() != 0) {
			IActivityNode activityNode = queueNode.get(0);
			queueNode.remove(0);
			
			input = countAmount(activityNode);
			if (activityNode != null) {
				if (activityNode instanceof IAction) {
					expectedInput = activityNode.getIncomings().length + ((IAction) activityNode).getInputs().length;
				} else {
					expectedInput = activityNode.getIncomings().length;
				}
			}
			
			while (activityNode != null && !alphabetNode.containsKey(activityNode.getName())	// Verifica se nó é nulo, se nó já foi criado e se todos os nós de entrada dele já foram criados
					&& input == expectedInput) {
				
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
				
				if (activityNode != null) {
					if (activityNode instanceof IAction) {
						expectedInput = activityNode.getIncomings().length + ((IAction) activityNode).getInputs().length;
					} else {
						expectedInput = activityNode.getIncomings().length;
					}
				}
				
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
			IFlow inFlow[] = activityNode.getIncomings();
			
			for (int i = 0; i <  inFlow.length; i++) {
				System.out.println("aq " + inFlow[i].getId() + " " + activityNode.getName() );
				if (syncChannelsEdge.containsKey(inFlow[i].getId())) {
					input++;
				}
			}	
			
			
			if (activityNode instanceof IAction) {
				IInputPin inPin[] = ((IAction) activityNode).getInputs();
				
				for (int i = 0; i <  inPin.length; i++) {
					IFlow inFlowPin[] = inPin[i].getIncomings();
					for (int x = 0; x < inFlowPin.length; x++) {
						if (syncObjectsEdge.containsKey(inFlowPin[x].getId())) {
							input++;
						}
					}
				}
				
			} else {
				for (int i = 0; i <  inFlow.length; i++) {
					if (syncObjectsEdge.containsKey(inFlow[i].getId())) {
						input++;
					}
				}
			}
			System.out.println(activityNode.getName() + " " + input + " " + activityNode.getIncomings().length);
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
		IOutputPin outPins[] = ((IAction) activityNode).getOutputs();
		IInputPin inPins[] = ((IAction) activityNode).getInputs();
//		boolean syncBool = false;
//		boolean sync2Bool = false;
		List<String> namesMemoryLocal = new ArrayList<>(); 
		int countInFlowPin = 0;
		int countOutFlowPin = 0;
		//ArrayList<Pair<String, String>> ceInitials = new ArrayList<>();
		//ArrayList<Pair<String, String>> oeInitials = new ArrayList<>();
		
		String definition = activityNode.getDefinition();
		String definitionFinal[] = new String[0];
		
		if (definition != null && !(definition.equals(""))) {
			definitionFinal = definition.replace(" ", "").split(";");
		}
		
		
		action.append(nameAction + " = ");
		
		
		action.append("(");
		for (int i = 0; i <  inFlows.length; i++) {
			if (syncChannelsEdge.containsKey(inFlows[i].getId())) {
				String ceIn = syncChannelsEdge.get(inFlows[i].getId());
				
				action.append("(");
				if (i >= 0 && (i < inFlows.length - 1 || inPins.length > 0)) {
					ce(alphabet, action, ceIn, " -> SKIP) ||| ");
				} else {
					ce(alphabet, action, ceIn, " -> SKIP)");
				}

				//ceInitials.add(tupla);
				//syncBool = true;
			}
		}

		for (int i = 0; i <  inPins.length; i++) {
			IFlow inFlowPin[] = inPins[i].getIncomings();
			for (int x = 0; x < inFlowPin.length; x++) {
				if (syncObjectsEdge.containsKey(inFlowPin[x].getId())) {
					String oeIn = syncObjectsEdge.get(inFlowPin[x].getId());
					//String nameObject = objectEdges.get(oeIn);
					String nameObject = inPins[i].getName();
					
					action.append("(");
					if (i >= 0 && (i < inPins.length - 1 || x < inFlowPin.length - 1)) {
						oe(alphabet, action, oeIn, "?" + nameObject, " -> ");
						setLocal(alphabet, action, nameObject, activityNode.getName(), nameObject);
						action.append("SKIP) ||| ");
					} else {
						oe(alphabet, action, oeIn, "?" + nameObject, " -> ");
						setLocal(alphabet, action, nameObject, activityNode.getName(), nameObject);
						action.append("SKIP)");
					}
					
					if (!namesMemoryLocal.contains(nameObject)) {
						namesMemoryLocal.add(nameObject);
					}
					
					//oeInitials.add(tupla);
					//sync2Bool = true;
				}
			}
		}
		
		action.append("); ");
		
		lock(alphabet, action, 0, nameAction);
		event(alphabet, nameAction, action);
		
		for (int i = 0; i < namesMemoryLocal.size(); i++) {
			for (int j = 0; j < definitionFinal.length; j++) {
				String expression[] = definitionFinal[j].split("=");
				if (expression[0].equals(namesMemoryLocal.get(i))){
					List<String> expReplaced = replaceExpression(expression[1]);	//get expression replace '+','-','*','/'
					for (String value : expReplaced) {				//get all parts
						for (int x = 0; x < namesMemoryLocal.size(); x++) {
							if (value.equals(namesMemoryLocal.get(x))) {
								getLocal(alphabet, action, namesMemoryLocal.get(x), activityNode.getName(), namesMemoryLocal.get(x));
							}
						}
					}
					
					setLocal(alphabet, action, expression[0], activityNode.getName(), "(" + expression[1] + ")");
					
				}
			}
		}
		
		//count outFlowsPin
		for (int i = 0; i < inPins.length; i++) {
			countInFlowPin += inPins[i].getIncomings().length;
		}
		
		for (int i = 0; i < outPins.length; i++) {
			countOutFlowPin += outPins[i].getOutgoings().length;
		}
		
		lock(alphabet, action, 1, nameAction);
		update(alphabet, action, inFlows.length + countInFlowPin, outFlows.length + countOutFlowPin);
		
		for (String nameObj : namesMemoryLocal) {
			getLocal(alphabet, action, nameObj, activityNode.getName(), nameObj);
		}
		
		action.append("(");
		
		for (int i = 0; i <  outFlows.length; i++) {	//creates the parallel output channels
			String ce = createCE();
			syncChannelsEdge.put(outFlows[i].getId(), ce);
			
			action.append("(");
			
			if (i >= 0 && (i < outFlows.length - 1 || outPins.length > 0)) {
				ce(alphabet, action, ce, " -> SKIP) ||| ");
			} else {
				ce(alphabet, action, ce, " -> SKIP)");
			} 
		}	
		
		String nameObject = "";
		
		for (int i = 0; i < inPins.length; i++) {
			IFlow inFlowPin[] = inPins[i].getIncomings();
			for (int x = 0; x < inFlowPin.length; x++) {
				String channel = syncObjectsEdge.get(inFlowPin[x].getId());
				nameObject += objectEdges.get(channel);
			}
		}
		
		for (int i = 0; i <  outPins.length; i++) {	//creates the parallel output channels
			IFlow outFlowPin[] = outPins[i].getOutgoings();
			
			for (int x = 0; x < outFlowPin.length; x++) {
				String oe = createOE(nameObject);
				syncObjectsEdge.put(outFlowPin[x].getId(), oe);	
				
				objectEdges.put(oe, nameObject);
				String value = "";
				for (int j = 0; j < definitionFinal.length; j++) {
					String expression[] = definitionFinal[j].split("=");
					if (expression[0].equals(outPins[i].getName())) {
						value = expression[1];
					}
				}
				
				action.append("(");
				if (i >= 0 && (i < outPins.length - 1 || x < outFlowPin.length - 1)) {
					oe(alphabet, action, oe, "!(" + value + ")", " -> SKIP) ||| ");
				} else {
					oe(alphabet, action, oe, "!(" + value + ")", " -> SKIP)");
				}
				
			}
		}	
		

		action.append("); ");
		
		action.append(nameAction + "\n");
		
		action.append(nameActionTermination + " = ");
		
		if (namesMemoryLocal.size() > 0) {
			for (int i = 0; i < namesMemoryLocal.size(); i++) {
				action.append("(");
			}
			action.append("(" + nameAction + " /\\ " + endDiagram + ") ");
			
			for (int i = 0; i < namesMemoryLocal.size(); i++) {
				action.append("[|{|");
				action.append("get_" + namesMemoryLocal.get(i) + "_" + activityNode.getName() + "_" + ad.getName() + ",");
				action.append("set_" + namesMemoryLocal.get(i) + "_" + activityNode.getName() + "_" + ad.getName() + ",");
				action.append("endDiagram_" + ad.getName());
				action.append("|}|] ");
				action.append("Mem_" + activityNode.getName() + "_" + ad.getName() + "_" + namesMemoryLocal.get(i) + "_t(0)) ");
			}
			
			action.append("\\{|");
			
			for (int i = 0; i < namesMemoryLocal.size(); i++) {
				if (i == namesMemoryLocal.size() - 1) {
					action.append("get_" + namesMemoryLocal.get(i) + "_" + activityNode.getName() + "_" + ad.getName() + ",");
					action.append("set_" + namesMemoryLocal.get(i) + "_" + activityNode.getName() + "_" + ad.getName());
				} else {
					action.append("get_" + namesMemoryLocal.get(i) + "_" + activityNode.getName() + "_" + ad.getName() + ",");
					action.append("set_" + namesMemoryLocal.get(i) + "_" + activityNode.getName() + "_" + ad.getName() + ",");
				}
			}
			
			action.append("|}\n");
			
		} else {
			action.append(nameAction + " /\\ " + endDiagram + "\n");
		}

		alphabet.add("endDiagram_" + ad.getName());
		alphabetNode.put(activityNode.getName(), alphabet);
		
		if (outFlows.length > 0) {
			activityNode = outFlows[0].getTarget();	//set next action or control node
			
			for (int i = 1; i < outFlows.length; i++) {	//puts the remaining nodes in the queue
				if (!queueNode.contains(outFlows[i].getTarget())) {
					queueNode.add(outFlows[i].getTarget());
				}
			}
			
			for (int i = 0; i <  outPins.length; i++) {	//creates the parallel output channels
				IFlow outFlowPin[] = outPins[i].getOutgoings();
				for (int x = 0; x < outFlowPin.length; x++) {
					if (outFlowPin[x].getTarget() instanceof IInputPin) {
						for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
							if (activityNodeSearch instanceof IAction) {
								IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
								for (int y = 0; y < inFlowPin.length; y++) {
									if (inFlowPin[y].getId().equals(outFlowPin[x].getTarget().getId())) {
										if (!queueNode.contains(activityNodeSearch)) {
											queueNode.add(activityNodeSearch);
										}
									}
								}
							}
						}
					} else {
						if (!queueNode.contains(outFlowPin[x].getTarget())) {
							queueNode.add(outFlowPin[x].getTarget());
						}
					}
				}
			}
			
		} else {
			
			IFlow outFlowOut[] = outPins[0].getOutgoings();
			if (outFlowOut[0].getTarget() instanceof IInputPin) {
				for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
					if (activityNodeSearch instanceof IAction) {
						IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
						for (int y = 0; y < inFlowPin.length; y++) {
							if (inFlowPin[y].getId().equals(outFlowOut[0].getTarget().getId())) {
								activityNode = activityNodeSearch;	
							}
						}
					}
				}
			} else {
				activityNode = outFlowOut[0].getTarget();	
			}
			
			for (int i = 0; i <  outPins.length; i++) {	//creates the parallel output channels
				IFlow outFlowPin[] = outPins[i].getOutgoings();
				for (int x = 0; x < outFlowPin.length; x++) {
					if (outFlowPin[x].getTarget() instanceof IInputPin) {
						for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
							if (activityNodeSearch instanceof IAction) {
								IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
								for (int y = 0; y < inFlowPin.length; y++) {
									if (inFlowPin[y].getId().equals(outFlowPin[x].getTarget().getId())) {
										if (!queueNode.contains(activityNodeSearch) && (i != 0 || x != 0)) {
											queueNode.add(activityNodeSearch);
										}
									}
								}
							}
						}
					} else {
						if (!queueNode.contains(outFlowPin[x].getTarget()) && (i != 0 || x != 0)) {
							queueNode.add(outFlowPin[x].getTarget());
						}
					}
				}
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
		HashMap<String, String> nameObjects = new HashMap<>();
		IFlow inFlows[] = activityNode.getIncomings();
		
		finalNode.append(nameFinalNode + " = ");

		ArrayList<String> ceInitials = new ArrayList<>();
		for (int i = 0; i <  inFlows.length; i++) {
			ceInitials.add(inFlows[i].getId());
			
			if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
				String ceIn2 = syncObjectsEdge.get(inFlows[i].getId());
				nameObjects.put(inFlows[i].getId(), objectEdges.get(ceIn2));
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
			syncChannelsEdge.put(outFlows[i].getId(), ce);
			System.out.println("aa " + outFlows[i].getId() + " " + outFlows[i].getTarget().getName());
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
		
		for (int i = 0; i <  inFlows.length; i++) {
			if (syncChannelsEdge.containsKey(inFlows[i].getId())) {
				String ceIn = syncChannelsEdge.get(inFlows[i].getId());
				ce(alphabet, callBehavior, ceIn, " -> ");
			}
		}
		
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
			syncChannelsEdge.put(flow.getId(), ce);
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
		
		for (int i = 0; i <  inFlows.length; i++) {
			if (syncChannelsEdge.containsKey(inFlows[i].getId())) {
				String ceIn = syncChannelsEdge.get(inFlows[i].getId());
				ce(alphabet, forkNode, ceIn, " -> ");
				syncBool = true;
			}
		}
		
		for (int i = 0; i <  inFlows.length; i++) {
			if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
				String oeIn = syncObjectsEdge.get(inFlows[i].getId());
				nameObject = objectEdges.get(oeIn);
				oe(alphabet, forkNode, oeIn, "?" + nameObject, " -> ");
				sync2Bool = true;
			}
		}
		
		update(alphabet, forkNode, inFlows.length, outFlows.length);
		
		forkNode.append("(");
		
		if (syncBool) {
			for (int i = 0; i <  outFlows.length; i++) {	//creates the parallel output channels
				String ce = createCE();
				syncChannelsEdge.put(outFlows[i].getId(), ce);
				
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
				syncObjectsEdge.put(outFlows[i].getId(), oe);
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
		
		if (outFlows[0].getTarget() instanceof IInputPin) {
			for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
				if (activityNodeSearch instanceof IAction) {
					IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
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
						IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
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
		HashMap<String, String> nameObjects = new HashMap<>();
		List<String> objects = new ArrayList<>();
		String nameObject = null;
		List<String> nameObjectAdded = new ArrayList<>();
		boolean syncBool = false;
		boolean sync2Bool = false;
		
		ArrayList<String> ceInitials = new ArrayList<>();
		for (int i = 0; i <  inFlows.length; i++) {
			ceInitials.add(inFlows[i].getId());
			if (syncChannelsEdge.containsKey(inFlows[i].getId())) {
				syncBool = true;
			}
			
			if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
				String ceIn2 = syncObjectsEdge.get(inFlows[i].getId());
				nameObject = objectEdges.get(ceIn2);
				nameObjects.put(inFlows[i].getId(), nameObject);
				sync2Bool = true;
			}
		}
		
		joinNode.append(nameJoin + " = (");
		
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
				
				if (!objects.contains(nameObject)) {
					objects.add(nameObject);
				}
				
				joinNode.append("(");
				
				if (i >= 0 && i < ceInitials.size() - 1) {
					ce(alphabet, joinNode, oeIn, "?" + nameObject + " -> ");
					setLocal(alphabet, joinNode, nameObject, activityNode.getName(), nameObject);
					joinNode.append("SKIP) ||| ");
				} else {
					ce(alphabet, joinNode, oeIn, "?" + nameObject + " -> ");
					setLocal(alphabet, joinNode, nameObject, activityNode.getName(), nameObject);
					joinNode.append("SKIP)");
				}
			}
			
		}
		
		joinNode.append("); ");

		update(alphabet, joinNode, inFlows.length, outFlows.length);
		
		if (sync2Bool) {
			for (String nameObjectOut : objects) {
				getLocal(alphabet, joinNode, nameObjectOut, activityNode.getName(), nameObjectOut);
			}
		}
		
		joinNode.append("(");
		
		nameObject = "";
		
		for (int i = 0; i < inFlows.length; i++) {
			String channel = syncObjectsEdge.get(inFlows[i].getId());
			if (objectEdges.get(channel) != null && !nameObjectAdded.contains(objectEdges.get(channel))) {
				nameObjectAdded.add(objectEdges.get(channel));
				nameObject += objectEdges.get(channel);
			}
		}
		
		if (sync2Bool) {	
			for (int i = 0; i <  objects.size(); i++) {	//creates the parallel output channels
				String oe = createOE(nameObject);
				syncObjectsEdge.put(outFlows[0].getId(), oe);	//just one output
				objectEdges.put(oe, nameObject);
				joinNode.append("(");
				
				if (i >= 0 && i < objects.size() - 1) {
					ce(alphabet, joinNode, oe, "!" + objects.get(i) + " -> SKIP) |~| ");
					countOe_ad--;
				} else {
					ce(alphabet, joinNode, oe, "!" + objects.get(i) + " -> SKIP)");
				}
			}
		} else if (syncBool) {
			for (int i = 0; i <  outFlows.length; i++) {	//creates the parallel output channels
				String ce = createCE();
				syncChannelsEdge.put(outFlows[i].getId(), ce);
				
				joinNode.append("(");
				
				if (i >= 0 && i < outFlows.length - 1) {
					ce(alphabet, joinNode, ce, " -> SKIP) ||| ");
				} else {
					ce(alphabet, joinNode, ce, " -> SKIP)");
				}
			}	
		}
		
		joinNode.append("); ");
		
		joinNode.append(nameJoin + "\n");
	
		joinNode.append(nameJoinTermination + " = ");
		
		for (int i = 0; i < objects.size(); i++) {
			joinNode.append("(");
		}
		
		joinNode.append("(" + nameJoin + " /\\ " + endDiagram + ")");

		for (int i = 0; i < objects.size(); i++) {	//creates the parallel output channels
			joinNode.append(" [|{|");
			joinNode.append("get_" + objects.get(i) + "_" + activityNode.getName() + "_" + ad.getName() + ",");
			joinNode.append("set_" + objects.get(i) + "_" + activityNode.getName() + "_" + ad.getName() + ",");
			joinNode.append("endDiagram_" + ad.getName() + "|}|] ");
			joinNode.append("Mem_" + activityNode.getName() + "_" + ad.getName() + "_" + objects.get(i) + "_t(0))");
		}
		
		if (objects.size() > 0) {
			joinNode.append(" \\{|");
			
			for (int i = 0; i < objects.size(); i++) {
				joinNode.append("get_" + objects.get(i) + "_" + activityNode.getName() + "_" + ad.getName() + ",");
				joinNode.append("set_" + objects.get(i) + "_" + activityNode.getName() + "_" + ad.getName());
				if (i < objects.size() - 1) {
					joinNode.append(",");
				}
			}
			
			joinNode.append("|}");
			
		} 
		
		joinNode.append("\n");
		
		alphabet.add("endDiagram_" + ad.getName());
		alphabetNode.put(activityNode.getName(), alphabet);
		
		if (outFlows[0].getTarget() instanceof IInputPin) {
			for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
				if (activityNodeSearch instanceof IAction) {
					IInputPin inPins[] = ((IAction) activityNodeSearch).getInputs();
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
		
		return activityNode;
	}
	
	private IActivityNode defineMerge(IActivityNode activityNode, StringBuilder nodes) {
		StringBuilder merge = new StringBuilder();
		ArrayList<String> alphabet = new ArrayList<>();
		String nameMerge = activityNode.getName() + "_" + ad.getName();
		String nameMergeTermination = activityNode.getName() + "_" + ad.getName() + "_t";
		String endDiagram = "END_DIAGRAM_" + ad.getName();
		IFlow outFlows[] = activityNode.getOutgoings();
		IFlow inFlows[] = activityNode.getIncomings();
		String nameObject = null;
		String nameObjectUnique = "";
		List<String> nameObjectAdded = new ArrayList<>();
		HashMap<String, String> nameObjects = new HashMap<>();
		List<String> namesMemoryLocal = new ArrayList<>(); 
		
		ArrayList<String> ceInitials = new ArrayList<>();
		for (int i = 0; i <  inFlows.length; i++) {
			ceInitials.add(inFlows[i].getId());
			
			if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
				String ceIn2 = syncObjectsEdge.get(inFlows[i].getId());
				nameObjects.put(inFlows[i].getId(), objectEdges.get(ceIn2));
			}
		}

		merge.append(nameMerge + " = ");
	
		merge.append("(");
		
		
		for (int i = 0; i < ceInitials.size(); i++) {		//get unique channel
			if (nameObjects.get(ceInitials.get(i)) != null) {
				if (!nameObjectAdded.contains(nameObjects.get(ceInitials.get(i)))) {
					nameObjectAdded.add(nameObjects.get(ceInitials.get(i)));
					nameObjectUnique += nameObjects.get(ceInitials.get(i));
				}
			}
		}
		
		if (!nameObjectUnique.equals("")) {
			namesMemoryLocal.add(nameObjectUnique);
		}
		
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
					ce(alphabet, merge, oeIn, "?" + nameObject + " -> ");
					setLocal(alphabet, merge, nameObjectUnique, activityNode.getName(), nameObject);
					merge.append("SKIP) [] ");
				} else {
					ce(alphabet, merge, oeIn, "?" + nameObject + " -> ");
					setLocal(alphabet, merge, nameObjectUnique, activityNode.getName(), nameObject);
					merge.append("SKIP)");
				}
			}
		}
		
		merge.append("); ");
		
		update(alphabet, merge, 1, 1);
		
		if (!nameObjectUnique.equals("")) {
			getLocal(alphabet, merge, nameObjectUnique, activityNode.getName(), nameObjectUnique);
			String oe = createOE(nameObjectUnique); //creates output channels
			syncObjectsEdge.put(outFlows[0].getId(), oe);
			objectEdges.put(oe, nameObjectUnique);
			oe(alphabet, merge, oe,"!" + nameObjectUnique, " -> ");
			
			merge.append(nameMerge + "\n");
			merge.append(nameMergeTermination + " = ");
			
			merge.append("((" + nameMerge + " /\\ " + endDiagram + ") ");
			
			merge.append("[|{|");
			merge.append("get_" + nameObjectUnique + "_" + activityNode.getName() + "_" + ad.getName() + ",");
			merge.append("set_" + nameObjectUnique + "_" + activityNode.getName() + "_" + ad.getName() + ",");
			merge.append("endDiagram_" + ad.getName());
			merge.append("|}|] ");
			merge.append("Mem_" + activityNode.getName() + "_" + ad.getName() + "_" + nameObjectUnique + "_t(0)) ");
			
			merge.append("\\{|");
			merge.append("get_" + nameObjectUnique + "_" + activityNode.getName() + "_" + ad.getName() + ",");
			merge.append("set_" + nameObjectUnique + "_" + activityNode.getName() + "_" + ad.getName());
			merge.append("|}\n");

		} else {
			String ce = createCE(); //creates output channels
			syncChannelsEdge.put(outFlows[0].getId(), ce);
			ce(alphabet, merge, ce, " -> ");
			
			merge.append(nameMerge + "\n");
			merge.append(nameMergeTermination + " = ");
			merge.append(nameMerge + " /\\ " + endDiagram + "\n");
		} 
		
		alphabet.add("endDiagram_" + ad.getName());
		alphabetNode.put(activityNode.getName(), alphabet);
		
		if (outFlows[0].getTarget() instanceof IInputPin) {
			for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
				if (activityNodeSearch instanceof IAction) {
					IInputPin inPins[] = ((IAction) activityNodeSearch).getInputs();
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
					//decisionInputFlow = inFlows[i].getSource().getName();
					decisionInputFlow = objectEdges.get(syncObjectsEdge.get(inFlows[i].getId()));
				}
			}
		}
		
		if (decisionInputFlow != null && inFlows.length == 1) { 	//just object
			decision.append(nameDecision + " = ");
			
			for (int i = 0; i <  inFlows.length; i++) {
				if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
					String ceIn = syncObjectsEdge.get(inFlows[i].getId());
					oe(alphabet, decision, ceIn, "?" + decisionInputFlow, " -> ");
				}
			}

			update(alphabet, decision, 1, 1);
			
			decision.append("(");
			
			for (int i = 0; i < outFlows.length; i++) {	//creates the parallel output channels
				String oe = createOE(decisionInputFlow);
				syncObjectsEdge.put(outFlows[i].getId(), oe);
				objectEdges.put(oe, decisionInputFlow);
				
				decision.append(outFlows[i].getGuard() + " & (");
				
				if (i >= 0 && i < outFlows.length - 1) {
					oe(alphabet, decision, oe, "!" + decisionInputFlow, " -> SKIP) [] ");
				} else {
					oe(alphabet, decision, oe, "!" + decisionInputFlow, " -> SKIP)");
				}
			}
			
			decision.append("); ");
			
			decision.append(nameDecision + "\n");
			
			decision.append(nameDecisionTermination + " = ");
			decision.append(nameDecision + " /\\ " + endDiagram + "\n");

			alphabet.add("endDiagram_" + ad.getName());
			alphabetNode.put(activityNode.getName(), alphabet);
			
//			activityNode = outFlows[0].getTarget();	//set next action or control node
//			
//			for (int i = 1; i < outFlows.length; i++) {	//puts the remaining nodes in the queue
//				if (!queueNode.contains(outFlows[i].getTarget())) {
//					queueNode.add(outFlows[i].getTarget());
//				}
//			}
			
			if (outFlows[0].getTarget() instanceof IInputPin) {
				for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
					if (activityNodeSearch instanceof IAction) {
						IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
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
							IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
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
			
			nodes.append(decision.toString());
		} else if (decisionInputFlow != null && inFlows.length > 1) {					//object and control
			decision.append(nameDecision + " = ");
			
			String sync2 = "";
//			for (Pair<String, String> tupla : syncChannelsEdge.keySet()) {	//get sync channel
//				if (tupla.getValue().equals(activityNode.getName())) {
//					sync2 = tupla;
//				}
//			}
//			
			String sync = "";
//			for (Pair<String, String> tupla : syncObjectsEdge.keySet()) {	//get sync channel
//				if (tupla.getValue().equals(activityNode.getName())) {
//					sync = tupla;
//				}
//			}
			
			for (int i = 0; i <  inFlows.length; i++) {
				if (syncChannelsEdge.containsKey(inFlows[i].getId())) {
					sync2 = inFlows[i].getId();
				}
				
				if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
					sync = inFlows[i].getId();
				}
			}
				
			
			
			String ceIn2 = syncChannelsEdge.get(sync2);
			String ceIn = syncObjectsEdge.get(sync);
			
			decision.append("((");
			ce(alphabet, decision, ceIn2, " -> SKIP");
			
			decision.append(") ||| (");
			oe(alphabet, decision, ceIn, "?" + decisionInputFlow, " -> ");
			
			setLocal(alphabet, decision, decisionInputFlow, activityNode.getName(), decisionInputFlow);
			decision.append("SKIP)); ");
			
			update(alphabet, decision, 2, 1);
			getLocal(alphabet, decision, decisionInputFlow, activityNode.getName(), decisionInputFlow);
			
			decision.append("(");
			
			for (int i = 0; i < outFlows.length; i++) {	//creates the parallel output channels
				String ce = createCE();
				syncChannelsEdge.put(outFlows[i].getId(), ce);
				
				decision.append(outFlows[i].getGuard() + " & (");
				
				if (i >= 0 && i < outFlows.length - 1) {
					ce(alphabet, decision, ce, " -> SKIP) [] ");
				} else {
					ce(alphabet, decision, ce, " -> SKIP)");
				}
			}
			
			decision.append("); ");
			
			decision.append(nameDecision + "\n");
			
			decision.append(nameDecisionTermination + " = ");
			decision.append("((" + nameDecision + " /\\ " + endDiagram + ") ");
	
			decision.append("[|{|");
			decision.append("get_" + decisionInputFlow + "_" + activityNode.getName() + "_" + ad.getName() + ",");
			decision.append("set_" + decisionInputFlow + "_" + activityNode.getName() + "_" + ad.getName() + ",");
			decision.append("endDiagram_" + ad.getName());
			decision.append("|}|] ");
			decision.append("Mem_" + activityNode.getName() + "_" + ad.getName() + "_" + decisionInputFlow + "_t(0)) ");
			
			decision.append("\\{|");
			decision.append("get_" + decisionInputFlow + "_" + activityNode.getName() + "_" + ad.getName() + ",");
			decision.append("set_" + decisionInputFlow + "_" + activityNode.getName() + "_" + ad.getName());
			decision.append("|}\n");
				
			alphabet.add("endDiagram_" + ad.getName());
			alphabetNode.put(activityNode.getName(), alphabet);

			activityNode = outFlows[0].getTarget();	//set next action or control node
			
			for (int i = 1; i < outFlows.length; i++) {	//puts the remaining nodes in the queue
				if (!queueNode.contains(outFlows[i].getTarget())) {
					queueNode.add(outFlows[i].getTarget());
				}
			}
			
			nodes.append(decision.toString());
		} else {		//just control
			decision.append(nameDecision + " = ");
			
			String sync = "";
//			for (Pair<String, String> tupla : syncChannelsEdge.keySet()) {	//get sync channel
//				if (tupla.getValue().equals(activityNode.getName())) {
//					sync = tupla;
//				}
//			}
			
			sync = inFlows[0].getId();
			
			String ceIn = syncChannelsEdge.get(sync);
			
			ce(alphabet, decision, ceIn, " -> ");
			update(alphabet, decision, 1, 1);
			
			decision.append("(");
			
			for (int i = 0; i <  outFlows.length; i++) {	//creates the parallel output channels
				String ce = createCE();
				syncChannelsEdge.put(outFlows[i].getId(), ce);
				
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
		HashMap<String, String> nameObjects = new HashMap<>();
		IFlow inFlows[] = activityNode.getIncomings();
		
		flowFinal.append(nameFlowFinal + " = ");

		ArrayList<String> ceInitials = new ArrayList<>();
		
		for (int i = 0; i <  inFlows.length; i++) {
			ceInitials.add(inFlows[i].getId());
			
			if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
				String ceIn2 = syncObjectsEdge.get(inFlows[i].getId());
				nameObjects.put(inFlows[i].getId(), objectEdges.get(ceIn2));
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
			syncObjectsEdge.put(outFlows[i].getId(), oe);
			objectEdges.put(oe, activityNode.getName());
			
			parameterNode.append("(");
			
			if (i >= 0 && i < outFlows.length - 1) {
				oe(alphabet, parameterNode, oe, "!" + activityNode.getName(), " -> SKIP) ||| ");
			} else {
				oe(alphabet, parameterNode, oe, "!" + activityNode.getName(), " -> SKIP)");
			}
		}
		
		parameterNode.append(")\n");

		allInitial.add(nameParameterNode);	
		for (String channel : alphabet) {
			if (!alphabetAllInitialAndParameter.contains(channel)) {
				alphabetAllInitialAndParameter.add(channel);
			}
		}
		
		if (outFlows[0].getTarget() instanceof IInputPin) {
			for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
				if (activityNodeSearch instanceof IAction) {
					IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
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
		
		
		for (int i = 1; i <  outFlows.length; i++) {	//creates the parallel output channels
			if (outFlows[i].getTarget() instanceof IInputPin) {
				for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
					if (activityNodeSearch instanceof IAction) {
						IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
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
	
	private void setLocal(ArrayList<String> alphabetNode, StringBuilder action, String nameObject, String nameNode, String data) {
		String set = "set_" + nameObject + "_" + nameNode + "_" + ad.getName() + "." + countSet_ad++;
		action.append(set +"!" + data + " -> ");
		Pair<String, String> memoryLocalPair = new Pair<String, String>(nameNode, nameObject);
		if (!memoryLocal.contains(memoryLocalPair)) {
			memoryLocal.add(memoryLocalPair);
		}
	}
	
	private void getLocal(ArrayList<String> alphabetNode, StringBuilder action, String nameObject, String nameNode, String data) {
		String get = "get_" + nameObject + "_" + nameNode + "_" + ad.getName() + "." + countGet_ad++;
		action.append(get +"?" + data + " -> ");
		Pair<String, String> memoryLocalPair = new Pair<String, String>(nameNode, nameObject);
		if (!memoryLocal.contains(memoryLocalPair)) {
			memoryLocal.add(memoryLocalPair);
		}
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
	
	private void oe(ArrayList<String> alphabetNode, StringBuilder action, String oe, String data, String posOe) {
		alphabetNode.add(oe);
		action.append(oe + data + posOe);
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
	
	private List<String> replaceExpression(String expression) {
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
		
		if (!value.equals("")) {	//add last value
			expReplaced.add(value);
		}
		
		return expReplaced;
	}
	
}
