package com.telesim.telesim.models;

import org.springframework.web.bind.annotation.RequestBody;

public class USSDRequest {
    private final String _sessionId;
    private final String _phoneNumber;
    private final String _networkCode;
    private final String _serviceCode;
    private final String _text;

    public USSDRequest(String sessionId, String phoneNumber, String networkCode, String serviceCode,String text){
        _sessionId = sessionId;
        _phoneNumber = phoneNumber;
        _networkCode = networkCode;
        _serviceCode = serviceCode;
        _text = text;
    }

    public String get_sessionId(){
        return _sessionId;
    }

    public String get_phoneNumber() {
        return _phoneNumber;
    }

    public String get_networkCode() {
        return _networkCode;
    }

    public String get_serviceCode() {
        return _serviceCode;
    }

    public String get_text(){
        return _text;
    }
}
