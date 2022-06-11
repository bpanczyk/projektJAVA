import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

/**
 * Publiczna klasa "GameField", dziedzicząca po JPanel oraz implementująca interfejs "ActionListener".
 * 	zawiera ona wszystkie funkcjonalności związane z polem gry.
 */

public class GameField extends JPanel implements ActionListener {
    private final int SIZE = 320; // rozmiar pola
    private final int DOT_SIZE = 16; // piksele są zajęte przez jedną komórkę węża
    private final int ALL_DOTS = 400; // maksymalna liczba komórek węża mieszczących się w pudełku to 320*320
    private Image dot; // obraz węża
    private Image apple; // zdjęcie jabłka
    private Image slowlyDot; // obraz strzałki która spowalnia
    private Image speedDot; // obraz strzałki która przyspieszenia
    private Image backendGame; // tło
    private Image gameOver; // tło dla końca gry
    private Image borderIcon; // obraz bariera
    private int appleX; // Pozycja X jabłka
    private int appleY; // Pozycja Y jabłka
    private int slowlyX; //położenie X strzałki zwalnianiającej w przestrzeni
    private int slowlyY; // położenie Y strzałki zwalnianiającej w przestrzeni
    private int speedX; // położenie X strzałki przyspieszającej w przestrzeni
    private int speedY; // położenie Y strzałki przyspieszającej w przestrzeni
    private int[] x = new int[ALL_DOTS]; // tablica przechowująca ruchy węża w wymiarze X
    private int[] y = new int[ALL_DOTS]; // tablica przechowująca ruchy węża w wymiarze Y
    private int dots; // rozmiar węża
    private Timer timer; // regulator czasowy
    private boolean left = false; // ruch w lewo
    private boolean right = true; // ruch w prawo
    private boolean up = false; // ruch w góre
    private boolean down = false; // ruch w dół
    private boolean inGame = true; // informacja, czy wąż "żyje", czy nie
    private int score; // wynik gry
    private boolean isPause = false; // pauza
    private boolean speedApple = false; // czy przyspieszające jabłko jest aktywne
    private boolean slowlyApple = false; // czy spowolnieniające jabłko jest aktywne
    private int timeFTUA = 25; // czas, aby strzałki zniknęły z mapy
    private String url = "results" + File.separator + "result.dat";

    public GameField() throws IOException {
        loadImages(); // ładowanie obrazów na początku gry
        startTimer(); // uruchomienie timer'a
        initGame(); // inicjacja gry
        addKeyListener(new FieldKeyListener()); // początkowy rysunek węża
        setFocusable(true); // skupienie się na polu gry
        setPreferredSize(new Dimension(SIZE, SIZE)); // dodano, aby pozostać w oknie
        setFocusable(true);
        createApple(); // tworzy jabłko
    }


    /**
     * Metoda służąca do inicjowania gry.
     */

    private void initGame() {
        left = false;
        right = true;
        up = false;
        down = false;
        inGame = true;
        dots = 3;
        for (int i = 0; i < dots; i++) { // inicjuje początkowe wartości dla węża
            x[i] = 48 - i * DOT_SIZE; // x od 48 do zera węża (lewa krawędź)
            y[i] = 48; // w grze jest niezmieniona
        }
        timer.start();
        timer.setDelay(250); // zresetuj timer do 250
        score = 0; // zresetuj wynik
    }

    private void startTimer() {
        timer = new Timer(250, this); // tworzenie nowego timera
        timer.start(); // uruchamianie timer'a
    }


    /**
     * Metoda służąca do tworzenia elementu gry: jabłka.
     */

    private void createApple() {
        if (OptionsMenu.withoutBarriers.isSelected()) { // opcja przetwarzania, gdy nie wybrano żadnych barier
            this.appleX = new Random().nextInt(20) * DOT_SIZE; // współrzędna X jabłka w obrębie pola
            this.appleY = new Random().nextInt(20) * DOT_SIZE; // współrzędna Y jabłka w obrębie pola
        }
        if (OptionsMenu.verticalBarriers.isSelected()) { // opcja przetwarzania po wybraniu barier pionowych
            this.appleX = new Random().nextInt(20) * DOT_SIZE; // współrzędna X jabłka w obrębie pola
            this.appleY = (new Random().nextInt(18) + 1) * DOT_SIZE; // współrzędna Y jabłka w obrębie pola
        }
        if (OptionsMenu.horizontalBarriers.isSelected()) { // opcja przetwarzania, gdy wybrane są bariery poziome
            this.appleX = (new Random().nextInt(18) + 1) * DOT_SIZE; // współrzędna X jabłka w obrębie pola
            this.appleY = new Random().nextInt(20) * DOT_SIZE; // współrzędna Y jabłka w obrębie pola
        }
        if (OptionsMenu.perimeterBarriers.isSelected()) { // opcja przetwarzania, gdy wybrane są bariery obwodowe
            this.appleX = (new Random().nextInt(18) + 1) * DOT_SIZE; // współrzędna X jabłka w obrębie pola
            this.appleY = (new Random().nextInt(18) + 1) * DOT_SIZE; // współrzędna Y jabłka w obrębie pola
        }
        newApple(); // Tworzenie nowego jabłka.

        if (!speedApple && !slowlyApple) { // Szansa na pojawienie się występuje tylko wtedy, gdy żadne z jabłek bonusowych nie jest już aktywne.
            if (new Random().nextBoolean()) { // metoda losowego generowania nietypowych strzałek - prawdopodobieństwo powstania - 0,25.
                if (new Random().nextBoolean()) {
                    if (new Random().nextBoolean()) { //przespieszenie
                        createSpeedApple();
                        speedApple = true;
                    } else { // spowolnienie
                        createSlowlyApple();
                        slowlyApple = true;
                    }
                }
            }
        }
    }

    /**
     * Metoda do tworzenia nowego jabłka.
     */

    private void newApple() {
        for (int i = dots; i > 0; i--) {
            if (this.x[i] == this.appleX && this.y[i] == this.appleY) {
                createApple();
            }
        }
    }

    /**
     * Metoda do tworzenia bonusowego jabłka.
     */

    private void createSpeedApple() {
        if (OptionsMenu.withoutBarriers.isSelected()) { // opcja przetwarzania, gdy nie wybrano żadnych barier
            this.speedX = new Random().nextInt(20) * DOT_SIZE; // // współrzędna X prędkości srzałki
            this.speedY = new Random().nextInt(20) * DOT_SIZE; // współrzędna Y prędkości srzałki
        }
        if (OptionsMenu.verticalBarriers.isSelected()) { // opcja przetwarzania po wybraniu barier pionowych
            this.speedX = new Random().nextInt(20) * DOT_SIZE; // współrzędna X prędkości srzałki
            this.speedY = (new Random().nextInt(18) + 1) * DOT_SIZE; // współrzędna Y prędkości srzałki
        }
        if (OptionsMenu.horizontalBarriers.isSelected()) { // opcja przetwarzania, gdy wybrane są bariery poziome
            this.speedX = (new Random().nextInt(18) + 1) * DOT_SIZE; // współrzędna X prędkości srzałki
            this.speedY = new Random().nextInt(20) * DOT_SIZE; // współrzędna Y prędkości srzałki
        }
        if (OptionsMenu.perimeterBarriers.isSelected()) { // opcja przetwarzania, gdy wybrane są bariery obwodowe
            this.speedX = (new Random().nextInt(18) + 1) * DOT_SIZE; // współrzędne prędkości srzałki
            this.speedY = (new Random().nextInt(18) + 1) * DOT_SIZE; // współrzędne prędkości srzałki
        }
        newSpeedDot(); //
    }

    /**
     * Metoda, która nie pozwala na wzajemne nakładanie się obiektów takich jak newApple().
     */

    private void newSpeedDot() { //
        for (int i = dots; i > 0; i--) { //
            if (this.x[i] == this.speedX && this.y[i] == this.speedY) {
                createSpeedApple();
            }
        }
        if (this.appleX == this.speedX && this.appleY == this.speedY) { //  Czy trafiło w jabłko
            createSpeedApple();
        }
    }

    /**
     * Metoda która generuje strzałkę spowolnienia.
     */

    private void createSlowlyApple() {
        if (OptionsMenu.withoutBarriers.isSelected()) { // opcja przetwarzania, gdy nie wybrano żadnych barier
            this.slowlyX = new Random().nextInt(20) * DOT_SIZE; // współrzędna X strzałki zwalniającej
            this.slowlyY = new Random().nextInt(20) * DOT_SIZE; // współrzędna Y strzałki zwalniającej
        }
        if (OptionsMenu.verticalBarriers.isSelected()) { // opcja przetwarzania po wybraniu barier pionowych
            this.slowlyX = new Random().nextInt(20) * DOT_SIZE; //współrzędna X strzałki zwalniającej
            this.slowlyY = (new Random().nextInt(18) + 1) * DOT_SIZE; // współrzędna Y strzałki zwalniającej
        }
        if (OptionsMenu.horizontalBarriers.isSelected()) { // opcja przetwarzania, gdy wybrane są bariery poziome
            this.slowlyX = (new Random().nextInt(18) + 1) * DOT_SIZE; // współrzędna X strzałki zwalniającej
            this.slowlyY = new Random().nextInt(20) * DOT_SIZE; // współrzędna Y strzałki zwalniającej
        }
        if (OptionsMenu.perimeterBarriers.isSelected()) { // opcja przetwarzania, gdy wybrane są bariery obwodowe
            this.slowlyX = (new Random().nextInt(18) + 1) * DOT_SIZE; // współrzędna X strzałki zwalniającej
            this.slowlyY = (new Random().nextInt(18) + 1) * DOT_SIZE; // współrzędna Y strzałki zwalniającej
        }
        newSlowlyDot(); // metoda która nie pozwalia  nakładania się obiektów, jak newApple();
    }

    /**
     *
     */

    private void newSlowlyDot() {
        for (int i = dots; i > 0; i--) { // Czy wąż zjada strzałę?
            if (this.x[i] == this.slowlyX && this.y[i] == this.slowlyY) {
                createSlowlyApple();
            }
        }
        if (this.appleX == this.slowlyX && this.appleY == this.slowlyY) { // Czy trafi na strzałki
            createSlowlyApple();
        }
    }

    /**
     * Metoda do ładowania zdjęć.
     */

    private void loadImages() throws IOException {
        apple = ImageIO.read(Objects.requireNonNull(GameField.class.getClassLoader().getResourceAsStream("plus.png"))); // załaduj obraz do obiektu
        dot = ImageIO.read(Objects.requireNonNull(GameField.class.getClassLoader().getResourceAsStream("star.png"))); // załadowanie punktu - (część) węża
        slowlyDot = ImageIO.read(Objects.requireNonNull(GameField.class.getClassLoader().getResourceAsStream("slowly.png"))); // obraz spowolnienia
        speedDot = ImageIO.read(Objects.requireNonNull(GameField.class.getClassLoader().getResourceAsStream("speed.png"))); // obraz, aby przyspieszyć
        backendGame = ImageIO.read(Objects.requireNonNull(GameField.class.getClassLoader().getResourceAsStream("backendGame.png")));  // obraz tła gry
        gameOver = ImageIO.read(Objects.requireNonNull(GameField.class.getClassLoader().getResourceAsStream("gameOver.png"))); //  ekran końca gry
        borderIcon = ImageIO.read(Objects.requireNonNull(GameField.class.getClassLoader().getResourceAsStream("borderIcon.png"))); // obramowanie obrazu
    }


    /**
     * Klasyczne przerysowanie podstawowych elementów okna.
     */

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (inGame) {
            g.drawImage(backendGame, 0, 0, this); // Rysowanie tła dla pola
            g.drawImage(apple, appleX, appleY, this);
            for (int i = 0; i < dots; i++) { // Przerysowanie całego wąża
                g.drawImage(dot, x[i], y[i], this);
            }
            if (speedApple) { // Jeśli strzałka przyspieszająca jest aktywna, narysuj ją
                g.drawImage(speedDot, speedX, speedY, this);
            }
            if (slowlyApple) { // Jeśli strzałka spowolnienia jest aktywna, narysuj ją
                g.drawImage(slowlyDot, slowlyX, slowlyY, this);
            }

            /**
             * Rysowanie okna w zależności od wybranego trybu gry.
             */

            // Jeśli wybrana jest opcja bez barier, to nie ma dodatkowego rysowania
            if (OptionsMenu.verticalBarriers.isSelected()) { // Jeśli wybrany jest wariant z przegrodami pionowymi, to narysuj przegrody pionowe.
                for (int i = 0; i < SIZE; i += DOT_SIZE) {
                    g.drawImage(borderIcon, i, 0, this);
                    g.drawImage(borderIcon, i, SIZE - DOT_SIZE, this);
                }
            }
            if (OptionsMenu.horizontalBarriers.isSelected()) { // Jeśli zaznaczona jest opcja z barierami poziomymi, to narysuj przegrody poziome.
                for (int i = 0; i < SIZE; i += DOT_SIZE) {
                    g.drawImage(borderIcon, 0, i, this);
                    g.drawImage(borderIcon, SIZE - DOT_SIZE, i, this);
                }
            }
            if (OptionsMenu.perimeterBarriers.isSelected()) { // Jeśli wybrana jest opcja z barierami na obwodzie, to narysuj je.
                for (int i = 0; i < SIZE; i += DOT_SIZE) {
                    g.drawImage(borderIcon, 0, i, this);
                    g.drawImage(borderIcon, SIZE - DOT_SIZE, i, this);
                    g.drawImage(borderIcon, i, 0, this);
                    g.drawImage(borderIcon, i, SIZE - DOT_SIZE, this);
                }
            }
        } else {
            g.drawImage(gameOver, 0, 0, this);
            String gameOver = "koniec gry. \r\n" + "Score: " + score;
            Font f = new Font("Arial", Font.BOLD, 14); // Utwórz czcionkę arial w rozmiarze 14 pogrubioną.
            g.setColor(Color.white); // Renderowanie koloru.
            g.setFont(f); // шрифт отрисовки
            g.drawString(gameOver, 90, SIZE / 2 + 15); // Pokazanie Game over.
            try {
                BufferedWriter fileWriter = new BufferedWriter(new FileWriter(url, true)); // Wywołanie fileWriter, aby zapisać wynik gry.
                fileWriter.write(score + "\r\n"); // Zapisanie wyniku.
                fileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            timer.stop();
        }
    }

    private void move() {
        for (int i = dots; i > 0; i--) {
            x[i] = x[i - 1]; // Przesunięcie współrzędnej X do końca (przesuń ciało)
            y[i] = y[i - 1]; // Przesunięcie współrzędnej Y do końca (przesuń ciało)
        }
        if (left) {
            x[0] -= DOT_SIZE; // Jeśli przesuwa się w lewo - przesunięcie współrzędnej X o jedną komórkę na lewo od aktualnej
        }
        if (right) {
            x[0] += DOT_SIZE; // Jeśli przesuwa się w prawo - przesunięcie współrzędnej X o jedną komórkę na prawo od aktualnej
        }
        if (up) {
            y[0] -= DOT_SIZE; // Jeśli porusza się w górę - przesunięcie współrzędnej Y o jedną komórkę wyżej niż aktualna
        }
        if (down) {
            y[0] += DOT_SIZE; // Jeśli przesuwa się w dół - przesunięcie współrzędnej Y o jedną komórkę poniżej aktualnej
        }
    }

    /**
     * Metoda służacą do podejmowania działań w wyniku spotkania się węża z jabłkiem.
     */

    private void checkApple() {
        if (x[0] == appleX && y[0] == appleY) { // Jeśli głowa spotka się z jabłkiem, to:
            dots++; // zwiększ węża
            score++;
            createApple(); // odtwórz jabłko

            if (score == 5) { // Przyspiesz węża.
                timer.setDelay(220);
            }
            if (score == 10) {
                timer.setDelay(200);
            }
            if (score == 15) {
                timer.setDelay(180);
            }
            if (score == 20) {
                timer.setDelay(160);
            }
            if (score == 30) {
                timer.setDelay(140);
            }
            if (score == 40) {
                timer.setDelay(120);
            }
            if (score == 50) {
                timer.setDelay(100);
            }
            if (score == 60) {
                timer.setDelay(80);
            }
        }

        if (speedApple) { // Jeśli wąż "zjadł" element spowalniający to:
            if (x[0] == speedX && y[0] == speedY) { //
                speedApple = false; // element znika
                timeFTUA = 25; // Reset licznika czasu.
                this.timer.setDelay(100); // przyspiesz węża
            }
        }
        if (slowlyApple) { // Jeśli wąż zjadł element spowalniający to:
            if (x[0] == slowlyX && y[0] == slowlyY) {
                slowlyApple = false; // element znika
                timeFTUA = 25; // Reset licznika czasu.
                this.timer.setDelay(300); // spowalnia węża
            }
        }
    }

    /**
     * Metoda sprawdzająca, czy wąż spotkał swój ogon.
     */

    private void checkCollisions() {
        for (int i = dots; i > 0; i--) {
            if (i > 3 && x[0] == x[i] && y[0] == y[i]) {
                inGame = false;
            }
        }
        /**
         * Kontrola wyjścia węża z pola
         */
        if (OptionsMenu.withoutBarriers.isSelected()) {
            if (x[0] > SIZE - DOT_SIZE) {
                x[0] = 0;
            }
            if (x[0] < 0) {
                x[0] = SIZE - DOT_SIZE;
            }
            if (y[0] > SIZE - DOT_SIZE) {
                y[0] = 0;
            }
            if (y[0] < 0) {
                y[0] = SIZE - DOT_SIZE;
            }
        }

        /**
         * Sprawdzenie, czy bariery są aktywne, czy nie
         */

        if (OptionsMenu.verticalBarriers.isSelected()) {
            checkVerticalCollisions();
        }
        if (OptionsMenu.horizontalBarriers.isSelected()) {
            checkHorizontalCollisions();
        }
        if (OptionsMenu.perimeterBarriers.isSelected()) {
            checkPerimeterCollisions();
        }
    }

    /**
     * Metoda sprawdzająca, czy wąż wyszedł poza pionowe granice planszy.
     */

    private void checkVerticalCollisions() {
        if (x[0] > SIZE - DOT_SIZE) {
            x[0] = 0;
        }
        if (x[0] < 0) {
            x[0] = SIZE - DOT_SIZE;
        }
        if (y[0] > SIZE - DOT_SIZE * 2) {
            inGame = false;
        }
        if (y[0] < DOT_SIZE) {
            inGame = false;
        }
    }

    /**
     * Metoda sprawdzająca, czy wąż wyszedł poza poziome granice planszy.
     */

    private void checkHorizontalCollisions() {
        if (x[0] > SIZE - DOT_SIZE * 2) {
            inGame = false;
        }
        if (x[0] < DOT_SIZE) {
            inGame = false;
        }
        if (y[0] > SIZE - DOT_SIZE) {
            y[0] = 0;
        }
        if (y[0] < 0) {
            y[0] = SIZE - DOT_SIZE;
        }
    }

    /**
     * Metoda sprawdzająca, czy wąż wyszedł poza obwód.
     */

    private void checkPerimeterCollisions() {
        if (x[0] > SIZE - DOT_SIZE * 2) {
            inGame = false;
        }
        if (x[0] < DOT_SIZE) {
            inGame = false;
        }
        if (y[0] > SIZE - DOT_SIZE * 2) {
            inGame = false;
        }
        if (y[0] < DOT_SIZE) {
            inGame = false;
        }
    }

    /**
     * Metoda generująca pole, za każdym razem przy zmianie jakiegokolwiek z obiektów.
     */

    public void actionPerformed(ActionEvent e) {
        if (inGame) {
            checkCollisions();
            checkApple();
            move();
            if (slowlyApple || speedApple) {
                timeFTUA--;
            }
            if (timeFTUA == 0) {
                if (slowlyApple) {
                    slowlyApple = false;
                    timeFTUA = 25;
                } else if (speedApple) {
                    speedApple = false;
                    timeFTUA = 25;
                }
            }
        }
        repaint(); // Przerysowanie pola albo przez system, albo przez wywołanie gracza.
    }

    /**
     * Klasa służąca do przedefiniowania klawiszy.
     */

    class FieldKeyListener extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            super.keyPressed(e);
            int key = e.getKeyCode();

            /**
             * Jeśli wybrano opcję sterowania strzałką.
             */

            if (OptionsMenu.controlOnArrow.isSelected()) {
                if (key == KeyEvent.VK_LEFT && !right) {
                    left = true;
                    up = false;
                    down = false;
                }
                if (key == KeyEvent.VK_RIGHT && !left) {
                    right = true;
                    up = false;
                    down = false;
                }
                if (key == KeyEvent.VK_DOWN && !up) {
                    down = true;
                    left = false;
                    right = false;
                }
                if (key == KeyEvent.VK_UP && !down) {
                    up = true;
                    left = false;
                    right = false;
                }
            } else if (OptionsMenu.controlOnWasd.isSelected()) { // Jeśli wybrano opcję sterowania WASD.
                if (key == KeyEvent.VK_A && !right) {
                    left = true;
                    up = false;
                    down = false;
                }
                if (key == KeyEvent.VK_D && !left) {
                    right = true;
                    up = false;
                    down = false;
                }
                if (key == KeyEvent.VK_S && !up) {
                    down = true;
                    left = false;
                    right = false;
                }
                if (key == KeyEvent.VK_W && !down) {
                    up = true;
                    left = false;
                    right = false;
                }
            }

            if (key == KeyEvent.VK_P) { // Pauza
                if (isPause) {
                    timer.start();
                    isPause = false;
                } else {
                    timer.stop();
                    isPause = true;
                }
            }
            if (key == KeyEvent.VK_ENTER) { // Wyjście do menu głównego.
                try {
                    MainMenu.locationWindow = MainMenu.gameWindow.getLocation(); // Podawanie ostatniej współrzędnej okna.
                    MainMenu.gameWindow.setVisible(false);
                    new MainMenu();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

    }

}
