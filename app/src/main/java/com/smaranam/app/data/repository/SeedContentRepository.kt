package com.smaranam.app.data.repository

import com.smaranam.app.data.content.SeedContent
import com.smaranam.app.domain.model.Deity
import com.smaranam.app.domain.model.Naama
import com.smaranam.app.domain.model.Naamavali
import com.smaranam.app.domain.model.SeedDeities
import com.smaranam.app.domain.repository.ContentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class SeedContentRepository : ContentRepository {
    private val deities = listOf(
        SeedDeities.ganesha,
        SeedDeities.lakshmi,
        SeedDeities.venkateswara,
    )

    override fun observeDeities(): Flow<List<Deity>> = flowOf(deities)

    override suspend fun getDeity(deityId: String): Deity? {
        return deities.find { it.id == deityId }
    }

    override suspend fun getNaamavalisForDeity(deityId: String): List<Naamavali> {
        return SeedContent.allNaamavalis.filter { it.deityId == deityId }
    }

    override suspend fun getNaamavali(naamavaliId: String): Naamavali? {
        return SeedContent.allNaamavalis.find { it.id == naamavaliId }
    }

    override suspend fun getNaamasForNaamavali(naamavaliId: String): List<Naama> {
        return SeedContent.allNaamas.filter { it.naamavaliId == naamavaliId }
    }

    override suspend fun getNaama(naamaId: String): Naama? {
        return SeedContent.allNaamas.find { it.id == naamaId }
    }
}
