package clap;

import java.util.ArrayList;
import java.util.List;

/**
 * The parent of possible arguments encapsulates common properties and works as a parameterless argument.
 */
public class SimpleArgument {
    /**
     * The manager which holds this argument.
     */
    final Manager owner;

    /**
     * The names of the argument.
     */
    List<String> names = new ArrayList<>();

    /**
     * The description of the argument.
     */
    String description;

    /**
     * The flag whether the argument is set.
     */
    boolean argIsSet = false;

    /**
     * The constructor of the argument.
     * @param owner The manager which holds this argument
     */
    SimpleArgument(Manager owner) {
        this.owner = owner;
    }

    /**
     * Accepts value from the command line as a string, converts it and stores it.
     * @param value The new value of the argument
     */
    void setValue(String value) throws ArgumentException {
        argIsSet = true;
    };

    /**
     * Returns whether the argument is set.
     * @return true if the argument is set, false otherwise
     * @throws ArgumentException If parsing fails.
     */
    public boolean isSet() throws ArgumentException {
        if (!owner.areArgumentsParsed()) {
            owner.parseArguments();
        }
        return argIsSet;
    }

    /**
     * Generates documentation for the argument.
     * @return Helpful documentation of the argument
     */
    public String getArgumentHelp() {
        return description;
    }

    /**
     * Returns whether the argument expects parameters.
     * @return true if the argument expects parameters, false otherwise
     */
    public boolean expectsParameters() {
        return false;
    }

    /**
     * Returns whether the argument is required.
     * @return true if the argument is required, false otherwise
     */
    public boolean isRequired() {
        return false;
    }
}
