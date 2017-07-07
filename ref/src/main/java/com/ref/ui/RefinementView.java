package com.ref.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.ISequenceDiagram;
import com.change_vision.jude.api.inf.project.ModelFinder;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;
import com.change_vision.jude.api.inf.project.ProjectEditUnit;
import com.change_vision.jude.api.inf.project.ProjectEvent;
import com.change_vision.jude.api.inf.project.ProjectEventListener;
import com.change_vision.jude.api.inf.ui.IPluginExtraTabView;
import com.change_vision.jude.api.inf.ui.ISelectionListener;
import com.ref.RefinementController;
import com.refinement.exceptions.RefinementException;

public class RefinementView extends JPanel implements IPluginExtraTabView,
		ProjectEventListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8087206220395822235L;
	private JRadioButton strictRefinementType;
	private JRadioButton weakRefinementType;
	private JComboBox<String> combo1;
	private JComboBox<String> combo2;
	private ProjectAccessor projectAccessor;

	public RefinementView() {
		try {
			projectAccessor = ProjectAccessorFactory.getProjectAccessor();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initComponents();
	}

	private void initComponents() {
		setLayout(new GridBagLayout());
		ButtonGroup group = new ButtonGroup();
		strictRefinementType = new JRadioButton("Strict");
		weakRefinementType = new JRadioButton("Weak");
		group.add(strictRefinementType);
		group.add(weakRefinementType);
		combo1 = new JComboBox<String>();
		combo2 = new JComboBox<String>();
		JButton renameBut = new JButton("Rename messages");
		JButton button = new JButton("Check");
		updateComboBoxes();
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		add(createLabelPane("Refinement Type:"),c);
		c.gridx = 1;
		c.gridy = 0;
		add(strictRefinementType,c);
		c.gridx = 1;
		c.gridy = 1;
		add(weakRefinementType, c);
		c.gridx = 0;
		c.gridy = 2;
		add(createLabelPane("Seq. Diagram: "), c);
		c.gridx = 1;
		c.gridy = 2;
		add(combo1,c);
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		c.ipady = 20;

		c.anchor = GridBagConstraints.CENTER;
		add(createLabelPane("is refined by"), c);
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.gridx = 0;
		c.gridy = 4;

		add(createLabelPane("Seq. Diagram: "), c);
		c.gridx = 1;
		c.gridy = 4;

		add(combo2, c);
		c.gridx = 0;
		c.gridy = 5;
		add(renameBut, c);
		c.gridx = 0;
		c.gridy = 6;
		c.gridwidth = 2;
		add(button, c);
		addProjectEventListener();
		
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Properties p = new Properties();
					File propertyFile = new File(FDR3LocationDialog.FDR3_PROPERTY_FILE);
					if (!propertyFile.exists()) {
						JOptionPane.showMessageDialog(combo1.getParent().getParent(), "FDR3 location not set!", "Error", JOptionPane.ERROR_MESSAGE);
					} else {
						p.load(new FileInputStream(propertyFile));
						String property = p.getProperty(FDR3LocationDialog.FDR3_LOCATION_PROPERTY);
						if (property == null || property.equals("")) {
							JOptionPane.showMessageDialog(combo1.getParent().getParent(), "FDR3 location not set!", "Error", JOptionPane.ERROR_MESSAGE);
						} else {
							ISequenceDiagram seq1 = null;
							ISequenceDiagram seq2 = null;
							INamedElement[] findSequence;
							try {
								findSequence = findSequence();
								for (int i = 0; i < findSequence.length; i++) {
									if (seq1 != null && seq2 != null) {
										break;
									}
									if (findSequence[i].getName().equals(combo1.getSelectedItem())) {
										seq1 = (ISequenceDiagram)findSequence[i];
									} else if (findSequence[i].getName().equals(combo2.getSelectedItem())) {
										seq2 = (ISequenceDiagram)findSequence[i];
									}
								}
							} catch (ProjectNotFoundException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							if (seq1 != null && seq2 != null) {
								RefinementController controller = RefinementController.getInstance();
								try {
									controller.checkRefinement(seq1,seq2);
								} catch (RefinementException e1) {
									e1.printStackTrace();
									JOptionPane.showMessageDialog(getParent(), e1.getMessage(), "Refinement Error!", JOptionPane.ERROR_MESSAGE);
								} catch (InvalidEditingException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							} else {
								JOptionPane.showMessageDialog(getParent(), "Could not find sequence diagram.", "Refinement Error!", JOptionPane.ERROR_MESSAGE);
							}
						}
					}
					
				} catch (FileNotFoundException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				
				
							
				
				
				
				
			}
		});
	}
	
	private INamedElement[] findSequence() throws ProjectNotFoundException {
		INamedElement[] foundElements = projectAccessor
				.findElements(new ModelFinder() {
					public boolean isTarget(INamedElement namedElement) {
						return namedElement instanceof ISequenceDiagram;
					}
				});
		return foundElements;
	}

	private void updateComboBoxes() {
		try {
			combo1.removeAllItems();
			combo1.addItem("<Select a SD>");
			INamedElement[] findSequence = findSequence();
			for (int i = 0; i < findSequence.length; i++) {
				
				combo1.addItem(findSequence[i].getName());
			}
			combo2.removeAllItems();
			combo2.addItem("<Select a SD>");
			for (int i = 0; i < findSequence.length; i++) {
				combo2.addItem(findSequence[i].getName());
			}			
		} catch (ProjectNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}

	private void addProjectEventListener() {
		projectAccessor.addProjectEventListener(this);
	}

	private Container createLabelPane(String str) {
		JLabel label = new JLabel(str);
		return label;
	}

	@Override
	public void projectChanged(ProjectEvent e) {
		ProjectEditUnit[] projectEditUnit = e.getProjectEditUnit();
		for (int i = 0; i < projectEditUnit.length; i++) {
			if (projectEditUnit[i].getEntity() instanceof ISequenceDiagram) {
				updateComboBoxes();
				break;
			}
		}

	}

	@Override
	public void projectClosed(ProjectEvent e) {
	}

	@Override
	public void projectOpened(ProjectEvent e) {
		updateComboBoxes();
	}

	@Override
	public void addSelectionListener(ISelectionListener listener) {
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public String getDescription() {
		return "Show Refinement here";
	}

	@Override
	public String getTitle() {
		return "Refinement View";
	}

	public void activated() {

	}

	public void deactivated() {

	}
}
