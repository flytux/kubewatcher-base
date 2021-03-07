package com.kubeworks.watcher.data.entity;

import com.kubeworks.watcher.data.vo.AlertCategory;
import com.kubeworks.watcher.data.vo.AlertResource;
import com.kubeworks.watcher.data.vo.AlertType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class AlertRuleId implements Serializable {

    public AlertRuleId(AlertType type, AlertCategory category, AlertResource resource) {
        this.type = type;
        this.category = category;
        this.resource = resource;
    }

    @Column(name = "type", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    AlertType type;

    @Column(name = "category", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    AlertCategory category;

    @Column(name = "resource", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    AlertResource resource;
}
