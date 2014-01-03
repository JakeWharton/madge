package com.jakewharton.madge;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
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

  private final Matrix delegateMatrix = new Matrix();
  private final float[] delegateMatrixValues = new float[9];
  private final Paint scaleValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

  private Bitmap grid;

  private boolean drawScaleValueEnabled;

  public MadgeCanvas(Context context) {
    super(false);
    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    size = Math.max(displayMetrics.widthPixels, displayMetrics.heightPixels);
    setColor(DEFAULT_COLOR);
    scaleValuePaint.setTextAlign(Paint.Align.CENTER);
  }

  public void clearCache() {
    cache.clear();
  }

  public void setDrawScaleValueEnabled(boolean drawScaleValueEnabled) {
    this.drawScaleValueEnabled = drawScaleValueEnabled;
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

    // inverse grid color
    scaleValuePaint.setColor(~color | 0xFF000000);
  }

  @SuppressWarnings("deprecation")
  private void drawScaleValue(Bitmap bitmap, float inputScaleX, float inputScaleY, int offsetX,
      int offsetY) {
    // Note: although this method is deprecated,
    //       it seems to work well when hardware acceleration is not used
    getMatrix(delegateMatrix);
    float[] matrix = delegateMatrixValues;
    delegateMatrix.getValues(matrix);

    // 1. canvas matrix
    float scaleX = (float) Math.hypot(matrix[Matrix.MSCALE_X], matrix[Matrix.MSKEW_Y]);
    float scaleY = (float) Math.hypot(matrix[Matrix.MSCALE_Y], matrix[Matrix.MSKEW_X]);

    // 2. user input
    scaleX *= inputScaleX;
    scaleY *= inputScaleY;

    // 3. canvas/bitmap densities ratio
    int canvasDensity = getDensity();
    int bitmapDensity = bitmap.getDensity();
    if (canvasDensity != Bitmap.DENSITY_NONE && bitmapDensity != Bitmap.DENSITY_NONE) {
      float densityFactor = (float) canvasDensity / bitmapDensity;
      scaleX *= densityFactor;
      scaleY *= densityFactor;
    }

    final float precision = 100f;
    scaleX = (int) (scaleX * precision) / precision;
    scaleY = (int) (scaleY * precision) / precision;

    String text = Math.abs(scaleX - scaleY) < 1 / precision //
        ? String.valueOf(scaleX) //
        : scaleX + " x " + scaleY;

    drawText(text, bitmap.getWidth() / 2 * inputScaleX + offsetX,
        bitmap.getHeight() / 2 * inputScaleY + offsetY, scaleValuePaint);
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
    if (drawScaleValueEnabled) {
      drawScaleValue(bitmap, 1, 1, 0, 0);
    }
  }

  @Override public void drawBitmap(Bitmap bitmap, Rect src, RectF dst, Paint paint) {
    super.drawBitmap(overlayPixels(bitmap), src, dst, paint);
    if (drawScaleValueEnabled) {
      float srcWidth = src != null ? src.width() : bitmap.getWidth();
      float srcHeight = src != null ? src.height() : bitmap.getHeight();
      float dstWidth = dst != null ? dst.width() : bitmap.getWidth();
      float dstHeight = dst != null ? dst.height() : bitmap.getHeight();
      int offsetX = dst != null ? (int) dst.left : 0;
      int offsetY = dst != null ? (int) dst.top : 0;
      drawScaleValue(bitmap, dstWidth / srcWidth, dstHeight / srcHeight, offsetX, offsetY);
    }
  }

  @Override public void drawBitmap(Bitmap bitmap, Rect src, Rect dst, Paint paint) {
    super.drawBitmap(overlayPixels(bitmap), src, dst, paint);
    if (drawScaleValueEnabled) {
      float srcWidth = src != null ? src.width() : bitmap.getWidth();
      float srcHeight = src != null ? src.height() : bitmap.getHeight();
      float dstWidth = dst != null ? dst.width() : bitmap.getWidth();
      float dstHeight = dst != null ? dst.height() : bitmap.getHeight();
      int offsetX = dst != null ? dst.left : 0;
      int offsetY = dst != null ? dst.top : 0;
      drawScaleValue(bitmap, dstWidth / srcWidth, dstHeight / srcHeight, offsetX, offsetY);
    }
  }

  @Override
  public void drawBitmap(int[] colors, int offset, int stride, float x, float y, int width,
      int height, boolean hasAlpha, Paint paint) {
    super.drawBitmap(colors, offset, stride, x, y, width, height, hasAlpha, paint);
  }

  @Override
  public void drawBitmap(int[] colors, int offset, int stride, int x, int y, int width, int height,
      boolean hasAlpha, Paint paint) {
    super.drawBitmap(colors, offset, stride, x, y, width, height, hasAlpha, paint);
  }
}
