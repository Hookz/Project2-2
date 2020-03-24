package Interop;

import Group2.AgentsFactory;
import Interop.Action.*;
import Interop.Agent.*;
import Interop.Percept.*;
import Interop.Percept.Scenario.*;
import Interop.Geometry.*;
import Interop.Utils.Utils;

import Interop.Percept.Smell.*;
import Interop.Percept.Sound.*;
import Interop.Percept.Vision.*;

import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.*;

public class GameController{

//    These should be initialised and created in the percept method
//    private ScenarioPercepts scenarioPercept;
//    private ScenarioIntruderPercepts scenarioIntruderPercepts;
//    private ScenarioGuardPercepts scenarioGuardPercepts;
    public final boolean DEBUG_TEXT = false;

    public final int height;
    public final int width;
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

    private final double pheromoneExpireRounds;
    private final int sprintCooldown;
    private final int pheromoneCooldown;

    private final double slowDownModifierWindow;
    private final double slowDownModifierDoor;
    private final double slowDownModifierSentryTower;

    private List<Rectangle> targetArea;
    private List<Rectangle> spawnAreaIntruders;
    private List<Rectangle> spawnAreaGuards;
    public List<Rectangle> walls;
    public List<Teleport> teleports;
    public List<Rectangle> shaded;
    private List<Rectangle> doors;
    private List<Rectangle> windows;
    private List<Rectangle> sentries;
    public HashMap<Intruder,Ellipse2D> intruderLocations = new HashMap<>();
    public HashMap<Guard,Ellipse2D> guardLocations = new HashMap<>();
    private HashMap<Intruder,Direction> intruderDirections = new HashMap<>();
    private HashMap<Guard,Direction> guardDirections = new HashMap<>();
    public HashMap<Smell,Ellipse2D> guardSmellLocations = new HashMap<>();
    public HashMap<Smell,Ellipse2D> intruderSmellLocations = new HashMap<>();
    public HashMap<Sound,Ellipse2D> soundLocations = new HashMap<>();
    private HashMap<Intruder,Integer> intruderSprintCooldowns = new HashMap<>();
    private HashMap<Intruder,Integer> intruderPheromoneCooldowns = new HashMap<>();
    private HashMap<Guard,Integer> guardPheromoneCooldowns = new HashMap<>();
    private HashMap<Intruder,Boolean> intruderTeleportFlag = new HashMap<>();
    private HashMap<Guard,Boolean> guardTeleportFlag = new HashMap<>();
    private HashMap<Intruder, Distance> intruderViewRange = new HashMap<>();
    private HashMap<Guard, Distance> guardViewRange = new HashMap<>();
    private HashMap<Intruder, Double> intruderMaxMoveDistance = new HashMap<>();
    private HashMap<Intruder, Double> intruderMaxSprintDistance = new HashMap<>();
    private HashMap<Guard, Double> guardMaxMoveDistance = new HashMap<>();

    private List<Intruder> intruders = new ArrayList<>();
    private List<Guard> guards = new ArrayList<>();

    private Turn turn = Turn.GuardTurn;

    private ScenarioPercepts scenarioPercepts;
    private ScenarioIntruderPercepts scenarioIntruderPercepts;
    private ScenarioGuardPercepts scenarioGuardPercepts;
    private HashMap<Guard, AreaPercepts> guardsAreaPercepts = new HashMap<>();
    private HashMap<Intruder, AreaPercepts> intruderAreaPercepts = new HashMap<>();
    private HashMap<Intruder, SoundPercepts> intruderSoundPercepts = new HashMap<>();
    private HashMap<Guard, SoundPercepts> guardSoundPercepts = new HashMap<>();
    private HashMap<Intruder, SmellPercepts> intruderSmellPercepts = new HashMap<>();
    private HashMap<Guard, SmellPercepts> guardSmellPercepts = new HashMap<>();
    private HashMap<Intruder, ObjectPercepts> intruderObjectPercept = new HashMap<>();
    private HashMap<Guard, ObjectPercepts> guardObjectPercepts = new HashMap<>();

    private boolean wasLastActionExecuted;


    private final double COLLISION_CHECK_STEP_SIZE = 0.05;

    public GameController(int gameMode, int height, int width, int numGuards, int numIntruders, double captureDistance, int winConditionIntruderRounds, double maxRotationAngle, double maxMoveDistanceIntruder,
                              double maxSprintDistanceIntruder, double maxMoveDistanceGuard, int pheromoneExpireRounds, int sprintCooldown, int pheromoneCooldown, double radiusPheromone, double slowDownModifierWindow,
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
        this.pheromoneExpireRounds = pheromoneExpireRounds;

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

        this.scenarioPercepts = new ScenarioPercepts(this.gameMode, new Distance(captureDistance), Angle.fromDegrees(maxRotationAngle), new SlowDownModifiers(slowDownModifierWindow, slowDownModifierDoor,
                        slowDownModifierSentryTower), new Distance(radiusPheromone), pheromoneCooldown);
        this.scenarioIntruderPercepts = new ScenarioIntruderPercepts(this.scenarioPercepts, winConditionIntruderRounds, new Distance(maxMoveDistanceIntruder), new Distance(maxSprintDistanceIntruder), sprintCooldown);
        this.scenarioGuardPercepts = new ScenarioGuardPercepts(this.scenarioPercepts, new Distance(maxMoveDistanceGuard));

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
                boolean spawned = false;
                Ellipse2D intruderLocation = null;
                while(!spawned) {
                    spawned = true;
                    double x = spawnAreaIntruders.get(0).getX() + spawnAreaIntruders.get(0).getWidth() * random.nextDouble();
                    double y = spawnAreaIntruders.get(0).getY() + spawnAreaIntruders.get(0).getHeight() * random.nextDouble();
                    intruderLocation = new Ellipse2D.Double(x, y, 1, 1);
                    if(!intruderLocations.isEmpty()) {
                        ArrayList<Intruder> initialisedIntruders = new ArrayList<>(intruderLocations.keySet());
                        for (Intruder otherIntruder : initialisedIntruders) {
                            double diffX = Math.abs(x - intruderLocations.get(otherIntruder).getX());
                            double diffY = Math.abs(y - intruderLocations.get(otherIntruder).getY());
                            double distance = Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));
                            if (distance < 1) {
                                if (DEBUG_TEXT) {
                                    System.out.println("Tried to spawn on top of other agent");
                                }
                                spawned = false;
                            }
                        }
                    }
                }
                intruderLocations.put(intruder, intruderLocation);
                if(DEBUG_TEXT){System.out.println("Intruder Spawned");}
                double directionAngle = (2*Math.PI) * random.nextDouble();
                Direction direction = Direction.fromRadians(directionAngle);
                intruderDirections.put(intruder,direction);

                intruderTeleportFlag.put(intruder, false);

                intruderSprintCooldowns.put(intruder,0);
                intruderPheromoneCooldowns.put(intruder,0);
                setAreaPerceptsIntruder(intruder);
                setIntruderSoundPercepts(intruder);
                setIntruderSmellPercepts(intruder);
                setIntruderObjectPercept(intruder);
            }
        }

        //Guard Agent Contructor
        if(numGuards>0) {
            guards = AgentsFactory.createGuards(numGuards);
            for (Guard guard : guards) {
                boolean spawned = false;
                Ellipse2D guardLocation = null;
                while(!spawned) {
                    spawned = true;
                    double x = spawnAreaGuards.get(0).getX() + spawnAreaGuards.get(0).getWidth() * random.nextDouble();
                    double y = spawnAreaGuards.get(0).getY() + spawnAreaGuards.get(0).getHeight() * random.nextDouble();
                    guardLocation = new Ellipse2D.Double(x, y, 1, 1);
                    if (!guardLocations.isEmpty()) {
                        ArrayList<Guard> initialisedGuards = new ArrayList<>(guardLocations.keySet());
                        for (Guard otherGuard : initialisedGuards) {
                            double diffX = Math.abs(x - guardLocations.get(otherGuard).getX());
                            double diffY = Math.abs(y - guardLocations.get(otherGuard).getY());
                            double distance = Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));
                            if (distance < 1) {
                                if (DEBUG_TEXT) {
                                    System.out.println("Tried to spawn on top of other agent");
                                }
                                spawned = false;
                            }
                        }
                    }
                }
                guardLocations.put(guard, guardLocation);

                double directionAngle = (2*Math.PI) * random.nextDouble();
                Direction direction = Direction.fromRadians(directionAngle);
                guardDirections.put(guard,direction);

                guardPheromoneCooldowns.put(guard,0);

                guardTeleportFlag.put(guard, false);

                setAreaPerceptsGuard(guard);
                setGuardSoundPercepts(guard);
                setGuardSmellPercepts(guard);
                setGuardObjectPercepts(guard);
            }
        }

        boolean intruderStart = random.nextBoolean();
        if(intruderStart){
            turn = Turn.IntruderTurn;
        }
    }

    boolean gameOver = false;
    private void runGame(){
        while(!gameOver) {
            playSingleTurn();
        }
    }

    public void playSingleTurn() {
        smellDecay();
        soundDecay();
        switch (turn) {
            case GuardTurn:
                if(!guards.isEmpty()) {
                    for (Guard guard : guards) {
                        guardPheromoneCooldownDecay(guard);
                        GuardPercepts percept = guardPercept(guard);
                        Action action = guard.getAction(percept);
                        guardAct(action, percept, guard);
                    }
                }
                break;
            case IntruderTurn:
                if(!intruders.isEmpty()) {
                    for (Intruder intruder : intruders) {
                        //if (DEBUG_TEXT) System.out.println("Intruder Location: x "+intruderLocations.get(intruder).getX()+" y "+intruderLocations.get(intruder).getY());
                        sprintCooldownDecay(intruder);
                        intruderPheromoneCooldownDecay(intruder);
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
    private GuardPercepts guardPercept(Guard guard){

        FieldOfView field = new FieldOfView(guardViewRange.get(guard), Angle.fromDegrees(viewAngle));
        VisionPrecepts vision = new VisionPrecepts(field, guardObjectPercepts.get(guard));
        SoundPercepts sounds = guardSoundPercepts.get(guard);
        SmellPercepts smells = guardSmellPercepts.get(guard);
        AreaPercepts areaPercepts = guardsAreaPercepts.get(guard);


        return new GuardPercepts(vision, sounds, smells, areaPercepts, scenarioGuardPercepts, wasLastActionExecuted);
    }

    private IntruderPercepts intruderPercept(Intruder intruder){

        //Here I am considering that we only have one target area
        double centerDistance = new Distance(new Point(targetArea.get(0).getCenterX(), targetArea.get(0).getCenterY()), new Point(intruderLocations.get(intruder).getCenterX(), intruderLocations.get(intruder).getCenterY())).getValue();
        double deltaX = Math.abs(intruderLocations.get(intruder).getCenterX() - targetArea.get(0).getCenterX());
        Direction targetDirection = Direction.fromRadians(Math.acos(deltaX / centerDistance));
        FieldOfView field = new FieldOfView(intruderViewRange.get(intruder), Angle.fromDegrees(viewAngle));
        VisionPrecepts vision = new VisionPrecepts(field, intruderObjectPercept.get(intruder));
        SoundPercepts sounds = intruderSoundPercepts.get(intruder);
        SmellPercepts smells = intruderSmellPercepts.get(intruder);
        AreaPercepts areaPercepts = intruderAreaPercepts.get(intruder);

        return new IntruderPercepts(targetDirection, vision, sounds, smells, areaPercepts, this.scenarioIntruderPercepts, wasLastActionExecuted);
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
            if(checkIfLegalMove(guardLocation,newGuardLocation,COLLISION_CHECK_STEP_SIZE)) {
                //Teleporting functionality and limitation
                for(Teleport teleport:teleports) {
                    //Leaving a teleporter area, remove flag
                    if(teleport.getArea().contains(guardLocation.getX(),guardLocation.getY()) && !teleport.getArea().contains(newX, newY)){
                        guardTeleportFlag.replace(guard,false);
                    }else if(teleport.getArea().contains(newX, newY) && !guardTeleportFlag.get(guard)) { //Entering a teleporter without flag, teleport
                        newX = teleport.getGoal().getX();
                        newY = teleport.getGoal().getY();
                        guardTeleportFlag.replace(guard,true);
                    }
                }
                guardLocations.put(guard, newGuardLocation);
                soundLocations.put(stepSound,stepSoundLocation);
                wasLastActionExecuted = true;
            }
            else wasLastActionExecuted = false;


        }else if(action instanceof Rotate){
            Direction guardDirection = guardDirections.get(guard);
            if(((Rotate) action).getAngle().getDegrees()<=percept.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees()) {
                double newGuardAngle = (guardDirection.getDegrees() + ((Rotate) action).getAngle().getDegrees()) % 360;
                guardDirections.put(guard, Direction.fromDegrees(newGuardAngle));
                wasLastActionExecuted = true;
            }

        }else if(action instanceof Yell){
            Sound yell = new Sound(SoundPerceptType.Yell,new Distance(yellSoundRadius));
            Ellipse2D yellLocation = new Ellipse2D.Double(guardLocations.get(guard).getX(),guardLocations.get(guard).getY(),
                    yellSoundRadius*2.0,yellSoundRadius*2.0);
            soundLocations.put(yell,yellLocation);
            wasLastActionExecuted = true;

        }else if(action instanceof DropPheromone){
            if(guardPheromoneCooldowns.get(guard)==0) {
                Smell smell = new Smell(((DropPheromone) action).getType(), new Distance(radiusPheromone));
                Ellipse2D smellLocation = new Ellipse2D.Double(guardLocations.get(guard).getX(), guardLocations.get(guard).getY(),
                        smell.getRadius().getValue() * 2.0, smell.getRadius().getValue() * 2.0); //*2 = Radius -> Diameter
                guardSmellLocations.put(smell, smellLocation);
                guardPheromoneCooldowns.replace(guard, pheromoneCooldown);
                wasLastActionExecuted = true;
            }else{
                System.out.println("Still on cooldown, turn skipped");
                wasLastActionExecuted = false;
            }
        }else if(action instanceof NoAction){
            wasLastActionExecuted = true;
        }else{
            wasLastActionExecuted = false;
            System.out.println("No action picked...");
        }

        setAreaPerceptsGuard(guard);
        setGuardSoundPercepts(guard);
        setGuardSmellPercepts(guard);
        setGuardObjectPercepts(guard);
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
            if(checkIfLegalMove(intruderLocation,newintruderLocation, COLLISION_CHECK_STEP_SIZE)) {
                //Teleporting functionality and limitation
                for(Teleport teleport:teleports) {
                    //Leaving a teleporter area, remove flag
                    if(teleport.getArea().contains(intruderLocation.getX(),intruderLocation.getY()) && !teleport.getArea().contains(newX, newY)){
                        intruderTeleportFlag.replace(intruder,false);
                    }else if(teleport.getArea().contains(newX, newY) && !intruderTeleportFlag.get(intruder)) { //Entering a teleporter without flag, teleport
                        newX = teleport.getGoal().getX();
                        newY = teleport.getGoal().getY();
                        intruderTeleportFlag.replace(intruder,true);
                    }
                }
                intruderLocations.put(intruder, newintruderLocation);
                soundLocations.put(stepSound,stepSoundLocation);
                wasLastActionExecuted = true;
            }
            else wasLastActionExecuted = false;

        }else if(action instanceof Rotate){
            Direction intruderDirection = intruderDirections.get(intruder);
            double newintruderAngle = (intruderDirection.getDegrees() + ((Rotate) action).getAngle().getDegrees())%360;
            intruderDirections.put(intruder,Direction.fromDegrees(newintruderAngle));
            wasLastActionExecuted = true;

        }else if(action instanceof Sprint){
            if(intruderSprintCooldowns.get(intruder)==0) {
                Ellipse2D intruderLocation = intruderLocations.get(intruder);
                Direction intruderDirection = intruderDirections.get(intruder);
                Sound stepSound = new Sound(SoundPerceptType.Noise, new Distance(maxMoveSoundRadius));
                Ellipse2D stepSoundLocation = (Ellipse2D) intruderLocations.get(intruder).clone();
                double newX = intruderLocation.getX() + Math.cos(intruderDirection.getDegrees()) * ((Move) action).getDistance().getValue();
                double newY = intruderLocation.getY() + Math.sin(intruderDirection.getDegrees()) * ((Move) action).getDistance().getValue();
                Ellipse2D newintruderLocation = new Ellipse2D.Double(newX, newY, intruderLocation.getWidth(), intruderLocation.getHeight());

                if (checkIfLegalMove(intruderLocation, newintruderLocation, COLLISION_CHECK_STEP_SIZE)) {
                    //Teleporting functionality and limitation
                    for(Teleport teleport:teleports) {
                        //Leaving a teleporter area, remove flag
                        if(teleport.getArea().contains(intruderLocation.getX(),intruderLocation.getY()) && !teleport.getArea().contains(newX, newY)){
                            intruderTeleportFlag.replace(intruder,false);
                        }else if(teleport.getArea().contains(newX, newY) && !intruderTeleportFlag.get(intruder)) { //Entering a teleporter without flag, teleport
                            newX = teleport.getGoal().getX();
                            newY = teleport.getGoal().getY();
                            intruderTeleportFlag.replace(intruder,true);
                        }
                    }

                    intruderLocations.put(intruder, newintruderLocation);
                    soundLocations.put(stepSound, stepSoundLocation);
                    intruderSprintCooldowns.replace(intruder,sprintCooldown);
                    wasLastActionExecuted = true;

                }else{
                    wasLastActionExecuted = false;
                    System.out.println("Illegal move attempted");
                }
            }else{
                wasLastActionExecuted = false;
                System.out.println("Still on cooldown, turn skipped");
            }

        }else if(action instanceof DropPheromone){
            if(intruderPheromoneCooldowns.get(intruder)==0) {
                Smell smell = new Smell(((DropPheromone) action).getType(), new Distance(radiusPheromone));
                Ellipse2D smellLocation = new Ellipse2D.Double(intruderLocations.get(intruder).getX(), intruderLocations.get(intruder).getY(),
                        smell.getRadius().getValue() * 2.0, smell.getRadius().getValue() * 2.0);
                intruderSmellLocations.put(smell, smellLocation);
                wasLastActionExecuted = true;
            }else{
                System.out.println("Still on cooldown, turn skipped");
                wasLastActionExecuted = false;
            }

        }else if(action instanceof NoAction){
            wasLastActionExecuted = true;
        }else{
            System.out.println("No action picked...");
            wasLastActionExecuted = false;
        }

        setAreaPerceptsIntruder(intruder);
        setIntruderSoundPercepts(intruder);
        setIntruderSmellPercepts(intruder);
        setIntruderObjectPercept(intruder);
    }

    private boolean checkIfLegalMove(Ellipse2D initialLocation,
                                     Ellipse2D newLocation, double stepSize) {
        // Check for collisions. If collision occurs, return false and skip turn

        if((newLocation.getX()-newLocation.getWidth()/2)<0||(newLocation.getX()+newLocation.getWidth()/2)>width||(newLocation.getY()-newLocation.getHeight()/2)<0||(newLocation.getY()+newLocation.getHeight()/2)>height)
            return false;

        double startX = initialLocation.getX();
        double startY = initialLocation.getY();
        double goalX = newLocation.getX();
        double goalY = newLocation.getY();
        //boolean legal = true;
        List<Point2D> points = new ArrayList<Point2D>();
        List<Ellipse2D> temp_agents = new ArrayList<Ellipse2D>();
        Line2DExtended line = new Line2DExtended(startX, startY, goalX, goalY);
        Point2D current;
        Ellipse2D temp_agent = initialLocation;
        int counter = 0;
        while (temp_agent.getX()<newLocation.getX()&&temp_agent.getY()<newLocation.getY()) {
            ++counter;
            current = line.evalAtX(initialLocation.getX()+stepSize*counter);
            if(current.getX()<newLocation.getX()&&current.getY()<newLocation.getY()) {
                points.add(current);
                temp_agent = new Ellipse2D.Double(current.getX(), current.getY(),
                        1.0, 1.0);
                temp_agents.add(temp_agent);
            }
            else break;
        }
        temp_agents.add(newLocation);
        for (int i = 0; i < temp_agents.size(); i++) {
            for(Intruder intruder:intruders){
                if(!intruderLocations.get(intruder).equals(initialLocation)) {
                    double diffX = Math.abs(temp_agents.get(i).getX() - intruderLocations.get(intruder).getX());
                    double diffY = Math.abs(temp_agents.get(i).getY() - intruderLocations.get(intruder).getY());
                    double distance = Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));
                    if (distance < 1) {
                        if (DEBUG_TEXT) {
                            System.out.println("Intruder Collision with other agent");
                        }
                        return false;
                    }
                }
            }
            for (int j = 0; j < walls.size(); j++) {
                if (temp_agents.get(i).intersects(walls.get(j))) {
                    if(DEBUG_TEXT){
                        System.out.println("Intruder Collision with wall");}
                    return false;
                }
            }
        }

        // If at some point during checking collision is detected, change legal
        // to false

        return true;
    }

    //Smell radius decreases in size each turn
    private void smellDecay(){
        if (!intruderSmellLocations.isEmpty()) {
            ArrayList<Smell> remove = new ArrayList<>();
            for (Smell smell : intruderSmellLocations.keySet()) {
                Distance previousSmellRadius = smell.getRadius();
                Distance newSmellRadius = new Distance(previousSmellRadius.getValue() - (radiusPheromone / pheromoneExpireRounds));
                if (newSmellRadius.getValue() > 0) {
                    Smell updatedSmell = new Smell(smell.getType(), newSmellRadius);
                    Ellipse2D location = intruderSmellLocations.get(smell);
                    Ellipse2D updatedLocation = new Ellipse2D.Double(location.getX(), location.getY(), newSmellRadius.getValue(), newSmellRadius.getValue());
                    remove.add(smell);
                    intruderSmellLocations.put(updatedSmell, updatedLocation);
                } else {
                    remove.add(smell);
                }
            }
            intruderSmellLocations.remove(remove);
        }
    }

    private void soundDecay(){
        if(!soundLocations.isEmpty()){
            ArrayList<Sound> remove = new ArrayList<>();
            for (Sound sound:soundLocations.keySet()){
                sound.decaySound();
                if (sound.getDuration()<=0){
                    remove.add(sound);
                }
            }
            soundLocations.remove(remove);
        }
    }

    private void sprintCooldownDecay(Intruder intruder){
        int old = intruderSprintCooldowns.get(intruder);
        if(old>0) {
            intruderSprintCooldowns.replace(intruder, old--);
        }
    }

    private void intruderPheromoneCooldownDecay(Intruder intruder) {
        int old = intruderPheromoneCooldowns.get(intruder);
        if (old > 0) {
            intruderPheromoneCooldowns.replace(intruder, old--);
        }
    }

    private void guardPheromoneCooldownDecay(Guard guard) {
        int old = guardPheromoneCooldowns.get(guard);
        if (old > 0) {
            guardPheromoneCooldowns.replace(guard, old--);
        }
    }

    private boolean checkVictory(){
        //Check if game is over and victory is achieved
        boolean gameOver = false;

        //If victory condition is met, set gameOver to true
        return gameOver;
    }

    private void setAreaPerceptsIntruder(Intruder intruder) {
        Iterator it = (new HashMap<>(intruderLocations)).entrySet().iterator();
        while (it.hasNext()) {
            boolean inWindow = false;
            boolean inDoor = false;
            boolean inSentryTower = false;
            boolean justTeleported = false;
            Map.Entry pair = (Map.Entry)it.next();
            Ellipse2D location = (Ellipse2D) pair.getValue();
            for(Rectangle window: windows) {
                if(location.intersects(window)) {
                    inWindow = true;
                    intruderMaxMoveDistance.put(intruder, defaultMaxMoveDistanceIntruder*slowDownModifierWindow);
                    intruderMaxSprintDistance.put(intruder, defaultMaxSprintDistanceIntruder*slowDownModifierWindow);
                    intruderViewRange.put(intruder, new Distance(viewRangeIntruderNormal));
                }
            }
            for(Rectangle door: doors) {
                if(location.intersects(door)) {
                    inDoor = true;
                    intruderMaxMoveDistance.put(intruder, defaultMaxMoveDistanceIntruder*slowDownModifierDoor);
                    intruderMaxSprintDistance.put(intruder, defaultMaxSprintDistanceIntruder*slowDownModifierDoor);
                    intruderViewRange.put(intruder, new Distance(viewRangeIntruderShaded));
                }
            }
            for(Rectangle sentry: sentries) {
                if(location.intersects(sentry)) {
                    inSentryTower = true;
                    intruderMaxMoveDistance.put(intruder, defaultMaxMoveDistanceIntruder*slowDownModifierSentryTower);
                    intruderMaxSprintDistance.put(intruder, defaultMaxSprintDistanceIntruder*slowDownModifierSentryTower);
                    intruderViewRange.put(intruder, new Distance(viewRangeSentry[1]));
                }
            }

            if(!inWindow && !inDoor && !inSentryTower) {
                intruderMaxMoveDistance.put(intruder, defaultMaxMoveDistanceIntruder);
                intruderMaxSprintDistance.put(intruder, defaultMaxSprintDistanceIntruder);
                intruderViewRange.put(intruder, new Distance(viewRangeIntruderNormal));
            }

            if(intruderTeleportFlag.get(intruder)) justTeleported = true;

            this.intruderAreaPercepts.put(intruder, new AreaPercepts(inWindow, inDoor, inSentryTower, justTeleported));
            it.remove();
        }
    }

    private void setAreaPerceptsGuard(Guard guard) {
        Iterator it = (new HashMap<>(guardLocations)).entrySet().iterator();
        while (it.hasNext()) {
            boolean inWindow = false;
            boolean inDoor = false;
            boolean inSentryTower = false;
            boolean justTeleported = false;
            Map.Entry pair = (Map.Entry)it.next();
            Ellipse2D location = (Ellipse2D) pair.getValue();
            for(Rectangle window: windows) {
                if(location.intersects(window)) {
                    inWindow = true;
                    guardMaxMoveDistance.put(guard, defaultMaxMoveDistanceGuard*slowDownModifierWindow);
                    guardViewRange.put(guard, new Distance(viewRangeGuardNormal));

                }
            }
            for(Rectangle door: doors) {
                if(location.intersects(door)) {
                    inDoor = true;
                    guardMaxMoveDistance.put(guard, defaultMaxMoveDistanceGuard*slowDownModifierDoor);
                    guardViewRange.put(guard, new Distance(viewRangeGuardShaded));

                }
            }
            for(Rectangle sentry: sentries) {
                if(location.intersects(sentry)) {
                    inSentryTower = true;
                    guardMaxMoveDistance.put(guard, defaultMaxMoveDistanceGuard*slowDownModifierSentryTower);
                    guardViewRange.put(guard, new Distance(viewRangeSentry[1]));

                }
            }

            if(!inWindow && !inDoor && !inSentryTower) {
                guardMaxMoveDistance.put(guard, defaultMaxMoveDistanceGuard);
                guardViewRange.put(guard, new Distance(viewRangeGuardNormal));
            }

            if(guardTeleportFlag.get(guard)) justTeleported = true;

            this.guardsAreaPercepts.put(guard, new AreaPercepts(inWindow, inDoor, inSentryTower, justTeleported));
            it.remove();
        }
    }


    private void setIntruderSoundPercepts(Intruder intruder) {

        Ellipse2D intruderLoc = intruderLocations.get(intruder);
        HashSet<SoundPercept> soundPerceptsSet = new HashSet<>();
        Iterator it = (new HashMap<>(soundLocations)).entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            Ellipse2D soundLoc = (Ellipse2D) pair.getValue();
            double centerDistance = new Distance(new Point(soundLoc.getCenterX(), soundLoc.getCenterY()), new Point(intruderLoc.getCenterX(), intruderLoc.getCenterY())).getValue();
            double radiusSum = soundLoc.getWidth() + intruderLoc.getWidth();
            if(centerDistance != 0) {
                if (centerDistance <= radiusSum) {
                    Sound sound = (Sound) pair.getKey();
                    double deltaX = Math.abs(intruderLoc.getCenterX() - soundLoc.getCenterX());
                    Direction soundDirection = Direction.fromRadians(Math.acos(deltaX / centerDistance));
                    Angle perceivedAngle = soundDirection.getDistance(intruderDirections.get(intruder));
                    Direction perceivedDirection = Direction.fromRadians(perceivedAngle.getRadians());
                    soundPerceptsSet.add(new SoundPercept(sound.getType(), perceivedDirection));
                }
            }
            it.remove();
        }
        SoundPercepts soundPercepts = new SoundPercepts(soundPerceptsSet);
        this.intruderSoundPercepts.put(intruder, soundPercepts);
    }



    private void setGuardSoundPercepts(Guard guard) {

        Ellipse2D guardLoc = guardLocations.get(guard);
        HashSet<SoundPercept> soundPerceptsSet = new HashSet<>();
        Iterator it = (new HashMap<>(soundLocations)).entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            Ellipse2D soundLoc = (Ellipse2D) pair.getValue();
            double centerDistance = new Distance(new Point(soundLoc.getCenterX(), soundLoc.getCenterY()), new Point(guardLoc.getCenterX(), guardLoc.getCenterY())).getValue();
            double radiusSum = soundLoc.getWidth() + guardLoc.getWidth();
            if(centerDistance != 0) {
                if (centerDistance <= radiusSum) {
                    Sound sound = (Sound) pair.getKey();
                    double deltaX = Math.abs(guardLoc.getCenterX() - soundLoc.getCenterX());
                    Direction soundDirection = Direction.fromRadians(Math.acos(deltaX / centerDistance));
                    Angle perceivedAngle = soundDirection.getDistance(guardDirections.get(guard));
                    Direction perceivedDirection = Direction.fromRadians(perceivedAngle.getRadians());
                    soundPerceptsSet.add(new SoundPercept(sound.getType(), perceivedDirection));
                }
            }
            it.remove();
        }

        SoundPercepts soundPercepts = new SoundPercepts(soundPerceptsSet);
        this.guardSoundPercepts.put(guard, soundPercepts);

    }

    private void setIntruderSmellPercepts(Intruder intruder) {

        Ellipse2D intruderLoc = intruderLocations.get(intruder);
        HashSet<SmellPercept> smellPerceptsSet = new HashSet<>();
        Iterator it = (new HashMap<>(intruderSmellLocations)).entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            Ellipse2D smellLoc = (Ellipse2D) pair.getValue();
            double centerDistance = new Distance(new Point(smellLoc.getCenterX(), smellLoc.getCenterY()), new Point(intruderLoc.getCenterX(), intruderLoc.getCenterY())).getValue();
            double radiusSum = smellLoc.getWidth() + intruderLoc.getWidth();
            if (centerDistance <= radiusSum) {
                Smell smell = (Smell) pair.getKey();
                smellPerceptsSet.add(new SmellPercept(smell.getType(), new Distance(centerDistance)));
            }
            it.remove();
        }
        SmellPercepts smellPercepts = new SmellPercepts(smellPerceptsSet);
        this.intruderSmellPercepts.put(intruder, smellPercepts);
    }


    private void setGuardSmellPercepts(Guard guard) {

        Ellipse2D guardLoc = guardLocations.get(guard);
        HashSet<SmellPercept> smellPerceptsSet = new HashSet<>();
        Iterator it = (new HashMap<>(guardSmellLocations)).entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            Ellipse2D smellLoc = (Ellipse2D) pair.getValue();
            double centerDistance = new Distance(new Point(smellLoc.getCenterX(), smellLoc.getCenterY()), new Point(guardLoc.getCenterX(), guardLoc.getCenterY())).getValue();
            double radiusSum = smellLoc.getWidth() + guardLoc.getWidth();
            if (centerDistance <= radiusSum) {
                Smell smell = (Smell) pair.getKey();
                smellPerceptsSet.add(new SmellPercept(smell.getType(), new Distance(centerDistance)));
            }
            it.remove();
        }
        SmellPercepts smellPercepts = new SmellPercepts(smellPerceptsSet);
        this.guardSmellPercepts.put(guard, smellPercepts);
    }





    private void setIntruderObjectPercept(Intruder intruder) {

        //LinkedList<Line2D.Double> rays = new LinkedList<>();
        HashSet<ObjectPercept> objectPerceptsSet = new HashSet<>();
        double maxDist = intruderViewRange.get(intruder).getValue() - 0.000000000000001;
        double x = intruderLocations.get(intruder).getX();
        double y = intruderLocations.get(intruder).getY();
        double notInViewDistance = 0;
        if(intruderAreaPercepts.get(intruder).isInSentryTower()) notInViewDistance = viewRangeSentry[0];

        //int startOfAngle = Math.round((float) (intruderDirections.get(intruder).getDegrees() - viewAngle/2));
        //int endOfAngle = Math.round((float) (intruderDirections.get(intruder).getDegrees() + viewAngle/2));

        for (int i = 0; i < viewAngle; i++) {
            double dir = intruderDirections.get(intruder).getRadians() - Math.toRadians(viewAngle/2) + i*Math.PI/180;
            if(i == 0) dir += 0.000000000000001;
            double minDist = maxDist;
            ObjectPerceptType object = ObjectPerceptType.EmptySpace;

            for (Rectangle wall : walls) {
                Line2D.Double line1 = new Line2D.Double(wall.x, wall.y, wall.x + wall.width, wall.y );
                Line2D.Double line2 = new Line2D.Double(wall.x + wall.width, wall.y, wall.x + wall.width, wall.y + wall.height);
                Line2D.Double line3 = new Line2D.Double(wall.x, wall.y + wall.height, wall.x + wall.width, wall.y + wall.height);
                Line2D.Double line4 = new Line2D.Double(wall.x, wall.y, wall.x, wall.y + wall.height);
                double dist1 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line1.x1, line1.y1, line1.x2, line1.y2);
                double dist2 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line2.x1, line2.y1, line2.x2, line2.y2);
                double dist3 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line3.x1, line3.y1, line3.x2, line3.y2);
                double dist4 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line4.x1, line4.y1, line4.x2, line4.y2);
                double dist = Math.min(Math.min(dist1, dist2), Math.min(dist3, dist4));
                if (dist < minDist && dist > notInViewDistance) {
                    minDist = dist;
                    object = ObjectPerceptType.Wall;
                }
            }

            for (Rectangle window : windows) {
                Line2D.Double line1 = new Line2D.Double(window.x, window.y, window.x + window.width, window.y );
                Line2D.Double line2 = new Line2D.Double(window.x + window.width, window.y, window.x + window.width, window.y + window.height);
                Line2D.Double line3 = new Line2D.Double(window.x, window.y + window.height, window.x + window.width, window.y + window.height);
                Line2D.Double line4 = new Line2D.Double(window.x, window.y, window.x, window.y + window.height);
                double dist1 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line1.x1, line1.y1, line1.x2, line1.y2);
                double dist2 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line2.x1, line2.y1, line2.x2, line2.y2);
                double dist3 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line3.x1, line3.y1, line3.x2, line3.y2);
                double dist4 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line4.x1, line4.y1, line4.x2, line4.y2);
                double dist = Math.min(Math.min(dist1, dist2), Math.min(dist3, dist4));
                if (dist < minDist && dist > notInViewDistance) {
                    minDist = dist;
                    object = ObjectPerceptType.Window;
                }
            }

            for (Rectangle door : doors) {
                Line2D.Double line1 = new Line2D.Double(door.x, door.y, door.x + door.width, door.y );
                Line2D.Double line2 = new Line2D.Double(door.x + door.width, door.y, door.x + door.width, door.y + door.height);
                Line2D.Double line3 = new Line2D.Double(door.x, door.y + door.height, door.x + door.width, door.y + door.height);
                Line2D.Double line4 = new Line2D.Double(door.x, door.y, door.x, door.y + door.height);
                double dist1 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line1.x1, line1.y1, line1.x2, line1.y2);
                double dist2 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line2.x1, line2.y1, line2.x2, line2.y2);
                double dist3 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line3.x1, line3.y1, line3.x2, line3.y2);
                double dist4 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line4.x1, line4.y1, line4.x2, line4.y2);
                double dist = Math.min(Math.min(dist1, dist2), Math.min(dist3, dist4));
                if (dist < minDist && dist > notInViewDistance) {
                    minDist = dist;
                    object = ObjectPerceptType.Door;
                }
            }

            for (Rectangle sentry : sentries) {
                Line2D.Double line1 = new Line2D.Double(sentry.x, sentry.y, sentry.x + sentry.width, sentry.y );
                Line2D.Double line2 = new Line2D.Double(sentry.x + sentry.width, sentry.y, sentry.x + sentry.width, sentry.y + sentry.height);
                Line2D.Double line3 = new Line2D.Double(sentry.x, sentry.y + sentry.height, sentry.x + sentry.width, sentry.y + sentry.height);
                Line2D.Double line4 = new Line2D.Double(sentry.x, sentry.y, sentry.x, sentry.y + sentry.height);
                double dist1 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line1.x1, line1.y1, line1.x2, line1.y2);
                double dist2 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line2.x1, line2.y1, line2.x2, line2.y2);
                double dist3 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line3.x1, line3.y1, line3.x2, line3.y2);
                double dist4 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line4.x1, line4.y1, line4.x2, line4.y2);
                double dist = Math.min(Math.min(dist1, dist2), Math.min(dist3, dist4));
                if (dist < minDist && dist > notInViewDistance) {
                    minDist = dist;
                    object = ObjectPerceptType.SentryTower;
                }
            }

            for (Rectangle shade : shaded) {
                Line2D.Double line1 = new Line2D.Double(shade.x, shade.y, shade.x + shade.width, shade.y );
                Line2D.Double line2 = new Line2D.Double(shade.x + shade.width, shade.y, shade.x + shade.width, shade.y + shade.height);
                Line2D.Double line3 = new Line2D.Double(shade.x, shade.y + shade.height, shade.x + shade.width, shade.y + shade.height);
                Line2D.Double line4 = new Line2D.Double(shade.x, shade.y, shade.x, shade.y + shade.height);
                double dist1 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line1.x1, line1.y1, line1.x2, line1.y2);
                double dist2 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line2.x1, line2.y1, line2.x2, line2.y2);
                double dist3 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line3.x1, line3.y1, line3.x2, line3.y2);
                double dist4 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line4.x1, line4.y1, line4.x2, line4.y2);
                double dist = Math.min(Math.min(dist1, dist2), Math.min(dist3, dist4));
                if (dist < minDist && dist > notInViewDistance) {
                    minDist = dist;
                    object = ObjectPerceptType.ShadedArea;
                }
            }


            for (Rectangle target : targetArea) {
                Line2D.Double line1 = new Line2D.Double(target.x, target.y, target.x + target.width, target.y );
                Line2D.Double line2 = new Line2D.Double(target.x + target.width, target.y, target.x + target.width, target.y + target.height);
                Line2D.Double line3 = new Line2D.Double(target.x, target.y + target.height, target.x + target.width, target.y + target.height);
                Line2D.Double line4 = new Line2D.Double(target.x, target.y, target.x, target.y + target.height);
                double dist1 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line1.x1, line1.y1, line1.x2, line1.y2);
                double dist2 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line2.x1, line2.y1, line2.x2, line2.y2);
                double dist3 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line3.x1, line3.y1, line3.x2, line3.y2);
                double dist4 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line4.x1, line4.y1, line4.x2, line4.y2);
                double dist = Math.min(Math.min(dist1, dist2), Math.min(dist3, dist4));
                if (dist < minDist && dist > notInViewDistance) {
                    minDist = dist;
                    object = ObjectPerceptType.TargetArea;
                }
            }

            for(int j=0; j<teleports.size(); j++) {
                Rectangle teleport = teleports.get(j).getArea();
                Line2D.Double line1 = new Line2D.Double(teleport.x, teleport.y, teleport.x + teleport.width, teleport.y );
                Line2D.Double line2 = new Line2D.Double(teleport.x + teleport.width, teleport.y, teleport.x + teleport.width, teleport.y + teleport.height);
                Line2D.Double line3 = new Line2D.Double(teleport.x, teleport.y + teleport.height, teleport.x + teleport.width, teleport.y + teleport.height);
                Line2D.Double line4 = new Line2D.Double(teleport.x, teleport.y, teleport.x, teleport.y + teleport.height);
                double dist1 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line1.x1, line1.y1, line1.x2, line1.y2);
                double dist2 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line2.x1, line2.y1, line2.x2, line2.y2);
                double dist3 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line3.x1, line3.y1, line3.x2, line3.y2);
                double dist4 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line4.x1, line4.y1, line4.x2, line4.y2);
                double dist = Math.min(Math.min(dist1, dist2), Math.min(dist3, dist4));
                if (dist < minDist && dist > notInViewDistance) {
                    minDist = dist;
                    object = ObjectPerceptType.Teleport;
                }
            }



            Iterator intruderLocIterator = (new HashMap<>(intruderLocations)).entrySet().iterator();
            while (intruderLocIterator.hasNext()) {
                Map.Entry pair = (Map.Entry) intruderLocIterator.next();
                Intruder currentIntruder = (Intruder) pair.getKey();
                if(currentIntruder != intruder) {
                    Ellipse2D otherIntruderEllipse = (Ellipse2D) pair.getValue();
                    Rectangle otherIntruder = otherIntruderEllipse.getBounds();
                    Line2D.Double line1 = new Line2D.Double(otherIntruder.x, otherIntruder.y, otherIntruder.x + otherIntruder.width, otherIntruder.y);
                    Line2D.Double line2 = new Line2D.Double(otherIntruder.x + otherIntruder.width, otherIntruder.y, otherIntruder.x + otherIntruder.width, otherIntruder.y + otherIntruder.height);
                    Line2D.Double line3 = new Line2D.Double(otherIntruder.x, otherIntruder.y + otherIntruder.height, otherIntruder.x + otherIntruder.width, otherIntruder.y + otherIntruder.height);
                    Line2D.Double line4 = new Line2D.Double(otherIntruder.x, otherIntruder.y, otherIntruder.x, otherIntruder.y + otherIntruder.height);
                    double dist1 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line1.x1, line1.y1, line1.x2, line1.y2);
                    double dist2 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line2.x1, line2.y1, line2.x2, line2.y2);
                    double dist3 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line3.x1, line3.y1, line3.x2, line3.y2);
                    double dist4 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line4.x1, line4.y1, line4.x2, line4.y2);
                    double dist = Math.min(Math.min(dist1, dist2), Math.min(dist3, dist4));
                    if (dist < minDist && dist > notInViewDistance) {
                        minDist = dist;
                        object = ObjectPerceptType.Intruder;
                    }
                }
                intruderLocIterator.remove();
            }

            Iterator guardLocIterator = (new HashMap<>(guardLocations)).entrySet().iterator();
            while (guardLocIterator.hasNext()) {
                Map.Entry pair = (Map.Entry) guardLocIterator.next();
                Ellipse2D guardEllipse = (Ellipse2D) pair.getValue();
                Rectangle guard = guardEllipse.getBounds();
                Line2D.Double line1 = new Line2D.Double(guard.x, guard.y, guard.x + guard.width, guard.y );
                Line2D.Double line2 = new Line2D.Double(guard.x + guard.width, guard.y, guard.x + guard.width, guard.y + guard.height);
                Line2D.Double line3 = new Line2D.Double(guard.x, guard.y + guard.height, guard.x + guard.width, guard.y + guard.height);
                Line2D.Double line4 = new Line2D.Double(guard.x, guard.y, guard.x, guard.y + guard.height);
                double dist1 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line1.x1, line1.y1, line1.x2, line1.y2);
                double dist2 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line2.x1, line2.y1, line2.x2, line2.y2);
                double dist3 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line3.x1, line3.y1, line3.x2, line3.y2);
                double dist4 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line4.x1, line4.y1, line4.x2, line4.y2);
                double dist = Math.min(Math.min(dist1, dist2), Math.min(dist3, dist4));
                if (dist < minDist && dist > notInViewDistance) {
                    minDist = dist;
                    object = ObjectPerceptType.Guard;
                }
                guardLocIterator.remove();
            }

            //Adding the difference between the agent's direction and the y-axis to match the agent coordinate system
            double setToYAxisAngle = Utils.clockAngle(Math.cos(intruderDirections.get(intruder).getRadians()), Math.sin(intruderDirections.get(intruder).getRadians()));
            Point intersectionPoint = new Point(Math.cos(dir + setToYAxisAngle) * minDist, Math.sin(dir +setToYAxisAngle) * minDist);
            objectPerceptsSet.add(new ObjectPercept(object, intersectionPoint));



            /*
            FieldOfView field = new FieldOfView(intruderViewRange.get(intruder), Angle.fromDegrees(viewAngle));
            if(!field.isInView(intersectionPoint)) {
                double angle =  Math.toDegrees(dir + setToYAxisAngle);
                System.out.println("Angle of point: " +angle);
                field.isInView(intersectionPoint);
            } */
            //rays.add(new Line2D.Double(x, y, intersectionPoint.getX(), intersectionPoint.getY()));
        }
        intruderObjectPercept.put(intruder, new ObjectPercepts(objectPerceptsSet));
    }

    private void setGuardObjectPercepts(Guard guard) {

        //LinkedList<Line2D.Double> rays = new LinkedList<>();
        HashSet<ObjectPercept> objectPerceptsSet = new HashSet<>();
        double maxDist = guardViewRange.get(guard).getValue() - 0.000000000000001;
        double x = guardLocations.get(guard).getX();
        double y = guardLocations.get(guard).getY();
        double notInViewDistance = 0;
        if(guardsAreaPercepts.get(guard).isInSentryTower()) notInViewDistance = viewRangeSentry[0];

        for (int i = 0; i < viewAngle; i++) {
            double dir = guardDirections.get(guard).getRadians() - Math.toRadians(viewAngle/2) + i*Math.PI/180;
            if(i == 0) dir += 0.000000000000001;
            double minDist = maxDist;
            ObjectPerceptType object = ObjectPerceptType.EmptySpace;


            for (Rectangle wall : walls) {
                Line2D.Double line1 = new Line2D.Double(wall.x, wall.y, wall.x + wall.width, wall.y );
                Line2D.Double line2 = new Line2D.Double(wall.x + wall.width, wall.y, wall.x + wall.width, wall.y + wall.height);
                Line2D.Double line3 = new Line2D.Double(wall.x, wall.y + wall.height, wall.x + wall.width, wall.y + wall.height);
                Line2D.Double line4 = new Line2D.Double(wall.x, wall.y, wall.x, wall.y + wall.height);
                double dist1 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line1.x1, line1.y1, line1.x2, line1.y2);
                double dist2 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line2.x1, line2.y1, line2.x2, line2.y2);
                double dist3 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line3.x1, line3.y1, line3.x2, line3.y2);
                double dist4 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line4.x1, line4.y1, line4.x2, line4.y2);
                double dist = Math.min(Math.min(dist1, dist2), Math.min(dist3, dist4));
                if (dist < minDist && dist > notInViewDistance) {
                    minDist = dist;
                    object = ObjectPerceptType.Wall;
                }
            }

            for (Rectangle window : windows) {
                Line2D.Double line1 = new Line2D.Double(window.x, window.y, window.x + window.width, window.y );
                Line2D.Double line2 = new Line2D.Double(window.x + window.width, window.y, window.x + window.width, window.y + window.height);
                Line2D.Double line3 = new Line2D.Double(window.x, window.y + window.height, window.x + window.width, window.y + window.height);
                Line2D.Double line4 = new Line2D.Double(window.x, window.y, window.x, window.y + window.height);
                double dist1 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line1.x1, line1.y1, line1.x2, line1.y2);
                double dist2 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line2.x1, line2.y1, line2.x2, line2.y2);
                double dist3 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line3.x1, line3.y1, line3.x2, line3.y2);
                double dist4 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line4.x1, line4.y1, line4.x2, line4.y2);
                double dist = Math.min(Math.min(dist1, dist2), Math.min(dist3, dist4));
                if (dist < minDist && dist > notInViewDistance) {
                    minDist = dist;
                    object = ObjectPerceptType.Window;
                }
            }

            for (Rectangle door : doors) {
                Line2D.Double line1 = new Line2D.Double(door.x, door.y, door.x + door.width, door.y );
                Line2D.Double line2 = new Line2D.Double(door.x + door.width, door.y, door.x + door.width, door.y + door.height);
                Line2D.Double line3 = new Line2D.Double(door.x, door.y + door.height, door.x + door.width, door.y + door.height);
                Line2D.Double line4 = new Line2D.Double(door.x, door.y, door.x, door.y + door.height);
                double dist1 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line1.x1, line1.y1, line1.x2, line1.y2);
                double dist2 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line2.x1, line2.y1, line2.x2, line2.y2);
                double dist3 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line3.x1, line3.y1, line3.x2, line3.y2);
                double dist4 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line4.x1, line4.y1, line4.x2, line4.y2);
                double dist = Math.min(Math.min(dist1, dist2), Math.min(dist3, dist4));
                if (dist < minDist && dist > notInViewDistance) {
                    minDist = dist;
                    object = ObjectPerceptType.Door;
                }
            }

            for (Rectangle sentry : sentries) {
                Line2D.Double line1 = new Line2D.Double(sentry.x, sentry.y, sentry.x + sentry.width, sentry.y );
                Line2D.Double line2 = new Line2D.Double(sentry.x + sentry.width, sentry.y, sentry.x + sentry.width, sentry.y + sentry.height);
                Line2D.Double line3 = new Line2D.Double(sentry.x, sentry.y + sentry.height, sentry.x + sentry.width, sentry.y + sentry.height);
                Line2D.Double line4 = new Line2D.Double(sentry.x, sentry.y, sentry.x, sentry.y + sentry.height);
                double dist1 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line1.x1, line1.y1, line1.x2, line1.y2);
                double dist2 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line2.x1, line2.y1, line2.x2, line2.y2);
                double dist3 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line3.x1, line3.y1, line3.x2, line3.y2);
                double dist4 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line4.x1, line4.y1, line4.x2, line4.y2);
                double dist = Math.min(Math.min(dist1, dist2), Math.min(dist3, dist4));
                if (dist < minDist && dist > notInViewDistance) {
                    minDist = dist;
                    object = ObjectPerceptType.SentryTower;
                }
            }

            for (Rectangle shade : shaded) {
                Line2D.Double line1 = new Line2D.Double(shade.x, shade.y, shade.x + shade.width, shade.y );
                Line2D.Double line2 = new Line2D.Double(shade.x + shade.width, shade.y, shade.x + shade.width, shade.y + shade.height);
                Line2D.Double line3 = new Line2D.Double(shade.x, shade.y + shade.height, shade.x + shade.width, shade.y + shade.height);
                Line2D.Double line4 = new Line2D.Double(shade.x, shade.y, shade.x, shade.y + shade.height);
                double dist1 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line1.x1, line1.y1, line1.x2, line1.y2);
                double dist2 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line2.x1, line2.y1, line2.x2, line2.y2);
                double dist3 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line3.x1, line3.y1, line3.x2, line3.y2);
                double dist4 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line4.x1, line4.y1, line4.x2, line4.y2);
                double dist = Math.min(Math.min(dist1, dist2), Math.min(dist3, dist4));
                if (dist < minDist && dist > notInViewDistance) {
                    minDist = dist;
                    object = ObjectPerceptType.ShadedArea;
                }
            }


            for (Rectangle target : targetArea) {
                Line2D.Double line1 = new Line2D.Double(target.x, target.y, target.x + target.width, target.y );
                Line2D.Double line2 = new Line2D.Double(target.x + target.width, target.y, target.x + target.width, target.y + target.height);
                Line2D.Double line3 = new Line2D.Double(target.x, target.y + target.height, target.x + target.width, target.y + target.height);
                Line2D.Double line4 = new Line2D.Double(target.x, target.y, target.x, target.y + target.height);
                double dist1 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line1.x1, line1.y1, line1.x2, line1.y2);
                double dist2 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line2.x1, line2.y1, line2.x2, line2.y2);
                double dist3 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line3.x1, line3.y1, line3.x2, line3.y2);
                double dist4 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line4.x1, line4.y1, line4.x2, line4.y2);
                double dist = Math.min(Math.min(dist1, dist2), Math.min(dist3, dist4));
                if (dist < minDist && dist > notInViewDistance) {
                    minDist = dist;
                    object = ObjectPerceptType.TargetArea;
                }
            }

            for(int j=0; j<teleports.size(); j++) {
                Rectangle teleport = teleports.get(j).getArea();
                Line2D.Double line1 = new Line2D.Double(teleport.x, teleport.y, teleport.x + teleport.width, teleport.y );
                Line2D.Double line2 = new Line2D.Double(teleport.x + teleport.width, teleport.y, teleport.x + teleport.width, teleport.y + teleport.height);
                Line2D.Double line3 = new Line2D.Double(teleport.x, teleport.y + teleport.height, teleport.x + teleport.width, teleport.y + teleport.height);
                Line2D.Double line4 = new Line2D.Double(teleport.x, teleport.y, teleport.x, teleport.y + teleport.height);
                double dist1 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line1.x1, line1.y1, line1.x2, line1.y2);
                double dist2 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line2.x1, line2.y1, line2.x2, line2.y2);
                double dist3 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line3.x1, line3.y1, line3.x2, line3.y2);
                double dist4 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line4.x1, line4.y1, line4.x2, line4.y2);
                double dist = Math.min(Math.min(dist1, dist2), Math.min(dist3, dist4));
                if (dist < minDist && dist > notInViewDistance) {
                    minDist = dist;
                    object = ObjectPerceptType.Teleport;
                }
            }


            Iterator intruderLocIterator = (new HashMap<>(intruderLocations)).entrySet().iterator();
            while (intruderLocIterator.hasNext()) {
                Map.Entry pair = (Map.Entry) intruderLocIterator.next();
                Ellipse2D intruderEllipse = (Ellipse2D) pair.getValue();
                Rectangle intruder = intruderEllipse.getBounds();
                Line2D.Double line1 = new Line2D.Double(intruder.x, intruder.y, intruder.x + intruder.width, intruder.y );
                Line2D.Double line2 = new Line2D.Double(intruder.x + intruder.width, intruder.y, intruder.x + intruder.width, intruder.y + intruder.height);
                Line2D.Double line3 = new Line2D.Double(intruder.x, intruder.y + intruder.height, intruder.x + intruder.width, intruder.y + intruder.height);
                Line2D.Double line4 = new Line2D.Double(intruder.x, intruder.y, intruder.x, intruder.y + intruder.height);
                double dist1 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line1.x1, line1.y1, line1.x2, line1.y2);
                double dist2 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line2.x1, line2.y1, line2.x2, line2.y2);
                double dist3 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line3.x1, line3.y1, line3.x2, line3.y2);
                double dist4 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line4.x1, line4.y1, line4.x2, line4.y2);
                double dist = Math.min(Math.min(dist1, dist2), Math.min(dist3, dist4));
                if (dist < minDist && dist > notInViewDistance) {
                    minDist = dist;
                    object = ObjectPerceptType.Intruder;
                }
                intruderLocIterator.remove();
            }

            Iterator guardLocIterator = (new HashMap<>(guardLocations)).entrySet().iterator();
            while (guardLocIterator.hasNext()) {
                Map.Entry pair = (Map.Entry) guardLocIterator.next();
                Guard currentGuard = (Guard) pair.getKey();
                if(currentGuard != guard) {
                    Ellipse2D guardEllipse = (Ellipse2D) pair.getValue();
                    Rectangle otherGuard = guardEllipse.getBounds();
                    Line2D.Double line1 = new Line2D.Double(otherGuard.x, otherGuard.y, otherGuard.x + otherGuard.width, otherGuard.y);
                    Line2D.Double line2 = new Line2D.Double(otherGuard.x + otherGuard.width, otherGuard.y, otherGuard.x + otherGuard.width, otherGuard.y + otherGuard.height);
                    Line2D.Double line3 = new Line2D.Double(otherGuard.x, otherGuard.y + otherGuard.height, otherGuard.x + otherGuard.width, otherGuard.y + otherGuard.height);
                    Line2D.Double line4 = new Line2D.Double(otherGuard.x, otherGuard.y, otherGuard.x, otherGuard.y + otherGuard.height);
                    double dist1 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line1.x1, line1.y1, line1.x2, line1.y2);
                    double dist2 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line2.x1, line2.y1, line2.x2, line2.y2);
                    double dist3 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line3.x1, line3.y1, line3.x2, line3.y2);
                    double dist4 = getRayCast(x, y, x + Math.cos(dir) * maxDist, y + Math.sin(dir) * maxDist, line4.x1, line4.y1, line4.x2, line4.y2);
                    double dist = Math.min(Math.min(dist1, dist2), Math.min(dist3, dist4));
                    if (dist < minDist && dist > notInViewDistance) {
                        minDist = dist;
                        object = ObjectPerceptType.Guard;
                    }
                }
                guardLocIterator.remove();
            }



            //Adding the difference between the agent's direction and the y-axis to match the agent coordinate system
            double setToYAxisAngle = Utils.clockAngle(Math.cos(guardDirections.get(guard).getRadians()), Math.sin(guardDirections.get(guard).getRadians()));
            Point intersectionPoint = new Point(Math.cos(dir + setToYAxisAngle) * minDist, Math.sin(dir + setToYAxisAngle) * minDist);

            /*
            FieldOfView field = new FieldOfView(guardViewRange.get(guard), Angle.fromDegrees(viewAngle));
            if(!field.isInView(intersectionPoint)) {
                double angle =  Math.toDegrees(dir + setToYAxisAngle);
                System.out.println("Angle of point: " +angle);
                field.isInView(intersectionPoint);
            } */

            objectPerceptsSet.add(new ObjectPercept(object, intersectionPoint));
            //rays.add(new Line2D.Double(x, y, intersectionPoint.getX(), intersectionPoint.getY()));
        }
        guardObjectPercepts.put(guard, new ObjectPercepts(objectPerceptsSet));
    }



    public static double getRayCast(double p0_x, double p0_y, double p1_x, double p1_y, double p2_x, double p2_y, double p3_x, double p3_y) {
        double s1_x, s1_y, s2_x, s2_y;
        s1_x = p1_x - p0_x;
        s1_y = p1_y - p0_y;
        s2_x = p3_x - p2_x;
        s2_y = p3_y - p2_y;

        double s, t;
        s = (-s1_y * (p0_x - p2_x) + s1_x * (p0_y - p2_y)) / (-s2_x * s1_y + s1_x * s2_y);
        t = (s2_x * (p0_y - p2_y) - s2_y * (p0_x - p2_x)) / (-s2_x * s1_y + s1_x * s2_y);

        if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
            // Collision detected
            double x = p0_x + (t * s1_x);
            double y = p0_y + (t * s1_y);

            Point point1 = new Point(p0_x, p0_y);
            Point point2 = new Point(x, y);
            return new Distance(point1, point2).getValue();
        }

        return Double.MAX_VALUE; // No collision
    }




}