package org.telegram.ui.Components;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

public class MotionJellyEngine {
    private float translationY = 0;
    private float velocityY = 0;
    private final float stiffness = 180f;
    private final float damping = 0.45f;
    private long lastTime = 0;

    public void updateJellyPhysics(float deltaY, ViewGroup container) {
        long currentTime = System.nanoTime();
        if (lastTime == 0) {
            lastTime = currentTime;
            return;
        }
        float timeStep = Math.min((currentTime - lastTime) / 1000000000f, 0.032f);
        lastTime = currentTime;

        float externalForce = -deltaY * 0.35f;
        float springForce = -stiffness * translationY;
        float dampingForce = -damping * velocityY;
        
        float acceleration = externalForce + springForce + dampingForce;
        
        velocityY += acceleration * timeStep;
        translationY += velocityY * timeStep;

        final int childCount = container.getChildCount();
        final float stretch = 1.0f + Math.abs(translationY) * 0.0005f;

        for (int i = 0; i < childCount; i++) {
            View child = container.getChildAt(i);
            if (child != null) {
                child.setTranslationY(translationY);
                child.setScaleY(stretch);
            }
        }
        container.invalidate();
    }

    public static void animateTextAppearance(final View textView) {
        if (textView == null) return;
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
