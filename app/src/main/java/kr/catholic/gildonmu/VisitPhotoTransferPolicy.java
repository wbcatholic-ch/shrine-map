package kr.catholic.gildonmu;

/**
 * Pure, thread-safe photo transfer admission and priority policy.
 * This class never starts network jobs or modifies journal, GPS or photo files.
 * A future transfer engine must check it before EACH new file and after any
 * interruptible chunk. An in-flight file needs a separate safe-stop protocol.
 */
final class VisitPhotoTransferPolicy {
    enum Job { NEW_UPLOAD, REQUESTED_PHOTO, BULK_RESTORE }
    enum Block { NONE, CLOUD_DISABLED, LOCATION_ACTIVE, PHOTO_PROCESSING, STAMP_LIST, HIGHER_PRIORITY, BACKGROUND }

    private boolean cloudEnabled;
    private boolean locationActive;
    private boolean photoProcessing;
    private boolean stampListVisible;
    private boolean appVisible = true;
    private boolean requestedPhotoPending;
    private boolean newUploadPending;
    private boolean bulkRestorePending;

    synchronized void setCloudEnabled(boolean enabled) { cloudEnabled = enabled; }
    synchronized void setLocationActive(boolean active) { locationActive = active; }
    synchronized void setPhotoProcessing(boolean active) { photoProcessing = active; }
    synchronized void setStampListVisible(boolean visible) { stampListVisible = visible; }
    synchronized void setAppVisible(boolean visible) { appVisible = visible; }
    synchronized void setRequestedPhotoPending(boolean pending) { requestedPhotoPending = pending; }
    synchronized void setNewUploadPending(boolean pending) { newUploadPending = pending; }
    synchronized void setBulkRestorePending(boolean pending) { bulkRestorePending = pending; }

    synchronized Block blocked(Job job) {
        if (!cloudEnabled) return Block.CLOUD_DISABLED;
        if (locationActive) return Block.LOCATION_ACTIVE;
        if (photoProcessing) return Block.PHOTO_PROCESSING;
        // An already-authorized transfer may continue while this process remains
        // alive but its Activity is not visible. Cloud consent, GPS/map/photo
        // priorities and network eligibility remain enforced by the caller.
        // This does not schedule work after process death or grant background
        // execution time beyond Android OS limits.
        if (stampListVisible && job == Job.BULK_RESTORE) return Block.STAMP_LIST;
        if (job == Job.BULK_RESTORE && (newUploadPending || requestedPhotoPending)) return Block.HIGHER_PRIORITY;
        if (job == Job.REQUESTED_PHOTO && newUploadPending) return Block.HIGHER_PRIORITY;
        return Block.NONE;
    }

    synchronized Job nextEligibleJob() {
        if (blocked(Job.NEW_UPLOAD) == Block.NONE && newUploadPending) return Job.NEW_UPLOAD;
        if (blocked(Job.REQUESTED_PHOTO) == Block.NONE && requestedPhotoPending) return Job.REQUESTED_PHOTO;
        return bulkRestorePending && blocked(Job.BULK_RESTORE) == Block.NONE ? Job.BULK_RESTORE : null;
    }
}
