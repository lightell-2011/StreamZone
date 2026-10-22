package com.streamzone.app;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;

public class MainActivity extends Activity {
    private WebView webView;
    private PermissionRequest pendingRequest;
    private static final int MEDIA_PERMISSION_REQUEST = 420;
    private SharedPreferences prefs;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(16, 13, 24));
        getWindow().setNavigationBarColor(Color.rgb(16, 13, 24));
        prefs = getSharedPreferences("streamzone", MODE_PRIVATE);
        buildWebView();
        String savedUrl = prefs.getString("server_url", "");
        if (savedUrl.isEmpty()) showServerDialog();
        else loadServer(savedUrl);
    }

    private void buildWebView() {
        webView = new WebView(this);
        webView.setBackgroundColor(Color.rgb(16, 13, 24));
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.getSettings().setAllowFileAccess(false);
        webView.getSettings().setAllowContentAccess(false);
        webView.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                String scheme = uri.getScheme();
                return !("https".equalsIgnoreCase(scheme) || "http".equalsIgnoreCase(scheme));
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override public void onPermissionRequest(PermissionRequest request) {
                runOnUiThread(() -> {
                    boolean needsCamera = false, needsAudio = false;
                    for (String resource : request.getResources()) {
                        if (PermissionRequest.RESOURCE_VIDEO_CAPTURE.equals(resource)) needsCamera = true;
                        if (PermissionRequest.RESOURCE_AUDIO_CAPTURE.equals(resource)) needsAudio = true;
                    }
                    boolean cameraOk = !needsCamera || checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
                    boolean audioOk = !needsAudio || checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
                    if (cameraOk && audioOk) {
                        request.grant(request.getResources());
                    } else {
                        pendingRequest = request;
                        String[] permissions;
                        if (needsCamera && needsAudio) permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
                        else if (needsCamera) permissions = new String[]{Manifest.permission.CAMERA};
                        else permissions = new String[]{Manifest.permission.RECORD_AUDIO};
                        requestPermissions(permissions, MEDIA_PERMISSION_REQUEST);
                    }
                });
            }
        });
        setContentView(webView);
    }

    private void showServerDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(42, 20, 42, 5);
        TextView note = new TextView(this);
        note.setText("Enter your StreamZone website address. The app needs a running StreamZone server. For camera streaming, use an HTTPS address. Localhost works only on the same device.");
        note.setTextColor(Color.DKGRAY);
        note.setTextSize(14);
        layout.addView(note);
        EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setHint("https://your-streamzone-site.example");
        input.setText(prefs.getString("server_url", ""));
        input.setInputType( android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_URI);
        layout.addView(input);
        new AlertDialog.Builder(this)
            .setTitle("Connect StreamZone")
            .setView(layout)
            .setCancelable(false)
            .setPositiveButton("Connect", (dialog, which) -> {
                String url = input.getText().toString().trim();
                if (!url.startsWith("https://") && !url.startsWith("http://")) {
                    new AlertDialog.Builder(this).setMessage("Enter a full address beginning with https:// or http://.")
                        .setPositiveButton("OK", (d, w) -> showServerDialog()).show();
                    return;
                }
                prefs.edit().putString("server_url", url).apply();
                loadServer(url);
            })
            .setNeutralButton("Change later", (dialog, which) -> {
                String url = prefs.getString("server_url", "");
                if (!url.isEmpty()) loadServer(url);
                else showServerDialog();
            })
            .show();
    }

    private void loadServer(String url) {
        if (webView != null) webView.loadUrl(url);
    }

    @Override public boolean onCreateOptionsMenu(android.view.Menu menu) {
        menu.add("Change server address").setOnMenuItemClickListener(item -> {
            showServerDialog();
            return true;
        });
        menu.add("Reload").setOnMenuItemClickListener(item -> {
            if (webView != null) webView.reload();
            return true;
        });
        return true;
    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MEDIA_PERMISSION_REQUEST && pendingRequest != null) {
            boolean allGranted = grantResults.length > 0;
            for (int result : grantResults) if (result != PackageManager.PERMISSION_GRANTED) allGranted = false;
            if (allGranted) pendingRequest.grant(pendingRequest.getResources());
            else pendingRequest.deny();
            pendingRequest = null;
        }
    }

    @Override public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }
}
