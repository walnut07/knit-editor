#include <stdio.h>
#include <stdlib.h>
// termios is a general terminal interface in Unix.
#include <termios.h>
#include <unistd.h>

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
    disableRawMode();
    return 0;
}
