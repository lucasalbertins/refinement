package com.ref.parser.activityDiagram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ref.astah.adapter.ActivityNode;
import com.ref.exceptions.ParsingException;
import com.ref.interfaces.activityDiagram.IAction;
import com.ref.interfaces.activityDiagram.IActivity;
import com.ref.interfaces.activityDiagram.IActivityDiagram;
import com.ref.interfaces.activityDiagram.IActivityNode;
import com.ref.interfaces.activityDiagram.IActivityParameterNode;
import com.ref.interfaces.activityDiagram.IControlFlow;
import com.ref.interfaces.activityDiagram.IFlow;
import com.ref.interfaces.activityDiagram.IInputPin;
import com.ref.interfaces.activityDiagram.IObjectFlow;
import com.ref.interfaces.activityDiagram.IObjectNode;
import com.ref.interfaces.activityDiagram.IOutputPin;
import com.ref.interfaces.activityDiagram.IPin;
import com.sun.tools.doclets.internal.toolkit.util.DocFinder.Output;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ADDefineEvents {
	private HashMap<Pair<IActivity,String>, ArrayList<String>> namesMemoryLocal;
	//static List<String> namesMemoryLocal = new ArrayList<>();
    HashMap<String, String> typeMemoryLocal = new HashMap<>();
    
    public void AnyEvents(StringBuilder callBehaviour, String nameCallBehaviour) {
    	
    	for (int i=1; i < namesMemoryLocal.size(); i++) {
    		 if (!"*".equals(namesMemoryLocal(i))) {
    			 callBehaviour.append(namesMemoryLocal.get(i));
    		 }
    		//callBehaviour.append(namesMemoryLocal.get(i));
    		
    		//callBehaviour.append(expReplaced.get(i));
    	} 
    		
    }

	private Object namesMemoryLocal(int i) {
		// TODO Auto-generated method stub
		return null;
	}


}    
    