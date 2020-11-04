package com.ref.refinement.activityDiagram;

import java.io.File;
import java.io.PrintWriter;

import javax.swing.JOptionPane;

import com.ref.adapter.astah.AstahAdapter;
import com.ref.exceptions.FDRException;
import com.ref.exceptions.ParsingException;
import com.ref.fdr.FdrWrapper;
import com.ref.parser.activityDiagram.ADParser;
import com.ref.ui.CheckingProgressBar;
import com.ref.wellformedness.WellFormedness;
import com.change_vision.jude.api.inf.model.IActivityDiagram;
import com.change_vision.jude.api.inf.model.IDiagram;


public class ActivityController {
//criar método de checar deadlock e determinismo
//antes de traduzir verificar se ta certo(wellformedness)
	public CheckingProgressBar progressBar;
	public static enum VerificationType { DEADLOCK, DETERMINISM};
	
	public void AstahInvocation(IDiagram diagram, VerificationType type) throws FDRException,ParsingException{
		progressBar = new CheckingProgressBar();
		boolean wellformed = WellFormedness.WellFormed();
		
		if (wellformed) {
			//astahAdapter.setActivity(((IActivityDiagram) diagram).getActivity());
			ADParser parser = new ADParser(((IActivityDiagram) diagram).getActivity(), diagram.getName(), (IActivityDiagram) diagram);
			String diagramCSP = parser.parserDiagram();
			
			String fs = System.getProperty("file.separator");
			String uh = System.getProperty("user.home");
			File directory = new File(uh+fs+"TempAstah");
			directory.mkdirs();

			PrintWriter writer = new PrintWriter(uh + fs + "TempAstah" + fs + ((IActivityDiagram) diagram).getActivity() + ".csp", "UTF-8");
			writer.print(diagramCSP);

			writer.flush();
			writer.close();
			
			if (type == VerificationType.DEADLOCK) {
				progressBar.setNewTitle("Checking deadlock");
				progressBar.setAssertion(1);
				
				try {
					FdrWrapper.getInstance().checkDeadlock(uh + fs + "TempAstah" + fs + ((IActivityDiagram) diagram).getActivity() + ".csp", parser, diagram.getName(), progressBar);
				} catch (Exception e) {
					throw new FDRException("An error occurred during checking deadlock.");
				}

				/*new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							FdrWrapper.getInstance().checkDeadlock(uh + fs + "TempAstah" + fs + ((IActivityDiagram) diagram).getActivity() + ".csp", parser, diagram.getName(), progressBar);
						} catch (Exception e) {
							JOptionPane.showMessageDialog( window.getParent(), "An error occurred during checking deadlock.","Checking deadlock Error", JOptionPane.ERROR_MESSAGE);
							e.printStackTrace();
						}
					}
				}).start();*/
			} else {
				progressBar.setNewTitle("Checking non-determinism");
				progressBar.setAssertion(1);
				
				/*new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							FdrWrapper.getInstance().checkDeadlock(uh + fs + "TempAstah" + fs + ((IActivityDiagram) diagram).getActivity() + ".csp", parser, diagram.getName(), progressBar);
						} catch (Exception e) {
							JOptionPane.showMessageDialog( window.getParent(), "An error occurred during checking non-determinism.","Checking non-determinism Error", JOptionPane.ERROR_MESSAGE);
							e.printStackTrace();
						}
					}
				}).start();*/
			} 
			
		}else {
			//enviar mensagem de not wellformed
		}
		
	}
	
}
