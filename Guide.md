
# BotScript Guide

BotScript has been created for beginners, but hopefully has something for people of all skill levels! This guide will go over some coding basics to get you started, and act as a language reference.

### Contents
- [First things first](#first-things-first)
- [Commenting out code](#commenting-out-code)
- [Values](#values)
- [Declaring Variables](#declaring-variables)
- [Operators](#operators)
- [Functions](#functions)
- [Defining Functions](#defining-functions)
- [While Loop](#while-loop)
- [If/Else Statement](#ifelse-statement)
- [Casting](#casting)
- [Images](#images)
- [Image Examples](#image-examples)
- [Errors](#errors)
- [Notes](#notes)


### First things first

The first thing coders do when learning a new language, is to write "Hello World" on the screen.

In BotScript it is quite easy! Simply type `print("Hello World")` !

 ### Commenting out code
 Sometimes, you want to write a note, description, or label in your code.
 Add `%` at the start of any line, and it will be completely ignored as code.
  
 Note: The `%` symbol is also used to represent the modulo operator. Make sure the `%`  symbol is the very first character of a line if you want to write a comment on that line!
 ```
 print("hello")
 % This line will be ignored
 print("world")
 % print("this line will also be ignored")
 ```

### Values

Now you're acquainted with the `print()` function, let's try some other inputs.

BotScript features a range of different value types.

-- String : A string is a sequence of characters. For example, `"Hello World"` is a string! `"123"` is also a valid string, even though it contains numbers.

-- Integer : Just like in maths, an integer is a whole number. e.g. `5` , `-49` , `123456` , `0`

-- Float : A number that contains a decimal point. e.g. `1.9` , `0.0` , `-123.456` , `29.29` , `99.99999`

-- Boolean : Like a yes/no answer, or an on/off switch, booleans have 2 possible values: `true` or `false` .

-- Array : Arrays are ordered lists of values. BotScript is lenient with arrays. Arrays can contain any type of value, including other arrays. They can also be entirely empty. For example:

-  `["Hello", "World"]`

-  `[1,2,3,4,5]`

-  `[true, false]`

-  `["abc", 1, 3.0, true, ["def", "ghi"], false]`

-  `[]` 
  
-- Image: An image can be stored as a value in BotScript. You can create an image value with the function `createImage(x,y)`

--  Null : A null is an empty value, and BotScript doesn't generally allow you to create these. If you are encountering Null type values, you may have errors in your code. 

 ### Declaring Variables
To declare a variable, follow these examples:

Note: The variable's name must only contain alphanumeric characters.
```
% Output:
% 15

x = 10
y = 5
print(x+y)
```
```
% Output:
% 10
% hello

z = 10
print(z)

z = "hello"
print(z)
```
### Operators
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
% Output: 12
print( ( 2 + 2 ) * 3 )
```

```
% Output: 1
print(5 % 2)
```

```
% Output: false
print(true & false)
```
```
% Output: true
print(true | false)
```
```
% Output: true
print(1 + 1 = 2)
```
```
% Output: true
print(5.0 > 3)
```

```
% Output: true
print(["a","b"] = ["a","b"])
```

```
% Output: false
print(!true)
```

```
% Output: 2
print(4/2)
```

```
% Note: division results in a float, not an integer
% Output: 2.5
print(5/2)
```

```
% Output: 5.0
print(2.5 * 2)
```

### Functions

Functions are pieces of code which can be used whenever they are needed, by referring to them by name.

BotScript has some useful functions which will be handy for your programming.

#### General Functions
- You've already encountered the `print()` function. `print(x)` will write or "print" `x` to the console.

-  `length(x)` will get you the number of elements in an array, or the size of a string, depending on what you put into it! e.g. `length(["a","b"])` will return `2`, and `length("Hello World")` will return `11`.

-  `charAt(str,int)` will return the character found at location `int` of the string `str`. For example, `charAt("Hello world",8)` will return `r`. Keep in mind, computers start counting indexes at 0, not 1.

-  `type(x)` will return a string that describes what type of value `x` is. `type("Hello")` will return `"STRING"`, `type(1.0)` returns `"FLOAT"`.

#### Array Functions
- `add(arr,x)` adds value `x` to array `arr`
- `remove(arr,int)` removes element at position `int` of the array `arr`. Keep in mind, computers start counting indexes at 0, not 1.
-  `get(arr,int)` will get the element at position `int` of the array `arr`. `get([123,456,789], 1)` will return `456`. Again, count the index from 0 not 1.
- `set(arr,int,x)` replaces the element at position `int` of the array `arr` with a new value, `x`.

#### Image Functions
- `createImage(x,y)` returns a new Image value with width `x` and height `y`.
- `setPixel(img,x,y,r,g,b)` sets the pixel at position `x,y` in Image `img` to a new colour value `r,g,b`.
- `getPixel(img,x,y)` gets the pixel at position `x,y` in Image `img` and returns its RGB colour as an array of integers in the form `[R,G,B]`. 
- `getDimensions(img)` returns the dimensions of Image `img` as an array in the form `[width,height]`.
- `setCanvas(img)` sets the output canvas to the image `img` At the end of execution, the canvas is rendered and displayed with the console output. Don't forget to call `canvasVisible(true)` if you want to display the canvas.
- `canvasVisible(bool)` determines whether the canvas is displayed. if Boolean `bool` is true, the canvas will be displayed. The default value is `false`, so don't forget to call this function if you want to display your canvas!

#### Casting Functions
- `castString(x)` casts value `x` to a string and returns it.
- `castInteger(x)` casts value `x` to an integer and returns it.
- `castFloat(x)` casts value `x` to a float and returns it.
- `castBoolean(x)` casts value `x` to a boolean and returns it.

#### Maths Functions
-  `random()` will return a random float between 0.0 and 1.0.
- `sin(x)` returns the sine of a Float angle `x` between 0.0 and pi, as a Float.
- `cos(x)` returns the cosine of a Float angle `x` between 0.0 and pi, as a Float.
- `pow(b,p)` returns the Float result of the exponent `b^p`.

 

### Defining Functions

If you find yourself writing the same piece of code over and over again, it might be more efficient to turn it into a function!

To write a function which will return the sum of two numbers, you would type the following:

Note: The function's name must only contain alphanumerics.
```
function sum(a,b){
return a + b
}
```

having defined this function, calling `sum(2,5)` will now return `7`.

It is optional whether your function takes any parameters (`a,b` above.)
and also optional whether it returns anything (`return a + b` above.) 

Examples:

```
% Output:
% hi

function printHi(){
print("hi")
}

printHi()
```

```
function multiply(x,y){
return x * y
}
```

```
% Output:
% Hi
% Hi
% Hi

% Prints a string multiple times 
function multiPrint(str, int){
i = 0
while(i < int){
print(str)
i = i + 1
}
}

multiPrint("Hi",3)
```
  
### While Loop
In the last example, you might've noticed the while loop:
```
while(condition){
% Do something
}
```
The while loop repeatedly runs the code inside the curly braces, while the condition is `true`. For example:
```
% Output:
% 5
% 4
% 3
% 2
% 1
% Lift off!

a = 5
while(a > 0){
print(a)
a = a - 1
}
print("Lift off!")
```
```
% Output:
% H
% o
% w
% d
% y

word = "Howdy"
i = 0
while(i < length(word)){
print( charAt(word, i) )
i = i + 1
}
```

### If/Else Statement
```
if(condition){
% Do something
}
```
An if statement only executes the code in the curly braces if the condition is `true`.  
Optionally, an `else{}` block can be added, which will be executed if the condition is false. For example:
```
% Output:
% That's more like it!

if(1>2){
print("This can't be right!")
}

if(1<2){
print("That's more like it!")
}
```
```
% Output:
% The conditional is false, so the else block is executed instead

if(2+2=5){
print("Orwell would be proud.")
}else{
print("The conditional is false, so the else block is executed.")
}
```

### Casting
You may wish to convert a value from one type to another. This can be done by casting. Here are the functions which allow you to cast:
`castInteger()`, `castFloat()`,`castString()`,`castBoolean()`

Some examples:
```
% Output: 
% 101
% 11

print("10" + 1)
print(castInteger("10") + 1)
```
```
% Output: casting successful

result = castBoolean("true")
if(result){
print("casting successful")
}
```
This table shows what types can be cast to each other
|<p> From **→**</p> <p>To **↓**</p> |String  |Boolean  |Integer  |Float |
|--|--|--|--|--|
|String   |Yes|Yes|Yes|Yes|
|Boolean  |Yes - If string is "true" or "false" |Yes |Yes - returns true if integer is not 0 |Yes - casts float to integer and returns true if not 0  |
|Integer  |Yes - if string is valid integer e.g. "12" |No  | Yes |Yes - Decimal part of float is removed e.g. 3.9 becomes 3|
|Float    |Yes - if string is valid float e.g. "3.9"  |No  | Yes |Yes |

### Images
BotScript has support for image values, which can be rendered to the canvas and displayed along with the text output.

Images are essentially big 2D arrays of pixels, where each pixel is located at an x,y coordinate, and stores an array of integers representing its colour in RGB.

#### Input an image
If you are writing a script for a CodeBot comment section, you may also attach an image to your comment, and it will be loaded into your program! It will be assigned to the varible name `input`. You can manipulate it just like any other image. 

![205505315_2593998520901770_4454234929828484802_n](https://user-images.githubusercontent.com/17404588/127153720-215c145e-e140-4353-bd90-e444d706b0cd.png)

<img width="482" alt="a" src="https://user-images.githubusercontent.com/17404588/127153741-10f8c63e-d604-47e5-ba71-db22a3aefca4.png">


There are several images included in the program by default:
`sun`, `monky`, `alphabet`, `fiordland`, `kapiti`, `kiwi`, `pohutukawa`, `tui`.
`alphabet` is a character map that can be used to render a simple pixel font to an image:

<img width="288" alt="alphabet" src="https://user-images.githubusercontent.com/17404588/127492893-b803ae6d-3827-462f-8389-bef80e20001d.png">


#### Create an image
`myImage = createImage(10,10)` creates a blank 10x10 image.
#### Change pixels
`setPixel(myImage, 0,0, 255,255,255)` sets the pixel at location `x=0`, `y=0` of `myImage` to the RGB colour white (`255,255,255`).
#### Get the values of a pixel
`getPixel(myImage,5,5)` gets the RGB values of a pixel as an array value in the form `[R,G,B]`
#### Display an image
`setCanvas(myImage)` sets the output canvas to the image we made.
`canvasVisible(true)` tells the program to display the canvas. 

### Image Examples
You may find it helpful to use these examples as templates

#### Magenta
```
img = createImage(100,100)
% loop through all x values
x=0
while(x<100){

% loop through all y values
y=0
while(y<100){
% set each pixel to magenta 
setPixel(img, x, y,255,0,255)
y = y + 1
}

x = x + 1
}

setCanvas(img)
canvasVisible(true)
```
Output:

![Magenta](https://user-images.githubusercontent.com/17404588/120744578-6c009d80-c54f-11eb-8d65-94487815afcc.png)


#### Blue Noise
```
img = createImage(100,100)
% loop through all x values
x=0
while(x<100){

% loop through all y values
y=0
while(y<100){
% create a random number between 0 and 255 
value = castInteger( random()*255 )
if(value > 255){ value = 255 }
% set the B values of the pixel to the random number,
% and the R and G values to 0. 
setPixel(img, x, y,0,0,value)
y = y + 1
}

x = x + 1
}

setCanvas(img)
canvasVisible(true)
```
Output:

![BlueNoise](https://user-images.githubusercontent.com/17404588/120744456-280d9880-c54f-11eb-958d-84a354216a32.png)


### Errors
If your syntax is incorrect, or you try to do something that is not allowed in BotScript, you will probably get an error. A (hopefully useful) description of the error will be printed to the console, along with the line it occurred at. Your code execution will then stop. Here are some examples of code which may cause errors. 
```
% Can't add integer to boolean
print(1+true)
```
```
% Can't compare string to integer
print("Hello" > 10)
```
```
% Incomplete statement
a =
```
```
% Invalid function name (Alphabet characters only)
function test1(){
} 
```
```
% Not a valid statement
6
```
```
% Can't call "length()" on a boolean value
print(length(true))
```

### Notes
- Newline characters are an important part of the syntax of BotScript. Facebook comments don't support indentation, so indentation is not supported.


#### Limitations
Because BotScript was designed to run in a Facebook comments section, it has some limitations that you may not expect from other scripting languages.

- Output length - Facebook doesn't allow comments of more than 8000 characters. CodeBot has to limit the console output of every script that runs. If a script prints out more than 4000 characters, an error will be thrown and execution ends. 
- Image size - Similarly, CodeBot limits Images to a maximum size of 2000x2000 pixels. Images attached to a comment which are larger will still be loaded, but they will be trimmed down. Attempting to create an image larger than 2000x2000 will cause an error to be thrown.
- Execution time - CodeBot is run on a PC and needs to share processing time between all the commented scripts, so the execution time is limited to 5 minutes per script, which should hopefully be ample time to render some pretty fractals or mine a billionth of a Bitcoin or something.
