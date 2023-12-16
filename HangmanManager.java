// add imports as necessary
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

/**
 * Manages the details of EvilHangman. This class keeps
 * tracks of the possible words from a dictionary during
 * rounds of hangman, based on guesses so far.
 *
 */
public class HangmanManager {

    private boolean debug;

    // All possible words from file
    private final Set<String> dictionary;

    // Words that meet length and currentPattern during game
    private ArrayList<String> currentWords;

    // List of INCORECTLY guessed letters
    private ArrayList<Character> guesses;

    // Pattern that all new words contain
    private StringBuilder currentPattern;

    // EASY, MEDIUM, HARD
    private HangmanDifficulty difficulty;

    private int numGuesses;
    private String secretWord;

    /**
     * Create a new HangmanManager from the provided set of words and phrases.
     * pre: words != null, words.size() > 0
     * 
     * @param words   A set with the words for this instance of Hangman.
     * @param debugOn true if we should print out debugging to System.out.
     */
    public HangmanManager(Set<String> words, boolean debugOn) {
        if (words == null || words.size() <= 0) {
            throw new IllegalArgumentException("HangmanManager. " +
                    "words can't be null or zero");
        }

        dictionary = words;
        debug = debugOn;
    }

    /**
     * Create a new HangmanManager from the provided set of words and phrases.
     * Debugging is off.
     * pre: words != null, words.size() > 0
     * 
     * @param words A set with the words for this instance of Hangman.
     */
    public HangmanManager(Set<String> words) {
        if (words == null || words.size() <= 0) {
            throw new IllegalArgumentException("HangmanManager. " +
                    "words can't be null or zero");
        }
        dictionary = words;
        debug = false;
    }

    /**
     * Get the number of words in this HangmanManager of the given length.
     * pre: none
     * 
     * @param length The given length to check.
     * @return the number of words in the original Dictionary
     *         with the given length
     */
    public int numWords(int length) {
        int totalWords = 0;
        for (String word : dictionary) {
            if (word.length() == length) {
                totalWords++;
            }
        }
        return totalWords;
    }

    /**
     * Get for a new round of Hangman. Think of a round as a
     * complete game of Hangman.
     * 
     * @param wordLen    the length of the word to pick this time.
     *                   numWords(wordLen) > 0
     * @param numGuesses the number of wrong guesses before the
     *                   player loses the round. numGuesses >= 1
     * @param diff       The difficulty for this round.
     */
    public void prepForRound(int wordLen, int numGuesses, HangmanDifficulty diff) {
        if (numWords(wordLen) <= 0 || numGuesses < 1) {
            throw new IllegalArgumentException("prepForRound. Dictionary must " +
                    "contain words of wordLen and numGuesses > 0 ");
        }
        // Initialize instance variables
        difficulty = diff;
        secretWord = "";
        currentPattern = new StringBuilder();
        guesses = new ArrayList<>();
        this.numGuesses = numGuesses;
        currentWords = new ArrayList<>();

        // Iterate through dictionary
        final Iterator<String> it = dictionary.iterator();
        while (it.hasNext()) {
            String tempWord = it.next();

            // If word is of wordLen, add to current words
            if (tempWord.length() == wordLen) {
                currentWords.add(tempWord);
            }
        }

        // Set initial pattern to all "-"
        for (int i = 0; i < wordLen; i++) {
            currentPattern.append("-");
        }
    }

    /**
     * The number of words still possible (live) based on the guesses so far.
     * Guesses will eliminate possible words.
     * 
     * @return the number of words that are still possibilities based on the
     *         original dictionary and the guesses so far.
     */
    public int numWordsCurrent() {
        return currentWords.size();
    }

    /**
     * Get the number of wrong guesses the user has left in
     * this round (game) of Hangman.
     * 
     * @return the number of wrong guesses the user has left
     *         in this round (game) of Hangman.
     */
    public int getGuessesLeft() {
        return numGuesses;
    }

    /**
     * Return a String that contains the letters the user has guessed
     * so far during this round.
     * The characters in the String are in alphabetical order.
     * The String is in the form [let1, let2, let3, ... letN].
     * For example [a, c, e, s, t, z]
     * 
     * @return a String that contains the letters the user
     *         has guessed so far during this round.
     */
    public String getGuessesMade() {
        StringBuilder sb = new StringBuilder();
        sb.append(guesses.toString());
        return sb.toString();
    }

    /**
     * Check the status of a character.
     * 
     * @param guess The characater to check.
     * @return true if guess has been used or guessed this round of Hangman,
     *         false otherwise.
     */
    public boolean alreadyGuessed(char guess) {
        for (char c : guesses) {
            if (c == guess) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the current pattern. The pattern contains '-''s for
     * unrevealed (or guessed) characters and the actual character
     * for "correctly guessed" characters.
     * 
     * @return the current pattern.
     */
    public String getPattern() {
        return currentPattern.toString();
    }

    /**
     * Manages the details of Family. This class implements
     * the Comparable interface and consequently the compareTo
     * method in order to sort word patterns by their frequency
     * using two instance variables: pattern and words.
     *
     */
    private class Family implements Comparable<Family> {

        // Key and value for wordFamilies map
        private String pattern;
        private ArrayList<String> words;

        public Family(String pattern, ArrayList<String> words) {
            this.pattern = pattern;
            this.words = words;
        }

        // Overriding compareTo
        public int compareTo(Family two) {

            // Difference indicates greater frequency
            if (words.size() != two.words.size()) {
                return two.words.size() - words.size();
            }

            // Difference of # dashes indicates
            int countDashThis = countDashes(pattern);
            int countDashOther = countDashes(two.pattern);
            if (countDashThis != countDashOther) {
                return countDashOther - countDashThis;
            }

            return pattern.compareTo(two.pattern);

        }

        /**
         * Count # of empty space in pattern
         * 
         * @param pattern current pattern
         * @return return # of "-" in a pattern
         */
        private int countDashes(String pattern) {
            int numDashes = 0;
            for (char c : pattern.toCharArray()) {
                if (c == '-') {
                    numDashes++;
                }
            }
            return numDashes;
        }
    }

    /**
     * Create pattern for current word based on new guess
     * 
     * @param pattern current pattern
     * @param guess   character guessed
     * @return returns tempPattern
     */
    private String makeTempPattern(String word, char guess) {
        String tempPattern = "";
        for (int i = 0; i < word.length(); i++) {

            // If word contains char guess, add to new pattern
            if (word.charAt(i) == guess) {
                tempPattern += guess;

                // Else, use previous pattern's char
            } else {
                tempPattern += currentPattern.toString().charAt(i);
            }
        }
        return tempPattern;
    }

    /**
     * Updates currentPattern, currentWords, numGuesses, and secret word
     * 
     * @param newPattern      new pattern based on difficulty
     * @param newCurrentWords currentWords = newCurrentWords
     * @return returns tempPattern
     */
    private void updateVars(String newPattern, ArrayList<String> newCurrentWords) {

        // No change in pattern -> guess was wrong
        if (currentPattern.toString().equals(newPattern)) {
            numGuesses--;
        }

        currentWords = new ArrayList<>(newCurrentWords);
        currentPattern = new StringBuilder(newPattern);

        // If word completed or out of guesses, set secretWord
        if (!currentPattern.toString().contains("-") || numGuesses == 0) {
            int x = (int) (Math.random() * numWordsCurrent());
            secretWord = currentWords.get(x);
        }

    }

    /**
     * Creates Family object that contains newPattern
     * and newCurrentWords based on difficulty
     * 
     * @param familyPatterns ArrayList of all Family objects for round
     * @return returns Family
     */
    private Family decideDiff(ArrayList<Family> familyPatterns) {
        final int DIV_BY_FOUR = 4;
        String newPattern = getPattern();
        ArrayList<String> newCurrentWords = new ArrayList<>();

        /*
         * If medium -> select, if present, 2nd hardest every 4th guess
         * If easy -> select, if present, 2nd hardest every other guess
         */
        if (((difficulty.equals(HangmanDifficulty.MEDIUM) && guesses.size() % DIV_BY_FOUR == 0)
                || (difficulty.equals(HangmanDifficulty.EASY) && guesses.size() % 2 == 0))
                && familyPatterns.size() > 1) {
            newPattern = familyPatterns.get(1).pattern;
            newCurrentWords = familyPatterns.get(1).words;

            // Else, always pick hardest pattern
        } else {
            newPattern = familyPatterns.get(0).pattern;
            newCurrentWords = familyPatterns.get(0).words;
        }

        return new Family(newPattern, newCurrentWords);
    }

    /**
     * Update the game status (pattern, wrong guesses, word list),
     * based on the give guess.
     * 
     * @param guess pre: !alreadyGuessed(ch), the current guessed character
     * @return return a tree map with the resulting patterns and the number of
     *         words in each of the new patterns.
     *         The return value is for testing and debugging purposes.
     */

    public TreeMap<String, Integer> makeGuess(char guess) {
        if (alreadyGuessed(guess)) {
            throw new IllegalStateException("Character has already been guessed.");
        }

        // wordFamilies -> (pattern, words)
        TreeMap<String, ArrayList<String>> wordFamilies = new TreeMap<>();

        // frequencies -> (pattern, # of words)
        TreeMap<String, Integer> frequencies = new TreeMap<>();

        for (String word : currentWords) {
            String tempPattern = makeTempPattern(word, guess);

            // If new pattern, add to wordFamilies and frequencies
            if (wordFamilies.get(tempPattern) == null) {
                ArrayList<String> newList = new ArrayList<>();
                newList.add(word);
                frequencies.put(tempPattern, 1);
                wordFamilies.put(tempPattern, newList);

                // Else, add word to pattern's list and increment frequency
            } else {
                frequencies.put(tempPattern, frequencies.get(tempPattern) + 1);
                wordFamilies.get(tempPattern).add(word);
            }
        }

        // List of wordFamilies represented as Family objects
        ArrayList<Family> sortedFamilies = new ArrayList<>();
        for (String key : wordFamilies.keySet()) {
            sortedFamilies.add(new Family(key, wordFamilies.get(key)));
        }

        // Sort Family objects by implemented compareTo
        Collections.sort(sortedFamilies);

        // Increment guesses BEFORE calling decideDiff() -> avoid logic error
        guesses.add(guess);

        // Find newPattern and newCurrentWords through sorted Family objects
        Family getDiff = decideDiff(sortedFamilies);

        updateVars(getDiff.pattern, getDiff.words);
        return frequencies;
    }

    /**
     * Return the secret word this HangmanManager finally ended up
     * picking for this round.
     * If there are multiple possible words left one is selected at random.
     * <br>
     * pre: numWordsCurrent() > 0
     * 
     * @return return the secret word the manager picked.
     */
    public String getSecretWord() {
        if (numWordsCurrent() <= 0) {
            throw new IllegalArgumentException("getSecretWord. " +
                    "numWordsCurrent must be greater than 0");
        }
        if (debug) {
            System.out.println("SECRET WORD: " + secretWord);
        }
        return secretWord;
    }
}
