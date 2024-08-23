package clap;

/**
 * Represents an exception that is thrown when an argument error occurs.
 */
public final class ArgumentException extends Exception {
    /**
     * The constructor of the exception.
     * @param message The message of the exception
     */
    public ArgumentException(String message) {
        super(message);
    }
}
