package com.ref.refinement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.ISequenceDiagram;
import com.ref.parser.SDParser;
import com.ref.ui.FDR3LocationDialog;
import com.refinement.exceptions.RefinementException;

import uk.ac.ox.cs.fdr.Assertion;
import uk.ac.ox.cs.fdr.AssertionList;
import uk.ac.ox.cs.fdr.Session;
import uk.ac.ox.cs.fdr.fdr;

public class RefinementController {
	
	private static RefinementController instance;
	private URLClassLoader urlCl;
	private Class fdrClass;
	private Class sessionClass;
	private Class assertionClass;
	private Class cancelerClass;
	private Class counterexampleClass;
	private Class deadlockCounterexampleClass;
	private Class determinismCounterexampleClass;
	private Class divergenceCounterexampleClass;
	private Class minAcceptanceCounterexampleClass;
	private Class traceCounterexampleClass;
	private Class debugContextClass;
	private Class refinementCounterexampleClass;
	private Class propertyCounterexampleClass;
	private Class behaviourClass;
	private Class ExplicitDivergenceBehaviour;
	private Class IrrelevantBehaviour;
	private Class LoopBehaviour;
    private Class MinAcceptanceBehaviour;
    private Class SegmentedBehaviour;
    private Class Event;
    private Class TraceBehaviour;
    private Class Machine;
    private Class Node;
    private Class ProcessName;
    private SDParser parser;
	
	private RefinementController() {
		loadFDR();
	}


	private void loadFDR() {
		try {
			Properties p = new Properties();
			p.load(new FileInputStream(new File(FDR3LocationDialog.FDR3_PROPERTY_FILE)));
			String filename = p.getProperty(FDR3LocationDialog.FDR3_JAR_LOCATION_PROPERTY);
			File f = new File(filename);
			
			loadFDRClasses(f);
		    
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	private void loadFDRClasses(File f) throws MalformedURLException, ClassNotFoundException {
		urlCl = new URLClassLoader(new URL[] { f.toURL() }, System.class.getClassLoader());
		fdrClass = urlCl.loadClass("uk.ac.ox.cs.fdr.fdr");
		sessionClass = urlCl.loadClass("uk.ac.ox.cs.fdr.Session");
		assertionClass = urlCl.loadClass("uk.ac.ox.cs.fdr.Assertion");
		cancelerClass = urlCl.loadClass("uk.ac.ox.cs.fdr.Canceller");
		counterexampleClass = urlCl.loadClass("uk.ac.ox.cs.fdr.Counterexample");
		deadlockCounterexampleClass = urlCl.loadClass("uk.ac.ox.cs.fdr.DeadlockCounterexample");
		determinismCounterexampleClass = urlCl.loadClass("uk.ac.ox.cs.fdr.DeterminismCounterexample");
		divergenceCounterexampleClass = urlCl.loadClass("uk.ac.ox.cs.fdr.DivergenceCounterexample");
		minAcceptanceCounterexampleClass = urlCl.loadClass("uk.ac.ox.cs.fdr.MinAcceptanceCounterexample"); 
		traceCounterexampleClass = urlCl.loadClass("uk.ac.ox.cs.fdr.TraceCounterexample");
		debugContextClass = urlCl.loadClass("uk.ac.ox.cs.fdr.DebugContext");
		refinementCounterexampleClass = urlCl.loadClass("uk.ac.ox.cs.fdr.RefinementCounterexample");
		propertyCounterexampleClass = urlCl.loadClass("uk.ac.ox.cs.fdr.PropertyCounterexample");
		behaviourClass = urlCl.loadClass("uk.ac.ox.cs.fdr.Behaviour");
		ExplicitDivergenceBehaviour = 
	    		urlCl.loadClass("uk.ac.ox.cs.fdr.ExplicitDivergenceBehaviour");
	    IrrelevantBehaviour = 
	    		urlCl.loadClass("uk.ac.ox.cs.fdr.IrrelevantBehaviour");
	    LoopBehaviour = 
	    		urlCl.loadClass("uk.ac.ox.cs.fdr.LoopBehaviour");
	    MinAcceptanceBehaviour = 
	    		urlCl.loadClass("uk.ac.ox.cs.fdr.MinAcceptanceBehaviour");
	    SegmentedBehaviour = 
	    		urlCl.loadClass("uk.ac.ox.cs.fdr.SegmentedBehaviour");
	    Event = 
	    		urlCl.loadClass("uk.ac.ox.cs.fdr.Event");
	    TraceBehaviour = 
	    		urlCl.loadClass("uk.ac.ox.cs.fdr.TraceBehaviour");
	    Machine = 
	    		urlCl.loadClass("uk.ac.ox.cs.fdr.Machine");
	    Node = 
	    		urlCl.loadClass("uk.ac.ox.cs.fdr.Node");
	    ProcessName = 
	    		urlCl.loadClass("uk.ac.ox.cs.fdr.ProcessName");
	}

	public static RefinementController getInstance() {
		if (instance == null){
			instance = new RefinementController();
		}
		return instance;
	}

	public void checkRefinement(ISequenceDiagram seq1, ISequenceDiagram seq2) throws RefinementException, InvalidEditingException {
		loadFDR();
		if (seq1 != null && seq2 != null) {
			parser = new SDParser(seq1, seq2); 
			//String process = parser.parseSDs();
			//process = includeAssertions(process);

			try {
				executeRefinement();
			} catch (IOException e) {
				throw new RefinementException("Error generating csp file.");
			}

			//String filename = generateFile(process);
		} else {
			throw new RefinementException("Could not recover sequence diagram.");
		}

	}

	private void executeRefinement() throws IOException {
		try {	
			String sep = System.getProperty("file.separator");
			String filename;
			filename = AstahAPI.getAstahAPI().getProjectAccessor().getProjectPath();
			filename = filename.substring(0, filename.lastIndexOf(sep));
			filename = filename + sep + "refTemp.csp";
			File file = new File(filename);
			FileWriter fw = new FileWriter(file);
			fw.append("channel a,b\n");
			fw.append("P = a -> b -> P\n");
			fw.append("Q = a -> Q\n");
			fw.append("assert Q [T= P");
			fw.flush();
			fw.close();
			Session session = new Session();
			session.loadFile(filename);
			AssertionList assertions = session.assertions();
			for (Assertion assertion : assertions) {
				assertion.execute(null);
				if (assertion.passed()) {
					System.out.println(assertion.toString() + ": Passou");
				} else {
					System.out.println(assertion.toString() + ": Não Passou");
				}
			}
			fdr.libraryExit();
			Object sessionObject = sessionClass.newInstance();
			invokeProperty(sessionClass, sessionObject, "loadFile", String.class, filename);
			List list = (List)invokeProperty(sessionClass, sessionObject, "assertions", null, null);
			for (Object object : list) {
				invokeProperty(assertionClass, object, "execute", cancelerClass, null);
				boolean passed = (Boolean) invokeProperty(assertionClass, object, "passed", null, null);
				String toString = (String)invokeProperty(assertionClass, object, "toString", null, null);
				System.out.println(toString +" "+
						(passed ? "Passed" : "Failed"));	
				
				List ces = (List)invokeProperty(assertionClass, object, "counterexamples", null, null);
				for (Object ce : ces) {
					describeCounterexample(sessionObject,ce);
				}
			}
			
			fdrClass.getMethod("libraryExit").invoke(null);
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProjectNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}
	
	private void describeCounterexample(Object session, Object counterexample) throws Exception {
		// Firstly, just print a simple description of the counterexample
	    //
	    // This uses dynamic casting to check the assertion type.
		
	    System.out.print("Counterexample type: ");
	    if (deadlockCounterexampleClass.isInstance(counterexample))
	        System.out.println("deadlock");
	    else if (determinismCounterexampleClass.isInstance(counterexample))
	        System.out.println("determinism");
	    else if (divergenceCounterexampleClass.isInstance(counterexample))
	        System.out.println("divergence");
	    else if (minAcceptanceCounterexampleClass.isInstance(counterexample))
	    {
	        Object minAcceptance = minAcceptanceCounterexampleClass.cast(counterexample);
	        System.out.print("minimal acceptance refusing {");
	        List<Long> mAList = (List<Long>)invokeProperty(minAcceptanceCounterexampleClass, 
        			minAcceptance, "minAcceptance", null, null);
	        for (Long event : mAList)
	            System.out.print(invokeProperty(sessionClass, session,
	            		"uncompileEvent", long.class, event).toString()
	            		 + ", ");
	        System.out.println("}");
	    }
	    else if (traceCounterexampleClass.isInstance(counterexample))
	    {
	    		Object trace = traceCounterexampleClass.cast(counterexample);
	    		Long errorEvent = (Long)invokeProperty(traceCounterexampleClass, trace, "errorEvent", null, null);
	    		Object event = invokeProperty(sessionClass, sessionClass.cast(session), "uncompileEvent", long.class, errorEvent);
	        System.out.println("trace with event "+ 
	        		(String)invokeProperty(Event, event, "toString", null, null));
	    }
	    else
	        System.out.println("unknown");

	    System.out.println("Children:");

	    // In order to print the children we use a DebugContext. This allows for
	    // division of behaviours into their component behaviours, and also ensures
	    // proper alignment amongst the child components.
	    Object debugContext = null;
	    
	    if (refinementCounterexampleClass.isInstance(counterexample)) {
	    		Constructor constructor =
		            debugContextClass.getConstructor(new Class[]{refinementCounterexampleClass,boolean.class});
		    debugContext = constructor.newInstance(refinementCounterexampleClass.cast(counterexample),false);
	    }
	    else if (propertyCounterexampleClass.isInstance(counterexample)) {
	    		Constructor constructor =
		            debugContextClass.getConstructor(new Class[]{propertyCounterexampleClass,boolean.class});
		    debugContext = constructor.newInstance(propertyCounterexampleClass.cast(counterexample),false);
	    }

	    invokeProperty(debugContextClass, debugContext, "initialise", cancelerClass, null);
	    
	    List behaviours = (List)invokeProperty(debugContextClass, debugContext, "rootBehaviours", null, null);
	    for (Object object : behaviours) {
			Object root = behaviourClass.cast(object);
			describeBehaviour(session, debugContext, root, 2, true);
		}
	        
		
	}

	private void describeBehaviour(Object session, Object debugContext, Object behaviour, int indent, boolean recurse) throws Exception {
		// Describe the behaviour type
	    printIndent(indent); System.out.print("behaviour type: ");
	    indent += 2;
	    
	    if (ExplicitDivergenceBehaviour.isInstance(behaviour))
	        System.out.println("explicit divergence after trace");
	    else if (IrrelevantBehaviour.isInstance(behaviour))
	        System.out.println("irrelevant");
	    else if (LoopBehaviour.isInstance(behaviour))
	    {
	        Object loop = LoopBehaviour.cast(behaviour);
	        System.out.println("loops after index " + 
	        		invokeProperty(LoopBehaviour, loop, "loopIndex", null, null));
	    }
	    else if (MinAcceptanceBehaviour.isInstance(behaviour))
	    {
	        Object minAcceptance = MinAcceptanceBehaviour.cast(behaviour);
	        System.out.print("minimal acceptance refusing {");
	        
	        List<Long> mAList = (List<Long>)invokeProperty(MinAcceptanceBehaviour, 
        			minAcceptance, "minAcceptance", null, null);
	        for (Long event : mAList){ 
	        		Object ev = invokeProperty(sessionClass, session,
	            		"uncompileEvent", long.class, event);
	        		ev = Event.cast(ev);
	        		System.out.print((String)invokeProperty(Event, ev, "toString", null, null)
	            		 + ", ");
	        }
	            
	        		
	        System.out.println("}");
	    }
	    else if (SegmentedBehaviour.isInstance(behaviour))
	    {
	        Object segmented = SegmentedBehaviour.cast(behaviour);
	        System.out.println("Segmented behaviour consisting of:");
	        // Describe the sections of this behaviour. Note that it is very
	        // important that false is passed to the the descibe methods below
	        // because segments themselves cannot be divded via the DebugContext.
	        // That is, asking for behaviourChildren for a behaviour of a
	        // SegmentedBehaviour is not allowed.
	        List traces = (List)invokeProperty(SegmentedBehaviour, segmented, "priorSections", null, null);
	        for (Object trace : traces) 
				describeBehaviour(session, debugContext, TraceBehaviour.cast(trace), indent + 2, false);
	        
	        describeBehaviour(session, debugContext, invokeProperty(SegmentedBehaviour, segmented, "last", null, null),
	            indent + 2, false);
	    }
	    else if (TraceBehaviour.isInstance(behaviour))
	    {
	    		
	    		Object trace = TraceBehaviour.cast(behaviour);
	    		Long ev = (Long)invokeProperty(TraceBehaviour, trace, "errorEvent", null, null);
	    		Object event = invokeProperty(sessionClass, session, "uncompileEvent", long.class, ev);
	        System.out.println("performs event " + 
	        		(String)invokeProperty(Event, event, "toString", null, null));
	    }

	    // Describe the trace of the behaviour
	    printIndent(indent); System.out.print("Trace: ");
	    List<Long> trace = (List<Long>)invokeProperty(behaviourClass, behaviour, "trace", null, null);
	    for (Long event : trace)
	    {
	        // INVALIDEVENT indiciates that this machine did not perform an event at
	        // the specified index (i.e. it was not synchronised with the machines
	        // that actually did perform the event).
	    		int invalidevent = -1;
	    		invalidevent = (Integer)fdrClass.getField("INVALIDEVENT").get(invalidevent);
	        if (event == invalidevent)
	            System.out.print("-, ");
	        else {
	    			Object ev = invokeProperty(sessionClass, session, "uncompileEvent", long.class, event);
	    			System.out.print((String)invokeProperty(
	    					Event, ev, "toString", null, null) + ", ");
	        }
	            
	    }
	    System.out.println();

	    // Describe any named states of the behaviour
	    printIndent(indent); System.out.print("States: ");
	    List nodes = (List)invokeProperty(behaviourClass, behaviour, "nodePath", null, null);
	    for (Object node : nodes)
	    {
	        if (node == null)
	            System.out.print("-, ");
	        else
	        {
	        		
	        		Object machine = invokeProperty(behaviourClass, behaviour, "machine", null, null);
	        		Method method = sessionClass.getDeclaredMethod("machineNodeName", Machine,Node);
		    	  	method.setAccessible(true);
				Object processName = method.invoke(session, machine, node);
	             
	            if (processName == null)
	                System.out.print("(unknown), ");
	            else
	                System.out.print((String)invokeProperty(
	    					ProcessName, processName, "toString", null, null)+", ");
	        }
	    }
	    System.out.println();

	    // Describe our own children recursively
	    if (recurse) {
	    		List behaviours = (List)invokeProperty(debugContextClass, debugContext, "behaviourChildren", behaviourClass, behaviour);
	        for (Object child : behaviours)
	            describeBehaviour(session, debugContext, child, indent + 2, true);
	    }
		
	}
	
	/**
	 * Prints a number of spaces to out.
	 */
	private static void printIndent(int indent) {
	    for (int i = 0; i < indent; ++i)
	        System.out.print(' ');
	}



	private static Object invokeProperty(Class dsClass, Object ds, String propertyName, Class paramClass,
		      Object paramValue) throws Exception
		  {
		    try
		    {
		    	  Method method;
		      if (paramClass != null) {
		    	  	method = dsClass.getDeclaredMethod(propertyName, paramClass);
		    	  	method.setAccessible(true);
				return method.invoke(ds, paramValue);
		      } else {
		    	  	method = dsClass.getDeclaredMethod(propertyName);
		    	  	method.setAccessible(true);
				return method.invoke(ds);
		      }
		    	  
		      
		     
		    }
		    catch (Exception e)
		    {
		      throw new Exception("Failed to invoke method");
		    }
		  }

	private String generateFile(String process) {
		// TODO Auto-generated method stub
		return null;
	}

	private String includeAssertions(String process) {
		return null;

	}
}
