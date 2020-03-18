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
import java.io.File;

public class GUI extends JFrame {
    public final int SCN_W = 800;
    public final int SCN_H = 600;
    public final String SCN_TITLE = "Tom & Jerry";
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

        super.setLayout(new GridLayout(0, 1));
        super.add(this.canvas);
        super.setJMenuBar(menuBar);
        super.setSize(SCN_W, SCN_H);
        super.setTitle(titleFactory());
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public String titleFactory() {
        String res = SCN_TITLE + ", Scenario \"" + scenarioName + "\"";
        if (Launcher.paused) res += " - Paused - ";
        return res;
    }
}

