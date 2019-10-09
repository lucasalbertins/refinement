package com.ref.parser.process.parsers;

import com.change_vision.jude.api.inf.model.*;
import com.ref.parser.MessageParser;
import com.ref.parser.ParserHelper;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ParParser extends FragmentParser {

    private Set<IMessage> parsedMsgs = new HashSet<>();
    @Override
    public String parseFrag(ICombinedFragment fragment, ILifeline lifeline, ISequenceDiagram seq, Map<INamedElement, String> fragMapping) {

        StringBuilder sb = new StringBuilder();
        sb.append("\n");

        IInteractionOperand[] operands = fragment.getInteractionOperands();
        sb.append("(");

        int numberOfMsgs = 0;
        System.out.println("Number of operands " + operands.length);
        for(IInteractionOperand operand : operands){
//            System.out.println("guard : " + operand.getGuard());
            IMessage[] messages = operand.getMessages();
            numberOfMsgs += messages.length;

            System.out.println(operand.getGuard() + " tem " + messages.length + "mensagem, lifeline " + lifeline.getName());
            if(messages.length > 1) {
                sb.append("(");
            }
            for (IMessage message: messages) {
                System.out.println("parseando " + message.getName());
                String parsedMsg = MessageParser.getInstance().translateMessageForLifeline(message, lifeline, seq);
                parsedMsg = parsedMsg.replace("SKIP", "");
                ParserHelper.getInstance().addMsgInfo(new MessageInfo(message, parsedMsg, true));
                sb.append(parsedMsg);
                this.parsedMsgs.add(message);
            }
            sb.append("SKIP");
            if(messages.length > 1) {
                sb.append(")");
            }

//            sb.delete(sb.length()-2, sb.length());
            sb.append("|||");
        }
        sb.delete(sb.length()-3,sb.length());
        sb.append(");");

        return sb.toString();
    }

    @Override
    public Set<IMessage> getParsedMsgs() {
        return this.parsedMsgs;
    }
}
