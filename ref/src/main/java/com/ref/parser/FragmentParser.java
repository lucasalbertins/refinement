package com.ref.parser;

import JP.co.esm.caddies.jomt.jutil.a;
import com.change_vision.jude.api.inf.model.*;

public class FragmentParser {

    public String parseAlt(ICombinedFragment fragment,ILifeline lifeline, ISequenceDiagram seq) {

        StringBuilder sb = new StringBuilder();
        sb.append("alt?");

        IInteractionOperand[] operands = fragment.getInteractionOperands();

        for(int index = 1; index <= operands.length; index++){
            sb.append("G").append(index).append("?");
        }

        sb.append("-> (");

        int i = 1;

        for (IInteractionOperand operand : operands){
            for(IMessage msg : operand.getMessages()){
                sb.append("G").append(i).append(" & ");
                sb.append(MessageParser.getInstance().translateMessageForLifeline(msg,lifeline,seq));
                sb.append("\n").append("[]").append("\n");
            }
            i++;
        }

        System.out.println(sb.toString());
        return null;
    }
}
