package nodomain.aditya1875more.stashly.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import nodomain.aditya1875more.stashly.R

@Composable
fun LottieAnimationExample(modifier: Modifier = Modifier, resId: Int = R.raw.animation) {
    // Load Lottie composition
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(resId))

    // Animate the composition infinitely
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

  Box (
      contentAlignment = Alignment.BottomCenter
  ){
      LottieAnimation(
          composition = composition,
          progress = progress,
          modifier = Modifier.then(modifier)
      )
  }
}