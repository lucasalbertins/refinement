package com.ref.activityDiagram;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ADParserTestActionNode.class, ADParserTestChannel.class, ADParserTesteDecisionNode.class,
		ADParserTesteFlowFinalNode.class, ADParserTestFinalNode.class, ADParserTestForkNode.class,
		ADParserTestInitialNode.class, ADParserTestJoinNode.class, ADParserTestLock.class, AdParserTestMergeNode.class,
		ADParserTestProcessSync.class, AdParserTestTokenManager.class, AdParserTestType.class, AdParserTestMemory.class, AdParserTestMainNodes.class,
		ADParserTestCheckDeadlock.class, ADParserTestCheckDeterminism.class, ADParserTestBehaviourCall.class,  AdParserTestObjectNode.class})
public class AllTests {

}
