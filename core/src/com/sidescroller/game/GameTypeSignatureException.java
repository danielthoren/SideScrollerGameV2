package com.sidescroller.game;

import com.badlogic.gdx.physics.box2d.Body;

/**
 * Exception thrown when the signature ('TypeOfGameObject') does not correspond to the actual object, thus causing
 * an exception when casting.
 */
public class GameTypeSignatureException extends RuntimeException
{
    private Object objectToBlame;

    public GameTypeSignatureException(String message){super(message);}
    public GameTypeSignatureException(String messege, Throwable cause){super(messege, cause);}
    public GameTypeSignatureException(String message, Exception e){super(message, e.getCause());}
    public GameTypeSignatureException(String message, Exception e, Object objectToBlame){
	super(message, e.getCause());
	this.objectToBlame = objectToBlame;
    }

    /**
     * Returns the object that caused the exception. The object should be a 'GameOBject' but this can not be ensured.
     * @return The object to blame for the exception.
     */
    public Object getObjectToBlame(){
	return objectToBlame;
    }
}
