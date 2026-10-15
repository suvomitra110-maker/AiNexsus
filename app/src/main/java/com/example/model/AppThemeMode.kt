package com.example.model

enum class AppThemeMode(val title: String, val subtitle: String, val iconEmoji: String) {
    SYSTEM("Auto", "Follows device system settings", "📱"),
    DARK("Late-Night Dark", "Deep indigo & OLED black for late study", "🌙"),
    LIGHT("Daylight Clean", "Crisp high-contrast daylight mode", "☀️")
}
