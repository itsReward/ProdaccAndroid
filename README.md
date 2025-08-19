# JobKeep - Vehicle Garage Management Mobile App

[![Android API](https://img.shields.io/badge/API-31%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=31)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2024.06.00-green.svg)](https://developer.android.com/jetpack/compose)

## 📱 About

**JobKeep** is a comprehensive Android mobile application for vehicle garage management, designed to streamline automotive service operations. The app enables technicians, service advisors, and managers to efficiently manage job cards, track time, handle inventory, and monitor service progress in real-time.

This mobile app is part of the larger **ProdAcc Vehicle Garage Management System**, specifically optimized for mobile workflows and field operations.

## 🎯 Key Features

### 🔧 Job Card Management
- **Create & Manage**: Create new job cards and manage existing ones
- **Real-time Status Tracking**: Automatic status updates (Open → Diagnostics → Work in Progress → Testing → Waiting for Payment → Closed)
- **Assignment Management**: Assign technicians to specific job cards
- **Priority Handling**: Mark and filter by job priority levels

### ⏱️ Time Tracking
- **Clock In/Out**: Simple time tracking for work sessions
- **Timesheet Creation**: Detailed work reports with timestamps
- **Special Timesheet Types**:
  - **Diagnostics**: Automatically triggers diagnostics status
  - **Quality Control Test**: Triggers testing status for quality assurance
- **Progress Reporting**: Add detailed work progress reports

### 👥 Role-Based Access Control
- **Technician**: Access assigned job cards, time tracking, progress updates
- **Service Advisor**: Manage own job cards, assign technicians, request parts
- **Supervisor**: Quality control, workflow oversight, all job card access
- **Manager**: Business analytics, operational oversight
- **Admin**: Full system access and configuration

### 📦 Inventory Management
- **Parts Lookup**: Search and view available inventory
- **Requisition System**: Request parts for job cards (requires approval)
- **Stock Level Monitoring**: Real-time inventory status
- **Supplier Information**: Access to supplier details and contact info

### 🔔 Real-time Notifications
- **WebSocket Integration**: Live updates for job status changes
- **Role-based Notifications**: Relevant notifications based on user role
- **Foreground Service**: Maintains connection for real-time updates
- **Notification Permissions**: Smart permission handling for Android 13+

### 📊 Customer & Vehicle Management
- **Client Records**: View and manage customer information
- **Vehicle History**: Complete service history for each vehicle
- **Contact Management**: Customer contact details and communication

## 🏗️ Technical Architecture

### Modern Android Development Stack
- **UI Framework**: Jetpack Compose (100% Compose UI)
- **Architecture**: MVVM with Clean Architecture principles
- **Dependency Injection**: Hilt for dependency management
- **Navigation**: Compose Navigation for type-safe navigation
- **State Management**: StateFlow and Compose State
- **Networking**: Retrofit + OkHttp for REST API communication
- **Local Storage**: Room Database for offline support
- **Real-time**: WebSocket client for live updates

### Project Structure
```
app/                          # Main application module
├── src/main/java/com/example/prodacc/
│   ├── ui/                   # UI layer (Compose screens & ViewModels)
│   ├── MainActivity.kt       # Main activity
│   └── JobKeepApplication.kt # Application class
│
core/                         # Core modules
├── auth/                     # Authentication module
├── data/                     # Data layer (repositories, API, local DB)
└── designsystem/             # UI design system & reusable components
│
products/                     # Products/Inventory feature module
navigation/                   # Navigation configuration
```

### Key Dependencies
```kotlin
// Core Android
implementation("androidx.core:core-ktx:1.13.1")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
implementation("androidx.activity:activity-compose:1.9.1")

// Jetpack Compose
implementation(platform("androidx.compose:compose-bom:2024.06.00"))
implementation("androidx.compose.material3:material3:1.2.1")

// Dependency Injection
implementation("com.google.dagger:hilt-android:2.51")
implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

// Networking
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

// Local Database
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")

// Navigation
implementation("androidx.navigation:navigation-compose:2.7.7")
```

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Arctic Fox or later
- **Minimum SDK**: API 31 (Android 12)
- **Target SDK**: API 34 (Android 14)
- **Kotlin**: 1.9.0
- **Gradle**: 8.7

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/jobkeep-android.git
   cd jobkeep-android
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory

3. **Configure Backend Connection**
   - Update the base URL in your network configuration
   - Ensure the backend Spring Boot API is running
   - Configure any required API endpoints

4. **Build and Run**
   ```bash
   ./gradlew assembleDebug
   ```
   Or use Android Studio's build/run functionality

### Backend Requirements
This app requires the **ProdAcc Spring Boot backend** to be running. The backend provides:
- RESTful API endpoints for all data operations
- JWT authentication
- WebSocket server for real-time updates
- PostgreSQL database integration

## 📋 User Workflows

### For Technicians
1. **Login** → View assigned job cards
2. **Select Job Card** → Clock in for work session
3. **Start Diagnostics** → Create diagnostics timesheet (triggers diagnostics status)
4. **Perform Work** → Create work timesheets (triggers work-in-progress status)
5. **Quality Testing** → Create testing timesheet (triggers testing status)
6. **Complete Work** → Clock out and submit detailed report

### For Service Advisors
1. **Customer Contact** → Create new job card
2. **Vehicle Assignment** → Link job card to customer's vehicle
3. **Staff Assignment** → Assign technicians and supervisor
4. **Parts Management** → Request required parts (needs approval)
5. **Progress Monitoring** → Track work progress and status
6. **Invoice Creation** → Generate invoice upon completion

### For Supervisors & Managers
1. **Overview Dashboard** → Monitor all active job cards
2. **Quality Control** → Review completed work and testing
3. **Staff Management** → Oversee technician assignments
4. **Analytics** → View performance metrics and reports

## 🔒 Security Features

- **JWT Authentication**: Secure token-based authentication
- **Role-based Access**: Granular permissions based on user roles
- **Data Encryption**: Secure communication with backend
- **Offline Security**: Local data protection when offline
- **Session Management**: Automatic token refresh and logout

## 🌐 Offline Support

- **Room Database**: Local caching of critical data
- **Sync Mechanism**: Automatic synchronization when connection restored
- **Offline Indicators**: Clear UI indicators for connection status
- **Data Persistence**: Work continues seamlessly offline

## 🔔 Notifications System

- **Real-time Updates**: WebSocket-powered live notifications
- **Role-based Filtering**: Only relevant notifications for each user
- **Foreground Service**: Maintains connection in background
- **Rich Notifications**: Detailed information in notification content
- **Action Buttons**: Quick actions directly from notifications

## 🧪 Testing

### Running Tests
```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest

# Specific test class
./gradlew test --tests="com.example.prodacc.ExampleUnitTest"
```

### Test Coverage
- Unit tests for ViewModels and business logic
- Integration tests for Repository classes
- UI tests for critical user flows
- MockWebServer for API testing

## 🚀 Deployment

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

### Build Variants
- **Debug**: Development build with logging and debugging tools
- **Release**: Optimized production build with ProGuard/R8

## 📊 Performance

### Optimization Features
- **Lazy Loading**: Efficient data loading strategies
- **Image Caching**: Optimized image loading and caching
- **Memory Management**: Proper lifecycle management
- **Background Processing**: Efficient background task handling
- **Network Optimization**: Request batching and caching

### Monitoring
- **Crash Reporting**: Comprehensive crash tracking
- **Performance Metrics**: App performance monitoring
- **User Analytics**: Usage pattern analysis
- **Network Monitoring**: API call performance tracking

## 🤝 Contributing

### Development Guidelines
1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/AmazingFeature`)
3. **Follow** Kotlin coding standards and Android best practices
4. **Add** tests for new functionality
5. **Commit** changes (`git commit -m 'Add AmazingFeature'`)
6. **Push** to branch (`git push origin feature/AmazingFeature`)
7. **Open** a Pull Request

### Code Style
- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use [ktlint](https://ktlint.github.io/) for code formatting
- Maintain consistent naming conventions
- Add meaningful comments for complex logic

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🔗 Related Projects

- **Backend API**: [ProdAcc Spring Boot API](https://github.com/your-username/prodacc-backend)
- **Web Application**: [ProdAcc React Web App](https://github.com/your-username/prodacc-web)

## 📞 Support

### Documentation
- [API Documentation](https://api-docs.prodacc.com)
- [User Guide](https://docs.prodacc.com/mobile)
- [Development Wiki](https://github.com/your-username/jobkeep-android/wiki)

### Contact
- **Issues**: [GitHub Issues](https://github.com/your-username/jobkeep-android/issues)
- **Discussions**: [GitHub Discussions](https://github.com/your-username/jobkeep-android/discussions)
- **Email**: support@prodacc.com

---

**Built with ❤️ for the automotive service industry**
