import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Objects;

public class MainMenu extends JFrame {

    private JButton newGame = new JButton("Nowa gra"); // Przyciski.
    private JButton exit = new JButton("Wyjscie");
    private JButton records = new JButton("rekordy");
    private JButton options = new JButton("Opcje");
    private JPanel mainMenuPanel; // panel elementow przyciskow
    private Image mainScreen; // tło dla głównego okna
    public static Point locationWindow;// Zmienna statyczna, do której można uzyskać dostęp ze wszystkich okien, aby umieścić je tam, gdzie jest to konieczne.
    static {
        if(locationWindow == null) { // Jeśli okno jest włączane po raz pierwszy, to tworzymy obiekt z początkowymi współrzędnymi.
            locationWindow = new Point(400, 200);
        }
    }

    public static MainWindow gameWindow; // Statyczna klasa do wyłączania okna po śmierci węża.


    public MainMenu() throws IOException {
        setTitle("Menu"); // nagłówek
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // Zamykanie okna krzyżykiem.
        setSize(336, 365); // rozmiar okna
        setLocation(locationWindow);
        setFocusable(true);
        loadImages();

        mainMenuPanel = new JPanel() { // Panel do rozmieszczania elementów, nadpisywanie metody draw przez anonimową klasę.

            /**
             * rysowanie okna.
             */
            @Override
            public void paint (Graphics g) {
                g.drawImage(mainScreen, 0, 0, this); // Rysowanie obraz tła na całym oknie.
                super.printComponents(g);
            }
        };
        setFocusable(true);
        mainMenuPanel.add(newGame); // Dodawanie przycisków do panelu.
        mainMenuPanel.add(exit);
        mainMenuPanel.add(records);
        mainMenuPanel.add(BorderLayout.SOUTH, options);
        mainMenuPanel.setVisible(true);
        add(BorderLayout.CENTER, mainMenuPanel);
        setVisible(true); // окно видимо
        System.out.println("Uruchamianie menu glownego");

        ActionListener startGameListener = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {

                    locationWindow = getLocation();// Przekazywanie ostatnich współrzędnych okna podczas zamykania
                    gameWindow = new MainWindow(); // Tworzenie obiektu okna, który uruchamia grę.
                    setVisible(false); // Usuwanie bieżącego okna.

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        };
        newGame.addActionListener(startGameListener);

        ActionListener exitGameListener = new ActionListener() { //  Wyjście z gry.

            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        };
        exit.addActionListener(exitGameListener);

        ActionListener recordsListener = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    locationWindow = getLocation();
                    new RecordsMenu();
                    setVisible(false);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        };
        records.addActionListener(recordsListener);

        ActionListener optionsListener = new ActionListener() {

            /**
             * Przycisk przetwarzania z opcjami.
             */

            public void actionPerformed(ActionEvent e) {
                locationWindow = getLocation();
                new OptionsMenu();
                setVisible(false);
            }
        };
        options.addActionListener(optionsListener);
    }
    /**
     * Zapisujemy obrazek menu głównego.
     */
    private void loadImages() throws IOException {
        mainScreen = ImageIO.read(Objects.requireNonNull(MainMenu.class.getClassLoader().getResourceAsStream("mainScreen.png"))); // Obrazek menu główne.
    }

    public static void main(String[] args) throws IOException {
        new MainMenu();
    }
}
