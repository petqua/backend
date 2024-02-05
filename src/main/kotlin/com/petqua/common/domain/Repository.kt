package com.petqua.common.domain

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.findByIdOrNull

inline fun <reified T, ID> CrudRepository<T, ID>.findByIdOrThrow(
    id: ID, e: Exception = IllegalArgumentException("${T::class.java.name} entity 를 찾을 수 없습니다. id=$id")
): T = findByIdOrNull(id) ?: throw e


inline fun <reified T, ID> CrudRepository<T, ID>.existByIdOrThrow(
    id: ID,
    e: Exception = IllegalArgumentException("${T::class.java.name} entity 를 찾을 수 없습니다. id=$id")
): Unit = if (!existsById(id!!)) throw e else Unit

inline fun <reified T, ID> CrudRepository<T, ID>.findActiveByIdOrThrow(
    id: ID, e: Exception = IllegalArgumentException("${T::class.java.name} entity 를 찾을 수 없습니다. id=$id")
): T = findByIdOrNull(id)?.apply {
    if (this is SoftDeleteEntity) validateDeleted()
} ?: throw e


inline fun <reified T, ID> CrudRepository<T, ID>.existActiveByIdOrThrow(
    id: ID,
    e: Exception = IllegalArgumentException("${T::class.java.name} entity 를 찾을 수 없습니다. id=$id")
) {
    findByIdOrNull(id)?.apply {
        if (this is SoftDeleteEntity) validateDeleted()
    } ?: throw e
}
