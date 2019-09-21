package com.ref.parser;

import com.change_vision.jude.api.inf.model.*;
import com.ref.parser.process.parsers.FragmentParser;
import com.ref.parser.process.parsers.FragmentParserFactory;
import com.ref.parser.process.parsers.MessageInfo;

import java.util.*;

public class SDprocessParser {

    private ISequenceDiagram seq1;
    private ISequenceDiagram seq2;
    private Map<String, String> lfsWithUnderscore;
    private List<String> processes;
    private List<String> sd1Alphabet;
    private List<String> sd2Alphabet;
    private MessageParser msgParser;
    private FragmentParserFactory fragmentFactory;
    private ParallelParser parallelParser;
    private Map<INamedElement,String> altMapping;

    public SDprocessParser(ISequenceDiagram seq1, ISequenceDiagram seq2, Map<String, String> lfsWithUnderscore, Map<INamedElement, String> altMapping) {
        this.seq1 = seq1;
        this.seq2 = seq2;
        this.lfsWithUnderscore = lfsWithUnderscore;
        this.processes = new ArrayList<>();
        this.fragmentFactory = new FragmentParserFactory();
        this.msgParser = MessageParser.getInstance();
        this.msgParser.init(lfsWithUnderscore);
        this.parallelParser = new ParallelParser(lfsWithUnderscore);
        this.altMapping = altMapping;
    }

    public String parseSD(ISequenceDiagram seq) {
        StringBuilder process = new StringBuilder();

        // Generate processes for lifelines
        for (ILifeline lifeline : seq.getInteraction().getLifelines()) {
            process.append(parseLifeline(lifeline, seq));
        }

        // Generate processes for Messages
        for (IMessage iMessage : seq.getInteraction().getMessages()) {
            process.append(msgParser.translateMessageForProcess(iMessage, seq));
        }
        // Generate MessagesBuffer Process
        process.append(parallelParser.translateMessagesBuffer(seq)).append("\n");
        process.append(parallelParser.getParalelProcess(seq,msgParser.getAlphabetMap(),this.processes));
        msgParser.clearAlphabetMap();
        process.append("\n");
        process.append(parallelParser.getSDprocess(seq,msgParser.getRefinementAlphabet()));

        if (seq.equals(seq1)) {
            sd1Alphabet = new ArrayList<>(msgParser.getRefinementAlphabet());
        } else {
            sd2Alphabet = new ArrayList<>(msgParser.getRefinementAlphabet());
        }
        msgParser.clearRefAlphabet();
        //refinementAlphabet.clear();

        return process.toString();
    }

    private String parseLifeline(ILifeline lifeline, ISequenceDiagram seq) {
        StringBuilder process = new StringBuilder();
        process.append(seq.getName());
        if (!lifeline.getName().equals("")) {
            process.append("_").append(lifeline.getName());
        }
        process.append("_").append(lifeline.getBase());
        process.append("(sd_id");

        List<String> lfs = new ArrayList<>();

        for (INamedElement fragment : lifeline.getFragments()) {
            if( fragment instanceof IMessage){
                IMessage msg = (IMessage) fragment;
                ILifeline life1 = (ILifeline) msg.getSource();
                ILifeline life2 = (ILifeline) msg.getTarget();
                if (!lfs.contains(lfsWithUnderscore.get(life1.getBase().toString())))
                    lfs.add(lfsWithUnderscore.get(life1.getBase().toString()));
                if (!lfs.contains(lfsWithUnderscore.get(life2.getBase().toString())))
                    lfs.add(lfsWithUnderscore.get(life2.getBase().toString()));
            }
        }

        for (String life : lfs) {
            process.append(",").append(life);
        }

        process.append(")");

        processes.add(process.toString());
        process.append(" =");
        for (INamedElement fragment : lifeline.getFragments()) {
            process.append(translateFragment(fragment, lifeline, seq));
        }

        process.deleteCharAt(process.length() - 1);
        process.append("\n");
        return process.toString();
    }

    private String translateFragment(INamedElement fragment, ILifeline lifeline, ISequenceDiagram seq) {


        if (fragment instanceof IMessage && !fragmentFactory.getParsedMsgs().contains(fragment)){
            String parsedMsg = msgParser.translateMessageForLifeline((IMessage) fragment, lifeline, seq);
            ParserHelper.getInstance().addMsgInfo(new MessageInfo((IMessage) fragment,parsedMsg, false ));
            return "(" + parsedMsg + ");";
        } else if (fragment instanceof ICombinedFragment) {
            ICombinedFragment frag = (ICombinedFragment) fragment;
            FragmentParser fragParser = this.fragmentFactory.getFragmentParser(frag);
            String fragResult = fragParser.parseFrag(frag, lifeline, seq, altMapping);
            fragmentFactory.addParsedMsgs(fragParser.getParsedMsgs());
            return fragResult;
        } else if (fragment instanceof IStateInvariant) {
            return null;
        } else if (fragment instanceof IInteractionUse) {
            return null;
        }
        return null;
    }

    public List<String> getSd1Alphabet() {
        return sd1Alphabet;
    }

    public List<String> getSd2Alphabet() {
        return sd2Alphabet;
    }

    public void setAltMapping(Map<INamedElement, String> altMapping) {
        this.altMapping = altMapping;
    }

}
