package com.ref.adapter.astah;

import com.ref.exceptions.WellFormedException;
import com.ref.interfaces.activityDiagram.IAction;
import com.ref.interfaces.activityDiagram.IClass;
import com.ref.interfaces.activityDiagram.IFlow;
import com.ref.interfaces.activityDiagram.IOutputPin;

public class OutputPin extends Pin implements IOutputPin{

	//com.change_vision.jude.api.inf.model.IOutputPin outputPin;
	//private IFlow[] incomings;
	//private IFlow[] outgoings;
	//private IClass base;
	//private IAction owner;

	public OutputPin(com.change_vision.jude.api.inf.model.IOutputPin outputPin) throws WellFormedException {
		super(outputPin);
		//this.outputPin = outputPin;
		this.base = new Class(outputPin.getBase());
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
		return ((com.change_vision.jude.api.inf.model.IOutputPin)activityNode).getId();
	}

	@Override
	public String getName() {
		return ((com.change_vision.jude.api.inf.model.IOutputPin)activityNode).getName();
	}

	@Override
	public String getDefinition() {
		return ((com.change_vision.jude.api.inf.model.IOutputPin)activityNode).getDefinition();
	}

	@Override
	public String[] getStereotypes() {
		return ((com.change_vision.jude.api.inf.model.IOutputPin)activityNode).getStereotypes();
	}

	@Override
	public IAction getOwner() {
		return this.owner;
	}
	
	@Override
	public void setOwner(com.change_vision.jude.api.inf.model.IAction owner) throws WellFormedException {
		this.owner = new Action(owner);
	}

}
