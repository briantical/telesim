package com.telesim.telesim.configuration.actions;

import com.telesim.telesim.models.USSDEvents;
import com.telesim.telesim.models.USSDStates;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Component
public class PreAuthApprovedAction implements Action<USSDStates, USSDEvents> {
    @Override
    public void execute(StateContext<USSDStates, USSDEvents> context) {
        System.out.println("Sending Notification of PreAuth Approved");
    }
}