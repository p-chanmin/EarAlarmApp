package com.dev.core.admob.banner

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.dev.core.admob.BuildConfig
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun BannersAds(
    modifier: Modifier = Modifier,
    adRequest: AdRequest,
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.FULL_BANNER)
                adUnitId = BuildConfig.ADMOB_BANNER_ID
            }
        },
        update = { adView ->
            adView.loadAd(adRequest)
        }
    )
}