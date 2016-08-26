package com.sidescroller.player;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.sidescroller.game.*;
import com.sidescroller.map.Map;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel on 2016-06-06.
 */
public class Player implements Draw, Update, InputListener, CollisionListener {

    private Body body;
    private Sprite sprite;
    private Direction direction;
    private List<Body> collidingBodies;
	private SideScrollGameV2 sideScrollGameV2;
    private Contact groundContact;

    private Vector2 acceleration, maxVelocity, deceleration;
    private final long id;

    private long groundResetTimer;

    private boolean isRunning, isGrounded, isCollisionLeft, isCollisionRight;

    private float bodyWidth;
    private float bodyHeight;
    private float density;
    private float restitution;
    private float friction;
    private float sensorThickness;

    private int interactKey;
    private int leftKey;
    private int rightKey;
    private int upKey;

    private boolean isInteractKey;
    private boolean isLeftKey;
    private boolean isRightKey;
    private boolean isUpKey;
    private boolean clearCollidingBodies;

    //Default values
    private static final Vector2 DEFAULT_MAX_VELOCITY = new Vector2(1, 5);
    private static final Vector2 ACCELERATION_DEFAULT = new Vector2(10, 0.6f);
    private static final Vector2 DECELERATION_DEFAULT = new Vector2(100, 0);
    private static final float GROUNDED_THRESHOLD = 0.01f;
    private static final int GROUNDED_RESET_THRESHOLD = 50;

    public Player(long id, Map map, SideScrollGameV2 sideScrollGameV2, Vector2 position, Texture texture, float friction, float density, float restitution, float bodyWidth) {
        this.id = id;
		this.sideScrollGameV2 = sideScrollGameV2;
        this.friction = friction;
        this.density = density;
        this.restitution = restitution;
        this.bodyWidth = bodyWidth;
        sensorThickness = (float) (0.1 * bodyWidth);
        collidingBodies = new ArrayList<Body>(1);
        direction = Direction.RIGHT;
        maxVelocity = DEFAULT_MAX_VELOCITY;
        acceleration = ACCELERATION_DEFAULT;
        deceleration = DECELERATION_DEFAULT;
        groundResetTimer = -1;
        isRunning = false;
        isGrounded = false;
        isCollisionLeft = false;
        isCollisionRight = false;
        clearCollidingBodies = false;
        sprite = new Sprite(texture);
        bodyHeight = bodyWidth * ((float) texture.getHeight()/texture.getWidth());
        sprite.setSize(bodyWidth, bodyHeight);
        sprite.setOrigin(0,0);
        createBody(position, new Vector2(bodyWidth, bodyHeight), map);
        groundContact = null;

        //setting default keybindings
        interactKey = Keys.E;
        leftKey = Keys.LEFT;
        rightKey = Keys.RIGHT;
        upKey = Keys.UP;

        isInteractKey = true;
        isLeftKey = true;
        isRightKey = true;
        isUpKey = true;

        map.updateLayerDepth(SideScrollGameV2.PLAYER_DRAW_LAYER);
        body.setUserData(this);
    }

	/**
	 * Reloads the body in to a specified map. This is used when loading a new map so that the player stays the same.
     * @param map The map to load the player in to.
     * @param position The position to load the player in to.
     */
    public void recreateBodoy(Map map, Vector2 position){
        createBody(position, new Vector2(bodyWidth, bodyHeight), map);
        clearCollidingBodies = true;
        sideScrollGameV2.getCurrentMap().updateLayerDepth(SideScrollGameV2.PLAYER_DRAW_LAYER);
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
      * of the body, how the parts shoudl look, be sized and positioned relative to each other. Thus the function is left as it is.
      */
     private void createBody(Vector2 position, Vector2 size, Map map) {
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

         //Do note that the SetAsBox takes half of the width and half of the height then spanning said measurments
         //out on both sides of the centerpoint (bodyposition). The height of each element is first divided by two
         //(because the shapes takes half width and height) and then by 3 since there are 3 elements on a player.
         float radious;
         boolean useMiddleBox;
         Vector2 middleBoxSize;
         Vector2 upperCirclePos;
         Vector2 bottomCirclePos;
         if (size.y >= size.x) {
             radious = size.x / 2;
             useMiddleBox = (size.y / size.x > 2);
             //imSize.x/50 is a scalable small number that is substracted from the middlebox to avoid an edge between the circle and the box.
             middleBoxSize = new Vector2(size.x - size.x / 50, size.y - 2 * radious);
             bottomCirclePos = (new Vector2(0, (size.y - radious * 4) / 2 > 0 ?
                                               -((size.y - radious * 4) / 2) - radious :
                                               -((size.y - 2 * radious) / 2)));
             upperCirclePos = (new Vector2(0, (size.y - radious * 4) / 2 > 0 ?
                                              (size.y - radious * 4) / 2 + radious :
                                              (size.y - 2 * radious) / 2));
         } else {
             radious = size.y / 2;
             useMiddleBox = (size.x / size.y > 2);
             //imSize.x/50 is a scalable small number that is substracted from the middlebox to avoid an edge between the circle and the box.
             middleBoxSize = new Vector2(size.y - size.y / 50, size.x - 2 * radious);
             bottomCirclePos = (new Vector2(
                     (size.x - radious * 4) / 2 > 0 ? -((size.x - radious * 4) / 2) - radious : -((size.x - 2 * radious) / 2), 0));
             upperCirclePos = (new Vector2(
                     (size.x - radious * 4) / 2 > 0 ? (size.x - radious * 4) / 2 + radious : ((size.x - 2 * radious) / 2), 0));
         }
         Vector2 bottomSensorPos = new Vector2(0, bottomCirclePos.y - radious);
         Vector2 bottomSensorSize = new Vector2(size.x - size.x / 4, sensorThickness * 2);
         Vector2 leftSensorPos = new Vector2(-size.x / 2 - sensorThickness, 0);
         Vector2 leftSensorSize = new Vector2(sensorThickness, size.y - size.y / 5);
         Vector2 rightSensorPos = new Vector2(size.x / 2 + sensorThickness, 0);
         Vector2 rightSensorSize = new Vector2(sensorThickness, size.y - size.y / 5);

         //Initializing the shapes
         upperCircleShape.setRadius(radious);
         bottomCircleShape.setRadius(radious);
         middleBoxShape.setAsBox(middleBoxSize.x / 2, middleBoxSize.y / 2);
         bottomSensorShape.setAsBox(bottomSensorSize.x / 2, bottomSensorSize.y / 2, bottomSensorPos, 0);
         leftSensorShape.setAsBox(leftSensorSize.x / 2, leftSensorSize.y / 2, leftSensorPos, 0);
         rightSensorShape.setAsBox(rightSensorSize.x / 2, rightSensorSize.y / 2, rightSensorPos, 0);

         //Setting the position of the circles
         upperCircleShape.setPosition(upperCirclePos);
         bottomCircleShape.setPosition(bottomCirclePos);

         Filter filter = new Filter();
         filter.categoryBits = SideScrollGameV2.PLAYER_CATEGORY;
         filter.maskBits = SideScrollGameV2.ENVIROMENT_CATEGORY;

         //Creating the fixture of the body. The concrete part that can be touched (the part that can collide)
         upperCircle.shape = upperCircleShape;
         upperCircle.density = density;
         upperCircle.friction = 0;
         upperCircle.restitution = restitution;
         upperCircle.isSensor = false;
         upperCircle.filter.maskBits = filter.maskBits;
         upperCircle.filter.categoryBits = filter.categoryBits;
         middleBox.shape = middleBoxShape;
         middleBox.density = density;
         middleBox.friction = 0;
         middleBox.restitution = restitution;
         middleBox.isSensor = false;
         middleBox.filter.categoryBits = filter.categoryBits;
         middleBox.filter.maskBits = filter.maskBits;
         bottomCircle.shape = bottomCircleShape;
         bottomCircle.density = density;
         bottomCircle.friction = friction;
         bottomCircle.restitution = restitution;
         bottomCircle.isSensor = false;
         bottomCircle.filter.maskBits = filter.maskBits;
         bottomCircle.filter.categoryBits = filter.categoryBits;
         bottomSensor.shape = bottomSensorShape;
         bottomSensor.isSensor = true;
         bottomSensor.density = 0;
         bottomSensor.friction = 0;
         bottomSensor.filter.categoryBits = filter.categoryBits;
         bottomSensor.filter.maskBits = filter.maskBits;
         leftSensor.shape = leftSensorShape;
         leftSensor.isSensor = true;
         leftSensor.density = 0;
         leftSensor.friction = 0;
         leftSensor.filter.maskBits = filter.maskBits;
         leftSensor.filter.categoryBits = filter.categoryBits;
         rightSensor.shape = rightSensorShape;
         rightSensor.isSensor = true;
         rightSensor.density = 0;
         rightSensor.friction = 0;
         rightSensor.filter.categoryBits = filter.categoryBits;
         rightSensor.filter.maskBits = filter.maskBits;

         //Creating the body using the fixtureDef and the BodyDef created beneath
         BodyDef bodyDef = new BodyDef();
         bodyDef.position.set(position);
         bodyDef.type = BodyType.DynamicBody;
         body = map.createBody(bodyDef);
         if (useMiddleBox) {body.createFixture(middleBox);}
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

        setGroundContactFriction();
    }

	/**
	 * If the player is currently colliding with the ground (something under the player) and is not running then set the friction
     * to something high to prevent sliding. If running then set the friction to 0 so that running can be done.
     */
    private void setGroundContactFriction(){
        if (groundContact != null) {
            if (!isRunning && isGrounded) {
                groundContact.setFriction(100);
                System.out.println("friction");
            } else {
                groundContact.setFriction(0);
                System.out.println("no friction");
            }
        }
    }
    /**
     * The function that draws the object every frame
     * @param batch The SpriteBatch with wich to draw
     */
    @Override
    public void draw(SpriteBatch batch, int layer){
        if (layer == SideScrollGameV2.PLAYER_DRAW_LAYER) {
            sprite.setPosition(body.getPosition().x - (sprite.getWidth() / 2), body.getPosition().y - (sprite.getHeight() / 2));
            sprite.setRotation(SideScrollGameV2.radToDeg(body.getAngle()));
            sprite.draw(batch);
        }
    }

    /**
     * Handles the collision checks of the player. If a sensor collides with something then set the specific sensor
     * collision statis to either true or false. This is used to determine if the player can jump or not (the bottom sensor
     * must collide with something) among other things.
     * @param contact A object containing the two bodies and fixtures that made contact. It also contains collision data
     */
    public void beginContact(Contact contact){
        Body otherBody = body;

        if (contact.getFixtureA().getBody().getUserData().equals(this)) {
            otherBody = contact.getFixtureB().getBody();
            if (contact.getFixtureA().isSensor()) {
                boolean prevIsGrounded = isGrounded;
                sensorSwitch(contact.getFixtureA(), true);
                if (!prevIsGrounded && isGrounded){
                    groundContact = contact;
                }
            }
        }
        else if (contact.getFixtureB().getBody().getUserData().equals(this)){
            otherBody = contact.getFixtureA().getBody();
            if (contact.getFixtureB().isSensor()) {
                sensorSwitch(contact.getFixtureB(), true);
            }
        }

        if (!collidingBodies.contains(otherBody)) {
            collidingBodies.add(otherBody);
        }
    }

    /**
     * Handles the collisionchecks of the player. If a sensor collides with something then set the specific sensor
     * collision statis to either true or false. This is used to determine if the player can jump or not (the bottomsensor
     * must collide with something) among other things.
     * @param contact A object containing the two bodies and fixtures that made contact. It also contains collisiondata
     */
    public void endContact(Contact contact){
        Body otherBody = body;
        if (contact.getFixtureA().getBody().getUserData().equals(this)){
            otherBody = contact.getFixtureB().getBody();
            if (contact.getFixtureA().isSensor()) {
                sensorSwitch(contact.getFixtureA(), false);
            }
        }
        if (contact.getFixtureB().getBody().getUserData().equals(this)){
            otherBody = contact.getFixtureA().getBody();
            if (contact.getFixtureB().isSensor()){
                sensorSwitch(contact.getFixtureB(), false);
            }
        }

        //Removing the object from the list
        collidingBodies.remove(otherBody);
    }

    /**
     * Checks wich sensor has collided and then sets the appropriate flag to the specified value.
     * This is used to determine if the player is on the ground of not (among other things).
     * @param fixture The sensor Fixture that has collided.
     * @param setValue The value to set the flag to (false if collision has ended and true if collision has begun)
     */
    private void sensorSwitch(Fixture fixture, boolean setValue){
        //The cases beneath are the only ones that are going to be sent to this function due to how the body is built up. More might
        //be added later though.
        switch ((Direction) fixture.getUserData()){
            case DOWN :isGrounded = setValue;
                break;
            case LEFT:isCollisionLeft = setValue;
                break;
            case RIGHT:isCollisionRight = setValue;
                break;
        }
    }

    /** Called when a key was pressed
     *
     * @param keycode one of the constants in {@link Keys}
     */
    public void keyDown(int keycode){
        if (isLeftKey && keycode == leftKey){
            isRunning = true;
            direction = Direction.LEFT;
        }
        else if (isRightKey && keycode == rightKey){
            isRunning = true;
            direction = Direction.RIGHT;
        }
        else if (isUpKey && keycode == upKey){
            if(isGrounded) {
                jump();
            }
        }
        //If keycode is interactkey then check if any of the colliding bodies belongs to a interactobject. If so then
        //notify that object that the interaction has started. Can be used for levers, moving rocks osv.
        else if (isInteractKey && keycode == interactKey){
            notifyInteractObjects(true);
        }

    }

    /** Called when a key was released
     *
     * @param keycode one of the constants in {@link Keys}
     */
    public void keyUp (int keycode){
        if ((isLeftKey && keycode == leftKey) || (isRightKey && keycode == rightKey)){
            isRunning = false;
        }
        //If keycode is interactkey then check if any of the colliding bodies belongs to a interactobject. If so then
        //notify that object that the interaction has started. Can be used for levers, moving rocks osv.
        else if (isInteractKey && keycode == interactKey){
            notifyInteractObjects(false);
        }
    }

    /**
     * Checks all of the bodies in the 'collidingBodies list containing the objects that the player is currently colliding with.
     * If any of the bodies userdata contains a 'InteractGameObject' then it notifies that object that it is being
     * interacted with. All of the objects in the game world should implement the interface 'GameObject'. Thus we tre to
     * cast the bodies userdata value to a 'GameObject' then checks if it is of the type 'InteractGameObject'. If one or both
     * of these casts fails, the body is removed from the world.
     * @param startInteract True if the interaction is now starting, otherwise false.
     */
    private void notifyInteractObjects(boolean startInteract){
        //If keycode is interactkey then check if any of the colliding bodies belongs to a interactobject. If so then
        //notify that object that the interaction has started. Can be used for levers, moving rocks osv.
        for (Body body : collidingBodies){
            try{
                GameObject object;
                object = (GameObject) body.getUserData();
                if (object.getTypeOfGameObject() == TypeOfGameObject.INTERACTOBJECT || object.getTypeOfGameObject() == TypeOfGameObject.DOOR){
                    InteractGameObject interactGameObject = (InteractGameObject) object;
                    if (startInteract) {interactGameObject.startInteract(this);}
                    else {interactGameObject.endInteract(this);}
                }
            }
            catch (ClassCastException e){
                System.out.println("Error corrected. Body with wrong type in 'UserData' (either not 'GameObject' or not InteractObject')");
                sideScrollGameV2.getCurrentMap().removeBody(body);
            }
        }
        if (clearCollidingBodies){collidingBodies.clear();}
    }

	/**
	 * Decelerating the player from running to a stop
     */
    @Deprecated
    private void decelerationCheck(){
        if (!isRunning && isGrounded){
            if (body.getLinearVelocity().x > deceleration.x / 4){
                body.applyForceToCenter(-deceleration.x, 0, true);
            }
            else if (body.getLinearVelocity().x < -deceleration.x / 4){
                body.applyForceToCenter(deceleration.x, 0, true);
            }
            else{
                body.setLinearVelocity(0f, body.getLinearVelocity().y);
            }
        }
    }

    /**
     * Handles the acceleration and decelerationCheck of the character
     */
    private void runHandler(){
        //decelerationCheck();
        if (isRunning && isGrounded){
            //Code for smooth acceleration when on the ground
            if (direction == Direction.RIGHT && body.getLinearVelocity().x < maxVelocity.x){
                body.applyForceToCenter(acceleration.x, 0, true);
            }
            else if (direction == Direction.LEFT && body.getLinearVelocity().x > -maxVelocity.x){
                body.applyForceToCenter(-acceleration.x, 0, true);
            }
        }
        else if (isRunning && !isGrounded){
            //Code for smooth acceleration when in the air
            if (direction == Direction.RIGHT && body.getLinearVelocity().x < maxVelocity.x){
                body.applyForceToCenter(acceleration.x / 10, 0, true);
            }
            else if (direction == Direction.LEFT && body.getLinearVelocity().x > -maxVelocity.x){
                body.applyForceToCenter(-acceleration.x / 10, 0, true);
            }
        }
    }

    /**
     * Function that makes the player jump
     */
    private void jump(){
        //Impuls is calculated by F = m * g (Force = mass * gravity) where g = 9.82
        Vector2 impuls = new Vector2(0, acceleration.y);
        body.applyLinearImpulse(impuls, body.getLocalCenter(), true);
    }


    public long getId(){return id;}

    public TypeOfGameObject getTypeOfGameObject(){return TypeOfGameObject.PLAYER;}

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
            return gameObject.getId() == id;
        }
        catch (Exception e){
            return false;
        }
    }

    public Body getBody(){return body;}

    public boolean isRunning() {return isRunning;}

    public void setIsRunning(final boolean running) {isRunning = running;}

    public int getInteractKey() {return interactKey;}

    public void setInteractKey(final int interactKey) {this.interactKey = interactKey;}

    public int getLeftKey() {return leftKey;}

    public void setLeftKey(final int leftKey) {this.leftKey = leftKey;}

    public int getRightKey() {return rightKey;}

    public void setRightKey(final int rightKey) {this.rightKey = rightKey;}

    public int getUpKey() {return upKey;}

    public void setUpKey(final int upKey) {this.upKey = upKey;}

    public void setIsUpKey(final boolean upKey) {this.isUpKey = upKey;}

    public void setIsRightKey(final boolean rightKey) {this.isRightKey = rightKey;}

    public void setIsLeftKey(final boolean leftKey) {this.isLeftKey = leftKey;}

    public void setIsInteractKey(final boolean interactKey) {this.isInteractKey = interactKey;}
}
