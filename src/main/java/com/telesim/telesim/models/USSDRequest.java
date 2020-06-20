package com.telesim.telesim.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class USSDRequest {
    @Id
    @GeneratedValue
    private  Long id;
    private  String text;
    private  String sessionId;
    private  String phoneNumber;
    private  String networkCode;
    private  String serviceCode;

    @Enumerated(EnumType.STRING)
    private  USSDStates ussdstate;
}
