package com.ref.adapter.astah;

import java.util.HashMap;

import com.change_vision.jude.api.inf.model.IAction;
import com.change_vision.jude.api.inf.model.IActivityParameterNode;
import com.change_vision.jude.api.inf.model.IControlNode;
import com.change_vision.jude.api.inf.model.IObjectNode;
import com.ref.exceptions.WellFormedException;
import com.ref.interfaces.activityDiagram.IActivity;
import com.ref.interfaces.activityDiagram.IActivityDiagram;
import com.ref.interfaces.activityDiagram.IActivityNode;
import com.ref.interfaces.activityDiagram.IPin;

public class Activity implements IActivity{
	private com.change_vision.jude.api.inf.model.IActivity activity;
	private IActivityDiagram activityDiagram;
	private IActivityNode[] activityNodes;
	private HashMap<String,String> owners;
	
	public Activity(com.change_vision.jude.api.inf.model.IActivity activity) throws WellFormedException {
		super();
		this.activity = activity;
		owners = new HashMap<>();
		this.activityNodes = new IActivityNode[activity.getActivityNodes().length];
		for (int i = 0; i < activityNodes.length; i++) {
			com.change_vision.jude.api.inf.model.IActivityNode node = activity.getActivityNodes()[i];
			if(node instanceof com.change_vision.jude.api.inf.model.IAction) {
				this.activityNodes[i] = new Action((IAction) node);
				((com.ref.interfaces.activityDiagram.IAction)this.activityNodes[i]).getInputs();
			}else if(node instanceof com.change_vision.jude.api.inf.model.IControlNode) {
				this.activityNodes[i] = new ControlNode((IControlNode) node);
			}else if(node instanceof com.change_vision.jude.api.inf.model.IActivityParameterNode) {
				this.activityNodes[i] = new ActivityParameterNode((IActivityParameterNode) node);
			}else if(node instanceof com.change_vision.jude.api.inf.model.IPin) {
				com.change_vision.jude.api.inf.model.IAction owner = (IAction) node.getOwner();// vê quem é o dono do pino
				
				for(com.change_vision.jude.api.inf.model.IPin pin : owner.getInputs()) {// varre os pinos de entrada do nó
					if(pin.getId().equals(node.getId())) {// se encontrar
						this.activityNodes[i] = new InputPin((com.change_vision.jude.api.inf.model.IInputPin)node);
						owners.put(this.activityNodes[i].getId(), owner.getId());
					}
				}
				for(com.change_vision.jude.api.inf.model.IPin pin : owner.getOutputs()) {//varre os pinos de saida do nó
					if(pin.getId().equals(node.getId())) {// se encontrar
						this.activityNodes[i] = new OutputPin((com.change_vision.jude.api.inf.model.IOutputPin)node);
						owners.put(this.activityNodes[i].getId(), owner.getId());
					}
				}
				
			}else if(node instanceof com.change_vision.jude.api.inf.model.IObjectNode) {
				this.activityNodes[i] = new ObjectNode((IObjectNode) node);
			}			
		}
		
		//Varre os nos e atribui os pinos aos actions e vice versa 
		for(int i = 0; i<this.activityNodes.length;i++) {
			if(owners.containsKey(this.activityNodes[i].getId())) {//se for um pino
				for (int j = 0; j < activityNodes.length; j++) {
					if(activityNodes[j].getId().equals(owners.get(activityNodes[i].getId()))) {//procurando o dono do pino
						((IPin) activityNodes[i]).setOwner((com.ref.interfaces.activityDiagram.IAction) activityNodes[j]);//colocando o dono do pino
						if(((IPin) activityNodes[i]) instanceof InputPin) {//se for pino de entrada
							((Action) activityNodes[j]).addPin(((InputPin) activityNodes[i]));

						}else {//se for pino de saida
							((Action) activityNodes[j]).addPin(((OutputPin) activityNodes[i]));
						}
					}
				}
			}
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
