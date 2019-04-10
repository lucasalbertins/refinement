package com.ref.parser.process.parsers;

import com.change_vision.jude.api.inf.model.*;
import com.ref.parser.MessageParser;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AltParser extends FragmentParser{

    private Set<IMessage> parsedMsgs = new HashSet<>();

    public String parseFrag(ICombinedFragment fragment, ILifeline lifeline, ISequenceDiagram seq, Map<INamedElement, String> altMapping) {

        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append(altMapping.get(fragment));

        IInteractionOperand[] operands = fragment.getInteractionOperands();
        for(int i = 0; i < operands.length; i++){
            sb.append("?").append(operands[i].getGuard());
        }
        sb.append(" -> ");
        sb.append("(");

        for(IInteractionOperand operand : operands){
            sb.append(operand.getGuard());
            sb.append(" & ");
//            System.out.println("guard : " + operand.getGuard());
            IMessage[] messages = operand.getMessages();

            for (IMessage message: messages) {
                sb.append("(");
//                System.out.println(message.getName());
                sb.append(MessageParser.getInstance().translateMessageForLifeline(message, lifeline, seq));
                this.parsedMsgs.add(message);
                sb.append(")");
                sb.append(";");
            }
            sb.delete(sb.length()-1, sb.length());
            sb.append("\n").append("[]").append("\n");
        }
        sb.delete(sb.length()-4,sb.length());
        sb.append(")\n");

        return sb.toString();
    }

    @Override
    public Set<IMessage> getParsedMsgs() {
        return this.parsedMsgs;
    }


}
