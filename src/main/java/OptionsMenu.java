import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Wybór danych przed rozpoczęciem gry.
 */

public class OptionsMenu  extends JFrame {

    private JButton mainMenuButton;
    public static JRadioButton withoutBarriers = new JRadioButton("Bez barier", true); // Typ gry wybrany domyślnie.
    public static JRadioButton verticalBarriers = new JRadioButton("Bariery w gorze i na dole");
    public static JRadioButton horizontalBarriers = new JRadioButton("Bariery po bokach");
    public static JRadioButton perimeterBarriers = new JRadioButton("Bariery obwodowe");
    public static JRadioButton controlOnWasd = new JRadioButton("WASD");
    public static JRadioButton controlOnArrow = new JRadioButton("Strzalki na klawiaturze", true); // Sterowanie strzałkami jest domyślnie zaznaczone.
    private ButtonGroup radioMap = new ButtonGroup();
    private ButtonGroup radioControl = new ButtonGroup();

    private JPanel optionsPanel; // Karty.
    private JPanel controlPanel;
    private JTextArea aboutControl; // Pole opisu kontrolki.

    public OptionsMenu() {
        setTitle("Opcje");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(336, 365);
        setLocation(MainMenu.locationWindow);
        setFocusable(true);
//        loadImages();

        optionsPanel = new JPanel(); // // Tworzenie panelu do rozmieszczenia elementów wyboru mapy.
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBackground(Color.lightGray);
        optionsPanel.setOpaque(false);

        controlPanel = new JPanel();// Tworzenie panel do rozmieszczenia elementów wyboru kontroli.
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
        controlPanel.setBackground(Color.lightGray);
        controlPanel.setOpaque(false);

        /**
         * Połączone przyciski opcji wyboru mapy w jeden łańcuch logiczny.
         */
        radioMap.add(withoutBarriers);
        radioMap.add(verticalBarriers);
        radioMap.add(horizontalBarriers);
        radioMap.add(perimeterBarriers);
        radioControl.add(controlOnWasd);
        radioControl.add(controlOnArrow);

        /**
         * Dodawanie przycisków dla wyboru mapy do panelu.
         */

        optionsPanel.add(withoutBarriers);
        optionsPanel.add(verticalBarriers);
        optionsPanel.add(horizontalBarriers);
        optionsPanel.add(perimeterBarriers);

        controlPanel.add(controlOnWasd); // Dodanie przycisku opcji wyboru sterowania do panelu.
        controlPanel.add(controlOnArrow);
        /**
         * Przycisk wyjścia do ekranu głównego
         */

        mainMenuButton = new JButton("Menu");
        optionsPanel.add(BorderLayout.CENTER, mainMenuButton);

        withoutBarriers.setOpaque(false);
        verticalBarriers.setOpaque(false);
        horizontalBarriers.setOpaque(false);
        perimeterBarriers.setOpaque(false);
        controlOnWasd.setOpaque(false);
        controlOnArrow.setOpaque(false);
        /**
         * Ustawienie czcionki dla przycisku.
         */

        withoutBarriers.setFont(new Font("Arial", Font.PLAIN, 16));
        verticalBarriers.setFont(new Font("Arial", Font.PLAIN, 16));
        horizontalBarriers.setFont(new Font("Arial", Font.PLAIN, 16));
        perimeterBarriers.setFont(new Font("Arial", Font.PLAIN, 16));
        controlOnWasd.setFont(new Font("Arial", Font.PLAIN, 16));
        controlOnArrow.setFont(new Font("Arial", Font.PLAIN, 16));

        aboutControl = new JTextArea();
        aboutControl.setOpaque(false);
        aboutControl.setBackground(Color.lightGray);
        aboutControl.setFont(new Font("Arial", Font.PLAIN, 16));
        aboutControl.append(" Pauza : P \r\n" + " Wyjscie do menu glownego : Enter \r\n" +
                "\r\n" + " Niebieska strzalka w dol - zwolnij \r\n" + "Czerwona strzalka w gore - przyspieszenie\r\n" +
                " jabłko - wzrost weza \r\n" + "\r\n Kontrola:");
        aboutControl.setEditable(false);

        add(BorderLayout.CENTER, controlPanel);
        add(BorderLayout.SOUTH, optionsPanel);
        add(BorderLayout.NORTH, aboutControl);

        setVisible(true);

        ActionListener mainMenuButtonListener = new ActionListener() {
            @Override
            /**
             * Interakcja z przyciskiem
             */
            public void actionPerformed(ActionEvent e) {
                try {
                    MainMenu.locationWindow = getLocation();
                    new MainMenu(); //  Aktualizacja okna.
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                setVisible(false); // Zamknij bieżące okno.
            }
        };
        mainMenuButton.addActionListener(mainMenuButtonListener);
    }


}
