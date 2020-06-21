package com.telesim.telesim.services;

import com.telesim.telesim.models.USSDEvents;
import com.telesim.telesim.models.USSDRequest;
import com.telesim.telesim.models.USSDStates;
import org.springframework.statemachine.StateMachine;

public interface USSDRequestService {
    USSDRequest newUSSDRequest(USSDRequest ussdRequest);

    StateMachine<USSDStates, USSDEvents> authorize(String sessionId);

    StateMachine<USSDStates, USSDEvents> cancel(String sessionId);

    StateMachine<USSDStates, USSDEvents> complete(String sessionId);

    StateMachine<USSDStates, USSDEvents> process(String sessionId);

}
