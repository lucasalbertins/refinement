package com.ref.parser.activityDiagram;

import com.change_vision.jude.api.inf.model.IActivity;

import java.util.List;

public class ADDefinePool {

    private IActivity ad;
    private List<String> signalChannels;
    private String firstDiagram;
    private List<Pair<String, Integer>> countAccept;
    private ADUtils adUtils;

    public ADDefinePool(IActivity ad, List<String> signalChannels, String firstDiagram, List<Pair<String, Integer>> countAccept, ADUtils adUtils) {
        this.ad = ad;
        this.signalChannels = signalChannels;
        this.firstDiagram = firstDiagram;
        this.countAccept = countAccept;
        this.adUtils = adUtils;
    }

    public String definePool() {
        StringBuilder pool = new StringBuilder();
        String nameDiagram = adUtils.nameDiagramResolver(ad.getName());

        if (firstDiagram.equals(ad.getId())) {
        	
        	if(!signalChannels.isEmpty()) {
	        	String poolDatatype = "datatype POOLNAME = ";
	        	pool.append(poolDatatype);
	        	for(String signal: signalChannels) {
	        		pool.append(signal + "|");
	        	}
	        	if(pool.lastIndexOf("|") != -1) pool.replace(pool.lastIndexOf("|"),pool.lastIndexOf("|")+1,"\n");
        	
	        	for(String signal: signalChannels) {
	        		pool.append("POOL(id,"+signal+") = pool_"+signal+"_t(id,<>)\n");
	        	}
	        	pool.append("pools(id) =[|{|endDiagram_"+nameDiagram+".id|}|]x:POOLNAME @ POOL(id,x)\n");
        	}
        	
            for (String signal: signalChannels) {
                String poolName = "pool_" + signal;
                String eventName = "event_" + signal + "_" + nameDiagram;

                pool.append( poolName + "(id,l) = ");
                pool.append("(signal_" + signal + ".id?" + eventName + " -> ");
                pool.append("if length(l) < 5 then " + poolName + "(id,l^<" + eventName + ">) ");
                pool.append("else " + poolName + "(id,l))");

                int lengthAccept = 0;

                for (int i = 0; i < countAccept.size(); i++) {
                    if (countAccept.get(i).getKey().equals(signal)) {
                        lengthAccept = countAccept.get(i).getValue();
                        break;
                    }
                }

                for (int i = 0; i < lengthAccept - 1; i++) {
                    pool.append(" [] (length(l) > 0 & accept_" + signal + ".id." + (i+1) + "!head(l) -> " + poolName + "(id,tail(l)))");
                }

                pool.append("\n");

                pool.append(poolName + "_t(id,l) = " + poolName + "(id,l) /\\ END_DIAGRAM_" + nameDiagram + "(id)\n");
            }
            
        }

        return pool.toString();
    }
}
