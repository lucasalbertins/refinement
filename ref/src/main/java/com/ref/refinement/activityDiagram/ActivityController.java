package com.ref.refinement.activityDiagram;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import com.ref.adapter.astah.Activity;
import com.ref.adapter.astah.ActivityDiagram;
import com.ref.exceptions.FDRException;
import com.ref.exceptions.ParsingException;
import com.ref.exceptions.WellFormedException;
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
	
	public void AstahInvocation(IDiagram diagram, VerificationType type) throws FDRException,ParsingException, FileNotFoundException, UnsupportedEncodingException, WellFormedException{
		boolean wellformed = WellFormedness.WellFormed();
		
		if (wellformed) {
			progressBar = new CheckingProgressBar();
			Activity activity = new Activity(((IActivityDiagram) diagram).getActivity());
			ActivityDiagram activityDiagram = new ActivityDiagram( (IActivityDiagram) diagram);
			ADParser parser = new ADParser(activity, diagram.getName(),activityDiagram);
			String diagramCSP = parser.parserDiagram();
			
			String fs = System.getProperty("file.separator");
			String uh = System.getProperty("user.home");
			File directory = new File(uh+fs+"TempAstah");
			directory.mkdirs();
			PrintWriter writer;
			
		
			writer = new PrintWriter(uh + fs + "TempAstah" + fs + activity + ".csp", "UTF-8");
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
			} else {
				progressBar.setNewTitle("Checking non-determinism");
				progressBar.setAssertion(1);
			} 
			
		}else {
			//enviar mensagem de not wellformed
		}
		
	}
	
}
