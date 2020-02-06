package fr.themsou.renderEngine;

import fr.themsou.entities.Camera;
import fr.themsou.entities.Entity;
import fr.themsou.entities.Light;
import fr.themsou.models.TexturedModel;
import fr.themsou.shaders.StaticShader;
import fr.themsou.shaders.TerrainShader;
import fr.themsou.terrains.Terrain;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MasterRenderer {

    private static final float FOV = 70;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000f;
    private Matrix4f projectionMatrix;

    private StaticShader shader = new StaticShader();
    private EntityRenderer renderer;

    private TerrainShader terrainShader = new TerrainShader();
    private TerrainRenderer terrainRenderer;

    private HashMap<TexturedModel, List<Entity>> entities = new HashMap<>();
    private List<Terrain> terrains = new ArrayList<>();

    public MasterRenderer(){

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);

        createProjectionMatrix();
        renderer = new EntityRenderer(shader, projectionMatrix);
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
    }

    public void render(Light sun, Camera camera){

        prepare();

        // EntityRenderer

        shader.start();
        shader.loadLight(sun);
        shader.loadViewMatrix(camera);

        renderer.render(entities);

        shader.stop();
        entities.clear();

        // TerrainRenderer

        terrainShader.start();
        terrainShader.loadLight(sun);
        terrainShader.loadViewMatrix(camera);

        terrainRenderer.render(terrains);

        terrainShader.stop();

    }

    public void prepare(){

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0.2f, 0.2f,0.2f, 1);
    }

    public void processEntity(Entity entity){ // Ajoute une entity dans la liste des entités à rendre
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = entities.get(entityModel);

        if(batch != null){
            batch.add(entity);
        }else{
            batch = new ArrayList<>();
            batch.add(entity);
            entities.put(entityModel, batch);
        }
    }
    public void processTerrain(Terrain terrain){ // Ajoute un terrain dans la liste des terrains à rendre
        terrains.add(terrain);
    }

    private void createProjectionMatrix(){ // Initialise la matrice de ptojection

        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_lenght = FAR_PLANE - NEAR_PLANE;

        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_lenght);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_lenght);
        projectionMatrix.m33 = 0;

    }

    public void cleanUp(){ // Supprime les shaderss de la mémoire
        shader.cleanUP();
        terrainShader.cleanUP();
    }

}