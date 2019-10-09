package com.ref.parser.channel.parsers;

import com.change_vision.jude.api.inf.model.ICombinedFragment;
import com.change_vision.jude.api.inf.model.IInteractionOperand;
import com.change_vision.jude.api.inf.model.INamedElement;

public class ParChannelParser extends FragmentChannel {
    @Override
    public String parseFrag(INamedElement fragment, int currentFrag) {
       return "";
    }

    @Override
    public String getFragType() {
        return "par";
    }
}
