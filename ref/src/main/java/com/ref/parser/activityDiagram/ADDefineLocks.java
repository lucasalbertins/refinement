package com.ref.parser.activityDiagram;

import com.change_vision.jude.api.inf.model.IActivity;

import java.util.List;

public class ADDefineLocks {

    private IActivity ad;
    private List<String> lockChannel;
    private ADUtils adUtils;

    public ADDefineLocks(IActivity ad, List<String> lockChannel, ADUtils adUtils) {
        this.ad = ad;
        this.lockChannel = lockChannel;
        this.adUtils = adUtils;
    }

    public String defineLock() {
        StringBuilder locks = new StringBuilder();
        String nameDiagram = adUtils.nameDiagramResolver(ad.getName());

        if (lockChannel.size() > 0) {
            for (String lock : lockChannel) {
                locks.append("Lock_" + lock + " = lock_" + lock + ".lock -> lock_" + lock + ".unlock -> Lock_" + lock + " [] endDiagram_" + nameDiagram + " -> SKIP\n");
            }

            locks.append("Lock_" + nameDiagram + " = ");

            if (lockChannel.size() == 1) {
                locks.append("Lock_" + lockChannel.get(0) + "\n");
            } else {
                for (int i = 0; i < lockChannel.size() - 1; i++) {
                    locks.append("(");
                }

                locks.append("Lock_" + lockChannel.get(0));

                for (int i = 1; i < lockChannel.size(); i++) {
                    locks.append(" [|{|endDiagram_" + nameDiagram + "|}|] Lock_" + lockChannel.get(i) + ")");
                }

                locks.append("\n");
            }
        }

        return locks.toString();
    }
}
