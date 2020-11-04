package com.ref.interfaces.activityDiagram;

public interface IFlow extends INamedElement{	

	IActivityNode getTarget();

	IActivityNode getSource();

	String getGuard();
	
}
