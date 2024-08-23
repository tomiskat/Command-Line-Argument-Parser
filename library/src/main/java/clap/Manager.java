package clap;

import java.util.*;

/**
 * The main class of the Command Line Argument Parser organizes other parts.
 */
public final class Manager {
    /**
     * The manager keeps created instances of arguments here.
     */
    private final List<SimpleArgument> arguments = new ArrayList<>();


    private final List<SimpleArgument> namedArgs = new ArrayList<>();
    private final List<SimpleArgument> unnamedArgs = new ArrayList<>();
    private int unnamedArgsIndex = 0;
    /**
     * There are raw arguments from the parameter of the main function.
     */
    private final String[] rawArguments;
    /**
     * Trailing plain arguments at the end of the command line arguments (if not defined in argument list).
     */
    private final List<String> trailingArguments = new ArrayList<>();
    private boolean argumentsParsed = false;
    private int parsedArgumentsCount = 0;


    /**
     * Constructor of the manager.
     * @param arguments arguments from the command line
     */
    public Manager(String[] arguments) {
        if (arguments == null) {
            throw new IllegalArgumentException("Arguments cannot be null!");
        }
        rawArguments = arguments;
    }


    /**
     * Returns number of parsed arguments.
     * @return number of parsed arguments
     * @throws ArgumentException If parsing fails.
     */
    public int getParsedArgumentsCount() throws ArgumentException {
        if (!argumentsParsed) {
            parseArguments();
        }
        return parsedArgumentsCount;
    }


    /**
     * Returns trailing arguments.
     * @return trailing arguments as an array of strings
     * @throws ArgumentException If parsing fails.
     */
    public String[] getTrailingArguments() throws ArgumentException {
        if (!argumentsParsed) {
            parseArguments();
        }
        return trailingArguments.toArray(new String[0]);
    }


    /**
     * Returns whether the arguments are parsed.
     * @return true if arguments are parsed, false otherwise
     */
    public boolean areArgumentsParsed() {
        return argumentsParsed;
    }


    /**
     * Generates a documentation of the specified arguments.
     * @return helpful documentation of the arguments
     */
    public String getHelp() {
        StringBuilder help = new StringBuilder();
        for (SimpleArgument arg : arguments) {
            help.append("Argument names: ").append(arg.names).append("\n");
            help.append("Description:    ").append(arg.getArgumentHelp()).append("\n");
            help.append("\n");
        }
        return help.toString();
    }


    /**
     * This function parses program arguments and stores results in argument instances. Only single parsing is allowed. It can be called by other functions.
     * @throws ArgumentException If parsing has already been performed.
     */
    public void parseArguments() throws ArgumentException {
        if (argumentsParsed) {
            throw new ArgumentException("Only single parsing is allowed!");
        }

        sortArguments();
        int index = 0;

        while (index < rawArguments.length) {
            if (rawArguments[index].equals("--")) {
                parsePlainArguments(++index);
                break;
            }
            String argValue = rawArguments[index];
            index = parseArgument(argValue, index);
        }

        argumentsParsed = true;
        checkRequiredArguments();
    }


    /**
     * This function parses plain arguments after "--" delimiter.
     * @param index The index of the argument in the {@link #rawArguments}
     */
    private void parsePlainArguments(int index) throws ArgumentException {
        while (index < rawArguments.length) {
            parseUnnamedArg(rawArguments[index++]);
        }
    }


    /**
     * Sort arguments to {@link #unnamedArgs} and {@link #namedArgs}
     */
    private void sortArguments() {
        for (SimpleArgument argument : arguments) {
            if (argument.names.isEmpty()) {
                unnamedArgs.add(argument);
            }
            else {
                namedArgs.add(argument);
            }
        }
    }


    /**
     * Check that all required arguments are set.
     */
    private void checkRequiredArguments() throws ArgumentException {
        for (SimpleArgument argument : arguments) {
                checkRequiredArgument(argument);
        }
    }


    /**
     * Check that argument is set if it is required.
     * @param argument The argument to check
     */
    private void checkRequiredArgument(SimpleArgument argument) throws ArgumentException {
        if (argument.isRequired() && !argument.isSet()) {
            throw new ArgumentException("Required argument " + argument.names + " is not set!");
        }
    }


    /**
     * Method to parse given argument.
     * @param argValue The argument to parse
     * @param index The index of the argument in the {@link #rawArguments}
     * @return The index of next argument in the {@link #rawArguments}
     */
    private int parseArgument(String argValue, int index) throws ArgumentException {
        if (argValue.startsWith("-")) {
            SimpleArgument argument = findArgument(argValue);
            index = parseNamedArg(argument, index);
        }
        else {
            parseUnnamedArg(argValue);
        }
        return ++index;
    }


    /**
     * Parse unnamed arguments
     * @param argValue The argument value
     */
    private void parseUnnamedArg(String argValue) throws ArgumentException {
        if (unnamedArgsIndex < unnamedArgs.size()) {
            unnamedArgs.get(unnamedArgsIndex++).setValue(argValue);
        }
        else {
            trailingArguments.add(argValue);
        }
        parsedArgumentsCount++;
    }


    /**
     * parse named argument
     * @param argument The argument to parse
     * @param index The index of the argument in the {@link #rawArguments}
     * @return The index of next argument in the {@link #rawArguments}
     */
    private int parseNamedArg(SimpleArgument argument, int index) throws ArgumentException {
        if (!argument.expectsParameters()) {
            argument.setValue(null);
        }
        else {
            checkParameterIsPresent(index);
            argument.setValue(rawArguments[++index]);
        }

        parsedArgumentsCount++;
        return index;
    }


    /**
     * Check that the parameter for paramsArgument is present
     * @param index The index of the argument in the {@link #rawArguments}
     * @throws ArgumentException If the parameter is not present
     */
    private void checkParameterIsPresent(int index) throws ArgumentException {
        if (index + 1 >= rawArguments.length) {
            throw new ArgumentException("Argument " + rawArguments[index] + " expects a parameter!");
        }
    }


    /**
     * Find argument by name
     * @param argName The name of the argument
     * @return {@link SimpleArgument}
     * @throws ArgumentException If the argument is not found
     */
    private SimpleArgument findArgument(String argName) throws ArgumentException {
        String searchedName = argName.replaceFirst("^-{0,2}", "");
        for (SimpleArgument argument : namedArgs) {
            if (argument.names.contains(searchedName)) {
                return argument;
            }
        }
        throw new ArgumentException("Unknown argument name " + argName);
    }


    /**
     * Commences a creation of a new argument.
     * @param type Runtime representation of the argument type
     * @param <T> The type of the argument
     * @return Argument builder for specification of a new argument
     * @throws ArgumentException If the argument does not have an expected constructor.
     */
    public <T extends SimpleArgument> ArgumentBuilder<T> makeArgument(Class<T> type) throws ArgumentException {
        try {
            T arg = type.getDeclaredConstructor(Manager.class).newInstance(this);
            arguments.add(arg);
            return new ArgumentBuilder<>(arg);
        }
        catch (Exception e) {
            throw new ArgumentException("Argument constructor malfunction!");
        }
    }

    /**
     * Checks if a name is not already defined.
     * @param name potential name to be tested
     * @return false if the name is already used, true otherwise
     */
    public boolean isNameUnique(String name) {
        for (SimpleArgument argument : arguments) {
            if (argument.names.contains(name)) {
                return false;
            }
        }
        return true;
    }
}
