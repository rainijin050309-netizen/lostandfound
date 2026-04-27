package com.lostandfound.lostandfound.stats.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface StatsMapper {

    @Select("""
            SELECT
                IFNULL(category, '未分类') AS category,
                SUM(CASE WHEN type = 'lost' THEN 1 ELSE 0 END) AS lost,
                SUM(CASE WHEN type = 'found' THEN 1 ELSE 0 END) AS found,
                COUNT(*) AS total
            FROM item
            GROUP BY IFNULL(category, '未分类')
            ORDER BY total DESC
            """)
    List<Map<String, Object>> selectCategoryStats();

    @Select("""
            SELECT
                IFNULL(location, '未知地点') AS location,
                COUNT(*) AS count
            FROM item
            GROUP BY IFNULL(location, '未知地点')
            ORDER BY count DESC
            LIMIT 10
            """)
    List<Map<String, Object>> selectLocationStats();

    @Select("""
            SELECT
                DATE_FORMAT(COALESCE(lostDate, foundDate), '%Y-%m') AS month,
                SUM(CASE WHEN type = 'lost' THEN 1 ELSE 0 END) AS lost,
                SUM(CASE WHEN type = 'found' THEN 1 ELSE 0 END) AS found,
                COUNT(*) AS total
            FROM item
            WHERE COALESCE(lostDate, foundDate) IS NOT NULL
            GROUP BY DATE_FORMAT(COALESCE(lostDate, foundDate), '%Y-%m')
            ORDER BY month
            """)
    List<Map<String, Object>> selectMonthlyStats();

    @Select("""
            SELECT
                (SELECT COUNT(*) FROM item WHERE type = 'lost') AS total_lost_items,
                (SELECT COUNT(*) FROM item WHERE type = 'found') AS total_found_items,
                (SELECT COUNT(*) FROM item WHERE type = 'lost') AS total_lost_reports,
                (SELECT COUNT(*) FROM item WHERE type = 'found') AS total_found_reports,
                (SELECT COUNT(DISTINCT itemId) FROM claim WHERE status = 'approved') AS total_matches,
                (SELECT COUNT(*) FROM claim) AS total_claims,
                (SELECT COUNT(*) FROM claim WHERE status = 'approved') AS approved_claims,
                (SELECT COUNT(*) FROM claim WHERE status = 'pending') AS pending_claims,
                (SELECT COUNT(*) FROM claim WHERE status = 'rejected') AS rejected_claims
            """)
    Map<String, Object> selectOverviewStats();
}
