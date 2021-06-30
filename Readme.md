
# CodeBot

Codebot is a Java Facebook bot which parses Facebook comments as [BotScript](#What-is-Botscript) code, executes them, and replies with the output.

CodeBot is hosted at [facebook.com/CodeBotOfficial](https://facebook.com/CodeBotOfficial)

### Contents
 * [What is BotScript?](#What-is-BotScript?)
 * [Features](#Features)
 * [Setup](#Setup)
	 * [Download and Install](Download-and-Install)
	 * [Acquire API Keys](Acquire-API-Keys)
	 * [Create a post](Create-a-post)
 * [Usage](#Usage)
 * [How do I learn BotScript?](#How-do-I-learn-BotScript?)
 * [Why make CodeBot?](#Why-make-CodeBot?)
 * [How Does it work?](#How-Does-it-work?)


## What is BotScript?
BotScript is a simple scripting language, designed to be used in a Facebook comments section. If you want to write code on the [CodeBot Facebook page](https://facebook.com/CodeBotOfficial), you need to learn the [basics of BotScript](https://github.com/JoshBolitho/CodeBot/blob/main/Guide.md). 

Notes: ...

## Features
For more information about using BotScript, check out [the guide](https://github.com/JoshBolitho/CodeBot/blob/main/Guide.md).

### Variables
Variables can be any of the following types: 
- String `"Example"`
- Integer `123`
- Float `3.14159`
- Boolean `true`
- Array `[1, 2, 0.5, true, "Example", [3, 4]]`
- Image `(See following section)`
### Images 
...

### Operators
BotScript has all the standard operators you'd expect to see in a simple scripting language. 

- Maths: `+`  `-` `*`  `/` `%` `()`
- Comparators `>` `<` `=` 
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
...

### While Block
...

### Casting
...

### Error Handling
...

## Setup
This project is designed to run on my computer, and control the [CodeBot Facebook page](https://facebook.com/CodeBotOfficial), but if you wanted, you could take it and run your own page! You can also use this project to test your BotScript code locally. 

### Download and Install
```
git clone https://github.com/JoshBolitho/CodeBot.git
```
Open in your IDE of choice and make sure to add JUnit5.4 to your Project

### Acquire API Keys
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
- Facebook access tokens for posting on the page you wish to automate.
- A cloudinary account, with an unsigned upload preset for temporary image storage. You can create a new upload preset in Settings > Upload > Upload Presets.


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

## How Does it work?
...
