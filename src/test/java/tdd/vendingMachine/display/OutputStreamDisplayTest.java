package tdd.vendingMachine.display;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnit4.class)
public class OutputStreamDisplayTest {

    @Test
    public void print_message_test() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Display display = new OutputStreamDisplay(baos);

        display.printMessage("test");

        assertThat(baos.toString()).isEqualTo("test");
    }
}
