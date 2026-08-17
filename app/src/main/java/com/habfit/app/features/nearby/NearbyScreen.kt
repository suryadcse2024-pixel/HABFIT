package com.habfit.app.features.nearby

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.habfit.app.ui.theme.Background
import com.habfit.app.ui.theme.PrimaryText
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun NearbyScreen() {
    val singapore = GeoPoint(1.35, 103.87)

    Scaffold(
        containerColor = Background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        controller.setZoom(12.0)
                        controller.setCenter(singapore)
                        
                        val marker = Marker(this)
                        marker.position = singapore
                        marker.title = "PowerFit Gym"
                        marker.snippet = "1.2 km away"
                        marker.icon = ctx.getDrawable(org.osmdroid.library.R.drawable.marker_default)
                        overlays.add(marker)
                    }
                }
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(24.dp)
            ) {
                Text(
                    text = "FITNESS AROUND YOU",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
            }
        }
    }
}
