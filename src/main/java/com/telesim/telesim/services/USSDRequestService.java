package com.telesim.telesim.services;

import com.telesim.telesim.models.USSDEvents;
import com.telesim.telesim.models.USSDRequest;
import com.telesim.telesim.models.USSDStates;
import org.springframework.statemachine.StateMachine;

public interface USSDRequestService {
    USSDRequest newUSSDRequest(USSDRequest ussdRequest);

    StateMachine<USSDStates, USSDEvents> authorize(Long sessionId);

    StateMachine<USSDStates, USSDEvents> cancel(Long sessionId);

    StateMachine<USSDStates, USSDEvents> complete(Long sessionId);

    StateMachine<USSDStates, USSDEvents> process(Long sessionId);

}
