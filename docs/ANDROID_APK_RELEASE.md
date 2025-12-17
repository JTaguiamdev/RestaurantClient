# 📱 Auto-Release Android APK - Simple Guide

## What This Does

Automatically builds your **Restaurant Client Android APK** and publishes it to GitHub Releases when you create a version tag.

**Important:** This releases the **Android app** (Kotlin), NOT the ARROW backend server (Rust).

---

## Quick Setup (2 Steps)

### 1. Configure API URL (One Time Only)

Tell your Android app where your ARROW backend is:

1. Go to GitHub repository → **Settings** → **Secrets** → **Actions**
2. Click **New repository secret**
3. Name: `API_BASE_URL`
4. Value: `http://your-arrow-server:8080/api/`
5. Save

### 2. Create Release

```bash
git tag v1.0.0
git push origin v1.0.0
```

Wait 5-10 minutes → Download APK from GitHub Releases!

---

## Tag Naming

**Format:** `v1.0.0` (must start with 'v' and have 3 numbers)

- `v1.0.0` = First release
- `v1.0.1` = Bug fix
- `v1.1.0` = New feature
- `v2.0.0` = Major update

---

## What You Get

**File:** `RestaurantClient-v1.0.0-debug.apk`

- Installable Android app (15-25 MB)
- Works on Android 8.0+ devices
- Users can download and install
- App connects to your ARROW server

---

## How to Install APK

**On Phone:**
1. GitHub → Releases → Download APK
2. Open APK file
3. Enable "Unknown Sources" if asked
4. Install

**Result:** Restaurant Client app installed on Android device!

---

## Understanding the System

```
┌─────────────────────────────────┐
│   ARROW Backend Server (Rust)   │  ← Deployed separately on server
│   - Handles API requests         │
│   - MySQL database               │
└─────────────────────────────────┘
              ↑
              │ HTTP/API calls
              ↓
┌─────────────────────────────────┐
│  Restaurant Client App (Kotlin) │  ← THIS is what gets released!
│  - Android mobile app            │
│  - Installed on phones           │
│  - User interface                │
└─────────────────────────────────┘
```

**This workflow builds the Android app APK, not the backend server!**

---

## Full Example

```bash
# 1. Finish coding your Android app
git add .
git commit -m "Restaurant Client v1.0.0"
git push

# 2. Create release tag
git tag v1.0.0
git push origin v1.0.0

# 3. GitHub builds APK automatically (5-10 min)

# 4. Go to GitHub → Releases → Download APK

# 5. Install APK on Android phone

# 6. App connects to your ARROW backend!
```

---

## Files Created

**.github/workflows/release.yml** - Builds APK on tag push  
**.github/workflows/build-on-push.yml** - Tests build on every commit

---

## More Info

See **GITHUB_RELEASE_GUIDE.md** for detailed documentation.

---

**Made with ❤️ for easy Android app releases**
