Change Log
==========

Version 1.1.4 *(2016-07-06)*
----------------------------

 * Fix: Correct the scale text positioning when a Bitmap is drawn at an offset.


Version 1.1.3 *(2016-02-20)*
----------------------------

 * Fix: Use a BitmapShader to draw the pixel grid making initialization instant


Version 1.1.2 *(2015-01-30)*
----------------------------

 * Fix: Use a more efficient size and allocation for the grid. This reduces memory consumption of
   the layout by over 90% with no adverse performance effects.
 * Fix: Tile the grid to ensure we cover the entire bitmap size.
 * Fix: Do not allocate large bitmap unless actually enabled.


Version 1.1.1 *(2014-01-03)*
----------------------------

 * Fix: Invalidate view when changing whether the scale ratio is enabled.


Version 1.1.0 *(2014-01-03)*
----------------------------

 * New: Option to draw the scale ratio as text on top of the pixel grid. Control with
   `setOverlayRatioEnabled`.


Version 1.0.1 *(2013-12-05)*
----------------------------

 * Properly expose whether the overlay is enabled or not via `isOverlayEnabled()`.
 * Toggling the overlay will automatically trigger a drawing pass.
 * Free bitmap resources when disabling the overlay.
 * Fix: Changing the color now actually changes the color.


Version 1.0.0 *(2013-12-05)*
----------------------------

Initial release.
