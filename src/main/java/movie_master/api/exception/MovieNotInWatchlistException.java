package movie_master.api.exception;

public class MovieNotInWatchlistException extends RuntimeException {

    public MovieNotInWatchlistException(long movieId, long userId) {
        super(String.format("Movie %s not found in watchlist of user %s", movieId, userId));
    }
}
