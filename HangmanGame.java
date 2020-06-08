import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HangmanGame {
    private static ImageView saveIcon = new ImageView(new Image("Save.png"));
    private static ImageView newIcon = new ImageView(new Image("New.png"));
    private static ImageView loadIcon = new ImageView(new Image("Load.png"));
    private static ImageView exitIcon = new ImageView(new Image("Exit.png"));
    private static Button newButton = new Button("New", newIcon);
    private static Button loadButton = new Button("Load", loadIcon);
    private static Button saveButton = new Button("Save", saveIcon);
    private static Button exitButton = new Button("Exit", exitIcon);
    private static Button startButton = new Button("Start Playing");
    private static ToolBar toolBar = new ToolBar(newButton, loadButton, saveButton, exitButton);
    private static BorderPane borderPane = new BorderPane();
    private static final int letterButtonWidth = 60;
    private static final int letterButtonHeight = 60;
    private WordChooser wordChooser = new WordChooser();
    private Label remainingGuessesLabel = new Label();
    private Scene scene;
    private List<Integer> guessedLetters;
    private boolean isGameModified;
    private List<Button> letterButtons;
    private VBox vBox;
    private ImageView figure;

    public HangmanGame(Stage primaryStage) {
        vBox = new VBox();
        figure = new ImageView();
        setupBorderPane();
        scene = new Scene(borderPane, 1200, 1200);
        setupButtons(primaryStage);
        guessedLetters = new ArrayList<>();
        letterButtons = new ArrayList<>();
        isGameModified = false;
        remainingGuessesLabel.setFont(new Font("Andale Mono", 20));

    }

    public Scene getScene() { return this.scene; }

    public boolean isGameModified() {
        return isGameModified;
    }

    private void setupBorderPane() {
        toolBar.setOrientation(Orientation.HORIZONTAL);
        toolBar.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        borderPane.setTop(toolBar);
        StackPane stackPane = new StackPane(startButton);
        stackPane.setBackground(new Background(new BackgroundFill(Color.GRAY, new CornerRadii(3), Insets.EMPTY)));
        stackPane.setPrefHeight(45);
        borderPane.setBottom(stackPane);
    }

    /**
     * This method setups the tool bar buttons and start playing button.
     * It also sets up the red x button on the top of the window to ask the user to save the game before exiting just
     * like the toolbar's exit button.
     * Also, the method writes an event listener for the screen's width. If the screen is ever resized the nodes will
     * adjust their sizes accordingly.
     * @param primaryStage is used to show the file chooser window
     */
    private void setupButtons(Stage primaryStage) {
        saveButton.setDisable(true);
        startButton.setDisable(true);
        startButton.setVisible(false);
        setupExitButton();
        setupNewButton();
        setupSaveButton(primaryStage);
        setupLoadButton(primaryStage);
        setupStartButton();
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            exitButton.fire();
        });
        //Setting up a listener to listen for any changes in screen size.
        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            double screenWidth = primaryStage.getWidth();
            for (LetterLabel l: wordChooser.getLetterLabelsList()) {
                l.setLabelWidth((screenWidth / 3) / wordChooser.getChosenWord().length()); //For every black box we will set its width.
            }
            vBox.setPrefWidth(screenWidth / 2); //Set the black boxes, and letter button table to take up half the screen.
            figure.setFitWidth(screenWidth / 2);  //Set stick figure image to take up the other half of the screen
            figure.setFitHeight(screenWidth / 2);
            for(Button button: letterButtons) {
                button.setPrefWidth((screenWidth / 2) / 7); //For each button in the letter button table give it an equal
                button.setPrefHeight((screenWidth / 2) / 7); //size that all buttons together will have width of half the screen.
            }
        });
    }

    private void setupNewButton() {
        Button temp = new Button(); //A temporary button to hold the actual action of the new button
        temp.setOnAction(event -> { //This is because we want to throw a popup if the game is modified and then run
            this.clearScene();      //the new action or immediately run the action if game not modified.
            startButton.setDisable(false);
            startButton.setVisible(true);
        });
        newButton.setOnMousePressed(e -> {
            if(isGameModified) {
                Hangman.displayConfirmationAlert("Would you like to save the game before starting a new game?", saveButton, temp);
            }
            else {
                temp.fire();
            }
        });
    }

    private void setupExitButton() {
        exitButton.setOnAction(e -> {
            Button temp = new Button();
            temp.setOnAction(event -> {
                Platform.exit();
                System.exit(0);
            });
            if(isGameModified) {
                Hangman.displayConfirmationAlert("Would you like to save the game before closing?", saveButton, temp);
            }
            else {
                temp.fire();
            }
        });
    }

    private void setupSaveButton(Stage primaryStage) {
        saveButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Hangman Game");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Hangman Saves", "*.hng"));
            try {
                File selectedFile = fileChooser.showSaveDialog(primaryStage);
                PrintWriter output = new PrintWriter(selectedFile);
                output.println(wordChooser.getChosenWord());
                output.println(wordChooser.getChosenWord().length() - wordChooser.getUnguessedLetters());
                for (LetterLabel l: wordChooser.getLetterLabelsList()) {
                    if(l.wasGuessed()) {
                        output.println(l.getLetter());
                    }
                }
                output.println(Hangman.getGuessesAllowed() - Hangman.remainingGuesses);
                output.println(guessedLetters.size());
                for (Integer index: guessedLetters) {
                    output.println(index);
                }
                this.isGameModified = false;
                saveButton.setDisable(true);
                output.close();
            } catch (FileNotFoundException e) {
            } catch (NullPointerException e) {
            }
        });
    }

    /**
     * Just like the new button, uses a temp button to hold the actual loading action. The exit button fires temp.
     * @param primaryStage is used to show the file chooser window
     */
    private void setupLoadButton(Stage primaryStage) {
        Button temp = new Button();
        temp.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load Hangman Game");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Hangman Saves", "*.hng"));
            try {
                File selectedFile = fileChooser.showOpenDialog(primaryStage);
                Scanner reader = new Scanner(selectedFile);
                this.clearScene();
                wordChooser.setChosenWord(reader.nextLine());
                int numGuesses = reader.nextInt();
                reader.nextLine();
                for(int i = 0; i < numGuesses; i++) {
                    String letter = reader.nextLine();
                    wordChooser.checkGuessCorrect(letter);
                    this.guessedLetters.add(letter.charAt(0) - 65);
                }
                numGuesses = reader.nextInt();
                int guessedLettersSize = reader.nextInt();
                for(int i = 0; i < guessedLettersSize; i++) {
                    this.guessedLetters.add(reader.nextInt());
                }
                reader.close();
                Hangman.remainingGuesses = Hangman.getGuessesAllowed() - numGuesses;
                Hangman.drawNext(figure);
                borderPane.setCenter(figure);
                this.displayWordBoxAndLetters();
                startButton.setDisable(true);
            } catch (FileNotFoundException e) {
            } catch (NullPointerException e) {
            }
        });
        loadButton.setOnMousePressed(e -> {
            if(isGameModified) {
                Hangman.displayConfirmationAlert("Would you like to save the game before loading a new game?", saveButton, temp);
            }
            else {
                temp.fire();
            }
        });
    }

    /**
     * Adding functionality to start playing button, if there is no words.txt in directory, it will show a popup.
     */
    private void setupStartButton() {
        startButton.setOnAction(e -> {
            startButton.setDisable(true);
            Hangman.remainingGuesses = Hangman.getGuessesAllowed();
            try {
                this.clearScene();
                wordChooser.setListOfWords();
                wordChooser.setRandomWord();
                this.displayWordBoxAndLetters();
            } catch(FileNotFoundException fnf) {
                Hangman.displayGameOverAlert("Error: no 'words.txt' detected.");
            }catch (NullPointerException ex) {
                Hangman.displayGameOverAlert("Error: no 'words.txt' detected.");
            }
        });
    }

    /**
     * Disables all buttons besides tool bar buttons excluding save.
     * And removes all labels and images.
     */
    private void clearScene() {
        wordChooser.clear();
        this.guessedLetters.clear();
        borderPane.setRight(new VBox());
        borderPane.setCenter(new ImageView());
        this.isGameModified = false;
        saveButton.setDisable(true);
        disableAllLetterButtons();
    }

    /**
     * Puts the table of letters on screen as well as the black boxes for the word to be guessed.
     */
    private void displayWordBoxAndLetters() {
        remainingGuessesLabel.setText("Remaining guesses: " + Hangman.remainingGuesses);
        vBox = new VBox(remainingGuessesLabel, wordChooser.getLetterLabels(), createLetterButtons());
        setupKeyInput();
        vBox.setPadding(new Insets(10, 20, 0, 0));
        vBox.setSpacing(100);
        borderPane.setRight(vBox);
    }

    /**
     * Creates all the buttons for the letters in a table like format.
     * To make the buttons we loop through every letter A-Z and give it an action.
     * The grid pane allows for 7 elements in a row with 4 rows.
     * @return a gridpane of the all the buttons to be put onto the screen.
     */
    private GridPane createLetterButtons() {
        letterButtons.clear();
        GridPane gridPane = new GridPane();
        for(char i = 'A', j = 0; i <= 'Z'; j++) {
            for(int k = 0; k < 7 && i <= 'Z'; i++, k++) {
                Button button = new Button();
                button.setPrefSize(letterButtonWidth, letterButtonHeight);
                button.setText(Character.toString(i));
                if(guessedLetters.contains(i - 65)) {   //This if is useful when we have loaded a game
                    button.setDisable(true);            //If in the loaded game this was guessed, disable button
                    if(wordChooser.getChosenWord().contains(Character.toString(i))) {  //If it was good guess, make green
                        BackgroundFill green = new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY);
                        button.setBackground(new Background(green));
                    }
                    else {  //else bad guess, make red.
                        BackgroundFill red = new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY);
                        button.setBackground(new Background(red));
                    }
                }
                char finalI = i;
                button.setOnAction(event -> {
                    if(!isGameModified) {   //If the game hasnt changed until this button press, enable the save button.
                        isGameModified = true;
                        saveButton.setDisable(false);
                    }
                    guessedLetters.add(finalI - 65);    //Add this button's index to our guessed letters list.
                    button.setDisable(true);
                    if(wordChooser.checkGuessCorrect(button.getText())) {
                        handleGuess(true);
                        BackgroundFill green = new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY);
                        button.setBackground(new Background(green));

                    }
                    else {
                        handleGuess(false);
                        BackgroundFill red = new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY);
                        button.setBackground(new Background(red));
                    }
                });
                letterButtons.add(button);
                gridPane.add(button, k, j);
            }
        }
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        return gridPane;
    }

    /**
     * To get key inputs we will simply take whatever key it is and make sure it is a letter from A-Z, and
     * then we will just fire its corresponding button that we have already made from the letterButtons list.
     */
    private void setupKeyInput() {
        scene.setOnKeyPressed(event -> {
            if(event.getText().length() == 1) {
                Character key = event.getText().toUpperCase().charAt(0);
                if(key - 65 >= 0 && key - 65 <26) letterButtons.get(key - 65).fire();
            }

        });
    }

    private void disableAllLetterButtons() {
        for (Button button : letterButtons) {
            button.setDisable(true);
        }
    }

    private void handleGuess(boolean guess) {
        if(!guess) {    //If guess is wrong
            Hangman.remainingGuesses -= 1;
            Hangman.drawNext(figure);
            borderPane.setCenter(figure);
            remainingGuessesLabel.setText("Remaining guesses: " + Hangman.remainingGuesses);
        }
        if(Hangman.remainingGuesses == 0) { //If no more remaining guesses, lose
            wordChooser.revealWord();
            this.isGameModified = false;
            saveButton.setDisable(true);
            disableAllLetterButtons();
            Hangman.displayGameOverAlert("You lost. (The word was \"" + wordChooser.getChosenWord() + "\")");

        }
        else if(wordChooser.getUnguessedLetters() == 0) { //If we have guessed all letters in the word, win
            Hangman.displayGameOverAlert("You won.");
            this.isGameModified = false;
            saveButton.setDisable(true);
            disableAllLetterButtons();
        }
    }
}
