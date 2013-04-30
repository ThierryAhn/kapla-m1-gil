package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.awt.Dimension;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import com.itextpdf.text.DocumentException;
import model.notice.GeneratePdf;
import model.notice.ImageActions;
import model.notice.NoticeImage;

/**
 * Classe NoticeInterface qui represente l'interface de la notice.
 * @author Groupe C M1GIL 2013.
 *
 */
@SuppressWarnings("serial")
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
		setSize(600, 600);
		
		// centrer la fenetre
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
		setLocation(x, y);

		// panel de choix du dossier des images
		JPanel northPanel = new JPanel(new BorderLayout());

		// champ de texte qui contient le chemin du repertoire des images
		final JTextField folderPath = new JTextField();
		folderPath.setEditable(false);

		// bouton de choix du dossier des images
		JButton chooseFolder = new JButton("Choose Folder");
		
		// design bouton
		chooseFolder.setBackground(new Color(128, 15, 1));
		chooseFolder.setForeground(Color.BLACK);
		//chooseFolder.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		
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
		// design bouton
		generatePdf.setBackground(new Color(128, 15, 1));
		generatePdf.setForeground(Color.BLACK);
		// action bouton
		generatePdf.addActionListener(new ActionListener(){
			@SuppressWarnings("unused")
			public void actionPerformed(ActionEvent e) {
				for(NoticeImage noticeImage : arrayImages){
					GeneratePdf pdf = new GeneratePdf(arrayImages);
					try {
						pdf.generate();
					} catch (MalformedURLException e1) {
						e1.printStackTrace();
					} catch (DocumentException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
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




