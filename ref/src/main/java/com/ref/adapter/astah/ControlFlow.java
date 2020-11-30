package com.ref.adapter.astah;

import com.ref.interfaces.activityDiagram.IActivityNode;
import com.ref.interfaces.activityDiagram.IControlFlow;

public class ControlFlow extends Flow implements IControlFlow{
	//com.change_vision.jude.api.inf.model.IFlow flow;
	//private IActivityNode target;
	//private IActivityNode source;
	
	public ControlFlow(com.change_vision.jude.api.inf.model.IFlow flow) {
		super(flow);
		//this.flow = flow;
	}
	
	@Override
	public IActivityNode getTarget() {
		return target;
	}

	@Override
	public IActivityNode getSource() {
		return source;
	}

	@Override
	public String getGuard() {
		return flow.getGuard();
	}
	
	@Override
	public String getId() {
		return flow.getId();
	}

	@Override
	public String getName() {
		return flow.getName();
	}

	@Override
	public String getDefinition() {
		return flow.getDefinition();
	}

	@Override
	public String[] getStereotypes() {
		return flow.getStereotypes();
	}
	public void setTarget(IActivityNode target) {
		this.target = target;
	}

	public void setSource(IActivityNode source) {
		this.source = source;
	}
}
