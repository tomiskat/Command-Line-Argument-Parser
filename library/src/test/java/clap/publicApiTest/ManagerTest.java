package clap.publicApiTest;

import clap.*;
import clap.arguments.*;
import clap.exceptions.ArgumentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Manager")
final class ManagerTest {
    /**
     * Provide variable-length arrays of plain arguments (strings).
     */

    @Test
    void areArgumentsParsedReturnsFalseBeforeParsing() {
        // arrange
        final String[] args = new String[]{};
        final Manager manager = new Manager(args);
        // act & assert
        assertFalse(manager.areArgumentsParsed());
    }

    @Test
    void areArgumentsParsedReturnsTrueAfterParsing() throws ArgumentException {
        // arrange
        final String[] args = new String[]{};
        final Manager manager = new Manager(args);
        // act
        manager.parseArguments();
        // assert
        assertTrue(manager.areArgumentsParsed());
    }

    @Test
    void throwsOnInvalidManagerInstantiation() {
        assertThrows(IllegalArgumentException.class, () -> new Manager(null));
    }

    @Test
    void throwsOnMultipleParseArgumentsCall() throws ArgumentException {
        // arrange
        final String[] args = new String[]{};
        final Manager manager = new Manager(args);
        // act & assert
        manager.parseArguments();
        assertThrows(ArgumentException.class, manager::parseArguments);
    }

    @Test
    void allowsMixingPlainArgumentsAndOptions() throws ArgumentException {
        // arrange
        // expected values of arguments (arg types and values chosen arbitrarily)
        final Integer argAExpectedValue = 1;
        final var firstPlainArgExpectedValue = "foo";
        final String argBExpectedValue = "bar";
        final var secondPlainArgExpectedValue = "baz";

        final var args = new String[]{"-a", argAExpectedValue.toString(), firstPlainArgExpectedValue, "-b",
                argBExpectedValue, secondPlainArgExpectedValue};

        final Manager manager = new Manager(args);

        // define arguments in manager
        final var argA = manager.makeArgument(IntegerArgument.class).addName("a").finishGeneral();
        final var argB = manager.makeArgument(StringArgument.class).addName("b").finishGeneral();
        final var firstPlainArg = manager.makeArgument(StringArgument.class).finishGeneral();
        final var secondPlainArg = manager.makeArgument(StringArgument.class).finishGeneral();
        // act
        manager.parseArguments();
        // assert
        // check values of arguments and their counts
        final var optAValueCount = argA.getNumberOfValues();
        final var optAValue = argA.getValue(0);

        final var optBValueCount = argB.getNumberOfValues();
        final var optBValue = argB.getValue(0);

        final var firstPlainArgValue = firstPlainArg.getValue(0);
        final var firstPlainArgValueCount = firstPlainArg.getNumberOfValues();

        final var secondPlainArgValue = secondPlainArg.getValue(0);
        final var secondPlainArgValueCount = secondPlainArg.getNumberOfValues();

        assertEquals(1, optAValueCount);
        assertEquals(argAExpectedValue, optAValue);

        assertEquals(1, optBValueCount);
        assertEquals(argBExpectedValue, optBValue);

        assertEquals(1, firstPlainArgValueCount);
        assertEquals(firstPlainArgExpectedValue, firstPlainArgValue);

        assertEquals(1, secondPlainArgValueCount);
        assertEquals(secondPlainArgExpectedValue, secondPlainArgValue);
    }

    @Test
    void parsesOnlyPlainArgumentsAfterPlainArgumentDelimiter() throws ArgumentException {
        // arrange
        final Integer optAExpectedValue = 1;
        final var options = new String[]{"-a", optAExpectedValue.toString()};
        final int optAUnexpectedValue = 10;
        // the trail contains assignment to '-a', which should be ignored, because it's after delimiter
        final var expectedTrail = new String[]{"-a", Integer.toString(optAUnexpectedValue), "world"};
        final var delimiter = "--";

        final String[] args =
                Stream.of(options, new String[]{delimiter}, expectedTrail).flatMap(Stream::of).toArray(String[]::new);

        final Manager manager = new Manager(args);

        final var argA = manager.makeArgument(IntegerArgument.class).addName("a").finishGeneral();
        // act
        manager.parseArguments();
        // assert
        final var optAValue = argA.getValue(0);
        final var receivedTrail = manager.getTrailingArguments();

        assertEquals(optAExpectedValue, optAValue);
        assertArrayEquals(expectedTrail, receivedTrail);
    }

    @Test
    void throwsOnMissingRequiredArguments() throws ArgumentException {
        // arrange
        final var args = new String[]{"-b", "1"};
        final Manager manager = new Manager(args);
        manager.makeArgument(IntegerArgument.class).addName("a").finishGeneral().required();
        // act & assert
        assertThrows(ArgumentException.class, manager::parseArguments);
    }

    @Test
    void parsesListValueArguments() throws ArgumentException {
        // arrange
        final var optAExpectedValues = new Integer[]{1, 2, 3};
        final String[] optAExpectedValuesAsStrings =
                Arrays.stream(optAExpectedValues).map(Object::toString).toArray(String[]::new);
        final var optASeparator = ';';

        final String[] args = Stream.of("-a",
                String.join(String.valueOf(optASeparator), optAExpectedValuesAsStrings)).toArray(String[]::new);

        final Manager manager = new Manager(args);

        final var optA =
                manager.makeArgument(IntegerArgument.class).addName("a").finishGeneral().setSeparator(optASeparator);
        // act
        manager.parseArguments();
        // assert
        final var optAValues = new ArrayList<Integer>();
        for (int i = 0; i < optAExpectedValues.length; i++)
            optAValues.add(optA.getValue(i));

        assertArrayEquals(optAExpectedValues, optAValues.toArray());
    }

    @Test
    void throwsOnInvalidValueInListValueArguments() throws ArgumentException {
        // arrange
        final String[] args = new String[]{"-a", "1,2,a"};
        final Manager manager = new Manager(args);
        manager.makeArgument(IntegerArgument.class).addName("a").finishGeneral().setSeparator(',');
        // act & assert
        assertThrows(ArgumentException.class, manager::parseArguments);
    }

    @Test
    void parsesSingleValueArguments() throws ArgumentException {
        // arrange
        final Integer optAExpectedValue = 1;
        final String[] args = new String[]{"-a", optAExpectedValue.toString()};
        final Manager manager = new Manager(args);
        final var optA = manager.makeArgument(IntegerArgument.class).addName("a").finishGeneral();
        // act
        manager.parseArguments();
        // assert
        final var optAValue = optA.getValue(0);

        assertEquals(optAExpectedValue, optAValue);
    }

    @Test
    void parsesValuesInDefinedOrder() throws ArgumentException {
        // arrange
        final String arg1ExpectedValue = "foo";
        final String arg2ExpectedValue = "bar";
        final var args = new String[]{arg1ExpectedValue, arg2ExpectedValue};
        final Manager manager = new Manager(args);
        final var arg1 = manager.makeArgument(StringArgument.class).finishGeneral();
        final var arg2 = manager.makeArgument(StringArgument.class).finishGeneral();
        // act
        manager.parseArguments();
        // assert
        final var arg1Value = arg1.getValue(0);
        final var arg2Value = arg2.getValue(0);
        assertEquals(arg1ExpectedValue, arg1Value);
        assertEquals(arg2ExpectedValue, arg2Value);
    }

    @Test
    void usesLastValueIfOptionProvidedMultipleTimes() throws ArgumentException {
        // arrange
        final String optAExpectedValue = "second";
        final String[] args = new String[]{"-a", "first", "-a", optAExpectedValue};
        final Manager manager = new Manager(args);
        final var optA = manager.makeArgument(StringArgument.class).addName("a").finishGeneral();
        // act
        manager.parseArguments();
        // assert
        final var optAValue = optA.getValue(0);
        assertEquals(optAExpectedValue, optAValue);
    }

    @ParameterizedTest
    @ValueSource(strings = {"-a", "--along"})
    void parsesOptionsWithMultipleNames(String chosenOptionName) throws ArgumentException {
        // arrange
        final String optAExpectedValue = "value";
        final String[] args = new String[]{chosenOptionName, optAExpectedValue};
        final Manager manager = new Manager(args);
        final var optA = manager.makeArgument(StringArgument.class).addName("a").addName("along").finishGeneral();
        // act
        manager.parseArguments();
        // assert
        final var optAValue = optA.getValue(0);
        assertEquals(optAExpectedValue, optAValue);
    }

    @Test
    void throwsOnParsingUnknownOption() {
        // arrange
        final String[] args = new String[]{"-a", "--", "plainArg"}; // '-a' is the unknown flag
        final Manager manager = new Manager(args);
        // act & assert
        assertThrows(ArgumentException.class, manager::parseArguments);
    }

    /**
     * Provides arguments for {@link #getParsedArgumentCountReturnsCorrectNumber(String[])}.
     * @return Stream of arguments.
     */
    static Stream<Arguments> providePlainArgs() {
        return Stream.of(
                Arguments.of((Object) new String[]{"a"}),
                Arguments.of((Object) new String[]{"a", "b"}),
                Arguments.of((Object) new String[]{"a", "b", "c"})
        );
    }
    @ParameterizedTest
    @MethodSource("providePlainArgs")
    void getParsedArgumentCountReturnsCorrectNumber(String[] plainArgs) throws ArgumentException {
        // arrange
        final Manager manager = new Manager(plainArgs);
        // act
        manager.parseArguments();
        // assert
        final var parsedArgumentCount = manager.getParsedArgumentsCount();
        assertEquals(plainArgs.length, parsedArgumentCount);
    }
}
