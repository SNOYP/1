package rgr;

import process.Actor;
import process.DispatcherFinishException;
import process.QueueForTransactions;

public class TransactionTV extends Actor {
    private QueueForTransactions<TransactionTV> queueCheck;
    private boolean serviceDone = false;

    @Override
    protected void rule() throws DispatcherFinishException {
        double createTime = getDispatcher().getCurrentTime();
        nameForProtocol = "ТВ " + String.format("%.2f", createTime);
        queueCheck.add(this);
        waitForCondition(() -> serviceDone, "мають завершити всі перевірки");
        getDispatcher().printToProtocol("  " + getNameForProtocol() + " успішно покинув систему.");
    }

    public void setQueueCheck(QueueForTransactions<TransactionTV> queueCheck) {
        this.queueCheck = queueCheck;
    }
    public void setServiceDone(boolean b) {
        this.serviceDone = b;
    }
}