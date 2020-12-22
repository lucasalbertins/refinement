package com.ref.traceability.activityDiagram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ref.astah.adapter.Activity;
import com.ref.interfaces.activityDiagram.IAction;
import com.ref.interfaces.activityDiagram.IActivity;
import com.ref.interfaces.activityDiagram.IActivityNode;
import com.ref.interfaces.activityDiagram.IActivityParameterNode;
import com.ref.interfaces.activityDiagram.IControlNode;
import com.ref.interfaces.activityDiagram.IFlow;
import com.ref.interfaces.activityDiagram.IObjectNode;
import com.ref.interfaces.activityDiagram.IPin;
import com.ref.parser.activityDiagram.ADAlphabet;
import com.ref.parser.activityDiagram.ADCompositeAlphabet;
import com.ref.parser.activityDiagram.Pair;

public class CounterExampleBuilder {

	private ADAlphabet alphabetAD;
	private Activity activity;
	private List<String> traceCounterExample;
	private HashMap<String,Integer> IdSignals;

	public CounterExampleBuilder(List<String> traceCounterExample, Activity activity, ADAlphabet alphabetAD, HashMap<String,Integer> IdSignals) {
		this.activity = activity;
		this.alphabetAD = alphabetAD;
		this.IdSignals = IdSignals;
		
		List<String> trace = new ArrayList<>();// tratamento necessário no trace
		for (String objTrace : traceCounterExample) {
            String[] objTracePartition = objTrace.split("\\.");
            if (objTracePartition.length > 1) {
            	String aux = objTracePartition[0] + ".id";
            	if(!objTracePartition[0].startsWith("oe_")) {
	            	for(int i=2;i<objTracePartition.length;i++) {
	            		aux+="."+objTracePartition[i];
	            	}
            	}
            	else {
            		for(int i=2;i<objTracePartition.length-1;i++) {
	            		aux+="."+objTracePartition[i];
	            	}
            	}
                trace.add(aux);
            } else {
                trace.add(objTrace);
            }
        }
		this.traceCounterExample = trace;
	}

	public HashMap<IActivity, List<String>> createCounterExample(IActivity diagram) {
		HashMap<IActivity,List<String>> nodesCE = new HashMap<>();
		nodesCE.put(diagram, searchDiagram(diagram));
		for (int i = 0; i < diagram.getActivityNodes().length ; i++) {
			if(diagram.getActivityNodes()[i] instanceof IAction && ((IAction)diagram.getActivityNodes()[i]).isCallBehaviorAction()) {
				nodesCE.putAll(createCounterExample(((IAction)diagram.getActivityNodes()[i]).getCallingActivity()));
			}
		}
		return nodesCE;
	}
	
	//varre o diagrama e devolve o id de quem pertece ao trace
	private List<String> searchDiagram(IActivity diagram){
		List<String> ids = new ArrayList<>();
		Pair<IActivity, String> key = null;//diagrama,nome do no 	
    	for (int i = 0; i < diagram.getActivityNodes().length ; i++) {
    		IActivityNode node = diagram.getActivityNodes()[i];
			if (node instanceof IAction) {
				if (((IAction) node).isAcceptEventAction()) {
					//String idAntigo = newIdSignals.get(actionNode.getID());
					int signalNumber = IdSignals.get(node.getId());
					key = new Pair<IActivity, String>(activity,"accept_"+nameNodeResolver(node.getName())+"_"+signalNumber);
				}
				else if(((IAction)node).isSendSignalAction()) {
					//String idAntigo = newIdSignals.get(actionNode.getID());
					int signalNumber = IdSignals.get(node.getId());
					key = new Pair<IActivity, String>(activity,"signal_"+nameNodeResolver(node.getName())+"_"+signalNumber);
				} else {
					key = new Pair<IActivity, String>(diagram,nameNodeResolver(node.getName()));
				}
			}else if(node instanceof IActivityParameterNode) {
				key = new Pair<IActivity, String>(diagram, "parameter_"+nameNodeResolver(node.getName()));
			}else {
				key = new Pair<IActivity, String>(diagram, nameNodeResolver(node.getName()));
			}
			if (alphabetAD instanceof ADCompositeAlphabet) {//se o diagram mais externo tiver CBAs
				HashMap<Pair<IActivity, String>, ArrayList<String>> aux = new HashMap<>();
				aux = ((ADCompositeAlphabet) alphabetAD).getAllAlphabetNodes();
				HashMap<Pair<IActivity,String>,String> allSyncChannels = ((ADCompositeAlphabet) alphabetAD).getAllsyncChannelsEdge(); 
				HashMap<Pair<IActivity,String>,String> allSyncObj = ((ADCompositeAlphabet) alphabetAD).getAllsyncObjectsEdge();
				
				if(node instanceof IActivityParameterNode) {
					aux = alphabetAD.getParameterAlphabetNode();
				}
				
				if (aux.containsKey(key)) {
					List<String> allflowsNode = aux.get(key);

					for (String objTrace : traceCounterExample) {//se contem no trace
						if (allflowsNode != null && allflowsNode.contains(objTrace)) {//se contem no trace TODO(está errado)
							if (!ids.contains(node.getId()) && 
									((node instanceof IAction && !objTrace.startsWith("oe_") && !objTrace.startsWith("ce_")) 
											|| node instanceof IControlNode || node instanceof IObjectNode)) {
								ids.add(node.getId());
							}						
							for(int j = 0; j < node.getIncomings().length; j++) {//varrer as arestas de entrada
								IFlow flow = node.getIncomings()[j];
								Pair<IActivity, String> chave = new Pair<IActivity, String>(diagram,flow.getId());
								String channel = allSyncChannels.get(chave);//verifica nas arestas de controle
								String channelObj = allSyncObj.get(chave);//verifica nas arestas de objeto
								if(channel != null && traceCounterExample.contains(channel)) {//se for de controle e estiver no trace
									if (!ids.contains(flow.getId())) {
										ids.add(flow.getId());
									}
								}else if(channelObj != null && traceCounterExample.contains(channelObj)) {//se for de objeto e estiver no trace
									if (!ids.contains(flow.getId())) {
										ids.add(flow.getId());
									}
								}
							}
							
							for(int j = 0; j < node.getOutgoings().length; j++) {//varrer as arestas de saida
								IFlow flow = node.getOutgoings()[j];
								Pair<IActivity, String> chave = new Pair<IActivity, String>(diagram,flow.getId());
								String channel = allSyncChannels.get(chave);//verifica nas arestas de controle
								String channelObj = allSyncObj.get(chave);//verifica nas arestas de objeto
								if(channel != null && traceCounterExample.contains(channel)) {//se for de controle e estiver no trace
									if (!ids.contains(flow.getId())) {
										ids.add(flow.getId());
									}
								}else if(channelObj != null && traceCounterExample.contains(channelObj)) {//se for de objeto e estiver no trace
									if (!ids.contains(flow.getId())) {
										ids.add(flow.getId());
									}
								}
							}
							
							if(node instanceof IAction) {
								if(((IAction)node).getInputs().length>0) {
									for(int j = 0 ; j < ((IAction)node).getInputs().length; j++) {
										IPin pin = ((IAction)node).getInputs()[j];
										for(IFlow flow :pin.getIncomings()) {
											Pair<IActivity, String> chave = new Pair<IActivity, String>(diagram,flow.getId());
											String channel = allSyncChannels.get(chave);//verifica nas arestas de controle
											String channelObj = allSyncObj.get(chave);//verifica nas arestas de objeto
											if(channel != null && traceCounterExample.contains(channel)) {//se for de controle e estiver no trace
												if (!ids.contains(flow.getId())) {
													ids.add(flow.getId());
												}
											}else if(channelObj != null && traceCounterExample.contains(channelObj)) {//se for de objeto e estiver no trace
												if (!ids.contains(flow.getId())) {
													ids.add(flow.getId());
												}
											}
										}
										
									}
								}
								if(((IAction)node).getOutputs().length>0) {
									for(int j = 0 ; j < ((IAction)node).getOutputs().length; j++) {
										IPin pin = ((IAction)node).getOutputs()[j];
										for(IFlow flow :pin.getOutgoings()) {
											Pair<IActivity, String> chave = new Pair<IActivity, String>(diagram,flow.getId());
											String channel = allSyncChannels.get(chave);//verifica nas arestas de controle
											String channelObj = allSyncObj.get(chave);//verifica nas arestas de objeto
											if(channel != null && traceCounterExample.contains(channel)) {//se for de controle e estiver no trace
												if (!ids.contains(flow.getId())) {
													ids.add(flow.getId());
												}
											}else if(channelObj != null && traceCounterExample.contains(channelObj)) {//se for de objeto e estiver no trace
												if (!ids.contains(flow.getId())) {
													ids.add(flow.getId());
												}
											}
										}
										
									}
								}
							}
						}
					}
				}
			} else {
				HashMap<Pair<IActivity, String>, ArrayList<String>> aux = new HashMap<>();
				aux = alphabetAD.getAlphabetAD();//TODO pinos não estão presentes aqui
				HashMap<Pair<IActivity,String>,String> SyncChannels = alphabetAD.getSyncChannelsEdge(); 
				HashMap<Pair<IActivity,String>,String> SyncObj = alphabetAD.getSyncObjectsEdge();
				
				if(node instanceof IActivityParameterNode) {
					aux = alphabetAD.getParameterAlphabetNode();
				}
				
				List<String> allflowsNode = aux.get(key);
				for (String objTrace : traceCounterExample) {
					if (allflowsNode != null && allflowsNode.contains(objTrace)) {//se contem no trace TODO(está errado)
						if (!ids.contains(node.getId()) && 
								((node instanceof IAction && !objTrace.startsWith("oe_") && !objTrace.startsWith("ce_")) 
										|| node instanceof IControlNode || node instanceof IObjectNode)) {
							ids.add(node.getId());
						}
						for(int j = 0; j < node.getIncomings().length; j++) {//varrer as arestas de entrada
							IFlow flow = node.getIncomings()[j];
							Pair<IActivity, String> chave = new Pair<IActivity, String>(diagram,flow.getId());
							String channel = SyncChannels.get(chave);//verifica nas arestas de controle
							String channelObj = SyncObj.get(chave);//verifica nas arestas de objeto
							if(channel != null && traceCounterExample.contains(channel)) {//se for de controle e estiver no trace
								if (!ids.contains(flow.getId())) {
									ids.add(flow.getId());
								}
							}else if(channelObj != null && traceCounterExample.contains(channelObj)) {//se for de objeto e estiver no trace
								if (!ids.contains(flow.getId())) {
									ids.add(flow.getId());
								}
							}
							
						}
						for(int j = 0; j < node.getOutgoings().length; j++) {//varrer as arestas de entrada
							IFlow flow = node.getOutgoings()[j];
							Pair<IActivity, String> chave = new Pair<IActivity, String>(diagram,flow.getId());
							String channel = SyncChannels.get(chave);//verifica nas arestas de controle
							String channelObj = SyncObj.get(chave);//verifica nas arestas de objeto
							if(channel != null && traceCounterExample.contains(channel)) {//se for de controle e estiver no trace
								if(!ids.contains(flow.getId())) {
									ids.add(flow.getId());
								}
							}else if(channelObj != null && traceCounterExample.contains(channelObj)) {//se for de objeto e estiver no trace
								if(!ids.contains(flow.getId())) {
									ids.add(flow.getId());
								}
							}
						}
					}
					
					if(node instanceof IAction) {
						if(((IAction)node).getInputs().length>0) {
							for(int j = 0 ; j < ((IAction)node).getInputs().length; j++) {
								IPin pin = ((IAction)node).getInputs()[j];
								for(IFlow flow :pin.getIncomings()) {
									Pair<IActivity, String> chave = new Pair<IActivity, String>(diagram,flow.getId());
									String channel = SyncChannels.get(chave);//verifica nas arestas de controle
									String channelObj = SyncObj.get(chave);//verifica nas arestas de objeto
									if(channel != null && traceCounterExample.contains(channel)) {//se for de controle e estiver no trace
										if (!ids.contains(flow.getId())) {
											ids.add(flow.getId());
										}
									}else if(channelObj != null && traceCounterExample.contains(channelObj)) {//se for de objeto e estiver no trace
										if (!ids.contains(flow.getId())) {
											ids.add(flow.getId());
										}
									}
								}
								
							}
						}
						if(((IAction)node).getOutputs().length>0) {
							for(int j = 0 ; j < ((IAction)node).getOutputs().length; j++) {
								IPin pin = ((IAction)node).getOutputs()[j];
								for(IFlow flow :pin.getOutgoings()) {
									Pair<IActivity, String> chave = new Pair<IActivity, String>(diagram,flow.getId());
									String channel = SyncChannels.get(chave);//verifica nas arestas de controle
									String channelObj = SyncObj.get(chave);//verifica nas arestas de objeto
									if(channel != null && traceCounterExample.contains(channel)) {//se for de controle e estiver no trace
										if (!ids.contains(flow.getId())) {
											ids.add(flow.getId());
										}
									}else if(channelObj != null && traceCounterExample.contains(channelObj)) {//se for de objeto e estiver no trace
										if (!ids.contains(flow.getId())) {
											ids.add(flow.getId());
										}
									}
								}
								
							}
						}
					}
				}
			} 
		}
    	return ids;
	}	
	
	private static String nameNodeResolver(String name) {
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
