# Release Checklist

Quick checklist for creating a new release.

## Pre-Release

- [ ] All features tested and working
- [ ] All bugs fixed
- [ ] Code reviewed
- [ ] Tests passing (`./gradlew test`)
- [ ] Build succeeds locally (`./gradlew assembleDebug`)
- [ ] README.md updated with changes
- [ ] Documentation updated
- [ ] Version number decided (following semantic versioning)

## Version Number

Current version: `v____`  
Next version: `v____`

Type of release:
- [ ] MAJOR (breaking changes) - `vX.0.0`
- [ ] MINOR (new features) - `v1.X.0`
- [ ] PATCH (bug fixes) - `v1.0.X`
- [ ] Pre-release - `v1.0.0-beta`

## Release Steps

- [ ] Create git tag
  ```bash
  git tag -a v1.0.0 -m "Release message"
  ```

- [ ] Push tag to GitHub
  ```bash
  git push origin v1.0.0
  ```

- [ ] Wait for GitHub Actions to complete

- [ ] Check release on GitHub
  - [ ] APK files attached
  - [ ] Release notes added
  - [ ] Links working

- [ ] Test APK installation
  - [ ] Download debug APK
  - [ ] Install on test device
  - [ ] Verify app works correctly

## Post-Release

- [ ] Update version number in code for next development
- [ ] Create announcement (if needed)
- [ ] Update project board
- [ ] Archive old release (if needed)
- [ ] Update CHANGELOG.md

## Release Notes Template

```markdown
## What's New in v1.0.0

### ✨ New Features
- Feature 1
- Feature 2

### 🐛 Bug Fixes
- Fixed bug 1
- Fixed bug 2

### 🔧 Improvements
- Improvement 1
- Improvement 2

### 📝 Changes
- Change 1
- Change 2

### ⚠️ Breaking Changes (if any)
- Breaking change 1

### 📦 Downloads
- Debug APK: For testing
- Release APK: For production
```

## Rollback Plan (If Issues Found)

- [ ] Delete release from GitHub
- [ ] Delete tag
  ```bash
  git tag -d v1.0.0
  git push origin :refs/tags/v1.0.0
  ```
- [ ] Fix issues
- [ ] Create new patch version
