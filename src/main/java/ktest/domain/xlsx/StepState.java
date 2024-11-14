package ktest.domain.xlsx;

import ktest.domain.Action;

import java.util.List;

public final class StepState {
    private final Action action;
    private final String broker;
    private final List<String> tags;
    private final String testCase;
    private final String topic;
    private boolean succeeded;

    StepState(final String pTestCase, final String pBroker, final String pTopic, final Action pAction, final List<String> pTags) {
        testCase = pTestCase;
        broker = pBroker;
        topic = pTopic;
        action = pAction;
        tags = pTags;
    }

    Action action() {
        return action;
    }

    String broker() {
        return broker;
    }

    boolean succeeded() {
        return succeeded;
    }

    public void success() {
        succeeded = true;
    }

    List<String> tags() {
        return tags;
    }

    String testCase() {
        return testCase;
    }

    String topic() {
        return topic;
    }
}