package com.telesim.telesim.services;

import com.telesim.telesim.models.USSDEvents;
import com.telesim.telesim.models.USSDRequest;
import com.telesim.telesim.models.USSDStates;
import com.telesim.telesim.repository.USSDRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class USSDStateChangeListener extends StateMachineInterceptorAdapter<USSDStates, USSDEvents> {
    private final USSDRequestRepository ussdRequestRepository;

    @Override
    public void preStateChange(State<USSDStates, USSDEvents> state, Message<USSDEvents> message, Transition<USSDStates, USSDEvents> transition, StateMachine<USSDStates, USSDEvents> stateMachine) {
        Optional.ofNullable(message).ifPresent(msg -> {
            Optional.ofNullable(String.class.cast(msg.getHeaders().getOrDefault(USSDRequestServiceImpl.SESSIONID_HEADER, "")))
                    .ifPresent(sessionId -> {
                        USSDRequest ussdRequest = ussdRequestRepository.findBySessionId(sessionId).get(0);
                        ussdRequest.setUssdstate(state.getId());
                        ussdRequestRepository.save(ussdRequest);
                    });
        });
    }
}
