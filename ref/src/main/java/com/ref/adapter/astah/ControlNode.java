package com.ref.adapter.astah;

import com.ref.interfaces.activityDiagram.IControlNode;

public class ControlNode implements IControlNode{

	@Override
	public boolean isInitialNode() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFlowFinalNode() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFinalNode() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isForkNode() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isJoinNode() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDecisionMergeNode() {
		// TODO Auto-generated method stub
		return false;
	}

}
