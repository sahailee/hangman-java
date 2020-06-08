import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Hangman extends Application {
    private static final int GUESSESALLOWED = 10;
    static int remainingGuesses = GUESSESALLOWED;
    private static List<Image> figureStages = new ArrayList<>();
    private static Stage primaryStage;

    private static HangmanGame hangmanGame;
    @Override
    public void start(Stage primaryStage) throws Exception {
        Hangman.primaryStage = primaryStage;
        hangmanGame = new HangmanGame(primaryStage);
        primaryStage.setScene(hangmanGame.getScene());
        primaryStage.setTitle("Hangman");
        loadFigureImages();
        primaryStage.show();

    }
    public static void main(String[] args){
        Application.launch(args);

    }

    public static final int getGuessesAllowed() { return GUESSESALLOWED; }

    /**
     * Alert with one button that just closes the alert.
     * @param message is the message to be displayed to user.
     */
    public static void displayGameOverAlert(String message) {
        Stage alert = new Stage();
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(primaryStage);
        alert.setTitle("Game Over");
        Label label = new Label(message);
        Button close = new Button("CLOSE");
        close.setOnMousePressed(event -> alert.close());
        VBox vBox = new VBox(label, close);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(5);
        Scene scene = new Scene(vBox, 300,300);
        label.setFont(new Font(scene.getWidth() * (1.5 / message.length())));
        alert.setScene(scene);
        alert.show();
    }

    /**
     * Three button alert, with the third button just closing the alert.
     * @param message   to be displayed to the user.
     * @param yesAction what to do when first button is pressed
     * @param noAction  what to do when second button is pressed.
     */
    public static void displayConfirmationAlert(String message, Button yesAction,
                                                Button noAction) {
        Stage alert = new Stage();
        Label label = new Label(message);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(primaryStage);
        Button yesButton = new Button("Yes");
        Button noButton = new Button("No");
        Button cancelButton = new Button("Cancel");
        yesButton.setOnMousePressed(event -> {
            alert.close();
            yesAction.fire();
            if(!hangmanGame.isGameModified()) {
                noAction.fire();
            }

        });
        noButton.setOnMousePressed(event -> {
            alert.close();
            noAction.fire();
        });
        cancelButton.setOnMousePressed(event -> {
            alert.close();
        });
        HBox hBox = new HBox(yesButton, noButton, cancelButton);
        VBox vBox = new VBox(label, hBox);
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(5);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(5);
        Scene scene = new Scene(vBox, 400,350);
        alert.setScene(scene);
        alert.show();
    }

    /**
     * Adds every image of the hangman in order to the figureStages list.
     */
    private static void loadFigureImages() {
        for(int i = 0; i < 10; i++) {
            figureStages.add(new Image(("hangman_" + i + ".png")));
        }
    }

    /**
     * Updates the hangman drawing to the argument.
     * @param imageView
     */
    static void drawNext(ImageView imageView) {
        int index = GUESSESALLOWED - remainingGuesses - 1;
        if (index >= 0) { //Can easily get the next drawing by accessing from the list.
            imageView.setImage(figureStages.get(GUESSESALLOWED - remainingGuesses - 1));
        }
        else {
            imageView = new ImageView(); //Guess hasn't been wrong so display nothing
        }
    }


}
