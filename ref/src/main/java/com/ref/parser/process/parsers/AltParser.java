package com.ref.parser.process.parsers;

import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.*;
import com.change_vision.jude.api.inf.presentation.IPresentation;
import com.ref.parser.MessageParser;
import com.ref.parser.ParserHelper;

import javax.swing.text.html.parser.Parser;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AltParser extends FragmentParser{

    private Set<IMessage> parsedMsgs = new HashSet<>();
    private FragmentInfo fragInfo = new FragmentInfo();

    public String parseFrag(ICombinedFragment fragment, ILifeline lifeline, ISequenceDiagram seq, Map<INamedElement, String> altMapping) {


        System.out.println("Frag id:" + fragment.getId());
        if(fragment.getOwner() != null){
            System.out.println("OWner ID:"  + fragment.getOwner().getId());
        }
        StringBuilder sb = new StringBuilder();
        String fragName = altMapping.get(fragment);
        sb.append("\n");
        sb.append(fragName);

        fragInfo.setName(fragName);
        fragInfo.setType("alt");
//        System.out.println("STARTED FRAGMENT : " + fragName);
        IInteractionOperand[] operands = fragment.getInteractionOperands();
        fragInfo.setNumberOfOperands(operands.length);
        for(int i = 0; i < operands.length; i++){
            String guard = operands[i].getGuard();
            if (guard.equals("") || guard.equals("else")){
                sb.append(".").append("true");
            }else{
                sb.append("?").append(guard);
            }
        }
        sb.append(" -> ");
        sb.append("(");

        try {
            IPresentation[] presentations = fragment.getPresentations();
            System.out.println("ALT PRESENTATION: " + presentations[0].getProperties());
        } catch (InvalidUsingException e) {
            e.printStackTrace();
        }

        int numberOfMsgs = 0;
//        System.out.println("Number of operands " + operands.length);
        boolean hasElse = false;
        for(IInteractionOperand operand : operands){
//            System.out.println("Iniciou operand");
            String guard = operand.getGuard();
            if(guard.equals("") || guard.equals("else")){
                hasElse=true;
                sb.append("true");
            }else{
                sb.append(guard);
            }
            sb.append(" & ");
//            System.out.println("guard : " + operand.getGuard());

            IMessage[] messages = operand.getMessages();

            numberOfMsgs += messages.length;

//            System.out.println(operand.getGuard() + " tem " + messages.length + "mensagem, lifeline " + lifeline.getName());
            for (IMessage message: messages) {
//                System.out.println("Parseando" + message.getName());

                sb.append("(");
                String parsedMsg = MessageParser.getInstance().translateMessageForLifeline(message, lifeline, seq);
                ParserHelper.getInstance().addMsgInfo(new MessageInfo(message, parsedMsg, true));
                sb.append(parsedMsg);
                this.parsedMsgs.add(message);
                sb.append(")");
                sb.append(";");
            }
            sb.delete(sb.length()-1, sb.length());
            sb.append("\n").append("[]").append("\n");
        }
        if(!hasElse){
            sb.append("SKIP");
        }else{
            sb.delete(sb.length()-4,sb.length());
        }
        sb.append(");");
        fragInfo.setNumberOfMessages(numberOfMsgs);
        ParserHelper.getInstance().addFragmentInfo(altMapping.get(fragment), this.fragInfo);

        return sb.toString();
    }

    @Override
    public Set<IMessage> getParsedMsgs() {
        return this.parsedMsgs;
    }


}
