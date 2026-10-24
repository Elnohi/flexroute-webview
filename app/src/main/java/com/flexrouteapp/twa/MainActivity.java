package com.flexrouteapp.twa;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {

    private static final String APP_URL = "https://flexrouteapp.com/flexroute";

    private WebView webView;
    private ProgressBar progressBar;

    // Back button (Android 13+ / API 36). When an app targets Android 16 (API 36), the system no longer calls
    // onBackPressed() or dispatches KEYCODE_BACK on Android 16 phones, so Back must be handled through the
    // back-invoked callback. It is registered ONLY while the WebView has history to go back to; when there
    // is none, the system handles Back itself (leaves the app, with the normal back-to-home animation).
    private OnBackInvokedCallback backCallback;
    private boolean backCallbackRegistered = false;

    // For file chooser (camera/gallery access)
    private ValueCallback<Uri[]> filePathCallback;
    private static final int FILE_CHOOSER_REQUEST = 100;

    // For GPS: Android needs a runtime location grant before the WebView can
    // deliver a position, even though the manifest declares the permission.
    private static final int LOCATION_REQUEST = 101;
    private String pendingGeoOrigin;
    private GeolocationPermissions.Callback pendingGeoCallback;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            backCallback = () -> {
                if (webView != null && webView.canGoBack()) webView.goBack();
            };
        }

        // Full-screen layout
        RelativeLayout layout = new RelativeLayout(this);
        layout.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));

        // Progress bar shown while page loads
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        progressBar.setId(View.generateViewId());
        RelativeLayout.LayoutParams pbParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, 8);
        pbParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        progressBar.setLayoutParams(pbParams);
        layout.addView(progressBar);

        // WebView
        webView = new WebView(this);
        RelativeLayout.LayoutParams wvParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        webView.setLayoutParams(wvParams);
        layout.addView(webView);

        setContentView(layout);

        // WebView settings
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);           // Required — FlexRoute is a JS app
        settings.setDomStorageEnabled(true);           // localStorage for session persistence
        settings.setDatabaseEnabled(true);             // IndexedDB / WebSQL
        settings.setGeolocationEnabled(true);          // GPS for start-address detection
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAllowFileAccess(true);             // File access for camera/gallery
        settings.setAllowContentAccess(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        // User-agent: identifies as FlexRoute WebView for analytics
        settings.setUserAgentString(
                settings.getUserAgentString() + " FlexRouteApp/1.0");

        // WebViewClient: keep navigation inside the app, handle external links
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                // Keep flexrouteapp.com and netlify function calls inside WebView
                if (url.startsWith("https://flexrouteapp.com") ||
                    url.contains(".netlify/functions/")) {
                    return false;
                }
                // Open external URLs (maps, waze, share links) in browser
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                updateBackHandling();
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);
                updateBackHandling();
            }
        });

        // WebChromeClient: GPS, file chooser, progress bar
        webView.setWebChromeClient(new WebChromeClient() {

            // Progress bar
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }

            // GPS permission — needed for "Use my location" feature
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin,
                    GeolocationPermissions.Callback callback) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                        || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED
                        || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {
                    callback.invoke(origin, true, false);
                    return;
                }
                // Not granted yet: ask the user, then answer the page from
                // onRequestPermissionsResult. A page request still waiting is declined.
                if (pendingGeoCallback != null) {
                    pendingGeoCallback.invoke(pendingGeoOrigin, false, false);
                }
                pendingGeoOrigin = origin;
                pendingGeoCallback = callback;
                requestPermissions(new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, LOCATION_REQUEST);
            }

            // File chooser — needed for camera/gallery when scanning itinerary
            @Override
            public boolean onShowFileChooser(WebView webView,
                    ValueCallback<Uri[]> filePath,
                    FileChooserParams fileChooserParams) {
                if (filePathCallback != null) {
                    filePathCallback.onReceiveValue(null);
                }
                filePathCallback = filePath;
                Intent intent = fileChooserParams.createIntent();
                // Enable multi-select — mirrors the HTML input's "multiple"
                // attribute which WebView doesn't forward automatically.
                // Without this, the gallery only allows single-image picks
                // and drivers have to upload screenshots one at a time.
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                try {
                    startActivityForResult(intent, FILE_CHOOSER_REQUEST);
                } catch (Exception e) {
                    filePathCallback = null;
                    return false;
                }
                return true;
            }

            // Camera/mic permissions for future use
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                request.grant(request.getResources());
            }
        });

        // Load the app
        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        } else {
            webView.loadUrl(APP_URL);
        }
    }

    // Handle file chooser result (single or multi-select)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_CHOOSER_REQUEST) {
            if (filePathCallback != null) {
                Uri[] results = null;
                if (resultCode == Activity.RESULT_OK && data != null) {
                    // Multi-select: files arrive via getClipData()
                    if (data.getClipData() != null) {
                        int count = data.getClipData().getItemCount();
                        results = new Uri[count];
                        for (int i = 0; i < count; i++) {
                            results[i] = data.getClipData().getItemAt(i).getUri();
                        }
                    }
                    // Single-select: file arrives via getData()
                    else if (data.getData() != null) {
                        results = new Uri[] { data.getData() };
                    }
                }
                filePathCallback.onReceiveValue(results);
                filePathCallback = null;
            }
        }
    }

    // Answer the page's pending GPS request once the user responds to the dialog
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
            int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST && pendingGeoCallback != null) {
            boolean granted = false;
            for (int r : grantResults) {
                if (r == PackageManager.PERMISSION_GRANTED) granted = true;
            }
            pendingGeoCallback.invoke(pendingGeoOrigin, granted, false);
            pendingGeoCallback = null;
            pendingGeoOrigin = null;
        }
    }

    // Register the back callback while the WebView can go back, unregister it when it can't.
    private void updateBackHandling() {
        if (backCallback == null || webView == null) return;           // only exists on Android 13+
        boolean need = webView.canGoBack();
        OnBackInvokedDispatcher dispatcher = getOnBackInvokedDispatcher();
        if (need && !backCallbackRegistered) {
            dispatcher.registerOnBackInvokedCallback(OnBackInvokedDispatcher.PRIORITY_DEFAULT, backCallback);
            backCallbackRegistered = true;
        } else if (!need && backCallbackRegistered) {
            dispatcher.unregisterOnBackInvokedCallback(backCallback);
            backCallbackRegistered = false;
        }
    }

    // Older Android (below 13, or when the app has not enabled the back callback): Back navigates within the
    // WebView history before closing the app. On Android 16 with targetSdk 36 this is not called.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // Save WebView state across rotations
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
        updateBackHandling();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.destroy();
    }
}
