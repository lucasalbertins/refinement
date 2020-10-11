package com.ref.interfaces.activityDiagram;

public interface IOutputPin {

	IFlow[] getOutgoings();

	IClass getBase();

	String getName();

}
