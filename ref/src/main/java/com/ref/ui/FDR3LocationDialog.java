package com.ref.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import com.ref.fdr.FdrWrapper;

public class FDR3LocationDialog extends JDialog {

	public static final String FDR3_PROPERTY_FILE = "ref.properties";
	public static final String FDR3_LOCATION_PROPERTY = "fdr3_location";
	public static final String FDR3_JAR_LOCATION_PROPERTY = "fdr3_jar_location";

	private JTextField tf;
	private JButton findButton;
	private JButton applyButton;
	private JFileChooser fc;
	private JLabel msg;
	private Properties prop;

	public FDR3LocationDialog(JFrame frame, boolean modal)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		super(frame, modal);
		File propertyFile = new File(FDR3_PROPERTY_FILE);
		if (!propertyFile.exists()) {
			propertyFile.createNewFile();
			prop = new Properties();
			prop.load(new FileInputStream(propertyFile));
			prop.setProperty(FDR3_LOCATION_PROPERTY, "");
			prop.setProperty(FDR3_JAR_LOCATION_PROPERTY, "");
			prop.store(new FileOutputStream(propertyFile), null);
		} else {
			prop = new Properties();
			prop.load(new FileInputStream(propertyFile));
		}
		initComponents();
		this.setTitle("FDR3 Location");
		this.setLocation(new Point(276, 182));
		this.setSize(new Dimension(450, 150));
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.setResizable(false);
		this.setVisible(true);

	}

	private void initComponents() {
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;

		add(new JLabel("FDR3 folder:"), gbc);
		gbc.gridx++;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 100;

		tf = new JTextField();

		tf.setText(prop.getProperty(FDR3_LOCATION_PROPERTY));
		tf.setSize(300, tf.getHeight());
		add(tf, gbc);
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx++;
		gbc.weightx = 0;
		findButton = new JButton("Find");
		findButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					fc = new JFileChooser();
					if (System.getProperty("os.name").startsWith("Mac OS X")) {
						fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					} else {
						fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					}
					int returnVal = fc.showDialog(findButton.getParent(), "Select");
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						tf.setText(fc.getSelectedFile().getAbsolutePath());
					}

				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
		});
		add(findButton, gbc);
		gbc.gridx = 2;
		gbc.gridy++;
		applyButton = new JButton("Apply");
		applyButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (tf.getText().equals("")) {
					msg.setText("Please select a valid folder!");
				} else {
					String filename = null;
					if (System.getProperty("os.name").startsWith("Mac OS X")) {
						filename = tf.getText() + "/Contents/Frameworks/fdr.jar";

					} else if (System.getProperty("os.name").contains("Win")) {
						filename = tf.getText() + "\\bin\\fdr.jar";
					}
					System.out.println(filename);
					File fdrlib = new File(filename);
					if (!fdrlib.exists()) {
						msg.setText("Library fdr.jar not found!");
					} else {
						try {
							prop.setProperty(FDR3_LOCATION_PROPERTY, tf.getText());
							prop.setProperty(FDR3_JAR_LOCATION_PROPERTY, filename);
							FdrWrapper wrapper = FdrWrapper.getInstance();
							wrapper.loadFDR(filename);
							wrapper.loadClasses();
							prop.store(new FileOutputStream(new File(FDR3_PROPERTY_FILE)), null);
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						} catch (IOException e1) {
							e1.printStackTrace();
						} catch (ClassNotFoundException e1) {
							e1.printStackTrace();
						}
						FDR3LocationDialog.this.setVisible(false);
						FDR3LocationDialog.this
								.dispatchEvent(new WindowEvent(FDR3LocationDialog.this, WindowEvent.WINDOW_CLOSING));

					}
					// TODO: Add other operating systems

				}

			}
		});
		add(applyButton, gbc);
		gbc.gridx = 0;
		gbc.gridy++;
		gbc.weightx = 3;
		msg = new JLabel();
		msg.setForeground(Color.RED);
		add(msg, gbc);
	}

	public static void main(String[] args) throws ClassNotFoundException {

	}
}
