package com.ref.parser.process.parsers;

public class FragmentInfo {
    private int numberOfMessages;
    private int numberOfOperands;

    public FragmentInfo() {
    }

    public void setNumberOfMessages(int numberOfMessages) {
        this.numberOfMessages = numberOfMessages;
    }

    public void setNumberOfOperands(int numberOfOperands) {
        this.numberOfOperands = numberOfOperands;
    }

    public int getNumberOfMessages() {
        return numberOfMessages;
    }

    public int getNumberOfOperands() {
        return numberOfOperands;
    }
}
