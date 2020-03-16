package Interop;

import Group2.Teleport;
import Interop.Percept.Scenario.*;
import Interop.Geometry.*;
import java.awt.Rectangle;
import java.util.List;

class GameController{

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
    }



}