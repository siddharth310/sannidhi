package com.naamalu.app.domain.repository

import com.naamalu.app.domain.model.Deity
import com.naamalu.app.domain.model.Naamavali
import com.naamalu.app.domain.model.Naama
import kotlinx.coroutines.flow.Flow

interface ContentRepository {
    fun observeDeities(): Flow<List<Deity>>

    suspend fun getDeity(deityId: String): Deity?

    suspend fun getNaamavalisForDeity(deityId: String): List<Naamavali>

    suspend fun getNaamavali(naamavaliId: String): Naamavali?

    suspend fun getNaamasForNaamavali(naamavaliId: String): List<Naama>

    suspend fun getNaama(naamaId: String): Naama?
}
