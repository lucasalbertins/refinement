package com.ref.traceability;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.BasicModelEditor;
import com.change_vision.jude.api.inf.editor.ModelEditorFactory;
import com.change_vision.jude.api.inf.editor.SequenceDiagramEditor;
import com.change_vision.jude.api.inf.editor.TransactionManager;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.ILifeline;
import com.change_vision.jude.api.inf.model.IMessage;
import com.change_vision.jude.api.inf.model.IModel;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IOperation;
import com.change_vision.jude.api.inf.model.ISequenceDiagram;
import com.change_vision.jude.api.inf.presentation.ILinkPresentation;
import com.change_vision.jude.api.inf.presentation.INodePresentation;
import com.change_vision.jude.api.inf.presentation.PresentationPropertyConstants;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;
import com.ref.interfaces.activityDiagram.IActivity;
import com.ref.parser.activityDiagram.ADUtils;

public class CounterexampleDescriptor {
	
    private Map<String, String> lifelinesMap;
    private List<String> lifelineBases;
    private List<String> rawEvents;
    private String swimmingLine;
    
    /*
        The counterExampleDescriptor needs a map of all lifelines contained in the current project.
        The map entries have the format < lf(int)id, (lf base)_(lf instance) >
        and are defined in the lifelines3 attribute of SDParser class
     */
    public CounterexampleDescriptor(Map<String, String> lifelinesMap) {
        this.lifelinesMap = lifelinesMap;
        this.lifelineBases = new ArrayList<>(lifelinesMap.values());
    }
    
    public CounterexampleDescriptor(List<String> lifelineBases) throws Exception {
    	try {
    		ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
			buildCounterExample("Test_SD", lifelineBases, projectAccessor);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
    }
    /*
        This is the method that creates a counterexample sequence diagram in Astah.
        It receives a list of events as parameter, this list is created by the FdrWrapper
        class as a result of the method()
     */
    public void buildCounterExample(String name, List<String> entrada, ProjectAccessor projectAccessor)
            throws Exception {
//        this.rawEvents = preProcess(entrada);
        this.rawEvents = new ArrayList<>(preProcess(entrada));

        try {
            TransactionManager.beginTransaction();
            createSequenceDiagram(rawEvents, projectAccessor);
            TransactionManager.endTransaction();
            projectAccessor.save();

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } finally {
            TransactionManager.abortTransaction();
            // projectAccessor.close();
        }
    }
    /*
        This method removes events from the counterExample event list. It removes events that are not used in
        the counter Example SD, such as beginInteraction and endInteraction
     */
    private List<String> preProcess(List<String> entrada) {
    	List<String> result = new ArrayList<String>(); 
    	String[] split = entrada.get(0).split("::");
    	this.swimmingLine = split[0];
    	for (int i = 0; i < entrada.size(); i++) {
    		String newtrace = entrada.get(i).replace(this.swimmingLine + "::", "").replace("Call", "");
			result.add(newtrace);
    	}
    	return result;
    }

    private void createSequenceDiagram(List<String> events, ProjectAccessor projectAccessor) throws Exception {
        IModel project = projectAccessor.getProject();
        // create sequence diagram
        SequenceDiagramEditor de = projectAccessor.getDiagramEditorFactory().getSequenceDiagramEditor();
        // create diagram name
        ISequenceDiagram newDgm = de.createSequenceDiagram(project,"counterExample_" + swimmingLine + "_" + LocalDateTime.now());
        // Creates the lifelines and position them properly in the sequence diagram
        List<INodePresentation> myLifelines = CreateLifelines(project, de);
        // create messages, combinedFragment, interactionUse, stateInvariant
        CreateMessages(events, de, myLifelines);        
    }
    
    private List<INodePresentation> CreateLifelines(IModel project, SequenceDiagramEditor de) throws InvalidEditingException, ClassNotFoundException, ProjectNotFoundException {
    	List<INodePresentation> myLifelines = new ArrayList<INodePresentation>();
    	double position = 100;
    	INodePresentation objPs1 = de.createLifeline("", position);
    	ILifeline lifeline = (ILifeline) objPs1.getModel();
    	
    	// create class actor and set do ILifeline(lifeline)
    	BasicModelEditor bme = ModelEditorFactory.getBasicModelEditor();
    	IClass actor;
    	try {
    		actor = bme.createClass(project, "Actor0");    	
    	} catch (InvalidEditingException e) {
    		e.getMessage();
    	}
    	finally {
    		actor = findNamedElement(project.getOwnedElements(), "Actor0", IClass.class);
    	}
    	actor.addStereotype("actor");
    	lifeline.setBase(actor);
    	
    	myLifelines.add(objPs1);
    	position += 300;
    	
    	INodePresentation objPs2 = de.createLifeline(swimmingLine, position);
//    	objPs2.setProperty("fill.color", "#ADD8E6");
    	lifeline = (ILifeline) objPs2.getModel();
    	myLifelines.add(objPs2);
    	
    	return myLifelines;
    }
    
    private void CreateMessages(List<String> events, SequenceDiagramEditor de, List<INodePresentation> myLifelines) throws InvalidEditingException {
        List<ILinkPresentation> msgs = new ArrayList<ILinkPresentation>();
        int msgPosition = 160;
        for (int i = 0; i < events.size(); i++) {
				msgPosition =  BuildMessage(msgPosition, events, de, myLifelines, msgs, i);
        }
    }

    private int BuildMessage(int msgPosition, List<String> events, SequenceDiagramEditor de, List<INodePresentation> myLifelines, 
    	List<ILinkPresentation> msgs, int i) throws InvalidEditingException {
    	ILinkPresentation msg = null;
    	IMessage m = null;
    	String[] split = events.get(i).split("\\.");
    	String ev = split[0].toString();
    	String args = "";
    	String arg1 = split[split.length-1].toString();
    	String arg2 = split[split.length-2].toString();
    	if (!(arg1.equals("in")) && !(arg1.equals("out"))) {
			args = arg1;
			if (!(arg2.equals("in")) && !(arg2.equals("out")) && !(arg2.equals(ev))) {
				args += "," + arg2;
			}
		}
    	
    	if (events.get(i).contains(".in")) {
    		msg = de.createMessage(ev, myLifelines.get(0), myLifelines.get(1), msgPosition);
    		m = (IMessage) msg.getModel();
    		m.setArgument(args);
		} else if (events.get(i).contains(".out")) {
			msg = de.createMessage(ev, myLifelines.get(1), myLifelines.get(0), msgPosition);
			m = (IMessage) msg.getModel();
			m.setArgument(args);
		} else {
			msg = de.createMessage(ev, myLifelines.get(1), myLifelines.get(0), msgPosition);
			m = (IMessage) msg.getModel();
			m.setArgument(args);
		}    	
    	if (i == events.size()-1) {
			msg.setProperty("line.color", "#FF0000");
			msg.setProperty("font.color", "#FF0000");
//			msg.setProperty(PresentationPropertyConstants.Key.FONT_COLOR, "#FF0000");
		}
		m.setAsynchronous(true);
		msgPosition += 50;
			
    	return msgPosition;
    }
    
    private <T extends INamedElement> T findNamedElement(INamedElement[] children, String name, Class<T> clazz) {
        for (INamedElement child : children) {
            if (clazz.isInstance(child) && child.getName().equals(name)) {
                return clazz.cast(child);
            }
        }
        return null;
    }
    
}
