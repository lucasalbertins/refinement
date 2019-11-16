package com.ref.parser.process.parsers;

import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.*;
import com.change_vision.jude.api.inf.presentation.INodePresentation;
import com.change_vision.jude.api.inf.presentation.IPresentation;
import com.ref.parser.MessageParser;
import com.ref.parser.ParserHelper;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OptParser extends FragmentParser {

    private Set<IMessage> parsedMsgs = new HashSet<>();
    private FragmentInfo fragInfo = new FragmentInfo();


    @Override
    public String parseFrag(ICombinedFragment fragment, ILifeline lifeline, ISequenceDiagram seq, Map<INamedElement, String> fragMapping) {

        StringBuilder sb = new StringBuilder();
        String fragname = fragMapping.get(fragment);
        sb.append("\n");
        sb.append(fragname);

        fragInfo.setName(fragname);
        fragInfo.setType("opt");

        IPresentation[] presentations = new IPresentation[0];
        try {
            presentations = fragment.getPresentations();
            INodePresentation np = (INodePresentation) presentations[0];
            System.out.println(np.getHeight());
//            System.out.println("OPT PRESENTATION: " + presentations[0].getProperties());
        } catch (InvalidUsingException e) {
            e.printStackTrace();
        }

        IInteractionOperand[] operands = fragment.getInteractionOperands();
        fragInfo.setNumberOfOperands(operands.length);
        for (int i = 0; i < operands.length; i++) {
            String guard = operands[i].getGuard();
            if (guard.equals("")) {
                sb.append(".").append("true");
            } else {
                sb.append("?").append(guard);
            }
        }
        sb.append(" -> ");
        sb.append("(");

        int numberOfMsgs = 0;
        for (IInteractionOperand operand : operands) {
            String guard = operand.getGuard();
            if (guard.equals("")) {
                sb.append("true");
            } else {
                sb.append(guard);
            }
            sb.append(" & ");
//            System.out.println("guard : " + operand.getGuard());
            IMessage[] messages = operand.getMessages();
            numberOfMsgs += messages.length;

            for (IMessage message : messages) {
                sb.append("(");
                String parsedMsg = MessageParser.getInstance().translateMessageForLifeline(message, lifeline, seq);
                ParserHelper.getInstance().addMsgInfo(new MessageInfo(message, parsedMsg, true));
                sb.append(parsedMsg);
                this.parsedMsgs.add(message);
                sb.append(")");
            }
        }
        sb.append(" [] SKIP");
        sb.append(");");
        fragInfo.setNumberOfMessages(numberOfMsgs);
        ParserHelper.getInstance().addFragmentInfo(fragMapping.get(fragment), this.fragInfo);

        return sb.toString();
    }

    @Override
    public Set<IMessage> getParsedMsgs() {
        return this.parsedMsgs;
    }
}
