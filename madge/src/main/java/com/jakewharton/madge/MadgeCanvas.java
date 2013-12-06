package com.jakewharton.madge;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DrawFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.DisplayMetrics;
import java.util.Map;
import java.util.WeakHashMap;

final class MadgeCanvas extends Canvas {
  private static final int DEFAULT_COLOR = 0x88FF0088;

  private final Map<Bitmap, Bitmap> cache = new WeakHashMap<>();
  private final int size;

  private Bitmap grid;
  Canvas delegate;

  public MadgeCanvas(Context context) {
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

  @Override public boolean isHardwareAccelerated() {
    return false;
  }

  private Bitmap replace(Bitmap bitmap) {
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
    delegate.drawBitmap(replace(bitmap), left, top, paint);
  }

  @Override public void drawBitmap(Bitmap bitmap, Rect src, RectF dst, Paint paint) {
    delegate.drawBitmap(replace(bitmap), src, dst, paint);
  }

  @Override public void drawBitmap(Bitmap bitmap, Rect src, Rect dst, Paint paint) {
    delegate.drawBitmap(replace(bitmap), src, dst, paint);
  }

  @Override
  public void drawBitmap(int[] colors, int offset, int stride, float x, float y, int width,
      int height, boolean hasAlpha, Paint paint) {
    delegate.drawBitmap(colors, offset, stride, x, y, width, height, hasAlpha, paint);
  }

  @Override
  public void drawBitmap(int[] colors, int offset, int stride, int x, int y, int width,
      int height, boolean hasAlpha, Paint paint) {
    delegate.drawBitmap(colors, offset, stride, x, y, width, height, hasAlpha, paint);
  }

  @Override public void setBitmap(Bitmap bitmap) {
    delegate.setBitmap(bitmap);
  }

  @Override public boolean isOpaque() {
    return delegate.isOpaque();
  }

  @Override public int getWidth() {
    return delegate.getWidth();
  }

  @Override public int getHeight() {
    return delegate.getHeight();
  }

  @Override public int getDensity() {
    return delegate.getDensity();
  }

  @Override public void setDensity(int density) {
    delegate.setDensity(density);
  }

  @Override public int getMaximumBitmapWidth() {
    return delegate.getMaximumBitmapWidth();
  }

  @Override public int getMaximumBitmapHeight() {
    return delegate.getMaximumBitmapHeight();
  }

  @Override public int save() {
    return delegate.save();
  }

  @Override public int save(int saveFlags) {
    return delegate.save(saveFlags);
  }

  @Override public int saveLayer(RectF bounds, Paint paint, int saveFlags) {
    return delegate.saveLayer(bounds, paint, saveFlags);
  }

  @Override public int saveLayer(float left, float top, float right, float bottom, Paint paint,
      int saveFlags) {
    return delegate.saveLayer(left, top, right, bottom, paint, saveFlags);
  }

  @Override public int saveLayerAlpha(RectF bounds, int alpha, int saveFlags) {
    return delegate.saveLayerAlpha(bounds, alpha, saveFlags);
  }

  @Override public int saveLayerAlpha(float left, float top, float right, float bottom, int alpha,
      int saveFlags) {
    return delegate.saveLayerAlpha(left, top, right, bottom, alpha, saveFlags);
  }

  @Override public void restore() {
    delegate.restore();
  }

  @Override public int getSaveCount() {
    return delegate.getSaveCount();
  }

  @Override public void restoreToCount(int saveCount) {
    delegate.restoreToCount(saveCount);
  }

  @Override public void translate(float dx, float dy) {
    delegate.translate(dx, dy);
  }

  @Override public void scale(float sx, float sy) {
    delegate.scale(sx, sy);
  }

  @Override public void rotate(float degrees) {
    delegate.rotate(degrees);
  }

  @Override public void skew(float sx, float sy) {
    delegate.skew(sx, sy);
  }

  @Override public void concat(Matrix matrix) {
    delegate.concat(matrix);
  }

  @Override public void setMatrix(Matrix matrix) {
    delegate.setMatrix(matrix);
  }

  @SuppressWarnings("deprecation")
  @Override public void getMatrix(Matrix ctm) {
    delegate.getMatrix(ctm);
  }

  @Override public boolean clipRect(RectF rect, Region.Op op) {
    return delegate.clipRect(rect, op);
  }

  @Override public boolean clipRect(Rect rect, Region.Op op) {
    return delegate.clipRect(rect, op);
  }

  @Override public boolean clipRect(RectF rect) {
    return delegate.clipRect(rect);
  }

  @Override public boolean clipRect(Rect rect) {
    return delegate.clipRect(rect);
  }

  @Override
  public boolean clipRect(float left, float top, float right, float bottom, Region.Op op) {
    return delegate.clipRect(left, top, right, bottom, op);
  }

  @Override public boolean clipRect(float left, float top, float right, float bottom) {
    return delegate.clipRect(left, top, right, bottom);
  }

  @Override public boolean clipRect(int left, int top, int right, int bottom) {
    return delegate.clipRect(left, top, right, bottom);
  }

  @Override public boolean clipPath(Path path, Region.Op op) {
    return delegate.clipPath(path, op);
  }

  @Override public boolean clipPath(Path path) {
    return delegate.clipPath(path);
  }

  @Override public boolean clipRegion(Region region, Region.Op op) {
    return delegate.clipRegion(region, op);
  }

  @Override public boolean clipRegion(Region region) {
    return delegate.clipRegion(region);
  }

  @Override public DrawFilter getDrawFilter() {
    return delegate.getDrawFilter();
  }

  @Override public void setDrawFilter(DrawFilter filter) {
    delegate.setDrawFilter(filter);
  }

  @Override public boolean quickReject(RectF rect, EdgeType type) {
    return delegate.quickReject(rect, type);
  }

  @Override public boolean quickReject(Path path, EdgeType type) {
    return delegate.quickReject(path, type);
  }

  @Override
  public boolean quickReject(float left, float top, float right, float bottom, EdgeType type) {
    return delegate.quickReject(left, top, right, bottom, type);
  }

  @Override public boolean getClipBounds(Rect bounds) {
    return delegate.getClipBounds(bounds);
  }

  @Override public void drawRGB(int r, int g, int b) {
    delegate.drawRGB(r, g, b);
  }

  @Override public void drawARGB(int a, int r, int g, int b) {
    delegate.drawARGB(a, r, g, b);
  }

  @Override public void drawColor(int color) {
    delegate.drawColor(color);
  }

  @Override public void drawColor(int color, PorterDuff.Mode mode) {
    delegate.drawColor(color, mode);
  }

  @Override public void drawPaint(Paint paint) {
    delegate.drawPaint(paint);
  }

  @Override public void drawPoints(float[] pts, int offset, int count, Paint paint) {
    delegate.drawPoints(pts, offset, count, paint);
  }

  @Override public void drawPoints(float[] pts, Paint paint) {
    delegate.drawPoints(pts, paint);
  }

  @Override public void drawPoint(float x, float y, Paint paint) {
    delegate.drawPoint(x, y, paint);
  }

  @Override
  public void drawLine(float startX, float startY, float stopX, float stopY, Paint paint) {
    delegate.drawLine(startX, startY, stopX, stopY, paint);
  }

  @Override public void drawLines(float[] pts, int offset, int count, Paint paint) {
    delegate.drawLines(pts, offset, count, paint);
  }

  @Override public void drawLines(float[] pts, Paint paint) {
    delegate.drawLines(pts, paint);
  }

  @Override public void drawRect(RectF rect, Paint paint) {
    delegate.drawRect(rect, paint);
  }

  @Override public void drawRect(Rect r, Paint paint) {
    delegate.drawRect(r, paint);
  }

  @Override public void drawRect(float left, float top, float right, float bottom, Paint paint) {
    delegate.drawRect(left, top, right, bottom, paint);
  }

  @Override public void drawOval(RectF oval, Paint paint) {
    delegate.drawOval(oval, paint);
  }

  @Override public void drawCircle(float cx, float cy, float radius, Paint paint) {
    delegate.drawCircle(cx, cy, radius, paint);
  }

  @Override public void drawArc(RectF oval, float startAngle, float sweepAngle, boolean useCenter,
      Paint paint) {
    delegate.drawArc(oval, startAngle, sweepAngle, useCenter, paint);
  }

  @Override public void drawRoundRect(RectF rect, float rx, float ry, Paint paint) {
    delegate.drawRoundRect(rect, rx, ry, paint);
  }

  @Override public void drawPath(Path path, Paint paint) {
    delegate.drawPath(path, paint);
  }

  @Override public void drawBitmap(Bitmap bitmap, Matrix matrix, Paint paint) {
    delegate.drawBitmap(bitmap, matrix, paint);
  }

  @Override
  public void drawBitmapMesh(Bitmap bitmap, int meshWidth, int meshHeight, float[] verts,
      int vertOffset, int[] colors, int colorOffset, Paint paint) {
    delegate.drawBitmapMesh(bitmap, meshWidth, meshHeight, verts, vertOffset, colors, colorOffset,
        paint);
  }

  @Override
  public void drawVertices(VertexMode mode, int vertexCount, float[] verts, int vertOffset,
      float[] texs, int texOffset, int[] colors, int colorOffset, short[] indices,
      int indexOffset, int indexCount, Paint paint) {
    delegate.drawVertices(mode, vertexCount, verts, vertOffset, texs, texOffset, colors,
        colorOffset, indices, indexOffset, indexCount, paint);
  }

  @Override
  public void drawText(char[] text, int index, int count, float x, float y, Paint paint) {
    delegate.drawText(text, index, count, x, y, paint);
  }

  @Override public void drawText(String text, float x, float y, Paint paint) {
    delegate.drawText(text, x, y, paint);
  }

  @Override public void drawText(String text, int start, int end, float x, float y, Paint paint) {
    delegate.drawText(text, start, end, x, y, paint);
  }

  @Override
  public void drawText(CharSequence text, int start, int end, float x, float y, Paint paint) {
    delegate.drawText(text, start, end, x, y, paint);
  }

  @SuppressWarnings("deprecation")
  @Override public void drawPosText(char[] text, int index, int count, float[] pos, Paint paint) {
    delegate.drawPosText(text, index, count, pos, paint);
  }

  @SuppressWarnings("deprecation")
  @Override public void drawPosText(String text, float[] pos, Paint paint) {
    delegate.drawPosText(text, pos, paint);
  }

  @Override
  public void drawTextOnPath(char[] text, int index, int count, Path path, float hOffset,
      float vOffset, Paint paint) {
    delegate.drawTextOnPath(text, index, count, path, hOffset, vOffset, paint);
  }

  @Override
  public void drawTextOnPath(String text, Path path, float hOffset, float vOffset, Paint paint) {
    delegate.drawTextOnPath(text, path, hOffset, vOffset, paint);
  }

  @Override public void drawPicture(Picture picture) {
    delegate.drawPicture(picture);
  }

  @Override public void drawPicture(Picture picture, RectF dst) {
    delegate.drawPicture(picture, dst);
  }

  @Override public void drawPicture(Picture picture, Rect dst) {
    delegate.drawPicture(picture, dst);
  }
}
