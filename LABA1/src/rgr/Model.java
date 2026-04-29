package rgr;

import process.Dispatcher;
import process.QueueForTransactions;
import process.MultiActor;
import stat.DiscretHisto;
import stat.Histo;
import stat.IHisto;
import widgets.stat.IStatisticsable;
import widgets.experiments.IExperimentable;
import widgets.trans.ITransMonitoring; // Додано для Лабораторної 7
import widgets.trans.ITransProcesable; // Додано для Лабораторної 7

import java.util.LinkedHashMap;
import java.util.Map;

public class Model implements IStatisticsable, IExperimentable, ITransProcesable {
    private Dispatcher dispatcher;
    private RGRStage1Frame gui;

    private GeneratorTV generator;
    private DeviceCheck deviceCheck;
    private MultiActor multiDeviceCheck; 
    private DeviceTune deviceTune;

    private QueueForTransactions<TransactionTV> queueCheck;
    private QueueForTransactions<TransactionTV> queueTune;

    DiscretHisto discretHistoCheckQueue = new DiscretHisto();
    DiscretHisto discretHistoTuneQueue = new DiscretHisto();
    Histo histoWaitCheck = new Histo();
    Histo histoWaitTune = new Histo();
    Histo histoServiceTime = new Histo();

    public Model(Dispatcher d, RGRStage1Frame g) {
        if (d == null || g == null) {
            System.out.println("Не визначено диспетчера або GUI для Model");
            System.exit(0);
        }
        dispatcher = d;
        gui = g;
        componentsToStartList();
    }

    public void componentsToStartList() {
        dispatcher.addStartingActor(getGenerator());
        dispatcher.addStartingActor(getMultiDeviceCheck());
        dispatcher.addStartingActor(getDeviceTune());
    }

    public void initForTest() {
        getGenerator().setPainter(gui.getDiagramArrivals().getPainter());
        getQueueCheck().setPainter(gui.getDiagramCheckQueue().getPainter());
        getQueueTune().setPainter(gui.getDiagramTuneQueue().getPainter());

        if (gui.getCbProtocolToConsole().isSelected())
            dispatcher.setProtocolFileName("Console");
        else
            dispatcher.setProtocolFileName("");
    }

    // ==========================================
    // РЕАЛІЗАЦІЯ ІНТЕРФЕЙСУ IStatisticsable (Лаба 5)
    // ==========================================
    @Override
    public void initForStatistics() {
        dispatcher.setProtocolFileName("");
    }

    @Override
    public Map<String, IHisto> getStatistics() {
        Map<String, IHisto> stats = new LinkedHashMap<>();
        stats.put("1. Довжина черги на контроль", discretHistoCheckQueue);
        stats.put("2. Довжина черги на налаштування", discretHistoTuneQueue);
        stats.put("3. Час перебування ТВ в системі", histoServiceTime);
        stats.put("4. Час простою тестувальника", histoWaitCheck);
        stats.put("5. Час простою майстра", histoWaitTune);
        return stats;
    }

    // ==========================================
    // РЕАЛІЗАЦІЯ ІНТЕРФЕЙСУ IExperimentable (Лаба 6)
    // ==========================================
    @Override
    public void initForExperiment(double factor) {
        getDeviceCheck().setProbDefect(factor);
        dispatcher.setProtocolFileName("");
    }

    @Override
    public Map<String, Double> getResultOfExperiment() {
        Map<String, Double> results = new LinkedHashMap<>();
        results.put("Сер. черга на контроль", discretHistoCheckQueue.getAverage());
        results.put("Сер. черга на налаштування (Брак)", discretHistoTuneQueue.getAverage());
        results.put("Сер. час в системі", histoServiceTime.getAverage());
        results.put("Час простою майстра", histoWaitTune.getAverage());
        return results;
    }

    // ==========================================
    // РЕАЛІЗАЦІЯ ІНТЕРФЕЙСУ ITransProcesable (Лаба 7)
    // ==========================================
    @Override
    public void initForTrans(double finishTime) {
        // Налаштування часу моделювання для дослідження перехідного процесу
        gui.getTimeSetting().setDouble(finishTime);
        getGenerator().setFinishTime(finishTime);
        getDeviceCheck().setFinishTime(finishTime);
        getDeviceTune().setFinishTime(finishTime);
        dispatcher.setProtocolFileName(""); // Вимикаємо протокол для прискорення
    }

    @Override
    public Map<String, ITransMonitoring> getMonitoringObjects() {
        // Передаємо черги для моніторингу перехідних процесів
        Map<String, ITransMonitoring> map = new LinkedHashMap<>();
        map.put("Черга на контроль", (ITransMonitoring) getQueueCheck());
        map.put("Черга на налаштування", (ITransMonitoring) getQueueTune());
        return map;
    }

    // ==========================================
    // ГЕТТЕРИ ТА СТВОРЕННЯ АКТОРІВ
    // ==========================================
    public GeneratorTV getGenerator() {
        if (generator == null) {
            generator = new GeneratorTV();
            generator.setNameForProtocol("Генератор ТВ");
            generator.setModel(this);
            generator.setFinishTime(gui.getTimeSetting().getDouble());
            generator.setRnd(gui.getRndArrival().getRandom());
        }
        return generator;
    }

    public DeviceCheck getDeviceCheck() {
        if (deviceCheck == null) {
            deviceCheck = new DeviceCheck();
            deviceCheck.setNameForProtocol("Тестувальник");
            deviceCheck.setQueueCheck(getQueueCheck());
            deviceCheck.setQueueTune(getQueueTune());
            deviceCheck.setRnd(gui.getRndCheck().getRandom());
            deviceCheck.setProbDefect(gui.getProbDefect().getDouble());
            deviceCheck.setFinishTime(gui.getTimeSetting().getDouble());
            deviceCheck.setHistoForActorWaitingTime(histoWaitCheck); 
        }
        return deviceCheck;
    }

    public MultiActor getMultiDeviceCheck() {
        if (multiDeviceCheck == null) {
            multiDeviceCheck = new MultiActor();
            multiDeviceCheck.setNameForProtocol("Бригада тестувальників");
            multiDeviceCheck.setOriginal(getDeviceCheck());
            multiDeviceCheck.setNumberOfClones(gui.getCountTesters().getInt());
        }
        return multiDeviceCheck;
    }

    public DeviceTune getDeviceTune() {
        if (deviceTune == null) {
            deviceTune = new DeviceTune();
            deviceTune.setNameForProtocol("Майстер налаштування");
            deviceTune.setQueueTune(getQueueTune());
            deviceTune.setQueueCheck(getQueueCheck());
            deviceTune.setRnd(gui.getRndTune().getRandom());
            deviceTune.setFinishTime(gui.getTimeSetting().getDouble());
            deviceTune.setHistoForActorWaitingTime(histoWaitTune); 
        }
        return deviceTune;
    }

    public QueueForTransactions<TransactionTV> getQueueCheck() {
        if (queueCheck == null) {
            queueCheck = new QueueForTransactions<>("Черга на контроль", dispatcher, discretHistoCheckQueue);
        }
        return queueCheck;
    }

    public QueueForTransactions<TransactionTV> getQueueTune() {
        if (queueTune == null) {
            queueTune = new QueueForTransactions<>("Черга на налаштування", dispatcher, discretHistoTuneQueue);
        }
        return queueTune;
    }

    public TransactionTV createTransaction() {
        TransactionTV tv = new TransactionTV();
        tv.setQueueCheck(getQueueCheck());
        tv.setHistoServiceTime(histoServiceTime); 
        return tv;
    }
}