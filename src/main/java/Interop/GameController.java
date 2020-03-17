package Interop;

import Group2.AgentsFactory;
import Group2.Teleport;
import Interop.Action.*;
import Interop.Agent.Guard;
import Interop.Agent.Intruder;
import Interop.Percept.GuardPercepts;
import Interop.Percept.IntruderPercepts;
import Interop.Percept.Percepts;
import Interop.Percept.Scenario.*;
import Interop.Geometry.*;
import Interop.Percept.Smell.SmellPercept;
import Interop.Percept.Sound.SoundPercept;
import Interop.Percept.Sound.SoundPerceptType;

import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.util.*;

public class GameController{

//    These should be initialised and created in the percept method
//    private ScenarioPercepts scenarioPercept;
//    private ScenarioIntruderPercepts scenarioIntruderPercepts;
//    private ScenarioGuardPercepts scenarioGuardPercepts;

    private final int height;
    private final int width;
    private final int numGuards;
    private final int numIntruders;
    private final double captureDistance;
    private final int winConditionIntruderRounds;
    private final GameMode gameMode;

    private final double maxRotationAngle;
    private final double defaultMaxMoveDistanceIntruder;
    private final double defaultMaxSprintDistanceIntruder;
    private final double defaultMaxMoveDistanceGuard;

    private final double viewAngle;
    private final int viewRays;
    private final double viewRangeIntruderNormal;
    private final double viewRangeIntruderShaded;
    private final double viewRangeGuardNormal;
    private final double viewRangeGuardShaded;
    private final double[] viewRangeSentry;

    private final double yellSoundRadius;
    private final double maxMoveSoundRadius;
    private final double windowSoundRadius;
    private final double doorSoundRadius;
    private final double radiusPheromone;

    private final double pheromoneDuration = 10; //SHOULD BE IN SCENARIO FILE BUT ISN'T SO WE SET IT MANUALLY HERE
    private final int sprintCooldown;
    private final int pheromoneCooldown;

    private final double slowDownModifierWindow;
    private final double slowDownModifierDoor;
    private final double slowDownModifierSentryTower;

    private List<Rectangle> targetArea;
    private List<Rectangle> spawnAreaIntruders;
    private List<Rectangle> spawnAreaGuards;
    private List<Rectangle> walls;
    private List<Teleport> teleports;
    private List<Rectangle> shaded;
    private List<Rectangle> doors;
    private List<Rectangle> windows;
    private List<Rectangle> sentries;
    private HashMap<Intruder,Ellipse2D> intruderLocations;
    private HashMap<Guard,Ellipse2D> guardLocations;
    private HashMap<Intruder,Direction> intruderDirections;
    private HashMap<Guard,Direction> guardDirections;
    private HashMap<Smell,Ellipse2D> guardSmellLocations;
    private HashMap<Smell,Ellipse2D> intruderSmellLocations;
    private HashMap<Sound,Ellipse2D> soundLocations;
    private HashMap<Intruder,Integer> intruderSprintCooldowns;
    private HashMap<Intruder,Integer> intruderPheromoneCooldowns;
    private HashMap<Guard,Integer> guardPheromoneCooldowns;

    private List<Intruder> intruders;
    private List<Guard> guards;

    private Turn turn = Turn.GuardTurn;

    public GameController(int gameMode, int height, int width, int numGuards, int numIntruders, double captureDistance, int winConditionIntruderRounds, double maxRotationAngle, double maxMoveDistanceIntruder,
                              double maxSprintDistanceIntruder, double maxMoveDistanceGuard, int sprintCooldown, int pheromoneCooldown, double radiusPheromone, double slowDownModifierWindow,
                              double slowDownModifierDoor, double slowDownModifierSentryTower, double viewAngle, int viewRays, double viewRangeIntruderNormal, double viewRangeIntruderShaded,
                              double viewRangeGuardNormal, double viewRangeGuardShaded, double[] viewRangeSentry, double yellSoundRadius, double maxMoveSoundRadius, double windowSoundRadius,
                              double doorSoundRadius, List<Rectangle> targetArea, List<Rectangle> spawnAreaIntruders, List<Rectangle> spawnAreaGuards, List<Rectangle> walls, List<Teleport> teleports,
                              List<Rectangle> shaded, List<Rectangle> doors, List<Rectangle> windows, List<Rectangle> sentries)
    {
//        this.scenarioPercept = new ScenarioPercepts(gamemode, new Distance(captureDistance), Angle.fromDegrees(maxRotationAngle), new SlowDownModifiers(slowDownModifierWindow, slowDownModifierDoor,
//                        slowDownModifierSentryTower), new Distance(radiusPheromone), pheromoneCooldown);
//        this.scenarioIntruderPercepts = new ScenarioIntruderPercepts(this.scenarioPercept, winConditionIntruderRounds, new Distance(maxMoveDistanceIntruder), new Distance(maxSprintDistanceIntruder), sprintCooldown);
//        this.scenarioGuardPercepts = new ScenarioGuardPercepts(this.scenarioPercept, new Distance(maxMoveDistanceGuard));

        this.gameMode = GameMode.values()[gameMode];
        this.height = height;
        this.width = width;
        this.numGuards = numGuards;
        this.numIntruders = numIntruders;
        this.captureDistance = captureDistance;
        this.winConditionIntruderRounds = winConditionIntruderRounds;

        this.maxRotationAngle = maxRotationAngle;
        this.defaultMaxMoveDistanceIntruder = maxMoveDistanceIntruder;
        this.defaultMaxSprintDistanceIntruder = maxSprintDistanceIntruder;
        this.defaultMaxMoveDistanceGuard = maxMoveDistanceGuard;

        this.sprintCooldown = sprintCooldown;
        this.pheromoneCooldown = pheromoneCooldown;

        this.slowDownModifierWindow = slowDownModifierWindow;
        this.slowDownModifierDoor = slowDownModifierDoor;
        this.slowDownModifierSentryTower = slowDownModifierSentryTower;

        this.viewAngle = viewAngle;
        this.viewRays = viewRays;
        this.viewRangeIntruderNormal = viewRangeIntruderNormal;
        this.viewRangeIntruderShaded = viewRangeIntruderShaded;
        this.viewRangeGuardNormal = viewRangeGuardNormal;
        this.viewRangeGuardShaded = viewRangeGuardShaded;
        this.viewRangeSentry = viewRangeSentry;

        this.yellSoundRadius = yellSoundRadius;
        this.maxMoveSoundRadius = maxMoveSoundRadius;
        this.windowSoundRadius = windowSoundRadius;
        this.doorSoundRadius = doorSoundRadius;
        this.radiusPheromone = radiusPheromone;

        this.targetArea = targetArea;
        this.spawnAreaIntruders = spawnAreaIntruders;
        this.spawnAreaGuards = spawnAreaGuards;
        this.walls = walls;
        this.teleports = teleports;
        this.shaded = shaded;
        this.doors = doors;
        this.windows = windows;
        this.sentries = sentries;

        /* THIS IS FOR PERIOD 1 AS WE ONLY NEED AN EXPLORATION AGENT
        Later there needs to be a functionality, such that we can choose which groups intruder, and which groups guard
        to use in the game.

        Also based on assumption that there will be 1 spawnArea per group
         */

        //Intruder Agent Constuctor
        Random random = new Random();
        if(numIntruders>0) {
            intruders = AgentsFactory.createIntruders(numIntruders);
            for (Intruder intruder : intruders) {
                double x = spawnAreaIntruders.get(0).getX() + spawnAreaIntruders.get(0).getWidth() * random.nextDouble();
                double y = spawnAreaIntruders.get(0).getY() + spawnAreaIntruders.get(0).getHeight() * random.nextDouble();
                Ellipse2D intruderLocation = new Ellipse2D.Double(x, y, 1, 1);
                intruderLocations.put(intruder, intruderLocation);

                double directionAngle = (2*Math.PI) * random.nextDouble();
                Direction direction = Direction.fromRadians(directionAngle);
                intruderDirections.put(intruder,direction);

                intruderSprintCooldowns.put(intruder,0);
                intruderPheromoneCooldowns.put(intruder,0);
            }
        }

        //Guard Agent Contructor
        if(numGuards>0) {
            guards = AgentsFactory.createGuards(numGuards);
            for (Guard guard : guards) {
                double x = spawnAreaGuards.get(0).getX() + spawnAreaGuards.get(0).getWidth() * random.nextDouble();
                double y = spawnAreaGuards.get(0).getY() + spawnAreaGuards.get(0).getHeight() * random.nextDouble();
                Ellipse2D guardLocation = new Ellipse2D.Double(x, y, 1, 1);
                guardLocations.put(guard, guardLocation);

                double directionAngle = (2*Math.PI) * random.nextDouble();
                Direction direction = Direction.fromRadians(directionAngle);
                guardDirections.put(guard,direction);

                guardPheromoneCooldowns.put(guard,0);
            }
        }

        //Run the UI construction
        //UI.createUI(<all the walls etc above here>)

        boolean intruderStart = random.nextBoolean();
        if(intruderStart){
            turn = Turn.IntruderTurn;
        }

        runGame();

    }

    private void runGame(){
        boolean gameOver = false;
        while(!gameOver) {
            smellDecay();
            soundDecay();
            switch (turn) {
                case GuardTurn:
                    if(!guards.isEmpty()) {
                        for (Guard guard : guards) {
                            GuardPercepts percept = guardPercept(guard);
                            Action action = guard.getAction(percept);
                            guardAct(action, percept, guard);
                        }
                    }
                    break;
                case IntruderTurn:
                    if(!intruders.isEmpty()) {
                        for (Intruder intruder : intruders) {
                            IntruderPercepts percept = intruderPercept(intruder);
                            Action action = intruder.getAction(percept);
                            intruderAct(action, percept, intruder);
                        }
                    }
                    break;
            }

            gameOver = checkVictory();

            //Switch turn
            if (turn.equals(Turn.GuardTurn)) {
                turn = Turn.IntruderTurn;
            } else {
                turn = Turn.GuardTurn;
            }
        }
    }

    private GuardPercepts guardPercept(Guard guard){

        return null;
    }

    private IntruderPercepts intruderPercept(Intruder intruder){

        return null;
    }

    private void guardAct(Action action, GuardPercepts percept, Guard guard){
        if(action instanceof Move){
            Ellipse2D guardLocation = guardLocations.get(guard);
            Direction guardDirection = guardDirections.get(guard);
            Sound stepSound = new Sound(SoundPerceptType.Noise,new Distance(maxMoveSoundRadius));
            Ellipse2D stepSoundLocation = (Ellipse2D) guardLocations.get(guard).clone();
            double newX = guardLocation.getX() + Math.cos(guardDirection.getDegrees()) * ((Move) action).getDistance().getValue();
            double newY = guardLocation.getY() + Math.sin(guardDirection.getDegrees()) * ((Move) action).getDistance().getValue();
            Ellipse2D newGuardLocation = new Ellipse2D.Double(newX,newY,guardLocation.getWidth(),guardLocation.getHeight());
            if(checkIfLegalMove(guardLocation,newGuardLocation)) {
                guardLocations.put(guard, newGuardLocation);
                soundLocations.put(stepSound,stepSoundLocation);
            }

        }else if(action instanceof Rotate){
            Direction guardDirection = guardDirections.get(guard);
            double newGuardAngle = guardDirection.getDegrees() + ((Rotate) action).getAngle().getDegrees();
            guardDirections.put(guard,Direction.fromDegrees(newGuardAngle));

        }else if(action instanceof Yell){
            Sound yell = new Sound(SoundPerceptType.Yell,new Distance(yellSoundRadius));
            Ellipse2D yellLocation = new Ellipse2D.Double(guardLocations.get(guard).getX(),guardLocations.get(guard).getY(),
                    yellSoundRadius*2.0,yellSoundRadius*2.0);
            soundLocations.put(yell,yellLocation);

        }else if(action instanceof DropPheromone){
            if(guardPheromoneCooldowns.get(guard)==0) {
                Smell smell = new Smell(((DropPheromone) action).getType(), new Distance(radiusPheromone));
                Ellipse2D smellLocation = new Ellipse2D.Double(guardLocations.get(guard).getX(), guardLocations.get(guard).getY(),
                        smell.getRadius().getValue() * 2.0, smell.getRadius().getValue() * 2.0); //*2 = Radius -> Diameter
                guardSmellLocations.put(smell, smellLocation);
                guardPheromoneCooldowns.replace(guard, pheromoneCooldown);
            }else{
                System.out.println("Still on cooldown, turn skipped");
            }
        }else if(action instanceof NoAction){

        }else{
            System.out.println("No action picked...");
        }

    }

    private void intruderAct(Action action, IntruderPercepts percept, Intruder intruder){
        if(action instanceof Move){
            Ellipse2D intruderLocation = intruderLocations.get(intruder);
            Direction intruderDirection = intruderDirections.get(intruder);
            Sound stepSound = new Sound(SoundPerceptType.Noise,new Distance(maxMoveSoundRadius));
            Ellipse2D stepSoundLocation = (Ellipse2D) intruderLocations.get(intruder).clone();
            double newX = intruderLocation.getX() + Math.cos(intruderDirection.getDegrees()) * ((Move) action).getDistance().getValue();
            double newY = intruderLocation.getY() + Math.sin(intruderDirection.getDegrees()) * ((Move) action).getDistance().getValue();
            Ellipse2D newintruderLocation = new Ellipse2D.Double(newX,newY,intruderLocation.getWidth(),intruderLocation.getHeight());
            if(checkIfLegalMove(intruderLocation,newintruderLocation)) {
                intruderLocations.put(intruder, newintruderLocation);
                soundLocations.put(stepSound,stepSoundLocation);
            }

        }else if(action instanceof Rotate){
            Direction intruderDirection = intruderDirections.get(intruder);
            double newintruderAngle = intruderDirection.getDegrees() + ((Rotate) action).getAngle().getDegrees();
            intruderDirections.put(intruder,Direction.fromDegrees(newintruderAngle));

        }else if(action instanceof Sprint){
            if(intruderSprintCooldowns.get(intruder)==0) {
                Ellipse2D intruderLocation = intruderLocations.get(intruder);
                Direction intruderDirection = intruderDirections.get(intruder);
                Sound stepSound = new Sound(SoundPerceptType.Noise, new Distance(maxMoveSoundRadius));
                Ellipse2D stepSoundLocation = (Ellipse2D) intruderLocations.get(intruder).clone();
                double newX = intruderLocation.getX() + Math.cos(intruderDirection.getDegrees()) * ((Move) action).getDistance().getValue();
                double newY = intruderLocation.getY() + Math.sin(intruderDirection.getDegrees()) * ((Move) action).getDistance().getValue();
                Ellipse2D newintruderLocation = new Ellipse2D.Double(newX, newY, intruderLocation.getWidth(), intruderLocation.getHeight());
                if (checkIfLegalMove(intruderLocation, newintruderLocation)) {
                    intruderLocations.put(intruder, newintruderLocation);
                    soundLocations.put(stepSound, stepSoundLocation);
                }else{
                    System.out.println("Illegal move attempted");
                }
            }else{
                System.out.println("Still on cooldown, turn skipped");
            }

        }else if(action instanceof DropPheromone){
            if(intruderPheromoneCooldowns.get(intruder)==0) {
                Smell smell = new Smell(((DropPheromone) action).getType(), new Distance(radiusPheromone));
                Ellipse2D smellLocation = new Ellipse2D.Double(intruderLocations.get(intruder).getX(), intruderLocations.get(intruder).getY(),
                        smell.getRadius().getValue() * 2.0, smell.getRadius().getValue() * 2.0);
                intruderSmellLocations.put(smell, smellLocation);
            }else{
                System.out.println("Still on cooldown, turn skipped");
            }

        }else if(action instanceof NoAction){

        }else{
            System.out.println("No action picked...");
        }
    }

    private boolean checkIfLegalMove(Ellipse2D initialLocation, Ellipse2D newLocation){
        //Check for collisions. If collision occurs, return false and skip turn

        boolean legal = true;

        //If at some point during checking collision is detected, change legal to false

        return legal;
    }

    //Smell radius decreases in size each turn
    private void smellDecay(){
        if (!intruderSmellLocations.isEmpty()) {
            for (Smell smell : intruderSmellLocations.keySet()) {
                Distance previousSmellRadius = smell.getRadius();
                Distance newSmellRadius = new Distance(previousSmellRadius.getValue() - (radiusPheromone / pheromoneDuration));
                if (newSmellRadius.getValue() > 0) {
                    Smell updatedSmell = new Smell(smell.getType(), newSmellRadius);
                    Ellipse2D location = intruderSmellLocations.get(smell);
                    Ellipse2D updatedLocation = new Ellipse2D.Double(location.getX(), location.getY(), newSmellRadius.getValue(), newSmellRadius.getValue());
                    intruderSmellLocations.remove(smell);
                    intruderSmellLocations.put(updatedSmell, updatedLocation);
                } else {
                    intruderSmellLocations.remove(smell);
                }
            }
        }
    }

    private void soundDecay(){
        if(!soundLocations.isEmpty()){
            for (Sound sound:soundLocations.keySet()){
                sound.decaySound();
                if (sound.getDuration()<=0){
                    soundLocations.remove(sound);
                }
            }
        }
    }

    private boolean checkVictory(){
        //Check if game is over and victory is achieved
        boolean gameOver = false;

        //If victory condition is met, set gameOver to true
        return gameOver;
    }
}