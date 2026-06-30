package kr.catholic.gildonmu;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;


public class MainActivity extends Activity {
    private static final String START_URL = "https://wbcatholic-ch.github.io/shrine-map/";
    private static final String MAIN_HOST = "wbcatholic-ch.github.io";
    private static final int REQ_LOCATION = 7001;
    private static final int SYSTEM_BAR_NAVY = Color.rgb(14, 21, 53);
    private static final int SPLASH_IVORY = Color.rgb(245, 240, 232);

    private FrameLayout rootLayout;
    private View statusBarOverlay;
    private FrameLayout launchOverlay;
    private long launchStartedAt;
    private WebView webView;
    private GeolocationPermissions.Callback pendingGeoCallback;
    private String pendingGeoOrigin;
    private boolean pendingLaunchStorageReset;
    private boolean launchStorageResetReloading;
    private static final long BACKGROUND_TO_COVER_MS = 30L * 60L * 1000L;
    private long lastBackgroundedAt = 0L;
    private FrameLayout transientIvoryOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        addLaunchOverlay();

        setContentView(rootLayout);
        configureShellLayout();

        if (!hasLocationPermission()) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, REQ_LOCATION);
        }

        configureWebView();
        loadStartUrl("create", false);
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
                int top = getStatusBarInsetTop(insets);
                int bottom = getNavigationBarInsetBottom(insets);
                boolean tablet = isTabletLayout();

                /*
                 * V267:
                 * Real Android tablets use decor-fits-system-windows, so the WebView content frame
                 * is already placed below the status bar. Adding another top margin here doubled or
                 * misaligned the top area on some Galaxy Tab models. Fold/phone keeps the old full
                 * WebView frame and status overlay behavior.
                 */
                int webTop = (tablet && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) ? 0 : (tablet ? top : 0);
                int overlayTop = (tablet && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) ? 0 : top;

                FrameLayout.LayoutParams webParams = (FrameLayout.LayoutParams) webView.getLayoutParams();
                if (webParams.topMargin != webTop || webParams.bottomMargin != bottom) {
                    webParams.topMargin = webTop;
                    webParams.bottomMargin = bottom;
                    webView.setLayoutParams(webParams);
                }

                FrameLayout.LayoutParams statusParams = (FrameLayout.LayoutParams) statusBarOverlay.getLayoutParams();
                if (statusParams.height != overlayTop) {
                    statusParams.height = overlayTop;
                    statusBarOverlay.setLayoutParams(statusParams);
                }
                statusBarOverlay.bringToFront();
                return insets;
            }
        });
        rootLayout.requestApplyInsets();
    }

    private int getStatusBarInsetTop(WindowInsets insets) {
        if (insets == null) return 0;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                return insets.getInsets(WindowInsets.Type.statusBars()).top;
            }
        } catch (Exception ignored) {
        }
        return insets.getSystemWindowInsetTop();
    }

    private int getNavigationBarInsetBottom(WindowInsets insets) {
        if (insets == null) return 0;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                return insets.getInsets(WindowInsets.Type.navigationBars()).bottom;
            }
        } catch (Exception ignored) {
        }
        return insets.getSystemWindowInsetBottom();
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
            // V267: real tablets must not draw under the status bar; Fold/phone keep the previous full-screen shell.
            window.setDecorFitsSystemWindows(isTabletLayout());
        }

        window.setStatusBarColor(SYSTEM_BAR_NAVY);
        window.setNavigationBarColor(SYSTEM_BAR_NAVY);
        window.getDecorView().setBackgroundColor(SYSTEM_BAR_NAVY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.setNavigationBarDividerColor(SYSTEM_BAR_NAVY);
        }
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
        lastBackgroundedAt = System.currentTimeMillis();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        configureSystemBars();
        if (rootLayout != null) {
            rootLayout.requestApplyInsets();
        }
        dispatchNativeDeviceClassToWeb(20L);
        long elapsed = lastBackgroundedAt > 0L ? System.currentTimeMillis() - lastBackgroundedAt : 0L;
        if (lastBackgroundedAt > 0L && elapsed >= BACKGROUND_TO_COVER_MS) {
            showTransientIvoryOverlay(360L);
            dispatchNativeResumeToWeb(true, 20L);
        } else {
            dispatchNativeResumeToWeb(false, 60L);
        }
    }

    /**
     * Fold/open-cover screen changes must not recreate or reload the WebView.
     * Keep the current DOM and let the web layer handle it as a normal resize.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        configureSystemBars();
        configureShellLayout();
        if (rootLayout != null) {
            rootLayout.requestApplyInsets();
        }
        dispatchNativeDeviceClassToWeb(0L);
        if (webView != null) {
            if (isFoldLikeDevice()) {
                // V267: hide the unstable native resize surface itself. This prevents the old left/right WebView
                // frame from being visible before the PWA has applied its final centered layout.
                webView.setAlpha(0f);
                dispatchViewportChangedToWeb(40L);
                dispatchViewportChangedToWeb(180L);
                webView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (webView != null) webView.setAlpha(1f);
                        } catch (Exception ignored) {
                        }
                    }
                }, 240L);
            } else {
                dispatchViewportChangedToWeb(0L);
                dispatchViewportChangedToWeb(180L);
            }
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
                                    "window.__OAI_ANDROID_NATIVE_FOLD_SETTLE_UNTIL=Date.now()+360;" +
                                    "document.documentElement.classList.add('oai-fold-resizing','oai-trail-map-settling');" +
                                    "if(window.oaiApplyAndroidScreenClass){window.oaiApplyAndroidScreenClass('android-fold-stable-final-force-remeasure');}" +
                                    "if(window.oaiRecalibrateCoverViewport){window.oaiRecalibrateCoverViewport('android-fold-stable');}" +
                                    "window.dispatchEvent(new Event('resize'));" +
                                    "window.dispatchEvent(new CustomEvent('oai-android-fold-stable'));" +
                                    "setTimeout(function(){try{document.documentElement.classList.remove('oai-fold-resizing','oai-trail-map-settling');}catch(e){}},220);" +
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
            handleLauncherReturn();
        }
    }


    private void handleLauncherReturn() {
        if (webView == null) return;
        long elapsed = lastBackgroundedAt > 0L ? System.currentTimeMillis() - lastBackgroundedAt : 0L;
        if (lastBackgroundedAt > 0L && elapsed >= BACKGROUND_TO_COVER_MS) {
            showTransientIvoryOverlay(360L);
            dispatchNativeResumeToWeb(true, 20L);
            return;
        }
        dispatchNativeResumeToWeb(false, 20L);
    }

    private void dispatchNativeResumeToWeb(final boolean forceCover, long delayMs) {
        if (webView == null) return;
        final long elapsed = lastBackgroundedAt > 0L ? Math.max(0L, System.currentTimeMillis() - lastBackgroundedAt) : 0L;
        webView.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (webView == null) return;
                    String script;
                    if (forceCover) {
                        script = "(function(){try{" +
                                "window.__OAI_ANDROID_NATIVE_FORCE_COVER_UNTIL=Date.now()+5000;" +
                                "window.__OAI_ANDROID_NATIVE_SHORT_RESUME_UNTIL=0;" +
                                "window.dispatchEvent(new CustomEvent('oai-android-native-resume',{detail:{forceCover:true,elapsed:" + elapsed + "}}));" +
                                "if(typeof window.goToCover==='function')window.goToCover();" +
                                "}catch(e){}})();";
                    } else {
                        script = "(function(){try{" +
                                "window.__OAI_ANDROID_NATIVE_SHORT_RESUME_UNTIL=Date.now()+15000;" +
                                "sessionStorage.removeItem('oai_home_backgrounded_at');" +
                                "sessionStorage.removeItem('oai_home_background_token_v345');" +
                                "sessionStorage.removeItem('oai_home_background_token_v344');" +
                                "document.documentElement.classList.remove('oai-background-return-intro','oai-cover-under-intro-reveal','oai-resume-freeze','oai-resume-return-veil');" +
                                "window.dispatchEvent(new CustomEvent('oai-android-native-resume',{detail:{forceCover:false,elapsed:" + elapsed + "}}));" +
                                "}catch(e){}})();";
                    }
                    webView.evaluateJavascript(script, null);
                } catch (Exception ignored) {
                }
            }
        }, Math.max(0L, delayMs));
    }

    private void showTransientIvoryOverlay(long keepMs) {
        if (rootLayout == null) return;
        try {
            if (transientIvoryOverlay == null) {
                transientIvoryOverlay = new FrameLayout(this);
                transientIvoryOverlay.setBackgroundColor(SPLASH_IVORY);
                transientIvoryOverlay.setAlpha(1f);
                rootLayout.addView(transientIvoryOverlay, new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                ));
            } else {
                transientIvoryOverlay.animate().cancel();
                transientIvoryOverlay.setAlpha(1f);
                transientIvoryOverlay.bringToFront();
            }
            if (statusBarOverlay != null) statusBarOverlay.bringToFront();
            final View overlay = transientIvoryOverlay;
            overlay.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        overlay.animate().cancel();
                        overlay.animate()
                                .alpha(0f)
                                .setDuration(110)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        try {
                                            if (overlay.getParent() instanceof ViewGroup) {
                                                ((ViewGroup) overlay.getParent()).removeView(overlay);
                                            }
                                        } catch (Exception ignored) {
                                        }
                                        if (overlay == transientIvoryOverlay) transientIvoryOverlay = null;
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


    private boolean isFoldLikeDevice() {
        try {
            String m = String.valueOf(Build.MANUFACTURER) + " "
                    + String.valueOf(Build.BRAND) + " "
                    + String.valueOf(Build.MODEL) + " "
                    + String.valueOf(Build.DEVICE) + " "
                    + String.valueOf(Build.PRODUCT);
            return m.matches("(?i).*\\bSM-F\\d+.*")
                    || m.matches("(?i).*\\bF9\\d+.*")
                    || m.toLowerCase().contains("fold");
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
            return m.matches("(?i).*\\bSM-[TX]\\w+.*")
                    || m.toLowerCase().contains("galaxy tab");
        } catch (Exception ignored) {
            return false;
        }
    }

    private boolean isTabletLayout() {
        try {
            if (isFoldLikeDevice()) return false;
            if (isGalaxyTabLikeDevice()) return true;
            Configuration config = getResources().getConfiguration();
            return config != null && config.smallestScreenWidthDp >= 600;
        } catch (Exception ignored) {
            return false;
        }
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

    private void configureWebView() {
        WebSettings s = webView.getSettings();

        try {
            String defaultUa = s.getUserAgentString();
            if (defaultUa == null) defaultUa = "";
            // V266: Tablet token is restored only for real tablets. Fold/open screens stay on phone/Fold layout.
            defaultUa = defaultUa.replaceAll("\\s*GildongmuAndroid/\\d+", "");
            defaultUa = defaultUa.replaceAll("\\s*GildongmuTablet/\\d+", "");
            String tabletToken = isTabletLayout() ? " GildongmuTablet/267" : "";
            s.setUserAgentString(defaultUa + " GildongmuAndroid/267" + tabletToken);
        } catch (Exception ignored) {
        }
        // Keep WebView text enlargement at the fixed standard value.
        // Cover menu/refresh text sizes are controlled by the web CSS and must remain fixed.
        s.setTextZoom(100);
        s.setJavaScriptEnabled(true);
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
                                    "if(sheet&&sheet.classList&&sheet.classList.contains('open')&&typeof window._loadNearby==='function'){window._loadNearby();}" +
                                    "}catch(e){}})();",
                            null
                    );
                } catch (Exception ignored) {
                }
            }
        }, 350L);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAndRemoveTask();
            } else {
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
