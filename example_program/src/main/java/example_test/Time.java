package example_test;

import clap.*;

import java.util.List;

/**
 * Example program for testing time command located at <a href="https://d3s.mff.cuni.cz/teaching/nprg043/task1/">teacher page</a>
 */
public class Time {
    public static void main(String[] args) throws ArgumentException {
        // Manager is the main class of the library. It is used to parse arguments and to hold arguments
        Manager manager = new Manager(args);

        /* We differentiate between arguments with and without parameters
         * Argument without parameters is: SimpleArgument
         * Arguments with parameters are: StringArgument, IntegerArgument
         */

        // Simple Argument (-p, --portability)
        SimpleArgument portability = manager.makeArgument(SimpleArgument.class)     // set className
                .addName("p").addName("portability")                                // set aliases
                .setDescription("Output format")                                    // set description
                .finishGeneral();                                                   // finish general settings

        // to access SimpleArgument value, we can use isSet() method
        Boolean portabilityValue = portability.isSet();




        // String Argument (-f FORMAT, --format=FORMAT)
        StringArgument format = manager.makeArgument(StringArgument.class)      // set className
                .addName("f").addName("format")                                 // set aliases
                .setDescription("Specifying output format")                     // set description
                .finishGeneral();                                               // finish general settings


        // we can set argument with parameters as required
        format.required();

        // if argument shall expect more parameters, we can set separator
        format.setSeparator(':');


        // to check whether parameterArgument was set, we can use isSet() method
        boolean formatValueSet = format.isSet();

        // to get parameters count, we can use getNumberOfValues() method
        int numberOfValues = format.getNumberOfValues();

        // to get i-th parameter, we can use getValue(int i) method - index starts at 0
        if (format.isSet()) {
            int i = format.getNumberOfValues() - 1;
            String lastFormatValue = format.getValue(i);
        }


        // if we want to validate argument value during parsing, we can use setValidator() method
        List<String> validFormats = List.of(new String[]{"am", "pm"});
        format.setValidator(new Validator<String>() {
            @Override
            public boolean validate(String value) {
                return validFormats.contains(value);
            }
        });

        // we can also use lambda expression for setting validator
        format.setValidator(value -> validFormats.contains(value));

        // (in this case, we can also use method reference)
        format.setValidator(validFormats::contains);




        // general
        // for help, we can call getArgumentHelp() method on argument
        String helpValue = format.getArgumentHelp();

        // for general help, we can call getHelp() method on Manager class
        String generalHelp = manager.getHelp();

        // to get count of parsed arguments, we can use get getParsedArgumentsCount() method on Manager class
        int parsedArgumentsCount = manager.getParsedArgumentsCount();




        // Let´s put everything together and create IntegerArgument for available port numbers
        // Integer Argument (-P, --ports)
        IntegerArgument ports = manager.makeArgument(IntegerArgument.class)    // set className
                .addName("P").addName("port")                                  // set aliases
                .setDescription("Available ports")                             // set description
                .finishGeneral()                                               // finish general settings
                .setValidator(value -> value > 0)                              // set validator
                .setSeparator(',')                                             // set separator
                .required();                                                   // set required

        if (ports.isSet()) {
            int portsCount = ports.getNumberOfValues();
            int firstPort = ports.getValue(0);
            int lastPort = ports.getValue(portsCount - 1);
        }


        // Some useful hints:
        // 1. If we want to check whether program runs without arguments, we can use getParsedArgumentsCount()
        // 2. If we want to check whether program runs with only 1 specific argument, we can use
        //    getParsedArgumentsCount() and check whether method isSet() returns true (for SimpleArgument)
        //    or method isArgumentPresent() returns true (for StringArgument and IntegerArgument)


        System.out.println("Time program passed");
    }
}
