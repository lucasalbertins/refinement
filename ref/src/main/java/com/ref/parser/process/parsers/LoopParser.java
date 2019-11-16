package com.ref.parser.process.parsers;

import com.change_vision.jude.api.inf.model.*;
import com.ref.parser.MessageParser;
import com.ref.parser.ParserHelper;
import com.ref.parser.SDParser;

import java.util.*;

public class LoopParser extends FragmentParser {

    private Set<IMessage> parsedMsgs = new HashSet<>();

    @Override
    public String parseFrag(ICombinedFragment fragment, ILifeline lifeline, ISequenceDiagram seq, Map<INamedElement, String> fragMapping) {
        StringBuilder sb = new StringBuilder();
        String loopName = seq.getName() + "_" + fragMapping.get(fragment) + "_" +lifeline.getName()+ "_" +lifeline.getBase().toString();
        String lfParams = getFragmentLifelines(fragment);

        sb.append(loopName);
        String loopBounds =  fragment.getName();

        String loopParams = "(" +lfParams+")";
        if(loopBounds.contains(",")){
            String[] bounds = loopBounds.split(",");
            String bound1 =  bounds[0].replace("(","");
            String bound2 = bounds[1].replace(")","");
            loopParams = "("+"0,"+bound1+","+bound2 +"," + lfParams +")";
        }else if(!loopBounds.equals("")){
            loopParams = fragment.getName();
            loopParams = loopParams.replace(")",","+ lfParams + ")");
            loopParams = loopParams.replace("(","(0,");
        }
        sb.append(loopParams);
        sb.append(";");
        parseLoop(fragment, loopName,lfParams,lifeline,seq);


        return sb.toString();
    }

    private void parseLoop(ICombinedFragment fragment, String loopName, String lifelineParams ,ILifeline lifeline,ISequenceDiagram seq){
        String loopBounds = fragment.getName();
        String loopParams = "(" + lifelineParams + ")";
        int loopType = 1;

        if(loopBounds.contains(",")){
            loopParams = "(iter, bound,maxbound,"+ lifelineParams+ ")";
            loopType = 3;
        }else if(!loopBounds.equals("")){
            loopParams = "(iter, bound," + lifelineParams + ")";
            loopType = 2;
        }

        StringBuilder loopBuilder = new StringBuilder();
        loopBuilder.append(loopName).append(loopParams);
        loopBuilder.append("=");

        String loopCondition = "";
        if(loopType != 1){
            loopCondition = "if(iter<=bound) then ";
        }

        loopBuilder.append(loopCondition);

        IInteractionOperand[] operands = fragment.getInteractionOperands();
        for(IInteractionOperand operand : operands){
            IMessage[] messages = operand.getMessages();
            StringBuilder msgs = new StringBuilder();

            for (IMessage message: messages) {
                String parsedMsg = MessageParser.getInstance().translateMessageForLifeline(message, lifeline, seq);
                parsedMsg = parsedMsg.replace("SKIP", "");
                ParserHelper.getInstance().addMsgInfo(new MessageInfo(message, parsedMsg, true));
                msgs.append(parsedMsg);
                loopBuilder.append(parsedMsg);
                this.parsedMsgs.add(message);
            }
            switch (loopType) {
                case 1 : loopBuilder.append(loopName).append("(" + lifelineParams +")"); break;
                case 2 : loopBuilder.append(loopName).append("(iter+1,bound," + lifelineParams +")"); break;
                case 3 : loopBuilder.append(loopName).append("(iter+1,bound,maxbound," + lifelineParams + ")");
            }
            String loopElse = getElse(msgs.toString(), loopType, loopName, lifelineParams);
            loopBuilder.append(loopElse);
        }

//        System.out.println("Loop :" + loopBuilder.toString());
         ParserHelper.getInstance().addExtraProcess(loopBuilder.toString());
    }

    private String getElse(String messages, int loopType, String loopName, String lifelineParams) {
        StringBuilder elseBuilder = new StringBuilder();
        if(loopType == 2) {
            elseBuilder.append(" else SKIP");
        }else if (loopType == 3) {
            elseBuilder.append(" else ").append(loopName).append("aux").append("(iter,bound,maxbound," + lifelineParams + ")\n");
            elseBuilder.append(loopName).append("aux").append("(iter,bound,maxbound," + lifelineParams + ")=");
            elseBuilder.append("if(iter<=maxbound) then ");
            elseBuilder.append(messages);
            elseBuilder.append(loopName).append("aux").append("(iter+1, bound,maxbound," +  lifelineParams +")").append(" [] SKIP");
            elseBuilder.append(" else SKIP");
        }
        return elseBuilder.toString();
    }

    public String getFragmentLifelines(ICombinedFragment fragment) {
        List<String> lifelines = new ArrayList<>();
        IInteractionOperand[] operands = fragment.getInteractionOperands();
        Map<String, String> lfsWithUnderscore = SDParser.getLfsWithUnderscore();
        for(IInteractionOperand operand : operands) {
            IMessage[] messages = operand.getMessages();
            for(IMessage msg : messages) {
                ILifeline lfSource = (ILifeline)msg.getSource();
                ILifeline lfDest = (ILifeline)msg.getTarget();
                if (!lifelines.contains(lfsWithUnderscore.get(lfSource.getBase().toString())))
                    lifelines.add(lfsWithUnderscore.get(lfSource.getBase().toString()));
                if (!lifelines.contains(lfsWithUnderscore.get(lfDest.getBase().toString())))
                    lifelines.add(lfsWithUnderscore.get(lfDest.getBase().toString()));
            }
        }

        StringBuilder params = new StringBuilder();

        for (String lf : lifelines) {
            params.append(lf).append(",");
        }

        if(params.length() > 0){
            params.deleteCharAt(params.length()-1);
        }

        return params.toString();
    }

    @Override
    public Set<IMessage> getParsedMsgs() {
        return this.parsedMsgs;
    }
}
