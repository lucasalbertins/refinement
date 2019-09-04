package com.ref.parser.process.parsers;

public class FragmentInfo {
    private int numberOfMessages;
    private int numberOfOperands;
    private String type;
    private String name;

    public FragmentInfo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
