package org.telegram.ui.Components;

import android.view.View;
import android.view.ViewGroup;
import android.animation.ValueAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;

public class MotionJellyEngine {
    private float translationY = 0;
    private float velocityY = 0;
    private final float stiffness = 180f;
    private final float damping = 0.45f;
    private final float timeStep = 0.016f;

    public void updateJellyPhysics(float deltaY, ViewGroup container) {
        float force = -deltaY * 0.35f;
        velocityY += force;
        float springForce = -stiffness * translationY;
        float dampingForce = -damping * velocityY;
        velocityY += springForce + dampingForce;
        translationY += velocityY * timeStep;

        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            child.setTranslationY(translationY);
            float stretch = 1.0f + Math.abs(translationY) * 0.0005f;
            child.setScaleY(stretch);
        }
        container.invalidate();
    }

    public static void animateTextAppearance(final View textView) {
        textView.setAlpha(0f);
        textView.setTranslationY(20f);
        textView.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(400)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .start();
    }
}
