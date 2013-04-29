package model.notice;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfWriter;

public class GeneratePdf {
	
	private ArrayList<NoticeImage> arrayImages;
	
	/**
	 * Constructeur.
	 * @param arrayImages liste d'images a inserer dans le pdf.
	 */
	public GeneratePdf(ArrayList<NoticeImage> arrayImages){
		this.arrayImages = arrayImages;
	}
	
	/**
	 * Retourne la liste des images.
	 * @return la liste des images.
	 */
	public ArrayList<NoticeImage> getArrayImages() {
		return arrayImages;
	}
	
	/**
	 * Modifie la liste des images.
	 * @param arrayImages nouvelle liste d'images
	 */
	public void setArrayImages(ArrayList<NoticeImage> arrayImages) {
		this.arrayImages = arrayImages;
	}
	
	/**
	 * Genere le fichier pdf.
	 * @throws DocumentException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public void generate() throws DocumentException, MalformedURLException, 
				IOException{
		Document document = new Document();
		PdfWriter.getInstance(document, 
				new FileOutputStream("Fichier.pdf"));
		document.open();
		
		// recuperations des images et ecriture dans le fichier pdf
		for(NoticeImage noticeImage : arrayImages){
			Image image = Image.getInstance(noticeImage.getPath());
			image.scaleAbsolute(300, 300);
			document.add(image);
			document.add(new Phrase("\n"));
			document.add(new Phrase(noticeImage.getComment()));
			document.add(new Phrase("\n"));
		}
		document.close();
	}
}
