package com.ref.adapter.astah;

import com.ref.interfaces.activityDiagram.IClass;

public class Class implements IClass {
	com.change_vision.jude.api.inf.model.IClass classe;
	
	public Class(com.change_vision.jude.api.inf.model.IClass classe) {
		super();
		this.classe = classe;
	}


	@Override
	public String getName() {
		return classe.getName();
	}

}
