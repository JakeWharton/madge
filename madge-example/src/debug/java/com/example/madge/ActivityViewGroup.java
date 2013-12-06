package com.example.madge;

import android.app.Activity;
import android.view.ViewGroup;
import com.jakewharton.madge.MadgeFrameLayout;

public final class ActivityViewGroup {
  public static ViewGroup get(Activity activity) {
    MadgeFrameLayout madge = new MadgeFrameLayout(activity);
    madge.setOverlayEnabled(true);
    activity.setContentView(madge);
    return madge;
  }

  private ActivityViewGroup() {
    throw new AssertionError("No instances.");
  }
}
