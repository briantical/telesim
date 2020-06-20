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
    public static final String USSD_ID_HEADER = "ussd_id";

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
    public StateMachine<USSDStates, USSDEvents> authorize(Long ussd_id) {
        StateMachine<USSDStates, USSDEvents> sm = build(ussd_id);
        sendEvent(ussd_id, sm, USSDEvents.AUTHORIZE);
        return sm;
    }

    @Transactional
    @Override
    public StateMachine<USSDStates, USSDEvents> cancel(Long ussd_id) {
        StateMachine<USSDStates, USSDEvents> sm = build(ussd_id);
        sendEvent(ussd_id, sm, USSDEvents.CANCEL);
        return sm;
    }

    @Transactional
    @Override
    public StateMachine<USSDStates, USSDEvents> complete(Long ussd_id) {
        StateMachine<USSDStates, USSDEvents> sm = build(ussd_id);
        sendEvent(ussd_id, sm, USSDEvents.COMPLETE);
        return sm;
    }

    @Transactional
    @Override
    public StateMachine<USSDStates, USSDEvents> process(Long ussd_id) {
        StateMachine<USSDStates, USSDEvents> sm = build(ussd_id);
        sendEvent(ussd_id, sm, USSDEvents.PROCESS);
        return sm;
    }

    private void sendEvent(Long ussd_id, StateMachine<USSDStates, USSDEvents> sm, USSDEvents event){
        Message<USSDEvents> msg = MessageBuilder.withPayload(event)
                .setHeader(USSD_ID_HEADER, ussd_id)
                .build();

        sm.sendEvent(msg);
    }

    private StateMachine<USSDStates, USSDEvents> build (Long ussd_id){
        USSDRequest ussdRequest = ussdRequestRepository.getOne(ussd_id);
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
