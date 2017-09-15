package com.ref.parser;

import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.ILifeline;
import com.change_vision.jude.api.inf.model.IMessage;
import com.change_vision.jude.api.inf.model.ISequenceDiagram;
import com.change_vision.jude.api.inf.presentation.ILinkPresentation;

public class MessageParser {
	
	private static MessageParser instance;
	
	private MessageParser() {
	}
	
	public static MessageParser getInstance() {
		if (instance == null) {
			instance = new MessageParser();
		}
		return instance;
	}
	
	public String translateMessageForProcess(IMessage msg, ISequenceDiagram seq) {
		if (msg == null) {
			throw new NullPointerException("Message cannot be null.");
		}
		if (seq == null) {
			throw new NullPointerException("The Sequence Diagram seq cannot be null.");
		}
		StringBuilder sb = new StringBuilder();
		
		if (msg.isSynchronous()) {
				sb.append(seq.getName()).append("_").append(msg.getName());
				sb.append(" = ");
				sb.append(((ILifeline)msg.getTarget()).getBase()).append("_mOP.s.");
				sb.append(msg.getIndex()).append(".").append(msg.getSource().getId());
				sb.append(".").append(msg.getTarget().getId()).append("?x");
				sb.append(" -> ");
				sb.append(((ILifeline)msg.getTarget()).getBase()).append("_mOP.r.");
				sb.append(msg.getIndex()).append(".").append(msg.getSource().getId());
				sb.append(".").append(msg.getTarget().getId());
				sb.append("!x -> ").append(seq.getName()).append("_").append(msg.getName());
		} else if (msg.isAsynchronous() && !msg.isReturnMessage()) {
				sb.append(seq.getName()).append("_").append(msg.getName());
				sb.append(" = ");
				sb.append(((ILifeline)msg.getTarget()).getBase()).append("_mSIG.s.");
				sb.append(msg.getIndex()).append(".").append(msg.getSource().getId());
				sb.append(".").append(msg.getTarget().getId()).append("?x");
				sb.append(" -> ");
				sb.append(((ILifeline)msg.getTarget()).getBase()).append("_mSIG.r.");
				sb.append(msg.getIndex()).append(".").append(msg.getSource().getId());
				sb.append(".").append(msg.getTarget().getId());
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
			sb.append("_r = ");
			sb.append(((ILifeline)syncMsg.getTarget()).getBase()).append("_mOP.s.");
			sb.append(syncMsg.getIndex()).append("r.").append(syncMsg.getSource().getId());
			sb.append(".").append(syncMsg.getTarget().getId()).append("?x -> ");
			sb.append(((ILifeline)syncMsg.getTarget()).getBase()).append("_mOP.r.");
			sb.append(syncMsg.getIndex()).append("r.").append(syncMsg.getSource().getId());
			sb.append(".").append(syncMsg.getTarget().getId()).append("!x -> ");
			sb.append(seq.getName()).append("_").append(syncMsg.getName()).append("_r");
		}
		sb.append("\n");
		return sb.toString();
	}
	
	public String translateMessageForLifeline(IMessage msg, ILifeline lifeline, ISequenceDiagram seq) {
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
		if (msg.isSynchronous()) {
			if (msg.getSource().getId().equals(lifeline.getId())) {
				sb.append(((ILifeline)msg.getTarget()).getBase()).append("_mOP.s.");
				sb.append(msg.getIndex()).append(".").append(lifeline.getId());
				sb.append(".").append(msg.getTarget().getId()).append(".");
				sb.append(msg.getName()).append("_I");
				treatArguments(sb, msg.getArgument());
				sb.append(" -> SKIP");
			} else if (msg.getTarget().getId().equals(lifeline.getId())) {
				sb.append(lifeline.getBase()).append("_mOP.r.");
				sb.append(msg.getIndex()).append(".").append(msg.getSource().getId());
				sb.append(".").append(msg.getTarget().getId());
				sb.append("?oper:{x | x <- ").append(((ILifeline)msg.getTarget()).getBase()).append("_OPS");
				sb.append(",(x == ").append(msg.getName()).append("_I)}");
				sb.append(" -> SKIP");
			}
			
		} else if (msg.isAsynchronous() && !msg.isReturnMessage()) {
			if (msg.getSource().getId().equals(lifeline.getId())) {
				sb.append(((ILifeline)msg.getTarget()).getBase()).append("_mSIG.s.");
				sb.append(msg.getIndex()).append(".").append(lifeline.getId());
				sb.append(".").append(msg.getTarget().getId()).append(".");
				sb.append(msg.getName()).append("_S");
				treatArguments(sb, msg.getArgument());
				sb.append(" -> SKIP");
			} else {
				sb.append(lifeline.getBase()).append("_mSIG.r.");
				sb.append(msg.getIndex()).append(".").append(msg.getSource().getId());
				sb.append(".").append(msg.getTarget().getId());
				sb.append("?signal:{x | x <- ").append(((ILifeline)msg.getTarget()).getBase()).append("_SIG");
				sb.append(",(x == ").append(msg.getName()).append("_S)}");
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
				sb.append(((ILifeline)syncMsg.getTarget()).getBase()).append("_mOP.r.");
				sb.append(syncMsg.getIndex()).append("r.").append(syncMsg.getSource().getId());
				sb.append(".").append(syncMsg.getTarget().getId()).append("?out:");
				sb.append("{x | x <-").append(((ILifeline)syncMsg.getTarget()).getBase()).append("_OPS");
				sb.append(",(x == ").append(syncMsg.getName()).append("_O)}");
				sb.append(" -> SKIP");
			} else if (msg.getSource().getId().equals(lifeline.getId())) {
				sb.append(((ILifeline)syncMsg.getTarget()).getBase()).append("_mOP.s.");
				sb.append(syncMsg.getIndex()).append("r.").append(syncMsg.getSource().getId());
				sb.append(".").append(syncMsg.getTarget().getId()).append(".");
				sb.append(syncMsg.getName()).append("_O");
				treatArguments(sb, msg.getArgument());
				sb.append(" -> SKIP");
			}
		}
		return sb.toString();
	}

	private IMessage getPreviousMessage(IMessage msg, ISequenceDiagram seq) throws InvalidUsingException {
		IMessage[] messages = seq.getInteraction().getMessages();
		IMessage previous = null;
		double p1x = ((ILinkPresentation)msg.getPresentations()[0]).getPoints()[0].getX();
		double p1y = ((ILinkPresentation)msg.getPresentations()[0]).getPoints()[0].getY();
		double p2x = ((ILinkPresentation)msg.getPresentations()[0]).getPoints()[1].getX();
		double p2y = ((ILinkPresentation)msg.getPresentations()[0]).getPoints()[1].getY();
		
		if (messages != null) {
			double msgp1x = 0;
			double msgp1y = 0;
			double msgp2x = 0;
			double msgp2y = 0;
			
			double maxp1y = 0;
			double maxp2y = 0;
			
			for (int i = 0; i < messages.length; i++) {
			
				msgp1x = ((ILinkPresentation)messages[i].getPresentations()[0]).getPoints()[0].getX();
				msgp1y = ((ILinkPresentation)messages[i].getPresentations()[0]).getPoints()[0].getY();
				msgp2x = ((ILinkPresentation)messages[i].getPresentations()[0]).getPoints()[1].getX();
				msgp2y = ((ILinkPresentation)messages[i].getPresentations()[0]).getPoints()[1].getY();
				
				if (p1x == msgp2x && p2x == msgp1x && msgp1y < p1y && 
						msgp2y < p2y && msgp1y > maxp1y && msgp2y > maxp2y) {
					maxp1y = msgp1y;
					maxp2y = msgp2y;
					previous = messages[i];
				}
			}
		}
		return previous;
	}
	

	private void treatArguments(StringBuilder sb, String argument) {
		// TODO Auto-generated method stub
	}

	public String translateMessagesBuffer(ISequenceDiagram seq1)  {
		StringBuilder process = new StringBuilder();
		process.append(seq1.getName()).append("_MessagesBuffer = ");
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
			} else {
				process.append(seq1.getName()).append("_").append(msg.getName());
			}
			
			process.append(" ||| ");
		}
		process.delete(process.length()-5, process.length()-1);
		return process.toString();
				
	}

	
}