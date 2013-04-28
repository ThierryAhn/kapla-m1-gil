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
	
	/**
	 * Constructeur.
	 * @param path chemin de l'image.
	 */
	public NoticeImage(String path){
		this.path = path;
		this.comment = "";
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
	
}
