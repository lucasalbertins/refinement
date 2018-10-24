package com.ref.parser;

import com.change_vision.jude.api.inf.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SDprocessParser {

    private ISequenceDiagram seq1;
    private ISequenceDiagram seq2;
    private Map<String,String> lfsWithUnderscore;
    private int numLife;
    private String paralel;
    private List<String> processes;
    private List<String> sd1Alphabet;
    private List<String> sd2Alphabet;
    //private Map<String, String> alphabetMap;
    //private List<String> refinementAlphabet;


    public SDprocessParser(ISequenceDiagram seq1, ISequenceDiagram seq2, Map<String,String> lfsWithUnderscore) {
        this.seq1 = seq1;
        this.seq2 = seq2;
        this.lfsWithUnderscore = lfsWithUnderscore;
        this.processes = new ArrayList<>();
        //       refinementAlphabet = new ArrayList<>();
//        sd1Alphabet = new ArrayList<>();
//        sd2Alphabet = new ArrayList<>();
    }

    public String parseSD1(){
        return parseSD(seq1);
    }

    public String parseSD2(){
        return parseSD(seq2);
    }

    private String parseSD(ISequenceDiagram seq) {
        StringBuilder process = new StringBuilder();

        // Generate processes for lifelines
        for (ILifeline lifeline : seq.getInteraction().getLifelines()) {
            process.append(parseLifeline(lifeline, seq));
        }
        // Generate processes for Messages
        for (IMessage iMessage : seq.getInteraction().getMessages()) {
            process.append(MessageParser.getInstance().translateMessageForProcess(iMessage, seq));
        }
        // Generate MessagesBuffer Process
        process.append(MessageParser.getInstance().translateMessagesBuffer(seq)).append("\n");
        process.append(paralelProcess(seq));
        process.append("\n");
        process.append("SD_").append(seq.getName()).append("(sd_id");

        for (int i = 1; i <= numLife; i++) {
            process.append(",lf").append(i).append("_id");
        }
        process.append(") = beginInteraction ->((");
        process.append(paralel);
        process.append("; endInteraction -> SKIP)");
        process.append("[|{|");

        for (String alfa : MessageParser.getInstance().getRefinementAlphabet()) {
            process.append(alfa).append(",");
        }
        process.deleteCharAt(process.length() - 1);
        process.append(",endInteraction");
        process.append("|}|]");
        process.append(MessageParser.getInstance().getMsgBuffer()).append(")");

        if (seq.equals(seq1)) {
            sd1Alphabet = new ArrayList<>(MessageParser.getInstance().getRefinementAlphabet());
        } else {
            sd2Alphabet = new ArrayList<>(MessageParser.getInstance().getRefinementAlphabet());
        }

        MessageParser.getInstance().clearRefAlphabet();
        //refinementAlphabet.clear();

        paralel = "";

        return process.toString();
    }

    private String paralelProcess(ISequenceDiagram seq) {
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
        paralel = sbAux.toString();
        sbAux.append(" = ");
        numLife = i - 1;
        // i-1 = numero de lfsWithUnderscore

        StringBuilder sb = new StringBuilder();
        // sb.append(refinementAlphabet.get(0));
        sb.append(MessageParser.getInstance().getAlphabetMap().get(bases.get(0)));

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
            // aux.append(refinementAlphabet.get(j));
            // sb.append(", " + alphabetMap.get(bases.get(j)));
            sbAux.append("|} || {|");
            sbAux.append(MessageParser.getInstance().getAlphabetMap().get(bases.get(j + 1)));
            sb.append(", ").append(MessageParser.getInstance().getAlphabetMap().get(bases.get(j + 1)));
            sbAux.append("|} ]");
            sbAux.append(processes.get(j + 1));
        }
        processes.clear();
        MessageParser.getInstance().clearAlphabetMap();
        //alphabetMap.clear();

        return sbAux.toString();
    }

    private String parseLifeline(ILifeline lifeline, ISequenceDiagram seq) {
        StringBuilder process = new StringBuilder();
        StringBuilder aux = new StringBuilder();
        process.append(seq.getName());
        aux.append(seq.getName());
        if (!lifeline.getName().equals("")) {
            process.append("_").append(lifeline.getName());
            aux.append("_").append(lifeline.getName());
        }
        process.append("_").append(lifeline.getBase());
        aux.append("_").append(lifeline.getBase());
        process.append("(sd_id");
        aux.append("(sd_id");

        List<String> lfs = new ArrayList<>();

        for (INamedElement fragment : lifeline.getFragments()) {
            IMessage msg = (IMessage) fragment;
            ILifeline life1 = (ILifeline) msg.getSource();
            ILifeline life2 = (ILifeline) msg.getTarget();
            if (!lfs.contains(lfsWithUnderscore.get(life1.getBase().toString())))
                lfs.add(lfsWithUnderscore.get(life1.getBase().toString()));
            if (!lfs.contains(lfsWithUnderscore.get(life2.getBase().toString())))
                lfs.add(lfsWithUnderscore.get(life2.getBase().toString()));
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
        processes.add(aux.toString());
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
