import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import java.io.FileNotFoundException;
import java.util.*;

public class WordChooser {
    private String chosenWord;
    private List<String> listOfWords;
    private List<LetterLabel> letterLabels;
    private int unguessedLetters;

    public WordChooser() {
        listOfWords = new ArrayList<>();
        chosenWord = "";
        letterLabels = new ArrayList<>();
        unguessedLetters = 0;

    }

    public void decrementUnguessedLetters() {
        this.unguessedLetters -= 1;
    }

    /**
     * Gets the number of unguessed letters left.
     * @return
     */
    public int getUnguessedLetters() { return unguessedLetters; }

    /**
     * Sets the word to guess and creates a list of labels with each letter of the word in a label.
     * @param chosenWord
     */
    public void setChosenWord(String chosenWord) {
        this.chosenWord = chosenWord.toUpperCase();
        unguessedLetters = chosenWord.length();
        for (int i = 0; i < chosenWord.length(); i++) {
            LetterLabel letterLabel = new LetterLabel(Character.toString(this.chosenWord.charAt(i)));
            letterLabels.add(letterLabel);
        }
    }

    /**
     * Returns the black boxes of the word.
     * @return
     */
    public HBox getLetterLabels() {
        HBox hBox = new HBox();
        double screenWidth = Screen.getPrimary().getVisualBounds().getWidth() / 3;
        for (LetterLabel l: letterLabels) {
            l.setLabelWidth(screenWidth / chosenWord.length());
            hBox.getChildren().add(l.getStackPane());
        }
        hBox.setSpacing(5);

        hBox.setMaxWidth(screenWidth);
        return hBox;
    }


    public List<LetterLabel> getLetterLabelsList() {
        return letterLabels;
    }

    public String getChosenWord() { return this.chosenWord; }

    /**
     * Sets the list of words from the words.txt to chose a word from
     * @throws FileNotFoundException
     */
    public void setListOfWords() throws FileNotFoundException, NullPointerException {
        Scanner reader = new Scanner(WordChooser.class.getResourceAsStream("words.txt"));
        while(reader.hasNextLine()) {
            listOfWords.add(reader.nextLine());
        }
        reader.close();
    }

    public void setRandomWord() {
        int i = (int) (Math.random() * listOfWords.size());
        this.setChosenWord(listOfWords.get(i));
    }

    public boolean checkGuessCorrect(String letter) {
        for (LetterLabel l: letterLabels) {
            if(l.updateGuessed(letter)) {
                decrementUnguessedLetters();
            }
        }
        return chosenWord.contains(letter);
    }

    /**
     * Reveals all the letters that were not guessed/
     */
    public void revealWord() {
        for (LetterLabel l: letterLabels) {
            l.revealLetter();
        }
    }

    /**
     * Resets the instance.
     */
    public void clear() {
        this.chosenWord = "";
        this.unguessedLetters = 0;
        this.letterLabels.clear();
    }

}
class LetterLabel {
    private static final int labelWidth = 30;
    private static final int labelHeight = 50;
    private static final int letterFontSize = 25;
    private boolean guessed;
    private Text letter;
    private StackPane stackPane;
    private Rectangle rectangle;
    public LetterLabel (String letter){
        guessed = false;
        this.letter = new Text(letter);
        this.letter.setVisible(false);
        this.letter.setFont(new Font(letterFontSize));
        this.letter.setFill(Color.WHITE);
        rectangle = new Rectangle(labelWidth, labelHeight, Color.BLACK);
        rectangle.setStroke(Color.BLACK);
        stackPane = new StackPane(rectangle, this.letter);
    }

    public Boolean wasGuessed() { return this.guessed; }
    public void setLetter(String s) {
        this.letter.setText(s);
    }
    public void setLabelWidth(double width) {
        this.rectangle.setWidth(width);
        this.rectangle.setHeight(width + 40);
        this.letter.setFont(new Font(width));
    }
    public String getLetter() {
        return this.letter.getText();
    }

    /**
     * Update the value of this.guessed.
     * If this letter hasn't already been guessed, then check if the argument letter matches with this.letter.
     * If it matches then show the letter to the user to show that their guess was correct.
     * @param letter
     * @return
     */
    public boolean updateGuessed(String letter) {
        if(!this.guessed) {
            this.guessed = letter.equals(this.letter.getText());
            this.letter.setVisible(this.guessed);
            return this.guessed;
        }
        return false;
    }

    /**
     * At the end of the game all unguess letters must be shown.
     * If the current instance was not guessed, then we make its letter visible and change its appearance to signify
     * that the user did not guess the letter.
     */
    public void revealLetter() {
        if(!this.guessed) {
            this.letter.setFill(Color.ORANGERED);
            rectangle.setFill(Color.GRAY);
            rectangle.setStroke(Color.GRAY);
            this.letter.setVisible(true);
        }
    }
    public StackPane getStackPane() { return stackPane; }
}
