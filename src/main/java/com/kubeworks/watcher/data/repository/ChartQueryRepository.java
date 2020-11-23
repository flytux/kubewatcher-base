package com.kubeworks.watcher.data.repository;

import com.kubeworks.watcher.data.entity.ChartQuery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChartQueryRepository extends JpaRepository<ChartQuery, Long>{

    //ChartQuery findById(long cQueryId);

}
