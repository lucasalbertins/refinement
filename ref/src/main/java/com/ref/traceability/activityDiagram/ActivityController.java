package com.ref.traceability.activityDiagram;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

import com.change_vision.jude.api.inf.model.IActivityDiagram;
import com.change_vision.jude.api.inf.model.IDiagram;
import com.ref.astah.adapter.Activity;
import com.ref.astah.adapter.ActivityDiagram;
import com.ref.astah.adapter.AdapterUtils;
import com.ref.astah.traceability.CounterExampleAstah;
import com.ref.exceptions.FDRException;
import com.ref.exceptions.ParsingException;
import com.ref.exceptions.WellFormedException;
import com.ref.fdr.FdrWrapper;
import com.ref.interfaces.activityDiagram.IActivity;
import com.ref.parser.activityDiagram.ADParser;
import com.ref.parser.activityDiagram.ADUtils;
import com.ref.ui.CheckingProgressBar;
import com.ref.wellformedness.WellFormedness;


public class ActivityController {
//criar método de checar deadlock e determinismo
//antes de traduzir verificar se ta certo(wellformedness)
	//public CheckingProgressBar progressBar;
	public static enum VerificationType { DEADLOCK, DETERMINISM};
	
	public void AstahInvocation(IDiagram diagram, VerificationType type, CheckingProgressBar progressBar) throws FDRException,ParsingException, FileNotFoundException, UnsupportedEncodingException, WellFormedException{
		boolean wellformed = WellFormedness.WellFormed();
		
		if (wellformed) {
			//progressBar = new CheckingProgressBar();
			Activity activity = new Activity(((IActivityDiagram) diagram).getActivity());
			ActivityDiagram activityDiagram = new ActivityDiagram( (IActivityDiagram) diagram);
			activity.setActivityDiagram(activityDiagram);
			ADParser parser = new ADParser(activity, diagram.getName(),activityDiagram);
			String diagramCSP = parser.parserDiagram();
			
			String fs = System.getProperty("file.separator");
			String uh = System.getProperty("user.home");
			File directory = new File(uh+fs+"TempAstah");
			directory.mkdirs();
			PrintWriter writer;
			
		
			writer = new PrintWriter(uh + fs + "TempAstah" + fs + ADUtils.nameResolver(activity.getName()) + ".csp", "UTF-8");
			writer.print(diagramCSP);
			writer.flush();
			writer.close();
			
			List<String> traceCounterExample = null;
			if (type == VerificationType.DEADLOCK) {
				//progressBar.setNewTitle("Checking deadlock");
				//progressBar.setAssertion(0);
				AdapterUtils.resetStatics();
				try {
					traceCounterExample = FdrWrapper.getInstance().checkDeadlock(uh + fs + "TempAstah" + fs + ADUtils.nameResolver(((IActivityDiagram) diagram).getName()) + ".csp", parser, diagram.getName(), progressBar);
				} catch (Exception e) {
					AdapterUtils.resetStatics();
					throw new FDRException("An error occurred during checking deadlock.");
				}
			} else {
				//progressBar.setNewTitle("Checking non-determinism");
				//progressBar.setAssertion(1);
				AdapterUtils.resetStatics();
				try {
					traceCounterExample = FdrWrapper.getInstance().checkDeterminism(uh + fs + "TempAstah" + fs + ADUtils.nameResolver(((IActivityDiagram) diagram).getName()) + ".csp", parser, diagram.getName(), progressBar);
				} catch (Exception e) {
					AdapterUtils.resetStatics();
					throw new FDRException("An error occurred during checking non-determinism.");
				}
			} 
			
			
			if (traceCounterExample!=null && !traceCounterExample.isEmpty()) {//se tiver trace
				CounterExampleBuilder cb = new CounterExampleBuilder(traceCounterExample, activity,parser.getAlphabetAD(),ADParser.IdSignals);
				/* responsavel por vincular os eventos do contra exemplo CSP aos IDs dos elementos do diagrama dos diagramas 
				 * */
				HashMap<IActivity, List<String>> counterExample = cb.createCounterExample(activity);//quem deve ser pintado em cada diagrama
				/* criar a cópia dos diagramas e pintar os elementos que fazem parte do contraexemplo 
				 * */
				CounterExampleAstah.createCounterExample(counterExample, diagram, type);//"copia nossa", original astah, tipo de contra exemplo
			}
			
			
		}else {
			//enviar mensagem de not wellformed
		}
		
	}
	
}
