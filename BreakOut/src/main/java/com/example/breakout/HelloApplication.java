package com.example.breakout;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 *
 * @author hp
 */
public class HelloApplication extends Application {

    // All the variables , objects and nodes used in the game

    final int ROWS1 = 5;
    final int COLUMNS1 = 9;
    final int ROWS2 = 7;
    final int COLUMNS2 = 11;
    final int HEIGHT = 800;
    final int WIDTH = 1200;
    boolean moveFlag = true, up = true, right = true, powerFlag = true, levelDone = false, widend = false, shrinked = false, enlarged = false, speeding = false, paused = false;
    int score = 0, bricksNumber = 0, lives = 3, widining = 100, enlarging = 10, level = 1, randomPower;
    double speedingValue = 0.3;

    Timeline gamePlay, falling, gameOver, moveRight, moveLeft;
    PauseTransition pausePower = new PauseTransition(Duration.seconds(10));

    Paddle paddle = new Paddle();
    Brick[][] bricks;
    Pane brickPane = new Pane();
    BorderPane menuPane = new BorderPane();
    BorderPane root = new BorderPane();
    BorderPane bottomPane = new BorderPane();
    Scene scene = new Scene(root, WIDTH, HEIGHT);
    Scene menuScene = new Scene(menuPane, WIDTH, HEIGHT);
    Stage gameOverStage = new Stage();
    Button btnRestart = new Button("Restart");
    Ball ball = new Ball();
    double radius = ball.getRadius();

    Rectangle rectBack = new Rectangle(0, 0, WIDTH, HEIGHT - 70);
    Rectangle rectScore = new Rectangle(0, HEIGHT - 70, WIDTH, HEIGHT);
    Rectangle rectPowerUp = new Rectangle();

    Label labelScore = new Label("Score: " + score);
    Label labelLevel = new Label("Level: " + level);
    GridPane livesPane = new GridPane();
    Label winLabel, nextLevelLabel;
    VBox winVBox = new VBox(50);

    double ballSpeed = ball.getSpeed();
    double xChange = ballSpeed;
    double yChange = ballSpeed;
    double cx = ball.circle.getCenterX();
    double cy = ball.circle.getCenterY();

    @Override
    public void start(Stage primaryStage) {
        // creating the start menu
        createStartMenu();

        // starting the game on any key pressed
        menuScene.setOnKeyPressed(e -> {
            primaryStage.setScene(scene);
            gamePlay.play();
            gameOver.play();

        });

        // the bottom pane which contains score , level and lives
        createBottomPane();

        // adding the bricks
        layBricks();

        // centering the ball and the paddle
        centering();

        // adding everything to the main pane (root)
        root.getChildren().addAll(rectBack, rectScore, ball.circle,
                paddle.rect);
        root.setTop(brickPane);
        root.setBottom(bottomPane);

        // paddle movement
        movePaddle();

        // gameplay timeline
        createGamePlayTimeLine();

        // what appears after finishing the level
        createWinVBox();

        // gameOver window
        createGameOverWindow();

        // game over timeline
        createGameOverTimeLine();

        primaryStage.setTitle("Breakout");
        primaryStage.setScene(menuScene);

        // remove the maximizing button
        primaryStage.maximizedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                primaryStage.setMaximized(false);
            }
        });
        primaryStage.show();
    }

    void createStartMenu() {
        Label menuLabel = new Label("BREAKOUT");
        menuLabel.setTextFill(Color.RED);
        menuLabel.setFont(new Font(100));
        menuLabel.setAlignment(Pos.CENTER);

        // background for the start menu
        Rectangle menuRect = new Rectangle(WIDTH, HEIGHT, Color.BLACK);
        menuRect.setStroke(Color.BROWN);
        menuRect.setStrokeWidth(20);

        Label menuLabel1Start = new Label("Press any key to start");
        menuLabel1Start.setTextFill(Color.WHITE);
        menuLabel1Start.setFont(new Font(30));
        menuLabel1Start.setTranslateY(-40);

        // السطر ده معناه ان النود هتكون في المكان الي احنا محددينه لما نضيفها ل بوردر باين
        /////////////////////////////////////
        BorderPane.setAlignment(menuLabel1Start, Pos.BOTTOM_CENTER);
        ////////////////////////////////////

        // add everything to our main pane
        menuPane.getChildren().add(menuRect);
        menuPane.setCenter(menuLabel);
        menuPane.setBottom(menuLabel1Start);

        // the background for the game
        rectBack.setFill(Color.BLACK);
        rectBack.setStroke(Color.BROWN);
        rectBack.setStrokeWidth(5);
    }

    void createBottomPane() {
        // the bottom pane which contains score , level and lives
        // هنا هنعمل باين نحط فيها السكور عالشمال و الليفل في النص و الأرواح عاليمين

        labelScore.setTextFill(Color.RED);
        labelScore.setFont(new Font(30));
        labelLevel.setTextFill(Color.RED);
        labelLevel.setFont(new Font(30));

        // adding the lives shapes
        for (int i = 0; i < lives; i++) {
            Life life = new Life();
            livesPane.add(life.circle, i, 0);
        }

        livesPane.setAlignment(Pos.CENTER);
        livesPane.setHgap(10);
        bottomPane.setPadding(new Insets(10));
        bottomPane.setLeft(labelScore);
        bottomPane.setRight(livesPane);
        bottomPane.setCenter(labelLevel);
    }

    void layBricks() {
        // resetting the score
        bricksNumber = 0;

        // in case of restart to remove all bricks
        brickPane.getChildren().clear();
        try {
            for (int i = 0; i < ROWS2; i++) {
                for (int j = 0; j < COLUMNS2; j++) {
                    bricks[i][j] = null;
                }
            }
        } catch (Exception e) {
        }

        // عندنا هنا بيتحدد شكل و توزيع الطوب عالشاشة علي أساس الليفل
        // adding the bricks for level 1
        if (level == 1) {

            // بعمل أراي حجمها بيبقي علي حسب عدد صفوف و عدد عواميد الليفل
            bricks = new Brick[ROWS1][COLUMNS1];

            for (int i = 0; i < ROWS1; i++) {
                for (int j = 0; j < COLUMNS1; j++) {
                    Brick brick;
                    // defining the type of the brick
                    /* في عندنا تلت أنواع من الطوب بيتم تحديد النوع علي حسب الرقم الي ببعته لل كونستراكتور
                       1 -> يتكسر من ضربة واحدة
                       2 -> يتكسر من ضربتين
                      -1 -> مابيتكسرش
                     */
                    if (i == 2) {
                        brick = new Brick(2);
                    } else {
                        brick = new Brick(1);
                    }

                    // putting the bricks in position
                    // و بعد ما نحدد نوع الطوب الي احنا عايزينه بنرصه علي الشاشة ونضيفهم لل باين
                    bricks[i][j] = brick;
                    brickPane.getChildren().add(brick.rect);
                    brick.setxPos(150 + (brick.getWidth() + 20) * j);
                    brick.setyPos(60 + (brick.getHeight() + 20) * i);

                    // كل مانضيف طوبة بنزود عدد الطوب علشان نعرف نحسب السكور
                    bricksNumber++;
                }
            }
        }
        // adding the bricks for level 2
        else if (level == 2) {
            bricks = new Brick[ROWS2][COLUMNS2];

            for (int i = 0; i < ROWS2; i++) {
                for (int j = 0; j < COLUMNS2; j++) {
                    Brick brick;

                    // defining the type of the brick
                    if ((j == 1 || j == COLUMNS2 - 2) && i != 0 && i != ROWS2 - 1) {
                        brick = new Brick(-1);
                        bricksNumber--;
                    } else if (i == 0 || i == 3 || i == ROWS2 - 1) {
                        brick = new Brick(2);
                    } else {
                        brick = new Brick(1);
                    }

                    // putting the bricks in position
                    bricks[i][j] = brick;
                    brickPane.getChildren().add(brick.rect);
                    brick.setxPos(60 + (brick.getWidth() + 20) * j);
                    brick.setyPos(50 + (brick.getHeight() + 20) * i);
                    bricksNumber++;
                }
            }
        }
    }

    void centering() {
        ball.circle.setCenterX(WIDTH / 2);
        ball.circle.setCenterY(HEIGHT-140);
        // resetting the ball direction
        // دايما الكورة لما تبدأ تتحرك هتبقي لفوق و يمين او شمال
        up = true;
        right = (Math.random() < 0.5);

        paddle.rect.setX(WIDTH / 2 - paddle.getWidth() / 2);
        paddle.rect.setY(HEIGHT-120);
        moveFlag = true;
    }

    void movePaddle() {

        /* الفكرة هنا ان احنا عندنا اتنين تايم لاين واحد للحركة اليمين و واحد للحركة الشمال
            لما اللاعب يضغط عالسهم اليمين هيشغل التايم لاين اليمين
            و لما يشيل صباعه من عالسهم اليمين التايم لاين اليمين هيقف
            و هكذا مع الشمال
            و ضيفنا هنا خاصية انه يوقف اللعبة و انه ينتقل لليفل تاني بعد مايخلص الليفل
        */

        // set timelines for moving left and right to provide a smooth movement
        moveRight = new Timeline(new KeyFrame(Duration.millis(1), e -> {
            if (moveFlag && paddle.getxPos() < WIDTH - paddle.getWidth()) {
                paddle.moveRight();
            }
        }));
        moveRight.setCycleCount(Timeline.INDEFINITE);

        moveLeft = new Timeline(new KeyFrame(Duration.millis(1), e -> {
            if (moveFlag && paddle.getxPos() > 0) {
                paddle.moveLeft();
            }
        }));
        moveLeft.setCycleCount(Timeline.INDEFINITE);

        // paddle movement when using the keys and pausing the game and moving to another level
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.RIGHT && moveFlag) {
                moveRight.play();
            }
            if (event.getCode() == KeyCode.LEFT && moveFlag) {
                moveLeft.play();
            }
            // pausing the game on pressing escape
            if (event.getCode() == KeyCode.ESCAPE && !paused) {
                gamePlay.pause();
                gameOver.pause();
                try {
                    falling.pause();
                } catch (Exception e) {
                }
                moveFlag = false;
                paused = true;
                moveLeft.stop();
                moveRight.stop();
            } else if (event.getCode() == KeyCode.ESCAPE && paused) {
                gamePlay.play();
                gameOver.play();
                try {
                    falling.play();
                } catch (Exception e) {
                }
                moveFlag = true;
                paused = false;
                moveLeft.stop();
                moveRight.stop();
            }
            // لما يضغط انتر بعد ما يخلص الليفل هياخده لليفل تاني
            if (event.getCode() == KeyCode.ENTER && levelDone) {
                level++;
                restart();
            }
        });

        // stop the movement when releasing the keys
        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.RIGHT && moveFlag) {
                moveRight.stop();
            }
            if (event.getCode() == KeyCode.LEFT && moveFlag) {
                moveLeft.stop();
            }
        });
    }

    void moveBall() {
        // updating the y and x changes
        xChange = ballSpeed;
        yChange = ballSpeed;

        // لو الحركة ناحية اليمين هيزود الاكس و لو ناحية الشمال هيقللها
        if (right) {
            ball.circle.setCenterX(cx + xChange);
        } else {
            ball.circle.setCenterX(cx - xChange);
        }
        // لو الحركة لفوق هيقلل الواي و لو بتحت هيزودها
        if (up) {
            ball.circle.setCenterY(cy - yChange);
        } else {
            ball.circle.setCenterY(cy + yChange);
        }

    }

    void powerUp(int randomPower, Brick b) {

        // بنعمل مستطيل للخاصية
        rectPowerUp = new Rectangle();
        rectPowerUp.setX(b.getxPos() + b.getWidth() / 2);
        rectPowerUp.setY(b.getyPos() + b.getHeight());

        // shape of the powerup depending on the random number
        createShapeOfPowerUp(randomPower);

        // adding the powerup to the screen
        root.getChildren().add(rectPowerUp);

        // making sure that there is only one powerup appears on the screen
        powerFlag = false;

        //  a timer for removing the powerup after 10 sec
        pausePower.setOnFinished(e -> {
            removePowerUp();
        });

        // a timeline for falling of the powerup
        falling = new Timeline(new KeyFrame(Duration.millis(1), e -> {

            // moving the powerup on the screeen
            rectPowerUp.setY(rectPowerUp.getY() + 0.25);

            // activate the powerup when touching the paddle
            if (paddle.rect.getBoundsInParent().intersects(rectPowerUp.getBoundsInParent())) {
                // removing the powerup shape off the screen
                root.getChildren().remove(rectPowerUp);

                // adding the power up depending on the random number
                addPowerUp(randomPower);

                falling.stop();
            }
            // removing the powerup shape on falling off the screen
            if (rectPowerUp.getY() > rectBack.getY() + rectBack.getHeight()) {
                root.getChildren().remove(rectPowerUp);
                // allowing another powerup to appear
                powerFlag = true;
                falling.stop();
            }
        }));
        falling.setCycleCount(Timeline.INDEFINITE);
        falling.play();

    }

    void createShapeOfPowerUp(int randomPower) {
        if (randomPower < 2) {
            // widning shape
            rectPowerUp.setWidth(70);
            rectPowerUp.setHeight(10);
            rectPowerUp.setFill(Color.GREEN);
            rectPowerUp.setArcWidth(5);
            rectPowerUp.setArcHeight(5);
        } else if (randomPower < 4) {
            // shrinking shape
            rectPowerUp.setWidth(40);
            rectPowerUp.setHeight(10);
            rectPowerUp.setFill(Color.RED);
            rectPowerUp.setArcWidth(5);
            rectPowerUp.setArcHeight(5);
        } else if (randomPower < 6) {
            // enlarging the ball shape
            rectPowerUp.setWidth(40);
            rectPowerUp.setHeight(40);
            rectPowerUp.setArcWidth(40);
            rectPowerUp.setArcHeight(40);
            rectPowerUp.setFill(Color.CYAN);
            rectPowerUp.setStroke(Color.BLUE);
            rectPowerUp.setStrokeWidth(3);
        } else if (randomPower < 8) {
            // increasing the speed shape
            rectPowerUp.setWidth(20);
            rectPowerUp.setHeight(20);
            rectPowerUp.setArcWidth(20);
            rectPowerUp.setArcHeight(20);
            rectPowerUp.setFill(Color.RED);
        }
    }

    void addPowerUp(int randomPower) {
        if (randomPower < 2) {
            // increase the width of the paddle
            paddle.setWidth(paddle.getWidth() + widining);
            widend = true;
        } else if (randomPower < 4) {
            // decrease the width of th paddle
            paddle.setWidth(paddle.getWidth() - widining);
            shrinked = true;
        } else if (randomPower < 6) {
            // increase the size of the ball
            ball.setRadius(ball.getRadius() + enlarging);
            enlarged = true;
        } else if (randomPower < 8) {
            // increase the speed of the ball
            ballSpeed += speedingValue;
            speeding = true;
        }
        // start the timer to remove the powerup after 10 seconds
        pausePower.play();
    }

    void removePowerUp() {
        if (widend) {
            paddle.setWidth(paddle.getWidth() - widining);
            widend = false;
        } else if (shrinked) {
            paddle.setWidth(paddle.getWidth() + widining);
            shrinked = false;
        } else if (enlarged) {
            ball.setRadius(ball.getRadius() - enlarging);
            enlarged = false;
        } else if (speeding) {
            ballSpeed -= speedingValue;
            speeding = false;
        }
        // allowing another powerup to appear
        powerFlag = true;
    }

    void wallCollision() {
        // التصادم مع الحيطة اليمين او الشمال هيغير اتجاهها في ناحية الإكس
        // 3 is a safety measure
        if ((cx <= radius + 3 && !right) || (cx >= WIDTH - radius - 3 && right)) {
            right = !right;
        }
        // التصادم مع الحيطة الي فوق
        if (cy <= radius + 3 && up) {
            up = !up;
        }
        // for testing purposes
//        if (cy >= 900 - radius) {
//            up = !up;
//        }

    }

    void paddleCollision() {
        // لو خبط في العصاية العصاية يبقي هيغير اتجاهها لفوق
        if ((paddle.rect.getBoundsInParent().intersects(ball.circle.getBoundsInParent()))
                && !up) {
            up = !up;

            // inverting in x direction
            // بنغير اتجاه الإكس علي حسب خبط في أي نص من العصاية
            if (cx <= paddle.getxPos() + paddle.getWidth() / 2 - radius && right) {
                right = !right;
            } else if (cx >= paddle.getxPos() + paddle.getWidth() / 2 + radius && !right) {
                right = !right;
            }
        }
    }

    void brickCollision() {
        // هنلف علي كل طوبة و نشوف لو تصادمت مع الكورة
        for (int i = 0; i < bricks.length; i++) {
            for (int j = 0; j < bricks[0].length; j++) {
                Brick b = bricks[i][j];
                // removing the brick
                try {
                    //collision with the brick
                    if (ball.circle.getBoundsInParent().intersects(b.rect.getBoundsInParent())) {

                        // بنباصيلها الطوبة الي خبطت فيها الكورة ومكانها في الأراي
                        removeBrick(b, i, j);

                        //inverting the movement
                        // لو الكورة خبطت في الجنب اليمين او الشمال هتغير اتجاهها في الإكس
                        // غير كده هتغير اتجاهها في الواي
                        if ((cy > b.getyPos() - radius + 1) && (cy < b.getyPos() + b.getHeight() + radius - 1)) {
                            if (cx <= b.getxPos() + b.getWidth() / 2 - radius && right) {
                                right = !right;
                            } else if (cx >= b.getxPos() + b.getWidth() / 2 + radius && !right) {
                                right = !right;
                            }
                        } else {
                            up = !up;
                        }
                        break;
                    }
                } catch (Exception exception) {
                }
            }
        }
    }

    void removeBrick(Brick b, int i, int j) {

        /* لو الطوبة بتتكسر من ضربة واحدة بيتم ازالتها وبيتم اضافة فرصة لخاصية انها تظهر
            لو بتتكسر من ضربتين بيتم تحويلها لطوبة تتكسر من ضربة واحدة
        */
        // check whether it's one or two hits
        if (b.getHit() == 1) {
            // remove the brick from both the array and the pane
            brickPane.getChildren().remove(b.rect);
            bricks[i][j] = null;

            // updating the score
            score += 10;
            labelScore.setText("Score: " + score);

            // chance for a power up
            randomPower = (int) (Math.random() * 10);
            if (randomPower <= 7 && powerFlag) {
                // بنبعتلها الطوبة علشان نحدد مكان نزول الخاصية
                // و بيتبعتلها الرقم الي علي أساسه بنحدد نوع الخاصية
                powerUp(randomPower, b);
            }
        } else if (b.getHit() == 2) {
            b.changeBrick();
        }
    }

    void createGamePlayTimeLine() {
        /* هنا يقا التايم لاين الي بيحصل فيه كل حاجة في اللعبة
             حركة الكورة وتصادمها مع الطوب و الحيطة و العصاية
        */

        // creating the timeline for the gameplay
        gamePlay = new Timeline(new KeyFrame(Duration.millis(1), e -> {

            // updating the rشdius in case of enlarging
            radius = ball.getRadius();

            // updating the cx and cy
            cx = ball.circle.getCenterX();
            cy = ball.circle.getCenterY();

            // moving the ball
            moveBall();

            // updating cx and cy after moving the ball
            cx = ball.circle.getCenterX();
            cy = ball.circle.getCenterY();

            // collision with walls
            wallCollision();

            // collision with the paddle
            paddleCollision();

            // removing the bricks when touched , updating the score and chance for powerup
            brickCollision();
        }));
        gamePlay.setCycleCount(Timeline.INDEFINITE);
    }

    void createGameOverTimeLine() {
        /* هنا هيكون تايم لاين شغال في نفس الوقت الي اللعبة شغالة فيها و بيراقب
          كل ما الكورة تقع عالأرض اللاعب هيخسر روح و هنعيد مكان الكورة والعصاية لمكانهم الأصلي
           لو عدد أرواح اللاعب خلص هيوقف حركة العصاية و حركة الكورة و يظهرله شاشة النهاية
          و في حالة ان اللاعب كسر كل الطوب هيظهرله ال باين الخاصة بالفوز ويوقف حركة الكورة و العصاية
        */
        gameOver = new Timeline(new KeyFrame(Duration.millis(1), e -> {
            // when the ball fell off the screen
            if (ball.circle.getCenterY() >= rectBack.getHeight() - radius) {
                gamePlay.stop();
                lives--;
                // stopping movement of the paddle
                moveRight.stop();
                moveLeft.stop();
                // recentering the paddle and the ball
                centering();
                try {
                    livesPane.getChildren().remove(lives - 1);
                } catch (Exception ex) {
                }
                gamePlay.play();
            }
            // after losing all lives
            if (lives == 0) {
                gamePlay.stop();
                try {
                    livesPane.getChildren().remove(0);
                } catch (Exception ex) {
                }
                moveFlag = false;
                // stopping the powerup timer
                pausePower.stop();
                // show the game over window
                gameOverStage.show();
                gameOver.stop();
            }
            // if the player break all the bricks he wins
            if (score == bricksNumber * 10) {
                endLevel();
            }
        }));
        gameOver.setCycleCount(Timeline.INDEFINITE);
    }

    void endLevel() {
        gamePlay.stop();
        try {
            falling.stop();
        } catch (Exception e) {
        }
        moveFlag = false;
        if (level == 1) {
            levelDone = true;
        }
        root.setCenter(winVBox);
        nextLevelLabel.setText("Press enter to move to LEVEL " + (level + 1));
        gameOver.stop();
    }

    void createWinVBox() {
        winLabel = new Label("YOU WIN!");
        winLabel.setTextFill(Color.SPRINGGREEN);
        winLabel.setFont(new Font(200));
        winVBox.getChildren().add(winLabel);
        nextLevelLabel = new Label("Press enter to move to LEVEL " + (level + 1));
        nextLevelLabel.setTextFill(Color.WHITE);
        nextLevelLabel.setFont(new Font(30));
        winVBox.getChildren().add(nextLevelLabel);
        winVBox.setAlignment(Pos.CENTER);
        winVBox.setTranslateY(-50);
    }

    void createGameOverWindow() {

        //  النافذة الي هتظهر في حالة اللاعب خسر كل أرواحه و هيختار اما يعيد الدور او يخرج

        Label overLabel = new Label("GAME OVER");
        overLabel.setTextFill(Color.RED);
        overLabel.setFont(new Font(50));
        btnRestart.setFont(new Font(20));
        btnRestart.setTextFill(Color.WHITE);

        // set the color of the button background
        btnRestart.setStyle("-fx-background-color: #A52A2A;");

        Button btnExit = new Button("Exit");
        btnExit.setFont(new Font(20));
        btnExit.setTextFill(Color.WHITE);
        btnExit.setStyle("-fx-background-color: #A52A2A;");

        HBox overHBox = new HBox(10, btnRestart, btnExit);
        overHBox.setAlignment(Pos.CENTER);
        overHBox.setTranslateY(-20);

        Rectangle overRect = new Rectangle(500, 300, Color.BLACK);
        overRect.setStroke(Color.BROWN);
        overRect.setStrokeWidth(10);
        overRect.setArcWidth(30);
        overRect.setArcHeight(30);

        BorderPane overPane = new BorderPane();
        overPane.getChildren().addAll(overRect);
        overPane.setCenter(overLabel);
        overPane.setBottom(overHBox);

        Scene overScene = new Scene(overPane, 500, 300);
        gameOverStage.setScene(overScene);
        gameOverStage.setTitle("Game over");
        gameOverStage.maximizedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                gameOverStage.setMaximized(false);
            }
        });
        // remove the top bar
        gameOverStage.initStyle(StageStyle.UNDECORATED);
        // restarting the game
        btnRestart.setOnAction(e -> {
            restart();
        });
        // closing the game
        btnExit.setOnAction(e -> {
            Platform.exit();
        });
    }

    void restart() {


        levelDone = false;
        root.getChildren().remove(winVBox);

        // reset the bricks
        layBricks();

        // reset the circle and paddle
        centering();

        // reset the powerups
        try {
            // removing any powerup on the screen
            root.getChildren().remove(rectPowerUp);
        } catch (Exception ex) {
        }
        // removing any powerup with the player
        removePowerUp();

        // reset the score
        score = 0;
        lives = 3;
        labelScore.setText("Score: " + score);
        labelLevel.setText("Level: " + level);

        // reset lives
        livesPane.getChildren().clear();
        for (int i = 0; i < lives; i++) {
            Life life = new Life();
            livesPane.add(life.circle, i, 0);
        }

        gameOverStage.close();
        gamePlay.play();
        gameOver.play();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
