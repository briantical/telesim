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

import javax.annotation.RegEx;
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

        System.out.println("Service Code :" + serviceCode);

        USSDRequest savedUSSDRequest = ussdRequestService.newUSSDRequest(ussdRequest);
        System.out.println("Should be NEW");
        System.out.println(savedUSSDRequest.getUssdstate());

        StateMachine<USSDStates, USSDEvents> sm = ussdRequestService.preAuth(savedUSSDRequest.getId());
        USSDRequest preAuthedUSSD = ussdRequestRepository.getOne(savedUSSDRequest.getId());

        System.out.println("Should be PRE_AUTH or PRE_AUTH_ERROR");
        System.out.println(sm.getState().getId());

        System.out.println(preAuthedUSSD);
        @RegEx
        String chwCode = "^\\d{5}$";
        boolean matches = Pattern.matches(chwCode, text);

        String response;
        switch (text){
            case "":
                response = "CON Welcome to Telesim Solutions\n" +
                            "1. Register for service. \n" +
                            "2. Login into service. \n" +
                            "3. More Information and support";
                break;
            case "1":
                response = "END Register for Telesim Solutions \n" +
                           "Visit telesimsolutions.com to register";
                break;
            case "2":
                response = "CON Enter your CHW Code: ";
                break;
            case "3":
                response = "END Visit telesimsolutions.com for support";
                break;
            case "12345":
                response = "CON Welcome back, Brian Ivan\n" +
                            "Select a training module: \n" +
                            "1. Getting Started \n" +
                            "2. COVID-19 SOPs \n" +
                            "3. Cerebral Malaria \n" +
                            "4. Malnutrition with Kids";
                break;
            default:
                response = "END Invalid input";
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
