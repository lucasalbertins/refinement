package com.ref.interfaces.activityDiagram;

public interface IActivityNode {

	IFlow[] getIncomings();

	IFlow[] getOutgoings();

	String getName();

	String getId();

	String getDefinition();

}
