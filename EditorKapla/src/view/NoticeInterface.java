package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import java.awt.Dimension;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


import model.notice.ImageActions;
import model.notice.NoticeImage;

/**
 * Classe NoticeInterface qui represente l'interface de la notice.
 * @author Groupe C M1GIL 2013.
 *
 */
public class NoticeInterface extends JFrame{

	/**
	 * Chemin du dossier des images.
	 */
	private String imagesPath;
	/**
	 * Panel des images.
	 */
	private JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 
			PADDING, PADDING));
	/**
	 * Nombre d'images.
	 */
	private int countImages = 0;
	/**
	 * Liste contenant les images.
	 */
	private ArrayList<NoticeImage> arrayImages = new ArrayList<NoticeImage>();

	/**
	 * Padding entre les images.
	 */
	private static final int PADDING = 15;
	/**
	 * Taille des images.
	 */
	private static int WIDTH = 150;
	private static int HEIGHT = 150;

	public NoticeInterface(){
		super("New Notice");
		setLayout(new BorderLayout());


		// panel de choix du dossier des images
		JPanel northPanel = new JPanel(new BorderLayout());

		// champ de texte qui contient le chemin du repertoire des images
		final JTextField folderPath = new JTextField();
		folderPath.setEditable(false);

		// bouton de choix du dossier des images
		JButton chooseFolder = new JButton("Choose Folder");
		chooseFolder.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				//chooser.setDialogTitle(choosertitle);
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					folderPath.setText(chooser.getSelectedFile().toString());
					imagesPath = chooser.getSelectedFile().toString();

					// recuperation des images du dossier
					getFiles(imagesPath);
				}

				// mise a jour du panel des images
				centerPanel.removeAll();
				for(NoticeImage noticeImage : arrayImages){
					try {
						BufferedImage originalImage = ImageIO.read(new File(
								noticeImage.getPath()));
						BufferedImage resizeImage = resizeImage(
								originalImage, BufferedImage.TYPE_INT_ARGB);
						ImageIcon newImageIcon = new ImageIcon(resizeImage);

						JLabel tempLabel = new JLabel(newImageIcon);
						JPanel tempPanel = new JPanel(new BorderLayout());
						tempPanel.add(tempLabel);
						tempPanel.add(new ImageActions(NoticeInterface.this, 
								noticeImage), BorderLayout.SOUTH);
						
						centerPanel.add(tempPanel);
						
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				centerPanel.repaint();
				centerPanel.validate();
				add(centerPanel);
			}

		});
		
		JPanel southPanel = new JPanel(new BorderLayout());
		JButton generatePdf = new JButton("Generer Pdf");
		generatePdf.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				for(NoticeImage noticeImage : arrayImages){
					System.out.println(noticeImage.getPath() + " => " 
							+ noticeImage.getComment());
				}
			}
		});
		southPanel.add(new JLabel());
		southPanel.add(generatePdf, BorderLayout.EAST);

		// ajout des composants du northPanel
		northPanel.add(folderPath);
		northPanel.add(chooseFolder, BorderLayout.EAST);

		// ajout des panels
		add(northPanel, BorderLayout.NORTH);
		add(centerPanel);
		add(southPanel, BorderLayout.SOUTH);

		pack();
		setVisible(true);
	}

	/**
	 * Liste les fichiers d'un dossier.
	 * @param folder dossier dont il faut lister les fichiers.
	 */
	private void getFiles(String folder) {
		File file = new File(folder);
		File[] files = file.listFiles();

		arrayImages.clear();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if(files[i].isDirectory() == false) {
					NoticeImage noticeImage = new NoticeImage(imagesPath +"\\"
							+files[i].getName());

					arrayImages.add(noticeImage);
					countImages++;
				}
			}
		}
	}	

	/**
	 * Redimensionne une image. 
	 * @param originalImage l'image d'origine
	 * @param type type des variables WIDTH et HEIGHT
	 * @return une image redimensionnee.
	 */
	private static BufferedImage resizeImage(BufferedImage originalImage, 
			int type){
		
		BufferedImage resizedImage = new BufferedImage(WIDTH, HEIGHT, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, WIDTH, HEIGHT, null);
		g.dispose();

		return resizedImage;	
	}

}




