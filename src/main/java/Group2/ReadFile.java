package Group2;
public class ReadFile{
  private int gameMode;
  private int height;
  private int width;
  private int numGuards;
  private int numIntruders;
  private double captureDistance;
  private int winConditionIntruderRounds;
  private double maxRotationAngle;
  private double maxMoveDistanceIntruder;
  private double maxSprintDistanceIntruder;
  private double maxMoveDistanceGuard;
  private int sprintCooldown;
  private int pheromoneCooldown;
  private double radiusPheromone;
  private double slowDownModifierWindow;
  private double slowDownModifierDoor;
  private double slowDownModifierSentryTower;
  private double viewAngle;
  private int viewRays;
  private double viewRangeIntruderNormal;
  private double viewRangeIntruderShaded;
  private double viewRangeGuardNormal;
  private double viewRangeGuardShaded;
  private double yellSoundRadius;
  private double maxMoveSoundRadius;
  private double windowSoundRadius;
  private double doorSoundRadius;
  private Rectangle targetArea;
  private Rectangle spawnAreaIntruders;
  private Rectangle spawnAreaGuards;
  private List<Rectangle> wall = new ArrayList<Rectangle>();
  private List<Teleport> teleport = new ArrayList<Teleport>();
  private List<Rectangle> shaded = new ArrayList<Rectangle>();
  private List<Rectangle> door = new ArrayList<Rectangle>();
  private List<Rectangle> window = new ArrayList<Rectangle>();
  private List<Rectangle> sentry = new ArrayList<Rectangle>();

  public static void main(String[] args){
    readFile();
  }
  public static void readFile(){
  try{
    final BufferedReader r = new BufferedReader(new FileReader(new File()));
    String nextline;
    while(nextline = r.readLine() != null){
      String linesplit = nextline.split(" = ");
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
          viewRangeIntruderNormal = Double.parseDouble(linesplit[1]);
          break;
        case "viewRangeGuardShaded":
          viewRangeIntruderShaded = Double.parseDouble(linesplit[1]);
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
          int[] area = Arrays.stream(linesplit.split(",")).mapToInt(Integer::parseInt).toArray();
          targetArea = new Reactangle(Math.min(area[0],area[2]),Math.min(area[1],area[3]),Math.abs(area[2]-area[0]),Math.abs(area[3]-area[1]));
          break;
        case "spawnAreaIntruders":
          int[] area = Arrays.stream(linesplit.split(",")).mapToInt(Integer::parseInt).toArray();
          spawnAreaIntruders = new Reactangle(Math.min(area[0],area[2]),Math.min(area[1],area[3]),Math.abs(area[2]-area[0]),Math.abs(area[3]-area[1]));
          break;
        case "spawnAreaGuards":
          int[] area = Arrays.stream(linesplit.split(",")).mapToInt(Integer::parseInt).toArray();
          spawnAreaGuards = new Reactangle(Math.min(area[0],area[2]),Math.min(area[1],area[3]),Math.abs(area[2]-area[0]),Math.abs(area[3]-area[1]));
          break;
        case "wall":
          int[] area = Arrays.stream(linesplit.split(",")).mapToInt(Integer::parseInt).toArray();
          wall.add(new Reactangle(Math.min(area[0],area[2]),Math.min(area[1],area[3]),Math.abs(area[2]-area[0]),Math.abs(area[3]-area[1])));
          break;
        case "teleport":
          int[] area = Arrays.stream(linesplit.split(",")).mapToInt(Integer::parseInt).toArray();
          teleport.add(new Teleport(new Rectangle(Math.min(area[0],area[2]),Math.min(area[1],area[3]),Math.abs(area[2]-area[0]),Math.abs(area[3]-area[1])),new Point(area[4],area[5])));
          break;
        case "shaded":
          int[] area = Arrays.stream(linesplit.split(",")).mapToInt(Integer::parseInt).toArray();
          shaded.add(new Reactangle(Math.min(area[0],area[2]),Math.min(area[1],area[3]),Math.abs(area[2]-area[0]),Math.abs(area[3]-area[1])));
          break;
        case "door":
          int[] area = Arrays.stream(linesplit.split(",")).mapToInt(Integer::parseInt).toArray();
          door.add(new Reactangle(Math.min(area[0],area[2]),Math.min(area[1],area[3]),Math.abs(area[2]-area[0]),Math.abs(area[3]-area[1])));
          break;
        case "window":
          int[] area = Arrays.stream(linesplit.split(",")).mapToInt(Integer::parseInt).toArray();
          window.add(new Reactangle(Math.min(area[0],area[2]),Math.min(area[1],area[3]),Math.abs(area[2]-area[0]),Math.abs(area[3]-area[1])));
          break;
        case "sentry":
          int[] area = Arrays.stream(linesplit.split(",")).mapToInt(Integer::parseInt).toArray();
          sentry.add(new Reactangle(Math.min(area[0],area[2]),Math.min(area[1],area[3]),Math.abs(area[2]-area[0]),Math.abs(area[3]-area[1])));
          break;
      }
    }
  }
  catch(FileNotFoundException e){
    e.printStackTrace();
  }
}
}
