package movie_master.api.exception;

public class UserCannotFriendThemself extends Exception {

    public UserCannotFriendThemself() { super("A user cannot be friends with themselves"); }
}
