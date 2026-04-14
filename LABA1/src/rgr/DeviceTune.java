package rgr;

import process.Actor;
import process.DispatcherFinishException;
import process.QueueForTransactions;
import rnd.Randomable;
import java.util.function.BooleanSupplier;

public class DeviceTune extends Actor {
    private QueueForTransactions<TransactionTV> queueTune;
    private QueueForTransactions<TransactionTV> queueCheck;
    private Randomable rnd;
    private double finishTime;

    @Override
    protected void rule() throws DispatcherFinishException {
        BooleanSupplier tuneQueueSize = () -> queueTune.size() > 0;
        
        while (getDispatcher().getCurrentTime() <= finishTime) {
            waitForCondition(tuneQueueSize, "очікування бракованого ТВ");
            TransactionTV tv = queueTune.removeFirst();
            holdForTime(rnd.next());
            getDispatcher().printToProtocol("  " + getNameForProtocol() + " налаштував ТВ. Повертаємо на повторний контроль.");
            queueCheck.add(tv);
        }
    }

    public void setQueueTune(QueueForTransactions<TransactionTV> q) { this.queueTune = q; }
    public void setQueueCheck(QueueForTransactions<TransactionTV> q) { this.queueCheck = q; }
    public void setRnd(Randomable rnd) { this.rnd = rnd; }
    public void setFinishTime(double finishTime) { this.finishTime = finishTime; }
}