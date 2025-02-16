#include <stdio.h>
#include <stdlib.h>
// termios is a general terminal interface in Unix.
#include <termios.h>
#include <unistd.h>

void enableRawMode() {
    struct termios raw;
    tcgetattr(STDIN_FILENO, &raw);  // Get current terminal attributes

    raw.c_lflag &= ~(ECHO | ICANON | ISIG | IEXTEN);  // Disable echo, canonical mode, signals
    // ECHO: Echoes input characters.
    // ICANON: Enables canonical mode.
    //    In canonical mode, input is made line by line.
    //    In non-canonical mode, input is available immediately, without having to type a line delimiter.
    // ISIG: When any of the characters are received, generate the corresponding signal: NTR, QUIT, SUSP, or DSUSP
    // IEXTEN: Terminal may perform implementation-specific processing on input.
    //

    // Raw level aspects of input processing
    raw.c_iflag &= ~(BRKINT | ICRNL | INPCK | ISTRIP | IXON);
    // BRKINT: a break condition clears the terminal input
    // ICRNL: Carriage return characters received as input are passed as a newline character (\n)
    // INPCK: Conducts parity checking on each input.
    raw.c_oflag &= ~(OPOST);  // Disable output processing
    raw.c_cflag |= (CS8);  // Set character size to 8 bits

    tcsetattr(STDIN_FILENO, TCSAFLUSH, &raw);  // Apply the new settings
}

void disableRawMode() {
    struct termios original;
    tcgetattr(STDIN_FILENO, &original);  // Get current terminal attributes
    original.c_lflag |= (ECHO | ICANON | ISIG | IEXTEN);  // Re-enable normal mode
    original.c_iflag |= (BRKINT | ICRNL | INPCK | ISTRIP | IXON);
    original.c_oflag |= (OPOST);
    original.c_cflag &= ~(CS8);
    tcsetattr(STDIN_FILENO, TCSAFLUSH, &original);  // Restore settings
}

int main() {
    enableRawMode();
    return 0;
}
