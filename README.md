# ExcelSync 📊

A specialized Android utility app built with Jetpack Compose that facilitates bulk uploading of election booth data from Excel/CSV files to Firebase Realtime Database.

## Features 🌟

- Upload booth data from Excel/CSV files to Firebase
- Real-time upload status and progress tracking
- Validates data before upload
- Supports bulk operations
- Simple and intuitive UI

## Tech Stack 💻

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM
- **Backend**: Firebase Realtime Database

## Dependencies 📦

```gradle
dependencies {
    // Firebase
    implementation platform('com.google.firebase:firebase-bom:32.7.0')
    implementation 'com.google.firebase:firebase-database-ktx'
    
    // Jetpack Compose
    implementation 'androidx.activity:activity-compose:1.8.2'
    implementation 'androidx.compose.material3:material3'
    
    // Coroutines
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3'
}
```

## Setup Instructions 🚀

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/ExcelSync.git
   ```

2. Firebase Setup:
   - Create a new Firebase project
   - Add your Android app to Firebase project
   - Download `google-services.json` and place it in the `app/` directory
   - Enable Realtime Database

3. Database Rules:
   ```json
   {
     "rules": {
       "Cities": {
         ".read": true,
         ".write": "auth != null"
       }
     }
   }
   ```

## File Format 📝

CAN CHANGE AS PER REQUIREMENT

Prepare your Excel/CSV file with the following columns in order:

| Column | Description | Required |
|--------|-------------|-----------|
| Name | Booth name | Yes |
| ID | Unique booth identifier | Yes |
| Latitude | Geographic latitude | Yes |
| Longitude | Geographic longitude | Yes |
| District | District name | Yes |
| Taluka | Taluka name | Yes |
| BLO Name | Booth Level Officer name | No |
| BLO Contact | BLO contact number | No |

Example:
```csv
Name,ID,Latitude,Longitude,District,Taluka,BLO Name,BLO Contact
Primary School Wadgaon,PS001,18.5204,73.8567,Pune,Haveli,Amit Patil,9876543210
```

## Usage Guide 📱

1. Prepare Excel/CSV File:
   - Create file following the format above
   - Save as CSV (Comma delimited)

2. Upload Process:
   - Open ExcelSync app
   - Click "Upload File" button
   - Select your CSV file
   - Wait for upload completion
   - Check success/error message

## Firebase Structure 🗂️

CAN CHANGE AS PER REQUIREMENT

```
Cities/
├── District1/
│   ├── BoothID1/
│   │   ├── name: "Booth Name"
│   │   ├── id: "BoothID1"
│   │   ├── latitude: 18.5204
│   │   ├── longitude: 73.8567
│   │   ├── district: "District1"
│   │   ├── taluka: "Taluka1"
│   │   ├── bloName: "Officer Name"
│   │   └── bloContact: "1234567890"
│   └── BoothID2/
└── District2/
```

## Contributing 🤝

1. Fork the repository
2. Create feature branch: `git checkout -b feature/NewFeature`
3. Commit changes: `git commit -m 'Add NewFeature'`
4. Push to branch: `git push origin feature/NewFeature`
5. Submit pull request



## License 📄

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Contact 📧

Your Name - Vikramaditya Khupse
Project Link: [https://github.com/yourusername/ExcelSync](https://github.com/yourusername/ExcelSync)

---
Made with ❤️ for Efficient Data Management
