package com.telesim.telesim.configuration.actions;

import com.telesim.telesim.models.USSDEvents;
import com.telesim.telesim.models.USSDStates;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

public class AuthorizeAction implements Action<USSDStates, USSDEvents> {
    @Override
    public void execute(StateContext<USSDStates, USSDEvents> context) {
        System.out.println("Authorizing action");
    }
}
