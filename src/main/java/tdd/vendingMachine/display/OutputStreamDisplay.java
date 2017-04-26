package tdd.vendingMachine.display;

import java.io.OutputStream;
import java.io.PrintStream;

public class OutputStreamDisplay implements Display {

    private PrintStream printStream;

    public OutputStreamDisplay(OutputStream outputStream) {
        this.printStream = new PrintStream(outputStream);
    }

    @Override
    public void printMessage(String message) {
        printStream.print(message);
    }

}
