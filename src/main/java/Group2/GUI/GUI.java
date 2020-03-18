package Group2.GUI;

import Group2.ReadFile;
import Interop.GameController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class GUI extends JFrame {
    public final int SCN_W = 800;
    public final int SCN_H = 600;
    public final String SCN_TITLE = "Tom & Jerry - GUI";
    public GameCanvas canvas;
    public GameController controller;
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

                    controller = ReadFile.generateController();
                    Launcher.controller = controller;
                }
                else {
                    System.out.println("User cancelled the task.");
                }
            }
        });
        JMenuItem saveMenuItem = new JMenuItem("Save Scenario");
        JMenuItem analysisMenuItem = new JMenuItem("Export Analysis");

        fileMenu.add(loadMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(analysisMenuItem);

        menuBar.add(fileMenu);
        JMenu editMenu = new JMenu("Edit");
        JMenuItem intruderMenuItem = new JMenuItem("Add Intruder");
        JMenuItem guardMenuItem = new JMenuItem("Add Guard");
        JMenuItem pausePlayMenuItem = new JMenuItem("Play Simulation");

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
        JMenuItem fullscreenWindowedMenuItem = new JMenuItem("Toggle Fullscreen");

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
        super.setTitle(SCN_TITLE);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

