package com.ref.parser.activityDiagram;

import java.util.ArrayList;
import java.util.HashMap;

import com.change_vision.jude.api.inf.model.IActivity;

public abstract class ADAlphabet {
	protected HashMap<String, ArrayList<String>> alphabetAD;//alterar tudas as chaves pra Pair<IActivity,String>
	protected HashMap<String, String> syncChannelsEdge;
	protected HashMap<String, String> syncObjectsEdge;
	protected HashMap<String, ArrayList<String>> parameterAlphabetNode;
	protected IActivity ad;
	
	public ADAlphabet(IActivity ad) {
		this.ad = ad;
		alphabetAD = new HashMap<>();
		syncChannelsEdge = new HashMap<>();
		syncObjectsEdge = new HashMap<>();
		parameterAlphabetNode = new HashMap<>();
	}
	
	public HashMap<String, ArrayList<String>> getAlphabetAD() {
		return alphabetAD;
	}
	public void setAlphabetAD(HashMap<String, ArrayList<String>> alphabetAD) {
		this.alphabetAD = alphabetAD;
	}
	public HashMap<String, String> getSyncChannelsEdge() {
		return syncChannelsEdge;
	}
	public void setSyncChannelsEdge(HashMap<String, String> syncChannelsEdge) {
		this.syncChannelsEdge = syncChannelsEdge;
	}
	public HashMap<String, String> getSyncObjectsEdge() {
		return syncObjectsEdge;
	}
	public void setSyncObjectsEdge(HashMap<String, String> syncObjectsEdge) {
		this.syncObjectsEdge = syncObjectsEdge;
	}

	public HashMap<String, ArrayList<String>> getParameterAlphabetNode() {
		return parameterAlphabetNode;
	}

	public void setParameterAlphabetNode(HashMap<String, ArrayList<String>> parameterAlphabetNode) {
		this.parameterAlphabetNode = parameterAlphabetNode;
	}

	public void add(ADAlphabet adAlphabet) {
		// TODO Auto-generated method stub
		
	}
	
	public IActivity getAd() {
		return ad;
	}
	public void setAd(IActivity ad) {
		this.ad = ad;
	}
	
}
