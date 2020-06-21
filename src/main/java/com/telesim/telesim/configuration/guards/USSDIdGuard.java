package com.telesim.telesim.configuration.guards;

import com.telesim.telesim.models.USSDEvents;
import com.telesim.telesim.models.USSDStates;
import com.telesim.telesim.services.USSDRequestServiceImpl;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;

public class USSDIdGuard implements Guard<USSDStates, USSDEvents> {

    @Override
    public boolean evaluate(StateContext<USSDStates, USSDEvents> context) {
        return context.getMessageHeader(USSDRequestServiceImpl.SESSIONID_HEADER) != null;
    }
}
