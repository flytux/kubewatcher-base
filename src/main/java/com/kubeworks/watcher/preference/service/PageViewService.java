package com.kubeworks.watcher.preference.service;

import com.kubeworks.watcher.data.entity.Page;
import com.kubeworks.watcher.data.entity.PageRowPanel;

import java.util.List;
import java.util.Map;

public interface PageViewService {

    Page getPageView(long menuId);

    Map<Long, PageRowPanel> getPagePanels(long menuId);

    Page getPageInfo(long menuId);

    List<Page> getPageList();

}
