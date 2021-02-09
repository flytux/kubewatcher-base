package com.kubeworks.watcher.data.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@EntityListeners(value = AuditingEntityListener.class)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KwAlertRule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ruleId", columnDefinition = "bigint unsigned", nullable = false)
    long ruleId;


    @Column(name = "detectType", length = 50, nullable = false)
    String detectType; //Metrics, log

    @Column(name = "category", length = 50, nullable = false)
    String category; //cluster object, nodes, pods, process, event,..

    @Column(name = "resource", length = 50, nullable = false)
    String resource; //CPU, Mem, Disk,..

    @Column(name = "dangerLevel", columnDefinition = "bigint unsigned", nullable = false)
    long dangerLevel; //위험 임계값, detect log 일 경우 0

    @Column(name = "warningLevel", columnDefinition = "bigint unsigned", nullable = false)
    long warningLevel; //워닝 임계값, detect log 일 경우 0

    @Column(name = "duration", columnDefinition = "bigint unsigned", nullable = false)
    long duration; //지속시간, 분단위, detect log 일 경우 0

    @Column(name = "detectString", length = 200, nullable = false)
    String detectString; //Log 검출 문자열

    @Column(name = "severity", length = 50, nullable = false)
    String severity; //심각도, 위험/경고/정보 (위험일 경우 SMS 발송, Severity 관계없이 화면 Alert)

}
