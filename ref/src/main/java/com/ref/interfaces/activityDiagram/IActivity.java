package com.ref.interfaces.activityDiagram;

public interface IActivity extends INamedElement{

	IActivityDiagram getActivityDiagram();

	IActivityNode[] getActivityNodes();

	void setName(String nameAD);

}
