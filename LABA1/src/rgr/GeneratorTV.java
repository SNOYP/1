package rgr;

import process.Actor;
import process.DispatcherFinishException;
import rnd.Randomable;
import widgets.Painter;

public class GeneratorTV extends Actor {
    private Randomable rnd;
    private double finishTime;
    private Model model;
    private Painter painter;
    private int createdCount = 0;

    @Override
    protected void rule() throws DispatcherFinishException {
        if (painter != null) {
            painter.placeToXY(0.0f, 0.0f);
        }
        while (getDispatcher().getCurrentTime() <= finishTime) {
            holdForTime(rnd.next()); 
            float currentTime = (float) getDispatcher().getCurrentTime();
            if (painter != null) {
                painter.drawToXY(currentTime, createdCount);
                createdCount++;
                painter.drawToXY(currentTime, createdCount);
            } else {
                createdCount++;
            }
            getDispatcher().printToProtocol("  " + getNameForProtocol() + " створив новий ТВ. Усього: " + createdCount);
         
            getDispatcher().addStartingActor(model.createTransaction());
        }
    }

    public void setRnd(Randomable rnd) { this.rnd = rnd; }
    public void setFinishTime(double finishTime) { this.finishTime = finishTime; }
    public void setModel(Model model) { this.model = model; }
    public void setPainter(Painter painter) { this.painter = painter; }
}