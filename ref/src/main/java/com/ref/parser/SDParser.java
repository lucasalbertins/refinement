package com.ref.parser;

import java.util.*;

import com.change_vision.jude.api.inf.model.*;

public class SDParser {

    private ISequenceDiagram seq1;
    private ISequenceDiagram seq2;

    //Intermediate results, used in tests
    private String definedTypes;
    private String channels;
    private String sd1Parse;
    private String sd2Parse;

    private String paralel;

    // Auxiliary lists and maps for lifeline data
    private static Map<String, String> lfsWithUnderscore;
    private static Map<String, String> lfsWithoutUnderscore;
    private Map<String, String> lifelineMapping;

    // Auxiliary lists and maps for processes
    private static List<String> processes;
    private static List<String> refinementAlphabet;
    private static List<String> msgProcesses;
    private static Map<String, String> alphabetMap;
    private static List<String> sd1Alphabet;
    private static List<String> sd2Alphabet;

    private int numLife;

    public SDParser(ISequenceDiagram seq1, ISequenceDiagram seq2) {
        this.seq1 = seq1;
        this.seq2 = seq2;
        processes = new ArrayList<>();
        refinementAlphabet = new ArrayList<>();
        lfsWithUnderscore = new HashMap<>();
        lfsWithoutUnderscore = new HashMap<>();
        lifelineMapping = new TreeMap<>();
        alphabetMap = new HashMap<>();
        msgProcesses = new ArrayList<>();

        int numberOfLifelines = 1;
        numberOfLifelines = loadLifelines(seq1, numberOfLifelines);
        loadLifelines(seq2, numberOfLifelines);
    }

    private int loadLifelines(ISequenceDiagram seq, int numberOfLifelines) {

        for (ILifeline lifeline : seq.getInteraction().getLifelines()) {
            if (!lfsWithUnderscore.containsKey(lifeline.getBase().toString())) {
                lfsWithUnderscore.put(lifeline.getBase().toString(), "lf" + numberOfLifelines + "_id");
                lfsWithoutUnderscore.put(lifeline.getBase().toString(), "lf" + numberOfLifelines + "id");
                lifelineMapping(lifeline, numberOfLifelines);
                numberOfLifelines++;
            }
        }
        return numberOfLifelines;
    }

    public String parseSDs() {
        StringBuilder process = new StringBuilder();

        // Generate datatype definition
        SDdataTypeParser dataTypeParser = new SDdataTypeParser(seq1, seq2, lfsWithoutUnderscore);
        this.definedTypes = dataTypeParser.defineTypes();
        process.append(this.definedTypes);

        // Generate channels
        SDchannelParser channelParser = new SDchannelParser(seq1, seq2);
        this.channels = channelParser.parseChannels();
        process.append(this.channels);

        // Generate lifeline and message processes
        this.sd1Parse = parseSD(seq1);
        process.append(sd1Parse);
        process.append("\n");
        this.sd2Parse = parseSD(seq2);
        process.append(sd2Parse);
        return process.toString();
    }

    public String parseSD(ISequenceDiagram seq) {
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

        for (String alfa : refinementAlphabet) {
            process.append(alfa).append(",");
        }
        process.deleteCharAt(process.length() - 1);
        process.append(",endInteraction");
        process.append("|}|]");
        process.append(MessageParser.getInstance().getMsgBuffer()).append(")");

        if (seq.equals(seq1)) {
            sd1Alphabet = new ArrayList<>(refinementAlphabet);
        } else {
            sd2Alphabet = new ArrayList<>(refinementAlphabet);
        }

        refinementAlphabet.clear();

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
            // aux.append(refinementAlphabet.get(j));
            // sb.append(", " + alphabetMap.get(bases.get(j)));
            sbAux.append("|} || {|");
            sbAux.append(alphabetMap.get(bases.get(j + 1)));
            sb.append(", ").append(alphabetMap.get(bases.get(j + 1)));
            sbAux.append("|} ]");
            sbAux.append(processes.get(j + 1));
        }
        processes.clear();
        alphabetMap.clear();

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

    public String getRefinementAssertion() {
        StringBuilder sb = new StringBuilder();
        sb.append(ParserUtilities.getInstance().buildAssertions(seq1, seq2, 0));
        sb.append(ParserUtilities.getInstance().buildAssertions(seq2, seq1, 1));
        return sb.toString();
    }

    public String getDefinedTypes() {
        return this.definedTypes;
    }

    public String getChannels() {
        return this.channels;
    }

    public String getSd1Parse() {
        return sd1Parse;
    }

    public String getSd2Parse() {
        return sd2Parse;
    }

    public static String getAlphabetMapEntry(String key) {
        return alphabetMap.get(key);
    }

    public static boolean alphabetMapContains(String key) {
        return alphabetMap.containsKey(key);
    }

    public static void addToAlphabetMap(String key, String entry) {
        alphabetMap.put(key, entry);
    }

    public static List<String> getMsgProcesses() {
        return msgProcesses;
    }

    public Map<String, String> getLifelineMapping() {
        return lifelineMapping;
    }

    public static Map<String, String> getLfsWithoutUnderscore() {
        return lfsWithoutUnderscore;
    }

    public static Map<String, String> getLfsWithUnderscore() {
        return lfsWithUnderscore;
    }

    public static List<String> getSd1Alphabet() {
        return sd1Alphabet;
    }

    public static List<String> getSd2Alphabet() {
        return sd2Alphabet;
    }

    public void lifelineMapping(ILifeline lifeline, int aux) {
        String base = lifeline.getBase().toString();
        String instance = lifeline.getName();
//        System.out.println("Adicionou " + base);
        lifelineMapping.put("lf" + aux + "id", base + "_" + instance);
    }

    public static void addToRefinementAlphabet(String elem) {
        refinementAlphabet.add(elem);
    }

}
