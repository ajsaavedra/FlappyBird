package com.tonyjs.flappybird;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Created by tonysaavedra on 6/23/16.
 */
public class MainApp extends Application {
    public static boolean GAME_SET;

    private Parent createContent() {
        Pane root = new Pane();

        ImageView title = new ImageView(new Image(getClass().getResource("/images/title.png").toExternalForm()));
        title.setFitWidth(178);
        title.setFitHeight(50);
        title.setLayoutX(200);
        title.setLayoutY(50);

        ImageView flappyBird = new ImageView(new Image(getClass().getResource("/images/bird2.png").toExternalForm()));
        flappyBird.setFitWidth(50);
        flappyBird.setFitHeight(45);
        flappyBird.setLayoutX(260);
        flappyBird.setLayoutY(120);

        Rectangle bg = new Rectangle(600, 300);
        bg.setFill(Color.rgb(78,192,202));

        Text inst = new Text("Use the Space Bar or\nMouse Click to Start");
        inst.setFont(Font.font("Courier", FontWeight.EXTRA_BOLD, 25));
        inst.setFill(Color.WHITE);
        inst.setStroke(Color.BLACK);
        inst.setLayoutX(140);
        inst.setLayoutY(230);

        root.getChildren().addAll(bg, title, flappyBird, inst);
        return root;
    }

    public void startGame() {
        if (!GAME_SET) {
            GAME_SET = true;
            FlappyBird game = new FlappyBird();
            Stage st = new Stage();
            try {
                game.start(st);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createContent());
        scene.setOnKeyPressed(e -> {
            startGame();
        });
        scene.setOnMousePressed(e -> {
            startGame();
        });
        primaryStage.setTitle("Flappy Bird");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}