package com.ref.adapter.astah;

import com.change_vision.jude.api.inf.model.IAction;
import com.change_vision.jude.api.inf.model.IActivityParameterNode;
import com.change_vision.jude.api.inf.model.IControlNode;
import com.change_vision.jude.api.inf.model.IObjectNode;
import com.ref.exceptions.WellFormedException;
import com.ref.interfaces.activityDiagram.IActivity;
import com.ref.interfaces.activityDiagram.IActivityDiagram;
import com.ref.interfaces.activityDiagram.IActivityNode;

public class Activity implements IActivity{
	private com.change_vision.jude.api.inf.model.IActivity activity;
	private IActivityDiagram activityDiagram;
	private IActivityNode[] activityNodes;
	
	public Activity(com.change_vision.jude.api.inf.model.IActivity activity) throws WellFormedException {
		super();
		this.activity = activity;
		this.activityNodes = new IActivityNode[activity.getActivityNodes().length];
		for (int i = 0; i < activityNodes.length; i++) {
			com.change_vision.jude.api.inf.model.IActivityNode node = activity.getActivityNodes()[i];
			//trocar if not pino por else if pino
			if(!(node instanceof com.change_vision.jude.api.inf.model.IPin)) {	
				if(node instanceof com.change_vision.jude.api.inf.model.IAction) {
					this.activityNodes[i] = new Action((IAction) node);
					((com.ref.interfaces.activityDiagram.IAction)this.activityNodes[i]).getInputs();
				}else if(node instanceof com.change_vision.jude.api.inf.model.IControlNode) {
					this.activityNodes[i] = new ControlNode((IControlNode) node);
				}else if(node instanceof com.change_vision.jude.api.inf.model.IActivityParameterNode) {
					this.activityNodes[i] = new ActivityParameterNode((IActivityParameterNode) node);
				}else if(node instanceof com.change_vision.jude.api.inf.model.IObjectNode) {
					this.activityNodes[i] = new ObjectNode((IObjectNode) node);
				}
			}

			//Fazer for pra varrer os nos e atribuir os pinos aos actions 
			
			
			
			//this.activityNodes[i] = new ActivityNode(activity.getActivityNodes()[i]);
		}
	}

	public void setActivityDiagram(IActivityDiagram activityDiagram) {
		this.activityDiagram = activityDiagram;
	}

	@Override
	public String getId() {
		return activity.getId();
	}

	@Override
	public String getName() {
		return activity.getName();
	}

	@Override
	public String getDefinition() {
		return activity.getDefinition();
	}

	@Override
	public String[] getStereotypes() {
		return activity.getStereotypes();
	}

	@Override
	public IActivityDiagram getActivityDiagram() {
		return this.activityDiagram;
	}

	@Override
	public IActivityNode[] getActivityNodes() {
		return this.activityNodes;
	}

	@Override
	public void setName(String nameAD){
		try {
			this.activity.setName(nameAD);
		} catch (com.change_vision.jude.api.inf.exception.InvalidEditingException e) {
			// TODO ver essa parte
			e.printStackTrace();
		}
	}
	

}
