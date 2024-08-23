package clap.publicApiTest;

import clap.*;
import org.junit.jupiter.api.*;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Integer argument")
final class IntegerArgumentTest extends SimpleArgumentBaseTest {
    // Tests on validators could be moved to the parent test class,
    // but currently it is not possible, because the setValidator
    // method is not available in the argument's parent class.
    @Test
    void throwsOnInvalidValueInArgumentWithValidator() throws ArgumentException {
        // arrange
        final var invalidValue = -1;
        final var args = new String[]{"-a", Integer.toString(invalidValue)};

        final Manager manager = new Manager(args);

        final var argA = manager.makeArgument(IntegerArgument.class).addName("a").finishGeneral();
        final Validator<Integer> validator = (Integer value) -> value >= 0;
        argA.setValidator(validator);
        // act & assert
        assertThrows(ArgumentException.class, manager::parseArguments);
    }

    @Test
    void doesNotThrowOnValidValueInArgumentWithValidator() throws ArgumentException {
        // arrange
        final var validValue = 10;
        final var args = new String[]{"-a", Integer.toString(validValue)};
        final Manager manager = new Manager(args);
        final var argA = manager.makeArgument(IntegerArgument.class).addName("a").finishGeneral();
        final Validator<Integer> validator = (Integer value) -> value >= 0;
        argA.setValidator(validator);
        // act & assert
        assertDoesNotThrow(manager::parseArguments);
    }

    @Override
    Stream<String[]> stringValuesToParse() {
        return Stream.of(new String[]{"1"}, new String[]{"1", "2"});
    }

    @TestFactory
    Stream<DynamicTest> numberOfValuesIsSetCorrectly() {
        return stringValuesToParse().map((valuesToParse) -> DynamicTest.dynamicTest("number of values set correctly for " + valuesToParse.length + " args", () -> {
            // arrange
            final var separator = ',';
            final var listValueArgument = String.join(String.valueOf(separator), valuesToParse);
            final Manager manager = new Manager(new String[]{listValueArgument});
            final var arg = manager.makeArgument(IntegerArgument.class).finishGeneral();
            arg.setSeparator(separator);

            // act
            manager.parseArguments();
            // assert
            assertEquals(valuesToParse.length, arg.getNumberOfValues());
        }));
    }

    @Override
    Class<? extends SimpleArgument> getArgumentClass() {
        return IntegerArgument.class;
    }
}
