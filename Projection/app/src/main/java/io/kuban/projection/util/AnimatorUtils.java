package io.kuban.projection.util;

import android.content.Context;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.OnCompositionLoadedListener;

/**
 * Created by wangxuan on 17/11/22.
 */

public class AnimatorUtils {


    public static void loading(Context context, final LottieAnimationView mAnimationView, String fileName, final boolean isLoop) {
        LottieComposition.Factory.fromAssetFileName(context, fileName, new OnCompositionLoadedListener() {
            @Override
            public void onCompositionLoaded(LottieComposition composition) {
                mAnimationView.setComposition(composition);
                mAnimationView.playAnimation();
                mAnimationView.loop(isLoop);
            }
        });
    }

    public static void startAnima(LottieAnimationView mAnimationView) {
        boolean inPlaying = mAnimationView.isAnimating();
        if (!inPlaying) {
            mAnimationView.setProgress(0f);
            mAnimationView.playAnimation();
        }
    }

    public static void stopAnima(LottieAnimationView mAnimationView) {
        boolean inPlaying = mAnimationView.isAnimating();
        if (inPlaying) {
            mAnimationView.cancelAnimation();
        }
    }
}
