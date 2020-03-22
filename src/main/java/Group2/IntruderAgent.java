package Group2;

import Interop.Agent.Intruder;
import Interop.Action.*;
import Interop.Geometry.Distance;
import Interop.Percept.IntruderPercepts;
public class IntruderAgent implements Intruder{
    private int ID;

    public IntruderAgent(int ID){
        this.ID = ID;
    }

    @Override
    public IntruderAction getAction(IntruderPercepts percepts) {

        return new Move(new Distance(1));
    }
}
