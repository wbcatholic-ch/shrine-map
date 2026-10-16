package kr.catholic.gildonmu;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/** Compatibility no-op for photo uploads queued by older Android versions. */
public class VisitPhotoSyncWorker extends Worker {
    public VisitPhotoSyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }
    @NonNull @Override public Result doWork() {
        return Result.success();
    }
}
