package model.notice;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;


/**
 * Classe CommentDefinition qui represente la fenetre de commentaire.
 * @author Groupe C M1GIL 2013.
 *
 */
@SuppressWarnings("serial")
public class CommentDefinition extends JDialog{
	
	private JTextArea comment = new JTextArea("Tapez votre commentaire ici");
	
	/**
	 * Constructeur.
	 * @param owner frame parent.
	 */
	public CommentDefinition(JFrame owner, final NoticeImage noticeImage){
		
		super(owner, "Comment", true);
		setLayout(new BorderLayout());
		
		// centrer
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - 
	    		this.getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - 
	    		this.getHeight()) / 2);
	    setLocation(x, y);
		
		
		JPanel buttonPanel = new JPanel(new BorderLayout());
		// bouton annuler
		JButton annuler = new JButton("Annuler");
		annuler.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		
		// bouton de validation du commentaire
		JButton valider = new JButton("Valider");
		valider.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				noticeImage.setComment(comment.getText());
				dispose();
			}
		});
		
		// ajout des boutons au panel
		buttonPanel.add(annuler, BorderLayout.WEST);
		buttonPanel.add(new JLabel());
		buttonPanel.add(valider, BorderLayout.EAST);
		
		// ajout des composants.
		add(comment);
		add(buttonPanel, BorderLayout.SOUTH);
		
		
		pack();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}
}
