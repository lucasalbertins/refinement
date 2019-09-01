package com.ref.parser;

import com.change_vision.jude.api.inf.model.ILifeline;
import com.ref.parser.process.parsers.FragmentInfo;

import java.util.*;

public class ParserHelper {

    private static ParserHelper instance;
    private Map<String, Set<String>> lifelineFrags;
    private Map<ILifeline, Set<String>> lifelineBaseFrags;
    private Map<String, FragmentInfo> fragmentsInfo;

    private ParserHelper(){
        this.lifelineFrags = new HashMap<>();
        this.lifelineBaseFrags = new HashMap<>();
        this.fragmentsInfo = new HashMap<>();
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
            this.lifelineBaseFrags.get(lifeline).add(filterFragment(fragment));
            System.out.println("added " + filterFragment(fragment) + " to " + lifeline.getName());
        }else {
            Set<String> fragList = new HashSet<>();
            fragList.add(filterFragment(fragment));
            this.lifelineFrags.put(lifeline.getId(), fragList);
            this.lifelineBaseFrags.put(lifeline,fragList);
            System.out.println("added " + filterFragment(fragment) + " to " + lifeline.getName());
        }
    }

    public Set<String> getAllFrags(){
        Set<String> frags = new HashSet<>();
        for(String key : this.lifelineFrags.keySet()){
            Set<String> fragsSet = this.lifelineFrags.get(key);
            frags.addAll(fragsSet);
            for(String frag : fragsSet){
                }
        }
        return frags;
    }

    public String getLifelineFrags(ILifeline lifeline){
        StringBuilder sb = new StringBuilder();
        Set<String> frags = this.lifelineFrags.get(lifeline.getId());
        if(frags != null) {
            for(String frag : frags){
                sb.append(frag).append(",");
            }
            sb.deleteCharAt(sb.length()-1);
        }

        return sb.toString();
    }

    public List<ILifeline> getLifelinesByFrag(String frag) {
        List<ILifeline> lifelines = new ArrayList<>();
//        System.out.println("LifelineBases :" + this.lifelineBaseFrags.toString());
        for(Map.Entry<ILifeline,Set<String>> entry : this.lifelineBaseFrags.entrySet()){
            if(entry.getValue().contains(frag))
                lifelines.add(entry.getKey());
        }
        return lifelines;
    }

    private String filterFragment(String frag){
        String[] split1 = frag.split(":");
        String[] split2 = split1[0].split(" ");
        return split2[1];
    }

    public void addFragmentInfo(String fragment, FragmentInfo info) {
        System.out.println("Setting info for " + fragment);
        this.fragmentsInfo.put(fragment,info);
    }

    public FragmentInfo getFragmentInfo(String fragment) {
        return this.fragmentsInfo.get(fragment);
    }
}
