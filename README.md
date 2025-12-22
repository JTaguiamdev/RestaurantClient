# Itadaki - Restaurant Client Android App

![Android](https://img.shields.io/badge/Android-33--36-green?logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-blue?logo=kotlin)
![Material Design 3](https://img.shields.io/badge/Material%20Design-3-purple)
![License](https://img.shields.io/badge/License-MIT-blue)

**A modern Android restaurant ordering system featuring Itadaki brand identity, glassmorphism UI, and animated backgrounds**

---

## 📥 Download

[![Download APK](https://img.shields.io/badge/Download-Latest%20APK-red?style=for-the-badge&logo=android)](https://github.com/JTaguiamdev/RestaurantClient/releases/download/latest/RestaurantClient-debug.apk)

*Note: This is a debug build. You may need to allow "Install from Unknown Sources" on your device.*

---

## 📱 About

**Itadaki** (頂 - "to receive") - A comprehensive mobile application for restaurant order management with stunning UI/UX. Built with Kotlin and Material Design 3, featuring glassmorphism effects and optimized performance.

### 📸 Screenshots

<p align="center">
  <img src="screenshots/Screenshot_20251222_100529.png" width="30%" alt="App Screenshot 1" />
  <img src="screenshots/Screenshot_20251222_100647.png" width="30%" alt="App Screenshot 2" />
  <img src="screenshots/Screenshot_20251222_100928.png" width="30%" alt="App Screenshot 3" />
</p>

<p align="center">
  <img src="screenshots/Screenshot_20251222_100955.png" width="30%" alt="App Screenshot 4" />
  <img src="screenshots/Screenshot_20251222_101007.png" width="30%" alt="App Screenshot 5" />
  <img src="screenshots/Screenshot_20251222_101021.png" width="30%" alt="App Screenshot 6" />
</p>

---

### ✨ Key Features
- 🎨 **Itadaki Brand Identity** - Warm cream backgrounds with deep red accents
- 💎 **Glassmorphism UI** - Modern blur effects and frosted glass design
- 🎬 **Animated Backgrounds** - GIF animations with smart caching
- 🎠 **Auto-scrolling Carousel** - Product showcase with 15+ menu items
- ⚡ **Optimized Performance** - 60 FPS animations with adaptive quality tiers
- ♿ **Accessibility** - WCAG AA compliant with full TalkBack support

---

### Customer Features
- 🔐 Secure authentication with animated login
- 🎠 Product carousel with 15+ featured dishes
- 🍽️ Browse menu with glassmorphic cards
- 🛒 Real-time shopping cart management
- 💳 Streamlined checkout process
- 📦 Order history tracking

### Administrator Features
- 📊 Dashboard with real-time statistics
- 👥 User management (CRUD operations)
- 🍔 Product management with categories
- 📋 Order tracking and management

---

## 🛠️ Tech Stack

- **Language:** Kotlin 2.1.0
- **Architecture:** MVVM
- **UI:** Material Design 3, XML Layouts, ViewBinding
- **DI:** Hilt 2.51.1
- **Networking:** Retrofit 2.9.0, OkHttp 4.11.0
- **Async:** Kotlin Coroutines 1.7.3
- **Image Loading:** Coil 2.x (GIF support)
- **Glassmorphism:** BlurView 3.2.0
- **Security:** AndroidX Security Crypto
- **Min SDK:** API 33 (Android 13)
- **Target SDK:** API 36 (Android 15)

---

## 📦 Installation

### Prerequisites
- Android Studio Ladybug 2024.2.1+
- JDK 21
- Android device/emulator with API 33+

### Steps

1. **Clone the repository**
```bash
git clone https://github.com/JTaguiamdev/RestaurantClient.git
cd RestaurantClient
```

2. **Configure Backend API**
   
Create/update `local.properties`:
```properties
API_BASE_URL=http://your-backend-server.com/api/
```

3. **Build and Run**
   - Open in Android Studio
   - Sync Gradle
   - Run on device/emulator

---

## 🎯 Usage

### For Customers
1. **Login/Register** - Create account or sign in
2. **Browse Products** - View menu with search and filters
3. **Add to Cart** - Manage items and quantities
4. **Checkout** - Review order and confirm
5. **Track Orders** - View order history and status

### For Administrators
1. **Dashboard** - View statistics and insights
2. **Manage Users** - Add, edit, delete users
3. **Manage Products** - CRUD operations for menu items
4. **Track Orders** - Monitor and update order status

---

## 📚 Documentation

Comprehensive guides available in the project root:
- [**UI_MODERNIZATION_PLAN.md**](UI_MODERNIZATION_PLAN.md) - Complete UI roadmap
- [**GLASS_COMPONENTS.md**](GLASS_COMPONENTS.md) - Glass component library (25+ components)
- [**PHASE_5_POLISH_OPTIMIZATION.md**](PHASE_5_POLISH_OPTIMIZATION.md) - Performance & optimization
- [**THEME_SELECTION_GUIDE.md**](THEME_SELECTION_GUIDE.md) - Complete theming guide

---

## ⚡ Performance

| Metric | Target | Achieved |
|--------|--------|----------|
| Frame Rate | 60 FPS | ✅ 60 FPS |
| Blur Render Time | <16ms | ✅ <16ms |
| Memory Overhead | <50MB | ✅ <50MB |
| Cold Start Time | <3s | ✅ <2s |

### Performance Tiers
- **HIGH_QUALITY** (6GB+ RAM): Full effects, 60 FPS
- **BALANCED** (3-6GB RAM): Optimized effects, 55+ FPS
- **POWER_SAVER** (<3GB RAM): Reduced effects, 45+ FPS

---

## ♿ Accessibility

- ✅ WCAG 2.1 AA compliant
- ✅ 4.5:1 minimum color contrast
- ✅ 48dp minimum touch targets
- ✅ Full TalkBack support
- ✅ Respects "Reduce Motion" setting

---

## 🔐 Security

- Encrypted storage with AndroidX Security Crypto
- HTTPS communication
- JWT token authentication
- Role-based access control
- Input validation and sanitization

---

## 🧪 Testing

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Run all tests
./gradlew check
```

---

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'feat: add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- **Material Design 3** - Modern design system
- **BlurView Library** - Glassmorphism effects
- **Hilt** - Dependency injection
- **Retrofit** - REST API client
- **Kotlin Coroutines** - Async programming
- **ARROW Server Team** - Robust backend

---

**Made with ❤️ using Kotlin and Material Design 3**

© 2025 Restaurant Client • Modern • Accessible • Performant
