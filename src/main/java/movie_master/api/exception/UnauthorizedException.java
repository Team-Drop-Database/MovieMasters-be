package movie_master.api.exception;

public class UnauthorizedException extends Exception {

    public UnauthorizedException() {
        super("You are not authorized to perform this action");
    }
}
