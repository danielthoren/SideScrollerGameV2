package com.sidescroller.player;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.sidescroller.game.*;

import java.util.Arrays;
import java.util.Properties;

public class Player implements Draw, Update, InputListener, CollisionListener {

    private Body body;
    private Sprite sprite;
    private Direction direction;

    private Vector2 acceleration, maxVelocity, deceleration;
    private final long iD;

    private long groundResetTimer;

    private boolean isBottomSensor, isRightSensor, isLeftSensor;
    private boolean isRunning, isGrounded, isCollisionLeft, isCollisionRight;

    //Default values
    private static final Vector2 DEFAULT_MAX_VELOCITY = new Vector2(5f, 20f);
    private static final float GROUNDED_THRESHOLD = 0.01f;
    private static final int GROUNDED_RESET_THRESHOLD = 50;


    //Jumping
    private static final int DEFAULT_NUMBER_OF_JUMPS = 2;
    private int numberOfJumpsLeft;


    //Inventory
    private InventoryItem currentItem;
    private Inventory inventory;

    //Loading properties
    Properties prop = new Properties();

    public Player(long iD, World world, Vector2 position, Texture texture, float friction, float density, float restitution, float bodyWidth) {
        this.iD = iD;
        direction = Direction.RIGHT;
        maxVelocity = DEFAULT_MAX_VELOCITY;
        acceleration = new Vector2(30f, 50f);
        deceleration = new Vector2(100f, 0f);
        groundResetTimer = -1;
        isBottomSensor = false;
        isLeftSensor = false;
        isRightSensor = false;
        isRunning = false;
        isGrounded = false;
        isCollisionLeft = false;
        isCollisionRight = false;
        sprite = new Sprite(texture);
        float bodyHeight = bodyWidth * ((float) texture.getHeight()/texture.getWidth());
        sprite.setSize(bodyWidth, bodyHeight);
        sprite.setOrigin(0,0);
        createBody(world, position, new Vector2(bodyWidth, bodyHeight), density, friction, restitution, 0.1f);
        body.setUserData(this);
        numberOfJumpsLeft = DEFAULT_NUMBER_OF_JUMPS;
        inventory = new Inventory(10, 100);
        currentItem = inventory.getDefaultItem();
    }

    /**
      * Creates the players body using the 'imSize' parameter to create the different shapes that the body consists of.
      * The body is built up by two circles and a square that sit on top of each other in the following order:
      * circle
      * square
      * circle
      * This is to make the body move smoothly in the game world and to give a valid representation of collision with the
      * player body.
      *
      * !OBS: The function can be shorted but it would be pointless since all that happens in the function is the creation of the different parts
      * of the body, how the parts should look, be sized and positioned relative to each other. Thus the function is left as it is.
      * @param world The world in which to create the player body.
      */
     private void createBody(World world, Vector2 position, Vector2 size, float density, float friction, float restitution, float sensorThickness){
         FixtureDef upperCircle = new FixtureDef();
         FixtureDef middleBox = new FixtureDef();
         FixtureDef bottomCircle = new FixtureDef();
         FixtureDef bottomSensor = new FixtureDef();
         FixtureDef leftSensor = new FixtureDef();
         FixtureDef rightSensor = new FixtureDef();

         PolygonShape middleBoxShape = new PolygonShape();
         PolygonShape bottomSensorShape = new PolygonShape();
         PolygonShape leftSensorShape = new PolygonShape();
         PolygonShape rightSensorShape = new PolygonShape();
         CircleShape upperCircleShape = new CircleShape();
         CircleShape bottomCircleShape = new CircleShape();

         //Do note that the SetAsBox takes half of the width and half of the height then spanning said measurements
         //out on both sides of the centerpoint (bodyposition). The height of each element is first divided by two
         //(because the shapes takes half width and height) and then by 3 since there are 3 elements on a player.
         float radius;
         boolean useMiddleBox;
         Vector2 middleBoxSize;
         Vector2 upperCirclePos;
         Vector2 bottomCirclePos;
         if (size.y >= size.x) {
             radius = size.x/2;
             useMiddleBox = (size.y / size.x > 2);
             //imSize.x/50 is a scalable small number that is subtracted from the middle box to avoid an edge between the circle and the box.
             middleBoxSize = new Vector2(size.x - size.x / 50 , size.y - 2*radius);
             bottomCirclePos = (new Vector2(0, (size.y - radius * 4) / 2 > 0 ? -((size.y - radius * 4) / 2) - radius : -((size.y - 2*radius)/2)));
             upperCirclePos = (new Vector2(0, (size.y - radius * 4) / 2 > 0 ? (size.y - radius * 4) / 2 + radius : (size.y - 2*radius)/2));
         }
         else{
             radius = size.y/2;
             useMiddleBox = (size.x / size.y > 2);
             //imSize.x/50 is a scalable small number that is subtracted from the middle box to avoid an edge between the circle and the box.
             middleBoxSize = new Vector2(size.y - size.y / 50, size.x - 2*radius);
             bottomCirclePos = (new Vector2((size.x - radius * 4) / 2 > 0 ? -((size.x - radius * 4) / 2) - radius : -((size.x - 2*radius)/2), 0));
             upperCirclePos = (new Vector2((size.x - radius * 4) / 2 > 0 ? (size.x - radius * 4) / 2 + radius : ((size.x - 2*radius)/2), 0));
         }
         Vector2 bottomSensorPos = new Vector2(0, bottomCirclePos.y - radius);
         Vector2 bottomSensorSize = new Vector2(size.x - size.x / 4, sensorThickness * 2);
         Vector2 leftSensorPos = new Vector2(-size.x / 2 - sensorThickness, 0);
         Vector2 leftSensorSize = new Vector2(sensorThickness, size.y - size.y/5);
         Vector2 rightSensorPos = new Vector2(size.x/2 + sensorThickness, 0);
         Vector2 rightSensorSize = new Vector2(sensorThickness, size.y - size.y/5);

         //Initializing the shapes
         upperCircleShape.setRadius(radius);
         bottomCircleShape.setRadius(radius);
         middleBoxShape.setAsBox(middleBoxSize.x/2, middleBoxSize.y / 2);
         bottomSensorShape.setAsBox(bottomSensorSize.x / 2, bottomSensorSize.y / 2, bottomSensorPos, 0);
         leftSensorShape.setAsBox(leftSensorSize.x / 2, leftSensorSize.y / 2, leftSensorPos, 0);
         rightSensorShape.setAsBox(rightSensorSize.x / 2, rightSensorSize.y / 2, rightSensorPos, 0);

         //Setting the position of the circles
         upperCircleShape.setPosition(upperCirclePos);
         bottomCircleShape.setPosition(bottomCirclePos);

         //Creating the fixture of the body. The concrete part that can be touched (the part that can collide)
         upperCircle.shape = upperCircleShape;
         upperCircle.density = density;
         upperCircle.friction = 0;
         upperCircle.restitution = restitution;
         upperCircle.isSensor = false;
         middleBox.shape = middleBoxShape;
         middleBox.density = density;
         middleBox.friction = 0;
         middleBox.restitution = restitution;
         middleBox.isSensor = false;
         bottomCircle.shape = bottomCircleShape;
         bottomCircle.density = density;
         bottomCircle.friction = friction;
         bottomCircle.restitution = restitution;
         bottomCircle.isSensor = false;
         bottomSensor.shape = bottomSensorShape;
         bottomSensor.isSensor = true;
         bottomSensor.density = 0;
         bottomSensor.friction = 0;
         leftSensor.shape = leftSensorShape;
         leftSensor.isSensor = true;
         leftSensor.density = 0;
         leftSensor.friction = 0;
         rightSensor.shape = rightSensorShape;
         rightSensor.isSensor = true;
         rightSensor.density = 0;
         rightSensor.friction = 0;

         //Creating the body using the fixtureDef and the BodyDef created beneath
         BodyDef bodyDef = new BodyDef();
         bodyDef.position.set(position);
         bodyDef.type = BodyDef.BodyType.DynamicBody;
         body = world.createBody(bodyDef);
         if (useMiddleBox){body.createFixture(middleBox);}
         body.createFixture(upperCircle);
         body.createFixture(bottomCircle);
         body.createFixture(bottomSensor).setUserData(Direction.DOWN);
         body.createFixture(rightSensor).setUserData(Direction.RIGHT);
         body.createFixture(leftSensor).setUserData(Direction.LEFT);
         body.setFixedRotation(true);
         body.setUserData(this);
         body.setActive(true);
         body.setSleepingAllowed(false);

     }

    /**
     * The function that updates the object every frame
     */
    public void update() {
        runHandler();
        //Ensures that the sensor value is not wrong. If velocity.y is 0 for two frames then the character is isGrounded
        if (body.getLinearVelocity().y > -GROUNDED_THRESHOLD && body.getLinearVelocity().y < GROUNDED_THRESHOLD && !isGrounded){
            if (groundResetTimer == -1){groundResetTimer = System.currentTimeMillis();}
            else if (System.currentTimeMillis() - groundResetTimer >= GROUNDED_RESET_THRESHOLD){
                groundResetTimer = -1;
                isGrounded = true;
            }
        }
        else{
            groundResetTimer = -1;
        }
    }
    /**
     * The function that draws the object every frame
     * @param batch The SpriteBatch with which to draw
     */
    @Override
    public void draw(SpriteBatch batch, int layer){
        //TODO Fix so that player layer is loaded from file
        if (layer == 0) {
            sprite.setPosition(body.getPosition().x - (sprite.getWidth() / 2), body.getPosition().y - (sprite.getHeight() / 2));
            sprite.setRotation(SideScrollerGameV2.radToDeg(body.getAngle()));
            sprite.draw(batch);
        }
    }

    /**
     * Handles the collision checks of the player. If a sensor collides with something then set the specific sensor
     * collision status to either true or false. This is used to determine if the player can jump or not (the bottom sensor
     * must collide with something) among other things.
     * @param contact A object containing the two bodies and fixtures that made contact. It also contains collisiondata
     */
    public void beginContact(Contact contact){

        boolean playerContact = false;
        if (contact.getFixtureA().getBody().getUserData().equals(this) && contact.getFixtureA().isSensor()){
            sensorSwitch(contact.getFixtureA(), true);
            playerContact = true;
        }
        if (contact.getFixtureB().getBody().getUserData().equals(this) && contact.getFixtureB().isSensor()) {
            sensorSwitch(contact.getFixtureB(), true);
            playerContact = true;
        }

        if(!isRunning && playerContact) {
            contact.setFriction(100);
        }
    }

    /**
     * Handles the collision checks of the player. If a sensor collides with something then set the specific sensor
     * collision status to either true or false. This is used to determine if the player can jump or not (the bottom sensor
     * must collide with something) among other things.
     * @param contact A object containing the two bodies and fixtures that made contact. It also contains collisiondata
     */
    public void endContact(Contact contact){
        if (contact.getFixtureA().getBody().getUserData().equals(this) && contact.getFixtureA().isSensor()){
            sensorSwitch(contact.getFixtureA(), false);
        }
        if (contact.getFixtureB().getBody().getUserData().equals(this) && contact.getFixtureB().isSensor()){
            sensorSwitch(contact.getFixtureB(), false);
        }
    }

    /**
     * Checks which sensor has collided and then sets the appropriate flag to the specified value.
     * This is used to determine if the player is on the ground of not (among other things).
     * @param fixture The sensor Fixture that has collided.
     * @param setValue The value to set the flag to (false if collision has ended and true if collision has begun)
     */
    private void sensorSwitch(Fixture fixture, boolean setValue){
        //The cases beneath are the only ones that are going to be sent to this function due to how the body is built up. More might
        //be added later though.
        switch ((Direction) fixture.getUserData()){
            case DOWN:
                isGrounded = setValue;
                //Resets the jump counter
                //TODO Change?
                if(setValue){
                    numberOfJumpsLeft = DEFAULT_NUMBER_OF_JUMPS;
                }
                break;
            case LEFT:
                isCollisionLeft = setValue;
                break;
            case RIGHT:
                isCollisionRight = setValue;
                break;
        }
    }

    /** Called when a key was pressed
     *
     * @param keycode one of the constants in */
    public void keyDown(int keycode){
        System.out.println("key pressed");
        if (keycode == Input.Keys.LEFT){
            isRunning = true;
            direction = Direction.LEFT;
        }
        else if (keycode == Input.Keys.RIGHT){
            isRunning = true;
            direction = Direction.RIGHT;
        }
        else if (keycode == Input.Keys.UP){
            jump();
        }
        else if (keycode == Input.Keys.P){
            System.out.println(currentItem);
        }
        else if(keycode == Input.Keys.I){
            System.out.println(Arrays.toString(inventory.getItems()));
            System.out.println(inventory.getSpaceUsed());
        }
        else if(keycode == Input.Keys.O){
            createDummyItem();
        }

    }

    /** Called when a key was released
     *
     * @param keycode one of the constants in  */
    public void keyUp (int keycode){
        System.out.println("key released");
        if (keycode == Input.Keys.LEFT || keycode == Input.Keys.RIGHT){
            isRunning = false;
        }
    }

    /**
     * Handles the acceleration and deceleration of the character
     */
    private void runHandler(){

        //Decelerating the player from running to a stop
        if (!isRunning && isGrounded){
            if (body.getLinearVelocity().x > deceleration.x / 4){
                body.applyForceToCenter(-deceleration.x, 0f, true);
            }
            else if (body.getLinearVelocity().x < -deceleration.x / 4){
                body.applyForceToCenter(deceleration.x, 0f, true);
            }
            else{
                body.setLinearVelocity(0f, body.getLinearVelocity().y);
            }
        }
        if (isRunning && isGrounded){
            //Code for smooth acceleration when on the ground
            if (direction == Direction.RIGHT && body.getLinearVelocity().x < maxVelocity.x){
                body.applyForceToCenter(acceleration.x, 0f, true);
            }
            else if (direction == Direction.LEFT && body.getLinearVelocity().x > -maxVelocity.x){
                body.applyForceToCenter(-acceleration.x, 0f, true);
            }
        }
        else if (isRunning && !isGrounded){
            //Code for smooth acceleration when in the air
            if (direction == Direction.RIGHT && body.getLinearVelocity().x < maxVelocity.x){
                body.applyForceToCenter(acceleration.x, 0f, true);
            }
            else if (direction == Direction.LEFT && body.getLinearVelocity().x > -maxVelocity.x){
                body.applyForceToCenter(-acceleration.x, 0f, true);
            }
        }
    }

    /**
     * Function that makes the player jump
     */
    private void jump(){

        //TODO Change code the reset the jump. Update is delayed so the IsGrounded is still true after the jump.

        /*
        if (isGrounded) {
            //Impulse is calculated by F = m * g (Force = mass * gravity) where g = 9.82
            Vector2 impulse = new Vector2(0, (float) (9.82 * body.getMass()));
            body.applyLinearImpulse(impulse, body.getLocalCenter(), true);
            hasJumpLefts = true;
        }
        //Does a double jump
        else if(hasJumpLefts){
            //This is used to make the second jump not care what direction the body is going
            //i.e. if the body was going down the forces would have cancelled each other out.
            body.setLinearVelocity(body.getLinearVelocity().x,0);

            float accY =  (float) (9.82 * body.getMass());
            Vector2 impulse = new Vector2(0, accY);
            body.applyLinearImpulse(impulse, body.getLocalCenter(), true);
            hasJumpLefts = false;
        }
        */
        if(numberOfJumpsLeft > 0){
            //This is used to make the second jump not care what Y-direction the body is going
            //i.e. if the body was going down the forces would have cancelled each other out.
            body.setLinearVelocity(body.getLinearVelocity().x,0);

            float accY =  (float) (9.82 * body.getMass());
            Vector2 impulse = new Vector2(0, accY);
            body.applyLinearImpulse(impulse, body.getLocalCenter(), true);
            numberOfJumpsLeft -= 1;
        }
    }


    private void equipItem(int itemId){
        currentItem = inventory.getItem(itemId);
    }


    private void createDummyItem(){

        InventoryItem dummyItem = new TestItem(2,2,"test item");

        try{
            inventory.addToInventory(dummyItem);
        } catch (InventoryFullException e){
            e.printStackTrace();
        }

    }


    public long getId(){return iD;}

    /**
     * Overridden version of Equals that ensures that both object pointers are the exact same instantiation of
     * its class.
     * @param obj The object to compare to.
     * @return True if they are equal, else false.
     */
    @Override
    public boolean equals(Object obj) {
        try{
            GameObject gameObject = (GameObject) obj;
            return gameObject.getId() == this.getId();
        }
        catch (Exception e){
            return false;
        }
    }
}
