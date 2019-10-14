package com.ref.parser.process.parsers;

import com.change_vision.jude.api.inf.model.ICombinedFragment;
import com.change_vision.jude.api.inf.model.IMessage;

import java.util.HashSet;
import java.util.Set;

public class FragmentParserFactory {

    private Set<IMessage> parsedMsgs = new HashSet<>();

    public FragmentParser getFragmentParser(ICombinedFragment frag){
        if(frag.isAlt()){
            return new AltParser();
        }
        else if(frag.isOpt()){
            return new OptParser();
        }else if(frag.isPar()){
            return new ParParser();
        }else if(frag.isLoop()){
            return  new LoopParser();
        }
        return null;
    }

    public void addParsedMsgs(Set<IMessage> msgs){
        this.parsedMsgs.addAll(msgs);
    }

    public Set<IMessage> getParsedMsgs(){
        return this.parsedMsgs;
    }
}
