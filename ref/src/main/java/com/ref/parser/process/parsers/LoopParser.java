package com.ref.parser.process.parsers;

import com.change_vision.jude.api.inf.model.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LoopParser extends FragmentParser {

    private Set<IMessage> parsedMsgs = new HashSet<>();

    @Override
    public String parseFrag(ICombinedFragment fragment, ILifeline lifeline, ISequenceDiagram seq, Map<INamedElement, String> fragMapping) {
        StringBuilder sb = new StringBuilder();
        String loopName = fragMapping.get(fragment) + "_" +lifeline.getName()+ "_" +lifeline.getBase().toString();
        sb.append(loopName);
        parseLoop(fragment, loopName);


        return sb.toString();
    }

    private String parseLoop(ICombinedFragment fragmento, String loopName){
        String loopBounds = fragmento.getName();
        String loopParams = "()";

        if(loopBounds.contains(",")){
            loopParams = "(iter, lbound,ubound)";
        }else if(!loopBounds.equals("")){
            loopParams = "(iter, ubound)";
        }


        return "";
    }

    @Override
    public Set<IMessage> getParsedMsgs() {
        return this.parsedMsgs;
    }
}
