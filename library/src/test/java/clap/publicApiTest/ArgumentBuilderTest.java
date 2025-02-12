package clap.publicApiTest;

import clap.*;
import clap.arguments.*;
import clap.exceptions.ArgumentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Argument builder")
final class ArgumentBuilderTest {
    @Test
    void throwsOnDoubleFinishGeneralCall() throws ArgumentException {
        // arrange
        final var args = new String[]{};
        final Manager manager = new Manager(args);
        final var argBuilder = manager.makeArgument(IntegerArgument.class).addName("a");
        argBuilder.finishGeneral();
        // act & assert
        assertThrows(ArgumentException.class, argBuilder::finishGeneral);
    }

}
