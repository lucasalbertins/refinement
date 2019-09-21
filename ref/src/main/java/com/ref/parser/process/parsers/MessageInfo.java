package com.ref.parser.process.parsers;

import com.change_vision.jude.api.inf.model.IMessage;

public class MessageInfo {

    private IMessage message;
    private String translation;
    private boolean isFragMessage;

    public MessageInfo(IMessage message, String translation, boolean isFragMessage) {
        this.message = message;
        this.translation = translation;
        this.isFragMessage = isFragMessage;
    }

    public IMessage getMessage() {
        return message;
    }

    public void setMessage(IMessage message) {
        this.message = message;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public boolean isFragMessage() {
        return isFragMessage;
    }

    public void setFragMessage(boolean fragMessage) {
        isFragMessage = fragMessage;
    }

    @Override
    public String toString() {
        return "Message: " + message.getName() + ", Translation: " + translation;
    }
}
