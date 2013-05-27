package model.geomitries;

import java.io.Serializable;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

/**
 * Classe BrickProperties qui contient des proprietes de la brique.
 * @author Groupe C M1 GIL.
 */
@SuppressWarnings("serial")
public class BrickProperties implements Serializable{
	/**
	 * Identifiant du brique.
	 */
	private int id;
	/**
	 * Position de la brique.
	 */
	private Vector3f position;
	/**
	 * Couleur de la brique.
	 */
	private ColorRGBA color;
	
	/**
	 * Initialise une brique avec l'identifiant, la position et la couleur.
	 * @param id identifiant de la brique.
	 * @param position position de la brique.
	 * @param color couleur de la brique.
	 */
	public BrickProperties(int id, Vector3f position, ColorRGBA color){
		this.id = id;
		this.position = position;
		this.color = color;
	}

	/**
	 * Retourne l'identifiant de la brique.
	 * @return l'identifiant de la brique.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Modifie l'identifiant de la brique.
	 * @param id nouvel identifiant de la brique.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Retourne la position de la brique.
	 * @return la position de la brique.
	 */
	public Vector3f getPosition() {
		return position;
	}

	/**
	 * Modifie la position de la brique.
	 * @param position nouvelle position de la brique.
	 */
	public void setPosition(Vector3f position) {
		this.position = position;
	}
	
	/**
	 * Retourne la couleur de la brique.
	 * @return la couleur de la brique.
	 */
	public ColorRGBA getColor() {
		return color;
	}

	/**
	 * Modifie la couleur de la brique.
	 * @param color nouvelle couleur de la brique.
	 */
	public void setColor(ColorRGBA color) {
		this.color = color;
	}
}