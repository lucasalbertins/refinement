package com.ref.parser;

import com.change_vision.jude.api.inf.model.*;

import java.util.*;

public class SDchannelParser {

    private ISequenceDiagram seq1;
    private ISequenceDiagram seq2;

    private Map<INamedElement, String> altMapping;

    public SDchannelParser(ISequenceDiagram seq1, ISequenceDiagram seq2) {
        this.seq1 = seq1;
        this.seq2 = seq2;
    }

    public String parseChannels() {
        StringBuilder channelBuilder = new StringBuilder();
        channelBuilder.append("channel beginInteraction,endInteraction\n");// ID_SD

        List<IClass> blocks = new ArrayList<>();

        addBlocksToList(seq1, blocks);
        addBlocksToList(seq2, blocks);

        Set<String> channels = new HashSet<>();
        StringBuilder aux;
        for (IClass block : blocks) {
            List<IMessage> blockMessages = ParserUtilities.getInstance().getBlockMessages(seq1, seq2, block);
            for (IMessage iMessage : blockMessages) {
                aux = new StringBuilder();
                aux.append("channel ").append(block.getName());

                if (iMessage.isAsynchronous()) {
                    aux.append("_mSIG: COM.ID.ID.").append(block.getName());
                    aux.append("_SIG\n");
                } else {
                    aux.append("_mOP: COM.ID.ID.").append(block.getName());
                    aux.append("_OPS\n");
                }

                if (!channels.contains(aux.toString())) {
                    channelBuilder.append(aux.toString());
                    channels.add(aux.toString());
                }
            }
        }

        channelBuilder.append(fragmentChannels());

        return channelBuilder.toString();
    }

    private String fragmentChannels() {
        altMapping = new HashMap<>();
        int currentAlt = 1;
        List<ILifeline> lifelines = ParserUtilities.getInstance().getLifelines(seq1, seq2);
        StringBuilder altChannels = new StringBuilder();
        for (ILifeline lf : lifelines) {
            INamedElement[] aux = lf.getFragments();
            for (INamedElement frag : aux) {
                if (frag instanceof ICombinedFragment && !altMapping.containsKey(frag)) {
                    String altName = parseAlt(frag, currentAlt);
                    altMapping.put(frag,"alt"+currentAlt);
                    altChannels.append(altName);
                    currentAlt++;
                }
            }
        }
//        System.out.println(altChannels);

        return altChannels.toString();
    }

    private String parseAlt(INamedElement fragment, int currentAlt) {
        StringBuilder sb = new StringBuilder();
        ICombinedFragment castFrag = (ICombinedFragment) fragment;
        if (castFrag.isAlt()) {
            IInteractionOperand[] operands = castFrag.getInteractionOperands();
            sb.append("channel alt").append(currentAlt).append(": ");
            for (IInteractionOperand operand : operands) {
                if (operand.getGuard().equals(""))
                    sb.append("{True}.");
                else
                    sb.append("Bool.");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("\n");
        }

        return sb.toString();
    }

    // Adds the blocks of a given sequence diagram to a given List

    private void addBlocksToList(ISequenceDiagram seq, List<IClass> blocks) {
        for (ILifeline lifeline : seq.getInteraction().getLifelines()) {
            blocks.add(lifeline.getBase());
        }
    }

    public Map<INamedElement, String> getAltMapping() {
        return altMapping;
    }

}
