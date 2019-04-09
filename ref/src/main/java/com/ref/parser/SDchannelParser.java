package com.ref.parser;

import com.change_vision.jude.api.inf.model.*;
import com.ref.parser.channel.parsers.FragmentChannel;
import com.ref.parser.channel.parsers.FragmentChannelFactory;

import java.util.*;

public class SDchannelParser {

    private ISequenceDiagram seq1;
    private ISequenceDiagram seq2;
    private FragmentChannelFactory fragFactory;
    private Map<INamedElement, String> fragMapping;

    public SDchannelParser(ISequenceDiagram seq1, ISequenceDiagram seq2) {
        this.seq1 = seq1;
        this.seq2 = seq2;
        fragFactory = new FragmentChannelFactory();
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
        String fragName = "";
        fragMapping = new HashMap<>();
        int currentFrag = 1;
        List<ILifeline> lifelines = ParserUtilities.getInstance().getLifelines(seq1, seq2);
        StringBuilder altChannels = new StringBuilder();
        for (ILifeline lf : lifelines) {
            INamedElement[] aux = lf.getFragments();
            for (INamedElement frag : aux) {
                if (frag instanceof ICombinedFragment && !fragMapping.containsKey(frag)) {
                    FragmentChannel channelFragment = fragFactory.getChannelFragment((ICombinedFragment) frag);
                    fragName = channelFragment.parseFrag(frag, currentFrag);
                    fragMapping.put(frag,channelFragment.getFragType()+currentFrag);
                    altChannels.append(fragName);
                    currentFrag++;
                    ParserHelper.getInstance().addLifelineFrag(lf,fragName);
                }
                if(fragMapping.containsKey(frag))
                    ParserHelper.getInstance().addLifelineFrag(lf,fragName);
            }
        }
//        System.out.println(altChannels);

        return altChannels.toString();
    }

//    private String parseFrag(INamedElement fragment, int currentFrag) {
//        StringBuilder sb = new StringBuilder();
//        ICombinedFragment castFrag = (ICombinedFragment) fragment;
//        if (castFrag.isAlt()) {
//            IInteractionOperand[] operands = castFrag.getInteractionOperands();
//            sb.append("channel alt").append(currentFrag).append(": ");
//            for (IInteractionOperand operand : operands) {
//                if (operand.getGuard().equals(""))
//                    sb.append("{True}.");
//                else
//                    sb.append("Bool.");
//            }
//            sb.deleteCharAt(sb.length() - 1);
//            sb.append("\n");
//        }
//
//        return sb.toString();
//    }

    // Adds the blocks of a given sequence diagram to a given List

    private void addBlocksToList(ISequenceDiagram seq, List<IClass> blocks) {
        for (ILifeline lifeline : seq.getInteraction().getLifelines()) {
            blocks.add(lifeline.getBase());
        }
    }

    public Map<INamedElement, String> getFragMapping() {
        return fragMapping;
    }

}
