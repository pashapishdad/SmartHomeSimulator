/**
 * Custom exception class to replace any startup exception that could occur.
 * Main causes for the startup to fail is a failure on the SQL scripts that have not been touched since Delivery 1 and should not be edited.
 */
public class StartupFailureException extends RuntimeException {

    private final String message;
    private final Exception e;

    public StartupFailureException(final Exception e) {
        this.message = "Startup failure";
        this.e = e;
    }

    public String getErrorCause() {
        return message + "\n" + e.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
