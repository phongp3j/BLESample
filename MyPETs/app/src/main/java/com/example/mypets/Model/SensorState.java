package com.example.mypets.Model;

import android.graphics.Color;

public enum SensorState {
    CONNECTING("#CDDC39"),   // Màu vàng (Yellow)
    CONNECTED("#4CAF50"),    // Màu xanh lá cây (Green)
    DISCONNECTED("#F44336"); // Màu đỏ (Red)

    private final String hexColor;

    SensorState(String hexColor) {
        this.hexColor = hexColor;
    }

    // Phương thức trả về mã màu dạng hex
    public String getHexColor() {
        return hexColor;
    }

    // Phương thức trả về giá trị màu để dùng cho TextView
    public int getColor() {
        return Color.parseColor(hexColor);
    }
}
