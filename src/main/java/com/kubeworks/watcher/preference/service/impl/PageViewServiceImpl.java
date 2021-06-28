package com.kubeworks.watcher.preference.service.impl;

import com.kubeworks.watcher.data.entity.Page;
import com.kubeworks.watcher.data.entity.PageRow;
import com.kubeworks.watcher.data.entity.PageRowPanel;
import com.kubeworks.watcher.data.repository.PageRepository;
import com.kubeworks.watcher.preference.service.PageViewService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service(value="pageViewService")
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
            .map(PageRow::getPageRowPanels)
            .flatMap(Collection::stream)
            .collect(Collectors.toMap(PageRowPanel::getSortOrder, Function.identity()));
    }

    /*
        차트로 구성된 대시보드의 페이지 구성 및 Prometheus query 정보를 쿼리한다.
     */
    @Override
    public Page getPageInfo(long menuId) {
        return pageRepository.findByMenuId(menuId).orElse(null);
    }

    public List<Page> getPageList() {
        return pageRepository.findAllBy();
    }
}
