package com.ref.parser;

import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.ILifeline;
import com.change_vision.jude.api.inf.model.IMessage;
import com.change_vision.jude.api.inf.model.ISequenceDiagram;
import com.change_vision.jude.api.inf.presentation.ILinkPresentation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParserUtilities {

    private static ParserUtilities instance;

    private ParserUtilities() {
    }

    public static ParserUtilities getInstance() {
        if (instance == null)
            instance = new ParserUtilities();

        return instance;
    }

    public List<IMessage> getBlockMessages(ISequenceDiagram seq1, ISequenceDiagram seq2, IClass block) {
        List<IMessage> ret = new ArrayList<>();
        addMsgsToList(seq1, block, ret);
        addMsgsToList(seq2, block, ret);

        return ret;
    }

    private void addMsgsToList(ISequenceDiagram seq1, IClass block, List<IMessage> ret) {
        for (IMessage msg : seq1.getInteraction().getMessages()) {
            ILifeline life = (ILifeline) msg.getTarget();
            if (!msg.isReturnMessage() && life.getBase().equals(block) && !messageExists(ret, msg)) {
                ret.add(msg);
            }
        }
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

    public String buildAssertions(ISequenceDiagram seq1, ISequenceDiagram seq2, int aux, List<String> sd1Alphabet, List<String> sd2Alphabet, Map<String, String> lfsWithoutUnderscore) {
        StringBuilder sb = new StringBuilder();
        sb.append("assert ");
        sb.append("SD_").append(seq1.getName());

        if (seq1.getName().equals("Seq0")) {
            sb.append("(sd1id");
        } else
            sb.append("(sd2id");

        for (ILifeline lifeline : seq1.getInteraction().getLifelines()) {
            sb.append(",").append(SDParser.getLfsWithUnderscore().get(lifeline.getBase().toString().replace("_id","id")));
        }
        sb.append(")");
        if (aux == 0) {
            String diferentes = eventosDiferentes(sd1Alphabet, sd2Alphabet);
            if (!diferentes.equals(""))
                sb.append("\\{|").append(diferentes).append("|}");
        }
        sb.append(" [T= ");
        sb.append("SD_").append(seq2.getName());

        if (seq2.getName().equals("Seq1")) {
            sb.append("(sd2id");
        } else
            sb.append("(sd1id");

        for (ILifeline lifeline : seq2.getInteraction().getLifelines()) {
            sb.append(",").append(SDParser.getLfsWithUnderscore().get(lifeline.getBase().toString().replace("_id","id")));
        }
        sb.append(")");
        if (aux == 1) {
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
        //boolean adicionou = false;

        for (String evento : sd2Alphabet) {
            if (!sd1Alphabet.contains(evento)) {
                sb.append(evento).append(",");
                //      adicionou = true;
            }
        }
        String resultado = "";
        if (sb.length() > 0)
            resultado = sb.deleteCharAt(sb.length() - 1).toString();

        return resultado.replace("_id", "id");
    }

    public IMessage getPreviousMessage(IMessage msg, ISequenceDiagram seq) throws InvalidUsingException {
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

    public String addInstancesAndBases(IMessage msg) {
        StringBuilder sb = new StringBuilder();
        if (!(msg.getSource()).getName().equals(""))
            sb.append((msg.getSource()).getName()).append("_");

        sb.append(((ILifeline) msg.getSource()).getBase()).append("_");

        if (!(msg.getTarget()).getName().equals("")) {
            sb.append((msg.getTarget()).getName()).append("_");
        }
        sb.append(((ILifeline) msg.getTarget()).getBase());

        return sb.toString();
    }

}
