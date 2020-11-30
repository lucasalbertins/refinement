package com.ref.adapter.astah;

import java.util.ArrayList;
import java.util.List;

import com.change_vision.jude.api.inf.model.IActivityParameterNode;
import com.change_vision.jude.api.inf.model.IObjectNode;
import com.change_vision.jude.api.inf.model.IPin;
import com.ref.adapter.astah.AdapterUtils.WhiteDiamondNodeType;
import com.ref.exceptions.WellFormedException;
import com.ref.interfaces.activityDiagram.IActivityNode;
import com.ref.interfaces.activityDiagram.IClass;
import com.ref.interfaces.activityDiagram.IControlNode;
import com.ref.interfaces.activityDiagram.IObjectFlow;

public class ObjectFlow extends Flow implements IObjectFlow {
	//com.change_vision.jude.api.inf.model.IFlow flow;
	//private IActivityNode target;
	//private IActivityNode source;
	private IClass base;

	
	public ObjectFlow(com.change_vision.jude.api.inf.model.IFlow flow) throws WellFormedException {
		super(flow);
		//this.flow = flow;
		com.change_vision.jude.api.inf.model.IActivityNode aux = findBase(flow.getSource(),new ArrayList<>());
		if(aux instanceof com.change_vision.jude.api.inf.model.IPin) {
			this.base = new Class(((com.change_vision.jude.api.inf.model.IObjectNode) aux).getBase());
		}
		else if(aux instanceof com.change_vision.jude.api.inf.model.IObjectNode) {
			this.base = new Class(((com.change_vision.jude.api.inf.model.IObjectNode) aux).getBase());
		}else if(aux instanceof com.change_vision.jude.api.inf.model.IActivityParameterNode) {
			this.base = new Class(((com.change_vision.jude.api.inf.model.IObjectNode) aux).getBase());
		}else {
			//algo deu errado
			System.out.println("Deu ruim cambada!!!\n");
		}
		
	}
	@Override
	public IClass getBase() {
		return base;
	}
	
	@Override
	public String getId() {
		return flow.getId();
	}

	@Override
	public String getName() {
		return flow.getName();
	}

	@Override
	public String getDefinition() {
		return flow.getDefinition();
	}

	@Override
	public String[] getStereotypes() {
		return flow.getStereotypes();
	}

	@Override
	public IActivityNode getTarget() {
		return target;
	}

	@Override
	public IActivityNode getSource() {
		return source;
	}

	@Override
	public String getGuard() {
		return flow.getGuard();
	}

	public void setTarget(IActivityNode target) {
		this.target = target;
	}

	public void setSource(IActivityNode source) {
		this.source = source;
	}
	
	private com.change_vision.jude.api.inf.model.IActivityNode findBase(com.change_vision.jude.api.inf.model.IActivityNode base, List<String> searched) throws WellFormedException {
		//com.change_vision.jude.api.inf.model.IActivityNode base = flow.getSource();
		
		if(base instanceof com.change_vision.jude.api.inf.model.IActivityParameterNode) {
			return base;
		}
		else if( base instanceof com.change_vision.jude.api.inf.model.IObjectNode) {
			return base;
		}else if(base instanceof com.change_vision.jude.api.inf.model.IPin) {
			return base;//não é pra ter como ser Input
		}
		else if(base instanceof com.change_vision.jude.api.inf.model.IControlNode){
			return navegateForBase((com.change_vision.jude.api.inf.model.IControlNode) flow.getSource(),searched);
		}else {//se for IAction
			return null;//deu erro em algum lugar
		}
	}
	
	private com.change_vision.jude.api.inf.model.IActivityNode navegateForBase(com.change_vision.jude.api.inf.model.IControlNode base, List<String> searched) throws WellFormedException {
		if(AdapterUtils.wDNodeType(base) == WhiteDiamondNodeType.MERGE_NODE) {

			for(int i = 0; i< base.getIncomings().length; i++) {
				
				if(!searched.contains((base.getIncomings()[i].getId()))) {
					
					searched.add(base.getIncomings()[i].getId());
					if(base.getIncomings()[i].getSource() instanceof IControlNode) {
						return navegateForBase((com.change_vision.jude.api.inf.model.IControlNode) base.getIncomings()[i].getSource(),searched);
					}else {
						return findBase(base.getIncomings()[i].getSource(),searched);
					}
				}
				
			}
			
		}else if(AdapterUtils.wDNodeType(base) == WhiteDiamondNodeType.DECISION_NODE) {
			if(base.getIncomings()[0].getSource() instanceof com.change_vision.jude.api.inf.model.IControlNode) {
				return navegateForBase((com.change_vision.jude.api.inf.model.IControlNode) base.getIncomings()[0].getSource(), searched);
			}else {
				return findBase(base.getIncomings()[0].getSource(), searched);
			}
		}else if(base.isForkNode()) {
			if(base.getIncomings()[0].getSource() instanceof IControlNode) {
				return navegateForBase((com.change_vision.jude.api.inf.model.IControlNode) base.getIncomings()[0].getSource(),searched);
			}else {
				return findBase(base.getIncomings()[0].getSource(),searched);
			}			
		}else if(base.isJoinNode()) {
			List<IClass> types = new ArrayList<>();
			com.change_vision.jude.api.inf.model.IActivityNode baseNode;
			for(int i = 0; i< base.getIncomings().length; i++) {//acha os tipos dos caminhos
				if (!searched.contains(base.getIncomings()[i].getId())) {
					searched.add(base.getIncomings()[i].getId());
					if(AdapterUtils.flowType(base.getIncomings()[i], searched) == AdapterUtils.FlowType.OBJECT_FLOW) {//se o caminho resultar em object seguir ele 
						baseNode = findBase(base.getIncomings()[i].getSource(),searched);// não tenho certeza
						
						if( baseNode instanceof IObjectNode) {
							types.add(new Class(((IObjectNode) baseNode).getBase()));
						}
						else if(baseNode instanceof IActivityParameterNode) {
							types.add(new Class(((IActivityParameterNode) baseNode).getBase()));
						}else if(baseNode instanceof IPin) {
							types.add(new Class(((IPin) baseNode).getBase()));
						}
					}
				}
			}
			
			for(int i =0; i< types.size();i++) {//se algum tipo for diferente entao algo deu errado
				if(!(types.get(0).equals(types.get(i)))) {//se algum for diferente
					throw new WellFormedException("There is more then 1 type of objectFlow in the "+base.getName() +" Node. \n");
				}
			}
			
			
		}
		
		
		
		
		
		
		return null;
	}
}
