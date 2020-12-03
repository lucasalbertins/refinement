package com.ref.adapter.astah;

import com.ref.exceptions.WellFormedException;
import com.ref.interfaces.activityDiagram.IAction;
import com.ref.interfaces.activityDiagram.IActivity;
import com.ref.interfaces.activityDiagram.IFlow;
import com.ref.interfaces.activityDiagram.IInputPin;
import com.ref.interfaces.activityDiagram.IOutputPin;

public class Action extends ActivityNode implements IAction {

	//private com.change_vision.jude.api.inf.model.IAction action;
	//private IFlow[] incomings;
	//private IFlow[] outgoings;
	private IInputPin[] inputs;
	private IOutputPin[] outputs;
	private IActivity activity;
	
	public Action(com.change_vision.jude.api.inf.model.IAction action) throws WellFormedException {
		super(action);
		//this.action = action;
		
		this.inputs = new IInputPin[action.getInputs().length];
		for (int i = 0; i < inputs.length; i++) {//tirar daqui e botar no Activity
			this.inputs[i] = new InputPin(action.getInputs()[i]);
		}
		
		this.outputs = new IOutputPin[action.getOutputs().length];
		for (int i = 0; i < inputs.length; i++) {
			this.outputs[i] = new OutputPin(action.getOutputs()[i]);
		}
		
		if (isCallBehaviorAction()) {
			this.activity = new Activity(action.getCallingActivity());
		}
	}

	@Override
	public IFlow[] getIncomings() {	
		return this.incomings;		
	}

	@Override
	public IFlow[] getOutgoings() {
		return this.outgoings;	
	}

	@Override
	public String getId() {
		return ((com.change_vision.jude.api.inf.model.IAction) activityNode).getId();
	}

	@Override
	public String getDefinition() {
		return ((com.change_vision.jude.api.inf.model.IAction) activityNode).getDefinition();
	}

	@Override
	public IInputPin[] getInputs() {
		return this.inputs;
	}

	@Override
	public IOutputPin[] getOutputs() {
		return this.outputs;
	}

	@Override
	public IActivity getCallingActivity() {
		return this.activity;
	}

	@Override
	public boolean isCallBehaviorAction() {
		//return action.isCallBehaviorAction();
		return ((com.change_vision.jude.api.inf.model.IAction) activityNode).isCallBehaviorAction();
	}

	@Override
	public boolean isSendSignalAction() {
		//return action.isSendSignalAction();
		return ((com.change_vision.jude.api.inf.model.IAction) activityNode).isSendSignalAction();
	}

	@Override
	public boolean isAcceptEventAction() {
		//return action.isAcceptEventAction();
		return ((com.change_vision.jude.api.inf.model.IAction) activityNode).isAcceptEventAction();
	}

	@Override
	public String getName() {
		//return action.getName();
		return ((com.change_vision.jude.api.inf.model.IAction) activityNode).getName();
	}

	@Override
	public String[] getStereotypes() {
		//return action.getStereotypes();
		return ((com.change_vision.jude.api.inf.model.IAction) activityNode).getStereotypes();
	}

}
