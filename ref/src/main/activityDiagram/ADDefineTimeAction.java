package com.ref.parser.activityDiagram;

import com.ref.interfaces.activityDiagram.IAction;
import com.ref.interfaces.activityDiagram.IActivity;
import com.ref.interfaces.activityDiagram.IActivityNode;
import com.ref.interfaces.activityDiagram.IOutputPin;

	
public class ADDefineTimeAction {
	
	public static String wait(String partitionName, String nameAccept, IActivity ad, IOutputPin[] outPins) {
		String nameLoop = "";
		String nameAcceptTime = "";
		boolean timeevent = false;
		for (IActivityNode activityNode : ad.getActivityNodes()) {
			if ((activityNode instanceof IAction)) {
				if (((IAction) activityNode).isAcceptTimeEventAction()) {
					timeevent = true;
				}
			}
		}
		if ((nameAccept.substring(0,nameAccept.indexOf('t')+1).equals("wait")) && (timeevent == true)){
			nameAcceptTime = "WAIT(" + nameAccept.substring(nameAccept.indexOf('t')+5,nameAccept.length()) + "); ";
			//robo.add(nameAcceptTime);
		}else {
			nameAcceptTime = partitionName + "::" + nameAccept;
			
			for (int i = 0; i < outPins.length; i++) {
				nameLoop = "?"+outPins[i].getName();
			}
				
			nameAcceptTime = nameAcceptTime + nameLoop + " -> ";
		}
		
		return nameAcceptTime;
	}
}