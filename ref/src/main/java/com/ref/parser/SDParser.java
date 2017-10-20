package com.ref.parser;

import java.util.ArrayList;
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
	private static List<String> processos;
	private static List<String> alfabeto;
	private String paralel;
	private int numLife;

	public SDParser(ISequenceDiagram seq1, ISequenceDiagram seq2) {
		this.seq1 = seq1;
		this.seq2 = seq2;
		processos = new ArrayList<String>();
		alfabeto = new ArrayList<String>();
	}

	public static void addProcesso(String elem) {
		processos.add(elem);
	}

	public static void addAlfabeto(String elem) {
		alfabeto.add(elem);
	}

	public String parseSDs() throws InvalidEditingException {
		StringBuilder process = new StringBuilder();
		process.append(defineTypes());
		process.append(parseChannels());
		process.append(parseSD1());
		// process.append(parseSD2());
		return process.toString();
	}

	public String defineTypes() throws InvalidEditingException {
		StringBuilder types = new StringBuilder();
		// int max = checkMaxIndex();
		// types.append("SDnat = {"); //numero da msg
		// for (int i = 1; i <= max; i++) {
		// System.out.println("adicionou "+ i);
		// types.append(i + ",");
		// }
		// types.append(getReplyIndexes());
		// types.deleteCharAt(types.length()-1);
		// types.append("}\n");

		types.append("datatype COM = s | r\n");
		Set<IClass> blocks = new HashSet<IClass>();

		types.append("ID1 = {");
		for (ILifeline lifeline : seq1.getInteraction().getLifelines()) {
			types.append(getLifelineId(lifeline));
			types.append(",");
			blocks.add(lifeline.getBase());
		}
		types.deleteCharAt(types.length() - 1);
		types.append("}\n");
		types.append("ID2 = {");
		for (ILifeline lifeline : seq2.getInteraction().getLifelines()) {
			types.append(getLifelineId(lifeline));
			types.append(",");
			blocks.add(lifeline.getBase());
		}
		types.deleteCharAt(types.length() - 1);
		types.append("}\n");
		types.append("ID_SD = {<").append(seq1.getId()).append(">,<").append(seq2.getId()).append(">}\n");

		types.append(defineArguments());

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
				System.out.println("seq 1 adicionou " + msg.getIndex());
			}
		}
		for (IMessage msg : seq2.getInteraction().getMessages()) {
			if (msg.isSynchronous()) {
				sb.append(msg.getIndex()).append("r,");
			}
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	private String getLifelineId(ILifeline lifeline) {
		return lifeline.getId();// lifeline.getName() != null ?
		// lifeline.getName()+"_"+lifeline.getBase()+"_id":
		// lifeline.getBase()+"_id";
	}

	private int checkMaxIndex() {
		int idx1 = 0;
		int idx2 = 0;
		for (IMessage msg : seq1.getInteraction().getMessages()) {
			if (!msg.isReturnMessage() && !msg.isCreateMessage() && !msg.isDestroyMessage()) {
				idx1++;
			}
		}
		for (IMessage msg : seq2.getInteraction().getMessages()) {
			if (!msg.isReturnMessage() && !msg.isCreateMessage() && !msg.isDestroyMessage()) {
				idx2++;
			}
		}
		return Math.max(idx1, idx2);
	}

	private void defineBlockMessages(StringBuilder types, IClass block) {

		Set<IMessage> messages = getBlockMessages(block);
		StringBuilder operations = new StringBuilder();
		StringBuilder signals = new StringBuilder();
		StringBuilder getters = new StringBuilder();
		for (IMessage message : messages) {
			if (message.isAsynchronous()) {
				signals.append(message.getName());
				if (!"".equals(message.getArgument())) {
					treatArguments(signals, message.getArgument());
					treatGetterArguments(getters, message.getArgument());
				}
				signals.append(" | ");
			} else if (message.isSynchronous()) {
				operations.append(message.getName() + "_I");
				getters.append("get_id(").append(message.getName()).append("_I");
				if (!"".equals(message.getArgument())) {
					treatArguments(operations, message.getArgument());
					treatGetterArguments(getters, message.getArgument());
				}
				getters.append(") = ").append(message.getName()).append("_I\n");

				operations.append(" | ");
				operations.append(message.getName() + "_O");
				getters.append("get_id(").append(message.getName()).append("_O");
				if (!"".equals(message.getReturnValueVariable())) {
					treatArguments(operations, message.getReturnValueVariable());
					treatGetterArguments(getters, message.getArgument());
				}
				getters.append(") = ").append(message.getName()).append("_O\n");
				operations.append(" | ");
			}

		}
		if (!signals.toString().isEmpty()) {
			signals.delete(signals.length() - 3, signals.length());
			types.append("datatype ").append(block.getName());
			types.append("_SIG = ").append(signals.toString()).append("\n");
		}
		if (!operations.toString().isEmpty()) {
			operations.delete(operations.length() - 3, operations.length());
			types.append("datatype ").append(block.getName());
			types.append("_OPS = ").append(operations.toString()).append("\n");
		}

		types.append(getters.toString());
	}

	private Set<IMessage> getBlockMessages(IClass block) {

		Set<IMessage> messages = new HashSet<IMessage>();
		messages.addAll(Arrays.asList(seq1.getInteraction().getMessages()));
		messages.addAll(Arrays.asList(seq2.getInteraction().getMessages()));

		Set<IMessage> ret = new HashSet<IMessage>();

		for (IOperation operation : block.getOperations()) {
			for (IMessage iMessage : messages) {
				if (iMessage.getOperation() != null && iMessage.getOperation().getOwner() != null
						&& iMessage.getOperation().getOwner().equals(block)
						&& iMessage.getOperation().getName().equals(operation.getName())
						&& !existMessage(ret, iMessage)) {
					ret.add(iMessage);
				}
			}
		}
		return ret;
	}

	private boolean existMessage(Set<IMessage> ret, IMessage mes) {
		for (IMessage iMessage : ret) {
			if (iMessage.getOperation() != null && iMessage.getOperation().getOwner() != null
					&& iMessage.getOperation().getOwner() == mes.getOperation().getOwner()
					&& iMessage.getName().equals(mes.getName())) {
				return true;
			}
		}
		return false;
	}

	private void treatArguments(StringBuilder types, String argument) {
		StringBuilder aux = new StringBuilder();

		if (argument.contains(":")) {
			String[] arguments = argument.split(",");
			for (int i = 0; i < arguments.length; i++) {
				if (arguments[i].contains(":")) {
					String[] temp = arguments[i].split(":");
					aux.append(".My" + temp[1]);
				} else {
					if (isInteger(arguments[i])) {
						aux.append(".IntParams");
					} else if (isDouble(arguments[i])) {
						aux.append(".DoubleParams");
					} else if (isChar(arguments[i])) {
						aux.append(".CharParams");
					} else if (isString(arguments[i])) {
						aux.append(".StringParams");
					}
				}
			}
		}
		types.append(aux.toString());
	}

	private void treatGetterArguments(StringBuilder sb, String argument) {
		if (argument.contains(":")) {
			String[] arguments = argument.split(",");
			for (int i = 0; i < arguments.length; i++) {
				sb.append("._");
			}
		}
	}

	public String defineArguments() {

		Set<IMessage> messages = new HashSet<IMessage>();
		messages.addAll(Arrays.asList(seq1.getInteraction().getMessages()));
		messages.addAll(Arrays.asList(seq2.getInteraction().getMessages()));

		StringBuilder parametros = new StringBuilder();
		String integers = "IntParams = {";
		String doubles = "DoubleParams = {";
		String chars = "CharParams = {";
		String strings = "StringParams={";

		boolean hasInt = false;
		boolean hasDouble = false;
		boolean hasChar = false;
		boolean hasString = false;

		for (IMessage iMessage : messages) {
			String argument = iMessage.getArgument();
			if (argument.contains(":")) {
				String[] arguments = argument.split(",");
				for (int i = 0; i < arguments.length; i++) {
					if (arguments[i].contains(":")) {
						String[] aux = arguments[i].split(":");
						parametros.append("My" + aux[1] + " ={");
						if (aux[1].equals("Integer")) {
							parametros.append("0,1,2,3,4,5,6,7,8,9}\n");
						} else if (aux[1].equals("String")) {
							parametros.append("\"teste\"}\n");
						} else if (aux[1].equals("Double")) {
							parametros.append("1.0,2.0,3.4,4.1,5.4");
						}
					} else {
						if (isInteger(arguments[i])) {
							integers += arguments[i];
							hasInt = true;
						} else if (isDouble(arguments[i])) {
							doubles += arguments[i];
							hasDouble = true;
						} else if (isChar(arguments[i])) {
							chars += arguments[i];
							hasChar = true;
						} else if (isString(arguments[i])) {
							strings += arguments[i];
							hasString = true;
						} else if (!arguments[i].equals(""))
							System.out.println("ERRO");// jogar exception
					}
				}
			}
		}
		if (hasInt) {
			integers += "}\n";
			parametros.append(integers);
		}
		if (hasDouble) {
			doubles += "}\n";
			parametros.append(doubles);
		}
		if (hasChar) {
			chars += "}\n";
			parametros.append(chars);
		}
		if (hasString) {
			strings += "}\n";
			parametros.append(strings);
		}

		// System.out.println(parametros.toString());

		return parametros.toString();
	}

	private boolean isInteger(String param) {
		try {
			Integer.parseInt(param);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	private boolean isDouble(String param) {
		try {
			Double.parseDouble(param);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	private boolean isChar(String param) {
		if (param.contains("\'"))
			return true;
		else
			return false;
	}

	private boolean isString(String param) {
		if (param.contains("\""))
			return true;
		else
			return false;
	}

	public String parseChannels() {
		StringBuilder channelsSTR = new StringBuilder();
		channelsSTR.append("channel beginInteration,endInteraction\n");// ID_SD
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
				// channelsSTR.append("_mOP:
				// COM.SDNat.ID.ID.").append(block.getName());
				channelsSTR.append("_mOP: COM.ID.ID.").append(block.getName());
				channelsSTR.append("_OPS\n");
			}
			if (hasSignal) {
				channelsSTR.append("channel ").append(block.getName());
				// channelsSTR.append("_mSIG:
				// COM.SDNat.ID.ID.").append(block.getName());
				channelsSTR.append("_mSIG: COM.ID.ID.").append(block.getName());
				channelsSTR.append("_SIG\n");
			}
		}

		return channelsSTR.toString();
	}

	public String parseSD1() {
		StringBuilder process = new StringBuilder();

		// Generate processes for lifelines
		for (ILifeline lifeline : seq1.getInteraction().getLifelines()) {
			process.append(translateLifeline(lifeline));
		}
		// Generate processes for Messages
		for (IMessage iMessage : seq1.getInteraction().getMessages()) {
			process.append(MessageParser.getInstance().translateMessageForProcess(iMessage, seq1));
		}
		// Generate MessagesBuffer Process
		process.append(MessageParser.getInstance().translateMessagesBuffer(seq1)).append("\n");
		process.append(auxiliar());
		process.append("\n");
		process.append("SD(sd_id");
		
		for(int i = 1; i<= numLife;i++){
			process.append(",lf" + i + "_id");
		}
		process.append(") = beginInteraction ->((");
		process.append(paralel);
		process.append("; endinteraction -> SKIP)");
		process.append("[|{|");
		
		for (String alfa : alfabeto) {
			process.append(alfa + ",");
		}
		process.deleteCharAt(process.length() - 1);
		process.append("]|}|");
		process.append(MessageParser.getInstance().getMsgBuffer() + ")");
		
		
		
		return process.toString();
	}

	private String auxiliar() {
		StringBuilder aux = new StringBuilder();
		aux.append("AlphaParallel(sd_id");
		int i = 1;
		for (ILifeline lifelines : seq1.getInteraction().getLifelines()) {
			aux.append(",lf" + i + "_id");
			i++;
		}
		aux.append(")");
		paralel = aux.toString();
		aux.append(" = ");
		numLife = i-1;
		//i-1 = numero de lifelines
		
		StringBuilder sb = new StringBuilder();
		sb.append(alfabeto.get(0));
		
		for(int x = 2; x < i-1; x++){
			aux.append("(");
		}
		
		for(int j = 0; j < i-2;j++){
			
			if(j%2 == 0)
				aux.append(processos.get(j));
			else
				aux.append(")");
			
			aux.append("[ {|");
			aux.append(sb.toString());
			aux.append("|} || {|");
			aux.append(alfabeto.get(j+1));
			sb.append(", "+ alfabeto.get(j+1));
			aux.append("|} ]");
			aux.append(processos.get(j+1));
		}
		
		return aux.toString();
	}

	private String translateLifeline(ILifeline lifeline) {
		StringBuilder process = new StringBuilder();
		StringBuilder aux = new StringBuilder();
		process.append(seq1.getName()).append("_").append(lifeline.getBase());
		aux.append(seq1.getName()).append("_").append(lifeline.getBase());
		process.append("(sd_id");
		aux.append("(sd_id");

		int i = 1;
		for (ILifeline lifelines : seq1.getInteraction().getLifelines()) {
			process.append(",lf" + i + "_id");
			aux.append(",lf" + i + "_id");
			i++;
		}

		process.append(") =");
		aux.append(")");

		for (INamedElement fragment : lifeline.getFragments()) {
			process.append(translateFragment(fragment, lifeline, seq1));
		}

		process.deleteCharAt(process.length() - 1);
		process.append("\n");
		processos.add(aux.toString());
		return process.toString();
	}

	private String translateFragment(INamedElement fragment, ILifeline lifeline, ISequenceDiagram seq) {
		if (fragment instanceof IMessage) {
			return "(" + MessageParser.getInstance().translateMessageForLifeline((IMessage) fragment, lifeline, seq)
					+ ");";
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
