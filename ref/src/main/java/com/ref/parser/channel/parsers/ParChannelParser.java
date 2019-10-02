package com.ref.parser.channel.parsers;

import com.change_vision.jude.api.inf.model.ICombinedFragment;
import com.change_vision.jude.api.inf.model.IInteractionOperand;
import com.change_vision.jude.api.inf.model.INamedElement;

public class ParChannelParser extends FragmentChannel {
    @Override
    public String parseFrag(INamedElement fragment, int currentFrag) {
        StringBuilder sb = new StringBuilder();
        ICombinedFragment castFrag = (ICombinedFragment) fragment;
        if (castFrag.isPar()) {
            IInteractionOperand[] operands = castFrag.getInteractionOperands();
            sb.append("channel par").append(currentFrag).append(": ");
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

    @Override
    public String getFragType() {
        return "par";
    }
}
