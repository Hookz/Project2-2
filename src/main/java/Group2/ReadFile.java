package Group2;
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
  private static Rectangle targetArea;
  private static Rectangle spawnAreaIntruders;
  private static Rectangle spawnAreaGuards;
  private static List<Rectangle> wall = new ArrayList<Rectangle>();
  private static List<Teleport> teleport = new ArrayList<Teleport>();
  private static List<Rectangle> shaded = new ArrayList<Rectangle>();
  private static List<Rectangle> door = new ArrayList<Rectangle>();
  private static List<Rectangle> window = new ArrayList<Rectangle>();
  private static List<Rectangle> sentry = new ArrayList<Rectangle>();

  public static void main(String[] args) {
    readFile();
    System.out.println(gameMode);
    System.out.println(height);
    System.out.println(width);
    System.out.println(numGuards);
    System.out.println(numIntruders);
    System.out.println(captureDistance);
    System.out.println(winConditionIntruderRounds);
    System.out.println(maxRotationAngle);
    System.out.println(maxMoveDistanceIntruder);
    System.out.println(maxSprintDistanceIntruder);
    System.out.println(maxMoveDistanceGuard);
    System.out.println(sprintCooldown);
    System.out.println(pheromoneCooldown);
    System.out.println(radiusPheromone);
    System.out.println(slowDownModifierWindow);
    System.out.println(slowDownModifierDoor);
    System.out.println(slowDownModifierSentryTower);
    System.out.println(viewAngle);
    System.out.println(viewRays);
    System.out.println(viewRangeIntruderNormal);
    System.out.println(viewRangeIntruderShaded);
    System.out.println(viewRangeGuardNormal);
    System.out.println(viewRangeGuardShaded);
    System.out.println(yellSoundRadius);
    System.out.println(maxMoveSoundRadius);
    System.out.println(windowSoundRadius);
    System.out.println(doorSoundRadius);
  }
  public static void readFile(){
  try{
    int lineNumber = 0;
    final BufferedReader r = new BufferedReader(new FileReader(new File(System.getProperty("java.class.path")+"/config.txt")));
    String nextline;
    int[] area;
    int x,y,areaWidth,areaHeight;
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
        case "targetArea":
          area = Arrays.stream(linesplit[1].split(",")).mapToInt(Integer::parseInt).toArray();
          x = Math.min(Math.min(area[0],area[2]),Math.min(area[4],area[6]));
          y = Math.min(Math.min(area[1],area[3]),Math.min(area[5],area[7]));
          areaWidth = Math.max(Math.max(area[0],area[2]),Math.max(area[4],area[6])) - x;
          areaHeight = Math.max(Math.max(area[1],area[3]),Math.max(area[5],area[7])) - y;
          targetArea = new Rectangle(x,y,areaWidth,areaHeight);
          break;
        case "spawnAreaIntruders":
          area = Arrays.stream(linesplit[1].split(",")).mapToInt(Integer::parseInt).toArray();
          x = Math.min(Math.min(area[0],area[2]),Math.min(area[4],area[6]));
          y = Math.min(Math.min(area[1],area[3]),Math.min(area[5],area[7]));
          areaWidth = Math.max(Math.max(area[0],area[2]),Math.max(area[4],area[6])) - x;
          areaHeight = Math.max(Math.max(area[1],area[3]),Math.max(area[5],area[7])) - y;
          spawnAreaIntruders = new Rectangle(x,y,areaWidth,areaHeight);
          break;
        case "spawnAreaGuards":
          area = Arrays.stream(linesplit[1].split(",")).mapToInt(Integer::parseInt).toArray();
          x = Math.min(Math.min(area[0],area[2]),Math.min(area[4],area[6]));
          y = Math.min(Math.min(area[1],area[3]),Math.min(area[5],area[7]));
          areaWidth = Math.max(Math.max(area[0],area[2]),Math.max(area[4],area[6])) - x;
          areaHeight = Math.max(Math.max(area[1],area[3]),Math.max(area[5],area[7])) - y;
          spawnAreaGuards = new Rectangle(x,y,areaWidth,areaHeight);
          break;
        case "wall":
          area = Arrays.stream(linesplit[1].split(",")).mapToInt(Integer::parseInt).toArray();
          x = Math.min(Math.min(area[0],area[2]),Math.min(area[4],area[6]));
          y = Math.min(Math.min(area[1],area[3]),Math.min(area[5],area[7]));
          areaWidth = Math.max(Math.max(area[0],area[2]),Math.max(area[4],area[6])) - x;
          areaHeight = Math.max(Math.max(area[1],area[3]),Math.max(area[5],area[7])) - y;
          wall.add(new Rectangle(x,y,areaWidth,areaHeight));
          break;
        case "teleport":
          area = Arrays.stream(linesplit[1].split(",")).mapToInt(Integer::parseInt).toArray();
          x = Math.min(Math.min(area[0],area[2]),Math.min(area[4],area[6]));
          y = Math.min(Math.min(area[1],area[3]),Math.min(area[5],area[7]));
          areaWidth = Math.max(Math.max(area[0],area[2]),Math.max(area[4],area[6])) - x;
          areaHeight = Math.max(Math.max(area[1],area[3]),Math.max(area[5],area[7])) - y;
          teleport.add(new Teleport(new Rectangle(x,y,areaWidth,areaHeight),new Point(area[4],area[5])));
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
          door.add(new Rectangle(x,y,areaWidth,areaHeight));
          break;
        case "window":
          area = Arrays.stream(linesplit[1].split(",")).mapToInt(Integer::parseInt).toArray();
          x = Math.min(Math.min(area[0],area[2]),Math.min(area[4],area[6]));
          y = Math.min(Math.min(area[1],area[3]),Math.min(area[5],area[7]));
          areaWidth = Math.max(Math.max(area[0],area[2]),Math.max(area[4],area[6])) - x;
          areaHeight = Math.max(Math.max(area[1],area[3]),Math.max(area[5],area[7])) - y;
          window.add(new Rectangle(x,y,areaWidth,areaHeight));
          break;
        case "sentry":
          area = Arrays.stream(linesplit[1].split(",")).mapToInt(Integer::parseInt).toArray();
          x = Math.min(Math.min(area[0],area[2]),Math.min(area[4],area[6]));
          y = Math.min(Math.min(area[1],area[3]),Math.min(area[5],area[7]));
          areaWidth = Math.max(Math.max(area[0],area[2]),Math.max(area[4],area[6])) - x;
          areaHeight = Math.max(Math.max(area[1],area[3]),Math.max(area[5],area[7])) - y;
          sentry.add(new Rectangle(x,y,areaWidth,areaHeight));
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
}
