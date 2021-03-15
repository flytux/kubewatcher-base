package com.kubeworks.watcher.data.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@Entity
@EntityListeners(value = AuditingEntityListener.class)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AlertRuleMetric extends BaseEntity {

    @EmbeddedId
    AlertRuleId alertRuleId;

    @Column(name = "metric_name", length = 20, nullable = false)
    String metricName;

    @Column(name = "expression", length = 1000, nullable = false)
    String expression;

    @Column(name = "message_template", length = 1000, nullable = false)
    @ColumnDefault("'%s %s usage'")
    String messageTemplate;


}
