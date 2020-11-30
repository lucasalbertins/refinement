package com.ref.interfaces.activityDiagram;

public interface IControlNode extends IActivityNode{

	boolean isInitialNode();

	boolean isFlowFinalNode();

	boolean isFinalNode();

	boolean isForkNode();

	boolean isJoinNode();

	boolean isMergeNode();
	
	boolean isDecisionNode();
	
}
