package com.tonyjs.flappybird;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by tonysaavedra on 6/21/16.
 */
public class FlappyBird extends Application {
    private int APP_HEIGHT = 700;
    private int APP_WIDTH = 400;
    private int TOTAL_SCORE = 0;
    private long spaceClickA;
    private double motionTime, elapsedTime;
    private boolean CLICKED, GAME_START, HIT_PIPE, GAME_OVER;
    private LongValue startNanoTime;
    private Sprite firstFloor, secondFloor, birdSprite;
    private Bird bird;
    private Text scoreLabel;
    private GraphicsContext gc;
    private AnimationTimer timer;
    private ArrayList<Pipe> pipes;
    private Sound coin, hit, wing, swoosh, die;
    private ImageView gameOver, startGame;
    private Group root;

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
                setOnUserInput();
            }
        });

        scene.setOnMousePressed(e -> {
            setOnUserInput();
        });
    }

    private void setOnUserInput() {
        if (!HIT_PIPE) {
            CLICKED = true;
            if (!GAME_START) {
                root.getChildren().remove(startGame);
                swoosh.playClip();
                GAME_START = true;
            } else {
                wing.playClip();
                spaceClickA = System.currentTimeMillis();
                birdSprite.setVelocity(0, -250);
            }
        }
        if (GAME_OVER) {
            startNewGame();
        }
    }

    private Parent getContent() {
        root = new Group();
        Canvas canvas = new Canvas(APP_WIDTH, APP_HEIGHT);
        gc = canvas.getGraphicsContext2D();

        ImageView bg = setBackground();
        setFloor();
        pipes = new ArrayList<>();
        setPipes();
        setBird();
        setLabels();
        setSounds();

        root.getChildren().addAll(bg, canvas, scoreLabel, startGame);
        return root;
    }

    private ImageView setBackground() {
        Random random = new Random();
        int bg = random.nextInt(2);
        String filePath = bg > 0 ? "/images/background.png" : "/images/background_night.png";
        ImageView imageView = new ImageView(new Image(getClass().getResource(filePath).toExternalForm()));
        imageView.setFitWidth(APP_WIDTH);
        imageView.setFitHeight(APP_HEIGHT);
        return imageView;
    }

    private void setLabels() {
        scoreLabel = new Text("0");
        scoreLabel.setFont(Font.font("Courier", FontWeight.EXTRA_BOLD, 50));
        scoreLabel.setStroke(Color.BLACK);
        scoreLabel.setFill(Color.WHITE);
        scoreLabel.setLayoutX(20);
        scoreLabel.setLayoutY(40);

        gameOver = new ImageView(new Image(getClass().getResource("/images/game_over.png").toExternalForm()));
        gameOver.setFitWidth(178);
        gameOver.setFitHeight(50);
        gameOver.setLayoutX(110);
        gameOver.setLayoutY(100);

        startGame = new ImageView(new Image(getClass().getResource("/images/ready.png").toExternalForm()));
        startGame.setFitWidth(178);
        startGame.setFitHeight(50);
        startGame.setLayoutX(100);
        startGame.setLayoutY(100);
    }

    private void setSounds() {
        coin = new Sound("/sounds/score.mp3");
        hit = new Sound("/sounds/hit.mp3");
        wing = new Sound("/sounds/wing.mp3");
        swoosh = new Sound("/sounds/swoosh.mp3");
        die = new Sound("/sounds/die.mp3");
    }

    private void setBird() {
        bird = new Bird();
        birdSprite = bird.getBird();
        birdSprite.render(gc);
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

                if (GAME_START) {
                    renderPipes();
                    checkPipeScroll();
                    updateTotalScore();

                    if (birdHitPipe()) {
                        root.getChildren().add(gameOver);
                        stopScroll();
                        playHitSound();
                        motionTime += 0.18;
                        if (motionTime > 0.5) {
                            birdSprite.addVelocity(0, 400);
                            birdSprite.render(gc);
                            birdSprite.update(elapsedTime);
                            motionTime = 0;
                        }
                    }

                    if (birdHitFloor()) {
                        timer.stop();
                        GAME_OVER = true;
                        die.playClip();
                    }
                }
            }
        };
        timer.start();
    }

    private void startNewGame() {
        root.getChildren().remove(gameOver);
        root.getChildren().add(startGame);
        pipes.clear();
        setFloor();
        setPipes();
        setBird();
        resetVariables();
        startGame();
    }

    private void resetVariables() {
        updateScoreLabel(0);
        TOTAL_SCORE = 0;
        HIT_PIPE = false;
        CLICKED = false;
        GAME_OVER = false;
        GAME_START = false;
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

    private void updateTotalScore() {
        if (!HIT_PIPE) {
            for (Pipe pipe : pipes) {
                if (pipe.getPipe().getPositionX() == birdSprite.getPositionX()) {
                    updateScoreLabel(++TOTAL_SCORE);
                    coin.playClip();
                    break;
                }
            }
        }
    }

    private void updateScoreLabel(int score) {
        scoreLabel.setText(Integer.toString(score));
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

    private boolean birdHitPipe() {
        for (Pipe pipe : pipes) {
            if (!HIT_PIPE && birdSprite.intersectsSprite(pipe.getPipe())) {
                HIT_PIPE = true;
                showHitEffect();
                return true;
            }
        }
        return false;
    }

    private void showHitEffect() {
        ParallelTransition parallelTransition = new ParallelTransition();
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.10), root);
        fadeTransition.setToValue(0);
        fadeTransition.setCycleCount(2);
        fadeTransition.setAutoReverse(true);
        parallelTransition.getChildren().add(fadeTransition);
        parallelTransition.play();
    }

    private void playHitSound() {
        if (HIT_PIPE) {
            hit.playClip();
        }
    }

    private boolean birdHitFloor() {
        return birdSprite.intersectsSprite(firstFloor) ||
                birdSprite.intersectsSprite(secondFloor) ||
                birdSprite.getPositionX() < 0;
    }

    private void stopScroll() {
        for (Pipe pipe : pipes) {
            pipe.getPipe().setVelocity(0, 0);
        }
        firstFloor.setVelocity(0, 0);
        secondFloor.setVelocity(0, 0);
    }

    private void checkPipeScroll() {
        if (pipes.size() > 0) {
            Sprite p = pipes.get(pipes.size() - 1).getPipe();
            if (p.getPositionX() == APP_WIDTH / 2 - 80) {
                setPipes();
            } else if (p.getPositionX() <= -p.getWidth()) {
                pipes.remove(0);
                pipes.remove(0);
            }
        }
    }

    private void setPipes() {
        int height = getRandomPipeHeight();

        Pipe pipe = new Pipe(true, height);
        Pipe downPipe = new Pipe(false, 425 - height);

        pipe.getPipe().setVelocity(-.4, 0);
        downPipe.getPipe().setVelocity(-.4, 0);

        pipe.getPipe().render(gc);
        downPipe.getPipe().render(gc);

        pipes.addAll(Arrays.asList(pipe, downPipe));
    }

    private int getRandomPipeHeight() {
        return (int) (Math.random() * (410 - 25)) + 25;
    }

    private void renderPipes() {
        for (Pipe pipe : pipes) {
            Sprite p = pipe.getPipe();
            p.render(gc);
            p.update(5);
        }
    }

    public class LongValue {
        public long value;

        public LongValue(long i) {
            this.value = i;
        }
    }
}
