package com.ref.adapter.astah;

import com.ref.interfaces.activityDiagram.IActivityParameterNode;
import com.ref.interfaces.activityDiagram.IClass;

public class ActivityParameterNode implements IActivityParameterNode{
	com.change_vision.jude.api.inf.model.IActivityParameterNode action;	
	
	public ActivityParameterNode(com.change_vision.jude.api.inf.model.IActivityParameterNode action) {
		this.action = action;
	}


	@Override
	public IClass getBase() {
		IClass c = new Class(action.getBase());
		return c;
	}

}
