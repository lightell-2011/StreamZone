STREAMZONE — ANDROID FIRST PLAN (FREE TOOLS)

WHAT THIS ZIP CONTAINS
- web/ : the first StreamZone website/backend source code
- android/ : an Android Studio project for a simple APK wrapper
The Android project is source code, not a compiled APK yet.

IMPORTANT
The Android app asks for a website/server URL because live channels and chat require a running backend. The website must be deployed and served over HTTPS for reliable camera access on Android. Until we deploy it, the APK wrapper will not have a public StreamZone URL to connect to. Do not enter a random URL.

BUILD THE APK ON WINDOWS
1. Install Android Studio (free): https://developer.android.com/studio
2. Extract this ZIP.
3. In Android Studio choose Open, then select the `android` folder.
4. Let Gradle sync and install any SDK components Android Studio offers.
5. For a test build, choose Build > Build Bundle(s) / APK(s) > Build APK(s).
6. Android Studio will show the APK location when the build completes.

NEXT REQUIRED STEP: DEPLOY THE BACKEND
The web/ folder contains the current website and Node.js backend. The APK uses the server URL entered on first launch. We need to deploy web/ to a free HTTPS-capable host before the APK can work online for other people. Free hosting plans have limits and may sleep or change. Large-scale live video cannot be guaranteed free.

LOCAL WEBSITE TEST
Install Node.js LTS from https://nodejs.org/
Open a Command Prompt in the `web` folder and run:
  npm install
  npm start
Then open http://localhost:3000 on the Windows PC.

CAMERA SECURITY
For browser camera and microphone access, modern browsers require HTTPS or localhost. On Android, use a real HTTPS deployment URL for camera streaming. The local PC URL is only for local testing and is not a public internet address.

This first Android build is a WebView wrapper around the responsive website, not yet a full native Android rewrite. It is the quickest free path to an APK and lets us reuse the same app for Windows later.
