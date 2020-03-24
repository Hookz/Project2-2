package Group2;
import Interop.GameController;
import Interop.Teleport;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class ReadFile{
  private static int gameMode;
  private static int height;
  private static int width;
  private static int numGuards;
  private static int numIntruders;
  private static double captureDistance;
  private static int winConditionIntruderRounds;
  private static double maxRotationAngle;
  private static double maxMoveDistanceIntruder;
  private static double maxSprintDistanceIntruder;
  private static double maxMoveDistanceGuard;
  private static int sprintCooldown;
  private static int pheromoneCooldown;
  private static double radiusPheromone;
  private static double slowDownModifierWindow;
  private static double slowDownModifierDoor;
  private static double slowDownModifierSentryTower;
  private static double viewAngle;
  private static int viewRays;
  private static double viewRangeIntruderNormal;
  private static double viewRangeIntruderShaded;
  private static double viewRangeGuardNormal;
  private static double viewRangeGuardShaded;
  private static double yellSoundRadius;
  private static double maxMoveSoundRadius;
  private static double windowSoundRadius;
  private static double doorSoundRadius;
  private static int pheromoneExpireRounds;
  private static List<Rectangle> targetArea = new ArrayList<Rectangle>();
  private static List<Rectangle> spawnAreaIntruders = new ArrayList<Rectangle>();
  private static List<Rectangle> spawnAreaGuards = new ArrayList<Rectangle>();
  private static double[] viewRangeSentry;
  private static List<Rectangle> walls = new ArrayList<Rectangle>();
  private static List<Teleport> teleports = new ArrayList<Teleport>();
  private static List<Rectangle> shaded = new ArrayList<Rectangle>();
  private static List<Rectangle> doors = new ArrayList<Rectangle>();
  private static List<Rectangle> windows = new ArrayList<Rectangle>();
  private static List<Rectangle> sentries = new ArrayList<Rectangle>();

  public static void readFile(File fileToLoad){
  try{
    int lineNumber = 0;
    final BufferedReader r = new BufferedReader(new FileReader(fileToLoad));
    String nextline;
    int[] area;
    int x,y,areaWidth,areaHeight, targetX, targetY, targetAreaWidth, targetAreaHeight;
    while((nextline = r.readLine()) != null){
      ++lineNumber;
      String[] linesplit = nextline.split(" = ");
      switch(linesplit[0]){
        case "gameMode":
          gameMode = Integer.parseInt(linesplit[1]);
          break;
        case "height":
          height = Integer.parseInt(linesplit[1]);
          break;
        case "width":
          width = Integer.parseInt(linesplit[1]);
          break;
        case "numGuards":
          numGuards = Integer.parseInt(linesplit[1]);
          break;
        case "numIntruders":
          numIntruders = Integer.parseInt(linesplit[1]);
          break;
        case "captureDistance":
          captureDistance = Double.parseDouble(linesplit[1]);
          break;
        case "winConditionIntruderRounds":
          winConditionIntruderRounds = Integer.parseInt(linesplit[1]);
          break;
        case "maxRotationAngle":
          maxRotationAngle = Double.parseDouble(linesplit[1]);
          break;
        case "maxMoveDistanceIntruder":
          maxMoveDistanceIntruder = Double.parseDouble(linesplit[1]);
          break;
        case "maxSprintDistanceIntruder":
          maxSprintDistanceIntruder = Double.parseDouble(linesplit[1]);
        case "maxMoveDistanceGuard":
          maxMoveDistanceGuard = Double.parseDouble(linesplit[1]);
          break;
        case "sprintCooldown":
          sprintCooldown = Integer.parseInt(linesplit[1]);
          break;
        case "pheromoneCooldown":
          pheromoneCooldown = Integer.parseInt(linesplit[1]);
          break;
        case "radiusPheromone":
          radiusPheromone = Double.parseDouble(linesplit[1]);
          break;
        case "slowDownModifierWindow":
          slowDownModifierWindow = Double.parseDouble(linesplit[1]);
          break;
        case "slowDownModifierDoor":
          slowDownModifierDoor = Double.parseDouble(linesplit[1]);
          break;
        case "slowDownModifierSentryTower":
          slowDownModifierSentryTower = Double.parseDouble(linesplit[1]);
          break;
        case "viewAngle":
          viewAngle = Double.parseDouble(linesplit[1]);
          break;
        case "viewRays":
          viewRays = Integer.parseInt(linesplit[1]);
          break;
        case "viewRangeIntruderNormal":
          viewRangeIntruderNormal = Double.parseDouble(linesplit[1]);
          break;
        case "viewRangeIntruderShaded":
          viewRangeIntruderShaded = Double.parseDouble(linesplit[1]);
          break;
        case "viewRangeGuardNormal":
          viewRangeGuardNormal = Double.parseDouble(linesplit[1]);
          break;
        case "viewRangeGuardShaded":
          viewRangeGuardShaded = Double.parseDouble(linesplit[1]);
          break;
        case "yellSoundRadius":
          yellSoundRadius = Double.parseDouble(linesplit[1]);
          break;
        case "maxMoveSoundRadius":
          maxMoveSoundRadius = Double.parseDouble(linesplit[1]);
          break;
        case "windowSoundRadius":
          windowSoundRadius = Double.parseDouble(linesplit[1]);
          break;
        case "doorSoundRadius":
          doorSoundRadius = Double.parseDouble(linesplit[1]);
          break;
        case "pheromoneExpireRounds":
          pheromoneExpireRounds = Integer.parseInt(linesplit[1]);
          break;
        case "targetArea":
          area = Arrays.stream(linesplit[1].split(",")).mapToInt(Integer::parseInt).toArray();
          x = Math.min(Math.min(area[0],area[2]),Math.min(area[4],area[6]));
          y = Math.min(Math.min(area[1],area[3]),Math.min(area[5],area[7]));
          areaWidth = Math.max(Math.max(area[0],area[2]),Math.max(area[4],area[6])) - x;
          areaHeight = Math.max(Math.max(area[1],area[3]),Math.max(area[5],area[7])) - y;
          targetArea.add(new Rectangle(x,y,areaWidth,areaHeight));
          break;
        case "spawnAreaIntruders":
          area = Arrays.stream(linesplit[1].split(",")).mapToInt(Integer::parseInt).toArray();
          x = Math.min(Math.min(area[0],area[2]),Math.min(area[4],area[6]));
          y = Math.min(Math.min(area[1],area[3]),Math.min(area[5],area[7]));
          areaWidth = Math.max(Math.max(area[0],area[2]),Math.max(area[4],area[6])) - x;
          areaHeight = Math.max(Math.max(area[1],area[3]),Math.max(area[5],area[7])) - y;
          spawnAreaIntruders.add(new Rectangle(x,y,areaWidth,areaHeight));
          break;
        case "spawnAreaGuards":
          area = Arrays.stream(linesplit[1].split(",")).mapToInt(Integer::parseInt).toArray();
          x = Math.min(Math.min(area[0],area[2]),Math.min(area[4],area[6]));
          y = Math.min(Math.min(area[1],area[3]),Math.min(area[5],area[7]));
          areaWidth = Math.max(Math.max(area[0],area[2]),Math.max(area[4],area[6])) - x;
          areaHeight = Math.max(Math.max(area[1],area[3]),Math.max(area[5],area[7])) - y;
          spawnAreaGuards.add(new Rectangle(x,y,areaWidth,areaHeight));
          break;
        case "wall":
          area = Arrays.stream(linesplit[1].split(",")).mapToInt(Integer::parseInt).toArray();
          x = Math.min(Math.min(area[0],area[2]),Math.min(area[4],area[6]));
          y = Math.min(Math.min(area[1],area[3]),Math.min(area[5],area[7]));
          areaWidth = Math.max(Math.max(area[0],area[2]),Math.max(area[4],area[6])) - x;
          areaHeight = Math.max(Math.max(area[1],area[3]),Math.max(area[5],area[7])) - y;
          walls.add(new Rectangle(x,y,areaWidth,areaHeight));
          break;
        case "teleport":
          area = Arrays.stream(linesplit[1].split(",")).mapToInt(Integer::parseInt).toArray();
          x = Math.min(Math.min(area[0],area[2]),Math.min(area[4],area[6]));
          y = Math.min(Math.min(area[1],area[3]),Math.min(area[5],area[7]));
          areaWidth = Math.max(Math.max(area[0],area[2]),Math.max(area[4],area[6])) - x;
          areaHeight = Math.max(Math.max(area[1],area[3]),Math.max(area[5],area[7])) - y;
          targetX = Math.min(Math.min(area[8],area[10]),Math.min(area[12],area[14]));
          targetY = Math.min(Math.min(area[9],area[11]),Math.min(area[13],area[15]));
          targetAreaWidth = Math.max(Math.max(area[8],area[10]),Math.max(area[12],area[14])) - targetX;
          targetAreaHeight = Math.max(Math.max(area[9],area[11]),Math.max(area[13],area[15])) - targetY;
          teleports.add(new Teleport(new Rectangle(x,y,areaWidth,areaHeight),new Rectangle(targetX,targetY,targetAreaWidth,targetAreaHeight)));
          teleports.add(new Teleport(new Rectangle(targetX,targetY,targetAreaWidth,targetAreaHeight),new Rectangle(x,y,areaWidth,areaHeight)));
          break;
        case "shaded":
          area = Arrays.stream(linesplit[1].split(",")).mapToInt(Integer::parseInt).toArray();
          x = Math.min(Math.min(area[0],area[2]),Math.min(area[4],area[6]));
          y = Math.min(Math.min(area[1],area[3]),Math.min(area[5],area[7]));
          areaWidth = Math.max(Math.max(area[0],area[2]),Math.max(area[4],area[6])) - x;
          areaHeight = Math.max(Math.max(area[1],area[3]),Math.max(area[5],area[7])) - y;
          shaded.add(new Rectangle(x,y,areaWidth,areaHeight));
          break;
        case "door":
          area = Arrays.stream(linesplit[1].split(",")).mapToInt(Integer::parseInt).toArray();
          x = Math.min(Math.min(area[0],area[2]),Math.min(area[4],area[6]));
          y = Math.min(Math.min(area[1],area[3]),Math.min(area[5],area[7]));
          areaWidth = Math.max(Math.max(area[0],area[2]),Math.max(area[4],area[6])) - x;
          areaHeight = Math.max(Math.max(area[1],area[3]),Math.max(area[5],area[7])) - y;
          doors.add(new Rectangle(x,y,areaWidth,areaHeight));
          break;
        case "window":
          area = Arrays.stream(linesplit[1].split(",")).mapToInt(Integer::parseInt).toArray();
          x = Math.min(Math.min(area[0],area[2]),Math.min(area[4],area[6]));
          y = Math.min(Math.min(area[1],area[3]),Math.min(area[5],area[7]));
          areaWidth = Math.max(Math.max(area[0],area[2]),Math.max(area[4],area[6])) - x;
          areaHeight = Math.max(Math.max(area[1],area[3]),Math.max(area[5],area[7])) - y;
          windows.add(new Rectangle(x,y,areaWidth,areaHeight));
          break;
        case "sentry":
          area = Arrays.stream(linesplit[1].split(",")).mapToInt(Integer::parseInt).toArray();
          x = Math.min(Math.min(area[0],area[2]),Math.min(area[4],area[6]));
          y = Math.min(Math.min(area[1],area[3]),Math.min(area[5],area[7]));
          areaWidth = Math.max(Math.max(area[0],area[2]),Math.max(area[4],area[6])) - x;
          areaHeight = Math.max(Math.max(area[1],area[3]),Math.max(area[5],area[7])) - y;
          sentries.add(new Rectangle(x,y,areaWidth,areaHeight));
          break;
        case "viewRangeSentry":
          viewRangeSentry = Arrays.stream(linesplit[1].split(",")).mapToDouble(Double::parseDouble).toArray();
          break;
        default:
          System.out.println("Invalid input sequence in config.txt on line "+lineNumber);
          break;
      }
    }
  }
  catch(IOException e){
    e.printStackTrace();
  }
  }

  public static GameController generateController() {
    return new GameController(gameMode, height, width, numGuards, numIntruders, captureDistance, winConditionIntruderRounds, maxRotationAngle, maxMoveDistanceIntruder,
            maxSprintDistanceIntruder, maxMoveDistanceGuard, pheromoneExpireRounds, sprintCooldown, pheromoneCooldown, radiusPheromone, slowDownModifierWindow, slowDownModifierDoor, slowDownModifierSentryTower, viewAngle, viewRays, viewRangeIntruderNormal, viewRangeIntruderShaded,
            viewRangeGuardNormal, viewRangeGuardShaded, viewRangeSentry, yellSoundRadius, maxMoveSoundRadius, windowSoundRadius, doorSoundRadius, targetArea, spawnAreaIntruders, spawnAreaGuards, walls, teleports,
            shaded, doors, windows, sentries);
  }
}
