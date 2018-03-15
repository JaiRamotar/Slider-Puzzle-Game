import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SliderPuzzle extends Application {
    //Button array, start button, blank button
    Button[][] buttons;
    Button startButton;
    Button blankButton;

    //Image array, blank image
    Image[][] images;
    Image blank = new Image(getClass().getResourceAsStream("BLANK.png"));

    //Array of points of each button and array of the order that the points of each button should be in when the puzzle is solved
    Point2D[][] currentPoints;
    Point2D[][] orderedPoints;

    //Listview with puzzle names
    ListView<String> lv;

    //Row and column of blank button
    int blankRow;
    int blankColumn;

    //Timer
    TextField time;
    Timeline updateTimer;
    int minutes;
    int seconds;

    //Thumbnail previewing selected puzzle
    Label thumbnail = new Label();

    public void start(Stage primaryStage) {
        //Pane to store all components
        Pane aPane = new Pane();

        //Buttons and images array
        buttons = new Button[4][4];
        images = new Image[4][4];

        //currentPoints and orderedPoints arrays
        currentPoints = new Point2D[4][4];
        orderedPoints = new Point2D[4][4];

        //Thumbnail
        thumbnail.setPrefSize(187, 187);
        thumbnail.relocate(770, 10);
        thumbnail.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("BLANK.png"))));

        //Start button
        startButton = new Button("Start");
        startButton.setStyle("-fx-font: 12 arial; -fx-base: DARKGREEN; " + "-fx-text-fill: rgb(255,255,255);");
        startButton.relocate(770, 358);
        startButton.setPrefSize(190, 35);
        startButton.setDisable(true);

        //ListView
        lv = new ListView<String>();
        lv.setItems(FXCollections.observableArrayList("Pets", "Scenery", "Lego", "Numbers"));
        lv.setPrefSize(190, 140);
        lv.relocate(770, 208);

        //Timer label and text
        Label timeLabel = new Label("Time: ");
        timeLabel.relocate(770, 410);
        time = new TextField("0:00");
        time.setPrefSize(123, 20);
        time.relocate(837, 407);
        time.setEditable(false);

        //Make button grid
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                buttons[i][j] = new Button();
                orderedPoints[i][j] = new Point2D(i, j);
                buttons[i][j].relocate(10 + 188 * i, 10 + 188 * j);
                buttons[i][j].setPrefSize(187, 187);
                buttons[i][j].setPadding(new Insets(0, 0, 0, 0));
                images[i][j] = blank;

                buttons[i][j].setGraphic(new ImageView(images[i][j]));

                aPane.getChildren().add(buttons[i][j]);

            }
        }

        //Timer
        updateTimer = new Timeline(new KeyFrame(Duration.millis(1000), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                seconds++;
                if (seconds == 60) {
                    minutes++;
                    seconds = 0;
                }
                time.setText(String.format("%d:%02d", minutes, seconds));
            }
        }));
        updateTimer.setCycleCount(Timeline.INDEFINITE);

        //ListView click
        lv.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                startButton.setDisable(false);
                thumbnail.setGraphic(new ImageView(new Image(getClass().getResourceAsStream(lv.getSelectionModel().getSelectedItem() + "_Thumbnail.png"))));
            }

        });
        //Start Button Click
        startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if (startButton.getStyle() == "-fx-font: 12 arial; -fx-base: DARKGREEN; " + "-fx-text-fill: rgb(255,255,255);") {

                    minutes = 0;
                    seconds = 0;
                    time.setText("0:00");
                    updateTimer.play();
                    startButton.setStyle("-fx-font: 12 arial; -fx-base: DARKRED; " + "-fx-text-fill: rgb(255,255,255);");
                    startButton.setText("Stop");
                    thumbnail.setDisable(true);
                    lv.setDisable(true);
                    //Sets images of button grid
                    for (int i = 0; i < 4; i++) {
                        for (int j = 0; j < 4; j++) {
                            buttons[i][j].setDisable(false);
                            images[i][j] = new Image(getClass().getResourceAsStream(lv.getSelectionModel().getSelectedItem() + "_" + j + i + ".png"));
                            buttons[i][j].setGraphic(new ImageView(images[i][j]));
                            currentPoints[i][j] = new Point2D(i, j);
                        }
                    }
                    //Sets random button as the blank button
                    blankRow = (int) (Math.random() * 4);
                    blankColumn = (int) (Math.random() * 4);
                    images[blankRow][blankColumn] = blank;
                    buttons[blankRow][blankColumn].setGraphic(new ImageView(images[blankRow][blankColumn]));
                    blankButton = buttons[blankRow][blankColumn];
                    //Shuffles board to start the game
                    for (int i = 0; i < 5000; i++) {
                        int pos1 = (int) (Math.random() * 4);
                        int pos2 = (int) (Math.random() * 4);

                        swap(pos1, pos2);
                    }
                    //Checks if the puzzle is already solved as soon as it starts
                    isWinner(currentPoints, orderedPoints);
                }
                //Stop button click
                else if (startButton.getStyle() == "-fx-font: 12 arial; -fx-base: DARKRED; " + "-fx-text-fill: rgb(255,255,255);") {
                    updateTimer.stop();
                    startButton.setStyle("-fx-font: 12 arial; -fx-base: DARKGREEN; " + "-fx-text-fill: rgb(255,255,255);");
                    startButton.setText("Start");
                    thumbnail.setDisable(false);
                    lv.setDisable(false);

                    for (int i = 0; i < 4; i++) {
                        for (int j = 0; j < 4; j++) {
                            images[i][j] = new Image(getClass().getResourceAsStream("BLANK.png"));
                            buttons[i][j].setGraphic(new ImageView(images[i][j]));
                        }
                    }

                }
            }
        });

        //Array of buttons click
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                buttons[i][j].setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        for (int row = 0; row < 4; row++) {
                            for (int col = 0; col < 4; col++) {
                                if (event.getSource() == buttons[row][col] && event.getSource() != blankButton) {
                                    swap(row, col);
                                    isWinner(currentPoints, orderedPoints);
                                }
                            }
                        }
                    }
                });
            }
        }

        aPane.getChildren().addAll(thumbnail, lv, startButton, timeLabel, time);

        Scene scene = new Scene(aPane, 960, 760); // Set window size
        primaryStage.setTitle("Slider Puzzle Game"); // Set window title
        primaryStage.setResizable(false); //Set window to not be resizeable
        primaryStage.setScene(scene); //Show scene
        primaryStage.show(); // Show window
    }

    //Swaps blank image with clicked image and shuffles images at the beginning of the game
    public void swap(int row, int col) {
        Node oldGraphic = buttons[row][col].getGraphic();
        double oldX;
        double oldY;
        double oldBlankX;
        double oldBlankY;

        int xValue = Math.abs(row - blankRow);
        int yValue = Math.abs(col - blankColumn);

        if (xValue + yValue == 1) {
            buttons[row][col].setGraphic(blankButton.getGraphic());
            blankButton.setGraphic((oldGraphic));

            oldX = currentPoints[row][col].getX();
            oldY = currentPoints[row][col].getY();

            oldBlankX = currentPoints[blankRow][blankColumn].getX();
            oldBlankY = currentPoints[blankRow][blankColumn].getY();

            currentPoints[row][col] = new Point2D(oldBlankX, oldBlankY);
            currentPoints[blankRow][blankColumn] = new Point2D(oldX, oldY);

            blankRow = row;
            blankColumn = col;

            blankButton = buttons[blankRow][blankColumn];

        }
    }

    //Determines if the game has been completed
    public void isWinner(Point2D[][] currentGrid, Point2D[][] orderedGrid) {
        int counter = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {

                if ((currentGrid[i][j].equals(orderedGrid[i][j]))) {
                    counter += 1;
                }
            }
        }

        //The game has been completed, resets the board
        if (counter == 16) {
            updateTimer.stop();
            startButton.setStyle("-fx-font: 12 arial; -fx-base: DARKGREEN; " + "-fx-text-fill: rgb(255,255,255);");
            startButton.setText("Start");
            thumbnail.setDisable(false);
            lv.setDisable(false);

            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    buttons[i][j].setDisable(true);
                }
            }
            buttons[blankRow][blankColumn].setGraphic(new ImageView(new Image(getClass().getResourceAsStream(lv.getSelectionModel().getSelectedItem() + "_" + blankColumn + blankRow + ".png"))));
        }
    }

    public static void main(String[] args) {
        launch(args); // Initialize/start
    }
}
