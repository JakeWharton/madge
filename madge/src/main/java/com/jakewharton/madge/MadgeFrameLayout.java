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

  public void setOverlayEnabled(boolean enabled) {
    if (enabled != this.enabled) {
      if (Build.VERSION.SDK_INT >= HONEYCOMB) {
        layerize(enabled);
      }
      this.enabled = enabled;
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

  public boolean isEnabled() {
    return enabled;
  }

  @Override protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
    if (!enabled) {
      return super.drawChild(canvas, child, drawingTime);
    }
    try {
      canvasDelegate.delegate = canvas;
      return super.drawChild(canvasDelegate, child, drawingTime);
    } finally {
      canvasDelegate.delegate = null;
    }
  }

  @Override public boolean isHardwareAccelerated() {
    return !enabled;
  }
}
