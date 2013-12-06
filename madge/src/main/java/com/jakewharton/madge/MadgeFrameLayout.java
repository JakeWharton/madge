package com.jakewharton.madge;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import static android.os.Build.VERSION_CODES.HONEYCOMB;

public final class MadgeFrameLayout extends FrameLayout {
  private final MadgeCanvas canvasDelegate;
  private boolean enabled;

  public MadgeFrameLayout(Context context) {
    super(context);
    canvasDelegate = new MadgeCanvas(context);
  }

  public MadgeFrameLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    canvasDelegate = new MadgeCanvas(context);
  }

  public MadgeFrameLayout(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    canvasDelegate = new MadgeCanvas(context);
  }

  public void setOverlayColor(int color) {
    canvasDelegate.setColor(color);
  }

  public void setOverlayDrawRatioEnabled(boolean drawRatioEnabled) {
    canvasDelegate.setDrawScaleValueEnabled(drawRatioEnabled);
  }

  public boolean isOverlayEnabled() {
    return enabled;
  }

  public void setOverlayEnabled(boolean enabled) {
    if (enabled != this.enabled) {
      if (Build.VERSION.SDK_INT >= HONEYCOMB) {
        layerize(enabled);
      }
      this.enabled = enabled;

      if (!enabled) {
        canvasDelegate.clearCache();
      }

      invalidate();
    }
  }

  @TargetApi(HONEYCOMB)
  private void layerize(boolean enabled) {
    if (enabled) {
      setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    } else {
      setLayerType(View.LAYER_TYPE_NONE, null);
    }
  }

  @Override protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
    if (!enabled) {
      return super.drawChild(canvas, child, drawingTime);
    }
    MadgeCanvas delegate = canvasDelegate;
    try {
      delegate.setDelegate(canvas);
      return super.drawChild(delegate, child, drawingTime);
    } finally {
      delegate.clearDelegate();
    }
  }

  @Override public boolean isHardwareAccelerated() {
    return !enabled && super.isHardwareAccelerated();
  }
}
