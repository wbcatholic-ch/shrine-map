# WebView JavaScript bridge methods are invoked from JavaScript rather than
# ordinary Java call sites, so they must remain visible after R8 optimization.
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Preserve runtime-visible annotations used by the JavaScript bridge rule.
-keepattributes RuntimeVisibleAnnotations,RuntimeInvisibleAnnotations
