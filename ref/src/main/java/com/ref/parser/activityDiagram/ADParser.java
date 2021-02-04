package com.ref.parser.activityDiagram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.model.IAction;
import com.change_vision.jude.api.inf.model.IActivity;
import com.change_vision.jude.api.inf.model.IActivityDiagram;
import com.change_vision.jude.api.inf.model.IActivityNode;
import com.ref.exceptions.ParsingException;
import com.ref.refinement.activityDiagram.DeadlockCounterExample;
import com.ref.refinement.activityDiagram.DeterminismCounterExample;

public class ADParser {

    private IActivity ad;
    private IActivityDiagram adDiagram;

    public int countGet_ad;
    public int countSet_ad;
    public int countCe_ad;
    public int countOe_ad;
    public int countUntil_ad;
    public int countAny_ad;
    public int countUpdate_ad;
    public int countClear_ad;
    public int limiteInf;
    public int limiteSup;

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
    private static HashMap<String,List<IActivity>> signalChannels = new HashMap<>();
    private List<String> signalChannelsLocal;
    private List<String> localSignalChannelsSync;
    private List<String> createdSignal;
    private List<String> createdAccept;
    private HashMap<String,Integer> allGuards;

    private ADAlphabet alphabetAD;
    public ADDefineChannels dChannels;
    public ADDefineTypes dTypes;
    public ADDefineMemories dMemories;
    public ADDefineMainNodes dMainNodes;
    public ADDefineNodesActionAndControl dNodesActionAndControl;
    public ADDefineLocks dLocks;
    public ADDefineTokenManager dTokenManager;
    public ADDefineProcessSync dProcessSync;
    public ADDefinePool dPool;
    
    public Map<String,String> robo;
    public List<String> eventsUntil;
    public HashMap<String, String> untilList;

    public ADParser(IActivity ad, String nameAD, IActivityDiagram adDiagram) {
        this.ad = ad;
        this.adDiagram = adDiagram;
        setName(nameAD);
        setFirstDiagram();
        this.countGet_ad = 1;
        this.countSet_ad = 1;
        this.countCe_ad = 1;
        this.countOe_ad = 1;
        this.countUntil_ad = 0;
        this.countAny_ad = 0;
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
        allGuards = new HashMap<>();
        
        this.robo = new HashMap<String, String>();
        this.eventsUntil = new ArrayList<>();
        this.untilList = new HashMap<>();
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
        this.countUntil_ad = 1;
        this.countAny_ad = 1;
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
        callBehaviourInputs = new HashMap<>();
        callBehaviourNumber = new ArrayList<>();
        localSignalChannelsSync = new ArrayList<>();
        signalChannelsLocal = new ArrayList<>();
        createdSignal = new ArrayList<>();
        createdAccept = new ArrayList<>();
        resetStatic();
        allGuards = new HashMap<>();
        firstDiagram = ad.getId(); //set first diagram
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
        signalChannels = new HashMap<>();
        alphabetPool = new HashSet<String>();
    }

    private void setName(String nameAD) {
        try {
            this.ad.setName(nameAD);
        } catch (InvalidEditingException e) {
            e.printStackTrace();
        }
    }

    /*
     * Master Function
     * */

    public String parserDiagram() throws ParsingException {

        boolean reset = false;
        String check = "";
        String callBehaviour = "";

        if (countCall.size() == 0) { // igual a primeira ocorrencia
            check = "\nassert MAIN :[deadlock free]" +
                    "\nassert MAIN :[divergence free]" +
                    "\nassert MAIN :[deterministic]";
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
        }

        //String lock = defineLock();
        String channel = defineChannels();
        String main = defineMainNodes();
        String type = defineTypes();
        String tokenManager = defineTokenManager();
        String memory = defineMemories();
        String processSync = defineProcessSync();
        String pool = "";
//        String pool = definePool();
        // ----------------------------------------------
        ADUtils adUtils = defineADUtils();
        HashMap<String, String> parameterValueDiagram = adUtils.getParameterValueDiagram("");
        String robochart = parameterValueDiagram.get("robochart");
        String robochart_alphabet = parameterValueDiagram.get("robochart_alphabet");
        if (robochart != null && !robochart.equals("")) {
			robochart = "include " + robochart + "\n";
		} else {
			throw new ParsingException("Specify the Robochart file providing the property \"robochart = {robochartFilePath};\" in the definition field.");
		}
        
        // ----------------------------------------------
    	String check_props = "\n\n";
    	check_props += 
    			"NRecurse(S, P) = |~| ev : S @ ev -> P\n"
    			+ "\n"
    			+ "WAIT(alphabet,event) = \n"
    			+ "	NRecurse(diff(alphabet, {event}), WAIT(alphabet,event))\n"
    			+ "	|~|\n"
    			+ "	event -> SKIP\n"
    			+ "\n"
    			+ "WAIT_PROCCESSES(processes) = ||| CONTROL : processes @ CONTROL\n\n"
    			+ "Prop = PROP(Wait_control_processes) \n\n"
    			+  adUtils.alphabetRobo(robochart_alphabet)
    			+ "\n\n";
    	
    	if (countAny_ad > 0 && countUntil_ad > 0) {
			check_props +=
					"PROP(processes) = (MAIN [|{|begin, end, chaos|}|] WAIT_PROCCESSES(processes) ) \\ {|begin, end, chaos|} /\\ endActivity_" + ad.getName() + " -> SKIP \n\n"
					+ adUtils.printUntils()
					+ adUtils.printAny();
		} else if (countUntil_ad > 0) {
			check_props +=
					"PROP(processes) = (MAIN [|{|begin, end|}|] WAIT_PROCCESSES(processes) ) \\ {|begin, end|} /\\ endActivity_" + ad.getName() + " -> SKIP \n\n"
					+ adUtils.printUntils();
		} else if (countAny_ad > 0) {
			check_props +=
					"PROP(processes) = (MAIN [|{|chaos|}|] WAIT_PROCCESSES(processes) ) \\ {|chaos|} /\\ endActivity_" + ad.getName() + " -> SKIP \n\n"
					+ adUtils.printAny();
		} else {
			check_props +=
					"PROP(processes) = (MAIN)\n\n";
		}
    	   
    	check_props += ""
				+ adUtils.printControlProcesses()
				+ "\n\n\n\n"
				+ adUtils.mapEvents();
    		
        String parser = (firstDiagram.equals(ad.getId())?"transparent normal\n":"")+
        		robochart +
        		type +
                channel +
                main +
                processSync +
                nodes +
                memory +
                tokenManager +
                /*lock +*/
                // pool +
                // (firstDiagram.equals(ad.getId())?"\nAlphabetPool = {|endDiagram_"+ADUtils.nameResolver(ad.getName())+(!alphabetPool.isEmpty()?","+alphabetPoolToString():"")+"|}\n":"")+
                callBehaviour +
                check +
                check_props;

        //reseta os valores estaticos
        if (reset) {
        	DeadlockCounterExample.callBehaviourList = callBehaviourList;
        	DeterminismCounterExample.callBehaviourList = callBehaviourList;
            resetStatic();
        }

        //alphabet usado na geração dos contraexemplos
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
        if (countcallBehavior.containsKey(idKey)) {//se ja tiver o CBA
            aux1 = countcallBehavior.get(idKey);
            List<String>aux2 = new ArrayList<String>();
            for(Pair<String,String> a:aux1) {//pegar os ids
            	aux2.add(a.getKey());
            }
            for(String diagram:aux2) {//varrer atras do diagrama que chamou
            	if(diagram.equals(idCalling)){
            		break;
            	}
            	i++;
            }
            if(i > aux1.size()) {//se ele n estiver la, add
            	Pair<String,String> pair = new Pair<String, String>(idCalling, nameCalling);
            	aux1.add(pair);
            	countcallBehavior.replace(idKey,aux1);
            }
        } else {//se nao existir o CBA
        	aux1 = new ArrayList<Pair<String,String>>();
        	Pair<String,String> pair = new Pair<String, String>(idCalling, nameCalling);
        	aux1.add(pair);
            countcallBehavior.put(idKey, aux1);
        }
    }

    private void defineCallBehaviourList() throws ParsingException {
    	if(countCall.size() == 0) {//pega os CBA do 1 diagrama
        	for (IActivityNode activityNode : ad.getActivityNodes()) {//pega todos os nós
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
        	}
        	boolean mudou =true;
        	List<IActivity> aux1 = new ArrayList<>();
        	List<IActivity> aux3 = new ArrayList<>();
        	aux3.addAll(callBehaviourList);
        	while(mudou) {
	        	if(callBehaviourList.size() != 0) {//pega os CBA dentro de CBA
	        		for(IActivity CBAs: callBehaviourList) {//para cada CBA
	        			for(IActivityNode adCBAs: CBAs.getActivityNodes()) {//para cada nó
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

	        	for(IActivity CBA:aux1) {//faz a união dos conjuntos
	        		if(!callBehaviourList.contains(CBA)) {
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

	private void definePoolAlphabet() {
		if(firstDiagram.equals(ad.getId())) {//pega do 1 diagrama
        	for (IActivityNode activityNode : ad.getActivityNodes()) {//pega todos os nós
        		if (activityNode instanceof IAction) {
                    if (((IAction) activityNode).isAcceptEventAction()||((IAction) activityNode).isSendSignalAction()) {//se for accept ou send
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
                signalChannels, localSignalChannelsSync, allGuards, createdSignal, createdAccept, syncChannelsEdge, syncObjectsEdge,
                signalChannelsLocal, this, robo, eventsUntil, untilList);
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
                parameterNodesOutput, memoryLocalChannel, unionList, typeUnionList, countSignal, countAccept, adUtils, this);

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
                countSignal, countAccept, signalChannels, localSignalChannelsSync, createdSignal, createdAccept, allGuards, signalChannelsLocal, adUtils, this);

        return dNodesActionAndControl.defineNodesActionAndControl();
    }

    public String defineLock() {
        ADUtils adUtils = defineADUtils();

        dLocks = new ADDefineLocks(ad, lockChannel, adUtils);

        return dLocks.defineLock();
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
