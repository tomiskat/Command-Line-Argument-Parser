package clap.arguments;

import clap.exceptions.ArgumentException;
import clap.Manager;
import clap.Validator;

import java.util.regex.Pattern;

/**
 * There are common functionalities of the arguments with parameters;
 * @param <T> The type of the value of the parameters
 */
class ParamsArgument<T> extends SimpleArgument {
    /**
     * The array of the parameters.
     */
    T[] parameters;

    /**
     * The validator of the parameters.
     */
    Validator<T> validator;

    /**
     * The separator of the parameters.
     */
    char separator;

    /**
     * The flag if the argument has more parameters.
     */
    boolean hasMultipleParams = false;

    /**
     * The flag if the argument is required.
     */
    boolean required = false;

    /**
     * Constructor of the argument with parameters.
     * @param owner the {@link Manager} of the argument
     */
    public ParamsArgument(Manager owner) {
        super(owner);
    }

    /**
     * Get the value of the parameter on the given index (zero based).
     * @param index given index of the value
     * @return value of the parameter
     * @throws ArgumentException If parsing fails.
     */
    public T getValue(int index) throws ArgumentException {
        if (!owner.areArgumentsParsed()) {
            owner.parseArguments();
        }
        return parameters[index];
    }

    /**
     * Get the number of the values of the parameter.
     * @return number of the values
     * @throws ArgumentException If parsing fails.
     */
    public int getNumberOfValues() throws ArgumentException {
        if (!owner.areArgumentsParsed()) {
            owner.parseArguments();
        }
        return argIsSet ? parameters.length : 0;
    }

    /**
     * Check that values of the parameters are valid.
     * @param values values of the parameters
     */
    void validateValues(T[] values) throws ArgumentException {
        if (validator == null) {
            return;
        }

        for (T v : values) {
            if (!validator.validate(v)) {
                throw new ArgumentException("Validation failed with value: " + v.toString());
            }
        }
    }

    /**
     * Split the given string of values of the parameter.
     * @param value string of the values
     * @return array of the values
     */
    String[] splitValue(String value) {
        if (hasMultipleParams) {
            return value.split(Pattern.quote(Character.toString(separator)));
        }
        else {
            return new String[]{ value };
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean expectsParameters() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRequired() {
        return required;
    }
}
