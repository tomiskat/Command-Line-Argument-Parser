/*
This file is not an original part of the directory.
It was created by the other student.
*/

package clap.publicApiTest;

import clap.*;

import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


public class ComplexTests {
   private interface ArgCreator<T extends SimpleArgument>{
      T make(Manager m) throws ArgumentException;
   }

   private interface ArgChecker<T extends SimpleArgument>{
      boolean check(T a) throws ArgumentException;
   }
   private ArgCreator<SimpleArgument> makeArg(String[] names){
      return (m -> {
         var arg = m.makeArgument(SimpleArgument.class);
         for (String name : names){
            arg.addName(name);
         }
         return arg.finishGeneral();
      });
   }

   private ArgCreator<StringArgument> makeStringArg(String[] names, boolean required){
      return (m -> {
         var arg = m.makeArgument(StringArgument.class);
         for (String name : names){
            arg.addName(name);
         }
         var finished = arg.finishGeneral();
         if(required){
            finished.required();
         }
         return finished;
      });
   }

   private  ArgCreator<StringArgument> makeStringArg(String[] names, boolean required, char separator){
      return m->makeStringArg(names, required).make(m).setSeparator(separator);
   }

   private ArgCreator<StringArgument> makeStringArg(String[] names, boolean required, Validator<String> validator){
      return m -> makeStringArg(names, required).make(m).setValidator(validator);
   }

   private ArgCreator<StringArgument> makeStringArg(String[] names, boolean required, char separator, Validator<String> validator){
      return m -> makeStringArg(names, required, separator).make(m).setValidator(validator);
   }

   private ArgCreator<IntegerArgument> makeIntArg(String[] names, boolean required){
      return (m -> {
         var arg = m.makeArgument(IntegerArgument.class);
         for (String name : names){
            arg.addName(name);
         }
         var finished = arg.finishGeneral();
         if(required){
            finished.required();
         }
         return finished;
      });
   }

   private  ArgCreator<IntegerArgument> makeIntArg(String[] names, boolean required, char separator){
      return m->makeIntArg(names, required).make(m).setSeparator(separator);
   }

   private ArgCreator<IntegerArgument> makeIntArg(String[] names, boolean required, Validator<Integer> validator){
      return m -> makeIntArg(names, required).make(m).setValidator(validator);
   }

   private ArgCreator<IntegerArgument> makeIntArg(String[] names, boolean required, char separator, Validator<Integer> validator){
      return m -> makeIntArg(names, required, separator).make(m).setValidator(validator);
   }

   private ArgChecker<SimpleArgument> checkIsset(){
      return SimpleArgument::isSet;
   }

   private ArgChecker<SimpleArgument> checkIsNotSet(){
      return a->!a.isSet();
   }

   private ArgChecker<SimpleArgument> checkValues(String[] values){
      return a->{
         if (a instanceof StringArgument arg){
            if (arg.getNumberOfValues() != values.length)
               return false;
            for(int i=0; i<values.length; i++){
               if (!arg.getValue(i).equals(values[i])) {
                  return false;
               }
            }
            return true;
         }
         return false;
      };
   }

   private ArgChecker<SimpleArgument> checkValues(Integer[] values){
      return a->{
         if (a instanceof IntegerArgument arg){
            if (arg.getNumberOfValues() != values.length)
               return false;
            for(int i=0; i<values.length; i++){
               if (!Objects.equals(arg.getValue(i), values[i])) {
                  return false;
               }
            }
            return true;
         }
         return false;
      };
   }


    @Test
    public void trivialTest(){
    String[] args = {"a"};
    new Manager(args);
    assertTrue(true, "should succeed");
   }
   @Test
   public void failTest(){
      assertFalse(false, "Should fail");
   }

   @Test
   public void threeSimpleArgs(){
      assertValidInput(new String[]{"-a", "-b", "-c"}, Arrays.asList(
              makeArg(new String[]{"a"}),
              makeArg(new String[]{"b"}),
              makeArg(new String[]{"c"})
      ), "Define flag arguments a b and c, parse input containing all of them");
   }

   @Test
   public void threeSimpleArgsOutput(){
      assertOutput(new String[]{"-a", "-b", "-c"}, Arrays.asList(
              makeArg(new String[]{"a"}),
              makeArg(new String[]{"b"}),
              makeArg(new String[]{"c"})
      ),Arrays.asList(
              checkIsset(), checkIsset(), checkIsset()
      ), "Define flag arguments a b and c, parse input containing all of them, check if they're set");
      assertOutput(new String[]{"-a", "-c"}, Arrays.asList(
              makeArg(new String[]{"a"}),
              makeArg(new String[]{"b"}),
              makeArg(new String[]{"c"})
      ),Arrays.asList(
              checkIsset(), checkIsNotSet(), checkIsset()
      ), "Define flag arguments a b and c, parse input containing some of them, check if they're set");
   }

   @Test
   public void threeStringArgsRequired(){
      assertValidInput(new String[]{"-a", "a", "-b", "b", "-c", "c"}, Arrays.asList(
              makeStringArg(new String[]{"a"}, true),
              makeStringArg(new String[]{"b"}, true),
              makeStringArg(new String[]{"c"}, true)
      ), "Define three required string arguments a, b and c, parse input containing all of them");
   }

   @Test
   public void threeArgsRequired(){
      assertValidInput(new String[]{"-a", "a", "-b", "1", "-c", "c"}, Arrays.asList(
              makeStringArg(new String[]{"a"}, true),
              makeIntArg(new String[]{"b"}, true),
              makeStringArg(new String[]{"c"}, true)
      ), "Define three required arguments a, b and c, parse input containing all of them");
   }

   @Test
   public void threeStringArgsRequiredOneMissing(){
      assertInValidInput(new String[]{"-c", "c" ,"-b", "b"}, Arrays.asList(
              makeStringArg(new String[]{"a"}, true),
              makeStringArg(new String[]{"b"}, true),
              makeStringArg(new String[]{"c"}, true)
      ), "Define three required string arguments a, b and c, try parsing input without one");
   }

   @Test
   public void threeArgsCheckOutput(){
      assertOutput(new String[]{"-a", "a", "-b", "b", "-c", "c"}, Arrays.asList(
              makeStringArg(new String[]{"a"}, true),
              makeStringArg(new String[]{"b"}, false),
              makeStringArg(new String[]{"c"}, true)
      ), Arrays.asList(
              checkValues(new String[]{"a"}),
              checkValues(new String[]{"b"}),
              checkValues(new String[]{"c"})
      ), "Define three string arguments a,b and c, parse input, check the arguments' values");

      assertOutput(new String[]{"-b", "b", "-a", "a", "-c", "c"}, Arrays.asList(
              makeStringArg(new String[]{"a"}, true),
              makeStringArg(new String[]{"b"}, false),
              makeStringArg(new String[]{"c"}, true)
      ), Arrays.asList(
              checkValues(new String[]{"a"}),
              checkValues(new String[]{"b"}),
              checkValues(new String[]{"c"})
      ), "Define three string arguments a,b and c, parse input, check the arguments' values");
      assertOutput(new String[]{"-a", "100", "-b", "20", "-c", "c"}, Arrays.asList(
              makeIntArg(new String[]{"a"}, true),
              makeIntArg(new String[]{"b"}, false),
              makeStringArg(new String[]{"c"}, true)
      ), Arrays.asList(
              checkValues(new Integer[]{100}),
              checkValues(new Integer[]{20}),
              checkValues(new String[]{"c"})
      ), "Define three arguments a,b and c, parse input , check the arguments' values");
      assertOutput(new String[]{"-c", "c", "-a", "100", "-b", "20"}, Arrays.asList(
              makeIntArg(new String[]{"a"}, true),
              makeIntArg(new String[]{"b"}, false),
              makeStringArg(new String[]{"c"}, true)
      ), Arrays.asList(
              checkValues(new Integer[]{100}),
              checkValues(new Integer[]{20}),
              checkValues(new String[]{"c"})
      ), "Define three arguments a,b and c, parse input , check the arguments' values");
   }

   @Test
   public void threeArgsDefinedOneMissingInInput(){
      assertOutput(new String[]{"-a", "a", "-c", "c"}, Arrays.asList(
              makeStringArg(new String[]{"a"}, true),
              makeStringArg(new String[]{"b"}, false),
              makeStringArg(new String[]{"c"}, true)
      ), Arrays.asList(
              checkValues(new String[]{"a"}),
              checkIsNotSet(),
              checkValues(new String[]{"c"})
      ), "Define three string arguments a,b and c, parse input, check the argument's values");
   }

   @Test
   public void variadicOptionDeclaration(){
      assertValidInput(new String[]{"-a", "a,b", "-b", "b,c"}, Arrays.asList(
              makeStringArg(new String[]{"a"}, true, ','),
              makeStringArg(new String[]{"b"}, true, ',')
      ), "Defines two string options a and b with comma separator, parses valid input");
      assertValidInput(new String[]{"-a", "1,2", "-b", "3,4"}, Arrays.asList(
              makeIntArg(new String[]{"a"}, true, ','),
              makeIntArg(new String[]{"b"}, true, ',')
      ), "Defines two int options a and b with comma separator, parses valid input");
   }

   @Test
   public void variadicParsing(){
      assertOutput(new String[]{"-a", "a,b", "-b", "b,c"}, Arrays.asList(
              makeStringArg(new String[]{"a"}, true, ','),
              makeStringArg(new String[]{"b"}, true, ',')
              ), Arrays.asList(
                      checkValues(new String[]{"a","b"}),
                      checkValues(new String[]{"b","c"})
              ),"Define two string options a and b with comma separator, parse input '-a a,b -b b,c' and check the values");
      assertOutput(new String[]{"-a", "1,2", "-b", "100,200"}, Arrays.asList(
                      makeIntArg(new String[]{"a"}, true, ','),
                      makeIntArg(new String[]{"b"}, true, ',')
              ), Arrays.asList(
                      checkValues(new Integer[]{1,2}),
                      checkValues(new Integer[]{100,200})
              ),"Define two int options a and b with comma separator, parse input '-a a,b -b b,c' and check the values");

   }

   @Test
   public void ArgInvalidSeparator(){
      assertInvalidDeclaration(List.of(
              makeStringArg(new String[]{"a"}, false, ' ')
      ), "Try to define a string option with a space separator");
      assertInvalidDeclaration(List.of(
              makeIntArg(new String[]{"a"}, false, ' ')
      ), "Try to define an int option with a space separator");
   }

   @Test
   public void ArgBadName(){
      assertInvalidDeclaration(List.of(
              makeStringArg(new String[]{" "}, false)
      ), "Try to define a string option with a space as name");
      assertInvalidDeclaration(List.of(
              makeIntArg(new String[]{" "}, false)
      ), "Try to define an int option with a space as name");
   }
   @Test
   public void ArgConflictingNames(){
      assertInvalidDeclaration(Arrays.asList(
              makeStringArg(new String[]{"a", "b"}, false),
              makeStringArg(new String[]{"b", "a"}, false)
      ), "Try to define two string options with identical names");
      assertInvalidDeclaration(Arrays.asList(
              makeIntArg(new String[]{"a", "b"}, false),
              makeIntArg(new String[]{"b", "a"}, false)
      ), "Try to define two int options with identical names");
      assertInvalidDeclaration(Arrays.asList(
              makeIntArg(new String[]{"a", "b"}, false),
              makeStringArg(new String[]{"b", "a"}, false)
      ), "Try to define string and int options with identical names");
   }

   @Test
   public void ArgParseMultipleNames(){
      assertOutput("--alpha a -b b --gamma val1;val2".split(" "), Arrays.asList(
              makeStringArg(new String[]{"a", "alpha", "another"}, true),
              makeStringArg(new String[]{"beta", "b", "third_name"}, true),
              makeStringArg(new String[]{"g", "gamma"}, true, ';')
      ),
              Arrays.asList(
                      checkValues(new String[]{"a"}),
                      checkValues(new String[]{"b"}),
                      checkValues(new String[]{"val1", "val2"})
              ), "Define three string options with several aliases, parse input and check the values");

      assertOutput("-a a --beta b -g val1;val2".split(" "), Arrays.asList(
                      makeStringArg(new String[]{"a", "alpha", "another"}, true),
                      makeStringArg(new String[]{"beta", "b", "third_name"}, true),
                      makeStringArg(new String[]{"g", "gamma"}, true, ';')
              ),
              Arrays.asList(
                      checkValues(new String[]{"a"}),
                      checkValues(new String[]{"b"}),
                      checkValues(new String[]{"val1","val2"})
              ), "Define three string options with several aliases, parse input and check the values");

      assertOutput("-a a --beta b -g 50;100".split(" "), Arrays.asList(
                      makeStringArg(new String[]{"a", "alpha", "another"}, true),
                      makeStringArg(new String[]{"beta", "b", "third_name"}, true),
                      makeIntArg(new String[]{"g", "gamma"}, true, ';')
              ),
              Arrays.asList(
                      checkValues(new String[]{"a"}),
                      checkValues(new String[]{"b"}),
                      checkValues(new Integer[]{50,100})
              ), "Define three options with several aliases, parse input and check the values");

   }

   @Test
   public void ArgValidatorValid(){
      assertValidInput("--opt1 a --opt2 b --opt3 qwertyuiop,asdfghjkl,zxcvbnm".split(" "), Arrays.asList(
              makeStringArg(new String[]{"opt1"}, true, (s->s.length()<100)),
              makeStringArg(new String[]{"opt2"}, false, s->s.equals("b") || s.equals("c")),
              makeStringArg(new String[]{"opt3"}, true, ',', s->s.length()>5)
      ), "Define three options with a validator, parse valid input");
      assertValidInput("--opt1 101 --opt2 5 --opt3 1,2,1000,5000".split(" "), Arrays.asList(
              makeIntArg(new String[]{"opt1"}, true, s->s>100),
              makeIntArg(new String[]{"opt2"}, false, s->s%5==0),
              makeIntArg(new String[]{"opt3"}, false, ',', s->s<5 || s>50)
      ), "Define three options with a validator, parse valid input");
   }
   @Test
   public void ArgValidatorInvalid(){
      assertInValidInput(new String[]{"--opt1", "a" }, Arrays.asList(
              makeStringArg(new String[]{"opt1"}, true, s->s.length()>100),
              makeStringArg(new String[]{"opt2"}, false, s->s.equals("b") || s.equals("c")),
              makeStringArg(new String[]{"opt3"}, false, ',', s->s.length()>5)
      ), "Define three options with a validator, parse invalid input");
      assertInValidInput(new String[]{"--opt2", "b"}, Arrays.asList(
              makeStringArg(new String[]{"opt1"}, false, (s->s.length()<100)),
              makeStringArg(new String[]{"opt2"}, false, s->s.equals("q") || s.equals("p")),
              makeStringArg(new String[]{"opt3"}, false, ',', s->s.length()>5)
      ), "Define three options with a validator, parse invalid input");
      assertInValidInput(new String[]{"--opt3", "qwertyuiop,asdfghjkl,zxcvbnm"}, Arrays.asList(
              makeStringArg(new String[]{"opt1"}, false, (s->s.length()<100)),
              makeStringArg(new String[]{"opt2"}, false, s->s.equals("b") || s.equals("c")),
              makeStringArg(new String[]{"opt3"}, true, ',', s->s.length()<5)
      ), "Define three options with a validator, parse invalid input");
      assertInValidInput(new String[]{"--opt1", "100" }, Arrays.asList(
              makeIntArg(new String[]{"opt1"}, true, s->s>100),
              makeIntArg(new String[]{"opt2"}, false, s->s%5==0),
              makeIntArg(new String[]{"opt3"}, false, ',', s->s<5 || s>50)
      ), "Define three options with a validator, parse invalid input");
      assertInValidInput(new String[]{"--opt2", "16"}, Arrays.asList(
              makeIntArg(new String[]{"opt1"}, true, s->s>100),
              makeIntArg(new String[]{"opt2"}, false, s->s%5==0),
              makeIntArg(new String[]{"opt3"}, false, ',', s->s<5 || s>50)
      ), "Define three options with a validator, parse invalid input");
      assertInValidInput(new String[]{"--opt3", "20,25,30"}, Arrays.asList(
              makeIntArg(new String[]{"opt1"}, true, s->s>100),
              makeIntArg(new String[]{"opt2"}, false, s->s%5==0),
              makeIntArg(new String[]{"opt3"}, false, ',', s->s<5 || s>50)
      ), "Define three options with a validator, parse invalid input");
   }

   @Test
   public void ArgValidatorSomeValid(){
      assertInValidInput(new String[]{"--opt1", "a;bb;c;dd"},
              List.of(
                      makeStringArg(new String[]{"opt1"}, true, (s -> s.length() == 1))
              ), "Define a variadic string option, parse input with some provided values valid, some invalid");
      assertInValidInput(new String[]{"--opt1", "100;116;27"},
              List.of(
                      makeIntArg(new String[]{"opt1"}, true, (s -> s % 99 == 1))
              ), "Define a variadic int option, parse input with some provided values valid, some invalid");

   }

   @Test
   public void stringArgVariadicEmptyEntry(){
      assertOutput(new String[]{"--opt1", "a,,cccc"}, List.of(
                      makeStringArg(new String[]{"opt1"}, false, ',')),
              List.of(
                      checkValues(new String[]{"a", "", "cccc"})
              ), "Define a variadic string option, pass an empty string as one of its values"
      );
   }

   @Test
   public void intArgVariadicNegativeNumbers(){
      assertOutput("--opt1 -1,-2,-3 --opt2 -12789".split(" "), Arrays.asList(
              makeIntArg(new String[]{"opt1"}, true, ','),
                      makeIntArg(new String[]{"opt2"}, true)),
              Arrays.asList(
                      checkValues(new Integer[]{-1,-2,-3}),
                      checkValues(new Integer[]{-12789})
              ), "Declares two int options, parses input with negative values"
      );
   }

   @Test
   public void intArgNotInt(){
      assertInValidInput(new String[]{"--opt1", "a"}, List.of(
              makeIntArg(new String[]{"opt1"}, true)
      ), "Define an int option, pass it a non-number argument");
      assertInValidInput(new String[]{"--opt1", "1.56"}, List.of(
              makeIntArg(new String[]{"opt1"}, true)
      ), "Define an int option, pass it a non-integer argument");
      assertInValidInput(new String[]{"a"}, List.of(
              makeIntArg(new String[]{}, true)
      ), "Define a plain int arg, pass it a non-number argument");
   }

   @Test
   public void ArgsPlain(){
      assertOutput(new String[]{"abc", "def"}, Arrays.asList(
              makeStringArg(new String[]{}, true), makeStringArg(new String[]{}, true)),
              Arrays.asList(
                      checkValues(new String[]{"abc"}),
                      checkValues(new String[]{"def"})
              ), "Define two plain string args, check their values"
      );
      assertOutput(new String[]{"123", "456"}, Arrays.asList(
                      makeIntArg(new String[]{}, true), makeIntArg(new String[]{}, true)),
              Arrays.asList(
                      checkValues(new Integer[]{123}),
                      checkValues(new Integer[]{456})
              ), "Define two plain int args, check their values"
      );
      assertOutput(new String[]{"abc", "456"}, Arrays.asList(
                      makeStringArg(new String[]{}, true), makeIntArg(new String[]{}, true)),
              Arrays.asList(
                      checkValues(new String[]{"abc"}),
                      checkValues(new Integer[]{456})
              ), "Define two plain string and int args, check their values"
      );
   }

   @Test
   public void ArgsPlainNotEnough(){
      assertInValidInput("abc def".split(" "), Arrays.asList(
              makeStringArg(new String[]{}, true),
              makeStringArg(new String[]{}, true),
              makeStringArg(new String[]{}, true)
      ), "Defines three required plain arguments, parses string with only two");
   }

   @Test
   public void argsPlainVariadic(){
      assertOutput(new String[]{"abc", "def,ijk,klm,opq"}, Arrays.asList(
                      makeStringArg(new String[]{}, true), makeStringArg(new String[]{}, true, ',')),
              Arrays.asList(
                      checkValues(new String[]{"abc"}),
                      checkValues(new String[]{"def","ijk","klm","opq"})
              ), "Define two plain string args one of which is variadic, check their arguments"
      );
   }
   @Test
   public void requiredPlainArg(){
      assertOutput(new String[]{"abc"}, List.of(
              makeStringArg(new String[]{}, true)
      ), List.of(
              checkValues(new String[]{"abc"})
      ), "Defines three plain arguments, two are optional, parses input with only one value");
   }


   @Test
   public void interleaving(){
      assertOutput("abcd --o1 ab 36 --o2 option2 --o3 35 abc,def,ghi".split(" "),Arrays.asList(
              makeStringArg(new String[]{"o2"}, true),
              makeStringArg(new String[]{"o4"}, false),
              makeIntArg(new String[]{"o3", "o33"}, false),
              makeStringArg(new String[]{}, false),
              makeStringArg(new String[]{"o1"}, false),
              makeIntArg(new String[]{}, false),
              makeStringArg(new String[]{}, true, ',')
      ),Arrays.asList(
              checkValues(new String[]{"option2"}),
              checkIsNotSet(),
              checkValues(new Integer[]{35}),
              checkValues(new String[]{"abcd"}),
              checkValues(new String[]{"ab"}),
              checkValues(new Integer[]{36}),
              checkValues(new String[]{"abc","def","ghi"})
      ),"Defines many arguments and parses an input with plain args interleaving with values");

      assertOutput("abcd --o1 ab 36 --o2 option2 --abc abc --o3 35 -- --abc,def,ghi".split(" "),Arrays.asList(
              makeStringArg(new String[]{"o2"}, true),
              makeStringArg(new String[]{"o4"}, false),
              makeIntArg(new String[]{"o3", "o33"}, false),
              makeStringArg(new String[]{}, false),
              makeStringArg(new String[]{"o1"}, false),
              makeStringArg(new String[]{"abc"}, true),
              makeIntArg(new String[]{}, false),
              makeStringArg(new String[]{}, true, ',')
      ),Arrays.asList(
              checkValues(new String[]{"option2"}),
              checkIsNotSet(),
              checkValues(new Integer[]{35}),
              checkValues(new String[]{"abcd"}),
              checkValues(new String[]{"ab"}),
              checkValues(new String[]{"abc"}),
              checkValues(new Integer[]{36}),
              checkValues(new String[]{"--abc","def","ghi"})
      ),"Defines many arguments and parses an input with plain args interleaving with values, including an argument after the -- delimiter");

   }
   @Test
   public void trailNoArgs(){
      Manager m = new Manager(new String[]{"abc", "def", "ghi"});
      try{
         assertArrayEquals(m.getTrailingArguments(), new String[]{"abc", "def", "ghi"}, "Value mismatch in trail");
      }catch(ArgumentException e){
         fail("Unexpected exception during parsing arguments");
      }
   }

   @Test
   public void trailWithArgs(){
      Manager m = new Manager("abc -o 111 def --opt2 opt2 ghi jkl -- -a -b".split(" "));
      try{
         var arg1 = m.makeArgument(StringArgument.class).addName("opt2").finishGeneral().required();
         var arg2 = m.makeArgument(IntegerArgument.class).addName("o").addName("a").finishGeneral().required();
         var arg3 = m.makeArgument(StringArgument.class).finishGeneral().required();
         var arg4 = m.makeArgument(StringArgument.class).finishGeneral().required();

         assertEquals(arg1.getValue(0), "opt2");
         assertEquals(arg2.getValue(0).intValue(), 111);
         assertEquals(arg3.getValue(0), "abc");
         assertEquals(arg4.getValue(0), "def");
         assertArrayEquals(m.getTrailingArguments(), new String[]{"ghi", "jkl", "-a", "-b"});
      }catch (ArgumentException e){
         fail("Unexpected exception during parsing arguments");
      }
   }

   @Test
   public void Description(){
      Manager m = new Manager(new String[]{});
      try {
         var arg = m.makeArgument(StringArgument.class).setDescription("Dummy description");
         arg.finishGeneral();
         assertTrue(m.getHelp().length()>0, "Description doesn't exist");
      }catch(ArgumentException e){
         fail("Unexpected exception during adding arguments");
      }

   }



   
   public void assertValidInput(String[] user_input, List<ArgCreator<? extends SimpleArgument>> specs, String description){
      Manager m = new Manager(user_input);
      for (ArgCreator ac : specs){
         try{
            ac.make(m);
         }
         catch(ArgumentException e){
            fail("Encountered unexpected argument exception during adding arguments in test " + description);
         }
      }
      try{
         m.parseArguments();
      }
      catch(ArgumentException e){
         fail("Encountered unexpected argument exception during parsing in test " + description);
      }
   }

   public void assertInValidInput(String[] user_input, List<ArgCreator<? extends SimpleArgument>> specs, String description){
      Manager m = new Manager(user_input);
      for (ArgCreator ac : specs){
         try{
            ac.make(m);
         }
         catch(ArgumentException e){
            fail("Encountered unexpected argument exception during adding arguments in test " + description);
         }
      }
      assertThrows(ArgumentException.class, m::parseArguments,"No exception encontered during parsing an intentionally invalid input in test " + description);
   }


   public void assertOutput(String[] user_input, List<ArgCreator<? extends SimpleArgument>> specs, List<ArgChecker<SimpleArgument>> output, String description){
      Manager m = new Manager(user_input);
      ArrayList<SimpleArgument> args = new ArrayList<>();
      for (ArgCreator<? extends SimpleArgument> ac : specs){
         try{
            args.add(ac.make(m));
         }
         catch(ArgumentException e){
            fail("Encountered unexpected argument exception during adding arguments in test " + description);
         }
      }
      try{
         m.parseArguments();
      }
      catch(ArgumentException e){
         fail("Encountered unexpected argument exception during parsing in test " + description + "\n" + e.getMessage());
      }

      int index = 0;
      for (ArgChecker<SimpleArgument> ac : output){
         try {
            assertTrue(ac.check(args.get(index++)), "Mismatched ouput of argument at index " + index + " in test " + description);
         }
         catch (ArgumentException e) {
            fail("Unexpected exception when checking output in test " + description);
         }
      }
   }

   public void assertInvalidDeclaration(List<ArgCreator<? extends  SimpleArgument>> specs, String description){
      assertThrows(ArgumentException.class, ()->{
         Manager m = new Manager(new String[]{});
         for (ArgCreator<? extends SimpleArgument> ac : specs){
               ac.make(m);
         }
      }, "Intentionally invalid option declaration doesn't trigger an exception in test " + description);
   }


}
