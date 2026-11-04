# AirFiber Control

An Android app for opening the OpenWrt admin page for a Jio AirFiber router. The default address is `http://192.168.2.1`; the address can be changed in the app.

## Build and download

GitHub Actions builds a debug APK on pushes to `main` or `master`, pull requests, and manual runs. Open the repository's **Actions** tab, select **Build Android APK**, open a completed run, and download the **AirFiber-Control-APK** artifact. Extract the ZIP and install `app-debug.apk` on the Samsung Galaxy S24. Android may ask you to allow installing apps from the browser or file manager used to open it.

## Use

1. Connect the S24 to the router's Wi-Fi network.
2. Open AirFiber Control. It starts with `http://192.168.2.1`.
3. Tap **Open**, then sign in on the router's own OpenWrt page.

The app does not ask for or save router credentials. It displays the router's own web interface in an embedded browser. HTTP is enabled because many OpenWrt installations expose their local admin page over HTTP; use this only on a trusted home Wi-Fi network. The router page and login are not reachable through this app when the phone is off that network.

## Project

- Android application ID: `com.rajneeshmahala.airfibercontrol`
- Minimum Android version: Android 8.0 (API 26)
- Build command: `gradle assembleDebug`
