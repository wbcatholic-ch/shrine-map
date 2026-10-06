package kr.catholic.gildonmu;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.pm.PackageManager;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
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
import android.util.Base64;
import android.util.DisplayMetrics;
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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class MainActivity extends Activity {
    private static final String START_URL = "https://wbcatholic-ch.github.io/shrine-map/";
    private static final String MAIN_HOST = "wbcatholic-ch.github.io";
    private static final int REQ_LOCATION = 7001;
    private static final int REQ_GOOGLE_DRIVE_AUTH = 7011;
    private static final String GOOGLE_DRIVE_APPDATA_SCOPE = "https://www.googleapis.com/auth/drive.appdata";
    private static final String GOOGLE_DRIVE_BACKUP_FILE = "catholic-gildongmu-backup.json";
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
    // Natural portrait displays stay portrait-only. Natural landscape displays
    // follow the user's system auto-rotate preference.
    private int lastAppliedOrientationPolicy = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
    private String pendingDriveAction = "";
    private String pendingDrivePayload = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
     * We judge the display by its NATURAL orientation rather than the Activity's
     * current width/height. That matters on foldables because a portrait-locked
     * Activity can report portrait dimensions even when the newly unfolded
     * physical display is naturally landscape.
     *
     * - Natural portrait (height >= width): portrait-only.
     * - Natural landscape (width > height): follow the user's system rotation setting.
     */
    private void applyOrientationPolicy(String reason) {
        try {
            boolean naturalLandscape = isNaturalDisplayLandscape();
            int desired = naturalLandscape
                    ? ActivityInfo.SCREEN_ORIENTATION_USER
                    : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            if (lastAppliedOrientationPolicy == desired && getRequestedOrientation() == desired) {
                return;
            }
            lastAppliedOrientationPolicy = desired;
            setRequestedOrientation(desired);
        } catch (Exception ignored) {
        }
    }

    /**
     * Resolve physical/natural display orientation from current real display size
     * plus Display rotation. ROTATION_90/270 means Android has swapped the natural
     * width/height for the current presentation, so swap them back before comparing.
     */
    private boolean isNaturalDisplayLandscape() {
        try {
            Display display = getWindowManager().getDefaultDisplay();
            if (display == null) return false;

            DisplayMetrics metrics = new DisplayMetrics();
            display.getRealMetrics(metrics);
            int currentWidth = Math.max(0, metrics.widthPixels);
            int currentHeight = Math.max(0, metrics.heightPixels);
            if (currentWidth <= 0 || currentHeight <= 0) return false;

            int rotation = display.getRotation();
            boolean quarterTurn = rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270;
            int naturalWidth = quarterTurn ? currentHeight : currentWidth;
            int naturalHeight = quarterTurn ? currentWidth : currentHeight;
            return naturalWidth > naturalHeight;
        } catch (Exception ignored) {
            return false;
        }
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
        pendingDriveAction = action == null ? "" : action;
        pendingDrivePayload = payload == null ? "" : payload;
        List<Scope> scopes = new ArrayList<>();
        scopes.add(new Scope(GOOGLE_DRIVE_APPDATA_SCOPE));
        AuthorizationRequest request = AuthorizationRequest.builder().setRequestedScopes(scopes).build();
        Identity.getAuthorizationClient(this).authorize(request)
                .addOnSuccessListener(new OnSuccessListener<AuthorizationResult>() {
                    @Override public void onSuccess(AuthorizationResult result) {
                        if (result.hasResolution()) {
                            try {
                                startIntentSenderForResult(result.getPendingIntent().getIntentSender(), REQ_GOOGLE_DRIVE_AUTH, null, 0, 0, 0);
                            } catch (IntentSender.SendIntentException e) { notifyDriveStatus("error", "Google Drive 연결을 시작하지 못했습니다."); }
                        } else {
                            continueGoogleDriveAction(result);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override public void onFailure(Exception e) { notifyDriveStatus("error", "Google Drive 권한을 확인하지 못했습니다."); }
                });
    }

    private void continueGoogleDriveAction(final AuthorizationResult result) {
        final String action = pendingDriveAction;
        final String payload = pendingDrivePayload;
        pendingDriveAction = "";
        pendingDrivePayload = "";
        final String token = result == null ? null : result.getAccessToken();
        if (token == null || token.length() == 0) { notifyDriveStatus("error", "Google Drive 권한을 확인하지 못했습니다."); return; }
        if ("connect".equals(action)) { notifyDriveStatus("connected", "Google Drive가 연결되었습니다. 기록을 안전하게 보관합니다."); return; }
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    if ("save".equals(action)) { saveBackupToDrive(token, payload); notifyDriveStatus("saved", "Google Drive에 기록을 저장했습니다."); }
                    else if ("load".equals(action)) { loadBackupFromDrive(token); }
                } catch (Exception e) { notifyDriveStatus("error", "load".equals(action) ? "Google Drive 기록을 불러오지 못했습니다. 잠시 후 다시 시도해 주세요." : "Google Drive에 기록을 저장하지 못했습니다. 잠시 후 다시 시도해 주세요."); }
            }
        }).start();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQ_GOOGLE_DRIVE_AUTH) return;
        try { continueGoogleDriveAction(Identity.getAuthorizationClient(this).getAuthorizationResultFromIntent(data)); }
        catch (ApiException e) { pendingDriveAction=""; pendingDrivePayload=""; notifyDriveStatus("error", "Google Drive 연결이 취소되었습니다."); }
    }

    private void saveBackupToDrive(String token, String json) throws Exception {
        String id = findBackupFileId(token);
        if (id == null) createBackupFile(token, json); else updateBackupFile(token, id, json);
    }

    private String findBackupFileId(String token) throws Exception {
        String url = "https://www.googleapis.com/drive/v3/files?spaces=appDataFolder&fields=files(id,name)&pageSize=100";
        String body = requestDrive(token, "GET", url, null, null);
        JSONArray files = new JSONObject(body).optJSONArray("files");
        if (files == null) return null;
        for (int i = 0; i < files.length(); i++) {
            JSONObject file = files.optJSONObject(i);
            if (file != null && GOOGLE_DRIVE_BACKUP_FILE.equals(file.optString("name"))) {
                String id = file.optString("id", "");
                if (id.length() > 0) return id;
            }
        }
        return null;
    }

    private void createBackupFile(String token, String json) throws Exception {
        String boundary = "cgm" + UUID.randomUUID().toString().replace("-", "");
        String metadata = "{\"name\":\"" + GOOGLE_DRIVE_BACKUP_FILE + "\",\"parents\":[\"appDataFolder\"],\"mimeType\":\"application/json\"}";
        String body = "--"+boundary+"\r\nContent-Type: application/json; charset=UTF-8\r\n\r\n"+metadata+"\r\n--"+boundary+"\r\nContent-Type: application/json; charset=UTF-8\r\n\r\n"+json+"\r\n--"+boundary+"--\r\n";
        requestDrive(token, "POST", "https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart", body.getBytes(StandardCharsets.UTF_8), "multipart/related; boundary="+boundary);
    }

    private void updateBackupFile(String token, String id, String json) throws Exception {
        requestDrive(token, "PATCH", "https://www.googleapis.com/upload/drive/v3/files/"+id+"?uploadType=media", json.getBytes(StandardCharsets.UTF_8), "application/json; charset=UTF-8");
    }

    private void loadBackupFromDrive(String token) throws Exception {
        String id = findBackupFileId(token);
        if (id == null) { notifyDriveStatus("empty", "Google Drive에 저장된 기록이 없습니다."); return; }
        String json = requestDrive(token, "GET", "https://www.googleapis.com/drive/v3/files/"+id+"?alt=media", null, null);
        final String encoded = Base64.encodeToString(json.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);
        runOnUiThread(new Runnable(){@Override public void run(){ if(webView!=null)webView.evaluateJavascript("window.oaiGoogleDriveBackupReceived&&window.oaiGoogleDriveBackupReceived("+JSONObject.quote(encoded)+")",null); }});
    }

    private String requestDrive(String token, String method, String urlString, byte[] payload, String contentType) throws Exception {
        HttpURLConnection c=(HttpURLConnection)new URL(urlString).openConnection(); c.setRequestMethod(method); c.setRequestProperty("Authorization","Bearer "+token); c.setRequestProperty("Accept","application/json"); c.setConnectTimeout(15000); c.setReadTimeout(20000);
        if(payload!=null){c.setDoOutput(true);c.setRequestProperty("Content-Type",contentType);OutputStream out=c.getOutputStream();out.write(payload);out.close();}
        int code=c.getResponseCode();InputStream in=code>=200&&code<300?c.getInputStream():c.getErrorStream();String body=readAll(in);c.disconnect();if(code<200||code>=300)throw new Exception("Drive "+code+": "+body);return body;
    }

    private String readAll(InputStream in) throws Exception { if(in==null)return "";BufferedReader r=new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));StringBuilder b=new StringBuilder();String line;while((line=r.readLine())!=null)b.append(line);r.close();return b.toString(); }

    private void notifyDriveStatus(final String status, final String message) { runOnUiThread(new Runnable(){@Override public void run(){if(webView!=null)webView.evaluateJavascript("window.oaiGoogleDriveStatus&&window.oaiGoogleDriveStatus("+JSONObject.quote(status)+","+JSONObject.quote(message)+")",null);}}); }

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
        public void connectGoogleDrive() { runOnUiThread(new Runnable(){@Override public void run(){ requestGoogleDriveAuthorization("connect", ""); }}); }

        @JavascriptInterface
        public void saveGoogleDriveBackup(final String json) { runOnUiThread(new Runnable(){@Override public void run(){ requestGoogleDriveAuthorization("save", json); }}); }

        @JavascriptInterface
        public void loadGoogleDriveBackup() { runOnUiThread(new Runnable(){@Override public void run(){ requestGoogleDriveAuthorization("load", ""); }}); }

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
