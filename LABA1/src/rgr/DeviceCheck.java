package rgr;

import process.Actor;
import process.DispatcherFinishException;
import process.QueueForTransactions;
import rnd.Randomable;
import java.util.function.BooleanSupplier;

public class DeviceCheck extends Actor {
    private QueueForTransactions<TransactionTV> queueCheck;
    private QueueForTransactions<TransactionTV> queueTune;
    private Randomable rnd;
    private double probDefect;
    private double finishTime;

    @Override
    protected void rule() throws DispatcherFinishException {
        BooleanSupplier queueSize = () -> queueCheck.size() > 0;
        while (getDispatcher().getCurrentTime() <= finishTime) {
            waitForCondition(queueSize, "у черзі має з'явитися ТВ на контроль");
            TransactionTV tv = queueCheck.removeFirst();
            holdForTime(rnd.next());
            if (Math.random() < probDefect) {
                getDispatcher().printToProtocol("  " + getNameForProtocol() + " виявив БРАК! ТВ відправлено на налаштування.");
                queueTune.add(tv); 
            } else {
                getDispatcher().printToProtocol("  " + getNameForProtocol() + " перевірку завершено успішно.");
                tv.setServiceDone(true); 
            }
        }
    }
    public void setQueueCheck(QueueForTransactions<TransactionTV> q) { this.queueCheck = q; }
    public void setQueueTune(QueueForTransactions<TransactionTV> q) { this.queueTune = q; }
    public void setRnd(Randomable rnd) { this.rnd = rnd; }
    public void setProbDefect(double prob) { this.probDefect = prob; }
    public void setFinishTime(double finishTime) { this.finishTime = finishTime; }
}