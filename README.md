# 🛍️ Clot - E-Commerce App

A modern Android e-commerce application built with **Jetpack Compose**, featuring product browsing, shopping cart management, wishlist functionality, and Firebase integration.

---

## ✨ Features

### 🛒 Product Catalog
- Fetch and display products using Retrofit and a mock API
- Grid/List product browsing experience
- Category-based UI

### 📦 Product Details
- Image carousel for product previews
- Size and color selection
- Dynamic pricing with INR conversion
- Add to Cart & Wishlist functionality

### 🛍️ Cart Management
- Add/remove products from cart
- Quantity adjustment
- Automatic subtotal, tax, and shipping fee calculations

### ❤️ Wishlist System
- Save favorite products
- Toggle wishlist items dynamically
- Persistent wishlist using Firebase

### 📍 Address Management
- Add/Edit/Delete shipping addresses
- Set default delivery address
- Firestore integration for user-specific addresses

### 🎨 Modern UI
- Built completely with Jetpack Compose
- Material 3 design system
- Responsive layouts and smooth animations
- Dark & Light theme support

---

# 🛠 Tech Stack

| Category | Technology |
|----------|-------------|
| Language | Kotlin |
| UI | Jetpack Compose |
| Architecture | MVVM |
| Dependency Injection | Hilt |
| Networking | Retrofit + Gson |
| Async Programming | Kotlin Coroutines & Flow |
| Image Loading | Coil |
| Authentication | Firebase Auth |
| Database | Firebase Firestore & Realtime Database |
| Local Storage | DataStore Preferences |
| Navigation | Compose Navigation |

---

# 📂 Project Structure

```bash
com.example.internshipminiproject
├── data
│   ├── model        # Data classes (Product, User, CartItem, etc.)
│   └── remote       # API services and networking
├── viewmodel        # Business logic and state management
├── Screen           # UI screens (Cart, Checkout, ProductDetails)
└── ui.theme         # Material 3 themes, colors, typography
```

---

# ⚙️ Installation

## 1️⃣ Clone the Repository

```bash
git clone https://github.com/Subrat-Senapati/Clot.git
```

## 2️⃣ Open in Android Studio

- Use **Android Studio Ladybug** or newer

## 3️⃣ Add Firebase Configuration

- Download `google-services.json`
- Place it inside:

```bash
app/google-services.json
```

## 4️⃣ Sync Gradle

- Sync the project with Gradle files

## 5️⃣ Run the App

- Use Emulator or Physical Device
- Minimum SDK: **24**

---

# 🔥 Firebase Features Used

- Firebase Authentication
- Firestore Database
- Realtime Database

---

# 🧠 Architecture

This project follows the **MVVM Architecture Pattern**:

```text
UI (Compose Screens)
       ↓
ViewModel
       ↓
Repository
       ↓
Remote API / Firebase
```

---

# 🚀 Future Improvements

- Payment Gateway Integration
- Order Tracking
- Push Notifications
- Product Search & Filters
- Admin Dashboard
- Offline Caching

---

# 👨‍💻 Author

### Subrat Senapati

- GitHub: https://github.com/Subrat-Senapati

---

# 📄 License

This project is licensed under the MIT License.
