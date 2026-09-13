package com.smaranam.app.di

import android.content.Context
import com.smaranam.app.data.local.SmaranamDatabase
import com.smaranam.app.data.local.UserSessionStore
import com.smaranam.app.data.repository.RecitationRepositoryImpl
import com.smaranam.app.data.repository.SeedContentRepository
import com.smaranam.app.data.repository.UserSessionRepositoryImpl
import com.smaranam.app.domain.repository.ContentRepository
import com.smaranam.app.domain.repository.RecitationRepository
import com.smaranam.app.domain.repository.UserSessionRepository

class AppContainer(context: Context) {
    private val database = SmaranamDatabase.create(context)
    private val userSessionStore = UserSessionStore(context)

    val recitationRepository: RecitationRepository = RecitationRepositoryImpl(
        dao = database.recitationDao(),
    )

    val userSessionRepository: UserSessionRepository = UserSessionRepositoryImpl(
        store = userSessionStore,
    )

    val contentRepository: ContentRepository = SeedContentRepository()
}
