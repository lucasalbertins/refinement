package com.ref.parser;

import java.util.ArrayList;
import java.util.List;

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

		sb.append(".").append(SDParser.getLfsWithUnderscore().get(lf1));
		aux.append(".").append(SDParser.getLfsWithUnderscore().get(lf1));
		sb.append(".").append(SDParser.getLfsWithUnderscore().get(lf2));
		aux.append(".").append(SDParser.getLfsWithUnderscore().get(lf2));
		
	}
	
	private void addInstancesAndBases(StringBuilder sb, IMessage msg){
		if(!((ILifeline) msg.getSource()).getName().equals(""))
			sb.append(((ILifeline) msg.getSource()).getName()).append("_");
		
		sb.append(((ILifeline) msg.getSource()).getBase()).append("_");
		
		if(!((ILifeline) msg.getTarget()).getName().equals("")){
			sb.append(((ILifeline) msg.getTarget()).getName()).append("_");
		}
		
		sb.append(((ILifeline) msg.getTarget()).getBase());
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
			sb.append(seq.getName()).append("_");
			addInstancesAndBases(sb, msg);
			sb.append("_").append(msg.getName());
			
			if(SDParser.getMsgProcesses().contains(sb.toString())){
				return "";
			}
			else
				SDParser.getMsgProcesses().add(sb.toString());
				
			sb.append("(sd_id");
			sb.append(",").append(SDParser.getLfsWithUnderscore().get(base1));
			sb.append(",").append(SDParser.getLfsWithUnderscore().get(base2));
			sb.append(") =");
			

			sb.append(((ILifeline) msg.getTarget()).getBase()).append("_mOP.s");
			aux.append(((ILifeline) msg.getTarget()).getBase()).append("_mOP.s");

			addIDS(base1,base2,sb, aux);
			//sb.append("!"+msg.getName()+"_I");
			sb.append("?x");
			sb.append(":{x | x<-");
			sb.append(((ILifeline) msg.getTarget()).getBase()).append("_OPS");
			sb.append(",get_id(x) == ").append(msg.getName()).append("_I}");
			aux.append(".").append(msg.getName()).append("_I");
			SDParser.addToRefinementAlphabet(aux.toString());
			
			if(SDParser.alphabetMapContains(base1)){
				String alfa = SDParser.getAlphabetMapEntry(base1);
				alfa += ","+aux.toString();
				SDParser.addToAlphabetMap(base1,alfa);
			}else{
				SDParser.addToAlphabetMap(base1,aux.toString());
			}

			aux = new StringBuilder();
			sb.append(" -> ");
			sb.append(((ILifeline) msg.getTarget()).getBase()).append("_mOP.r");
			aux.append(((ILifeline) msg.getTarget()).getBase()).append("_mOP.r");

			addIDS(base1, base2, sb, aux);
			//sb.append("!"+msg.getName()+"_I");
			sb.append("!x -> ").append(seq.getName()).append("_");
			addInstancesAndBases(sb, msg);
			sb.append("_").append(msg.getName());
			sb.append("(sd_id");
			sb.append(",").append(SDParser.getLfsWithUnderscore().get(base1));
			sb.append(",").append(SDParser.getLfsWithUnderscore().get(base2));
			sb.append(")");
			aux.append(".").append(msg.getName()).append("_I");
			SDParser.addToRefinementAlphabet(aux.toString());
			
			if(SDParser.alphabetMapContains(base2)){
				String alfa = SDParser.getAlphabetMapEntry(base2);
				alfa += ", "+aux.toString();
				SDParser.addToAlphabetMap(base2,alfa);
			}else{
				SDParser.addToAlphabetMap(base2,aux.toString());
			}
			
			
		} else if (msg.isAsynchronous() && !msg.isReturnMessage()) {
			sb.append(seq.getName()).append("_");
			addInstancesAndBases(sb, msg);
			sb.append("_").append(msg.getName());
			
			if(SDParser.getMsgProcesses().contains(sb.toString())){
				return "";
			}
			else
				SDParser.getMsgProcesses().add(sb.toString());
			
			sb.append("(sd_id");
			sb.append(",").append(SDParser.getLfsWithUnderscore().get(base1));
			sb.append(",").append(SDParser.getLfsWithUnderscore().get(base2));
			
			sb.append(") = ");
			sb.append(((ILifeline) msg.getTarget()).getBase()).append("_mSIG.s");
			aux.append(((ILifeline) msg.getTarget()).getBase()).append("_mSIG.s");
			
			addIDS(base1, base2, sb, aux);
			//sb.append("!"+msg.getName());
			sb.append("?x");
			sb.append(":{x | x<-");
			sb.append(((ILifeline) msg.getTarget()).getBase()).append("_SIG");
			sb.append(",get_id(x) == ").append(msg.getName()+"}");
			sb.append(" -> ");
			aux.append(".").append(msg.getName());
			SDParser.addToRefinementAlphabet(aux.toString());
			
			if(SDParser.alphabetMapContains(base1)){
				String alfa = SDParser.getAlphabetMapEntry(base1);
				alfa += ", "+aux.toString();
				SDParser.addToAlphabetMap(base1,alfa);
			}else{
				SDParser.addToAlphabetMap(base1,aux.toString());
			}
			
			aux = new StringBuilder();
			sb.append(((ILifeline) msg.getTarget()).getBase()).append("_mSIG.r");
			aux.append(((ILifeline) msg.getTarget()).getBase()).append("_mSIG.r");
			
			addIDS(base1, base2, sb, aux);
			//sb.append("!"+msg.getName());
			sb.append("!x -> ");
			sb.append(seq.getName()).append("_");
			addInstancesAndBases(sb, msg);
			sb.append("_").append(msg.getName());
			sb.append("(sd_id");
			sb.append(",").append(SDParser.getLfsWithUnderscore().get(base1));
			sb.append(",").append(SDParser.getLfsWithUnderscore().get(base2));
			sb.append(")");
			aux.append(".").append(msg.getName());
			
			SDParser.addToRefinementAlphabet(aux.toString());
			if(SDParser.alphabetMapContains(base2)){
				String alfa = SDParser.getAlphabetMapEntry(base2);
				alfa += ", "+aux.toString();
				SDParser.addToAlphabetMap(base2,alfa);
			}else{
				SDParser.addToAlphabetMap(base2,aux.toString());
			}
			
			
		} else if (msg.isReturnMessage()) {
			IMessage syncMsg = null;
			try {
				syncMsg = getPreviousMessage(msg, seq);
			} catch (InvalidUsingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sb.append(seq.getName()).append("_");
			if(!((ILifeline) syncMsg.getTarget()).getName().equals(""))
				sb.append(((ILifeline) syncMsg.getTarget()).getName()).append("_");
			
			sb.append(((ILifeline) syncMsg.getTarget()).getBase()).append("_");
			
			if(!((ILifeline) syncMsg.getSource()).getName().equals(""))
				sb.append(((ILifeline) syncMsg.getSource()).getName()).append("_");
			
			sb.append(((ILifeline) syncMsg.getSource()).getBase());
			sb.append("_").append(syncMsg.getName());
			sb.append("_r");
			
			if(SDParser.getMsgProcesses().contains(sb.toString())){
				return "";
			}
			else
				SDParser.getMsgProcesses().add(sb.toString());
			
			sb.append("(sd_id");
			
			sb.append(",").append(SDParser.getLfsWithUnderscore().get(base1));
			sb.append(",").append(SDParser.getLfsWithUnderscore().get(base2));
			
			sb.append(") = ");
			

			sb.append(((ILifeline) syncMsg.getTarget()).getBase()).append("_mOP.s");
			aux.append(((ILifeline) syncMsg.getTarget()).getBase()).append("_mOP.s");
			addIDS(base1, base2, sb, aux);
			//sb.append("!"+syncMsg.getName()+"_O");
			sb.append("?x");
			sb.append(":{x | x<-");
			sb.append(((ILifeline) msg.getSource()).getBase()).append("_OPS");
			sb.append(",get_id(x) == ").append(syncMsg.getName() + "_O}");
			sb.append(" -> ");
			aux.append(".").append(syncMsg.getName()).append("_O");
			SDParser.addToRefinementAlphabet(aux.toString());
			
			if(SDParser.alphabetMapContains(base1)){
				String alfa = SDParser.getAlphabetMapEntry(base1);
				alfa +=", " + aux.toString();
				SDParser.addToAlphabetMap(base1,alfa);
			}else{
				SDParser.addToAlphabetMap(base1,aux.toString());
			}
			
			aux = new StringBuilder();
			sb.append(((ILifeline) syncMsg.getTarget()).getBase()).append("_mOP.r");
			aux.append(((ILifeline) syncMsg.getTarget()).getBase()).append("_mOP.r");
			addIDS(base1, base2, sb, aux);
			//sb.append("!"+msg.getName()+"_O");
			sb.append("!x -> ");
			sb.append(seq.getName()).append("_");
			if(!((ILifeline) syncMsg.getTarget()).getName().equals(""))
				sb.append(((ILifeline) syncMsg.getTarget()).getName()).append("_");
			
			sb.append(((ILifeline) syncMsg.getTarget()).getBase()).append("_");
			
			if(!((ILifeline) syncMsg.getSource()).getName().equals(""))
				sb.append(((ILifeline) syncMsg.getSource()).getName()).append("_");
			
			sb.append(((ILifeline) syncMsg.getSource()).getBase());

			sb.append("_").append(syncMsg.getName()).append("_r");
			sb.append("(sd_id");
			sb.append(",").append(SDParser.getLfsWithUnderscore().get(base1));
			sb.append(",").append(SDParser.getLfsWithUnderscore().get(base2));
			sb.append(")");
			aux.append(".").append(syncMsg.getName()).append("_O");
			SDParser.addToRefinementAlphabet(aux.toString());
			if(SDParser.alphabetMapContains(base2)){
				String alfa = SDParser.getAlphabetMapEntry(base2);
				alfa += ", " + aux.toString();
				SDParser.addToAlphabetMap(base2,alfa);
			}else{
				SDParser.addToAlphabetMap(base2,aux.toString());
			}
			
		}
		sb.append("\n");
//		SDParser.addToRefinementAlphabet(aux.toString());
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
//				sb.append(((ILifeline) msg.getSource()).getBase()).append(".");
//				sb.append(((ILifeline) msg.getSource()).getName()).append(".");
				sb.append(((ILifeline) msg.getTarget()).getBase()).append("_mOP.s");
				sb.append("!").append(SDParser.getLfsWithUnderscore().get(base1));
				sb.append("!").append(SDParser.getLfsWithUnderscore().get(base2));
				sb.append("."+msg.getName()+"_I");
				//treatArguments(sb, msg.getArgument());
				//treatArguments(aux, msg.getArgument());
				sb.append(" -> SKIP");
			} else if (msg.getTarget().getId().equals(lifeline.getId())) {
//				sb.append(((ILifeline) msg.getSource()).getBase()).append(".");
//				sb.append(((ILifeline) msg.getSource()).getName()).append(".");
				sb.append(lifeline.getBase()).append("_mOP.r");
				sb.append("!").append(SDParser.getLfsWithUnderscore().get(base1));
				sb.append("!").append(SDParser.getLfsWithUnderscore().get(base2));
				//sb.append("!"+msg.getName()+"_I");
				sb.append("?oper:{x | x <- ").append(((ILifeline) msg.getTarget()).getBase()).append("_OPS");
				sb.append(",(get_id(x) == ").append(msg.getName()).append("_I)}");
				sb.append(" -> SKIP");
			}

		} else if (msg.isAsynchronous() && !msg.isReturnMessage()) {
			if (msg.getSource().getId().equals(lifeline.getId())) {
//				sb.append(((ILifeline) msg.getSource()).getBase()).append(".");
//				sb.append(((ILifeline) msg.getSource()).getName()).append(".");
				sb.append(((ILifeline) msg.getTarget()).getBase()).append("_mSIG.s");
				sb.append("!").append(SDParser.getLfsWithUnderscore().get(base1));
				sb.append("!").append(SDParser.getLfsWithUnderscore().get(base2));
				sb.append("."+msg.getName());
				treatArguments(sb, msg.getArgument());
				treatArguments(aux, msg.getArgument());
				sb.append(" -> SKIP");
			} else {
//				sb.append(((ILifeline) msg.getSource()).getBase()).append(".");
//				sb.append(((ILifeline) msg.getSource()).getName()).append(".");
				sb.append(lifeline.getBase()).append("_mSIG.r");
				sb.append("!").append(SDParser.getLfsWithUnderscore().get(base1));
				sb.append("!").append(SDParser.getLfsWithUnderscore().get(base2));
				//sb.append("!"+msg.getName());
				sb.append("?signal:{x | x <- ").append(((ILifeline) msg.getTarget()).getBase()).append("_SIG");
				sb.append(",(get_id(x) == ").append(msg.getName()).append(")}");
				aux.append(",(get_id(x) == ").append(msg.getName()).append(")}");
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
//				sb.append(((ILifeline) msg.getSource()).getBase()).append(".");
//				sb.append(((ILifeline) msg.getSource()).getName()).append(".");
				sb.append(((ILifeline) syncMsg.getTarget()).getBase()).append("_mOP.r");
				sb.append("!").append(SDParser.getLfsWithUnderscore().get(base1));
				sb.append("!").append(SDParser.getLfsWithUnderscore().get(base2));
				//sb.append("!"+msg.getName()+"_O");
				sb.append("?out:");
				sb.append("{x | x <-").append(((ILifeline) syncMsg.getTarget()).getBase()).append("_OPS");
				sb.append(",(get_id(x) == ").append(syncMsg.getName()).append("_O)}");
				sb.append(" -> SKIP");
			} else if (msg.getSource().getId().equals(lifeline.getId())) {
//				sb.append(((ILifeline) msg.getSource()).getBase()).append(".");
//				sb.append(((ILifeline) msg.getSource()).getName()).append(".");
				sb.append(((ILifeline) syncMsg.getTarget()).getBase()).append("_mOP.s");
				sb.append("!").append(SDParser.getLfsWithUnderscore().get(base1));
				sb.append("!").append(SDParser.getLfsWithUnderscore().get(base2));
				sb.append("."+syncMsg.getName()).append("_O");
				treatArguments(sb, msg.getArgument());
				sb.append(" -> SKIP");
			}
		}
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
		StringBuilder aux = new StringBuilder();
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
		
		List<String>added = new ArrayList<String>();
		
		
		for (IMessage msg : seq1.getInteraction().getMessages()) {
			aux = new StringBuilder();
			if (msg.isReturnMessage()) {
				IMessage syncMsg = null;
				try {
					syncMsg = getPreviousMessage(msg, seq1);
				} catch (InvalidUsingException e) {
					e.printStackTrace();
				}
//				process.append(seq1.getName()).append("_").append(syncMsg.getName()).append("_r");
				aux.append(seq1.getName()).append("_");
				if(!((ILifeline) syncMsg.getTarget()).getName().equals(""))
					aux.append(((ILifeline) syncMsg.getTarget()).getName()).append("_");
				
				aux.append(((ILifeline) syncMsg.getTarget()).getBase()).append("_");
				
				if(!((ILifeline) syncMsg.getSource()).getName().equals(""))
					aux.append(((ILifeline) syncMsg.getSource()).getName()).append("_");
				
				aux.append(((ILifeline) syncMsg.getSource()).getBase());

				aux.append("_").append(syncMsg.getName()).append("_r");
			} else {
//				process.append(seq1.getName()).append("_").append(msg.getName());
				aux.append(seq1.getName()).append("_");
				addInstancesAndBases(aux, msg);
				aux.append("_").append(msg.getName());
			}
			if(added.contains(aux.toString())){
				
			}else{
				added.add(aux.toString());
				process.append(aux.toString());
				process.append("(");
				process.append("sd_id,");
				
				ILifeline life = (ILifeline) msg.getSource();
				process.append(SDParser.getLfsWithUnderscore().get(life.getBase().toString()));
				process.append(",");
				life = (ILifeline) msg.getTarget();
				process.append(SDParser.getLfsWithUnderscore().get(life.getBase().toString()));
				//			for(int j = 1; j<=i-1;j++){
//				process.append("lf" + j + "_id,");
//			}
				process.append(")");
				process.append(" ||| ");				
			}
		}
		process.delete(process.length() - 5, process.length());
		process.append(")");
		process.append("/").append("\\").append("endInteraction -> SKIP");
		return process.toString();

	}

}
