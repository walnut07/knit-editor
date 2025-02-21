# `Knit`: A command-line text editor written in Kotlin.

Welcome to Knit, a command-line text editor written in Kotlin. 

In this personal project, I build my own text editor with only basic features, using Kotlin.

> [!NOTE]
> 1. I started this project for learning purposes. Please do not use Knit for serious editing work—but feel free to play around with it!
> 2. I’m writing a tutorial on how to craft a text editor in Kotlin. I’ll announce it on my blog (mtkrm.com) once it’s ready.

https://github.com/user-attachments/assets/4b62aa62-2076-4fec-b958-0634eef0a17b

## How it stores text buffer: Doubly-Linked List
Knit uses **a doubly-linked list** to manage its text buffer. 
Each line of text is stored as a node in the list. This allows efficient insertion and deletion between lines while modifying a line can be expensive.

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

## How to Try Knit

1. **Generate the JAR File**
    - After cloning this repo, run:
      ```bash
      ./gradlew shadowJar
      ```
    - The JAR file (e.g. `knit-0.1.jar`) will be generated in the `build/libs/` folder.

2. **Run the JAR File**
    - In the terminal, run:
      ```bash
      java -jar build/libs/knit-1.0.jar
      ```
    - This will launch Knit in your terminal.

## Features
- [x] **Arrows**
    - [x] Supports up, down, left, and right arrow keys.
    - [ ] Missing support for jump moves (Command + arrow keys).

- [x] **Text Input**
    - [x] Inserts characters at the cursor position.

- [x] **Commands**
    - [x] Supports Enter/Line Feed, Carriage Return, and Delete.
    - [ ] Missing support for saving the text buffer to a designated file (currently, it only displays the buffer).
    - [ ] Missing support for Quit, Save, Exit, and many other commands.

- [ ] **Ambitious Goals**
    - [ ] Undo/redo functionality.
    - [ ] Copy/paste functionality.
    - [ ] Syntax highlighting.

## Guide to the codebase
- Main application logic: `Main.kt`, `EditorApp.kt`
- User input handling: `controller/`
- State management: `managers/`
- Data models: `models/`
- Testing: tests for controllers are located in `src/test/kotlin/controller/`
