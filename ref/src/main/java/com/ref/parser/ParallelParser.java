package com.ref.parser;

import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.ILifeline;
import com.change_vision.jude.api.inf.model.IMessage;
import com.change_vision.jude.api.inf.model.ISequenceDiagram;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ParallelParser {

    private String msgBuffer;
    private Map<String, String> lfsWithUnderscore;
    private String parallel;
    private int numLife;

    public ParallelParser(Map<String,String> lfsWithUnderscore){
        this.lfsWithUnderscore = lfsWithUnderscore;
    }

    public String translateMessagesBuffer(ISequenceDiagram seq) {
        StringBuilder process = new StringBuilder();
        StringBuilder aux;
        process.append(seq.getName()).append("_MessagesBuffer(sd_id");

        for (int i = 1; i <= seq.getInteraction().getLifelines().length; i++) {
            process.append(",lf").append(i).append("_id");
        }

        process.append(")");
        msgBuffer = process.toString();
        process.append(" = ");
        process.append("(");

        List<String> added = new ArrayList<>();

        for (IMessage msg : seq.getInteraction().getMessages()) {
            aux = new StringBuilder();
            if (msg.isReturnMessage()) {
                IMessage syncMsg = null;
                try {
                    syncMsg = ParserUtilities.getInstance().getPreviousMessage(msg, seq);
                } catch (InvalidUsingException e) {
                    e.printStackTrace();
                }
                aux.append(seq.getName()).append("_");
                if (!(Objects.requireNonNull(syncMsg).getTarget()).getName().equals(""))
                    aux.append((syncMsg.getTarget()).getName()).append("_");

                aux.append(((ILifeline) syncMsg.getTarget()).getBase()).append("_");

                if (!( syncMsg.getSource()).getName().equals(""))
                    aux.append(( syncMsg.getSource()).getName()).append("_");

                aux.append(((ILifeline) syncMsg.getSource()).getBase());

                aux.append("_").append(syncMsg.getName()).append("_r");
            } else {
                aux.append(seq.getName()).append("_");
                aux.append(ParserUtilities.getInstance().addInstancesAndBases(msg));
                aux.append("_").append(msg.getName());
            }
            if (!added.contains(aux.toString())) {
                added.add(aux.toString());
                process.append(aux.toString());
                process.append("(");
                process.append("sd_id,");

                ILifeline life = (ILifeline) msg.getSource();
                process.append(lfsWithUnderscore.get(life.getBase().toString()));
                process.append(",");
                life = (ILifeline) msg.getTarget();
                process.append(lfsWithUnderscore.get(life.getBase().toString()));

                process.append(")");
                process.append(" ||| ");
            }
        }
        process.delete(process.length() - 5, process.length());
        process.append(")");
        process.append("/").append("\\").append("endInteraction -> SKIP");
        return process.toString();
    }

    public String getParalelProcess(ISequenceDiagram seq, Map<String,String> alphabetMap, List<String> processes) {
        StringBuilder sbAux = new StringBuilder();
        sbAux.append(seq.getName()).append("Parallel(sd_id");
        List<String> bases = new ArrayList<>();
        int i = 1;
        for (ILifeline lifeline : seq.getInteraction().getLifelines()) {
            sbAux.append(",");
            sbAux.append(lfsWithUnderscore.get(lifeline.getBase().toString()));
            i++;
            bases.add(lifeline.getBase().toString());
        }
        sbAux.append(")");
        parallel = sbAux.toString();
        sbAux.append(" = ");
        numLife = i - 1;

        StringBuilder sb = new StringBuilder();
        sb.append(alphabetMap.get(bases.get(0)));

        for (int x = 2; x < i - 1; x++) {
            sbAux.append("(");
        }

        for (int j = 0; j < i - 2; j++) {

            if (j % 2 == 0)
                sbAux.append(processes.get(j));
            else
                sbAux.append(")");

            sbAux.append("[ {|");
            sbAux.append(sb.toString());
            sbAux.append("|} || {|");
            sbAux.append(alphabetMap.get(bases.get(j + 1)));
            sb.append(", ").append(alphabetMap.get(bases.get(j + 1)));
            sbAux.append("|} ]");
            sbAux.append(processes.get(j + 1));
        }
        processes.clear();

        return sbAux.toString();
    }

    public String getSDprocess(ISequenceDiagram seq, List<String> refinementAlphabet ){
        StringBuilder process = new StringBuilder();
        process.append("SD_").append(seq.getName()).append("(sd_id");

        for (int i = 1; i <= numLife; i++) {
            process.append(",lf").append(i).append("_id");
        }
        process.append(") = beginInteraction ->((");
        process.append(parallel);
        process.append("; endInteraction -> SKIP)");
        process.append("[|{|");

        for (String alfa : refinementAlphabet) {
            process.append(alfa).append(",");
        }
        process.deleteCharAt(process.length() - 1);
        process.append(",endInteraction");
        process.append("|}|]");
        process.append(msgBuffer).append(")");

        return process.toString();
    }
}
