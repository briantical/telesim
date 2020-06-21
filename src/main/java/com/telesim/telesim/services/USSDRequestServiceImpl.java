package com.telesim.telesim.services;

import com.telesim.telesim.models.USSDEvents;
import com.telesim.telesim.models.USSDRequest;
import com.telesim.telesim.models.USSDStates;
import com.telesim.telesim.repository.USSDRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class USSDRequestServiceImpl implements USSDRequestService {
    public static final String SESSIONID_HEADER = "sessionId";

    private final USSDRequestRepository ussdRequestRepository;
    private final StateMachineFactory<USSDStates, USSDEvents> stateMachineFactory;
    private final USSDStateChangeListener ussdStateChangeListener;

    @Override
    public USSDRequest newUSSDRequest(USSDRequest ussdRequest) {
        ussdRequest.setUssdstate(USSDStates.NEW);
        return ussdRequestRepository.save(ussdRequest);
    }

    @Transactional
    @Override
    public StateMachine<USSDStates, USSDEvents> authorize(String sessionId) {
        StateMachine<USSDStates, USSDEvents> sm = build(sessionId);
        sendEvent(sessionId, sm, USSDEvents.AUTHORIZE);
        return sm;
    }

    @Transactional
    @Override
    public StateMachine<USSDStates, USSDEvents> cancel(String sessionId) {
        StateMachine<USSDStates, USSDEvents> sm = build(sessionId);
        sendEvent(sessionId, sm, USSDEvents.CANCEL);
        return sm;
    }

    @Transactional
    @Override
    public StateMachine<USSDStates, USSDEvents> complete(String sessionId) {
        StateMachine<USSDStates, USSDEvents> sm = build(sessionId);
        sendEvent(sessionId, sm, USSDEvents.COMPLETE);
        return sm;
    }

    @Transactional
    @Override
    public StateMachine<USSDStates, USSDEvents> process(String sessionId) {
        StateMachine<USSDStates, USSDEvents> sm = build(sessionId);
        sendEvent(sessionId, sm, USSDEvents.PROCESS);
        return sm;
    }

    private void sendEvent(String sessionId, StateMachine<USSDStates, USSDEvents> sm, USSDEvents event){
        Message<USSDEvents> msg = MessageBuilder.withPayload(event)
                .setHeader(SESSIONID_HEADER, sessionId)
                .build();

        sm.sendEvent(msg);
    }

    private StateMachine<USSDStates, USSDEvents> build (String sessionId){
        USSDRequest ussdRequest = ussdRequestRepository.findBySessionId(sessionId).get(0);
        StateMachine<USSDStates, USSDEvents> sm = stateMachineFactory.getStateMachine(Long.toString(ussdRequest.getId()));
        sm.stop();
        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.addStateMachineInterceptor(ussdStateChangeListener);
                    sma.resetStateMachine(new DefaultStateMachineContext<>(ussdRequest.getUssdstate(), null, null, null));
                });
        sm.start();
        return sm;
    }
}
