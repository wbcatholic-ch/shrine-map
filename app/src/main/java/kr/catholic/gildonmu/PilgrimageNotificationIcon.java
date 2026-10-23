package kr.catholic.gildonmu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/** Shared notification artwork: Android-compliant monochrome status icon and original app art. */
final class PilgrimageNotificationIcon {
    private PilgrimageNotificationIcon() {}

    static Bitmap largeIcon(Context context) {
        Bitmap original = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        if (original == null) return null;
        int size = Math.max(48, Math.round(context.getResources().getDisplayMetrics().density * 64f));
        Bitmap scaled = Bitmap.createScaledBitmap(original, size, size, true);
        if (scaled != original) original.recycle();
        return scaled;
    }
}
