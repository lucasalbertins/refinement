package com.ref.parser;

import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.ILifeline;
import com.change_vision.jude.api.inf.model.IMessage;
import com.change_vision.jude.api.inf.model.ISequenceDiagram;

import java.util.ArrayList;
import java.util.List;

public class SDchannelParser {

    private ISequenceDiagram seq1;
    private ISequenceDiagram seq2;
    private List<String> channels;

    public SDchannelParser(ISequenceDiagram seq1, ISequenceDiagram seq2) {
        this.seq1 = seq1;
        this.seq2 = seq2;
        this.channels = new ArrayList<>();
    }

    public String parseChannels() {
        StringBuilder channelsSTR = new StringBuilder();
        StringBuilder auxChannel;
        channelsSTR.append("channel beginInteraction,endInteraction\n");// ID_SD
        List<IClass> blocks = new ArrayList<>();
        for (ILifeline lifeline : this.seq1.getInteraction().getLifelines()) {
            blocks.add(lifeline.getBase());
        }
        for (ILifeline lifeline : this.seq2.getInteraction().getLifelines()) {
            blocks.add(lifeline.getBase());
        }

        for (IClass block : blocks) {
            auxChannel = new StringBuilder();
            List<IMessage> blockMessages = ParserUtilities.getInstance().getBlockMessages(seq1,seq2,block);
            boolean hasSignal = false;
            boolean hasOperation = false;
            for (IMessage iMessage : blockMessages) {
                if (iMessage.isAsynchronous()) {
                    hasSignal = true;
                    if (hasOperation) {
                        break;
                    }
                }
                if (iMessage.isSynchronous()) {
                    hasOperation = true;
                    if (hasSignal) {
                        break;
                    }
                }
            }
            if (hasOperation) {
                auxChannel.append("channel ").append(block.getName());
                auxChannel.append("_mOP: COM.ID.ID.").append(block.getName());
                auxChannel.append("_OPS\n");
            }
            if (hasSignal) {
                auxChannel.append("channel ").append(block.getName());
                auxChannel.append("_mSIG: COM.ID.ID.").append(block.getName());
                auxChannel.append("_SIG\n");
            }
            if (!channels.contains(auxChannel.toString())) {
                channelsSTR.append(auxChannel.toString());
                channels.add(auxChannel.toString());
            }
        }

        return channelsSTR.toString();
    }


}
