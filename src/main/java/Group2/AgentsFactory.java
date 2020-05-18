package Group2;

import Group2.Agents.ExplorerAgent;
import Group2.Agents.TargetFinder;
import Interop.Agent.Guard;
import Interop.Agent.Intruder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class provides common way to build agents for the competition.
 *
 * Sharing knowledge between agents is NOT ALLOWED.
 *
 * For example:
 * Agents must not hold ANY references to common objects or references to each other.
 */
public class AgentsFactory {
    static public List<Intruder> createIntruders(int number) {
        ArrayList<Intruder> intruders = new ArrayList<>();
        for(int i=0;i<number;i++){
            TargetFinder intruder = new TargetFinder(i);
            intruders.add(intruder);
        }
        return intruders;
    }
    static public List<Guard> createGuards(int number) {
        ArrayList<Guard> guards = new ArrayList<>();
        for(int i=0;i<number;i++){
            Guard guard = new GuardAgent(i);
            guards.add(guard);
        }
        return guards;
    }
}