package com.tonyjs.flappybird;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by tonysaavedra on 6/22/16.
 */
public class Bird {
    private Sprite bird;
    private ArrayList<Sprite> flight = new ArrayList<>();
    private int currentBird = 0;
    private double locationX = 100;
    private double locationY = 200;
    private int BIRD_WIDTH = 50;
    private int BIRD_HEIGHT = 45;

    public Bird() {
        bird = new Sprite();
        bird.resizeImage("/images/bird1.png", BIRD_WIDTH, BIRD_HEIGHT);
        bird.setPositionXY(locationX, locationY);
        setFlightAnimation();
    }

    public void setFlightAnimation() {
        Sprite bird2 = new Sprite();
        bird2.resizeImage("/images/bird2.png", BIRD_WIDTH, BIRD_HEIGHT);
        bird2.setPositionXY(locationX, locationY);

        Sprite bird3 = new Sprite();
        bird3.resizeImage("/images/bird1.png", BIRD_WIDTH, BIRD_HEIGHT);
        bird3.setPositionXY(locationX, locationY);

        Sprite bird4 = new Sprite();
        bird4.resizeImage("/images/bird3.png", BIRD_WIDTH, BIRD_HEIGHT);
        bird4.setPositionXY(locationX, locationY);

        flight.addAll(Arrays.asList(bird, bird2, bird3, bird4));
    }

    public Sprite getBird() {
        return bird;
    }

    public Sprite animate() {
        if (currentBird == flight.size() - 1) {
            currentBird = 0;
        }

        return flight.get(currentBird++);
    }
}
