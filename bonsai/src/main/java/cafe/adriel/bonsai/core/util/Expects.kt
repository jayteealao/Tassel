package cafe.adriel.bonsai.core.util

import java.util.UUID

internal val randomUUID: String
    get() = UUID.randomUUID().toString()
