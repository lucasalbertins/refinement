package com.ref.parser;

import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.ILifeline;
import com.change_vision.jude.api.inf.model.IMessage;
import com.change_vision.jude.api.inf.model.ISequenceDiagram;

import java.util.*;

public class SDdataTypeParser {

    private ISequenceDiagram seq1;
    private ISequenceDiagram seq2;
    private Map<String,String> lfIdMap;
    private List<String> msgs;
    private List<String> getters;
    private List<String> op;
    private List<String> sig;

    public SDdataTypeParser(ISequenceDiagram seq1, ISequenceDiagram seq2, Map<String, String> lfIdMap) {
        this.seq1 = seq1;
        this.seq2 = seq2;
        this.lfIdMap = lfIdMap;
        this.msgs = new ArrayList<>();
        this.getters = new ArrayList<>();
        this.op = new ArrayList<>();
        this.sig = new ArrayList<>();
    }

    public String defineTypes(){
        StringBuilder types = new StringBuilder();

        types.append("datatype COM = s | r\n");
        List<IClass> blocks = new LinkedList<>();

        types.append("datatype ID = ");

        List<String> added = new ArrayList<>();

        defineLifelineID(types, seq1,blocks, added);
        types.deleteCharAt(types.length() - 1);
        defineLifelineID(types, seq2,blocks, added);

        // types.deleteCharAt(types.length() - 1);
        types.append("\n");
        types.append("datatype ID_SD = ").append("sd1id").append("|").append("sd2id").append("\n");

        types.append(defineArguments());

        StringBuilder typesAux = new StringBuilder();
        for (IClass block : blocks) {
            defineBlockMessages(typesAux, block);
        }

        types.append("datatype MSG = ");
        for (String msg : this.msgs) {
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

    private void defineLifelineID(StringBuilder types, ISequenceDiagram seq,List<IClass> blocks, List<String> added) {
        for (ILifeline lifeline : seq.getInteraction().getLifelines()) {
            if (!added.contains(lfIdMap.get(lifeline.getBase().toString()))) {
                types.append(this.lfIdMap.get(lifeline.getBase().toString()));
                added.add(this.lfIdMap.get(lifeline.getBase().toString()));
                types.append("|");
                blocks.add(lifeline.getBase());
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


    private void defineBlockMessages(StringBuilder types, IClass block) {

        List<IMessage> messages = ParserUtilities.getInstance().getBlockMessages(seq1,seq2,block);
        List<String> datatypes = new ArrayList<>();
        StringBuilder auxiliar = new StringBuilder();
        StringBuilder operationsAux;
        StringBuilder gettersAux;
        StringBuilder signalsAux;

        //StringBuilder finalGetters = new StringBuilder();
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
                    addMessages(signalsAux, signals, sig);
                }

            } else if (message.isSynchronous()) {
                operationsAux.append(message.getName()).append("_I");
                gettersAux.append("get_id(").append(message.getName()).append("_I");
                if (!"".equals(message.getArgument())) {
                    // treatArguments(operationsAux, message.getArgument());
                    // treatGetterArguments(gettersAux, message.getArgument());
                }
                gettersAux.append(") = ").append(message.getName()).append("_I\n");

                if (!op.contains(operationsAux.toString())) {
                    addMessages(operationsAux, operations, op);
                }

                operationsAux = new StringBuilder();

                operationsAux.append(message.getName()).append("_O");
                gettersAux.append("get_id(").append(message.getName()).append("_O");
                if (!"".equals(message.getReturnValueVariable())) {
                    // treatArguments(operationsAux,
                    // message.getReturnValueVariable());
                    // treatGetterArguments(gettersAux, message.getArgument());
                }
                gettersAux.append(") = ").append(message.getName()).append("_O\n");

                if (!op.contains(operationsAux.toString())) {
                    addMessages(operationsAux, operations, op);
                }

            }
            if (!this.getters.contains(gettersAux.toString())) {
                //finalGetters.append(gettersAux.toString());
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

    private void addMessages(StringBuilder msgAux, StringBuilder signals, List<String> sig) {
        this.msgs.add(msgAux.toString());
        signals.append(msgAux.toString());
        sig.add(msgAux.toString());
        signals.append(" | ");
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
