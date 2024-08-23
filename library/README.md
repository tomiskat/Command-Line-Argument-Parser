## Classes and Usage

The overall functionality is divided into several classes. The main class is `Manager`. The manager in the class manages the library. Only this class can be explicitly instantiated by a user. Its constructor requires an array of raw arguments from the command line. The manager can create three types of command line arguments, i.e. simple arguments, string arguments and integer arguments. Each type is represented by a class. The respective classes are `SimpleArgument`, `StringArgument` and `IntegerArgument`. String arguments and integer arguments have values, and the values have corresponding types. A user requests an instance of an argument builder from the manager. The builder is used to configure the general properties of the the created argument. The builder can configure names and description of the argument. An argument can have multiple names, they are its aliases. When the general configuration is finished, the builder returns the configured argument. The following example illustrates the general configuration.

```java
Manager manager = new Manager(args);

SimpleArgument ba = manager.makeArgument(SimpleArgument.class)
  .addName("v")
  .addName("version")
  .setDescription("Show version.")
  .finishGeneral();
```

String arguments and integer arguments expect a value on the command line, and they have specific configuration. The specific configuration is set directly on a particular instance after the general configuration. The arguments can be required. They can have a separator. If an argument has a separator, it expects multiple values separated by the separator on the command line. A separator cannot be a whitespace character. They can also have a validator. A validator is a function which accepts a value and returns a boolean value. If the returned value is false, the input value is not allowed, and an exception is thrown. The following example shows specific configuration.

```java
IntegerArgument ia = manager.makeArgument(IntegerArgument.class)
  .addName("sizes")
  .finishGeneral()
  .required()
  .setSeparator(',')
  .setValidator(value -> value > 3);
```

The command line argument can be parsed explicitly as well as implicitly. The arguments can be parsed implicitly when the first value is requested. The explicit way may be useful for catching exceptions. Changing the configuration after the parsing is pointless. To get count of parsed arguments, we can use manager method `getParsedArgumentsCount()`. Values can be obtained from defined arguments. Values can be selected by index. A user can check whether argument was parsed with the method `isSet()`. The following example shows parsing and values requests.

```java
manager.parseArguments(); // this line is unnecessary
int count = manager.getParsedArgumentsCount();
boolean b = ba.isSet();
int i = ia.getValue(2);
```

This library enforces best practices. All arguments are defined first. Then, the command line arguments are parsed. Finally, the values are obtained from the defined argument objects. These phases cannot be rearranged or mixed.

## Special Usage

Defined arguments with special configuration can perform special tasks. The configuration is intuitive, and it leads to the corresponding special purpose. A string argument or an integer argument without any name expects a plain (nameless) command line argument without a name. The manager captures explicitly defined plain arguments. If there are more plain arguments, the remaining plain arguments are stored in the manager. Method `getTrailingArguments()` returns them as a string array. The command line arguments with names are identified by that name. The plain command line arguments are identified by their possition. The order of plain arguments is important in the source code and on the command line. The plain arguments are captured according to their position. If a defined plain argument expects an integer, the corresponding plain argument on the command line must be an integer. The manager does not try to rearrange arguments to match their defined types.

## Compilation

If you want to use the library, simply add the path of the `clap` Java package to your classpath, like so.

`-classpath "<to this library>/src/main/java"`

Alternatively, you can add the library to your [Maven](https://maven.apache.org/) project.

## Running Tests

To run the tests you need [Maven](https://maven.apache.org/) installed. Then, simply run the following command in the root directory of the project.

`mvn test`

## Generating documentation

The reference documentation can be generated with [Maven](https://maven.apache.org/). Run the following command.

`mvn javadoc:javadoc`

This project contains the generated documentation in the [apidocs](apidocs) directory.
