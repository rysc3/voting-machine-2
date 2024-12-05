package Main;

public class CardReader {
    private boolean cardInserted;
    private String cardCode;
    private boolean hasFailure;

    public CardReader() {
        this.cardInserted = false;
        this.cardCode = null;
        this.hasFailure = false;
    }

    /*
     * Inserts a card with a specific code
     * "V" - Voter
     * "A" - Election Official
     */
    public enum LoginType {
        A, V
    }

    public void insertCard(String code) {
        if (!cardInserted) {
            setCardInsert(true);
            this.cardCode = code;

            System.out.println("Card inserted with access code: " + code);
        } else {
            System.out.println("A card is already inserted.");
        }
    }

    private void setCardInsert(boolean insert) {
        this.cardInserted = insert;
    }

    // Check if a card is inserted
    public boolean isCardIn() {
        return cardInserted;
    }

    // Determine the type of card based on the first letter of the code (V or A)
    public String cardType() {

        if (!cardInserted || cardCode == null || cardCode.isEmpty()) {
            return null;
        }

        char firstLetter = cardCode.charAt(0);

        if (firstLetter == 'A') {
            return "Admin";
        } else if (firstLetter == 'V') {
            return "Voter";
        } else {
            return null;
        }
    }

    // Return the card code if a card is inserted
    public String cardCode() {
        if (cardInserted) {
            return cardCode;
        } else {
            return "No card inserted.";
        }
    }

    // Ejects the card
    public void ejectCard() {
        if (cardInserted) {
            setCardInsert(false);
            cardCode = null;
            System.out.println("Card ejected.");
        } else {
            System.out.println("No card to eject.");
        }
    }

    // Erases the card's code and eject the card
    public void eraseCard() {
        if (cardInserted) {
            System.out.println("Erasing card data...");
            cardCode = null;
            ejectCard();
        } else {
            System.out.println("No card to erase.");
        }
    }

    // force failure on the card reader
    public void setFailureStatus(boolean in) {
        this.hasFailure = in;
    }

    // Check if the card reader has failure
    public boolean getFailureStatus() {
        return hasFailure;
    }

}