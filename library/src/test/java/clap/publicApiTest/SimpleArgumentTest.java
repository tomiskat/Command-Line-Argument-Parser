package clap.publicApiTest;

import clap.*;
import clap.arguments.SimpleArgument;
import clap.exceptions.ArgumentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Contains tests that are executed on class {@link SimpleArgument},
 * but not on its subclasses. To see tests that are executed on
 * subclasses too, see {@link SimpleArgumentBaseTest}.
 */
@DisplayName("Simple argument")
class SimpleArgumentTest {
    @Test
    void doesNotThrowOnParsingDuplicatedSimpleArguments() throws ArgumentException {
        // arrange
        final String[] args = new String[]{"-a", "-a"};
        final Manager manager = new Manager(args);
        manager.makeArgument(SimpleArgument.class).addName("a").finishGeneral();
        // act & assert
        assertDoesNotThrow(manager::parseArguments);
    }
}
