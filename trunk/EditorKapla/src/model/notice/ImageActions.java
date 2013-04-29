package model.notice;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


/**
 * Classe ImageActions qui permet d'effectuer differentes actions sur les images.
 * @author Groupe C M1GIL 2013.
 *
 */
public class ImageActions extends JPanel{
	
	/**
	 * Constructeur.
	 * @param noticeImage image sur lequel il faut appliquer les actions.
	 * @param owner frame parent.
	 */
	public ImageActions(final JFrame owner, final NoticeImage noticeImage){
		setLayout(new GridLayout(1,2));
		
		// bouton pour commenter
		JButton comment = new JButton("Comment");
		comment.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				new CommentDefinition(owner, noticeImage);
			}
			
		});
		
		
		
		// bouton pour zoomer
		JButton zoom = new JButton("Agrandir");
		
		
		// ajout des composants
		add(zoom); add(comment);
	}
}
