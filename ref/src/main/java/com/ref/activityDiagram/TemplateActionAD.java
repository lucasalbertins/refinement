package com.ref.activityDiagram;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IActivityDiagram;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.IDiagram;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;
import com.ref.fdr.FdrWrapper;
import com.ref.parser.activityDiagram.ADParser;
import com.ref.ui.FDR3LocationDialog;

public class TemplateActionAD implements IPluginActionDelegate {

	public Object run(IWindow window) throws UnExpectedException {

		try {
			FDR3LocationDialog dialog = new FDR3LocationDialog((JFrame) window.getParent(), true);	
			
			IDiagram diagram = AstahAPI.getAstahAPI().getViewManager().getDiagramViewManager().getCurrentDiagram();
			
			if (diagram instanceof IActivityDiagram) {
				ADParser parser = new ADParser(((IActivityDiagram) diagram).getActivity(), ((IActivityDiagram) diagram).getName());
				String diagramCSP = parser.parserDiagram();
				
				String fs = System.getProperty("file.separator");
				String uh = System.getProperty("user.home");
				File directory = new File(uh+fs+"TempAstah");
				directory.mkdirs();
				
				PrintWriter writer = new PrintWriter(uh + fs + "TempAstah" + fs + ((IActivityDiagram) diagram).getActivity() + ".csp", "UTF-8");
				writer.print(diagramCSP);		
				
				writer.flush();
				writer.close();
				
				int result = FdrWrapper.getInstance().checkDeadlock(uh + fs + "TempAstah" + fs + ((IActivityDiagram) diagram).getActivity() + ".csp", 0);
				System.out.println(result);
				if (result == 0) {
					JOptionPane.showMessageDialog(window.getParent(), ((IActivityDiagram) diagram).getName() + " is deadlock free!","Check Deadlock", JOptionPane.INFORMATION_MESSAGE);
				} else if (result == 1) {
					JOptionPane.showMessageDialog(window.getParent(), ((IActivityDiagram) diagram).getName() + " deadlock detected!","Check Deadlock", JOptionPane.INFORMATION_MESSAGE);
				} else if (result == 2) {
					JOptionPane.showMessageDialog(window.getParent(), ((IActivityDiagram) diagram).getName() + " compilation failed!","Check Deadlock", JOptionPane.INFORMATION_MESSAGE);
				}
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
