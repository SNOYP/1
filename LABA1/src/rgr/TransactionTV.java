package rgr;

import process.Actor;
import process.DispatcherFinishException;
import process.QueueForTransactions;
import stat.Histo; // Імпорт гістограми

public class TransactionTV extends Actor {
    private QueueForTransactions<TransactionTV> queueCheck;
    private boolean serviceDone = false;
    private Histo histoServiceTime; // Гістограма для часу в системі

    @Override
    protected void rule() throws DispatcherFinishException {
        double createTime = getDispatcher().getCurrentTime(); // Фіксуємо час створення
        nameForProtocol = "ТВ " + String.format("%.2f", createTime);
        queueCheck.add(this);
        
        waitForCondition(() -> serviceDone, "мають завершити всі перевірки");
        
        // Зберігаємо загальний час перебування в системі (від генерації до виходу)
        if (histoServiceTime != null) {
            histoServiceTime.add(getDispatcher().getCurrentTime() - createTime);
        }
        
        getDispatcher().printToProtocol("  " + getNameForProtocol() + " успішно покинув систему.");
    }

    public void setQueueCheck(QueueForTransactions<TransactionTV> queueCheck) {
        this.queueCheck = queueCheck;
    }
    
    public void setServiceDone(boolean b) {
        this.serviceDone = b;
    }
    
    public void setHistoServiceTime(Histo histoServiceTime) {
        this.histoServiceTime = histoServiceTime;
    }
}