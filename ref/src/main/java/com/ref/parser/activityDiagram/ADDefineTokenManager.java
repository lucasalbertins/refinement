package com.ref.parser.activityDiagram;

import com.change_vision.jude.api.inf.model.IActivity;

public class ADDefineTokenManager {

    private IActivity ad;
    private ADUtils adUtils;

    public ADDefineTokenManager(IActivity ad, ADUtils adUtils) {
        this.ad = ad;
        this.adUtils = adUtils;
    }

    public String defineTokenManager() {
        StringBuilder tokenManager = new StringBuilder();
        String nameDiagram = adUtils.nameDiagramResolver(ad.getName());

        tokenManager.append("TokenManager_" + nameDiagram + "(x,init) = update_" + nameDiagram + "?c?y:limiteUpdate_" + nameDiagram
                + " -> x+y < 10 & x+y > -10 & TokenManager_" + nameDiagram + "(x+y,1) [] clear_" + nameDiagram + "?c -> endDiagram_" + nameDiagram
                + " -> SKIP [] x == 0 & init == 1 & endDiagram_" + nameDiagram + " -> SKIP\n");
        tokenManager.append("TokenManager_" + adUtils.nameDiagramResolver(ad.getName()) + "_t(x,init) = TokenManager_" + nameDiagram + "(x,init)\n");

        return tokenManager.toString();
    }
}
