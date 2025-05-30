# 📱 Text Summarizer Android App

An AI-powered Android application that summarizes text, extracts text from images (OCR), and syncs data across devices.

## ✨ Key Features

- **🤖 AI Text Summarization** - Google Generative AI (Gemini) with adjustable length
- **📸 OCR Text Extraction** - Extract text from images and QR codes using ML Kit
- **🔍 Smart Search** - Search through saved summaries by title and content
- **🌐 Translation** - Multi-language detection and translation
- **☁️ Cloud Sync** - Firebase authentication and cross-device synchronization
- **💾 Offline Storage** - Room database for local data management

## 🛠️ Tech Stack

- **Language**: Kotlin
- **Architecture**: MVVM
- **Database**: Room (SQLite) + Firebase Firestore
- **AI/ML**: Google Generative AI, ML Kit (OCR, Translation, Barcode)
- **Authentication**: Firebase Auth + Google Sign-In
- **UI**: Material Design with Lottie animations

## 🚀 Quick Setup

1. **Clone and Open**
   ```powershell
   git clone https://github.com/yourusername/text-summarizer-android.git
   ```

2. **Firebase Setup**
   - Create Firebase project at [console.firebase.google.com](https://console.firebase.google.com/)
   - Add Android app with package `com.example.Text_Summarizer`
   - Download `google-services.json` to `app/` directory
   - Enable Authentication, Firestore, and Storage

3. **API Keys**
   Add to `app/src/main/res/values/strings.xml`:
   ```xml
   <string name="api_key">YOUR_GOOGLE_AI_API_KEY</string>
   <string name="default_web_client_id">YOUR_FIREBASE_WEB_CLIENT_ID</string>
   ```

4. **Build & Run**
   ```powershell
   .\gradlew clean build
   ```

## 📱 App Screens
- **🏠 Home**: Enter text and get AI-powered summaries with adjustable length
- **📸 OCR**: Capture images or select from gallery for text extraction
- **💾 Saved**: Browse and search through saved summaries
- **🌐 Translate**: View original and translated text side-by-side
- **👤 Profile**: User authentication, settings, and cloud backup

## 🔑 Required Permissions

- `CAMERA` - Text extraction from camera
- `INTERNET` - AI processing and cloud sync
- `READ_EXTERNAL_STORAGE` - Image selection
- `ACCESS_NETWORK_STATE` - Connectivity checks

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License.

---

**Built with ❤️ using Kotlin • Android • Firebase • Google AI**
