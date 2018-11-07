package com.ref.parser;

import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.ILifeline;
import com.change_vision.jude.api.inf.model.IMessage;
import com.change_vision.jude.api.inf.model.ISequenceDiagram;

import java.util.*;

public class SDdataTypeParser {

    private ISequenceDiagram seq1;
    private ISequenceDiagram seq2;
    private Map<String, String> lfIdMap;
    private List<String> msgs;
    private List<String> getters;

    public SDdataTypeParser(ISequenceDiagram seq1, ISequenceDiagram seq2, Map<String, String> lfIdMap) {
        this.seq1 = seq1;
        this.seq2 = seq2;
        this.lfIdMap = new HashMap<>(lfIdMap);
        this.msgs = new ArrayList<>();
        this.getters = new ArrayList<>();
    }

    public String defineTypes() {
        StringBuilder types = new StringBuilder();

        types.append("datatype COM = s | r\n");
        List<IClass> blocks = new LinkedList<>();

        types.append("datatype ID = ");

        defineLifelineID(types, seq1, blocks);
        types.deleteCharAt(types.length() - 1);
        defineLifelineID(types, seq2, blocks);

        types.append("\n");
        types.append("datatype ID_SD = ").append("sd1id").append("|").append("sd2id").append("\n");

        // This is not being used atm
        //types.append(defineArguments());

        StringBuilder blockMessages = new StringBuilder();
        for (IClass block : blocks) {
            blockMessages.append(defineBlockMessages(block));
        }

        types.append("datatype MSG = ");
        for (String msg : this.msgs) {
            types.append(msg).append("|");
        }
        types.deleteCharAt(types.length() - 1);
        types.append("\n");

        types.append(blockMessages.toString());

        for (String get : getters) {
            types.append(get);
        }

        return types.toString();
    }

    private void defineLifelineID(StringBuilder types, ISequenceDiagram seq, List<IClass> blocks) {
        for (ILifeline lifeline : seq.getInteraction().getLifelines()) {
            String base = lifeline.getBase().toString();
            if (this.lfIdMap.containsKey(base)) {
                types.append(this.lfIdMap.get(base).replace("_id","id"));
                types.append("|");
                blocks.add(lifeline.getBase());
                this.lfIdMap.remove(base);
            }
        }
    }

    public String defineArguments() {

        List<IMessage> messages = new ArrayList<>();
        messages.addAll(Arrays.asList(seq1.getInteraction().getMessages()));
        messages.addAll(Arrays.asList(seq2.getInteraction().getMessages()));

        StringBuilder parametros = new StringBuilder();
        StringBuilder integers = new StringBuilder("IntParams = {");
        StringBuilder doubles = new StringBuilder("DoubleParams = {");
        StringBuilder chars = new StringBuilder("CharParams = {");
        StringBuilder strings = new StringBuilder("StringParams={");

        boolean hasInt = false;
        boolean hasDouble = false;
        boolean hasChar = false;
        boolean hasString = false;

        for (IMessage iMessage : messages) {
            String argument = iMessage.getArgument();
            if (argument.contains(":")) {
                String[] arguments = argument.split(",");
                for (String argument1 : arguments) {
                    if (argument1.contains(":")) {
                        String[] aux = argument1.split(":");
                        parametros.append("My").append(aux[1]).append(" ={");
                        switch (aux[1]) {
                            case "Integer":
                                parametros.append("0,1,2,3,4,5,6,7,8,9}\n");
                                break;
                            case "String":
                                parametros.append("\"teste\"}\n");
                                break;
                            case "Double":
                                parametros.append("1.0,2.0,3.4,4.1,5.4");
                                break;
                        }
                    } else {
                        if (isInteger(argument1)) {
                            integers.append(argument1);
                            hasInt = true;
                        } else if (isDouble(argument1)) {
                            doubles.append(argument1);
                            hasDouble = true;
                        } else if (isChar(argument1)) {
                            chars.append(argument1);
                            hasChar = true;
                        } else if (isString(argument1)) {
                            strings.append(argument1);
                            hasString = true;
                        } else if (!argument1.equals(""))
                            System.out.println("ERRO");// jogar exception
                    }
                }
            }
        }
        if (hasInt) {
            integers.append("}\n");
            parametros.append(integers);
        }
        if (hasDouble) {
            doubles.append("}\n");
            parametros.append(doubles);
        }
        if (hasChar) {
            chars.append("}\n");
            parametros.append(chars);
        }
        if (hasString) {
            strings.append("}\n");
            parametros.append(strings);
        }

        // System.out.println(parametros.toString());

        return parametros.toString();
    }

    private String defineBlockMessages(IClass block) {

        StringBuilder result = new StringBuilder();
        List<IMessage> messages = ParserUtilities.getInstance().getBlockMessages(seq1, seq2, block);
        StringBuilder gettersAux;

        // This is for sync messages
        StringBuilder operations = new StringBuilder();

        // This is for async messages
        StringBuilder signals = new StringBuilder();

        for (IMessage message : messages) {
            gettersAux = new StringBuilder();

            if (message.isAsynchronous())
                signals.append(addMessage(message.getName(), gettersAux, false));
            else
                operations.append(addMessage(message.getName(), gettersAux, true));

            if (!this.getters.contains(gettersAux.toString())) {
                getters.add(gettersAux.toString());
            }
        }

        if (!signals.toString().isEmpty()) {
            signals.delete(signals.length() - 3, signals.length());
            result.append("subtype ").append(block.getName());
            result.append("_SIG = ").append(signals.toString()).append("\n");
        }
        if (!operations.toString().isEmpty()) {
            operations.delete(operations.length() - 3, operations.length());
            result.append("subtype ").append(block.getName());
            result.append("_OPS = ").append(operations.toString()).append("\n");
        }

        return result.toString();
    }

    private String addMessage(String message, StringBuilder getter, boolean isSync) {
        if (isSync) message += "_I";
        StringBuilder result = new StringBuilder();
        getter.append("get_id(").append(message);

//                if (!"".equals(message.getArgument())) { // This is not being used atm (params)
//                    // treatArguments(signalsAux, message.getArgument());
//                    // treatGetterArguments(gettersAux, message.getArgument());
//                }

        getter.append(") = ").append(message);
        getter.append("\n");
        if (!msgs.contains(message)) {
            this.msgs.add(message);
            result.append(message);
            result.append(" | ");

            if (isSync) {
                String returnMessage = message.replace("_I", "_O");
                this.msgs.add(returnMessage);
                getter.append("get_id(").append(returnMessage);
                getter.append(") = ").append(returnMessage).append("\n");
                result.append(returnMessage);
                result.append(" | ");
            }

        }

        return result.toString();
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
        return param.contains("\'");
    }

    private boolean isString(String param) {
        return param.contains("\"");
    }


}
