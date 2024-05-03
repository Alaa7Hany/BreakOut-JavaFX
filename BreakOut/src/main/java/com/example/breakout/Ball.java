package com.example.breakout;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 *
 * @author hp
 */
public class Ball {

    Circle circle = new Circle();
    private double radius = 10;
    private double speed = 0.5;
    private Color color = Color.DARKORANGE;

    public Ball() {
        circle.setRadius(radius);
        circle.setFill(color);
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
        circle.setRadius(radius);
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
        circle.setFill(color);
    }

    public double getXPos() {
        return circle.getCenterX();
    }
    public double getYPos() {
        return circle.getCenterY();
    }
}
