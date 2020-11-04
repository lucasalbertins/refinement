package com.ref.adapter.astah;

import com.ref.interfaces.activityDiagram.IControlNode;
import com.ref.interfaces.activityDiagram.IFlow;

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
		if () {
            activityNode = defineDecision(activityNode, nodes, 0); // create decision node and set next action node
        } else {
            activityNode = defineMerge(activityNode, nodes, 0); // create merge node and set next action node
        }
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IFlow[] getIncomings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFlow[] getOutgoings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDefinition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getStereotypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isDecisionNode() {
		return (activityNode.getOutgoings().length > 1 || (activityNode.getOutgoings()[0].getGuard() != null &&  !activityNode.getOutgoings()[0].getGuard().equals("")));
	}

	@Override
	public boolean isMergeNode() {
		return !(activityNode.getOutgoings().length > 1 || (activityNode.getOutgoings()[0].getGuard() != null &&  !activityNode.getOutgoings()[0].getGuard().equals("")));
	}

}
