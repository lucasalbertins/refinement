package com.ref.interfaces.activityDiagram;


public interface IAction extends IActivityNode{

	IInputPin[] getInputs();

	IOutputPin[] getOutputs();

	IActivity getCallingActivity();

	boolean isCallBehaviorAction();

	boolean isSendSignalAction();

	boolean isAcceptEventAction();

	String getName();

}
