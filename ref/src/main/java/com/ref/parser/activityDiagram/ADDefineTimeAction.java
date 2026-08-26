package com.ref.parser.activityDiagram;

import com.ref.interfaces.activityDiagram.IAction;
import com.ref.interfaces.activityDiagram.IActivity;
import com.ref.interfaces.activityDiagram.IActivityNode;
import com.ref.interfaces.activityDiagram.IOutputPin;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ADDefineTimeAction {

    
     
    private static final Pattern WAIT_PATTERN =
            Pattern.compile("^wait\\D*(\\d+)$");

    public static String wait(String partitionName, String nameAccept,
                              IActivity ad, IOutputPin[] outPins) {

        boolean hasAcceptTimeEvent = false;

        
        for (IActivityNode node : ad.getActivityNodes()) {
            if (node instanceof IAction && ((IAction) node).isAcceptTimeEventAction()) {
                hasAcceptTimeEvent = true;
                break;
            }
        }

        Matcher matcher = WAIT_PATTERN.matcher(nameAccept);

        if (matcher.find() && hasAcceptTimeEvent) {
            String t = matcher.group(1); 
            return "WAIT(" + t + "); ";
        }

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
