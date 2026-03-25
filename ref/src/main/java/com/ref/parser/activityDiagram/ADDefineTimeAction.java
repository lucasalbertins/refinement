/*
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

*/
package com.ref.parser.activityDiagram;

import com.ref.interfaces.activityDiagram.IAction;
import com.ref.interfaces.activityDiagram.IActivity;
import com.ref.interfaces.activityDiagram.IActivityNode;
import com.ref.interfaces.activityDiagram.IOutputPin;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ADDefineTimeAction {

    // REGEX PARA IDENTIFICAR "wait(t)"
     
    private static final Pattern WAIT_PATTERN =
            Pattern.compile("^wait\\D*(\\d+)$");// Pattern.CASE_INSENSITIVE);
    		//Pattern.compile("^wait\\((0|[1-9]\\d*)\\)$");
	//private static final Pattern WAIT_PATTERN =
		    //Pattern.compile("^wait\\((0|[1-9][0-9]*)\\)$");//, Pattern.CASE_INSENSITIVE);

    public static String wait(String partitionName, String nameAccept,
                              IActivity ad, IOutputPin[] outPins) {

        boolean hasAcceptTimeEvent = false;

        // Verifica se existe AcceptTimeEventAction no Activity Diagram
        for (IActivityNode node : ad.getActivityNodes()) {
            if (node instanceof IAction && ((IAction) node).isAcceptTimeEventAction()) {
                hasAcceptTimeEvent = true;
                break;
            }
        }

        // Tenta encontrar padrão wait(t)
        Matcher matcher = WAIT_PATTERN.matcher(nameAccept);

        // Se for AcceptTimeEventAction + reconhecido como wait → gerar WAIT(t);
        if (matcher.find() && hasAcceptTimeEvent) {
            String t = matcher.group(1); // captura o número t
            return "WAIT(" + t + "); ";
        }

        // Caso contrário → gerar sintaxe padrão CSP para chamadas
        StringBuilder sb = new StringBuilder();

        sb.append(partitionName)
          .append("::")
          .append(nameAccept);

        if (outPins != null) {
            for (IOutputPin pin : outPins) {
                sb.append("?").append(pin.getName());
            }
        }

        sb.append(" -> ");
        return sb.toString();
    }
}
