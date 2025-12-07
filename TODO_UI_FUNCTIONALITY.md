# Orderly App - UI & Functionality TODO List

This document outlines a list of potential UI/UX improvements and additional functionalities for the Orderly Android application. This list aims to enhance the user experience and expand the app's capabilities.

## 1. UI/UX Enhancements

### 1.1 General
-   [ ] **Custom Icons:** Replace all default Android drawable icons (e.g., `ic_menu_search`, `ic_menu_agenda`, `ic_menu_close_clear_cancel`) with custom, branded icons for a consistent and professional look.
-   [ ] **Material 3 Theming:** Further refine the app's theme using a custom Material You color palette, dynamic coloring, and adaptive layouts where appropriate.
-   [ ] **Animations & Transitions:** Implement smooth animations for screen transitions, item additions/removals, and loading states to improve perceived performance and user delight.
-   [ ] **Empty States:** Design and implement user-friendly empty states for `Product List` (if no products), `Shopping Cart` (if empty), and `My Orders` (if no orders).

### 1.2 Product List Screen (`activity_product_list.xml`, `item_product.xml`)
-   [ ] **Image Loading:** Use a dedicated image loading library (e.g., Glide, Coil) with proper placeholders and error handling for product images.
-   [ ] **Badge on Cart Icon:** Dynamically update the badge on the shopping cart icon in the `Top App Bar` to reflect the current number of items in the cart.

### 1.3 Product Detail Screen (`activity_product_detail.xml`)
-   [ ] **Image Carousel:** If a product has multiple images, implement an image carousel or gallery.
-   [ ] **Quantity Selector Feedback:** Provide visual feedback when quantity changes (e.g., slight animation or haptic feedback).
-   [ ] **Add to Cart Confirmation:** Enhance the "Add to Cart" confirmation (e.g., a more prominent Snackbar or a brief animation on the cart icon).

### 1.4 Shopping Cart Screen (`activity_shopping_cart.xml`, `item_cart.xml`)
-   [ ] **Item Removal Animation:** Animate item removal from the cart for a smoother experience.
-   [ ] **Quantity Update Feedback:** Provide immediate visual update when quantities are changed.
-   [ ] **Subtotal Breakdown:** Display a clear breakdown of subtotal, taxes (if applicable), and total.

### 1.5 Checkout Screen (`activity_checkout.xml`)
-   [ ] **Order Confirmation/Success Screen:** After a successful order placement, navigate to a dedicated "Thank You" or order confirmation screen with order details and options (e.g., "View Orders," "Continue Shopping").
-   [ ] **Address/Payment Selection:** If applicable, implement UI for selecting/adding shipping addresses and payment methods. (Requires backend support).

## 2. Additional Functionalities

### 2.1 Navigation & State Management
-   [ ] **Implement Navigation:** Wire up actual `Intents` to navigate between all activities, especially for menu items (Profile, My Orders, Cart) and button actions (Add to Cart, Proceed to Checkout, Place Order).
-   [ ] **Cart Logic:** Implement the full client-side logic for adding, updating, and removing items from the shopping cart. This includes managing cart state across the application (e.g., using a Singleton or a dedicated ViewModel for the cart).
-   [ ] **Login Navigation:** Ensure successful login navigates directly to the `ProductListActivity`.

### 2.2 API Integration
-   [ ] **Product Search:** Implement the search functionality in the `Product List` screen to filter products based on user input.
-   [ ] **Product Sorting/Filtering:** Implement options to sort and filter products (e.g., by price, category).
-   [ ] **Real Order Creation:** Integrate the `Create Order` functionality with the actual backend API.
-   [ ] **User Profile Update:** (If applicable) Implement functionality to update user profile details.

### 2.3 Error Handling & Feedback
-   [ ] **User-Friendly Error Messages:** Replace generic `Toast` messages with more informative and actionable error messages, potentially using `Snackbar` or custom dialogs.
-   [ ] **Loading Indicators:** Show appropriate loading indicators (e.g., `ProgressBar`, skeleton screens) during network operations and data fetching.
-   [ ] **Retry Mechanisms:** Implement retry options for failed network requests.
