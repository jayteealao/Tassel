package xyz.graphitenerd.tassel.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import xyz.graphitenerd.tassel.model.SmartCollection
import xyz.graphitenerd.tassel.model.SmartCollectionWithCount

@Composable
fun SmartCollectionsSection(
    collections: List<SmartCollectionWithCount>,
    onCollectionClick: (SmartCollection) -> Unit,
    modifier: Modifier = Modifier,
    compactMode: Boolean = true
) {
    if (collections.isEmpty()) return
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "Collections",
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 0.dp, vertical = 8.dp)
        )

        if (compactMode) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 0.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(collections) { collectionWithCount ->
                    CompactSmartCollectionCard(
                        collectionWithCount = collectionWithCount,
                        onClick = onCollectionClick
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.padding(horizontal = 0.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                collections.forEach { collectionWithCount ->
                    SmartCollectionCard(
                        collectionWithCount = collectionWithCount,
                        onClick = onCollectionClick
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}
