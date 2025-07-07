# MINIMAP

<p align="center">
     <img src = https://github.com/V-P-A-AppDev/MINIMAP/blob/main/images/minimaplogo.png?raw=true>
</p>

This repository contains Android application code for MiniMap, a location-based help request platform.

## 📱 Introduction

MiniMap is an Android application that facilitates community assistance by connecting people who need help with those willing to provide it. The app uses real-time location services and Firebase to create a seamless helping experience.

### 👥 User Types

#### Helper
- Users who agree to help others
- Receive notifications when someone nearby requests assistance
- Can view requester details and location
- Built-in rating system for accountability

#### Requester  
- Users who need assistance
- Send help requests to users in their vicinity
- Can specify the type of help needed
- Real-time chat with assigned helpers

## 🎯 Mission

To make helping people easier and more accessible. By creating a system that notifies willing helpers and streamlines the assistance process, we believe communities will become more supportive and people in need will receive help faster and more efficiently.

## 💡 Solution

Creating a location-based application that:
- Shows help requests on an interactive map
- Connects helpers with requesters in real-time
- Provides secure communication channels
- Maintains user privacy and safety

## 🏗️ Architecture

<p align="center">
     <img src="https://github.com/V-P-A-AppDev/MINIMAP/blob/main/images/uml_new.png"/>
</p>

## 🛠️ Technical Stack

### Core Technologies
- **Platform**: Android (API 28+)
- **Language**: Java
- **Database**: Firebase Realtime Database
- **Authentication**: Firebase Auth
- **Maps**: Google Maps Android API
- **Location**: Google Play Services Location
- **Image Loading**: Glide
- **Geolocation**: GeoFire
 
# [Presentation](https://github.com/V-P-A-AppDev/MINIMAP/blob/main/images/PRES1.pptx)
![](https://github.com/V-P-A-AppDev/MINIMAP/blob/main/images/PRES1/Slide1.jpg)
![](https://github.com/V-P-A-AppDev/MINIMAP/blob/main/images/PRES1/Slide2.jpg)
![](https://github.com/V-P-A-AppDev/MINIMAP/blob/main/images/PRES1/Slide3.jpg)
![](https://github.com/V-P-A-AppDev/MINIMAP/blob/main/images/PRES1/Slide4.jpg)
![](https://github.com/V-P-A-AppDev/MINIMAP/blob/main/images/PRES1/Slide5.jpg)
![](https://github.com/V-P-A-AppDev/MINIMAP/blob/main/images/PRES1/Slide6.jpg)
![](https://github.com/V-P-A-AppDev/MINIMAP/blob/main/images/PRES1/Slide7.jpg)
![](https://github.com/V-P-A-AppDev/MINIMAP/blob/main/images/PRES1/Slide8.jpg)
![](https://github.com/V-P-A-AppDev/MINIMAP/blob/main/images/PRES1/Slide9.jpg)
![](https://github.com/V-P-A-AppDev/MINIMAP/blob/main/images/PRES1/Slide11.jpg)
![](https://github.com/V-P-A-AppDev/MINIMAP/blob/main/images/PRES1/Slide12.jpg)
![](https://github.com/V-P-A-AppDev/MINIMAP/blob/main/images/PRES1/Slide13.jpg)
![](https://github.com/V-P-A-AppDev/MINIMAP/blob/main/images/PRES1/Slide14.jpg)
![](https://github.com/V-P-A-AppDev/MINIMAP/blob/main/images/PRES1/Slide15.jpg)
![](https://github.com/V-P-A-AppDev/MINIMAP/blob/main/images/PRES1/Slide16.jpg)
![](https://github.com/V-P-A-AppDev/MINIMAP/blob/main/images/PRES1/Slide17.jpg)
![](https://github.com/V-P-A-AppDev/MINIMAP/blob/main/images/PRES1/Slide18.jpg)
![](https://github.com/V-P-A-AppDev/MINIMAP/blob/main/images/PRES1/Slide19.jpg)
![](https://github.com/V-P-A-AppDev/MINIMAP/blob/main/images/PRES1/Slide21.jpg)

## 🚀 Installation & Setup

### For Users
1. Download the APK from the [Releases](Release/) section
2. Enable "Install from Unknown Sources" in your device settings
3. Install the APK file
4. Grant necessary permissions (Location, Internet)
5. Create an account and start helping or requesting assistance

### For Developers
1. Clone the repository:
   ```bash
   git clone https://github.com/V-P-A-AppDev/MINIMAP.git
   ```

2. Open the project in Android Studio

3. Configure Firebase:
   - Create a Firebase project
   - Download `google-services.json` and place it in `app/`
   - Enable Authentication and Realtime Database

4. Configure Google Maps:
   - Get a Google Maps API key
   - Update `google_maps_api.xml` with your key

5. Build and run the application

## 📱 Features

### Core Features
- **Real-time Location Tracking**: Accurate GPS-based location services
- **Interactive Map**: Google Maps integration with custom markers
- **Push Notifications**: Instant alerts for help requests
- **In-app Chat**: Secure communication between helpers and requesters
- **User Profiles**: Customizable profiles with ratings
- **Geofencing**: Location-based request matching

### Security Features
- **Encrypted Communication**: All data transmitted over HTTPS
- **Input Validation**: Protection against malicious input
- **Session Management**: Secure user authentication
- **Data Privacy**: Sensitive information protection

## 🔧 Configuration

### Firebase Setup
1. Create a Firebase project
2. Enable Authentication (Email/Password)
3. Enable Realtime Database
4. Configure security rules
5. Add your `google-services.json` file

### Google Maps Setup
1. Enable Google Maps Android API
2. Create API key with restrictions
3. Update `google_maps_api.xml` files
4. Configure billing (if required)


## 🧪 Testing

### Security Testing
- Test password validation with various inputs
- Verify HTTPS enforcement for network requests
- Test input sanitization with malicious content
- Verify backup exclusions work correctly
- Test code obfuscation in release builds

### Functionality Testing
- User registration and authentication
- Location services and map functionality
- Help request creation and response
- Real-time chat communication
- User profile management

## 📈 Performance

- **Target SDK**: 34 (Android 14)
- **Minimum SDK**: 28 (Android 9.0)
- **Optimized**: ProGuard obfuscation and resource shrinking
- **Network**: Efficient Firebase Realtime Database usage
- **Memory**: Optimized image loading with Glide


## 📄 License

This project is for academic use only. The developers do not hold any responsibility for any faults, security issues, data loss, trust issues, or any other possible kind of damage that it might create to you or your device. Use at your own risk.


## 📱 Download

### QR Code
<img src=https://github.com/V-P-A-AppDev/MINIMAP/blob/main/Release/27.12.2021/qr-code.png width="250" height="250">

### Direct Download
[Download APK](https://github.com/V-P-A-AppDev/MINIMAP/raw/main/Release/27.12.2021/MiniMap_27_12_2021_14_59.apk)

---

**⚠️ Disclaimer**: This application was created for academic use only. We do not hold any responsibility for any faults, security issues, data loss, trust issues, or any other possible kind of damage that it might create to you or your device. Use at your own risk.
