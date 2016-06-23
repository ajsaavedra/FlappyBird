package com.tonyjs.flappybird;

/**
 * Created by tonysaavedra on 6/22/16.
 */
public class Pipe {
    private Sprite pipe;
    private double locationX;
    private double locationY;
    private double height;
    private double width;

    public Pipe(boolean isFaceUp, int height) {
        this.pipe = new Sprite();
        this.pipe.resizeImage(isFaceUp ? "/images/up_pipe.png" : "/images/down_pipe.png", 70, height);
        this.width = 70;
        this.height = height;
        this.locationX = 400;
        this.locationY = isFaceUp? 600 - height : 0;
        this.pipe.setPositionXY(locationX, locationY);
    }

    public Sprite getPipe() {
        return pipe;
    }
}
