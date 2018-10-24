package com.ref.parser;

import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.ILifeline;
import com.change_vision.jude.api.inf.model.IMessage;
import com.change_vision.jude.api.inf.model.ISequenceDiagram;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParserUtilities {

    private static ParserUtilities instance;

    private ParserUtilities(){
    }

    public static ParserUtilities getInstance(){
        if(instance == null)
            instance = new ParserUtilities();

        return instance;
    }

    public List<IMessage> getBlockMessages(ISequenceDiagram seq1, ISequenceDiagram seq2, IClass block) {

        List<IMessage> ret = new ArrayList<>();

        for (IMessage msg : seq1.getInteraction().getMessages()) {
            ILifeline life = (ILifeline) msg.getTarget();
            if (!msg.isReturnMessage() && life.getBase().equals(block) && !messageExists(ret, msg)) {
                ret.add(msg);
            }
        }

        for (IMessage msg : seq2.getInteraction().getMessages()) {
            ILifeline life = (ILifeline) msg.getTarget();
            if (!msg.isReturnMessage() && life.getBase().equals(block) && !messageExists(ret,msg)) {
                ret.add(msg);
            }
        }

        return ret;
    }

    private boolean messageExists(List<IMessage> ret, IMessage mes) {
        for (IMessage iMessage : ret) {
            if (iMessage.getOperation() != null && iMessage.getOperation().getOwner() != null
                    && iMessage.getOperation().getOwner() == mes.getOperation().getOwner()
                    && iMessage.getName().equals(mes.getName()) && iMessage.getArgument().equals(mes.getArgument())) {
                return true;
            }
        }
        return false;
    }


    public String buildAssertions(ISequenceDiagram seq1, ISequenceDiagram seq2, int aux, List<String> sd1Alphabet, List<String> sd2Alphabet) {
        StringBuilder sb = new StringBuilder();
        sb.append("assert ");
        sb.append("SD_").append(seq1.getName());

        if (seq1.getName().equals("Seq0")) {
            sb.append("(sd1id");
        } else
            sb.append("(sd2id");

        for (ILifeline lifeline : seq1.getInteraction().getLifelines()) {
            sb.append(",").append(SDParser.getLfsWithoutUnderscore().get(lifeline.getBase().toString()));
        }
        sb.append(")");
        if (aux == 1) {
            sb.append("\\{|").append(eventosDiferentes(sd1Alphabet,sd2Alphabet)).append("|}");
        }
        sb.append(" [T= ");
        sb.append("SD_").append(seq2.getName());

        if (seq2.getName().equals("Seq1")) {
            sb.append("(sd2id");
        } else
            sb.append("(sd1id");

        for (ILifeline lifeline : seq2.getInteraction().getLifelines()) {
            sb.append(",").append(SDParser.getLfsWithoutUnderscore().get(lifeline.getBase().toString()));
        }
        sb.append(")");
        if (aux == 0) {
            String diferentes = eventosDiferentes(sd1Alphabet, sd2Alphabet);
            if (!diferentes.equals("")) {
                sb.append("\\{|").append(diferentes).append("|}\n");
            } else
                sb.append("\n");
        }

        return sb.toString();
    }

    private String eventosDiferentes(List<String> sd1Alphabet, List<String> sd2Alphabet) {
        StringBuilder sb = new StringBuilder();
        boolean adicionou = false;

        for (String evento : sd2Alphabet) {
            if (!sd1Alphabet.contains(evento)) {
                sb.append(evento).append(",");
                adicionou = true;
            }
        }

        if (adicionou) {
            ArrayList<String> elementos1 = new ArrayList<>();
            for (Map.Entry<String, String> entry : SDParser.getLfsWithUnderscore().entrySet()) {
                //System.out.println(entry.getKey() + "/" + entry.getValue());
                elementos1.add(entry.getValue());
            }

            ArrayList<String> elementos2 = new ArrayList<>();
            for (Map.Entry<String, String> entry : SDParser.getLfsWithoutUnderscore().entrySet()) {
                //System.out.println(entry.getKey() + "/" + entry.getValue());
                elementos2.add(entry.getValue());
            }

            String resultado = sb.deleteCharAt(sb.length() - 1).toString();

            for (int i = 0; i < elementos1.size(); i++) {
                resultado = resultado.replaceAll(elementos1.get(i), elementos2.get(i));
            }

            return resultado;
        }
        return "";
    }

}
