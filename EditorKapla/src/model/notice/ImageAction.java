package model.notice;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ImageAction extends JLabel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Boite de dialogue de commentaire.
	 */
	private CommentDefinition comment;
	/**
	 * Image.
	 */
	private NoticeImage noticeImage;
	/**
	 * Checkbox qui permet d'inclure l'image dans la notice.
	 */
	private JCheckBox check;

	public ImageAction(final JFrame owner, final NoticeImage noticeImage){
		super(new ImageIcon(noticeImage.getPath()));
		this.noticeImage = noticeImage;
		
		// redimensionnement de l'image
		try {
			BufferedImage originalImage = ImageIO.read(new File(
					noticeImage.getPath()));
			BufferedImage resizeImage = resizeImage(
					originalImage, BufferedImage.TYPE_INT_ARGB, 171, 150);
			ImageIcon newImageIcon = new ImageIcon(resizeImage);
			this.setIcon(newImageIcon);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// action sur l'image
		this.addMouseListener(new MouseListener(){
			
			
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				comment = new CommentDefinition(owner, ImageAction.this);
				comment.setVisible(true);
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				setBorder(null);
			}

			@Override
			public void mousePressed(MouseEvent arg0) {}

			@Override
			public void mouseReleased(MouseEvent arg0) {}

		});

		// checkbox check
		check = new JCheckBox();
		check.setBackground(Color.DARK_GRAY);
		check.setSelected(true);
		check.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				noticeImage.setSelected(check.isSelected());
			}
		});
	}

	/**
	 * Retourne l'image.
	 * @return l'image.
	 */
	public NoticeImage getNoticeImage(){
		return noticeImage;
	}
	
	/**
	 * Retourne le checkbox check.
	 * @return le checkbox check.
	 */
	public JCheckBox getCheck(){
		return check;
	}
	
	/**
	 * Redimensionne une image. 
	 * @param originalImage l'image d'origine
	 * @param type type des variables width et height
	 * @param width largeur de l'image
	 * @param height hauteur de l'image
	 * @return une image redimensionnee.
	 */
	private static BufferedImage resizeImage(BufferedImage originalImage, 
			int type, int width, int height){

		BufferedImage resizedImage = new BufferedImage(width, height, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, width, height, null);
		g.dispose();

		return resizedImage;	
	}
}
