package Group2.GUI;

import Group2.ReadFile;
import Interop.GameController;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.io.File;
import java.util.ArrayList;

public class GUI extends JFrame {
    public final int SCN_W = 800;
    public final int SCN_H = 600;
    public static final String SCN_TITLE = "Tom & Jerry";
    public String scenarioName = "No Scenario Loaded";
    public GameCanvas canvas;
    public GameController controller;

    public boolean fullscreen = false;

    public static ArrayList<String> debugTexts = new ArrayList<>();
    public static String debugInput = "";
    public static boolean drawSounds = true;
    public static boolean drawIntruderSmell = true;
    public static boolean drawTeleport = true;
    public static boolean drawTargetArea = true;
    public static boolean drawWalls = true;

    public GUI(GameCanvas canvasInstance){
        super("Tom & Jerry - Loading..."); //default title has to be given
        this.canvas = canvasInstance;

        //Menu Bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem loadMenuItem = new JMenuItem(new AbstractAction("Load Scenario") {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Loading scenario...");
                canvas.text1 = "Loading Scenario...";
                canvas.text2 = "";

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

                if (fileChooser.showOpenDialog(canvas) == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    System.out.println("Selected scenario: " + selectedFile.getAbsolutePath());
                    ReadFile.readFile(selectedFile);

                    scenarioName = selectedFile.getName();
                    controller = ReadFile.generateController();
                    Launcher.controller = controller;
                    GUI.super.remove(canvas);
                    canvas = new GameCanvas(controller);
                    GUI.super.add(canvas);

                    Launcher.canvas = canvas;
                }
                else {
                    System.out.println("User cancelled the task.");
                    canvas.text1 = canvas.TEXT_MIDDLE_DEFAULT;
                    canvas.text2 = canvas.TEXT_SMALL_DEFAULT;
                }
            }
        });
        JMenuItem saveMenuItem = new JMenuItem("Save Scenario");
        JMenuItem closeMenuItem = new JMenuItem(new AbstractAction("Close Scenario") {
            public void actionPerformed(ActionEvent e) {
                GUI.super.remove(canvas);
                canvas = new GameCanvas(null);
                GUI.super.add(canvas);
                scenarioName = "No Scenario Loaded";
                //reset Launcher fields
                Launcher.canvas = canvas;
                Launcher.controller = null;
                Launcher.turn = 0;
            }
        });
        JMenuItem analysisMenuItem = new JMenuItem("Export Analysis");

        fileMenu.add(loadMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(closeMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(analysisMenuItem);

        menuBar.add(fileMenu);
        JMenu editMenu = new JMenu("Edit");
        JMenuItem intruderMenuItem = new JMenuItem("Add Intruder");
        JMenuItem guardMenuItem = new JMenuItem("Add Guard");
        JMenuItem pausePlayMenuItem = new JMenuItem(new AbstractAction("Pause Simulation") {
            public void actionPerformed(ActionEvent e) {
                if (Launcher.paused) {
                    Launcher.paused = false;

                    ((JMenuItem) e.getSource()).setText("Pause Simulation");
                    return;
                }

                ((JMenuItem) e.getSource()).setText("Play Simulation");
                Launcher.paused = true;
                canvas.repaint(); //print once more incase there's debug menu activated
            }
        });

        menuBar.add(editMenu);
        editMenu.add(intruderMenuItem);
        editMenu.add(guardMenuItem);
        editMenu.addSeparator();
        editMenu.add(pausePlayMenuItem);

        JMenu viewMenu = new JMenu("View");
        JMenu perspectiveSubmenu = new JMenu("Change Perspective");
        JMenuItem godPerspectiveMenuItem = new JMenuItem("God");
        JMenuItem guardPerspectiveMenuItem = new JMenuItem("Guards");
        JMenuItem intruderPerspectiveMenuItem = new JMenuItem("Intruders");
        JMenuItem fullscreenWindowedMenuItem = new JMenuItem(new AbstractAction("Toggle Fullscreen") {
            int[] originalDimension = new int[2];
            public void actionPerformed(ActionEvent e) {
                if (!fullscreen) {
                    originalDimension[0] = GUI.super.getSize().width;
                    originalDimension[1] = GUI.super.getSize().height;
                    GUI.super.dispose();
                    GUI.super.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    GUI.super.setUndecorated(true);
                    GUI.super.pack();
                    GUI.super.setVisible(true);
                    ((JMenuItem) e.getSource()).setText("Toggle Windowed");
                    fullscreen = true;
                }
                else {
                    GUI.super.dispose();
                    GUI.super.setExtendedState(0);
                    GUI.super.setUndecorated(false);
                    GUI.super.pack();
                    GUI.super.setSize(originalDimension[0], originalDimension[1]);
                    GUI.super.setVisible(true);
                    ((JMenuItem) e.getSource()).setText("Toggle Fullscreen");
                    fullscreen = false;
                }
            }
        });

        menuBar.add(viewMenu);
        viewMenu.add(perspectiveSubmenu);
        perspectiveSubmenu.add(godPerspectiveMenuItem);
        perspectiveSubmenu.addSeparator();
        perspectiveSubmenu.add(guardPerspectiveMenuItem);
        perspectiveSubmenu.add(intruderPerspectiveMenuItem);
        viewMenu.add(fullscreenWindowedMenuItem);
        //End Menubar

        //Register Keyclicks
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                return;
            }

            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println(e.getKeyCode());
                switch (e.getKeyCode()) {
                    case 114: //F3
                        //Toggle GUI Debug
                        canvas.showDebug = !canvas.showDebug;
                        canvas.repaint();
                        break;
                    case 118: //F7 - FPS -= 5
                        Launcher.setFPS(Launcher.UPDATE_PER_SECOND - 5);
                        break;
                    case 119: //F8 - Pause
                        //Toggle GUI Debug
                        canvas.showDebug = !canvas.showDebug;
                        canvas.repaint();
                        break;
                    case 120: //F9 - FPS += 5
                        Launcher.setFPS(Launcher.UPDATE_PER_SECOND + 5);
                        break;
                    default:
                        //use to manipulate debug console user input
                        int kc = e.getKeyCode();
                        if (kc == 8) { //backspace
                            if (debugInput.length() > 0)
                                debugInput = debugInput.substring(0, debugInput.length() - 1);
                        } else if (kc == 10) { //enter
                            debugTexts.add("> " + debugInput + ":");
                            processCommand();
                            debugInput = "";
                        } else {
                            debugInput += (char) kc;
                        }
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                return;
            }
        });
        //End Keyclicks

        //Register Swing GUI Settings
        super.setLayout(new GridLayout(0, 1));
        super.add(this.canvas);
        super.setJMenuBar(menuBar);
        super.setSize(SCN_W, SCN_H);
        super.setTitle(titleFactory());
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public String titleFactory() {
        String res = SCN_TITLE;
        if (Launcher.paused) res += " - " + coolPausedTextGenerator() + " - ";
        res += ", Scenario \"" + scenarioName + "\"";
        return res;
    }

    //was so tempting to do.
    private String pausedCoolTextOnTitle = "Paused";
    private int currentCapitalIndex = 0;
    public String coolPausedTextGenerator() {
        currentCapitalIndex = (currentCapitalIndex + 1) % pausedCoolTextOnTitle.length();
        String before = pausedCoolTextOnTitle.substring(0, currentCapitalIndex).toLowerCase();
        String toCapitalize = pausedCoolTextOnTitle.substring(currentCapitalIndex, currentCapitalIndex + 1).toUpperCase();
        String after = pausedCoolTextOnTitle.substring(currentCapitalIndex + 1).toLowerCase();

        //Debug:
        // System.out.println("Current Text: '" + pausedCoolTextOnTitle + ", currentCapitalIndex: " + currentCapitalIndex + ", Before: '" + before + "'" + ", toCapitalize: '" + toCapitalize + "', after: '" + after + "'");
        pausedCoolTextOnTitle = before + toCapitalize + after;
        return pausedCoolTextOnTitle;
    }

    private void processCommand() {
        String command = debugInput;
        String[] split = command.split(" ");

        switch (split[0]) {
            case "HELP":
                debugTexts.add("Welcome to command utilities");
                debugTexts.add("HELP - displays these text");
                debugTexts.add("KEYS - displays available key shortcuts");
                debugTexts.add("TOGGLE arg - toggle on/off display of properties");
                debugTexts.add("INTERVAL arg - change update interval 0:30");
                debugTexts.add("CLEAR - clears history");
                break;
            case "KEYS":
                debugTexts.add("F3 - Toggle debug screen on/off.");
                debugTexts.add("F7 - Decrease FPS by 5.");
                debugTexts.add("F9 - Increase FPS by 5.");
                break;
            case "CLEAR":
                debugTexts.clear();
                break;
            case "TOGGLE":
                if (split.length != 2) { //what to toggle?
                    debugTexts.add("No parameter provided.");
                    debugTexts.add("WALLS/TARGET/TELEPORT/SMELL/SOUND ?");
                    break;
                }
                else {
                    switch (split[1]) {
                        case "WALLS":
                            drawWalls = !drawWalls;
                            debugTexts.add("Toggled walls.");
                            break;
                        case "TARGET":
                            drawTargetArea = !drawTargetArea;
                            debugTexts.add("Toggled target area.");
                            break;
                        case "TELEPORT":
                            drawTeleport = !drawTeleport;
                            debugTexts.add("Toggled teleports.");
                            break;
                        case "SMELL":
                            drawIntruderSmell = !drawIntruderSmell;
                            debugTexts.add("Toggled intruder smells.");
                            break;
                        case "SOUND":
                            drawSounds = !drawSounds;
                            debugTexts.add("Toggled sounds.");
                            break;
                        default:
                            debugTexts.add("Unknown parameter provided.");
                            debugTexts.add("WALLS/TARGET/TELEPORT/SMELL/SOUND ?");
                            break;
                    }
                }
                break;
            case "INTERVAL":
                if (split.length != 2) {
                    debugTexts.add("No parameter provided.");
                    debugTexts.add("Provide a decimal [0:30] ?");
                    break;
                }

                int newInterval;
                try {
                    newInterval = Integer.parseInt(split[1]);
                }
                catch (Exception e) {
                    debugTexts.add("Unknown parameter provided.");
                    debugTexts.add("Please input a decimal.");
                    break;
                }

                Launcher.setFPS(newInterval);
                debugTexts.add("Set interval as " + newInterval + ".");
                break;
            default:
                debugTexts.add("Command not found.");
        }
    }
}

