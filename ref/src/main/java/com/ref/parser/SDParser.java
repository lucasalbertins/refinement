package com.ref.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.ICombinedFragment;
import com.change_vision.jude.api.inf.model.IInteractionUse;
import com.change_vision.jude.api.inf.model.ILifeline;
import com.change_vision.jude.api.inf.model.IMessage;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.ISequenceDiagram;
import com.change_vision.jude.api.inf.model.IStateInvariant;

public class SDParser {

	private ISequenceDiagram seq1;
	private ISequenceDiagram seq2;
	private static List<String> processos;
	private static List<String> alfabeto;
	private String paralel;
	private int numLife;

	// LISTAS AUXILIARES PARA EVITAR REPETICAO
	private List<String> channels;
	private List<String> datatypes;
	private List<String> getters;
	private List<String> op;
	private List<String> sig;
	private List<String> mensagens;
	private static Map<String, String> lifelines;
	private static Map<String, String> lifelines2;
	public static Map<String, String> alfabets;
	public static List<String> procs;
	
	public List<String> alfabetosd1;
	public List<String> alfabetosd2;

	public SDParser(ISequenceDiagram seq1, ISequenceDiagram seq2) {
		this.seq1 = seq1;
		this.seq2 = seq2;
		processos = new ArrayList<String>();
		alfabeto = new ArrayList<String>();
		channels = new ArrayList<String>();
		datatypes = new ArrayList<String>();
		getters = new ArrayList<String>();
		op = new ArrayList<String>();
		sig = new ArrayList<String>();
		mensagens = new ArrayList<String>();
		lifelines = new HashMap<String, String>();
		lifelines2 = new HashMap<String, String>();
		alfabets = new HashMap<String, String>();
		procs = new ArrayList<String>(); {
		};
		// alfabetosd1 = new ArrayList<String>();
		// alfabetosd2 = new ArrayList<String>();
	}

	public static String getNome(String key) {
		return lifelines.get(key);
	}

	public static String getNome2(String key) {
		return lifelines2.get(key);
	}

	public static void addProcesso(String elem) {
		processos.add(elem);
	}

	public static void addAlfabeto(String elem) {
		alfabeto.add(elem);
	}

	public void carregaLifelines() {

		int aux = 1;
		for (ILifeline lifeline : seq1.getInteraction().getLifelines()) {
			lifelines.put(getLifelineBase(lifeline), "lf" + aux + "_id");
			lifelines2.put(getLifelineBase(lifeline), "lf" + aux + "id");
			aux++;
		}

		for (ILifeline lifeline : seq2.getInteraction().getLifelines()) {
			if (!lifelines.containsKey(getLifelineBase(lifeline))) {
				lifelines.put(getLifelineBase(lifeline), "lf" + aux + "_id");
				lifelines2.put(getLifelineBase(lifeline), "lf" + aux + "id");
				aux++;
			}
		}

	}

	public String parseSDs() throws InvalidEditingException {
		StringBuilder process = new StringBuilder();
		process.append(defineTypes());
		process.append(parseChannels());
		process.append(parseSD(seq1));
		process.append(parseSD(seq2));
		return process.toString();
	}

	public String defineTypes() throws InvalidEditingException {
		StringBuilder types = new StringBuilder();

		types.append("datatype COM = s | r\n");
		List<IClass> blocks = new LinkedList<IClass>();

		types.append("datatype ID = ");

		List<String> added = new ArrayList<String>();

		for (ILifeline lifeline : seq1.getInteraction().getLifelines()) {
			types.append(lifelines2.get(getLifelineBase(lifeline)));
			added.add(lifelines2.get(getLifelineBase(lifeline)));
			types.append("|");
			blocks.add(lifeline.getBase());
		}
		types.deleteCharAt(types.length() - 1);
		for (ILifeline lifeline : seq2.getInteraction().getLifelines()) {
			if (!added.contains(lifelines2.get(getLifelineBase(lifeline)))) {
				types.append(lifelines2.get(getLifelineBase(lifeline)));
				types.append("|");
				blocks.add(lifeline.getBase());
			}
		}

		// types.deleteCharAt(types.length() - 1);
		types.append("\n");
		types.append("datatype ID_SD = ").append("sd1id").append("|").append("sd2id").append("\n");

		types.append(defineArguments());

		StringBuilder typesAux = new StringBuilder();
		for (IClass block : blocks) {
			defineBlockMessages(typesAux, block);
		}

		types.append("datatype MSG = ");
		for (String msg : mensagens) {
			types.append(msg).append("|");
		}
		types.deleteCharAt(types.length() - 1);
		types.append("\n");
		types.append(typesAux.toString());
		for (String get : getters) {
			types.append(get);
		}

		return types.toString();
	}

	// private String getReplyIndexes() {
	// StringBuilder sb = new StringBuilder();
	// for (IMessage msg : seq1.getInteraction().getMessages()) {
	// if (msg.isSynchronous()) {
	// sb.append(msg.getIndex()).append("r,");
	// // System.out.println("seq 1 adicionou " + msg.getIndex());
	// }
	// }
	// for (IMessage msg : seq2.getInteraction().getMessages()) {
	// if (msg.isSynchronous()) {
	// sb.append(msg.getIndex()).append("r,");
	// }
	// }
	// sb.deleteCharAt(sb.length() - 1);
	// return sb.toString();
	// }

	// private String getLifelineId(ILifeline lifeline) {
	// return lifeline.getId();// lifeline.getName() != null ?
	// // lifeline.getName()+"_"+lifeline.getBase()+"_id":
	// // lifeline.getBase()+"_id";
	// }

	private String getLifelineBase(ILifeline lifeline) {
		return lifeline.getBase().toString();
	}

	// private int checkMaxIndex() {
	// int idx1 = 0;
	// int idx2 = 0;
	// for (IMessage msg : seq1.getInteraction().getMessages()) {
	// if (!msg.isReturnMessage() && !msg.isCreateMessage() &&
	// !msg.isDestroyMessage()) {
	// idx1++;
	// }
	// }
	// for (IMessage msg : seq2.getInteraction().getMessages()) {
	// if (!msg.isReturnMessage() && !msg.isCreateMessage() &&
	// !msg.isDestroyMessage()) {
	// idx2++;
	// }
	// }
	// return Math.max(idx1, idx2);
	// }

	private void defineBlockMessages(StringBuilder types, IClass block) {

		List<IMessage> messages = getBlockMessages(block);
		StringBuilder auxiliar = new StringBuilder();
		StringBuilder operationsAux = new StringBuilder();
		StringBuilder gettersAux;
		StringBuilder signalsAux = new StringBuilder();

		StringBuilder finalGetters = new StringBuilder();
		StringBuilder operations = new StringBuilder();
		StringBuilder signals = new StringBuilder();

		for (IMessage message : messages) {

			gettersAux = new StringBuilder();
			operationsAux = new StringBuilder();
			signalsAux = new StringBuilder();

			if (message.isAsynchronous()) {
				signalsAux.append(message.getName());
				gettersAux.append("get_id(").append(message.getName());
				if (!"".equals(message.getArgument())) {
					// treatArguments(signalsAux, message.getArgument());
					// treatGetterArguments(gettersAux, message.getArgument());
				}
				gettersAux.append(") = ").append(message.getName()).append("\n");
				if (!sig.contains(signalsAux.toString())) {
					mensagens.add(signalsAux.toString());
					signals.append(signalsAux.toString());
					sig.add(signalsAux.toString());
					signals.append(" | ");
				}

			} else if (message.isSynchronous()) {
				operationsAux.append(message.getName() + "_I");
				gettersAux.append("get_id(").append(message.getName()).append("_I");
				if (!"".equals(message.getArgument())) {
					// treatArguments(operationsAux, message.getArgument());
					// treatGetterArguments(gettersAux, message.getArgument());
				}
				gettersAux.append(") = ").append(message.getName()).append("_I\n");

				if (!op.contains(operationsAux.toString())) {
					mensagens.add(operationsAux.toString());
					operations.append(operationsAux.toString());
					op.add(operationsAux.toString());
					operations.append(" | ");
				}

				operationsAux = new StringBuilder();

				operationsAux.append(message.getName() + "_O");
				gettersAux.append("get_id(").append(message.getName()).append("_O");
				if (!"".equals(message.getReturnValueVariable())) {
					// treatArguments(operationsAux,
					// message.getReturnValueVariable());
					// treatGetterArguments(gettersAux, message.getArgument());
				}
				gettersAux.append(") = ").append(message.getName()).append("_O\n");

				if (!op.contains(operationsAux.toString())) {
					mensagens.add(operationsAux.toString());
					operations.append(operationsAux.toString());
					op.add(operationsAux.toString());
					operations.append(" | ");
				}

			}
			if (!this.getters.contains(gettersAux.toString())) {
				finalGetters.append(gettersAux.toString());
				getters.add(gettersAux.toString());
			}

		}

		if (!signals.toString().isEmpty()) {
			signals.delete(signals.length() - 3, signals.length());
			auxiliar.append("subtype ").append(block.getName());
			auxiliar.append("_SIG = ").append(signals.toString()).append("\n");
			if (!datatypes.contains(auxiliar.toString())) {
				types.append(auxiliar.toString());
				datatypes.add(auxiliar.toString());
			}
		}
		if (!operations.toString().isEmpty()) {
			auxiliar = new StringBuilder();
			operations.delete(operations.length() - 3, operations.length());
			auxiliar.append("subtype ").append(block.getName());
			auxiliar.append("_OPS = ").append(operations.toString()).append("\n");
			if (!datatypes.contains(auxiliar.toString())) {
				types.append(auxiliar.toString());
				datatypes.add(auxiliar.toString());
			}
		}

		// types.append(finalGetters.toString());
	}

	private List<IMessage> getBlockMessages(IClass block) {

		List<IMessage> messages = new ArrayList<IMessage>();
		messages.addAll(Arrays.asList(seq1.getInteraction().getMessages()));
		messages.addAll(Arrays.asList(seq2.getInteraction().getMessages()));

		List<IMessage> ret = new ArrayList<IMessage>();

		for (IMessage msg : seq1.getInteraction().getMessages()) {
			ILifeline life = (ILifeline) msg.getTarget();
			if (!msg.isReturnMessage() && life.getBase().equals(block) && !existMessage(ret, msg)) {
				ret.add(msg);
			}
		}

		for (IMessage msg : seq2.getInteraction().getMessages()) {
			ILifeline life = (ILifeline) msg.getTarget();
			if (!msg.isReturnMessage() && life.getBase().equals(block) && !ret.contains(msg)) {
				ret.add(msg);
			}
		}

		// for (IOperation operation : block.getOperations()) {
		// for (IMessage iMessage : messages) {
		// // System.out.println("MSG " + iMessage.getName());
		// if (iMessage.getOperation() != null &&
		// iMessage.getOperation().getOwner() != null
		// && iMessage.getOperation().getOwner().equals(block)
		// && iMessage.getOperation().getName().equals(operation.getName())
		// && !existMessage(ret, iMessage)) {
		// //System.out.println("Adicionou " + iMessage.getName());
		// ret.add(iMessage);
		// }
		// }
		// }
		return ret;
	}

	private boolean existMessage(List<IMessage> ret, IMessage mes) {
		for (IMessage iMessage : ret) {
			if (iMessage.getOperation() != null && iMessage.getOperation().getOwner() != null
					&& iMessage.getOperation().getOwner() == mes.getOperation().getOwner()
					&& iMessage.getName().equals(mes.getName()) && iMessage.getArgument().equals(mes.getArgument())) {
				return true;
			}
		}
		return false;
	}

	// private void treatArguments(StringBuilder types, String argument) {
	// StringBuilder aux = new StringBuilder();
	//
	// if (argument.contains(":")) {
	// String[] arguments = argument.split(",");
	// for (int i = 0; i < arguments.length; i++) {
	// if (arguments[i].contains(":")) {
	// String[] temp = arguments[i].split(":");
	// aux.append(".My" + temp[1]);
	// } else {
	// if (isInteger(arguments[i])) {
	// aux.append(".IntParams");
	// } else if (isDouble(arguments[i])) {
	// aux.append(".DoubleParams");
	// } else if (isChar(arguments[i])) {
	// aux.append(".CharParams");
	// } else if (isString(arguments[i])) {
	// aux.append(".StringParams");
	// }
	// }
	// }
	// }
	// types.append(aux.toString());
	// }

	// private void treatGetterArguments(StringBuilder sb, String argument) {
	// if (argument.contains(":")) {
	// String[] arguments = argument.split(",");
	// for (int i = 0; i < arguments.length; i++) {
	// sb.append("._");
	// }
	// }
	// }

	public String defineArguments() {

		List<IMessage> messages = new ArrayList<IMessage>();
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
		StringBuilder auxChannel = new StringBuilder();
		channelsSTR.append("channel beginInteraction,endInteraction\n");// ID_SD
		List<IClass> blocks = new ArrayList<IClass>();
		for (ILifeline lifeline : seq1.getInteraction().getLifelines()) {
			blocks.add(lifeline.getBase());
		}
		for (ILifeline lifeline : seq2.getInteraction().getLifelines()) {
			blocks.add(lifeline.getBase());
		}

		for (IClass block : blocks) {
			auxChannel = new StringBuilder();
			List<IMessage> blockMessages = getBlockMessages(block);
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
				auxChannel.append("channel ").append(block.getName());
				auxChannel.append("_mOP: COM.ID.ID.").append(block.getName());
				auxChannel.append("_OPS\n");
			}
			if (hasSignal) {
				auxChannel.append("channel ").append(block.getName());
				auxChannel.append("_mSIG: COM.ID.ID.").append(block.getName());
				auxChannel.append("_SIG\n");
			}
			if (!channels.contains(auxChannel.toString())) {
				channelsSTR.append(auxChannel.toString());
				channels.add(auxChannel.toString());
			}
		}

		return channelsSTR.toString();
	}

	public String parseSD(ISequenceDiagram seq) {
		StringBuilder process = new StringBuilder();

		// Generate processes for lifelines
		for (ILifeline lifeline : seq.getInteraction().getLifelines()) {
			process.append(translateLifeline(lifeline, seq));
		}
		// Generate processes for Messages
		for (IMessage iMessage : seq.getInteraction().getMessages()) {
			process.append(MessageParser.getInstance().translateMessageForProcess(iMessage, seq));
		}
		// Generate MessagesBuffer Process
		process.append(MessageParser.getInstance().translateMessagesBuffer(seq)).append("\n");
		process.append(auxiliar(seq));
		process.append("\n");
		process.append("SD_" + seq.getName() + "(sd_id");

		for (int i = 1; i <= numLife; i++) {
			process.append(",lf" + i + "_id");
		}
		process.append(") = beginInteraction ->((");
		process.append(paralel);
		process.append("; endInteraction -> SKIP)");
		process.append("[|{|");

		for (String alfa : alfabeto) {
			process.append(alfa + ",");
		}
		process.deleteCharAt(process.length() - 1);
		process.append(",endInteraction");
		process.append("|}|]");
		process.append(MessageParser.getInstance().getMsgBuffer() + ")");

		if (seq.equals(seq1)) {
			alfabetosd1 = new ArrayList<String>(alfabeto);
		} else {
			alfabetosd2 = new ArrayList<String>(alfabeto);
		}

		alfabeto.clear();
		;
		paralel = "";

		return process.toString();
	}

	private String auxiliar(ISequenceDiagram seq) {
		StringBuilder aux = new StringBuilder();
		aux.append(seq.getName()).append("Parallel(sd_id");
		List<String> bases = new ArrayList<String>();
		int i = 1;
		for (ILifeline lifeline : seq.getInteraction().getLifelines()) {
			aux.append(",");
			aux.append(lifelines.get(getLifelineBase(lifeline)));
			i++;
			bases.add(lifeline.getBase().toString());
		}
		aux.append(")");
		paralel = aux.toString();
		aux.append(" = ");
		numLife = i - 1;
		// i-1 = numero de lifelines

		StringBuilder sb = new StringBuilder();
		// sb.append(alfabeto.get(0));
		sb.append(alfabets.get(bases.get(0)));

		for (int x = 2; x < i - 1; x++) {
			aux.append("(");
		}

		for (int j = 0; j < i - 2; j++) {

			if (j % 2 == 0)
				aux.append(processos.get(j));
			else
				aux.append(")");

			aux.append("[ {|");
			aux.append(sb.toString());
			// aux.append(alfabeto.get(j));
			// sb.append(", " + alfabets.get(bases.get(j)));
			aux.append("|} || {|");
			aux.append(alfabets.get(bases.get(j + 1)));
			sb.append(", " + alfabets.get(bases.get(j + 1)));
			aux.append("|} ]");
			aux.append(processos.get(j + 1));
		}
		processos.clear();
		alfabets.clear();

		return aux.toString();
	}

	private String translateLifeline(ILifeline lifeline, ISequenceDiagram seq) {
		StringBuilder process = new StringBuilder();
		StringBuilder aux = new StringBuilder();
		process.append(seq.getName()).append("_").append(lifeline.getBase());
		aux.append(seq.getName()).append("_").append(lifeline.getBase());
		process.append("(sd_id");
		aux.append("(sd_id");

		List<String> lfs = new ArrayList<String>();

		for (INamedElement fragment : lifeline.getFragments()) {
			IMessage msg = (IMessage) fragment;
			ILifeline life1 = (ILifeline) msg.getSource();
			ILifeline life2 = (ILifeline) msg.getTarget();
			if (!lfs.contains(lifelines.get(life1.getBase().toString())))
				lfs.add(lifelines.get(life1.getBase().toString()));
			if (!lfs.contains(lifelines.get(life2.getBase().toString())))
				lfs.add(lifelines.get(life2.getBase().toString()));
		}

		for (String life : lfs) {
			process.append(",").append(life);
			aux.append(",").append(life);
		}

		process.append(") =");
		aux.append(")");

		for (INamedElement fragment : lifeline.getFragments()) {
			process.append(translateFragment(fragment, lifeline, seq));
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

	public String refinementAssertion() {

		StringBuilder sb = new StringBuilder();
		sb.append("assert ");
		sb.append("SD_" + seq1.getName());
		sb.append("(sd1id");
		for (ILifeline lifeline : seq1.getInteraction().getLifelines()) {
			sb.append("," + lifelines2.get(getLifelineBase(lifeline)));
		}
		sb.append(") [T= ");
		sb.append("SD_" + seq2.getName());
		sb.append("(sd2id");
		for (ILifeline lifeline : seq2.getInteraction().getLifelines()) {
			sb.append("," + lifelines2.get(getLifelineBase(lifeline)));
		}
		sb.append(")");
		sb.append("\\{|" + eventosDiferentes() + "|}\n");
		return sb.toString();
	}

	private String eventosDiferentes() {
		StringBuilder sb = new StringBuilder();

		for (String evento : alfabetosd2) {
			if (!alfabetosd1.contains(evento)) {
				sb.append(evento).append(",");
			}
		}
		
		ArrayList<String> elementos1 = new ArrayList<String>();
		for (Map.Entry<String, String> entry : lifelines.entrySet()) {
			//System.out.println(entry.getKey() + "/" + entry.getValue());
			elementos1.add(entry.getValue());
		}

		ArrayList<String> elementos2 = new ArrayList<String>();
		for (Map.Entry<String, String> entry : lifelines2.entrySet()) {
			//System.out.println(entry.getKey() + "/" + entry.getValue());
			elementos2.add(entry.getValue());
		}

		
		String resultado = sb.deleteCharAt(sb.length() - 1).toString();

		for(int i = 0; i < elementos1.size();i++){
			resultado = resultado.replaceAll(elementos1.get(i), elementos2.get(i));			
		}

		return resultado;
	}

}
