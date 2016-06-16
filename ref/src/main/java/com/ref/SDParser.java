package com.ref;

import com.change_vision.jude.api.inf.model.ISequenceDiagram;

public class SDParser {

	private ISequenceDiagram seq1;
	private ISequenceDiagram seq2;
	
	public SDParser(ISequenceDiagram seq1, ISequenceDiagram seq2) {
		this.seq1 = seq1;
		this.seq2 = seq2;
	}

	public static String parseSDs(ISequenceDiagram seq1, ISequenceDiagram seq2) {
		String process = "";
		parseHeader();
		parseSD();
		parseSD();
		return process;
	}

	public static void parseSD() {
		// TODO Auto-generated method stub
		
	}

	public static void parseHeader() {
		// TODO Auto-generated method stub
		
	}

	public String defineTypes() {
		return null;
	}

}
