package com.ref.parser;

import java.util.ArrayList;
import java.util.HashMap;

import com.change_vision.jude.api.inf.model.IAction;
import com.change_vision.jude.api.inf.model.IActivityDiagram;
import com.change_vision.jude.api.inf.model.IActivityNode;
import com.change_vision.jude.api.inf.model.IActivityParameterNode;
import com.change_vision.jude.api.inf.model.IControlNode;
import com.change_vision.jude.api.inf.model.IFlow;

import javafx.util.Pair;

public class ADParser {

	private IActivityDiagram ad;
	
	private int countGet_ad;
	private int countSet_ad;
	private int countCn_ad;
	private int countUpdate_ad;
	private int countClear_ad;
	private int countLock_ad;
	//limiteUpdate_ad = {(-2)..2}
	
	private static HashMap<String, Integer> countCall = new HashMap<>();
	private HashMap<String, ArrayList<String>> alphabetNode;
	private HashMap<Pair<String, String>, String> syncChannels; 
	private ArrayList<IActivityNode> queueNode;
	
	public ADParser(IActivityDiagram ad) {
		this. ad = ad;
		this.countGet_ad = 1;
		this.countSet_ad = 1;
		this.countCn_ad = 1;
		this.countUpdate_ad = 2;
		this.countClear_ad = 1;
		this.countLock_ad = 1;
		this.alphabetNode = new HashMap<>();
		addCountCall();
		syncChannels = new HashMap<>();
		queueNode = new ArrayList<>();
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
		IActivityNode nodes[] =  ad.getActivity().getActivityNodes();
		boolean parameter = false;
		int numCall = countCall.get(ad.getName());
		
		for (IActivityNode activityNode : nodes) {
			if (activityNode instanceof IActivityParameterNode) {	// ainda falta verificar isso
				parameter = true;
			}
		}
		
		if (parameter) {
			channels.append("channel startActivity_" + ad.getName() + "_" + numCall + ": typeIn_" + ad.getName() + "\n");	// Tipos de parametros ainda a decidir
		} else {
			channels.append("channel startActivity_" + ad.getName() + "_" + numCall  + "\n");
		}
		
		if (parameter) {
			channels.append("channel endActivity_" + ad.getName() + "_" + numCall  + ": typeOut_" + ad.getName() + "\n");	// Tipos de parametros ainda a decidir
		} else {
			channels.append("channel endActivity_" + ad.getName() + "_" + numCall  + "\n");
		}
		
		if (countGet_ad > 1) {
			channels.append("channel set_x_" + ad.getName() + "_" + numCall  + ", set_y_" + ad.getName() + "_" + numCall  + ": countSet_" + ad.getName() + "_" + numCall  + ".typeIn_" + ad.getName() + "_" + numCall  + "\n");
		}
		
		if (countSet_ad > 1) {
			channels.append("channel get_x_" + ad.getName() + "_" + numCall  + ", get_y_" + ad.getName() + "_" + numCall  + ": countGet_" + ad.getName() + "_" + numCall  + ".typeIn_" + ad.getName() + "_" + numCall  + "\n");
		}
		
		if (countCn_ad > 1) {
			channels.append("channel cn_" + ad.getName() + "_" + numCall  + ": countCn_" + ad.getName() + "_" + numCall  + "\n");
		}

		if (countClear_ad > 1) {
			channels.append("channel clear_" + ad.getName() + "_" + numCall  + ": countClear_" + ad.getName() + "_" + numCall  + "\n");
		}
		
		channels.append("channel update_" + ad.getName() + "_" + numCall  + ": countUpdate_" + ad.getName() + "_" + numCall  + ".limiteUpdate_" + ad.getName() + "_" + numCall  + "\n");
		
		channels.append("channel endDiagram_" + ad.getName() + "_" + numCall  + "\n");
		
		if (countLock_ad > 1) {
			channels.append("channel lock_" + ad.getName() + "_" + numCall  + ": countLock_" + ad.getName() + "_" + numCall  + ".{0,1}\n");
		}
		
		channels.append("channel get_value_" + ad.getName() + "_" + numCall  + "\n");
		
		channels.append("channel loop\n");
		
		//System.out.println(channels);
		
		return channels.toString();
	}
	
	public String defineTypes() {
		StringBuilder types = new StringBuilder();
		int numCall = countCall.get(ad.getName());
		
		if (countGet_ad > 1) {
			types.append("countGet_" + ad.getName() + "_" + numCall  + " = {1.." + --countGet_ad + "}\n");
		}
		
		if (countSet_ad > 1) {
			types.append("countSet_" + ad.getName() + "_" + numCall  + " = {1.." + --countSet_ad + "}\n");
		}
		
		if (countCn_ad > 1) {
			types.append("countCn_" + ad.getName() + "_" + numCall  + " = {1.." + --countCn_ad + "}\n");
		}
		
		types.append("countUpdate_" + ad.getName() + "_" + numCall  + " = {1.." + --countUpdate_ad + "}\n");

		if (countClear_ad > 1) {
			types.append("countClear_" + ad.getName() + "_" + numCall  + " = {1.." + --countClear_ad + "}\n");
		}
		
		types.append("limiteUpdate_" + ad.getName() + "_" + numCall  + " = {(-2)..2}\n");	// valor fixo
		
		if (countLock_ad > 1) {
			types.append("countLock_" + ad.getName() + "_" + numCall  + " = {1.." + --countLock_ad + "}\n");
		}
		
		//System.out.println(types);
		
		return types.toString();
	}
	
	public String defineNodesActionAndControl() {
		StringBuilder nodes = new StringBuilder();
		
		for (IActivityNode activityNode : ad.getActivity().getActivityNodes()) {
			if (activityNode instanceof IControlNode) {
				if (((IControlNode) activityNode).isInitialNode()) {
					queueNode.add(activityNode);
				}
			}
		}
		
		while (queueNode.size() != 0) {
			IActivityNode activityNode = queueNode.get(0);
			queueNode.remove(0);
			
			while (activityNode != null) {
				if (activityNode instanceof IAction) {
					if (((IAction) activityNode).isCallBehaviorAction()) {
						activityNode = defineCallBehavior(activityNode, nodes);
					} else {
						activityNode = defineAction(activityNode, nodes);	// create action node and set next action node
					}
				}
				
				if (activityNode instanceof IControlNode) {
					if (((IControlNode) activityNode).isFinalNode()) {
						activityNode = defineFinalNode(activityNode, nodes); // create final node and set next action node
					} else if (((IControlNode) activityNode).isInitialNode()) {
						activityNode = defineInitialNode(activityNode, nodes); // create initial node and set next action node
					} else if (((IControlNode) activityNode).isForkNode()) {
						activityNode = defineFork(activityNode, nodes); // create fork node and set next action node
					} else if (((IControlNode) activityNode).isJoinNode()) {
						activityNode = defineJoin(activityNode, nodes); // create join node and set next action node
					} else if (((IControlNode) activityNode).isMergeNode()) {
						activityNode = defineMerge(activityNode, nodes); // create merge node and set next action node
					} else if (((IControlNode) activityNode).isDecisionMergeNode()) {
						activityNode = defineDecision(activityNode, nodes); // create decision node and set next action node
					} 
				}
			}	
		}
		
		System.out.println(nodes);

		return nodes.toString();
	}
	
	private IActivityNode defineAction(IActivityNode activityNode, StringBuilder nodes) {
		StringBuilder action = new StringBuilder();
		ArrayList<String> alphabet = new ArrayList<>();
		int numCall = countCall.get(ad.getName());
		String nameAction = activityNode.getName() + "_" + ad.getName() + "_" + numCall;
		String nameActionTermination = activityNode.getName() + "_" + ad.getName() + "_t_" + numCall;
		String endDiagram = "END_DIAGRAM_" + ad.getName() + "_" + numCall;
		
		action.append(nameAction + " = ");
		
		Pair<String, String> sync = new Pair<String, String>("", "");
		for (Pair<String, String> tupla : syncChannels.keySet()) {	//get sync channel
			if (tupla.getValue().equals(activityNode.getName())) {
				sync = tupla;
			}
		}
		
		String cnIn = syncChannels.get(sync);
		
		cn(alphabet, numCall, action, cnIn, " -> ");
		lock(alphabet, numCall, action, 1);
		
		for (IFlow flow : activityNode.getOutgoings()) {	//creates output channels
			String cn = createCN(numCall);
			syncChannels.put(new Pair<String, String>(activityNode.getName(), flow.getTarget().getName()), cn);
			cn(alphabet, numCall, action, cn, " -> ");
		}

		update(alphabet, numCall, action, activityNode.getIncomings().length, activityNode.getOutgoings().length);
		lock(alphabet, numCall, action, 0);
		
		action.append(nameAction + "\n");
		
		action.append(nameActionTermination + " = ");
		action.append(nameAction + " /\\ " + endDiagram + "\n");

		alphabetNode.put(activityNode.getName(), alphabet);
		
		IFlow flow[] = activityNode.getOutgoings();
		activityNode = flow[0].getTarget();	//set next action or control node
		
		nodes.append(action.toString());
		
		return activityNode;
	}
	
	private IActivityNode defineFinalNode (IActivityNode activityNode, StringBuilder nodes) {
		StringBuilder finalNode = new StringBuilder();
		ArrayList<String> alphabet = new ArrayList<>();
		int numCall = countCall.get(ad.getName());
		String nameFinalNode = activityNode.getName() + "_" + ad.getName() + "_" + numCall;
		String nameFinalNodeTermination = activityNode.getName() + "_" + ad.getName() + "_t_" + numCall;
		String endDiagram = "END_DIAGRAM_" + ad.getName() + "_" + numCall;
		
		finalNode.append(nameFinalNode + " = ");

		
		Pair<String, String> sync = new Pair<String, String>("", "");
		for (Pair<String, String> tupla : syncChannels.keySet()) {	//get sync channel
			if (tupla.getValue().equals(activityNode.getName())) {
				sync = tupla;
			}
		}
		
		String cnIn = syncChannels.get(sync);
		
		cn(alphabet, numCall, finalNode, cnIn, " -> ");
		lock(alphabet, numCall, finalNode, 1);
		clear(alphabet, numCall, finalNode);
		lock(alphabet, numCall, finalNode, 0);
		
		finalNode.append("SKIP\n");
		
		finalNode.append(nameFinalNodeTermination + " = ");
		finalNode.append(nameFinalNode + " /\\ " + endDiagram + "\n");

		alphabetNode.put(activityNode.getName(), alphabet);

		activityNode = null;	

		nodes.append(finalNode.toString());
		
		return activityNode;
	}
	
	private IActivityNode defineInitialNode (IActivityNode activityNode, StringBuilder nodes) {
		StringBuilder initialNode = new StringBuilder();
		ArrayList<String> alphabet = new ArrayList<>();
		int numCall = countCall.get(ad.getName());
		String nameInitialNode = activityNode.getName() + "_" + ad.getName() + "_" + numCall;
		
		initialNode.append(nameInitialNode + " = ");
		
		for (IFlow flow : activityNode.getOutgoings()) {	//creates output channels
			String cn = createCN(numCall);
			syncChannels.put(new Pair<String, String>(activityNode.getName(), flow.getTarget().getName()), cn);
			cn(alphabet, numCall, initialNode, cn, " -> ");
		}
		
		initialNode.append("SKIP\n");

		alphabetNode.put(activityNode.getName(), alphabet);

		IFlow flows[] = activityNode.getOutgoings();
		activityNode = flows[0].getTarget();	//set next action or control node
		
		for (int i = 1; i < flows.length; i++) {	//puts the remaining nodes in the queue
			queueNode.add(flows[i].getTarget());
		}
		
		nodes.append(initialNode.toString());
		
		return activityNode;
	} 
	
	private IActivityNode defineCallBehavior(IActivityNode activityNode, StringBuilder nodes) {
		StringBuilder callBehavior = new StringBuilder();
		ArrayList<String> alphabet = new ArrayList<>();
		int numCall = countCall.get(ad.getName());
		String nameCallBehavior = activityNode.getName() + "_" + ad.getName() + "_" + numCall;
		String nameCallBehaviorTermination = activityNode.getName() + "_" + ad.getName() + "_t_" + numCall;
		String endDiagram = "END_DIAGRAM_" + ad.getName() + "_" + numCall;
		
		callBehavior.append(nameCallBehavior + " = ");
		
		Pair<String, String> sync = new Pair<String, String>("", "");
		for (Pair<String, String> tupla : syncChannels.keySet()) {	//get sync channel
			if (tupla.getValue().equals(activityNode.getName())) {
				sync = tupla;
			}
		}
		
		String cnIn = syncChannels.get(sync);
		
		cn(alphabet, numCall, callBehavior, cnIn, " -> ");
		lock(alphabet, numCall, callBehavior, 1);
		get(alphabet, numCall, callBehavior, 'x');
		startActivity(alphabet, numCall, callBehavior);
		endActivity(alphabet, numCall, callBehavior);
		set(alphabet, numCall, callBehavior, 'y');
		
		for (IFlow flow : activityNode.getOutgoings()) {	//creates output channels
			String cn = createCN(numCall);
			syncChannels.put(new Pair<String, String>(activityNode.getName(), flow.getTarget().getName()), cn);
			cn(alphabet, numCall, callBehavior, cn, " -> ");
		}
		
		update(alphabet, numCall, callBehavior, activityNode.getIncomings().length, activityNode.getOutgoings().length);
		lock(alphabet, numCall, callBehavior, 0);
		
		callBehavior.append(nameCallBehavior + "\n");
		
		callBehavior.append(nameCallBehaviorTermination + " = ");
		callBehavior.append(nameCallBehavior + " /\\ " + endDiagram + "\n");

		alphabetNode.put(activityNode.getName(), alphabet);
		
		IFlow flow[] = activityNode.getOutgoings();
		activityNode = flow[0].getTarget();	//set next action or control node
		
		nodes.append(callBehavior.toString());
		
		//### Ainda falta a chamada recursiva para criar o diagrama ###
		
		return activityNode;
	}
	
	private IActivityNode defineFork(IActivityNode activityNode, StringBuilder nodes) {
		StringBuilder fork = new StringBuilder();
		ArrayList<String> alphabet = new ArrayList<>();
		int numCall = countCall.get(ad.getName());
		String nameFork = activityNode.getName() + "_" + ad.getName() + "_" + numCall;
		String nameForkTermination = activityNode.getName() + "_" + ad.getName() + "_t_" + numCall;
		String endDiagram = "END_DIAGRAM_" + ad.getName() + "_" + numCall;
		
		fork.append(nameFork + " = ");
		
		Pair<String, String> sync = new Pair<String, String>("", "");
		for (Pair<String, String> tupla : syncChannels.keySet()) {	//get sync channel
			if (tupla.getValue().equals(activityNode.getName())) {
				sync = tupla;
			}
		}
		
		String cnIn = syncChannels.get(sync);
		
		cn(alphabet, numCall, fork, cnIn, " -> ");
		lock(alphabet, numCall, fork, 1);
		
		fork.append("(");
		
		IFlow flows[] =  activityNode.getOutgoings();
		for (int i = 0; i <  flows.length; i++) {	//creates the parallel output channels
			String cn = createCN(numCall);
			syncChannels.put(new Pair<String, String>(activityNode.getName(), flows[i].getTarget().getName()), cn);
			
			if (i > 0 && i < flows.length - 1) {
				cn(alphabet, numCall, fork, cn, " -> SKIP ||| ");
			} else if (i == 0 && flows.length > 1) {
				cn(alphabet, numCall, fork, cn, " -> SKIP ");
			} else {
				cn(alphabet, numCall, fork, cn, " -> SKIP");
			}
		}
		
		fork.append("); ");
		
		update(alphabet, numCall, fork, activityNode.getIncomings().length, flows.length);
		lock(alphabet, numCall, fork, 0);
		
		fork.append(nameFork + "\n");
		
		fork.append(nameForkTermination + " = ");
		fork.append(nameFork + " /\\ " + endDiagram + "\n");

		alphabetNode.put(activityNode.getName(), alphabet);
		
		activityNode = flows[0].getTarget();	//set next action or control node
		
		for (int i = 1; i < flows.length; i++) {	//puts the remaining nodes in the queue
			queueNode.add(flows[i].getTarget());
		}
		
		nodes.append(fork.toString());
		
		return activityNode;
	}
	
	private IActivityNode defineJoin(IActivityNode activityNode, StringBuilder nodes) {
		StringBuilder join = new StringBuilder();
		ArrayList<String> alphabet = new ArrayList<>();
		int numCall = countCall.get(ad.getName());
		String nameJoin = activityNode.getName() + "_" + ad.getName() + "_" + numCall;
		String nameJoinTermination = activityNode.getName() + "_" + ad.getName() + "_t_" + numCall;
		String endDiagram = "END_DIAGRAM_" + ad.getName() + "_" + numCall;
		
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
	
			if (i > 0 && i < activityNode.getOutgoings().length - 1) {
				cn(alphabet, numCall, join, cnIn, " -> SKIP ||| ");
			} else if (i == 0 && activityNode.getOutgoings().length > 1) {
				cn(alphabet, numCall, join, cnIn, " -> SKIP ");
			} else {
				cn(alphabet, numCall, join, cnIn, " -> SKIP");
			}
		}
		
		join.append("); ");
		
		lock(alphabet, numCall, join, 1);
		
		for (IFlow flow : activityNode.getOutgoings()) {	//creates output channels
			String cn = createCN(numCall);
			syncChannels.put(new Pair<String, String>(activityNode.getName(), flow.getTarget().getName()), cn);
			cn(alphabet, numCall, join, cn, " -> ");
		}
		
		update(alphabet, numCall, join, activityNode.getIncomings().length, activityNode.getOutgoings().length);
		lock(alphabet, numCall, join, 0);
		
		join.append(nameJoin + "\n");
		
		join.append(nameJoinTermination + " = ");
		join.append(nameJoin + " /\\ " + endDiagram + "\n");

		alphabetNode.put(activityNode.getName(), alphabet);
		
		IFlow flow[] = activityNode.getOutgoings();
		activityNode = flow[0].getTarget();	//set next action or control node
		
		nodes.append(join.toString());
		
		return activityNode;
	}
	
	private IActivityNode defineMerge(IActivityNode activityNode, StringBuilder nodes) {
		StringBuilder merge = new StringBuilder();
		ArrayList<String> alphabet = new ArrayList<>();
		int numCall = countCall.get(ad.getName());
		String nameMerge = activityNode.getName() + "_" + ad.getName() + "_" + numCall;
		String nameMergeTermination = activityNode.getName() + "_" + ad.getName() + "_t_" + numCall;
		String endDiagram = "END_DIAGRAM_" + ad.getName() + "_" + numCall;
		
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
	
			if (i > 0 && i < activityNode.getOutgoings().length - 1) {	//creates the input channels (merge)
				cn(alphabet, numCall, merge, cnIn, " -> SKIP [] ");
			} else if (i == 0 && activityNode.getOutgoings().length > 1) {
				cn(alphabet, numCall, merge, cnIn, " -> SKIP ");
			} else {
				cn(alphabet, numCall, merge, cnIn, " -> SKIP");
			}
		}
		
		merge.append("); ");
		
		lock(alphabet, numCall, merge, 1);
		
		for (IFlow flow : activityNode.getOutgoings()) {	//creates output channels
			String cn = createCN(numCall);
			syncChannels.put(new Pair<String, String>(activityNode.getName(), flow.getTarget().getName()), cn);
			cn(alphabet, numCall, merge, cn, " -> ");
		}
		
		update(alphabet, numCall, merge, activityNode.getIncomings().length, activityNode.getOutgoings().length);
		lock(alphabet, numCall, merge, 0);
		
		merge.append(nameMerge + "\n");
		
		merge.append(nameMergeTermination + " = ");
		merge.append(nameMerge + " /\\ " + endDiagram + "\n");

		alphabetNode.put(activityNode.getName(), alphabet);
		
		IFlow flow[] = activityNode.getOutgoings();
		activityNode = flow[0].getTarget();	//set next action or control node
		
		nodes.append(merge.toString());
		
		return activityNode;
	}
	
	private IActivityNode defineDecision(IActivityNode activityNode, StringBuilder nodes) {
		StringBuilder decision = new StringBuilder();
		ArrayList<String> alphabet = new ArrayList<>();
		int numCall = countCall.get(ad.getName());
		String nameDecision = activityNode.getName() + "_" + ad.getName() + "_" + numCall;
		String nameDecisionTermination = activityNode.getName() + "_" + ad.getName() + "_t_" + numCall;
		String endDiagram = "END_DIAGRAM_" + ad.getName() + "_" + numCall;
		
		decision.append(nameDecision + " = ");
		
		Pair<String, String> sync = new Pair<String, String>("", "");
		for (Pair<String, String> tupla : syncChannels.keySet()) {	//get sync channel
			if (tupla.getValue().equals(activityNode.getName())) {
				sync = tupla;
			}
		}
		
		String cnIn = syncChannels.get(sync);
		
		cn(alphabet, numCall, decision, cnIn, " -> ");
		lock(alphabet, numCall, decision, 1);
		
		decision.append("(");
		
		IFlow flows[] =  activityNode.getOutgoings();
		for (int i = 0; i <  flows.length; i++) {	//creates the parallel output channels
			String cn = createCN(numCall);
			syncChannels.put(new Pair<String, String>(activityNode.getName(), flows[i].getTarget().getName()), cn);
			
			decision.append(flows[i].getGuard() + " & ");
			
			if (i > 0 && i < flows.length - 1) {
				cn(alphabet, numCall, decision, cn, " -> SKIP [] ");
			} else if (i == 0 && flows.length > 1) {
				cn(alphabet, numCall, decision, cn, " -> SKIP ");
			} else {
				cn(alphabet, numCall, decision, cn, " -> SKIP");
			}
		}
		
		decision.append("); ");
		
		update(alphabet, numCall, decision, activityNode.getIncomings().length, flows.length);
		lock(alphabet, numCall, decision, 0);
		
		decision.append(nameDecision + "\n");
		
		decision.append(nameDecisionTermination + " = ");
		decision.append(nameDecision + " /\\ " + endDiagram + "\n");

		alphabetNode.put(activityNode.getName(), alphabet);
		
		activityNode = flows[0].getTarget();	//set next action or control node
		
		for (int i = 1; i < flows.length; i++) {	//puts the remaining nodes in the queue
			queueNode.add(flows[i].getTarget());
		}
		
		nodes.append(decision.toString());
		
		return activityNode;
	}
	
	private String createCN(int numCall) {
		return "cn_" + ad.getName() + "_" + numCall + "." + countCn_ad++;
	}
	
	private void startActivity(ArrayList<String> alphabetNode, int numCall, StringBuilder action) {
		String startActivity = "startActivity_" + ad.getName() + "_" + numCall;
		alphabetNode.add(startActivity);
		action.append(startActivity + "!x -> ");
	}
	
	private void endActivity(ArrayList<String> alphabetNode, int numCall, StringBuilder action) {
		String endActivity = "endActivity_" + ad.getName() + "_" + numCall;
		alphabetNode.add(endActivity);
		action.append(endActivity + "?y -> ");
	}
	
	private void get(ArrayList<String> alphabetNode, int numCall, StringBuilder action, char c) {
		if (c == 'x') {
			String get = "get_x_" + ad.getName() + "_" + numCall + "." + countGet_ad++;
			alphabetNode.add(get);
			action.append(get +"?x -> ");
		} else {
			String get = "get_y_" + ad.getName() + "_" + numCall + "." + countGet_ad++;
			alphabetNode.add(get);
			action.append(get +"?y -> ");
		}
	}
	
	private void set(ArrayList<String> alphabetNode, int numCall, StringBuilder action, char c) {
		if (c == 'x') {
			String set = "set_x_" + ad.getName() + "_" + numCall + "." + countSet_ad++;
			alphabetNode.add(set);
			action.append(set +"!x -> ");
		} else {
			String set = "set_y_" + ad.getName() + "_" + numCall + "." + countSet_ad++;
			alphabetNode.add(set);
			action.append(set +"!y -> ");
		}
	}
	
	private void lock(ArrayList<String> alphabetNode, int numCall, StringBuilder action, int inOut) {
		if (inOut == 0) {
			String lock = "lock_" + ad.getName() + "_" + numCall + "." + countLock_ad++;
			alphabetNode.add(lock);
			action.append(lock + ".0 -> ");
		} else {
			String lock = "lock_" + ad.getName() + "_" + numCall + "." + countLock_ad;
			alphabetNode.add(lock);
			action.append(lock + ".1 -> ");
		}
	}
	
	private void cn(ArrayList<String> alphabetNode, int numCall, StringBuilder action, String cn, String posCn) {
		alphabetNode.add(cn);
		action.append(cn + posCn);
	}
	
	private void update(ArrayList<String> alphabetNode, int numCall, StringBuilder action, int countInFlows, int countOutFlows) {
		String update = "update_" + ad.getName() + "_" + numCall + "." + countUpdate_ad++;
		alphabetNode.add(update);
		action.append(update + "!(" + countOutFlows + "-" + countInFlows + ") -> ");
	}

	private void clear(ArrayList<String> alphabetNode, int numCall, StringBuilder action) {
		String update = "clear_" + ad.getName() + "_" + numCall + "." + countClear_ad++;
		alphabetNode.add(update);
		action.append(update + " -> ");
	}
	
}
