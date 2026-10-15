package kr.catholic.gildonmu;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
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
    private static final int REQ_GOOGLE_DRIVE_AUTH = 7011;
    private static final int REQ_VISIT_PHOTO_PICKER = 7012;
    private static final String GOOGLE_DRIVE_APPDATA_SCOPE = "https://www.googleapis.com/auth/drive.appdata";
    private static final int SYSTEM_BAR_NAVY = Color.rgb(14, 21, 53);
    private static final int SPLASH_IVORY = Color.rgb(245, 240, 232);

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
    private String pendingDriveAction = "";
    private String pendingDrivePayload = "";
    private final ArrayList<String[]> pendingDriveQueue = new ArrayList<>();
    private boolean driveAuthorizationInFlight = false;
    private String cachedDriveAccessToken = "";
    private long cachedDriveAccessTokenAt = 0L;
    private static final long DRIVE_TOKEN_CACHE_MS = 45L * 60L * 1000L;
    private String pendingVisitPhotoRequestId = "";
    private int pendingVisitPhotoMaxCount = 0;
    private final ArrayList<Uri> pendingVisitPhotoUris = new ArrayList<>();
    private boolean pendingVisitPhotoTrimmed = false;
    private volatile boolean visitPhotoSyncRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        if (!hasLocationPermission()) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, REQ_LOCATION);
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
            int desired = decision.requestedOrientation;
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
    }

    @Override
    protected void onPause() {
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
        applyOrientationPolicy("resume");
        configureSystemBars();
        if (rootLayout != null) {
            rootLayout.requestApplyInsets();
        }
        dispatchNativeDeviceClassToWeb(20L);
        dispatchPendingNativeResumeToWeb(80L, false);
        // Cloud photo synchronization is permanently disabled; retain local photos.
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
        if (!"visit-photo-load".equals(action) && !"data-transfer-save".equals(action) && !"data-transfer-load".equals(action) && !"dev-photo-backup-test".equals(action) && !"dev-photo-backup-verify".equals(action)) return;
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
        // Do not persist Drive tokens or re-enable the old background worker.
        ArrayList<String[]> queued = new ArrayList<>(pendingDriveQueue);
        pendingDriveQueue.clear();
        for (String[] item : queued) if (item != null && item.length >= 2) executeGoogleDriveAction(item[0], item[1], token);
    }

    // All Drive reads and writes are processed in submission order.
    private final ExecutorService driveOperationExecutor = Executors.newSingleThreadExecutor();

    private boolean isVisitPhotoCloudDisabled(){ return true; }

    private void executeGoogleDriveAction(final String action, final String payload, final String token) {
        // v319 experiment: suspend all visit-photo Drive operations; local GPS/visits remain intact.
        if (isVisitPhotoCloudDisabled() && !action.startsWith("data-transfer-") && !"dev-photo-backup-test".equals(action) && !"dev-photo-backup-verify".equals(action)) return;
        if ("connect".equals(action)) { notifyDriveStatus("connected", "사진 저장소가 연결되었습니다."); scheduleVisitPhotoSyncWork(); return; }
        driveOperationExecutor.execute(new Runnable() {
            @Override public void run() {
                try {
                    if ("data-transfer-save".equals(action)) { saveTransferData(token,payload); }
                    else if ("data-transfer-load".equals(action)) { loadTransferData(token); }
                    else if ("visit-photo-upload".equals(action)) { uploadVisitPhotosToDrive(token, payload); }
                    else if ("dev-photo-backup-test".equals(action)) { performDevPhotoBackupTest(token); }
                    else if ("dev-photo-backup-verify".equals(action)) { performDevPhotoBackupVerify(token); }
                    else if ("visit-photo-sync-all".equals(action)) { scheduleVisitPhotoSyncWork(); }
                    else if ("visit-photo-load".equals(action)) { loadVisitPhotoFromDrive(token, payload); }
                    else if ("visit-photo-delete".equals(action)) { deleteVisitPhotoFromDrive(token, payload); }
                } catch (Exception e) {
                    if (String.valueOf(e.getMessage()).contains("Drive 401")) { cachedDriveAccessToken = ""; cachedDriveAccessTokenAt = 0L; visitPhotoPrefs().edit().remove(PREF_WORKER_DRIVE_TOKEN).remove(PREF_WORKER_DRIVE_TOKEN_AT).apply(); }
                    if (action.startsWith("data-transfer-")) notifyDataTransferResult(action.endsWith("save")?"save":"load",false,"","Google Drive 데이터 작업을 완료하지 못했습니다.");
                    else notifyDriveActionFailure(action, payload);
                }
            }
        });
    }

    private void notifyDriveActionFailure(String action, String payload) {
        if ("dev-photo-backup-test".equals(action)) { notifyDevPhotoBackupTest("error", "Google Drive 연결 또는 전송에 실패했습니다."); return; }
        if ("dev-photo-backup-verify".equals(action)) { notifyDevPhotoVerify("error", "Google Drive 연결 또는 다운로드에 실패했습니다."); return; }
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
        for(String[] item:queued){if(item==null||item.length<2)continue;String action=item[0],payload=item[1];if("dev-photo-backup-test".equals(action)){notifyDevPhotoBackupTest("error",message);continue;}if("dev-photo-backup-verify".equals(action)){notifyDevPhotoVerify("error",message);continue;}if("visit-photo-sync-all".equals(action))continue;if(action.startsWith("data-transfer-")){notifyDataTransferResult(action.endsWith("save")?"save":"load",false,"",message);continue;}if(action.startsWith("visit-photo-")){try{JSONObject p=new JSONObject(payload);String req=p.optString("requestId","");if("visit-photo-load".equals(action))notifyVisitPhotoLoaded(req,false,"","",message);else if("visit-photo-delete".equals(action))notifyVisitPhotoDeleteResult(req,false,message);else notifyVisitPhotoUploadResult(req,false,"[]",message);}catch(Exception ignored){}}else notifyDriveStatus("error",message);}
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_VISIT_PHOTO_PICKER) { handleVisitPhotoPickerResult(resultCode, data); return; }
        if (requestCode != REQ_GOOGLE_DRIVE_AUTH) return;
        if (resultCode != RESULT_OK || data == null) { driveAuthorizationInFlight=false;failPendingDriveQueue("Google Drive 연결이 취소되었습니다.");return; }
        try { continueGoogleDriveAction(Identity.getAuthorizationClient(this).getAuthorizationResultFromIntent(data)); }
        catch (ApiException e) { driveAuthorizationInFlight=false;failPendingDriveQueue("Google Drive 연결이 취소되었습니다."); }
    }

    private void startVisitPhotoPicker(final String requestId, final int maxCount) {
        pendingVisitPhotoRequestId=requestId==null?"":requestId;
        pendingVisitPhotoMaxCount=Math.max(1,Math.min(15,maxCount));
        try {
            // 사용자는 휴대폰 갤러리에서 사진을 고르고, 저장만 Google Drive에 합니다.
            // ACTION_PICK + Images 컬렉션을 사용해 Drive/문서 선택기보다 갤러리 중심 UI를 엽니다.
            Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
            startActivityForResult(intent,REQ_VISIT_PHOTO_PICKER);
        } catch (Exception e) {
            notifyVisitPhotoUploadResult(pendingVisitPhotoRequestId,false,"[]","사진 선택 화면을 열지 못했습니다.");
            pendingVisitPhotoRequestId="";pendingVisitPhotoMaxCount=0;
        }
    }

    private void handleVisitPhotoPickerResult(int resultCode, Intent data) {
        final String requestId=pendingVisitPhotoRequestId;
        final int maxCount=pendingVisitPhotoMaxCount;
        pendingVisitPhotoRequestId="";pendingVisitPhotoMaxCount=0;
        if(requestId.length()==0)return;
        if(resultCode!=RESULT_OK||data==null){notifyVisitPhotoLocalComplete(requestId,true,0,0,"");return;}
        final ArrayList<Uri> uris=new ArrayList<>();
        final boolean[] trimmed=new boolean[]{false};
        try{
            ClipData clip=data.getClipData();
            if(clip!=null){trimmed[0]=clip.getItemCount()>maxCount;for(int i=0;i<clip.getItemCount()&&uris.size()<maxCount;i++){Uri u=clip.getItemAt(i).getUri();if(u!=null)uris.add(u);}}
            else if(data.getData()!=null)uris.add(data.getData());
        }catch(Exception ignored){}
        if(uris.isEmpty()){notifyVisitPhotoLocalComplete(requestId,false,0,0,"선택한 사진을 확인하지 못했습니다.");return;}
        new Thread(new Runnable(){@Override public void run(){prepareVisitPhotosLocalFirst(requestId,uris,trimmed[0],maxCount);}}).start();
    }

    private static class PreparedVisitPhoto { final byte[] full; final byte[] thumb; PreparedVisitPhoto(byte[] f,byte[] t){full=f;thumb=t;} }

    private void prepareVisitPhotosLocalFirst(String requestId,ArrayList<Uri> uris,boolean trimmed,int maxCount){
        int ready=0,total=uris==null?0:uris.size();
        notifyVisitPhotoProgress(requestId,0,total);
        if(uris!=null){for(Uri uri:uris){
            try{
                PreparedVisitPhoto prepared=prepareVisitPhotoFast(uri);if(prepared==null||prepared.full==null||prepared.full.length==0)continue;
                String localId="local_"+UUID.randomUUID().toString().replace("-","");
                writePendingVisitPhoto(localId,prepared.full,prepared.thumb);
                JSONObject item=new JSONObject();item.put("photoId",localId);item.put("fileId",localId);item.put("driveFileId","");item.put("name","방문사진.jpg");item.put("mimeType","image/jpeg");item.put("savedAt",String.valueOf(System.currentTimeMillis()));item.put("syncStatus","pending");
                ready++;notifyVisitPhotoLocalReady(requestId,item.toString(),ready,total);
            }catch(Exception ignored){}
        }}
        String msg=trimmed?("선택 가능한 사진 수는 "+maxCount+"장입니다. 앞에서부터 "+maxCount+"장만 등록했습니다."):"";
        notifyVisitPhotoLocalComplete(requestId,ready>0,ready,total,msg);
        if(ready>0){runOnUiThread(new Runnable(){@Override public void run(){try{requestGoogleDriveAuthorization("visit-photo-sync-all","");}catch(Exception ignored){}}});}
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
    private void performDevPhotoBackupTest(String token){
        if(!devPhotoBackupTestRunning.compareAndSet(false,true)){
            notifyDevPhotoBackupTest("error","이미 사진 백업 시험이 진행 중입니다.");return;
        }
        int uploaded=0,existing=0,failures=0,checked=0;
        try{
            ArrayList<String> ids=pendingVisitPhotoIds();
            java.util.Collections.sort(ids);
            for(String photoId:ids){
                if(checked>=5)break;
                if(isLocalPhotoDeleted(photoId)||visitPhotoRemoteId(photoId).length()>0)continue;
                byte[] bytes=readPendingVisitPhoto(photoId,false);
                if(bytes==null||bytes.length==0)continue;
                checked++;
                try{
                    // Idempotence after interrupted uploads: check the cloud name BEFORE sending.
                    String name="cgm-visit-"+photoId.replace("local_","")+".jpg";
                    String remoteId=findExistingDevPhotoFile(token,name);
                    boolean alreadyOnDrive=remoteId.length()>0;
                    if(!alreadyOnDrive)remoteId=createVisitPhotoFile(token,name,bytes);
                    if(remoteId==null||remoteId.length()==0)throw new Exception("Missing Drive file ID");
                    if(isLocalPhotoDeleted(photoId)){
                        // The user deleted the picture during upload. Never record it as active.
                        queueVisitPhotoDeleteId(remoteId);failures++;continue;
                    }
                    setVisitPhotoRemoteId(photoId,remoteId);
                    notifyVisitPhotoSyncResult(photoId,true,remoteId,"image/jpeg",String.valueOf(System.currentTimeMillis()),"");
                    if(alreadyOnDrive)existing++;else uploaded++;
                }catch(Exception e){
                    failures++;
                    if(String.valueOf(e.getMessage()).contains("Drive 401"))throw e;
                }
            }
            String message="시험 완료: 신규 "+uploaded+"장, 기존 백업 발견 "+existing+"장, 실패 "+failures+"장. 휴대폰 사진은 삭제하지 않았습니다.";
            if(checked==0)message="백업할 새 사진이 없습니다. 이미 백업된 사진과 삭제된 사진은 제외했습니다.";
            notifyDevPhotoBackupTest(failures==0?"done":"partial",message);
        }catch(Exception e){notifyDevPhotoBackupTest("error","시험 중 오류가 발생했습니다. 로컬 사진은 유지됩니다.");}
        finally{devPhotoBackupTestRunning.set(false);}
    }

    // v323: Read-only verification of at most five previously backed-up local photos.
    // Never modify the local files, Drive files, metadata mapping, or visit records.
    private final java.util.concurrent.atomic.AtomicBoolean devPhotoVerifyRunning = new java.util.concurrent.atomic.AtomicBoolean(false);
    private void notifyDevPhotoVerify(final String state,final String message){
        runOnUiThread(new Runnable(){@Override public void run(){
            if(webView==null)return;
            webView.evaluateJavascript("(function(){if(window.oaiDevPhotoVerifyResult)window.oaiDevPhotoVerifyResult("
                +JSONObject.quote(state)+","+JSONObject.quote(message)+");})()",null);
        }});
    }
    private void performDevPhotoBackupVerify(String token){
        if(!devPhotoVerifyRunning.compareAndSet(false,true)){
            notifyDevPhotoVerify("error","이미 확인 시험이 진행 중입니다.");return;
        }
        int checked=0,matched=0,mismatched=0,missing=0;
        try{
            ArrayList<String> ids=pendingVisitPhotoIds();
            java.util.Collections.sort(ids);
            for(String photoId:ids){
                if(checked>=5)break;
                if(isLocalPhotoDeleted(photoId))continue;
                String remoteId=visitPhotoRemoteId(photoId);
                if(remoteId.length()==0)continue;
                byte[] local=readPendingVisitPhoto(photoId,false);
                if(local==null||local.length==0)continue;
                checked++;
                try{
                    byte[] remote=requestDriveBytes(token,"GET","https://www.googleapis.com/drive/v3/files/"
                        +Uri.encode(remoteId)+"?alt=media");
                    if(remote==null||remote.length==0){missing++;continue;}
                    BitmapFactory.Options bounds=new BitmapFactory.Options();bounds.inJustDecodeBounds=true;
                    BitmapFactory.decodeByteArray(remote,0,remote.length,bounds);
                    if(bounds.outWidth<=0||bounds.outHeight<=0){mismatched++;continue;}
                    java.security.MessageDigest digest=java.security.MessageDigest.getInstance("SHA-256");
                    byte[] first=digest.digest(local);byte[] second=digest.digest(remote);
                    if(local.length==remote.length&&java.util.Arrays.equals(first,second))matched++;
                    else mismatched++;
                }catch(Exception ex){missing++;}
            }
            String message=checked==0
                ?"검증 가능한 로컬 원본과 Google 백업 연결 사진이 없습니다. 기존 사진은 그대로입니다."
                :"읽기 전용 확인: "+checked+"장 중 원본 일치 "+matched+"장, 내용 불일치 "+mismatched
                  +"장, 다운로드 실패 "+missing+"장. 사진·백업·방문기록은 변경하지 않았습니다.";
            notifyDevPhotoVerify(checked>0&&mismatched==0&&missing==0?"done":"partial",message);
        }catch(Exception ex){notifyDevPhotoVerify("error","Google 백업 확인 중 오류가 발생했습니다. 사진과 기록은 변경하지 않았습니다.");}
        finally{devPhotoVerifyRunning.set(false);}
    }

    private File pendingVisitPhotoDir(){File dir=new File(getFilesDir(),"visit_photos_pending");if(!dir.exists())dir.mkdirs();return dir;}
    private File pendingVisitPhotoFile(String id,boolean thumb){String safe=(id==null?"":id).replaceAll("[^A-Za-z0-9_-]","");return new File(pendingVisitPhotoDir(),(thumb?"t_":"f_")+safe+".jpg");}
    private void writePendingVisitPhoto(String id,byte[] full,byte[] thumb) throws Exception {FileOutputStream a=new FileOutputStream(pendingVisitPhotoFile(id,false));a.write(full);a.close();FileOutputStream b=new FileOutputStream(pendingVisitPhotoFile(id,true));b.write(thumb);b.close();}
    private byte[] readPendingVisitPhoto(String id,boolean thumb){try{File f=pendingVisitPhotoFile(id,thumb);if(!f.exists()||f.length()<=0)return null;InputStream in=new java.io.FileInputStream(f);ByteArrayOutputStream out=new ByteArrayOutputStream((int)Math.min(f.length(),3*1024*1024));byte[] b=new byte[8192];int n;while((n=in.read(b))>0)out.write(b,0,n);in.close();return out.toByteArray();}catch(Exception e){return null;}}
    private void deletePendingVisitPhoto(String id){try{pendingVisitPhotoFile(id,false).delete();pendingVisitPhotoFile(id,true).delete();}catch(Exception ignored){}}
    private void deletePendingVisitPhotoFull(String id){try{pendingVisitPhotoFile(id,false).delete();}catch(Exception ignored){}}
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
    private synchronized String visitPhotoRemoteId(String photoId){try{return visitPhotoRemoteMap().optString(photoId,"");}catch(Exception e){return "";}}
    private synchronized void setVisitPhotoRemoteId(String photoId,String remoteId){try{JSONObject m=visitPhotoRemoteMap();if(remoteId==null||remoteId.length()==0)m.remove(photoId);else m.put(photoId,remoteId);visitPhotoPrefs().edit().putString(PREF_VISIT_PHOTO_REMOTE_MAP,m.toString()).apply();}catch(Exception ignored){}}
    private synchronized Set<String> deletedLocalPhotoIds(){try{return new HashSet<>(visitPhotoPrefs().getStringSet(PREF_VISIT_PHOTO_DELETED_LOCAL_IDS,Collections.<String>emptySet()));}catch(Exception e){return new HashSet<>();}}
    private synchronized boolean isLocalPhotoDeleted(String photoId){return photoId!=null&&deletedLocalPhotoIds().contains(photoId);}
    private synchronized void markLocalPhotoDeleted(String photoId){if(photoId==null||photoId.length()==0)return;Set<String> s=deletedLocalPhotoIds();s.add(photoId);visitPhotoPrefs().edit().putStringSet(PREF_VISIT_PHOTO_DELETED_LOCAL_IDS,new HashSet<>(s)).apply();}
    private synchronized Set<String> pendingVisitPhotoDeleteIds(){try{return new HashSet<>(visitPhotoPrefs().getStringSet(PREF_VISIT_PHOTO_DELETE_QUEUE,Collections.<String>emptySet()));}catch(Exception e){return new HashSet<>();}}
    private synchronized void queueVisitPhotoDeleteId(String id){if(id==null||id.length()==0||id.startsWith("local_"))return;Set<String> s=pendingVisitPhotoDeleteIds();s.add(id);visitPhotoPrefs().edit().putStringSet(PREF_VISIT_PHOTO_DELETE_QUEUE,new HashSet<>(s)).apply();}
    private synchronized void removeVisitPhotoDeleteId(String id){if(id==null||id.length()==0)return;Set<String> s=pendingVisitPhotoDeleteIds();if(s.remove(id))visitPhotoPrefs().edit().putStringSet(PREF_VISIT_PHOTO_DELETE_QUEUE,new HashSet<>(s)).apply();}
    private void syncPendingVisitPhotoDeletes(String token){if(token==null||token.length()==0)return;for(String id:new ArrayList<>(pendingVisitPhotoDeleteIds())){try{requestDrive(token,"DELETE","https://www.googleapis.com/drive/v3/files/"+id,null,null);removeVisitPhotoDeleteId(id);deleteVisitPhotoCache(id);}catch(Exception e){String m=String.valueOf(e.getMessage());if(m.contains("Drive 404")){removeVisitPhotoDeleteId(id);deleteVisitPhotoCache(id);}else if(m.contains("Drive 401")){cachedDriveAccessToken="";cachedDriveAccessTokenAt=0L;break;}}}}


    private void scheduleVisitPhotoSyncWork(){
        if(isVisitPhotoCloudDisabled())return;
        try{
            // Re-entering the app is not a reason to enqueue a no-op sync job.
            // The worker still checks both queues when it actually runs.
            if(pendingVisitPhotoIds().isEmpty() && pendingVisitPhotoDeleteIds().isEmpty())return;
            Constraints constraints=new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
            OneTimeWorkRequest req=new OneTimeWorkRequest.Builder(VisitPhotoSyncWorker.class)
                    .setConstraints(constraints)
                    .setBackoffCriteria(BackoffPolicy.EXPONENTIAL,30,TimeUnit.SECONDS)
                    .build();
            // KEEP prevents repeated resume/page-load events from appending jobs.
            // WorkManager retries the existing job when necessary.
            WorkManager.getInstance(getApplicationContext()).enqueueUniqueWork(VISIT_PHOTO_SYNC_WORK,ExistingWorkPolicy.KEEP,req);
        }catch(Exception ignored){}
    }

    private void notifyKnownVisitPhotoMappings(){
        // Send restored photo mappings in bounded batches rather than one UI-thread
        // runnable / evaluateJavascript call per photo on every page load.
        try {
            JSONObject map = visitPhotoRemoteMap();
            Set<String> deleted = deletedLocalPhotoIds();
            java.util.Iterator<String> it = map.keys();
            final String savedAt = String.valueOf(System.currentTimeMillis());
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

    private void retryPendingVisitPhotoSyncIfAuthorized(long delayMs){if(isVisitPhotoCloudDisabled())return;if(webView==null)return;webView.postDelayed(new Runnable(){@Override public void run(){long now=System.currentTimeMillis();if(cachedDriveAccessToken!=null&&cachedDriveAccessToken.length()>0&&now-cachedDriveAccessTokenAt<DRIVE_TOKEN_CACHE_MS){scheduleVisitPhotoSyncWork();}}},Math.max(0L,delayMs));}
    private void syncPendingVisitPhotosToDrive(String token){
        if(isVisitPhotoCloudDisabled())return;
        if(token==null||token.length()==0||visitPhotoSyncRunning)return;visitPhotoSyncRunning=true;
        try{
            final ArrayList<String> ids=pendingVisitPhotoIds();
            ExecutorService pool=Executors.newFixedThreadPool(2);
            for(final String photoId:ids){
                String existingRemote=visitPhotoRemoteId(photoId);if(existingRemote.length()>0){notifyVisitPhotoSyncResult(photoId,true,existingRemote,"image/jpeg",String.valueOf(System.currentTimeMillis()),"");continue;}
                pool.submit(new Runnable(){@Override public void run(){
                    try{if(isLocalPhotoDeleted(photoId))return;byte[] full=readPendingVisitPhoto(photoId,false);if(full==null||full.length==0)return;String remoteName="cgm-visit-"+photoId.replace("local_","")+".jpg";String remoteId=createVisitPhotoFile(token,remoteName,full);if(remoteId==null||remoteId.length()==0)return;if(isLocalPhotoDeleted(photoId)){queueVisitPhotoDeleteId(remoteId);try{requestDrive(token,"DELETE","https://www.googleapis.com/drive/v3/files/"+remoteId,null,null);removeVisitPhotoDeleteId(remoteId);}catch(Exception ignored){}return;}setVisitPhotoRemoteId(photoId,remoteId);byte[] thumb=readPendingVisitPhoto(photoId,true);cacheVisitPhotoBytes(remoteId,full,false);if(thumb!=null&&thumb.length>0)cacheVisitPhotoBytes(remoteId,thumb,true);deletePendingVisitPhotoFull(photoId);notifyVisitPhotoSyncResult(photoId,true,remoteId,"image/jpeg",String.valueOf(System.currentTimeMillis()),"");}
                    catch(Exception e){if(String.valueOf(e.getMessage()).contains("Drive 401")){cachedDriveAccessToken="";cachedDriveAccessTokenAt=0L;notifyVisitPhotoSyncResult(photoId,false,"","","","Google Drive 연결이 필요합니다.");}else notifyVisitPhotoSyncResult(photoId,false,"","","","Google Drive 동기화를 나중에 다시 시도합니다.");}
                }});
            }
            pool.shutdown();try{pool.awaitTermination(10,TimeUnit.MINUTES);}catch(InterruptedException e){Thread.currentThread().interrupt();}
            syncPendingVisitPhotoDeletes(token);trimVisitPhotoCache();
        }finally{visitPhotoSyncRunning=false;}
    }

    private void loadVisitPhotoFromDrive(String token,String payloadText) throws Exception {
        JSONObject payload=new JSONObject(payloadText);String req=payload.optString("requestId",""),id=payload.optString("fileId","");boolean thumb=payload.optBoolean("thumb",false);
        if(id.length()==0){notifyVisitPhotoLoaded(req,false,"","","사진 정보를 찾을 수 없습니다.");return;}
        byte[] cached=readVisitPhotoCache(id,thumb);
        if(cached!=null&&cached.length>0){notifyVisitPhotoLoaded(req,true,"image/jpeg",visitPhotoAssetUrl(id,thumb),"");return;}
        byte[] full=readVisitPhotoCache(id,false);
        if(full==null||full.length==0){full=requestDriveBytes(token,"GET","https://www.googleapis.com/drive/v3/files/"+id+"?alt=media");cacheVisitPhotoBytes(id,full,false);}
        byte[] send=thumb?makeVisitPhotoThumbnail(full):full;if(thumb)cacheVisitPhotoBytes(id,send,true);trimVisitPhotoCache();
        notifyVisitPhotoLoaded(req,true,"image/jpeg",visitPhotoAssetUrl(id,thumb),"");
    }

    private void deleteVisitPhotoFromDrive(String token,String payloadText) throws Exception {
        JSONObject payload=new JSONObject(payloadText);String req=payload.optString("requestId",""),id=payload.optString("fileId","");
        if(id.length()==0){notifyVisitPhotoDeleteResult(req,false,"사진 정보를 찾을 수 없습니다.");return;}
        if(id.startsWith("local_")){deletePendingVisitPhoto(id);notifyVisitPhotoDeleteResult(req,true,"");return;}
        queueVisitPhotoDeleteId(id);
        try{requestDrive(token,"DELETE","https://www.googleapis.com/drive/v3/files/"+id,null,null);removeVisitPhotoDeleteId(id);deleteVisitPhotoCache(id);notifyVisitPhotoDeleteResult(req,true,"");}
        catch(Exception e){if(String.valueOf(e.getMessage()).contains("Drive 404")){removeVisitPhotoDeleteId(id);deleteVisitPhotoCache(id);notifyVisitPhotoDeleteResult(req,true,"");return;}throw e;}
    }

    private File visitPhotoCacheDir() { File dir=new File(getCacheDir(),"visit_photos");if(!dir.exists())dir.mkdirs();return dir; }
    private File visitPhotoCacheFile(String id,boolean thumb){return new File(visitPhotoCacheDir(),(thumb?"t_":"f_")+id.replaceAll("[^A-Za-z0-9_-]","") + ".jpg");}
    private void cacheVisitPhotoBytes(String id,byte[] bytes,boolean thumb){if(id==null||id.length()==0||bytes==null||bytes.length==0)return;try{FileOutputStream out=new FileOutputStream(visitPhotoCacheFile(id,thumb));out.write(bytes);out.close();}catch(Exception ignored){}}
    private byte[] readVisitPhotoCache(String id,boolean thumb){try{File f=visitPhotoCacheFile(id,thumb);if(!f.exists()||f.length()<=0)return null;InputStream in=new java.io.FileInputStream(f);ByteArrayOutputStream out=new ByteArrayOutputStream((int)Math.min(f.length(),2*1024*1024));byte[] b=new byte[8192];int n;while((n=in.read(b))>0)out.write(b,0,n);in.close();f.setLastModified(System.currentTimeMillis());return out.toByteArray();}catch(Exception e){return null;}}
    private void deleteVisitPhotoCache(String id){try{visitPhotoCacheFile(id,false).delete();visitPhotoCacheFile(id,true).delete();}catch(Exception ignored){}}
    private byte[] makeVisitPhotoThumbnail(byte[] full){if(full==null||full.length==0)return full;try{Bitmap b=BitmapFactory.decodeByteArray(full,0,full.length);if(b==null)return full;int w=b.getWidth(),h=b.getHeight(),longSide=Math.max(w,h);if(longSide>360){float sc=360f/(float)longSide;Bitmap small=Bitmap.createScaledBitmap(b,Math.max(1,Math.round(w*sc)),Math.max(1,Math.round(h*sc)),true);if(small!=b)b.recycle();b=small;}ByteArrayOutputStream out=new ByteArrayOutputStream();b.compress(Bitmap.CompressFormat.JPEG,72,out);b.recycle();return out.toByteArray();}catch(Exception e){return full;}}
    private void trimVisitPhotoCache(){try{File[] files=visitPhotoCacheDir().listFiles();if(files==null)return;long total=0;for(File f:files)total+=f.length();long max=100L*1024L*1024L;if(total<=max)return;java.util.Arrays.sort(files,new java.util.Comparator<File>(){public int compare(File a,File b){boolean af=a.getName().startsWith("f_"),bf=b.getName().startsWith("f_");if(af!=bf)return af?-1:1;return Long.compare(a.lastModified(),b.lastModified());}});for(File f:files){if(total<=70L*1024L*1024L)break;long len=f.length();if(f.delete())total-=len;}}catch(Exception ignored){}}

    private byte[] requestDriveBytes(String token,String method,String urlString) throws Exception {
        HttpURLConnection c=(HttpURLConnection)new URL(urlString).openConnection();c.setRequestMethod(method);c.setRequestProperty("Authorization","Bearer "+token);c.setConnectTimeout(15000);c.setReadTimeout(30000);int code=c.getResponseCode();InputStream in=code>=200&&code<300?c.getInputStream():c.getErrorStream();ByteArrayOutputStream out=new ByteArrayOutputStream();if(in!=null){byte[] buf=new byte[8192];int n;while((n=in.read(buf))>0)out.write(buf,0,n);in.close();}c.disconnect();if(code<200||code>=300)throw new Exception("Drive "+code);return out.toByteArray();
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
        new Thread(new Runnable(){@Override public void run(){
            if(fileId!=null&&fileId.startsWith("local_")){byte[] local=readPendingVisitPhoto(fileId,thumb);if(local==null||local.length==0)local=readPendingVisitPhoto(fileId,false);if(local!=null&&local.length>0){notifyVisitPhotoLoaded(requestId,true,"image/jpeg",visitPhotoAssetUrl(fileId,thumb),"");return;}notifyVisitPhotoLoaded(requestId,false,"","","로컬 사진을 찾지 못했습니다.");return;}
            byte[] cached=readVisitPhotoCache(fileId,thumb);
            if(cached!=null&&cached.length>0){notifyVisitPhotoLoaded(requestId,true,"image/jpeg",visitPhotoAssetUrl(fileId,thumb),"");return;}
            runOnUiThread(new Runnable(){@Override public void run(){try{JSONObject p=new JSONObject();p.put("requestId",requestId);p.put("fileId",fileId);p.put("thumb",thumb);requestGoogleDriveAuthorization("visit-photo-load",p.toString());}catch(Exception e){notifyVisitPhotoLoaded(requestId,false,"","","사진을 불러오지 못했습니다.");}}});
        }}).start();
    }

    private void loadVisitPhotoFastV2(final String requestId,final String photoId,final String driveFileId,final boolean thumb){
        new Thread(new Runnable(){@Override public void run(){
            String pid=photoId==null?"":photoId;String did=driveFileId==null?"":driveFileId;
            byte[] local=readPendingVisitPhoto(pid,thumb);if(local==null||local.length==0)local=readPendingVisitPhoto(pid,false);
            if(local!=null&&local.length>0){if(thumb&&readPendingVisitPhoto(pid,true)==null)local=makeVisitPhotoThumbnail(local);notifyVisitPhotoLoaded(requestId,true,"image/jpeg",visitPhotoAssetUrl(pid,thumb),"");return;}
            if(did.length()==0)did=visitPhotoRemoteId(pid);if(did.length()==0&&!pid.startsWith("local_"))did=pid;
            if(did.length()==0){notifyVisitPhotoLoaded(requestId,false,"","","사진을 아직 동기화하지 못했습니다.");return;}
            final String remote=did;byte[] cached=readVisitPhotoCache(remote,thumb);if(cached!=null&&cached.length>0){notifyVisitPhotoLoaded(requestId,true,"image/jpeg",visitPhotoAssetUrl(remote,thumb),"");return;}
            runOnUiThread(new Runnable(){@Override public void run(){try{JSONObject p=new JSONObject();p.put("requestId",requestId);p.put("fileId",remote);p.put("thumb",thumb);requestGoogleDriveAuthorization("visit-photo-load",p.toString());}catch(Exception e){notifyVisitPhotoLoaded(requestId,false,"","","사진을 불러오지 못했습니다.");}}});
        }}).start();
    }

    private class GildongmuNativeBridge {
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
        public void connectGoogleDrive() { runOnUiThread(new Runnable(){@Override public void run(){ requestGoogleDriveAuthorization("connect", ""); }}); }



        @JavascriptInterface
        public void pickVisitPhotos(final String requestId, final int maxCount) { runOnUiThread(new Runnable(){@Override public void run(){ startVisitPhotoPicker(requestId,maxCount); }}); }

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
                notifyDevPhotoBackupTest("working","Google Drive에 사진 최대 5장을 시험 백업합니다.");
                requestGoogleDriveAuthorization("dev-photo-backup-test","");
            }});
        }

        @JavascriptInterface
        public void verifyBackupFiveVisitPhotos() {
            runOnUiThread(new Runnable(){@Override public void run(){
                if(devPhotoVerifyRunning.get()){
                    notifyDevPhotoVerify("error","확인 시험이 이미 진행 중입니다.");return;
                }
                notifyDevPhotoVerify("working","Google Drive 백업 사진을 읽기 전용으로 확인하고 있습니다.");
                requestGoogleDriveAuthorization("dev-photo-backup-verify","");
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
                pendingGeoOrigin = origin;
                pendingGeoCallback = callback;
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, REQ_LOCATION);
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
                                    "if(typeof window.oaiRefreshCurrentLocation==='function'){window.oaiRefreshCurrentLocation({reason:'android-location-permission',cycleId:'permission-'+Date.now(),preserveMapCenter:true});}" +
                                    "else if(sheet&&sheet.classList&&sheet.classList.contains('open')&&typeof window._loadNearby==='function'){window._loadNearby();}" +
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
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        } else {
            finish();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBackPressed() {
        handleSystemBack();
    }

    @Override
    protected void onDestroy() {
        unregisterSystemBackCallback();
        if (webView != null) {
            webView.stopLoading();
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
