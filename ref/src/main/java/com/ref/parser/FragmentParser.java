package com.ref.parser;

import JP.co.esm.caddies.jomt.jutil.a;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.*;
import com.change_vision.jude.api.inf.presentation.ILinkPresentation;
import com.change_vision.jude.api.inf.presentation.INodePresentation;
import com.change_vision.jude.api.inf.presentation.IPresentation;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FragmentParser {

    private Set<IMessage> parsedMsgs = new HashSet<>();

    public String parseAlt(ICombinedFragment fragment, ILifeline lifeline, ISequenceDiagram seq, Map<INamedElement, String> altMapping) {

        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append(altMapping.get(fragment));

        IPresentation sdPresentations[] = new IPresentation[0];
        try {
            sdPresentations = seq.getPresentations();
        } catch (InvalidUsingException e) {
            e.printStackTrace();
        }
        INodePresentation combinedFragmentPresentation = getCombinedFragmentPresentation(sdPresentations, fragment);

        showIncludedMsgs(sdPresentations ,combinedFragmentPresentation);

        IInteractionOperand[] operands = fragment.getInteractionOperands();
        for(IInteractionOperand operand : operands){
            sb.append("?").append(operand.getGuard());
        }
        return "null";
    }

    private void showIncludedMsgs(IPresentation[] sdPresentations, INodePresentation combinedFragmentPresentation) {
        Rectangle2D combinedFragmentRectangle = combinedFragmentPresentation.getRectangle();
        for (IPresentation presentation : sdPresentations) {
            if (presentation.getType().equals("Message")) {
                ILinkPresentation messagePresentation = (ILinkPresentation) presentation;
                Point2D[] messagePoints = messagePresentation.getPoints();
                if(combinedFragmentRectangle.contains(messagePoints[0]) && combinedFragmentRectangle.contains(messagePoints[1]))
                    System.out.println("includes message : " + ((IMessage)messagePresentation.getModel()).getName() + " presentation : " + combinedFragmentPresentation.getLabel() + " " + combinedFragmentPresentation.getID());
            }
        }
    }

    private static INodePresentation getCombinedFragmentPresentation(
            IPresentation[] presentations, ICombinedFragment fragment) {
        INodePresentation combinedFragmentPresentation = null;
        for (IPresentation presentation : presentations) {
            if (presentation.getType().equals("CombinedFragment") && presentation.getModel().equals(fragment)) {
                if (presentation instanceof INodePresentation) {
                    combinedFragmentPresentation = (INodePresentation) presentation;
                }
            }
        }
        return combinedFragmentPresentation;
    }

    public Set<IMessage> getParsedMsgs() {
        return parsedMsgs;
    }

    public void setParsedMsgs(Set<IMessage> parsedMsgs) {
        this.parsedMsgs = parsedMsgs;
    }

}
