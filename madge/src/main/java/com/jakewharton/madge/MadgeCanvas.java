package com.jakewharton.madge;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import java.util.Map;
import java.util.WeakHashMap;

/** A {@link Canvas} which overlays a colored pixel grid on any {@link Bitmap} drawn. */
final class MadgeCanvas extends DelegateCanvas {
  private static final int DEFAULT_COLOR = 0x88FF0088;

  private final Map<Bitmap, Bitmap> cache = new WeakHashMap<>();
  private final int size;

  private Bitmap grid;

  public MadgeCanvas(Context context) {
    super(false);
    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    size = Math.max(displayMetrics.widthPixels, displayMetrics.heightPixels);
    setColor(DEFAULT_COLOR);
  }

  public void clearCache() {
    cache.clear();
  }

  public void setColor(int color) {
    if (grid != null) {
      grid.recycle();
    }

    grid = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        if (i % 2 == j % 2) {
          grid.setPixel(i, j, color);
        }
      }
    }

    clearCache();
  }

  private Bitmap overlayPixels(Bitmap bitmap) {
    Bitmap replacement = cache.get(bitmap);
    if (replacement != null) {
      return replacement;
    }
    int height = bitmap.getHeight();
    int width = bitmap.getWidth();
    replacement = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(replacement);
    canvas.drawBitmap(bitmap, 0, 0, null);
    canvas.drawBitmap(grid, 0, 0, null);
    cache.put(bitmap, replacement);
    return replacement;
  }

  @Override public void drawBitmap(Bitmap bitmap, float left, float top, Paint paint) {
    super.drawBitmap(overlayPixels(bitmap), left, top, paint);
  }

  @Override public void drawBitmap(Bitmap bitmap, Rect src, RectF dst, Paint paint) {
    super.drawBitmap(overlayPixels(bitmap), src, dst, paint);
  }

  @Override public void drawBitmap(Bitmap bitmap, Rect src, Rect dst, Paint paint) {
    super.drawBitmap(overlayPixels(bitmap), src, dst, paint);
  }

  @Override
  public void drawBitmap(int[] colors, int offset, int stride, float x, float y, int width,
      int height, boolean hasAlpha, Paint paint) {
    super.drawBitmap(colors, offset, stride, x, y, width, height, hasAlpha, paint);
  }

  @Override
  public void drawBitmap(int[] colors, int offset, int stride, int x, int y, int width,
      int height, boolean hasAlpha, Paint paint) {
    super.drawBitmap(colors, offset, stride, x, y, width, height, hasAlpha, paint);
  }
}
