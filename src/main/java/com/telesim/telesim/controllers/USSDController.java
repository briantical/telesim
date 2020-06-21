package com.telesim.telesim.controllers;

import com.telesim.telesim.models.USSDEvents;
import com.telesim.telesim.models.USSDRequest;
import com.telesim.telesim.models.USSDStates;
import com.telesim.telesim.repository.USSDRequestRepository;
import com.telesim.telesim.services.USSDRequestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.statemachine.StateMachine;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/v1")
public class USSDController {
    @Autowired
    USSDRequestServiceImpl ussdRequestService;
    @Autowired
    USSDRequestRepository ussdRequestRepository;
    USSDRequest ussdRequest;

    @GetMapping("/ussd")
    public String doGet(){
        return "Welcome to Telesim Solutions USSD Api";
    }

    @PostMapping("/ussd")
    public ResponseEntity<String> doPost(@RequestParam MultiValueMap<String, String> ussdpost){
        String text = ussdpost.getFirst("text");
        String sessionId = ussdpost.getFirst("sessionId");
        String phoneNumber = ussdpost.getFirst("phoneNumber");
        String networkCode = ussdpost.getFirst("networkCode");
        String serviceCode = ussdpost.getFirst("serviceCode");

        ussdRequest = USSDRequest.builder()
                .text(text)
                .sessionId(sessionId)
                .phoneNumber(phoneNumber)
                .networkCode(networkCode)
                .serviceCode(serviceCode)
                .build();

//        String chwCode = "^\\d{5}$";
//        boolean matches = Pattern.matches(chwCode, text);



        String response;
        text = text != null ? text : "";
        switch (text){
            case "":
                response = "CON Welcome to Telesim Solutions\n" +
                            "1. Register for service. \n" +
                            "2. Login into service. \n" +
                            "3. More Information and support";
                USSDRequest savedUSSDRequest = ussdRequestService.newUSSDRequest(ussdRequest);
                System.out.println("NEW " + savedUSSDRequest.getUssdstate());
                break;
            case "1":
                response = "END Register for Telesim Solutions \n" +
                           "Visit telesimsolutions.com to register";
                StateMachine<USSDStates, USSDEvents> sm_complete = ussdRequestService.complete(sessionId);
                System.out.println("COMPLETE " + sm_complete.getState().getId());
                break;
            case "2":
                response = "CON Enter your CHW Code: ";
                StateMachine<USSDStates, USSDEvents> sm_authorized = ussdRequestService.authorize(sessionId);
                System.out.println("AUTHORIZE" + sm_authorized.getState().getId());
                break;
            case "3":
                response = "END Visit telesimsolutions.com for support";
                StateMachine<USSDStates, USSDEvents> sm_complete_1 = ussdRequestService.complete(sessionId);
                System.out.println("SECOND COMPLETE" + sm_complete_1.getState().getId());
                break;
            case "12345":
                response = "CON Welcome back, Brian Ivan\n" +
                            "Select a training module: \n" +
                            "1. Getting Started \n" +
                            "2. COVID-19 SOPs \n" +
                            "3. Cerebral Malaria \n" +
                            "4. Malnutrition with Kids";
                StateMachine<USSDStates, USSDEvents> sm_process = ussdRequestService.process(sessionId);
                System.out.println("PROCESS" + sm_process.getState().getId());
                break;
            default:
                response = "END Invalid input";
                    StateMachine<USSDStates, USSDEvents> sm_cancel = ussdRequestService.cancel(sessionId);
                    System.out.println("CANCEL" + sm_cancel.getState().getId());
        }
        return responder(response);
    }

    private ResponseEntity<String> responder(String response){
        var headers = new HttpHeaders();
        headers.add("content-type", "text/plain");
        headers.add("charset", "utf-8");
        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(response);
    }
}
