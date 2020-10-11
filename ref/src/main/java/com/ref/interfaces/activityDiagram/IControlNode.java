package com.ref.interfaces.activityDiagram;

public interface IControlNode {

	boolean isInitialNode();

	boolean isFlowFinalNode();

	boolean isFinalNode();

	boolean isForkNode();

	boolean isJoinNode();

	boolean isDecisionMergeNode();//TODO separar em isDecision e isMerge


}
