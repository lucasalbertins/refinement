package com.ref.interfaces.activityDiagram;

public interface INamedElement {

	IFlow[] getIncomings();

	IAction getOwner();

	String getId();

	String getName();

}
