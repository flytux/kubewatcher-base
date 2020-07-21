package com.kubeworks.watcher.ecosystem.grafana.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.ZonedDateTime;

@Getter @Setter
@FieldDefaults(level= AccessLevel.PRIVATE)
public class DashboardMeta {

    String type;
    boolean canSave;
    boolean canEdit;
    boolean canAdmin;
    boolean canStar;
    String slug;
    String url;
    ZonedDateTime expires;
    ZonedDateTime created;
    ZonedDateTime updated;
    String updatedBy;
    String createdBy;
    int version;
    boolean hasAcl;
    boolean isFolder;
    long folderId;
    String folderTitle;
    String folderUrl;
    boolean provisioned;
    String provisionedExternalId;

    String host;
    String panelUrl;

    public void setUrl(String url) {
        this.url = url;
        if (StringUtils.isNotEmpty(url)) {
            this.panelUrl = RegExUtils.replaceFirst(url, "/d", "/d-solo");
        }
    }
}
