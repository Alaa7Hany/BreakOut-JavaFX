package com.example.breakout;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author hp
 */
public class Paddle{

    Rectangle rect = new Rectangle();

    private double height = 20;
    private double width = 200;
    private double speed = 1;
    private Color color = Color.WHITE;


    public Paddle() {
        rect.setHeight(height);
        rect.setWidth(width);
        rect.setFill(color);
        rect.setArcWidth(20);
        rect.setArcHeight(20);
    }

    public double getHight() {
        return height;
    }

    public double getWidth() {
        return width;
    }

    public double getxPos() {
        return rect.getX();
    }

    public double getyPos(){
        return rect.getY();
    }

    public void setHight(double hight) {
        this.height = hight;
    }

    public void setWidth(double width) {
        this.width = width;
        rect.setWidth(width);
    }

    public void moveRight() {
        rect.setX(getxPos()+speed);
    }

    public void moveLeft() {
        rect.setX(getxPos()-speed);

    }

    public Color getColor() {
        return color;
    }


}