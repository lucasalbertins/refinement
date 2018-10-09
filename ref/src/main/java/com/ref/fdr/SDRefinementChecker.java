package com.ref.fdr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SDRefinementChecker {

    public boolean checkRefinement(String filename) {
        FdrWrapper.getInstance().loadFile(filename);
        return FdrWrapper.getInstance().executeAssertions();
    }

    public Map<Integer,List<String>> describeCounterExample(String refinementType){

        List<Object> counterExamples = FdrWrapper.getInstance().getCounterExamples();
        List<String> traces = new ArrayList<>();
        Map<Integer, List<String>> result = new HashMap<Integer,List<String>>();

        for(int i = 0; i < counterExamples.size(); i++){
            if(refinementType.toLowerCase().equals("strict") || (refinementType.toLowerCase().equals("weak") && i == 1)){
                traces = new ArrayList<>();
                buildCounterExample(counterExamples.get(i),traces);
                result.put(i, traces);
            }
        }
        return result;
    }

    private void buildCounterExample(Object counterExample, List<String> trace) {
        String errorEvent = FdrWrapper.getInstance().getErrorEvent(counterExample);
        trace.add(errorEvent);
        if(errorEvent.equals("endInteraction")){
            try {
                FdrWrapper.getInstance().strictCounterExample(counterExample, trace);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            FdrWrapper.getInstance().traceCounterExample(counterExample,trace);
        }

        for(String event : trace){
            System.out.println(event);
        }

    }
}
