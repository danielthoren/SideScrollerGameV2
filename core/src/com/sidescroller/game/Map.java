package com.sidescroller.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.Body;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Holds the different values of the map. Having more than one instance of the map may be importaint in the future when
 * multiple maps may be needed for one session (doors and such).
 */
public class Map
{
    private List<Draw> drawObjects;
    private List<Update> updateObjects;
    private List<InputListener> inputListenerList;
    private List<CollisionListener> collisionListenerList;

    private List<Draw> drawObjectsStagedForRemoval;
    private List<Update> updateObjectsStagedForRemoval;
    private List<InputListener> inputListenersStagedForRemoval;
    private List<CollisionListener> collisionListenersStagedForRemoval;
    private List<Body> bodiesStagedForRemoval;

    private List<Draw> drawObjectsStagedForAddition;
    private List<Update> updateObjectsStagedForAddition;
    private List<InputListener> inputListenersStagedForAddition;
    private List<CollisionListener> collisionListenersStagedForAddition;

    private long objectID;

    private World world;

    /**
     * Creates an instance of 'Map' wich is a container for all of the world objects. It also contains an abstractionlayer
     * for removing and adding new objects to the world, preventing the program from crashing.
     */
    public Map(Vector2 gravity, boolean doSleep) {
        world = new World(gravity, doSleep);
        objectID = 0;
        drawObjects = new ArrayList<Draw>(10);
        updateObjects = new ArrayList<Update>(10);
        collisionListenerList = new ArrayList<CollisionListener>(10);
        inputListenerList = new ArrayList<InputListener>(10);
        drawObjectsStagedForRemoval = new ArrayList<Draw>(2);
        updateObjectsStagedForRemoval = new ArrayList<Update>(2);
        inputListenersStagedForRemoval = new ArrayList<InputListener>(2);
        collisionListenersStagedForRemoval = new ArrayList<CollisionListener>(2);
        bodiesStagedForRemoval = new ArrayList<Body>(2);
        drawObjectsStagedForAddition = new ArrayList<Draw>(2);
        updateObjectsStagedForAddition = new ArrayList<Update>(2);
        inputListenersStagedForAddition = new ArrayList<InputListener>(2);
        collisionListenersStagedForAddition = new ArrayList<CollisionListener>(2);
    }

    /**
     * Removes objects staged for removal and clears the 'StagedForRemoval' lists. Theese lists are a buffer to prevent
     * instantanious removal of objects during runtime, if objects were removed during iteration over lists containing theese
     * objects or during the world step then the program would crash.
     */
    public void removeStagedOBjects (){
        //List used to keep track of wich id:s bodies have been removed. Used to prevent multiple removals of the same body.
        //If a item that will be staged for removal when collision occurs with a type of object then two such bodies will
        //be added to the 'stagedForRemoval' if the item collides with two objects of said type between one step.
        List<Long> bodyIDRemoved = new ArrayList<Long>();
        //Destroying all of the bodies that are staged for removal
        for (Body body : bodiesStagedForRemoval){
            if (!bodyIDRemoved.contains(((GameObject)body.getUserData()).getId())) {
                bodyIDRemoved.add(((GameObject)body.getUserData()).getId());
                world.destroyBody(body);
            }
        }
        //Removing all of the 'Draw' objects from the maps global list
        for (Draw objectRemove : drawObjectsStagedForRemoval){
            for (Iterator<Draw> iterator = drawObjects.iterator(); iterator.hasNext();){
                Draw object = iterator.next();
                if (objectRemove.getId() == object.getId()){
                    iterator.remove();
                }
            }
        }
        //Removing all of the 'Update' objects from the maps global list
        for (Update objectRemove : updateObjectsStagedForRemoval){
            for (Iterator<Update> iterator = updateObjects.iterator(); iterator.hasNext();){
                Update object = iterator.next();
                if (objectRemove.getId() == object.getId()){
                    iterator.remove();
                }
            }
        }
        //Removing all of the 'gamelogic.InputListener' objects from the maps global list
        for (InputListener listenerRemova : inputListenersStagedForRemoval){
            for (Iterator<InputListener> iterator = inputListenerList.iterator(); iterator.hasNext();) {
                InputListener inputListener = iterator.next();
                if (inputListener.getId() == listenerRemova.getId()){
                    iterator.remove();
                }
            }
        }
        //Removing all of the 'gamelogic.CollisionListener' objects from the maps global list
        for (CollisionListener collisionListenerRemove : collisionListenersStagedForRemoval){
            for (Iterator<CollisionListener> iterator = collisionListenerList.iterator(); iterator.hasNext();){
                CollisionListener collisionListener = iterator.next();
                if (collisionListener.getId() == collisionListenerRemove.getId()) {
                    iterator.remove();
                }
            }
        }
        //Clearing all of the 'StagedForRemoval' lists
        bodiesStagedForRemoval.clear();
        updateObjectsStagedForRemoval.clear();
        drawObjectsStagedForRemoval.clear();
        inputListenersStagedForRemoval.clear();
        collisionListenersStagedForRemoval.clear();
}

    /**
     * Adding objects staged for addition and clears the 'StagedForAddition' lists. Theese lists are a buffer to prevent
     * instantanious addition of objects during runtime, if objects were removed during iteration over lists containing theese
     * objects or during the world step then the program would crash.
     */
    public void addStagedObjects (){
        //Adding all of the staged 'Draw' objects to the maps global list
        for (Draw object : drawObjectsStagedForAddition){
            drawObjects.add(object);
        }
        //Adding all of the staged 'Update' objects to the maps global list
        for (Update object : updateObjectsStagedForAddition){
            updateObjects.add(object);
        }
        //Adding all of the staged 'gamelogic.InputListener' objects to the maps global list
        for (InputListener listener : inputListenersStagedForAddition){
            inputListenerList.add(listener);
        }
        //Adding all of the staged 'gamelogic.CollisionListener' objects to the maps global list
        for (CollisionListener collisionListener : collisionListenersStagedForAddition){
            collisionListenerList.add(collisionListener);
        }
        //Clearing the objects staged for addition
        drawObjectsStagedForAddition.clear();
        updateObjectsStagedForAddition.clear();
        inputListenersStagedForAddition.clear();
        collisionListenersStagedForAddition.clear();
    }

    public long getObjectID() {
        objectID++;
        return objectID;
    }

    public void removeBody(Body body){bodiesStagedForRemoval.add(body);}

    public void removeCollisionListener(CollisionListener listener){collisionListenersStagedForRemoval.add(listener);}

    public void removeInputListener(InputListener listener){inputListenersStagedForRemoval.add(listener);}

    public void removeDrawObject(Draw object){drawObjectsStagedForRemoval.add(object);}

    public void removeUpdateObject(Update object){updateObjectsStagedForRemoval.add(object);}

    public void addDrawObject(Draw object) {drawObjectsStagedForAddition.add(object);}

    public void addUpdateObject(Update object) {updateObjectsStagedForAddition.add(object);}

    public void addInputListener(InputListener object) { inputListenersStagedForAddition.add(object);}

    public void addCollisionListener(CollisionListener object) { collisionListenersStagedForAddition.add(object);}

    public List<Draw> getDrawObjects() {return drawObjects;}

    public List<Update> getUpdateObjects() {return updateObjects;}

    public Iterable<InputListener> getInputListenerList() {return inputListenerList;}

    public Iterable<CollisionListener> getCollisionListenerList() {return collisionListenerList;}

    public World getWorld() {return world;}
}
