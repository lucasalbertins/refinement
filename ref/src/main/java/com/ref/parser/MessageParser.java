package com.ref.parser;

import java.util.*;

import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.ILifeline;
import com.change_vision.jude.api.inf.model.IMessage;
import com.change_vision.jude.api.inf.model.ISequenceDiagram;

public class MessageParser {

    private Map<String, String> lfsWithUnderscore;
    private List<String> msgProcesses;
    private List<String> refinementAlphabet;
    private Map<String, String> alphabetMap;

    public Map<String, String> getAlphabetMap() {
        return alphabetMap;
    }

    public List<String> getRefinementAlphabet() {
        return refinementAlphabet;
    }

    public MessageParser(Map<String, String> lfsWithUnderscore) {
        this.lfsWithUnderscore = lfsWithUnderscore;
        refinementAlphabet = new ArrayList<>();
        alphabetMap = new HashMap<>();
        msgProcesses = new ArrayList<>();
    }

    public String translateMessageForProcess(IMessage msg, ISequenceDiagram seq) {
        String base1 = ((ILifeline) msg.getSource()).getBase().toString();
        String base2 = ((ILifeline) msg.getTarget()).getBase().toString();

        StringBuilder sb = new StringBuilder();

        if (msg.isSynchronous()) {
            sb.append(parseMsg(msg, seq, base1, base2, true));
        } else if (msg.isAsynchronous() && !msg.isReturnMessage()) {
            sb.append(parseMsg(msg, seq, base1, base2, false));
        } else if (msg.isReturnMessage()) {
            sb.append(parseReturnMsg(msg, seq, base1, base2));
        }

        return sb.toString();
    }

    private String parseMsg(IMessage msg, ISequenceDiagram seq, String base1, String base2, boolean isSync) {
        StringBuilder aux = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        sb.append(seq.getName()).append("_");
        sb.append(ParserUtilities.getInstance().addInstancesAndBases(msg));
        sb.append("_").append(msg.getName());

        if (msgProcesses.contains(sb.toString())) {
            return "";
        } else
            msgProcesses.add(sb.toString());

        sb.append("(sd_id");
        sb.append(",").append(lfsWithUnderscore.get(base1));
        sb.append(",").append(lfsWithUnderscore.get(base2));
        sb.append(") = ");


        if (isSync) {
            sb.append(((ILifeline) msg.getTarget()).getBase()).append("_mOP.s");
            aux.append(((ILifeline) msg.getTarget()).getBase()).append("_mOP.s");
        } else {
            sb.append(((ILifeline) msg.getTarget()).getBase()).append("_mSIG.s");
            aux.append(((ILifeline) msg.getTarget()).getBase()).append("_mSIG.s");
        }

        addIDS(base1, base2, sb, aux);
        //sb.append("!"+msg.getName()+"_I");
        sb.append("?x:{x | x<-");

        if (isSync) {
            sb.append(((ILifeline) msg.getTarget()).getBase()).append("_OPS");
            sb.append(",get_id(x) == ").append(msg.getName()).append("_I}");
            aux.append(".").append(msg.getName()).append("_I");
        } else {
            sb.append(((ILifeline) msg.getTarget()).getBase()).append("_SIG");
            sb.append(",get_id(x) == ").append(msg.getName()).append("}");
            sb.append(" -> ");
            aux.append(".").append(msg.getName());
        }

        refinementAlphabet.add(aux.toString());
        //SDParser.addToRefinementAlphabet(aux.toString());

        addToAlphabetMap(base1, aux);

        aux = new StringBuilder();

        if (isSync) {
            sb.append(" -> ");
            sb.append(((ILifeline) msg.getTarget()).getBase()).append("_mOP.r");
            aux.append(((ILifeline) msg.getTarget()).getBase()).append("_mOP.r");
        } else {
            sb.append(((ILifeline) msg.getTarget()).getBase()).append("_mSIG.r");
            aux.append(((ILifeline) msg.getTarget()).getBase()).append("_mSIG.r");
        }

        addIDS(base1, base2, sb, aux);
        //sb.append("!"+msg.getName()+"_I");
        sb.append("!x -> ").append(seq.getName()).append("_");
        sb.append(ParserUtilities.getInstance().addInstancesAndBases(msg));
        sb.append("_").append(msg.getName());
        sb.append("(sd_id");
        sb.append(",").append(lfsWithUnderscore.get(base1));
        sb.append(",").append(lfsWithUnderscore.get(base2));
        sb.append(")");
        aux.append(".").append(msg.getName());
        if (isSync) {
            aux.append("_I");
        }

        refinementAlphabet.add(aux.toString());
        addToAlphabetMap(base2, aux);

        return sb.append("\n").toString();
    }

    private String parseReturnMsg(IMessage msg, ISequenceDiagram seq, String base1, String base2) {
        StringBuilder sb = new StringBuilder();
        StringBuilder aux = new StringBuilder();
        IMessage syncMsg = null;
        try {
            syncMsg = ParserUtilities.getInstance().getPreviousMessage(msg, seq);
        } catch (InvalidUsingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        sb.append(seq.getName()).append("_");
        if (!(Objects.requireNonNull(syncMsg).getTarget()).getName().equals(""))
            sb.append((syncMsg.getTarget()).getName()).append("_");

        sb.append(((ILifeline) syncMsg.getTarget()).getBase()).append("_");

        if (!(syncMsg.getSource()).getName().equals(""))
            sb.append((syncMsg.getSource()).getName()).append("_");

        sb.append(((ILifeline) syncMsg.getSource()).getBase());
        sb.append("_").append(syncMsg.getName());
        sb.append("_r");

        if (msgProcesses.contains(sb.toString())) {
            return "";
        } else
            msgProcesses.add(sb.toString());
        sb.append("(sd_id");
        sb.append(",").append(lfsWithUnderscore.get(base1));
        sb.append(",").append(lfsWithUnderscore.get(base2));
        sb.append(") = ");
        sb.append(((ILifeline) syncMsg.getTarget()).getBase()).append("_mOP.s");
        aux.append(((ILifeline) syncMsg.getTarget()).getBase()).append("_mOP.s");
        addIDS(base1, base2, sb, aux);
        //sb.append("!"+syncMsg.getName()+"_O");
        sb.append("?x");
        sb.append(":{x | x<-");
        sb.append(((ILifeline) msg.getSource()).getBase()).append("_OPS");
        sb.append(",get_id(x) == ").append(syncMsg.getName()).append("_O}");
        sb.append(" -> ");
        aux.append(".").append(syncMsg.getName()).append("_O");
        refinementAlphabet.add(aux.toString());

        addToAlphabetMap(base1, aux);

        aux = new StringBuilder();
        sb.append(((ILifeline) syncMsg.getTarget()).getBase()).append("_mOP.r");
        aux.append(((ILifeline) syncMsg.getTarget()).getBase()).append("_mOP.r");
        addIDS(base1, base2, sb, aux);
        sb.append("!x -> ");
        sb.append(seq.getName()).append("_");
        if (!(syncMsg.getTarget()).getName().equals(""))
            sb.append((syncMsg.getTarget()).getName()).append("_");

        sb.append(((ILifeline) syncMsg.getTarget()).getBase()).append("_");

        if (!(syncMsg.getSource()).getName().equals(""))
            sb.append((syncMsg.getSource()).getName()).append("_");

        sb.append(((ILifeline) syncMsg.getSource()).getBase());

        sb.append("_").append(syncMsg.getName()).append("_r");
        sb.append("(sd_id");
        sb.append(",").append(lfsWithUnderscore.get(base1));
        sb.append(",").append(lfsWithUnderscore.get(base2));
        sb.append(")");
        aux.append(".").append(syncMsg.getName()).append("_O");
        refinementAlphabet.add(aux.toString());
        addToAlphabetMap(base2, aux);

        return sb.append("\n").toString();
    }

    private void addToAlphabetMap(String base1, StringBuilder aux) {
        if (alphabetMap.containsKey(base1)) {
            String alfa = alphabetMap.get(base1);
            alfa += ", " + aux.toString();
            alphabetMap.put(base1, alfa);
        } else {
            alphabetMap.put(base1, aux.toString());
        }
    }

    public String translateMessageForLifeline(IMessage msg, ILifeline lifeline, ISequenceDiagram seq) {
        ILifeline lifeline1 = (ILifeline) msg.getSource();
        ILifeline lifeline2 = (ILifeline) msg.getTarget();
        String base1 = lifeline1.getBase().toString();
        String base2 = lifeline2.getBase().toString();

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
                sb.append("!").append(lfsWithUnderscore.get(base1));
                sb.append("!").append(lfsWithUnderscore.get(base2));
                sb.append(".").append(msg.getName()).append("_I");
                //treatArguments(sb, msg.getArgument());
                //treatArguments(aux, msg.getArgument());
                sb.append(" -> SKIP");
            } else if (msg.getTarget().getId().equals(lifeline.getId())) {
//				sb.append(((ILifeline) msg.getSource()).getBase()).append(".");
//				sb.append(((ILifeline) msg.getSource()).getName()).append(".");
                sb.append(lifeline.getBase()).append("_mOP.r");
                sb.append("!").append(lfsWithUnderscore.get(base1));
                sb.append("!").append(lfsWithUnderscore.get(base2));
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
                sb.append("!").append(lfsWithUnderscore.get(base1));
                sb.append("!").append(lfsWithUnderscore.get(base2));
                sb.append(".").append(msg.getName());
                treatArguments(sb, msg.getArgument());
                treatArguments(aux, msg.getArgument());
                sb.append(" -> SKIP");
            } else {
//				sb.append(((ILifeline) msg.getSource()).getBase()).append(".");
//				sb.append(((ILifeline) msg.getSource()).getName()).append(".");
                sb.append(lifeline.getBase()).append("_mSIG.r");
                sb.append("!").append(lfsWithUnderscore.get(base1));
                sb.append("!").append(lfsWithUnderscore.get(base2));
                //sb.append("!"+msg.getName());
                sb.append("?signal:{x | x <- ").append(((ILifeline) msg.getTarget()).getBase()).append("_SIG");
                sb.append(",(get_id(x) == ").append(msg.getName()).append(")}");
                aux.append(",(get_id(x) == ").append(msg.getName()).append(")}");
                sb.append(" -> SKIP");
            }
        } else if (msg.isReturnMessage()) {
            IMessage syncMsg = null;
            try {
                syncMsg = ParserUtilities.getInstance().getPreviousMessage(msg, seq);
            } catch (InvalidUsingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (msg.getTarget().getId().equals(lifeline.getId())) {
                sb.append(((ILifeline) Objects.requireNonNull(syncMsg).getTarget()).getBase()).append("_mOP.r");
                sb.append("!").append(lfsWithUnderscore.get(base1));
                sb.append("!").append(lfsWithUnderscore.get(base2));
                sb.append("?out:");
                sb.append("{x | x <-").append(((ILifeline) syncMsg.getTarget()).getBase()).append("_OPS");
                sb.append(",(get_id(x) == ").append(syncMsg.getName()).append("_O)}");
                sb.append(" -> SKIP");
            } else if (msg.getSource().getId().equals(lifeline.getId())) {
                sb.append(((ILifeline) Objects.requireNonNull(syncMsg).getTarget()).getBase()).append("_mOP.s");
                sb.append("!").append(lfsWithUnderscore.get(base1));
                sb.append("!").append(lfsWithUnderscore.get(base2));
                sb.append(".").append(syncMsg.getName()).append("_O");
                treatArguments(sb, msg.getArgument());
                sb.append(" -> SKIP");
            }
        }
        return sb.toString();
    }

    private void treatArguments(StringBuilder sb, String argument) {
        StringBuilder aux = new StringBuilder();

        if (argument.contains(":")) {
            String[] arguments = argument.split(",");
            for (String argument1 : arguments) {
                if (argument1.contains(":")) {
                    String[] temp = argument1.split(":");
                    aux.append("?").append(temp[0].trim()).append(":{").append(temp[0]).append("|").append(temp[0]).append("<-My").append(temp[1]).append("}");
                } else {
                    aux.append("!(").append(argument1).append(")");
                }
            }
        }
        sb.append(aux.toString());
    }

    public void clearRefAlphabet() {
        refinementAlphabet.clear();
    }

    public void clearAlphabetMap() {
        alphabetMap.clear();
    }

    private void addIDS(String lf1, String lf2, StringBuilder sb, StringBuilder aux) {

        sb.append(".").append(lfsWithUnderscore.get(lf1));
        aux.append(".").append(lfsWithUnderscore.get(lf1));
        sb.append(".").append(lfsWithUnderscore.get(lf2));
        aux.append(".").append(lfsWithUnderscore.get(lf2));

    }


}
