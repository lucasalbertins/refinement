package com.ref.wellformedness;

import java.util.ArrayList;

import com.ref.exceptions.WellFormedException;
import com.ref.interfaces.activityDiagram.IAction;
import com.ref.interfaces.activityDiagram.IActivityNode;

public class CBAWellFormedness {
	private CBAWellFormedness() {
	}

	public static void wellFormed(ArrayList<IActivityNode> actNodes) throws WellFormedException {
		for (IActivityNode actCba : actNodes) {// verificando os activity nodes em busca de um call behaviour action
			if (actCba instanceof IAction) {
				// caso o node seja um CBA mas não referencie outro diagrama
				if (((IAction) actCba).isCallBehaviorAction() && ((IAction) actCba).getCallingActivity() == null) {
					throw new WellFormedException("There's no Activity related to the Call Behaviour Action");
				}
			}
		}
	}

}
