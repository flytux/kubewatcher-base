package com.kubeworks.watcher.data.mapper

import com.kubeworks.watcher.data.vo.ClusterPodUsage
import com.kubeworks.watcher.data.vo.UsageMetricType
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select

import java.time.LocalDateTime

@Mapper
interface ClusterPodUsageMapper {

    @Select('''
        SELECT application, namespace, pod_count, max_cpu, avg_cpu, max_memory, avg_memory, create_time
          FROM `cluster_pod_usage`
    ''')
    List<ClusterPodUsage> selectAll()

    @Insert('''
        <script>
            INSERT INTO `cluster_pod_usage`
                <trim prefix="(" suffix=")" suffixOverrides=",">
                    <if test="application != null">
                        `application`,
                    </if>
                    <if test="namespace != null">
                        `namespace`,
                    </if>
                    <if test="podCount != null">
                        `pod_count`,
                    </if>
                    <if test="maxCpu != null">
                        `max_cpu`,
                    </if>
                    <if test="avgCpu != null">
                        `avg_cpu`,
                    </if>
                    <if test="maxMemory != null">
                        `max_memory`,
                    </if>
                    <if test="avgMemory != null">
                        `avg_memory`,
                    </if>
                        `create_time`,
                </trim>
            VALUES
                <trim prefix="(" suffix=")" suffixOverrides=",">
                    <if test="application != null">
                        #{application},
                    </if>
                    <if test="namespace != null">
                        #{namespace},
                    </if>
                    <if test="podCount != null">
                        #{podCount},
                    </if>
                    <if test="maxCpu != null">
                        #{maxCpuNumber},
                    </if>
                    <if test="avgCpu != null">
                        #{avgCpuNumber},
                    </if>
                    <if test="maxMemory != null">
                        #{maxMemoryNumber},
                    </if>
                    <if test="avgMemory != null">
                        #{avgMemoryNumber},
                    </if>
                    <choose>
                        <when test="createTime != null">
                            #{createTime},
                        </when>
                        <otherwise>
                            now(),
                        </otherwise>
                    </choose>
                </trim>
        </script>
    ''')
    int insert(ClusterPodUsage clusterPodUsage)


    @Insert('''
        <script>
            INSERT INTO `cluster_pod_usage`
                (
                        `application`,
                        `namespace`,
                        `pod_count`,
                        `max_cpu`,
                        `avg_cpu`,
                        `max_memory`,
                        `avg_memory`,
                        `create_time`
                )
            VALUES
                <foreach item="item" separator="," collection="list">
                    (
                        #{item.application},
                        #{item.namespace},
                        #{item.podCount},
                        #{item.maxCpuNumber},
                        #{item.avgCpuNumber},
                        #{item.maxMemoryNumber},
                        #{item.avgMemoryNumber},
                        #{item.createTime}
                     )
                </foreach>
        </script>
    ''')
    int inserts(List<ClusterPodUsage> clusterPodUsages)

    @Select('''
        <script>
            SELECT
              application, namespace, 
              max(pod_count) as max_pod_count, avg(pod_count) as pod_count,
              max(max_cpu) as max_cpu, round(avg(avg_cpu), 9) as avg_cpu, 
              max(max_memory) as max_memory, round(avg(avg_memory), 0) as avg_memory
            FROM `cluster_pod_usage`
            WHERE
            <trim suffixOverrides="AND">
                1=1 AND
                <if test="@org.apache.commons.lang3.StringUtils@isNotEmpty(namespace)" >
                    namespace = #{namespace} AND
                </if>
                <if test="start != null and end != null" >
                    `create_time` BETWEEN #{start} AND #{end} 
                </if>
            </trim>
            GROUP BY namespace, application
            ORDER BY namespace, application
        </script>
    ''')
    List<ClusterPodUsage> selectSummaryByNamespacePeriod(@Param("namespace") String namespace,
                                                         @Param("start") LocalDateTime start,
                                                         @Param("end") LocalDateTime end)

    @Select('''
        <script>
            SELECT
              application, namespace,
              <choose>
                <when test="@org.apache.commons.lang3.StringUtils@equalsIgnoreCase(usageMetricType.name(), 'POD')">
                    max(pod_count) as max_pod_count, avg(pod_count) as pod_count,
                </when>
                <when test="@org.apache.commons.lang3.StringUtils@equalsIgnoreCase(usageMetricType.name(), 'CPU')">
                    max(max_cpu) as max_cpu, round(avg(avg_cpu), 9) as avg_cpu,
                </when>
                <when test="@org.apache.commons.lang3.StringUtils@equalsIgnoreCase(usageMetricType.name(), 'MEMORY')">
                    max(max_memory) as max_memory, round(avg(avg_memory), 0) as avg_memory,
                </when>
                <otherwise>
                    max(pod_count) as max_pod_count, avg(pod_count) as pod_count,          
                    max(max_cpu) as max_cpu, round(avg(avg_cpu), 9) as avg_cpu,            
                    max(max_memory) as max_memory, round(avg(avg_memory), 0) as avg_memory,
                </otherwise>
              </choose>
 
              ${groupByFormat} as create_time 
            FROM `cluster_pod_usage`
            WHERE
            <trim suffixOverrides="AND">
                1=1 AND
                <if test="@org.apache.commons.lang3.StringUtils@isNotEmpty(namespace)" >
                    namespace = #{namespace} AND
                </if>
                <if test="@org.apache.commons.lang3.StringUtils@isNotEmpty(application)" >
                    application = #{application} AND
                </if>
                <if test="start != null and end != null" >
                    `create_time` BETWEEN #{start} AND #{end} 
                </if>
            </trim>

            GROUP BY namespace, application, ${groupByFormat} 
            ORDER BY create_time, namespace, application
        </script> 
    ''')
    List<ClusterPodUsage> selectStatisticsSummaryByPeriodGroup(@Param("namespace") String namespace, @Param("application") String application,
                                                               @Param("usageMetricType") UsageMetricType usageMetricType, @Param("groupByFormat") String groupByFormat,
                                                               @Param("start") LocalDateTime start, @Param("end") LocalDateTime end)
}