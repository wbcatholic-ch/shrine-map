package kr.catholic.gildonmu;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/** Disabled compatibility worker; never claim a photo was uploaded when no transfer ran. */
public class VisitPhotoSyncWorker extends Worker {
    public VisitPhotoSyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }
    @NonNull @Override public Result doWork() {
        // The integrated transfer implementation is not connected yet.
        // Do not mark a scheduled backup as successfully completed.
        return Result.failure();
    }
}
