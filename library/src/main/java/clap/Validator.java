package clap;

/**
 * The interface for the validator of the parameters.
 * @param <T> The type of the value of the parameters
 */
@FunctionalInterface
public interface Validator<T> {
    /**
     * Validates the value of the parameter.
     * @param value the value of the parameter
     * @return true if the value is valid, false otherwise
     */
    boolean validate(T value);
}
