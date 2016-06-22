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
import javafx.stage.Stage;

/**
 * Created by tonysaavedra on 6/21/16.
 */
public class FlappyBird extends Application {
    private int APP_HEIGHT = 700;
    private int APP_WIDTH = 400;
    private long startNanoTime, elapsedTime;
    private Sprite firstFloor, secondFloor;
    private GraphicsContext gc;
    private AnimationTimer timer;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Flappy Bird");
        primaryStage.setResizable(false);

        Parent root = getContent();
        Scene main = new Scene(root, APP_WIDTH, APP_HEIGHT);

        primaryStage.setScene(main);
        primaryStage.show();
        startGame();
    }

    public Parent getContent() {
        Group root = new Group();
        Canvas canvas = new Canvas(APP_WIDTH, APP_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        ImageView bg = setBackground();
        setFloor();
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
        firstFloor.resizeImage("/images/floor.png");
        firstFloor.setPositionXY(0, APP_HEIGHT - 100);
        firstFloor.setVelocityX(-.5, 0);
        firstFloor.render(gc);

        secondFloor = new Sprite();
        secondFloor.resizeImage("/images/floor.png");
        secondFloor.setPositionXY(firstFloor.getWidth(), APP_HEIGHT - 100);
        secondFloor.setVelocityX(-.5, 0);
        secondFloor.render(gc);
    }

    private void startGame() {
        startNanoTime = System.nanoTime();

        timer = new AnimationTimer() {
            public void handle(long now) {
                elapsedTime = (long) ((now - startNanoTime) / 1000000000.0);
                gc.clearRect(0, 0, APP_WIDTH, APP_HEIGHT);
                moveFloor();
            }
        };
        timer.start();
    }

    private void moveFloor() {
        firstFloor.render(gc);
        secondFloor.render(gc);
        firstFloor.update(5);
        secondFloor.update(5);
        if (firstFloor.getPositionX() <= -APP_WIDTH + 10) {
            firstFloor.setPositionXY(secondFloor.getPositionX() + secondFloor.getWidth(),
                    APP_HEIGHT - 100);
        } else if (secondFloor.getPositionX() <= -APP_WIDTH + 10) {
            secondFloor.setPositionXY(firstFloor.getPositionX() + firstFloor.getWidth(),
                    APP_HEIGHT - 100);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
