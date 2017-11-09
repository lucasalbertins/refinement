package com.ref.parser;

import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.ILifeline;
import com.change_vision.jude.api.inf.model.IMessage;
import com.change_vision.jude.api.inf.model.ISequenceDiagram;
import com.change_vision.jude.api.inf.presentation.ILinkPresentation;

public class MessageParser {

	private static MessageParser instance;
	private String msgBuffer;

	public String getMsgBuffer(){
		return msgBuffer;
	}
	
	private MessageParser() {
	}

	public static MessageParser getInstance() {
		if (instance == null) {
			instance = new MessageParser();
		}
		return instance;
	}

	private void addIDS(String lf1, String lf2, StringBuilder sb,StringBuilder aux) {

		sb.append(".").append(SDParser.getNome(lf1));
		aux.append(".").append(SDParser.getNome(lf1));
		sb.append(".").append(SDParser.getNome(lf2));
		aux.append(".").append(SDParser.getNome(lf2));
		
//		int i = numLF - 1;
//		for (int j = 1; j <= i; j++) {
//			sb.append(".lf" + j + "_id");
//			aux.append(".lf" + j + "_id");
//		}

	}
	
	private void addIDSr(String lf1, String lf2, StringBuilder sb, StringBuilder aux){
		
		
//		int i = numLF - 1;
//		for (int j = i; j >=1; j--) {
//			sb.append(".lf" + j + "_id");
//			aux.append(".lf" + j + "_id");
//		}
	}

	public String translateMessageForProcess(IMessage msg, ISequenceDiagram seq) {
		ILifeline lifeline1 = (ILifeline) msg.getSource();
		ILifeline lifeline2 = (ILifeline) msg.getTarget();
		String base1 = lifeline1.getBase().toString();
		String base2 = lifeline2.getBase().toString();
		
		if (msg == null) {
			throw new NullPointerException("Message cannot be null.");
		}
		if (seq == null) {
			throw new NullPointerException("The Sequence Diagram seq cannot be null.");
		}
		StringBuilder sb = new StringBuilder();
		StringBuilder aux = new StringBuilder();

		if (msg.isSynchronous()) {
			sb.append(seq.getName()).append("_").append(msg.getName());
			sb.append("(sd_id");
			sb.append(",").append(SDParser.getNome(base1));
			sb.append(",").append(SDParser.getNome(base2));
			sb.append(") =");
			
			sb.append(((ILifeline) msg.getTarget()).getBase()).append("_mOP.s");
			aux.append(((ILifeline) msg.getTarget()).getBase()).append("_mOP.s");
			// sb/* .append(msg.getIndex()).append(".")
			// */.append(msg.getSource().getId());
			// sb.append(".").append(msg.getTarget().getId())

			addIDS(base1,base2,sb, aux);
			sb.append("?x");
			sb.append(":{x | x<-");
			sb.append(((ILifeline) msg.getTarget()).getBase()).append("_OPS");
			sb.append(",get_id(x) == ").append(msg.getName()).append("_I}");
			SDParser.addAlfabeto(aux.toString());
			aux = new StringBuilder();
			sb.append(" -> ");
			sb.append(((ILifeline) msg.getTarget()).getBase()).append("_mOP.r");
			aux.append(((ILifeline) msg.getTarget()).getBase()).append("_mOP.r");
			// sb/* .append(msg.getIndex()).append(".")
			// */.append(msg.getSource().getId());
			// sb.append(".").append(msg.getTarget().getId());

			addIDS(base1, base2, sb, aux);
			sb.append("!x -> ").append(seq.getName()).append("_").append(msg.getName());
		} else if (msg.isAsynchronous() && !msg.isReturnMessage()) {
			sb.append(seq.getName()).append("_").append(msg.getName());
			sb.append("(sd_id");
			sb.append(",").append(SDParser.getNome(base1));
			sb.append(",").append(SDParser.getNome(base2));
			
			sb.append(") = ");
			sb.append(((ILifeline) msg.getTarget()).getBase()).append("_mSIG.s");
			aux.append(((ILifeline) msg.getTarget()).getBase()).append("_mSIG.s");
			// sb/* .append(msg.getIndex()).append(".")
			// */.append(msg.getSource().getId());
			// sb.append(".").append(msg.getTarget().getId())
			
			addIDS(base1, base2, sb, aux);
			sb.append("?x");
			sb.append(":{x | x<-");
			sb.append(((ILifeline) msg.getTarget()).getBase()).append("_OPS");
			sb.append(",get_id(x) == m0_I}");
			sb.append(" -> ");
			SDParser.addAlfabeto(aux.toString());
			aux = new StringBuilder();
			sb.append(((ILifeline) msg.getTarget()).getBase()).append("_mSIG.r");
			aux.append(((ILifeline) msg.getTarget()).getBase()).append("_mSIG.r");
			// sb/* .append(msg.getIndex()).append(".")
			// */.append(msg.getSource().getId());
			// sb.append(".").append(msg.getTarget().getId());
			
			addIDS(base1, base2, sb, aux);
			sb.append("!x -> ");
			sb.append(seq.getName()).append("_").append(msg.getName());
		} else if (msg.isReturnMessage()) {
			IMessage syncMsg = null;
			try {
				syncMsg = getPreviousMessage(msg, seq);
			} catch (InvalidUsingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sb.append(seq.getName()).append("_").append(syncMsg.getName());
			sb.append("_r ");
			sb.append("(sd_id");
			
			sb.append(",").append(SDParser.getNome(base1));
			sb.append(",").append(SDParser.getNome(base2));
			
//			for (int j = i-1; j >=1; j--) {
//				sb.append(",lf"+j+"_id");
//			}
			sb.append(") = ");
			
			sb.append(((ILifeline) syncMsg.getTarget()).getBase()).append("_mOP.s");
			aux.append(((ILifeline) syncMsg.getTarget()).getBase()).append("_mOP.s");
			/*
			 * sb.append(syncMsg.getIndex()).append("r.").append(syncMsg.
			 * getSource().getId());
			 */
			// sb.append(".").append(syncMsg.getTarget().getId())
			addIDSr(base1, base2, sb, aux);
			sb.append("?x");
			sb.append(":{x | x<-");
			sb.append(((ILifeline) msg.getTarget()).getBase()).append("_OPS");
			sb.append(",get_id(x) == m0_O}");
			sb.append(" -> ");
			SDParser.addAlfabeto(aux.toString());
			aux = new StringBuilder();
			sb.append(((ILifeline) syncMsg.getTarget()).getBase()).append("_mOP.r");
			aux.append(((ILifeline) syncMsg.getTarget()).getBase()).append("_mOP.r");
			// sb/* .append(syncMsg.getIndex()).append("r.")
			// */.append(syncMsg.getSource().getId());
			// sb.append(".").append(syncMsg.getTarget().getId())
			addIDSr(base1, base2, sb, aux);
			sb.append("!x -> ");
			sb.append(seq.getName()).append("_").append(syncMsg.getName()).append("_r");
		}
		sb.append("\n");
		SDParser.addAlfabeto(aux.toString());
		return sb.toString();
	}

	public String translateMessageForLifeline(IMessage msg, ILifeline lifeline, ISequenceDiagram seq) {
		ILifeline lifeline1 = (ILifeline) msg.getSource();
		ILifeline lifeline2 = (ILifeline) msg.getTarget();
		String base1 = lifeline1.getBase().toString();
		String base2 = lifeline2.getBase().toString();
		
		if (msg == null) {
			throw new NullPointerException("Message cannot be null.");
		}
		if (seq == null) {
			throw new NullPointerException("The Sequence Diagram seq cannot be null.");
		}
		if (lifeline == null) {
			throw new NullPointerException("The Lifeline lifeline cannot be null.");
		}
		StringBuilder sb = new StringBuilder();
		StringBuilder aux = new StringBuilder();
		if (msg.isSynchronous()) {
			if (msg.getSource().getId().equals(lifeline.getId())) {
				sb.append(((ILifeline) msg.getTarget()).getBase()).append("_mOP.s");
				// aux.append(((ILifeline)
				// msg.getTarget()).getBase()).append("_mOP.s");
				// COLOCA O ENVIO
				// sb/* .append(msg.getIndex()).append(".")
				// */.append(lifeline.getId());
				// sb.append(".").append(msg.getTarget().getId()).append(".");
				//sb.append("!lf1_id").append("!lf2_id.");
				sb.append("!").append(SDParser.getNome(base1));
				sb.append("!").append(SDParser.getNome(base2));
				// aux.append(".lf1_id").append(".lf2_id.");
				//sb.append(msg.getName()).append("_I");
				treatArguments(sb, msg.getArgument());
				treatArguments(aux, msg.getArgument());
				sb.append(" -> SKIP");
			} else if (msg.getTarget().getId().equals(lifeline.getId())) {
				sb.append(lifeline.getBase()).append("_mOP.r");
				// aux.append(lifeline.getBase()).append("_mOP.r");
				// sb/* .append(msg.getIndex()).append(".")
				// */.append(msg.getSource().getId());
				// sb.append(".").append(msg.getTarget().getId());
				//sb.append("!lf1_id").append("!lf2_id");
				sb.append("!").append(SDParser.getNome(base1));
				sb.append("!").append(SDParser.getNome(base2));
				// aux.append(".lf1_id").append(".lf2_id");
				sb.append("?oper:{x | x <- ").append(((ILifeline) msg.getTarget()).getBase()).append("_OPS");
				// aux.append("?oper:{x | x <- ").append(((ILifeline)
				// msg.getTarget()).getBase()).append("_OPS");
				sb.append(",(x == ").append(msg.getName()).append("_I)}");
				// aux.append(",(x == ").append(msg.getName()).append("_I)}");
				sb.append(" -> SKIP");
			}

		} else if (msg.isAsynchronous() && !msg.isReturnMessage()) {
			if (msg.getSource().getId().equals(lifeline.getId())) {
				sb.append(((ILifeline) msg.getTarget()).getBase()).append("_mSIG.s");
				// aux.append(((ILifeline)
				// msg.getTarget()).getBase()).append("_mSIG.s");
				// sb/* .append(msg.getIndex()).append(".")
				// */.append(lifeline.getId());
				// sb.append(".").append(msg.getTarget().getId()).append(".");
				//sb.append("!lf1_id").append("!lf2_id.");
				sb.append("!").append(SDParser.getNome(base1));
				sb.append("!").append(SDParser.getNome(base2));
				// aux.append(".lf1_id").append(".lf2_id.");
				sb.append(msg.getName()).append("_S");
				// aux.append(msg.getName()).append("_S");
				treatArguments(sb, msg.getArgument());
				treatArguments(aux, msg.getArgument());
				sb.append(" -> SKIP");
			} else {
				sb.append(lifeline.getBase()).append("_mSIG.r");
				// aux.append(lifeline.getBase()).append("_mSIG.r");
				// sb/* .append(msg.getIndex()).append(".")
				// */.append(msg.getSource().getId());
				// sb.append(".").append(msg.getTarget().getId());
				//sb.append("!lf1_id").append("!lf2_id");
				sb.append("!").append(SDParser.getNome(base1));
				sb.append("!").append(SDParser.getNome(base2));
				// aux.append(".lf1_id").append(".lf2_id.");
				sb.append("?signal:{x | x <- ").append(((ILifeline) msg.getTarget()).getBase()).append("_SIG");
				// aux.append("?signal:{x | x <- ").append(((ILifeline)
				// msg.getTarget()).getBase()).append("_SIG");
				sb.append(",(x == ").append(msg.getName()).append("_S)}");
				aux.append(",(x == ").append(msg.getName()).append("_S)}");
				sb.append(" -> SKIP");
			}
		} else if (msg.isReturnMessage()) {
			IMessage syncMsg = null;
			try {
				syncMsg = getPreviousMessage(msg, seq);
			} catch (InvalidUsingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (msg.getTarget().getId().equals(lifeline.getId())) {
				sb.append(((ILifeline) syncMsg.getTarget()).getBase()).append("_mOP.r");
				// aux.append(((ILifeline)
				// syncMsg.getTarget()).getBase()).append("_mOP.r");
				// sb/* .append(syncMsg.getIndex()).append("r.")
				// */.append(syncMsg.getSource().getId());
				// sb.append(".").append(syncMsg.getTarget().getId())
				//sb.append("!lf1_id").append("!lf2_id");
				sb.append("!").append(SDParser.getNome(base1));
				sb.append("!").append(SDParser.getNome(base2));
				// aux.append(".lf1_id").append(".lf2_id.");
				sb.append("?out:");
				// aux.append("?out:");
				sb.append("{x | x <-").append(((ILifeline) syncMsg.getTarget()).getBase()).append("_OPS");
				// aux.append("{x | x <-").append(((ILifeline)
				// syncMsg.getTarget()).getBase()).append("_OPS");
				sb.append(",(x == ").append(syncMsg.getName()).append("_O)}");
				// aux.append(",(x ==
				// ").append(syncMsg.getName()).append("_O)}");
				sb.append(" -> SKIP");
			} else if (msg.getSource().getId().equals(lifeline.getId())) {
				sb.append(((ILifeline) syncMsg.getTarget()).getBase()).append("_mOP.s");
				// aux.append(((ILifeline)
				// syncMsg.getTarget()).getBase()).append("_mOP.s");
				// sb/* .append(syncMsg.getIndex()).append("r.")
				// */.append(syncMsg.getSource().getId());
				// sb.append(".").append(syncMsg.getTarget().getId()).append(".");
				//sb.append("!lf1_id").append("!lf2_id");
				sb.append("!").append(SDParser.getNome(base1));
				sb.append("!").append(SDParser.getNome(base2));
				// aux.append(".lf1_id").append(".lf2_id.");
				sb.append(syncMsg.getName()).append("_O");
				// aux.append(syncMsg.getName()).append("_O");
				treatArguments(sb, msg.getArgument());
				// treatArguments(aux, msg.getArgument());
				sb.append(" -> SKIP");
			}
		}
		// SDParser.addList(aux.toString());
		return sb.toString();
	}

	private IMessage getPreviousMessage(IMessage msg, ISequenceDiagram seq) throws InvalidUsingException {
		IMessage[] messages = seq.getInteraction().getMessages();
		IMessage previous = null;
		double p1x = ((ILinkPresentation) msg.getPresentations()[0]).getPoints()[0].getX();
		double p1y = ((ILinkPresentation) msg.getPresentations()[0]).getPoints()[0].getY();
		double p2x = ((ILinkPresentation) msg.getPresentations()[0]).getPoints()[1].getX();
		double p2y = ((ILinkPresentation) msg.getPresentations()[0]).getPoints()[1].getY();

		if (messages != null) {
			double msgp1x = 0;
			double msgp1y = 0;
			double msgp2x = 0;
			double msgp2y = 0;

			double maxp1y = 0;
			double maxp2y = 0;

			for (int i = 0; i < messages.length; i++) {

				msgp1x = ((ILinkPresentation) messages[i].getPresentations()[0]).getPoints()[0].getX();
				msgp1y = ((ILinkPresentation) messages[i].getPresentations()[0]).getPoints()[0].getY();
				msgp2x = ((ILinkPresentation) messages[i].getPresentations()[0]).getPoints()[1].getX();
				msgp2y = ((ILinkPresentation) messages[i].getPresentations()[0]).getPoints()[1].getY();

				if (p1x == msgp2x && p2x == msgp1x && msgp1y < p1y && msgp2y < p2y && msgp1y > maxp1y
						&& msgp2y > maxp2y) {
					maxp1y = msgp1y;
					maxp2y = msgp2y;
					previous = messages[i];
				}
			}
		}
		return previous;
	}

	private void treatArguments(StringBuilder sb, String argument) {
		StringBuilder aux = new StringBuilder();

		if (argument.contains(":")) {
			String[] arguments = argument.split(",");
			for (int i = 0; i < arguments.length; i++) {
				if (arguments[i].contains(":")) {
					String[] temp = arguments[i].split(":");
					aux.append("?" + temp[0].trim()).append(":{" + temp[0]).append("|" + temp[0])
							.append("<-My" + temp[1] + "}");
				} else {
					aux.append("!(" + arguments[i] + ")");
				}
			}
		}
		sb.append(aux.toString());
	}

	public String translateMessagesBuffer(ISequenceDiagram seq1) {
		StringBuilder process = new StringBuilder();
		process.append(seq1.getName()).append("_MessagesBuffer(sd_id");
		int i = 1;
		for (ILifeline lifelines : seq1.getInteraction().getLifelines()) {
			process.append(",lf" + i + "_id");
			i++;
		}
		process.append(")");
		msgBuffer = process.toString();
		process.append(" = ");
		process.append("(");
		for (IMessage msg : seq1.getInteraction().getMessages()) {
			if (msg.isReturnMessage()) {
				IMessage syncMsg = null;
				try {
					syncMsg = getPreviousMessage(msg, seq1);
				} catch (InvalidUsingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				process.append(seq1.getName()).append("_").append(syncMsg.getName()).append("_r");
				//process.append("(sd_id, lf1_id, lf2_id)");
			} else {
				process.append(seq1.getName()).append("_").append(msg.getName());
				//process.append("(sd_id, lf1_id, lf2_id)");
			}
			for(int j = 1; j<=i-1;j++){
				process.append(",lf" + j + "_id");
			}
			process.append(")");
			process.append(" ||| ");
		}
		process.delete(process.length() - 5, process.length() - 1);
		process.append(")");
		process.append("/").append("\\").append("endInteraction -> SKIP");
		return process.toString();

	}

}
