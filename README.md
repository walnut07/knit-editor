# `Knit`: A command-line text editor written in Kotlin.

Welcome to Knit, a command-line text editor written in Kotlin. 

In this personal project, I build my own text editor with only basic features, using Kotlin.

> [!NOTE]
> 1. This repo is a work in progress. Many features are still under development.
> 2. Once the core functionalities are implemented, Knit will likely be released in the Jar format.
> 3. I may create a tutorial on how to craft a text editor in Kotlin—if so, I'll announce it here!

https://github.com/user-attachments/assets/4b62aa62-2076-4fec-b958-0634eef0a17b

## How it stores text buffer: Doubly-Linked List
Knit uses **a doubly-linked list** to manage its text buffer. 
Each line of text is stored as a node in the list, allowing efficient insertion, deletion, and navigation between lines.

```
Hello, World!
This is Knit.
Goodbye!
  +-------------------------+     +-------------------------+     +---------------------+
  |        Line 1           | <-> |         Line 2          | <-> |       Line 3        |
  | "Hello, World!"         |     | "This is Knit."         |     | "Goodbye!"          |
  | prev: null              |     | prev: Line 1            |     | prev: Line 2        |
  | next: Line 2            |     | next: Line 3            |     | next: null          |
  +-------------------------+     +-------------------------+     +---------------------+
```

## Guide to the codebase
- Main application logic: Main.kt, EditorApp.kt
- User input handling: `controller/` folder.
- State management: `managers/` folder
- Data models: `models/` folder
- Testing: unit tests for controllers are located in `src/test/kotlin/controller/`
