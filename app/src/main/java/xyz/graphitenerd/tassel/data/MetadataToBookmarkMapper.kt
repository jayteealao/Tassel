package xyz.graphitenerd.tassel.data

import com.raqun.beaverlib.data.local.DbEntityMapper
import com.raqun.beaverlib.model.MetaData
import xyz.graphitenerd.tassel.model.Bookmark

class MetadataToBookmarkMapper : DbEntityMapper<Bookmark, MetaData> {
    override fun map(entity: Bookmark): MetaData {
        return MetaData(
            entity.rawUrl,
            entity.url,
            entity.title,
            entity.desc,
            entity.imageUrl,
            entity.name,
            entity.mediaType,
            entity.favIcon
        )
    }

    override fun map(domainObject: MetaData): Bookmark {
        return Bookmark(
            rawUrl = domainObject.rawUrl,
            url = domainObject.url,
            title = domainObject.title,
            desc = domainObject.desc,
            imageUrl = domainObject.imageUrl,
            name = domainObject.name,
            mediaType = domainObject.mediaType,
            favIcon = domainObject.favIcon,
        )
    }
}
