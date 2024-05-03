package com.example.breakout;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author hp
 */
public class Brick {
    Rectangle rect = new Rectangle();
    private double height = 30;
    private double width = 80;
    final private Color oneHit = Color.PINK;
    final private Color twoHit = Color.DEEPPINK;
    final private Color noHit = Color.DIMGRAY;
    private int hit = 0;

    public Brick(int x){

        if (x == 1) {
            rect.setFill(oneHit);
            hit = 1;
        }
        else if (x == 2){
            rect.setFill(twoHit);
            hit = 2;
        }else if (x == -1){
            rect.setFill(noHit);
            hit = -1;
        }
        rect.setHeight(height);
        rect.setWidth(width);
        rect.setStroke(Color.WHITE);
        rect.setStrokeWidth(3);
    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setWidth(double width) {
        this.width = width;

    }

    public void setxPos(double xPos){
        rect.setX(xPos);
    }

    public void setyPos(double yPos){
        rect.setY(yPos);
    }

    public double getxPos() {
        return rect.getX();
    }

    public double getyPos() {
        return rect.getY();
    }

    public int getHit() {
        return hit;
    }

    public Color getOneHit() {
        return oneHit;
    }

    public Color getTwoHit() {
        return twoHit;
    }

    public void changeBrick(){
        rect.setFill(oneHit);
        hit = 1;
    }
}