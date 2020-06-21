package com.telesim.telesim.repository;

import com.telesim.telesim.models.USSDRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface USSDRequestRepository extends JpaRepository<USSDRequest, Long> {
    List<USSDRequest> findBySessionId(String sessionId);
}
