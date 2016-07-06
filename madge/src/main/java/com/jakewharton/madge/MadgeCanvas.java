package com.jakewharton.madge;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.WeakHashMap;

import static android.graphics.Bitmap.Config.ALPHA_8;
import static android.graphics.Bitmap.Config.ARGB_8888;
import static android.graphics.Paint.ANTI_ALIAS_FLAG;
import static android.graphics.Paint.Align.CENTER;
import static android.graphics.Paint.Style.FILL;
import static android.graphics.Paint.Style.STROKE;
import static android.graphics.Shader.TileMode.REPEAT;

/** A {@link Canvas} which overlays a colored pixel grid on any {@link Bitmap} drawn. */
final class MadgeCanvas extends DelegateCanvas {
  private static final int DEFAULT_COLOR = 0x88FF0088;
  private static final int TEXT_SIZE_DP = 14;

  private final Map<Bitmap, Bitmap> cache = new WeakHashMap<>();

  private final Matrix delegateMatrix = new Matrix();
  private final float[] delegateMatrixValues = new float[9];
  private final Paint checkerboardPaint = new Paint();
  private final Paint scaleValuePaintFill = new Paint(ANTI_ALIAS_FLAG);
  private final Paint scaleValuePaintStroke = new Paint(ANTI_ALIAS_FLAG);
  private final float scaleValueOffset;

  private boolean overlayRatioEnabled;

  public MadgeCanvas(Context context) {
    super(false);
    setColor(DEFAULT_COLOR);

    // Create a 2x2 checkerboard alpha pattern in a shader.
    ByteBuffer buffer = ByteBuffer.allocateDirect(4);
    buffer.put(0, (byte) 0xFF);
    buffer.put(1, (byte) 0x00);
    buffer.put(2, (byte) 0x00);
    buffer.put(3, (byte) 0xFF);
    Bitmap checker = Bitmap.createBitmap(2, 2, ALPHA_8);
    checker.copyPixelsFromBuffer(buffer);
    BitmapShader shader = new BitmapShader(checker, REPEAT, REPEAT);
    checkerboardPaint.setShader(shader);

    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    float scaleValueTextSize = TEXT_SIZE_DP * displayMetrics.density;
    scaleValueOffset = scaleValueTextSize / 2;

    scaleValuePaintFill.setTextAlign(CENTER);
    scaleValuePaintFill.setStyle(FILL);
    scaleValuePaintFill.setTextSize(scaleValueTextSize);

    scaleValuePaintStroke.setTextAlign(CENTER);
    scaleValuePaintStroke.setStyle(STROKE);
    scaleValuePaintStroke.setStrokeWidth(scaleValueTextSize * 0.10f); // 10% stroke.
    scaleValuePaintStroke.setTextSize(scaleValueTextSize);
    scaleValuePaintStroke.setAlpha(0x66); // 40% opacity.
  }

  public void clearCache() {
    cache.clear();
  }

  public void setOverlayRatioEnabled(boolean overlayRatioEnabled) {
    this.overlayRatioEnabled = overlayRatioEnabled;
  }

  public boolean isOverlayRatioEnabled() {
    return overlayRatioEnabled;
  }

  public void setColor(int color) {
    clearCache();

    checkerboardPaint.setColor(color);
    scaleValuePaintStroke.setColor(color);

    float[] hsv = new float[3];
    Color.colorToHSV(color, hsv);
    hsv[0] += 210; // Move color to split complementary (180 + 30)...
    hsv[0] %= 360; // Keep it in the color space.
    scaleValuePaintFill.setColor(Color.HSVToColor(hsv));
  }

  public int getColor() {
    return checkerboardPaint.getColor();
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

    int save = save();

    scale(1f / scaleX, 1f / scaleY);
    drawText(text, scaleX * bitmap.getWidth() / 2 + offsetX,
        scaleY * bitmap.getHeight() / 2 + offsetY + scaleValueOffset, scaleValuePaintStroke);
    drawText(text, scaleX * bitmap.getWidth() / 2 + offsetX,
        scaleY * bitmap.getHeight() / 2 + offsetY + scaleValueOffset, scaleValuePaintFill);

    restoreToCount(save);
  }

  private Bitmap overlayPixels(Bitmap bitmap) {
    Bitmap replacement = cache.get(bitmap);
    if (replacement != null) {
      return replacement;
    }

    // Create the replacement bitmap of the same size.
    int height = bitmap.getHeight();
    int width = bitmap.getWidth();
    replacement = Bitmap.createBitmap(width, height, ARGB_8888);
    Canvas canvas = new Canvas(replacement);

    // Draw the original into the replacement.
    canvas.drawBitmap(bitmap, 0, 0, null);

    // Tile the grid over the entire size of the bitmap.
    canvas.drawRect(0, 0, width, height, checkerboardPaint);

    // Cache the replacement so subsequent frames do not have to pay the cost of creation.
    cache.put(bitmap, replacement);

    return replacement;
  }

  @Override public void drawBitmap(Bitmap bitmap, float left, float top, Paint paint) {
    super.drawBitmap(overlayPixels(bitmap), left, top, paint);
    if (overlayRatioEnabled) {
      drawScaleValue(bitmap, 1, 1, (int)left, (int)top);
    }
  }

  @Override public void drawBitmap(Bitmap bitmap, Rect src, RectF dst, Paint paint) {
    super.drawBitmap(overlayPixels(bitmap), src, dst, paint);
    if (overlayRatioEnabled) {
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
    if (overlayRatioEnabled) {
      float srcWidth = src != null ? src.width() : bitmap.getWidth();
      float srcHeight = src != null ? src.height() : bitmap.getHeight();
      float dstWidth = dst != null ? dst.width() : bitmap.getWidth();
      float dstHeight = dst != null ? dst.height() : bitmap.getHeight();
      int offsetX = dst != null ? dst.left : 0;
      int offsetY = dst != null ? dst.top : 0;
      drawScaleValue(bitmap, dstWidth / srcWidth, dstHeight / srcHeight, offsetX, offsetY);
    }
  }
}
