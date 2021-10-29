package com.ref.parser.activityDiagram;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ref.astah.adapter.ActivityNode;
import com.ref.exceptions.ParsingException;
import com.ref.fdr.FdrWrapper;
import com.ref.interfaces.activityDiagram.IAction;
import com.ref.interfaces.activityDiagram.IActivity;
import com.ref.interfaces.activityDiagram.IActivityDiagram;
import com.ref.interfaces.activityDiagram.IActivityNode;
import com.ref.traceability.activityDiagram.ActivityController;


public class ADParser {

    private IActivity ad;
    private IActivityDiagram adDiagram;

    public int countGet_ad;
    public int countSet_ad;
    public int countCe_ad;
    public int countOe_ad;
    public int countUpdate_ad;
    public int countClear_ad;
    public int limiteInf;
    public int limiteSup;
    public int countUntil_ad;
    public int countAny_ad;
    
    private static String firstDiagram = null;
    public static boolean containsCallBehavior = false;
    public static HashMap<String, Integer> countCall = new HashMap<>();
    public static HashMap<String,List<Pair<String,String>>> countcallBehavior = new HashMap<>();
    private HashMap<Pair<IActivity,String>, ArrayList<String>> alphabetNode;
    private HashMap<Pair<IActivity,String>, ArrayList<String>> parameterAlphabetNode;
    public HashMap<Pair<IActivity,String>, String> syncChannelsEdge;            //ID flow, channel
    public HashMap<Pair<IActivity,String>, String> syncObjectsEdge;
    public static Set<String> alphabetPool = new HashSet<String>();
    public static List<IActivity> callBehaviourList = new ArrayList<>();
    private HashMap<String, String> objectEdges;                //channel; name
    private List<IActivityNode> queueNode;
    private List<IActivityNode> queueRecreateNode;
    private static List<IActivity> callBehaviourListCreated = new ArrayList<>();
    private List<String> eventChannel;
    private List<String> lockChannel;
    private List<String> allInitial;
    private ArrayList<String> alphabetAllInitialAndParameter;
    private HashMap<String, String> parameterNodesInput;        //name; type
    private HashMap<String, String> parameterNodesOutput;
    private HashMap<String, String> parameterNodesOutputObject; //name; object
    private List<Pair<String, Integer>> callBehaviourNumber;     //name; int
    private Map<Pair<String, String>,String> memoryLocal;             //nameNode, nameObject
    private List<Pair<String, String>> memoryLocalChannel;
    private List<ArrayList<String>> unionList;
    private HashMap<String, String> typeUnionList;
    private static HashMap<String, List<String>> callBehaviourInputs = new HashMap<>(); //name; List inputs
    private static HashMap<String, List<String>> callBehaviourOutputs = new HashMap<>(); //name; List outputs
    private static List<Pair<String, Integer>> countSignal = new ArrayList<>();
    private static List<Pair<String, Integer>> countAccept = new ArrayList<>();
    private static List<Pair<String, Integer>> countAction = new ArrayList<>();
    private static HashMap<String,List<IActivity>> signalChannels = new HashMap<>();
    private List<String> signalChannelsLocal;
    private List<String> localSignalChannelsSync;
    private List<String> createdSignal;
    private List<String> createdAccept;
    private List<String> createdAction;
    private HashMap<String,Integer> allGuards;
    public static HashMap<String,Integer> IdSignals = new HashMap<>();
    
    private ADAlphabet alphabetAD;
    public ADDefineChannels dChannels;
    public ADDefineTypes dTypes;
    public ADDefineMemories dMemories;
    public ADDefineMainNodes dMainNodes;
    public ADDefineNodesActionAndControl dNodesActionAndControl;
    public ADDefineTokenManager dTokenManager;
    public ADDefineProcessSync dProcessSync;
    public ADDefinePool dPool;
    
	////////////////////////////////////////////////////////////////////////////////////////
	public List<String> robo;
	public List<String> eventsUntil;
	public HashMap<String, String> untilList;
	public List<String> waitAccept;
	public List<String> listUnion;
	public StringBuilder alphabetUnion;
	////////////////////////////////////////////////////////////////////////////////////////

    public ADParser(IActivity ad, String nameAD, IActivityDiagram adDiagram) {
        this.ad = ad;
        this.adDiagram = adDiagram;
        setName(nameAD);
        setFirstDiagram();
        this.countGet_ad = 1;
        this.countSet_ad = 1;
        this.countCe_ad = 1;
        this.countOe_ad = 1;
        this.countUpdate_ad = 1;
        this.countClear_ad = 1;
        this.limiteInf = 99;
        this.limiteSup = -99;
        this.alphabetNode = new HashMap<>();
        this.parameterAlphabetNode = new HashMap<>();
        syncChannelsEdge = new HashMap<>();
        syncObjectsEdge = new HashMap<>();
        objectEdges = new HashMap<>();
        queueNode = new ArrayList<>();
        queueRecreateNode = new ArrayList<>();
        eventChannel = new ArrayList<>();
        lockChannel = new ArrayList<>();
        allInitial = new ArrayList<>();
        alphabetAllInitialAndParameter = new ArrayList<>();
        parameterNodesInput = new HashMap<>();
        parameterNodesOutput = new HashMap<>();
        parameterNodesOutputObject = new HashMap<>();
        memoryLocal = new HashMap<Pair<String,String>,String>();
        memoryLocalChannel = new ArrayList<>();
        unionList = new ArrayList<>();
        typeUnionList = new HashMap<>();
        callBehaviourNumber = new ArrayList<>();
        localSignalChannelsSync = new ArrayList<>();
        signalChannelsLocal = new ArrayList<>();
        createdSignal = new ArrayList<>();
        createdAccept = new ArrayList<>();
        createdAction = new ArrayList<>();
        allGuards = new HashMap<>();
        this.countUntil_ad = 0;
        this.countAny_ad = 0;
        this.robo = new ArrayList<String>();
        this.eventsUntil = new ArrayList<>();
        this.untilList = new HashMap<>();
        this.waitAccept = new ArrayList<>();
        this.listUnion = new ArrayList<>();
        this.alphabetUnion = new StringBuilder();
        
    }

    private void setFirstDiagram() {
        if (firstDiagram == null) {
            firstDiagram = ad.getId();
        }
    }

    public void checkCountCallInitial() {
        ADUtils adUtils = defineADUtils();

        if (countCall.get(adUtils.nameDiagramResolver(ad.getName())) == null) {
            adUtils.addCountCall(adUtils.nameDiagramResolver(ad.getName()));
        }
    }

    public void clearBuffer() {
        this.countGet_ad = 1;
        this.countSet_ad = 1;
        this.countCe_ad = 1;
        this.countOe_ad = 1;
        this.countUpdate_ad = 1;
        this.countClear_ad = 1;
        this.limiteInf = 99;
        this.limiteSup = -99;
        this.alphabetNode = new HashMap<>();
        this.parameterAlphabetNode = new HashMap<>();
        syncChannelsEdge = new HashMap<>();
        syncObjectsEdge = new HashMap<>();
        objectEdges = new HashMap<>();
        queueNode = new ArrayList<>();
        queueRecreateNode = new ArrayList<>();
        eventChannel = new ArrayList<>();
        lockChannel = new ArrayList<>();
        allInitial = new ArrayList<>();
        alphabetAllInitialAndParameter = new ArrayList<>();
        parameterNodesInput = new HashMap<>();
        parameterNodesOutput = new HashMap<>();
        parameterNodesOutputObject = new HashMap<>();
        memoryLocal = new HashMap<Pair<String,String>,String>();
        memoryLocalChannel = new ArrayList<>();
//        unionList = new ArrayList<>();
        typeUnionList = new HashMap<>();
        callBehaviourInputs = new HashMap<>();
        callBehaviourNumber = new ArrayList<>();
        localSignalChannelsSync = new ArrayList<>();
        signalChannelsLocal = new ArrayList<>();
        createdSignal = new ArrayList<>();
        createdAccept = new ArrayList<>();
        createdAction = new ArrayList<>();
        resetStatic();
        allGuards = new HashMap<>();
        firstDiagram = ad.getId(); //set first diagram
        this.countUntil_ad = 1;
        this.countAny_ad = 1;
    }

    public void resetStatic() {
        firstDiagram = null;
        containsCallBehavior = false;
        countCall = new HashMap<>();
        countcallBehavior = new HashMap<>();
        callBehaviourList = new ArrayList<>();
        callBehaviourInputs = new HashMap<>();
        callBehaviourOutputs = new HashMap<>();
        callBehaviourListCreated = new ArrayList<>();
        countSignal = new ArrayList<>();
        countAccept = new ArrayList<>();
        countAction = new ArrayList<>();
        signalChannels = new HashMap<>();
        alphabetPool = new HashSet<String>();
    }

    private void setName(String nameAD) {
        this.ad.setName(nameAD);
    }

    /*
     * Master Function
     * */

    public String parserDiagram() throws Exception, ParsingException {
    	
        boolean reset = false;
        String check = "";
        String callBehaviour = "";
        ADUtils adUtils = defineADUtils();
        String partitionName;
		try {
			partitionName = this.ad.getPartitions()[0].getSubPartitions()[0].getName();
		} catch (Exception e) {
			throw new ParsingException(
					"The module should have a partition. \n Please, insert and try again.");
		}
		
        if (countCall.size() == 0) { //If first occurrence
            check = //"\nassert MAIN :[deadlock free]" +
                    //"\nassert MAIN :[divergence free]" +
                    //"\nassert MAIN :[deterministic]" +
                    "\nassert Prop_" + adUtils.nameDiagramResolver(ad.getName()) + " [T= P_" + partitionName;
            reset = true;
        }
        
        defineCallBehaviourList();
        
        checkCountCallInitial();
        
        definePoolAlphabet();

        String nodes = defineNodesActionAndControl();

        for (IActivity adCalling: callBehaviourList) {
            if (!callBehaviourListCreated.contains(adCalling)) {
                callBehaviourListCreated.add(adCalling);
                ADParser adParser = new ADParser(adCalling, adCalling.getActivityDiagram().getName(), adCalling.getActivityDiagram());
                callBehaviour += "\n" + (adParser.parserDiagram());
                alphabetAD = new ADCompositeAlphabet(ad);
                this.alphabetAD.add(adParser.getAlphabetAD());
            }
            listUnion.add("alphabet_Astah_" + adUtils.nameDiagramResolver(adCalling.getName()));
        }  
        listUnion.add("alphabet_Astah_" + adUtils.nameDiagramResolver(ad.getName()));
        if (callBehaviourList.size() > 0 && firstDiagram.equals(ad.getId())) {
        	for (int i = 0; i < callBehaviourList.size(); i++) {
        		alphabetUnion.append("union(");
        	}			
        	int u = 1;
        	for (int i = 0; i < listUnion.size(); i++) {
        		alphabetUnion.append(listUnion.get(i));  				
        		if (u < 2) {
        			alphabetUnion.append(", ");
        			u++;
        		} else if (u >= 2 && i < listUnion.size()-1) {
        			alphabetUnion.append("), ");
        			u = 2;
        		} else {
        			alphabetUnion.append(")");
        		}				
        	}	
		} else {
			alphabetUnion.append("alphabet_Astah_" + adUtils.nameDiagramResolver(ad.getName()));
		}
		
        String channel = defineChannels();
        String main = defineMainNodes();
        String type = defineTypes();
        String tokenManager = defineTokenManager();
        String memory = defineMemories();
        String processSync = defineProcessSync();
//        String pool = definePool();
////////////////////////////////////////////////////////////////////////////////////////        
//        String procName = "P_" + partitionName;
//        String fs = System.getProperty("file.separator");
//        String cspFile = ActivityController.getInstance().getRobochartFolder() + fs + adUtils.nameDiagramResolver(ad.getName())  + ".csp";
////////////////////////////////////////////////////////////////////////////////////////        
        adUtils = defineADUtils();
        HashMap<String, String> parameterValueDiagram = adUtils.getParameterValueDiagram("");
//        String robochart = parameterValueDiagram.get("robochart");
        String robochart_alphabet = parameterValueDiagram.get("robochart_alphabet");
//        String robochart_alphabet = FdrWrapper.getInstance().processAlphabet2(cspFile, procName);
//        if (robochart != null && !robochart.equals("")) {
//			robochart = "include " + robochart + "\n";
        String robochart = "";
			try {
				robochart = "include \"" + ActivityController.getInstance().getRoboInclude() + "\"\n";
			} catch (IOException e) {
				throw new ParsingException("Specify the Robochart file providing the property \"robochart = {robochartFilePath};\" in the definition field.");
			}
//		} else {
//			throw new ParsingException("Specify the Robochart file providing the property \"robochart = {robochartFilePath};\" in the definition field.");
//		}

        String n_recurse = "\n\nNRecurse(S, P) = |~| ev : S @ ev -> P";
//        String wait_props = "\n\n" + adUtils.printUntilWithPins2();
        String wait_props = adUtils.printUntilWithPins2();
        
        	if (firstDiagram.equals(ad.getId())) {
				wait_props += "\n\nWAIT(alphabet,event) = \n"
    						+ "	NRecurse(diff(alphabet, {event}), WAIT(alphabet,event))\n"
    						+ "	|~|\n"
    						+ "	event -> SKIP\n\n";						
			} else {
				wait_props += "\n\n";
			}

			wait_props += "WAIT_PROCCESSES_" + adUtils.nameDiagramResolver(ad.getName()) + "(processes) = ( ||| CONTROL : processes @ CONTROL )  /\\ endDiagram_" + adUtils.nameDiagramResolver(ad.getName()) + "?id -> SKIP\n\n"
						+ "Prop_" + adUtils.nameDiagramResolver(ad.getName()) + " = PROP_" + adUtils.nameDiagramResolver(ad.getName()) + "(Wait_control_processes_" + adUtils.nameDiagramResolver(ad.getName()) + ") \\ ";							
				if (!firstDiagram.equals(ad.getId())) {
					wait_props += "alphabet_Astah_" + adUtils.nameDiagramResolver(ad.getName()) + " \n\n";							
				} else {
					wait_props += alphabetUnion.toString() + " \n\n";
				}	
			wait_props +=  adUtils.alphabetRobo(robochart_alphabet) + "\n\n";
			
			if (countAny_ad > 0 && countUntil_ad > 0) {
				wait_props +=
						"PROP_" + adUtils.nameDiagramResolver(ad.getName()) + "(processes) = (MAIN [|{|begin, end, chaos, endDiagram_" + adUtils.nameDiagramResolver(ad.getName()) + "|}|] WAIT_PROCCESSES_" + adUtils.nameDiagramResolver(ad.getName()) + "(processes) ) \\ {|begin, end, chaos|}\n\n"
								+ adUtils.printUntils()
								+ adUtils.printAny();
			} else if (countUntil_ad > 0) {
				wait_props +=
						"PROP_" + adUtils.nameDiagramResolver(ad.getName()) + "(processes) = (MAIN [|{|begin, end, endDiagram_" + adUtils.nameDiagramResolver(ad.getName()) + "|}|] WAIT_PROCCESSES_" + adUtils.nameDiagramResolver(ad.getName()) + "(processes) ) \\ {|begin, end|}\n\n"
								+ adUtils.printUntils();
			} else if (countAny_ad > 0) {
				wait_props +=
						"PROP_" + adUtils.nameDiagramResolver(ad.getName()) + "(processes) = (MAIN [|{|chaos, endDiagram_" + adUtils.nameDiagramResolver(ad.getName()) + "|}|] WAIT_PROCCESSES_" + adUtils.nameDiagramResolver(ad.getName()) + "(processes) ) \\ {|chaos|}\n\n"
								+ adUtils.printAny();
			} else {
				wait_props +=
						"PROP_" + adUtils.nameDiagramResolver(ad.getName()) + "(processes) = (MAIN)\n\n";
			}
			
			wait_props += adUtils.printControlProcesses();       	

        //    	String head_prop_nodes = "\nNode_" + adUtils.nameDiagramResolver(ad.getName()) + "(id) = composeNodes(id)\r\n";
	        String wait_props_nodes = "\r\n"
	        		+ "Node_" + adUtils.nameDiagramResolver(ad.getName()) + "(id) = composeNodes_" + adUtils.nameDiagramResolver(ad.getName()) + "(id)\r\n\n"
	        		+ "composeNodes_" + adUtils.nameDiagramResolver(ad.getName()) + "(id) = \r\n"
	        		+ "	let\r\n"
	        		+ "	    alphabet_" + adUtils.nameDiagramResolver(ad.getName()) + "_s = seq(alphabet_" + adUtils.nameDiagramResolver(ad.getName()) + ")\r\n"
	        		+ "		composeNodes_(id,<ev>,_) = ProcessDiagram_" + adUtils.nameDiagramResolver(ad.getName()) + "(id,ev)\r\n"
	        		+ "		composeNodes_(id,<ev>^tail,past) = \r\n"
	        		+ "			ProcessDiagram_" + adUtils.nameDiagramResolver(ad.getName()) + "(id,ev) \r\n"
	        		+ "				[|union(diff(AlphabetDiagram_" + adUtils.nameDiagramResolver(ad.getName()) + "(id,ev),past),{endDiagram_" + adUtils.nameDiagramResolver(ad.getName()) + ".id})|] \r\n"
	        		+ "			( composeNodes_(id,tail,union(past,AlphabetDiagram_" + ad.getName() + "(id,ev))) )\r\n"
	        		+ "	within \r\n"
	        		+ "		composeNodes_(id,alphabet_" + adUtils.nameDiagramResolver(ad.getName()) + "_s,{})\n\n";  
    	
    	//---------------------------------------------------------------------------------------------------------------
    	
        String parser = (firstDiagram.equals(ad.getId())?"transparent normal\n":"")+
        		(firstDiagram.equals(ad.getId())?robochart:"") +
        		type +
                channel +
                main +
                processSync +
                nodes +
                memory +
                tokenManager +
                /*lock +*/
//                pool +            
//                (firstDiagram.equals(ad.getId())?"\nAlphabetPool = {|endDiagram_"+ADUtils.nameResolver(ad.getName())+(!alphabetPool.isEmpty()?","+alphabetPoolToString():"")+"|}\n":"")+
                callBehaviour +
                check +
                (firstDiagram.equals(ad.getId())?n_recurse:"") +
                wait_props +
                wait_props_nodes;
//                (firstDiagram.equals(ad.getId())?check_prop_nodes:"");

        //reset the static values
        if (reset) {
            resetStatic();
        }

        //alphabet used on the counterexample
        if(alphabetAD == null) {
        	this.alphabetAD = new ADLeafAlphabet(ad);
        }
        alphabetAD.setAlphabetAD(alphabetNode);
        alphabetAD.setSyncChannelsEdge(syncChannelsEdge);
        alphabetAD.setSyncObjectsEdge(syncObjectsEdge);
        alphabetAD.setParameterAlphabetNode(parameterAlphabetNode);
        
        return parser;
    }


	public ADAlphabet getAlphabetAD() {
		return this.alphabetAD;
	}

	public static void addCountCallBehavior(String idKey,String idCalling,String nameCalling) {
        int i = 1;
        List<Pair<String,String>> aux1;
        if (countcallBehavior.containsKey(idKey)) {//If already has the CBA
            aux1 = countcallBehavior.get(idKey);
            List<String>aux2 = new ArrayList<String>();
            for(Pair<String,String> a:aux1) {//get the ids
            	aux2.add(a.getKey());
            }
            for(String diagram:aux2) {//sweeps throught the calling diagram 
            	if(diagram.equals(idCalling)){
            		break;
            	}
            	i++;
            }
            if(i > aux1.size()) {//If it isn't there, add it
            	Pair<String,String> pair = new Pair<String, String>(idCalling, nameCalling);
            	aux1.add(pair);
            	countcallBehavior.replace(idKey,aux1);	
            }
        } else {//IF the CBA doesn't exist
        	aux1 = new ArrayList<Pair<String,String>>();
        	Pair<String,String> pair = new Pair<String, String>(idCalling, nameCalling);
        	aux1.add(pair);
            countcallBehavior.put(idKey, aux1);
        }
    }
    
    private void defineCallBehaviourList() throws ParsingException {
    	if(countCall.size() == 0) {//Gets the CBAs of the first diagram
        	for (IActivityNode activityNode : ad.getActivityNodes()) {//Get all nodes
////////////////////////////////////////////////////////////////////////////////////////        		
        		if (activityNode instanceof IAction) {
                    if (((IAction) activityNode).isCallBehaviorAction()) {
                    	if (!activityNode.hasStereotype("ANY")) {
                    		if(((IAction) activityNode).getCallingActivity() == null) {
                        		throw new ParsingException("Call Behavior Action "+activityNode.getName() +" not linked\n");
                        	}else {
                        		callBehaviourList.add(((IAction) activityNode).getCallingActivity());
                        		addCountCallBehavior(((IAction) activityNode).getCallingActivity().getId(), activityNode.getId(),activityNode.getName());
                        	}
                    	}
                    }
        		}
////////////////////////////////////////////////////////////////////////////////////////     
        		
//        		if (activityNode instanceof IAction) {
//                    if (((IAction) activityNode).isCallBehaviorAction()) {
//                    	if(((IAction) activityNode).getCallingActivity() == null) {
//                    		throw new ParsingException("Call Behavior Action "+activityNode.getName() +" not linked\n");
//                    	}else {
//                    		if(!containsCBA(((IAction) activityNode).getCallingActivity())) {
//		                		callBehaviourList.add(((IAction) activityNode).getCallingActivity());
//                    		}
//                    		addCountCallBehavior(((IAction) activityNode).getCallingActivity().getId(), activityNode.getId(),activityNode.getName());
//	                		
//                    	}
//                    }
//        		}
        	}
        	
        	boolean mudou =true;
        	List<IActivity> aux1 = new ArrayList<>();
        	List<IActivity> aux3 = new ArrayList<>();
        	aux3.addAll(callBehaviourList);
        	
        	while(mudou) {
	        	if(callBehaviourList.size() != 0) {//Gets the CBA that is inside a CBA
	        		for(IActivity CBAs: callBehaviourList) {//For each CBA
	        			for(IActivityNode adCBAs: CBAs.getActivityNodes()) {//For each node
	        				if(adCBAs instanceof IAction) {
	        					if(((IAction) adCBAs).isCallBehaviorAction() && !aux1.contains(((IAction) adCBAs).getCallingActivity())) {//se for CBA e n tiver sido add
	        						aux1.add(((IAction) adCBAs).getCallingActivity());
	        					}
	        					if(((IAction) adCBAs).isCallBehaviorAction()) {
	        						addCountCallBehavior(((IAction) adCBAs).getCallingActivity().getId(), adCBAs.getId(),adCBAs.getName());
	        					}
	        				}
	        			}
	        		}
	        	}
	        	
	        	for(IActivity CBA:aux1) {//Joins the sets
	        		if(!containsCBA(CBA)) {
	        			callBehaviourList.add(CBA);
	        		}
	        	}
	        	if(aux3.equals(callBehaviourList)) {
	        		mudou = false;
	        	}else {
	        		aux3 = callBehaviourList;
	        	}
        	}
        }
	}

	private boolean containsCBA(IActivity callingActivity) {
		for(IActivity activity: callBehaviourList) {
			if(callingActivity.getId().equals(activity.getId())) {
				return true;
			}
		}
		return false;
	}

	private void definePoolAlphabet() {
		if(firstDiagram.equals(ad.getId())) {//Gets from the first diagram
        	for (IActivityNode activityNode : ad.getActivityNodes()) {//Gets all nodes
        		if (activityNode instanceof IAction) {
                    if (((IAction) activityNode).isAcceptEventAction()||((IAction) activityNode).isSendSignalAction()) {//If is accept or send
                    	alphabetPool.add("signal_"+((IAction) activityNode).getName());
    					alphabetPool.add("accept_"+((IAction) activityNode).getName());
                    }
        		}
        	}
        	for(IActivity CBAs: callBehaviourList) {
        		for(IActivityNode sendOrAcceptSignal: CBAs.getActivityNodes()) {
        			if(sendOrAcceptSignal instanceof IAction) {
        				if(((IAction)sendOrAcceptSignal).isAcceptEventAction() ||((IAction)sendOrAcceptSignal).isSendSignalAction()) {
        					alphabetPool.add("signal_"+((IAction) sendOrAcceptSignal).getName());
        					alphabetPool.add("accept_"+((IAction) sendOrAcceptSignal).getName());
        				}
        			}
        		}
        	}
		}
	}
	
	private String alphabetPoolToString() {
		StringBuilder toString = new StringBuilder();
		String aux = "";
		for(String a:alphabetPool) {
			toString.append(a+",");
		}
		toString.replace(toString.lastIndexOf(","), toString.lastIndexOf(",")+1, "");
		aux = toString.toString();
		return aux.replace(" ", "").replace("!", "_").replace("@", "_")
                .replace("%", "_").replace("&", "_").replace("*", "_")
                .replace("(", "_").replace(")", "_").replace("+", "_")
                .replace("-", "_").replace("=", "_").replace("?", "_")
                .replace(":", "_").replace("/", "_").replace(";", "_")
                .replace(">", "_").replace("<", "_").replace("{", "_")
                .replace("}", "_").replace("|", "_").replace("\\", "_")
                .replace("\n", "_");
	}
    
    private ADUtils defineADUtils() {
        ADUtils adUtils = new ADUtils(ad, adDiagram, countCall, eventChannel, lockChannel, parameterNodesOutputObject, callBehaviourNumber,
                memoryLocal,  memoryLocalChannel, callBehaviourInputs, callBehaviourOutputs, countSignal, countAccept,
                signalChannels, localSignalChannelsSync, allGuards, createdSignal, createdAccept, syncChannelsEdge, syncObjectsEdge, objectEdges,
                signalChannelsLocal, this, robo, eventsUntil, untilList, countAction, createdAction, waitAccept);
        return adUtils;
    }

    public String defineChannels() throws ParsingException {
        ADUtils adUtils = defineADUtils();

        dChannels = new ADDefineChannels(allGuards, ad, parameterNodesInput, parameterNodesOutput,
                memoryLocal, parameterNodesOutputObject, syncObjectsEdge, objectEdges,
                eventChannel, lockChannel, firstDiagram, signalChannels, adUtils, this);

        return dChannels.defineChannels();
    }

    public String defineTypes() {
        ADUtils adUtils = defineADUtils();

        dTypes = new ADDefineTypes(ad, adDiagram, firstDiagram, countCall, alphabetNode, objectEdges, parameterNodesInput,
                parameterNodesOutput, memoryLocalChannel, unionList, typeUnionList, countSignal, countAccept, adUtils, this, countAction);

        return dTypes.defineTypes();
    }

    public String defineMemories() {
        ADUtils adUtils = defineADUtils();

        dMemories = new ADDefineMemories(ad, parameterNodesInput, parameterNodesOutput, memoryLocal, adUtils);

        return dMemories.defineMemories();
    }

    public String defineNodesActionAndControl() throws ParsingException {
        ADUtils adUtils = defineADUtils();

        dNodesActionAndControl = new ADDefineNodesActionAndControl(ad, adDiagram, countCall, alphabetNode, parameterAlphabetNode,
                syncChannelsEdge, syncObjectsEdge, objectEdges, queueNode, queueRecreateNode, callBehaviourList, eventChannel,
                lockChannel, allInitial, alphabetAllInitialAndParameter, parameterNodesInput, parameterNodesOutput, parameterNodesOutputObject,
                callBehaviourNumber, memoryLocal, memoryLocalChannel, unionList, typeUnionList, callBehaviourInputs, callBehaviourOutputs,
                countSignal, countAccept, signalChannels, localSignalChannelsSync, createdSignal, createdAccept, allGuards, signalChannelsLocal, 
                adUtils, this, countAction, createdAction, waitAccept);

        return dNodesActionAndControl.defineNodes();
    }

    public String defineTokenManager() {
        ADUtils adUtils = defineADUtils();

        dTokenManager = new ADDefineTokenManager(ad, adUtils);

        return dTokenManager.defineTokenManager();
    }

    public String definePool() {
        ADUtils adUtils = defineADUtils();

        dPool = new ADDefinePool(ad, signalChannels, firstDiagram, countAccept, adUtils);

        return dPool.definePool();
    }

    public String defineProcessSync() {
        ADUtils adUtils = defineADUtils();

        dProcessSync = new ADDefineProcessSync(ad, alphabetNode, adUtils);

        return dProcessSync.defineProcessSync();
    }

    public String defineMainNodes() {
        ADUtils adUtils = defineADUtils();

        dMainNodes = new ADDefineMainNodes(ad, firstDiagram, lockChannel, parameterNodesInput, parameterNodesOutput, callBehaviourNumber,
                callBehaviourInputs, localSignalChannelsSync, signalChannelsLocal, adUtils, this);

        return dMainNodes.defineMainNodes();
    }
}