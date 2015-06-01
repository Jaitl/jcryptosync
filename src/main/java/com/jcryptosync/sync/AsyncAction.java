package com.jcryptosync.sync;

import java.util.concurrent.RecursiveAction;

public class AsyncAction extends RecursiveAction {

    Action action;

    public void executeAction(Action action) {
        this.action = action;
        fork();
    }

    @Override
    protected void compute() {
        action.action();
    }

    public interface Action{
        void action();
    }
}
