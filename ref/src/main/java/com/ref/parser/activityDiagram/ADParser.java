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

import javafx.util.Pair;

public class ADParser {

	private IActivity ad;
	
	private int countGet_ad;
	private int countSet_ad;
	private int countCn_ad;
	private int countUpdate_ad;
	private int countClear_ad;
	//limiteUpdate_ad = {(-2)..2}
	
	private static HashMap<String, Integer> countCall;
	private HashMap<String, ArrayList<String>> alphabetNode;
	private HashMap<Pair<String, String>, String> syncChannels; 
	private ArrayList<IActivityNode> queueNode;
	private ArrayList<IActivity> callBehaviorList;
	private ArrayList<String> eventChannel;
	private ArrayList<String> lockChannel;
	private ArrayList<String> allInitial;
	private ArrayList<String> alphabetAllInitial;
	
	public ADParser(IActivity ad, String nameAD) {
		this.ad = ad;
		setName(nameAD);
		this.countGet_ad = 1;
		this.countSet_ad = 1;
		this.countCn_ad = 1;
		this.countUpdate_ad = 1;
		this.countClear_ad = 1;
		this.alphabetNode = new HashMap<>();
		countCall = new HashMap<>();
		//addCountCall(); //comentado durante os testes
		syncChannels = new HashMap<>();
		queueNode = new ArrayList<>();
		callBehaviorList = new ArrayList<>();
		eventChannel = new ArrayList<>();
		lockChannel = new ArrayList<>();
		allInitial = new ArrayList<>();
		alphabetAllInitial = new ArrayList<>();
	}
	
	public void clearBuffer() {
		this.countGet_ad = 1;
		this.countSet_ad = 1;
		this.countCn_ad = 1;
		this.countUpdate_ad = 1;
		this.countClear_ad = 1;
		this.alphabetNode = new HashMap<>();
		countCall.clear();
		addCountCall();
		syncChannels = new HashMap<>();
		queueNode = new ArrayList<>();
		callBehaviorList = new ArrayList<>();
		eventChannel = new ArrayList<>();
		lockChannel = new ArrayList<>();
		allInitial = new ArrayList<>();
		alphabetAllInitial = new ArrayList<>();
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
		boolean parameterIn = false;
		boolean parameterOut = false;
		
		for (IActivityNode activityNode : nodes) {
			if (activityNode instanceof IActivityParameterNode && activityNode.getOutgoings().length > 0) {	// ainda falta verificar isso
				parameterIn = true;
			}
			
			if (activityNode instanceof IActivityParameterNode && activityNode.getIncomings().length > 0) {	// ainda falta verificar isso
				parameterOut = true;
			}
		}
		
		if (parameterIn) {
			channels.append("channel startActivity_" + ad.getName() + ": ID_" + ad.getName() + ".typeIn_" + ad.getName() + "\n");	// Tipos de parametros ainda a decidir
		} else {
			channels.append("channel startActivity_" + ad.getName() + ": ID_" + ad.getName() + "\n");
		}
		
		if (parameterOut) {
			channels.append("channel endActivity_" + ad.getName() + ": ID_" + ad.getName() + ".typeIn_" + ad.getName() + "\n");	// Tipos de parametros ainda a decidir
		} else {
			channels.append("channel endActivity_" + ad.getName() + ": ID_" + ad.getName() + "\n");
		}
		
		if (countGet_ad > 1 || countSet_ad > 1) {
			channels.append("channel set_x_" + ad.getName() + ", set_y_" + ad.getName() + ": countSet_" + ad.getName() + ".typeIn_" + ad.getName() + "\n");
			channels.append("channel get_x_" + ad.getName() + ", get_y_" + ad.getName() + ": countGet_" + ad.getName() + ".typeIn_" + ad.getName() + "\n");
		}
		
		if (countCn_ad > 1) {
			channels.append("channel cn_" + ad.getName() + ": countCn_" + ad.getName() + "\n");
		}

		if (countClear_ad > 1) {
			channels.append("channel clear_" + ad.getName() + ": countClear_" + ad.getName() + "\n");
		}
		
		channels.append("channel update_" + ad.getName() + ": countUpdate_" + ad.getName() + ".limiteUpdate_" + ad.getName() + "\n");
		
		channels.append("channel endDiagram_" + ad.getName() + "\n");
		
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
		IActivityNode nodes[] =  ad.getActivityNodes();
		boolean parameter = false;
		
		if (countCall.size() > 0) {
			for (String nameDiagram : countCall.keySet()) {
				types.append("ID_" + nameDiagram + " = {1.." + countCall.get(nameDiagram) + "}\n");
			}
		}
		
		types.append("datatype T = lock | unlock\n");

		for (IActivityNode activityNode : nodes) {
			if (activityNode instanceof IActivityParameterNode) {	// ainda falta verificar isso
				parameter = true;
			}
		}
		
		if (parameter) {
			types.append("typeIn_" + ad.getName() + " = {0..1}\n");
		}
		
		if (countGet_ad > 1 || countSet_ad > 1) {
			if (countGet_ad == 1) {
				types.append("countGet_" + ad.getName() + " = {1.." + countGet_ad + "}\n");
			} else {
				types.append("countGet_" + ad.getName() + " = {1.." + (countGet_ad - 1) + "}\n");
			}
			
			if (countSet_ad == 1) {
				types.append("countSet_" + ad.getName() + " = {1.." + countSet_ad + "}\n");
			} else {
				types.append("countSet_" + ad.getName() + " = {1.." + (countSet_ad - 1) + "}\n");
			}
		}

		if (countCn_ad > 1) {
			types.append("countCn_" + ad.getName() + " = {1.." + (countCn_ad - 1) + "}\n");
		}
		
		types.append("countUpdate_" + ad.getName() + " = {1.." + (countUpdate_ad - 1) + "}\n");

		if (countClear_ad > 1) {
			types.append("countClear_" + ad.getName() + " = {1.." + (countClear_ad - 1) + "}\n");
		}
		
		types.append("limiteUpdate_" + ad.getName() + " = {(-2)..2}\n");	// valor fixo
		
		System.out.println(types);
		
		return types.toString();
	}
	
	public String defineNodesActionAndControl() {
		StringBuilder nodes = new StringBuilder();
		
		for (IActivityNode activityNode : ad.getActivityNodes()) {
			if (activityNode instanceof IControlNode) {
				if (((IControlNode) activityNode).isInitialNode()) {
					if (!queueNode.contains(activityNode)) {
						queueNode.add(activityNode);
					}
				}
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
					} else if (((IControlNode) activityNode).isDecisionMergeNode() && activityNode.getIncomings().length == 1) {
						activityNode = defineDecision(activityNode, nodes); // create decision node and set next action node
					} else if (((IControlNode) activityNode).isMergeNode() && activityNode.getOutgoings().length == 1) {
						activityNode = defineMerge(activityNode, nodes); // create merge node and set next action node
					} 
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
		alphabetAllInitial.add("endDiagram_" + ad.getName());
		
		alphabetNode.put("init", alphabetAllInitial);
		
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
			
			for (Pair<String, String> tupla : syncChannels.keySet()) {	//get amount input
				if (tupla.getValue().equals(activityNode.getName())) {
					input++;
				}
			}
			
			IFlow flows[] = activityNode.getIncomings();
			
			for (int i = 0; i < flows.length; i++) {	//get amount input
				if (flows[i].getSource() instanceof IActivityParameterNode) {
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
		
		action.append(nameAction + " = ");
		
		Pair<String, String> sync = new Pair<String, String>("", "");
		for (Pair<String, String> tupla : syncChannels.keySet()) {	//get sync channel
			if (tupla.getValue().equals(activityNode.getName())) {
				sync = tupla;
			}
		}
		
		String cnIn = syncChannels.get(sync);
		
		cn(alphabet, action, cnIn, " -> ");
		lock(alphabet, action, 0, nameAction);
		event(alphabet, nameAction, action);
		lock(alphabet, action, 1, nameAction);
		update(alphabet, action, inFlows.length, outFlows.length);
		
		for (IFlow flow : outFlows) {	//creates output channels
			String cn = createCN();
			syncChannels.put(new Pair<String, String>(activityNode.getName(), flow.getTarget().getName()), cn);
			cn(alphabet, action, cn, " -> ");
		}
		
		action.append(nameAction + "\n");
		
		action.append(nameActionTermination + " = ");
		action.append(nameAction + " /\\ " + endDiagram + "\n");

		alphabet.add("endDiagram_" + ad.getName());
		alphabetNode.put(activityNode.getName(), alphabet);
		
		IFlow flows[] = outFlows;
		activityNode = flows[0].getTarget();	//set next action or control node
		
		for (int i = 1; i < flows.length; i++) {	//puts the remaining nodes in the queue
			if (!queueNode.contains(flows[i].getTarget())) {
				queueNode.add(flows[i].getTarget());
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
		
		finalNode.append(nameFinalNode + " = ");

		ArrayList<Pair<String, String>> cnInitials = new ArrayList<>();
		for (Pair<String, String> tupla : syncChannels.keySet()) {	//get all sync channels
			if (tupla.getValue().equals(activityNode.getName())) {
				cnInitials.add(tupla);
			}
		}
		
		finalNode.append("(");
		for (int i = 0; i < cnInitials.size(); i++) {
			String cnIn = syncChannels.get(cnInitials.get(i));	//get the parallel input channels
	
			finalNode.append("(");
			
			if (i >= 0 && i < cnInitials.size() - 1) {
				cn(alphabet, finalNode, cnIn, " -> SKIP) [] ");
			} else {
				cn(alphabet, finalNode, cnIn, " -> SKIP)");
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
		
		IFlow flows[] =  outFlows;
		for (int i = 0; i <  flows.length; i++) {	//creates the parallel output channels
			String cn = createCN();
			syncChannels.put(new Pair<String, String>(activityNode.getName(), flows[i].getTarget().getName()), cn);
			
			initialNode.append("(");
			
			if (i >= 0 && i < flows.length - 1) {
				cn(alphabet, initialNode, cn, " -> SKIP) ||| ");
			} else {
				cn(alphabet, initialNode, cn, " -> SKIP)");
			}
		}
		
		initialNode.append(")\n");
		
		
//		for (IFlow flow : outFlows) {	//creates output channels
//			String cn = createCN();
//			syncChannels.put(new Pair<String, String>(activityNode.getName(), flow.getTarget().getName()), cn);
//			cn(alphabet, initialNode, cn, " -> ");
//		}
//		
//		initialNode.append("SKIP\n");

		allInitial.add(nameInitialNode);	
		for (String channel : alphabet) {
			if (!alphabetAllInitial.contains(channel)) {
				alphabetAllInitial.add(channel);
			}
		}

		activityNode = flows[0].getTarget();	//set next action or control node
		
		for (int i = 1; i < flows.length; i++) {	//puts the remaining nodes in the queue
			if (!queueNode.contains(flows[i].getTarget())) {
				queueNode.add(flows[i].getTarget());
			}
		}
		
		nodes.append(initialNode.toString());
		
		return activityNode;
	} 
	
	private IActivityNode defineCallBehavior(IActivityNode activityNode, StringBuilder nodes) {
		StringBuilder callBehavior = new StringBuilder();
		ArrayList<String> alphabet = new ArrayList<>();
		String nameCallBehavior = activityNode.getName() + "_" + ad.getName();
		String nameCallBehaviorTermination = activityNode.getName() + "_" + ad.getName() + "_t";
		String endDiagram = "END_DIAGRAM_" + ad.getName();
		IFlow outFlows[] = activityNode.getOutgoings();
		IFlow inFlows[] = activityNode.getIncomings();
		
		callBehavior.append(nameCallBehavior + " = ");
		
		Pair<String, String> sync = new Pair<String, String>("", "");
		for (Pair<String, String> tupla : syncChannels.keySet()) {	//get sync channel
			if (tupla.getValue().equals(activityNode.getName())) {
				sync = tupla;
			}
		}
		
		String cnIn = syncChannels.get(sync);
		
		cn(alphabet, callBehavior, cnIn, " -> ");
		get(alphabet, callBehavior, 'x');
		startActivity(alphabet, callBehavior);
		endActivity(alphabet, callBehavior);
		set(alphabet, callBehavior, 'y');
		update(alphabet, callBehavior, inFlows.length, outFlows.length);
		
		for (IFlow flow : outFlows) {	//creates output channels
			String cn = createCN();
			syncChannels.put(new Pair<String, String>(activityNode.getName(), flow.getTarget().getName()), cn);
			cn(alphabet, callBehavior, cn, " -> ");
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
		StringBuilder fork = new StringBuilder();
		ArrayList<String> alphabet = new ArrayList<>();
		String nameFork = activityNode.getName() + "_" + ad.getName();
		String nameForkTermination = activityNode.getName() + "_" + ad.getName() + "_t";
		String endDiagram = "END_DIAGRAM_" + ad.getName();
		IFlow outFlows[] = activityNode.getOutgoings();
		IFlow inFlows[] = activityNode.getIncomings();
		
		fork.append(nameFork + " = ");
		
		Pair<String, String> sync = new Pair<String, String>("", "");
		for (Pair<String, String> tupla : syncChannels.keySet()) {	//get sync channel
			if (tupla.getValue().equals(activityNode.getName())) {
				sync = tupla;
			}
		}
		
		String cnIn = syncChannels.get(sync);
		
		cn(alphabet, fork, cnIn, " -> ");
		update(alphabet, fork, inFlows.length, outFlows.length);
		
		fork.append("(");
		
		IFlow flows[] =  outFlows;
		for (int i = 0; i <  flows.length; i++) {	//creates the parallel output channels
			String cn = createCN();
			syncChannels.put(new Pair<String, String>(activityNode.getName(), flows[i].getTarget().getName()), cn);
			
			fork.append("(");
			
			if (i >= 0 && i < flows.length - 1) {
				cn(alphabet, fork, cn, " -> SKIP) ||| ");
			} else {
				cn(alphabet, fork, cn, " -> SKIP)");
			}
		}
		
		fork.append("); ");
		
		fork.append(nameFork + "\n");
		
		fork.append(nameForkTermination + " = ");
		fork.append(nameFork + " /\\ " + endDiagram + "\n");

		alphabet.add("endDiagram_" + ad.getName());
		alphabetNode.put(activityNode.getName(), alphabet);
		
		activityNode = flows[0].getTarget();	//set next action or control node
		
		
		for (int i = 1; i < flows.length; i++) {	//puts the remaining nodes in the queue
			if (!queueNode.contains(flows[i].getTarget())) {
				queueNode.add(flows[i].getTarget());
			}
		}
		
		nodes.append(fork.toString());
		
		return activityNode;
	}
	
	private IActivityNode defineJoin(IActivityNode activityNode, StringBuilder nodes) {
		StringBuilder join = new StringBuilder();
		ArrayList<String> alphabet = new ArrayList<>();
		String nameJoin = activityNode.getName() + "_" + ad.getName();
		String nameJoinTermination = activityNode.getName() + "_" + ad.getName() + "_t";
		String endDiagram = "END_DIAGRAM_" + ad.getName();
		IFlow outFlows[] = activityNode.getOutgoings();
		IFlow inFlows[] = activityNode.getIncomings();
		
		join.append(nameJoin + " = ");
		
		ArrayList<Pair<String, String>> cnInitials = new ArrayList<>();
		for (Pair<String, String> tupla : syncChannels.keySet()) {	//get all sync channels
			if (tupla.getValue().equals(activityNode.getName())) {
				cnInitials.add(tupla);
			}
		}
		
		join.append("(");
		for (int i = 0; i < cnInitials.size(); i++) {
			String cnIn = syncChannels.get(cnInitials.get(i));	//get the parallel input channels
	
			join.append("(");
			
			if (i >= 0 && i < cnInitials.size() - 1) {
				cn(alphabet, join, cnIn, " -> SKIP) ||| ");
			} else {
				cn(alphabet, join, cnIn, " -> SKIP)");
			}
		}
		
		join.append("); ");
		
		update(alphabet, join, inFlows.length, outFlows.length);
		
		for (IFlow flow : outFlows) {	//creates output channels
			String cn = createCN();
			syncChannels.put(new Pair<String, String>(activityNode.getName(), flow.getTarget().getName()), cn);
			cn(alphabet, join, cn, " -> ");
		}
		
		join.append(nameJoin + "\n");
		
		join.append(nameJoinTermination + " = ");
		join.append(nameJoin + " /\\ " + endDiagram + "\n");

		alphabet.add("endDiagram_" + ad.getName());
		alphabetNode.put(activityNode.getName(), alphabet);
		
		IFlow flow[] = outFlows;
		activityNode = flow[0].getTarget();	//set next action or control node
		
		nodes.append(join.toString());
		
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
		
		merge.append(nameMerge + " = ");
		
		ArrayList<Pair<String, String>> cnInitials = new ArrayList<>();
		for (Pair<String, String> tupla : syncChannels.keySet()) {		//get all sync channels
			if (tupla.getValue().equals(activityNode.getName())) {
				cnInitials.add(tupla);
			}
		}
		
		merge.append("(");
		for (int i = 0; i < cnInitials.size(); i++) {
			String cnIn = syncChannels.get(cnInitials.get(i));
	
			merge.append("(");
			
			if (i >= 0 && i < cnInitials.size() - 1) {	//creates the input channels (merge)
				cn(alphabet, merge, cnIn, " -> SKIP) [] ");
			} else {
				cn(alphabet, merge, cnIn, " -> SKIP)");
			}
		}
		
		merge.append("); ");
		
		update(alphabet, merge, inFlows.length, outFlows.length);
		
		for (IFlow flow : outFlows) {	//creates output channels
			String cn = createCN();
			syncChannels.put(new Pair<String, String>(activityNode.getName(), flow.getTarget().getName()), cn);
			cn(alphabet, merge, cn, " -> ");
		}
		
		merge.append(nameMerge + "\n");
		
		merge.append(nameMergeTermination + " = ");
		merge.append(nameMerge + " /\\ " + endDiagram + "\n");

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
		
		decision.append(nameDecision + " = ");
		
		Pair<String, String> sync = new Pair<String, String>("", "");
		for (Pair<String, String> tupla : syncChannels.keySet()) {	//get sync channel
			if (tupla.getValue().equals(activityNode.getName())) {
				sync = tupla;
			}
		}
		
		String cnIn = syncChannels.get(sync);
		
		cn(alphabet, decision, cnIn, " -> ");
		update(alphabet, decision, inFlows.length, 1);
		get(alphabet, decision, 'x');
		
		decision.append("(");
		
		IFlow flows[] =  outFlows;
		for (int i = 0; i <  flows.length; i++) {	//creates the parallel output channels
			String cn = createCN();
			syncChannels.put(new Pair<String, String>(activityNode.getName(), flows[i].getTarget().getName()), cn);
			
			decision.append(flows[i].getGuard() + " & (");
			
			if (i >= 0 && i < flows.length - 1) {
				cn(alphabet, decision, cn, " -> SKIP) [] ");
			} else {
				cn(alphabet, decision, cn, " -> SKIP)");
			}
		}
		
		decision.append("); ");
		
		decision.append(nameDecision + "\n");
		
		decision.append(nameDecisionTermination + " = ");
		decision.append(nameDecision + " /\\ " + endDiagram + "\n");

		alphabet.add("endDiagram_" + ad.getName());
		alphabetNode.put(activityNode.getName(), alphabet);
		
		activityNode = flows[0].getTarget();	//set next action or control node
		
		for (int i = 1; i < flows.length; i++) {	//puts the remaining nodes in the queue
			if (!queueNode.contains(flows[i].getTarget())) {
				queueNode.add(flows[i].getTarget());
			}
		}
		
		nodes.append(decision.toString());
		
		return activityNode;
	}
	
	private IActivityNode defineFlowFinal (IActivityNode activityNode, StringBuilder nodes) {
		StringBuilder flowFinal = new StringBuilder();
		ArrayList<String> alphabet = new ArrayList<>();
		String nameFlowFinal = activityNode.getName() + "_" + ad.getName();
		String nameFlowFinalTermination = activityNode.getName() + "_" + ad.getName() + "_t" ;
		String endDiagram = "END_DIAGRAM_" + ad.getName();
		
		flowFinal.append(nameFlowFinal + " = ");

		ArrayList<Pair<String, String>> cnInitials = new ArrayList<>();
		for (Pair<String, String> tupla : syncChannels.keySet()) {	//get all sync channels
			if (tupla.getValue().equals(activityNode.getName())) {
				cnInitials.add(tupla);
			}
		}
		
		flowFinal.append("(");
		for (int i = 0; i < cnInitials.size(); i++) {
			String cnIn = syncChannels.get(cnInitials.get(i));	//get the parallel input channels
	
			flowFinal.append("(");
			
			if (i >= 0 && i < cnInitials.size() - 1) {
				cn(alphabet, flowFinal, cnIn, " -> SKIP) [] ");
			} else {
				cn(alphabet, flowFinal, cnIn, " -> SKIP)");
			}
		}
		
		flowFinal.append("); ");
		
		update(alphabet, flowFinal, 1, 0);
		
		flowFinal.append("SKIP\n");
		
		flowFinal.append(nameFlowFinalTermination + " = ");
		flowFinal.append(nameFlowFinal + " /\\ " + endDiagram + "\n");

		alphabet.add("endDiagram_" + ad.getName() );
		alphabetNode.put(activityNode.getName(), alphabet);

		activityNode = null;	

		nodes.append(flowFinal.toString());
		
		return activityNode;
	}
	
	private String createCN() {
		return "cn_" + ad.getName() + "." + countCn_ad++;
	}
	
	private void startActivity(ArrayList<String> alphabetNode, StringBuilder action) {
		String startActivity = "startActivity_" + ad.getName();
		alphabetNode.add(startActivity);
		action.append(startActivity + "!x -> ");
	}
	
	private void endActivity(ArrayList<String> alphabetNode, StringBuilder action) {
		String endActivity = "endActivity_" + ad.getName();
		alphabetNode.add(endActivity);
		action.append(endActivity + "?y -> ");
	}
	
	private void get(ArrayList<String> alphabetNode, StringBuilder action, char c) {
		if (c == 'x') {
			String get = "get_x_" + ad.getName() + "." + countGet_ad++;
			alphabetNode.add(get);
			action.append(get +"?x -> ");
		} else {
			String get = "get_y_" + ad.getName() + "." + countGet_ad++;
			alphabetNode.add(get);
			action.append(get +"?y -> ");
		}
	}
	
	private void set(ArrayList<String> alphabetNode, StringBuilder action, char c) {
		if (c == 'x') {
			String set = "set_x_" + ad.getName() + "." + countSet_ad++;
			alphabetNode.add(set);
			action.append(set +"!x -> ");
		} else {
			String set = "set_y_" + ad.getName() + "." + countSet_ad++;
			alphabetNode.add(set);
			action.append(set +"!y -> ");
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
	
	private void cn(ArrayList<String> alphabetNode, StringBuilder action, String cn, String posCn) {
		alphabetNode.add(cn);
		action.append(cn + posCn);
	}
	
	private void update(ArrayList<String> alphabetNode, StringBuilder action, int countInFlows, int countOutFlows) {
		String update = "update_" + ad.getName() + "." + countUpdate_ad++;
		alphabetNode.add(update);
		action.append(update + "!(" + countOutFlows + "-" + countInFlows + ") -> ");
	}

	private void clear(ArrayList<String> alphabetNode, StringBuilder action) {
		String update = "clear_" + ad.getName() + "." + countClear_ad++;
		alphabetNode.add(update);
		action.append(update + " -> ");
	}
	
}
