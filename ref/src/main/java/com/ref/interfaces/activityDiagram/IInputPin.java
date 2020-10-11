package com.ref.interfaces.activityDiagram;

public interface IInputPin {

	IFlow[] getIncomings();

	String getName();

	IClass getBase();

	String getId();

}
