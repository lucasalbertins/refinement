package com.ref.parser.channel.parsers;

import com.change_vision.jude.api.inf.model.ICombinedFragment;

public class FragmentChannelFactory {

    public FragmentChannel getChannelFragment(ICombinedFragment frag){
        if(frag.isAlt()){
            return new AltChannelParser();
        }else if(frag.isOpt()){
            return new OptChannelParser();
        }
        return null;
    }

}
