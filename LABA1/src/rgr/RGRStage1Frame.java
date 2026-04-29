package rgr;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import process.Dispatcher;
import process.IModelFactory;
import rnd.*;
import widgets.ChooseData;
import widgets.ChooseRandom;
import widgets.Diagram;
import widgets.stat.StatisticsManager;
import widgets.experiments.ExperimentManager; // Додано для Лабораторної 6

public class RGRStage1Frame extends JFrame {
    
    private ChooseRandom rndArrival;
    private ChooseRandom rndCheck;
    private ChooseRandom rndTune;
    private ChooseData countTesters;
    private ChooseData probDefect;
    private ChooseData timeSetting;
    
    private JPanel panelTest;
    private Diagram diagramArrivals;
    private Diagram diagramCheckQueue;
    private Diagram diagramTuneQueue;
    private JCheckBox cbProtocolToConsole;
    private JButton btnStart;
    
    // Менеджери
    private StatisticsManager statManager;
    private ExperimentManager expManager; // Додано для Лабораторної 6

    public RGRStage1Frame() {
        setTitle("РГР: Дослідження роботи ВТК (TestTV) | Варіант 14");
        setSize(1050, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(mainPanel);

        // ==========================================
        // 1. ПАНЕЛЬ НАЛАШТУВАНЬ
        // ==========================================
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(130, 130, 130)),
                " Параметри системи ", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13)));
        settingsPanel.setPreferredSize(new Dimension(320, 0));

        rndArrival = new ChooseRandom();
        rndArrival.setRandom(new Negexp(5.0)); 
        JPanel pArrival = createWrapperPanel("Інтервал надходження нових ТВ", rndArrival);

        rndCheck = new ChooseRandom();
        rndCheck.setRandom(new Norm(3.0, 0.5)); 
        JPanel pCheck = createWrapperPanel("Час перевірки на пункті контролю", rndCheck);

        rndTune = new ChooseRandom();
        rndTune.setRandom(new Uniform(5.0, 12.0));
        JPanel pTune = createWrapperPanel("Час налаштування бракованого ТВ", rndTune);

        countTesters = new ChooseData();
        countTesters.setTitle("Кількість тестувальників");
        countTesters.setInt(1);

        probDefect = new ChooseData();
        probDefect.setTitle("Частка бракованих ТВ (0.0 - 1.0)");
        probDefect.setDouble(0.15);

        timeSetting = new ChooseData();
        timeSetting.setTitle("Час моделювання системи");
        timeSetting.setInt(1400); 

        settingsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        settingsPanel.add(pArrival);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        settingsPanel.add(pCheck);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        settingsPanel.add(pTune);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        settingsPanel.add(countTesters);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        settingsPanel.add(probDefect);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        settingsPanel.add(timeSetting);
        settingsPanel.add(Box.createVerticalGlue());

        mainPanel.add(settingsPanel, BorderLayout.WEST);

        // ==========================================
        // 2. ПАНЕЛЬ ВКЛАДОК
        // ==========================================
        UIManager.put("TabbedPane.font", new Font("Monospaced", Font.BOLD, 14));
        JTabbedPane tabs = new JTabbedPane();

        // --- Вкладка "Test" ---
        panelTest = new JPanel(new BorderLayout(10, 10));
        panelTest.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JPanel diagramsPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        
        diagramArrivals = new Diagram();
        diagramArrivals.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), 
                " Динаміка надходження ТВ (Загальна кількість) ", TitledBorder.CENTER, TitledBorder.TOP, 
                new Font("Segoe UI", Font.BOLD, 13)));
        diagramArrivals.setVerticalMaxText("300"); 
        try { diagramArrivals.setPainterColor(new Color(34, 139, 34)); } catch (Exception ex) {}

        diagramCheckQueue = new Diagram();
        diagramCheckQueue.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), 
                " Розмір черги на контроль ", TitledBorder.CENTER, TitledBorder.TOP, 
                new Font("Segoe UI", Font.BOLD, 13)));
        diagramCheckQueue.setVerticalMaxText("15");
        try { diagramCheckQueue.setPainterColor(Color.RED); } catch (Exception ex) {}
        
        diagramTuneQueue = new Diagram();
        diagramTuneQueue.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), 
                " Розмір черги на налаштування (Брак) ", TitledBorder.CENTER, TitledBorder.TOP, 
                new Font("Segoe UI", Font.BOLD, 13)));
        diagramTuneQueue.setVerticalMaxText("10");
        try { diagramTuneQueue.setPainterColor(new Color(0, 0, 139)); } catch (Exception ex) {}

        diagramsPanel.add(diagramArrivals);
        diagramsPanel.add(diagramCheckQueue);
        diagramsPanel.add(diagramTuneQueue);
        
        JPanel testControlPanel = new JPanel(new BorderLayout());
        cbProtocolToConsole = new JCheckBox("Протокол на консоль", true);
        cbProtocolToConsole.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        btnStart = new JButton("Старт");
        btnStart.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnStart.setPreferredSize(new Dimension(120, 35));
        
        testControlPanel.add(cbProtocolToConsole, BorderLayout.WEST);
        testControlPanel.add(btnStart, BorderLayout.EAST);
        
        panelTest.add(diagramsPanel, BorderLayout.CENTER);
        panelTest.add(testControlPanel, BorderLayout.SOUTH);
        
        tabs.addTab(" Test ", panelTest);

        // --- Вкладка "Stat" ---
        statManager = new StatisticsManager();
        statManager.setFactory((d) -> new Model(d, this));
        tabs.addTab(" Stat ", statManager);

        // --- Вкладка "Regres" (Лабораторна 6) ---
        expManager = new ExperimentManager();
        expManager.setFactory((d) -> new Model(d, this)); // Підключаємо фабрику
        tabs.addTab(" Regres ", expManager);

        // --- Вкладка "Tz" ---
        JTextArea tzArea = new JTextArea();
        tzArea.setText("ТЕХНІЧНЕ ЗАВДАННЯ\n\n" +
                "Тема: Моделювання роботи відділу технічного контролю (TestTV)\n" +
                "Виконавець: Примаченко Вадим Олександрович | Варіант: 14\n\n" +
                "Опис системи:\n" +
                "На заключній стадії виробництва телевізорів здійснюється їх перевірка. " +
                "Якщо під час перевірки виявилося, що телевізор працює неправильно (імовірність браку), то він направляється " +
                "в пункт налаштування. Після налаштування телевізор знову направляється в пункт " +
                "контролю для перевірки. Телевізори, які пройшли перевірку, направляються в цех пакування.");
        tzArea.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        tzArea.setLineWrap(true);
        tzArea.setWrapStyleWord(true);
        tzArea.setEditable(false);
        tzArea.setMargin(new Insets(20, 20, 20, 20));
        tabs.addTab(" Tz ", new JScrollPane(tzArea));

        // --- Вкладка "Info" ---
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        JLabel detailsLabel = new JLabel(
            "<html><div style='text-align: center; font-family: Segoe UI;'>" +
            "<h2 style='color: #2c3e50; margin-bottom: 10px;'>Примаченко Вадим Олександрович</h2>" +
            "<p style='font-size: 15px; color: #444; line-height: 1.5;'>" +
            "<b>Спеціальність:</b> Комп'ютерна інженерія<br>" +
            "<b>Університет:</b> НУ «Чернігівська політехніка»<br>" +
            "<b>Варіант:</b> 14 (TestTV)<br><br>" +
            "<i>Розробка імітаційної моделі СМО на Java</i></p></div></html>"
        );
        infoPanel.add(detailsLabel);
        tabs.addTab(" Info ", infoPanel);

        mainPanel.add(tabs, BorderLayout.CENTER);

        // ==========================================
        // 3. ОБРОБКА ПОДІЙ
        // ==========================================
        
        panelTest.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                updateDiagramsXAxis();
            }
        });

        btnStart.addActionListener(e -> {
            updateDiagramsXAxis();
            startTest(); 
        });
    }

    private void updateDiagramsXAxis() {
        String maxTime = String.valueOf(timeSetting.getInt());
        diagramArrivals.setHorizontalMaxText(maxTime);
        diagramCheckQueue.setHorizontalMaxText(maxTime);
        diagramTuneQueue.setHorizontalMaxText(maxTime);
    }

    private JPanel createWrapperPanel(String title, JComponent comp) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), 
                title, TitledBorder.LEFT, TitledBorder.TOP, 
                new Font("Segoe UI", Font.PLAIN, 12)));
        panel.add(comp, BorderLayout.CENTER);
        panel.setMaximumSize(new Dimension(300, 60));
        return panel;
    }

    private void startTest() {
        diagramArrivals.clear(); 
        diagramCheckQueue.clear();
        diagramTuneQueue.clear();

        Dispatcher dispatcher = new Dispatcher();
        
        IModelFactory factory = (d) -> new Model(d, this);
        Model model = (Model) factory.createModel(dispatcher);

        btnStart.setEnabled(false);
        dispatcher.addDispatcherFinishListener(() -> btnStart.setEnabled(true));

        model.initForTest();
        dispatcher.start();
    }

    public ChooseRandom getRndArrival() { return rndArrival; }
    public ChooseRandom getRndCheck() { return rndCheck; }
    public ChooseRandom getRndTune() { return rndTune; }
    public ChooseData getCountTesters() { return countTesters; }
    public ChooseData getProbDefect() { return probDefect; }
    public ChooseData getTimeSetting() { return timeSetting; }
    
    public JPanel getPanelTest() { return panelTest; }
    public Diagram getDiagramArrivals() { return diagramArrivals; }
    public Diagram getDiagramCheckQueue() { return diagramCheckQueue; }
    public Diagram getDiagramTuneQueue() { return diagramTuneQueue; }
    public JCheckBox getCbProtocolToConsole() { return cbProtocolToConsole; }
    public JButton getBtnStart() { return btnStart; }
}