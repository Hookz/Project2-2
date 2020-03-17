package Group2;

import Interop.Agent.Intruder;
import Interop.Action.*;
import Interop.Percept.IntruderPercepts;
public class IntruderAgent implements Intruder{
    @Override
    public IntruderAction getAction(IntruderPercepts percepts) {
        return new NoAction();
    }
}
