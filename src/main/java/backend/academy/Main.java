package backend.academy;

import backend.academy.log.tools.LogController;

public class Main {
    public static void main(String[] args) {
        LogController logController = new LogController();
        logController.start(args, System.out);
    }

    private Main() {

    }
}
