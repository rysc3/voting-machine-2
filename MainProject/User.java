package MainProject;

public class User {
    private CardReader cardReader;

    public User(CardReader cardReader) {
        this.cardReader = cardReader;

        // Todo: if no card isnerted: continuously check if there a card has been inserted
        // Todo: if card inserted: continuously check if user is done (do not eject card, that will be done by votemanager)
    }
}
