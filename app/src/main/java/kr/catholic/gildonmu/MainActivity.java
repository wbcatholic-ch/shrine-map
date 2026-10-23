package kr.catholic.gildonmu;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.ClipData;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.pm.PackageManager;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.Insets;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.gms.auth.api.identity.AuthorizationRequest;
import com.google.android.gms.auth.api.identity.AuthorizationResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class MainActivity extends Activity {
    private static final String START_URL = "https://wbcatholic-ch.github.io/shrine-map/";
    private static final String MAIN_HOST = "wbcatholic-ch.github.io";
    private static final int REQ_LOCATION = 7001;
    private static final int REQ_ANNIVERSARY_NOTIFICATION = 7020;
    private static final int REQ_WALK_NOTIFICATION = 7021;
    private static final String ANNIVERSARY_TEST_CHANNEL = "pilgrimage_anniversary_test_sound_v3";
    private boolean anniversaryNotificationPermissionPending = false;
    private volatile boolean anniversaryNotificationOpened = false;
    private static final int REQ_GOOGLE_DRIVE_AUTH = 7011;
    private static final int REQ_VISIT_PHOTO_PICKER = 7012;
    private static final String GOOGLE_DRIVE_APPDATA_SCOPE = "https://www.googleapis.com/auth/drive.appdata";
    private static final int SYSTEM_BAR_NAVY = Color.rgb(14, 21, 53);
    private static final int SPLASH_IVORY = Color.rgb(245, 240, 232);

    // Photo traffic is admitted only after the existing Drive consent and network checks.
    private final VisitPhotoTransferPolicy visitPhotoTransferPolicy = new VisitPhotoTransferPolicy();
    private FrameLayout rootLayout;
    private View statusBarOverlay;
    private FrameLayout launchOverlay;
    private long launchStartedAt;
    private WebView webView;
    private OnBackInvokedCallback onBackInvokedCallback;
    private AppUpdateManager appUpdateManager;
    private FrameLayout appUpdateBanner;
    private boolean appUpdateBannerDismissedThisSession;
    private boolean appUpdateBannerPendingShow;
    private int appUpdateBannerIntroWaitAttempts;
    private static final int APP_UPDATE_BANNER_INTRO_WAIT_MAX_ATTEMPTS = 28;
    private static final long APP_UPDATE_BANNER_INTRO_WAIT_MS = 250L;
    private GeolocationPermissions.Callback pendingGeoCallback;
    private String pendingGeoOrigin;
    private boolean pendingLaunchStorageReset;
    private boolean launchStorageResetReloading;
    private static final long BACKGROUND_TO_COVER_MS = 30L * 60L * 1000L;
    private static final String LIFECYCLE_PREFS = "gildongmu_lifecycle_v1";
    private static final String PREF_BG_ACTIVE = "background_active";
    private static final String PREF_BG_WALL_AT = "background_wall_at";
    private static final String PREF_BG_ELAPSED_AT = "background_elapsed_at";
    private static final String PREF_BG_CYCLE_ID = "background_cycle_id";
    private static final String PREF_BG_SOURCE = "background_source";
    private static final String PREF_LAST_ACK_CYCLE_ID = "last_ack_cycle_id";
    private SharedPreferences lifecyclePrefs;
    private String pendingResumeCycleId = "";
    private String pendingResumeSource = "recent-apps";
    private long pendingResumeElapsedMs = 0L;
    private boolean pendingResumeLong = false;
    private boolean pendingResumeProcessRecreated = false;
    private String lastDispatchedResumeCycleId = "";
    private Runnable pendingResumeDispatchRunnable;
    private boolean activityWasStopped = false;
    private boolean createdFromSavedState = false;
    private FrameLayout transientIvoryOverlay;
    private int transientIvoryOverlayToken = 0;
    private static final long TRANSIENT_IVORY_FADE_OUT_MS = 280L;
    private int lastConfigShortDp = 0;
    private int lastConfigLongDp = 0;
    private int lastRootShortPx = 0;
    private int lastRootLongPx = 0;
    // Single source of truth for Android orientation policy.
    // Everything stays portrait-locked except an unfolded Fold-family inner display
    // whose natural shape is clearly wide (Fold8-like).
    private static final float WIDE_LANDSCAPE_ASPECT_MIN = 1.25f;
    private static final String ORIENTATION_LOG_TAG = "GildongmuOrientation";
    private int lastAppliedOrientationPolicy = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
    private boolean photoViewerRotationEnabled = false;
    private String pendingDriveAction = "";
    private String pendingDrivePayload = "";
    private final ArrayList<String[]> pendingDriveQueue = new ArrayList<>();
    private boolean driveAuthorizationInFlight = false;
    private String cachedDriveAccessToken = "";
    private long cachedDriveAccessTokenAt = 0L;
    private static final long DRIVE_TOKEN_CACHE_MS = 45L * 60L * 1000L;
    private String pendingVisitPhotoRequestId = "";
    private int pendingVisitPhotoMaxCount = 0;
    private final HashSet<String> pendingVisitPhotoExistingNames = new HashSet<>();
    private final ArrayList<Uri> pendingVisitPhotoUris = new ArrayList<>();
    private boolean pendingVisitPhotoTrimmed = false;
    private volatile boolean visitPhotoSyncRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        acceptAnniversaryIntent(getIntent());
        acceptWalkingReturnIntent(getIntent());
        if(getIntent()!=null && CAR_PILGRIMAGE_OPEN.equals(getIntent().getAction()))
            getSharedPreferences("pilgrimage_car_notice",MODE_PRIVATE).edit().putBoolean("pending_open",true).apply();
        try { WorkManager.getInstance(getApplicationContext()).cancelUniqueWork("visit-photo-drive-sync"); } catch (Exception ignored) {}
        applyOrientationPolicy("create");
        createdFromSavedState = savedInstanceState != null;
        lifecyclePrefs = getSharedPreferences(LIFECYCLE_PREFS, MODE_PRIVATE);
        restorePendingLifecycleCycle();
        configureSystemBars();

        rootLayout = new FrameLayout(this);
        rootLayout.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        rootLayout.setBackgroundColor(SPLASH_IVORY);

        webView = new WebView(this);
        webView.setBackgroundColor(SPLASH_IVORY);
        webView.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        rootLayout.addView(webView);

        statusBarOverlay = new View(this);
        statusBarOverlay.setBackgroundColor(SYSTEM_BAR_NAVY);
        FrameLayout.LayoutParams statusBarOverlayParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0
        );
        rootLayout.addView(statusBarOverlay, statusBarOverlayParams);

        prepareTransientIvoryOverlay();
        addLaunchOverlay();

        setContentView(rootLayout);
        configureShellLayout();
        registerSystemBackCallback();
        rememberCurrentConfigDimensions();
        rememberCurrentRootDimensions();
        installFoldSurfacePreArmWatcher();

        // Request location access on the first app launch, as required for this
        // location-based app. A denied request can later be retried from the
        // nearby screen through the existing native permission bridge.
        if (!hasLocationPermission() &&
                !getSharedPreferences("cgm_location_permission_v1", MODE_PRIVATE)
                    .getBoolean("initial_prompt_shown", false)) {
            rootLayout.postDelayed(new Runnable() { @Override public void run() {
                if (!isFinishing() && !hasLocationPermission() && !nearbyPermissionRequestedThisLaunch) {
                    getSharedPreferences("cgm_location_permission_v1", MODE_PRIVATE)
                        .edit().putBoolean("initial_prompt_shown", true).apply();
                    nearbyPermissionRequestedThisLaunch = true;
                    requestPermissions(new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    }, REQ_LOCATION);
                }
            }}, 1100L);
        }

        configureWebView();
        loadStartUrl("create", false);
        setupGooglePlayUpdateBannerSupport();
    }

    /**
     * Native entry veil must not draw or replay the intro.
     * PWA V335 keeps location lookup and Fold relayout stable on slow GPS starts, so Android no longer
     * performs a hidden double-load that delays the first cross.
     */
    private void addLaunchOverlay() {
        if (rootLayout == null || launchOverlay != null) return;
        launchStartedAt = System.currentTimeMillis();

        launchOverlay = new FrameLayout(this);
        launchOverlay.setBackgroundColor(SPLASH_IVORY);
        launchOverlay.setAlpha(1f);

        rootLayout.addView(launchOverlay, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
    }

    private void hideLaunchOverlay() {
        if (launchOverlay == null) return;
        final View overlay = launchOverlay;
        launchOverlay = null;

        long elapsed = System.currentTimeMillis() - launchStartedAt;
        long wait = Math.max(0L, 0L - elapsed);

        overlay.postDelayed(new Runnable() {
            @Override
            public void run() {
                overlay.animate().cancel();
                overlay.animate()
                        .alpha(0f)
                        .setDuration(120)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                try {
                                    if (overlay.getParent() instanceof ViewGroup) {
                                        ((ViewGroup) overlay.getParent()).removeView(overlay);
                                    }
                                } catch (Exception ignored) {
                                }
                            }
                        })
                        .start();
            }
        }, wait);
    }


    /**
     * Step 1 upper-position correction.
     * Keep the WebView in the same top position used by the PWA-like layout,
     * while a native overlay fills only the Android status-bar strip above it.
     * The bottom inset is intentionally preserved for the later bottom-position pass.
     */
    private void configureShellLayout() {
        if (rootLayout == null || webView == null || statusBarOverlay == null) return;

        rootLayout.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View view, WindowInsets insets) {
                SafeInsets safe = readSafeInsets(insets);
                int statusTop = safe.top > 0 ? safe.top : getStatusBarHeightFallback();
                int webTop = isTabletLayout() ? statusTop : 0;

                FrameLayout.LayoutParams webParams = (FrameLayout.LayoutParams) webView.getLayoutParams();
                if (webParams.topMargin != webTop
                        || webParams.bottomMargin != safe.bottom
                        || webParams.leftMargin != safe.left
                        || webParams.rightMargin != safe.right) {
                    webParams.topMargin = webTop;
                    webParams.bottomMargin = safe.bottom;
                    webParams.leftMargin = safe.left;
                    webParams.rightMargin = safe.right;
                    webView.setLayoutParams(webParams);
                }

                FrameLayout.LayoutParams statusParams = (FrameLayout.LayoutParams) statusBarOverlay.getLayoutParams();
                if (statusParams.height != statusTop) {
                    statusParams.height = statusTop;
                    statusBarOverlay.setLayoutParams(statusParams);
                }
                statusBarOverlay.bringToFront();
                return insets;
            }
        });
        rootLayout.requestApplyInsets();
    }

    private static class SafeInsets {
        final int left;
        final int top;
        final int right;
        final int bottom;

        SafeInsets(int left, int top, int right, int bottom) {
            this.left = Math.max(0, left);
            this.top = Math.max(0, top);
            this.right = Math.max(0, right);
            this.bottom = Math.max(0, bottom);
        }
    }

    private SafeInsets readSafeInsets(WindowInsets insets) {
        if (insets == null) {
            return new SafeInsets(0, 0, 0, 0);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Insets safeInsets = insets.getInsets(
                    WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout()
            );
            return new SafeInsets(safeInsets.left, safeInsets.top, safeInsets.right, safeInsets.bottom);
        }
        return new SafeInsets(
                insets.getSystemWindowInsetLeft(),
                insets.getSystemWindowInsetTop(),
                insets.getSystemWindowInsetRight(),
                insets.getSystemWindowInsetBottom()
        );
    }

    /**
     * Android orientation policy (single source of truth).
     *
     * Android 16 / targetSdk 36 ignores portrait-style orientation requests on
     * large displays.  Therefore a Fold6-class near-square inner display cannot
     * be protected reliably by repeatedly requesting PORTRAIT.  For restricted
     * large/fold screens we instead lock the CURRENT rotation.  In the normal
     * phone -> unfolded Fold flow that current rotation is portrait, so the
     * inner display stays visually portrait when the device is turned.
     *
     * Policy:
     * - Only a Fold-family OPEN inner display that is clearly wide
     *   (natural aspect >= 1.25) follows the user's Android auto-rotate setting.
     * - Fold-family cover screens and near-square inner displays use LOCKED.
     * - Ordinary phone-size screens use PORTRAIT.
     * - Other large screens also use LOCKED rather than a portrait request that
     *   Android 16 can ignore.
     */
    private void applyOrientationPolicy(String reason) {
        try {
            OrientationDecision decision = resolveOrientationDecision();
            int desired = photoViewerRotationEnabled ? ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR : decision.requestedOrientation;
            if (lastAppliedOrientationPolicy != desired) {
                lastAppliedOrientationPolicy = desired;
                setRequestedOrientation(desired);
            }
            Log.i(ORIENTATION_LOG_TAG,
                    "reason=" + reason
                            + " policy=" + decision.label
                            + " requested=" + desired
                            + " fold=" + decision.foldLike
                            + " wideFold=" + decision.wideFold
                            + " large=" + decision.largeScreen
                            + " px=" + decision.widthPx + "x" + decision.heightPx
                            + " dp=" + decision.widthDp + "x" + decision.heightDp
                            + " sw=" + decision.smallestWidthDp
                            + " rotation=" + decision.rotation
                            + " naturalAspect=" + decision.naturalAspect);
        } catch (Exception e) {
            Log.w(ORIENTATION_LOG_TAG, "orientation policy failed: " + reason, e);
        }
    }

    private OrientationDecision resolveOrientationDecision() {
        OrientationDecision out = new OrientationDecision();
        try {
            out.foldLike = isFoldLikeDevice();
            Configuration config = getResources() != null ? getResources().getConfiguration() : null;
            if (config != null) {
                out.widthDp = Math.max(0, config.screenWidthDp);
                out.heightDp = Math.max(0, config.screenHeightDp);
                out.smallestWidthDp = Math.max(0, config.smallestScreenWidthDp);
            }
            out.largeScreen = out.smallestWidthDp >= 600;

            Display display = getWindowManager().getDefaultDisplay();
            if (display != null) {
                DisplayMetrics metrics = new DisplayMetrics();
                display.getRealMetrics(metrics);
                out.widthPx = Math.max(0, metrics.widthPixels);
                out.heightPx = Math.max(0, metrics.heightPixels);
                out.rotation = display.getRotation();

                boolean quarterTurn = out.rotation == Surface.ROTATION_90
                        || out.rotation == Surface.ROTATION_270;
                int naturalWidth = quarterTurn ? out.heightPx : out.widthPx;
                int naturalHeight = quarterTurn ? out.widthPx : out.heightPx;
                out.naturalAspect = naturalHeight > 0
                        ? (float) naturalWidth / (float) naturalHeight
                        : 1f;
                out.wideFold = out.foldLike
                        && naturalWidth > naturalHeight
                        && out.naturalAspect >= WIDE_LANDSCAPE_ASPECT_MIN;
            }

            if (out.wideFold) {
                out.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER;
                out.label = "WIDE_FOLD_USER";
            } else if (out.foldLike || out.largeScreen) {
                // LOCKED is intentional here: on API 36 large displays Android can
                // ignore portrait-style requests.  Lock the rotation that is already
                // active when the Fold/tablet-sized surface appears.
                out.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED;
                out.label = out.foldLike ? "FOLD_LOCKED" : "LARGE_LOCKED";
            } else {
                out.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                out.label = "PHONE_PORTRAIT";
            }
        } catch (Exception e) {
            out.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            out.label = "FALLBACK_PORTRAIT";
        }
        return out;
    }

    private static final class OrientationDecision {
        int requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        String label = "UNKNOWN";
        boolean foldLike = false;
        boolean wideFold = false;
        boolean largeScreen = false;
        int widthPx = 0;
        int heightPx = 0;
        int widthDp = 0;
        int heightDp = 0;
        int smallestWidthDp = 0;
        int rotation = Surface.ROTATION_0;
        float naturalAspect = 1f;
    }

    /**
     * Fold/unfold metrics can settle over more than one layout/configuration frame.
     * Re-run the SAME centralized policy after settling; no alternate policy or JS
     * orientation patch is used.
     */
    private void scheduleOrientationPolicyRecheck() {
        final View target = rootLayout != null ? rootLayout : webView;
        if (target == null) return;
        target.postDelayed(new Runnable() {
            @Override
            public void run() {
                applyOrientationPolicy("settled");
            }
        }, 240L);
    }

    /**
     * Keep the Android shell simple and safe.
     * This does not touch location, WebView URL, JavaScript, or app back logic.
     */
    private void configureSystemBars() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false);
        }

        window.getDecorView().setBackgroundColor(SYSTEM_BAR_NAVY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.setStatusBarContrastEnforced(false);
            window.setNavigationBarContrastEnforced(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = window.getInsetsController();
            if (controller != null) {
                controller.setSystemBarsAppearance(
                        0,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                                | WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                );
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = window.getDecorView().getSystemUiVisibility();
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                flags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }
            window.getDecorView().setSystemUiVisibility(flags);
        }

        if (rootLayout != null) {
            rootLayout.setBackgroundColor(SYSTEM_BAR_NAVY);
        }
        applyPhotoViewerFullscreen();
    }

    // Hide system bars only while a full-size R2 or personal photo is displayed.
    // Keep transient swipe-to-reveal available and restore the app's bars on exit.
    private void applyPhotoViewerFullscreen() {
        Window window=getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller=window.getInsetsController();
            if (controller!=null) {
                if (photoViewerRotationEnabled) {
                    controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                    controller.hide(android.view.WindowInsets.Type.statusBars() | android.view.WindowInsets.Type.navigationBars());
                } else {
                    controller.show(android.view.WindowInsets.Type.statusBars() | android.view.WindowInsets.Type.navigationBars());
                }
            }
        } else {
            View decor=window.getDecorView();
            int flags=decor.getSystemUiVisibility();
            int mask=View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decor.setSystemUiVisibility(photoViewerRotationEnabled ? flags|mask : flags&~mask);
        }
    }

    @Override
    protected void onPause() {
        visitPhotoAppVisible = false;
        visitPhotoTransferPolicy.setAppVisible(false);
        // A hidden map/stampbook must not keep the bulk restore queue paused.
        // No UI interaction or new authorization is initiated in background.
        runAuthorizedVisitPhotoRestores();
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (!isChangingConfigurations() && !isFinishing()) {
            markNativeBackgroundStart("onStop");
            activityWasStopped = true;
        }
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        prepareNativeResumeFromStoredCycle();
    }

    @Override
    protected void onResume() {
        super.onResume();
        visitPhotoAppVisible = true;
        visitPhotoTransferPolicy.setAppVisible(true);
        // Existing consent is checked before any silent authorization attempt.
        // A cached token from an earlier session may have expired while the app was paused.
        resumeAuthorizedVisitPhotoTransfers();
        applyOrientationPolicy("resume");
        configureSystemBars();
        if (rootLayout != null) {
            rootLayout.requestApplyInsets();
        }
        dispatchNativeDeviceClassToWeb(20L);
        dispatchAnniversaryNotificationOpen();
        dispatchPendingNativeResumeToWeb(80L, false);
        // Reuse the existing app-update schedule; photo traffic is controlled separately.
        checkForPlayAppUpdate(pendingResumeCycleId.length() > 0 ? 1600L : 650L);
    }

    private void markNativeBackgroundStart(String source) {
        try {
            if (lifecyclePrefs == null) lifecyclePrefs = getSharedPreferences(LIFECYCLE_PREFS, MODE_PRIVATE);
            boolean alreadyActive = lifecyclePrefs.getBoolean(PREF_BG_ACTIVE, false);
            if (alreadyActive) return;
            String cycleId = UUID.randomUUID().toString();
            lifecyclePrefs.edit()
                    .putBoolean(PREF_BG_ACTIVE, true)
                    .putLong(PREF_BG_WALL_AT, System.currentTimeMillis())
                    .putLong(PREF_BG_ELAPSED_AT, SystemClock.elapsedRealtime())
                    .putString(PREF_BG_CYCLE_ID, cycleId)
                    .putString(PREF_BG_SOURCE, source != null ? source : "onStop")
                    .apply();
        } catch (Exception ignored) {
        }
    }

    private void restorePendingLifecycleCycle() {
        try {
            if (lifecyclePrefs == null) lifecyclePrefs = getSharedPreferences(LIFECYCLE_PREFS, MODE_PRIVATE);
            if (!lifecyclePrefs.getBoolean(PREF_BG_ACTIVE, false)) return;
            String cycleId = lifecyclePrefs.getString(PREF_BG_CYCLE_ID, "");
            if (cycleId == null || cycleId.length() == 0) return;
            String ack = lifecyclePrefs.getString(PREF_LAST_ACK_CYCLE_ID, "");
            if (cycleId.equals(ack)) {
                lifecyclePrefs.edit().putBoolean(PREF_BG_ACTIVE, false).apply();
                return;
            }
            pendingResumeCycleId = cycleId;
            pendingResumeSource = lifecyclePrefs.getString(PREF_BG_SOURCE, "process-recreated");
            pendingResumeElapsedMs = calculateStoredBackgroundElapsed();
            pendingResumeLong = pendingResumeElapsedMs >= BACKGROUND_TO_COVER_MS;
            pendingResumeProcessRecreated = true;
        } catch (Exception ignored) {
        }
    }

    private long calculateStoredBackgroundElapsed() {
        try {
            long storedElapsed = lifecyclePrefs.getLong(PREF_BG_ELAPSED_AT, 0L);
            long nowElapsed = SystemClock.elapsedRealtime();
            if (storedElapsed > 0L && nowElapsed >= storedElapsed) {
                return Math.max(0L, nowElapsed - storedElapsed);
            }
            long wall = lifecyclePrefs.getLong(PREF_BG_WALL_AT, 0L);
            return wall > 0L ? Math.max(0L, System.currentTimeMillis() - wall) : 0L;
        } catch (Exception ignored) {
            return 0L;
        }
    }

    private void prepareNativeResumeFromStoredCycle() {
        try {
            if (lifecyclePrefs == null) lifecyclePrefs = getSharedPreferences(LIFECYCLE_PREFS, MODE_PRIVATE);
            if (!lifecyclePrefs.getBoolean(PREF_BG_ACTIVE, false)) return;
            String cycleId = lifecyclePrefs.getString(PREF_BG_CYCLE_ID, "");
            if (cycleId == null || cycleId.length() == 0) return;
            String ack = lifecyclePrefs.getString(PREF_LAST_ACK_CYCLE_ID, "");
            if (cycleId.equals(ack)) {
                lifecyclePrefs.edit().putBoolean(PREF_BG_ACTIVE, false).apply();
                return;
            }
            pendingResumeCycleId = cycleId;
            pendingResumeElapsedMs = calculateStoredBackgroundElapsed();
            pendingResumeLong = pendingResumeElapsedMs >= BACKGROUND_TO_COVER_MS;
            pendingResumeSource = lifecyclePrefs.getString(PREF_BG_SOURCE, "onStop");
            pendingResumeProcessRecreated = createdFromSavedState || !activityWasStopped;
            if (pendingResumeLong) showTransientIvoryOverlay(360L);
        } catch (Exception ignored) {
        }
    }

    private void dispatchPendingNativeResumeToWeb(long delayMs, boolean forceRetry) {
        if (webView == null || pendingResumeCycleId == null || pendingResumeCycleId.length() == 0) return;
        final String cycleId = pendingResumeCycleId;
        if (!forceRetry && cycleId.equals(lastDispatchedResumeCycleId)) return;
        if (pendingResumeDispatchRunnable != null) webView.removeCallbacks(pendingResumeDispatchRunnable);
        pendingResumeDispatchRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (webView == null || !cycleId.equals(pendingResumeCycleId)) return;
                    lastDispatchedResumeCycleId = cycleId;
                    String safeCycle = cycleId.replace("\\", "\\\\").replace("'", "\\'");
                    String safeSource = String.valueOf(pendingResumeSource).replace("\\", "\\\\").replace("'", "\\'");
                    String script = "(function(){try{" +
                            "window.__OAI_NATIVE_LIFECYCLE_PROTOCOL=1;" +
                            "window.dispatchEvent(new CustomEvent('oai-android-native-resume',{detail:{" +
                            "protocolVersion:1,cycleId:'" + safeCycle + "',elapsed:" + pendingResumeElapsedMs +
                            ",forceCover:" + pendingResumeLong +
                            ",longReturn:" + pendingResumeLong +
                            ",processRecreated:" + pendingResumeProcessRecreated +
                            ",source:'" + safeSource + "'}}));" +
                            "}catch(e){}})();";
                    webView.evaluateJavascript(script, null);
                } catch (Exception ignored) {
                }
            }
        };
        webView.postDelayed(pendingResumeDispatchRunnable, Math.max(0L, delayMs));
    }

    private void acknowledgeNativeResumeCycle(String cycleId) {
        try {
            if (cycleId == null || cycleId.length() == 0) return;
            if (lifecyclePrefs == null) lifecyclePrefs = getSharedPreferences(LIFECYCLE_PREFS, MODE_PRIVATE);
            lifecyclePrefs.edit()
                    .putString(PREF_LAST_ACK_CYCLE_ID, cycleId)
                    .putBoolean(PREF_BG_ACTIVE, false)
                    .remove(PREF_BG_WALL_AT)
                    .remove(PREF_BG_ELAPSED_AT)
                    .remove(PREF_BG_CYCLE_ID)
                    .remove(PREF_BG_SOURCE)
                    .apply();
            if (cycleId.equals(pendingResumeCycleId)) {
                pendingResumeCycleId = "";
                pendingResumeElapsedMs = 0L;
                pendingResumeLong = false;
                pendingResumeProcessRecreated = false;
                activityWasStopped = false;
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Fold/open-cover screen changes must not recreate or reload the WebView.
     * Keep the current DOM and let the web layer handle it as a normal resize.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        long foldSurfaceVeilMs = foldSurfaceVeilDurationMs(newConfig);
        boolean foldSurfaceChange = foldSurfaceVeilMs > 0L;
        if (foldSurfaceChange) {
            showTransientIvoryOverlay(foldSurfaceVeilMs);
        }
        super.onConfigurationChanged(newConfig);
        applyOrientationPolicy("configuration");
        scheduleOrientationPolicyRecheck();
        configureSystemBars();
        configureShellLayout();
        rememberCurrentConfigDimensions();
        if (rootLayout != null) {
            rootLayout.requestApplyInsets();
        }
        dispatchNativeDeviceClassToWeb(0L);
        if (webView != null) {
            dispatchViewportChangedToWeb(foldSurfaceChange ? 80L : 120L);
        }
    }

    private void dispatchViewportChangedToWeb(long delayMs) {
        if (webView == null) return;
        webView.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (webView == null) return;
                    webView.requestLayout();
                    webView.invalidate();
                    webView.evaluateJavascript(
                            "try {" +
                                    nativeDeviceClassScript() +
                                    "window.__OAI_ANDROID_NATIVE_FOLD_SETTLE_UNTIL=Date.now()+320;" +
                                    "if(window.oaiApplyAndroidScreenClass){window.oaiApplyAndroidScreenClass('android-configuration-change-force-remeasure');}" +
                                    "if(window.oaiRecalibrateCoverViewport){window.oaiRecalibrateCoverViewport('android-configuration-change');}" +
                                    "window.dispatchEvent(new Event('resize'));" +
                                    "window.dispatchEvent(new CustomEvent('oai-android-viewport-change',{detail:{reason:'configuration'}}));" +
                                    "} catch(e) {}",
                            null
                    );
                } catch (Exception ignored) {
                }
            }
        }, Math.max(0L, delayMs));
    }

    /**
     * Launcher entry must start from the app cover, not from a previously saved
     * WebView URL such as a cathedral map. Recent-app return still keeps the
     * live Activity because it does not deliver a new launcher intent.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        acceptAnniversaryIntent(intent);
        acceptWalkingReturnIntent(intent);
        if(intent!=null && CAR_PILGRIMAGE_OPEN.equals(intent.getAction()))
            getSharedPreferences("pilgrimage_car_notice",MODE_PRIVATE).edit().putBoolean("pending_open",true).apply();
        if(webView!=null)webView.evaluateJavascript(
                "window._oaiOpenCarFromNotification&&window._oaiOpenCarFromNotification()",null);
        if (webView != null) webView.evaluateJavascript(
                "window._oaiOpenWalkingFromNotification&&window._oaiOpenWalkingFromNotification()", null);
        dispatchAnniversaryNotificationOpen();
        configureSystemBars();
        if (isLauncherIntent(intent)) {
            pendingResumeSource = "launcher";
        }
    }

    private void prepareTransientIvoryOverlay() {
        if (rootLayout == null || transientIvoryOverlay != null) return;
        try {
            transientIvoryOverlay = new FrameLayout(this);
            transientIvoryOverlay.setBackgroundColor(SPLASH_IVORY);
            transientIvoryOverlay.setAlpha(0f);
            transientIvoryOverlay.setVisibility(View.INVISIBLE);
            transientIvoryOverlay.setClickable(false);
            rootLayout.addView(transientIvoryOverlay, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
        } catch (Exception ignored) {
        }
    }

    private void showTransientIvoryOverlay(long keepMs) {
        if (rootLayout == null || keepMs <= 0L) return;
        try {
            prepareTransientIvoryOverlay();
            if (transientIvoryOverlay == null) return;
            final int token = ++transientIvoryOverlayToken;
            final View overlay = transientIvoryOverlay;
            overlay.animate().cancel();
            overlay.animate().setListener(null);
            overlay.setVisibility(View.VISIBLE);
            overlay.setClickable(true);
            overlay.setAlpha(1f);
            overlay.bringToFront();
            if (statusBarOverlay != null) statusBarOverlay.bringToFront();
            overlay.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (token != transientIvoryOverlayToken) return;
                    try {
                        overlay.animate().cancel();
                        overlay.animate()
                                .alpha(0f)
                                .setDuration(TRANSIENT_IVORY_FADE_OUT_MS)
                                .setInterpolator(new android.view.animation.DecelerateInterpolator())
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        if (token != transientIvoryOverlayToken) return;
                                        try {
                                            overlay.setAlpha(0f);
                                            overlay.setClickable(false);
                                            overlay.setVisibility(View.INVISIBLE);
                                        } catch (Exception ignored) {
                                        }
                                    }
                                })
                                .start();
                    } catch (Exception ignored) {
                    }
                }
            }, Math.max(0L, keepMs));
        } catch (Exception ignored) {
        }
    }

    private void rememberCurrentRootDimensions() {
        try {
            if (rootLayout == null) return;
            int w = Math.max(0, rootLayout.getWidth());
            int h = Math.max(0, rootLayout.getHeight());
            if (w <= 0 || h <= 0) return;
            lastRootShortPx = Math.min(w, h);
            lastRootLongPx = Math.max(w, h);
        } catch (Exception ignored) {
        }
    }

    private void installFoldSurfacePreArmWatcher() {
        if (rootLayout == null) return;
        rootLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                try {
                    if (!isFoldLikeDevice()) {
                        rememberCurrentRootDimensions();
                        return;
                    }
                    int newW = Math.max(0, right - left);
                    int newH = Math.max(0, bottom - top);
                    int oldW = Math.max(0, oldRight - oldLeft);
                    int oldH = Math.max(0, oldBottom - oldTop);
                    if (oldW <= 0 || oldH <= 0) {
                        oldW = lastRootShortPx > 0 && lastRootLongPx > 0 ? lastRootShortPx : 0;
                        oldH = lastRootShortPx > 0 && lastRootLongPx > 0 ? lastRootLongPx : 0;
                    }
                    long veilMs = foldSurfaceVeilDurationFromPixels(oldW, oldH, newW, newH);
                    if (veilMs > 0L) {
                        applyOrientationPolicy("fold-layout");
                        scheduleOrientationPolicyRecheck();
                        showTransientIvoryOverlay(veilMs);
                    }
                    rememberCurrentRootDimensions();
                } catch (Exception ignored) {
                }
            }
        });
    }

    private long foldSurfaceVeilDurationFromPixels(int oldW, int oldH, int newW, int newH) {
        try {
            if (!isFoldLikeDevice()) return 0L;
            if (oldW <= 0 || oldH <= 0 || newW <= 0 || newH <= 0) return 0L;
            int oldShort = Math.min(oldW, oldH);
            int oldLong = Math.max(oldW, oldH);
            int newShort = Math.min(newW, newH);
            int newLong = Math.max(newW, newH);
            int shortDiff = Math.abs(newShort - oldShort);
            int longDiff = Math.abs(newLong - oldLong);
            if (shortDiff < 120 && longDiff < 120) return 0L;
            long oldArea = Math.max(1L, (long) oldShort * (long) oldLong);
            long newArea = Math.max(1L, (long) newShort * (long) newLong);
            boolean openingToLargeScreen = newShort >= oldShort + 120
                    || newArea >= (oldArea * 13L / 10L)
                    || newLong >= oldLong + 220;
            return openingToLargeScreen ? 980L : 480L;
        } catch (Exception ignored) {
            return 0L;
        }
    }


    private int[] currentConfigDimensionsDp(Configuration config) {
        int wDp = 0;
        int hDp = 0;
        try {
            if (config == null) config = getResources().getConfiguration();
            if (config != null) {
                wDp = Math.max(0, config.screenWidthDp);
                hDp = Math.max(0, config.screenHeightDp);
            }
        } catch (Exception ignored) {
        }
        try {
            if ((wDp <= 0 || hDp <= 0)) {
                android.util.DisplayMetrics dm = getResources().getDisplayMetrics();
                if (dm != null && dm.density > 0f) {
                    if (wDp <= 0) wDp = Math.round(dm.widthPixels / dm.density);
                    if (hDp <= 0) hDp = Math.round(dm.heightPixels / dm.density);
                }
            }
        } catch (Exception ignored) {
        }
        int shortDp = Math.min(wDp, hDp);
        int longDp = Math.max(wDp, hDp);
        return new int[]{Math.max(0, shortDp), Math.max(0, longDp)};
    }

    private void rememberCurrentConfigDimensions() {
        int[] dims = currentConfigDimensionsDp(null);
        lastConfigShortDp = dims[0];
        lastConfigLongDp = dims[1];
    }

    private long foldSurfaceVeilDurationMs(Configuration newConfig) {
        try {
            if (!isFoldLikeDevice()) return 0L;
            int[] next = currentConfigDimensionsDp(newConfig);
            int nextShort = next[0];
            int nextLong = next[1];
            if (lastConfigShortDp <= 0 || lastConfigLongDp <= 0 || nextShort <= 0 || nextLong <= 0) {
                // V275: when the previous Fold size is unknown, keep the prepared veil long enough for an opening transition.
                return 980L;
            }
            int shortDiff = Math.abs(nextShort - lastConfigShortDp);
            int longDiff = Math.abs(nextLong - lastConfigLongDp);
            if (shortDiff < 96 && longDiff < 96) return 0L;

            long prevArea = Math.max(1L, (long) lastConfigShortDp * (long) lastConfigLongDp);
            long nextArea = Math.max(1L, (long) nextShort * (long) nextLong);
            boolean openingToLargeScreen = nextShort >= lastConfigShortDp + 80
                    || nextShort >= 600
                    || nextArea >= (prevArea * 13L / 10L);
            // The large Fold screen needs a longer prepared cover because WebView's first resize frame can still move underneath it.
            return openingToLargeScreen ? 980L : 480L;
        } catch (Exception ignored) {
            return 0L;
        }
    }

    private int getStatusBarHeightFallback() {
        try {
            int resId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resId > 0) return Math.max(0, getResources().getDimensionPixelSize(resId));
        } catch (Exception ignored) {
        }
        return 0;
    }

    private boolean isFoldLikeDevice() {
        try {
            String m = String.valueOf(Build.MANUFACTURER) + " "
                    + String.valueOf(Build.BRAND) + " "
                    + String.valueOf(Build.MODEL) + " "
                    + String.valueOf(Build.DEVICE) + " "
                    + String.valueOf(Build.PRODUCT);
            String l = m.toLowerCase();
            return m.matches("(?i).*\\bSM-F\\d+.*")
                    || m.matches("(?i).*\\bF9\\d+.*")
                    || l.contains("fold");
        } catch (Exception ignored) {
            return false;
        }
    }

    private boolean isGalaxyTabLikeDevice() {
        try {
            String m = String.valueOf(Build.MANUFACTURER) + " "
                    + String.valueOf(Build.BRAND) + " "
                    + String.valueOf(Build.MODEL) + " "
                    + String.valueOf(Build.DEVICE) + " "
                    + String.valueOf(Build.PRODUCT);
            String l = m.toLowerCase();
            return m.matches("(?i).*\\bSM-[TX]\\w+.*")
                    || l.contains("galaxy tab")
                    || l.contains("gta");
        } catch (Exception ignored) {
            return false;
        }
    }

    private boolean isTabletLayout() {
        try {
            if (isFoldLikeDevice()) return false;
            if (isGalaxyTabLikeDevice()) return true;
            Configuration config = getResources().getConfiguration();
            if (config != null) {
                if (config.smallestScreenWidthDp >= 600) return true;
                int sizeMask = config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
                if (sizeMask == Configuration.SCREENLAYOUT_SIZE_LARGE || sizeMask == Configuration.SCREENLAYOUT_SIZE_XLARGE) return true;
            }
            int[] dims = currentConfigDimensionsDp(config);
            int shortDp = dims[0];
            int longDp = dims[1];
            if (shortDp >= 600) return true;
            // V273: some long Android tablets report a Fold-like width in dp.
            // They still need tablet status-bar handling when the long side is clearly tablet-sized.
            return (shortDp >= 480 && longDp >= 860)
                    || (shortDp >= 520 && longDp >= 820)
                    || (shortDp >= 430 && longDp >= 900 && longDp >= shortDp * 17 / 10);
        } catch (Exception ignored) {
        }
        return false;
    }

    private String nativeDeviceClassScript() {
        boolean tablet = isTabletLayout();
        return "try {" +
                "var r=document.documentElement;" +
                "if(r){" +
                (tablet
                        ? "r.classList.add('oai-android-tablet');r.setAttribute('data-oai-native-device','tablet');"
                        : "r.classList.remove('oai-android-tablet');r.setAttribute('data-oai-native-device','phone');") +
                "}" +
                "} catch(e) {}";
    }

    private void dispatchNativeDeviceClassToWeb(long delayMs) {
        if (webView == null) return;
        webView.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (webView != null) {
                        webView.evaluateJavascript("(function(){" + nativeDeviceClassScript() + "})()", null);
                    }
                } catch (Exception ignored) {
                }
            }
        }, Math.max(0L, delayMs));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Do not save WebView URL/history here. When Android recreates the Activity,
        // restoring this state can reopen the last map screen on the next app entry.
        super.onSaveInstanceState(outState);
    }

    private boolean isLauncherIntent(Intent intent) {
        if (intent == null) return false;
        if (!Intent.ACTION_MAIN.equals(intent.getAction())) return false;
        return intent.hasCategory(Intent.CATEGORY_LAUNCHER);
    }

    private void loadStartUrl(String reason, boolean clearNavigationState) {
        if (webView == null) return;
        try {
            if (clearNavigationState) {
                pendingLaunchStorageReset = true;
                launchStorageResetReloading = false;
            }
            webView.stopLoading();
            webView.loadUrl(START_URL);
        } catch (Exception ignored) {
        }
    }

    private boolean isMainAppPage(String url) {
        Uri uri = parseUriOrNull(url);
        return isInternalAppUri(uri);
    }

    private void clearLaunchNavigationStateAndReload() {
        if (webView == null) return;
        final String clearScript = "(function(){" +
                "function shouldClear(k){" +
                "if(!k)return false;" +
                "return k.indexOf('oai_visible_screen_state')===0" +
                "|| k.indexOf('oai_fold_surface')===0" +
                "|| k.indexOf('oai_fold_transition')===0" +
                "|| k.indexOf('oai_viewport_no_intro')===0" +
                "|| k.indexOf('oai_viewport_width')===0" +
                "|| k.indexOf('oai_resume_current_state')===0" +
                "|| k.indexOf('oai_resume_last_noncover_state')===0" +
                "|| k.indexOf('oai_resume_screen_state')===0" +
                "|| k.indexOf('oai_resume_return_veil')===0" +
                "|| k.indexOf('oai_background_cover_reset_requested')===0" +
                "|| k.indexOf('oai_background_intro_return_until')===0" +
                "|| k.indexOf('oai_bg_started_at')===0" +
                "|| k.indexOf('oai_bg_token')===0" +
                "|| k.indexOf('oai_short_background_return_until')===0" +
                "|| k.indexOf('oai_home_backgrounded_at')===0" +
                "|| k.indexOf('oai_hidden_at')===0" +
                "|| k.indexOf('oai_internal_return_no_effect')===0" +
                "|| k.indexOf('oai_refresh_history_compact')===0" +
                "|| k.indexOf('oai_refresh_veil')===0" +
                "|| k==='catholic_integrated_return_v2';" +
                "}" +
                "function clearStore(store){" +
                "if(!store)return;" +
                "var keys=[];" +
                "for(var i=0;i<store.length;i++){var k=store.key(i);if(shouldClear(k))keys.push(k);}" +
                "for(var j=0;j<keys.length;j++){try{store.removeItem(keys[j]);}catch(e){}}" +
                "}" +
                "try{clearStore(localStorage);}catch(e){}" +
                "try{clearStore(sessionStorage);}catch(e){}" +
                "return true;" +
                "})()";
        try {
            webView.evaluateJavascript(clearScript, null);
        } catch (Exception ignored) {
        }
        webView.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (webView != null) {
                        webView.stopLoading();
                        webView.loadUrl(START_URL);
                    }
                } catch (Exception ignored) {
                }
            }
        }, 80L);
    }


    /**
     * Google Play update banner.
     * This checks the Play Store update state through Play Core and shows a small native banner.
     * The update button opens the official Google Play listing instead of running an in-app download flow.
     * It intentionally does not touch PWA routing, back handling, map code, or external-site navigation.
     */
    private void setupGooglePlayUpdateBannerSupport() {
        try {
            appUpdateManager = AppUpdateManagerFactory.create(this);
            checkForPlayAppUpdate(1200L);
        } catch (Exception ignored) {
        }
    }

    private void checkForPlayAppUpdate(long delayMs) {
        if (appUpdateManager == null) return;
        View postTarget = rootLayout != null ? rootLayout : webView;
        if (postTarget == null) return;
        postTarget.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (appUpdateManager == null) return;
                    appUpdateManager.getAppUpdateInfo().addOnSuccessListener(
                            new com.google.android.gms.tasks.OnSuccessListener<AppUpdateInfo>() {
                                @Override
                                public void onSuccess(AppUpdateInfo info) {
                                    handlePlayAppUpdateInfo(info);
                                }
                            }
                    );
                } catch (Exception ignored) {
                }
            }
        }, Math.max(0L, delayMs));
    }

    private void handlePlayAppUpdateInfo(AppUpdateInfo info) {
        if (info == null) return;
        try {
            int availability = info.updateAvailability();
            boolean available = availability == UpdateAvailability.UPDATE_AVAILABLE;
            if (available) {
                if (!appUpdateBannerDismissedThisSession) {
                    showAppUpdateBannerWhenIntroFinished();
                }
            } else {
                appUpdateBannerPendingShow = false;
                hideAppUpdateBanner();
            }
        } catch (Exception ignored) {
        }
    }

    private void showAppUpdateBannerWhenIntroFinished() {
        if (rootLayout == null) return;
        appUpdateBannerPendingShow = true;
        appUpdateBannerIntroWaitAttempts = 0;
        waitForCoverIntroThenShowAppUpdateBanner();
    }

    private void waitForCoverIntroThenShowAppUpdateBanner() {
        if (!appUpdateBannerPendingShow || appUpdateBannerDismissedThisSession || rootLayout == null) return;
        if (launchOverlay != null) {
            retryAppUpdateBannerAfterIntroWait();
            return;
        }
        if (webView == null) {
            showAppUpdateBanner();
            return;
        }
        try {
            webView.evaluateJavascript(
                    "(function(){try{"
                            + "var r=document.documentElement;"
                            + "var busy=r.classList.contains('oai-cover-booting')||r.classList.contains('oai-cover-revealing')||r.classList.contains('oai-first-entry-intro')||r.classList.contains('oai-background-return-intro')||r.classList.contains('oai-cover-under-intro-reveal')||r.classList.contains('oai-cover-first-reveal');"
                            + "return busy?'wait':'ready';"
                            + "}catch(e){return 'wait';}})();",
                    new android.webkit.ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            String v = value == null ? "" : value.replace("\"", "").trim();
                            if ("ready".equals(v)) {
                                showAppUpdateBanner();
                            } else {
                                retryAppUpdateBannerAfterIntroWait();
                            }
                        }
                    }
            );
        } catch (Exception ignored) {
            retryAppUpdateBannerAfterIntroWait();
        }
    }

    private void retryAppUpdateBannerAfterIntroWait() {
        if (!appUpdateBannerPendingShow || appUpdateBannerDismissedThisSession || rootLayout == null) return;
        appUpdateBannerIntroWaitAttempts++;
        if (appUpdateBannerIntroWaitAttempts >= APP_UPDATE_BANNER_INTRO_WAIT_MAX_ATTEMPTS) {
            showAppUpdateBanner();
            return;
        }
        rootLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                waitForCoverIntroThenShowAppUpdateBanner();
            }
        }, APP_UPDATE_BANNER_INTRO_WAIT_MS);
    }

    private void showAppUpdateBanner() {
        if (rootLayout == null) return;
        rootLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (launchOverlay != null) {
                        retryAppUpdateBannerAfterIntroWait();
                        return;
                    }
                    appUpdateBannerPendingShow = false;
                    hideAppUpdateBanner();
                    applyAppUpdateBannerBackdropBlur(true);
                    appUpdateBanner = new FrameLayout(MainActivity.this);
                    appUpdateBanner.setClickable(true);
                    appUpdateBanner.setBackgroundColor(Color.TRANSPARENT);

                    View backdrop = new View(MainActivity.this);
                    backdrop.setBackgroundColor(Color.argb(108, 255, 252, 244));
                    appUpdateBanner.addView(backdrop, new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    ));

                    LinearLayout card = new LinearLayout(MainActivity.this);
                    card.setOrientation(LinearLayout.VERTICAL);
                    card.setGravity(Gravity.CENTER_HORIZONTAL);
                    card.setPadding(dp(20), dp(18), dp(20), dp(18));
                    card.setClickable(true);
                    card.setBackground(makeRoundedBackground(Color.rgb(255, 252, 244), Color.rgb(190, 143, 48), dp(22), dp(2)));
                    card.setElevation(dp(14));

                    TextView title = new TextView(MainActivity.this);
                    title.setText("새 버전이 있습니다");
                    title.setGravity(Gravity.CENTER);
                    title.setTextColor(Color.rgb(38, 31, 22));
                    title.setTextSize(18f);
                    title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                    card.addView(title, new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    ));

                    TextView body = new TextView(MainActivity.this);
                    body.setText("더 안정적인 사용을 위해\nGoogle Play에서 업데이트해 주세요.");
                    body.setGravity(Gravity.CENTER);
                    body.setTextColor(Color.rgb(78, 67, 51));
                    body.setTextSize(14f);
                    body.setLineSpacing(dp(3), 1.0f);
                    LinearLayout.LayoutParams bodyParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    bodyParams.topMargin = dp(8);
                    card.addView(body, bodyParams);

                    LinearLayout actions = new LinearLayout(MainActivity.this);
                    actions.setOrientation(LinearLayout.HORIZONTAL);
                    actions.setGravity(Gravity.CENTER);
                    LinearLayout.LayoutParams actionsParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    actionsParams.topMargin = dp(16);

                    TextView later = makeBannerAction("나중에", false);
                    TextView update = makeBannerAction("업데이트하기", true);
                    LinearLayout.LayoutParams laterParams = new LinearLayout.LayoutParams(
                            dp(112),
                            dp(42)
                    );
                    LinearLayout.LayoutParams updateParams = new LinearLayout.LayoutParams(
                            dp(128),
                            dp(42)
                    );
                    updateParams.leftMargin = dp(10);
                    actions.addView(later, laterParams);
                    actions.addView(update, updateParams);
                    card.addView(actions, actionsParams);

                    later.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            appUpdateBannerDismissedThisSession = true;
                            hideAppUpdateBanner();
                        }
                    });
                    update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            appUpdateBannerDismissedThisSession = false;
                            hideAppUpdateBanner();
                            openPlayStoreListing();
                        }
                    });

                    int cardWidth = updateBannerCardWidthPx();
                    FrameLayout.LayoutParams cardParams = new FrameLayout.LayoutParams(
                            cardWidth,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    cardParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                    cardParams.topMargin = updateBannerCardTopMarginPx();
                    if (cardWidth == ViewGroup.LayoutParams.MATCH_PARENT) {
                        cardParams.leftMargin = dp(24);
                        cardParams.rightMargin = dp(24);
                    }
                    appUpdateBanner.addView(card, cardParams);

                    FrameLayout.LayoutParams bannerParams = new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    );
                    rootLayout.addView(appUpdateBanner, bannerParams);
                    appUpdateBanner.bringToFront();
                    if (statusBarOverlay != null) statusBarOverlay.bringToFront();
                } catch (Exception ignored) {
                }
            }
        });
    }

    private TextView makeBannerAction(String label, boolean primary) {
        TextView v = new TextView(this);
        v.setText(label);
        v.setGravity(Gravity.CENTER);
        v.setSingleLine(true);
        v.setTextSize(14f);
        v.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        v.setPadding(dp(10), 0, dp(10), 0);
        if (primary) {
            v.setTextColor(Color.WHITE);
            v.setBackground(makeRoundedBackground(Color.rgb(23, 36, 78), Color.rgb(23, 36, 78), dp(21), dp(1)));
        } else {
            v.setTextColor(Color.rgb(84, 67, 37));
            v.setBackground(makeRoundedBackground(Color.rgb(255, 248, 232), Color.rgb(213, 190, 135), dp(21), dp(1)));
        }
        return v;
    }

    private int updateBannerCardWidthPx() {
        try {
            Configuration config = getResources().getConfiguration();
            int screenWidthDp = config != null ? config.screenWidthDp : 0;
            if (screenWidthDp >= 520) return dp(390);
        } catch (Exception ignored) {
        }
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    private int updateBannerCardTopMarginPx() {
        int statusTop = Math.max(0, getStatusBarHeightFallback());
        try {
            Configuration config = getResources().getConfiguration();
            int screenHeightDp = config != null ? config.screenHeightDp : 0;
            int baseDp = 86;
            if (screenHeightDp > 760) baseDp = 104;
            if (isTabletLayout()) baseDp = 112;
            if (isFoldLikeDevice()) baseDp = 92;
            return statusTop + dp(baseDp);
        } catch (Exception ignored) {
            return statusTop + dp(86);
        }
    }

    private GradientDrawable makeRoundedBackground(int color, int strokeColor, int radiusPx, int strokePx) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(color);
        drawable.setCornerRadius(Math.max(0, radiusPx));
        if (strokePx > 0) {
            drawable.setStroke(strokePx, strokeColor);
        }
        return drawable;
    }

    private void hideAppUpdateBanner() {
        appUpdateBannerPendingShow = false;
        try {
            if (appUpdateBanner != null && appUpdateBanner.getParent() instanceof ViewGroup) {
                ((ViewGroup) appUpdateBanner.getParent()).removeView(appUpdateBanner);
            }
        } catch (Exception ignored) {
        }
        appUpdateBanner = null;
        applyAppUpdateBannerBackdropBlur(false);
    }

    private void applyAppUpdateBannerBackdropBlur(boolean enabled) {
        try {
            if (webView == null) return;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (enabled) {
                    webView.setRenderEffect(RenderEffect.createBlurEffect(dp(5), dp(5), Shader.TileMode.CLAMP));
                } else {
                    webView.setRenderEffect(null);
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void openPlayStoreListing() {
        String pkg = getPackageName();
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + pkg));
            intent.setPackage("com.android.vending");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        } catch (Exception ignored) {
        }
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + pkg));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception ignored) {
        }
    }

    private int dp(float value) {
        try {
            return Math.round(value * getResources().getDisplayMetrics().density);
        } catch (Exception ignored) {
            return Math.round(value);
        }
    }

    /**
     * Minimal native share bridge for the cover "공유하기" button.
     * It only opens Android's official share sheet with the provided text; it does not expose user data.
     */
    private void requestGoogleDriveAuthorization(String action, String payload) {
        // Only explicit on-demand reads of previously cloud-stored photos are permitted.
        if (!"photo-choice-read".equals(action) && !"photo-choice-save".equals(action) && !"visit-photo-load".equals(action) && !"data-transfer-save".equals(action) && !"data-transfer-load".equals(action) && !"dev-photo-backup-test".equals(action) && !"dev-photo-backup-verify".equals(action) && !"dev-photo-upload-audit".equals(action) && !"visit-photo-auto".equals(action) && !"visit-photo-silent".equals(action)) return;
        final String safeAction = action == null ? "" : action;
        final String safePayload = payload == null ? "" : payload;
        long now = System.currentTimeMillis();
        if (cachedDriveAccessToken != null && cachedDriveAccessToken.length() > 0 && now - cachedDriveAccessTokenAt < DRIVE_TOKEN_CACHE_MS) {
            executeGoogleDriveAction(safeAction, safePayload, cachedDriveAccessToken);
            return;
        }
        pendingDriveQueue.add(new String[]{safeAction, safePayload});
        if (driveAuthorizationInFlight) return;
        driveAuthorizationInFlight = true;
        List<Scope> scopes = new ArrayList<>();
        scopes.add(new Scope(GOOGLE_DRIVE_APPDATA_SCOPE));
        AuthorizationRequest request = AuthorizationRequest.builder().setRequestedScopes(scopes).build();
        Identity.getAuthorizationClient(this).authorize(request)
                .addOnSuccessListener(new OnSuccessListener<AuthorizationResult>() {
                    @Override public void onSuccess(AuthorizationResult result) {
                        if (result.hasResolution()) {
                            // A background/automatic check must never open consent UI.
                            boolean onlySilent=true;
                            for(String[] pending:pendingDriveQueue)
                                if(pending!=null&&pending.length>0&&!"visit-photo-silent".equals(pending[0])&&!"photo-choice-read".equals(pending[0]))onlySilent=false;
                            if(onlySilent){driveAuthorizationInFlight=false;pendingDriveQueue.clear();notifyPhotoChoice("unknown","");return;}
                            try {
                                startIntentSenderForResult(result.getPendingIntent().getIntentSender(), REQ_GOOGLE_DRIVE_AUTH, null, 0, 0, 0);
                            } catch (IntentSender.SendIntentException e) { driveAuthorizationInFlight=false; failPendingDriveQueue("Google Drive 연결을 시작하지 못했습니다."); }
                        } else {
                            continueGoogleDriveAction(result);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override public void onFailure(Exception e) { driveAuthorizationInFlight=false; failPendingDriveQueue("Google Drive 권한을 확인하지 못했습니다."); }
                });
    }

    private void continueGoogleDriveAction(final AuthorizationResult result) {
        final String token = result == null ? null : result.getAccessToken();
        driveAuthorizationInFlight = false;
        if (token == null || token.length() == 0) { failPendingDriveQueue("Google Drive 권한을 확인하지 못했습니다."); return; }
        cachedDriveAccessToken = token;
        cachedDriveAccessTokenAt = System.currentTimeMillis();
        // Never persist bearer tokens or silently launch an authorization dialog.
        ArrayList<String[]> queued = new ArrayList<>(pendingDriveQueue);
        pendingDriveQueue.clear();
        // A developer's read-only audit must not itself start automatic uploads.
        boolean auditOnly = !queued.isEmpty();
        for (String[] item : queued) {
            if (item == null || item.length < 2) continue;
            if (!"dev-photo-upload-audit".equals(item[0])) auditOnly = false;
            executeGoogleDriveAction(item[0], item[1], token);
        }
        if (!auditOnly) {
            runAuthorizedVisitPhotoUploads();
            runAuthorizedVisitPhotoRestores();
        }
    }

    // All Drive reads and writes are processed in submission order.
    private final ExecutorService driveOperationExecutor = Executors.newSingleThreadExecutor();
    // Only automatic restore downloads use two network workers. Uploads remain serialized.
    private final ExecutorService photoRestoreExecutor = Executors.newFixedThreadPool(4);
    // Inventory indexing must not wait behind large Drive uploads or remote listing.
    private final ExecutorService photoManifestExecutor = Executors.newSingleThreadExecutor();
    private final Object photoRestoreClaimLock = new Object();
    private final java.util.HashSet<String> photoRestoreClaims = new java.util.HashSet<>();
    // Image reads are independent of network transfers and never run on the UI thread.
    private final ExecutorService visitPhotoReadExecutor = Executors.newSingleThreadExecutor();

    // Explicit consent from the existing Drive settings is required. Never infer it from
    // a cached authorization token, which also serves ordinary visit-record backups.
    private volatile boolean visitPhotoAutoEnabled = false;
    private volatile boolean visitPhotoWifiOnly = true;
    private volatile long lastPhotoSilentAuthAttemptAt = 0L;
    private volatile boolean visitPhotoAppVisible = true;
    private boolean isVisitPhotoCloudDisabled(){ return !visitPhotoAutoEnabled; }
    private boolean visitPhotoNetworkAllowed(){
        try {
            android.net.ConnectivityManager cm = (android.net.ConnectivityManager)
                getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
            if(cm==null)return false;
            android.net.Network network=cm.getActiveNetwork();
            if(network==null)return false;
            android.net.NetworkCapabilities caps=cm.getNetworkCapabilities(network);
            return caps!=null && caps.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
                && (!visitPhotoWifiOnly || caps.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI));
        }catch(Exception e){return false;}
    }
    // No WorkManager job is enqueued here: the legacy Worker has no user-scoped
    // authorization and must never pretend to have completed a Drive transfer.
    private final java.util.concurrent.atomic.AtomicBoolean visitPhotoDispatchQueued =
        new java.util.concurrent.atomic.AtomicBoolean(false);
    // Foreground event-driven entry point: do not poll, start a Worker, or open consent UI.
    // Calls into Google Identity are made from the activity thread only.
    private void resumeAuthorizedVisitPhotoTransfers(){
        if(isVisitPhotoCloudDisabled() || !visitPhotoNetworkAllowed())return;
        if(cachedDriveAccessToken==null || cachedDriveAccessToken.isEmpty()
                || System.currentTimeMillis()-cachedDriveAccessTokenAt>=DRIVE_TOKEN_CACHE_MS){
            requestSilentPhotoAuthorizationIfAppropriate();
            return;
        }
        runAuthorizedVisitPhotoUploads();
        runAuthorizedVisitPhotoRestores();
        scheduleVisitPhotoRetryTick();
    }
    private void requestSilentPhotoAuthorizationIfAppropriate(){
        if(isVisitPhotoCloudDisabled()||!visitPhotoNetworkAllowed())return;
        if(visitPhotoTransferPolicy.blocked(VisitPhotoTransferPolicy.Job.NEW_UPLOAD)
                !=VisitPhotoTransferPolicy.Block.NONE)return;
        if(cachedDriveAccessToken!=null && !cachedDriveAccessToken.isEmpty()
           && System.currentTimeMillis()-cachedDriveAccessTokenAt<DRIVE_TOKEN_CACHE_MS)return;
        // Do not join an unrelated developer read-only authorization request.
        if(driveAuthorizationInFlight)return;
        long now=System.currentTimeMillis();
        // Bounded cooldown prevents authorization storms after a silent failure.
        if(now-lastPhotoSilentAuthAttemptAt<120000L)return;
        lastPhotoSilentAuthAttemptAt=now;
        requestGoogleDriveAuthorization("visit-photo-silent","");
    }
    private void runAuthorizedVisitPhotoUploads(){
        if(isVisitPhotoCloudDisabled() || !visitPhotoNetworkAllowed()
            || System.currentTimeMillis()<visitPhotoTransferErrorUntil)return;
        if(visitPhotoTransferPolicy.blocked(VisitPhotoTransferPolicy.Job.NEW_UPLOAD)
                != VisitPhotoTransferPolicy.Block.NONE)return;
        final String token=cachedDriveAccessToken;
        if(token==null||token.isEmpty() || System.currentTimeMillis()-cachedDriveAccessTokenAt>=DRIVE_TOKEN_CACHE_MS)return;
        if(!visitPhotoDispatchQueued.compareAndSet(false,true))return;
        driveOperationExecutor.execute(new Runnable(){@Override public void run(){
            boolean remaining=false;
            try {
                try(VisitPhotoTransferIndex index=new VisitPhotoTransferIndex(getApplicationContext())){
                    remaining=index.nextUploadCandidate(System.currentTimeMillis())!=null;
                }
                visitPhotoTransferPolicy.setNewUploadPending(remaining);
                if(remaining && !isVisitPhotoCloudDisabled() && visitPhotoNetworkAllowed())
                    syncPendingVisitPhotosToDrive(token);
                else if(!isVisitPhotoCloudDisabled() && visitPhotoNetworkAllowed()
                    && visitPhotoTransferPolicy.blocked(VisitPhotoTransferPolicy.Job.NEW_UPLOAD)
                        ==VisitPhotoTransferPolicy.Block.NONE)
                    uploadOneVisitPhotoThumbnail(token);
            }finally {
                visitPhotoDispatchQueued.set(false);
                try(VisitPhotoTransferIndex index=new VisitPhotoTransferIndex(getApplicationContext())){
                    remaining=index.nextUploadCandidate(System.currentTimeMillis())!=null
                        ||index.nextThumbnailUploadCandidate(System.currentTimeMillis())!=null;
                }catch(Exception ignored){remaining=false;}
                visitPhotoTransferPolicy.setNewUploadPending(remaining);
                // Schedule the next upload instead of recursively occupying the Drive executor.
                // Deferred uploads must not starve the bulk restore queue.
                if(remaining && System.currentTimeMillis()>=visitPhotoTransferErrorUntil)
                    retryPendingVisitPhotoSyncIfAuthorized(1500L);
                else runAuthorizedVisitPhotoRestores();
            }
        }});
    }

    // The app-process retry heartbeat only resubmits eligible, consented work.
    // It performs no photo IO itself, and stops when this Activity is destroyed.
    // Process death is NOT a durable background transfer; an independent Worker
    // still requires a separate authorization-safe implementation.
    private final android.os.Handler visitPhotoRetryHandler =
        new android.os.Handler(android.os.Looper.getMainLooper());
    private boolean visitPhotoRetryArmed = false;
    private final Runnable visitPhotoRetryTick = new Runnable(){@Override public void run(){
        visitPhotoRetryArmed=false;
        if(isFinishing() || isDestroyed() || isVisitPhotoCloudDisabled())return;
        // GPS/map and user-requested photos retain their priority. Never spin.
        if(visitPhotoNetworkAllowed()){
            // Revisit upload admission even while bulk restore has lower priority.
            // Otherwise a stale upload flag can prevent the retry tick from
            // re-entering the very upload path that clears that flag.
            if(visitPhotoAppVisible){
                resumeAuthorizedVisitPhotoTransfers();
            }else if(cachedDriveAccessToken!=null && !cachedDriveAccessToken.isEmpty()
                    && System.currentTimeMillis()-cachedDriveAccessTokenAt<DRIVE_TOKEN_CACHE_MS){
                // No new authorization in background; normal policy checks remain.
                runAuthorizedVisitPhotoUploads();
                runAuthorizedVisitPhotoRestores();
            }
        }
        scheduleVisitPhotoRetryTick();
    }};
    private void scheduleVisitPhotoRetryTick(){
        if(visitPhotoRetryArmed || isFinishing() || isDestroyed() || isVisitPhotoCloudDisabled())return;
        visitPhotoRetryArmed=true;
        visitPhotoRetryHandler.postDelayed(visitPhotoRetryTick,120000L);
    }
    private final java.util.concurrent.atomic.AtomicBoolean visitPhotoRestoreDispatchQueued =
        new java.util.concurrent.atomic.AtomicBoolean(false);
    private volatile long visitPhotoTransferErrorUntil = 0L;
    // Shared only within the serialized Drive executor; discarded on ambiguous failures.
    private volatile java.util.Map<String,java.util.ArrayList<String>> sessionDrivePhotoIndex;
    private synchronized java.util.Map<String,java.util.ArrayList<String>> getSessionDrivePhotoIndex(String token) throws Exception {
        if(sessionDrivePhotoIndex==null)sessionDrivePhotoIndex=listDevPhotoBackupIndex(token);
        return sessionDrivePhotoIndex;
    }
    // Cloud thumbnail objects are independent from the full-resolution photo.
    // Deterministic name prevents dependence on another metadata/Drive ID migration.
    private String visitPhotoThumbCloudName(String photoId){
        return "cgm-visit-thumb-"+photoId.replace("local_","")+".jpg";
    }
    private String findThumbnailCloudId(String token,String photoId) throws Exception {
        java.util.ArrayList<String> matches=getSessionDrivePhotoIndex(token).get(visitPhotoThumbCloudName(photoId));
        // Ambiguous names must never silently pick an unrelated object.
        return matches!=null&&matches.size()==1?matches.get(0):"";
    }
    // Upload one missing independent thumbnail. Original upload status is never rolled back.
    private boolean uploadOneVisitPhotoThumbnail(String token) {
        try(VisitPhotoTransferIndex index=new VisitPhotoTransferIndex(getApplicationContext())) {
            String photoId=index.nextThumbnailUploadCandidate(System.currentTimeMillis());
            if(photoId==null)return false;
            if(isLocalPhotoDeleted(photoId)){index.thumbUploaded(photoId);return true;}
            byte[] thumb=readPendingVisitPhoto(photoId,true);
            if(thumb==null||thumb.length==0)thumb=readVisitPhotoCache(photoId,true);
            if(thumb==null||thumb.length==0){
                // A cloud-only photo may not be restored yet; never block other transfers.
                index.thumbDeferred(photoId,System.currentTimeMillis()+3600000L);
                return index.nextThumbnailUploadCandidate(System.currentTimeMillis())!=null;
            }
            try {
                String name=visitPhotoThumbCloudName(photoId);
                java.util.Map<String,java.util.ArrayList<String>> cloud=getSessionDrivePhotoIndex(token);
                java.util.ArrayList<String> matches=cloud.get(name);
                String remote="";
                if(matches!=null && !matches.isEmpty()) {
                    for(String match:matches){if(drivePhotoMatchesLocal(token,match,thumb)){remote=match;break;}}
                    if(remote.length()==0)throw new java.io.IOException("Existing thumbnail filename collision");
                }else remote=createVisitPhotoFile(token,name,thumb);
                if(remote==null||remote.length()==0)throw new java.io.IOException("Thumbnail Drive ID missing");
                java.util.ArrayList<String> ids=new java.util.ArrayList<>();ids.add(remote);cloud.put(name,ids);
                index.thumbUploaded(photoId);
            }catch(Exception e){
                sessionDrivePhotoIndex=null;
                index.thumbDeferred(photoId,System.currentTimeMillis()+60000L);
                visitPhotoTransferErrorUntil=System.currentTimeMillis()+60000L;
                return false;
            }
            return index.nextThumbnailUploadCandidate(System.currentTimeMillis())!=null;
        }catch(Exception e){return false;}
    }
    private boolean validDurableVisitPhoto(File file){
        if(file==null||!file.isFile()||file.length()<100)return false;
        try{
            BitmapFactory.Options o=new BitmapFactory.Options();o.inJustDecodeBounds=true;
            BitmapFactory.decodeFile(file.getAbsolutePath(),o);
            return o.outWidth>0&&o.outHeight>0;
        }catch(Exception e){return false;}
    }
    private boolean quickVisitPhotoFilesPresent(String photoId){
        // Cheap filesystem check for a 20k-photo manifest, without decoding images.
        File p=pendingVisitPhotoFile(photoId,false),pt=pendingVisitPhotoFile(photoId,true);
        if(p.isFile()&&p.length()>100&&pt.isFile()&&pt.length()>100)return true;
        File f=visitPhotoStoreFile(photoId,false),t=visitPhotoStoreFile(photoId,true);
        return f.isFile()&&f.length()>100&&t.isFile()&&t.length()>100;
    }
    private boolean restoredPhotoReady(String photoId){
        File local=pendingVisitPhotoFile(photoId,false);
        File localThumb=pendingVisitPhotoFile(photoId,true);
        if(validDurableVisitPhoto(local)&&validDurableVisitPhoto(localThumb))return true;
        File full=visitPhotoStoreFile(photoId,false),thumb=visitPhotoStoreFile(photoId,true);
        if(!validDurableVisitPhoto(full))return false;
        if(!validDurableVisitPhoto(thumb)) {
            byte[] fullBytes=readVisitPhotoFile(full);
            if(fullBytes!=null){
                byte[] generated=makeVisitPhotoThumbnail(fullBytes);
                if(generated!=null && generated.length>0)cacheVisitPhotoBytes(photoId,generated,true);
            }
        }
        return validDurableVisitPhoto(thumb);
    }
    private void runAuthorizedVisitPhotoRestores(){
        if(isVisitPhotoCloudDisabled()||!visitPhotoNetworkAllowed())return;
        if(visitPhotoTransferPolicy.blocked(VisitPhotoTransferPolicy.Job.BULK_RESTORE)
              !=VisitPhotoTransferPolicy.Block.NONE)return;
        if(System.currentTimeMillis()<visitPhotoTransferErrorUntil)return;
        final String token=cachedDriveAccessToken;
        if(token==null||token.isEmpty() || System.currentTimeMillis()-cachedDriveAccessTokenAt>=DRIVE_TOKEN_CACHE_MS)return;
        if(!visitPhotoRestoreDispatchQueued.compareAndSet(false,true))return;
        final int workers=visitPhotoTransferPolicy.slowRestoreMode()?1:4;
        final java.util.concurrent.atomic.AtomicInteger remainingWorkers=new java.util.concurrent.atomic.AtomicInteger(workers);
        for(int i=0;i<workers;i++){
            // Two shared workers may restore full photos only when thumbnails are clear.
            final boolean thumbnailOnly=workers==1 ? true : i>=2;
            photoRestoreExecutor.execute(new Runnable(){@Override public void run(){
            try{
                while(visitPhotoNetworkAllowed() && !isVisitPhotoCloudDisabled()
                      && System.currentTimeMillis()>=visitPhotoTransferErrorUntil
                      && visitPhotoTransferPolicy.blocked(VisitPhotoTransferPolicy.Job.BULK_RESTORE)
                          ==VisitPhotoTransferPolicy.Block.NONE){
                    if(!restoreOneMissingVisitPhoto(token,thumbnailOnly)){
                        // One slow worker must process originals after thumbnails clear.
                        if(workers!=1 || !restoreOneMissingVisitPhoto(token,false))break;
                    }
                }
            }finally{
                if(remainingWorkers.decrementAndGet()==0){
                    visitPhotoRestoreDispatchQueued.set(false);
                    // A worker can see no unclaimed candidate while another worker
                    // holds the last visible row. Recheck the durable queue after
                    // every worker has released its claim, without a tight loop.
                    if(visitPhotoNetworkAllowed() && !isVisitPhotoCloudDisabled()
                            && System.currentTimeMillis()>=visitPhotoTransferErrorUntil
                            && visitPhotoTransferPolicy.blocked(VisitPhotoTransferPolicy.Job.BULK_RESTORE)
                                ==VisitPhotoTransferPolicy.Block.NONE){
                        try(VisitPhotoTransferIndex index=new VisitPhotoTransferIndex(getApplicationContext())){
                            long now=System.currentTimeMillis();
                            if(index.nextThumbnailRestore(now)!=null || index.nextRestore(now)!=null){
                                retryPendingVisitPhotoSyncIfAuthorized(1500L);
                            }
                        }catch(Exception e){
                            Log.w("VisitPhotoRestore","Could not check remaining restore queue",e);
                            retryPendingVisitPhotoSyncIfAuthorized(120000L);
                        }
                    }
                }
            }
        }});
        }
    }
    // Reserve only the requested candidate ID; SQLite remains the durable source of truth.
    // The reservation is in-memory and released after every success, pause or failure.
    private VisitPhotoTransferIndex.RestoreCandidate claimRestoreCandidate(
            VisitPhotoTransferIndex index,boolean thumbnail){
        synchronized(photoRestoreClaimLock){
            VisitPhotoTransferIndex.RestoreCandidate item=thumbnail
                ?index.nextThumbnailRestoreExcluding(System.currentTimeMillis(),photoRestoreClaims)
                :index.nextRestoreExcluding(System.currentTimeMillis(),photoRestoreClaims);
            if(item!=null)photoRestoreClaims.add(item.photoId);
            return item;
        }
    }
    private boolean restoreOneMissingVisitPhoto(String token,boolean thumbnailOnly){
        String claimedId=null;
        try(VisitPhotoTransferIndex index=new VisitPhotoTransferIndex(getApplicationContext())){
            // First pass: small thumbnails across the entire manifest, before full photos.
            VisitPhotoTransferIndex.RestoreCandidate small=claimRestoreCandidate(index,true);
            if(small!=null)claimedId=small.photoId;
            if(small!=null){
                if(isLocalPhotoDeleted(small.photoId)){
                    index.finishThumbnailRestore(small.photoId,false);
                    index.deferRestore(small.photoId,Long.MAX_VALUE);
                }else if(!validDurableVisitPhoto(visitPhotoStoreFile(small.photoId,true))
                      && !validDurableVisitPhoto(pendingVisitPhotoFile(small.photoId,true))){
                    String thumbId=findThumbnailCloudId(token,small.photoId);
                    if(thumbId.length()>0){
                        byte[] bytes=requestDriveBytes(token,"GET",
                            "https://www.googleapis.com/drive/v3/files/"+Uri.encode(thumbId)+"?alt=media",true);
                        if(visitPhotoTransferPolicy.interruptExistingBulkRestore())return false;
                        if(bytes==null||bytes.length<100||bytes.length>1024*1024)
                            throw new java.io.IOException("Invalid thumbnail payload size");
                        BitmapFactory.Options bounds=new BitmapFactory.Options();bounds.inJustDecodeBounds=true;
                        BitmapFactory.decodeByteArray(bytes,0,bytes.length,bounds);
                        if(bounds.outWidth<=0||bounds.outHeight<=0||Math.max(bounds.outWidth,bounds.outHeight)>720)
                            throw new java.io.IOException("Invalid thumbnail dimensions");
                        cacheVisitPhotoBytes(small.photoId,bytes,true);
                        if(!validDurableVisitPhoto(visitPhotoStoreFile(small.photoId,true)))
                            throw new java.io.IOException("Thumbnail was not durably stored");
                    }
                }
                // A missing cloud thumbnail is not a completed download.
                // The original restore path can generate a local thumbnail.
                boolean savedThumb=validDurableVisitPhoto(visitPhotoStoreFile(small.photoId,true))
                    ||validDurableVisitPhoto(pendingVisitPhotoFile(small.photoId,true));
                index.finishThumbnailRestore(small.photoId,savedThumb);
                return index.nextThumbnailRestore(System.currentTimeMillis())!=null
                        ||index.nextRestore(System.currentTimeMillis())!=null;
            }
            // The third worker handles thumbnails only, preserving the two-original limit.
            if(thumbnailOnly)return false;
            // Do not begin full-photo downloads while another worker is still
            // restoring thumbnails; thumbnails have strict global priority.
            if(index.nextThumbnailRestore(System.currentTimeMillis())!=null)return false;
            VisitPhotoTransferIndex.RestoreCandidate item=claimRestoreCandidate(index,false);
            if(item!=null)claimedId=item.photoId;
            if(item==null){
                synchronized(photoRestoreClaimLock){
                    if(photoRestoreClaims.isEmpty())visitPhotoTransferPolicy.setBulkRestorePending(false);
                }
                // Legacy Drive photos gained local thumbnails during restore. Queue their
                // separate cloud backup without altering the original restored objects.
                synchronized(photoRestoreClaimLock){
                    if(photoRestoreClaims.isEmpty())runAuthorizedVisitPhotoUploads();
                }
                return false;
            }
            String photoId=item.photoId, remote=item.driveId;
            if(isLocalPhotoDeleted(photoId)){
                // Permanently skip a user-deleted photo without stopping the rest of the queue.
                index.deferRestore(photoId,Long.MAX_VALUE);
                return index.nextRestore(System.currentTimeMillis())!=null;
            }
            if(restoredPhotoReady(photoId)){
                index.markRestored(photoId);
            }else{
                if(remote==null||remote.isEmpty()){
                    String name="cgm-visit-"+photoId.replace("local_","")+".jpg";
                    java.util.ArrayList<String> matching=getSessionDrivePhotoIndex(token).get(name);
                    // Never guess which file to use if remote names collide.
                    if(matching==null||matching.size()!=1){
                        index.deferRestore(photoId,System.currentTimeMillis()+3600000L);
                        return index.nextRestore(System.currentTimeMillis())!=null;
                    }
                    remote=matching.get(0);
                }
                byte[] downloaded=requestDriveBytes(token,"GET",
                    "https://www.googleapis.com/drive/v3/files/"+Uri.encode(remote)+"?alt=media",true);
                // A GPS/map transition may have occurred while the response arrived.
                // Keep the queue pending; never write an interrupted bulk download.
                if(visitPhotoTransferPolicy.interruptExistingBulkRestore())return false;
                if(downloaded==null||downloaded.length<100||downloaded.length>12*1024*1024)
                    throw new java.io.IOException("Unexpected photo payload size");
                BitmapFactory.Options bounds=new BitmapFactory.Options();bounds.inJustDecodeBounds=true;
                BitmapFactory.decodeByteArray(downloaded,0,downloaded.length,bounds);
                if(bounds.outWidth<=0||bounds.outHeight<=0)throw new java.io.IOException("Invalid photo format");
                if(visitPhotoTransferPolicy.interruptExistingBulkRestore())return false;
                cacheVisitPhotoBytes(photoId,downloaded,false);
                if(!validDurableVisitPhoto(visitPhotoStoreFile(photoId,true))){
                    byte[] thumb=makeVisitPhotoThumbnail(downloaded);
                    cacheVisitPhotoBytes(photoId,thumb,true);
                }
                if(!restoredPhotoReady(photoId))throw new java.io.IOException("Incomplete durable restore");
                // SQLite is the efficient remote mapping for a bulk restore.
                // Do not reserialize a 20,000-entry SharedPreferences JSON per photo.
                index.uploaded(photoId,remote);
                index.markRestored(photoId);
            }
            return index.nextRestore(System.currentTimeMillis())!=null;
        }catch(java.io.InterruptedIOException interrupted){
            // GPS/map/stamp-list interruption is expected, not a network failure.
            // Leave the restore entry pending and resume on the next allowed activity event.
            return false;
        }catch(Exception e){
            // Distinguish a single bad/missing photo from an unavailable Drive service.
            // A malformed photo or a missing cloud object must not stop other photos.
            String cause=String.valueOf(e.getMessage());
            boolean serviceFailure=cause.contains("Drive 401")||cause.contains("Drive 403")
                ||cause.contains("Drive 429")||cause.matches(".*Drive 5[0-9][0-9].*")
                ||e instanceof java.net.SocketTimeoutException
                ||e instanceof java.net.UnknownHostException
                ||e instanceof java.net.ConnectException;
            if(claimedId!=null){
                try(VisitPhotoTransferIndex index=new VisitPhotoTransferIndex(getApplicationContext())){
                    long delay=index.deferRestoreWithBackoff(claimedId,System.currentTimeMillis());
                    // A future retry must be scheduled even when all remaining entries are deferred.
                    retryPendingVisitPhotoSyncIfAuthorized(delay+1000L);
                }catch(Exception dbError){
                    Log.w("VisitPhotoRestore","Could not defer an individual photo",dbError);
                    serviceFailure=true;
                }
            }
            if(claimedId==null)serviceFailure=true; // Never spin if queue/index access failed.
            if(serviceFailure){
                visitPhotoTransferErrorUntil=System.currentTimeMillis()+60000L;
                sessionDrivePhotoIndex=null;
                if(cause.contains("Drive 401")){
                    cachedDriveAccessToken="";cachedDriveAccessTokenAt=0L;
                }
                retryPendingVisitPhotoSyncIfAuthorized(61000L);
                return false;
            }
            Log.w("VisitPhotoRestore","Individual photo deferred; other photos continue: "+devPhotoFailureKind(e));
            // Do not immediately reclaim this photo: its durable retry time is in SQLite.
            return true;
        }finally{
            if(claimedId!=null){
                synchronized(photoRestoreClaimLock){photoRestoreClaims.remove(claimedId);}
            }
        }
    }

    private void executeGoogleDriveAction(final String action, final String payload, final String token) {
        // v319 experiment: suspend all visit-photo Drive operations; local GPS/visits remain intact.
        if (isVisitPhotoCloudDisabled() && !action.startsWith("photo-choice-") && !action.startsWith("data-transfer-") && !"dev-photo-backup-test".equals(action) && !"dev-photo-backup-verify".equals(action) && !"dev-photo-upload-audit".equals(action)) return;
        if ("visit-photo-auto".equals(action)||"visit-photo-silent".equals(action)) {
            runAuthorizedVisitPhotoUploads();runAuthorizedVisitPhotoRestores();return;
        }
        if ("connect".equals(action)) { notifyDriveStatus("connected", "사진 저장소가 연결되었습니다."); runAuthorizedVisitPhotoUploads(); return; }
        driveOperationExecutor.execute(new Runnable() {
            @Override public void run() {
                try {
                    if ("photo-choice-read".equals(action)) { loadPhotoChoice(token); }
                    else if ("photo-choice-save".equals(action)) { savePhotoChoice(token,payload); }
                    else if ("data-transfer-save".equals(action)) { saveTransferData(token,payload); }
                    else if ("data-transfer-load".equals(action)) { loadTransferData(token); }
                    else if ("visit-photo-upload".equals(action)) { uploadVisitPhotosToDrive(token, payload); }
                    else if ("dev-photo-backup-test".equals(action)) { performDevPhotoBackupTest(token); }
                    else if ("dev-photo-backup-verify".equals(action)) { performDevPhotoBackupVerify(token); }
                    else if ("dev-photo-upload-audit".equals(action)) { performDevPhotoUploadAudit(token); }
                    else if ("visit-photo-sync-all".equals(action)) { syncPendingVisitPhotosToDrive(token); }
                    else if ("visit-photo-load".equals(action)) { loadVisitPhotoFromDrive(token, payload); }
                    else if ("visit-photo-delete".equals(action)) { deleteVisitPhotoFromDrive(token, payload); }
                } catch (Exception e) {
                    if (String.valueOf(e.getMessage()).contains("Drive 401")) { cachedDriveAccessToken = ""; cachedDriveAccessTokenAt = 0L; visitPhotoPrefs().edit().remove(PREF_WORKER_DRIVE_TOKEN).remove(PREF_WORKER_DRIVE_TOKEN_AT).apply(); }
                    if (action.startsWith("photo-choice-")) notifyPhotoChoice("unknown","");
                    else if (action.startsWith("data-transfer-")) notifyDataTransferResult(action.endsWith("save")?"save":"load",false,"","Google Drive 데이터 작업을 완료하지 못했습니다.");
                    else notifyDriveActionFailure(action, payload);
                }
            }
        });
    }

    private void notifyDriveActionFailure(String action, String payload) {
        if ("dev-photo-backup-test".equals(action)) { notifyDevPhotoBackupTest("error", "Google Drive 연결 또는 전송에 실패했습니다."); return; }
        if ("dev-photo-backup-verify".equals(action)) { notifyDevPhotoVerify("error", "Google Drive 연결 또는 다운로드에 실패했습니다."); return; }
        if ("dev-photo-upload-audit".equals(action)) { notifyDevPhotoUploadAudit("error", "Google Drive 연결 또는 읽기 확인에 실패했습니다."); return; }
        if ("visit-photo-sync-all".equals(action)) { return; }
        if (action != null && action.startsWith("visit-photo-")) {
            try {
                JSONObject p = new JSONObject(payload);String req=p.optString("requestId","");
                if ("visit-photo-load".equals(action)) notifyVisitPhotoLoaded(req,false,"","","사진을 불러오지 못했습니다.");
                else if ("visit-photo-delete".equals(action)) notifyVisitPhotoDeleteResult(req,false,"사진을 삭제하지 못했습니다.");
                else notifyVisitPhotoUploadResult(req,false,"[]","사진을 저장하지 못했습니다.");
            } catch(Exception ignored) {}
        } else notifyDriveStatus("error", "사진 저장소 작업을 완료하지 못했습니다.");
    }

    private void failPendingDriveQueue(String message) {
        ArrayList<String[]> queued = new ArrayList<>(pendingDriveQueue);pendingDriveQueue.clear();
        for(String[] item:queued){if(item==null||item.length<2)continue;String action=item[0],payload=item[1];if(action.startsWith("photo-choice-")){notifyPhotoChoice("unknown","");continue;}if("dev-photo-backup-test".equals(action)){notifyDevPhotoBackupTest("error",message);continue;}if("dev-photo-backup-verify".equals(action)){notifyDevPhotoVerify("error",message);continue;}if("dev-photo-upload-audit".equals(action)){notifyDevPhotoUploadAudit("error",message);continue;}if("visit-photo-sync-all".equals(action))continue;if(action.startsWith("data-transfer-")){notifyDataTransferResult(action.endsWith("save")?"save":"load",false,"",message);continue;}if(action.startsWith("visit-photo-")){try{JSONObject p=new JSONObject(payload);String req=p.optString("requestId","");if("visit-photo-load".equals(action))notifyVisitPhotoLoaded(req,false,"","",message);else if("visit-photo-delete".equals(action))notifyVisitPhotoDeleteResult(req,false,message);else notifyVisitPhotoUploadResult(req,false,"[]",message);}catch(Exception ignored){}}else notifyDriveStatus("error",message);}
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_VISIT_PHOTO_PICKER) { handleVisitPhotoPickerResult(resultCode, data); return; }
        if (requestCode != REQ_GOOGLE_DRIVE_AUTH) return;
        if (resultCode != RESULT_OK || data == null) { driveAuthorizationInFlight=false;failPendingDriveQueue("Google Drive 연결이 취소되었습니다.");return; }
        try { continueGoogleDriveAction(Identity.getAuthorizationClient(this).getAuthorizationResultFromIntent(data)); }
        catch (ApiException e) { driveAuthorizationInFlight=false;failPendingDriveQueue("Google Drive 연결이 취소되었습니다."); }
    }

    private static String normalizedVisitPhotoFilename(String value) {
        String name=value==null?"":value.trim();
        if(name.length()==0 || "방문사진.jpg".equals(name))return ""; // Legacy placeholder was never a source filename.
        return name.toLowerCase(java.util.Locale.ROOT);
    }

    private String getSelectedVisitPhotoFilename(Uri uri) {
        if(uri==null)return "";
        try(android.database.Cursor c=getContentResolver().query(uri,
                new String[]{android.provider.OpenableColumns.DISPLAY_NAME},null,null,null)) {
            if(c!=null&&c.moveToFirst())return String.valueOf(c.getString(0)==null?"":c.getString(0));
        }catch(Exception ignored){}
        return ""; // Unknown source filename: do not reject a legitimate photo.
    }

    private void startVisitPhotoPicker(final String requestId, final int maxCount, final String existingNamesJson) {
        pendingVisitPhotoRequestId=requestId==null?"":requestId;
        pendingVisitPhotoMaxCount=Math.max(1,Math.min(15,maxCount));
        pendingVisitPhotoExistingNames.clear();
        try {
            JSONArray existing=new JSONArray(existingNamesJson==null?"[]":existingNamesJson);
            for(int i=0;i<existing.length()&&i<15;i++) {
                String normalized=normalizedVisitPhotoFilename(existing.optString(i,""));
                if(!normalized.isEmpty())pendingVisitPhotoExistingNames.add(normalized);
            }
        }catch(Exception ignored){}
        try {
            // 갤러리 선택 방식 유지: ACTION_PICK_IMAGES는 Google 포토 형태의
            // 시스템 포토 피커를 열 수 있으므로 사용하지 않는다.
            // 갤러리 앱이 선택 한도를 지원하지 않더라도 결과 등록 단계에서
            // maxCount와 파일명 중복을 검사하여 초과 등록을 방지한다.
            Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
            startActivityForResult(intent,REQ_VISIT_PHOTO_PICKER);
        } catch (Exception e) {
            notifyVisitPhotoUploadResult(pendingVisitPhotoRequestId,false,"[]","사진 선택 화면을 열지 못했습니다.");
            pendingVisitPhotoRequestId="";pendingVisitPhotoMaxCount=0;pendingVisitPhotoExistingNames.clear();
        }
    }

    private void handleVisitPhotoPickerResult(int resultCode, Intent data) {
        final String requestId=pendingVisitPhotoRequestId;
        final int maxCount=pendingVisitPhotoMaxCount;
        final HashSet<String> existingNames=new HashSet<>(pendingVisitPhotoExistingNames);
        pendingVisitPhotoRequestId="";pendingVisitPhotoMaxCount=0;pendingVisitPhotoExistingNames.clear();
        if(requestId.length()==0)return;
        if(resultCode!=RESULT_OK||data==null){notifyVisitPhotoLocalComplete(requestId,true,0,0,"");return;}
        final ArrayList<Uri> uris=new ArrayList<>();
        try{
            ClipData clip=data.getClipData();
            if(clip!=null){
                for(int i=0;i<clip.getItemCount();i++){
                    Uri u=clip.getItemAt(i).getUri();if(u!=null)uris.add(u);
                }
            }else if(data.getData()!=null)uris.add(data.getData());
        }catch(Exception ignored){}
        if(uris.isEmpty()){notifyVisitPhotoLocalComplete(requestId,false,0,0,"선택한 사진을 확인하지 못했습니다.");return;}
        visitPhotoTransferPolicy.setPhotoProcessing(true);
        new Thread(new Runnable(){@Override public void run(){
            try { prepareVisitPhotosLocalFirst(requestId,uris,maxCount,existingNames); }
            finally {
                visitPhotoTransferPolicy.setPhotoProcessing(false);
                runOnUiThread(new Runnable(){@Override public void run(){
                    resumeAuthorizedVisitPhotoTransfers();
                }});
            }
        }}).start();
    }

    private static class PreparedVisitPhoto { final byte[] full; final byte[] thumb; PreparedVisitPhoto(byte[] f,byte[] t){full=f;thumb=t;} }

    private void prepareVisitPhotosLocalFirst(String requestId,ArrayList<Uri> uris,int maxCount,HashSet<String> existingNames){
        int ready=0,total=uris==null?0:uris.size(),duplicates=0,overLimit=0,failed=0;
        final HashSet<String> knownNames=existingNames==null?new HashSet<String>():new HashSet<>(existingNames);
        notifyVisitPhotoProgress(requestId,0,total);
        if(uris!=null){for(Uri uri:uris){
            try{
                // Query only DISPLAY_NAME first; skip duplicate files before bitmap decoding.
                String sourceName=getSelectedVisitPhotoFilename(uri);
                String key=normalizedVisitPhotoFilename(sourceName);
                if(!key.isEmpty()&&knownNames.contains(key)){duplicates++;continue;}
                // Gallery may allow more than the remaining quota. Register valid photos
                // in selection order, without decoding any photos past the quota.
                if(ready>=maxCount){overLimit++;continue;}
                PreparedVisitPhoto prepared=prepareVisitPhotoFast(uri);if(prepared==null||prepared.full==null||prepared.full.length==0){failed++;continue;}
                String localId="local_"+UUID.randomUUID().toString().replace("-","");
                writePendingVisitPhoto(localId,prepared.full,prepared.thumb);
                JSONObject item=new JSONObject();item.put("photoId",localId);item.put("fileId",localId);item.put("driveFileId","");
                item.put("name",sourceName.isEmpty()?"방문사진.jpg":sourceName);
                item.put("originalFileName",sourceName);item.put("mimeType","image/jpeg");
                item.put("savedAt",String.valueOf(System.currentTimeMillis()));item.put("syncStatus","pending");
                if(!key.isEmpty())knownNames.add(key);
                ready++;notifyVisitPhotoLocalReady(requestId,item.toString(),ready,total);
            }catch(Exception ignored){failed++;}
        }}
        StringBuilder message=new StringBuilder();
        if(ready>0)message.append("사진 ").append(ready).append("장을 휴대폰에 등록했습니다.");
        if(duplicates>0){if(message.length()>0)message.append(" ");message.append("중복 파일명 ").append(duplicates).append("장은 제외했습니다.");}
        if(overLimit>0){if(message.length()>0)message.append(" ");message.append("등록 가능 장수 초과 ").append(overLimit).append("장은 제외했습니다.");}
        if(failed>0){if(message.length()>0)message.append(" ");message.append("처리하지 못한 사진 ").append(failed).append("장은 목록을 확인해 주세요.");}
        notifyVisitPhotoLocalComplete(requestId,ready>0||duplicates>0||overLimit>0,ready,total,message.toString());
    }

    /* V307: 한 번 디코딩한 Bitmap에서 본문용 JPEG와 썸네일을 함께 만들어
       선택 직후 화면에 나타나는 시간을 줄이고 Drive 업로드 크기도 낮춘다. */
    private PreparedVisitPhoto prepareVisitPhotoFast(Uri uri) throws Exception {
        BitmapFactory.Options bounds=new BitmapFactory.Options();bounds.inJustDecodeBounds=true;
        InputStream in=getContentResolver().openInputStream(uri);BitmapFactory.decodeStream(in,null,bounds);if(in!=null)in.close();
        int sample=1,max=Math.max(bounds.outWidth,bounds.outHeight);while(max/sample>1900)sample*=2;
        BitmapFactory.Options opts=new BitmapFactory.Options();opts.inSampleSize=Math.max(1,sample);opts.inPreferredConfig=Bitmap.Config.ARGB_8888;
        in=getContentResolver().openInputStream(uri);Bitmap bitmap=BitmapFactory.decodeStream(in,null,opts);if(in!=null)in.close();if(bitmap==null)return null;
        int orientation=ExifInterface.ORIENTATION_NORMAL;
        try{InputStream exifIn=getContentResolver().openInputStream(uri);ExifInterface exif=new ExifInterface(exifIn);orientation=exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);if(exifIn!=null)exifIn.close();}catch(Exception ignored){}
        Matrix matrix=new Matrix();if(orientation==ExifInterface.ORIENTATION_ROTATE_90)matrix.postRotate(90);else if(orientation==ExifInterface.ORIENTATION_ROTATE_180)matrix.postRotate(180);else if(orientation==ExifInterface.ORIENTATION_ROTATE_270)matrix.postRotate(270);
        if(!matrix.isIdentity()){Bitmap rotated=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);if(rotated!=bitmap)bitmap.recycle();bitmap=rotated;}
        int w=bitmap.getWidth(),h=bitmap.getHeight(),longSide=Math.max(w,h);if(longSide>1600){float scale=1600f/(float)longSide;Bitmap scaled=Bitmap.createScaledBitmap(bitmap,Math.max(1,Math.round(w*scale)),Math.max(1,Math.round(h*scale)),true);if(scaled!=bitmap)bitmap.recycle();bitmap=scaled;}
        Bitmap thumbBitmap=bitmap;int tw=bitmap.getWidth(),th=bitmap.getHeight(),tlong=Math.max(tw,th);if(tlong>360){float ts=360f/(float)tlong;thumbBitmap=Bitmap.createScaledBitmap(bitmap,Math.max(1,Math.round(tw*ts)),Math.max(1,Math.round(th*ts)),true);}
        ByteArrayOutputStream thumbOut=new ByteArrayOutputStream();thumbBitmap.compress(Bitmap.CompressFormat.JPEG,72,thumbOut);if(thumbBitmap!=bitmap)thumbBitmap.recycle();
        ByteArrayOutputStream fullOut=new ByteArrayOutputStream();bitmap.compress(Bitmap.CompressFormat.JPEG,80,fullOut);bitmap.recycle();
        return new PreparedVisitPhoto(fullOut.toByteArray(),thumbOut.toByteArray());
    }

    private void uploadVisitPhotosToDrive(String token,String payloadText) throws Exception {
        // V305부터 사진은 Local-first로 등록한 뒤 백그라운드 동기화합니다.
        syncPendingVisitPhotosToDrive(token);
    }

    private String createVisitPhotoFile(String token,String name,byte[] bytes) throws Exception {
        String boundary="cgm"+UUID.randomUUID().toString().replace("-","");
        String metadata="{\"name\":"+JSONObject.quote(name)+",\"parents\":[\"appDataFolder\"],\"mimeType\":\"image/jpeg\"}";
        ByteArrayOutputStream body=new ByteArrayOutputStream();
        body.write(("--"+boundary+"\r\nContent-Type: application/json; charset=UTF-8\r\n\r\n"+metadata+"\r\n--"+boundary+"\r\nContent-Type: image/jpeg\r\n\r\n").getBytes(StandardCharsets.UTF_8));body.write(bytes);body.write(("\r\n--"+boundary+"--\r\n").getBytes(StandardCharsets.UTF_8));
        String response=requestDrive(token,"POST","https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart&fields=id,name,mimeType",body.toByteArray(),"multipart/related; boundary="+boundary);
        return new JSONObject(response).optString("id",null);
    }

    // v322: Explicit developer-only manual test. No WorkManager job, no local photo deletion.
    private final java.util.concurrent.atomic.AtomicBoolean devPhotoBackupTestRunning = new java.util.concurrent.atomic.AtomicBoolean(false);
    private void notifyDevPhotoBackupTest(final String state, final String message) {
        runOnUiThread(new Runnable(){@Override public void run(){
            if(webView == null)return;
            String script="(function(){if(typeof window.oaiDevPhotoBackupResult==='function')window.oaiDevPhotoBackupResult("
                +JSONObject.quote(state)+","+JSONObject.quote(message)+");})()";
            webView.evaluateJavascript(script,null);
        }});
    }
    private String findExistingDevPhotoFile(String token,String name) throws Exception {
        String q=java.net.URLEncoder.encode("name = '"+name+"' and 'appDataFolder' in parents and trashed = false","UTF-8");
        String raw=requestDrive(token,"GET","https://www.googleapis.com/drive/v3/files?spaces=appDataFolder&fields=files(id,name)&pageSize=10&q="+q,null,null);
        JSONArray files=new JSONObject(raw).optJSONArray("files");
        return files!=null&&files.length()>0?files.optJSONObject(0).optString("id",""):"";
    }
    // Developer-only batch index: list appDataFolder photos once, with pagination.
    // Never delete, rename, or overwrite an existing Drive file.
    private java.util.Map<String,java.util.ArrayList<String>> listDevPhotoBackupIndex(String token) throws Exception {
        java.util.Map<String,java.util.ArrayList<String>> index=new java.util.HashMap<>();
        String pageToken="";
        do {
            String url="https://www.googleapis.com/drive/v3/files?spaces=appDataFolder"
                +"&pageSize=1000&fields=nextPageToken,files(id,name)"
                +"&q="+java.net.URLEncoder.encode("name contains 'cgm-visit-' and 'appDataFolder' in parents and trashed = false","UTF-8");
            if(pageToken.length()>0)url+="&pageToken="+java.net.URLEncoder.encode(pageToken,"UTF-8");
            JSONObject result=new JSONObject(requestDrive(token,"GET",url,null,null));
            JSONArray files=result.optJSONArray("files");
            if(files!=null)for(int i=0;i<files.length();i++){
                JSONObject file=files.optJSONObject(i);if(file==null)continue;
                String name=file.optString("name","");String id=file.optString("id","");
                if(!name.startsWith("cgm-visit-")||!name.endsWith(".jpg")||id.length()==0)continue;
                java.util.ArrayList<String> matches=index.get(name);
                if(matches==null){matches=new java.util.ArrayList<>();index.put(name,matches);}
                matches.add(id);
            }
            pageToken=result.optString("nextPageToken","");
        }while(pageToken.length()>0);
        return index;
    }
    private boolean drivePhotoMatchesLocal(String token,String remoteId,byte[] local) throws Exception {
        byte[] remote=requestDriveBytes(token,"GET","https://www.googleapis.com/drive/v3/files/"
            +Uri.encode(remoteId)+"?alt=media");
        if(remote==null||remote.length!=local.length)return false;
        java.security.MessageDigest digest=java.security.MessageDigest.getInstance("SHA-256");
        byte[] a=digest.digest(local);digest.reset();
        return java.util.Arrays.equals(a,digest.digest(remote));
    }
    // Diagnostic-only: group errors without exposing tokens or personal photo identifiers.
    private String devPhotoFailureKind(Exception e) {
        String message=String.valueOf(e==null?"":e.getMessage());
        java.util.regex.Matcher m=java.util.regex.Pattern.compile("Drive\\s+(\\d{3})").matcher(message);
        if(m.find())return "HTTP "+m.group(1);
        if(e instanceof java.net.SocketTimeoutException)return "시간 초과";
        if(e instanceof java.net.UnknownHostException || e instanceof java.net.ConnectException)return "네트워크";
        if(e instanceof java.io.IOException)return "통신/파일 오류";
        if(message.contains("Existing Drive filename has nonmatching bytes"))return "기존 파일 이름 충돌/내용 불일치";
        if(message.contains("Missing Drive file ID"))return "Drive 파일 ID 없음";
        return "기타 오류";
    }
    private void tallyDevPhotoFailure(java.util.Map<String,Integer> errors,Exception e){
        String kind=devPhotoFailureKind(e);
        Integer count=errors.get(kind);errors.put(kind,count==null?1:count+1);
    }
    private String devPhotoFailureSummary(java.util.Map<String,Integer> errors){
        if(errors.isEmpty())return "없음";
        StringBuilder b=new StringBuilder();
        for(java.util.Map.Entry<String,Integer> item:errors.entrySet()){
            if(b.length()>0)b.append(", ");b.append(item.getKey()).append(" ").append(item.getValue()).append("건");
        }
        return b.toString();
    }
    private void performDevPhotoBackupTest(String token){
        if(!devPhotoBackupTestRunning.compareAndSet(false,true)){
            notifyDevPhotoBackupTest("error","이미 사진 백업 시험이 진행 중입니다.");return;
        }
        int uploaded=0,existing=0,failures=0,checked=0;
        java.util.Map<String,Integer> errors=new java.util.LinkedHashMap<>();
        long started=android.os.SystemClock.elapsedRealtime();
        try{
            ArrayList<String> ids=pendingVisitPhotoIds();
            java.util.Collections.sort(ids);
            // One paginated Drive listing instead of one search request per local photo.
            java.util.Map<String,java.util.ArrayList<String>> driveIndex=listDevPhotoBackupIndex(token);
            for(String photoId:ids){
                if(isLocalPhotoDeleted(photoId)||visitPhotoRemoteId(photoId).length()>0)continue;
                byte[] bytes=readPendingVisitPhoto(photoId,false);
                if(bytes==null||bytes.length==0)continue;
                checked++;
                if(checked==1 || checked%10==0)notifyDevPhotoBackupTest("working","사진 백업 진행 중: "+checked+"장 처리 시작, 신규 "+uploaded+"장, 기존 "+existing+"장, 실패 "+failures+"장.");
                try{
                    // Idempotence after interrupted uploads: check the cloud name BEFORE sending.
                    String name="cgm-visit-"+photoId.replace("local_","")+".jpg";
                    String remoteId="";
                    java.util.ArrayList<String> candidates=driveIndex.get(name);
                    // A matching filename is insufficient: verify content before reuse.
                    if(candidates!=null&&!candidates.isEmpty()){
                        for(String candidate:candidates){
                            if(drivePhotoMatchesLocal(token,candidate,bytes)){remoteId=candidate;break;}
                        }
                        if(remoteId.length()==0)throw new java.io.IOException("Existing Drive filename has nonmatching bytes");
                    }
                    boolean alreadyOnDrive=remoteId.length()>0;
                    if(!alreadyOnDrive)remoteId=createVisitPhotoFile(token,name,bytes);
                    if(remoteId==null||remoteId.length()==0)throw new Exception("Missing Drive file ID");
                    if(isLocalPhotoDeleted(photoId)){
                        // The user deleted the picture during upload. Never record it as active.
                        /* Preserve the uploaded Drive backup; do not queue deletion. */failures++;continue;
                    }
                    setVisitPhotoRemoteId(photoId,remoteId);
                    notifyVisitPhotoSyncResult(photoId,true,remoteId,"image/jpeg","","");
                    if(alreadyOnDrive)existing++;
                    else {
                        uploaded++;
                        java.util.ArrayList<String> newIds=new java.util.ArrayList<>();newIds.add(remoteId);
                        driveIndex.put(name,newIds);
                    }
                }catch(Exception e){
                    failures++;tallyDevPhotoFailure(errors,e);
                    if(String.valueOf(e.getMessage()).contains("Drive 401"))break;
                }
            }
            String message="시험 완료: 신규 "+uploaded+"장, 기존 백업 발견 "+existing+"장, 실패 "+failures+"장. 소요 "+(android.os.SystemClock.elapsedRealtime()-started)+"ms. 실패 원인: "+devPhotoFailureSummary(errors)+". 휴대폰 사진은 삭제하지 않았습니다.";
            if(checked==0)message="백업할 새 사진이 없습니다. 이미 백업된 사진과 삭제된 사진은 제외했습니다.";
            notifyDevPhotoBackupTest(failures==0?"done":"partial",message);
        }catch(Exception e){notifyDevPhotoBackupTest("error","시험 중 오류가 발생했습니다. 로컬 사진은 유지됩니다.");}
        finally{devPhotoBackupTestRunning.set(false);}
    }

    // v323: Read-only verification of at most five previously backed-up local photos.
    // Never modify the local files, Drive files, metadata mapping, or visit records.
    private final java.util.concurrent.atomic.AtomicBoolean devPhotoVerifyRunning = new java.util.concurrent.atomic.AtomicBoolean(false);
    private volatile long devPhotoVerifyRequestedAt=0L;
    private void notifyDevPhotoVerify(final String state,final String message){
        runOnUiThread(new Runnable(){@Override public void run(){
            if(webView==null)return;
            webView.evaluateJavascript("(function(){if(window.oaiDevPhotoVerifyResult)window.oaiDevPhotoVerifyResult("
                +JSONObject.quote(state)+","+JSONObject.quote(message)+");})()",null);
        }});
    }
    // v326: Read-only full-batch recovery rehearsal. Stage downloaded bytes in cache, then
    // compare to the untouched local photo. Never import into visit records.
    private void performDevPhotoBackupVerify(String token){
        if(!devPhotoVerifyRunning.compareAndSet(false,true)){
            notifyDevPhotoVerify("error","이미 확인 시험이 진행 중입니다.");return;
        }
        int checked=0,matched=0,mismatched=0,missing=0;
        java.util.Map<String,Integer> errors=new java.util.LinkedHashMap<>();
        long queuedAt=devPhotoVerifyRequestedAt;
        long queueMs=queuedAt>0?Math.max(0L,android.os.SystemClock.elapsedRealtime()-queuedAt):0L;
        long downloadMs=0L,verifyMs=0L,totalBytes=0L;
        long started=android.os.SystemClock.elapsedRealtime();
        File stagingDir=new File(getCacheDir(),"photo_restore_verify_v325");
        try{
            if(!stagingDir.exists()&&!stagingDir.mkdirs())throw new java.io.IOException("Cannot create staging directory");
            ArrayList<String> ids=pendingVisitPhotoIds();
            java.util.Collections.sort(ids);
            for(String photoId:ids){
                // v326: Developer-only manual batch verification processes all eligible photos.
                if(isLocalPhotoDeleted(photoId))continue;
                String remoteId=visitPhotoRemoteId(photoId);
                if(remoteId.length()==0)continue;
                byte[] local=readPendingVisitPhoto(photoId,false);
                if(local==null||local.length==0)continue;
                checked++;
                if(checked==1 || checked%5==0)notifyDevPhotoVerify("working","전체 백업 사진 확인 중: "+checked+"장 검사 시작, 일치 "+matched+"장, 불일치 "+mismatched+"장, 실패 "+missing+"장.");
                File staged=null;
                boolean downloadMeasured=false;
                long t0=android.os.SystemClock.elapsedRealtime();
                try{
                    byte[] remote=requestDriveBytes(token,"GET","https://www.googleapis.com/drive/v3/files/"
                        +Uri.encode(remoteId)+"?alt=media");
                    downloadMs+=android.os.SystemClock.elapsedRealtime()-t0;downloadMeasured=true;
                    if(remote==null||remote.length==0){missing++;continue;}
                    // Only transient cache, not the user's local picture or Drive file.
                    staged=File.createTempFile("verify_", ".jpg",stagingDir);
                    try(FileOutputStream out=new FileOutputStream(staged)){out.write(remote);out.getFD().sync();}
                    remote=null;
                    long t1=android.os.SystemClock.elapsedRealtime();
                    BitmapFactory.Options bounds=new BitmapFactory.Options();bounds.inJustDecodeBounds=true;
                    BitmapFactory.decodeFile(staged.getAbsolutePath(),bounds);
                    if(bounds.outWidth<=0||bounds.outHeight<=0){mismatched++;continue;}
                    java.security.MessageDigest digest=java.security.MessageDigest.getInstance("SHA-256");
                    byte[] localHash=digest.digest(local);
                    digest.reset();
                    long fileBytes=0;
                    try(InputStream in=new java.io.FileInputStream(staged)){
                        byte[] buffer=new byte[32768];int n;
                        while((n=in.read(buffer))!=-1){if(n>0){digest.update(buffer,0,n);fileBytes+=n;}}
                    }
                    totalBytes+=fileBytes;
                    if(local.length==fileBytes&&java.util.Arrays.equals(localHash,digest.digest()))matched++;
                    else mismatched++;
                    verifyMs+=android.os.SystemClock.elapsedRealtime()-t1;
                }catch(Exception ex){
                    missing++;if(!downloadMeasured)downloadMs+=Math.max(0L,android.os.SystemClock.elapsedRealtime()-t0);
                    tallyDevPhotoFailure(errors,ex);
                    if("HTTP 401".equals(devPhotoFailureKind(ex))){cachedDriveAccessToken="";cachedDriveAccessTokenAt=0L;}
                }
                finally{if(staged!=null&&staged.exists())staged.delete();}
            }
            String message=checked==0
                ?"검증 가능한 사진이 없습니다. 기존 사진은 그대로입니다."
                :"임시 다운로드 검증: "+checked+"장 중 일치 "+matched+"장, 불일치 "+mismatched
                  +"장, 실패 "+missing+"장. 받은 용량 "+(totalBytes/1024)+"KB, 다운로드 "
                  +downloadMs+"ms, 파일 확인 "+verifyMs+"ms, 대기/인증 "+queueMs+"ms, 전체 소요 "+(android.os.SystemClock.elapsedRealtime()-started+queueMs)+"ms. 실패 원인: "+devPhotoFailureSummary(errors)+". 기존 사진과 기록은 변경하지 않았습니다.";
            notifyDevPhotoVerify(checked>0&&mismatched==0&&missing==0?"done":"partial",message);
        }catch(Exception ex){notifyDevPhotoVerify("error","임시 다운로드 검증 중 오류가 발생했습니다. 기존 사진은 유지됩니다.");}
        finally{
            // Remove only files owned by this temporary verification directory.
            File[] files=stagingDir.listFiles();
            if(files!=null)for(File f:files)if(f.getName().startsWith("verify_")&&f.getName().endsWith(".jpg"))f.delete();
            devPhotoVerifyRunning.set(false);
        }
    }

    // Developer mode only: audit the five most recently saved local photos.
    // Read-only by construction: local files and existing SQLite via OPEN_READONLY,
    // Drive metadata via GET. Never repair links, enqueue or upload from this method.
    private final java.util.concurrent.atomic.AtomicBoolean devPhotoUploadAuditRunning =
        new java.util.concurrent.atomic.AtomicBoolean(false);
    private void notifyDevPhotoUploadAudit(final String state, final String message) {
        runOnUiThread(new Runnable(){@Override public void run(){
            if (webView != null) webView.evaluateJavascript(
                "(function(){if(window.oaiDevPhotoUploadAuditResult)window.oaiDevPhotoUploadAuditResult("
                    + JSONObject.quote(state) + "," + JSONObject.quote(message) + ");})()", null);
        }});
    }
    private String devPhotoLocalMd5(File file) throws Exception {
        java.security.MessageDigest md5=java.security.MessageDigest.getInstance("MD5");
        try(InputStream input=new java.io.FileInputStream(file)) {
            byte[] buffer=new byte[8192];int n;
            while((n=input.read(buffer))!=-1)if(n>0)md5.update(buffer,0,n);
        }
        char[] hex="0123456789abcdef".toCharArray();
        StringBuilder value=new StringBuilder(32);
        for(byte b:md5.digest()){value.append(hex[(b>>>4)&15]).append(hex[b&15]);}
        return value.toString();
    }
    // Developer-only cloud inventory. Counts actual Drive photo objects independently of
    // the journal's active photo references. Paginate with constant-sized responses;
    // never download content, change mappings, or delete a Drive object.
    private int countDevDrivePhotoFiles(String token) throws Exception {
        int total=0;
        String pageToken="";
        java.util.HashSet<String> seenTokens=new java.util.HashSet<>();
        do {
            String url="https://www.googleapis.com/drive/v3/files?spaces=appDataFolder"
                +"&pageSize=1000&fields=nextPageToken,files(name)"
                +"&q="+java.net.URLEncoder.encode(
                    "name contains 'cgm-visit-' and 'appDataFolder' in parents and trashed = false", "UTF-8");
            if(!pageToken.isEmpty())url+="&pageToken="+java.net.URLEncoder.encode(pageToken,"UTF-8");
            JSONObject response=new JSONObject(requestDrive(token,"GET",url,null,null));
            JSONArray entries=response.optJSONArray("files");
            if(entries==null)throw new java.io.IOException("Missing Drive photo inventory page");
            for(int i=0;i<entries.length();i++){
                JSONObject entry=entries.optJSONObject(i);
                if(entry==null)continue;
                String name=entry.optString("name","");
                if(name.startsWith("cgm-visit-") && name.endsWith(".jpg"))total++;
            }
            pageToken=response.optString("nextPageToken","");
            if(!pageToken.isEmpty()&&!seenTokens.add(pageToken))
                throw new java.io.IOException("Repeated Drive photo inventory page");
        }while(!pageToken.isEmpty());
        return total;
    }
    private void performDevPhotoUploadAudit(String token) {
        if(!devPhotoUploadAuditRunning.compareAndSet(false,true)){
            notifyDevPhotoUploadAudit("error","진단이 이미 진행 중입니다.");return;
        }
        android.database.sqlite.SQLiteDatabase db=null;
        try {
            // Scan filenames once without loading any photo into memory.
            File folder=new File(getFilesDir(),"visit_photos_pending");
            File[] files=folder.listFiles();
            java.util.ArrayList<File> latest=new java.util.ArrayList<>(5);
            if(files!=null)for(File file:files){
                String name=file.getName();
                if(!file.isFile()||!name.startsWith("f_local_")||!name.endsWith(".jpg"))continue;
                int at=0;
                while(at<latest.size() && (latest.get(at).lastModified()>file.lastModified()
                    || (latest.get(at).lastModified()==file.lastModified()
                        && latest.get(at).getName().compareTo(name)<=0)))at++;
                if(at<5){latest.add(at,file);if(latest.size()>5)latest.remove(5);}
            }
            // Drive inventory is independent of whether the user currently has any
            // local photos. A removed journal reference never implies cloud deletion.
            int localFullCount=0;
            if(files!=null)for(File file:files){
                String name=file.getName();
                if(file.isFile()&&name.startsWith("f_local_")&&name.endsWith(".jpg"))localFullCount++;
            }
            int restoredFullCount=0;
            File restoredFolder=new File(getFilesDir(),"visit_photos_restored");
            File[] restoredFiles=restoredFolder.listFiles();
            if(restoredFiles!=null)for(File file:restoredFiles){
                String name=file.getName();
                if(file.isFile()&&name.startsWith("f_")&&name.endsWith(".jpg"))restoredFullCount++;
            }
            String inventory;
            boolean inventoryComplete=false;
            try {
                int cloudFiles=countDevDrivePhotoFiles(token);
                inventory="Drive 방문사진 파일: "+cloudFiles+"개 (같은 이름의 중복 파일도 각각 계산)";
                inventoryComplete=true;
            }catch(Exception ex){
                inventory="Drive 전체 파일 수 조회 실패: "+devPhotoFailureKind(ex);
            }
            inventory+="\n휴대폰 영구 사진 파일: 신규 "+localFullCount+"개 / 복원 "+restoredFullCount
                +"개 (방문기록 표시 수와 별개)\n";
            if(latest.isEmpty()){
                notifyDevPhotoUploadAudit(inventoryComplete?"done":"partial",inventory
                    +"현재 신규 사진 파일이 없어 개별 업로드 상태 검사는 생략했습니다.\n사진과 기록은 변경하지 않았습니다.");
                return;
            }
            File database=getDatabasePath("visit_photo_transfer_index_v1.db");
            if(database.isFile())db=android.database.sqlite.SQLiteDatabase.openDatabase(
                database.getAbsolutePath(),null,android.database.sqlite.SQLiteDatabase.OPEN_READONLY);
            JSONObject mappings=visitPhotoRemoteMap(); // One read, without changing preferences.
            Set<String> deleted=deletedLocalPhotoIds();
            StringBuilder report=new StringBuilder(inventory)
                .append("최근 저장 사진 ").append(latest.size())
                .append("장 · 읽기 전용 상태 진단\n")
                .append("자동 업로드 설정: ").append(visitPhotoAutoEnabled?"켜짐":"꺼짐")
                .append(" / 네트워크 조건: ").append(visitPhotoNetworkAllowed()?"허용":"차단")
                .append(" / 인증: ").append(cachedDriveAccessToken!=null
                    && !cachedDriveAccessToken.isEmpty()
                    && System.currentTimeMillis()-cachedDriveAccessTokenAt<DRIVE_TOKEN_CACHE_MS
                    ?"유효":"필요")
                .append(" / 실행 정책: ").append(visitPhotoTransferPolicy.blocked(
                    VisitPhotoTransferPolicy.Job.NEW_UPLOAD).name()).append("\n");
            int cloudMatches=0,cloudUnlinked=0,cloudMissing=0,uncertain=0;
            for(int i=0;i<latest.size();i++){
                File file=latest.get(i);
                String photoId=file.getName().substring(2,file.getName().length()-4);
                String state="색인 없음",dbDriveId="",retry="",association="없음";
                if(db!=null){
                    try(android.database.Cursor cur=db.rawQuery(
                        "SELECT transfer_state,drive_file_id,retry_count,next_retry_at,place_code,visit_date FROM photo_transfer WHERE photo_id=?",
                        new String[]{photoId})){
                        if(cur.moveToFirst()){
                            state=cur.getString(0);dbDriveId=cur.getString(1);
                            long next=cur.getLong(3);
                            association=cur.getString(4)!=null&&!cur.getString(4).isEmpty()
                                && cur.getString(5)!=null
                                && cur.getString(5).matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")
                                ?"연결됨":"장소/방문일 미연결";
                            if(next>System.currentTimeMillis())retry=" (재시도 대기)";
                        }
                    }catch(Exception ex){state="색인 읽기 오류";uncertain++;}
                }
                String mappedId=mappings.optString(photoId,"");
                String linkedId=dbDriveId!=null&&!dbDriveId.isEmpty()?dbDriveId:mappedId;
                String remoteResult="확인 불가";
                if(deleted.contains(photoId)) {
                    remoteResult="로컬 삭제 표시 (Drive 조회 제외)";
                    uncertain++;
                } else {
                    try{
                        String name="cgm-visit-"+photoId.replace("local_", "")+".jpg";
                        String query="name = '"+name+"' and 'appDataFolder' in parents and trashed = false";
                        String url="https://www.googleapis.com/drive/v3/files?spaces=appDataFolder"
                            +"&pageSize=100&fields=nextPageToken,files(id,name,md5Checksum,size)&q="
                            +java.net.URLEncoder.encode(query,"UTF-8");
                        JSONObject result=new JSONObject(requestDrive(token,"GET",url,null,null));
                        JSONArray matches=result.optJSONArray("files");
                        boolean found=false,verified=false,linkedMatch=false;
                        String localMd5="";
                        if(matches!=null&&matches.length()>0){
                            found=true;
                            localMd5=devPhotoLocalMd5(file);
                            for(int j=0;j<matches.length();j++){
                                JSONObject remote=matches.optJSONObject(j);if(remote==null)continue;
                                if(file.length()==remote.optLong("size",-1L)
                                    && localMd5.equalsIgnoreCase(remote.optString("md5Checksum",""))){
                                    verified=true;
                                    if(remote.optString("id","").equals(linkedId))linkedMatch=true;
                                }
                            }
                        }
                        if(verified){
                            cloudMatches++;
                            if(linkedId.isEmpty()){cloudUnlinked++;remoteResult="Drive에 있음 · ID 연결 누락";}
                            else if(linkedMatch)remoteResult="Drive에 있음 · ID 연결 정상";
                            else{uncertain++;remoteResult="Drive에 있음 · 저장 ID 불일치";}
                        }else if(found){uncertain++;remoteResult="동일 이름 존재 · 내용 일치 안 함";}
                        else if(result.has("nextPageToken")&&!result.optString("nextPageToken","").isEmpty()){
                            uncertain++;remoteResult="Drive 결과 일부만 조회";
                        }else{cloudMissing++;remoteResult="Drive에서 발견되지 않음";}
                    }catch(Exception ex){
                        uncertain++;remoteResult="Drive 조회 오류: "+devPhotoFailureKind(ex);
                    }
                }
                String timestamp=new java.text.SimpleDateFormat("MM-dd HH:mm",java.util.Locale.KOREA)
                    .format(new java.util.Date(file.lastModified()));
                report.append(i+1).append(". ").append(timestamp)
                    .append(" · ").append(file.length()/1024L).append("KB")
                    .append("\n  SQLite: ").append(state==null?"없음":state).append(retry)
                    .append(" / 색인 Drive ID: ").append(dbDriveId==null||dbDriveId.isEmpty()?"없음":"있음")
                    .append(" / 저장 매핑 ID: ").append(mappedId.isEmpty()?"없음":"있음")
                    .append(" / 방문정보: ").append(association)
                    .append("\n  ").append(remoteResult).append("\n");
            }
            report.append("요약: Drive 내용 일치 ").append(cloudMatches)
                .append("장, 그중 ID 누락 ").append(cloudUnlinked)
                .append("장, Drive 미발견 ").append(cloudMissing)
                .append("장, 추가 확인 ").append(uncertain)
                .append("장.\n사진·기록·대기열·Drive 파일은 변경하지 않았습니다.");
            notifyDevPhotoUploadAudit(uncertain==0&&inventoryComplete?"done":"partial",report.toString());
        }catch(Exception ex){
            notifyDevPhotoUploadAudit("error","진단을 완료하지 못했습니다: "+devPhotoFailureKind(ex)
                +". 사진과 기록은 변경하지 않았습니다.");
        }finally{
            if(db!=null)db.close();
            devPhotoUploadAuditRunning.set(false);
        }
    }

    private File pendingVisitPhotoDir(){File dir=new File(getFilesDir(),"visit_photos_pending");if(!dir.exists())dir.mkdirs();return dir;}
    private File pendingVisitPhotoFile(String id,boolean thumb){String safe=(id==null?"":id).replaceAll("[^A-Za-z0-9_-]","");return new File(pendingVisitPhotoDir(),(thumb?"t_":"f_")+safe+".jpg");}
    // Write new photographs through private staging files. A power loss or full disk must
    // never expose a partially written JPEG as a complete, user-visible photo.
    // Do not replace pre-existing files: photo IDs are immutable.
    private void writePendingVisitPhoto(String id,byte[] full,byte[] thumb) throws Exception {
        if(id==null||!id.matches("local_[A-Za-z0-9_-]{8,200}")||full==null||full.length==0||thumb==null||thumb.length==0)
            throw new java.io.IOException("Invalid visit photo data");
        File photo=pendingVisitPhotoFile(id,false),thumbnail=pendingVisitPhotoFile(id,true);
        if(photo.exists()||thumbnail.exists())throw new java.io.IOException("Photo ID already exists");
        File stagedPhoto=null,stagedThumb=null;
        boolean photoPublished=false,thumbPublished=false;
        try{
            stagedPhoto=File.createTempFile("write_full_", ".tmp", pendingVisitPhotoDir());
            stagedThumb=File.createTempFile("write_thumb_", ".tmp", pendingVisitPhotoDir());
            writePhotoStage(stagedPhoto,full);
            writePhotoStage(stagedThumb,thumb);
            if(!stagedPhoto.renameTo(photo))throw new java.io.IOException("Cannot publish photo");
            photoPublished=true;
            if(!stagedThumb.renameTo(thumbnail))throw new java.io.IOException("Cannot publish thumbnail");
            thumbPublished=true;
        }catch(Exception e){
            // Only remove files created in this attempt; never touch existing photo IDs.
            if(thumbPublished)thumbnail.delete();
            if(photoPublished)photo.delete();
            throw e;
        }finally{
            if(stagedPhoto!=null)stagedPhoto.delete();
            if(stagedThumb!=null)stagedThumb.delete();
        }
        try(VisitPhotoTransferIndex index=new VisitPhotoTransferIndex(getApplicationContext())){
            index.queued(id);
        }catch(Exception ignored){/* Photo persistence must not depend on auxiliary indexing. */}
    }
    private static void writePhotoStage(File destination,byte[] bytes) throws java.io.IOException {
        try(FileOutputStream output=new FileOutputStream(destination)){
            output.write(bytes);
            output.getFD().sync();
        }
    }
    private byte[] readPendingVisitPhoto(String id,boolean thumb){try{File f=pendingVisitPhotoFile(id,thumb);if(!f.exists()||f.length()<=0)return null;InputStream in=new java.io.FileInputStream(f);ByteArrayOutputStream out=new ByteArrayOutputStream((int)Math.min(f.length(),3*1024*1024));byte[] b=new byte[8192];int n;while((n=in.read(b))>0)out.write(b,0,n);in.close();return out.toByteArray();}catch(Exception e){return null;}}
    private void deletePendingVisitPhoto(String id){try{pendingVisitPhotoFile(id,false).delete();pendingVisitPhotoFile(id,true).delete();}catch(Exception ignored){}}
    private ArrayList<String> pendingVisitPhotoIds(){ArrayList<String> ids=new ArrayList<>();try{File[] files=pendingVisitPhotoDir().listFiles();if(files==null)return ids;for(File f:files){String n=f.getName();if(n.startsWith("f_local_")&&n.endsWith(".jpg"))ids.add(n.substring(2,n.length()-4));}}catch(Exception ignored){}return ids;}
    private static final String VISIT_PHOTO_PREFS = "gildongmu_visit_photo_v1";
    private static final String PREF_VISIT_PHOTO_DELETE_QUEUE = "drive_delete_queue";
    private static final String PREF_VISIT_PHOTO_REMOTE_MAP = "remote_map_json";
    private static final String PREF_VISIT_PHOTO_DELETED_LOCAL_IDS = "deleted_local_ids";
    private static final String PREF_WORKER_DRIVE_TOKEN = "worker_drive_token";
    private static final String PREF_WORKER_DRIVE_TOKEN_AT = "worker_drive_token_at";
    private static final String VISIT_PHOTO_SYNC_WORK = "visit-photo-drive-sync";
    private SharedPreferences visitPhotoPrefs(){return getSharedPreferences(VISIT_PHOTO_PREFS,MODE_PRIVATE);}
    private synchronized JSONObject visitPhotoRemoteMap(){try{return new JSONObject(visitPhotoPrefs().getString(PREF_VISIT_PHOTO_REMOTE_MAP,"{}"));}catch(Exception e){return new JSONObject();}}
    private synchronized String visitPhotoRemoteId(String photoId){
        if(photoId==null||photoId.isEmpty())return "";
        // Indexed lookup avoids repeatedly parsing a potentially 20,000-photo JSON map.
        // The original preference data stays untouched and remains the fallback for
        // older installations or records not yet indexed.
        try(VisitPhotoTransferIndex index=new VisitPhotoTransferIndex(getApplicationContext())){
            String remote=index.uploadedDriveId(photoId);
            if(remote!=null&&!remote.isEmpty())return remote;
        }catch(Exception ignored){}
        try{return visitPhotoRemoteMap().optString(photoId,"");}catch(Exception e){return "";}
    }
    private synchronized void setVisitPhotoRemoteId(String photoId,String remoteId){try{JSONObject m=visitPhotoRemoteMap();if(remoteId==null||remoteId.length()==0)m.remove(photoId);else m.put(photoId,remoteId);visitPhotoPrefs().edit().putString(PREF_VISIT_PHOTO_REMOTE_MAP,m.toString()).apply();if(remoteId!=null&&!remoteId.isEmpty())try(VisitPhotoTransferIndex index=new VisitPhotoTransferIndex(getApplicationContext())){index.uploaded(photoId,remoteId);}catch(Exception ignored){};}catch(Exception ignored){}}
    private synchronized Set<String> deletedLocalPhotoIds(){try{return new HashSet<>(visitPhotoPrefs().getStringSet(PREF_VISIT_PHOTO_DELETED_LOCAL_IDS,Collections.<String>emptySet()));}catch(Exception e){return new HashSet<>();}}
    private synchronized boolean isLocalPhotoDeleted(String photoId){return photoId!=null&&deletedLocalPhotoIds().contains(photoId);}
    private synchronized void markLocalPhotoDeleted(String photoId){if(photoId==null||photoId.length()==0)return;Set<String> s=deletedLocalPhotoIds();s.add(photoId);visitPhotoPrefs().edit().putStringSet(PREF_VISIT_PHOTO_DELETED_LOCAL_IDS,new HashSet<>(s)).apply();try(VisitPhotoTransferIndex index=new VisitPhotoTransferIndex(getApplicationContext())){index.deleted(photoId);}catch(Exception ignored){}}
    private synchronized Set<String> pendingVisitPhotoDeleteIds(){try{return new HashSet<>(visitPhotoPrefs().getStringSet(PREF_VISIT_PHOTO_DELETE_QUEUE,Collections.<String>emptySet()));}catch(Exception e){return new HashSet<>();}}
    private synchronized void queueVisitPhotoDeleteId(String id){if(id==null||id.length()==0||id.startsWith("local_"))return;Set<String> s=pendingVisitPhotoDeleteIds();s.add(id);visitPhotoPrefs().edit().putStringSet(PREF_VISIT_PHOTO_DELETE_QUEUE,new HashSet<>(s)).apply();}
    private synchronized void removeVisitPhotoDeleteId(String id){if(id==null||id.length()==0)return;Set<String> s=pendingVisitPhotoDeleteIds();if(s.remove(id))visitPhotoPrefs().edit().putStringSet(PREF_VISIT_PHOTO_DELETE_QUEUE,new HashSet<>(s)).apply();}
    private void syncPendingVisitPhotoDeletes(String token){
        // v324: Backups are immutable. Preserve historical pending-delete metadata as-is.
        // Never issue a Drive DELETE request, including when automatic sync is enabled later.
        return;
    }


    private void scheduleVisitPhotoSyncWork(){
        // Compatibility entrypoint: never enqueue the nonfunctional legacy Worker.
        runAuthorizedVisitPhotoUploads();
    }

    private void notifyKnownVisitPhotoMappings(){
        // Send restored photo mappings in bounded batches rather than one UI-thread
        // runnable / evaluateJavascript call per photo on every page load.
        try {
            JSONObject map = visitPhotoRemoteMap();
            Set<String> deleted = deletedLocalPhotoIds();
            java.util.Iterator<String> it = map.keys();
            // This is a mapping notification, never a replacement registration date.
            final String savedAt = "";
            JSONArray batch = new JSONArray();
            while (it.hasNext()) {
                String photoId = it.next();
                String remote = map.optString(photoId, "");
                if (remote.length() == 0 || deleted.contains(photoId)) continue;
                JSONArray item = new JSONArray();
                item.put(photoId);
                item.put(remote);
                batch.put(item);
                if (batch.length() >= 40) {
                    dispatchVisitPhotoMappingBatch(batch.toString(), savedAt);
                    batch = new JSONArray();
                }
            }
            if (batch.length() > 0) dispatchVisitPhotoMappingBatch(batch.toString(), savedAt);
        } catch (Exception ignored) {}
    }

    private void dispatchVisitPhotoMappingBatch(final String batchJson, final String savedAt) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                if (webView == null) return;
                String script = "(function(){var f=window.oaiVisitPhotoSyncResult;if(typeof f!=='function')return;"
                        + "var a=" + batchJson + ";for(var i=0;i<a.length;i++){try{"
                        + "f(a[i][0],true,a[i][1],'image/jpeg'," + JSONObject.quote(savedAt) + ",'');"
                        + "}catch(e){}}})()";
                webView.evaluateJavascript(script, null);
            }
        });
    }

    private String visitPhotoAssetUrl(String id,boolean thumb){return "https://appassets.androidplatform.net/visit-photo/"+(thumb?"thumb/":"full/")+Uri.encode(id)+".jpg?v="+System.currentTimeMillis();}

    private WebResourceResponse interceptVisitPhotoAsset(Uri uri){
        try{
            if(uri==null||!"appassets.androidplatform.net".equalsIgnoreCase(uri.getHost()))return null;
            java.util.List<String> seg=uri.getPathSegments();if(seg.size()!=3||!"visit-photo".equals(seg.get(0)))return null;
            boolean thumb="thumb".equals(seg.get(1));String id=seg.get(2);if(id.endsWith(".jpg"))id=id.substring(0,id.length()-4);
            byte[] bytes=readPendingVisitPhoto(id,thumb);
            if((bytes==null||bytes.length==0)&&thumb){byte[] full=readPendingVisitPhoto(id,false);if(full!=null&&full.length>0)bytes=makeVisitPhotoThumbnail(full);}
            String cacheId=id;
            if(bytes==null||bytes.length==0){
                bytes=readVisitPhotoCache(cacheId,thumb);
                if(bytes==null||bytes.length==0){
                    String remote=visitPhotoRemoteId(id);if(remote!=null&&remote.length()>0){cacheId=remote;bytes=readVisitPhotoCache(cacheId,thumb);}
                }
            }
            if((bytes==null||bytes.length==0)&&thumb){byte[] full=readVisitPhotoCache(cacheId,false);if(full!=null&&full.length>0){bytes=makeVisitPhotoThumbnail(full);cacheVisitPhotoBytes(cacheId,bytes,true);}}
            if(bytes==null||bytes.length==0)return new WebResourceResponse("image/jpeg",null,404,"Not Found",java.util.Collections.<String,String>emptyMap(),new ByteArrayInputStream(new byte[0]));
            java.util.Map<String,String> headers=new java.util.HashMap<>();headers.put("Cache-Control","no-store");headers.put("Access-Control-Allow-Origin","*");
            return new WebResourceResponse("image/jpeg",null,200,"OK",headers,new ByteArrayInputStream(bytes));
        }catch(Exception e){return null;}
    }

    // One Activity-scoped retry appointment for all restore failures.
    // Keep the earliest deadline: a later failure must not postpone an earlier retry.
    private long visitPhotoNextRetryAtElapsed = -1L;
    private final Runnable visitPhotoDeferredRetry = new Runnable(){@Override public void run(){
        synchronized(MainActivity.this){
            visitPhotoNextRetryAtElapsed=-1L;
        }
        if(isFinishing() || isDestroyed() || isVisitPhotoCloudDisabled())return;
        // Preserve the normal network, GPS, photo-priority and authorization gates.
        resumeAuthorizedVisitPhotoTransfers();
    }};
    private void retryPendingVisitPhotoSyncIfAuthorized(long delayMs){
        if(isFinishing() || isDestroyed() || isVisitPhotoCloudDisabled())return;
        long now=android.os.SystemClock.elapsedRealtime();
        long delay=Math.max(0L,delayMs);
        long target=now+(delay>Long.MAX_VALUE-now?Long.MAX_VALUE-now:delay);
        synchronized(this){
            if(visitPhotoNextRetryAtElapsed>=0L && visitPhotoNextRetryAtElapsed<=target)return;
            visitPhotoRetryHandler.removeCallbacks(visitPhotoDeferredRetry);
            visitPhotoNextRetryAtElapsed=target;
            visitPhotoRetryHandler.postDelayed(visitPhotoDeferredRetry,delay);
        }
    }
    // Foreground transfer: one photo at a time, never delete or overwrite Drive objects.
    // One photo per transfer, never overwrite/delete an existing Drive object.
    // Checking the remote filename AND bytes prevents duplicates after a lost response.
    private void syncPendingVisitPhotosToDrive(String token){
        if(isVisitPhotoCloudDisabled() || !visitPhotoNetworkAllowed())return;
        if(token==null||token.length()==0||visitPhotoSyncRunning)return;
        // Never begin a transfer while GPS, maps, or photo processing has priority.
        if(visitPhotoTransferPolicy.blocked(VisitPhotoTransferPolicy.Job.NEW_UPLOAD)
                != VisitPhotoTransferPolicy.Block.NONE)return;
        visitPhotoSyncRunning=true;
        try{
            try(VisitPhotoTransferIndex index=new VisitPhotoTransferIndex(getApplicationContext())){
                int processed=0;
                while(processed<1 && !Thread.currentThread().isInterrupted()){
                    // Check at every file boundary. Never interrupt a half-written Drive upload.
                    if(!visitPhotoNetworkAllowed() || visitPhotoTransferPolicy.blocked(VisitPhotoTransferPolicy.Job.NEW_UPLOAD)
                            != VisitPhotoTransferPolicy.Block.NONE)break;
                    String photoId=index.nextUploadCandidate(System.currentTimeMillis());
                    if(photoId==null)break;
                    // Count every examined candidate, including unavailable/deleted ones.
                    // Otherwise an unexpected SQLite update failure could spin forever.
                    processed++;
                    // Only committed journal entries are eligible for cloud transfer.
                    if(isLocalPhotoDeleted(photoId)){index.deleted(photoId);continue;}
                    File localFile=pendingVisitPhotoFile(photoId,false);
                    if(!localFile.isFile()||localFile.length()>12L*1024*1024){
                        index.failedUpload(photoId,"Local photo is missing or oversized",System.currentTimeMillis()+3600000L);
                        continue;
                    }
                    byte[] full=readPendingVisitPhoto(photoId,false);
                    if(full==null||full.length==0){
                        index.failedUpload(photoId,"Local photo unavailable",System.currentTimeMillis()+3600000L);
                        continue;
                    }
                    String existing=visitPhotoRemoteId(photoId);
                    if(existing.length()>0){
                        index.uploaded(photoId,existing);
                        // SQLite and the web journal must agree even after a lost callback.
                        notifyVisitPhotoSyncResult(photoId,true,existing,"image/jpeg","","");
                        continue;
                    }
                    try{
                        java.util.Map<String,java.util.ArrayList<String>> driveIndex=getSessionDrivePhotoIndex(token);
                        String name="cgm-visit-"+photoId.replace("local_","")+".jpg";
                        String remoteId="";
                        java.util.ArrayList<String> candidates=driveIndex.get(name);
                        if(candidates!=null&&!candidates.isEmpty()){
                            for(String candidate:candidates){
                                if(drivePhotoMatchesLocal(token,candidate,full)){remoteId=candidate;break;}
                            }
                            if(remoteId.length()==0)throw new java.io.IOException("Existing Drive filename has nonmatching bytes");
                        }else{
                            remoteId=createVisitPhotoFile(token,name,full);
                        }
                        if(remoteId==null||remoteId.length()==0)throw new java.io.IOException("Missing Drive file ID");
                        if(isLocalPhotoDeleted(photoId)){index.deleted(photoId);continue;}
                        // Persist the mapping only after the server confirmed the upload.
                        index.uploaded(photoId,remoteId);
                        java.util.ArrayList<String> remoteMatches=new java.util.ArrayList<>();remoteMatches.add(remoteId);
                        driveIndex.put(name,remoteMatches);
                        // Preserve original photo registration time in the web journal.
                        notifyVisitPhotoSyncResult(photoId,true,remoteId,"image/jpeg","","");
                    }catch(Exception e){
                        index.failedUpload(photoId,devPhotoFailureKind(e),System.currentTimeMillis()+60000L);
                        sessionDrivePhotoIndex=null; // Lost replies require a fresh Drive listing.
                        String kind=devPhotoFailureKind(e);
                        if("네트워크".equals(kind)||"시간 초과".equals(kind)||kind.startsWith("HTTP 5")||"HTTP 429".equals(kind)){
                            visitPhotoTransferErrorUntil=System.currentTimeMillis()+60000L;
                            break;
                        }
                        if(String.valueOf(e.getMessage()).contains("Drive 401")){
                            cachedDriveAccessToken="";cachedDriveAccessTokenAt=0L;break;
                        }
                    }
                }
            }
        }catch(Exception ignored){
            // An unavailable Drive listing must never trigger blind uploads or a tight loop.
            sessionDrivePhotoIndex=null;
            visitPhotoTransferErrorUntil=System.currentTimeMillis()+60000L;
        }finally{visitPhotoSyncRunning=false;}
    }

    private void loadVisitPhotoFromDrive(String token,String payloadText) throws Exception {
        JSONObject payload=new JSONObject(payloadText);String req=payload.optString("requestId",""),id=payload.optString("fileId","");boolean thumb=payload.optBoolean("thumb",false);
        if(id.length()==0){notifyVisitPhotoLoaded(req,false,"","","사진 정보를 찾을 수 없습니다.");return;}
        byte[] cached=readVisitPhotoCache(id,thumb);
        if(cached!=null&&cached.length>0 && visitPhotoStoreFile(id,thumb).isFile()
                && visitPhotoStoreFile(id,thumb).length()>0){
            notifyVisitPhotoLoaded(req,true,"image/jpeg",visitPhotoAssetUrl(id,thumb),"");return;
        }
        // Foreground thumbnail: resolve the small cloud object first, without fetching
        // the 1600px original. Older backups transparently use the legacy full path.
        String photoId=payload.optString("photoId","");
        if(thumb&&photoId.matches("[A-Za-z0-9_-]{8,200}")&&!isLocalPhotoDeleted(photoId)){
            byte[] localThumb=readVisitPhotoCache(photoId,true);
            if(localThumb!=null&&localThumb.length>0){
                notifyVisitPhotoLoaded(req,true,"image/jpeg",visitPhotoAssetUrl(photoId,true),"");return;
            }
            VisitPhotoTransferPolicy.Block admission=visitPhotoTransferPolicy.blocked(
                VisitPhotoTransferPolicy.Job.REQUESTED_PHOTO);
            if(visitPhotoNetworkAllowed() && (admission==VisitPhotoTransferPolicy.Block.NONE
                  || admission==VisitPhotoTransferPolicy.Block.HIGHER_PRIORITY)){
                String thumbId="";
                try{thumbId=findThumbnailCloudId(token,photoId);}catch(Exception ignored){
                    // A thumbnail index lookup is optional; the existing full-photo ID
                    // remains the authoritative fallback for older backups.
                    sessionDrivePhotoIndex=null;
                }
                if(thumbId.length()>0){
                    byte[] small=requestDriveBytes(token,"GET",
                        "https://www.googleapis.com/drive/v3/files/"+Uri.encode(thumbId)+"?alt=media");
                    if(small!=null&&small.length>=100&&small.length<=1024*1024){
                        BitmapFactory.Options bounds=new BitmapFactory.Options();bounds.inJustDecodeBounds=true;
                        BitmapFactory.decodeByteArray(small,0,small.length,bounds);
                        if(bounds.outWidth>0&&bounds.outHeight>0
                           &&Math.max(bounds.outWidth,bounds.outHeight)<=720){
                            cacheVisitPhotoBytes(photoId,small,true);
                            if(validDurableVisitPhoto(visitPhotoStoreFile(photoId,true))){
                                notifyVisitPhotoLoaded(req,true,"image/jpeg",visitPhotoAssetUrl(photoId,true),"");return;
                            }
                        }
                    }
                }
            }
        }
        byte[] full=readVisitPhotoCache(id,false);
        if(full==null||full.length==0){
            VisitPhotoTransferPolicy.Block admission=visitPhotoTransferPolicy.blocked(
                    VisitPhotoTransferPolicy.Job.REQUESTED_PHOTO);
            if(!visitPhotoNetworkAllowed() || (admission!=VisitPhotoTransferPolicy.Block.NONE
                    && admission!=VisitPhotoTransferPolicy.Block.HIGHER_PRIORITY))
                throw new java.io.IOException("Photo transfer paused for location or network");
            full=requestDriveBytes(token,"GET","https://www.googleapis.com/drive/v3/files/"+id+"?alt=media");
            if(full==null||full.length==0)throw new java.io.IOException("Empty Drive photo");
            cacheVisitPhotoBytes(id,full,false);
        }
        if(photoId.matches("[A-Za-z0-9_-]{8,200}")&&!photoId.equals(id)&&!isLocalPhotoDeleted(photoId)){
            cacheVisitPhotoBytes(photoId,full,false);
        }
        // A successful network response is not sufficient: the image must exist in the
        // durable store before telling the WebView it can be displayed.
        if(!visitPhotoStoreFile(id,false).isFile() || visitPhotoStoreFile(id,false).length()==0)
            throw new java.io.IOException("Restored photo was not durably stored");
        if(thumb){
            byte[] small=makeVisitPhotoThumbnail(full);
            if(small==null||small.length==0)throw new java.io.IOException("Thumbnail generation failed");
            cacheVisitPhotoBytes(id,small,true);
            if(photoId.matches("[A-Za-z0-9_-]{8,200}")&&!photoId.equals(id)&&!isLocalPhotoDeleted(photoId))
                cacheVisitPhotoBytes(photoId,small,true);
            if(!visitPhotoStoreFile(id,true).isFile() || visitPhotoStoreFile(id,true).length()==0)
                throw new java.io.IOException("Restored thumbnail was not durably stored");
        }
        trimVisitPhotoCache();
        notifyVisitPhotoLoaded(req,true,"image/jpeg",visitPhotoAssetUrl(id,thumb),"");
    }

    private void deleteVisitPhotoFromDrive(String token,String payloadText) throws Exception {
        // v324: Never remove cloud backups through an app-side delete request.
        JSONObject payload=new JSONObject(payloadText);
        String req=payload.optString("requestId", "");
        notifyVisitPhotoDeleteResult(req,false,"Google 백업 파일 삭제는 지원하지 않습니다.");
    }

    // v330: Successfully restored photos and thumbnails are durable files, not disposable cache.
    // Keep legacy cache readable for pre-v330 devices; never delete it during migration.
    private File visitPhotoCacheDir() { File dir=new File(getCacheDir(),"visit_photos");if(!dir.exists())dir.mkdirs();return dir; }
    private File visitPhotoStoreDir(){File dir=new File(getFilesDir(),"visit_photos_restored");if(!dir.exists())dir.mkdirs();return dir;}
    private String visitPhotoFileName(String id,boolean thumb){return (thumb?"t_":"f_")+id.replaceAll("[^A-Za-z0-9_-]","")+".jpg";}
    private File visitPhotoCacheFile(String id,boolean thumb){return new File(visitPhotoCacheDir(),visitPhotoFileName(id,thumb));}
    private File visitPhotoStoreFile(String id,boolean thumb){return new File(visitPhotoStoreDir(),visitPhotoFileName(id,thumb));}
    private synchronized void cacheVisitPhotoBytes(String id,byte[] bytes,boolean thumb){
        if(id==null||id.length()==0||bytes==null||bytes.length==0)return;
        File dst=visitPhotoStoreFile(id,thumb);
        // Never replace an existing restored photo, even when a later Drive response differs.
        if(dst.exists())return;
        File tmp=null;
        try{
            tmp=File.createTempFile(dst.getName()+"_", ".part", dst.getParentFile());
            try(FileOutputStream out=new FileOutputStream(tmp)){
                out.write(bytes);
                out.getFD().sync();
            }
            if(!dst.exists())tmp.renameTo(dst);
        }catch(Exception ignored){
            // The original file and any earlier successful restoration remain untouched.
        }finally{
            if(tmp!=null&&tmp.exists())tmp.delete();
        }
    }
    private byte[] readVisitPhotoFile(File f){
        if(!f.exists()||f.length()<=0)return null;
        try(InputStream in=new java.io.FileInputStream(f);ByteArrayOutputStream out=new ByteArrayOutputStream((int)Math.min(f.length(),2*1024*1024))){byte[] b=new byte[8192];int n;while((n=in.read(b))>0)out.write(b,0,n);return out.toByteArray();}
        catch(Exception e){return null;}
    }
    private byte[] readVisitPhotoCache(String id,boolean thumb){
        byte[] data=readVisitPhotoFile(visitPhotoStoreFile(id,thumb));if(data!=null)return data;
        File legacy=visitPhotoCacheFile(id,thumb);data=readVisitPhotoFile(legacy);
        if(data!=null)cacheVisitPhotoBytes(id,data,thumb); // copy-and-verify later, never delete legacy
        return data;
    }
    private void deleteVisitPhotoCache(String id){try{visitPhotoStoreFile(id,false).delete();visitPhotoStoreFile(id,true).delete();visitPhotoCacheFile(id,false).delete();visitPhotoCacheFile(id,true).delete();}catch(Exception ignored){}}
    private byte[] makeVisitPhotoThumbnail(byte[] full){if(full==null||full.length==0)return full;try{Bitmap b=BitmapFactory.decodeByteArray(full,0,full.length);if(b==null)return full;int w=b.getWidth(),h=b.getHeight(),longSide=Math.max(w,h);if(longSide>360){float sc=360f/(float)longSide;Bitmap small=Bitmap.createScaledBitmap(b,Math.max(1,Math.round(w*sc)),Math.max(1,Math.round(h*sc)),true);if(small!=b)b.recycle();b=small;}ByteArrayOutputStream out=new ByteArrayOutputStream();b.compress(Bitmap.CompressFormat.JPEG,72,out);b.recycle();return out.toByteArray();}catch(Exception e){return full;}}
    private void trimVisitPhotoCache(){try{File[] files=visitPhotoCacheDir().listFiles();if(files==null)return;long total=0;for(File f:files)total+=f.length();long max=100L*1024L*1024L;if(total<=max)return;java.util.Arrays.sort(files,new java.util.Comparator<File>(){public int compare(File a,File b){boolean af=a.getName().startsWith("f_"),bf=b.getName().startsWith("f_");if(af!=bf)return af?-1:1;return Long.compare(a.lastModified(),b.lastModified());}});for(File f:files){if(total<=70L*1024L*1024L)break;long len=f.length();if(f.delete())total-=len;}}catch(Exception ignored){}}

    private byte[] requestDriveBytes(String token,String method,String urlString) throws Exception {
        return requestDriveBytes(token,method,urlString,false);
    }
    // Only bulk restore has a safely restartable download. Requested-photo and developer
    // verification reads retain the existing behavior; uploads use a different path.
    private byte[] requestDriveBytes(String token,String method,String urlString,
                                    boolean interruptBulkRestore) throws Exception {
        // This helper only handles individual visit photos. Hard cap protects the heap
        // from an accidental/corrupt oversized Drive object before JPEG validation.
        final int maxBytes=12*1024*1024;
        // An open socket may deliver a trickle of bytes forever without ever
        // triggering the per-read timeout. Bound the total time occupied by a
        // bulk-restore worker as well; the durable retry journal preserves work.
        final long bulkDeadline=interruptBulkRestore
            ?android.os.SystemClock.elapsedRealtime()+120000L:Long.MAX_VALUE;
        HttpURLConnection c=(HttpURLConnection)new URL(urlString).openConnection();
        c.setRequestMethod(method);c.setRequestProperty("Authorization","Bearer "+token);
        c.setConnectTimeout(15000);c.setReadTimeout(30000);
        try {
            int code=c.getResponseCode();
            if(code<200||code>=300)throw new java.io.IOException("Drive "+code);
            if(c.getContentLengthLong()>maxBytes)throw new java.io.IOException("Visit photo exceeds size limit");
            try(InputStream in=c.getInputStream();ByteArrayOutputStream out=new ByteArrayOutputStream()){
                byte[] buf=new byte[8192];int n,total=0;
                while(true){
                    if(interruptBulkRestore && android.os.SystemClock.elapsedRealtime()>=bulkDeadline)
                        throw new java.net.SocketTimeoutException("Bulk photo total transfer timeout");
                    if(interruptBulkRestore && visitPhotoTransferPolicy.interruptExistingBulkRestore())
                        throw new java.io.InterruptedIOException("Bulk photo restore paused for active use");
                    n=in.read(buf);
                    if(interruptBulkRestore && android.os.SystemClock.elapsedRealtime()>=bulkDeadline)
                        throw new java.net.SocketTimeoutException("Bulk photo total transfer timeout");
                    if(n==-1)break;
                    total+=n;
                    if(total>maxBytes)throw new java.io.IOException("Visit photo exceeds size limit");
                    out.write(buf,0,n);
                }
                if(interruptBulkRestore && visitPhotoTransferPolicy.interruptExistingBulkRestore())
                    throw new java.io.InterruptedIOException("Bulk photo restore paused for active use");
                return out.toByteArray();
            }
        } finally {c.disconnect();}
    }

    private void notifyVisitPhotoProgress(final String requestId,final int current,final int total){runOnUiThread(new Runnable(){@Override public void run(){if(webView!=null)webView.evaluateJavascript("window.oaiVisitPhotoProgress&&window.oaiVisitPhotoProgress("+JSONObject.quote(requestId)+","+current+","+total+")",null);}});}
    private void notifyVisitPhotoLocalReady(final String requestId,final String itemJson,final int current,final int total){runOnUiThread(new Runnable(){@Override public void run(){if(webView!=null)webView.evaluateJavascript("window.oaiVisitPhotoLocalReady&&window.oaiVisitPhotoLocalReady("+JSONObject.quote(requestId)+","+JSONObject.quote(itemJson==null?"{}":itemJson)+","+current+","+total+")",null);}});}
    private void notifyVisitPhotoLocalComplete(final String requestId,final boolean ok,final int ready,final int total,final String message){runOnUiThread(new Runnable(){@Override public void run(){if(webView!=null)webView.evaluateJavascript("window.oaiVisitPhotoLocalComplete&&window.oaiVisitPhotoLocalComplete("+JSONObject.quote(requestId)+","+ok+","+ready+","+total+","+JSONObject.quote(message==null?"":message)+")",null);}});}
    private void notifyVisitPhotoSyncResult(final String localId,final boolean ok,final String fileId,final String mime,final String savedAt,final String message){runOnUiThread(new Runnable(){@Override public void run(){if(webView!=null)webView.evaluateJavascript("window.oaiVisitPhotoSyncResult&&window.oaiVisitPhotoSyncResult("+JSONObject.quote(localId)+","+ok+","+JSONObject.quote(fileId==null?"":fileId)+","+JSONObject.quote(mime==null?"":mime)+","+JSONObject.quote(savedAt==null?"":savedAt)+","+JSONObject.quote(message==null?"":message)+")",null);}});}
    private void notifyVisitPhotoUploadResult(final String requestId,final boolean ok,final String json,final String message){runOnUiThread(new Runnable(){@Override public void run(){if(webView!=null)webView.evaluateJavascript("window.oaiVisitPhotoUploadResult&&window.oaiVisitPhotoUploadResult("+JSONObject.quote(requestId)+","+ok+","+JSONObject.quote(json==null?"[]":json)+","+JSONObject.quote(message==null?"":message)+")",null);}});}
    private void notifyVisitPhotoLoaded(final String requestId,final boolean ok,final String mime,final String payload,final String message){runOnUiThread(new Runnable(){@Override public void run(){if(webView!=null)webView.evaluateJavascript("window.oaiVisitPhotoLoaded&&window.oaiVisitPhotoLoaded("+JSONObject.quote(requestId)+","+ok+","+JSONObject.quote(mime==null?"":mime)+","+JSONObject.quote(payload==null?"":payload)+","+JSONObject.quote(message==null?"":message)+")",null);}});}
    private void notifyVisitPhotoDeleteResult(final String requestId,final boolean ok,final String message){runOnUiThread(new Runnable(){@Override public void run(){if(webView!=null)webView.evaluateJavascript("window.oaiVisitPhotoDeleteResult&&window.oaiVisitPhotoDeleteResult("+JSONObject.quote(requestId)+","+ok+","+JSONObject.quote(message==null?"":message)+")",null);}});}
    private void notifyVisitPhotoAuthCancelled(String action,String payloadText){try{JSONObject p=new JSONObject(payloadText);String req=p.optString("requestId","");if("visit-photo-load".equals(action))notifyVisitPhotoLoaded(req,false,"","","Google Drive 연결이 취소되었습니다.");else if("visit-photo-delete".equals(action))notifyVisitPhotoDeleteResult(req,false,"Google Drive 연결이 취소되었습니다.");else notifyVisitPhotoUploadResult(req,false,"[]","Google Drive 연결이 취소되었습니다.");}catch(Exception ignored){}}

    // Serialized snapshot saves; protect GPS visits independently of WebView state.
    private String requestDrive(String token, String method, String urlString, byte[] payload, String contentType) throws Exception {
        HttpURLConnection c=(HttpURLConnection)new URL(urlString).openConnection(); c.setRequestMethod("PATCH".equals(method)?"POST":method); if("PATCH".equals(method))c.setRequestProperty("X-HTTP-Method-Override","PATCH"); c.setRequestProperty("Authorization","Bearer "+token); c.setRequestProperty("Accept","application/json"); c.setConnectTimeout(15000); c.setReadTimeout(20000);
        if(payload!=null){c.setDoOutput(true);c.setRequestProperty("Content-Type",contentType);OutputStream out=c.getOutputStream();out.write(payload);out.close();}
        int code=c.getResponseCode();InputStream in=code>=200&&code<300?c.getInputStream():c.getErrorStream();String body=readAll(in);c.disconnect();if(code<200||code>=300)throw new Exception("Drive "+code+": "+body);return body;
    }

    private String readAll(InputStream in) throws Exception { if(in==null)return "";BufferedReader r=new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));StringBuilder b=new StringBuilder();String line;while((line=r.readLine())!=null)b.append(line);r.close();return b.toString(); }

    // Single Google Drive appDataFolder transfer document; no backup history or scheduled calls.
    // Network-choice receipt is stored independently of visit records and photo binaries.
    // It stores no bearer tokens, photo IDs or personal visit data.
    private static final String PHOTO_CHOICE_NAME="cgm-photo-network-consent-v1.json";
    private String findPhotoChoiceFile(String token) throws Exception {
        String q=java.net.URLEncoder.encode("name = '"+PHOTO_CHOICE_NAME+"' and 'appDataFolder' in parents and trashed = false","UTF-8");
        String result=requestDrive(token,"GET","https://www.googleapis.com/drive/v3/files?spaces=appDataFolder&fields=files(id,name)&pageSize=10&q="+q,null,null);
        JSONArray files=new JSONObject(result).optJSONArray("files");
        return files!=null&&files.length()>0?files.optJSONObject(0).optString("id",""):"";
    }
    private void notifyPhotoChoice(final String status,final String value){
        runOnUiThread(new Runnable(){@Override public void run(){
            if(webView!=null)webView.evaluateJavascript("window.oaiPhotoNetworkCloudResult&&window.oaiPhotoNetworkCloudResult("+JSONObject.quote(status)+","+JSONObject.quote(value)+")",null);
        }});
    }
    private void loadPhotoChoice(String token) throws Exception {
        String id=findPhotoChoiceFile(token);
        if(id.isEmpty()){notifyPhotoChoice("missing","");return;}
        String data=requestDrive(token,"GET","https://www.googleapis.com/drive/v3/files/"+id+"?alt=media",null,null);
        if(data.length()>4096)throw new Exception("Invalid choice receipt");
        JSONObject receipt=new JSONObject(data);
        if(receipt.optInt("schema")!=1)throw new Exception("Invalid choice schema");
        String network=receipt.optString("network","");
        if(!"wifi".equals(network)&&!"any".equals(network))throw new Exception("Invalid network choice");
        notifyPhotoChoice("found",network);
    }
    private void savePhotoChoice(String token,String network) throws Exception {
        if(!"wifi".equals(network)&&!"any".equals(network))throw new Exception("Invalid network choice");
        String data="{\"schema\":1,\"network\":"+JSONObject.quote(network)+"}";
        byte[] bytes=data.getBytes(StandardCharsets.UTF_8);
        String id=findPhotoChoiceFile(token);
        if(!id.isEmpty()){
            requestDrive(token,"PATCH","https://www.googleapis.com/upload/drive/v3/files/"+id+"?uploadType=media",bytes,"application/json; charset=UTF-8");
        }else{
            String boundary="cgm"+UUID.randomUUID().toString().replace("-","");
            String metadata="{\"name\":"+JSONObject.quote(PHOTO_CHOICE_NAME)+",\"parents\":[\"appDataFolder\"],\"mimeType\":\"application/json\"}";
            ByteArrayOutputStream body=new ByteArrayOutputStream();
            body.write(("--"+boundary+"\r\nContent-Type: application/json; charset=UTF-8\r\n\r\n"+metadata+"\r\n--"+boundary+"\r\nContent-Type: application/json\r\n\r\n").getBytes(StandardCharsets.UTF_8));
            body.write(bytes);body.write(("\r\n--"+boundary+"--\r\n").getBytes(StandardCharsets.UTF_8));
            requestDrive(token,"POST","https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart&fields=id",body.toByteArray(),"multipart/related; boundary="+boundary);
        }
        notifyPhotoChoice("saved",network);
    }
    private static final String TRANSFER_NAME="cgm-single-transfer-v1.json";
    private String findTransferFile(String token) throws Exception {
        String q=java.net.URLEncoder.encode("name = '"+TRANSFER_NAME+"' and 'appDataFolder' in parents and trashed = false","UTF-8");
        String result=requestDrive(token,"GET","https://www.googleapis.com/drive/v3/files?spaces=appDataFolder&fields=files(id,name)&pageSize=10&q="+q,null,null);
        JSONArray files=new JSONObject(result).optJSONArray("files");return files!=null&&files.length()>0?files.optJSONObject(0).optString("id",""):"";
    }
    private void saveTransferData(String token,String data) throws Exception {
        if(data==null||data.length()==0||data.length()>8*1024*1024)throw new Exception("Invalid transfer size");
        JSONObject parsed=new JSONObject(data);
        if(!"cgm-single-transfer".equals(parsed.optString("format"))||parsed.optInt("schema")!=1||parsed.optJSONObject("data")==null)throw new Exception("Invalid transfer format");
        String id=findTransferFile(token);
        byte[] bytes=data.getBytes(StandardCharsets.UTF_8);
        if(id.length()>0){
            requestDrive(token,"PATCH","https://www.googleapis.com/upload/drive/v3/files/"+id+"?uploadType=media",bytes,"application/json; charset=UTF-8");
        } else {
            String boundary="cgm"+UUID.randomUUID().toString().replace("-","");
            String metadata="{\"name\":"+JSONObject.quote(TRANSFER_NAME)+",\"parents\":[\"appDataFolder\"],\"mimeType\":\"application/json\"}";
            ByteArrayOutputStream body=new ByteArrayOutputStream();
            body.write(("--"+boundary+"\r\nContent-Type: application/json; charset=UTF-8\r\n\r\n"+metadata+"\r\n--"+boundary+"\r\nContent-Type: application/json\r\n\r\n").getBytes(StandardCharsets.UTF_8));
            body.write(bytes);body.write(("\r\n--"+boundary+"--\r\n").getBytes(StandardCharsets.UTF_8));
            requestDrive(token,"POST","https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart&fields=id",body.toByteArray(),"multipart/related; boundary="+boundary);
        }
        notifyDataTransferResult("save",true,"","");
    }
    private void loadTransferData(String token) throws Exception {
        String id=findTransferFile(token);
        if(id.length()==0){notifyDataTransferResult("load",false,"","이전에 옮긴 데이터 파일이 없습니다.");return;}
        String data=requestDrive(token,"GET","https://www.googleapis.com/drive/v3/files/"+id+"?alt=media",null,null);
        if(data.length()>8*1024*1024)throw new Exception("Transfer too large");
        notifyDataTransferResult("load",true,data,"");
    }
    private void notifyDataTransferResult(final String action,final boolean ok,final String payload,final String message){
        runOnUiThread(new Runnable(){@Override public void run(){
            if(webView!=null)webView.evaluateJavascript("window.oaiDataTransferResult&&window.oaiDataTransferResult("+JSONObject.quote(action)+","+ok+","+JSONObject.quote(payload==null?"":payload)+","+JSONObject.quote(message==null?"":message)+")",null);
        }});
    }

    private void notifyDriveStatus(final String status, final String message) { runOnUiThread(new Runnable(){@Override public void run(){if(webView!=null)webView.evaluateJavascript("window.oaiGoogleDriveStatus&&window.oaiGoogleDriveStatus("+JSONObject.quote(status)+","+JSONObject.quote(message)+")",null);}}); }

    private void loadVisitPhotoFast(final String requestId,final String fileId,final boolean thumb){
        visitPhotoReadExecutor.execute(new Runnable(){@Override public void run(){
            if(fileId!=null&&fileId.startsWith("local_")){byte[] local=readPendingVisitPhoto(fileId,thumb);if(local==null||local.length==0)local=readPendingVisitPhoto(fileId,false);if(local!=null&&local.length>0){notifyVisitPhotoLoaded(requestId,true,"image/jpeg",visitPhotoAssetUrl(fileId,thumb),"");return;}notifyVisitPhotoLoaded(requestId,false,"","","로컬 사진을 찾지 못했습니다.");return;}
            byte[] cached=readVisitPhotoCache(fileId,thumb);
            if(cached!=null&&cached.length>0){notifyVisitPhotoLoaded(requestId,true,"image/jpeg",visitPhotoAssetUrl(fileId,thumb),"");return;}
            runOnUiThread(new Runnable(){@Override public void run(){try{JSONObject p=new JSONObject();p.put("requestId",requestId);p.put("fileId",fileId);p.put("thumb",thumb);requestGoogleDriveAuthorization("visit-photo-load",p.toString());}catch(Exception e){notifyVisitPhotoLoaded(requestId,false,"","","사진을 불러오지 못했습니다.");}}});
        }});
    }

    private void loadVisitPhotoFastV2(final String requestId,final String photoId,final String driveFileId,final boolean thumb){
        visitPhotoReadExecutor.execute(new Runnable(){@Override public void run(){
            String pid=photoId==null?"":photoId;String did=driveFileId==null?"":driveFileId;
            // Fast path for an already verified durable photo: the asset interceptor
            // will read its bytes once when WebView actually requests the image.
            // Keep the existing byte-based path below for legacy and incomplete files.
            if(!pid.isEmpty()){
                File pending=pendingVisitPhotoFile(pid,thumb);
                File stored=visitPhotoStoreFile(pid,thumb);
                if(validDurableVisitPhoto(pending)||validDurableVisitPhoto(stored)){
                    notifyVisitPhotoLoaded(requestId,true,"image/jpeg",visitPhotoAssetUrl(pid,thumb),"");
                    return;
                }
            }
            byte[] local=readPendingVisitPhoto(pid,thumb);if(local==null||local.length==0)local=readPendingVisitPhoto(pid,false);
            if(local!=null&&local.length>0){
                if(thumb&&!validDurableVisitPhoto(visitPhotoStoreFile(pid,true))){
                    byte[] generated=makeVisitPhotoThumbnail(local);
                    if(generated!=null&&generated.length>0)cacheVisitPhotoBytes(pid,generated,true);
                }
                if(!thumb||validDurableVisitPhoto(visitPhotoStoreFile(pid,true))){
                    notifyVisitPhotoLoaded(requestId,true,"image/jpeg",visitPhotoAssetUrl(pid,thumb),"");return;
                }
                notifyVisitPhotoLoaded(requestId,false,"","","썸네일을 저장하지 못했습니다.");return;
            }
            if(did.length()==0)did=visitPhotoRemoteId(pid);if(did.length()==0&&!pid.startsWith("local_"))did=pid;
            if(did.length()==0){notifyVisitPhotoLoaded(requestId,false,"","","사진을 아직 동기화하지 못했습니다.");return;}
            if(thumb){byte[] restored=readVisitPhotoCache(pid,true);
                if(restored!=null&&restored.length>0){notifyVisitPhotoLoaded(requestId,true,"image/jpeg",visitPhotoAssetUrl(pid,true),"");return;}}
            final String remote=did;byte[] cached=readVisitPhotoCache(remote,thumb);if(cached!=null&&cached.length>0){notifyVisitPhotoLoaded(requestId,true,"image/jpeg",visitPhotoAssetUrl(remote,thumb),"");return;}
            runOnUiThread(new Runnable(){@Override public void run(){try{JSONObject p=new JSONObject();p.put("requestId",requestId);p.put("fileId",remote);p.put("photoId",pid);p.put("thumb",thumb);requestGoogleDriveAuthorization("visit-photo-load",p.toString());}catch(Exception e){notifyVisitPhotoLoaded(requestId,false,"","","사진을 불러오지 못했습니다.");}}});
        }});
    }

    private boolean nearbyPermissionRequestedThisLaunch = false;

    // Developer photo-download diagnostics: read the existing SQLite journal only.
    // Never starts cloud authorization, network I/O, downloads, retries or mutations.
    private void collectVisitPhotoDownloadDiagnostics() {
        visitPhotoReadExecutor.execute(new Runnable(){@Override public void run(){
            String report;
            String status="done";
            try {
                StringBuilder out=new StringBuilder(5000);
                out.append("사진 다운로드 진단 (읽기 전용)\n");
                out.append("자동 사진 동기화: ").append(visitPhotoAutoEnabled?"ON":"OFF").append("\n");
                out.append("네트워크 허용: ").append(visitPhotoNetworkAllowed()?"예":"아니오").append("\n");
                out.append("Wi-Fi 전용: ").append(visitPhotoWifiOnly?"예":"아니오").append("\n");
                out.append("앱 표시 상태: ").append(visitPhotoAppVisible?"화면":"백그라운드").append("\n");
                out.append("자동 복원 작업: ").append(visitPhotoRestoreDispatchQueued.get()?"동작 중":"대기").append("\n");
                out.append("화면 요청 우선 플래그: ").append(visitPhotoTransferPolicy.blocked(VisitPhotoTransferPolicy.Job.BULK_RESTORE)).append("\n");
                long now=System.currentTimeMillis();
                out.append("전체 오류 대기: ").append(Math.max(0L,(visitPhotoTransferErrorUntil-now)/1000L)).append("초\n");
                out.append("인증 캐시: ").append(cachedDriveAccessToken!=null&&!cachedDriveAccessToken.isEmpty()&&now-cachedDriveAccessTokenAt<DRIVE_TOKEN_CACHE_MS?"유효":"없음/만료").append("\n");
                java.io.File dbFile=getDatabasePath("visit_photo_transfer_index_v1.db");
                if(!dbFile.isFile()){
                    out.append("복원 대기열 DB 없음 (아직 등록되지 않았을 수 있음)\n");
                }else{
                    // OPEN_READONLY prevents schema upgrade, write or recovery-by-diagnostic.
                    try(android.database.sqlite.SQLiteDatabase db=android.database.sqlite.SQLiteDatabase.openDatabase(
                            dbFile.getAbsolutePath(),null,android.database.sqlite.SQLiteDatabase.OPEN_READONLY)){
                        try(android.database.Cursor c=db.rawQuery(
                          "SELECT transfer_state,thumb_state,COUNT(*) FROM photo_restore GROUP BY transfer_state,thumb_state",null)){
                            out.append("\n복원 상태별 건수\n");
                            while(c.moveToNext())out.append(c.getString(0)).append(" / ").append(c.getString(1)).append(": ").append(c.getInt(2)).append("\n");
                        }
                        try(android.database.Cursor c=db.rawQuery(
                          "SELECT COUNT(*), SUM(CASE WHEN next_retry_at>? THEN 1 ELSE 0 END), MAX(retry_count) FROM photo_restore",
                          new String[]{String.valueOf(now)})){
                            if(c.moveToFirst())out.append("복원 등록: ").append(c.getInt(0))
                               .append(" · 다음 시도 대기: ").append(c.getInt(1))
                               .append(" · 최대 실패횟수: ").append(c.getInt(2)).append("\n");
                        }
                        out.append("\n장소별 복원 대기 (상위 25개)\n");
                        try(android.database.Cursor c=db.rawQuery(
                          "SELECT place_code,COUNT(*),SUM(CASE WHEN thumb_state='pending' THEN 1 ELSE 0 END),"
                            +" SUM(CASE WHEN next_retry_at>? THEN 1 ELSE 0 END)"
                            +" FROM photo_restore WHERE transfer_state!='complete' GROUP BY place_code"
                            +" ORDER BY COUNT(*) DESC LIMIT 25",new String[]{String.valueOf(now)})){
                            while(c.moveToNext())out.append(c.getString(0)).append(": 미완료 ").append(c.getInt(1))
                                .append(" · 썸네일 대기 ").append(c.getInt(2)).append(" · 재시도 대기 ").append(c.getInt(3)).append("\n");
                        }
                        out.append("\n미완료 사진 샘플 (최대 30개, ID 일부만 표시)\n");
                        try(android.database.Cursor c=db.rawQuery(
                          "SELECT photo_id,place_code,transfer_state,thumb_state,retry_count,next_retry_at,drive_file_id"
                            +" FROM photo_restore WHERE transfer_state!='complete'"
                            +" ORDER BY retry_count DESC,next_retry_at DESC LIMIT 30",null)){
                            while(c.moveToNext()){
                                String pid=c.getString(0); String suffix=pid==null?"":pid.substring(Math.max(0,pid.length()-6));
                                // Validate local files only; no content decoding or remote request.
                                boolean thumb=validDurableVisitPhoto(visitPhotoStoreFile(pid,true))
                                    ||validDurableVisitPhoto(pendingVisitPhotoFile(pid,true));
                                boolean original=validDurableVisitPhoto(visitPhotoStoreFile(pid,false))
                                    ||validDurableVisitPhoto(pendingVisitPhotoFile(pid,false));
                                long wait=Math.max(0L,(c.getLong(5)-now)/1000L);
                                out.append(c.getString(1)).append(" / …").append(suffix)
                                   .append(" / ").append(c.getString(2)).append(" / 썸:").append(c.getString(3))
                                   .append(" / 로컬:").append(thumb?"T":"-").append(original?"F":"-")
                                   .append(" / Drive ID:").append(c.getString(6)==null||c.getString(6).isEmpty()?"없음":"있음")
                                   .append(" / 실패:").append(c.getInt(4)).append(" / 대기:").append(wait).append("초\n");
                            }
                        }
                    }
                }
                out.append("\n※ Drive 원격 파일 존재·실제 네트워크 오류는 조회하지 않습니다. ")
                   .append("사진·백업·작업 상태를 변경하지 않습니다.");
                report=out.toString();
            }catch(Exception e){status="error";report="읽기 전용 진단 실패: "+e.getClass().getSimpleName();}
            final String resultStatus=status, resultReport=report;
            runOnUiThread(new Runnable(){@Override public void run(){
                if(webView!=null)webView.evaluateJavascript(
                   "(function(){if(window.oaiDevPhotoDownloadDiagnosisResult)window.oaiDevPhotoDownloadDiagnosisResult("
                     +org.json.JSONObject.quote(resultStatus)+","+org.json.JSONObject.quote(resultReport)+");})()",null);
            }});
        }});
    }

    // Developer-only explicit remote inspection. Does not change photos, cloud state,
    // transfer journal, retry timers or backup preferences. Only small Drive metadata GETs.
    private final java.util.concurrent.atomic.AtomicBoolean devMissingPhotoDriveCheckRunning =
        new java.util.concurrent.atomic.AtomicBoolean(false);
    private void notifyDevMissingPhotoDriveCheck(final String state, final String report){
        runOnUiThread(new Runnable(){@Override public void run(){
            if(webView!=null)webView.evaluateJavascript(
                "(function(){if(window.oaiDevMissingPhotoDriveCheckResult)window.oaiDevMissingPhotoDriveCheckResult("
                +JSONObject.quote(state)+","+JSONObject.quote(report)+");})()",null);
        }});
    }
    private void checkMissingPhotosInDrive(){
        if(!devMissingPhotoDriveCheckRunning.compareAndSet(false,true)){
            notifyDevMissingPhotoDriveCheck("error","이미 Drive 확인을 진행하고 있습니다.");return;
        }
        // Never trigger Google account selection solely for diagnostics.
        final String token=cachedDriveAccessToken;
        if(token==null||token.isEmpty()||System.currentTimeMillis()-cachedDriveAccessTokenAt>=DRIVE_TOKEN_CACHE_MS){
            devMissingPhotoDriveCheckRunning.set(false);
            notifyDevMissingPhotoDriveCheck("error","Google 인증 캐시가 만료됐습니다. 앱에서 Google Drive를 정상 연결한 후 다시 확인하세요. 진단을 위해 계정 선택창을 띄우지 않습니다.");
            return;
        }
        driveOperationExecutor.execute(new Runnable(){@Override public void run(){
            String result;String state="done";
            try{
                File dbFile=getDatabasePath("visit_photo_transfer_index_v1.db");
                if(!dbFile.isFile())throw new java.io.IOException("복원 장부가 없습니다.");
                StringBuilder out=new StringBuilder("미완료 사진 Google Drive 확인 (읽기 전용)\n");
                out.append("검사 범위: 미완료 최대 10장 · Drive 파일 검색/메타데이터만 조회\n");
                int examined=0,missing=0,unique=0,duplicate=0,errors=0;
                try(android.database.sqlite.SQLiteDatabase db=android.database.sqlite.SQLiteDatabase.openDatabase(
                    dbFile.getAbsolutePath(),null,android.database.sqlite.SQLiteDatabase.OPEN_READONLY);
                    android.database.Cursor c=db.rawQuery(
                        "SELECT photo_id,place_code,drive_file_id FROM photo_restore "
                        +"WHERE transfer_state!='complete' ORDER BY next_retry_at ASC LIMIT 10",null)){
                    while(c.moveToNext()){
                        String pid=c.getString(0),place=c.getString(1),id=c.getString(2);
                        if(pid==null||pid.isEmpty())continue;
                        examined++;
                        String tail=pid.substring(Math.max(0,pid.length()-6));
                        String label=(place==null?"장소 미상":place)+" / …"+tail;
                        try{
                            if(id!=null&&!id.isEmpty()){
                                String url="https://www.googleapis.com/drive/v3/files/"+Uri.encode(id)
                                    +"?fields=id,name,trashed";
                                JSONObject detail=new JSONObject(requestDrive(token,"GET",url,null,null));
                                if(detail.optBoolean("trashed",false)){
                                    missing++;out.append(label).append(": 저장된 Drive ID의 파일이 휴지통에 있음\n");
                                }else{
                                    unique++;out.append(label).append(": 저장된 Drive ID 조회 성공 (파일 다운로드 미검증)\n");
                                }
                            }else{
                                String expected="cgm-visit-"+pid.replace("local_","")+".jpg";
                                // File names containing apostrophes need escaping in Drive q.
                                String q="name = '"+expected.replace("\\","\\\\").replace("'","\\'")
                                    +"' and 'appDataFolder' in parents and trashed = false";
                                String url="https://www.googleapis.com/drive/v3/files?spaces=appDataFolder"
                                    +"&pageSize=3&fields=nextPageToken,files(id,name)&q="
                                    +java.net.URLEncoder.encode(q,"UTF-8");
                                JSONObject found=new JSONObject(requestDrive(token,"GET",url,null,null));
                                JSONArray files=found.optJSONArray("files");
                                if(files==null)throw new java.io.IOException("Drive 검색 응답 형식 오류");
                                int count=files.length();
                                if(count==0){missing++;out.append(label).append(": 파일명 일치 0개\n");}
                                else if(count==1&&found.optString("nextPageToken","").isEmpty()){
                                    unique++;out.append(label).append(": 파일명 일치 1개 (Drive ID 등록 누락 가능)\n");
                                }else{duplicate++;out.append(label).append(": 파일명 일치 2개 이상 (자동 선택 금지)\n");}
                            }
                        }catch(Exception e){
                            errors++;
                            out.append(label).append(": 조회 오류 ").append(devPhotoFailureKind(e)).append("\n");
                        }
                    }
                }
                out.append("검사 ").append(examined).append("장 · 조회 성공 ").append(unique)
                    .append(" · 0개/휴지통 ").append(missing).append(" · 중복 ").append(duplicate)
                    .append(" · 오류 ").append(errors).append("\n");
                if(examined==0)out.append("현재 미완료 사진이 없습니다.\n");
                out.append("※ 파일 내용 다운로드 여부는 검사하지 않았습니다. 사진·Drive·장부·재시도 시간은 변경하지 않았습니다.");
                result=out.toString();
            }catch(Exception e){state="error";result="원격 확인 실패: "+devPhotoFailureKind(e);}
            finally{devMissingPhotoDriveCheckRunning.set(false);}
            notifyDevMissingPhotoDriveCheck(state,result);
        }});
    }

    // Developer-only, immediate notification. No recurring schedule and no visit data writes.
    private void sendAnniversaryTestNotification(){
        if(Build.VERSION.SDK_INT>=33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED){
            anniversaryNotificationPermissionPending=true;
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},REQ_ANNIVERSARY_NOTIFICATION);
            return;
        }
        NotificationManager manager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if(manager==null)return;
        if(Build.VERSION.SDK_INT>=26){
            NotificationChannel channel=new NotificationChannel(ANNIVERSARY_TEST_CHANNEL,"순례 추억 테스트",NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("개발자 모드에서 요청한 순례 추억 알림 테스트");
            channel.enableVibration(true);
            channel.setSound(android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION),
                new android.media.AudioAttributes.Builder().setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION).build());
            manager.createNotificationChannel(channel);
            NotificationChannel effective=manager.getNotificationChannel(ANNIVERSARY_TEST_CHANNEL);
            if(effective!=null)Log.i("AnniversaryNotification", "channel importance="+effective.getImportance()+" sound="+effective.getSound());
        }
        Intent open=new Intent(this,MainActivity.class);
        open.setAction("kr.catholic.gildonmu.ANNIVERSARY_TEST_OPEN");
        open.putExtra("anniversary_test_date","2026-10-09");
        open.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent tap=PendingIntent.getActivity(this,9821,open,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        Notification.Builder builder=Build.VERSION.SDK_INT>=26?new Notification.Builder(this,ANNIVERSARY_TEST_CHANNEL):new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.ic_anniversary_notification)
            .setContentTitle("그날의 순례를 기억하시나요?")
            .setContentText("10월 9일의 순례 사진과 추억을 다시 만나보세요.")
            .setStyle(new Notification.BigTextStyle().bigText("2026년 10월 9일의 순례 사진과 방문 기록을 다시 만나보세요. (개발자 테스트)"))
            .setAutoCancel(true).setContentIntent(tap).setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE);
        manager.notify(9821,builder.build());
    }
    private void dispatchAnniversaryNotificationOpen(){
        if(!anniversaryNotificationOpened||webView==null)return;
        // Warm WebView: the JS handler is already installed. Cold launch is consumed
        // by the web load handler after scripts and place catalog are available.
        webView.evaluateJavascript("(function(){if(window.__oaiAnniversaryHandlerReady){window.dispatchEvent(new CustomEvent('oai-anniversary-notification-open',{detail:{date:'2026-10-09'}}));return true;}return false;})()",value->{
            if("true".equals(value))anniversaryNotificationOpened=false;
        });
    }
    private void acceptAnniversaryIntent(Intent intent){
        if(intent!=null && "kr.catholic.gildonmu.ANNIVERSARY_TEST_OPEN".equals(intent.getAction()) &&
            "2026-10-09".equals(intent.getStringExtra("anniversary_test_date"))){
            anniversaryNotificationOpened=true;
        }
    }
    private static final String CAR_PILGRIMAGE_CHANNEL="pilgrimage_car_progress_v1";
    private static final String CAR_PILGRIMAGE_OPEN="kr.catholic.gildonmu.car.OPEN";
    private static final int CAR_PILGRIMAGE_NOTIFICATION_ID=6403;
    private long pendingCarStartedAt=0L;
    private int pendingCarVisited=0, pendingCarTotal=0;
    private String pendingCarNextName="";
    private void showCarPilgrimageNotice(long startedAt, int visited, int total, String nextName) {
        if(startedAt<=0L)return;
        if(Build.VERSION.SDK_INT>=33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED){
            pendingCarStartedAt=startedAt;
            pendingCarVisited=visited;
            pendingCarTotal=total;
            pendingCarNextName=nextName==null?"":nextName;
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},REQ_WALK_NOTIFICATION);
            return;
        }
        NotificationManager manager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=26)manager.createNotificationChannel(new NotificationChannel(
                CAR_PILGRIMAGE_CHANNEL,"자동차 순례 진행",NotificationManager.IMPORTANCE_LOW));
        Intent open=new Intent(this,MainActivity.class);
        open.setAction(CAR_PILGRIMAGE_OPEN);
        open.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent action=PendingIntent.getActivity(this,6403,open,
                PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        long minutes=Math.max(0L,(System.currentTimeMillis()-startedAt)/60000L);
        String elapsed=String.format(java.util.Locale.KOREA,"%02d:%02d",minutes/60,minutes%60);
        String progress="자동차 "+elapsed+" · 방문 "+Math.max(0,visited)+"/"+Math.max(0,total);
        String next=(nextName==null||nextName.trim().isEmpty())?"다음 순례지 확인 중":"다음: "+nextName.trim();
        String detail="순례시간 "+elapsed+" · 방문 "+Math.max(0,visited)+"/"+Math.max(0,total)+"곳\n"+next;
        Notification.Builder builder=Build.VERSION.SDK_INT>=26?
                new Notification.Builder(this,CAR_PILGRIMAGE_CHANNEL):new Notification.Builder(this);
        if(Build.VERSION.SDK_INT>=26){
            java.util.Calendar midnight=java.util.Calendar.getInstance();
            midnight.setTimeInMillis(startedAt);
            midnight.add(java.util.Calendar.DATE,1);
            midnight.set(java.util.Calendar.HOUR_OF_DAY,0);
            midnight.set(java.util.Calendar.MINUTE,0);
            midnight.set(java.util.Calendar.SECOND,0);
            midnight.set(java.util.Calendar.MILLISECOND,0);
            builder.setTimeoutAfter(Math.max(1000L,midnight.getTimeInMillis()-System.currentTimeMillis()));
        }
        builder.setSmallIcon(R.drawable.ic_pilgrimage_notification)
                .setLargeIcon(PilgrimageNotificationIcon.largeIcon(this))
                // 접힌 알림의 제목과 본문에 진행 요약을 각각 표시한다.
                .setContentTitle(progress)
                .setContentText(next)
                .setStyle(new Notification.BigTextStyle().bigText("자동차 순례 진행 중\n"+detail))
                .setContentIntent(action)
                .setOngoing(true).setOnlyAlertOnce(true).setShowWhen(false);
        manager.notify(CAR_PILGRIMAGE_NOTIFICATION_ID,builder.build());
    }
    private void stopCarPilgrimageNotice(){
        ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).cancel(CAR_PILGRIMAGE_NOTIFICATION_ID);
        pendingCarStartedAt=0L;
        getSharedPreferences("pilgrimage_car_notice",MODE_PRIVATE).edit().remove("pending_open").apply();
    }
    private void acceptWalkingReturnIntent(Intent intent) {
        if (intent != null && WalkingLocationService.ACTION_OPEN.equals(intent.getAction())) {
            getSharedPreferences("walking_service_status", MODE_PRIVATE).edit()
                    .putBoolean("pending_open", true).apply();
        }
    }
    private void dispatchWalkingService(String action, long startedAt, int visited, int total,
                                        String nextName, String distanceLabel, String waypointsJson) {
        Intent service = new Intent(this, WalkingLocationService.class);
        service.setAction(action);
        service.putExtra(WalkingLocationService.EXTRA_STARTED_AT, startedAt);
        service.putExtra(WalkingLocationService.EXTRA_VISITED, visited);
        service.putExtra(WalkingLocationService.EXTRA_TOTAL, total);
        service.putExtra(WalkingLocationService.EXTRA_NEXT_NAME, nextName == null ? "" : nextName);
        service.putExtra(WalkingLocationService.EXTRA_DISTANCE_LABEL,
                distanceLabel == null ? "거리 확인 중" : distanceLabel);
        if (waypointsJson != null) service.putExtra(WalkingLocationService.EXTRA_WAYPOINTS_JSON, waypointsJson);
        if (WalkingLocationService.ACTION_STOP.equals(action)) {
            synchronized (WalkingLocationService.FIX_QUEUE_LOCK) {
                getSharedPreferences("walking_service_status", MODE_PRIVATE).edit().clear().commit();
            }
            stopService(service);
        } else if (WalkingLocationService.ACTION_UPDATE.equals(action)) {
            startService(service);
        } else {
            ContextCompat.startForegroundService(this, service);
        }
    }
    private android.speech.tts.TextToSpeech gpsStampTts;
    private boolean gpsStampTtsReady;
    private void speakGpsStampNative(String message){
        if(message==null||message.trim().isEmpty())return;
        android.media.AudioManager audio=(android.media.AudioManager)getSystemService(AUDIO_SERVICE);
        if(audio==null||audio.getRingerMode()!=android.media.AudioManager.RINGER_MODE_NORMAL)return;
        if(gpsStampTts==null){
            final String firstMessage=message;
            gpsStampTts=new android.speech.tts.TextToSpeech(this,status->{
                gpsStampTtsReady=status==android.speech.tts.TextToSpeech.SUCCESS;
                if(gpsStampTtsReady){
                    gpsStampTts.setLanguage(java.util.Locale.KOREAN);
                    android.media.AudioManager currentAudio=(android.media.AudioManager)getSystemService(AUDIO_SERVICE);
                    if(currentAudio!=null&&currentAudio.getRingerMode()==android.media.AudioManager.RINGER_MODE_NORMAL)
                        gpsStampTts.speak(firstMessage,android.speech.tts.TextToSpeech.QUEUE_ADD,null,"gps-stamp-"+System.currentTimeMillis());
                }
            });
            return;
        }
        if(gpsStampTtsReady){
            gpsStampTts.speak(message,android.speech.tts.TextToSpeech.QUEUE_ADD,null,"gps-stamp-"+System.currentTimeMillis());
        }
    }
    private class GildongmuNativeBridge {
        @JavascriptInterface
        public void speakGpsStamp(String message){
            final String safe=message==null?"":message.trim();
            if(safe.isEmpty()||safe.length()>150)return;
            runOnUiThread(()->speakGpsStampNative(safe));
        }
        // Availability is scoped to this Android bridge, not the public web page.
        @JavascriptInterface
        public boolean isWalkingPilgrimageSupported() { return true; }
        // Foreground walking tracking specifically requires precise GPS permission.
        // hasLocationPermission() intentionally allows coarse-only for other app features,
        // so it must not be reused for this foreground location service.
        @JavascriptInterface
        public boolean hasWalkingLocationPermission() {
            return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                    || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                           == PackageManager.PERMISSION_GRANTED;
        }
        @JavascriptInterface
        public void startWalkingPilgrimage(long startedAt, int visited, int total,
                                           String nextName, String distanceLabel, String waypointsJson) {
            runOnUiThread(() -> {
                if (!hasWalkingLocationPermission()) return;
                // Android 13+: ask once when the user starts a walking pilgrimage.
                // A location foreground service can run without this permission,
                // but its ongoing notice may be absent from the notification drawer.
                if (Build.VERSION.SDK_INT >= 33 &&
                        checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQ_WALK_NOTIFICATION);
                }
                try { dispatchWalkingService("kr.catholic.gildonmu.walk.START",
                        startedAt, visited, total, nextName, distanceLabel, waypointsJson); }
                catch (RuntimeException ex) { Log.w("WalkingPilgrimage", "Start denied", ex); }
            });
        }
        @JavascriptInterface
        public void updateWalkingPilgrimage(long startedAt, int visited, int total,
                                            String nextName, String distanceLabel, String waypointsJson) {
            runOnUiThread(() -> {
                try { dispatchWalkingService(WalkingLocationService.ACTION_UPDATE,
                        startedAt, visited, total, nextName, distanceLabel, waypointsJson); }
                catch (RuntimeException ex) { Log.w("WalkingPilgrimage", "Update denied", ex); }
            });
        }
        @JavascriptInterface
        public boolean consumeWalkingReturnRequest() {
            android.content.SharedPreferences prefs = getSharedPreferences("walking_service_status", MODE_PRIVATE);
            if (!prefs.getBoolean("pending_open", false)) return false;
            if (prefs.getLong(WalkingLocationService.EXTRA_STARTED_AT, 0L) <= 0L) return false;
            return prefs.edit().remove("pending_open").commit();
        }
        @JavascriptInterface
        public String getWalkingPilgrimageStatus() {
            android.content.SharedPreferences prefs = getSharedPreferences("walking_service_status", MODE_PRIVATE);
            org.json.JSONObject data = new org.json.JSONObject();
            try {
                data.put("startedAt", prefs.getLong(WalkingLocationService.EXTRA_STARTED_AT, 0L));
                data.put("visited", prefs.getInt(WalkingLocationService.EXTRA_VISITED, 0));
                data.put("total", prefs.getInt(WalkingLocationService.EXTRA_TOTAL, 0));
                data.put("lastFix", prefs.getString("last_fix", ""));
            } catch (org.json.JSONException ignored) { }
            return data.toString();
        }
        // Peek, then acknowledge only after the web visit pipeline has processed the fixes.
        // Removing on read could silently lose a visit if the WebView is killed mid-replay.
        @JavascriptInterface
        public String consumeWalkingPilgrimageFixes(long expectedStartedAt) {
            android.content.SharedPreferences prefs = getSharedPreferences("walking_service_status", MODE_PRIVATE);
            if (expectedStartedAt <= 0L ||
                prefs.getLong(WalkingLocationService.EXTRA_STARTED_AT, 0L) != expectedStartedAt) return "";
            return prefs.getString("pending_fixes", "");
        }
        @JavascriptInterface
        public void acknowledgeWalkingPilgrimageFixes(long expectedStartedAt, long throughTime) {
            android.content.SharedPreferences prefs = getSharedPreferences("walking_service_status", MODE_PRIVATE);
            if (expectedStartedAt <= 0L || throughTime <= 0L ||
                prefs.getLong(WalkingLocationService.EXTRA_STARTED_AT, 0L) != expectedStartedAt) return;
            synchronized (WalkingLocationService.FIX_QUEUE_LOCK) {
                StringBuilder remaining = new StringBuilder();
                String[] rows = prefs.getString("pending_fixes", "").split("\n");
                for (String row : rows) {
                    if (row.isEmpty()) continue;
                    String[] parts = row.split(",");
                    if (parts.length != 4) continue;
                    try {
                        if (Long.parseLong(parts[3]) > throughTime) remaining.append(row).append('\n');
                    } catch (NumberFormatException ignored) { }
                }
                prefs.edit().putString("pending_fixes", remaining.toString()).commit();
            }
        }
        @JavascriptInterface
        public void updateCarPilgrimage(long startedAt, int visited, int total, String nextName){
            runOnUiThread(()->showCarPilgrimageNotice(startedAt,visited,total,nextName));
        }
        @JavascriptInterface
        public void stopCarPilgrimage(){
            runOnUiThread(()->stopCarPilgrimageNotice());
        }
        @JavascriptInterface
        public boolean consumeCarPilgrimageReturn(){
            SharedPreferences prefs=getSharedPreferences("pilgrimage_car_notice",MODE_PRIVATE);
            return prefs.getBoolean("pending_open",false) && prefs.edit().remove("pending_open").commit();
        }
        @JavascriptInterface
        public void stopWalkingPilgrimage() {
            runOnUiThread(() -> dispatchWalkingService(WalkingLocationService.ACTION_STOP,
                    0L, 0, 0, "", "", null));
        }

        @JavascriptInterface
        public void setPhotoViewerRotation(boolean enabled){
            runOnUiThread(()->{
                if(photoViewerRotationEnabled==enabled)return;
                photoViewerRotationEnabled=enabled;
                applyOrientationPolicy(enabled?"photo-open":"photo-close");
                applyPhotoViewerFullscreen();
            });
        }
        @JavascriptInterface
        public String consumePendingAnniversaryDate(){
            if(!anniversaryNotificationOpened)return "";
            anniversaryNotificationOpened=false;
            return "2026-10-09";
        }

        @JavascriptInterface
        public void sendPilgrimageAnniversaryTestNotification(){
            runOnUiThread(()->sendAnniversaryTestNotification());
        }
        @JavascriptInterface
        public void openAppLocationSettings(){
            runOnUiThread(new Runnable(){@Override public void run(){
                try{
                    Intent intent=new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:"+getPackageName()));
                    startActivity(intent);
                }catch(Exception ignored){}
            }});
        }
        @JavascriptInterface
        public boolean isNearbyLocationPermissionGranted(){return hasLocationPermission();}
        @JavascriptInterface
        public boolean canRequestNearbyLocationPermission(){
            return !hasLocationPermission() && !nearbyPermissionRequestedThisLaunch;
        }
        @JavascriptInterface
        public boolean requestNearbyLocationPermission(){
            if(hasLocationPermission() || nearbyPermissionRequestedThisLaunch)return false;
            nearbyPermissionRequestedThisLaunch=true;
            runOnUiThread(new Runnable(){@Override public void run(){
                if(!hasLocationPermission())requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                },REQ_LOCATION);
                else nearbyPermissionRequestedThisLaunch=false;
            }});
            return true;
        }
        // Future web integration: only real active work should set these flags.
        // This does not enable cloud transfers or initiate any network operation.
        @JavascriptInterface
        public void setVisitPhotoTransferActivity(final String activity, final boolean active) {
            if ("location".equals(activity)) visitPhotoTransferPolicy.setLocationActive(active);
            else if ("photo-processing".equals(activity)) visitPhotoTransferPolicy.setPhotoProcessing(active);
            else if ("stamp-list".equals(activity)) visitPhotoTransferPolicy.setStampListVisible(active);
            else if ("requested-photo".equals(activity)) visitPhotoTransferPolicy.setRequestedPhotoPending(active);
            if(!active && ("location".equals(activity) || "photo-processing".equals(activity)
                || "stamp-list".equals(activity) || "requested-photo".equals(activity))){
                runOnUiThread(new Runnable(){@Override public void run(){
                    resumeAuthorizedVisitPhotoTransfers();
                }});
            }
        }
        // Optional v380 bridge; older production APKs never receive these calls.
        // Only UI admission flags, no Drive read/write, GPS or visit record changes.
        @JavascriptInterface
        public void setVisitPhotoUiActivity(final String activity, final boolean active) {
            if ("map-visible".equals(activity)) visitPhotoTransferPolicy.setMapVisible(active);
            else if ("map-quiet".equals(activity)) visitPhotoTransferPolicy.setMapQuiet(active);
            else if ("stamp-quiet".equals(activity)) visitPhotoTransferPolicy.setStampQuiet(active);
            else return;
            if (active || "map-visible".equals(activity)) {
                runOnUiThread(new Runnable(){@Override public void run(){
                    resumeAuthorizedVisitPhotoTransfers();
                }});
            }
        }
        @JavascriptInterface
        public void shareApp(final String text) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    shareAppLink(text);
                }
            });
        }

        @JavascriptInterface
        public void transferVisitData(final String mode,final String payload){
            if(!"save".equals(mode)&&!"load".equals(mode))return;
            runOnUiThread(new Runnable(){@Override public void run(){
                requestGoogleDriveAuthorization("data-transfer-"+mode,"save".equals(mode)?payload:"");
            }});
        }

        @JavascriptInterface
        public void checkPhotoNetworkChoice(){
            runOnUiThread(new Runnable(){@Override public void run(){requestGoogleDriveAuthorization("photo-choice-read","");}});
        }
        @JavascriptInterface
        public void savePhotoNetworkChoice(final String network){
            if(!"wifi".equals(network)&&!"any".equals(network))return;
            runOnUiThread(new Runnable(){@Override public void run(){requestGoogleDriveAuthorization("photo-choice-save",network);}});
        }

        @JavascriptInterface
        public void connectGoogleDrive() { runOnUiThread(new Runnable(){@Override public void run(){ requestGoogleDriveAuthorization("connect", ""); }}); }



        // The web journal is authoritative for place/date association. Link only after
        // the journal has persisted and read back the new photo ID successfully.
        // No file deletion, upload or journal mutation is allowed from this bridge.
        // Called by the existing Drive settings, not a new first-run dialog.
        // requestAuthorization is true only for a deliberate user action.
        @JavascriptInterface
        public void setVisitPhotoCloudOptions(final boolean enabled, final boolean wifiOnly,
                                              final boolean requestAuthorization) {
            visitPhotoAutoEnabled=enabled;
            visitPhotoWifiOnly=wifiOnly;
            visitPhotoTransferPolicy.setCloudEnabled(enabled);
            if(!enabled){
                runOnUiThread(new Runnable(){@Override public void run(){
                    visitPhotoRetryHandler.removeCallbacks(visitPhotoRetryTick);
                    visitPhotoRetryArmed=false;
                }});
                return;
            }
            runOnUiThread(new Runnable(){@Override public void run(){
                if(requestAuthorization)requestGoogleDriveAuthorization("visit-photo-auto", "");
                else resumeAuthorizedVisitPhotoTransfers();
            }});
        }

        @JavascriptInterface
        public void linkVisitPhotoMetadata(final String photoId, final String placeCode, final String visitDate) {
            if (photoId == null || !photoId.matches("local_[A-Za-z0-9_-]{8,200}")
                    || placeCode == null || placeCode.length() == 0 || placeCode.length() >= 256
                    || visitDate == null || !visitDate.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) return;
            driveOperationExecutor.execute(new Runnable() { @Override public void run() {
                if (isLocalPhotoDeleted(photoId)) return;
                File full = pendingVisitPhotoFile(photoId, false);
                File thumb = pendingVisitPhotoFile(photoId, true);
                if (!full.isFile() || full.length() <= 0 || !thumb.isFile() || thumb.length() <= 0) return;
                try (VisitPhotoTransferIndex index = new VisitPhotoTransferIndex(getApplicationContext())) {
                    index.setPhotoMetadata(photoId, placeCode, visitDate);
                    // Once the journal link is confirmed, obtain an existing consented
                    // token silently if needed; otherwise the pending photo never starts.
                    runOnUiThread(new Runnable(){@Override public void run(){
                        resumeAuthorizedVisitPhotoTransfers();
                    }});
                } catch (Exception ignored) { /* Only a rebuildable auxiliary index. */ }
            } });
        }

        // Acknowledgement is emitted only after the SQLite batch has committed.
        // The legacy entry point remains for production WebView clients.
        @JavascriptInterface
        public void enqueueVisitPhotoRestoreManifest(final String json) {
            enqueueVisitPhotoRestoreManifestAck(json, "");
        }

        @JavascriptInterface
        public void enqueueVisitPhotoRestoreManifestAck(final String json, final String requestId) {
            if(json==null || json.length()>128*1024) {
                acknowledgePhotoManifest(requestId, false);
                return;
            }
            photoManifestExecutor.execute(new Runnable(){@Override public void run(){
                boolean accepted=false;
                try{
                    JSONArray entries=new JSONArray(json);
                    if(entries.length()>0 && entries.length()<=100){
                        try(VisitPhotoTransferIndex index=new VisitPhotoTransferIndex(getApplicationContext())){
                            android.database.sqlite.SQLiteDatabase db=index.getWritableDatabase();
                            db.beginTransaction();
                            try{
                                for(int i=0;i<entries.length();i++){
                                    JSONObject p=entries.optJSONObject(i);
                                    if(p==null)throw new IllegalArgumentException("Invalid restore entry");
                                    String id=p.optString("photoId","");
                                    String place=p.optString("placeCode","");
                                    String date=p.optString("visitDate","");
                                    if(!id.matches("[A-Za-z0-9_-]{8,200}") || place.isEmpty()
                                        || place.length()>=256 || !date.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}"))
                                        throw new IllegalArgumentException("Invalid restore metadata");
                                    index.enqueueRestore(id,p.optString("driveFileId",""),place,date);
                                    if(!quickVisitPhotoFilesPresent(id))index.requireRestoreIfMissing(id);
                                }
                                db.setTransactionSuccessful();
                                accepted=true;
                            }finally{db.endTransaction();}
                        }
                    }
                    if(accepted){
                        visitPhotoTransferPolicy.setBulkRestorePending(true);
                        runAuthorizedVisitPhotoRestores();
                        runOnUiThread(new Runnable(){@Override public void run(){scheduleVisitPhotoRetryTick();}});
                    }
                }catch(Exception e){
                    Log.e("VisitPhotoRestore", "Failed to register restore inventory batch", e);
                    retryPendingVisitPhotoSyncIfAuthorized(61000L);
                }
                acknowledgePhotoManifest(requestId, accepted);
            }});
        }

        private void acknowledgePhotoManifest(final String requestId, final boolean success){
            if(requestId==null || !requestId.matches("[A-Za-z0-9_-]{1,80}"))return;
            runOnUiThread(new Runnable(){@Override public void run(){
                if(webView!=null)webView.evaluateJavascript(
                    "window.oaiPhotoManifestAck&&window.oaiPhotoManifestAck("
                      +org.json.JSONObject.quote(requestId)+","+(success?"true":"false")+")",null);
            }});
        }

        @JavascriptInterface
        public void pickVisitPhotos(final String requestId, final int maxCount) { runOnUiThread(new Runnable(){@Override public void run(){ startVisitPhotoPicker(requestId,maxCount,"[]"); }}); }
        @JavascriptInterface public void pickVisitPhotosWithNames(final String requestId, final int maxCount, final String existingNamesJson) { runOnUiThread(new Runnable(){@Override public void run(){ startVisitPhotoPicker(requestId,maxCount,existingNamesJson); }}); }

        @JavascriptInterface
        public void loadVisitPhoto(final String requestId, final String fileId) { loadVisitPhotoFast(requestId,fileId,false); }

        @JavascriptInterface
        public void loadVisitPhotoThumb(final String requestId, final String fileId) { loadVisitPhotoFast(requestId,fileId,true); }

        @JavascriptInterface
        public void loadVisitPhotoV2(final String requestId, final String photoId, final String driveFileId, final boolean thumb) { loadVisitPhotoFastV2(requestId,photoId,driveFileId,thumb); }

        @JavascriptInterface
        public void deleteVisitPhoto(final String requestId, final String fileId) {
            if(fileId!=null&&fileId.startsWith("local_")){new Thread(new Runnable(){@Override public void run(){deletePendingVisitPhoto(fileId);notifyVisitPhotoDeleteResult(requestId,true,"");}}).start();return;}
            notifyVisitPhotoDeleteResult(requestId,true,"");
        }

        @JavascriptInterface
        public void deleteVisitPhotoV2(final String requestId, final String photoId, final String driveFileId) {
            new Thread(new Runnable(){@Override public void run(){
                String pid=photoId==null?"":photoId;String did=driveFileId==null?"":driveFileId;
                if(did.length()==0)did=visitPhotoRemoteId(pid);if(did.length()==0&&!pid.startsWith("local_"))did=pid;
                markLocalPhotoDeleted(pid);deletePendingVisitPhoto(pid);setVisitPhotoRemoteId(pid,"");
                // Remove only the user-selected local reference; never delete from Drive.
                notifyVisitPhotoDeleteResult(requestId,true,"");
            }}).start();
        }

        @JavascriptInterface
        public void testBackupFiveVisitPhotos() {
            runOnUiThread(new Runnable(){@Override public void run(){
                if(devPhotoBackupTestRunning.get()) {notifyDevPhotoBackupTest("error","시험이 이미 진행 중입니다.");return;}
                notifyDevPhotoBackupTest("working","Google Drive에 아직 백업되지 않은 사진 전체를 시험 백업합니다.");
                requestGoogleDriveAuthorization("dev-photo-backup-test","");
            }});
        }

        @JavascriptInterface
        public void verifyBackupFiveVisitPhotos() {
            runOnUiThread(new Runnable(){@Override public void run(){
                if(devPhotoVerifyRunning.get()){
                    notifyDevPhotoVerify("error","확인 시험이 이미 진행 중입니다.");return;
                }
                devPhotoVerifyRequestedAt=android.os.SystemClock.elapsedRealtime();
                notifyDevPhotoVerify("working","Google 인증과 이전 작업 완료를 기다리고 있습니다.");
                requestGoogleDriveAuthorization("dev-photo-backup-verify","");
            }});
        }

        @JavascriptInterface
        public void diagnoseVisitPhotoDownloads(){ collectVisitPhotoDownloadDiagnostics(); }
        @JavascriptInterface
        public void checkMissingVisitPhotosInDrive(){ checkMissingPhotosInDrive(); }

        @JavascriptInterface
        public void auditRecentVisitPhotoUploads() {
            runOnUiThread(new Runnable(){@Override public void run(){
                if(devPhotoUploadAuditRunning.get()){
                    notifyDevPhotoUploadAudit("error","진단이 이미 진행 중입니다.");return;
                }
                notifyDevPhotoUploadAudit("working","최근 5장 사진의 로컬·Drive 상태를 확인 중입니다.");
                requestGoogleDriveAuthorization("dev-photo-upload-audit","");
            }});
        }

        @JavascriptInterface
        public void syncPendingVisitPhotos() { /* Local-only photo storage: no cloud operation. */ }

        @JavascriptInterface
        public void acknowledgeResumeCycle(final String cycleId) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    acknowledgeNativeResumeCycle(cycleId);
                }
            });
        }

        @JavascriptInterface
        public void requestPendingResumeCycle() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dispatchPendingNativeResumeToWeb(0L, true);
                }
            });
        }
    }

    private void shareAppLink(String text) {
        String fallback = "가톨릭길동무 앱을 추천합니다.\n\n"
                + "전국 성당, 성지, 피정의집, 순례길 정보를 한눈에 볼 수 있는 가톨릭 생활 앱입니다.\n\n"
                + "Google Play에서 설치하기:\n"
                + "https://play.google.com/store/apps/details?id=kr.catholic.gildonmu";
        String body = text != null && text.trim().length() > 0 ? text : fallback;
        try {
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "가톨릭길동무");
            sendIntent.putExtra(Intent.EXTRA_TEXT, body);
            Intent chooser = Intent.createChooser(sendIntent, "가톨릭길동무 공유하기");
            startActivity(chooser);
        } catch (Exception ignored) {
        }
    }

    private void configureWebView() {
        WebSettings s = webView.getSettings();

        try {
            String defaultUa = s.getUserAgentString();
            if (defaultUa == null) defaultUa = "";
            defaultUa = defaultUa.replaceAll("\\s*GildongmuAndroid/\\d+", "");
            defaultUa = defaultUa.replaceAll("\\s*GildongmuTablet/\\d+", "");
            String nativeVersion = String.valueOf(BuildConfig.VERSION_CODE);
            String tabletToken = isTabletLayout() ? " GildongmuTablet/" + nativeVersion : "";
            s.setUserAgentString(defaultUa + " GildongmuAndroid/" + nativeVersion + tabletToken);
        } catch (Exception ignored) {
        }
        // Keep WebView text enlargement at the fixed standard value.
        // Cover menu/refresh text sizes are controlled by the web CSS and must remain fixed.
        s.setTextZoom(100);
        s.setJavaScriptEnabled(true);
        try {
            webView.addJavascriptInterface(new GildongmuNativeBridge(), "GildongmuNative");
        } catch (Exception ignored) {
        }
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setGeolocationEnabled(true);
        s.setLoadsImagesAutomatically(true);
        s.setJavaScriptCanOpenWindowsAutomatically(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setAllowFileAccess(false);
        s.setAllowContentAccess(false);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            s.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        }
        if ((getApplicationInfo().flags & android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                WebResourceResponse local=interceptVisitPhotoAsset(request==null?null:request.getUrl());
                return local!=null?local:super.shouldInterceptRequest(view,request);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                WebResourceResponse local=interceptVisitPhotoAsset(parseUriOrNull(url));
                return local!=null?local:super.shouldInterceptRequest(view,url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return handleTopLevelNavigation(request.getUrl());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return handleTopLevelNavigation(parseUriOrNull(url));
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (guardExternalPageStart(view, url)) return;
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                dispatchNativeDeviceClassToWeb(0L);
                if (pendingLaunchStorageReset) return;
                if (launchStorageResetReloading && isMainAppPage(url)) {
                    // Show the real PWA intro on the cleaned load. V245 hid the overlay
                    // until onPageFinished, which made the intro pass by in an instant.
                    hideLaunchOverlay();
                    return;
                }
                hideLaunchOverlay();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                dispatchNativeDeviceClassToWeb(0L);
                if (pendingLaunchStorageReset && isMainAppPage(url)) {
                    pendingLaunchStorageReset = false;
                    launchStorageResetReloading = true;
                    clearLaunchNavigationStateAndReload();
                    return;
                }
                if (launchStorageResetReloading && isMainAppPage(url)) {
                    launchStorageResetReloading = false;
                    try {
                        if (view != null) view.clearHistory();
                    } catch (Exception ignored) {
                    }
                    hideLaunchOverlay();
                    
                    return;
                }
                hideLaunchOverlay();
                if (isMainAppPage(url)) {
                    dispatchPendingNativeResumeToWeb(120L, true);
                    dispatchAnniversaryNotificationOpen();
                    // Existing photo ID mapping is read-only; no Drive background work.
                    webView.postDelayed(new Runnable(){@Override public void run(){notifyKnownVisitPhotoMappings();}},500L);
                }
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                if (hasLocationPermission()) {
                    callback.invoke(origin, true, true);
                    return;
                }
                // The nearby screen's own permission gate owns the retry flow.
                // Do not open a second system prompt behind the app explanation dialog.
                callback.invoke(origin, false, false);
            }
        });
    }










    private Uri parseUriOrNull(String url) {
        try {
            if (url == null || url.trim().length() == 0) return null;
            return Uri.parse(url);
        } catch (Exception ignored) {
            return null;
        }
    }

    private String getScheme(Uri uri) {
        try {
            return uri != null && uri.getScheme() != null ? uri.getScheme().toLowerCase() : "";
        } catch (Exception ignored) {
            return "";
        }
    }

    private String getHost(Uri uri) {
        try {
            return uri != null && uri.getHost() != null ? uri.getHost().toLowerCase() : "";
        } catch (Exception ignored) {
            return "";
        }
    }

    private boolean isInternalAppUri(Uri uri) {
        String scheme = getScheme(uri);
        String host = getHost(uri);
        return ("https".equals(scheme) || "http".equals(scheme)) && MAIN_HOST.equals(host);
    }

    private boolean isExternalAppUri(Uri uri) {
        if (uri == null) return false;
        if (isInternalAppUri(uri)) return false;
        String scheme = getScheme(uri);
        return "http".equals(scheme)
                || "https".equals(scheme)
                || "tel".equals(scheme)
                || "mailto".equals(scheme)
                || "sms".equals(scheme)
                || "geo".equals(scheme);
    }

    private boolean handleTopLevelNavigation(Uri uri) {
        if (!isExternalAppUri(uri)) return false;
        openExternal(uri);
        return true;
    }

    /**
     * Second safety net for HTTP/HTTPS external pages.
     * Some JavaScript/iframe initiated moves can reach WebView before shouldOverrideUrlLoading
     * finishes handling them.  Because cleartext traffic intentionally stays blocked inside
     * the app, any external http:// URL that starts loading here must be stopped immediately
     * and sent to the external browser.
     */
    private boolean guardExternalPageStart(WebView view, String url) {
        Uri uri = parseUriOrNull(url);
        if (!isExternalAppUri(uri)) return false;
        try {
            if (view != null) view.stopLoading();
        } catch (Exception ignored) {
        }
        openExternal(uri);
        return true;
    }

    private String lastExternalUrl;
    private long lastExternalOpenedAt;

    private void openExternal(Uri uri) {
        if (uri == null) return;
        try {
            String target = uri.toString();
            long now = System.currentTimeMillis();
            if (target.equals(lastExternalUrl) && now - lastExternalOpenedAt < 1500L) {
                return;
            }
            lastExternalUrl = target;
            lastExternalOpenedAt = now;
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } catch (Exception ignored) {
        }
    }

    private boolean hasLocationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQ_WALK_NOTIFICATION){
            // Walking foreground service remains untouched. A pending automobile
            // notification is shown only after the user grants notification access.
            if(pendingCarStartedAt>0L && grantResults!=null && grantResults.length>0
                    && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                long started=pendingCarStartedAt;
                pendingCarStartedAt=0L;
                showCarPilgrimageNotice(started,pendingCarVisited,pendingCarTotal,pendingCarNextName);
            }else pendingCarStartedAt=0L;
            return;
        }
        if(requestCode==REQ_ANNIVERSARY_NOTIFICATION){
            boolean pending=anniversaryNotificationPermissionPending;
            anniversaryNotificationPermissionPending=false;
            if(pending && grantResults!=null && grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                sendAnniversaryTestNotification();
            }
            return;
        }
        if (requestCode == REQ_LOCATION) {
            boolean granted = false;
            if (grantResults != null) {
                for (int result : grantResults) {
                    if (result == PackageManager.PERMISSION_GRANTED) {
                        granted = true;
                        break;
                    }
                }
            }
            if (pendingGeoCallback != null && pendingGeoOrigin != null) {
                pendingGeoCallback.invoke(pendingGeoOrigin, granted, granted);
            }
            pendingGeoCallback = null;
            pendingGeoOrigin = null;
            nearbyPermissionRequestedThisLaunch = false;
            if (granted) {
                retryVisibleLocationRequest();
            }
        }
    }

    private void retryVisibleLocationRequest() {
        if (webView == null) return;
        webView.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    webView.evaluateJavascript(
                            "(function(){try{var sheet=document.getElementById('sheet-nearby');" +
                                    "if(sheet&&sheet.classList&&sheet.classList.contains('open')&&typeof window._loadNearby==='function'){window._loadNearby();}" +
                                    "else if(typeof window.oaiRefreshCurrentLocation==='function'){window.oaiRefreshCurrentLocation({reason:'android-location-permission',cycleId:'permission-'+Date.now(),preserveMapCenter:true});}" +
                                    "}catch(e){}})();",
                            null
                    );
                } catch (Exception ignored) {
                }
            }
        }, 350L);
    }

    /**
     * Android 16 no longer dispatches onBackPressed() to apps targeting API 36.
     * Register the platform callback on Android 13+ and keep one owner for the
     * existing WebView-history/exit behavior across all supported Android versions.
     */
    private void registerSystemBackCallback() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || onBackInvokedCallback != null) {
            return;
        }
        onBackInvokedCallback = new OnBackInvokedCallback() {
            @Override
            public void onBackInvoked() {
                handleSystemBack();
            }
        };
        getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                onBackInvokedCallback
        );
    }

    private void unregisterSystemBackCallback() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || onBackInvokedCallback == null) {
            return;
        }
        try {
            getOnBackInvokedDispatcher().unregisterOnBackInvokedCallback(onBackInvokedCallback);
        } catch (Exception ignored) {
        }
        onBackInvokedCallback = null;
    }

    private void handleSystemBack() {
        // Let the independent first-run dialog consume Back before navigating history.
        // The JavaScript callback is read-only unless that dialog is actually open.
        if (webView != null) {
            webView.evaluateJavascript("(function(){return !!(window.oaiDismissFirstRunForBack&&window.oaiDismissFirstRunForBack());})()", new android.webkit.ValueCallback<String>() {
                @Override public void onReceiveValue(String value) {
                    if ("true".equals(value)) return;
                    if (webView != null && webView.canGoBack()) { webView.goBack(); return; }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) finishAndRemoveTask();
                    else finish();
                }
            });
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) finishAndRemoveTask();
        else finish();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBackPressed() {
        handleSystemBack();
    }

    @Override
    protected void onDestroy() {
        unregisterSystemBackCallback();
        // Do not interrupt a Drive upload half-way through a file.
        visitPhotoRetryHandler.removeCallbacks(visitPhotoRetryTick);
        visitPhotoRetryHandler.removeCallbacks(visitPhotoDeferredRetry);
        synchronized(this){visitPhotoNextRetryAtElapsed=-1L;}
        visitPhotoRetryArmed=false;
        visitPhotoReadExecutor.shutdown();
        photoRestoreExecutor.shutdown();
        photoManifestExecutor.shutdown();
        if (gpsStampTts != null) {
            gpsStampTts.shutdown();
            gpsStampTts = null;
            gpsStampTtsReady = false;
        }
        if (webView != null) {
            webView.stopLoading();
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
