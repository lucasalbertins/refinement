package com.ref.parser.activityDiagram;
import com.change_vision.jude.api.inf.model.*;
import com.ref.interfaces.activityDiagram.IAction;
import com.ref.interfaces.activityDiagram.IActivity;
import com.ref.interfaces.activityDiagram.IActivityNode;
import java.util.*;

public  class ADAstahAlphabet {
	
    private final String partitionName;
    
   
    public class ICallBehaviorAction {
    }

	public  ADAstahAlphabet(String partitionName, String nameAccept, IActivity activity, Object outPins) {
    	 this.partitionName = partitionName;
    	 
    }
    
    
    public  Set<String> getEvents(IActivity activity) {
        Set<String> events = new LinkedHashSet<>(); 
        Set<String> alphabet = new LinkedHashSet<>();
    	 for (IActivityNode node : activity.getActivityNodes()) {
             if (node instanceof IAction) {
                 IAction action = (IAction) node;
                 String actionName = action.getName();
                 if(!((IAction) node).isAcceptTimeEventAction()) {
                	 if (actionName == null || actionName.isEmpty())  continue; {
                		 if(node instanceof ICallBehaviorAction) {
                			 ICallBehaviorAction call = (ICallBehaviorAction) node;
                			 callBeahavionAlphabet(partitionName, alphabet, activity);
                			 
                		 }else{
                			 
                			 String direction = action.isAcceptEventAction() ? ".in" : ".out";
                        	 
                			 String event = partitionName + "::" + extractParameterFromName(ADUtils.nameResolver(actionName) + direction);
                			 events.add(event);
                			 
                		 }
                		
                	 }
                }  
             }
             
             //Adiciona tock 
             events.add("tock");
            
    	}
    	return events;
   }

    
     private void callBeahavionAlphabet(String partitionName2, Set<String> alphabet, IActivity activity) {
    	 Set<String> events = new LinkedHashSet<>(); 
    	 for (IActivityNode node : activity.getActivityNodes()) {
             if (node instanceof IAction) {
                 IAction action = (IAction) node;
                 String actionName = action.getName();
                 if(!((IAction) node).isAcceptTimeEventAction()) {
                	 if (actionName == null || actionName.isEmpty())  continue; {
                		 	 String direction = action.isAcceptEventAction() ? ".in" : ".out";
                	 		 String event = partitionName + "::" + extractParameterFromName(ADUtils.nameResolver(actionName) + direction);
                			 events.add(event);
                     		
                	 }
                }  
             }
             
             //Adiciona tock 
             events.add("tock");
            
             }
		
    	 }
     


	//Extrair os parametros 
    
   private String extractParameterFromName(String actionName) {
          	int end = actionName.indexOf('.');
        	if(end != -1) {
        		return ADUtils.nameResolver(actionName.substring(0,end));
        	}
        	return ADUtils.nameResolver(actionName);
    }
   
    

    
    //Retorna o alfabeto 
   
   
    public String getAlphabetCSP(IActivity activity) {
        Set<String> events = getEvents(activity);
        return "{| " + String.join(", ", events) + " |}";
   
    }
}    	

