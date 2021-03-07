package com.kubeworks.watcher.data.entity;

import com.kubeworks.watcher.data.converter.AlertSeverityConverter;
import com.kubeworks.watcher.data.vo.AlertSeverity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@DynamicInsert @DynamicUpdate
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = {"type", "category", "resource", "detectString"})})
@Entity
@EntityListeners(value = AuditingEntityListener.class)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AlertRule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ruleId", columnDefinition = "bigint unsigned", nullable = false)
    Long ruleId;

    @Embedded
    AlertRuleId alertRuleId;

//    @MapsId(value = "alertRuleId")
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumns( value = {
        @JoinColumn(name = "type", referencedColumnName = "type", insertable = false, updatable = false),
        @JoinColumn(name = "category", referencedColumnName = "category", insertable = false, updatable = false),
        @JoinColumn(name = "resource", referencedColumnName = "resource", insertable = false, updatable = false),
    }, foreignKey = @ForeignKey( ConstraintMode.NO_CONSTRAINT))
    AlertRuleMetric alertRuleMetric;


    @Column(name = "dangerLevel", columnDefinition = "bigint unsigned", nullable = false)
    @ColumnDefault("0")
    Long dangerLevel; //위험 임계값, detect log 일 경우 0

    @Column(name = "warningLevel", columnDefinition = "bigint unsigned", nullable = false)
    @ColumnDefault("0")
    Long warningLevel; //워닝 임계값, detect log 일 경우 0

    @Column(name = "duration", columnDefinition = "bigint unsigned", nullable = false)
    @ColumnDefault("0")
    Long duration; //지속시간, 분단위, detect log 일 경우 0

    @Column(name = "detectString", length = 200)
    @ColumnDefault("''")
    String detectString; //Log 검출 문자열

    @Column(name = "severity", length = 50)
    @ColumnDefault("''")
    @Convert(converter = AlertSeverityConverter.class)
    AlertSeverity severity; //심각도, 위험/경고/정보 (위험일 경우 SMS 발송, Severity 관계없이 화면 Alert)

    public void update(AlertRule alertRule) {
        this.dangerLevel = alertRule.getDangerLevel();
        this.warningLevel = alertRule.getWarningLevel();
        this.duration = alertRule.getDuration();
        this.detectString = alertRule.getDetectString();
        if (alertRule.getSeverity() == null) {
            this.severity = AlertSeverity.NONE;
        } else {
            this.severity = alertRule.getSeverity();
        }
    }
}
