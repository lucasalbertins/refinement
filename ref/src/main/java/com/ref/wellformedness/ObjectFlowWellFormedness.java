package com.ref.wellformedness;

import java.util.ArrayList;

import com.ref.exceptions.WellFormedException;
import com.ref.interfaces.activityDiagram.IControlNode;
import com.ref.interfaces.activityDiagram.IFlow;
import com.ref.interfaces.activityDiagram.IObjectFlow;
import com.ref.interfaces.activityDiagram.IObjectNode;

public class ObjectFlowWellFormedness {
	private ObjectFlowWellFormedness() {
	}
	public static void wellFormed(ArrayList<IObjectNode> objNodes) throws WellFormedException {
		for(IObjectNode objN:objNodes) {
			
			for(IFlow flow: objN.getIncomings()) {//checando todos os flows que estão apontando para um object node
				
				if(flow instanceof IObjectFlow) {
					
					if(!(flow.getSource() instanceof IObjectNode)) {//caso esse flow seja de objeto mas sua fonte não
						
						if(flow.getSource() instanceof IControlNode) {//checando se o tipo do object flow é o mesmo do destino, no caso de um control node na fonte
							if(!((IObjectFlow)flow).getBase().getName().equals(((IObjectNode)flow.getTarget()).getBase().getName())) {
								throw new WellFormedException("The Object Flow data type does not matches the target's"+flow.getTarget().getName()+" datatype");
							}	
						}
						//caso a fonte do flow não seja nem objeto e nem um control node valido, a fonte será inválida
						else throw new WellFormedException("The source node("+flow.getSource().getName()+") can't have outgoing Object flow");
					}
					//checando se os tipos da fonte e destino do oobject flow são iguais	
					else if(!((IObjectNode)flow.getSource()).getBase().getName().equals(((IObjectNode)flow.getTarget()).getBase().getName())) {
						throw new WellFormedException("The source node "+flow.getSource().getName()+" and the target "+flow.getTarget().getName()+" have conflicting data types");
					}
				}
			}	
		}
	}
}

