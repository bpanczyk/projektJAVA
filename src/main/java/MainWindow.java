import javax.swing.*;
import java.io.IOException;

public class MainWindow extends JFrame {
    public MainWindow() throws IOException {
        setTitle("Snake");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(336, 365);
        setLocation(MainMenu.locationWindow);// pozycja według ostatnich współrzędnych okna
        add(new GameField());
        setVisible(true);
    }
}
