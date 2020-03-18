package Group2.GUI;

import Group2.ReadFile;
import Interop.GameController;

import javax.swing.*;

public class Launcher {
    public static boolean gameRunning = true;
    public static GUI interfaceInstance;
    public static GameController controller = null;
    public static GameCanvas canvas = new GameCanvas(controller);

    public static void main(String[] args) {
        //macOS Native Menubar Enable, Source: https://stackoverflow.com/questions/307024/native-swing-menu-bar-support-for-macos-x-in-java
        try {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Test");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(ClassNotFoundException e) {
            System.out.println("ClassNotFoundException: " + e.getMessage());
        }
        catch(InstantiationException e) {
            System.out.println("InstantiationException: " + e.getMessage());
        }
        catch(IllegalAccessException e) {
            System.out.println("IllegalAccessException: " + e.getMessage());
        }
        catch(UnsupportedLookAndFeelException e) {
            System.out.println("UnsupportedLookAndFeelException: " + e.getMessage());
        }
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                interfaceInstance = new GUI(canvas);
                interfaceInstance.revalidate();
                interfaceInstance.setVisible(true);
            }
        });

        gameLoop();
    }

    /**
     * Optimal Update Duration keeps the game updates in varied time frames.
     * A delta is calculated between each frame so that logicLoop can calculate the missing keypoints (if there's any!)
     *
     * This data will be relying upon the agents, so I'm not sure how we would bind.
     */
    public static final long OPTIMAL_UPDATE_DURATION = 1000000000 / 30;
    public static void gameLoop() {
        long lastLoopTime = System.nanoTime();
        long lastFrameInThisSecondShownAt = 0;
        int fps = 0;

        while (gameRunning) {
            long now = System.nanoTime();
            long updateLength = now - lastLoopTime;
            double delta = updateLength / ((double) OPTIMAL_UPDATE_DURATION);
            lastLoopTime = now;

            lastFrameInThisSecondShownAt += updateLength;
            fps++;
            if (lastFrameInThisSecondShownAt >= 1000000000) {
                System.out.println("FPS: "+fps);
                lastFrameInThisSecondShownAt = 0;
                fps = 0;
            }

            logicLoop(delta);
            canvas.repaint();

            //sleep for other processes.
            try{
                Thread.sleep( (lastLoopTime-System.nanoTime() + OPTIMAL_UPDATE_DURATION) / 1000000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * logicLoop let's us manipulate the screen contents properties
     * such as their location, color etc.
     *
     * Do not do graphical updates here, modify them under GameCanvas > paint(...)
     * Parameter "delta" provides the time between last frame and current frame, therefore update accordingly to that value.
     * @param delta
     */
    public static void logicLoop(double delta) {
        //logic loop here.
    }
}