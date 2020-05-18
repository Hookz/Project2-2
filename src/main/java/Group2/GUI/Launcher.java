package Group2.GUI;

import Group2.ReadFile;
import Interop.GameController;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.io.File;
import java.util.ArrayList;

public class Launcher {
    public static boolean gameRunning = true;
    public static boolean paused = false;
    public static GUI interfaceInstance;
    public static GameController controller = null;
    public static GameCanvas canvas = new GameCanvas(controller);
    private static String selectedFileName = null;

    public static void main(String[] args) {
        if (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0) { //if macOS
            //macOS Native Menubar Enable, Source: https://stackoverflow.com/questions/307024/native-swing-menu-bar-support-for-macos-x-in-java
            try {
                System.setProperty("apple.laf.useScreenMenuBar", "true");
                System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Test");
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException e) {
                System.out.println("ClassNotFoundException: " + e.getMessage());
            } catch (InstantiationException e) {
                System.out.println("InstantiationException: " + e.getMessage());
            } catch (IllegalAccessException e) {
                System.out.println("IllegalAccessException: " + e.getMessage());
            } catch (UnsupportedLookAndFeelException e) {
                System.out.println("UnsupportedLookAndFeelException: " + e.getMessage());
            }
        }

        //Load from args a scenario if parameter given:
        if (args.length > 0) {
            File selectedFile = new File(args[0]);
            selectedFileName = selectedFile.getName();
            ReadFile.readFile(selectedFile);

            controller = ReadFile.generateController();
            System.out.println("Target area location: (" + controller.targetArea.get(0).getX() + ", " + controller.targetArea.get(0).getY() + ").");
            canvas = new GameCanvas(controller);
        }

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                interfaceInstance = new GUI(canvas);
                if (args.length > 0) interfaceInstance.scenarioName = selectedFileName;

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
    public static int UPDATE_PER_SECOND = 30; //used to final, but now I want to update it.
    public static long OPTIMAL_UPDATE_DURATION = 1000000000 / UPDATE_PER_SECOND;
    public static int fpsThisSecond = UPDATE_PER_SECOND;
    public static int fps = 0;
    public static int turn = 0;
    public static void gameLoop() {
        long lastLoopTime = System.nanoTime();
        long lastFrameInThisSecondShownAt = 0;

        while (gameRunning) {
            long now = System.nanoTime();
            long updateLength = now - lastLoopTime;
            lastLoopTime = now;

            lastFrameInThisSecondShownAt += updateLength;
            fps++;
            if (lastFrameInThisSecondShownAt >= 1000000000) {
                //Debug title, non-necessary.
                if (interfaceInstance != null) interfaceInstance.setTitle(interfaceInstance.titleFactory());// + ", Turn: " + turn + ", FPS: " + fps);
                lastFrameInThisSecondShownAt = 0;
                fpsThisSecond = fps;
                fps = 0;
            }

            if (!paused) {
                logicLoop();
                canvas.revalidate();
                canvas.repaint();
            }

            //sleep for other processes.
            try{
                Thread.sleep( Math.max((lastLoopTime-System.nanoTime() + OPTIMAL_UPDATE_DURATION) / 1000000, 1));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setFPS (int updateRate) {
        System.out.println("Attempting to update FPS to " + updateRate);
        if (updateRate > 0 && updateRate <= 144)
                UPDATE_PER_SECOND = updateRate;
        else {
            System.out.println("Error: Invalid FPS value. >0, <144 ?");
            return;
        }
        OPTIMAL_UPDATE_DURATION = 1000000000 / UPDATE_PER_SECOND;
    }
    /**
     * logicLoop let's us manipulate the screen contents properties
     * such as their location, color etc.
     *
     * Do not do graphical updates here, modify them under GameCanvas > paint(...)
     * Parameter "delta" (used to) provides the time between last frame and current frame, therefore update accordingly to that value.
     */
    public static void logicLoop() {
        if (controller != null) {
            //logic loop here.
            controller.playSingleTurn();
            turn++;
        }
    }
}