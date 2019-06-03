package com.ref.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

public class CheckingProgressBar extends JFrame implements Runnable, FocusListener {
    public void CheckingProgressBar() {
        setSize(380, 200);
        startElements();

        closeButton.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                setVisible(false);
            }
        });

        setVisible(true);
    }

    private void startElements() {
        Container contentPane = getContentPane();
        // this text area holds the activity output
        textArea = new JTextArea();
        textArea.setEnabled(false);
        // set up panel with button and progress bar
        JPanel panel = new JPanel();
        closeButton = new JButton("Close");
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setMaximum(5);
        reset();
        panel.add(progressBar);
        panel.add(closeButton);
        contentPane.add(new JScrollPane(textArea), "Center");
        contentPane.add(panel, "South");
        contentPane.setVisible(true);
    }

    public void setNewTitle(String title) {
        this.setTitle(title);
    }

    public void setAssertion(int assertion) {
        this.typeAssertion = assertion;
    }

    private void reset() {
        step1 = false;
        step1 = false;
        step3 = false;
        step4 = false;
        step5 = false;
        progressBar.setValue(0);
        closeButton.setEnabled(false);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setAlwaysOnTop(true);
    }

    private void handleValue(String log){
        int value = progressBar.getValue();

        if (!step1 && value == 1) {
            textArea.append("Translating diagram to CSP...\n");
            step1 = true;
        } else if (!step2 && value == 2) {
            if (typeAssertion == 0) {
                textArea.append("Checking for deadlock...\n");
            } else {
                textArea.append("Checking for non-determinism...\n");
            }
            step2 = true;
        } else if (!step3 && value == 3) {
            textArea.append("Creating counterexamples...\n");
            step3 = true;
        } else if (!step4 && value == 4) {
            textArea.append("Finished!\n");
            step4 = true;
        } else if (!step5 && value == 5) {
            textArea.append(log);
            step5 = true;
        }

        if (value == 5) {
            closeButton.setEnabled(true);
        }
    }

    public void setProgress(int value, String log) {
        progressBar.setValue(value);
        handleValue(log);
    }

    private boolean step1;
    private boolean step2;
    private boolean step3;
    private boolean step4;
    private boolean step5;

    private int typeAssertion;
    private JProgressBar progressBar;
    private JButton closeButton;
    private JTextArea textArea;

    @Override
    public void run() {
        CheckingProgressBar();
    }

    @Override
    public void focusGained(FocusEvent e) {
    }

    @Override
    public void focusLost(FocusEvent e) {
        setAlwaysOnTop(false);
        setAlwaysOnTop(true);
    }
}
