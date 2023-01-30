package com.example.runnable_blockingqueue_test.model;


import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;

@Data
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@AllArgsConstructor
//@Builder // 빌더 패턴!!
@Entity
public class RequestLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 프로젝트에 연결된 DB의 넘저링 전략을 따라간다.// 오라클 - 시퀀스, MySQL - auto_increment
    private int id;

    private String parameterMapStr;
    private String requestURI;

    @CreationTimestamp // 시간 자동 입력
    private Timestamp createDate;
}
