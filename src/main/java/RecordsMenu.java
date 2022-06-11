import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class RecordsMenu extends JFrame {

    private JTextArea textArea; // Pole, w którym będą wyświetlane rekordy.
    private JPanel recordsPanel; // Panel do układania elementów.
    private JButton mainMenuButton; // Przycisk, aby powrócić do ekranu głównego.
    private BufferedReader  fileReader;
    private BufferedWriter fileWriter;
    private Image scoreScreen; // Tło pod okienko z recrdami.
    private ArrayList<Integer> records = new ArrayList<Integer>();
    private String url = "results" + File.separator + "result.dat";

    /**
     * Okno rekordów.
     */

    public RecordsMenu() throws IOException {
        setTitle("Menu rekordow"); // tytuł okna
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(336, 365);
        setLocation(MainMenu.locationWindow);
        setFocusable(true);
        loadImages();

        recordsPanel = new JPanel() {  // Obiekt panelu do rozmieszczania elementów, nadpisujący metodę rysowania.
            @Override
            public void paint (Graphics g) { // Zastępowanie metody rysowania okna.
                g.drawImage(scoreScreen, 0, 0, this);
                super.printComponents(g);
            }
        };
        recordsPanel.setLayout(new BoxLayout(recordsPanel, BoxLayout.X_AXIS));

        textArea = new JTextArea(); // Pole tekstowe do wyświetlania rekordów.
        textArea.setBackground(Color.lightGray);
        textArea.setFont(new Font("Arial", Font.PLAIN, 18));
        textArea.setEditable(false);

        sortRecords(); // Metoda sortująca rekordy i wybierająca pierwszą dziesiątkę.

        fileReader = new BufferedReader(new FileReader(url)); //  Odczyt rekordów.
        int i = 1;
        while(fileReader.ready()) {
            if(i != 10) {
                textArea.append("  " + i + ": " + fileReader.readLine() + "\r\n");
            }
            else {
                textArea.append(i + ": " + fileReader.readLine() + "\r\n");
            }
            i++;// licznik
        }

        mainMenuButton = new JButton("Menu");

        recordsPanel.add(BorderLayout.EAST, textArea); // Dodanie do panelu pliku tekstowego z zapisem rekordów.
        recordsPanel.add(BorderLayout.WEST, mainMenuButton); // Dodanie przycisku do panelu.

        add(BorderLayout.CENTER, recordsPanel);
        System.out.println("uruchomienie tabeli liderow");

        setVisible(true);

        ActionListener menuListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    MainMenu.locationWindow = getLocation(); // Podanie ostatnich koordynatów okna.
                    new MainMenu();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                setVisible(false);
            }
        };
        mainMenuButton.addActionListener(menuListener);
    }

    /**
     * Sortowanie rekordów.
     */
    private void sortRecords() throws IOException {
        fileReader = new BufferedReader(new FileReader(url));

        while (fileReader.ready()) { // Dopóki w pliku jest wolna linia to wprowadź wszystkie wartości do listy.
            records.add(Integer.parseInt(fileReader.readLine()));
        }

        fileReader.close();
        fileWriter = new BufferedWriter(new FileWriter(url)); // Obiekt do nadpisania listy rekordów.

        Collections.sort(records);
        if(records.size() > 10) { // Jeśli jest więcej niż 10 rekordów, to tylko 10 jest zapisywanych z powrotem do pliku.
            for (int i = records.size() - 1; i > records.size() - 11; i--) {
                fileWriter.write(records.get(i) + "\r\n");
            }
        }
        else { // Jeśli w pliku jest mniej niż 10 rekordów, posortuj i nadpisz wszystko.
            for (int i = records.size() - 1; i > -1; i--) {
                fileWriter.write(records.get(i) + "\r\n");
                fileWriter.flush();
            }
        }
        fileWriter.close();
    }

    private void loadImages() throws IOException {
        scoreScreen = ImageIO.read(Objects.requireNonNull(RecordsMenu.class.getClassLoader().getResourceAsStream("scoreScreen.png")));
    }

}
