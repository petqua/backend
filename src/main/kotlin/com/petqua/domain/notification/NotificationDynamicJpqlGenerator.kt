package com.petqua.domain.notification

import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.dsl.jpql.JpqlDsl
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicate

class NotificationDynamicJpqlGenerator : Jpql() {
    companion object Constructor : JpqlDsl.Constructor<NotificationDynamicJpqlGenerator> {
        override fun newInstance(): NotificationDynamicJpqlGenerator = NotificationDynamicJpqlGenerator()
    }

    fun Jpql.notificationIdLt(lastViewedId: Long?): Predicate? {
        return lastViewedId?.let { path(Notification::id).lt(it) }
    }
}

