package com.ref.interfaces.activityDiagram;

import com.ref.exceptions.InvalidEditingException;

public interface IActivity {

	IActivityDiagram getActivityDiagram();

	IActivityNode[] getActivityNodes();

	void setName(String nameAD) throws InvalidEditingException;

	String getName();

	String getId();

}
