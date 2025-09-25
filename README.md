Noor of Gratitude: Deployment Guide <br>
Module 1: Client Mobile Application (Android): <br>
1. Requirements <br>
- Stable internet Connection <br>
- Android Studio (latest version recommended) <br>
- JDK 8 or above <br>
- Android device (with USB or pair devices Debugging enabled) or Android Studio Emulator <br>

2. Firebase Configuration Noor of Gratitude <br>
Noor of Gratitude uses Firebase Realtime Database, Authentication and Firestore. <br>
Step 1: Create Firebase Project <br>
- Go to Firebase Console. <br>
- Click Add Project → Enter a name (e.g., noorfatima) → Continue. <br>
Step 2: Add Android App <br>
- Click Add App → Android. <br>
- Enter package name: noorofgratitute.com <br>
- Download google-services.json and place inside app/ folder. <br>
Step 3: Enable Firebase Services <br>
- Authentication: Enable Email/Password and Google Sign-In. <br>
- Generate SHA-1 and SHA-256 keys using ./gradlew signingReport and add in Firebase. <br>

3. Project Android Setup: <br>
- Download the project folders from GitHub.com. <br>
- Open Android Studio. <br>
- Place the "google-service.json" (for Firebase authentication). <br>
- Wait for Gradle sync to complete. <br>
- Connect an Android device through USB cable or emulator. <br>
- Click the "Run" button in Android Studio. <br>
- The Application will be installed and launched automatically on the device or emulator. <br>

Module 2: Admin Web App (Django Backend Admin Panel): <br>
1. Requirements: <br>
- Stable internet connection <br>
- Python 3.13.2 or above <br>
- Code editor (VS Code / PyCharm recommended) <br>
- pip installed <br>
- Virtual environment (venv) <br>
- MySQL Server (latest version recommended) <br>
- Packages (installable via pip install -r requirements.txt): <br>
- firebase_admin 7.1.0 or above <br>
- mysqlclient 2.2.7 or above <br>
- pillow 11.3.0 or above <br>
- django-nested-admin 4.1.2 or above <br>
- djangorestframework 3.16.1 or above <br>

2. Steps to Set up Firebase Admin SDK in Django Backend <br>
Step 1: Generate Service Account Key <br>
- Go to Firebase Console <br>
- Select your project → Project Settings → Service Accounts tab <br>
- Click Generate new private key → Download the JSON file <br>
- Place this JSON file in Django backend project folder (e.g., noorfatima/serviceAccountKey.json) <br>
- Install dependencies: pip install firebase-admin <br>
- Initialize Firebase in Django using this JSON file <br>

3. Steps to Set up Admin Web App <br>
1. Download the project folder from GitHub.com. <br>
2. Activate the virtual environment: <br>
- Windows PowerShell: .\venv\Scripts\activate <br>
- Windows CMD: venv\Scripts\activate <br>
3. Navigate to project folder: <br>
- cd myproject <br>
4. Run the server: <br>
- python manage.py runserver <br>
5. If accessing from another device: <br>
- Windows: ipconfig or Mac/Linux: ifconfig <br>
6. Open Admin Panel <br>
- Browser: http://127.0.0.1:8000/admin (Use your PC IP for network access) <br>
7. Login with the superuser credentials. <br>
- user name: ------ <br>
- password: ------ <br>
8. To Stop Django Server: <br>
- Press Ctrl + C <br>

4. Post-Deployment Testing <br>
- After installing the APK: <br>

4.1. Client App Testing (Android) <br>
1. Authentication <br>
- Test Email/Password registration and login <br>
2. The Holy Quran <br>
- Verify that Surah and Juz lists are fetched from the backend <br>
3. Gratitude Community Support <br>
- Create public and private posts <br>
4. Mood Journal <br>
- Add mood entries <br>

4.2. Admin Panel Testing (Django Backend) <br>
1. Login to Admin Panel <br>
- Open: http://<your-ip-address>:8000/admin <br>
2. User Management <br>
- Enable/disable users <br>
3. Posts Management <br>
- Verify public and private post data in the admin panel <br>
4. Quran Content <br>
- Ensure Surah and Juz data is stored and fetched correctly <br>
