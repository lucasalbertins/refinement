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

    // Auxiliary lists and maps for lifeline data
    private static Map<String, String> lfsWithUnderscore;
    private static Map<String, String> lfsWithoutUnderscore;
    private Map<String, String> lifelineMapping;
    private List<String> sd1Alphabet;
    private List<String> sd2Alphabet;

    // Auxiliary lists and maps for processes
    //private static List<String> msgProcesses;

    public SDParser(ISequenceDiagram seq1, ISequenceDiagram seq2) {
        this.seq1 = seq1;
        this.seq2 = seq2;
        lfsWithUnderscore = new HashMap<>();
        lfsWithoutUnderscore = new HashMap<>();
        lifelineMapping = new TreeMap<>();
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

        //Generate lifeline and message processes
        SDprocessParser processParser = new SDprocessParser(seq1,seq2,lfsWithUnderscore);
        this.sd1Parse = processParser.parseSD1();
        process.append(sd1Parse);
        process.append("\n");
        this.sd2Parse = processParser.parseSD2();
        process.append(sd2Parse);

        return process.toString();
    }

    public String getRefinementAssertion() {
        StringBuilder sb = new StringBuilder();
        sb.append(ParserUtilities.getInstance().buildAssertions(seq1, seq2, 0, sd1Alphabet,sd2Alphabet));
        sb.append(ParserUtilities.getInstance().buildAssertions(seq2, seq1, 1,sd1Alphabet,sd2Alphabet));
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

    //public static List<String> getMsgProcesses() {
    //    return msgProcesses;
   // }

    public Map<String, String> getLifelineMapping() {
        return lifelineMapping;
    }

    public static Map<String, String> getLfsWithoutUnderscore() {
        return lfsWithoutUnderscore;
    }

    public static Map<String, String> getLfsWithUnderscore() {
        return lfsWithUnderscore;
    }

    public void lifelineMapping(ILifeline lifeline, int aux) {
        String base = lifeline.getBase().toString();
        String instance = lifeline.getName();
//        System.out.println("Adicionou " + base);
        lifelineMapping.put("lf" + aux + "id", base + "_" + instance);
    }

}
