package model.notice;

/**
 * Classe NoticeImage qui represente une image d'une notice.
 * @author Groupe C M1GIL 2013.
 *
 */
public class NoticeImage {
	/**
	 * Chemin de l'image.
	 */
	private String path;
	/**
	 * Commentaire sur l'image.
	 */
	private String comment;
	
	private boolean isSelected;
	
	/**
	 * Constructeur.
	 * @param path chemin de l'image.
	 */
	public NoticeImage(String path){
		this.path = path;
		this.comment = "";
		this.isSelected = true;
	}
	
	/**
	 * Retourne le chemin de l'image.
	 * @return le chemin de l'image.
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * Modifie le chemin de l'image.
	 * @param path nouveau chemin de l'image.
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
	/**
	 * Retourne le commentaire sur l'image.
	 * @return le commentaire sur l'image.
	 */
	public String getComment() {
		return comment;
	}
	
	/**
	 * Modifie le commentaire sur l'image.
	 * @param comment nouveau commentaire sur l'image.
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	/**
	 * Retourne true si l'image a ete coche, faux sinon.
	 * @return true si l'image a ete coche, faux sinon.
	 */
	public boolean isSelected() {
		return isSelected;
	}
	
	/**
	 * Modifie la valeur du checkbox de l'image.
	 * @param isSelected nouvelle valeur du checkbox de l'image.
	 */
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	
}
