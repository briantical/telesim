package com.telesim.telesim.configuration;

import com.telesim.telesim.configuration.actions.*;
import com.telesim.telesim.configuration.guards.USSDIdGuard;
import com.telesim.telesim.models.USSDEvents;
import com.telesim.telesim.models.USSDStates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

@Slf4j
@Configuration
@EnableStateMachineFactory
public class MachineConfiguration extends EnumStateMachineConfigurerAdapter<USSDStates, USSDEvents> {
    // My configuration
    private final Action<USSDStates, USSDEvents> authorizeAction = new AuthorizeAction();
    private final Action<USSDStates, USSDEvents> cancelAction = new CancelAction();
    private final Action<USSDStates, USSDEvents> completeAction = new CompleteAction();
    private final Action<USSDStates, USSDEvents> processAction = new ProcessAction();

    private final Action<USSDStates, USSDEvents> preAuthAction = new PreAuthAction();
    private final Action<USSDStates, USSDEvents> authAction = new AuthAction();
    private final Guard<USSDStates, USSDEvents> ussdIdGuard = new USSDIdGuard();
    private final Action<USSDStates, USSDEvents> preAuthApprovedAction = new PreAuthApprovedAction();
    private final Action<USSDStates, USSDEvents> preAuthDeclinedAction = new PreAuthDeclinedAction();
    private final Action<USSDStates, USSDEvents> authApprovedAction = new AuthApprovedAction();
    private final Action<USSDStates, USSDEvents> authDeclinedAction = new AuthDeclinedAction();


    @Override
    public void configure(StateMachineStateConfigurer<USSDStates, USSDEvents> states) throws Exception {
        states.withStates()
                .initial(USSDStates.NEW)
                .states(EnumSet.allOf(USSDStates.class))
                .end(USSDStates.CANCELLED)
                .end(USSDStates.COMPLETED)
                .end(USSDStates.AUTH)
                .end(USSDStates.PRE_AUTH_ERROR)
                .end(USSDStates.AUTH_ERROR);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<USSDStates, USSDEvents> transitions) throws Exception {
        transitions
                .withExternal()
                    .source(USSDStates.NEW)
                    .target(USSDStates.PROCESSING)
                    .event(USSDEvents.AUTHORIZE)
                    .action(authorizeAction)
                    .guard(ussdIdGuard)
                    .and()
                .withExternal()
                    .source(USSDStates.NEW)
                    .target(USSDStates.CANCELLED)
                    .event(USSDEvents.CANCEL)
                    .action(cancelAction)
                    .and()
                .withExternal()
                    .source(USSDStates.PROCESSING)
                    .target(USSDStates.PROCESSING)
                    .event(USSDEvents.PROCESS)
                    .action(processAction)
                    .and()
                .withExternal()
                    .source(USSDStates.PROCESSING)
                    .target(USSDStates.CANCELLED)
                    .event(USSDEvents.CANCEL)
                    .action(cancelAction)
                    .and()
                .withExternal()
                    .source(USSDStates.PROCESSING)
                    .target(USSDStates.COMPLETED)
                    .event(USSDEvents.COMPLETE)
                    .action(completeAction)
                    .and()


                .withExternal()
                    .source(USSDStates.NEW)
                    .target(USSDStates.NEW)
                    .event(USSDEvents.PRE_AUTHORIZE)
                    .action(preAuthAction)
                    .guard(ussdIdGuard)
                    .and()
                .withExternal()
                    .source(USSDStates.NEW)
                    .target(USSDStates.PRE_AUTH)
                    .event(USSDEvents.PRE_AUTH_APPROVE)
                    .action(preAuthApprovedAction)
                    .and()
                .withExternal()
                    .source(USSDStates.NEW)
                    .target(USSDStates.PRE_AUTH_ERROR)
                    .event(USSDEvents.PRE_AUTH_DECLINE)
                    .action(preAuthDeclinedAction)
                    .and()
                .withExternal()
                    .source(USSDStates.PRE_AUTH)
                    .target(USSDStates.PRE_AUTH)
                    .event(USSDEvents.AUTHORIZE)
                    .action(authAction)
                    .and()
                .withExternal()
                    .source(USSDStates.PRE_AUTH)
                    .target(USSDStates.AUTH)
                    .event(USSDEvents.AUTH_APPROVE)
                    .action(authApprovedAction)
                    .and()
                .withExternal().source(USSDStates.PRE_AUTH)
                    .target(USSDStates.AUTH_ERROR)
                    .event(USSDEvents.AUTH_DECLINE)
                    .action(authDeclinedAction);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<USSDStates, USSDEvents> config) throws Exception {
        StateMachineListenerAdapter<USSDStates, USSDEvents> adapter = new StateMachineListenerAdapter<>(){
            @Override
            public void stateChanged(State<USSDStates, USSDEvents> from, State<USSDStates, USSDEvents> to) {
                log.info(String.format("stateChanged(from: %s, to: %s)", from, to));
            }
        };
        config.withConfiguration()
                .listener(adapter);
    }
}
