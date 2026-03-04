# Theme Selection Guide for Restaurant Client App

**Last Updated:** December 17, 2025  
**Author:** Development Team  
**Purpose:** Guide for selecting the correct theme for each screen/component

---

##  Table of Contents
1. [Available Themes](#available-themes)
2. [Theme Selection by Screen Type](#theme-selection-by-screen-type)
3. [Activity-Specific Themes](#activity-specific-themes)
4. [Dialog Themes](#dialog-themes)
5. [Widget Styles](#widget-styles)
6. [Quick Reference Table](#quick-reference-table)
7. [Common Mistakes](#common-mistakes)

---

##  Available Themes

### 1. **Theme.RestaurantClient** (Default)
- **Parent:** `Base.Theme.RestaurantClient`
- **Base Parent:** `Theme.Material3.DayNight.NoActionBar`
- **Primary Use:** Customer-facing screens
- **Colors:** Brand colors (food delivery theme)
- **Status Bar:** Brand primary color
- **Features:**
  - Material Design 3
  - Light theme
  - No action bar (custom toolbar)
  - Red accent colors
  - Food delivery branding

**When to Use:**
-  All customer screens (default)
-  Product browsing
-  Shopping cart
-  Checkout
-  User profile (customer view)
-  Login/Authentication
-  Order history

### 2. **Theme.RestaurantClient.Admin**
- **Parent:** `Theme.RestaurantClient`
- **Primary Use:** Admin/management screens
- **Colors:** Blue admin theme
- **Status Bar:** Dark blue
- **Features:**
  - Professional blue palette
  - Admin-specific toolbar
  - Dark status bar
  - Management-focused design

**When to Use:**
-  Admin Dashboard
-  User Management
-  Product Management
-  Order Management
-  Create/Edit Admin screens
-  Any admin-only functionality

### 3. **GlassDialogTheme**
- **Parent:** `Theme.Material3.DayNight.Dialog`
- **Primary Use:** Custom dialogs with glassmorphism
- **Features:**
  - Transparent background
  - Floating appearance
  - Dim background
  - Glass effects

**When to Use:**
-  Custom confirmation dialogs
-  Input dialogs
-  Glass effect dialogs
-  Any dialog using glass components

### 4. **Base.Theme.RestaurantClient**
- **Primary Use:** Internal theme base (DO NOT USE DIRECTLY)
- **Purpose:** Foundation for other themes
- **Note:** Only used as parent for Theme.RestaurantClient

**When to Use:**
-  NEVER use directly in activities/layouts
-  Only use as parent when creating new theme variants

---

##  Theme Selection by Screen Type

### Customer Screens (Use: `Theme.RestaurantClient`)

#### Authentication
```xml
<!-- LoginActivity.kt -->
<!-- Use default theme (no explicit theme needed) -->
<activity android:name=".ui.auth.LoginActivity" />
```

#### Product Browsing
```xml
<!-- ProductListActivity.kt -->
<activity android:name=".ui.product.ProductListActivity" />

<!-- ProductDetailActivity.kt -->
<activity android:name=".ui.product.ProductDetailActivity" />
```

#### Shopping & Checkout
```xml
<!-- ShoppingCartActivity.kt -->
<activity android:name=".ui.cart.ShoppingCartActivity" />

<!-- CheckoutActivity.kt -->
<activity android:name=".ui.checkout.CheckoutActivity" />

<!-- OrderConfirmationActivity.kt -->
<activity android:name=".ui.order.OrderConfirmationActivity" />
```

#### Orders & Profile
```xml
<!-- MyOrdersActivity.kt -->
<activity android:name=".ui.order.MyOrdersActivity" />

<!-- OrderDetailActivity.kt -->
<activity android:name=".ui.order.OrderDetailActivity" />

<!-- UserProfileActivity.kt -->
<activity android:name=".ui.user.UserProfileActivity" />
```

### Admin Screens (Use: `Theme.RestaurantClient.Admin`)

#### Dashboard & Management
```xml
<!-- AdminDashboardActivity.kt -->
<activity
    android:name=".ui.admin.AdminDashboardActivity"
    android:theme="@style/Theme.RestaurantClient.Admin" />

<!-- UserManagementActivity.kt -->
<activity
    android:name=".ui.admin.UserManagementActivity"
    android:theme="@style/Theme.RestaurantClient.Admin" />

<!-- OrderManagementActivity.kt -->
<activity
    android:name=".ui.admin.OrderManagementActivity"
    android:theme="@style/Theme.RestaurantClient.Admin" />
```

#### Create/Edit Screens
```xml
<!-- CreateUserActivity.kt -->
<activity
    android:name=".ui.admin.CreateUserActivity"
    android:theme="@style/Theme.RestaurantClient.Admin" />

<!-- Product Editor Dialog -->
<!-- Uses dialog, so no activity theme needed -->
```

---

##  Activity-Specific Themes

### In AndroidManifest.xml

####  Correct Theme Assignment

```xml
<application
    android:theme="@style/Theme.RestaurantClient">  <!-- Default for all -->
    
    <!-- Customer Activities - Use default -->
    <activity android:name=".MainActivity" />
    <activity android:name=".ui.auth.LoginActivity" />
    <activity android:name=".ui.product.ProductListActivity" />
    <activity android:name=".ui.cart.ShoppingCartActivity" />
    
    <!-- Admin Activities - Override with Admin theme -->
    <activity
        android:name=".ui.admin.AdminDashboardActivity"
        android:theme="@style/Theme.RestaurantClient.Admin" />
    
    <activity
        android:name=".ui.admin.UserManagementActivity"
        android:theme="@style/Theme.RestaurantClient.Admin" />
        
</application>
```

####  Wrong Theme Assignment

```xml
<!-- DON'T DO THIS -->
<activity
    android:name=".ui.product.ProductListActivity"
    android:theme="@style/Theme.RestaurantClient.Admin" />  <!--  Wrong! -->

<!-- DON'T DO THIS -->
<activity
    android:name=".ui.admin.AdminDashboardActivity"
    android:theme="@style/Theme.RestaurantClient" />  <!--  Wrong! -->

<!-- DON'T DO THIS -->
<activity
    android:name=".MainActivity"
    android:theme="@style/Base.Theme.RestaurantClient" />  <!--  Wrong! -->
```

---

##  Dialog Themes

### Custom Glass Dialogs

#### In Kotlin Code
```kotlin
// Use GlassDialogTheme for custom dialogs
class MyGlassDialog(context: Context) : Dialog(context, R.style.GlassDialogTheme) {
    // Dialog implementation
}

// Or with helper
context.showGlassConfirmDialog(
    title = "Confirm",
    message = "Are you sure?",
    onPositive = { /* action */ }
)
```

####  Correct Usage
```kotlin
// Glass dialogs
AlertDialog.Builder(context, R.style.GlassDialogTheme)
    .setView(dialogLayout)
    .create()
```

####  Wrong Usage
```kotlin
// DON'T use Theme.RestaurantClient for dialogs
AlertDialog.Builder(context, R.style.Theme.RestaurantClient)  //  Wrong!
    .setView(dialogLayout)
    .create()
```

---

##  Widget Styles

### Glass Components

#### Buttons
```xml
<!-- Glass filled button -->
<com.google.android.material.button.MaterialButton
    style="@style/GlassButton.Filled"
    android:text="Click Me" />

<!-- Glass primary button -->
<com.google.android.material.button.MaterialButton
    style="@style/GlassButton.Primary"
    android:text="Primary Action" />

<!-- Glass success button -->
<com.google.android.material.button.MaterialButton
    style="@style/GlassButton.Success"
    android:text="Save" />
```

#### Cards
```xml
<!-- Glass card with blur -->
<com.google.android.material.card.MaterialCardView
    style="@style/GlassCard"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">
    <!-- Content -->
</com.google.android.material.card.MaterialCardView>

<!-- Elevated glass card -->
<com.google.android.material.card.MaterialCardView
    style="@style/GlassCard.Elevated">
    <!-- Content -->
</com.google.android.material.card.MaterialCardView>
```

#### FABs
```xml
<!-- Glass FAB -->
<com.google.android.material.floatingactionbutton.FloatingActionButton
    style="@style/GlassFAB"
    app:srcCompat="@drawable/ic_add" />

<!-- Glass accent FAB -->
<com.google.android.material.floatingactionbutton.FloatingActionButton
    style="@style/GlassFAB.Accent"
    app:srcCompat="@drawable/ic_add" />
```

#### Admin Badge/Chip
```xml
<!-- Admin badge chip (for roles, status, etc.) -->
<com.google.android.material.chip.Chip
    style="@style/Widget.RestaurantClient.AdminBadge"
    android:text="ADMIN" />
```

#### Text Input
```xml
<!-- Glass text input -->
<com.google.android.material.textfield.TextInputLayout
    style="@style/GlassTextInputLayout"
    android:hint="Enter text">
    <com.google.android.material.textfield.TextInputEditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
</com.google.android.material.textfield.TextInputLayout>
```

---

##  Quick Reference Table

| Screen/Component | Theme to Use | Reason |
|-----------------|--------------|---------|
| **Customer Screens** | | |
| Login | `Theme.RestaurantClient` (default) | Customer-facing |
| Product List | `Theme.RestaurantClient` (default) | Customer-facing |
| Product Detail | `Theme.RestaurantClient` (default) | Customer-facing |
| Shopping Cart | `Theme.RestaurantClient` (default) | Customer-facing |
| Checkout | `Theme.RestaurantClient` (default) | Customer-facing |
| My Orders | `Theme.RestaurantClient` (default) | Customer-facing |
| User Profile | `Theme.RestaurantClient` (default) | Customer-facing |
| **Admin Screens** | | |
| Admin Dashboard | `Theme.RestaurantClient.Admin` | Admin functionality |
| User Management | `Theme.RestaurantClient.Admin` | Admin functionality |
| Order Management | `Theme.RestaurantClient.Admin` | Admin functionality |
| Create User | `Theme.RestaurantClient.Admin` | Admin functionality |
| **Dialogs** | | |
| Glass Confirm | `GlassDialogTheme` | Custom glass dialog |
| Glass Input | `GlassDialogTheme` | Custom glass dialog |
| Standard AlertDialog | Default or GlassDialogTheme | Depends on design |
| **DO NOT USE** | | |
| Base.Theme.RestaurantClient |  NEVER | Internal theme base only |
| AppCompat.* themes |  AVOID | Use Material3 instead |

---

##  Common Mistakes

### 1. Using Base Theme Directly
```xml
<!--  WRONG -->
<activity
    android:name=".MainActivity"
    android:theme="@style/Base.Theme.RestaurantClient" />

<!--  CORRECT -->
<activity
    android:name=".MainActivity"
    android:theme="@style/Theme.RestaurantClient" />
<!-- Or use default (no theme attribute) -->
<activity android:name=".MainActivity" />
```

### 2. Wrong Theme for Admin Screens
```xml
<!--  WRONG -->
<activity
    android:name=".ui.admin.AdminDashboardActivity" />
<!-- Uses customer theme by default -->

<!--  CORRECT -->
<activity
    android:name=".ui.admin.AdminDashboardActivity"
    android:theme="@style/Theme.RestaurantClient.Admin" />
```

### 3. Using AppCompat Themes
```xml
<!--  WRONG -->
<activity
    android:name=".ui.product.ProductListActivity"
    android:theme="@style/Theme.AppCompat.Light.NoActionBar" />

<!--  CORRECT -->
<activity
    android:name=".ui.product.ProductListActivity" />
<!-- Uses Theme.RestaurantClient by default -->
```

### 4. Mixing Admin and Customer Themes
```xml
<!--  WRONG - User profile with admin theme -->
<activity
    android:name=".ui.user.UserProfileActivity"
    android:theme="@style/Theme.RestaurantClient.Admin" />

<!--  CORRECT -->
<activity android:name=".ui.user.UserProfileActivity" />
```

### 5. Not Using GlassDialogTheme for Glass Dialogs
```kotlin
//  WRONG
val dialog = AlertDialog.Builder(context)
    .setView(glassDialogLayout)
    .create()

//  CORRECT
val dialog = AlertDialog.Builder(context, R.style.GlassDialogTheme)
    .setView(glassDialogLayout)
    .create()

//  BEST - Use helper
context.showGlassConfirmDialog(
    title = "Confirm",
    message = "Are you sure?",
    onPositive = { /* action */ }
)
```

---

##  How to Identify Which Theme to Use

### Step 1: Identify the Screen Type
**Question:** Is this screen for admin or customer?
- **Customer** → Use `Theme.RestaurantClient` (default)
- **Admin** → Use `Theme.RestaurantClient.Admin`

### Step 2: Check the Screen Purpose
**Customer Screens:**
- Browsing products → Default theme
- Making purchases → Default theme
- Viewing orders → Default theme
- Profile management → Default theme

**Admin Screens:**
- Managing users → Admin theme
- Managing products → Admin theme
- Managing orders → Admin theme
- Viewing analytics → Admin theme

### Step 3: For Dialogs
**Question:** Is it a custom glass dialog?
- **Yes** → Use `GlassDialogTheme`
- **No** → Use default dialog theme

---

##  Decision Tree

```
Is it a Dialog?
├─ YES → Use GlassDialogTheme (if glass effects needed)
└─ NO → Is it an Admin screen?
    ├─ YES → Use Theme.RestaurantClient.Admin
    └─ NO → Use Theme.RestaurantClient (default)
```

---

##  Implementation Checklist

When adding a new screen:

- [ ] Identify if it's customer or admin facing
- [ ] Customer screens: No theme needed (uses default)
- [ ] Admin screens: Add `android:theme="@style/Theme.RestaurantClient.Admin"`
- [ ] Dialogs with glass: Use `R.style.GlassDialogTheme`
- [ ] Test theme on both light and dark mode (if applicable)
- [ ] Verify status bar and navigation bar colors
- [ ] Check that glassmorphism effects work correctly

---

##  Related Documentation

- [GLASS_COMPONENTS.md](GLASS_COMPONENTS.md) - Glass component usage
- [UI_MODERNIZATION_PLAN.md](UI_MODERNIZATION_PLAN.md) - UI modernization details
- [Material Design 3 Guidelines](https://m3.material.io/)

---

##  Tips

1. **When in doubt, use the default theme** - `Theme.RestaurantClient` works for 90% of screens
2. **Admin theme is ONLY for admin screens** - Don't mix with customer screens
3. **Never use Base theme directly** - It's only a parent theme
4. **Glass dialogs need GlassDialogTheme** - For proper transparency
5. **Use style helpers** - Prefer `showGlassConfirmDialog()` over manual dialog creation

---

##  Emergency Reference

**"I don't know which theme to use!"**

1. Is it admin? → `Theme.RestaurantClient.Admin`
2. Is it a dialog? → `GlassDialogTheme`
3. Everything else → `Theme.RestaurantClient` (or nothing, it's default)

**"My screen looks wrong!"**

1. Check AndroidManifest.xml for theme assignment
2. Admin screens MUST have `android:theme="@style/Theme.RestaurantClient.Admin"`
3. Customer screens should NOT have explicit theme (uses default)
4. Dialogs should use `R.style.GlassDialogTheme` if using glass effects

---

**Document Version:** 1.0  
**Last Updated:** December 17, 2025 12:00 PM  
**Maintained By:** Development Team

_This guide should be updated whenever new themes are added or theme structure changes._
