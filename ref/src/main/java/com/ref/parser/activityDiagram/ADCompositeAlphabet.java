package com.ref.parser.activityDiagram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.change_vision.jude.api.inf.model.IActivity;


public class ADCompositeAlphabet extends ADAlphabet{
	private List<ADAlphabet> alphabetList;
	private HashMap<Pair<IActivity,String>, ArrayList<String>> allAlphabets = new HashMap<>();
	
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
		HashMap<Pair<IActivity,String>,String> allAlphabets = new HashMap<>();
		for(ADAlphabet alphabet: this.alphabetList) {//para cada ADAlphabet da lista
			//se ele n for folha
			if(alphabet instanceof ADCompositeAlphabet && !((ADCompositeAlphabet)alphabet).syncChannelsEdge.isEmpty()) {
				allAlphabets.putAll(getLeafsyncChannelsEdge(alphabet));//add o alphabeto das folhas
				allAlphabets.putAll(alphabet.syncChannelsEdge);//add o alphabet dele
			}else {
				allAlphabets.putAll(alphabet.syncChannelsEdge);
			}
		}
		return allAlphabets;
	}
	
	private HashMap<Pair<IActivity,String>,String> getLeafsyncChannelsEdge(ADAlphabet alphabet){
		//enquanto for nao folha
		while(alphabet instanceof ADCompositeAlphabet && !((ADCompositeAlphabet)alphabet).alphabetList.isEmpty()) {
			//para cada elemento da lista
			for(ADAlphabet listAlphabet: ((ADCompositeAlphabet)alphabet).alphabetList) {
				return getLeafsyncChannelsEdge(listAlphabet);
			}
		}
		return syncChannelsEdge;
	}
	
	public HashMap<Pair<IActivity,String>,String> getAllsyncObjectsEdge() {
		HashMap<Pair<IActivity,String>,String> allAlphabets = new HashMap<>();
		for(ADAlphabet alphabet: this.alphabetList) {//para cada ADAlphabet da lista
			//se ele n for folha
			if(alphabet instanceof ADCompositeAlphabet && !((ADCompositeAlphabet)alphabet).syncObjectsEdge.isEmpty()) {
				allAlphabets.putAll(getLeafsyncObjectsEdge(alphabet));//add o alphabeto das folhas
				allAlphabets.putAll(alphabet.syncObjectsEdge);//add o alphabet dele
			}else {
				allAlphabets.putAll(alphabet.syncObjectsEdge);
			}
		}
		return allAlphabets;
	}
	
	private HashMap<Pair<IActivity,String>,String> getLeafsyncObjectsEdge(ADAlphabet alphabet){
		//enquanto for nao folha
		while(alphabet instanceof ADCompositeAlphabet && !((ADCompositeAlphabet)alphabet).alphabetList.isEmpty()) {
			//para cada elemento da lista
			for(ADAlphabet listAlphabet: ((ADCompositeAlphabet)alphabet).alphabetList) {
				return getLeafsyncObjectsEdge(listAlphabet);
			}
		}
		return syncObjectsEdge;
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
