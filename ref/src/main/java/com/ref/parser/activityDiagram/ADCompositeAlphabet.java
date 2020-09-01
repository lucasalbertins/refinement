package com.ref.parser.activityDiagram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.change_vision.jude.api.inf.model.IActivity;


public class ADCompositeAlphabet extends ADAlphabet{
	private List<ADAlphabet> alphabetList;
	private HashMap<Pair<IActivity,String>, ArrayList<String>> allAlphabets = new HashMap<>();
	private HashMap<Pair<IActivity,String>,String> allSyncChannels = new HashMap<Pair<IActivity,String>, String>();
	private HashMap<Pair<IActivity,String>,String> allSyncObject= new HashMap<Pair<IActivity,String>, String>();
	
	public ADCompositeAlphabet(IActivity ad) {
		super(ad);
		alphabetList = new ArrayList<>();
		allAlphabets = new HashMap<>();
	}
	
	public HashMap<Pair<IActivity,String>, ArrayList<String>> getAlphabetNodes() {
		return alphabetAD;
	}
	
	public HashMap<Pair<IActivity,String>, ArrayList<String>> getAllAlphabetNodes() {
		for(ADAlphabet alphabet: this.alphabetList) {//para cada ADAlphabet da lista
			//se ele n for folha
			if(alphabet instanceof ADCompositeAlphabet && !((ADCompositeAlphabet)alphabet).alphabetList.isEmpty()) {
				allAlphabets.putAll(getLeafAlphabet(alphabet));//add o alphabeto das folhas
			}
			else{
				allAlphabets.putAll(alphabet.alphabetAD);
			}
		}
		allAlphabets.putAll(alphabetAD);
		return allAlphabets;
	}
	
	private HashMap<Pair<IActivity,String>, ArrayList<String>> getLeafAlphabet(ADAlphabet alphabet){
		//enquanto for nao folha
		if(alphabet instanceof ADCompositeAlphabet && !((ADCompositeAlphabet)alphabet).alphabetList.isEmpty()) {
			//para cada elemento da lista
			for(ADAlphabet listAlphabet: ((ADCompositeAlphabet)alphabet).alphabetList) {
				allAlphabets.putAll(getLeafAlphabet(listAlphabet));
			}
		}
		
		return alphabet.alphabetAD;
	}
	
	public HashMap<Pair<IActivity,String>,String> getAllsyncChannelsEdge() {
		
		//if (!this.allSyncChannels.isEmpty()) {
			for (ADAlphabet alphabet : this.alphabetList) {//para cada ADAlphabet da lista
				//se ele n for folha
				if (alphabet instanceof ADCompositeAlphabet) {
						//&& !((ADCompositeAlphabet) alphabet).syncChannelsEdge.isEmpty()) {
					allSyncChannels.putAll(getLeafsyncChannelsEdge(alphabet));//add o alphabeto das folhas
				}
				else {
					allSyncChannels.putAll(alphabet.syncChannelsEdge);//add o alphabeto das folhas
				}
			}
			allSyncChannels.putAll(this.syncChannelsEdge);
		//}
		return allSyncChannels;
	}
	
	private HashMap<Pair<IActivity,String>,String> getLeafsyncChannelsEdge(ADAlphabet alphabet){
		//enquanto for nao folha
		if(alphabet instanceof ADCompositeAlphabet && !((ADCompositeAlphabet)alphabet).alphabetList.isEmpty()) {
			//para cada elemento da lista
			for(ADAlphabet listAlphabet: ((ADCompositeAlphabet)alphabet).alphabetList) {
				allSyncChannels.putAll(getLeafsyncChannelsEdge(listAlphabet));
			}
		}
		return alphabet.syncChannelsEdge;
	}
	
	public HashMap<Pair<IActivity,String>,String> getAllsyncObjectsEdge() {
		//if (!this.allSyncObject.isEmpty()) {
			for (ADAlphabet alphabet : this.alphabetList) {//para cada ADAlphabet da lista
				//se ele n for folha
				if (alphabet instanceof ADCompositeAlphabet) {
						//&& !((ADCompositeAlphabet) alphabet).syncObjectsEdge.isEmpty()) {
					allSyncObject.putAll(getLeafsyncObjectsEdge(alphabet));//add o alphabeto das folhas
				}
				else {
					allSyncObject.putAll(alphabet.syncObjectsEdge);
				}
			}
			allSyncObject.putAll(this.syncObjectsEdge);
		//}
		return allSyncObject;
	}
	
	private HashMap<Pair<IActivity,String>,String> getLeafsyncObjectsEdge(ADAlphabet alphabet){
		//enquanto for nao folha
		if(alphabet instanceof ADCompositeAlphabet && !((ADCompositeAlphabet)alphabet).alphabetList.isEmpty()) {
			//para cada elemento da lista
			for(ADAlphabet listAlphabet: ((ADCompositeAlphabet)alphabet).alphabetList) {
				allSyncObject.putAll(getLeafsyncObjectsEdge(listAlphabet));
			}
		}
		return alphabet.syncObjectsEdge;
	}
	
	public void add(ADAlphabet adAlphabet) {
		this.alphabetList.add(adAlphabet);
	}

	public List<ADAlphabet> getAlphabetList() {
		return alphabetList;
	}

	public void setAlphabetList(List<ADAlphabet> alphabetList) {
		this.alphabetList = alphabetList;
	}
}
