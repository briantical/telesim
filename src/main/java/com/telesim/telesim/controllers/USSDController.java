package com.telesim.telesim.controllers;

import com.telesim.telesim.models.USSDRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class USSDController {
    @GetMapping("/ussd")
    public String doGet(){
        return "Welcome to Telesim Solutions USSD Api";
    }

    @PostMapping("/ussd/")
    public ResponseEntity<String> doPost(@RequestBody USSDRequest ussdpost){
        String inputString = ussdpost.get_text();
        String response;

        switch (inputString){
            case "":
                response = "CON Welcome to Telesim Solutions";
//                            "1. Register for service. \n" +
//                            "2. Login into service. \n" +
//                            "3. More Information and support";
                break;
            case "1":
                response = "CON Register for Telesim Solutions \n" +
                        "Enter your occupation: (nurse)";
                break;
            case "2":
                response = "CON Enter your PIN: ";
                break;
            case "3":
                response = "END Visit telesimsolutions.com for support";
                break;
            case "1234":
                response = "END You have access";
                break;
            case "nurse":
                response = "END Welcome back nurse";
                break;
            default:
                response = "END Invalid input";
        }
//        Implement computation per given action code
//        TODO("implement data persisting")

//        Return a response per action
        var headers = new HttpHeaders();
        headers.add("content-type", "text/plain");
        headers.add("charset", "utf-8");
        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(response);
    }
}
