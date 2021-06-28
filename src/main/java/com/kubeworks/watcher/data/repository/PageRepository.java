package com.kubeworks.watcher.data.repository;

import com.kubeworks.watcher.data.entity.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PageRepository extends JpaRepository<Page, Long> {

    Optional<Page> findByMenuId(long menuId);
    List<Page> findAllBy();
    List<Page> findAllBy(Sort sort);
}
