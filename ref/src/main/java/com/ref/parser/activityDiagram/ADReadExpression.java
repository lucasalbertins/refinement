/**
package com.ref.parser.activityDiagram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;

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
import java.util.Optional;

//import com.astah.uml.model.CallBehaviorAction;
//import com.astah.uml.model.OpaqueBehavior;


public class ADReadExpression {
	
	private static final Pattern EVS_PATTERN =
		    Pattern.compile("__+evs_+([A-Za-z0-9_]+)_*");
	
	private static final Pattern UNION_PATTERN =
		    Pattern.compile("__+evs_+([A-Za-z0-9_]+)_*");

	
	
	public void createAny(ArrayList<String> alphabet, StringBuilder callBehaviour, String nameCallBehaviour) {
		int index = ++adParser.countAny_ad;
		
		Matcher m = EVS_PATTERN.matcher(nameCallBehaviour);
		Matcher mm = UNION_PATTERN.matcher(nameCallBehaviour);
		//alphabet.add("chaos." + index);
		//callBehaviour.append(alphabet);
		//callBehaviour.append("chaos." + index + " -> SKIP;");
		
		// Declaração no inicio da class 
		//private static final Pattern CHAOS_PATTERN =
	            //Pattern.compile("^\\*(\\\\)+evs\\([A-Za-z0-9_]+\\)$");// Pattern.CASE_INSENSITIVE);
		//nameCallBehaviour = callAction.getBehavior().getName();
		
		//String expDiff = "^\\*(\\\\)+evs\\([A-Za-z0-9_]+\\)$";
		//Pattern p = Pattern.compile(REGEX);
		//Matcher matcher = CHAOS_PATTERN.matcher(nameCallBehaviour);
		//if (nameCallBehaviour.indexOf("__evs_") != -1) {
		//Matcher matcher = CHAOS_PATTERN.matcher(nameCallBehaviour);
		
		if (m.matches()) {
			callBehaviour.append("CHAOS(diff(alpha_system_" + nameDiagramResolver(ad.getName()) + ", alpha_property_" + ADUtils.nameResolver(ad.getName()) + " )); ");
			
		//}else if (nameCallBehaviour.indexOf("__union_") != -1){
		}else if(mm.matches()) {	
				//callBehaviour.append("CHAOS(union(diff(Events,{|ChemicalDetectorSoftware::light,ChemicalDetectorSoftware::gas,ChemicalDetectorSoftware::siren,ChemicalDetectorSoftware::flag|}),{tock}))");
				 callBehaviour.append("CHAOS(union(diff(alpha_system_" + nameDiagramResolver(ad.getName()) + ", alpha_property_" + ADUtils.nameResolver(ad.getName()) + " ),{tock})); ");
		}
		
		//callBehaviour.append("CHAOS(diff(alpha_system_" + nameDiagramResolver(ad.getName()) + ", alpha_property_" + ADUtils.nameResolver(ad.getName()) + " )); ");
	}
}

***/
