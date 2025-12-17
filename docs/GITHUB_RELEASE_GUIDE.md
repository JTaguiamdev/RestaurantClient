# GitHub Releases Setup Guide

## 📦 Automatic APK Build & Release with GitHub Actions

This guide explains how to automatically build and release APK files when you create a new version tag.

---

## 🚀 Quick Start

### Creating a Release (Simple Method)

1. **Commit your changes:**
   ```bash
   git add .
   git commit -m "Release version 1.0.0"
   ```

2. **Create and push a version tag:**
   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```

3. **Wait for GitHub Actions:**
   - Go to your repository on GitHub
   - Click "Actions" tab
   - Watch the build process (takes ~5-10 minutes)



4. **Download your APK:**
   - Go to "Releases" tab
   - Find your new release
   - Download the APK files

---

## 📋 Setup Instructions

### Step 1: Configure GitHub Secrets

Your workflow needs environment variables. Set these up in GitHub:

1. Go to your repository on GitHub
2. Click **Settings** → **Secrets and variables** → **Actions**
3. Click **New repository secret**
4. Add these secrets:

| Secret Name | Description | Example Value |
|------------|-------------|---------------|
| `API_BASE_URL` | Your backend API URL | `https://your-api.com/api/` |
| `KEYSTORE_PASSWORD` | (Optional) Keystore password for signed APK | `your-password` |
| `KEY_ALIAS` | (Optional) Key alias | `your-alias` |
| `KEY_PASSWORD` | (Optional) Key password | `your-password` |

**Note:** The keystore secrets are only needed for signed release builds. Debug builds work without them.

---

### Step 2: Understand Tag Naming Convention

The workflow triggers on tags matching the pattern `v*.*.*`

**Valid tag formats:**
- ✅ `v1.0.0` - Major release
- ✅ `v1.2.3` - Standard version
- ✅ `v2.0.0-beta` - Beta release
- ✅ `v1.5.1-rc1` - Release candidate

**Invalid formats:**
- ❌ `1.0.0` - Missing 'v' prefix
- ❌ `version-1.0` - Wrong format
- ❌ `release-1` - Wrong format

---

## 🏷️ Version Tag Naming Guide

### Semantic Versioning (Recommended)

Use the format: `vMAJOR.MINOR.PATCH`

#### **MAJOR** version (v**X**.0.0)
Increment when you make incompatible API changes
```bash
v1.0.0 → v2.0.0
```

#### **MINOR** version (v1.**X**.0)
Increment when you add functionality in a backward-compatible manner
```bash
v1.0.0 → v1.1.0
```

#### **PATCH** version (v1.0.**X**)
Increment when you make backward-compatible bug fixes
```bash
v1.0.0 → v1.0.1
```

### Pre-release Tags

For testing versions before official release:

- **Alpha**: `v1.0.0-alpha` or `v1.0.0-alpha.1`
- **Beta**: `v1.0.0-beta` or `v1.0.0-beta.2`
- **Release Candidate**: `v1.0.0-rc1` or `v1.0.0-rc.1`

---

## 📝 Complete Release Workflow

### Method 1: Command Line

```bash
# 1. Make sure all changes are committed
git status

# 2. Create a tag
git tag -a v1.0.0 -m "Release version 1.0.0 - Initial release"

# 3. Push the tag to GitHub
git push origin v1.0.0
```

### Method 2: GitHub Web Interface

1. Go to your repository on GitHub
2. Click **Releases** on the right side
3. Click **Draft a new release**
4. Click **Choose a tag**
5. Type your tag (e.g., `v1.0.0`) and click **Create new tag**
6. Fill in the release title and description
7. Click **Publish release**

GitHub Actions will automatically build and attach APK files.

---

## 📂 Generated Files

When the workflow completes, these files will be attached to your release:

```
RestaurantClient-v1.0.0-debug.apk       (Debug build - for testing)
RestaurantClient-v1.0.0-release.apk     (Release build - for production)
```

### File Sizes (Approximate)
- Debug APK: ~15-25 MB (includes debugging info)
- Release APK: ~10-18 MB (optimized and minified)

---

## 🔄 What Happens Automatically

1. **Trigger**: You push a tag like `v1.0.0`
2. **GitHub Actions starts**:
   - Checks out your code
   - Sets up JDK 21
   - Creates `.env` file with API_BASE_URL
   - Builds debug APK
   - Builds release APK (if keystore configured)
   - Renames APK files with version number
3. **Creates GitHub Release**:
   - Uses tag as release name
   - Generates release notes template
   - Uploads debug APK
   - Uploads release APK
4. **You get notified** via GitHub

---

## 📱 Testing the APK

### Debug APK (For Developers)
```bash
# Download debug APK
# Install on device
adb install RestaurantClient-v1.0.0-debug.apk

# Or manually:
# 1. Download APK to phone
# 2. Enable "Install from Unknown Sources"
# 3. Open APK file
# 4. Install
```

### Release APK (For Users)
Same as debug, but optimized for production use.

---

## 🔍 Monitoring the Build

### View Build Status

1. Go to your repository
2. Click **Actions** tab
3. Click on the running workflow
4. Watch the logs in real-time

### Build Logs

Each step shows:
- ✅ Success (green checkmark)
- ❌ Failure (red X)
- 📝 Detailed logs

---

## 🛠️ Troubleshooting

### Build Fails

**Problem**: Gradle build fails

**Solution**:
```bash
# Test locally first
./gradlew assembleDebug --stacktrace

# If it works locally, check:
# 1. API_BASE_URL secret is set
# 2. Code is pushed to GitHub
# 3. No syntax errors in workflow file
```

### Missing APK in Release

**Problem**: Release created but no APK attached

**Solution**:
- Check Actions tab for errors
- Verify build completed successfully
- Check if APK was renamed correctly

### Tag Already Exists

**Problem**: "Tag already exists" error

**Solution**:
```bash
# Delete local tag
git tag -d v1.0.0

# Delete remote tag
git push origin :refs/tags/v1.0.0

# Create new tag
git tag v1.0.0
git push origin v1.0.0
```

---

## 📖 Example Release Workflow

### First Release (v1.0.0)

```bash
# Prepare for release
git checkout main
git pull origin main

# Create tag
git tag -a v1.0.0 -m "Initial release

Features:
- User authentication
- Product browsing
- Shopping cart
- Order management
- Admin dashboard"

# Push tag
git push origin v1.0.0

# Wait 5-10 minutes
# Check GitHub Releases tab
# Download APK
```

### Bug Fix Release (v1.0.1)

```bash
# Fix bugs in code
git add .
git commit -m "Fix: Cart calculation bug"

# Create patch tag
git tag -a v1.0.1 -m "Bug fix release

Fixed:
- Cart total calculation
- Login session timeout"

git push origin main
git push origin v1.0.1
```

### Feature Release (v1.1.0)

```bash
# Add new features
git add .
git commit -m "Feature: Add favorites"

# Create minor version tag
git tag -a v1.1.0 -m "Feature release

New:
- Favorite products feature
- Order filtering
- Push notifications"

git push origin main
git push origin v1.1.0
```

---

## 🎯 Best Practices

### 1. Always Test Before Tagging
```bash
./gradlew assembleDebug
# Test the APK thoroughly
# Then create tag
```

### 2. Write Clear Release Notes
```bash
git tag -a v1.2.0 -m "Clear description of what's new"
```

### 3. Use Pre-release Tags for Testing
```bash
# Beta testing
git tag v1.2.0-beta
git push origin v1.2.0-beta

# After testing is good
git tag v1.2.0
git push origin v1.2.0
```

### 4. Keep a CHANGELOG.md
Document all changes in a changelog file

### 5. Increment Versions Correctly
- Bug fixes: `v1.0.0` → `v1.0.1`
- New features: `v1.0.0` → `v1.1.0`
- Breaking changes: `v1.0.0` → `v2.0.0`

---

## 📋 Checklist Before Release

- [ ] All tests pass locally
- [ ] Code is committed and pushed
- [ ] README.md is updated
- [ ] Version number follows semantic versioning
- [ ] API_BASE_URL secret is configured
- [ ] Release notes are prepared
- [ ] Tag name is correct format (v*.*.*)

---

## 🔗 Quick Commands Cheat Sheet

```bash
# Create and push tag
git tag v1.0.0
git push origin v1.0.0

# View all tags
git tag -l

# Delete tag (if mistake)
git tag -d v1.0.0
git push origin :refs/tags/v1.0.0

# View tag details
git show v1.0.0

# Create annotated tag with message
git tag -a v1.0.0 -m "Release message"

# Check current version
git describe --tags

# List remote tags
git ls-remote --tags origin
```

---

## 📞 Need Help?

If you encounter issues:
1. Check the Actions tab for error logs
2. Verify all secrets are configured
3. Test the build locally first
4. Check the workflow file syntax
5. Open an issue on GitHub

---

## 🎉 Success!

Once setup, every time you push a version tag, you'll automatically get:
- ✅ APK files built and tested
- ✅ Release created on GitHub
- ✅ APK files attached to release
- ✅ Version history tracked

**Your release is ready to download and distribute!** 🚀

---

## 📅 Version History Example

```
v1.0.0 - 2025-01-15 - Initial release
v1.0.1 - 2025-01-20 - Bug fixes
v1.1.0 - 2025-02-01 - New features
v1.1.1 - 2025-02-05 - Hotfix
v2.0.0 - 2025-03-01 - Major update
```

---

Made with ❤️ for easy releases!
