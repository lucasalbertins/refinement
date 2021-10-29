package com.ref.traceability.activityDiagram;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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
import com.ref.parser.activityDiagram.ADAlphabet;
import com.ref.parser.activityDiagram.ADParser;
import com.ref.parser.activityDiagram.ADUtils;
import com.ref.traceability.CounterexampleDescriptor;
//import com.ref.traceability.CounterexampleDescriptor;
import com.ref.ui.CheckingProgressBar;
import com.ref.wellformedness.WellFormedness;


public class ActivityController {
	public static enum VerificationType { DEADLOCK, DETERMINISM, ROBOCHART };
	private static Properties prop;

	public static boolean firstInteration = true;

	public static final String FDR3_PROPERTY_FILE = System.getProperty("user.home") + System.getProperty("file.separator") + "ref.properties";
	public static final String FDR3_LOCATION_PROPERTY = "fdr3_location";
	public static final String FDR3_JAR_LOCATION_PROPERTY = "fdr3_jar_location";

	public static final String ROBOCHART_LOCATION_PROPERTY = "robochart_location";
	public static final String ROBOCHART_CSP_LOCATION_PROPERTY = "robochart_csp_location";

	private static ActivityController controller;

	private String robochartFolder;
	private String roboInclude;
	
	private List<String> eventsFdr = new ArrayList<>();
	
	public String getRobochartFolder() {
		return robochartFolder;
	}

	public void setRobochartFolder(String robochartFolder) {
		this.robochartFolder = robochartFolder;
	}

	public String getRoboInclude() {
		return roboInclude;
	}

	public void setRoboInclude(String roboInclude) {
		this.roboInclude = roboInclude;
	}
	
	public List<String> getEventsFdr() {
		return eventsFdr;
	}

	public void setEventsFdr(List<String> eventsFdr) {
		this.eventsFdr = eventsFdr;
	}

	private ActivityController() throws IOException {
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
			System.out.println("Gerado em " + prop.getProperty(ActivityController.ROBOCHART_LOCATION_PROPERTY ));
			if (prop.getProperty(ActivityController.ROBOCHART_CSP_LOCATION_PROPERTY) != null) {
				updateRobochartInfo(prop.getProperty(ActivityController.ROBOCHART_CSP_LOCATION_PROPERTY));				
			}
		}
	}

	public static ActivityController getInstance() throws IOException {
		if (controller == null) {
			controller = new ActivityController();
		}
		return controller;
	}

	public void AstahInvocation(IDiagram diagram, VerificationType type, CheckingProgressBar progressBar) throws Exception{

		Activity activity = new Activity(((IActivityDiagram) diagram).getActivity());
			ActivityDiagram activityDiagram = new ActivityDiagram( (IActivityDiagram) diagram);
			activity.setActivityDiagram(activityDiagram);

			HashMap<IActivity, List<String>> counterExample = checkProperty(activity,activityDiagram,type,progressBar);
			if(counterExample != null) {
				CounterExampleAstah.createCounterExample(counterExample, diagram, type);//"our copy", astah original, counter example type
			}
	}

	public HashMap<IActivity, List<String>> checkProperty(Activity activity,
			ActivityDiagram activityDiagram, VerificationType type, CheckingProgressBar progressBar) throws Exception{
		WellFormedness.WellFormed();

		settingFDR();
		String roboPath = prop.getProperty(ROBOCHART_CSP_LOCATION_PROPERTY);
		ADParser parser = new ADParser(activity, activityDiagram.getName(), activityDiagram, roboPath);
		String diagramCSP = parser.parserDiagram();

		String fs = System.getProperty("file.separator");
		String uh = System.getProperty("user.home");


		//File directory = new File(uh + fs + "TempAstah");
		//File directory = new File(uh + fs + "Google Drive" + fs + "UFRPE 2020" + fs + "Mestrado PPGIA" + fs + "RoboChart Models" + fs + "SimFW" + fs + "csp-gen" + fs + "defs");
		//directory.mkdirs();
		PrintWriter writer;

//		String robochartFolder = uh + fs + "Google Drive" + fs + "UFRPE 2020" + fs + "Mestrado PPGIA" + fs + "RoboChart Models" + fs + "SimFW" + fs + "csp-gen" + fs + "defs";
		String caminho = getRobochartFolder() + fs + ADUtils.nameResolver(activity.getName()) + ".csp";
		writer = new PrintWriter(caminho, "UTF-8");
		
		writer.print(diagramCSP);
		writer.flush();
		writer.close();
		
//		String cspFile = getRobochartFolder() + fs + ADUtils.nameResolver(activity.getName()) + ".csp";
//		String procName = "P_PathPlanningSM";
//		String alpha = FdrWrapper.getInstance().processAlphabet2(cspFile, procName);
//		System.out.println("Alphabet: = " + alpha);

		List<String> traceCounterExample = null;
		if (type == VerificationType.DEADLOCK) {
			AdapterUtils.resetStatics();
			try {
				traceCounterExample = FdrWrapper.getInstance().checkDeadlock(
						uh + fs + "TempAstah" + fs + ADUtils.nameResolver(activityDiagram.getName()) + ".csp", parser,
						activityDiagram.getName(), progressBar);
			} catch (Exception e) {
				AdapterUtils.resetStatics();
				throw new FDRException("An error occurred during checking deadlock.");
			}
		} else if (type == VerificationType.DETERMINISM) {
			AdapterUtils.resetStatics();
			try {
				traceCounterExample = FdrWrapper.getInstance().checkDeterminism(
						uh + fs + "TempAstah" + fs + ADUtils.nameResolver(activityDiagram.getName()) + ".csp", parser,
						activityDiagram.getName(), progressBar);
			} catch (Exception e) {
				AdapterUtils.resetStatics();
				throw new FDRException("An error occurred during checking non-determinism.");
			}
		} else {
			AdapterUtils.resetStatics();
			try {
				traceCounterExample = FdrWrapper.getInstance().checkRobochartProperty(
					getRobochartFolder() + fs + ADUtils.nameResolver(activityDiagram.getName()) + ".csp", parser,
					activityDiagram.getName(), progressBar);
			} catch (Exception e) {
				AdapterUtils.resetStatics();
				throw new FDRException("An error occurred during checking robochart property.");
			}
		}

		if (traceCounterExample != null && !traceCounterExample.isEmpty()) {// If there is a trace
			eventsFdr.clear();
			for (String evento : traceCounterExample) {
//				System.out.println(evento);
				eventsFdr.add(evento);
			}
			CounterexampleDescriptor cd = new CounterexampleDescriptor(eventsFdr);	
		}
		
		return null;

	}

	private void settingFDR() throws FDRException {
		File fdrProperty = new File(FDR3_PROPERTY_FILE);

		if (fdrProperty.exists()) {
			Properties prop = new Properties();
			try {
				prop.load(new FileInputStream(fdrProperty));
			} catch (IOException e1) {
				e1.printStackTrace();
				throw new FDRException(e1.getMessage());
			}
			FdrWrapper wrapper = FdrWrapper.getInstance();
			String pathFDR = prop.getProperty(FDR3_JAR_LOCATION_PROPERTY);
			String pathCSP = prop.getProperty(ROBOCHART_CSP_LOCATION_PROPERTY);
			if (!pathFDR.isEmpty() || !pathCSP.isEmpty()) {
				File fdrLocation = new File(pathFDR);
				File cspLocation = new File(pathCSP);

				if (fdrLocation.exists()) {
					wrapper.loadFDR(pathFDR);
					if (firstInteration) { // carrega as classes um unica vez
						try {
							wrapper.loadClasses();
						} catch (MalformedURLException | ClassNotFoundException e) {
							e.printStackTrace();
							throw new FDRException(e.getMessage());
						}
						firstInteration = false;
					}
				} else if (cspLocation.exists()) {
					wrapper.loadFDR(pathCSP);
					if (firstInteration) { // carrega as classes um unica vez
						try {
							wrapper.loadClasses();
						} catch (MalformedURLException | ClassNotFoundException e) {
							e.printStackTrace();
							throw new FDRException(e.getMessage());
						}
						firstInteration = false;
					}
				} else {
					throw new FDRException("FDR not found, set FDR location in Tools > Properties Plugin Configuration > FDR Location.");
				}
			} else {
				throw new FDRException("FDR not found, set FDR location in Tools > Properties Plugin Configuration > FDR Location.");
			}
		} else {
			throw new FDRException("FDR not found, set FDR location in Tools > Properties Plugin Configuration > FDR Location.");
		}
	}

	public void setFDRLocation(String location) throws FDRException {
		String filename = null;
		if (System.getProperty("os.name").startsWith("Mac OS X")) {
			filename = location + "/Contents/Frameworks/fdr.jar";

		} else if (System.getProperty("os.name").contains("Win")) {
			filename = location + "\\bin\\fdr.jar";
		}else {
			filename = location + "/lib/fdr.jar";
		}
		File fdrlib = new File(filename);
		if (!fdrlib.exists()) {
			throw new FDRException("Library fdr.jar not found!");

		} else {
			try {
				prop.setProperty(FDR3_LOCATION_PROPERTY, location);
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
		}
	}

	public String getFDRLocation() {
		return prop.getProperty(FDR3_LOCATION_PROPERTY);
	}

	//--------------------------------------------------------------------------

	public void setRobochartLocation(String location) throws FDRException {

		updateRobochartInfo(location);

		String filename = null;
		if (System.getProperty("os.name").startsWith("Mac OS X")) {
			filename = getRobochartFolder() + "/" + getRoboInclude();

		} else if (System.getProperty("os.name").contains("Win")) {
			filename = getRobochartFolder() + "\\" + getRoboInclude();
		} else { //LINUX
			filename = getRobochartFolder() + "/" + getRoboInclude();
		}
		File cspfile = new File(filename);
		if (!cspfile.exists()) {
			throw new FDRException(getRoboInclude() + " not found!");
		} else {
			try {
				prop.setProperty(ROBOCHART_LOCATION_PROPERTY, getRobochartFolder());
				prop.setProperty(ROBOCHART_CSP_LOCATION_PROPERTY, filename);
				prop.store(new FileOutputStream(new File(FDR3_PROPERTY_FILE)), null);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public String getRobochartLocation() {
		return prop.getProperty(ROBOCHART_CSP_LOCATION_PROPERTY);
	}
	
	public void updateRobochartInfo(String location) {
		File robolib = new File(location);

		setRoboInclude(robolib.getName());
		setRobochartFolder(robolib.getParent());
	}

}
