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

public class GUI extends JFrame {
    public final int SCN_W = 800;
    public final int SCN_H = 600;
    public static final String SCN_TITLE = "Tom & Jerry";
    public String scenarioName = "No Scenario Loaded";
    public GameCanvas canvas;
    public GameController controller;

    public boolean fullscreen = false;

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
                System.out.println("Keycode: " + e.getKeyCode());
                switch (e.getKeyCode()) {
                    case 114: //F3
                        //Toggle GUI Debug
                        canvas.showDebug = !canvas.showDebug;
                        canvas.repaint();
                        break;
                    case 118: //F7 - FPS -= 5
                        Launcher.setFPS(Launcher.fps - 5);
                        break;
                    case 119: //F8 - Pause
                        //Toggle GUI Debug
                        canvas.showDebug = !canvas.showDebug;
                        canvas.repaint();
                        break;
                    case 120: //F9 - FPS += 5
                        Launcher.setFPS(Launcher.fps + 5);
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
}

