package com.telesim.telesim.configuration.actions;

import com.telesim.telesim.models.USSDEvents;
import com.telesim.telesim.models.USSDStates;
import com.telesim.telesim.services.USSDRequestServiceImpl;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

public class CompleteAction implements Action<USSDStates, USSDEvents> {
    @Override
    public void execute(StateContext<USSDStates, USSDEvents> context) {
        System.out.println("COMPLETING ...");
        context.getStateMachine().sendEvent(MessageBuilder.withPayload(USSDEvents.COMPLETE)
                .setHeader(USSDRequestServiceImpl.SESSIONID_HEADER, context.getMessageHeader(USSDRequestServiceImpl.SESSIONID_HEADER))
                .build());
    }
}
