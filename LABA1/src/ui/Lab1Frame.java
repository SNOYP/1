package ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Random; 
import widgets.Diagram;
import widgets.ChooseData;
import stat.Histo;

public class Lab1Frame extends JFrame {
    private Diagram diagramDensity;
    private Diagram diagramIntegral;
    private ChooseData chooseDataSize;
    private JComboBox<String> chooseRandomBox; 
    private JTextArea textArea;
    private Histo histo;
    private Random randomGen; 

    public Lab1Frame() {
        setTitle("Лабораторна робота №1 | Примаченко Вадим [Варіант 14]");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel chartsPanel = new JPanel(new GridLayout(2, 1, 0, 10));

        diagramDensity = new Diagram();
        diagramDensity.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), 
                " Щільність розподілу імовірностей ", TitledBorder.CENTER, TitledBorder.TOP, 
                new Font("Segoe UI", Font.BOLD, 14)));

        diagramIntegral = new Diagram();
        diagramIntegral.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), 
                " Інтегральна функція ", TitledBorder.CENTER, TitledBorder.TOP, 
                new Font("Segoe UI", Font.BOLD, 14)));

        chartsPanel.add(diagramDensity);
        chartsPanel.add(diagramIntegral);
        
        add(chartsPanel, BorderLayout.CENTER);

        textArea = new JTextArea(20, 35); 
        textArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        textArea.setEditable(false);
        textArea.setBackground(new Color(245, 245, 245));
        
        JScrollPane scrollText = new JScrollPane(textArea);
        scrollText.setPreferredSize(new Dimension(350, 0)); 
        scrollText.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), 
                " Статистика ", TitledBorder.CENTER, TitledBorder.TOP, 
                new Font("Segoe UI", Font.BOLD, 14)));
        add(scrollText, BorderLayout.EAST);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 5));

        // ДОДАНІ ВСІ 6 РОЗПОДІЛІВ ЗГІДНО ЗІ ЗВІТОМ
        String[] distributions = {
            "Рівномірний [14; 56]", 
            "Розподіл Ерланга [m=14, k=2]", 
            "Нормальний [m=14, s=2.8]",
            "Трикутний [min=14, max=56, m=42]",
            "Дискретний",
            "Довільний"
        };
        chooseRandomBox = new JComboBox<>(distributions);
        chooseRandomBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chooseRandomBox.setPreferredSize(new Dimension(280, 40));
        
        chooseDataSize = new ChooseData();
        chooseDataSize.setTitle("Обсяг вибірки");
        chooseDataSize.setInt(1400); // Обсяг вибірки налаштовано на 1400 [cite: 33]

        JButton btnStart = new JButton("Generate");
        btnStart.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnStart.setForeground(Color.BLACK); 
        btnStart.setPreferredSize(new Dimension(130, 40));
        btnStart.setFocusPainted(false);
        btnStart.addActionListener(e -> runSimulation());

        controlPanel.add(chooseRandomBox); 
        controlPanel.add(chooseDataSize);
        controlPanel.add(btnStart);
        
        add(controlPanel, BorderLayout.SOUTH);

        histo = new Histo();
        randomGen = new Random();
    }

    private void runSimulation() {
        int size = chooseDataSize.getInt();
        int mode = chooseRandomBox.getSelectedIndex(); 
        String selectedName = chooseRandomBox.getSelectedItem().toString();
        
        histo.init(); 
        diagramDensity.clear();
        diagramIntegral.clear();

        diagramDensity.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), 
                " Щільність: " + selectedName + " ", TitledBorder.CENTER, TitledBorder.TOP, 
                new Font("Segoe UI", Font.BOLD, 14)));
        
        diagramIntegral.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), 
                " Інтегральна функція: " + selectedName + " ", TitledBorder.CENTER, TitledBorder.TOP, 
                new Font("Segoe UI", Font.BOLD, 14)));

        // 1. ГЕНЕРАЦІЯ ДАНИХ ДЛЯ ВАРІАНТУ 14
        for (int i = 0; i < size; i++) {
            double value = 0.0;
            double u = Math.random();
            switch (mode) {
                case 0: // Рівномірний (14 до 56)
                    value = 14.0 + (56.0 - 14.0) * u;
                    break;
                case 1: // Ерланга (середнє 14, k=2 -> lambda = 2/14 = 1/7)
                    value = -7.0 * Math.log(u * Math.random());
                    break;
                case 2: // Нормальний (m=14, sigma=2.8)
                    value = 14.0 + 2.8 * randomGen.nextGaussian();
                    break;
                case 3: // Трикутний (min=14, max=56, mode=42)
                    if (u < (42.0 - 14.0) / (56.0 - 14.0)) { 
                        value = 14.0 + Math.sqrt(u * (56.0 - 14.0) * (42.0 - 14.0));
                    } else {
                        value = 56.0 - Math.sqrt((1.0 - u) * (56.0 - 14.0) * (56.0 - 42.0));
                    }
                    break;
                case 4: // Дискретний (наближення для прикладу)
                    value = 14.0 + 7.0 * randomGen.nextInt(7);
                    break;
                case 5: // Довільний (значення 14, 28, 42, 56)
                    if (u < 0.49) {
                        value = 14.0 + (28.0 - 14.0) * (u / 0.49);
                    } else {
                        value = 42.0 + (56.0 - 42.0) * ((u - 0.49) / 0.51);
                    }
                    break;
            }
            histo.add(value);
        }

        histo.showRelFrec(diagramDensity); 
        textArea.setText(histo.toString());

        try { diagramIntegral.setPainterColor(Color.RED); } catch (Exception ex) {}
        
        // 2. ВІДМАЛЬОВКА ТЕОРЕТИЧНОЇ ІНТЕГРАЛЬНОЇ ФУНКЦІЇ (ОСІ ТА ГРАФІК)
        diagramIntegral.setVerticalMinText("0.0");
        diagramIntegral.setVerticalMaxText("1.2");

        switch (mode) {
            case 0: // Рівномірний
                diagramIntegral.setHorizontalMinText("14.0");
                diagramIntegral.setHorizontalMaxText("56.0");
                diagramIntegral.getPainter().placeToXY(14.0f, 0.0f);
                diagramIntegral.getPainter().drawToXY(56.0f, 1.0f);
                break;
                
            case 1: // Ерланга
                diagramIntegral.setHorizontalMinText("0.0");
                diagramIntegral.setHorizontalMaxText("40.0");
                diagramIntegral.getPainter().placeToXY(0.0f, 0.0f);
                for (float x = 0.0f; x <= 40.0f; x += 0.5f) {
                    float y = (float) (1.0 - Math.exp(-x/7.0) * (1.0 + x/7.0));
                    diagramIntegral.getPainter().drawToXY(x, y);
                }
                break;
                
            case 2: // Нормальний
                diagramIntegral.setHorizontalMinText("5.0");
                diagramIntegral.setHorizontalMaxText("23.0");
                diagramIntegral.getPainter().placeToXY(5.0f, 0.0f);
                for (float x = 5.0f; x <= 23.0f; x += 0.2f) {
                    float y = (float) (1.0 / (1.0 + Math.exp(-1.702 * (x - 14.0) / 2.8)));
                    diagramIntegral.getPainter().drawToXY(x, y);
                }
                break;

            case 3: // Трикутний
                diagramIntegral.setHorizontalMinText("14.0");
                diagramIntegral.setHorizontalMaxText("56.0");
                diagramIntegral.getPainter().placeToXY(14.0f, 0.0f);
                for (float x = 14.0f; x <= 56.0f; x += 0.5f) {
                    float y;
                    if (x <= 42.0f) {
                        y = ((x - 14.0f) * (x - 14.0f)) / ((56.0f - 14.0f) * (42.0f - 14.0f));
                    } else {
                        y = 1.0f - ((56.0f - x) * (56.0f - x)) / ((56.0f - 14.0f) * (56.0f - 42.0f));
                    }
                    diagramIntegral.getPainter().drawToXY(x, y);
                }
                break;
                
            case 4: // Дискретний (Сходинки)
                diagramIntegral.setHorizontalMinText("14.0");
                diagramIntegral.setHorizontalMaxText("56.0");
                diagramIntegral.getPainter().placeToXY(14.0f, 0.0f);
                for(int j = 0; j < 7; j++) {
                   float x1 = 14f + j * 7f;
                   float x2 = 14f + (j + 1) * 7f;
                   float y = (j + 1) / 7f;
                   diagramIntegral.getPainter().drawToXY(x2, y - 1f/7f); 
                   if(j < 6) diagramIntegral.getPainter().drawToXY(x2, y); 
                }
                break;

            case 5: // Довільний (з нульовою ймовірністю в центрі)
                diagramIntegral.setHorizontalMinText("14.0");
                diagramIntegral.setHorizontalMaxText("56.0");
                diagramIntegral.getPainter().placeToXY(14.0f, 0.0f);
                diagramIntegral.getPainter().drawToXY(28.0f, 0.49f);
                diagramIntegral.getPainter().drawToXY(42.0f, 0.49f);
                diagramIntegral.getPainter().drawToXY(56.0f, 1.0f);
                break;
        }
        
        diagramDensity.repaint();
        diagramIntegral.repaint();
    }
}