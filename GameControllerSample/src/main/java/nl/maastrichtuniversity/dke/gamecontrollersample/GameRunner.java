/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.maastrichtuniversity.dke.gamecontrollersample;

import nl.maastrichtuniversity.dke.explorer.Explorer;

import java.io.FileWriter;
import java.io.PrintWriter;
        
/**
 *
 * @author joel
 */

public class GameRunner {

    protected String mapDoc;
    protected Scenario scenario;
    
    GamePlayer p;   
    
    public static void main(String[] args){
        // the mapscenario should be passed as a parameter
        String mapD = Explorer.class.getResource("/testmap.txt").toString(); // testmap.txt
        GameRunner game = new GameRunner(mapD);
        game.p.setup();
        game.p.writeGameFile();
        game.p.start();
    }

    public GameRunner(String scn){
        mapDoc=scn;
        scenario = new Scenario(mapDoc);
        p = new GamePlayer(scenario);
    }
}
