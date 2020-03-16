package Interop;

import Group2.AgentsFactory;
import Group2.Teleport;
import Interop.Agent.Guard;
import Interop.Agent.Intruder;
import Interop.Percept.Scenario.*;
import Interop.Geometry.*;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class GameController{

    private ScenarioPercepts scenarioPercept;
    private ScenarioIntruderPercepts scenarioIntruderPercepts;
    private ScenarioGuardPercepts scenarioGuardPercepts;

    private int height;
    private int width;
    private int numGuards;
    private int numIntruders;

    private double viewAngle;
    private int viewRays;
    private double viewRangeIntruderNormal;
    private double viewRangeIntruderShaded;
    private double viewRangeGuardNormal;
    private double viewRangeGuardShaded;
    private double[] viewRangeSentry;

    private double yellSoundRadius;
    private double maxMoveSoundRadius;
    private double windowSoundRadius;
    private double doorSoundRadius;

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

    List<Intruder> intruders;
    List<Guard> guards;

    public GameController(int gameMode, int height, int width, int numGuards, int numIntruders, double captureDistance, int winConditionIntruderRounds, double maxRotationAngle, double maxMoveDistanceIntruder,
                              double maxSprintDistanceIntruder, double maxMoveDistanceGuard, int sprintCooldown, int pheromoneCooldown, double radiusPheromone, double slowDownModifierWindow,
                              double slowDownModifierDoor, double slowDownModifierSentryTower, double viewAngle, int viewRays, double viewRangeIntruderNormal, double viewRangeIntruderShaded,
                              double viewRangeGuardNormal, double viewRangeGuardShaded, double[] viewRangeSentry, double yellSoundRadius, double maxMoveSoundRadius, double windowSoundRadius,
                              double doorSoundRadius, List<Rectangle> targetArea, List<Rectangle> spawnAreaIntruders, List<Rectangle> spawnAreaGuards, List<Rectangle> walls, List<Teleport> teleports,
                              List<Rectangle> shaded, List<Rectangle> doors, List<Rectangle> windows, List<Rectangle> sentries)
    {
        GameMode gamemode = GameMode.values()[gameMode];
        this.scenarioPercept = new ScenarioPercepts(gamemode, new Distance(captureDistance), Angle.fromDegrees(maxRotationAngle), new SlowDownModifiers(slowDownModifierWindow, slowDownModifierDoor,
                        slowDownModifierSentryTower), new Distance(radiusPheromone), pheromoneCooldown);
        this.scenarioIntruderPercepts = new ScenarioIntruderPercepts(this.scenarioPercept, winConditionIntruderRounds, new Distance(maxMoveDistanceIntruder), new Distance(maxSprintDistanceIntruder), sprintCooldown);
        this.scenarioGuardPercepts = new ScenarioGuardPercepts(this.scenarioPercept, new Distance(maxMoveDistanceGuard));

        this.height = height;
        this.width = width;
        this.numGuards = numGuards;
        this.numIntruders = numIntruders;

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
                Ellipse2D intruderLocation = new Ellipse2D.Double(x, y, 0.5, 0.5);
                intruderLocations.put(intruder, intruderLocation);

                double directionAngle = (2*Math.PI) * random.nextDouble();
                Direction direction = Direction.fromRadians(directionAngle);
                intruderDirections.put(intruder,direction);
            }
        }

        //Guard Agent Contructor
        if(numGuards>0) {
            guards = AgentsFactory.createGuards(numGuards);
            for (Guard guard : guards) {
                double x = spawnAreaGuards.get(0).getX() + spawnAreaGuards.get(0).getWidth() * random.nextDouble();
                double y = spawnAreaGuards.get(0).getY() + spawnAreaGuards.get(0).getHeight() * random.nextDouble();
                Ellipse2D guardLocation = new Ellipse2D.Double(x, y, 0.5, 0.5);
                guardLocations.put(guard, guardLocation);

                double directionAngle = (2*Math.PI) * random.nextDouble();
                Direction direction = Direction.fromRadians(directionAngle);
                guardDirections.put(guard,direction);
            }
        }

        //Run the UI construction
        //UI.createUI(<all the walls etc above here>)



    }



}