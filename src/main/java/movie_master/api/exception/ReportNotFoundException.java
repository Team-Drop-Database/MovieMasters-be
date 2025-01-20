package movie_master.api.exception;

public class ReportNotFoundException extends RuntimeException {
    public ReportNotFoundException(long id) {
        super(String.format("Report with id: %d could not be found!", id));
    }
}
