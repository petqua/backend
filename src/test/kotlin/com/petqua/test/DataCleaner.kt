package com.petqua.test

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import javax.sql.DataSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.CacheManager
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

private const val FIRST_COLUMN = 1
private const val SHOW_TABLES_QUERY = "SHOW TABLES"
private const val TRUNCATE_QUERY = "TRUNCATE TABLE "

@Component
class DataCleaner(
    @Autowired
    val dataSource: DataSource,

    @PersistenceContext
    val entityManager: EntityManager,

    val truncateQueries: MutableList<String> = mutableListOf(),

    @Autowired
    val cacheManager: CacheManager,

    @Autowired
    val redisTemplate: RedisTemplate<String, Any>,
) {

    @Transactional
    fun clean() {
        if (truncateQueries.isEmpty()) {
            initialize()
        }
        truncateAllTables()
        clearCache()
        clearRedis()
    }

    private fun initialize() {
        dataSource.connection.createStatement().use { statement ->
            val resultSet = statement.executeQuery(SHOW_TABLES_QUERY)

            while (resultSet.next()) {
                val tableName = resultSet.getString(FIRST_COLUMN)
                truncateQueries.add("$TRUNCATE_QUERY $tableName")
            }
        }
    }

    private fun truncateAllTables() {
        truncateQueries.forEach { entityManager.createNativeQuery(it).executeUpdate() }
    }

    private fun clearCache() {
        cacheManager.cacheNames.forEach { cacheManager.getCache(it)?.clear() }
    }

    private fun clearRedis() {
        redisTemplate.keys("*").forEach { redisTemplate.delete(it) }
    }
}
