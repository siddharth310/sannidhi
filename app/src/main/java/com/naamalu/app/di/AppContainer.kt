package com.naamalu.app.di

import android.content.Context
import com.naamalu.app.data.local.NaamaluDatabase
import com.naamalu.app.data.local.UserSessionStore
import com.naamalu.app.data.repository.RecitationRepositoryImpl
import com.naamalu.app.data.repository.SeedContentRepository
import com.naamalu.app.data.repository.UserSessionRepositoryImpl
import com.naamalu.app.domain.repository.ContentRepository
import com.naamalu.app.domain.repository.RecitationRepository
import com.naamalu.app.domain.repository.UserSessionRepository

class AppContainer(context: Context) {
    private val database = NaamaluDatabase.create(context)
    private val userSessionStore = UserSessionStore(context)

    val recitationRepository: RecitationRepository = RecitationRepositoryImpl(
        dao = database.recitationDao(),
    )

    val userSessionRepository: UserSessionRepository = UserSessionRepositoryImpl(
        store = userSessionStore,
    )

    val contentRepository: ContentRepository = SeedContentRepository()
}
