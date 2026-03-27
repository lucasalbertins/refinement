package com.ref.activityDiagram.adapter;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ActionTest.class, ActivityDiagramTest.class, ActivityNodeTest.class,
		ActivityTest.class, ControlNodeTest.class, FlowTest.class,
		NamedElementTest.class, ObjectFlowTest.class, ObjectNodeTest.class,PinTest.class})
public class AllTests {

}
