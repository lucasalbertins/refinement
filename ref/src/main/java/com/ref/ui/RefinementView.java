package com.ref.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.ISequenceDiagram;
import com.change_vision.jude.api.inf.presentation.IPresentation;
import com.change_vision.jude.api.inf.project.ModelFinder;
import com.change_vision.jude.api.inf.project.ProjectAccessor;
import com.change_vision.jude.api.inf.project.ProjectAccessorFactory;
import com.change_vision.jude.api.inf.project.ProjectEditUnit;
import com.change_vision.jude.api.inf.project.ProjectEvent;
import com.change_vision.jude.api.inf.project.ProjectEventListener;
import com.change_vision.jude.api.inf.ui.IPluginExtraTabView;
import com.change_vision.jude.api.inf.ui.ISelectionListener;
import com.ref.fdr.FdrWrapper;
import com.ref.parser.SDParser;
import com.ref.refinement.CounterexampleDescriptor;

import JP.co.esm.caddies.jomt.jmodel.IMessagePresentation;

//import JP.co.esm.caddies.jomt.jmodel.IMessagePresentation;

public class RefinementView extends JPanel implements IPluginExtraTabView, ProjectEventListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8087206220395822235L;
	private JRadioButton strictRefinementType;
	private JRadioButton weakRefinementType;
	private JComboBox<String> combo1;
	private JComboBox<String> combo2;
	private FdrWrapper wrapper;
	private File cspFile;
	private CounterexampleDescriptor descriptor;
	private ProjectAccessor projectAccessor;

	public RefinementView() {
		try {
			projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
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
		add(createLabelPane("Refinement Type:"), c);
		c.gridx = 1;
		c.gridy = 0;
		add(strictRefinementType, c);
		c.gridx = 1;
		c.gridy = 1;
		add(weakRefinementType, c);
		c.gridx = 0;
		c.gridy = 2;
		add(createLabelPane("Seq. Diagram: "), c);
		c.gridx = 1;
		c.gridy = 2;
		add(combo1, c);
		c.gridx = 0;
		c.gridy = 3;

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
		c.gridy = 4;
		c.gridwidth = 2;
		c.ipady = 20;
		//add(renameBut, c);
		c.gridx = 0;
		c.gridy = 6;
		c.gridwidth = 2;
		add(button, c);
		addProjectEventListener();
		
		wrapper= FdrWrapper.getInstance();
		
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				try {
					boolean result = true;
					if (strictRefinementType.isSelected()) {
						executeRefinement();
						result = wrapper.verify("test.csp", "STRICT");
						if (result) {
							Map<Integer, List<String>> res = wrapper.getCounterExamples();
							for (int i = 0; i <= 1; i++) {
								if (res.get(i) != null) {
									descriptor.createSD("SD_result", res.get(i), projectAccessor);
									break;
								}
							}
						}
					} else if (weakRefinementType.isSelected()) {
						executeRefinement();
						result = wrapper.verify("test.csp", "WEAK");
						if (result) {
							Map<Integer, List<String>> res = wrapper.getCounterExamples();
							descriptor.createSD("SD_result", res.get(1), projectAccessor);
						}
					} else {
						JOptionPane.showMessageDialog(null, "Select a type of Refinement!", "Error",
								JOptionPane.WARNING_MESSAGE);
					}

					if (!result) {
						JOptionPane.showMessageDialog(null, "No Counter Examples found !", "Result",
								JOptionPane.INFORMATION_MESSAGE);
					}
					
					cspFile.delete();

				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, ex.getMessage(), "Error",
							JOptionPane.ERROR_MESSAGE);
				}

			}
		});
	}


	private void executeRefinement() {
		try {
			ISequenceDiagram seq1 = null;
			ISequenceDiagram seq2 = null;

			INamedElement[] sequence = findSequence();

			for (int i = 0; i < sequence.length; i++) {
				if (seq1 != null && seq2 != null)
					break;
				else {
					if (sequence[i].getName().equals(combo1.getSelectedItem())) {
						seq1 = (ISequenceDiagram) sequence[i];
					} else if (sequence[i].getName().equals(combo2.getSelectedItem())) {
						seq2 = (ISequenceDiagram) sequence[i];
					}
				}
			}

			SDParser parser = new SDParser(seq1, seq2);
			parser.carregaLifelines();
			this.descriptor = new CounterexampleDescriptor();
			descriptor.init(parser.getLifelineMapping());

			String resultado = parser.parseSDs();

			cspFile = new File("test.csp");
			FileWriter fw = new FileWriter(cspFile);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(resultado);
			bw.write("\n");
			bw.write(parser.refinementAssertion());
			bw.close();
			fw.close();			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private INamedElement[] findSequence() throws ProjectNotFoundException {
		INamedElement[] foundElements = projectAccessor.findElements(new ModelFinder() {
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
