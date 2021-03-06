package fr.themsou.main;

import fr.themsou.renderEngine.Loader;

public class Main {

    public static Loader loader = new Loader(); // Classe instancié une seul fois, elle permet de créer les RawModel et les ModelTexture
    public static MainLoop mainLoop;

    public static  void main(String args[]){

        DisplayManager.createDisplay(0, 0, 0, "Open Map Game");

        mainLoop = new MainLoop();
        mainLoop.startMainLoop();

    }

    public static void onCloseDisplay(){
        loader.cleanUP();
        DisplayManager.closeDisplay();
    }
}
