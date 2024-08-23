package clap;

/**
 * Class to represent Integer argument
 */
public final class IntegerArgument extends ParamsArgument<Integer> {

    /**
     * Constructor for IntegerArgument
     * @param owner Manager that owns this argument
     */
    IntegerArgument(Manager owner) {
        super(owner);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setValue(String value) throws ArgumentException {
        String[] values = splitValue(value);
        Integer[] intValues = parseValues(values);
        validateValues(intValues);
        parameters = intValues;
        argIsSet = true;
    }

    /**
     * Parses values to Integer.
     * @param values values to parse
     * @return parsed values
     */
    private Integer[] parseValues(String[] values) throws ArgumentException {
        Integer[] parsedValues = new Integer[values.length];
        for (int i = 0; i < values.length; i++) {
            try {
                parsedValues[i] = Integer.parseInt(values[i]);
            }
            catch (NumberFormatException e) {
                throw new ArgumentException("Cannot parse to int value: " + values[i]);
            }
        }
        return parsedValues;
    }

    /**
     * Sets validator for this argument.
     * @param validator validator that will be used
     * @return this instance
     */
    public IntegerArgument setValidator(Validator<Integer> validator) {
        this.validator = validator;
        return this;
    }

    /**
     * Sets separator for this argument.
     * @param separator separator that will be used, cannot be a whitespace character
     * @return this instance
     * @throws ArgumentException If the separator is a whitespace character.
     */
    public IntegerArgument setSeparator(char separator) throws ArgumentException {
        if (separator == ' ') {
            throw new ArgumentException("Separator cannot be a space character");
        }
        this.separator = separator;
        hasMultipleParams = true;
        return this;
    }

    /**
     * Sets this argument as required.
     * @return this instance
     */
    public IntegerArgument required() {
        required = true;
        return this;
    }
}
