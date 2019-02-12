package com.ref.activityDiagram;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.*;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;
import com.ref.fdr.FdrWrapper;
import com.ref.parser.activityDiagram.ADParser;
import com.ref.ui.FDR3LocationDialog;

import javax.swing.*;
import java.io.*;
import java.util.List;
import java.util.Properties;

public class TemplateDeterminismActionAD implements IPluginActionDelegate {

	public static boolean firstInteration = true;
	public Object run(IWindow window) throws UnExpectedException {

		try {
			File fdrProperty = new File(FDR3LocationDialog.FDR3_PROPERTY_FILE);

			if (fdrProperty.exists()) {
				Properties prop = new Properties();
				prop.load(new FileInputStream(fdrProperty));
				FdrWrapper wrapper = FdrWrapper.getInstance();
				String pathFDR = prop.getProperty(FDR3LocationDialog.FDR3_JAR_LOCATION_PROPERTY);

				if (!pathFDR.isEmpty()) {
					File fdrLocation = new File(pathFDR);

					if (fdrLocation.exists()) {
						wrapper.loadFDR(pathFDR);
						if (firstInteration && TemplateDeadlockActionAD.firstInteration) {
							wrapper.loadClasses();
							firstInteration = false;
						}

						IDiagram diagram = AstahAPI.getAstahAPI().getViewManager().getDiagramViewManager().getCurrentDiagram();

						if (diagram instanceof IActivityDiagram) {
							ADParser parser = new ADParser(((IActivityDiagram) diagram).getActivity(), ((IActivityDiagram) diagram).getName(), (IActivityDiagram) diagram);
							String diagramCSP = parser.parserDiagram();

							String fs = System.getProperty("file.separator");
							String uh = System.getProperty("user.home");
							File directory = new File(uh+fs+"TempAstah");
							directory.mkdirs();

							PrintWriter writer = new PrintWriter(uh + fs + "TempAstah" + fs + ((IActivityDiagram) diagram).getActivity() + ".csp", "UTF-8");
							writer.print(diagramCSP);

							writer.flush();
							writer.close();

							int result = FdrWrapper.getInstance().checkDeterminism(uh + fs + "TempAstah" + fs + ((IActivityDiagram) diagram).getActivity() + ".csp", parser);
							System.out.println(result);

							if (result == 1) {
								JOptionPane.showMessageDialog(window.getParent(), ((IActivityDiagram) diagram).getName() + " is deterministic!","Check Determinism", JOptionPane.INFORMATION_MESSAGE);
							} else if (result == 2) {
								JOptionPane.showMessageDialog(window.getParent(), "Non-Determinism detected in " + ((IActivityDiagram) diagram).getName(),"Check Determinism", JOptionPane.INFORMATION_MESSAGE);
							} else if (result == 3 || result == 0) {
								JOptionPane.showMessageDialog(window.getParent(), "Compilation failed in " + ((IActivityDiagram) diagram).getName(),"Check Determinism", JOptionPane.INFORMATION_MESSAGE);
							}
						}

					} else {
						JOptionPane.showMessageDialog(window.getParent(), "FDR not found, set FDR location in Tools > Properties Plugin Configuration > FDR Location.","FDR Location", JOptionPane.ERROR_MESSAGE);
					}
				} else {
				JOptionPane.showMessageDialog(window.getParent(), "FDR not found, set FDR location in Tools > Properties Plugin Configuration > FDR Location.","FDR Location", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(window.getParent(), "FDR not found, set FDR location in Tools > Properties Plugin Configuration > FDR Location.","FDR Location", JOptionPane.ERROR_MESSAGE);
			}
			
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(window.getParent(), "Plugin Property file not found!","File Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (IOException e) {
			JOptionPane.showMessageDialog( window.getParent(), "Error opening plugin property file!","File Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidUsingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}

	private void getAllClasses(INamedElement element, List<IClass> classList)
			throws ClassNotFoundException, ProjectNotFoundException {
		if (element instanceof IPackage && !element.getName().equals("java")) {
			for (INamedElement ownedNamedElement : ((IPackage) element).getOwnedElements()) {
				getAllClasses(ownedNamedElement, classList);
			}
		} else if (element instanceof IClass) {
			classList.add((IClass) element);
			for (IClass nestedClasses : ((IClass) element).getNestedClasses()) {
				getAllClasses(nestedClasses, classList);
			}
		}
	}

}