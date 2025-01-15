package movie_master.api.exception;

public class BannedAccountException extends Exception {

    public BannedAccountException() {
        super("This account has been banned.");
    }
}
