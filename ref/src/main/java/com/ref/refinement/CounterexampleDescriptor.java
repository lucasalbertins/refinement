package com.ref.refinement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.BasicModelEditor;
import com.change_vision.jude.api.inf.editor.ModelEditorFactory;
import com.change_vision.jude.api.inf.editor.SequenceDiagramEditor;
import com.change_vision.jude.api.inf.editor.TransactionManager;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.exception.LicenseNotFoundException;
import com.change_vision.jude.api.inf.exception.ProjectLockedException;
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
import com.change_vision.jude.api.inf.presentation.IPresentation;
import com.change_vision.jude.api.inf.project.ModelFinder;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;
import com.ref.parser.SDParser;

public class CounterexampleDescriptor {

	private Map<String, String> lifelines;
	private List<String> sortedLifelines;

	public void init(Map<String, String> lifelines) {
		this.lifelines = lifelines;
		this.sortedLifelines = sortMap(lifelines);
		System.out.println("Setou as lifelines");
	}

	private static INamedElement[] findSequence(ProjectAccessor projectAccessor) throws ProjectNotFoundException {
		INamedElement[] foundElements = projectAccessor.findElements(new ModelFinder() {
			public boolean isTarget(INamedElement namedElement) {
				return namedElement instanceof ISequenceDiagram;
			}
		});
		return foundElements;
	}

	public void createSD(String name, List<String> entrada, ProjectAccessor projectAccessor)
			throws ClassNotFoundException, LicenseNotFoundException, ProjectNotFoundException, IOException,
			ProjectLockedException {

		System.out.println("Entrou aqui");

		// loadInfo();
		List<String> events = preProcess(entrada);
		if (projectAccessor == null) {
			projectAccessor = ProjectAccessorFactory.getProjectAccessor();
			projectAccessor.create(name);
		}
		try {
			TransactionManager.beginTransaction();
			//createModels(events, projectAccessor);
			createSequenceDiagram(events, projectAccessor);
			TransactionManager.endTransaction();
			projectAccessor.save();

			System.out.println("Create SeqSample.asta Project done.");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			TransactionManager.abortTransaction();
			// projectAccessor.close();
		}
	}

	private List<String> preProcess(List<String> entrada) {

		List<String> result = new ArrayList<String>();

		for (String trace : entrada) {
			// System.out.println(trace);
			String newtrace = trace.replace("beginInteraction, ", "");
			newtrace = newtrace.replaceAll(", endInteraction", "");
			result.add(newtrace);
		}

		// String resultado = entrada.replace("beginInteraction, ", "");
		// resultado = resultado.replace(", ", "|");
		// String[] split = resultado.split(", ");
		// for(int i = 0; i < split.length; i++){
		// System.out.println(split[i] + "\n");
		// }

		return result;
	}

	private List<String> getClasses(List<String> events) {
		System.out.println("CLASSES:");
		List<String> classes = new ArrayList<String>();
		String classe = "";
		for (int i = 0; i < events.size(); i++) {
			List<String> messages = getMessages(events.get(i));
			for (int j = 0; j < messages.size(); j++) {
				if (!messages.get(j).equals("endInteraction") && !messages.get(j).equals("τ"))
					classe = messages.get(j).substring(0, 1);
				if (!classes.contains(classe)) {
					System.out.println(classe);
					classes.add(classe);
				}
			}
		}

		return classes;
	}

	private List<String> getOperations(String classe, List<String> events) {

		List<String> operations = new ArrayList<String>();
		String evento = "";
		String[] parts = null;
		String[] msg = null;
		for (int i = 0; i < events.size(); i++) {
			List<String> messages = getMessages(events.get(i));
			for (int j = 0; j < messages.size(); j++) {
				if (messages.get(j).substring(0, 1).equals(classe)) {
					evento = messages.get(j);
					parts = evento.split("\\.");
					msg = parts[4].split("\\_");
					if (!operations.contains(msg[0]))
						operations.add(msg[0]);
				}
			}
		}

		return operations;
	}

	private List<String> sortMap(Map<String, String> map) {

		Collection<String> values = map.values();
		List<String> nomes = new ArrayList<String>(values);
		Collections.sort(nomes);
		int iterator = 0;

		return nomes;
	}

	private void createModels(List<String> events, ProjectAccessor projectAccessor)
			throws ProjectNotFoundException, ClassNotFoundException, InvalidEditingException {
		// ProjectAccessor projectAccessor =
		// ProjectAccessorFactory.getProjectAccessor();
		IModel project = projectAccessor.getProject();
		BasicModelEditor bme = ModelEditorFactory.getBasicModelEditor();

		// List<String> classes = getClasses(events);
		List<String> operations = null;

		for (String lf : lifelines.keySet()) {
			String[] split = lifelines.get(lf).split("_");
			IClass cls1 = bme.createClass(project, split[0]);
			operations = getOperations(split[0], events);
			for (int j = 0; j < operations.size(); j++) {
				IOperation op = bme.createOperation(cls1, operations.get(j), "void");
			}
		}

		// IClass boundary = bme.createClass(project, "Boundary0");
		// boundary.addStereotype("boundary");
		//
		// IClass cls1 = bme.createClass(project, "Class1");
		// IOperation op = bme.createOperation(cls1, "add", "void");
		// bme.createParameter(op, "param0", boundary);
	}

	private void createSequenceDiagram(List<String> events, ProjectAccessor projectAccessor) throws Exception {

		// ProjectAccessor projectAccessor =
		// ProjectAccessorFactory.getProjectAccessor();
		IModel project = projectAccessor.getProject();
		// IClass cls1 = findNamedElement(project.getOwnedElements(), "Class1",
		// IClass.class);
		// IOperation op0 = findNamedElement(cls1.getOperations(), "add",
		// IOperation.class);

		// create sequence diagram
		SequenceDiagramEditor de = projectAccessor.getDiagramEditorFactory().getSequenceDiagramEditor();
		// ISequenceDiagram newDgm2 = de.createSequenceDiagram(op0, "Sequence
		// Diagram2");
		// newDgm2.getInteraction().setArgument("seq arg2");
		ISequenceDiagram newDgm = de.createSequenceDiagram(project, "Sequence Diagram1");

		// create lifelines
		List<INodePresentation> myLifelines = new ArrayList<INodePresentation>();
		double position = 0;
		for (String lf : sortedLifelines) {
			// System.out.println(lf);
			// System.out.println(lifelines.get(lf));
			String[] split = lf.split("_");
			IClass boundary = findNamedElement(project.getOwnedElements(), split[0], IClass.class);
			INodePresentation objPs1 = de.createLifeline(split[1], position);
			ILifeline lifeline1 = (ILifeline) objPs1.getModel();
			lifeline1.setBase(boundary);
			position = position + 200;
			myLifelines.add(objPs1);
		}

		// create messages, combinedFragment, interactionUse, stateInvariant

		INodePresentation framePs = (INodePresentation) findPresentationByType(newDgm, "Frame");
		List<ILinkPresentation> msgs = new ArrayList<ILinkPresentation>();

		List<String> msgsSpecification = getMessages(events.get(1));
		List<String> msgsImplementation;

		if (events.get(0).equals("endInteraction")) {
			msgsImplementation = getMessages(events.get(2));
		} else {
			msgsSpecification.add(events.get(0));
			msgsImplementation = null;
		}

		int[] ids = { -1, -1 };
		int msgPosition = 160;
		int msgType = 0;

		System.out.println("Specification possui: " + msgsSpecification.size());
		for (int i = 0; i < msgsSpecification.size(); i++) {
			String[] split = msgsSpecification.get(i).split("\\.");

			if (split[0].contains("SIG"))
				msgType = 1;
			else
				msgType = 0;

			if (!split[1].equals("r")) {
				ids = findLifeline(split[2], split[3], myLifelines);
				if (ids[0] != -1 && ids[1] != -1) {
					// de.createMessage(split[4], framePs,
					// myLifelines.get(id),
					// 80);
					String[] msgName = split[4].split("_");
					if (msgName.length >= 1 && !split[4].contains("_O")) {
						ILinkPresentation msg = de.createMessage(msgName[0], myLifelines.get(ids[0]),
								myLifelines.get(ids[1]), msgPosition);

						if (events.get(0).equals("endInteraction")
								&& !msgsImplementation.get(i).equals(msgsSpecification.get(i))) {
							msg.setProperty("line.color", "#FF0000");
						}

						if (!events.get(0).equals("endInteraction") && i == msgsSpecification.size() - 1) {
							msg.setProperty("line.color", "#FF0000");
						}

						if (msgType == 1) {
							IMessage m = (IMessage) msg.getModel();
							m.setAsynchronous(true);
						} else {
							msgs.add(msg);
						}

						msgPosition = msgPosition + 50;
					} else {
						IMessage message;
						for (ILinkPresentation msg : msgs) {
							message = (IMessage) msg.getModel();
							if (msgName[0].equals(message.getName())) {
								de.createReturnMessage("", msg);
								break;
							}
						}
					}

				}
			}

		}

		// if (events[events.length - 1] != "endInteraction") {
		// String[] split = events[events.length - 1].split("\\.");
		// ids = findLifeline(split[2], split[3], myLifelines);
		// if (ids[0] != -1 && ids[1] != -1) {
		// // de.createMessage(split[4], framePs, myLifelines.get(id),
		// // 80);
		// System.out.println(split[4]);
		// String[] msgName = split[4].split("_");
		// if (!msgName[1].equals("O")) {
		// ILinkPresentation msg = de.createMessage(msgName[0],
		// myLifelines.get(ids[0]),
		// myLifelines.get(ids[1]), msgPosition);
		// msg.setProperty("line.color", "#FF0000");
		// if (msgType == 1) {
		// IMessage m = (IMessage) msg.getModel();
		// m.setAsynchronous(true);
		// } else {
		// msgs.add(msg);
		// }
		//
		// }
		// }
		// }

		// msgPs.getSource().setProperty("fill.color", "#0000FF");
		// IMessage msg = (IMessage) msgPs.getModel();
		// msg.setAsynchronous(true);
		// msg.setOperation(op0);
		// msgPs.setProperty("parameter_visibility", "false");
		//
		// ILinkPresentation msgPs1 = de.createMessage("msg1",
		// msgPs.getSource(), objPs4, 190);
		// IMessage msg1 = (IMessage) msgPs1.getModel();
		// msg1.setArgument("arg1");
		// msg1.setGuard("guard1");
		// msg1.setReturnValue("retVal1");
		// msg1.setReturnValueVariable("retValVar1");
		// de.createReturnMessage("retMsg1", msgPs1);
		// ILinkPresentation msgPs11 = de.createMessage("msg11",
		// msgPs1.getTarget(), objPs5, 190);
		// de.createReturnMessage("retMsg11", msgPs11);
		//
		// INodePresentation combFragPs = de.createCombinedFragment("", "alt",
		// new Point2D.Double(420, 250), 300, 200);
		// ICombinedFragment combFrag = (ICombinedFragment)
		// combFragPs.getModel();
		// combFrag.getInteractionOperands()[0].setGuard("condition > 60");
		// combFrag.addInteractionOperand("", "else");
		// combFragPs.setProperty("operand.1.length", "100");
		//
		// de.createMessage("msg31", objPs4, objPs5, 270);
		// ILinkPresentation msgPs32 = de.createMessage("msg32", objPs4, objPs5,
		// 370);
		// msgPs32.setProperty("font.color", "#FF0000");
		//
		// INodePresentation usePs = de.createInteractionUse("use1", "arg0",
		// newDgm2, new Point2D.Double(10, 300), 250, 80);
		// usePs.setProperty("fill.color", "#FF0000"); //red
		// ILinkPresentation msgPs4 = de.createMessage("msg4", usePs, objPs3,
		// 350);
		// msgPs4.setProperty("line.color", "#FF0000");
		//
		// de.createStateInvariant(objPs5, "state1", 500);
		//
		// ILinkPresentation foundPs = de.createFoundMessage("foundMsg0", new
		// Point2D.Double(10, 430), objPs2);
		// IMessage foundMsg = (IMessage) foundPs.getModel();
		// foundMsg.addStereotype("stereotype1");
		// ILinkPresentation lostPs = de.createLostMessage("lostMsg0", objPs2,
		// new Point2D.Double(300, 480));
		// IMessage lostMsg = (IMessage) lostPs.getModel();
		// BasicModelEditor bme = ModelEditorFactory.getBasicModelEditor();
		// bme.createConstraint(lostMsg, "constraint1");
		// de.createDestroyMessage("destroyMsg0", objPs1, objPs2, 550);
		// de.createMessage("endMsg0", objPs5, framePs, 600);
		//
		// //create common elements
		// INodePresentation notePs1 = de.createNote("note for lifeline", new
		// Point2D.Double(700, 150));
		// de.createNoteAnchor(notePs1, objPs5);
		// INodePresentation notePs2 = de.createNote("note for message", new
		// Point2D.Double(400, 600));
		// notePs2.setProperty("fill.color", "#CC00CC");
		// de.createNoteAnchor(notePs2, lostPs);
	}

	private List<String> getMessages(String string) {
		List<String> msgs = new ArrayList<String>();
		String[] split = string.split(", ");
		for (int i = 0; i < split.length; i++) {
			msgs.add(split[i]);
		}
		return msgs;
	}

	private int[] findLifeline(String lfsrcID, String lfdestID, List<INodePresentation> myLifelines) {
		String lfName1 = lifelines.get(lfsrcID);
		String lfName2 = lifelines.get(lfdestID);
		String[] split1 = lfName1.split("_");
		String[] split2 = lfName2.split("_");
		int id = 0;
		int[] result = { -1, -1 };
		ILifeline life;

		for (INodePresentation lf : myLifelines) {
			life = (ILifeline) lf.getModel();
			if (life.getName().equals(split1[1])) {
				result[0] = id;
			}
			if (life.getName().equals(split2[1])) {
				result[1] = id;
			}
			id++;
		}
		return result;
	}

	private <T extends INamedElement> T findNamedElement(INamedElement[] children, String name, Class<T> clazz) {
		for (INamedElement child : children) {
			if (clazz.isInstance(child) && child.getName().equals(name)) {
				return clazz.cast(child);
			}
		}
		return null;
	}

	private IPresentation findPresentationByType(ISequenceDiagram dgm, String type) throws InvalidUsingException {
		for (IPresentation ps : dgm.getPresentations()) {
			if (ps.getType().equals(type)) {
				return ps;
			}
		}
		return null;
	}

}
