package Group2;
import Interop.Action.*;
import Interop.Agent.Guard;
import Interop.Percept.GuardPercepts;

public class GuardAgent implements Guard{
    @Override
    public GuardAction getAction(GuardPercepts percepts) {
        return new NoAction();
    }
}
