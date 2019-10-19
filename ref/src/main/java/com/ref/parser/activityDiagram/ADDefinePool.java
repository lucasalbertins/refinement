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
            for (String signal: signalChannels) {
                String poolName = "pool_" + signal;
                String eventName = "event_" + signal + "_" + nameDiagram;

                pool.append( poolName + "(l) = ");
                pool.append("(signal_" + signal + "?" + eventName + " -> ");
                pool.append("if length(l) < 5 then " + poolName + "(l^<" + eventName + ">) ");
                pool.append("else " + poolName + "(l))");

                int lengthAccept = 0;

                for (int i = 0; i < countAccept.size(); i++) {
                    if (countAccept.get(i).getKey().equals(signal)) {
                        lengthAccept = countAccept.get(i).getValue();
                        break;
                    }
                }

                for (int i = 0; i < lengthAccept - 1; i++) {
                    pool.append(" [] (length(l) > 0 & accept_" + signal + "." + (i+1) + "!head(l) -> " + poolName + "(tail(l)))");
                }

                pool.append("\n");

                pool.append(poolName + "_t(l) = " + poolName + "(l) /\\ END_DIAGRAM_" + nameDiagram + "\n");
            }
        }

        return pool.toString();
    }
}
