package com.ref.parser;

import com.change_vision.jude.api.inf.model.ILifeline;

import java.util.*;

public class ParserHelper {

    private static ParserHelper instance;
    private Map<String, Set<String>> lifelineFrags;

    private ParserHelper(){
        this.lifelineFrags = new HashMap<>();
    }

    public static ParserHelper getInstance(){
        if(instance == null){
            instance = new ParserHelper();
        }
        return instance;
    }

    public void addLifelineFrag(ILifeline lifeline, String fragment){
        if(this.lifelineFrags.containsKey(lifeline.getId())){
            this.lifelineFrags.get(lifeline.getId()).add(filterFragment(fragment));
            System.out.println("added " + filterFragment(fragment) + " to " + lifeline.getName());
        }else {
            Set<String> fragList = new HashSet<>();
            fragList.add(filterFragment(fragment));
            this.lifelineFrags.put(lifeline.getId(), fragList);
            System.out.println("added " + filterFragment(fragment) + " to " + lifeline.getName());
        }
    }

    public String getLifelineFrags(ILifeline lifeline){
        StringBuilder sb = new StringBuilder();
        Set<String> frags = this.lifelineFrags.get(lifeline.getId());
        for(String frag : frags){
            sb.append(frag).append(",");
        }
        sb.deleteCharAt(sb.length()-1);

        return sb.toString();
    }

    private String filterFragment(String frag){
        String[] split1 = frag.split(":");
        String[] split2 = split1[0].split(" ");
        return split2[1];
    }

}
