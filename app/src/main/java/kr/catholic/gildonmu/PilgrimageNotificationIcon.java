package kr.catholic.gildonmu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

/** Matching notification icons: monochrome walker in the status bar and the same walker on a dark large-icon tile. */
final class PilgrimageNotificationIcon {
    private PilgrimageNotificationIcon() {}

    static Bitmap largeIcon(Context context) {
        return largeIcon(context, false);
    }

    static Bitmap largeIcon(Context context, boolean car) {
        final float density = context.getResources().getDisplayMetrics().density;
        final int size = Math.max(48, Math.round(density * 64f));
        final Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.rgb(43, 59, 78));
        final Drawable walker = context.getDrawable(car ? R.drawable.ic_car_pilgrimage_notification : R.drawable.ic_pilgrimage_notification);
        if (walker != null) {
            final int margin = Math.round(size * .11f);
            walker.setBounds(margin, margin, size - margin, size - margin);
            walker.mutate().setTint(Color.WHITE);
            walker.draw(canvas);
        }
        return bitmap;
    }
}
