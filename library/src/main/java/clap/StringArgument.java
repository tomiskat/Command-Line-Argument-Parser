package clap;

/**
 * Class to represent an argument with a string parameters.
 */
public final class StringArgument extends ParamsArgument<String> {
    /**
     * Constructor for StringArgument
     * @param owner Manager that owns this argument
     */
    StringArgument(Manager owner) {
        super(owner);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setValue(String value) throws ArgumentException {
        String[] values = splitValue(value);
        validateValues(values);
        parameters = values;
        argIsSet = true;
    }

    /**
     * Sets validator for this argument.
     * @param validator validator that will be used
     * @return this instance
     */
    public StringArgument setValidator(Validator<String> validator) {
        this.validator = validator;
        return this;
    }

    /**
     * Sets separator for this argument.
     * @param separator separator that will be used, cannot be a whitespace character
     * @return this instance
     * @throws ArgumentException If the separator is a whitespace character.
     */
    public StringArgument setSeparator(char separator) throws ArgumentException {
        if (separator == ' ') {
            throw new ArgumentException("Separator cannot be a space character");
        }
        this.separator = separator;
        hasMultipleParams = true;
        return this;
    }

    /**
     * Sets argument as required.
     * @return this instance
     */
    public StringArgument required() {
        required = true;
        return this;
    }
}
