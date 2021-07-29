
# CodeBot
![GitHub](https://img.shields.io/github/license/JoshBolitho/CodeBot) ![GitHub last commit](https://img.shields.io/github/last-commit/JoshBolitho/CodeBot) ![GitHub issues](https://img.shields.io/github/issues/JoshBolitho/CodeBot) ![GitHub issues](https://img.shields.io/github/issues-raw/JoshBolitho/CodeBot)

CodeBot is a Java Facebook bot which turns the [CodeBot Facebook page](https://facebook.com/CodeBotOfficial)'s comment section into an interactive coding playground!
It parses Facebook comments as [BotScript](#What-is-Botscript) code, executes them, and replies with the output.

CodeBot is hosted at [facebook.com/CodeBotOfficial](https://facebook.com/CodeBotOfficial)

<div>
	<img width="279" alt="hello world" src="https://user-images.githubusercontent.com/17404588/123983562-13220900-da18-11eb-9818-610564a17fe4.PNG">
</div>

### Contents
 * [What is BotScript?](#what-is-botscript)
 * [Features](#features)
 * [Setup](#setup)
	 * [Download and install](#download-and-install)
	 * [Acquire API keys](#acquire-api-keys)
	 * [Create a post](#create-a-post)
 * [Usage](#usage)
 * [How do I learn BotScript?](#how-do-i-learn-botscript)
 * [Why make CodeBot?](#why-make-codebot)
 * [How does it work?](#how-does-it-work)


## What is BotScript?
BotScript is a simple scripting language, designed to be used in a Facebook comments section. If you want to write code on the [CodeBot Facebook page](https://facebook.com/CodeBotOfficial), you need to learn the [basics of BotScript](https://github.com/JoshBolitho/CodeBot/blob/main/Guide.md). 

## Features
For more information about using BotScript, check out [the guide](https://github.com/JoshBolitho/CodeBot/blob/main/Guide.md).

### Variables
Defining Variables is as simple as writing `x = 10`. 
Variables can be any of the following types: 
- String `"Example"`
- Integer `123`
- Float `3.14159`
- Boolean `true`
- Array `[1, 2, 0.5, true, "Example", [3, 4]]`
- Image `createImage(100,100)`
### Images 
BotScript supports image variables, which can be rendered to the canvas and displayed along with the text output.
Currently, there are two 500x500 image variables that are included in the program by default: `sun` and `monky`.

<img width="288" alt="Sun" src="https://user-images.githubusercontent.com/17404588/124071417-62594f80-da93-11eb-95e6-a50943856d8a.PNG">

Here are some of the things you can do with Images
- `myImage = createImage(10,10) creates a 10x10 image.`
- `setPixel(myImage, 0,0, 255,255,255) sets the pixel at location x=0, y=0 of myImage to the RGB colour white (255,255,255).`
- `getPixel(myImage,5,5) gets the RGB values of a pixel as an array variable in the form [R,G,B]`
- `setCanvas(myImage) sets the output canvas to the image we made. canvasVisible(true) tells the program to display the canvas.`

Here's an example of some code which creates an image:
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
```
Output:

![Magenta](https://user-images.githubusercontent.com/17404588/120744578-6c009d80-c54f-11eb-8d65-94487815afcc.png)



### Operators
BotScript has all the standard operators you'd expect to see in a simple scripting language. 

- Maths: `+`  `-` `*`  `/` `%` `()`
- Comparator: `>` `<` `=` 
- Boolean: `&` `|` `!`

### Functions 
You can define and use functions like this:
```
function sum(a,b){
return a + b
}

print(sum(1,2))

```
Recursion is also supported
```
function fact(x){
if(x=1){
return x
}
return x * fact(x-1)
}

print(fact(5))
```

As well as writing your own functions, BotScript comes with a bunch of functions already defined, such as `print()`, `cos()`, `length()`, `getPixel()`, and much more! [Here is the full list of functions that come with BotScript.](https://github.com/JoshBolitho/CodeBot/blob/main/Guide.md#functions) 

### If/Else Block
If statements can be created, with optional else statements
```
if(1>2){
print("wrong!")
}else{
print("right!")
}
```

### While Block
While loops can be created too
```
i=0
While(i<10){
print(i)
i=i+1
}
```

### Casting
BotScript includes several casting functions. [Instructions can be found in the guide](https://github.com/JoshBolitho/CodeBot/blob/main/Guide.md#casting).
- `castInteger()`
- `castFloat()`
- `castString()`
- `castBoolean()`

### Error Handling
CodeBot will catch errors on the line they occur at, printing the current location and the error. 
```
function throwError(){
print()
}
throwError()
```
```
Execution error at: print()
print: Wrong number of parameters: expecting 1, received 0
```

### Commenting
Code comments can be written by starting a line with a `%`
```
print("Hello")
% This line is a comment and will not be executed
print("World")
```

## Setup
This project is designed to run on my computer, and control the [CodeBot Facebook page](https://facebook.com/CodeBotOfficial), but if you wanted, you could take it and run your own page! You can also use this project to test your BotScript code locally. 

### Download and install
```
git clone https://github.com/JoshBolitho/CodeBot.git
```
Open in your IDE of choice and make sure to add JUnit5.4 to your Project

### Acquire API keys
This project requires a set of access tokens from multiple services. 
The following keys must be added in place of the empty values in `config.json`
```
{
"user_access_token": "",
"page_access_token": "",
"page_ID": "",
"cloudinary_upload_preset": ""
}
```
You will need:
- Facebook access tokens for posting on the page you wish to automate. I found [this guide](https://github.com/Boidushya/FrameBot/blob/master/generateToken.md) incredibly helpful.
- A cloudinary account, with an unsigned upload preset for temporary image storage. You can create a new upload preset in `Settings > Upload > Upload Presets`.

![Cloudinary](https://user-images.githubusercontent.com/17404588/123985207-621c6e00-da19-11eb-8bb0-ab0d75f0b2e1.png)


**Note**: API keys are only required to run CodeBot.main(), which is for running a Facebook page with CodeBot. If you just wish to use ScriptExecutor.main() to play around with BotScript locally, you don't need any API keys.

### Create a post
Information about the currently active post is stored in `post.json`.  `CodeBot.main()` can't get comments from a post unless the `currentPost` value is set. 

To create a post, change `mode` in `CodeBot.main()` to `"Post"`, and change `message` to whatever you want.
```
String mode = "Post";  
String message = "Test Post Wooo!";
```
 Run `CodeBot.main()`. If successful, the output should look like this:  `123456789012345_678901234567890`

`currentPost` in  `post.json` should now be updated with the new post's ID and the `repliedComments` array should be empty.
```
{  
  "currentPost": "678901234567890",  
  "repliedComments": [  
  ]  
}
```
Codebot is now ready to read and respond to comments. Don't forget to change `mode` back to `"Comment"`. 

## Usage
Currently, there are two ways to use CodeBot.

- `CodeBot.main()` Retrieves new comments from the current Facebook post,  parses and executes them as BotScript code, and then replies to each comment with the output.

- `ScriptExecutor.main()` Reads `testScript.txt` and executes it as BotScript code, printing the output to the console, and saving any canvas output to `canvas.png`


## How do I learn BotScript?
Check out [the guide](https://github.com/JoshBolitho/CodeBot/blob/main/Guide.md)!


## Why make CodeBot?
CodeBot started as a proof of concept using the [Facebook Graph API](https://developers.facebook.com/docs/graph-api/using-graph-api/), which allows reading and replying to comments from a post. Why not turn a Facebook post's comment section into an interactive coding experience?

Sure, it would be pretty easy to take comments, run them as Python code and reply with the output, but that would mean allowing random people on the internet to run python code on my computer on demand. Sounds like a recipe for disaster, even with a virtual machine. 

The solution? Create a scripting language in Java to add a layer of abstraction above the raw code and prevent users from destroying my computer! This also gave me a lot of freedom in what I wanted in my scripting language.

## How does it work?

### Using the Facebook Graph API
This section details how CodeBot uses Facebook's Graph API to upload posts to facebook, and to read and reply to comments.

#### The Facebook Graph API
The [Facebook Graph API](https://developers.facebook.com/docs/graph-api/) is an HTTP API which allows programmatical access to the Facebook graph. The data on Facebook is represented by a "social graph" represented by nodes and edges. 

Like in graph theory, nodes are objects: a page, user, post, comment etc. These nodes are connected by "edges" or links.

CodeBot uses two Graph API edges:

- `/comments`: The `/comments` edge links any object to all the comments on it. 
	
	`CodeBot.requestComments()` sends a `GET` request to the `/comments` edge of a Facebook post to retrieve data about all the comments on that post. 
	```java
	var getComments = HttpRequest.newBuilder(
		URI.create(String.format("https://graph.facebook.com/v9.0/%s/comments?"
			+ "access_token=%s&fields=message,id,attachment,from,comments,reactions.summary(total_count)",
			objectID, user_access_token)))
		.build();
	HttpResponse<String> response = client.send(getComments, HttpResponse.BodyHandlers.ofString());
	```

	`CodeBot.publishComment()` sends a `POST` request to the `/comments` edge of an object.
	
	`CodeBot.publishCommentImage()` also sends a `POST` request to the `/comments` edge of an object, with an additional  parameter, `attachment_url`. The [Cloudinary](#Cloudinary) section covers obtaining this URL.
	```java
	var publishComment = HttpRequest.newBuilder(  
        URI.create(String.format("https://graph.facebook.com/v9.0/%s/comments/", objectID)))
	    .POST(HttpRequest.BodyPublishers.ofString(  
            String.format("message=%s&access_token=%s", message, user_access_token)))
        .build();
    HttpResponse<String> response = client.send(publishComment, HttpResponse.BodyHandlers.ofString());
	```

- `/feed`: The `/feed` edge links a page or user to all the posts they have made. CodeBot sends a `POST` requests to `/feed` edge of the CodeBot page to upload posts to the page.
 
	`CodeBot.publishPost()` sends a `POST` request to the `/feed` edge of the CodeBot page to publish a Facebook post.
	```java
	var publishPost = HttpRequest.newBuilder().uri(  
        URI.create(String.format("https://graph.facebook.com/v9.0/%s/feed",page_ID)))  
        .POST(HttpRequest.BodyPublishers.ofString(  
                String.format("message=%s&access_token=%s",message, user_access_token))  
        ).build();
    HttpResponse<String> response = client.send(publishPost, HttpResponse.BodyHandlers.ofString());
	```


### Cloudinary
####  Uploading image to unsigned something or rather to host image link for image upload

### The Gson library  
CodeBot uses Google's GSON library to manage JSON formatting for several purposes.

- Loading config values - where secret tokens and profanity list is kept

- Persistent memory to keep track of the current post ID and managing 

- post scheduling  

- Logging errors

the POST and GET requests return responses in JSON format.
Most importantly, the GET \comments on a post. traversing the JSON respons allows CodeBot to read messages , ignore replied ones ... 

### How is a BotScript program represented in Java code?  
This section describes the classes used to represent a program that can be executed.

A variety of Java classes are used to represent a program. At its core, a program is simply a list of instructions, or "statements." In CodeBot, these instructions are represented by `ExecutableNode`s. 

E.g.
`print("Hello")` is an instruction that causes the expression `"hello"` to be written to the output.


`x = 1 + 2` is an instruction that causes the variable `x` to be added to the program's memory, and assigns it to the expression `1 + 2`. 
 
The following is a instruction that evaluates the expression `x=3`, and if it evaluates to `true`, it executes all the code within the "if" block `{}`. 
In this case, the "if" block simply contains a print statement, like in the first example.  
```
if(x=3){
print("Yay, maths!")
}
```
#### Abstract syntax trees
An [Abstract Syntax Tree (AST)](https://en.wikipedia.org/wiki/Abstract_syntax_tree) is a representation of the recursive, branching structure of the code defining a program. CodeBot creates ASTs to represent scripts. Executing the root node of the AST is equivalent to running the script. The parsing of a script from a string into an abstract syntax tree is covered in the [Parsing Section](#parsing).

The image below shows a slightly simplified version of how the following code for a factorial function would be represented as an abstract syntax tree. 
```
function fact(x){
if(x=1){
return x
}
return x * fact(x-1)
}
print(fact(5))
```
![Abstract Syntax Tree](https://user-images.githubusercontent.com/17404588/124100906-f981cf80-dab2-11eb-8a9c-c72811e8ded2.png)

When the root `ExecutableNode` "Program" is executed, it executes each of its sub-nodes  in order (shown here as left to right). Different `ExecutableNode`s have different behaviours, described in the [Representing the structure of the program](#representing-the-structure-of-the-program) section.

-----

#### Representing Values
The classes representing the different value types all implement the `Value` Interface. These classes act as wrappers around their respective values.
- `StringValue(String value)`
- `IntegerValue(int value)`
- `FloatValue(float value)`
- `BooleanValue(boolean value)`
- `ArrayValue(ArrayList<Expression> expressionArray)`
- `ImageValue(BufferedImage image)`  or `ImageValue(int x, int y)`
- `NullValue()`

```java
public interface Value {
	//Helpful methods which allow testing of a Value's type.
	//These methods make use of the ValueType enum.
	ValueType getType();
	boolean isType(ValueType v);
	
	// Casting methods are used to retrieve the value.
	// If we know a Value is a StringValue type by calling Value.isType(STRING),
	//we can call Value.castString() with certainty that the value will be returned.

	//We can also attempt to cast it to another type using the other casting methods. 
	//If our StringValue's value is "12", we could call castInteger() and it would succeed.
	String castString() throws ScriptException;  
	Integer castInteger() throws ScriptException;  
	Float castFloat() throws ScriptException;  
	Boolean castBoolean() throws ScriptException;  
	ArrayList<Variable> castArray() throws ScriptException;  
	BufferedImage castImage() throws ScriptException;
}
```

#### Representing Expressions
What is an expression, and how are they used?
Similar to an expression in maths,  An expression in programming is a combination of values, operations, and functions which can be evaluated to create a new value.

In maths, an expression may look like this: `x² + 3x + sin(x)`.
In BotScript, the same expression would look like this:  `pow(x,2) + 3*x + sin(x)`.
If the variable "x" has been defined already, we can evaluate this expression to a number. 

`Expression` classes all implement the `Expression` interface, which contains a single method, `evaluate()`.   
```
public interface Expression {  
    Variable evaluate(ProgramState programState, HashMap<String, Variable> functionVariables);  
}
```
Expressions come in several forms: 

-----
A value, represented by the `ValueExpression` class e.g.
- `5`
- `true`
- `[1,2,3]`

-----
A reference to a `Variable` defined in the program, represented by the `ReferenceExpression` class e.g. 
```
x = 5

% The "x" here is a reference to the x variable.
% It will be represented as a ReferenceExpression.
print(x)
``` 

-----
An operation, represented by the `OperationExpression` class.
Operations can be any of the following: 
-   Maths:  `+`  `-`  `*`  `/`  `%`  `()`
-   Comparator:  `>`  `<`  `=`
-   Boolean:  `&`  `|`  `!`

Here are some examples of `OperationExpression`s:
- `1+2`: (value 1) plus (value 2)
- `true | false`: (value true) or (value false)
- `x > 10`: (variable reference "x") greater than (value 10)
- `y < random()`: (variable reference "y") less than (function expression "random")
- `!true`: not (value true)
-----

A `Function` call, represented by the `FunctionExpression` class 
e.g. In the line `x = length("Hi")`, `length("hi")` is a FunctionExpression that causes the function `length()` to be called with the ValueExpression `"hi"` as its parameter.
This FunctionExpression will evaluate to the value `2`.	

-----
An Internal `Function` call, represented by the `InternalFunctionExpression` class. Internal functions aren't available for users to define or directly access themselves. 
They are used by the many pre-defined functions included in BotScript, such as `print()` or `getPixel()`, to manipulate values with Java code behind the scenes.

-----

#### Representing the state of the program
The program's state is represented by a `ProgramState` object. a `ProgramState` stores all the variables and functions that are currently defined in the program, as well as a string that represents everything which has been printed to the console. 

An empty `ProgramState` is initialised before parsing a script, and is passed throughout the program during execution so it is accessable everywhere.

```
private HashMap<String,Variable> programVariables = new HashMap<>();  
private HashMap<String,Function> programFunctions = new HashMap<>();  
private String consoleOutput = "";
```
- `programVariables` contains all the `Variable`s, accessable by name. Running the BotScript code `x = 10` would cause a the following entry to be added: `x, new IntegerVariable(10)`

- Similarly, `programFunctions` contains all the `Function`s, accessable by name.
 
- `consoleOutput` Stores everything that has been printed to the console.
Running `print("Hello World")` would cause `consoleOutput` to equal `"Hello World\n"`.
 
 
#### Representing the structure of the program
Each line or "statement" in a program is represented by a class implementing the `ExecutableNode` Interface. 
`ExecutableNode`s provide the entry point for execution of the program. The nodes in a program combine to form a program tree. 

```java
public interface ExecutableNode {
	void execute(ProgramState programState, HashMap<String, Variable> functionVariables) throws InterruptedException;
	String display(int depth);  
}
```
`ExecutableNode` classes include:

`ProgramNode`,  which stores an array of ExecutableNodes. 
When executed, this node executes all its sub-nodes in order.

-----
`VariableAssignmentNode`, which has the main purpose of adding an entry to to the `programState`'s `programVariables`  HashMap. This is how variables are defined in a program. 

If a `VariableAssignmentNode` is called from within a function, it will instead add an entry to the `functionVariables` HashMap which is also passed during execution.

-----
`FunctionAssignmentNode` Stores a `Function` object, and adds it to the programState's `programFunctions` HashMap.

-----
`FunctionExecutionNode` Represents a Function call. It stores the name of the function being executed, as well as an ArrayList of Expressions which represent the parameters to be passed.

-----
`IfNode` Represents an if statement. It stores a condition `Expression` which should evaluate to a boolean, an "if block" `ProgramNode`, and optionally an "else block" `ProgramNode`. If the the condition evaluates to `true`, the "if block" will execute. Otherwise, the "else block" will be executed (if it exists.) 

-----
`WhileNode` Represents a while loop statement. It stores a condition `Expression` which should evaluate to a boolean, and a "while block" `ProgramNode`. The "while block" will be executed repeatedly in a loop as long as the condition evaluates to `true`.



### Parsing 
This section covers the process of turning a string into a program.

Parsing is handled by `Parser.java`, a [recursive descent parser]() which builds an [Abstract Syntax Tree (AST)]() to represents the program. 

`Parser.parseScript(String script, ...)` is the entry point for parsing, taking a `String`, and returning a `ProgramNode` that is the root node of the Abstract Syntax Tree representing the parsed program.

#### Pre-processing
Empty lines and lines starting with the line comment character `%` are removed from the string before parsing begins.

#### Parser.parseScript()
`parseScript()` creates an empty `ProgramNode` called `program` to represent the parsed AST.
`parseScript()` then creates a `Scanner` which allows the string to be split into tokens as it is iterated through. 

After manually adding [internal functions]() to `program`, parsing begins. This simplified view of the code shows `parseScript()`  calling `parseExecutableNode(scanner)` until the scanner is empty.
```java
while (scanner.hasNext()) {
	scriptNode.addExecutableNode(parseExecutableNode(scanner));  
}
``` 
#### Parser.parseExecutableNode()
As described [earlier](#representing-the-structure-of-the-program), `ExecutableNode`s represent the main structure of the program. `ExecutableNode`s are parsed using the scanner by looking ahead at the next token, and testing it against patterns to decide what `ExecutableNode` needs to be parsed. The appropriate `ExecutableNode` parsing function is then run.

`parseExecutableNode()` tests the scanner `s`'s next token using the `Scanner.hasNext(Pattern)` method. 
```java
...
static Pattern While = Pattern.compile("while");  
static Pattern If = Pattern.compile("if");
static Pattern Function = Pattern.compile("function");
...
if(s.hasNext(If)){  
    return parseIfNode(s);  
}else if(s.hasNext(While)){  
    return parseWhileNode(s);  
}else if(s.hasNext(Function)){  
    return parseFunctionAssignment(s);
}else if(...
```
#### require()
`require()` is a handy function that is used constantly while parsing. It looks at the next token of a scanner and either matches and consumes the token, or throws an error. 

`require()` is used when we know what the next token should be, and we want to ensure it exists, and then move the scanner past it so it can deal with the next token. 

```java
String require (Pattern p, Scanner s) throws ScriptException {  
    if (s.hasNext(p)) {
	    ...
        return s.next();  
  }  
    //if the require fails:  
  ...
  throw new ScriptException("Expected \"" + p + "\"");  
  ...  
}
```

There are 3 different require methods:
- `require(String str, Scanner s)` matches the next token against a `String`
- `require (Pattern p, Scanner s)` matches the next token against a `Pattern`
- `optionalRequire(Pattern p, Scanner s)` matches the next token against a `Pattern`. If it is a match, it is consumed. otherwise, it does nothing.

#### Example: parseIfNode()
When `parseWhileNode()` is called, this means that `parseExecutableNode()` has matched the next token with the `While` pattern. In BotScript, a while statement looks like the following:
```
while(condition){
statement 1
statement 2
...
statement n
}
```
The first token we expect is `"While"`. 
We can also expect the open parentheses character `"("`.
Here are the first couple lines of `parseWhileNode()`:

```java
public WhileNode parseWhileNode(Scanner s){  
	require(While, s);
	require(OpenParenthesis, s);
	...
```
Next we need to parse the condition, which is an `Expression`. Therefore, we call `parseExpression()`.
After the condition, we parse the close parentheses character `)`, the open brace character `{`, and optionally a newline character.
```java
	Expression condition = parseExpression(s,null,null);  
	require(CloseParenthesis, s);  
	require(OpenBrace, s);  
	optionalRequire(NewLine, s);
```	

We are now up to parsing the code block of the while statement. 
A `ProgramNode` is created to store this code block.

We need to be able to parse any number of `ExecutableNode`s. The code block is closed off with a close brace character `}`, so we parse `ExecutableNode`s repeatedly until a `}` is reached.
```java
	//while scanner doesn't have close brace  
	ProgramNode whileBlock = new ProgramNode();  
	while(!s.hasNext(CloseBrace)){
		whileBlock.addExecutableNode(parseExecutableNode(s));
	}
```
The close brace character that ended the while statement's code block is then consumed, as well as a newline character which should follow it.

A new `WhileNode` is created and returned, with the condition and whileBlock as its parameters. The while statement has successfully been parsed.
```java
    require(CloseBrace, s);
    require(NewLine, s);
    
    return new WhileNode(condition,whileBlock);  
}
```

#### parseExpression()
This method is used anywhere that an `Expression` of any kind is expected e.g. The condition `Expression` of a while statement in the previous example.

Parsing `Expressions`, especially `OperationExpression`s, are particularly challenging because of the order of operations.

| Operator | Priority Level |
|--|--|
| * / % | 5 |
| + - | 4 |
| > < | 3 |
| = | 2 |
| & | 1 |
| \| | 0 |



## Credits
### Tools
- [IntelliJ Idea](https://www.jetbrains.com/idea/)
- [Cloudinary API](https://cloudinary.com/documentation/image_upload_api_reference)
- [Facebook Graph API](https://developers.facebook.com/docs/graph-api/)

### Libraries
- [Gson](https://github.com/google/gson)
- [JUnit 5](https://github.com/junit-team/junit5/)


### Resources
- [Eff.org](https://www.eff.org/files/2016/07/18/eff_large_wordlist.txt) for the big list of random words
- [Edabit.com](https://edabit.com/challenges) for some of the coding challenges
- [Victoria University of Wellington](https://www.wgtn.ac.nz/)'s COMP261 course, for teaching syntax parsing, and the "robot" assignment, which inspired early versions of CodeBot
- [Sunflower Image](https://www.almanac.com/sites/default/files/styles/amp_metadata_content_image_min_696px_wide/public/image_nodes/sunflower-1627193_1920.jpg)
- [Monkey Image](https://i.kym-cdn.com/photos/images/original/001/131/258/07c.jpg)

### People
-  [Boidushya](https://github.com/boidushya) for helping me sort out my API token using their [Facebook Graph API token guide](https://github.com/Boidushya/FrameBot/blob/master/generateToken.md)!
- People of the Bot Appreciation Society Discord channel, for general support!
