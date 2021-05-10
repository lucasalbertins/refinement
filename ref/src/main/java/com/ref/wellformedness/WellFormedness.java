package com.ref.wellformedness;



import java.util.ArrayList;
import java.util.Arrays;
import com.ref.exceptions.WellFormedException;
import com.ref.interfaces.activityDiagram.IActivityDiagram;
import com.ref.interfaces.activityDiagram.IActivityNode;
import com.ref.interfaces.activityDiagram.IObjectNode;

public class WellFormedness {

	public static void wellFormed(IActivityDiagram act) throws WellFormedException {
	ArrayList<IActivityNode> actNodes = new ArrayList<>(Arrays.asList(act.getActivity().getActivityNodes()));
	ArrayList<IObjectNode> objNodes = new ArrayList<>();
	for(IActivityNode a: actNodes) {
		if(a instanceof IObjectNode) {
			objNodes.add((IObjectNode)a);
		}
	}
	ObjectDefinitionWellFormedness.wellFormed(act, objNodes);
	ObjectFlowWellFormedness.wellFormed(objNodes);
	CBAWellFormedness.wellFormed(actNodes);
	
	}
}

