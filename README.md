# CodeBot

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
...
