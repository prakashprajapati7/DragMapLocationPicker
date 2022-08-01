# DragMapLocationPicker

We all come across applications or ideas where we may want users to pick or select their location from maps. From a UX perspective, it is more convenient to pick a location than to manually enter the fields given.
Also, from the developer's perspective getting user location with pinpoint accuracy helps in getting the job done as required concerning location more precisely.

Well, it's quite easy, you just have to call a few Google Maps APIs, set up location permissions precisely, and create your layout for location picker and that’s all!!!

Features :
* Runtime Location Permission Dialog (Supported Upto Android 12)
  (ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
* Pick locations using "touch" gestures on the map (Dragging Map, not Marker)
* Customize Address (like Society Name, City, Pin Code, Landmark etc)
* Get Latitude & Longitude
* Smooth Animation around marker while dragging map to find specific point on map
* Lottie Animation (https://airbnb.design/lottie/, https://lottiefiles.com/)

Prerequisites :

* minSdkVersion >= 21
* Google Play Services = 17.0.0 (You can change it's version as per your need)
* AndroidX

Adding Map Key in Manifest file
To add Google Maps to our Application we need the API key, to get yours refer to this blog on Android Google Maps Integration.
Please follow below link
https://developers.google.com/maps/documentation/android-sdk/start#enable-api-sdk
Add your Map key in manifest file in below block

<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="@string/google_map_key" />
    
Initally you will also get this error on first run
File google-services.json is missing. The Google Services Plugin cannot function without it.
For this error solution, please add your google-services.json in path (dragMapLocationPicker\app\)
Rebuild the project and Run.

Please find attachment Demo Video
https://user-images.githubusercontent.com/107295397/182077974-bb23ca47-d3e1-4832-bf62-6342f6c89f45.mp4

Screenshots
![Screenshot_1](https://user-images.githubusercontent.com/107295397/182078401-f23da3a5-ac32-438e-bf63-9a45779479dd.png)
![Screenshot_2](https://user-images.githubusercontent.com/107295397/182078412-fdb9f360-19c4-47c2-8af5-9f68cc1d52c5.png)

THANK YOU




