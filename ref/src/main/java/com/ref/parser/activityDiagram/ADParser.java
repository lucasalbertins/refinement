package com.ref.parser.activityDiagram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.model.*;

import javafx.util.Pair;

public class ADParser {

    private IActivity ad;
    private IActivityDiagram adDiagram;

    private int countGet_ad;
    private int countSet_ad;
    private int countCe_ad;
    private int countOe_ad;
    private int countUpdate_ad;
    private int countClear_ad;
    private int limiteInf;
    private int limiteSup;

    private static boolean firstLock = true;
    private static boolean firstMain = true;
    private static boolean firstChannel = true;

    public static HashMap<String, Integer> countCall = new HashMap<>();
    public HashMap<String, ArrayList<String>> alphabetNode;
    public HashMap<String, ArrayList<String>> parameterAlphabetNode;
    public HashMap<String, String> syncChannelsEdge;            //ID flow, channel
    public HashMap<String, String> syncObjectsEdge;
    private HashMap<String, String> objectEdges;                //channel; name
    private List<IActivityNode> queueNode;
    private List<IActivityNode> queueRecreateNode;
    private static List<IActivity> callBehaviourList = new ArrayList<>();
    private static List<IActivity> callBehaviourListCreated = new ArrayList<>();
    private List<String> eventChannel;
    private List<String> lockChannel;
    private List<String> allInitial;
    private ArrayList<String> alphabetAllInitialAndParameter;
    private HashMap<String, String> parameterNodesInput;        //name; type
    private HashMap<String, String> parameterNodesOutput;
    private HashMap<String, String> parameterNodesOutputObject; //name; object
    private List<Pair<String, Integer>> callBehaviourNumber;     //name; int
    private List<Pair<String, String>> memoryLocal;             //nameNode, nameObject
    private List<Pair<String, String>> memoryLocalChannel;
    private List<ArrayList<String>> unionList;
    private HashMap<String, String> typeUnionList;
    private static HashMap<String, List<String>> callBehaviourInputs = new HashMap<>(); //name; List inputs
    private static HashMap<String, List<String>> callBehaviourOutputs = new HashMap<>(); //name; List outputs

    public ADParser(IActivity ad, String nameAD, IActivityDiagram adDiagram) {
        this.ad = ad;
        this.adDiagram = adDiagram;
        setName(nameAD);
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
        memoryLocal = new ArrayList<>();
        memoryLocalChannel = new ArrayList<>();
        unionList = new ArrayList<>();
        typeUnionList = new HashMap<>();
        callBehaviourNumber = new ArrayList<>();
    }

    public void checkCountCallInitial() {
        if (countCall.get(nameResolver(ad.getName())) == null) {
            addCountCall(nameResolver(ad.getName()));
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
        countCall = new HashMap<>();
        callBehaviourList = new ArrayList<>();
        callBehaviourInputs = new HashMap<>();
        callBehaviourOutputs = new HashMap<>();
        callBehaviourListCreated = new ArrayList<>();
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
        memoryLocal = new ArrayList<>();
        memoryLocalChannel = new ArrayList<>();
        unionList = new ArrayList<>();
        typeUnionList = new HashMap<>();
        callBehaviourInputs = new HashMap<>();
        callBehaviourNumber = new ArrayList<>();
        firstLock = true;
        firstMain = true;
        firstChannel = true;
    }

    private void resetStatic() {
        firstChannel = true;
        firstMain = true;
        firstLock = true;
        countCall = new HashMap<>();
        callBehaviourList = new ArrayList<>();
        callBehaviourInputs = new HashMap<>();
        callBehaviourOutputs = new HashMap<>();
        callBehaviourListCreated = new ArrayList<>();
    }

    private void setName(String nameAD) {
        try {
            this.ad.setName(nameAD);
        } catch (InvalidEditingException e) {
            e.printStackTrace();
        }
    }

    private int addCountCall(String name) {
        int i = 1;
        if (countCall.containsKey(nameResolver(name))) {
            i = countCall.get(nameResolver(name));
            countCall.put(nameResolver(name), ++i);
        } else {
            countCall.put(nameResolver(name), 1);
        }
        return i;
    }

    /*
     * Master Function
     * */

    public String parserDiagram() {

        boolean reset = false;
        String check = "";
        String callBehaviour = "";

        if (countCall.get(nameResolver(ad.getName())) == null) { // igual a primeira ocorrencia

            check = "\nassert MAIN :[deadlock free]" +
                    "\nassert MAIN :[divergence free]" +
                    "\nassert MAIN :[deterministic]";
            reset = true;
        }

        checkCountCallInitial();

        String nodes = defineNodesActionAndControl();
        String lock = defineLock();
        String channel = defineChannels();
        String main = defineMainNodes();
        String type = defineTypes();
        String tokenManager = defineTokenManager();
        String memory = defineMemories();
        String processSync = defineProcessSync();

        for (IActivity ad: callBehaviourList) {
            if (!callBehaviourListCreated.contains(ad)) {
                callBehaviourListCreated.add(ad);
                callBehaviour += "\n" + (new ADParser(ad, ad.getName(), ad.getActivityDiagram())).parserDiagram();
            }
        }

        String parser = type +
                channel +
                main +
                processSync +
                nodes +
                memory +
                tokenManager +
                lock +
                callBehaviour +
                check;

        if (reset) {
            resetStatic();
        }

        return parser;
    }


    /*   */


    public String defineChannels() {
        StringBuilder channels = new StringBuilder();
        String nameDiagram = nameResolver(ad.getName());

        if (parameterNodesInput.size() > 0) {
            channels.append("channel startActivity_" + nameDiagram + ": ID_" + nameDiagram);

            for (String input : parameterNodesInput.keySet()) {
                channels.append("." + input + "_" + nameDiagram);
            }

            channels.append("\n");

        } else {
            channels.append("channel startActivity_" + nameDiagram + ": ID_" + nameDiagram + "\n");
        }

        if (parameterNodesOutput.size() > 0) {
            channels.append("channel endActivity_" + nameDiagram + ": ID_" + nameDiagram);

            for (String output : parameterNodesOutput.keySet()) {
                channels.append("." + output + "_" + nameDiagram);
            }

            channels.append("\n");

        } else {
            channels.append("channel endActivity_" + nameDiagram + ": ID_" + nameDiagram + "\n");
        }

        if (parameterNodesInput.size() > 0 || parameterNodesOutput.size() > 0 || memoryLocal.size() > 0) {

            for (String get : parameterNodesInput.keySet()) {
                channels.append("channel get_" + get + "_" + nameDiagram + ": countGet_" + nameDiagram + "." + get + "_" + nameDiagram + "\n");
            }

            for (String get : parameterNodesOutput.keySet()) {
                String object = parameterNodesOutputObject.get(get);

                if (object == null) {
                    object = get;
                }

                channels.append("channel get_" + get + "_" + nameDiagram + ": countGet_" + nameDiagram + "." + object + "_" + nameDiagram + "\n");
            }

            for (Pair<String, String> pair : memoryLocal) {
                channels.append("channel get_" + pair.getValue() + "_" + pair.getKey() + "_" + nameDiagram + ": countGet_" + nameDiagram + "." + pair.getValue() + "_" + nameDiagram + "\n");
            }

            for (String set : parameterNodesInput.keySet()) {
                channels.append("channel set_" + set + "_" + nameDiagram + ": countSet_" + nameDiagram + "." + set + "_" + nameDiagram + "\n");
            }

            for (String set : parameterNodesOutput.keySet()) {
                String object = parameterNodesOutputObject.get(set);

                if (object == null) {
                    object = set;
                }
                channels.append("channel set_" + set + "_" + nameDiagram + ": countSet_" + nameDiagram + "." + object + "_" + nameDiagram + "\n");
            }

            for (Pair<String, String> pair : memoryLocal) {
                channels.append("channel set_" + pair.getValue() + "_" + pair.getKey() + "_" + nameDiagram + ": countSet_" + nameDiagram + "." + pair.getValue() + "_" + nameDiagram + "\n");
            }

        }

        if (countCe_ad > 1) {
            channels.append("channel ce_" + nameDiagram + ": countCe_" + nameDiagram + "\n");
        }

        if (syncObjectsEdge.size() > 0) {
            ArrayList<String> allObjectEdges = new ArrayList<>();
            for (String objectEdge : syncObjectsEdge.values()) {    //get sync channel
                String nameParamater = objectEdges.get(objectEdge);

                if (!allObjectEdges.contains(nameParamater)) {
                    allObjectEdges.add(nameParamater);
                    channels.append("channel oe_" + nameParamater + "_" + nameDiagram + ": countOe_" + nameDiagram + "." + nameParamater + "_" + nameDiagram + "\n");
                }
            }

        }

//        if (countClear_ad > 1) {
////            channels.append("channel clear_" + nameDiagram + ": countClear_" + nameDiagram + "\n");
////        }

        channels.append("channel clear_" + nameDiagram + ": countClear_" + nameDiagram + "\n");

        channels.append("channel update_" + nameDiagram + ": countUpdate_" + nameDiagram + ".limiteUpdate_" + nameDiagram + "\n");

        channels.append("channel endDiagram_" + nameDiagram + "\n");

        if (eventChannel.size() > 0) {
            channels.append("channel ");

            for (int i = 0; i < eventChannel.size(); i++) {
                channels.append(eventChannel.get(i));

                if ((i + 1) < eventChannel.size()) {
                    channels.append(",");
                }
            }

            channels.append("\n");
        }

        if (lockChannel.size() > 0) {
            channels.append("channel ");

            for (int i = 0; i < lockChannel.size(); i++) {
                channels.append("lock_" + lockChannel.get(i));

                if ((i + 1) < lockChannel.size()) {
                    channels.append(",");
                }
            }

            channels.append(": T\n");
        }

        if (firstChannel) {
            channels.append("channel loop\n");
            firstChannel = false;
        }

        System.out.println(channels);

        return channels.toString();
    }

    public String defineTypes() {
        StringBuilder types = new StringBuilder();
        String nameDiagram = nameResolver(ad.getName());

//        if (countCall.size() > 0) {        //Provavelmente deve ser criado por ultimo
//            for (String nameDiagram2 : countCall.keySet()) {
//                types.append("ID_" + nameDiagram2 + " = {1.." + countCall.get(nameResolver(nameDiagram2)) + "}\n");
//            }
//        }

        if (firstLock) { // igual a primeira ocorrencia

            for (String id : countCall.keySet()) {
                types.append("ID_" + id + " = {1.." + countCall.get(id) + "}\n");
            }

            types.append("datatype T = lock | unlock\n");
            firstLock = false;
        }

        if (parameterNodesInput.size() > 0 || parameterNodesOutput.size() > 0) {
            HashMap<String, String> typesParameter = new HashMap<>();
            String[] definition = adDiagram.getDefinition().replace(" ", "").split(";");

            for (String def : definition) {
                String[] keyValue = def.split("=");
                typesParameter.put(keyValue[0], keyValue[1]);
            }

            for (String input : parameterNodesInput.keySet()) {
                types.append(input + "_" + nameDiagram + " = ");

                types.append(typesParameter.get(parameterNodesInput.get(input)) + "\n"); //Verificar se possivel usar o campo definition para definir o intervalo

            }

            for (String output : parameterNodesOutput.keySet()) {
                types.append(output + "_" + nameDiagram + " = ");

                types.append(typesParameter.get(parameterNodesOutput.get(output)) + "\n"); //Verificar se possivel usar o campo definition para definir o intervalo

            }

            List<String> buffer = new ArrayList<>();

            for (ArrayList<String> union : unionList) {
                String objectUnion = "";
                String nameLast = "";
                for (int i = 0; i < union.size(); i++) {
                    objectUnion += union.get(i);
                    nameLast = union.get(i);
                }

                if (!buffer.contains(objectUnion)) {
                    types.append(objectUnion + "_" + nameDiagram + " = ");
                    types.append(typesParameter.get(parameterNodesInput.get(nameLast)) + "\n");
                    //System.out.println("### " + objectUnion + "_" + nameDiagram +  " # " + typesParameter.get(parameterNodesInput.get(nameLast)));
                    buffer.add(objectUnion);
                }

            }

            for (Pair<String, String> pair : memoryLocalChannel) {
                if (!parameterNodesInput.containsKey(pair.getValue()) && !parameterNodesOutput.containsKey(pair.getValue()) && !buffer.contains(pair.getValue())) {
                    types.append(pair.getValue() + "_" + nameDiagram + " = ");
                    System.out.println("### " + pair.getKey() + " " + pair.getValue());
                    if (objectEdges.containsKey(pair.getKey())) {
                        if (typeUnionList.containsKey(objectEdges.get(pair.getKey()))) {
                            types.append(typesParameter.get(typeUnionList.get(objectEdges.get(pair.getKey()))) + "\n");
                        } else {
                            types.append(typesParameter.get(parameterNodesInput.get(objectEdges.get(pair.getKey()))) + "\n");
                        }
                    } else {
                        types.append(typesParameter.get(parameterNodesInput.get(pair.getValue())) + "\n");
                    }

                    buffer.add(pair.getValue());
                }

            }


        }

        if (countGet_ad > 1 || countSet_ad > 1) {
            if (countGet_ad == 1) {
                types.append("countGet_" + nameDiagram + " = {1.." + countGet_ad + "}\n");
            } else {
                types.append("countGet_" + nameDiagram + " = {1.." + (countGet_ad - 1) + "}\n");
            }

            if (countSet_ad == 1) {
                types.append("countSet_" + nameDiagram + " = {1.." + countSet_ad + "}\n");
            } else {
                types.append("countSet_" + nameDiagram + " = {1.." + (countSet_ad - 1) + "}\n");
            }
        }

        if (countCe_ad > 1) {
            types.append("countCe_" + nameDiagram + " = {1.." + (countCe_ad - 1) + "}\n");
        }

        if (countOe_ad > 1) {
            types.append("countOe_" + nameDiagram + " = {1.." + (countOe_ad - 1) + "}\n");
        }

        types.append("countUpdate_" + nameDiagram + " = {1.." + (countUpdate_ad - 1) + "}\n");

//        if (countClear_ad > 1) {
//            types.append("countClear_" + nameDiagram + " = {1.." + (countClear_ad - 1) + "}\n");
//        }

        types.append("countClear_" + nameDiagram + " = {1.." + (countClear_ad - 1) + "}\n");

        types.append("limiteUpdate_" + nameDiagram + " = {(" + limiteInf + ")..(" + limiteSup + ")}\n");

        System.out.println(types);

        return types.toString();
    }

    public String defineMemories() {
        StringBuilder memory = new StringBuilder();
        String nameDiagram = nameResolver(ad.getName());

        for (Pair<String, String> pair : memoryLocal) {
            memory.append("Mem_" + pair.getKey() + "_" + nameDiagram + "_" + pair.getValue() + "(" + pair.getValue() + ") = ");
            memory.append("get_" + pair.getValue() + "_" + pair.getKey() + "_" + nameDiagram + "?c!" + pair.getValue() + " -> ");
            memory.append("Mem_" + pair.getKey() + "_" + nameDiagram + "_" + pair.getValue() + "(" + pair.getValue() + ") [] ");
            memory.append("set_" + pair.getValue() + "_" + pair.getKey() + "_" + nameDiagram + "?c?" + pair.getValue() + " -> ");
            memory.append("Mem_" + pair.getKey() + "_" + nameDiagram + "_" + pair.getValue() + "(" + pair.getValue() + ")\n");

            memory.append("Mem_" + pair.getKey() + "_" + nameDiagram + "_" + pair.getValue() + "_t" + "(" + pair.getValue() + ") = ");
            memory.append("Mem_" + pair.getKey() + "_" + nameDiagram + "_" + pair.getValue() + "(" + pair.getValue() + ") /\\ END_DIAGRAM_" + nameDiagram + "\n");
        }


        if (parameterNodesInput.size() > 0 || parameterNodesOutput.size() > 0) {
            for (String input : parameterNodesInput.keySet()) {
                memory.append("Mem_" + nameDiagram + "_" + input + "(" + input + ") = ");
                memory.append("get_" + input + "_" + nameDiagram + "?c!" + input + " -> ");
                memory.append("Mem_" + nameDiagram + "_" + input + "(" + input + ") [] ");
                memory.append("set_" + input + "_" + nameDiagram + "?c?" + input + " -> ");
                memory.append("Mem_" + nameDiagram + "_" + input + "(" + input + ")\n");

                memory.append("Mem_" + nameDiagram + "_" + input + "_t" + "(" + input + ") = ");
                memory.append("Mem_" + nameDiagram + "_" + input + "(" + input + ") /\\ (endActivity_" + nameDiagram + "?" + input + " -> SKIP)\n");
            }

            for (String output : parameterNodesOutput.keySet()) {
                memory.append("Mem_" + nameDiagram + "_" + output + "(" + output + ") = ");
                memory.append("get_" + output + "_" + nameDiagram + "?c!" + output + " -> ");
                memory.append("Mem_" + nameDiagram + "_" + output + "(" + output + ") [] ");
                memory.append("set_" + output + "_" + nameDiagram + "?c?" + output + " -> ");
                memory.append("Mem_" + nameDiagram + "_" + output + "(" + output + ")\n");

                memory.append("Mem_" + nameDiagram + "_" + output + "_t" + "(" + output + ") = ");
                memory.append("Mem_" + nameDiagram + "_" + output + "(" + output + ") /\\ (endActivity_" + nameDiagram + "?" + output + " -> SKIP)\n");
            }

            memory.append("Mem_" + nameDiagram + " = ");

            for (int i = 0; i < parameterNodesInput.size() + parameterNodesOutput.size() - 1; i++) {
                memory.append("(");
            }

            int i = 0;

            for (String input : parameterNodesInput.keySet()) {
                if (i <= 1) {
                    memory.append("Mem_" + nameDiagram + "_" + input + "_t(" + getDefaultValue(parameterNodesInput.get(input)) + ")");
                    if (i % 2 == 0 && (i + 1 < parameterNodesInput.size() || parameterNodesOutput.size() > 0)) {
                        memory.append(" [|{|endActivity_" + nameDiagram + "|}|] ");
                    } else if (parameterNodesInput.size() + parameterNodesOutput.size() > 1) {
                        memory.append(")");
                    }
                } else {
                    memory.append(" [|{|endActivity_" + nameDiagram + "|}|] ");
                    memory.append("Mem_" + nameDiagram + "_" + input + "_t(" + getDefaultValue(parameterNodesInput.get(input)) + "))");
                }

                i++;
            }

            for (String output : parameterNodesOutput.keySet()) {
                if (i <= 1) {
                    memory.append("Mem_" + nameDiagram + "_" + output + "_t(" + getDefaultValue(parameterNodesOutput.get(output)) + ")");
                    if (i % 2 == 0 && parameterNodesOutput.size() > 1 &&
                            i < parameterNodesOutput.size()) {
                        memory.append(" [|{|endActivity_" + nameDiagram + "|}|] ");
                    } else if (parameterNodesInput.size() + parameterNodesOutput.size() > 1) {
                        memory.append(")");
                    }
                } else {
                    memory.append(" [|{|endActivity_" + nameDiagram + "|}|] ");
                    memory.append("Mem_" + nameDiagram + "_" + output + "_t(" + getDefaultValue(parameterNodesOutput.get(output)) + "))");
                }

                i++;
            }

        }

        memory.append("\n");

        return memory.toString();
    }

    public String defineMainNodes() {
        StringBuilder mainNode = new StringBuilder();
        String nameDiagram = nameResolver(ad.getName());
        ArrayList<String> alphabet = new ArrayList<>();

        if (firstMain) {
            mainNode.append("MAIN = " + nameDiagram + "(1); LOOP\n");
            mainNode.append("LOOP = loop -> LOOP\n");
            firstMain = false;
        }

        mainNode.append("END_DIAGRAM_" + nameDiagram + " = endDiagram_" + nameDiagram + " -> SKIP\n");
        mainNode.append(nameDiagram + "(ID_" + nameDiagram + ") = ");

        if (parameterNodesInput.size() + parameterNodesOutput.size() > 0) {
            mainNode.append("(");
        }

        if (countUpdate_ad > 0) {
            mainNode.append("(");
        }

        if (lockChannel.size() > 0) {
            mainNode.append("(");
        }

        for (int i = 0; i < callBehaviourNumber.size(); i++) {
            mainNode.append("(");
        }

        mainNode.append("Internal_" + nameDiagram + "(ID_" + nameDiagram + ")");

        for (Pair<String, Integer> callBehaviourAD : callBehaviourNumber) {
            mainNode.append(" [|{|startActivity_" + callBehaviourAD.getKey() + "." + callBehaviourAD.getValue() + ",endActivity_" + callBehaviourAD.getKey() + "." + callBehaviourAD.getValue() + "|}|] ");
            mainNode.append(callBehaviourAD.getKey() + "(" + callBehaviourAD.getValue() + "))");
        }

        mainNode.append(" [|{|update_" + nameDiagram + ",clear_" + nameDiagram + ",endDiagram_" + nameDiagram + "|}|] ");
        mainNode.append("TokenManager_" + nameDiagram + "_t(0,0))");

        if (lockChannel.size() > 0) {
            mainNode.append(" [|{|");
            for (String lock : lockChannel) {
                mainNode.append("lock_" + lock + ",");
            }
            mainNode.append("endDiagram_" + nameDiagram + "|}|] ");
        }

        if (parameterNodesInput.size() + parameterNodesOutput.size() > 0) {
            if (lockChannel.size() > 0) {
                mainNode.append("Lock_" + nameDiagram + ")");
            }

            mainNode.append(" [|{|");

            for (String input : parameterNodesInput.keySet()) {
                mainNode.append("get_" + input + "_" + nameDiagram + ",");
                mainNode.append("set_" + input + "_" + nameDiagram + ",");
            }

            for (String output : parameterNodesOutput.keySet()) {
                mainNode.append("get_" + output + "_" + nameDiagram + ",");
                mainNode.append("set_" + output + "_" + nameDiagram + ",");
            }

            mainNode.append("endActivity_" + nameDiagram + "|}|] ");

            mainNode.append("Mem_" + nameDiagram + ")\n");
        } else if (lockChannel.size() > 0) {
            mainNode.append("Lock_" + nameDiagram + ")\n");
        } else {
            mainNode.append("\n");
        }


        mainNode.append("Internal_" + nameDiagram + "(ID_" + nameDiagram + ") = ");
        mainNode.append("StartActivity_" + nameDiagram + "(ID_" + nameDiagram + "); Node_" + nameDiagram + "; EndActivity_" + nameDiagram + "(ID_" + nameDiagram + ")\n");


        mainNode.append("StartActivity_" + nameDiagram + "(ID_" + nameDiagram + ") = ");
        mainNode.append("startActivity_" + nameDiagram + ".ID_" + nameDiagram);

        if (parameterNodesInput.size() > 0) {
            if (callBehaviourInputs.containsKey(nameResolver(ad.getName()))) {
                for (String input : callBehaviourInputs.get(nameResolver(ad.getName()))) {
                    mainNode.append("?" + input);
                }
            } else {
                for (String input : parameterNodesInput.keySet()) {
                    mainNode.append("?" + input);
                }
            }


            mainNode.append(" -> ");

            if (callBehaviourInputs.containsKey(nameResolver(ad.getName()))) {
                for (String input : callBehaviourInputs.get(nameResolver(ad.getName()))) {
                    set(alphabet, mainNode, input, input);
                }
            } else {
                for (String input : parameterNodesInput.keySet()) {
                    set(alphabet, mainNode, input, input);
                }
            }

            mainNode.append("SKIP\n");
        } else {
            mainNode.append(" -> SKIP\n");
        }


        mainNode.append("EndActivity_" + nameDiagram + "(ID_" + nameDiagram + ") = ");

        if (parameterNodesOutput.size() > 0) {
            for (String input : parameterNodesOutput.keySet()) {
                get(alphabet, mainNode, input);
            }

            mainNode.append("endActivity_" + nameDiagram + ".ID_" + nameDiagram);

            for (String output : parameterNodesOutput.keySet()) {
                mainNode.append("!" + output);
            }

            mainNode.append(" -> SKIP");
        } else {
            mainNode.append("endActivity_" + nameDiagram + ".ID_" + nameDiagram + " -> SKIP");
        }

        mainNode.append("\n");
        return mainNode.toString();
    }

    private String getDefaultValue(String type) {
        HashMap<String, String> typesParameter = new HashMap<>();
        String[] definition = adDiagram.getDefinition().replace(" ", "").split(";");

        for (String def : definition) {
            String[] keyValue = def.split("=");
            typesParameter.put(keyValue[0], keyValue[1]);
        }

        String defaultValue = typesParameter.get(type).replace("{", "").replace("}", "").replace("(", "")
                .replace(")", "").split(",")[0];
        System.out.println(defaultValue + " 1");
        String defaultValueFinal = defaultValue.split("\\.\\.")[0];
        System.out.println(defaultValueFinal + " 2");
        return defaultValueFinal;
    }

    public String defineNodesActionAndControl() {
        for (IActivityNode activityNode : ad.getActivityNodes()) {
            if (activityNode instanceof IActivityParameterNode && activityNode.getOutgoings().length > 0) {
                parameterNodesInput.put(nameResolver(activityNode.getName()), ((IActivityParameterNode) activityNode).getBase().getName());
            }

            if (activityNode instanceof IActivityParameterNode && activityNode.getIncomings().length > 0) {
                parameterNodesOutput.put(nameResolver(activityNode.getName()), ((IActivityParameterNode) activityNode).getBase().getName());
            }
        }

        StringBuilder nodes = new StringBuilder();

        for (IActivityNode activityNode : ad.getActivityNodes()) {
            if (((activityNode instanceof IControlNode && ((IControlNode) activityNode).isInitialNode()) ||
                    activityNode instanceof IActivityParameterNode && activityNode.getIncomings().length == 0) &&
                    !queueNode.contains(activityNode)) {

                queueNode.add(activityNode);
            }
        }

        int input = 0;
        int expectedInput = 0;

        while (queueNode.size() != 0) {
            IActivityNode activityNode = queueNode.get(0);
            queueNode.remove(0);

            input = countAmount(activityNode);
            if (activityNode != null) {
                if (activityNode instanceof IAction) {
                    expectedInput = activityNode.getIncomings().length + ((IAction) activityNode).getInputs().length;
                } else {
                    expectedInput = activityNode.getIncomings().length;
                }
            }

            String name = activityNode.getName();
            if (activityNode instanceof IActivityParameterNode) {
                name = "parameter_" + activityNode.getName();
            }

            while (activityNode != null && !alphabetNode.containsKey(nameResolver(name)) && !queueRecreateNode.contains(activityNode)) {    // Verifica se nó é nulo, se nó já foi criado e se todos os nós de entrada dele já foram criados

                if (input == expectedInput) {
                    if (activityNode instanceof IAction) {
                        if (((IAction) activityNode).isCallBehaviorAction()) {
                            activityNode = defineCallBehaviour(activityNode, nodes, 0);
                        } else {
                            activityNode = defineAction(activityNode, nodes, 0);    // create action node and set next action node
                        }
                    } else if (activityNode instanceof IControlNode) {
                        if (((IControlNode) activityNode).isFinalNode()) {
                            activityNode = defineFinalNode(activityNode, nodes); // create final node and set next action node
                        } else if (((IControlNode) activityNode).isFlowFinalNode()) {
                            activityNode = defineFlowFinal(activityNode, nodes); // create flow final and set next action node
                        } else if (((IControlNode) activityNode).isInitialNode()) {
                            activityNode = defineInitialNode(activityNode, nodes); // create initial node and set next action node
                        } else if (((IControlNode) activityNode).isForkNode()) {
                            activityNode = defineFork(activityNode, nodes, 0); // create fork node and set next action node
                        } else if (((IControlNode) activityNode).isJoinNode()) {
                            activityNode = defineJoin(activityNode, nodes, 0); // create join node and set next action node
                        } else if (((IControlNode) activityNode).isDecisionMergeNode()) {

                            if (activityNode.getOutgoings().length > 1) {
                                activityNode = defineDecision(activityNode, nodes, 0); // create decision node and set next action node
                            } else {
                                IFlow flows[] = activityNode.getIncomings();///
                                boolean decision = false;
                                for (int i = 0; i < flows.length; i++) {

                                    String stereotype[] = flows[i].getStereotypes();

                                    for (int j = 0; j < stereotype.length; j++) {
                                        if (stereotype[j].equals("decisionInputFlow")) {
                                            decision = true;
                                        }
                                    }


                                }

                                if (decision) {
                                    activityNode = defineDecision(activityNode, nodes, 0); // create decision node and set next action node
                                } else {
                                    activityNode = defineMerge(activityNode, nodes, 0); // create merge node and set next action node
                                }
                            }
                        }
                    } else if (activityNode instanceof IActivityParameterNode) {
                        if (activityNode.getOutgoings().length > 0) {
                            activityNode = defineInputParameterNode(activityNode, nodes);
                        } else if (activityNode.getIncomings().length > 0) {
                            activityNode = defineOutputParameterNode(activityNode, nodes);
                        }

                    } else if (activityNode instanceof IObjectNode) {
                        activityNode = defineObjectNode(activityNode, nodes, 0);
                    }
                    input = countAmount(activityNode);

                    if (activityNode != null) {
                        if (activityNode instanceof IAction) {
                            expectedInput = activityNode.getIncomings().length + ((IAction) activityNode).getInputs().length;
                        } else {
                            expectedInput = activityNode.getIncomings().length;
                        }

                        name = activityNode.getName();
                        if (activityNode instanceof IActivityParameterNode) {
                            name = "parameter_" + activityNode.getName();
                        }
                    }
                } else {
                    if (activityNode instanceof IAction) {
                        if (((IAction) activityNode).isCallBehaviorAction()) {
                            queueRecreateNode.add(activityNode);
                            activityNode = defineCallBehaviour(activityNode, nodes, 1);
                        } else {
                            queueRecreateNode.add(activityNode);
                            activityNode = defineAction(activityNode, nodes, 1);    // create action node and set next action node
                        }
                    } else if (activityNode instanceof IControlNode) {
                        if (((IControlNode) activityNode).isFinalNode()) {
                            queueRecreateNode.add(activityNode);
                            activityNode = null;
                        } else if (((IControlNode) activityNode).isFlowFinalNode()) {
                            queueRecreateNode.add(activityNode);
                            activityNode = null;
                        } else if (((IControlNode) activityNode).isForkNode()) {
                            queueRecreateNode.add(activityNode);
                            activityNode = defineFork(activityNode, nodes, 1); // create fork node and set next action node
                        } else if (((IControlNode) activityNode).isJoinNode()) {
                            queueRecreateNode.add(activityNode);
                            activityNode = defineJoin(activityNode, nodes, 1); // create join node and set next action node
                        } else if (((IControlNode) activityNode).isDecisionMergeNode()) {
                            queueRecreateNode.add(activityNode);
                            if (activityNode.getOutgoings().length > 1) {
                                activityNode = defineDecision(activityNode, nodes, 1); // create decision node and set next action node
                            } else {
                                IFlow flows[] = activityNode.getIncomings();
                                boolean decision = false;
                                for (int i = 0; i < flows.length; i++) {

                                    String stereotype[] = flows[i].getStereotypes();

                                    for (int j = 0; j < stereotype.length; j++) {
                                        if (stereotype[j].equals("decisionInputFlow")) {
                                            decision = true;
                                        }
                                    }


                                }

                                if (decision) {
                                    activityNode = defineDecision(activityNode, nodes, 1); // create decision node and set next action node
                                } else {
                                    activityNode = defineMerge(activityNode, nodes, 1); // create merge node and set next action node
                                }
                            }
                        }
                    } else if (activityNode instanceof IObjectNode) {
                        queueRecreateNode.add(activityNode);
                        activityNode = defineObjectNode(activityNode, nodes, 1);
                    }

                    input = countAmount(activityNode);

                    if (activityNode != null) {
                        if (activityNode instanceof IAction) {
                            expectedInput = activityNode.getIncomings().length + ((IAction) activityNode).getInputs().length;
                        } else {
                            expectedInput = activityNode.getIncomings().length;
                        }

                        name = activityNode.getName();
                        if (activityNode instanceof IActivityParameterNode) {
                            name = "parameter_" + activityNode.getName();
                        }
                    }
                }
            }
        }

        while (queueRecreateNode.size() != 0) {
            IActivityNode activityNode = queueRecreateNode.get(0);
            queueRecreateNode.remove(0);

            input = countAmount(activityNode);
            if (activityNode != null) {
                if (activityNode instanceof IAction) {
                    expectedInput = activityNode.getIncomings().length + ((IAction) activityNode).getInputs().length;
                } else {
                    expectedInput = activityNode.getIncomings().length;
                }
            }

            String name = activityNode.getName();
            if (activityNode instanceof IActivityParameterNode) {
                name = "parameter_" + activityNode.getName();
            }

            while (activityNode != null && !alphabetNode.containsKey(nameResolver(name))) {    // Verifica se nó é nulo, se nó já foi criado e se todos os nós de entrada dele já foram criados
                if (activityNode instanceof IAction) {
                    if (((IAction) activityNode).isCallBehaviorAction()) {
                        activityNode = defineCallBehaviour(activityNode, nodes, 2);
                    } else {
                        activityNode = defineAction(activityNode, nodes, 2);    // create action node and set next action node
                    }
                } else if (activityNode instanceof IControlNode) {
                    if (((IControlNode) activityNode).isFinalNode()) {
                        activityNode = defineFinalNode(activityNode, nodes); // create final node and set next action node
                    } else if (((IControlNode) activityNode).isFlowFinalNode()) {
                        activityNode = defineFlowFinal(activityNode, nodes); // create flow final and set next action node
                    } else if (((IControlNode) activityNode).isForkNode()) {
                        activityNode = defineFork(activityNode, nodes, 2); // create fork node and set next action node
                    } else if (((IControlNode) activityNode).isJoinNode()) {
                        activityNode = defineJoin(activityNode, nodes, 2); // create join node and set next action node
                    } else if (((IControlNode) activityNode).isDecisionMergeNode()) {

                        if (activityNode.getOutgoings().length > 1) {
                            activityNode = defineDecision(activityNode, nodes, 2); // create decision node and set next action node
                        } else {
                            IFlow flows[] = activityNode.getIncomings();
                            boolean decision = false;
                            for (int i = 0; i < flows.length; i++) {

                                String stereotype[] = flows[i].getStereotypes();

                                for (int j = 0; j < stereotype.length; j++) {
                                    if (stereotype[j].equals("decisionInputFlow")) {
                                        decision = true;
                                    }
                                }


                            }

                            if (decision) {
                                activityNode = defineDecision(activityNode, nodes, 2); // create decision node and set next action node
                            } else {
                                activityNode = defineMerge(activityNode, nodes, 2); // create merge node and set next action node
                            }
                        }
                    }
                } else if (activityNode instanceof IObjectNode) {
                    activityNode = defineObjectNode(activityNode, nodes, 2);
                }

                input = countAmount(activityNode);

                if (activityNode != null) {
                    if (activityNode instanceof IAction) {
                        expectedInput = activityNode.getIncomings().length + ((IAction) activityNode).getInputs().length;
                    } else {
                        expectedInput = activityNode.getIncomings().length;
                    }

                    name = activityNode.getName();
                    if (activityNode instanceof IActivityParameterNode) {
                        name = "parameter_" + activityNode.getName();
                    }
                }
            }
        }

        //add initial central
        if (allInitial.size() > 0) {
            nodes.append("init_" + nameResolver(ad.getName()) + "_t = (" + allInitial.get(0));
            for (int i = 1; i < allInitial.size(); i++) {
                nodes.append(" ||| " + allInitial.get(i));
            }
        }

        nodes.append(") /\\ END_DIAGRAM_" + nameResolver(ad.getName()));
        alphabetAllInitialAndParameter.add("endDiagram_" + nameResolver(ad.getName()));

        alphabetNode.put("init", alphabetAllInitialAndParameter);

        System.out.println(nodes);

        nodes.append("\n");

        return nodes.toString();
    }

    public String defineLock() {
        StringBuilder locks = new StringBuilder();
        String nameDiagram = nameResolver(ad.getName());

        if (lockChannel.size() > 0) {
            for (String lock : lockChannel) {
                locks.append("Lock_" + lock + " = lock_" + lock + ".lock -> lock_" + lock + ".unlock -> Lock_" + lock + " [] endDiagram_" + nameDiagram + " -> SKIP\n");
            }

            locks.append("Lock_" + nameDiagram + " = ");

            if (lockChannel.size() == 1) {
                locks.append("Lock_" + lockChannel.get(0) + "\n");
            } else {
                for (int i = 0; i < lockChannel.size() - 1; i++) {
                    locks.append("(");
                }

                locks.append("Lock_" + lockChannel.get(0));

                for (int i = 1; i < lockChannel.size(); i++) {
                    locks.append(" [|{|endDiagram_" + nameDiagram + "|}|] Lock_" + lockChannel.get(i) + ")");
                }

                locks.append("\n");
            }
        }

        System.out.println(locks);

        return locks.toString();
    }

    public String defineTokenManager() {
        StringBuilder tokenManager = new StringBuilder();
        String nameDiagram = nameResolver(ad.getName());

        tokenManager.append("TokenManager_" + nameDiagram + "(x,init) = update_" + nameDiagram + "?c?y:limiteUpdate_" + nameDiagram
                + " -> x+y < 10 & x+y > -10 & TokenManager_" + nameDiagram + "(x+y,1) [] clear_" + nameDiagram + "?c -> endDiagram_" + nameDiagram
                + " -> SKIP [] x == 0 & init == 1 & endDiagram_" + nameDiagram + " -> SKIP\n");
        tokenManager.append("TokenManager_" + nameResolver(ad.getName()) + "_t(x,init) = TokenManager_" + nameDiagram + "(x,init)\n");

        System.out.println(tokenManager.toString());

        return tokenManager.toString();
    }

    public String defineProcessSync() {
        StringBuilder processSync = new StringBuilder();
        String termination = "_" + nameResolver(ad.getName()) + "_t";

        processSync.append("Node_" + nameResolver(ad.getName()) + " = ");

        if (alphabetNode.size() == 1) {
            for (String node : alphabetNode.keySet()) {
                processSync.append(node + termination + "\n");
            }
        } else {
            for (int i = 0; i < alphabetNode.size() - 1; i++) {
                processSync.append("(");
            }

            ArrayList<String> set = null;    // total set

            int add = 1;
            for (String node : alphabetNode.keySet()) {        //add first and second
                if (add == 1) {
                    ArrayList<String> alphabet = alphabetNode.get(node);
                    processSync.append(node + termination + " [{|");

                    if (alphabet.size() == 1) {
                        processSync.append(alphabet.get(0) + "|}||");
                    } else {

                        processSync.append(alphabet.get(0));

                        for (int i = 1; i < alphabet.size(); i++) {
                            processSync.append("," + alphabet.get(i));
                        }

                        processSync.append("|}||");
                    }

                    set = new ArrayList<>(alphabet);
                }

                if (add == 2) {
                    ArrayList<String> alphabet = alphabetNode.get(node);
                    processSync.append("{|");

                    if (alphabet.size() == 1) {
                        processSync.append(alphabet.get(0) + "|}]");
                    } else {

                        processSync.append(alphabet.get(0));

                        for (int i = 1; i < alphabet.size(); i++) {
                            processSync.append("," + alphabet.get(i));
                        }

                        processSync.append("|}] " + node + termination + ")");
                    }

                    for (String channel : alphabet) {        //add channels
                        if (!set.contains(channel)) {
                            set.add(channel);
                        }
                    }
                }

                add++;
            }

            add = 1;
            for (String node : alphabetNode.keySet()) {        //add first and second
                if (add > 2) {
                    processSync.append(" [{|");

                    if (set.size() == 1) {
                        processSync.append(set.get(0) + "|}||");
                    } else {

                        processSync.append(set.get(0));

                        for (int i = 1; i < set.size(); i++) {
                            processSync.append("," + set.get(i));
                        }

                        processSync.append("|}||");
                    }


                    ArrayList<String> alphabet = alphabetNode.get(node);
                    processSync.append("{|");

                    if (alphabet.size() == 1) {
                        processSync.append(alphabet.get(0) + "|}]");
                    } else {

                        processSync.append(alphabet.get(0));

                        for (int i = 1; i < alphabet.size(); i++) {
                            processSync.append("," + alphabet.get(i));
                        }

                        processSync.append("|}] " + node + termination + ")");
                    }

                    for (String channel : alphabet) {        //add channels
                        if (!set.contains(channel)) {
                            set.add(channel);
                        }
                    }
                }

                add++;
            }

        }

        processSync.append("\n");

        System.out.println(processSync.toString());

        return processSync.toString();
    }

    private int countAmount(IActivityNode activityNode) {
        int input = 0;
        if (activityNode != null) {
            input = 0;
            IFlow inFlow[] = activityNode.getIncomings();

            for (int i = 0; i < inFlow.length; i++) {
                System.out.println("aq " + inFlow[i].getId() + " " + nameResolver(activityNode.getName()));
                if (syncChannelsEdge.containsKey(inFlow[i].getId())) {
                    input++;
                }
            }


            if (activityNode instanceof IAction) {
                IInputPin inPin[] = ((IAction) activityNode).getInputs();

                for (int i = 0; i < inPin.length; i++) {
                    IFlow inFlowPin[] = inPin[i].getIncomings();
                    for (int x = 0; x < inFlowPin.length; x++) {
                        System.out.println("aq " + inFlowPin[x].getId() + " " + nameResolver(activityNode.getName()));
                        if (syncObjectsEdge.containsKey(inFlowPin[x].getId())) {
                            input++;
                        }
                    }
                }

            } else {
                for (int i = 0; i < inFlow.length; i++) {
                    System.out.println("aq " + inFlow[i].getId() + " " + nameResolver(activityNode.getName()));
                    if (syncObjectsEdge.containsKey(inFlow[i].getId())) {
                        input++;
                    }
                }
            }
            if (activityNode instanceof  IAction) {
                System.out.println(nameResolver(activityNode.getName()) + " " + input + " " + (activityNode.getIncomings().length + ((IAction) activityNode).getInputs().length));
            } else {
                System.out.println(nameResolver(activityNode.getName()) + " " + input + " " + activityNode.getIncomings().length);
            }
        }

        return input;
    }

    private IActivityNode defineAction(IActivityNode activityNode, StringBuilder nodes, int code) {
        StringBuilder action = new StringBuilder();
        ArrayList<String> alphabet = new ArrayList<>();
        String nameAction = nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName());
        String nameActionTermination = nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + "_t";
        String endDiagram = "END_DIAGRAM_" + nameResolver(ad.getName());
        IFlow outFlows[] = activityNode.getOutgoings();
        IFlow inFlows[] = activityNode.getIncomings();
        IOutputPin outPins[] = ((IAction) activityNode).getOutputs();
        IInputPin inPins[] = ((IAction) activityNode).getInputs();
//		boolean syncBool = false;
//		boolean sync2Bool = false;
        List<String> namesMemoryLocal = new ArrayList<>();
        HashMap<String, String> typeMemoryLocal = new HashMap<>();
        int countInFlowPin = 0;
        int countOutFlowPin = 0;
        //ArrayList<Pair<String, String>> ceInitials = new ArrayList<>();
        //ArrayList<Pair<String, String>> oeInitials = new ArrayList<>();

        if (code == 0) {
            String definition = activityNode.getDefinition();
            String definitionFinal[] = new String[0];

            if (definition != null && !(definition.equals(""))) {
                definitionFinal = definition.replace(" ", "").split(";");
            }


            action.append(nameAction + " = ");


            action.append("(");
            for (int i = 0; i < inFlows.length; i++) {
                if (syncChannelsEdge.containsKey(inFlows[i].getId())) {
                    String ceIn = syncChannelsEdge.get(inFlows[i].getId());

                    action.append("(");
                    if (i >= 0 && (i < inFlows.length - 1 || inPins.length > 0)) {
                        ce(alphabet, action, ceIn, " -> SKIP) ||| ");
                    } else {
                        ce(alphabet, action, ceIn, " -> SKIP)");
                    }

                    //ceInitials.add(tupla);
                    //syncBool = true;
                }
            }

            for (int i = 0; i < inPins.length; i++) {
                IFlow inFlowPin[] = inPins[i].getIncomings();
                for (int x = 0; x < inFlowPin.length; x++) {
                    if (syncObjectsEdge.containsKey(inFlowPin[x].getId())) {
                        String oeIn = syncObjectsEdge.get(inFlowPin[x].getId());
                        //String nameObject = objectEdges.get(oeIn);
                        String typeNameObject = objectEdges.get(oeIn);
                        String nameObject = inPins[i].getName();

                        action.append("(");
                        if (i >= 0 && (i < inPins.length - 1 || x < inFlowPin.length - 1)) {
                            oe(alphabet, action, oeIn, "?" + nameObject, " -> ");
                            setLocalInput(alphabet, action, nameObject, nameResolver(activityNode.getName()), nameObject, oeIn);
                            action.append("SKIP) ||| ");
                        } else {
                            oe(alphabet, action, oeIn, "?" + nameObject, " -> ");
                            setLocalInput(alphabet, action, nameObject, nameResolver(activityNode.getName()), nameObject, oeIn);
                            action.append("SKIP)");
                        }

                        if (!namesMemoryLocal.contains(nameObject)) {
                            namesMemoryLocal.add(nameObject);
                            typeMemoryLocal.put(nameObject, typeNameObject);
                        }

                        //oeInitials.add(tupla);
                        //sync2Bool = true;
                    }
                }
            }

            action.append("); ");

            lock(alphabet, action, 0, nameAction);
            event(alphabet, nameAction, action);

            for (int i = 0; i < namesMemoryLocal.size(); i++) {
                for (int j = 0; j < definitionFinal.length; j++) {
                    String expression[] = definitionFinal[j].split("=");
                    if (expression[0].equals(namesMemoryLocal.get(i))) {
                        List<String> expReplaced = replaceExpression(expression[1]);    //get expression replace '+','-','*','/'
                        for (String value : expReplaced) {                //get all parts
                            for (int x = 0; x < namesMemoryLocal.size(); x++) {
                                if (value.equals(namesMemoryLocal.get(x))) {
                                    getLocal(alphabet, action, namesMemoryLocal.get(x), nameResolver(activityNode.getName()), namesMemoryLocal.get(x));
                                }
                            }
                        }

                        setLocal(alphabet, action, expression[0], nameResolver(activityNode.getName()), "(" + expression[1] + ")");

                    }
                }
            }

            //count outFlowsPin
            for (int i = 0; i < inPins.length; i++) {
                countInFlowPin += inPins[i].getIncomings().length;
            }

            for (int i = 0; i < outPins.length; i++) {
                countOutFlowPin += outPins[i].getOutgoings().length;
            }

            lock(alphabet, action, 1, nameAction);
            update(alphabet, action, inFlows.length + countInFlowPin, outFlows.length + countOutFlowPin);

            for (String nameObj : namesMemoryLocal) {
                getLocal(alphabet, action, nameObj, nameResolver(activityNode.getName()), nameObj);
            }

            if (outFlows.length > 0 || outPins.length > 0) {
                action.append("(");
            }

            for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                String ce = createCE();
                syncChannelsEdge.put(outFlows[i].getId(), ce);

                action.append("(");

                if (i >= 0 && (i < outFlows.length - 1 || outPins.length > 0)) {
                    ce(alphabet, action, ce, " -> SKIP) ||| ");
                } else {
                    ce(alphabet, action, ce, " -> SKIP)");
                }
            }

            String nameObject = "";
            String lastName = "";
            ArrayList<String> union = new ArrayList<>();

            for (int i = 0; i < inPins.length; i++) {
                IFlow inFlowPin[] = inPins[i].getIncomings();
                for (int x = 0; x < inFlowPin.length; x++) {
                    String channel = syncObjectsEdge.get(inFlowPin[x].getId());
                    nameObject += objectEdges.get(channel);
                    union.add(objectEdges.get(channel));
                    lastName = objectEdges.get(channel);
                }
            }

            if (union.size() > 1) {
                unionList.add(union);
                typeUnionList.put(nameObject, parameterNodesInput.get(lastName));
            }

            for (int i = 0; i < outPins.length; i++) {    //creates the parallel output channels
                IFlow outFlowPin[] = outPins[i].getOutgoings();

                for (int x = 0; x < outFlowPin.length; x++) {
                    String oe = createOE(nameObject);
                    syncObjectsEdge.put(outFlowPin[x].getId(), oe);

                    objectEdges.put(oe, nameObject);
                    String value = "";
                    for (int j = 0; j < definitionFinal.length; j++) {
                        String expression[] = definitionFinal[j].split("=");
                        if (expression[0].equals(outPins[i].getName())) {
                            value = expression[1];
                        }
                    }

                    action.append("(");
                    if (i >= 0 && (i < outPins.length - 1 || x < outFlowPin.length - 1)) {
                        oe(alphabet, action, oe, "!(" + value + ")", " -> SKIP) ||| ");
                    } else {
                        oe(alphabet, action, oe, "!(" + value + ")", " -> SKIP)");
                    }

                }
            }

            if (outFlows.length > 0 || outPins.length > 0) {
                action.append("); ");
            }

            action.append(nameAction + "\n");

            action.append(nameActionTermination + " = ");

            if (namesMemoryLocal.size() > 0) {
                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                    action.append("(");
                }
                action.append("(" + nameAction + " /\\ " + endDiagram + ") ");

                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                    action.append("[|{|");
                    action.append("get_" + namesMemoryLocal.get(i) + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                    action.append("set_" + namesMemoryLocal.get(i) + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                    action.append("endDiagram_" + nameResolver(ad.getName()));
                    action.append("|}|] ");

                    String typeObj = parameterNodesInput.get(typeMemoryLocal.get(namesMemoryLocal.get(i)));
                    if (typeObj == null) {
                        typeObj = typeUnionList.get(typeMemoryLocal.get(namesMemoryLocal.get(i)));
                    }

                    action.append("Mem_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + "_" + namesMemoryLocal.get(i) + "_t(" + getDefaultValue(typeObj) + ")) ");
                }

                action.append("\\{|");

                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                    if (i == namesMemoryLocal.size() - 1) {
                        action.append("get_" + namesMemoryLocal.get(i) + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                        action.append("set_" + namesMemoryLocal.get(i) + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()));
                    } else {
                        action.append("get_" + namesMemoryLocal.get(i) + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                        action.append("set_" + namesMemoryLocal.get(i) + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                    }
                }

                action.append("|}\n");

            } else {
                action.append(nameAction + " /\\ " + endDiagram + "\n");
            }

            alphabet.add("endDiagram_" + nameResolver(ad.getName()));
            alphabetNode.put(nameResolver(activityNode.getName()), alphabet);

            if (outFlows.length > 0) {
                activityNode = outFlows[0].getTarget();    //set next action or control node

                for (int i = 1; i < outFlows.length; i++) {    //puts the remaining nodes in the queue
                    if (!queueNode.contains(outFlows[i].getTarget())) {
                        queueNode.add(outFlows[i].getTarget());
                    }
                }

                for (int i = 0; i < outPins.length; i++) {    //creates the parallel output channels
                    IFlow outFlowPin[] = outPins[i].getOutgoings();
                    for (int x = 0; x < outFlowPin.length; x++) {
                        if (outFlowPin[x].getTarget() instanceof IInputPin) {
                            for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                                if (activityNodeSearch instanceof IAction) {
                                    IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
                                    for (int y = 0; y < inFlowPin.length; y++) {
                                        if (inFlowPin[y].getId().equals(outFlowPin[x].getTarget().getId())) {
                                            if (!queueNode.contains(activityNodeSearch)) {
                                                queueNode.add(activityNodeSearch);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            if (!queueNode.contains(outFlowPin[x].getTarget())) {
                                queueNode.add(outFlowPin[x].getTarget());
                            }
                        }
                    }
                }

            } else if (outPins.length > 0) {

                IFlow outFlowOut[] = outPins[0].getOutgoings();
                if (outFlowOut[0].getTarget() instanceof IInputPin) {
                    for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                        if (activityNodeSearch instanceof IAction) {
                            IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
                            for (int y = 0; y < inFlowPin.length; y++) {
                                if (inFlowPin[y].getId().equals(outFlowOut[0].getTarget().getId())) {
                                    activityNode = activityNodeSearch;
                                }
                            }
                        }
                    }
                } else {
                    activityNode = outFlowOut[0].getTarget();
                }

                for (int i = 0; i < outPins.length; i++) {    //creates the parallel output channels
                    IFlow outFlowPin[] = outPins[i].getOutgoings();
                    for (int x = 0; x < outFlowPin.length; x++) {
                        if (outFlowPin[x].getTarget() instanceof IInputPin) {
                            for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                                if (activityNodeSearch instanceof IAction) {
                                    IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
                                    for (int y = 0; y < inFlowPin.length; y++) {
                                        if (inFlowPin[y].getId().equals(outFlowPin[x].getTarget().getId())) {
                                            if (!queueNode.contains(activityNodeSearch) && (i != 0 || x != 0)) {
                                                queueNode.add(activityNodeSearch);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            if (!queueNode.contains(outFlowPin[x].getTarget()) && (i != 0 || x != 0)) {
                                queueNode.add(outFlowPin[x].getTarget());
                            }
                        }
                    }
                }
            } else {
                activityNode = null;
            }

            nodes.append(action.toString());
        } else if (code == 1) {
            String definition = activityNode.getDefinition();
            String definitionFinal[] = new String[0];

            if (outFlows.length > 0 || outPins.length > 0) {
                action.append("(");
            }

            for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                String ce = createCE();
                syncChannelsEdge.put(outFlows[i].getId(), ce);

                action.append("(");

                if (i >= 0 && (i < outFlows.length - 1 || outPins.length > 0)) {
                    ce(alphabet, action, ce, " -> SKIP) ||| ");
                } else {
                    ce(alphabet, action, ce, " -> SKIP)");
                }
            }

            String nameObject = "";
            String lastName = "";

            ArrayList<String> union = new ArrayList<>();
            List<String> nameObjects = new ArrayList<>();
            List<String> nodesAdded = new ArrayList<>();

            for (int i = 0; i < inPins.length; i++) {
                IFlow inFlowPin[] = inPins[i].getIncomings();
                for (int x = 0; x < inFlowPin.length; x++) {

                    nameObjects.addAll(getObjects(inFlowPin[x], nodesAdded));

                }
            }

            for (String nameObj : nameObjects) {
                if (!union.contains(nameObj)) {
                    nameObject += nameObj;
                    union.add(nameObj);
                    lastName = nameObj;
                }
            }

            if (union.size() > 1) {
                unionList.add(union);
                typeUnionList.put(nameObject, parameterNodesInput.get(lastName));
            }

            for (int i = 0; i < outPins.length; i++) {    //creates the parallel output channels
                IFlow outFlowPin[] = outPins[i].getOutgoings();

                for (int x = 0; x < outFlowPin.length; x++) {
                    String oe = createOE(nameObject);
                    syncObjectsEdge.put(outFlowPin[x].getId(), oe);

                    objectEdges.put(oe, nameObject);
                    String value = "";
                    for (int j = 0; j < definitionFinal.length; j++) {
                        String expression[] = definitionFinal[j].split("=");
                        if (expression[0].equals(outPins[i].getName())) {
                            value = expression[1];
                        }
                    }

                    action.append("(");
                    if (i >= 0 && (i < outPins.length - 1 || x < outFlowPin.length - 1)) {
                        oe(alphabet, action, oe, "!(" + value + ")", " -> SKIP) ||| ");
                    } else {
                        oe(alphabet, action, oe, "!(" + value + ")", " -> SKIP)");
                    }

                }
            }

            if (outFlows.length > 0 || outPins.length > 0) {
                action.append("); ");
            }

            if (outFlows.length > 0) {
                activityNode = outFlows[0].getTarget();    //set next action or control node

                for (int i = 1; i < outFlows.length; i++) {    //puts the remaining nodes in the queue
                    if (!queueNode.contains(outFlows[i].getTarget())) {
                        queueNode.add(outFlows[i].getTarget());
                    }
                }

                for (int i = 0; i < outPins.length; i++) {    //creates the parallel output channels
                    IFlow outFlowPin[] = outPins[i].getOutgoings();
                    for (int x = 0; x < outFlowPin.length; x++) {
                        if (outFlowPin[x].getTarget() instanceof IInputPin) {
                            for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                                if (activityNodeSearch instanceof IAction) {
                                    IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
                                    for (int y = 0; y < inFlowPin.length; y++) {
                                        if (inFlowPin[y].getId().equals(outFlowPin[x].getTarget().getId())) {
                                            if (!queueNode.contains(activityNodeSearch)) {
                                                queueNode.add(activityNodeSearch);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            if (!queueNode.contains(outFlowPin[x].getTarget())) {
                                queueNode.add(outFlowPin[x].getTarget());
                            }
                        }
                    }
                }

            } else if (outPins.length > 0) {

                IFlow outFlowOut[] = outPins[0].getOutgoings();
                if (outFlowOut[0].getTarget() instanceof IInputPin) {
                    for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                        if (activityNodeSearch instanceof IAction) {
                            IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
                            for (int y = 0; y < inFlowPin.length; y++) {
                                if (inFlowPin[y].getId().equals(outFlowOut[0].getTarget().getId())) {
                                    activityNode = activityNodeSearch;
                                }
                            }
                        }
                    }
                } else {
                    activityNode = outFlowOut[0].getTarget();
                }

                for (int i = 0; i < outPins.length; i++) {    //creates the parallel output channels
                    IFlow outFlowPin[] = outPins[i].getOutgoings();
                    for (int x = 0; x < outFlowPin.length; x++) {
                        if (outFlowPin[x].getTarget() instanceof IInputPin) {
                            for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                                if (activityNodeSearch instanceof IAction) {
                                    IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
                                    for (int y = 0; y < inFlowPin.length; y++) {
                                        if (inFlowPin[y].getId().equals(outFlowPin[x].getTarget().getId())) {
                                            if (!queueNode.contains(activityNodeSearch) && (i != 0 || x != 0)) {
                                                queueNode.add(activityNodeSearch);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            if (!queueNode.contains(outFlowPin[x].getTarget()) && (i != 0 || x != 0)) {
                                queueNode.add(outFlowPin[x].getTarget());
                            }
                        }
                    }
                }
            } else {
                activityNode = null;
            }
        } else if (code == 2) {
            String definition = activityNode.getDefinition();
            String definitionFinal[] = new String[0];

            if (definition != null && !(definition.equals(""))) {
                definitionFinal = definition.replace(" ", "").split(";");
            }


            action.append(nameAction + " = ");


            action.append("(");
            for (int i = 0; i < inFlows.length; i++) {
                if (syncChannelsEdge.containsKey(inFlows[i].getId())) {
                    String ceIn = syncChannelsEdge.get(inFlows[i].getId());

                    action.append("(");
                    if (i >= 0 && (i < inFlows.length - 1 || inPins.length > 0)) {
                        ce(alphabet, action, ceIn, " -> SKIP) ||| ");
                    } else {
                        ce(alphabet, action, ceIn, " -> SKIP)");
                    }

                    //ceInitials.add(tupla);
                    //syncBool = true;
                }
            }

            for (int i = 0; i < inPins.length; i++) {
                IFlow inFlowPin[] = inPins[i].getIncomings();
                for (int x = 0; x < inFlowPin.length; x++) {
                    if (syncObjectsEdge.containsKey(inFlowPin[x].getId())) {
                        String oeIn = syncObjectsEdge.get(inFlowPin[x].getId());
                        //String nameObject = objectEdges.get(oeIn);
                        String typeNameObject = objectEdges.get(oeIn);
                        String nameObject = inPins[i].getName();

                        action.append("(");
                        if (i >= 0 && (i < inPins.length - 1 || x < inFlowPin.length - 1)) {
                            oe(alphabet, action, oeIn, "?" + nameObject, " -> ");
                            setLocalInput(alphabet, action, nameObject, nameResolver(activityNode.getName()), nameObject, oeIn);
                            action.append("SKIP) ||| ");
                        } else {
                            oe(alphabet, action, oeIn, "?" + nameObject, " -> ");
                            setLocalInput(alphabet, action, nameObject, nameResolver(activityNode.getName()), nameObject, oeIn);
                            action.append("SKIP)");
                        }

                        if (!namesMemoryLocal.contains(nameObject)) {
                            namesMemoryLocal.add(nameObject);
                            typeMemoryLocal.put(nameObject, typeNameObject);
                        }

                        //oeInitials.add(tupla);
                        //sync2Bool = true;
                    }
                }
            }

            action.append("); ");

            lock(alphabet, action, 0, nameAction);
            event(alphabet, nameAction, action);

            for (int i = 0; i < namesMemoryLocal.size(); i++) {
                for (int j = 0; j < definitionFinal.length; j++) {
                    String expression[] = definitionFinal[j].split("=");
                    if (expression[0].equals(namesMemoryLocal.get(i))) {
                        List<String> expReplaced = replaceExpression(expression[1]);    //get expression replace '+','-','*','/'
                        for (String value : expReplaced) {                //get all parts
                            for (int x = 0; x < namesMemoryLocal.size(); x++) {
                                if (value.equals(namesMemoryLocal.get(x))) {
                                    getLocal(alphabet, action, namesMemoryLocal.get(x), nameResolver(activityNode.getName()), namesMemoryLocal.get(x));
                                }
                            }
                        }

                        setLocal(alphabet, action, expression[0], nameResolver(activityNode.getName()), "(" + expression[1] + ")");

                    }
                }
            }

            //count outFlowsPin
            for (int i = 0; i < inPins.length; i++) {
                countInFlowPin += inPins[i].getIncomings().length;
            }

            for (int i = 0; i < outPins.length; i++) {
                countOutFlowPin += outPins[i].getOutgoings().length;
            }

            lock(alphabet, action, 1, nameAction);
            update(alphabet, action, inFlows.length + countInFlowPin, outFlows.length + countOutFlowPin);

            for (String nameObj : namesMemoryLocal) {
                getLocal(alphabet, action, nameObj, nameResolver(activityNode.getName()), nameObj);
            }

            if (outFlows.length > 0 || outPins.length > 0) {
                action.append("(");
            }

            for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                String ce = syncChannelsEdge.get(outFlows[i].getId());

                action.append("(");

                if (i >= 0 && (i < outFlows.length - 1 || outPins.length > 0)) {
                    ce(alphabet, action, ce, " -> SKIP) ||| ");
                } else {
                    ce(alphabet, action, ce, " -> SKIP)");
                }
            }

            for (int i = 0; i < outPins.length; i++) {    //creates the parallel output channels
                IFlow outFlowPin[] = outPins[i].getOutgoings();

                for (int x = 0; x < outFlowPin.length; x++) {
                    String oe = syncObjectsEdge.get(outFlowPin[x].getId());

                    String value = "";
                    for (int j = 0; j < definitionFinal.length; j++) {
                        String expression[] = definitionFinal[j].split("=");
                        if (expression[0].equals(outPins[i].getName())) {
                            value = expression[1];
                        }
                    }

                    action.append("(");
                    if (i >= 0 && (i < outPins.length - 1 || x < outFlowPin.length - 1)) {
                        oe(alphabet, action, oe, "!(" + value + ")", " -> SKIP) ||| ");
                    } else {
                        oe(alphabet, action, oe, "!(" + value + ")", " -> SKIP)");
                    }

                }
            }

            if (outFlows.length > 0 || outPins.length > 0) {
                action.append("); ");
            }

            action.append(nameAction + "\n");

            action.append(nameActionTermination + " = ");

            if (namesMemoryLocal.size() > 0) {
                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                    action.append("(");
                }
                action.append("(" + nameAction + " /\\ " + endDiagram + ") ");

                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                    action.append("[|{|");
                    action.append("get_" + namesMemoryLocal.get(i) + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                    action.append("set_" + namesMemoryLocal.get(i) + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                    action.append("endDiagram_" + nameResolver(ad.getName()));
                    action.append("|}|] ");

                    String typeObj = parameterNodesInput.get(typeMemoryLocal.get(namesMemoryLocal.get(i)));
                    if (typeObj == null) {
                        typeObj = typeUnionList.get(typeMemoryLocal.get(namesMemoryLocal.get(i)));
                    }

                    action.append("Mem_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + "_" + namesMemoryLocal.get(i) + "_t(" + getDefaultValue(typeObj) + ")) ");
                }

                action.append("\\{|");

                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                    if (i == namesMemoryLocal.size() - 1) {
                        action.append("get_" + namesMemoryLocal.get(i) + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                        action.append("set_" + namesMemoryLocal.get(i) + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()));
                    } else {
                        action.append("get_" + namesMemoryLocal.get(i) + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                        action.append("set_" + namesMemoryLocal.get(i) + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                    }
                }

                action.append("|}\n");

            } else {
                action.append(nameAction + " /\\ " + endDiagram + "\n");
            }

            alphabet.add("endDiagram_" + nameResolver(ad.getName()));
            alphabetNode.put(nameResolver(activityNode.getName()), alphabet);

            if (outFlows.length > 0) {
                activityNode = outFlows[0].getTarget();    //set next action or control node

                for (int i = 1; i < outFlows.length; i++) {    //puts the remaining nodes in the queue
                    if (!queueNode.contains(outFlows[i].getTarget())) {
                        queueNode.add(outFlows[i].getTarget());
                    }
                }

                for (int i = 0; i < outPins.length; i++) {    //creates the parallel output channels
                    IFlow outFlowPin[] = outPins[i].getOutgoings();
                    for (int x = 0; x < outFlowPin.length; x++) {
                        if (outFlowPin[x].getTarget() instanceof IInputPin) {
                            for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                                if (activityNodeSearch instanceof IAction) {
                                    IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
                                    for (int y = 0; y < inFlowPin.length; y++) {
                                        if (inFlowPin[y].getId().equals(outFlowPin[x].getTarget().getId())) {
                                            if (!queueNode.contains(activityNodeSearch)) {
                                                queueNode.add(activityNodeSearch);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            if (!queueNode.contains(outFlowPin[x].getTarget())) {
                                queueNode.add(outFlowPin[x].getTarget());
                            }
                        }
                    }
                }

            } else if (outPins.length > 0) {

                IFlow outFlowOut[] = outPins[0].getOutgoings();
                if (outFlowOut[0].getTarget() instanceof IInputPin) {
                    for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                        if (activityNodeSearch instanceof IAction) {
                            IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
                            for (int y = 0; y < inFlowPin.length; y++) {
                                if (inFlowPin[y].getId().equals(outFlowOut[0].getTarget().getId())) {
                                    activityNode = activityNodeSearch;
                                }
                            }
                        }
                    }
                } else {
                    activityNode = outFlowOut[0].getTarget();
                }

                for (int i = 0; i < outPins.length; i++) {    //creates the parallel output channels
                    IFlow outFlowPin[] = outPins[i].getOutgoings();
                    for (int x = 0; x < outFlowPin.length; x++) {
                        if (outFlowPin[x].getTarget() instanceof IInputPin) {
                            for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                                if (activityNodeSearch instanceof IAction) {
                                    IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
                                    for (int y = 0; y < inFlowPin.length; y++) {
                                        if (inFlowPin[y].getId().equals(outFlowPin[x].getTarget().getId())) {
                                            if (!queueNode.contains(activityNodeSearch) && (i != 0 || x != 0)) {
                                                queueNode.add(activityNodeSearch);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            if (!queueNode.contains(outFlowPin[x].getTarget()) && (i != 0 || x != 0)) {
                                queueNode.add(outFlowPin[x].getTarget());
                            }
                        }
                    }
                }
            } else {
                activityNode = null;
            }

            nodes.append(action.toString());
        }

        return activityNode;
    }

    private IActivityNode defineFinalNode(IActivityNode activityNode, StringBuilder nodes) {
        StringBuilder finalNode = new StringBuilder();
        ArrayList<String> alphabet = new ArrayList<>();
        String nameFinalNode = nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName());
        String nameFinalNodeTermination = nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + "_t";
        String endDiagram = "END_DIAGRAM_" + nameResolver(ad.getName());
        HashMap<String, String> nameObjects = new HashMap<>();
        IFlow inFlows[] = activityNode.getIncomings();

        finalNode.append(nameFinalNode + " = ");

        ArrayList<String> ceInitials = new ArrayList<>();
        for (int i = 0; i < inFlows.length; i++) {
            ceInitials.add(inFlows[i].getId());

            if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
                String ceIn2 = syncObjectsEdge.get(inFlows[i].getId());
                nameObjects.put(inFlows[i].getId(), objectEdges.get(ceIn2));
            }

        }

        finalNode.append("(");
        for (int i = 0; i < ceInitials.size(); i++) {
            String ceIn = syncChannelsEdge.get(ceInitials.get(i));    //get the parallel input channels
            String oeIn = syncObjectsEdge.get(ceInitials.get(i));

            if (ceIn != null) {
                finalNode.append("(");

                if (i >= 0 && i < ceInitials.size() - 1) {
                    ce(alphabet, finalNode, ceIn, " -> SKIP) [] ");
                } else {
                    ce(alphabet, finalNode, ceIn, " -> SKIP)");
                }
            } else {

                String nameObject = nameObjects.get(ceInitials.get(i));

                finalNode.append("(");

                if (i >= 0 && i < ceInitials.size() - 1) {
                    ce(alphabet, finalNode, oeIn, "?" + nameObject + " -> SKIP) [] ");
                } else {
                    ce(alphabet, finalNode, oeIn, "?" + nameObject + " -> SKIP)");
                }
            }

        }

        finalNode.append("); ");

        clear(alphabet, finalNode);

        finalNode.append("SKIP\n");

        finalNode.append(nameFinalNodeTermination + " = ");
        finalNode.append(nameFinalNode + " /\\ " + endDiagram + "\n");

        alphabet.add("endDiagram_" + nameResolver(ad.getName()));
        alphabetNode.put(nameResolver(activityNode.getName()), alphabet);

        activityNode = null;

        nodes.append(finalNode.toString());

        return activityNode;
    }

    private IActivityNode defineInitialNode(IActivityNode activityNode, StringBuilder nodes) {
        StringBuilder initialNode = new StringBuilder();
        ArrayList<String> alphabet = new ArrayList<>();
        String nameInitialNode = nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + "_t";
        IFlow outFlows[] = activityNode.getOutgoings();
        IFlow inFlows[] = activityNode.getIncomings();

        initialNode.append(nameInitialNode + " = ");

        update(alphabet, initialNode, inFlows.length, outFlows.length);

        initialNode.append("(");

        for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
            String ce = createCE();
            syncChannelsEdge.put(outFlows[i].getId(), ce);
            System.out.println("aa " + outFlows[i].getId() + " " + outFlows[i].getTarget().getName());
            initialNode.append("(");

            if (i >= 0 && i < outFlows.length - 1) {
                ce(alphabet, initialNode, ce, " -> SKIP) ||| ");
            } else {
                ce(alphabet, initialNode, ce, " -> SKIP)");
            }
        }

        initialNode.append(")\n");


//		for (IFlow flow : outFlows) {	//creates output channels
//			String ce = createCN();
//			syncChannels.put(new Pair<String, String>(nameResolver(activityNode.getName()), flow.getTarget().getName()), ce);
//			ce(alphabet, initialNode, ce, " -> ");
//		}
//
//		initialNode.append("SKIP\n");

        allInitial.add(nameInitialNode);
        for (String channel : alphabet) {
            if (!alphabetAllInitialAndParameter.contains(channel)) {
                alphabetAllInitialAndParameter.add(channel);
            }
        }

        activityNode = outFlows[0].getTarget();    //set next action or control node

        for (int i = 1; i < outFlows.length; i++) {    //puts the remaining nodes in the queue
            if (!queueNode.contains(outFlows[i].getTarget())) {
                queueNode.add(outFlows[i].getTarget());
            }
        }

        nodes.append(initialNode.toString());

        return activityNode;
    }

    private IActivityNode defineCallBehaviour(IActivityNode activityNode, StringBuilder nodes, int code) {    //Ainda nao testado
        StringBuilder callBehaviour = new StringBuilder();
        ArrayList<String> alphabet = new ArrayList<>();
        String nameCallBehaviour = nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName());
        String namCallBehaviourTermination = nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + "_t";
        String endDiagram = "END_DIAGRAM_" + nameResolver(ad.getName());
        IFlow outFlows[] = activityNode.getOutgoings();
        IFlow inFlows[] = activityNode.getIncomings();
        IOutputPin outPins[] = ((IAction) activityNode).getOutputs();
        IInputPin inPins[] = ((IAction) activityNode).getInputs();
        List<String> namesMemoryLocal = new ArrayList<>();
        List<String> namesOutpins = new ArrayList<>();
        HashMap<String, String> typeMemoryLocal = new HashMap<>();
        int countInFlowPin = 0;
        int countOutFlowPin = 0;
        callBehaviourList.add(((IAction) activityNode).getCallingActivity());

        for (int i = 0; i < outPins.length; i++) {
            namesOutpins.add(outPins[i].getName());
        }


        if (code == 0) {
            String definition = activityNode.getDefinition();
            String definitionFinal[] = new String[0];

            if (definition != null && !(definition.equals(""))) {
                definitionFinal = definition.replace(" ", "").split(";");
            }


            callBehaviour.append(nameCallBehaviour + " = ");


            callBehaviour.append("(");
            for (int i = 0; i < inFlows.length; i++) {
                if (syncChannelsEdge.containsKey(inFlows[i].getId())) {
                    String ceIn = syncChannelsEdge.get(inFlows[i].getId());

                    callBehaviour.append("(");
                    if (i >= 0 && (i < inFlows.length - 1 || inPins.length > 0)) {
                        ce(alphabet, callBehaviour, ceIn, " -> SKIP) ||| ");
                    } else {
                        ce(alphabet, callBehaviour, ceIn, " -> SKIP)");
                    }

                    //ceInitials.add(tupla);
                    //syncBool = true;
                }
            }

            for (int i = 0; i < inPins.length; i++) {
                IFlow inFlowPin[] = inPins[i].getIncomings();
                for (int x = 0; x < inFlowPin.length; x++) {
                    if (syncObjectsEdge.containsKey(inFlowPin[x].getId())) {
                        String oeIn = syncObjectsEdge.get(inFlowPin[x].getId());
                        //String nameObject = objectEdges.get(oeIn);
                        String typeNameObject = objectEdges.get(oeIn);
                        String nameObject = inPins[i].getName();

                        callBehaviour.append("(");
                        if (i >= 0 && (i < inPins.length - 1 || x < inFlowPin.length - 1)) {
                            oe(alphabet, callBehaviour, oeIn, "?" + nameObject, " -> ");
                            setLocalInput(alphabet, callBehaviour, nameObject, nameResolver(activityNode.getName()), nameObject, oeIn);
                            callBehaviour.append("SKIP) ||| ");
                        } else {
                            oe(alphabet, callBehaviour, oeIn, "?" + nameObject, " -> ");
                            setLocalInput(alphabet, callBehaviour, nameObject, nameResolver(activityNode.getName()), nameObject, oeIn);
                            callBehaviour.append("SKIP)");
                        }

                        if (!namesMemoryLocal.contains(nameObject)) {
                            namesMemoryLocal.add(nameObject);
                            typeMemoryLocal.put(nameObject, typeNameObject);
                        }

                        //oeInitials.add(tupla);
                        //sync2Bool = true;
                    }
                }
            }

            callBehaviour.append("); ");

            //count outFlowsPin
            for (int i = 0; i < inPins.length; i++) {
                countInFlowPin += inPins[i].getIncomings().length;
            }

            for (int i = 0; i < outPins.length; i++) {
                countOutFlowPin += outPins[i].getOutgoings().length;
            }

            for (String nameObj : namesMemoryLocal) {
                getLocal(alphabet, callBehaviour, nameObj, nameResolver(activityNode.getName()), nameObj);
            }
            //call
            int count = startActivity(alphabet, callBehaviour, ((IAction) activityNode).getCallingActivity().getName(), namesMemoryLocal);
            endActivity(alphabet, callBehaviour, ((IAction) activityNode).getCallingActivity().getName(), namesOutpins, count);

            update(alphabet, callBehaviour, inFlows.length + countInFlowPin, outFlows.length + countOutFlowPin);

            if (outFlows.length > 0 || outPins.length > 0) {
                callBehaviour.append("(");
            }

            for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                String ce = createCE();
                syncChannelsEdge.put(outFlows[i].getId(), ce);

                callBehaviour.append("(");

                if (i >= 0 && (i < outFlows.length - 1 || outPins.length > 0)) {
                    ce(alphabet, callBehaviour, ce, " -> SKIP) ||| ");
                } else {
                    ce(alphabet, callBehaviour, ce, " -> SKIP)");
                }
            }

            String nameObject = "";
            String lastName = "";
            ArrayList<String> union = new ArrayList<>();

            for (int i = 0; i < inPins.length; i++) {
                IFlow inFlowPin[] = inPins[i].getIncomings();
                for (int x = 0; x < inFlowPin.length; x++) {
                    String channel = syncObjectsEdge.get(inFlowPin[x].getId());
                    nameObject += objectEdges.get(channel);
                    union.add(objectEdges.get(channel));
                    lastName = objectEdges.get(channel);
                }
            }

            if (union.size() > 1) {
                unionList.add(union);
                typeUnionList.put(nameObject, parameterNodesInput.get(lastName));
            }

            for (int i = 0; i < outPins.length; i++) {    //creates the parallel output channels
                IFlow outFlowPin[] = outPins[i].getOutgoings();

                for (int x = 0; x < outFlowPin.length; x++) {
                    String oe = createOE(nameObject);
                    syncObjectsEdge.put(outFlowPin[x].getId(), oe);

                    objectEdges.put(oe, nameObject);
                    String value = "";
                    for (int j = 0; j < definitionFinal.length; j++) {
                        String expression[] = definitionFinal[j].split("=");
                        if (expression[0].equals(outPins[i].getName())) {
                            value = expression[1];
                        }
                    }

                    callBehaviour.append("(");
                    if (i >= 0 && (i < outPins.length - 1 || x < outFlowPin.length - 1)) {
                        oe(alphabet, callBehaviour, oe, "!(" + outPins[i].getName() + ")", " -> SKIP) ||| ");
                    } else {
                        oe(alphabet, callBehaviour, oe, "!(" + outPins[i].getName() + ")", " -> SKIP)");
                    }

                }
            }

            if (outFlows.length > 0 || outPins.length > 0) {
                callBehaviour.append("); ");
            }

            callBehaviour.append(nameCallBehaviour + "\n");

            callBehaviour.append(namCallBehaviourTermination + " = ");

            if (namesMemoryLocal.size() > 0) {
                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                    callBehaviour.append("(");
                }
                callBehaviour.append("(" + nameCallBehaviour + " /\\ " + endDiagram + ") ");

                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                    callBehaviour.append("[|{|");
                    callBehaviour.append("get_" + namesMemoryLocal.get(i) + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                    callBehaviour.append("set_" + namesMemoryLocal.get(i) + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                    callBehaviour.append("endDiagram_" + nameResolver(ad.getName()));
                    callBehaviour.append("|}|] ");

                    String typeObj = parameterNodesInput.get(typeMemoryLocal.get(namesMemoryLocal.get(i)));
                    if (typeObj == null) {
                        typeObj = typeUnionList.get(typeMemoryLocal.get(namesMemoryLocal.get(i)));
                    }

                    callBehaviour.append("Mem_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + "_" + namesMemoryLocal.get(i) + "_t(" + getDefaultValue(typeObj) + ")) ");
                }

                callBehaviour.append("\\{|");

                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                    if (i == namesMemoryLocal.size() - 1) {
                        callBehaviour.append("get_" + namesMemoryLocal.get(i) + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                        callBehaviour.append("set_" + namesMemoryLocal.get(i) + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()));
                    } else {
                        callBehaviour.append("get_" + namesMemoryLocal.get(i) + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                        callBehaviour.append("set_" + namesMemoryLocal.get(i) + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                    }
                }

                callBehaviour.append("|}\n");

            } else {
                callBehaviour.append(nameCallBehaviour + " /\\ " + endDiagram + "\n");
            }

            alphabet.add("endDiagram_" + nameResolver(ad.getName()));
            alphabetNode.put(nameResolver(activityNode.getName()), alphabet);

            if (outFlows.length > 0) {
                activityNode = outFlows[0].getTarget();    //set next action or control node

                for (int i = 1; i < outFlows.length; i++) {    //puts the remaining nodes in the queue
                    if (!queueNode.contains(outFlows[i].getTarget())) {
                        queueNode.add(outFlows[i].getTarget());
                    }
                }

                for (int i = 0; i < outPins.length; i++) {    //creates the parallel output channels
                    IFlow outFlowPin[] = outPins[i].getOutgoings();
                    for (int x = 0; x < outFlowPin.length; x++) {
                        if (outFlowPin[x].getTarget() instanceof IInputPin) {
                            for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                                if (activityNodeSearch instanceof IAction) {
                                    IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
                                    for (int y = 0; y < inFlowPin.length; y++) {
                                        if (inFlowPin[y].getId().equals(outFlowPin[x].getTarget().getId())) {
                                            if (!queueNode.contains(activityNodeSearch)) {
                                                queueNode.add(activityNodeSearch);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            if (!queueNode.contains(outFlowPin[x].getTarget())) {
                                queueNode.add(outFlowPin[x].getTarget());
                            }
                        }
                    }
                }

            } else if (outPins.length > 0) {

                IFlow outFlowOut[] = outPins[0].getOutgoings();
                if (outFlowOut[0].getTarget() instanceof IInputPin) {
                    for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                        if (activityNodeSearch instanceof IAction) {
                            IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
                            for (int y = 0; y < inFlowPin.length; y++) {
                                if (inFlowPin[y].getId().equals(outFlowOut[0].getTarget().getId())) {
                                    activityNode = activityNodeSearch;
                                }
                            }
                        }
                    }
                } else {
                    activityNode = outFlowOut[0].getTarget();
                }

                for (int i = 0; i < outPins.length; i++) {    //creates the parallel output channels
                    IFlow outFlowPin[] = outPins[i].getOutgoings();
                    for (int x = 0; x < outFlowPin.length; x++) {
                        if (outFlowPin[x].getTarget() instanceof IInputPin) {
                            for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                                if (activityNodeSearch instanceof IAction) {
                                    IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
                                    for (int y = 0; y < inFlowPin.length; y++) {
                                        if (inFlowPin[y].getId().equals(outFlowPin[x].getTarget().getId())) {
                                            if (!queueNode.contains(activityNodeSearch) && (i != 0 || x != 0)) {
                                                queueNode.add(activityNodeSearch);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            if (!queueNode.contains(outFlowPin[x].getTarget()) && (i != 0 || x != 0)) {
                                queueNode.add(outFlowPin[x].getTarget());
                            }
                        }
                    }
                }
            } else {
                activityNode = null;
            }

            nodes.append(callBehaviour.toString());
        } else if (code == 1) {
            String definition = activityNode.getDefinition();
            String definitionFinal[] = new String[0];

            if (outFlows.length > 0 || outPins.length > 0) {
                callBehaviour.append("(");
            }

            for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                String ce = createCE();
                syncChannelsEdge.put(outFlows[i].getId(), ce);

                callBehaviour.append("(");

                if (i >= 0 && (i < outFlows.length - 1 || outPins.length > 0)) {
                    ce(alphabet, callBehaviour, ce, " -> SKIP) ||| ");
                } else {
                    ce(alphabet, callBehaviour, ce, " -> SKIP)");
                }
            }

            String nameObject = "";
            String lastName = "";

            ArrayList<String> union = new ArrayList<>();
            List<String> nameObjects = new ArrayList<>();
            List<String> nodesAdded = new ArrayList<>();

            for (int i = 0; i < inPins.length; i++) {
                IFlow inFlowPin[] = inPins[i].getIncomings();
                for (int x = 0; x < inFlowPin.length; x++) {

                    nameObjects.addAll(getObjects(inFlowPin[x], nodesAdded));

                }
            }

            for (String nameObj : nameObjects) {
                if (!union.contains(nameObj)) {
                    nameObject += nameObj;
                    union.add(nameObj);
                    lastName = nameObj;
                }
            }

            if (union.size() > 1) {
                unionList.add(union);
                typeUnionList.put(nameObject, parameterNodesInput.get(lastName));
            }

            for (int i = 0; i < outPins.length; i++) {    //creates the parallel output channels
                IFlow outFlowPin[] = outPins[i].getOutgoings();

                for (int x = 0; x < outFlowPin.length; x++) {
                    String oe = createOE(nameObject);
                    syncObjectsEdge.put(outFlowPin[x].getId(), oe);

                    objectEdges.put(oe, nameObject);
                    String value = "";
                    for (int j = 0; j < definitionFinal.length; j++) {
                        String expression[] = definitionFinal[j].split("=");
                        if (expression[0].equals(outPins[i].getName())) {
                            value = expression[1];
                        }
                    }

                    callBehaviour.append("(");
                    if (i >= 0 && (i < outPins.length - 1 || x < outFlowPin.length - 1)) {
                        oe(alphabet, callBehaviour, oe, "!(" + value + ")", " -> SKIP) ||| ");
                    } else {
                        oe(alphabet, callBehaviour, oe, "!(" + value + ")", " -> SKIP)");
                    }

                }
            }

            if (outFlows.length > 0 || outPins.length > 0) {
                callBehaviour.append("); ");
            }

            if (outFlows.length > 0) {
                activityNode = outFlows[0].getTarget();    //set next action or control node

                for (int i = 1; i < outFlows.length; i++) {    //puts the remaining nodes in the queue
                    if (!queueNode.contains(outFlows[i].getTarget())) {
                        queueNode.add(outFlows[i].getTarget());
                    }
                }

                for (int i = 0; i < outPins.length; i++) {    //creates the parallel output channels
                    IFlow outFlowPin[] = outPins[i].getOutgoings();
                    for (int x = 0; x < outFlowPin.length; x++) {
                        if (outFlowPin[x].getTarget() instanceof IInputPin) {
                            for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                                if (activityNodeSearch instanceof IAction) {
                                    IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
                                    for (int y = 0; y < inFlowPin.length; y++) {
                                        if (inFlowPin[y].getId().equals(outFlowPin[x].getTarget().getId())) {
                                            if (!queueNode.contains(activityNodeSearch)) {
                                                queueNode.add(activityNodeSearch);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            if (!queueNode.contains(outFlowPin[x].getTarget())) {
                                queueNode.add(outFlowPin[x].getTarget());
                            }
                        }
                    }
                }

            } else if (outPins.length > 0) {

                IFlow outFlowOut[] = outPins[0].getOutgoings();
                if (outFlowOut[0].getTarget() instanceof IInputPin) {
                    for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                        if (activityNodeSearch instanceof IAction) {
                            IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
                            for (int y = 0; y < inFlowPin.length; y++) {
                                if (inFlowPin[y].getId().equals(outFlowOut[0].getTarget().getId())) {
                                    activityNode = activityNodeSearch;
                                }
                            }
                        }
                    }
                } else {
                    activityNode = outFlowOut[0].getTarget();
                }

                for (int i = 0; i < outPins.length; i++) {    //creates the parallel output channels
                    IFlow outFlowPin[] = outPins[i].getOutgoings();
                    for (int x = 0; x < outFlowPin.length; x++) {
                        if (outFlowPin[x].getTarget() instanceof IInputPin) {
                            for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                                if (activityNodeSearch instanceof IAction) {
                                    IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
                                    for (int y = 0; y < inFlowPin.length; y++) {
                                        if (inFlowPin[y].getId().equals(outFlowPin[x].getTarget().getId())) {
                                            if (!queueNode.contains(activityNodeSearch) && (i != 0 || x != 0)) {
                                                queueNode.add(activityNodeSearch);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            if (!queueNode.contains(outFlowPin[x].getTarget()) && (i != 0 || x != 0)) {
                                queueNode.add(outFlowPin[x].getTarget());
                            }
                        }
                    }
                }
            } else {
                activityNode = null;
            }
        } else if (code == 2) {
            String definition = activityNode.getDefinition();
            String definitionFinal[] = new String[0];

            if (definition != null && !(definition.equals(""))) {
                definitionFinal = definition.replace(" ", "").split(";");
            }


            callBehaviour.append(nameCallBehaviour + " = ");


            callBehaviour.append("(");
            for (int i = 0; i < inFlows.length; i++) {
                if (syncChannelsEdge.containsKey(inFlows[i].getId())) {
                    String ceIn = syncChannelsEdge.get(inFlows[i].getId());

                    callBehaviour.append("(");
                    if (i >= 0 && (i < inFlows.length - 1 || inPins.length > 0)) {
                        ce(alphabet, callBehaviour, ceIn, " -> SKIP) ||| ");
                    } else {
                        ce(alphabet, callBehaviour, ceIn, " -> SKIP)");
                    }

                    //ceInitials.add(tupla);
                    //syncBool = true;
                }
            }

            for (int i = 0; i < inPins.length; i++) {
                IFlow inFlowPin[] = inPins[i].getIncomings();
                for (int x = 0; x < inFlowPin.length; x++) {
                    if (syncObjectsEdge.containsKey(inFlowPin[x].getId())) {
                        String oeIn = syncObjectsEdge.get(inFlowPin[x].getId());
                        //String nameObject = objectEdges.get(oeIn);
                        String typeNameObject = objectEdges.get(oeIn);
                        String nameObject = inPins[i].getName();

                        callBehaviour.append("(");
                        if (i >= 0 && (i < inPins.length - 1 || x < inFlowPin.length - 1)) {
                            oe(alphabet, callBehaviour, oeIn, "?" + nameObject, " -> ");
                            setLocalInput(alphabet, callBehaviour, nameObject, nameResolver(activityNode.getName()), nameObject, oeIn);
                            callBehaviour.append("SKIP) ||| ");
                        } else {
                            oe(alphabet, callBehaviour, oeIn, "?" + nameObject, " -> ");
                            setLocalInput(alphabet, callBehaviour, nameObject, nameResolver(activityNode.getName()), nameObject, oeIn);
                            callBehaviour.append("SKIP)");
                        }

                        if (!namesMemoryLocal.contains(nameObject)) {
                            namesMemoryLocal.add(nameObject);
                            typeMemoryLocal.put(nameObject, typeNameObject);
                        }

                        //oeInitials.add(tupla);
                        //sync2Bool = true;
                    }
                }
            }

            callBehaviour.append("); ");

            //count outFlowsPin
            for (int i = 0; i < inPins.length; i++) {
                countInFlowPin += inPins[i].getIncomings().length;
            }

            for (int i = 0; i < outPins.length; i++) {
                countOutFlowPin += outPins[i].getOutgoings().length;
            }

            for (String nameObj : namesMemoryLocal) {
                getLocal(alphabet, callBehaviour, nameObj, nameResolver(activityNode.getName()), nameObj);
            }

            int count = startActivity(alphabet, callBehaviour, ((IAction) activityNode).getCallingActivity().getName(), namesMemoryLocal);
            endActivity(alphabet, callBehaviour, ((IAction) activityNode).getCallingActivity().getName(), namesOutpins, count);

            update(alphabet, callBehaviour, inFlows.length + countInFlowPin, outFlows.length + countOutFlowPin);

            if (outFlows.length > 0 || outPins.length > 0) {
                callBehaviour.append("(");
            }

            for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                String ce = syncChannelsEdge.get(outFlows[i].getId());

                callBehaviour.append("(");

                if (i >= 0 && (i < outFlows.length - 1 || outPins.length > 0)) {
                    ce(alphabet, callBehaviour, ce, " -> SKIP) ||| ");
                } else {
                    ce(alphabet, callBehaviour, ce, " -> SKIP)");
                }
            }

            for (int i = 0; i < outPins.length; i++) {    //creates the parallel output channels
                IFlow outFlowPin[] = outPins[i].getOutgoings();

                for (int x = 0; x < outFlowPin.length; x++) {
                    String oe = syncObjectsEdge.get(outFlowPin[x].getId());

                    callBehaviour.append("(");
                    if (i >= 0 && (i < outPins.length - 1 || x < outFlowPin.length - 1)) {
                        oe(alphabet, callBehaviour, oe, "!(" + outPins[i].getName() + ")", " -> SKIP) ||| ");
                    } else {
                        oe(alphabet, callBehaviour, oe, "!(" + outPins[i].getName() + ")", " -> SKIP)");
                    }

                }
            }

            if (outFlows.length > 0 || outPins.length > 0) {
                callBehaviour.append("); ");
            }

            callBehaviour.append(nameCallBehaviour + "\n");

            callBehaviour.append(namCallBehaviourTermination + " = ");

            if (namesMemoryLocal.size() > 0) {
                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                    callBehaviour.append("(");
                }
                callBehaviour.append("(" + nameCallBehaviour + " /\\ " + endDiagram + ") ");

                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                    callBehaviour.append("[|{|");
                    callBehaviour.append("get_" + namesMemoryLocal.get(i) + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                    callBehaviour.append("set_" + namesMemoryLocal.get(i) + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                    callBehaviour.append("endDiagram_" + nameResolver(ad.getName()));
                    callBehaviour.append("|}|] ");

                    String typeObj = parameterNodesInput.get(typeMemoryLocal.get(namesMemoryLocal.get(i)));
                    if (typeObj == null) {
                        typeObj = typeUnionList.get(typeMemoryLocal.get(namesMemoryLocal.get(i)));
                    }

                    callBehaviour.append("Mem_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + "_" + namesMemoryLocal.get(i) + "_t(" + getDefaultValue(typeObj) + ")) ");
                }

                callBehaviour.append("\\{|");

                for (int i = 0; i < namesMemoryLocal.size(); i++) {
                    if (i == namesMemoryLocal.size() - 1) {
                        callBehaviour.append("get_" + namesMemoryLocal.get(i) + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                        callBehaviour.append("set_" + namesMemoryLocal.get(i) + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()));
                    } else {
                        callBehaviour.append("get_" + namesMemoryLocal.get(i) + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                        callBehaviour.append("set_" + namesMemoryLocal.get(i) + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                    }
                }

                callBehaviour.append("|}\n");

            } else {
                callBehaviour.append(nameCallBehaviour + " /\\ " + endDiagram + "\n");
            }

            alphabet.add("endDiagram_" + nameResolver(ad.getName()));
            alphabetNode.put(nameResolver(activityNode.getName()), alphabet);

            if (outFlows.length > 0) {
                activityNode = outFlows[0].getTarget();    //set next action or control node

                for (int i = 1; i < outFlows.length; i++) {    //puts the remaining nodes in the queue
                    if (!queueNode.contains(outFlows[i].getTarget())) {
                        queueNode.add(outFlows[i].getTarget());
                    }
                }

                for (int i = 0; i < outPins.length; i++) {    //creates the parallel output channels
                    IFlow outFlowPin[] = outPins[i].getOutgoings();
                    for (int x = 0; x < outFlowPin.length; x++) {
                        if (outFlowPin[x].getTarget() instanceof IInputPin) {
                            for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                                if (activityNodeSearch instanceof IAction) {
                                    IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
                                    for (int y = 0; y < inFlowPin.length; y++) {
                                        if (inFlowPin[y].getId().equals(outFlowPin[x].getTarget().getId())) {
                                            if (!queueNode.contains(activityNodeSearch)) {
                                                queueNode.add(activityNodeSearch);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            if (!queueNode.contains(outFlowPin[x].getTarget())) {
                                queueNode.add(outFlowPin[x].getTarget());
                            }
                        }
                    }
                }

            } else if (outPins.length > 0) {

                IFlow outFlowOut[] = outPins[0].getOutgoings();
                if (outFlowOut[0].getTarget() instanceof IInputPin) {
                    for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                        if (activityNodeSearch instanceof IAction) {
                            IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
                            for (int y = 0; y < inFlowPin.length; y++) {
                                if (inFlowPin[y].getId().equals(outFlowOut[0].getTarget().getId())) {
                                    activityNode = activityNodeSearch;
                                }
                            }
                        }
                    }
                } else {
                    activityNode = outFlowOut[0].getTarget();
                }

                for (int i = 0; i < outPins.length; i++) {    //creates the parallel output channels
                    IFlow outFlowPin[] = outPins[i].getOutgoings();
                    for (int x = 0; x < outFlowPin.length; x++) {
                        if (outFlowPin[x].getTarget() instanceof IInputPin) {
                            for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                                if (activityNodeSearch instanceof IAction) {
                                    IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
                                    for (int y = 0; y < inFlowPin.length; y++) {
                                        if (inFlowPin[y].getId().equals(outFlowPin[x].getTarget().getId())) {
                                            if (!queueNode.contains(activityNodeSearch) && (i != 0 || x != 0)) {
                                                queueNode.add(activityNodeSearch);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            if (!queueNode.contains(outFlowPin[x].getTarget()) && (i != 0 || x != 0)) {
                                queueNode.add(outFlowPin[x].getTarget());
                            }
                        }
                    }
                }
            } else {
                activityNode = null;
            }

            nodes.append(callBehaviour.toString());
        }

        return activityNode;
    }

    private IActivityNode defineFork(IActivityNode activityNode, StringBuilder nodes, int code) {
        StringBuilder forkNode = new StringBuilder();
        ArrayList<String> alphabet = new ArrayList<>();
        String nameFork = nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName());
        String nameForkTermination = nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + "_t";
        String endDiagram = "END_DIAGRAM_" + nameResolver(ad.getName());
        IFlow outFlows[] = activityNode.getOutgoings();
        IFlow inFlows[] = activityNode.getIncomings();
        boolean syncBool = false;
        boolean sync2Bool = false;
        String nameObject = null;

        if (code == 0) {
            forkNode.append(nameFork + " = ");

            for (int i = 0; i < inFlows.length; i++) {
                if (syncChannelsEdge.containsKey(inFlows[i].getId())) {
                    String ceIn = syncChannelsEdge.get(inFlows[i].getId());
                    ce(alphabet, forkNode, ceIn, " -> ");
                    syncBool = true;
                }
            }

            for (int i = 0; i < inFlows.length; i++) {
                if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
                    String oeIn = syncObjectsEdge.get(inFlows[i].getId());
                    nameObject = objectEdges.get(oeIn);
                    oe(alphabet, forkNode, oeIn, "?" + nameObject, " -> ");
                    sync2Bool = true;
                }
            }

            update(alphabet, forkNode, inFlows.length, outFlows.length);

            forkNode.append("(");

            if (syncBool) {
                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String ce = createCE();
                    syncChannelsEdge.put(outFlows[i].getId(), ce);

                    forkNode.append("(");

                    if (i >= 0 && i < outFlows.length - 1) {
                        ce(alphabet, forkNode, ce, " -> SKIP) ||| ");
                    } else {
                        ce(alphabet, forkNode, ce, " -> SKIP)");
                    }
                }
            } else if (sync2Bool) {
                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String oe = createOE(nameObject);
                    syncObjectsEdge.put(outFlows[i].getId(), oe);
                    objectEdges.put(oe, nameObject);
                    forkNode.append("(");

                    if (i >= 0 && i < outFlows.length - 1) {
                        ce(alphabet, forkNode, oe, "!" + nameObject + " -> SKIP) ||| ");
                    } else {
                        ce(alphabet, forkNode, oe, "!" + nameObject + " -> SKIP)");
                    }
                }
            }

            forkNode.append("); ");

            forkNode.append(nameFork + "\n");

            forkNode.append(nameForkTermination + " = ");
            forkNode.append(nameFork + " /\\ " + endDiagram + "\n");

            alphabet.add("endDiagram_" + nameResolver(ad.getName()));
            alphabetNode.put(nameResolver(activityNode.getName()), alphabet);

            if (outFlows[0].getTarget() instanceof IInputPin) {
                for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                    if (activityNodeSearch instanceof IAction) {
                        IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
                        for (int y = 0; y < inFlowPin.length; y++) {
                            if (inFlowPin[y].getId().equals(outFlows[0].getTarget().getId())) {
                                activityNode = activityNodeSearch;
                            }
                        }
                    }
                }
            } else {
                activityNode = outFlows[0].getTarget();
            }

            for (int x = 1; x < outFlows.length; x++) {
                if (outFlows[x].getTarget() instanceof IInputPin) {
                    for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                        if (activityNodeSearch instanceof IAction) {
                            IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
                            for (int y = 0; y < inFlowPin.length; y++) {
                                if (inFlowPin[y].getId().equals(outFlows[x].getTarget().getId())) {
                                    if (!queueNode.contains(activityNodeSearch)) {
                                        queueNode.add(activityNodeSearch);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (!queueNode.contains(outFlows[x].getTarget())) {
                        queueNode.add(outFlows[x].getTarget());
                    }
                }
            }

            nodes.append(forkNode.toString());
        } else if (code == 1) {
            if (syncBool) {
                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String ce = createCE();
                    syncChannelsEdge.put(outFlows[i].getId(), ce);

                    forkNode.append("(");

                    if (i >= 0 && i < outFlows.length - 1) {
                        ce(alphabet, forkNode, ce, " -> SKIP) ||| ");
                    } else {
                        ce(alphabet, forkNode, ce, " -> SKIP)");
                    }
                }
            } else if (sync2Bool) {
                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String oe = createOE(nameObject);
                    syncObjectsEdge.put(outFlows[i].getId(), oe);
                    objectEdges.put(oe, nameObject);
                    forkNode.append("(");

                    if (i >= 0 && i < outFlows.length - 1) {
                        ce(alphabet, forkNode, oe, "!" + nameObject + " -> SKIP) ||| ");
                    } else {
                        ce(alphabet, forkNode, oe, "!" + nameObject + " -> SKIP)");
                    }
                }
            }

            if (outFlows[0].getTarget() instanceof IInputPin) {
                for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                    if (activityNodeSearch instanceof IAction) {
                        IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
                        for (int y = 0; y < inFlowPin.length; y++) {
                            if (inFlowPin[y].getId().equals(outFlows[0].getTarget().getId())) {
                                activityNode = activityNodeSearch;
                            }
                        }
                    }
                }
            } else {
                activityNode = outFlows[0].getTarget();
            }

            for (int x = 1; x < outFlows.length; x++) {
                if (outFlows[x].getTarget() instanceof IInputPin) {
                    for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                        if (activityNodeSearch instanceof IAction) {
                            IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
                            for (int y = 0; y < inFlowPin.length; y++) {
                                if (inFlowPin[y].getId().equals(outFlows[x].getTarget().getId())) {
                                    if (!queueNode.contains(activityNodeSearch)) {
                                        queueNode.add(activityNodeSearch);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (!queueNode.contains(outFlows[x].getTarget())) {
                        queueNode.add(outFlows[x].getTarget());
                    }
                }
            }

            nodes.append(forkNode.toString());
        } else if (code == 1) {
            for (int i = 0; i < inFlows.length; i++) {
                if (syncChannelsEdge.containsKey(inFlows[i].getId())) {
                    syncBool = true;
                }
            }

            for (int i = 0; i < inFlows.length; i++) {
                if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
                    String oeIn = syncObjectsEdge.get(inFlows[i].getId());
                    nameObject = objectEdges.get(oeIn);
                    sync2Bool = true;
                }
            }

            if (syncBool) {
                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String ce = createCE();
                    syncChannelsEdge.put(outFlows[i].getId(), ce);

                    forkNode.append("(");

                    if (i >= 0 && i < outFlows.length - 1) {
                        ce(alphabet, forkNode, ce, " -> SKIP) ||| ");
                    } else {
                        ce(alphabet, forkNode, ce, " -> SKIP)");
                    }
                }
            } else if (sync2Bool) {
                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String oe = createOE(nameObject);
                    syncObjectsEdge.put(outFlows[i].getId(), oe);
                    objectEdges.put(oe, nameObject);
                    forkNode.append("(");

                    if (i >= 0 && i < outFlows.length - 1) {
                        ce(alphabet, forkNode, oe, "!" + nameObject + " -> SKIP) ||| ");
                    } else {
                        ce(alphabet, forkNode, oe, "!" + nameObject + " -> SKIP)");
                    }
                }
            }

        } else if (code == 2) {
            forkNode.append(nameFork + " = ");

            for (int i = 0; i < inFlows.length; i++) {
                if (syncChannelsEdge.containsKey(inFlows[i].getId())) {
                    String ceIn = syncChannelsEdge.get(inFlows[i].getId());
                    ce(alphabet, forkNode, ceIn, " -> ");
                    syncBool = true;
                }
            }

            for (int i = 0; i < inFlows.length; i++) {
                if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
                    String oeIn = syncObjectsEdge.get(inFlows[i].getId());
                    nameObject = objectEdges.get(oeIn);
                    oe(alphabet, forkNode, oeIn, "?" + nameObject, " -> ");
                    sync2Bool = true;
                }
            }

            update(alphabet, forkNode, inFlows.length, outFlows.length);

            forkNode.append("(");

            if (syncBool) {
                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String ce = syncChannelsEdge.get(outFlows[i].getId());

                    forkNode.append("(");

                    if (i >= 0 && i < outFlows.length - 1) {
                        ce(alphabet, forkNode, ce, " -> SKIP) ||| ");
                    } else {
                        ce(alphabet, forkNode, ce, " -> SKIP)");
                    }
                }
            } else if (sync2Bool) {
                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String oe = syncObjectsEdge.get(outFlows[i].getId());

                    forkNode.append("(");

                    if (i >= 0 && i < outFlows.length - 1) {
                        ce(alphabet, forkNode, oe, "!" + nameObject + " -> SKIP) ||| ");
                    } else {
                        ce(alphabet, forkNode, oe, "!" + nameObject + " -> SKIP)");
                    }
                }
            }

            forkNode.append("); ");

            forkNode.append(nameFork + "\n");

            forkNode.append(nameForkTermination + " = ");
            forkNode.append(nameFork + " /\\ " + endDiagram + "\n");

            alphabet.add("endDiagram_" + nameResolver(ad.getName()));
            alphabetNode.put(nameResolver(activityNode.getName()), alphabet);

            if (outFlows[0].getTarget() instanceof IInputPin) {
                for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                    if (activityNodeSearch instanceof IAction) {
                        IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
                        for (int y = 0; y < inFlowPin.length; y++) {
                            if (inFlowPin[y].getId().equals(outFlows[0].getTarget().getId())) {
                                activityNode = activityNodeSearch;
                            }
                        }
                    }
                }
            } else {
                activityNode = outFlows[0].getTarget();
            }

            for (int x = 1; x < outFlows.length; x++) {
                if (outFlows[x].getTarget() instanceof IInputPin) {
                    for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                        if (activityNodeSearch instanceof IAction) {
                            IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
                            for (int y = 0; y < inFlowPin.length; y++) {
                                if (inFlowPin[y].getId().equals(outFlows[x].getTarget().getId())) {
                                    if (!queueNode.contains(activityNodeSearch)) {
                                        queueNode.add(activityNodeSearch);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (!queueNode.contains(outFlows[x].getTarget())) {
                        queueNode.add(outFlows[x].getTarget());
                    }
                }
            }

            nodes.append(forkNode.toString());
        }

        return activityNode;
    }

    private IActivityNode defineJoin(IActivityNode activityNode, StringBuilder nodes, int code) {
        StringBuilder joinNode = new StringBuilder();
        ArrayList<String> alphabet = new ArrayList<>();
        String nameJoin = nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName());
        String nameJoinTermination = nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + "_t";
        String endDiagram = "END_DIAGRAM_" + nameResolver(ad.getName());
        IFlow outFlows[] = activityNode.getOutgoings();
        IFlow inFlows[] = activityNode.getIncomings();
        HashMap<String, String> nameObjects = new HashMap<>();
        List<String> objects = new ArrayList<>();
        String nameObject = null;
        List<String> nameObjectAdded = new ArrayList<>();
        boolean syncBool = false;
        boolean sync2Bool = false;

        if (code == 0) {
            ArrayList<String> ceInitials = new ArrayList<>();
            for (int i = 0; i < inFlows.length; i++) {
                ceInitials.add(inFlows[i].getId());
                if (syncChannelsEdge.containsKey(inFlows[i].getId())) {
                    syncBool = true;
                }

                if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
                    String ceIn2 = syncObjectsEdge.get(inFlows[i].getId());
                    nameObject = objectEdges.get(ceIn2);
                    nameObjects.put(inFlows[i].getId(), nameObject);
                    sync2Bool = true;
                }
            }

            joinNode.append(nameJoin + " = (");

            for (int i = 0; i < ceInitials.size(); i++) {
                String ceIn = syncChannelsEdge.get(ceInitials.get(i));    //get the parallel input channels
                String oeIn = syncObjectsEdge.get(ceInitials.get(i));

                if (ceIn != null) {
                    joinNode.append("(");

                    if (i >= 0 && i < ceInitials.size() - 1) {
                        ce(alphabet, joinNode, ceIn, " -> SKIP) ||| ");
                    } else {
                        ce(alphabet, joinNode, ceIn, " -> SKIP)");
                    }
                } else {

                    nameObject = nameObjects.get(ceInitials.get(i));

                    if (!objects.contains(nameObject)) {
                        objects.add(nameObject);
                    }

                    joinNode.append("(");

                    if (i >= 0 && i < ceInitials.size() - 1) {
                        ce(alphabet, joinNode, oeIn, "?" + nameObject + " -> ");
                        setLocalInput(alphabet, joinNode, nameObject, nameResolver(activityNode.getName()), nameObject, oeIn);
                        joinNode.append("SKIP) ||| ");
                    } else {
                        ce(alphabet, joinNode, oeIn, "?" + nameObject + " -> ");
                        setLocalInput(alphabet, joinNode, nameObject, nameResolver(activityNode.getName()), nameObject, oeIn);
                        joinNode.append("SKIP)");
                    }
                }

            }

            joinNode.append("); ");

            update(alphabet, joinNode, inFlows.length, outFlows.length);

            if (sync2Bool) {
                for (String nameObjectOut : objects) {
                    getLocal(alphabet, joinNode, nameObjectOut, nameResolver(activityNode.getName()), nameObjectOut);
                }
            }

            joinNode.append("(");

            nameObject = "";

            for (int i = 0; i < inFlows.length; i++) {
                String channel = syncObjectsEdge.get(inFlows[i].getId());
                if (objectEdges.get(channel) != null && !nameObjectAdded.contains(objectEdges.get(channel))) {
                    nameObjectAdded.add(objectEdges.get(channel));
                    nameObject += objectEdges.get(channel);
                }
            }

            if (sync2Bool) {
                for (int i = 0; i < objects.size(); i++) {    //creates the parallel output channels
                    String oe = createOE(nameObject);
                    syncObjectsEdge.put(outFlows[0].getId(), oe);    //just one output
                    objectEdges.put(oe, nameObject);
                    joinNode.append("(");

                    if (i >= 0 && i < objects.size() - 1) {
                        ce(alphabet, joinNode, oe, "!" + objects.get(i) + " -> SKIP) |~| ");
                        countOe_ad--;
                    } else {
                        ce(alphabet, joinNode, oe, "!" + objects.get(i) + " -> SKIP)");
                    }
                }
            } else if (syncBool) {
                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String ce = createCE();
                    syncChannelsEdge.put(outFlows[i].getId(), ce);

                    joinNode.append("(");

                    if (i >= 0 && i < outFlows.length - 1) {
                        ce(alphabet, joinNode, ce, " -> SKIP) ||| ");
                    } else {
                        ce(alphabet, joinNode, ce, " -> SKIP)");
                    }
                }
            }

            joinNode.append("); ");

            joinNode.append(nameJoin + "\n");

            joinNode.append(nameJoinTermination + " = ");

            for (int i = 0; i < objects.size(); i++) {
                joinNode.append("(");
            }

            joinNode.append("(" + nameJoin + " /\\ " + endDiagram + ")");

            for (int i = 0; i < objects.size(); i++) {    //creates the parallel output channels
                joinNode.append(" [|{|");
                joinNode.append("get_" + objects.get(i) + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                joinNode.append("set_" + objects.get(i) + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                joinNode.append("endDiagram_" + nameResolver(ad.getName()) + "|}|] ");
                joinNode.append("Mem_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + "_" + objects.get(i) + "_t(" + getDefaultValue(parameterNodesInput.get(objects.get(i))) + "))");
            }

            if (objects.size() > 0) {
                joinNode.append(" \\{|");

                for (int i = 0; i < objects.size(); i++) {
                    joinNode.append("get_" + objects.get(i) + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                    joinNode.append("set_" + objects.get(i) + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()));
                    if (i < objects.size() - 1) {
                        joinNode.append(",");
                    }
                }

                joinNode.append("|}");

            }

            joinNode.append("\n");

            alphabet.add("endDiagram_" + nameResolver(ad.getName()));
            alphabetNode.put(nameResolver(activityNode.getName()), alphabet);

            if (outFlows[0].getTarget() instanceof IInputPin) {
                for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                    if (activityNodeSearch instanceof IAction) {
                        IInputPin inPins[] = ((IAction) activityNodeSearch).getInputs();
                        for (int y = 0; y < inPins.length; y++) {
                            if (inPins[y].getId().equals(outFlows[0].getTarget().getId())) {
                                activityNode = activityNodeSearch;
                            }
                        }
                    }
                }
            } else {
                activityNode = outFlows[0].getTarget();
            }

            nodes.append(joinNode.toString());
        } else if (code == 1) {
            ArrayList<String> ceInitials = new ArrayList<>();
            ArrayList<String> obj = new ArrayList<>();
            for (int i = 0; i < inFlows.length; i++) {
                ceInitials.add(inFlows[i].getId());
                if (syncChannelsEdge.containsKey(inFlows[i].getId())) {
                    syncBool = true;
                }

                if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
                    String ceIn2 = syncObjectsEdge.get(inFlows[i].getId());
                    nameObject = objectEdges.get(ceIn2);
                    nameObjects.put(inFlows[i].getId(), nameObject);

                    if (!obj.contains(nameObject)) {
                        obj.add(nameObject);
                    }

                    sync2Bool = true;
                }
            }

            nameObject = "";
            List<String> nodesAdded = new ArrayList<>();

            List<String> nameObjs = new ArrayList<>();
            for (int i = 0; i < inFlows.length; i++) {
                nameObjs.addAll(getObjects(inFlows[i], nodesAdded));
            }

            ArrayList<String> union = new ArrayList<>();
            String lastName = "";

            for (String nameObj : nameObjs) {
                if (!nameObjectAdded.contains(nameObj)) {
                    nameObjectAdded.add(nameObj);
                    nameObject += nameObj;
                    union.add(nameObj);
                    lastName = nameObj;
                }
            }

            if (union.size() > 1) {
                unionList.add(union);
                typeUnionList.put(nameObject, parameterNodesInput.get(lastName));
            }

            System.out.println("sync2Bool " + sync2Bool);
            System.out.println("syncBool " + syncBool);


            if (sync2Bool) {
                for (int i = 0; i < obj.size(); i++) {    //creates the parallel output channels
                    String oe = createOE(nameObject);
                    syncObjectsEdge.put(outFlows[0].getId(), oe);    //just one output
                    System.out.println("id " + outFlows[0].getId());
                    objectEdges.put(oe, nameObject);
                    joinNode.append("(");

                    if (i >= 0 && i < obj.size() - 1) {
                        ce(alphabet, joinNode, oe, "!" + obj.get(i) + " -> SKIP) |~| ");
                        countOe_ad--;
                    } else {
                        ce(alphabet, joinNode, oe, "!" + obj.get(i) + " -> SKIP)");
                    }
                }
            } else if (syncBool) {
                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String ce = createCE();
                    syncChannelsEdge.put(outFlows[i].getId(), ce);

                    joinNode.append("(");

                    if (i >= 0 && i < outFlows.length - 1) {
                        ce(alphabet, joinNode, ce, " -> SKIP) ||| ");
                    } else {
                        ce(alphabet, joinNode, ce, " -> SKIP)");
                    }
                }
            }

            if (outFlows[0].getTarget() instanceof IInputPin) {
                for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                    if (activityNodeSearch instanceof IAction) {
                        IInputPin inPins[] = ((IAction) activityNodeSearch).getInputs();
                        for (int y = 0; y < inPins.length; y++) {
                            if (inPins[y].getId().equals(outFlows[0].getTarget().getId())) {
                                activityNode = activityNodeSearch;
                            }
                        }
                    }
                }
            } else {
                activityNode = outFlows[0].getTarget();
            }

        } else if (code == 2) {
            ArrayList<String> ceInitials = new ArrayList<>();
            for (int i = 0; i < inFlows.length; i++) {
                ceInitials.add(inFlows[i].getId());
                if (syncChannelsEdge.containsKey(inFlows[i].getId())) {
                    syncBool = true;
                }

                if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
                    String ceIn2 = syncObjectsEdge.get(inFlows[i].getId());
                    nameObject = objectEdges.get(ceIn2);
                    nameObjects.put(inFlows[i].getId(), nameObject);
                    sync2Bool = true;
                }
            }

            joinNode.append(nameJoin + " = (");

            for (int i = 0; i < ceInitials.size(); i++) {
                String ceIn = syncChannelsEdge.get(ceInitials.get(i));    //get the parallel input channels
                String oeIn = syncObjectsEdge.get(ceInitials.get(i));

                if (ceIn != null) {
                    joinNode.append("(");

                    if (i >= 0 && i < ceInitials.size() - 1) {
                        ce(alphabet, joinNode, ceIn, " -> SKIP) ||| ");
                    } else {
                        ce(alphabet, joinNode, ceIn, " -> SKIP)");
                    }
                } else {

                    nameObject = nameObjects.get(ceInitials.get(i));

                    if (!objects.contains(nameObject)) {
                        objects.add(nameObject);
                    }

                    joinNode.append("(");

                    if (i >= 0 && i < ceInitials.size() - 1) {
                        ce(alphabet, joinNode, oeIn, "?" + nameObject + " -> ");
                        setLocalInput(alphabet, joinNode, nameObject, nameResolver(activityNode.getName()), nameObject, oeIn);
                        joinNode.append("SKIP) ||| ");
                    } else {
                        ce(alphabet, joinNode, oeIn, "?" + nameObject + " -> ");
                        setLocalInput(alphabet, joinNode, nameObject, nameResolver(activityNode.getName()), nameObject, oeIn);
                        joinNode.append("SKIP)");
                    }
                }

            }

            joinNode.append("); ");

            update(alphabet, joinNode, inFlows.length, outFlows.length);

            if (sync2Bool) {
                for (String nameObjectOut : objects) {
                    getLocal(alphabet, joinNode, nameObjectOut, nameResolver(activityNode.getName()), nameObjectOut);
                }
            }

            joinNode.append("(");

            if (sync2Bool) {
                for (int i = 0; i < objects.size(); i++) {    //creates the parallel output channels
                    String oe = syncObjectsEdge.get(outFlows[0].getId());    //just one output


                    joinNode.append("(");

                    if (i >= 0 && i < objects.size() - 1) {
                        ce(alphabet, joinNode, oe, "!" + objects.get(i) + " -> SKIP) |~| ");
                        //countOe_ad--;
                    } else {
                        ce(alphabet, joinNode, oe, "!" + objects.get(i) + " -> SKIP)");
                    }
                }
            } else if (syncBool) {
                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String ce = syncChannelsEdge.get(outFlows[i].getId());

                    joinNode.append("(");

                    if (i >= 0 && i < outFlows.length - 1) {
                        ce(alphabet, joinNode, ce, " -> SKIP) ||| ");
                    } else {
                        ce(alphabet, joinNode, ce, " -> SKIP)");
                    }
                }
            }

            joinNode.append("); ");

            joinNode.append(nameJoin + "\n");

            joinNode.append(nameJoinTermination + " = ");

            for (int i = 0; i < objects.size(); i++) {
                joinNode.append("(");
            }

            joinNode.append("(" + nameJoin + " /\\ " + endDiagram + ")");

            for (int i = 0; i < objects.size(); i++) {    //creates the parallel output channels
                joinNode.append(" [|{|");
                joinNode.append("get_" + objects.get(i) + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                joinNode.append("set_" + objects.get(i) + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                joinNode.append("endDiagram_" + nameResolver(ad.getName()) + "|}|] ");
                joinNode.append("Mem_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + "_" + objects.get(i) + "_t(" + getDefaultValue(parameterNodesInput.get(objects.get(i))) + "))");
            }

            if (objects.size() > 0) {
                joinNode.append(" \\{|");

                for (int i = 0; i < objects.size(); i++) {
                    joinNode.append("get_" + objects.get(i) + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                    joinNode.append("set_" + objects.get(i) + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()));
                    if (i < objects.size() - 1) {
                        joinNode.append(",");
                    }
                }

                joinNode.append("|}");

            }

            joinNode.append("\n");

            alphabet.add("endDiagram_" + nameResolver(ad.getName()));
            alphabetNode.put(nameResolver(activityNode.getName()), alphabet);

            if (outFlows[0].getTarget() instanceof IInputPin) {
                for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                    if (activityNodeSearch instanceof IAction) {
                        IInputPin inPins[] = ((IAction) activityNodeSearch).getInputs();
                        for (int y = 0; y < inPins.length; y++) {
                            if (inPins[y].getId().equals(outFlows[0].getTarget().getId())) {
                                activityNode = activityNodeSearch;
                            }
                        }
                    }
                }
            } else {
                activityNode = outFlows[0].getTarget();
            }

            nodes.append(joinNode.toString());
        }

        return activityNode;
    }

    private IActivityNode defineMerge(IActivityNode activityNode, StringBuilder nodes, int code) {
        StringBuilder merge = new StringBuilder();
        ArrayList<String> alphabet = new ArrayList<>();
        String nameMerge = nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName());
        String nameMergeTermination = nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + "_t";
        String endDiagram = "END_DIAGRAM_" + nameResolver(ad.getName());
        IFlow outFlows[] = activityNode.getOutgoings();
        IFlow inFlows[] = activityNode.getIncomings();
        String nameObject = null;
        String nameObjectUnique = "";
        List<String> nameObjectAdded = new ArrayList<>();
        HashMap<String, String> nameObjects = new HashMap<>();
        List<String> namesMemoryLocal = new ArrayList<>();
        String typeMemoryLocal = null;

        if (code == 0) {
            ArrayList<String> ceInitials = new ArrayList<>();
            for (int i = 0; i < inFlows.length; i++) {
                ceInitials.add(inFlows[i].getId());

                if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
                    String ceIn2 = syncObjectsEdge.get(inFlows[i].getId());
                    nameObjects.put(inFlows[i].getId(), objectEdges.get(ceIn2));
                }
            }

            merge.append(nameMerge + " = ");

            merge.append("(");


            for (int i = 0; i < ceInitials.size(); i++) {        //get unique channel
                if (nameObjects.get(ceInitials.get(i)) != null) {
                    if (!nameObjectAdded.contains(nameObjects.get(ceInitials.get(i)))) {
                        nameObjectAdded.add(nameObjects.get(ceInitials.get(i)));
                        nameObjectUnique += nameObjects.get(ceInitials.get(i));
                        typeMemoryLocal = nameObjects.get(ceInitials.get(i));
                    }
                }
            }

            if (!nameObjectUnique.equals("")) {
                namesMemoryLocal.add(nameObjectUnique);
            }

            for (int i = 0; i < ceInitials.size(); i++) {
                String ceIn = syncChannelsEdge.get(ceInitials.get(i));    //get the parallel input channels
                String oeIn = syncObjectsEdge.get(ceInitials.get(i));

                if (ceIn != null) {
                    merge.append("(");

                    if (i >= 0 && i < ceInitials.size() - 1) {
                        ce(alphabet, merge, ceIn, " -> SKIP) [] ");
                    } else {
                        ce(alphabet, merge, ceIn, " -> SKIP)");
                    }
                } else {

                    nameObject = nameObjects.get(ceInitials.get(i));
                    merge.append("(");

                    if (i >= 0 && i < ceInitials.size() - 1) {
                        ce(alphabet, merge, oeIn, "?" + nameObject + " -> ");
                        setLocalInput(alphabet, merge, nameObjectUnique, nameResolver(activityNode.getName()), nameObject, oeIn);
                        merge.append("SKIP) [] ");
                    } else {
                        ce(alphabet, merge, oeIn, "?" + nameObject + " -> ");
                        setLocalInput(alphabet, merge, nameObjectUnique, nameResolver(activityNode.getName()), nameObject, oeIn);
                        merge.append("SKIP)");
                    }
                }
            }

            merge.append("); ");

            update(alphabet, merge, 1, 1);

            if (!nameObjectUnique.equals("")) {
                getLocal(alphabet, merge, nameObjectUnique, nameResolver(activityNode.getName()), nameObjectUnique);
                String oe = createOE(nameObjectUnique); //creates output channels
                syncObjectsEdge.put(outFlows[0].getId(), oe);
                objectEdges.put(oe, nameObjectUnique);
                oe(alphabet, merge, oe, "!" + nameObjectUnique, " -> ");

                merge.append(nameMerge + "\n");
                merge.append(nameMergeTermination + " = ");

                merge.append("((" + nameMerge + " /\\ " + endDiagram + ") ");

                merge.append("[|{|");
                merge.append("get_" + nameObjectUnique + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                merge.append("set_" + nameObjectUnique + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                merge.append("endDiagram_" + nameResolver(ad.getName()));
                merge.append("|}|] ");
                merge.append("Mem_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + "_" + nameObjectUnique + "_t(" + getDefaultValue(parameterNodesInput.get(typeMemoryLocal)) + ")) ");

                merge.append("\\{|");
                merge.append("get_" + nameObjectUnique + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                merge.append("set_" + nameObjectUnique + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()));
                merge.append("|}\n");

            } else {
                String ce = createCE(); //creates output channels
                syncChannelsEdge.put(outFlows[0].getId(), ce);
                ce(alphabet, merge, ce, " -> ");

                merge.append(nameMerge + "\n");
                merge.append(nameMergeTermination + " = ");
                merge.append(nameMerge + " /\\ " + endDiagram + "\n");
            }

            alphabet.add("endDiagram_" + nameResolver(ad.getName()));
            alphabetNode.put(nameResolver(activityNode.getName()), alphabet);

            if (outFlows[0].getTarget() instanceof IInputPin) {
                for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                    if (activityNodeSearch instanceof IAction) {
                        IInputPin inPins[] = ((IAction) activityNodeSearch).getInputs();
                        for (int y = 0; y < inPins.length; y++) {
                            if (inPins[y].getId().equals(outFlows[0].getTarget().getId())) {
                                activityNode = activityNodeSearch;
                            }
                        }
                    }
                }
            } else {
                activityNode = outFlows[0].getTarget();
            }

            nodes.append(merge.toString());
        } else if (code == 1) {
//            ArrayList<String> ceInitials = new ArrayList<>();
////            for (int i = 0; i <  inFlows.length; i++) {
////                ceInitials.add(inFlows[i].getId());
////
////                if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
////                    String ceIn2 = syncObjectsEdge.get(inFlows[i].getId());
////                    nameObjects.put(inFlows[i].getId(), objectEdges.get(ceIn2));
////                }
////            }
////
////            for (int i = 0; i < ceInitials.size(); i++) {		//get unique channel
////                if (nameObjects.get(ceInitials.get(i)) != null) {
////                    if (!nameObjectAdded.contains(nameObjects.get(ceInitials.get(i)))) {
////                        nameObjectAdded.add(nameObjects.get(ceInitials.get(i)));
////                        nameObjectUnique += nameObjects.get(ceInitials.get(i));
////                    }
////                }
////            }

            ArrayList<String> union = new ArrayList<>();
            List<String> nameObjs = new ArrayList<>();
            List<String> nodesAdded = new ArrayList<>();

            for (int i = 0; i < inFlows.length; i++) {
                nameObjs.addAll(getObjects(inFlows[i], nodesAdded));
            }

            for (String nameObj : nameObjs) {
                if (!union.contains(nameObj)) {
                    nameObjectUnique += nameObj;
                    union.add(nameObj);
                    typeMemoryLocal = nameObj;
                }
            }

            if (union.size() > 1) {
                unionList.add(union);
                typeUnionList.put(nameObjectUnique, parameterNodesInput.get(typeMemoryLocal));
            }

            if (!nameObjectUnique.equals("")) {
                namesMemoryLocal.add(nameObjectUnique);
            }

            if (!nameObjectUnique.equals("")) {
                //getLocal(alphabet, merge, nameObjectUnique, nameResolver(activityNode.getName()), nameObjectUnique);
                String oe = createOE(nameObjectUnique); //creates output channels
                syncObjectsEdge.put(outFlows[0].getId(), oe);
                objectEdges.put(oe, nameObjectUnique);
                oe(alphabet, merge, oe, "!" + nameObjectUnique, " -> ");

                merge.append(nameMerge + "\n");
                merge.append(nameMergeTermination + " = ");

                merge.append("((" + nameMerge + " /\\ " + endDiagram + ") ");

                merge.append("[|{|");
                merge.append("get_" + nameObjectUnique + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                merge.append("set_" + nameObjectUnique + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                merge.append("endDiagram_" + nameResolver(ad.getName()));
                merge.append("|}|] ");
                merge.append("Mem_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + "_" + nameObjectUnique + "_t(" + getDefaultValue(parameterNodesInput.get(typeMemoryLocal)) + ")) ");

                merge.append("\\{|");
                merge.append("get_" + nameObjectUnique + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                merge.append("set_" + nameObjectUnique + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()));
                merge.append("|}\n");

            } else {
                String ce = createCE(); //creates output channels
                syncChannelsEdge.put(outFlows[0].getId(), ce);
                ce(alphabet, merge, ce, " -> ");

                merge.append(nameMerge + "\n");
                merge.append(nameMergeTermination + " = ");
                merge.append(nameMerge + " /\\ " + endDiagram + "\n");
            }

            if (outFlows[0].getTarget() instanceof IInputPin) {
                for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                    if (activityNodeSearch instanceof IAction) {
                        IInputPin inPins[] = ((IAction) activityNodeSearch).getInputs();
                        for (int y = 0; y < inPins.length; y++) {
                            if (inPins[y].getId().equals(outFlows[0].getTarget().getId())) {
                                activityNode = activityNodeSearch;
                            }
                        }
                    }
                }
            } else {
                activityNode = outFlows[0].getTarget();
            }

        } else if (code == 2) {
            ArrayList<String> ceInitials = new ArrayList<>();
            for (int i = 0; i < inFlows.length; i++) {
                ceInitials.add(inFlows[i].getId());

                if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
                    String ceIn2 = syncObjectsEdge.get(inFlows[i].getId());
                    nameObjects.put(inFlows[i].getId(), objectEdges.get(ceIn2));
                }
            }

            merge.append(nameMerge + " = ");

            merge.append("(");


            for (int i = 0; i < ceInitials.size(); i++) {        //get unique channel
                if (nameObjects.get(ceInitials.get(i)) != null) {
                    if (!nameObjectAdded.contains(nameObjects.get(ceInitials.get(i)))) {
                        nameObjectAdded.add(nameObjects.get(ceInitials.get(i)));
                        nameObjectUnique += nameObjects.get(ceInitials.get(i));
                        typeMemoryLocal = nameObjects.get(ceInitials.get(i));
                    }
                }
            }

            if (!nameObjectUnique.equals("")) {
                namesMemoryLocal.add(nameObjectUnique);
            }

            for (int i = 0; i < ceInitials.size(); i++) {
                String ceIn = syncChannelsEdge.get(ceInitials.get(i));    //get the parallel input channels
                String oeIn = syncObjectsEdge.get(ceInitials.get(i));

                if (ceIn != null) {
                    merge.append("(");

                    if (i >= 0 && i < ceInitials.size() - 1) {
                        ce(alphabet, merge, ceIn, " -> SKIP) [] ");
                    } else {
                        ce(alphabet, merge, ceIn, " -> SKIP)");
                    }
                } else {

                    nameObject = nameObjects.get(ceInitials.get(i));
                    merge.append("(");

                    if (i >= 0 && i < ceInitials.size() - 1) {
                        ce(alphabet, merge, oeIn, "?" + nameObject + " -> ");
                        setLocalInput(alphabet, merge, nameObjectUnique, nameResolver(activityNode.getName()), nameObject, oeIn);
                        merge.append("SKIP) [] ");
                    } else {
                        ce(alphabet, merge, oeIn, "?" + nameObject + " -> ");
                        setLocalInput(alphabet, merge, nameObjectUnique, nameResolver(activityNode.getName()), nameObject, oeIn);
                        merge.append("SKIP)");
                    }
                }
            }

            merge.append("); ");

            update(alphabet, merge, 1, 1);

            if (!nameObjectUnique.equals("")) {
                getLocal(alphabet, merge, nameObjectUnique, nameResolver(activityNode.getName()), nameObjectUnique);
                String oe = syncObjectsEdge.get(outFlows[0].getId());

                oe(alphabet, merge, oe, "!" + nameObjectUnique, " -> ");

                merge.append(nameMerge + "\n");
                merge.append(nameMergeTermination + " = ");

                merge.append("((" + nameMerge + " /\\ " + endDiagram + ") ");

                merge.append("[|{|");
                merge.append("get_" + nameObjectUnique + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                merge.append("set_" + nameObjectUnique + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                merge.append("endDiagram_" + nameResolver(ad.getName()));
                merge.append("|}|] ");
                merge.append("Mem_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + "_" + nameObjectUnique + "_t(" + getDefaultValue(parameterNodesInput.get(typeMemoryLocal)) + ")) ");

                merge.append("\\{|");
                merge.append("get_" + nameObjectUnique + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                merge.append("set_" + nameObjectUnique + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()));
                merge.append("|}\n");

            } else {
                String ce = syncChannelsEdge.get(outFlows[0].getId());
                ce(alphabet, merge, ce, " -> ");

                merge.append(nameMerge + "\n");
                merge.append(nameMergeTermination + " = ");
                merge.append(nameMerge + " /\\ " + endDiagram + "\n");
            }

            alphabet.add("endDiagram_" + nameResolver(ad.getName()));
            alphabetNode.put(nameResolver(activityNode.getName()), alphabet);

            if (outFlows[0].getTarget() instanceof IInputPin) {
                for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                    if (activityNodeSearch instanceof IAction) {
                        IInputPin inPins[] = ((IAction) activityNodeSearch).getInputs();
                        for (int y = 0; y < inPins.length; y++) {
                            if (inPins[y].getId().equals(outFlows[0].getTarget().getId())) {
                                activityNode = activityNodeSearch;
                            }
                        }
                    }
                }
            } else {
                activityNode = outFlows[0].getTarget();
            }

            nodes.append(merge.toString());
        }

        return activityNode;
    }

    private IActivityNode defineDecision(IActivityNode activityNode, StringBuilder nodes, int code) {
        StringBuilder decision = new StringBuilder();
        ArrayList<String> alphabet = new ArrayList<>();
        String nameDecision = nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName());
        String nameDecisionTermination = nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + "_t";
        String endDiagram = "END_DIAGRAM_" + nameResolver(ad.getName());
        IFlow outFlows[] = activityNode.getOutgoings();
        IFlow inFlows[] = activityNode.getIncomings();
        String decisionInputFlow = null;

        if (code == 0) {
            for (int i = 0; i < inFlows.length; i++) {

                String stereotype[] = inFlows[i].getStereotypes();

                for (int j = 0; j < stereotype.length; j++) {
                    if (stereotype[j].equals("decisionInputFlow")) {
                        //decisionInputFlow = inFlows[i].getSource().getName();
                        decisionInputFlow = objectEdges.get(syncObjectsEdge.get(inFlows[i].getId()));
                    }
                }
            }

            if (decisionInputFlow != null && inFlows.length == 1) {    //just object
                decision.append(nameDecision + " = ");

                for (int i = 0; i < inFlows.length; i++) {
                    if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
                        String ceIn = syncObjectsEdge.get(inFlows[i].getId());
                        oe(alphabet, decision, ceIn, "?" + decisionInputFlow, " -> ");
                    }
                }

                update(alphabet, decision, 1, 1);

                decision.append("(");

                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String oe = createOE(decisionInputFlow);
                    syncObjectsEdge.put(outFlows[i].getId(), oe);
                    objectEdges.put(oe, decisionInputFlow);

                    decision.append(outFlows[i].getGuard() + " & (");

                    if (i >= 0 && i < outFlows.length - 1) {
                        oe(alphabet, decision, oe, "!" + decisionInputFlow, " -> SKIP) [] ");
                    } else {
                        oe(alphabet, decision, oe, "!" + decisionInputFlow, " -> SKIP)");
                    }
                }

                decision.append("); ");

                decision.append(nameDecision + "\n");

                decision.append(nameDecisionTermination + " = ");
                decision.append(nameDecision + " /\\ " + endDiagram + "\n");

                alphabet.add("endDiagram_" + nameResolver(ad.getName()));
                alphabetNode.put(nameResolver(activityNode.getName()), alphabet);

//			activityNode = outFlows[0].getTarget();	//set next action or control node
//
//			for (int i = 1; i < outFlows.length; i++) {	//puts the remaining nodes in the queue
//				if (!queueNode.contains(outFlows[i].getTarget())) {
//					queueNode.add(outFlows[i].getTarget());
//				}
//			}

                if (outFlows[0].getTarget() instanceof IInputPin) {
                    for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                        if (activityNodeSearch instanceof IAction) {
                            IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
                            for (int y = 0; y < inFlowPin.length; y++) {
                                if (inFlowPin[y].getId().equals(outFlows[0].getTarget().getId())) {
                                    activityNode = activityNodeSearch;
                                }
                            }
                        }
                    }
                } else {
                    activityNode = outFlows[0].getTarget();
                }

                for (int x = 1; x < outFlows.length; x++) {
                    if (outFlows[x].getTarget() instanceof IInputPin) {
                        for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                            if (activityNodeSearch instanceof IAction) {
                                IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
                                for (int y = 0; y < inFlowPin.length; y++) {
                                    if (inFlowPin[y].getId().equals(outFlows[x].getTarget().getId())) {
                                        if (!queueNode.contains(activityNodeSearch)) {
                                            queueNode.add(activityNodeSearch);
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if (!queueNode.contains(outFlows[x].getTarget())) {
                            queueNode.add(outFlows[x].getTarget());
                        }
                    }
                }

                nodes.append(decision.toString());
            } else if (decisionInputFlow != null && inFlows.length > 1) {                    //object and control
                decision.append(nameDecision + " = ");

                String sync2 = "";
//			for (Pair<String, String> tupla : syncChannelsEdge.keySet()) {	//get sync channel
//				if (tupla.getValue().equals(nameResolver(activityNode.getName()))) {
//					sync2 = tupla;
//				}
//			}
//
                String sync = "";
//			for (Pair<String, String> tupla : syncObjectsEdge.keySet()) {	//get sync channel
//				if (tupla.getValue().equals(nameResolver(activityNode.getName()))) {
//					sync = tupla;
//				}
//			}

                for (int i = 0; i < inFlows.length; i++) {
                    if (syncChannelsEdge.containsKey(inFlows[i].getId())) {
                        sync2 = inFlows[i].getId();
                    }

                    if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
                        sync = inFlows[i].getId();
                    }
                }


                String ceIn2 = syncChannelsEdge.get(sync2);
                String ceIn = syncObjectsEdge.get(sync);

                decision.append("((");
                ce(alphabet, decision, ceIn2, " -> SKIP");

                decision.append(") ||| (");
                oe(alphabet, decision, ceIn, "?" + decisionInputFlow, " -> ");

                setLocal(alphabet, decision, decisionInputFlow, nameResolver(activityNode.getName()), decisionInputFlow);
                decision.append("SKIP)); ");

                update(alphabet, decision, 2, 1);
                getLocal(alphabet, decision, decisionInputFlow, nameResolver(activityNode.getName()), decisionInputFlow);

                decision.append("(");

                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String ce = createCE();
                    syncChannelsEdge.put(outFlows[i].getId(), ce);

                    decision.append(outFlows[i].getGuard() + " & (");

                    if (i >= 0 && i < outFlows.length - 1) {
                        ce(alphabet, decision, ce, " -> SKIP) [] ");
                    } else {
                        ce(alphabet, decision, ce, " -> SKIP)");
                    }
                }

                decision.append("); ");

                decision.append(nameDecision + "\n");

                decision.append(nameDecisionTermination + " = ");
                decision.append("((" + nameDecision + " /\\ " + endDiagram + ") ");

                decision.append("[|{|");
                decision.append("get_" + decisionInputFlow + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                decision.append("set_" + decisionInputFlow + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                decision.append("endDiagram_" + nameResolver(ad.getName()));
                decision.append("|}|] ");
                decision.append("Mem_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + "_" + decisionInputFlow + "_t(" + getDefaultValue(parameterNodesInput.get(decisionInputFlow)) + ")) ");

                decision.append("\\{|");
                decision.append("get_" + decisionInputFlow + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                decision.append("set_" + decisionInputFlow + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()));
                decision.append("|}\n");

                alphabet.add("endDiagram_" + nameResolver(ad.getName()));
                alphabetNode.put(nameResolver(activityNode.getName()), alphabet);

                activityNode = outFlows[0].getTarget();    //set next action or control node

                for (int i = 1; i < outFlows.length; i++) {    //puts the remaining nodes in the queue
                    if (!queueNode.contains(outFlows[i].getTarget())) {
                        queueNode.add(outFlows[i].getTarget());
                    }
                }

                nodes.append(decision.toString());
            } else {        //just control
                decision.append(nameDecision + " = ");

                String sync = "";
//			for (Pair<String, String> tupla : syncChannelsEdge.keySet()) {	//get sync channel
//				if (tupla.getValue().equals(nameResolver(activityNode.getName()))) {
//					sync = tupla;
//				}
//			}

                sync = inFlows[0].getId();

                String ceIn = syncChannelsEdge.get(sync);

                ce(alphabet, decision, ceIn, " -> ");
                update(alphabet, decision, 1, 1);

                decision.append("(");

                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String ce = createCE();
                    syncChannelsEdge.put(outFlows[i].getId(), ce);

                    decision.append("(");

                    if (i >= 0 && i < outFlows.length - 1) {
                        ce(alphabet, decision, ce, " -> SKIP) |~| ");
                    } else {
                        ce(alphabet, decision, ce, " -> SKIP)");
                    }
                }

                decision.append("); ");

                decision.append(nameDecision + "\n");

                decision.append(nameDecisionTermination + " = ");
                decision.append(nameDecision + " /\\ " + endDiagram + "\n");

                alphabet.add("endDiagram_" + nameResolver(ad.getName()));
                alphabetNode.put(nameResolver(activityNode.getName()), alphabet);

                activityNode = outFlows[0].getTarget();    //set next action or control node

                for (int i = 1; i < outFlows.length; i++) {    //puts the remaining nodes in the queue
                    if (!queueNode.contains(outFlows[i].getTarget())) {
                        queueNode.add(outFlows[i].getTarget());
                    }
                }

                nodes.append(decision.toString());
            }
        } else if (code == 1) {
            for (int i = 0; i < inFlows.length; i++) {

                String stereotype[] = inFlows[i].getStereotypes();

                for (int j = 0; j < stereotype.length; j++) {
                    if (stereotype[j].equals("decisionInputFlow")) {
                        //decisionInputFlow = inFlows[i].getSource().getName();
                        decisionInputFlow = objectEdges.get(syncObjectsEdge.get(inFlows[i].getId()));
                    }
                }
            }

            if (decisionInputFlow != null && inFlows.length == 1) {    //just object
                decision.append(nameDecision + " = ");

                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String oe = createOE(decisionInputFlow);
                    syncObjectsEdge.put(outFlows[i].getId(), oe);
                    objectEdges.put(oe, decisionInputFlow);

                    decision.append(outFlows[i].getGuard() + " & (");

                    if (i >= 0 && i < outFlows.length - 1) {
                        oe(alphabet, decision, oe, "!" + decisionInputFlow, " -> SKIP) [] ");
                    } else {
                        oe(alphabet, decision, oe, "!" + decisionInputFlow, " -> SKIP)");
                    }
                }

                if (outFlows[0].getTarget() instanceof IInputPin) {
                    for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                        if (activityNodeSearch instanceof IAction) {
                            IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
                            for (int y = 0; y < inFlowPin.length; y++) {
                                if (inFlowPin[y].getId().equals(outFlows[0].getTarget().getId())) {
                                    activityNode = activityNodeSearch;
                                }
                            }
                        }
                    }
                } else {
                    activityNode = outFlows[0].getTarget();
                }

                for (int x = 1; x < outFlows.length; x++) {
                    if (outFlows[x].getTarget() instanceof IInputPin) {
                        for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                            if (activityNodeSearch instanceof IAction) {
                                IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
                                for (int y = 0; y < inFlowPin.length; y++) {
                                    if (inFlowPin[y].getId().equals(outFlows[x].getTarget().getId())) {
                                        if (!queueNode.contains(activityNodeSearch)) {
                                            queueNode.add(activityNodeSearch);
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if (!queueNode.contains(outFlows[x].getTarget())) {
                            queueNode.add(outFlows[x].getTarget());
                        }
                    }
                }

            } else if (decisionInputFlow != null && inFlows.length > 1) {                    //object and control
                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String ce = createCE();
                    syncChannelsEdge.put(outFlows[i].getId(), ce);

                    decision.append(outFlows[i].getGuard() + " & (");

                    if (i >= 0 && i < outFlows.length - 1) {
                        ce(alphabet, decision, ce, " -> SKIP) [] ");
                    } else {
                        ce(alphabet, decision, ce, " -> SKIP)");
                    }
                }

                activityNode = outFlows[0].getTarget();    //set next action or control node

                for (int i = 1; i < outFlows.length; i++) {    //puts the remaining nodes in the queue
                    if (!queueNode.contains(outFlows[i].getTarget())) {
                        queueNode.add(outFlows[i].getTarget());
                    }
                }

            } else {        //just control
                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String ce = createCE();
                    syncChannelsEdge.put(outFlows[i].getId(), ce);

                    decision.append("(");

                    if (i >= 0 && i < outFlows.length - 1) {
                        ce(alphabet, decision, ce, " -> SKIP) |~| ");
                    } else {
                        ce(alphabet, decision, ce, " -> SKIP)");
                    }
                }

                activityNode = outFlows[0].getTarget();    //set next action or control node

                for (int i = 1; i < outFlows.length; i++) {    //puts the remaining nodes in the queue
                    if (!queueNode.contains(outFlows[i].getTarget())) {
                        queueNode.add(outFlows[i].getTarget());
                    }
                }
            }
        } else if (code == 2) {
            for (int i = 0; i < inFlows.length; i++) {

                String stereotype[] = inFlows[i].getStereotypes();

                for (int j = 0; j < stereotype.length; j++) {
                    if (stereotype[j].equals("decisionInputFlow")) {
                        //decisionInputFlow = inFlows[i].getSource().getName();
                        decisionInputFlow = objectEdges.get(syncObjectsEdge.get(inFlows[i].getId()));
                    }
                }
            }

            if (decisionInputFlow != null && inFlows.length == 1) {    //just object
                decision.append(nameDecision + " = ");

                for (int i = 0; i < inFlows.length; i++) {
                    if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
                        String ceIn = syncObjectsEdge.get(inFlows[i].getId());
                        oe(alphabet, decision, ceIn, "?" + decisionInputFlow, " -> ");
                    }
                }

                update(alphabet, decision, 1, 1);

                decision.append("(");

                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String oe = syncObjectsEdge.get(outFlows[i].getId());

                    decision.append(outFlows[i].getGuard() + " & (");

                    if (i >= 0 && i < outFlows.length - 1) {
                        oe(alphabet, decision, oe, "!" + decisionInputFlow, " -> SKIP) [] ");
                    } else {
                        oe(alphabet, decision, oe, "!" + decisionInputFlow, " -> SKIP)");
                    }
                }

                decision.append("); ");

                decision.append(nameDecision + "\n");

                decision.append(nameDecisionTermination + " = ");
                decision.append(nameDecision + " /\\ " + endDiagram + "\n");

                alphabet.add("endDiagram_" + nameResolver(ad.getName()));
                alphabetNode.put(nameResolver(activityNode.getName()), alphabet);

                if (outFlows[0].getTarget() instanceof IInputPin) {
                    for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                        if (activityNodeSearch instanceof IAction) {
                            IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
                            for (int y = 0; y < inFlowPin.length; y++) {
                                if (inFlowPin[y].getId().equals(outFlows[0].getTarget().getId())) {
                                    activityNode = activityNodeSearch;
                                }
                            }
                        }
                    }
                } else {
                    activityNode = outFlows[0].getTarget();
                }

                for (int x = 1; x < outFlows.length; x++) {
                    if (outFlows[x].getTarget() instanceof IInputPin) {
                        for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                            if (activityNodeSearch instanceof IAction) {
                                IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
                                for (int y = 0; y < inFlowPin.length; y++) {
                                    if (inFlowPin[y].getId().equals(outFlows[x].getTarget().getId())) {
                                        if (!queueNode.contains(activityNodeSearch)) {
                                            queueNode.add(activityNodeSearch);
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if (!queueNode.contains(outFlows[x].getTarget())) {
                            queueNode.add(outFlows[x].getTarget());
                        }
                    }
                }

                nodes.append(decision.toString());
            } else if (decisionInputFlow != null && inFlows.length > 1) {                    //object and control
                decision.append(nameDecision + " = ");

                String sync2 = "";
                String sync = "";

                for (int i = 0; i < inFlows.length; i++) {
                    if (syncChannelsEdge.containsKey(inFlows[i].getId())) {
                        sync2 = inFlows[i].getId();
                    }

                    if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
                        sync = inFlows[i].getId();
                    }
                }


                String ceIn2 = syncChannelsEdge.get(sync2);
                String ceIn = syncObjectsEdge.get(sync);

                decision.append("((");
                ce(alphabet, decision, ceIn2, " -> SKIP");

                decision.append(") ||| (");
                oe(alphabet, decision, ceIn, "?" + decisionInputFlow, " -> ");

                setLocal(alphabet, decision, decisionInputFlow, nameResolver(activityNode.getName()), decisionInputFlow);
                decision.append("SKIP)); ");

                update(alphabet, decision, 2, 1);
                getLocal(alphabet, decision, decisionInputFlow, nameResolver(activityNode.getName()), decisionInputFlow);

                decision.append("(");

                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String ce = syncChannelsEdge.get(outFlows[i].getId());

                    decision.append(outFlows[i].getGuard() + " & (");

                    if (i >= 0 && i < outFlows.length - 1) {
                        ce(alphabet, decision, ce, " -> SKIP) [] ");
                    } else {
                        ce(alphabet, decision, ce, " -> SKIP)");
                    }
                }

                decision.append("); ");

                decision.append(nameDecision + "\n");

                decision.append(nameDecisionTermination + " = ");
                decision.append("((" + nameDecision + " /\\ " + endDiagram + ") ");

                decision.append("[|{|");
                decision.append("get_" + decisionInputFlow + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                decision.append("set_" + decisionInputFlow + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                decision.append("endDiagram_" + nameResolver(ad.getName()));
                decision.append("|}|] ");
                decision.append("Mem_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + "_" + decisionInputFlow + "_t(" + getDefaultValue(parameterNodesInput.get(decisionInputFlow)) + ")) ");

                decision.append("\\{|");
                decision.append("get_" + decisionInputFlow + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
                decision.append("set_" + decisionInputFlow + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()));
                decision.append("|}\n");

                alphabet.add("endDiagram_" + nameResolver(ad.getName()));
                alphabetNode.put(nameResolver(activityNode.getName()), alphabet);

                activityNode = outFlows[0].getTarget();    //set next action or control node

                for (int i = 1; i < outFlows.length; i++) {    //puts the remaining nodes in the queue
                    if (!queueNode.contains(outFlows[i].getTarget())) {
                        queueNode.add(outFlows[i].getTarget());
                    }
                }

                nodes.append(decision.toString());
            } else {        //just control
                decision.append(nameDecision + " = ");

                String sync = "";

                sync = inFlows[0].getId();

                String ceIn = syncChannelsEdge.get(sync);

                ce(alphabet, decision, ceIn, " -> ");
                update(alphabet, decision, 1, 1);

                decision.append("(");

                for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
                    String ce = syncChannelsEdge.get(outFlows[i].getId());

                    decision.append("(");

                    if (i >= 0 && i < outFlows.length - 1) {
                        ce(alphabet, decision, ce, " -> SKIP) |~| ");
                    } else {
                        ce(alphabet, decision, ce, " -> SKIP)");
                    }
                }

                decision.append("); ");

                decision.append(nameDecision + "\n");

                decision.append(nameDecisionTermination + " = ");
                decision.append(nameDecision + " /\\ " + endDiagram + "\n");

                alphabet.add("endDiagram_" + nameResolver(ad.getName()));
                alphabetNode.put(nameResolver(activityNode.getName()), alphabet);

                activityNode = outFlows[0].getTarget();    //set next action or control node

                for (int i = 1; i < outFlows.length; i++) {    //puts the remaining nodes in the queue
                    if (!queueNode.contains(outFlows[i].getTarget())) {
                        queueNode.add(outFlows[i].getTarget());
                    }
                }

                nodes.append(decision.toString());
            }
        }

        return activityNode;
    }

    private IActivityNode defineFlowFinal(IActivityNode activityNode, StringBuilder nodes) {
        StringBuilder flowFinal = new StringBuilder();
        ArrayList<String> alphabet = new ArrayList<>();
        String nameFlowFinal = nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName());
        String nameFlowFinalTermination = nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + "_t";
        String endDiagram = "END_DIAGRAM_" + nameResolver(ad.getName());
        HashMap<String, String> nameObjects = new HashMap<>();
        IFlow inFlows[] = activityNode.getIncomings();

        flowFinal.append(nameFlowFinal + " = ");

        ArrayList<String> ceInitials = new ArrayList<>();

        for (int i = 0; i < inFlows.length; i++) {
            ceInitials.add(inFlows[i].getId());

            if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
                String ceIn2 = syncObjectsEdge.get(inFlows[i].getId());
                nameObjects.put(inFlows[i].getId(), objectEdges.get(ceIn2));
            }

        }

        flowFinal.append("(");
        for (int i = 0; i < ceInitials.size(); i++) {
            String ceIn = syncChannelsEdge.get(ceInitials.get(i));    //get the parallel input channels
            String oeIn = syncObjectsEdge.get(ceInitials.get(i));

            if (ceIn != null) {
                flowFinal.append("(");

                if (i >= 0 && i < ceInitials.size() - 1) {
                    ce(alphabet, flowFinal, ceIn, " -> SKIP) [] ");
                } else {
                    ce(alphabet, flowFinal, ceIn, " -> SKIP)");
                }
            } else {

                String nameObject = nameObjects.get(ceInitials.get(i));

                flowFinal.append("(");

                if (i >= 0 && i < ceInitials.size() - 1) {
                    ce(alphabet, flowFinal, oeIn, "?" + nameObject + " -> SKIP) [] ");
                } else {
                    ce(alphabet, flowFinal, oeIn, "?" + nameObject + " -> SKIP)");
                }
            }

        }

        flowFinal.append("); ");

        update(alphabet, flowFinal, 1, 0);

        flowFinal.append(nameFlowFinal + "\n");

        flowFinal.append(nameFlowFinalTermination + " = ");
        flowFinal.append(nameFlowFinal + " /\\ " + endDiagram + "\n");

        alphabet.add("endDiagram_" + nameResolver(ad.getName()));
        alphabetNode.put(nameResolver(activityNode.getName()), alphabet);

        activityNode = null;

        nodes.append(flowFinal.toString());

        return activityNode;
    }

    private IActivityNode defineInputParameterNode(IActivityNode activityNode, StringBuilder nodes) {
        StringBuilder parameterNode = new StringBuilder();
        ArrayList<String> alphabet = new ArrayList<>();
        String nameParameterNode = "parameter_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + "_t";
        IFlow outFlows[] = activityNode.getOutgoings();
        IFlow inFlows[] = activityNode.getIncomings();

        parameterNode.append(nameParameterNode + " = ");

        update(alphabet, parameterNode, inFlows.length, outFlows.length);
        get(alphabet, parameterNode, nameResolver(activityNode.getName()));

        parameterNode.append("(");

        for (int i = 0; i < outFlows.length; i++) {    //creates the parallel output channels
            String oe = createOE(nameResolver(activityNode.getName()));
            syncObjectsEdge.put(outFlows[i].getId(), oe);
            objectEdges.put(oe, nameResolver(activityNode.getName()));

            parameterNode.append("(");

            if (i >= 0 && i < outFlows.length - 1) {
                oe(alphabet, parameterNode, oe, "!" + nameResolver(activityNode.getName()), " -> SKIP) ||| ");
            } else {
                oe(alphabet, parameterNode, oe, "!" + nameResolver(activityNode.getName()), " -> SKIP)");
            }
        }

        parameterNode.append(")\n");

        parameterAlphabetNode.put(nameResolver(activityNode.getName()), alphabet);
        allInitial.add(nameParameterNode);
        for (String channel : alphabet) {
            if (!alphabetAllInitialAndParameter.contains(channel)) {
                alphabetAllInitialAndParameter.add(channel);
            }
        }

        if (outFlows[0].getTarget() instanceof IInputPin) {
            for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                if (activityNodeSearch instanceof IAction) {
                    IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
                    for (int y = 0; y < inFlowPin.length; y++) {
                        if (inFlowPin[y].getId().equals(outFlows[0].getTarget().getId())) {
                            activityNode = activityNodeSearch;
                        }
                    }
                }
            }
        } else {
            activityNode = outFlows[0].getTarget();
        }


        for (int i = 1; i < outFlows.length; i++) {    //creates the parallel output channels
            if (outFlows[i].getTarget() instanceof IInputPin) {
                for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                    if (activityNodeSearch instanceof IAction) {
                        IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
                        for (int y = 0; y < inFlowPin.length; y++) {
                            if (inFlowPin[y].getId().equals(outFlows[i].getTarget().getId())) {
                                if (!queueNode.contains(activityNodeSearch)) {
                                    queueNode.add(activityNodeSearch);
                                }
                            }
                        }
                    }
                }
            } else {
                if (!queueNode.contains(outFlows[i].getTarget())) {
                    queueNode.add(outFlows[i].getTarget());
                }
            }
        }


        nodes.append(parameterNode.toString());

        return activityNode;
    }

    private IActivityNode defineOutputParameterNode(IActivityNode activityNode, StringBuilder nodes) {
        StringBuilder outParameter = new StringBuilder();
        ArrayList<String> alphabet = new ArrayList<>();
        String nameOutParameter = "parameter_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName());
        String nameOutParameterTermination = nameOutParameter + "_t";
        String endDiagram = "END_DIAGRAM_" + nameResolver(ad.getName());
        //IFlow outFlows[] = activityNode.getOutgoings();
        IFlow inFlows[] = activityNode.getIncomings();
        String nameObject = null;
        String nameObjectUnique = "";
        List<String> nameObjectAdded = new ArrayList<>();
        HashMap<String, String> nameObjects = new HashMap<>();
        List<String> namesMemoryLocal = new ArrayList<>();
        String typeMemoryLocal = null;

        ArrayList<String> ceInitials = new ArrayList<>();
        for (int i = 0; i <  inFlows.length; i++) {
            ceInitials.add(inFlows[i].getId());

            if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
                String ceIn2 = syncObjectsEdge.get(inFlows[i].getId());
                nameObjects.put(inFlows[i].getId(), objectEdges.get(ceIn2));
            }
        }

        outParameter.append(nameOutParameter + " = ");

        outParameter.append("(");


        for (int i = 0; i < ceInitials.size(); i++) {		//get unique channel
            if (nameObjects.get(ceInitials.get(i)) != null) {
                if (!nameObjectAdded.contains(nameObjects.get(ceInitials.get(i)))) {
                    nameObjectAdded.add(nameObjects.get(ceInitials.get(i)));
                    nameObjectUnique += nameObjects.get(ceInitials.get(i));
                    typeMemoryLocal = nameObjects.get(ceInitials.get(i));
                }
            }
        }

        if (!nameObjectUnique.equals("")) {
            namesMemoryLocal.add(nameObjectUnique);
        }

        for (int i = 0; i < ceInitials.size(); i++) {
            String ceIn = syncChannelsEdge.get(ceInitials.get(i));	//get the parallel input channels
            String oeIn = syncObjectsEdge.get(ceInitials.get(i));

            if (ceIn != null) {
                outParameter.append("(");

                if (i >= 0 && i < ceInitials.size() - 1) {
                    ce(alphabet, outParameter, ceIn, " -> SKIP) [] ");
                } else {
                    ce(alphabet, outParameter, ceIn, " -> SKIP)");
                }
            } else {

                nameObject = nameObjects.get(ceInitials.get(i));
                outParameter.append("(");

                if (i >= 0 && i < ceInitials.size() - 1) {
                    ce(alphabet, outParameter, oeIn, "?" + nameObject + " -> ");
                    setLocalInput(alphabet, outParameter, nameObjectUnique, nameResolver(activityNode.getName()), nameObject, oeIn);
                    outParameter.append("SKIP) [] ");
                } else {
                    ce(alphabet, outParameter, oeIn, "?" + nameObject + " -> ");
                    setLocalInput(alphabet, outParameter, nameObjectUnique, nameResolver(activityNode.getName()), nameObject, oeIn);
                    outParameter.append("SKIP)");
                }
            }
        }

        outParameter.append("); ");

        getLocal(alphabet, outParameter, nameObjectUnique, nameResolver(activityNode.getName()), nameObjectUnique);
        set(alphabet, outParameter, activityNode.getName(), nameObjectUnique);

        update(alphabet, outParameter, 1, 0);

        String nameObjectReal = parameterNodesInput.get(typeMemoryLocal);

        if (nameObjectReal == null) {
            nameObjectReal = typeUnionList.get(typeMemoryLocal);
        }

        outParameter.append(nameOutParameter + "\n");
        outParameter.append(nameOutParameterTermination + " = ");

        outParameter.append("((" + nameOutParameter + " /\\ " + endDiagram + ") ");

        outParameter.append("[|{|");
        outParameter.append("get_" + nameObjectUnique + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
        outParameter.append("set_" + nameObjectUnique + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
        outParameter.append("endDiagram_" + nameResolver(ad.getName()));
        outParameter.append("|}|] ");
        outParameter.append("Mem_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + "_" + nameObjectUnique + "_t(" + getDefaultValue(nameObjectReal) + ")) ");

        outParameter.append("\\{|");
        outParameter.append("get_" + nameObjectUnique + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
        outParameter.append("set_" + nameObjectUnique + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()));
        outParameter.append("|}\n");

        alphabet.add("endDiagram_" + nameResolver(ad.getName()));
        alphabetNode.put(nameResolver("parameter_" + activityNode.getName()), alphabet);

        activityNode = null;

        nodes.append(outParameter.toString());

        return activityNode;
    }

    private IActivityNode defineObjectNode(IActivityNode activityNode, StringBuilder nodes, int code) {
        StringBuilder objectNode = new StringBuilder();
        ArrayList<String> alphabet = new ArrayList<>();
        String nameObjectNode = nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName());
        String nameObjectNodeTermination = nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + "_t";
        String endDiagram = "END_DIAGRAM_" + nameResolver(ad.getName());
        IFlow outFlows[] = activityNode.getOutgoings();
        IFlow inFlows[] = activityNode.getIncomings();
        String nameObject = null;
        String nameObjectUnique = "";
        List<String> nameObjectAdded = new ArrayList<>();
        HashMap<String, String> nameObjects = new HashMap<>();
        List<String> namesMemoryLocal = new ArrayList<>();
        String typeMemoryLocal = null;

        if (code == 0) {
            ArrayList<String> ceInitials = new ArrayList<>();
            for (int i = 0; i < inFlows.length; i++) {
                ceInitials.add(inFlows[i].getId());

                if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
                    String ceIn2 = syncObjectsEdge.get(inFlows[i].getId());
                    nameObjects.put(inFlows[i].getId(), objectEdges.get(ceIn2));
                }
            }

            objectNode.append(nameObjectNode + " = ");

            objectNode.append("(");


            for (int i = 0; i < ceInitials.size(); i++) {        //get unique channel
                if (nameObjects.get(ceInitials.get(i)) != null) {
                    if (!nameObjectAdded.contains(nameObjects.get(ceInitials.get(i)))) {
                        nameObjectAdded.add(nameObjects.get(ceInitials.get(i)));
                        nameObjectUnique += nameObjects.get(ceInitials.get(i));
                        typeMemoryLocal = nameObjects.get(ceInitials.get(i));
                    }
                }
            }

            if (!nameObjectUnique.equals("")) {
                namesMemoryLocal.add(nameObjectUnique);
            }

            for (int i = 0; i < ceInitials.size(); i++) {
                String oeIn = syncObjectsEdge.get(ceInitials.get(i)); //get the parallel input channels

                nameObject = nameObjects.get(ceInitials.get(i));
                objectNode.append("(");

                if (i >= 0 && i < ceInitials.size() - 1) {
                    ce(alphabet, objectNode, oeIn, "?" + nameObject + " -> ");
                    setLocalInput(alphabet, objectNode, nameObjectUnique, nameResolver(activityNode.getName()), nameObject, oeIn);
                    objectNode.append("SKIP) [] ");
                } else {
                    ce(alphabet, objectNode, oeIn, "?" + nameObject + " -> ");
                    setLocalInput(alphabet, objectNode, nameObjectUnique, nameResolver(activityNode.getName()), nameObject, oeIn);
                    objectNode.append("SKIP)");
                }
            }

            objectNode.append("); ");

            update(alphabet, objectNode, 1, activityNode.getOutgoings().length);

            getLocal(alphabet, objectNode, nameObjectUnique, nameResolver(activityNode.getName()), nameObjectUnique);

            objectNode.append("(");

            for (int i = 0; i < outFlows.length; i++) {
               String oe = createOE(nameObjectUnique); //creates output channels
               syncObjectsEdge.put(outFlows[i].getId(), oe);
               objectEdges.put(oe, nameObjectUnique);
               objectNode.append("(");

               if (i >= 0 && (i < outFlows.length - 1)) {
                   oe(alphabet, objectNode, oe, "!" + nameObjectUnique, " -> SKIP) ||| ");
               } else {
                   oe(alphabet, objectNode, oe, "!" + nameObjectUnique, " -> SKIP)");
               }
            }

            objectNode.append("); ");

            objectNode.append(nameObjectNode + "\n");
            objectNode.append(nameObjectNodeTermination + " = ");

            objectNode.append("((" + nameObjectNode + " /\\ " + endDiagram + ") ");

            objectNode.append("[|{|");
            objectNode.append("get_" + nameObjectUnique + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
            objectNode.append("set_" + nameObjectUnique + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
            objectNode.append("endDiagram_" + nameResolver(ad.getName()));
            objectNode.append("|}|] ");
            objectNode.append("Mem_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + "_" + nameObjectUnique + "_t(" + getDefaultValue(parameterNodesInput.get(typeMemoryLocal)) + ")) ");

            objectNode.append("\\{|");
            objectNode.append("get_" + nameObjectUnique + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
            objectNode.append("set_" + nameObjectUnique + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()));
            objectNode.append("|}\n");

            //

            alphabet.add("endDiagram_" + nameResolver(ad.getName()));
            alphabetNode.put(nameResolver(activityNode.getName()), alphabet);

            if (outFlows[0].getTarget() instanceof IInputPin) {
                for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                    if (activityNodeSearch instanceof IAction) {
                        IInputPin inPins[] = ((IAction) activityNodeSearch).getInputs();
                        for (int y = 0; y < inPins.length; y++) {
                            if (inPins[y].getId().equals(outFlows[0].getTarget().getId())) {
                                activityNode = activityNodeSearch;
                            }
                        }
                    }
                }
            } else {
                activityNode = outFlows[0].getTarget();
            }

            for (int i = 1; i < outFlows.length; i++) {    //creates the parallel output channels
                if (outFlows[i].getTarget() instanceof IInputPin) {
                    for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                        if (activityNodeSearch instanceof IAction) {
                            IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
                            for (int y = 0; y < inFlowPin.length; y++) {
                                if (inFlowPin[y].getId().equals(outFlows[i].getTarget().getId())) {
                                    if (!queueNode.contains(activityNodeSearch)) {
                                        queueNode.add(activityNodeSearch);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (!queueNode.contains(outFlows[i].getTarget())) {
                        queueNode.add(outFlows[i].getTarget());
                    }
                }
            }

            nodes.append(objectNode.toString());
        } else if (code == 1) {
            ArrayList<String> union = new ArrayList<>();
            List<String> nameObjs = new ArrayList<>();
            List<String> nodesAdded = new ArrayList<>();

            for (int i = 0; i < inFlows.length; i++) {
                nameObjs.addAll(getObjects(inFlows[i], nodesAdded));
            }

            for (String nameObj : nameObjs) {
                if (!union.contains(nameObj)) {
                    nameObjectUnique += nameObj;
                    union.add(nameObj);
                    typeMemoryLocal = nameObj;
                }
            }

            if (union.size() > 1) {
                unionList.add(union);
                typeUnionList.put(nameObjectUnique, parameterNodesInput.get(typeMemoryLocal));
            }

            if (!nameObjectUnique.equals("")) {
                namesMemoryLocal.add(nameObjectUnique);
            }

            for (int i = 0; i < outFlows.length; i++) {
                String oe = createOE(nameObjectUnique); //creates output channels
                syncObjectsEdge.put(outFlows[i].getId(), oe);
                objectEdges.put(oe, nameObjectUnique);

                if (i >= 0 && (i < outFlows.length - 1)) {
                    oe(alphabet, objectNode, oe, "!" + nameObjectUnique, " -> SKIP) ||| ");
                } else {
                    oe(alphabet, objectNode, oe, "!" + nameObjectUnique, " -> SKIP)");
                }
            }

            if (outFlows[0].getTarget() instanceof IInputPin) {
                for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                    if (activityNodeSearch instanceof IAction) {
                        IInputPin inPins[] = ((IAction) activityNodeSearch).getInputs();
                        for (int y = 0; y < inPins.length; y++) {
                            if (inPins[y].getId().equals(outFlows[0].getTarget().getId())) {
                                activityNode = activityNodeSearch;
                            }
                        }
                    }
                }
            } else {
                activityNode = outFlows[0].getTarget();
            }

            for (int i = 1; i < outFlows.length; i++) {    //creates the parallel output channels
                if (outFlows[i].getTarget() instanceof IInputPin) {
                    for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                        if (activityNodeSearch instanceof IAction) {
                            IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
                            for (int y = 0; y < inFlowPin.length; y++) {
                                if (inFlowPin[y].getId().equals(outFlows[i].getTarget().getId())) {
                                    if (!queueNode.contains(activityNodeSearch)) {
                                        queueNode.add(activityNodeSearch);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (!queueNode.contains(outFlows[i].getTarget())) {
                        queueNode.add(outFlows[i].getTarget());
                    }
                }
            }

        } else if (code == 2) {
            ArrayList<String> ceInitials = new ArrayList<>();
            for (int i = 0; i < inFlows.length; i++) {
                ceInitials.add(inFlows[i].getId());

                if (syncObjectsEdge.containsKey(inFlows[i].getId())) {
                    String ceIn2 = syncObjectsEdge.get(inFlows[i].getId());
                    nameObjects.put(inFlows[i].getId(), objectEdges.get(ceIn2));
                }
            }

            objectNode.append(nameObjectNode + " = ");

            objectNode.append("(");


            for (int i = 0; i < ceInitials.size(); i++) {        //get unique channel
                if (nameObjects.get(ceInitials.get(i)) != null) {
                    if (!nameObjectAdded.contains(nameObjects.get(ceInitials.get(i)))) {
                        nameObjectAdded.add(nameObjects.get(ceInitials.get(i)));
                        nameObjectUnique += nameObjects.get(ceInitials.get(i));
                        typeMemoryLocal = nameObjects.get(ceInitials.get(i));
                    }
                }
            }

            if (!nameObjectUnique.equals("")) {
                namesMemoryLocal.add(nameObjectUnique);
            }

            for (int i = 0; i < ceInitials.size(); i++) {
                String ceIn = syncChannelsEdge.get(ceInitials.get(i));    //get the parallel input channels
                String oeIn = syncObjectsEdge.get(ceInitials.get(i));

                if (ceIn != null) {
                    objectNode.append("(");

                    if (i >= 0 && i < ceInitials.size() - 1) {
                        ce(alphabet, objectNode, ceIn, " -> SKIP) [] ");
                    } else {
                        ce(alphabet, objectNode, ceIn, " -> SKIP)");
                    }
                } else {

                    nameObject = nameObjects.get(ceInitials.get(i));
                    objectNode.append("(");

                    if (i >= 0 && i < ceInitials.size() - 1) {
                        ce(alphabet, objectNode, oeIn, "?" + nameObject + " -> ");
                        setLocalInput(alphabet, objectNode, nameObjectUnique, nameResolver(activityNode.getName()), nameObject, oeIn);
                        objectNode.append("SKIP) [] ");
                    } else {
                        ce(alphabet, objectNode, oeIn, "?" + nameObject + " -> ");
                        setLocalInput(alphabet, objectNode, nameObjectUnique, nameResolver(activityNode.getName()), nameObject, oeIn);
                        objectNode.append("SKIP)");
                    }
                }
            }

            objectNode.append("); ");

            update(alphabet, objectNode, 1, outFlows.length);

            getLocal(alphabet, objectNode, nameObjectUnique, nameResolver(activityNode.getName()), nameObjectUnique);

            objectNode.append("(");

            for (int i = 0; i < outFlows.length; i++) {
                String oe = syncObjectsEdge.get(outFlows[i].getId());
                objectNode.append("(");

                if (i >= 0 && (i < outFlows.length - 1)) {
                    oe(alphabet, objectNode, oe, "!" + nameObjectUnique, " -> SKIP) ||| ");
                } else {
                    oe(alphabet, objectNode, oe, "!" + nameObjectUnique, " -> SKIP)");
                }
            }

            objectNode.append("); ");

            objectNode.append(nameObjectNode + "\n");
            objectNode.append(nameObjectNodeTermination + " = ");

            objectNode.append("((" + nameObjectNode + " /\\ " + endDiagram + ") ");

            objectNode.append("[|{|");
            objectNode.append("get_" + nameObjectUnique + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
            objectNode.append("set_" + nameObjectUnique + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
            objectNode.append("endDiagram_" + nameResolver(ad.getName()));
            objectNode.append("|}|] ");
            objectNode.append("Mem_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + "_" + nameObjectUnique + "_t(" + getDefaultValue(parameterNodesInput.get(typeMemoryLocal)) + ")) ");

            objectNode.append("\\{|");
            objectNode.append("get_" + nameObjectUnique + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()) + ",");
            objectNode.append("set_" + nameObjectUnique + "_" + nameResolver(activityNode.getName()) + "_" + nameResolver(ad.getName()));
            objectNode.append("|}\n");

            //

            alphabet.add("endDiagram_" + nameResolver(ad.getName()));
            alphabetNode.put(nameResolver(activityNode.getName()), alphabet);

            if (outFlows[0].getTarget() instanceof IInputPin) {
                for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                    if (activityNodeSearch instanceof IAction) {
                        IInputPin inPins[] = ((IAction) activityNodeSearch).getInputs();
                        for (int y = 0; y < inPins.length; y++) {
                            if (inPins[y].getId().equals(outFlows[0].getTarget().getId())) {
                                activityNode = activityNodeSearch;
                            }
                        }
                    }
                }
            } else {
                activityNode = outFlows[0].getTarget();
            }

            for (int i = 1; i < outFlows.length; i++) {    //creates the parallel output channels
                if (outFlows[i].getTarget() instanceof IInputPin) {
                    for (IActivityNode activityNodeSearch : ad.getActivityNodes()) {
                        if (activityNodeSearch instanceof IAction) {
                            IInputPin inFlowPin[] = ((IAction) activityNodeSearch).getInputs();
                            for (int y = 0; y < inFlowPin.length; y++) {
                                if (inFlowPin[y].getId().equals(outFlows[i].getTarget().getId())) {
                                    if (!queueNode.contains(activityNodeSearch)) {
                                        queueNode.add(activityNodeSearch);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (!queueNode.contains(outFlows[i].getTarget())) {
                        queueNode.add(outFlows[i].getTarget());
                    }
                }
            }

            nodes.append(objectNode.toString());
        }

        return activityNode;
    }

    private String createCE() {
        return "ce_" + nameResolver(ad.getName()) + "." + countCe_ad++;
    }

    private String createOE(String nameObject) {
        return "oe_" + nameObject + "_" + nameResolver(ad.getName()) + "." + countOe_ad++;
    }

    private int startActivity(ArrayList<String> alphabetNode, StringBuilder action, String nameAD, List<String> inputPins) {
        int count = 0;
        count = addCountCall(nameResolver(nameAD));
        String startActivity = "startActivity_" + nameResolver(nameAD) + "." + count;
        alphabetNode.add(startActivity);
        callBehaviourNumber.add(new Pair<>(nameResolver(nameAD), count));

        List<String> outputPinsUsed = callBehaviourInputs.get(nameResolver(nameAD));
        if (outputPinsUsed == null) {
            outputPinsUsed = inputPins;
            callBehaviourInputs.put(nameAD, inputPins);
        }

        for (String pin : outputPinsUsed) {
            startActivity += "!" + pin;
        }

        action.append(startActivity + " -> ");

        return count;
    }

    private void endActivity(ArrayList<String> alphabetNode, StringBuilder action, String nameAD, List<String> outputPins, int count) {
        String endActivity = "endActivity_" + nameResolver(nameAD) + "." + count;
        alphabetNode.add(endActivity);

        List<String> outputPinsUsed = callBehaviourOutputs.get(nameResolver(nameAD));
        if (outputPinsUsed == null) {
            outputPinsUsed = outputPins;
            callBehaviourOutputs.put(nameAD, outputPins);
        }

        for (String pin : outputPinsUsed) {
            endActivity += "?" + pin;
        }

        action.append(endActivity + " -> ");
    }

    private void get(ArrayList<String> alphabetNode, StringBuilder action, String nameObject) {
        String get = "get_" + nameObject + "_" + nameResolver(ad.getName()) + "." + countGet_ad++;
        alphabetNode.add(get);
        action.append(get + "?" + nameObject + " -> ");
    }

    private void set(ArrayList<String> alphabetNode, StringBuilder action, String nameMemory, String nameObject) {
        String set = "set_" + nameMemory + "_" + nameResolver(ad.getName()) + "." + countSet_ad++;
        alphabetNode.add(set);
        action.append(set +"!" + nameObject + " -> ");
        parameterNodesOutputObject.put(nameMemory, nameObject);
    }

    private void setLocal(ArrayList<String> alphabetNode, StringBuilder action, String nameObject, String nameNode, String data) {
        String set = "set_" + nameObject + "_" + nameNode + "_" + nameResolver(ad.getName()) + "." + countSet_ad++;
        action.append(set + "!" + data + " -> ");
        Pair<String, String> memoryLocalPair = new Pair<String, String>(nameNode, nameObject);
        if (!memoryLocal.contains(memoryLocalPair)) {
            memoryLocal.add(memoryLocalPair);
        }
    }

    private void getLocal(ArrayList<String> alphabetNode, StringBuilder action, String nameObject, String nameNode, String data) {
        String get = "get_" + nameObject + "_" + nameNode + "_" + nameResolver(ad.getName()) + "." + countGet_ad++;
        action.append(get + "?" + data + " -> ");
        Pair<String, String> memoryLocalPair = new Pair<String, String>(nameNode, nameObject);
        if (!memoryLocal.contains(memoryLocalPair)) {
            memoryLocal.add(memoryLocalPair);
        }
    }

    private void setLocalInput(ArrayList<String> alphabetNode, StringBuilder action, String nameObject, String nameNode, String data, String oeIn) {
        String set = "set_" + nameObject + "_" + nameNode + "_" + nameResolver(ad.getName()) + "." + countSet_ad++;
        action.append(set + "!" + data + " -> ");
        Pair<String, String> memoryLocalPair = new Pair<String, String>(nameNode, nameObject);
        memoryLocalChannel.add(new Pair<String, String>(oeIn, nameObject));

        if (!memoryLocal.contains(memoryLocalPair)) {
            memoryLocal.add(memoryLocalPair);
        }
    }

    private void lock(ArrayList<String> alphabetNode, StringBuilder action, int inOut, String nameNode) {
        if (inOut == 0) {
            String lock = "lock_" + nameNode;
            alphabetNode.add(lock);
            lockChannel.add(nameNode);
            action.append(lock + ".lock -> ");
        } else {
            String lock = "lock_" + nameNode;
            action.append(lock + ".unlock -> ");
        }
    }

    private void event(ArrayList<String> alphabet, String nameAction, StringBuilder action) {
        alphabet.add("event_" + nameAction);
        eventChannel.add("event_" + nameAction);
        action.append("event_" + nameAction + " -> ");
    }

    private void ce(ArrayList<String> alphabetNode, StringBuilder action, String ce, String posCe) {
        alphabetNode.add(ce);
        action.append(ce + posCe);
    }

    private void oe(ArrayList<String> alphabetNode, StringBuilder action, String oe, String data, String posOe) {
        alphabetNode.add(oe);
        action.append(oe + data + posOe);
    }

    private void update(ArrayList<String> alphabetNode, StringBuilder action, int countInFlows, int countOutFlows) {
        String update = "update_" + nameResolver(ad.getName()) + "." + countUpdate_ad++;
        alphabetNode.add(update);
        action.append(update + "!(" + countOutFlows + "-" + countInFlows + ") -> ");

        int result = countOutFlows - countInFlows;

        if (result < limiteInf) {
            limiteInf = result;

            if (limiteSup == -99) {
                limiteSup = result;
            }

        }

        if (result > limiteSup) {
            limiteSup = result;

            if (limiteInf == 99) {
                limiteInf = result;
            }

        }

    }

    private void clear(ArrayList<String> alphabetNode, StringBuilder action) {
        String update = "clear_" + nameResolver(ad.getName()) + "." + countClear_ad++;
        alphabetNode.add(update);
        action.append(update + " -> ");
    }

    private List<String> replaceExpression(String expression) {
        String value = "";
        char[] expChar = expression.toCharArray();
        List<String> expReplaced = new ArrayList<>();
        for (int i = 0; i < expChar.length; i++) {
            if (expChar[i] == '+' || expChar[i] == '-' || expChar[i] == '*' || expChar[i] == '/') {
                expReplaced.add(value);
                value = "";
            } else {
                value += expChar[i];
            }
        }

        if (!value.equals("")) {    //add last value
            expReplaced.add(value);
        }

        return expReplaced;
    }

    private List<String> getObjects(IFlow flow, List<String> nodes) {
        List<String> objects = new ArrayList<>();

        if (!nodes.contains(flow.getSource().getId())) {
            nodes.add(flow.getSource().getId());
            if (flow.getSource() instanceof IActivityParameterNode) {
                objects.add(flow.getSource().getName());
            } else if (flow.getSource() instanceof IOutputPin) {
                IInputPin inPins[] = ((IAction) flow.getSource().getOwner()).getInputs();
                for (int x = 0; x < inPins.length; x++) {
                    for (IFlow flowNode : inPins[x].getIncomings()) {
                        objects.addAll(getObjects(flowNode, nodes));
                    }
                }
            } else {
                for (IFlow flowNode : flow.getSource().getIncomings()) {
                    objects.addAll(getObjects(flowNode, nodes));
                }
            }
        }

        return objects;
    }

    private String nameResolver(String name) {
        return name.replace(" ", "").replace("!", "_").replace("@", "_")
                .replace("%", "_").replace("&", "_").replace("*", "_")
                .replace("(", "_").replace(")", "_").replace("+", "_")
                .replace("-", "_").replace("=", "_").replace("?", "_")
                .replace(":", "_").replace("/", "_").replace(";", "_")
                .replace(">", "_").replace("<", "_").replace(",", "_")
                .replace("{", "_").replace("}", "_").replace("|", "_")
                .replace("\\", "_");
    }
}