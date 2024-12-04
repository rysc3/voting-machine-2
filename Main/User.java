package Main;

public class User {
    private CardReader cardReader;
    private boolean userDone;

    public User(CardReader cardReader) {
        this.cardReader = cardReader;
        this.userDone = false;
    }

    public void startUserThread() {
        Thread userThread = new Thread(() -> {
            while (true) {
                //continuously check if a card has been inserted
                if (cardReader.isCardIn()){
                    System.out.println(cardReader.cardType() + " " + cardReader.cardCode());
                }
                // Todo: if card inserted: continuously check if user is done (do not eject card, that will be done by votemanager)


                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        userThread.start();
    }

    public void removeCard(CardReader reader) {
        if (!reader.isCardIn()) {
            System.out.println("No cards inserted.");
        }
        else {
            reader.ejectCard();
        }
    }

    public void eraseCard(CardReader reader) {
        if (!reader.isCardIn()) {
            System.out.println("No cards inserted.");
        }
        else {
            reader.eraseCard();
        }
    }

    public boolean isUserDone() {
        return userDone;
    }

    /*
     * Method to forcefully abort the user from voting. To be called 
     * when there was an error detected on the system.
     */
    public void abort(){
        System.out.println("(user) SYSTEM FAILURE DETECTED, Ejecting card. Votes were cleared.");
        removeCard(cardReader);
    }

}
