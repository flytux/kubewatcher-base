package com.kubeworks.watcher.preference.service.impl;

import com.kubeworks.watcher.data.entity.Page;
import com.kubeworks.watcher.data.entity.PageRow;
import com.kubeworks.watcher.data.entity.PageRowPanel;
import com.kubeworks.watcher.data.repository.PageRepository;
import com.kubeworks.watcher.preference.service.PageViewService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PageViewServiceImpl implements PageViewService {

    private final PageRepository pageRepository;

    @Autowired
    public PageViewServiceImpl(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    @SneakyThrows
    @Override
    public Page getPageView(long menuId) {
        Optional<Page> pageOptional = pageRepository.findByMenuId(menuId);
        if (!pageOptional.isPresent()) {
            throw new IllegalAccessException("잘못된 경로 입니다. menuId=" + menuId);
        }
        return pageOptional.get();
    }

    @Override
    public Map<Long, PageRowPanel> getPagePanels(long menuId) {
        Page page = getPageView(menuId);
        return page.getRows().stream()
            .map(PageRow::getPanels)
            .flatMap(Collection::stream)
            .collect(Collectors.toMap(PageRowPanel::getSort, pageRowPanel -> pageRowPanel));
    }

}
