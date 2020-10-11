package com.ref.interfaces.activityDiagram;

public interface IFlow {

	String getId();

	IActivityNode getTarget();

	INamedElement getSource();

	String getGuard();

	String[] getStereotypes();

}
