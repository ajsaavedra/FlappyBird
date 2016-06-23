package com.tonyjs.flappybird;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

/**
 * Created by tonysaavedra on 6/21/16.
 */
public class FlappyBird extends Application {
    private int APP_HEIGHT = 700;
    private int APP_WIDTH = 400;
    private long spaceClickA;
    private double motionTime, elapsedTime;
    private boolean CLICKED;
    private LongValue startNanoTime;
    private Sprite firstFloor, secondFloor, birdSprite;
    private Bird bird;
    private GraphicsContext gc;
    private AnimationTimer timer;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Flappy Bird");
        primaryStage.setResizable(false);

        Parent root = getContent();
        Scene main = new Scene(root, APP_WIDTH, APP_HEIGHT);
        setKeyFunctions(main);
        primaryStage.setScene(main);
        primaryStage.show();
        startGame();
    }

    private void setKeyFunctions(Scene scene) {
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                CLICKED = true;
                spaceClickA = System.currentTimeMillis();
                birdSprite.setVelocity(0, -250);
            }
        });
    }

    public Parent getContent() {
        Group root = new Group();
        Canvas canvas = new Canvas(APP_WIDTH, APP_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        ImageView bg = setBackground();
        setFloor();
        bird = new Bird();
        birdSprite = bird.getBird();
        root.getChildren().addAll(bg, canvas);
        return root;
    }

    private ImageView setBackground() {
        ImageView imageView = new ImageView(new Image(getClass().getResource("/images/background.png").toExternalForm()));
        imageView.setFitWidth(APP_WIDTH);
        imageView.setFitHeight(APP_HEIGHT);
        return imageView;
    }

    private void setFloor() {
        firstFloor = new Sprite();
        firstFloor.resizeImage("/images/floor.png", 400, 140);
        firstFloor.setPositionXY(0, APP_HEIGHT - 100);
        firstFloor.setVelocity(-.4, 0);
        firstFloor.render(gc);

        secondFloor = new Sprite();
        secondFloor.resizeImage("/images/floor.png", 400, 140);
        secondFloor.setPositionXY(firstFloor.getWidth(), APP_HEIGHT - 100);
        secondFloor.setVelocity(-.4, 0);
        secondFloor.render(gc);
    }

    private void startGame() {
        startNanoTime = new LongValue(System.nanoTime());

        timer = new AnimationTimer() {
            public void handle(long now) {
                elapsedTime = (now - startNanoTime.value) / 1000000000.0;
                startNanoTime.value = now;

                gc.clearRect(0, 0, APP_WIDTH, APP_HEIGHT);
                moveFloor();
                checkTimeBetweenSpaceHits();

                if (birdSprite.intersectsSprite(firstFloor) ||
                        birdSprite.intersectsSprite(secondFloor)) {
                    timer.stop();
                }
            }
        };
        timer.start();
    }

    private void checkTimeBetweenSpaceHits() {
        long difference = (System.currentTimeMillis() - spaceClickA) / 300;

        if (difference >= .001 && CLICKED) {
            CLICKED = false;
            birdSprite.addVelocity(0, 800);
            birdSprite.render(gc);
            birdSprite.update(elapsedTime);
        } else {
            animateBird();
        }
    }

    private void moveFloor() {
        firstFloor.render(gc);
        secondFloor.render(gc);
        firstFloor.update(5);
        secondFloor.update(5);
        if (firstFloor.getPositionX() <= -APP_WIDTH) {
            firstFloor.setPositionXY(secondFloor.getPositionX() + secondFloor.getWidth(),
                    APP_HEIGHT - 100);
        } else if (secondFloor.getPositionX() <= -APP_WIDTH) {
            secondFloor.setPositionXY(firstFloor.getPositionX() + firstFloor.getWidth(),
                    APP_HEIGHT - 100);
        }
    }

    private void animateBird() {
        birdSprite.render(gc);
        birdSprite.update(elapsedTime);

        motionTime += 0.18;
        if (motionTime > 0.5 && CLICKED) {
            Sprite temp = birdSprite;
            birdSprite = bird.animate();
            birdSprite.setPositionXY(temp.getPositionX(), temp.getPositionY());
            birdSprite.setVelocity(temp.getVelocityX(), temp.getVelocityY());
            motionTime = 0;
        }
    }

    public class LongValue {
        public long value;

        public LongValue(long i) {
            this.value = i;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
