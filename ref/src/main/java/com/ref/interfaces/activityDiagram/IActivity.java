package com.ref.interfaces.activityDiagram;

import com.ref.exceptions.InvalidEditingException;

public interface IActivity extends INamedElement{

	IActivityDiagram getActivityDiagram();

	IActivityNode[] getActivityNodes();

	void setName(String nameAD) throws InvalidEditingException;

}
