# Jetpack Compose Gradient Animations 🎨

A collection of advanced, high-performance gradient animations implemented using **Jetpack Compose** and **Canvas API**. This project demonstrates how to create fluid, organic background effects and masking transitions using mathematical functions like parabolas and sine waves.

## ✨ Features

The project includes several unique gradient animation types, organized in a smooth **Vertical Pager** interface:

1.  **Rotating Linear Gradient** 🔄: A vibrant linear gradient that continuously rotates around its center, creating a dynamic color-shifting effect.
2.  **Vertical Moving Gradient** ↕️: A vertical gradient that flows smoothly up and down, using `TileMode.Repeated` for seamless looping and a blur effect for extra softness.
3.  **Parabola Wave Gradient** 🌊: A sophisticated effect combining a moving parabolic vertex with sine-wave interference, resulting in an organic, liquid-like motion.
4.  **Parabola Gradient Carousel** 🎢: A complex color transition where the next palette is revealed through a parabolic mask that sweeps across the screen with feathering and glow effects.

## 📱 Demo

*I will insert a video here*

## 🛠 Technical Highlights

-   **Canvas Drawing**: Leverages `drawWithCache` and `onDrawBehind` for efficient frame-by-frame drawing.
-   **Mathematical Shapes**: Uses parabolic functions ($y = vY + a \cdot dx^2$) and sine waves to drive visual boundaries.
-   **Advanced Masking**: Implements custom clipping (`clipPath`, `clipRect`) with manual feathering and glow simulations for soft edges.
-   **Smooth UI**: Utilizes Compose's `VerticalPager` with a custom dots indicator and an animated hint for better UX.
-   **State Management**: Uses `Animatable` and `InfiniteTransition` for synchronized and performant animations.

## 🚀 Getting Started

### Prerequisites

- Android Studio Meerkat (or newer)
- Kotlin 2.0+
- Jetpack Compose

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/Animations.git
   ```
2. Open the project in Android Studio.
3. Build and run on an emulator or a physical device.

## 📂 Project Structure

- `screens/`: Contains the main UI layout and Pager logic.
- `ui/gradient/`: Core implementation of individual gradient animations.
- `ui/theme/`: Project-wide styling and color configuration.

---

Developed with ❤️ by Gleb Poroshin.
