package xyz.graphitenerd.tassel.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.graphitenerd.tassel.model.SmartCollection
import xyz.graphitenerd.tassel.model.SmartCollectionWithCount

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SmartCollectionCard(
    collectionWithCount: SmartCollectionWithCount,
    onClick: (SmartCollection) -> Unit,
    modifier: Modifier = Modifier
) {
    val collection = collectionWithCount.collection
    val count = collectionWithCount.count

    Card(
        onClick = { onClick(collection) },
        modifier = modifier
            .height(44.dp),
        elevation = 0.dp,
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, Color.Black),
        backgroundColor = Color.White
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = collection.icon,
                contentDescription = collection.title,
                modifier = Modifier.size(20.dp),
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = collection.title,
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            if (count > 0) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "($count)",
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CompactSmartCollectionCard(
    collectionWithCount: SmartCollectionWithCount,
    onClick: (SmartCollection) -> Unit,
    modifier: Modifier = Modifier
) {
    val collection = collectionWithCount.collection
    val count = collectionWithCount.count

    Card(
        onClick = { onClick(collection) },
        modifier = modifier
            .height(40.dp),
        elevation = 0.dp,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color.Black),
        backgroundColor = Color.White
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = collection.icon,
                contentDescription = collection.title,
                modifier = Modifier.size(18.dp),
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = collection.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            if (count > 0) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "($count)",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
