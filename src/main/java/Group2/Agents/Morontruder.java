package Group2.Agents;

import Interop.Action.*;
import Interop.Agent.Guard;
import Interop.Agent.Intruder;
import Interop.Geometry.Angle;
import Interop.Percept.GuardPercepts;
import Interop.Percept.IntruderPercepts;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPerceptType;

import java.util.Random;
import java.util.Set;

public class Morontruder implements Intruder {
    private int sprintCooldown = 0;
    private boolean lastTurnSawWall = false;
    private boolean negative = false;

    public IntruderAction getAction(IntruderPercepts percepts) {
        //Return whatever action to take, depending on the perception we have under here.
        Set<ObjectPercept> objects = percepts.getVision().getObjects().getAll();
        //If you see a wall, rotate
        Random random = new Random();
        double randomRotateChance = random.nextDouble();
        for(ObjectPercept object:objects){
            if(object.getType().equals(ObjectPerceptType.Wall)){
                if(lastTurnSawWall=false){negative = random.nextBoolean();}
                lastTurnSawWall = true;
                double angle = 20 + (20-percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees() * random.nextDouble());
                    if(negative){angle = angle*-1;}
                return new Rotate(Angle.fromDegrees(angle));
            }
            if(randomRotateChance<0.05){
                lastTurnSawWall = false;
                negative = random.nextBoolean();
                double angle = 20 + (20-percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees() * random.nextDouble());
                if(negative){angle = angle*-1;}
                return new Rotate(Angle.fromDegrees(angle));
            }
        }
        //Sprint, otherwise walk
        if(sprintCooldown==0){
            lastTurnSawWall = false;
            sprintCooldown = percepts.getScenarioIntruderPercepts().getSprintCooldown();
            return new Sprint(percepts.getScenarioIntruderPercepts().getMaxSprintDistanceIntruder());
        }else{
            lastTurnSawWall = false;
            sprintCooldown--;
            return new Move(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder());
        }
    }
}
