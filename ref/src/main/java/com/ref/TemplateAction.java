package com.ref;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IClass;
import com.change_vision.jude.api.inf.model.INamedElement;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.system.SystemPropertyAccessor;
import com.change_vision.jude.api.inf.system.SystemPropertyAccessorFactory;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;
import com.ref.ui.FDR3LocationDialog;

public class TemplateAction implements IPluginActionDelegate {

	public Object run(IWindow window) throws UnExpectedException {

		try {
			FDR3LocationDialog dialog = new FDR3LocationDialog((JFrame) window.getParent(), true);			
			
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(window.getParent(), "Plugin Property file not found!","File Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (IOException e) {
			JOptionPane.showMessageDialog( window.getParent(), "Error opening plugin property file!","File Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
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
