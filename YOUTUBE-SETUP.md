# StreamZone YouTube integration — setup

This feature is on the `feature/youtube-integration` branch. It adds `/youtube.html`, a YouTube navigation link on the StreamZone home page, live-stream search, embedded playback, account/channel details, subscriptions, and live-chat read/send UI.

## Required Google setup

1. Open [Google Cloud Console](https://console.cloud.google.com/) and create/select a project.
2. Enable **YouTube Data API v3**.
3. Create an **API key**. Restrict it to YouTube Data API v3 and, after deployment, to your HTTPS website referrer(s).
4. Configure the OAuth consent screen and add test users while the app is in testing.
5. Create an OAuth client ID of type **Web application**. Add the exact HTTPS origin of the deployed StreamZone website under Authorized JavaScript origins. Do not put the API key or client secret in source code.
6. Open StreamZone in a normal browser and visit `/youtube.html`. Choose **API setup**, enter the OAuth client ID and API key, then connect Google.

The page saves the client ID and API key in that browser's local storage; the access token is kept in memory only. Restrict the API key because browser-side keys are visible to users. Never add an OAuth client secret to this repository.

## Important limitations

- Google OAuth through the Google Identity Services browser flow is intended for a supported browser context. Google sign-in may be blocked inside an embedded Android WebView. The current Android project is a WebView wrapper, so a production APK needs an external-browser/native OAuth flow and a secure return-to-app mechanism before claiming reliable sign-in inside the APK.
- Google API access depends on the Google Cloud project, consent-screen status, quotas, and the user's granted scopes.
- YouTube live chat is only available for eligible active live streams. Sending messages requires authorization and can be denied by channel/chat restrictions.
- YouTube Data API does not provide every YouTube feature. Membership or paid-member details are not included in this first version.
- The website/backend still needs to be deployed on HTTPS for use by other people. The APK is not built by this change.

## Development

Run the existing website as described in `web/README.md`. Visit `http://localhost:3000/youtube.html` for layout testing; Google OAuth origin configuration and camera-related features require the appropriate supported context. Do not commit personal API credentials.
