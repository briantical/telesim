package com.telesim.telesim.configuration.actions;

import com.telesim.telesim.models.USSDEvents;
import com.telesim.telesim.models.USSDStates;
import com.telesim.telesim.services.USSDRequestServiceImpl;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class PreAuthAction implements Action<USSDStates, USSDEvents>{

    @Override
    public void execute(StateContext<USSDStates, USSDEvents> context) {
        System.out.println("PreAuth was called!!!");

        if (new Random().nextInt(10) < 8) {
            System.out.println("Pre Auth Approved");
            context.getStateMachine().sendEvent(MessageBuilder.withPayload(USSDEvents.PRE_AUTH_APPROVE)
                    .setHeader(USSDRequestServiceImpl.USSD_ID_HEADER, context.getMessageHeader(USSDRequestServiceImpl.USSD_ID_HEADER))
                    .build());

        } else {
            System.out.println("Per Auth Declined! No Credit!!!!!!");
            context.getStateMachine().sendEvent(MessageBuilder.withPayload(USSDEvents.PRE_AUTH_DECLINE)
                    .setHeader(USSDRequestServiceImpl.USSD_ID_HEADER, context.getMessageHeader(USSDRequestServiceImpl.USSD_ID_HEADER))
                    .build());
        }
    }
}
