package clap.arguments;

import clap.exceptions.ArgumentException;

/**
 * Provides enhanced syntax for the creation of arguments.
 * @param <T> The type of the argument
 */
public final class ArgumentBuilder<T extends SimpleArgument> {
    /**
     * The {@link SimpleArgument} that is being built.
     */
    private final T argument;

    /**
     * If the builder has finished, it cannot be used again.
     */
    private boolean finished = false;

    /**
     * The constructor of the builder.
     * @param argument The argument that is being built
     */
    public ArgumentBuilder(T argument) {
        this.argument = argument;
    }

    /**
     * Adds a name to the argument.
     * @param name The name to be added
     * @return The builder itself
     * @throws ArgumentException If this builder finishes, it cannot finish again. The name must be correct.
     */
    public ArgumentBuilder<T> addName(String name) throws ArgumentException {
        if (finished) {
            throw new ArgumentException("The builder has already finished.");
        }
        checkNameIsValid(name);
        argument.names.add(name);
        return this;
    }

    /**
     * Checks if the name is valid.
     * @param name The name to be checked
     */
    private void checkNameIsValid(String name) throws ArgumentException {
        checkValidFormat(name);
        checkIsUnique(name);
    }

    /**
     * Checks if the name has a valid format.
     * @param name The name to be checked
     */
    private void checkValidFormat(String name) throws ArgumentException {
        if (name == null || name.isEmpty() || name.equals(" ")) {
            throw new ArgumentException("Argument name cannot be empty or null or a space character.");
        }
        else if (name.startsWith("-")) {
            throw new ArgumentException("Argument name cannot start with a dash.");
        }
    }

    /**
     * Checks if the name is unique.
     * @param name The name to be checked
     */
    private void checkIsUnique(String name) throws ArgumentException {
        if (!argument.owner.isNameUnique(name)) {
            throw new ArgumentException("Argument name " + name + " is already taken.");
        }
    }

    /**
     * Sets description to the argument.
     * @param description The description of the argument
     * @return The builder itself
     * @throws ArgumentException If this builder finishes, it cannot finish again.
     */
    public ArgumentBuilder<T> setDescription(String description) throws ArgumentException {
        if (finished) {
            throw new ArgumentException("The builder has already finished.");
        }
        argument.description = description;
        return this;
    }

    /**
     * Finishes the general initialization of the common properties.
     * @return The initialized argument
     * @throws ArgumentException If this builder finishes, it cannot finish again.
     */
    public T finishGeneral() throws ArgumentException {
        if (finished) {
            throw new ArgumentException("The builder has already finished.");
        }
        finished = true;
        return argument;
    }
}
