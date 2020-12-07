package com.ref.adapter.astah;

import com.ref.exceptions.WellFormedException;
import com.ref.interfaces.activityDiagram.IAction;
import com.ref.interfaces.activityDiagram.IClass;
import com.ref.interfaces.activityDiagram.IFlow;
import com.ref.interfaces.activityDiagram.IInputPin;

public class InputPin extends Pin implements IInputPin{

	//com.change_vision.jude.api.inf.model.IInputPin inputPin;	
	//private IFlow[] incomings;
	//private IFlow[] outgoings;
	//private IClass base;
	//private IAction owner;
	
	public InputPin(com.change_vision.jude.api.inf.model.IInputPin pin) throws WellFormedException {
		super(pin);
		//this.inputPin = pin;
		this.base = new Class(pin.getBase());
	}

	@Override
	public IClass getBase() {
		return this.base;
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
		return ((com.change_vision.jude.api.inf.model.IInputPin)activityNode).getId();
	}

	@Override
	public String getName() {
		return ((com.change_vision.jude.api.inf.model.IInputPin)activityNode).getName();
	}

	@Override
	public String getDefinition() {
		return ((com.change_vision.jude.api.inf.model.IInputPin)activityNode).getDefinition();
	}

	@Override
	public String[] getStereotypes() {
		return ((com.change_vision.jude.api.inf.model.IInputPin)activityNode).getStereotypes();
	}

	@Override
	public IAction getOwner() {
		return this.owner;
	}


	
}
