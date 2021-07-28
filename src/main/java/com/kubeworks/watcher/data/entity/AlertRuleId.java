package com.kubeworks.watcher.data.entity;

import com.kubeworks.watcher.data.converter.AlertCategoryConverter;
import com.kubeworks.watcher.data.vo.AlertCategory;
import com.kubeworks.watcher.data.vo.AlertResource;
import com.kubeworks.watcher.data.vo.AlertType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class AlertRuleId implements Serializable {

    private static final long serialVersionUID = -8472345303069653722L;

    public AlertRuleId(AlertType type, AlertCategory category, AlertResource resource) {
        this.type = type;
        this.category = category;
        this.resource = resource;
    }

    @Column(name = "type", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    AlertType type;

    @Column(name = "category", length = 20)
    @ColumnDefault("''")
    @Convert(converter=AlertCategoryConverter.class)
    AlertCategory category;

    @Column(name = "resource", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    AlertResource resource;
}
