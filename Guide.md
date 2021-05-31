# BotScript Guide

BotScript has been created for beginners, but hopefully has something for people of all skill levels! This guide will go over some coding basics to get you started, and act as a language reference.

#### First things first

The first thing coders do when learning a new language, is to write "Hello World" on the screen.

In BotScript it is quite easy! Simply type `print("Hello World")` !

 #### Commenting
 Sometimes, you want to write a note, description, or label in your code.
 Add `# ` at the start of any line, and it will be completely ignored as code. Remember to leave a space after the hashtag!
 ```
 print("hello")
 # This will be ignored
 print("world")
 # print("this line will also be ignored")
 ```

#### Variables

Now you're acquainted with the `print()` function, let's try some other inputs.

BotScript features 5 different variable types.

-- String : A string is a sequence of characters. For example, `"Hello World"` is a string! `"123"` is also a valid string, even though it contains numbers.

-- Integer : Just like in maths, an integer is a whole number. e.g. `5` , `-49` , `123456` , `0`

-- Float : A number that contains a decimal point. e.g. `1.9` , `0.0` , `-123.456` , `29.29` , `99.99999`

-- Boolean : Like a yes/no answer or a switch, booleans have 2 possible values: `true` or `false` .

-- Array : Arrays are ordered lists of variables. BotScript is lenient with arrays. Arrays can contain any type of variable, including other arrays. They can also be entirely empty. For example:

-  `["Hello", "World"]`

-  `[1,2,3,4,5]`

-  `[true, false]`

-  `["abc", 1, 3.0, true, ["def", "ghi"], false]`

-  `[]` 
  
--  Null : A null is an empty variable, and BotScript doesn't generally allow you to create these. If you are encountering Null type variables, you may have errors in your code. 

 #### Declaring Variables
To declare a variable, follow these examples:

Note: The variable's name must only contain upper/lower case characters i.e. A-Z, a-z
```
# Output:
# 15

variable x = 10
variable y = 5
print(x+y)
```
```
# Output:
# 10
# hello

variable z = 10
print(z)

# Note: The variable `z` has already been declared, and it can be reassigned without using the `variable` keyword
z = "hello"
print(z)
```
#### Operators
Operators allow you to manipulate values. Here is a list of the operators supported in BotScript:
|Operator  |Name  |Description  |Usage|
|--|--|--|--|
|+  |Addition		|Add two numbers together  |	A + B|
|+  |String concatenation|Add two strings together. This mode is used when one or both side(s) of the `+` is a string|	A + B|
|-  |Subtraction  	|Subtract a number from another |A - B	|
|*  |Multiplication |Multiply two numbers  |	A * B|
|/  |Division  		|Divide A by B  |A / B	|
|%  |Modulus  		|Returns the remainder from dividing A by B  | A % B	|
|>  |Greater Than	|Returns true if A is greater than B | A > B	|
|<  |Less Than  	|Returns true if A is less than B  |A < B	|
|=  |Equals  		|  Returns true if A is equal to B (works for all types)| A = B |
|&  |And  			|Returns true if A and B are both true  |A & B	|
|\| |Or  			|Returns true if either A or B are true  |A \| B |
|! 	|Not  			|Returns the opposite of a boolean  |! A	|


Here are some examples:

Note:  You can use curved brackets `()` to ensure the order of operations is handled correctly.

```
# Output: 1
print(5 % 2)
```

```
# Output: false
print(true & false)
```
```
# Output: true
print(true | false)
```
```
# Note: It's best to use brackets here to ensure the order of operations is correct.
# Output: true
print((1 + 1) = 2)
```
```
# Output: true
print(5.0 > 3)
```

```
# Output: true
print(["a","b"] = ["a","b"])
```

```
# Output: false
print(!true)
```

```
# Output: 2
print(4/2)
```

```
# Note: division results in a float, not an integer
# Output: 2.5
print(5/2)
```

```
# Output: 5.0
print(2.5 * 2)
```

#### Functions

Functions are pieces of code which can be used whenever they are needed, by referring to them by name.

BotScript has some useful functions which will be handy for your programming.

- You've already encountered the `print()` function. `print(x)` will write or "print" `x` to the console.

-  `random()` will return a random float between 0.0 and 1.0.

-  `length()` will get you the number of elements in an array, or the size of a string, depending on what you put into it! e.g. `length(["a","b"])` will return `2`, and `length("Hello World")` will return `11`.

-  `charAt(str,int)` will return the character found at location `int` of the string `str`. For example, `charAt("Hello world",8)` will return `r`. Keep in mind, computers start counting indexes at 0, not 1.

-  `get(arr,int)` will get the element at location `int` of the array `arr`. `get([123,456,789], 1)` will return `456`. Again, count the index from 0 not 1.

-  `type(x)` will return a string that describes what type of variable `x` is. `type("Hello")` will return `"STRING"`, `type(1.0)` returns `"FLOAT"`.

#### Defining Functions

If you find yourself writing the same piece of code over and over again, it might be more efficient to turn it into a function!

To write a function which will return the sum of two numbers, you would type the following:

Note: The function's name must only contain upper/lower case characters i.e. A-Z, a-z
```
function sum(a,b){
return a + b
}
```

having defined this function, calling `sum(2,5)` will now return `7`.

It is optional whether your function takes any parameters (`a,b` above),
and also optional whether it returns anything (`return a + b` above  ) 

Examples:
```
# Output:
# Hi
# Hi
# Hi

# Prints a string multiple times 
function multiPrint(str, int){
variable i = 0
while(i < int){
print(str)
i = i + 1
}
}

multiPrint("Hi",3)
```

```
# Output:
# hi

function printHi(){
print("hi")
}

printHi()
```
  
#### While Loop
In the last example, you might've noticed the while loop:
```
while(condition){
# Do something
}
```
The while loop repeatedly runs the code inside the curly braces, while the condition is `true`. For example:
```
# Output:
# 5
# 4
# 3
# 2
# 1
# Lift off!

variable a = 5
while(a > 0){
print(a)
a = a - 1
}
print("Lift off!")
```
```
# Output:
# H
# o
# w
# d
# y

variable word = "Howdy"
variable i = 0
while(i < length(word)){
print( charAt(word, i) )
i = i + 1
}
```

#### If/Else Statement
```
if(condition){
# Do something
}
```
An if statement only executes the code in the curly braces if the condition is `true`.  
Optionally, an `else{}` block can be added, which will be executed if the condition is false. For example:
```
# Output:
# That's more like it!

if(1>2){
print("This can't be right!")
}

if(1<2){
print("That's more like it!")
}
```
```
# Output:
# The conditional is false, so the else block is executed instead

if(2+2=5){
print("Orwell would be proud.")
}else{
print("The conditional is false, so the else block is executed.")
}
```

#### Casting
You may wish to convert a variable from one type to another. This can be done by casting. Write the type you wish to cast a value to in brackets before the value, like these examples:

```
# Output: 
# 101
# 11

variable asString = "10" + 1
print(asString)

variable asInteger =  ((integer) "10") + 1
print(asInteger)
```
```
# Output: casting successful
variable result = (boolean) "true"
if(result){
print("casting successful")
}
```
This table shows what types can be cast to each other
|From **→** **↓** To |String  |Boolean  |Integer  |Float| Array |
|--|--|--|--|--|--|
|String   |Yes|Yes|Yes|Yes|Yes|
|Boolean  |Yes - If string is "true" or "false" |Yes |Yes - returns true if integer is not 0 |Yes - casts float to integer and returns true if not 0  |No  |
|Integer  |Yes - if string is valid integer e.g. "12" |No  | Yes |Yes - Decimal part of float is removed e.g. 3.9 becomes 3| No |
|Float    |Yes - if string is valid float e.g. "3.9"  |No  |   Yes |Yes | No |
|Array    |No|No  |No     |No  |Yes |

#### Errors
If your syntax is incorrect, or you try to do something that is not allowed in BotScript, you will probably get an error. A (hopefully useful) description of the error will be printed to the console, and your code execution will stop. Here are some examples of code which may cause errors. 
```
# Can't add integer to boolean
print(1+true)
```
```
# Can't compare string to integer
print("Hello" > 10)
```
```
# Incomplete statement
variable a =
```
```
# Invalid function name (Alphabet characters only)
function test1(){
} 
```
```
# Not a valid statement
6
```
```
# Can't call "length()" on a boolean value
print(length(true))
```
