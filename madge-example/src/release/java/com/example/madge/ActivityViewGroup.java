package com.example.madge;

import android.app.Activity;
import android.view.ViewGroup;

public final class ActivityViewGroup {
  public static ViewGroup get(Activity activity) {
    return (ViewGroup) activity.findViewById(android.R.id.content);
  }

  private ActivityViewGroup() {
    throw new AssertionError("No instances.");
  }
}
