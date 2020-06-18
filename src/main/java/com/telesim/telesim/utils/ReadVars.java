package com.telesim.telesim.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.FileReader;

public class ReadVars {
    public String username;
    public  String apiKey;

    public void setVariables(){
        JSONParser parser = new JSONParser();
        try {
            Object object = parser.parse(new FileReader("./../Config.json"));
            JSONObject jsonObject = (JSONObject) object;
            this.username = (String)jsonObject.get("username");
            this.apiKey = (String)jsonObject.get("apiKey");

        }catch (Exception e){
        }
    }

    public String getUsername(){
        return  this.username;
    }

    public String getApiKey(){
        return this.apiKey;
    }
}
