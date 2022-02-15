# The Programming Club IIITDMJ - Android [▢](https://github.com/yatharthagoenka/tpc/releases/download/production/TPC.apk)

<img alt="JAVA" src="https://img.shields.io/badge/JAVA-•-red" /> <img alt="RETROFIT" src="https://img.shields.io/badge/RETROFIT-•-orange" />
<img alt="FIRESTORE" src="https://img.shields.io/badge/FIRESTORE-•-blue" />
<img alt="RESTFUL" src="https://img.shields.io/badge/REST-•-brown" />
<img alt="JSOUP" src="https://img.shields.io/badge/JSOUP-•-green" />
<img alt="MVVM" src="https://img.shields.io/badge/MVVM-•-pink" />
<img alt="OkHTTP" src="https://img.shields.io/badge/OkHTTP-•-purple" />

</br>

<img src="app\src\main\res\drawable\app_icon.png" align="left" width="150" hspace="10" vspace="30">

TPC Android is a self-developed android application (JAVA) for the Programming Club at Indian Institute of Information Technology, Jabalpur [▢](https://iiitdmj.ac.in) (Check out the official website [here](https://www.iiitdmj.ac.in/webix.iiitdmj.ac.in/))</br>

The app features a `dual interface architecture`, exposing the users to various functionalities based on their roles and administrative access. Users are broadly bifurcated between two categories - Administrators and Club Members.

</br>

> Click on the icon beside the title to download APK.

## Features

 - Handles user authentication (Login / Verification / Registration) through `Google Realtime Database`. Users can login through their registered credentials or using Google Auth.
 - Create, Manage, View, RSVP and set reminders to upcoming events associated with the programming club. Event data is stored, fetched and managed through `Google Firestore` in real-time.
 > Note: Create, Manage and Delete functionalities are only accessible by the administrators.
 - View and set reminders to upcoming global contests relating to the field of Competitive Programming on Codechef, Codeforces and AtCoder, `all in one place`.
 API calls are made and managed by `Retrofit` while `JSoup` enables web-scraping functionalities.
 - Reminders set are synced to the user's Google Calender through the `Android Calender API` for easy access through all devices and across platforms.
 - Implements fragment lifecycle awareness and live data handling through `MVVM Architecture Components`.
 - View, Edit and Update your profile, with the information publicly visible to all users logged in to the system.

</br>

## Getting Started

1.  Pull down the code locally.
2.  Open Android Studio and select 'Open an existing Android Studio Project'
3.  Navigate to checked out repository.
4.  Switch the files view to `Android`.
5.  Locate the JAVA source files under `java/com.example.tpc` and resource layout files under `res/layout`.
> Navigate to the project source files in this repository - [Locate](https://github.com/yatharthagoenka/tpc/tree/master/app/src/main/java/com/example/tpc)

</br>

## Running the Sample App

Connect an Android device to your development machine.

> Use Android Wifi ADB for wireless installation/debugging through the connected android device. Read more about the extension [here](https://plugins.jetbrains.com/plugin/7983-android-wifi-adb)

### Android Studio

* Select `Run -> Run 'app'` (or `Debug 'app'`) from the menu bar
* Select the device you wish to run the app on and click 'OK'
</br>
</br>
### App Configurations
```
defaultConfig {
    // ...
    minSdkVersion: 23
    targetSdkVersion: 31
}
```

### Add repositories to project's `build.gradle`

```
allprojects {
    // ...
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

### Added dependencies to App module's `build.gradle`

```
// use the latest available versions

// Google Firebase
implementation 'com.google.firebase:firebase-auth:19.2.0'
implementation 'com.google.firebase:firebase-core:17.0.0'
implementation 'com.google.firebase:firebase-database:20.0.2'
implementation 'com.google.firebase:firebase-firestore:24.0.0'
implementation platform('com.google.firebase:firebase-bom:29.0.0')
implementation 'com.google.android.gms:play-services-auth:19.2.0'
implementation 'com.firebaseui:firebase-ui-auth:4.3.2'

// Retrofit API, GSON, JSOUP
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
compileOnly 'org.glassfish:javax.annotation:10.0-b28'
implementation 'org.jsoup:jsoup:1.11.2'

// Other external libraries for UI

```
</br>

### Permissions 
 - Internet
 ```
<uses-permission android:name="android.permission.INTERNET" />
 ```
 - No external storage permissions are required by the application.


</br>
</br>

> NOTE: The project is currently under production. Users might find bugs within the current release.
