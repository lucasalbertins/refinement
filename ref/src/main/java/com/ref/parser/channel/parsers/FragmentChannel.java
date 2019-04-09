package com.ref.parser.channel.parsers;

import com.change_vision.jude.api.inf.model.INamedElement;

public abstract class FragmentChannel {

    public abstract String parseFrag(INamedElement fragment, int currentFrag);

    public abstract String getFragType();

}
