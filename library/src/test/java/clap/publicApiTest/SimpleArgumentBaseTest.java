package clap.publicApiTest;

import clap.*;
import clap.arguments.SimpleArgument;
import clap.exceptions.ArgumentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Contains tests that are executed on class {@link SimpleArgument},
 * and on its subclasses. To see tests that are executed only on
 * {@link SimpleArgument}, see {@link SimpleArgumentTest}.
 */
@DisplayName("Simple argument")
class SimpleArgumentBaseTest {
    Class<? extends SimpleArgument> getArgumentClass() {
        return SimpleArgument.class;
    }

    @Test
    void isSetReturnsTrueOnSetArgument() throws ArgumentException, NoSuchElementException {
        // arrange
        final var valuesToParse = stringValuesToParse().findFirst();
        String[] args;
        // if there are no values to parse (i.e. the argument does not expect a value),
        // then we provide no value to the argument
        if (valuesToParse.isEmpty()) args = new String[]{"-a"};
        else {
            final var unwrappedValuesToParse = valuesToParse.get();

            if (unwrappedValuesToParse.length == 0) args = new String[]{"-a"};
                // if there is at least one value to parse, we pass it to the '-a' argument
            else args = new String[]{"-a", unwrappedValuesToParse[0]};
        }

        final Manager manager = new Manager(args);
        final var argA = manager.makeArgument(getArgumentClass()).addName("a").finishGeneral();
        // act
        manager.parseArguments();
        final boolean isArgASet = argA.isSet();
        // assert
        assertTrue(isArgASet);
    }

    /**
     * Provide string values that can be parsed into the type of the argument.
     * For example, if the argument is an IntegerArgument, the values could be "1", "2", "3".
     * The stream is empty if the argument does not expect a value. If the argument may
     * get a value, the stream should contain at least a few arrays of arguments. The arrays
     * should be of different lengths.
     */
    Stream<String[]> stringValuesToParse() {
        return Stream.empty();
    }
}
