package com.example.runnable_blockingqueue_test.repository;

import com.example.runnable_blockingqueue_test.model.RequestLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestLogRepository extends JpaRepository<RequestLog, Integer> {
}
