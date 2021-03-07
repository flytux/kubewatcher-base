package com.kubeworks.watcher.data.entity;

import com.kubeworks.watcher.data.converter.AlertSeverityConverter;
import com.kubeworks.watcher.data.vo.AlertSeverity;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;


@DynamicInsert @DynamicUpdate
@Entity
@EntityListeners(value = AuditingEntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter @Setter @ToString
@NoArgsConstructor
public class AlertHistory extends BaseEntity {

    @Builder
    private AlertHistory(AlertRuleId alertRuleId, String target, String message, AlertSeverity severity) {
        this.alertRuleId = alertRuleId;
        this.target = target;
        this.message = message;
        this.severity = severity;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id", columnDefinition = "bigint unsigned", nullable = false)
    Long historyId;

    @Embedded
    AlertRuleId alertRuleId;

    @Column(name = "target", length = 255, nullable = false)
    @ColumnDefault("'Unknown'")
    String target;

    @Column(name = "message", length = 1000, nullable = false)
    @ColumnDefault("''")
    String message;

    @Column(name = "severity", length = 50)
    @ColumnDefault("''")
    @Convert(converter = AlertSeverityConverter.class)
    AlertSeverity severity; //심각도, 위험/경고/정보 (위험일 경우 SMS 발송, Severity 관계없이 화면 Alert)

    @Column(nullable = false, columnDefinition = "tinyint default 0")
    boolean resolved;

    public void resolve(AlertHistory alertHistory) {
        this.resolved = alertHistory.isResolved();
    }

}
