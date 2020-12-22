package com.ref.ui.activityDiagram;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JOptionPane;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.model.IActivityDiagram;
import com.change_vision.jude.api.inf.model.IDiagram;
import com.change_vision.jude.api.inf.ui.IPluginActionDelegate;
import com.change_vision.jude.api.inf.ui.IWindow;
import com.ref.exceptions.FDRException;
import com.ref.exceptions.ParsingException;
import com.ref.exceptions.WellFormedException;
import com.ref.fdr.FdrWrapper;
import com.ref.log.Logador;
import com.ref.traceability.activityDiagram.ActivityController;
import com.ref.traceability.activityDiagram.ActivityController.VerificationType;
import com.ref.ui.CheckingProgressBar;
import com.ref.ui.FDR3LocationDialog;

public class TemplateDeadlockActionAD implements IPluginActionDelegate {

	public static boolean firstInteration = true;
	public ActivityController controler = new ActivityController();
	public Object run(IWindow window) {

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
						if (firstInteration && TemplateDeterminismActionAD.firstInteration) {
							wrapper.loadClasses();
							firstInteration = false;
						}

						IDiagram diagram = AstahAPI.getAstahAPI().getViewManager().getDiagramViewManager().getCurrentDiagram();

						if (diagram instanceof IActivityDiagram) {
							CheckingProgressBar progressBar = new CheckingProgressBar();
							progressBar.setNewTitle("Checking deadlock");
							progressBar.setAssertion(0);

							//controler.AstahInvocation(diagram, VerificationType.DEADLOCK,progressBar);
							new Thread(new Runnable() {
								@Override
								public void run() {
									try {
										controler.AstahInvocation(diagram, VerificationType.DEADLOCK,progressBar);
									}catch(ParsingException e) {
										JOptionPane.showMessageDialog( window.getParent(), e.getMessage(),"File Error", JOptionPane.ERROR_MESSAGE);
										e.printStackTrace();
									} catch(FDRException e) {
									 	JOptionPane.showMessageDialog( window.getParent(), e.getMessage(),"Checking Non-determinism Error", JOptionPane.ERROR_MESSAGE);
										e.printStackTrace();
									} catch (WellFormedException e) {
										JOptionPane.showMessageDialog( window.getParent(), e.getMessage(),"Well-formedness Error", JOptionPane.ERROR_MESSAGE);
										e.printStackTrace();
									} catch (Exception e) {
										JOptionPane.showMessageDialog( window.getParent(), "An error occurred during checking deadlock.","Checking Deadlock Error", JOptionPane.ERROR_MESSAGE);
										e.printStackTrace();
									}
								}
							}).start();
							
							/*ADParser parser = new ADParser(((IActivityDiagram) diagram).getActivity(), diagram.getName(), (IActivityDiagram) diagram);
							String diagramCSP = parser.parserDiagram();

							String fs = System.getProperty("file.separator");
							String uh = System.getProperty("user.home");
							File directory = new File(uh+fs+"TempAstah");
							directory.mkdirs();

							PrintWriter writer = new PrintWriter(uh + fs + "TempAstah" + fs + ((IActivityDiagram) diagram).getActivity() + ".csp", "UTF-8");
							writer.print(diagramCSP);

							writer.flush();
							writer.close();

							CheckingProgressBar progressBar = new CheckingProgressBar();
							progressBar.setNewTitle("Checking deadlock");
							progressBar.setAssertion(0);

							new Thread(new Runnable() {
								@Override
								public void run() {
									try {
										FdrWrapper.getInstance().checkDeadlock(uh + fs + "TempAstah" + fs + ((IActivityDiagram) diagram).getActivity() + ".csp", parser, diagram.getName(), progressBar);
									} catch (Exception e) {
										JOptionPane.showMessageDialog( window.getParent(), "An error occurred during checking deadlock.","Checking Deadlock Error", JOptionPane.ERROR_MESSAGE);
										e.printStackTrace();
									}
								}
							}).start();*/
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
			Logador.getInstance().log(e.getMessage());
		}/* catch(ParsingException e) {
			JOptionPane.showMessageDialog( window.getParent(), e.getMessage(),"File Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}*/ catch (InvalidUsingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logador.getInstance().log(e.getMessage());
		}/* catch(FDRException e) {
		 	JOptionPane.showMessageDialog( window.getParent(), e.getMessage(),"Checking Non-determinism Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (WellFormedException e) {
			JOptionPane.showMessageDialog(window.getParent(), e.getMessage(),"Well-formedness Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}*/ catch (Exception e) {
			JOptionPane.showMessageDialog(window.getParent(), e.getMessage(),"Fatal Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			Logador.getInstance().log(e.getMessage());
		}
		return null;
	}

}
