package Main;

public class Admin {

    public Admin(String cardCode) {
        System.out.println("New admin... card code: " + cardCode);
        Thread adminThread = new Thread(() -> {
            while (true) {


                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }
}
