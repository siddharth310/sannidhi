package com.smaranam.app.data.mapper

import com.smaranam.app.data.local.entity.RecitationEntity
import com.smaranam.app.domain.model.Recitation
import com.smaranam.app.domain.model.RecitationMode
import com.smaranam.app.domain.model.RecitationSource
import java.time.Instant

fun RecitationEntity.toDomain(): Recitation {
    return Recitation(
        id = id,
        userId = userId,
        deityId = deityId,
        naamavaliId = naamavaliId,
        mode = RecitationMode.valueOf(mode),
        count = count,
        naamaId = naamaId,
        startedAt = Instant.ofEpochMilli(startedAtEpochMillis),
        completedAt = Instant.ofEpochMilli(completedAtEpochMillis),
        source = RecitationSource.valueOf(source),
    )
}

fun Recitation.toEntity(): RecitationEntity {
    return RecitationEntity(
        id = id,
        userId = userId,
        deityId = deityId,
        naamavaliId = naamavaliId,
        mode = mode.name,
        count = count,
        naamaId = naamaId,
        startedAtEpochMillis = startedAt.toEpochMilli(),
        completedAtEpochMillis = completedAt.toEpochMilli(),
        source = source.name,
    )
}
