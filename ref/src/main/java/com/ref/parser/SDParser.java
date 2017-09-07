package com.ref.parser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.ICombinedFragment;
import com.change_vision.jude.api.inf.model.IInteractionUse;
import com.change_vision.jude.api.inf.model.ILifeline;
import com.change_vision.jude.api.inf.model.IMessage;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IOperation;
import com.change_vision.jude.api.inf.model.ISequenceDiagram;
import com.change_vision.jude.api.inf.model.IStateInvariant;

public class SDParser {

	private ISequenceDiagram seq1;
	private ISequenceDiagram seq2;

	public SDParser(ISequenceDiagram seq1, ISequenceDiagram seq2) {
		this.seq1 = seq1;
		this.seq2 = seq2;
	}

	public String parseSDs() throws InvalidEditingException {
		StringBuilder process = new StringBuilder();
		process.append(defineTypes());
		process.append(parseChannels());
		process.append(parseSD1());
		//process.append(parseSD2());
		return process.toString();
	}

	public String defineTypes() throws InvalidEditingException {
		StringBuilder types = new StringBuilder();
//		int max = checkMaxIndex();
//		types.append("SDnat = {"); //numero da msg
//		for (int i = 1; i <= max; i++) {
//			System.out.println("adicionou "+ i);
//			types.append(i + ",");
//		}
//		types.append(getReplyIndexes());
//		types.deleteCharAt(types.length()-1);
//		types.append("}\n");
		
		types.append("datatype COM = s | r\n");
		Set<IClass> blocks = new HashSet<IClass>();

		types.append("ID1 = {");
		for (ILifeline lifeline : seq1.getInteraction().getLifelines()) {
			types.append(getLifelineId(lifeline));
			types.append(",");
			blocks.add(lifeline.getBase());
		}
		types.deleteCharAt(types.length()-1);
		types.append("}\n");
		types.append("ID2 = {");
		for (ILifeline lifeline : seq2.getInteraction().getLifelines()) {
			types.append(getLifelineId(lifeline));
			types.append(",");
			blocks.add(lifeline.getBase());
		}
		types.deleteCharAt(types.length()-1);
		types.append("}\n");
		types.append("ID_SD = {<").append(seq1.getId()).append(">,<").append(
				seq2.getId()).append(">}\n");

		for (IClass block : blocks) {
			defineBlockMessages(types, block);
		}
		return types.toString();
	}

	private String getReplyIndexes() {
		StringBuilder sb = new StringBuilder();
		for (IMessage msg : seq1.getInteraction().getMessages()) {
			if (msg.isSynchronous()) {
				sb.append(msg.getIndex()).append("r,");
				System.out.println("seq 1 adicionou "+ msg.getIndex());
			}
		}
		for (IMessage msg : seq2.getInteraction().getMessages()) {
			if (msg.isSynchronous()) {
				sb.append(msg.getIndex()).append("r,");
			}
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}

	private String getLifelineId(ILifeline lifeline) {
		return lifeline.getId();//lifeline.getName() != null ? 
				//lifeline.getName()+"_"+lifeline.getBase()+"_id":
				//	lifeline.getBase()+"_id";
	}

	private int checkMaxIndex() {
		int idx1 = 0;
		int idx2 = 0;
		for (IMessage msg : seq1.getInteraction().getMessages()) {
			if (!msg.isReturnMessage() && !msg.isCreateMessage()
					&& !msg.isDestroyMessage()) {
				idx1++;
			}
		}
		for (IMessage msg : seq2.getInteraction().getMessages()) {
			if (!msg.isReturnMessage() && !msg.isCreateMessage()
					&& !msg.isDestroyMessage()) {
				idx2++;
			}
		}
		return Math.max(idx1, idx2);
	}

	private void defineBlockMessages(StringBuilder types, IClass block) {
		
		Set<IMessage> messages = getBlockMessages(block);
		StringBuilder operations = new StringBuilder();
		StringBuilder signals = new StringBuilder();
		for (IMessage message : messages) {
			if (message.isAsynchronous()) {
				signals.append(message.getName());
				if (!"".equals(message.getArgument())) {
					treatArguments(signals, message.getArgument());
				}
				signals.append(" | ");
			} else if (message.isSynchronous()) {
				operations.append(message.getName() + "_I");
				if (!"".equals(message.getArgument())) {
					treatArguments(operations, message.getArgument());
				}
				operations.append(" | ");
				operations.append(message.getName() + "_O");
				if (!"".equals(message.getReturnValueVariable())) {
					treatArguments(operations, message.getReturnValueVariable());
				}
				operations.append(" | ");
			}

		}
		if (!signals.toString().isEmpty()) {
			signals.delete(signals.length()-3, signals.length());
			types.append("datatype ").append(block.getName());
			types.append("_SIG = ").append(signals.toString()).append("\n");
		}
		if (!operations.toString().isEmpty()) {
			operations.delete(operations.length()-3, operations.length());
			types.append("datatype ").append(block.getName());
			types.append("_OPS = ").append(operations.toString()).append("\n");
		}
	}

	private Set<IMessage> getBlockMessages(IClass block) {
		
		Set<IMessage> messages = new HashSet<IMessage>();
		messages.addAll(Arrays.asList(seq1.getInteraction().getMessages()));
		messages.addAll(Arrays.asList(seq2.getInteraction().getMessages()));
				
		Set<IMessage> ret = new HashSet<IMessage>();
		
		for (IOperation operation : block.getOperations()) {
			for (IMessage iMessage : messages) {
				if (iMessage.getOperation() != null && 
						iMessage.getOperation().getOwner() != null && 
						iMessage.getOperation().getOwner().equals(block) && 
						iMessage.getOperation().getName().equals(operation.getName()) &&
						!existMessage(ret,iMessage)) {
					ret.add(iMessage);
				}
			}
		}
		return ret;
	}

	private boolean existMessage(Set<IMessage> ret, IMessage mes) {
		for (IMessage iMessage : ret) {
			if (iMessage.getOperation() != null &&
					iMessage.getOperation().getOwner() != null &&
					iMessage.getOperation().getOwner() == mes.getOperation().getOwner() &&
					iMessage.getName().equals(mes.getName())
					) {
				return true;
			}
		}
		return false;
	}

	private String treatArguments(StringBuilder types, String argument) {
		
		Set<IMessage> messages = new HashSet<IMessage>();
		messages.addAll(Arrays.asList(seq1.getInteraction().getMessages()));
		messages.addAll(Arrays.asList(seq2.getInteraction().getMessages()));
		
		for (IMessage iMessage : messages) {
			//System.out.println(iMessage);
		}
		
		return "";
	}
	
	public String defineArguments(){

		Set<IMessage> messages = new HashSet<IMessage>();
		messages.addAll(Arrays.asList(seq1.getInteraction().getMessages()));
		//messages.addAll(Arrays.asList(seq2.getInteraction().getMessages()));
		
		StringBuilder parametros = new StringBuilder();
		String integers = "IntParams = {";
		String doubles = "DoubleParams = {";
		String chars = "CharParams = {";
		
		boolean hasInt = false;
		boolean hasDouble = false;
		boolean hasChar = false;
		
		for (IMessage iMessage : messages) {
			String argument = iMessage.getArgument();
			if(argument.contains(":")){
				String[] arguments = argument.split(",");
				for(int i = 0; i< arguments.length;i++){
					if(arguments[i].contains(":")){
						String[]aux = arguments[i].split(":");
						parametros.append("My" + aux[1] + " ={");
						if(aux[1].equals("Integer")){
							parametros.append("0,1,2,3,4,5,6,7,8,9}\n");
						}else if(aux[1].equals("String")){
							parametros.append("teste}\n");
						}
					}
					else{
						if(isInteger(arguments[i])){
							integers += arguments[i];
							hasInt = true;
						}else if(isDouble(arguments[i])){
							doubles += arguments[i];
							hasDouble = true;
						}else{
							chars+=arguments[i];
							hasChar = true;
						}
					}
				}
			}
		}
		if(hasInt){
			integers+="}\n";
			parametros.append(integers);
		}
		if(hasDouble){
			doubles +="}\n";
			parametros.append(doubles);
		}
		if(hasChar){
			chars += "}\n";
			parametros.append(chars);
		}
		
		System.out.println(parametros.toString());
		
		return "";
	}
	
	private boolean isInteger(String param){	
		try{
			Integer.parseInt(param);
		}catch(NumberFormatException nfe){
			return false;
		}
		return true;
	}
	
	private boolean isDouble(String param){
		try{
			Double.parseDouble(param);
		}catch(NumberFormatException nfe){
			return false;
		}
		return true;
	}

	public String parseChannels() {
		StringBuilder channelsSTR = new StringBuilder();
		channelsSTR.append("channel beginInteration,endInteraction\n");//ID_SD
		Set<IClass> blocks = new HashSet<IClass>();
		for (ILifeline lifeline : seq1.getInteraction().getLifelines()) {
			blocks.add(lifeline.getBase());
		}
		for (ILifeline lifeline : seq2.getInteraction().getLifelines()) {
			blocks.add(lifeline.getBase());
		}
		
		for (IClass block : blocks) {
			Set<IMessage> blockMessages = getBlockMessages(block);
			boolean hasSignal = false;
			boolean hasOperation = false;
			for (IMessage iMessage : blockMessages) {
				if (iMessage.isAsynchronous()) {
					hasSignal = true;
					if (hasOperation) {
						break;
					}
				}
				if (iMessage.isSynchronous()) {
					hasOperation = true;
					if (hasSignal) {
						break;
					}
				}
			}
			if (hasOperation) {
				channelsSTR.append("channel ").append(block.getName());
				//channelsSTR.append("_mOP: COM.SDNat.ID.ID.").append(block.getName());
				channelsSTR.append("_mOP: COM.ID.ID.").append(block.getName());
				channelsSTR.append("_OPS\n");
			}
			if (hasSignal) {
				channelsSTR.append("channel ").append(block.getName());
				//channelsSTR.append("_mSIG: COM.SDNat.ID.ID.").append(block.getName());
				channelsSTR.append("_mSIG: COM.ID.ID.").append(block.getName());
				channelsSTR.append("_SIG\n");
			}
		}
		
		return channelsSTR.toString();
	}

	public String parseSD1() {
		StringBuilder process = new StringBuilder();
		//Generate processes for lifelines
		for (ILifeline lifeline : seq1.getInteraction().getLifelines()) {
			process.append(translateLifeline(lifeline));
		}
		//Generate processes for Messages
		for (IMessage iMessage : seq1.getInteraction().getMessages()) {
			process.append(MessageParser.getInstance().translateMessageForProcess(iMessage, seq1));
		}
		//Generate MessagesBuffer Process
		process.append(MessageParser.getInstance().translateMessagesBuffer(seq1));
		//System.out.println(process.toString());
		return process.toString();
	}

	private String translateLifeline(ILifeline lifeline) {
		StringBuilder process = new StringBuilder();
		process.append(seq1.getName()).append("_").append(lifeline.getBase());
		process.append("(sd_id");
		process.append(") = ");
		for (INamedElement fragment : lifeline.getFragments()) {
			process.append(translateFragment(fragment,lifeline,seq1));
		}
		process.deleteCharAt(process.length()-1);
		process.append("\n");
		return process.toString();
	}

	private String translateFragment(INamedElement fragment, ILifeline lifeline, 
			ISequenceDiagram seq) {
		if (fragment instanceof IMessage) {
			return "("+MessageParser.getInstance().translateMessageForLifeline((IMessage)fragment,lifeline,seq)+");";
		} else if (fragment instanceof ICombinedFragment) {
			return null;
		} else if (fragment instanceof IStateInvariant) {
			return null;
		} else if (fragment instanceof IInteractionUse) {
			return null;
		}
		return null;
	}

	

}
