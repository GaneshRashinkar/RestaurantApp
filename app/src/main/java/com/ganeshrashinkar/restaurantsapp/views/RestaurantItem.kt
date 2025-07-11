package com.ganeshrashinkar.restaurantsapp.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ganeshrashinkar.restaurantsapp.R
import com.ganeshrashinkar.restaurantsapp.api.Businesse
import com.ganeshrashinkar.restaurantsapp.api.Category
import com.ganeshrashinkar.restaurantsapp.api.Coordinates
import com.ganeshrashinkar.restaurantsapp.api.Location
import java.nio.file.WatchEvent


@Composable
fun RestaurantItem(data: Businesse) {
    Card(elevation = CardDefaults.cardElevation(15.dp),
        modifier = Modifier.padding(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(8.dp))
            AsyncImage(
                model = data.image_url,
                contentDescription = "",
                modifier = Modifier.size(60.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(2.dp))
            Column(modifier=Modifier.weight(1f)) {
                Text(data.name, fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    softWrap = true,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth()
                    )
                Text("${data.distance.toInt()} m ${data.location.address1}",
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth())
                if (data.is_closed) {
                    Text("Closed", color = Color.Red)
                } else {
                    Text("Currently Open", color = Color.Green)
                }
            }
            Spacer(Modifier.width(2.dp))
            Box(
                modifier = Modifier.size(60.dp).clip(CircleShape)
                    .background(color = colorResource(R.color.faintRed)),
                contentAlignment = Alignment.Center,
            ) {
                Text("${data.rating}", color = Color.White)
            }
            Spacer(Modifier.width(10.dp))
        }

    }

}

@Preview
@Composable
fun RestaurantItemPreview(){
    val location=Location("11 Madison Ave","","","New York","US",listOf("11 Madison Ave","New York, NY 10010"),"US","10010")
val data= Businesse(
    "eleven-madison-park-new-york",listOf(Category("newamerican","New American")), Coordinates(10.0,5.0),"(212) 889-0905",4062.929570044286,"nRO136GRieGtxz18uD61DA"
    ,"https://s3-media0.fl.yelpcdn.com/bphoto/N91ZB8f0d39UAJeqvb89DA/o.jpg",false,location,"Eleven Madison Park","+12128890905",4.3,2612,listOf(""),
    "https://www.yelp.com/biz/eleven-madison-park-new-york?adjust_creative=khrwb4-p4xx3WgGXR23LxA&utm_campaign=yelp_api_v3&utm_medium=api_v3_business_search&utm_source=khrwb4-p4xx3WgGXR23LxA"
)
    RestaurantItem(data)
}