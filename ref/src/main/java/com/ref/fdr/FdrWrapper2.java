package com.ref.fdr;

import java.io.PrintStream;

import uk.ac.ox.cs.fdr.Assertion;
import uk.ac.ox.cs.fdr.Behaviour; 
import uk.ac.ox.cs.fdr.Counterexample; 
import uk.ac.ox.cs.fdr.DebugContext; 
import uk.ac.ox.cs.fdr.FileLoadError;
import uk.ac.ox.cs.fdr.InputFileError;
import uk.ac.ox.cs.fdr.Node; 
import uk.ac.ox.cs.fdr.ProcessName;
import uk.ac.ox.cs.fdr.RefinementCounterexample; 
import uk.ac.ox.cs.fdr.Session; 
import uk.ac.ox.cs.fdr.TraceBehaviour;
import uk.ac.ox.cs.fdr.TraceCounterexample; 
import uk.ac.ox.cs.fdr.fdr; 

public class FdrWrapper2 {

	public static void main(String argv[]) {
		int returnCode;
		try {
			returnCode = realMain(argv);
		} finally {
			// Shutdown FDR
			fdr.libraryExit();
		}

		System.exit(returnCode);
	}

	/**
	 * The actual main function.
	 */
	private static int realMain(String[] argv) {
		PrintStream out = System.out;

		if (!fdr.hasValidLicense()) {
			out.println("Please run refines or FDR4 to obtain a valid license before running this.");
			return 1;
		}

	//	out.println("Using FDR version " + fdr.version());

//		if (argv.length != 1) {
//			out.println("Expected exactly one argument.");
//			return 1;
//		}

		String fileName = "result.csp";
		//out.println("Loading " + fileName);

		Session session = new Session();
		try {
			session.loadFile(fileName);
		} catch (FileLoadError error) {
			out.println("Could not load. Error: " + error.toString());
			return 1;
		}

		// Check each of the assertions
		for (Assertion assertion : session.assertions()) {
			//out.println("Checking: " + assertion.toString());
			try {
				assertion.execute(null);
				//out.println((assertion.passed() ? "Passed" : "Failed") + ", found "
				//		+ (assertion.counterexamples().size()) + " counterexamples");

				
				// Pretty print the counterexamples
				for (Counterexample counterexample : assertion.counterexamples()) {
					describeCounterexample(out, session, counterexample);
				}
			} catch (InputFileError error) {
				out.println("Could not compile: " + error.toString());
				return 1;
			}
		}

		return 0;
	}

	/**
	 * Pretty prints the specified counterexample to out.
	 */
	private static void describeCounterexample(PrintStream out, Session session, Counterexample counterexample) {
		// Firstly, just print a simple description of the counterexample
		//
		// This uses dynamic casting to check the assertion type.
		//out.print("Counterexample type: ");
		if (counterexample instanceof TraceCounterexample) {
			TraceCounterexample trace = (TraceCounterexample) counterexample;
			//codigo adicionado para imprimir trace da especificacao
			
			System.out.println("\nEspecificacao: ");
			out.print("Trace: ");
			
			for (Long event : trace.specificationBehaviour().trace()) {
				// INVALIDEVENT indiciates that this machine did not perform an
				// event at
				// the specified index (i.e. it was not synchronised with the
				// machines
				// that actually did perform the event).
				if (event == fdr.INVALIDEVENT)
					out.print("-, ");
				else
					out.print(session.uncompileEvent(event).toString() + ", ");
			}
			System.out.println("fim ");
			// fim do codigo adicionado
			out.println("trace with event " + session.uncompileEvent(trace.errorEvent()).toString());
		} else
			out.println("unknown");

		out.println("Children:");

		// In order to print the children we use a DebugContext. This allows for
		// division of behaviours into their component behaviours, and also
		// ensures
		// proper alignment amongst the child components.
		DebugContext debugContext = null;

		if (counterexample instanceof RefinementCounterexample)
			debugContext = new DebugContext((RefinementCounterexample) counterexample, false);
//		else if (counterexample instanceof PropertyCounterexample)
//			debugContext = new DebugContext((PropertyCounterexample) counterexample, false);

		debugContext.initialise(null);
		for (Behaviour root : debugContext.rootBehaviours()){
			describeBehaviour(out, session, debugContext, root, 2, true);
			break;			
		}
	}

	/**
	 * Prints a vaguely human readable description of the given behaviour to
	 * out.
	 */
	private static void describeBehaviour(PrintStream out, Session session, DebugContext debugContext,
			Behaviour behaviour, int indent, boolean recurse) {
		// Describe the behaviour type
		printIndent(out, indent);
		//out.print("behaviour type: ");
		indent += 2;
		 if (behaviour instanceof TraceBehaviour) {
			TraceBehaviour trace = (TraceBehaviour) behaviour;
			//out.println("performs event " + session.uncompileEvent(trace.errorEvent()).toString());
		}

		// Describe the trace of the behaviour
		printIndent(out, indent);
		out.print("Trace: ");
		for (Long event : behaviour.trace()) {
			// INVALIDEVENT indiciates that this machine did not perform an
			// event at
			// the specified index (i.e. it was not synchronised with the
			// machines
			// that actually did perform the event).
			if (event == fdr.INVALIDEVENT)
				out.print("-, ");
			else
				out.print(session.uncompileEvent(event).toString() + ", ");
		}
		out.println();

		// Describe any named states of the behaviour
		printIndent(out, indent);
		out.print("States: ");
		for (Node node : behaviour.nodePath()) {
			if (node == null)
				out.print("-, ");
			else {
				ProcessName processName = session.machineNodeName(behaviour.machine(), node);
				if (processName == null)
					out.print("(unknown), ");
				else
					out.print(processName.toString() + ", ");
			}
		}
		out.println();

		// Describe our own children recursively
		if (recurse) {
			for (Behaviour child : debugContext.behaviourChildren(behaviour))
				describeBehaviour(out, session, debugContext, child, indent + 2, true);
		}
	}

	/**
	 * Prints a number of spaces to out.
	 */
	private static void printIndent(PrintStream out, int indent) {
		for (int i = 0; i < indent; ++i)
			out.print(' ');
	}

}
