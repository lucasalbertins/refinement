package com.ref.fdr;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

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

	public List<String> fdrRefinement(String filename) throws Exception {
		PrintStream out = System.out;
		List<String> result = new ArrayList<String>();
		if (!fdr.hasValidLicense()) {
			throw new Exception("Please run refines or FDR4 to obtain a valid license before running this.");
		}

		String fileName = filename;

		Session session = new Session();
		try {
			session.loadFile(fileName);
		} catch (FileLoadError error) {
			throw new Exception("Could not load. Error: " + error.toString());
		}

		for (Assertion assertion : session.assertions()) {
			try {
				assertion.execute(null);

				for (Counterexample counterexample : assertion.counterexamples()) {
					result = describeCounterexample(out, session, counterexample);
				}
			} catch (InputFileError error) {
				throw new Exception("Could not compile: " + error.toString());
			}
		}

		return result;
	}

	private List<String> describeCounterexample(PrintStream out, Session session, Counterexample counterexample) {
		// out.print("Counterexample type: ");
		List<String> result = new ArrayList<String>();
		StringBuilder stb = new StringBuilder();

		if (counterexample instanceof TraceCounterexample) {
			TraceCounterexample trace = (TraceCounterexample) counterexample;
			// codigo adicionado para imprimir trace da especificacao
			// System.out.println("\nEspecificacao: ");
			// out.print("Trace: ");

			for (Long event : trace.specificationBehaviour().trace()) {
				if (event == fdr.INVALIDEVENT) {
					System.out.println(fdr.INVALIDEVENT);
					out.print("-, ");
					stb.append("-, ");
				} else {
					out.print(session.uncompileEvent(event).toString() + ", ");
					stb.append(session.uncompileEvent(event).toString() + ", ");
				}
			}
			result.add(stb.toString());
			// System.out.println("fim ");
			// fim do codigo adicionado
			 out.println("trace with event " +
			 session.uncompileEvent(trace.errorEvent()));
			// Evento que deu problema
		} else {
			// out.println("unknown");
		}

		// out.println("Children:");

		DebugContext debugContext = null;

		if (counterexample instanceof RefinementCounterexample)
			debugContext = new DebugContext((RefinementCounterexample) counterexample, false);

		debugContext.initialise(null);
		for (Behaviour root : debugContext.rootBehaviours()) {
			result.add(describeBehaviour(out, session, debugContext, root, 2, true));
			break;
		}

		return result;
	}

	private String describeBehaviour(PrintStream out, Session session, DebugContext debugContext, Behaviour behaviour,
			int indent, boolean recurse) {
		// Describe the behaviour type
		// printIndent(out, indent);
		// out.print("behaviour type: ");
		StringBuilder stb = new StringBuilder();
		// indent += 2;
		if (behaviour instanceof TraceBehaviour) {
			TraceBehaviour trace = (TraceBehaviour) behaviour;
			out.println("performs event " + session.uncompileEvent(trace.errorEvent()).toString());
		}

		// Describe the trace of the behaviour
		// printIndent(out, indent);
		// out.print("Trace: ");
		for (Long event : behaviour.trace()) {
			// INVALIDEVENT indiciates that this machine did not perform an
			// event at
			// the specified index (i.e. it was not synchronised with the
			// machines
			// that actually did perform the event).
			if (event == fdr.INVALIDEVENT) {
				out.print("-, ");
				stb.append("-, ");
			} else {
				out.print(session.uncompileEvent(event).toString() + ", ");
				stb.append(session.uncompileEvent(event).toString() + ", ");
			}
		}

		return stb.toString();
		// out.println();
		//
		// // Describe any named states of the behaviour
		// printIndent(out, indent);
		// out.print("States: ");
		// for (Node node : behaviour.nodePath()) {
		// if (node == null)
		// out.print("-, ");
		// else {
		// ProcessName processName =
		// session.machineNodeName(behaviour.machine(), node);
		// if (processName == null)
		// out.print("(unknown), ");
		// else
		// out.print(processName.toString() + ", ");
		// }
		// }
		// out.println();
		//
		// // Describe our own children recursively
		// if (recurse) {
		// for (Behaviour child : debugContext.behaviourChildren(behaviour))
		// describeBehaviour(out, session, debugContext, child, indent + 2,
		// true);
		// }
	}

	/**
	 * Prints a number of spaces to out.
	 */
	private static void printIndent(PrintStream out, int indent) {
		for (int i = 0; i < indent; ++i)
			out.print(' ');
	}

}
