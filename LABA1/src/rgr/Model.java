package rgr;

import process.Dispatcher;
import process.QueueForTransactions;
import process.MultiActor;
import stat.DiscretHisto;
import stat.Histo;

public class Model {
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
        // ПРИВ'ЯЗУЄМО НОВИЙ ЗЕЛЕНИЙ ГРАФІК ДО ГЕНЕРАТОРА
        getGenerator().setPainter(gui.getDiagramArrivals().getPainter());
        
        getQueueCheck().setPainter(gui.getDiagramCheckQueue().getPainter());
        getQueueTune().setPainter(gui.getDiagramTuneQueue().getPainter());

        if (gui.getCbProtocolToConsole().isSelected())
            dispatcher.setProtocolFileName("Console");
        else
            dispatcher.setProtocolFileName("");
    }

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
        return tv;
    }
}