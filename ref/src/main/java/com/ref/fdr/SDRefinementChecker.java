package com.ref.fdr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SDRefinementChecker {

    public boolean checkRefinement(String filename) {
        FdrWrapper.getInstance().loadFile(filename);
        List<Object> assertions = FdrWrapper.getInstance().getAssertions();

        return FdrWrapper.getInstance().executeAssertions(assertions);
    }

    public Map<Integer,List<String>> describeCounterExample(String refinementType){
        List<Object> counterExamples = FdrWrapper.getInstance().getCounterExamples();
        Map<Integer, List<String>> result = new HashMap<Integer,List<String>>();

        for(int i = 0; i < counterExamples.size(); i++){
            if( (refinementType.toLowerCase().equals("strict") || (refinementType.toLowerCase().equals("weak")) && i == 1)){
                result.put(i, buildCounterExample(counterExamples.get(i)));
            }
        }
        return result;
    }

    private List<String> buildCounterExample(Object counterExample) {
        List<String> trace = new ArrayList<>();
        String errorEvent = FdrWrapper.getInstance().getErrorEvent(counterExample);
        if(errorEvent.equals("endInteraction")){
            try {
                trace.add(errorEvent);
                trace.add(FdrWrapper.getInstance().getTraceSpecificationBehaviour(counterExample));
                trace.add(FdrWrapper.getInstance().getTraceImplementationBehaviour(counterExample));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            trace = FdrWrapper.getInstance().traceCounterExample(counterExample);
            trace.add(0, errorEvent);
            System.out.println("Trace :" + trace.toString());
        }
        return trace;
    }
}
