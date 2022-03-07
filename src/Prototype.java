	import java.io.File;
	import java.io.IOException;
	import java.util.ArrayList;
	import java.util.Collections;
	import java.util.Set;
	import java.util.TreeMap;
	import fr.unistra.pelican.Image;
	import fr.unistra.pelican.algorithms.io.ImageLoader;

public class Prototype {

		public static void main(String[] args) throws IOException {
			//Viewer2D.exec(imgDebruite);
        	Image imgFiltree = filtreMedian(ImageLoader.exec("C:\\Users\\Louise\\Desktop\\DUT info\\broad\\0001.png"));
        	double[][] histo1 = histogrammeNormalisation(histogrammeDiscretion(histogrammeImage(imgFiltree), imgFiltree), imgFiltree.getXDim() * imgFiltree.getYDim());
			recherche(imgFiltree.getName(), histo1);
		}
		
		public static Image chargerImage(String s) {
			Image test= ImageLoader.exec(s);
			return test;
		} //va charger une image pour un string donné
		
		public static Image filtreMedian(Image imgCouleur) {
			for (int i = 1; i < imgCouleur.getXDim()-2; i++) {
				for (int j = 1; j < imgCouleur.getYDim()-2;j++) {
					// 3 listes pour les 3 canaux :
					ArrayList<Integer> valeursR = new ArrayList<Integer>();
					ArrayList<Integer> valeursG = new ArrayList<Integer>();
					ArrayList<Integer> valeursB = new ArrayList<Integer>();
					for (int a= i-1; a<= i+1; a++) {
						for (int k = j-1; k<= j+1; k++) {
							valeursR.add(imgCouleur.getPixelXYBByte(a, k, 0));
							valeursG.add(imgCouleur.getPixelXYBByte(a, k, 1));
							valeursB.add(imgCouleur.getPixelXYBByte(a, k, 2));
						}
					}
					//tri des 3 canaux :
					Collections.sort(valeursR);
					Collections.sort(valeursG);
					Collections.sort(valeursB);
					imgCouleur.setPixelXYBByte(i, j, 0, valeursR.get(4));
					imgCouleur.setPixelXYBByte(i, j, 1, valeursG.get(4));
					imgCouleur.setPixelXYBByte(i, j, 2, valeursB.get(4));
				}
			}
		return imgCouleur;
		}
		
		
		public static double[][] histogrammeImage(Image img) throws IOException {
			double[][] tab = new double[3][256];
			for (int i = 0; i < img.getXDim()-1 ; i++) {
				for (int j = 0; j < img.getYDim()-1 ;j++) {
					int r = img.getPixelXYBByte(i, j, 0);
					tab[0][r] += 1;
					int g = img.getPixelXYBByte(i, j, 1);
					tab[1][g] += 1;
					int b = img.getPixelXYBByte(i, j, 2);
					tab[2][b] += 1;
				}
			}
			return tab;
		}
		
		
		public static double[][] histogrammeDiscretion(double[][] tab, Image img) throws IOException {
			double[][] tabDiv = new double[3][(tab[0].length)/2];
			int cpt = 0;
			double somme = 0;
			  for (int i = 0; i<256 ;i=i+2){
				  somme=0;
				  for (int j = 0; j<2;j++){
					  somme = somme + tab[0][i + j];
				  }
				  tabDiv[0][cpt] = somme;
				  cpt++;
			  }
			return tabDiv;
		}
			
		public static double[][] histogrammeNormalisation(double[][] histogramme, int nbPixel) throws IOException {
			double[][] nouvelHisto = new double[3][histogramme[1].length];
			for(int i=0;i<histogramme.length;i++) {
				for(int j=0;j<histogramme[i].length;j++) {
					nouvelHisto[i][j] = (histogramme[i][j]/nbPixel); 
				}
			}
			return nouvelHisto;
		}
		
		
		public static void recherche(String imgComparee, double[][] histo1) throws IOException {
			TreeMap<String, Double> map = new TreeMap<String,Double>();
			String url = "C:\\Users\\Louise\\Desktop\\DUT info\\broad\\";
			File dir  = new File(url);
	        File[] liste = dir.listFiles();
	        	for(File item : liste){ //parcourt du fichier
	        		if(item.isFile()) {
		                   if(!item.getName().equals(imgComparee)) {
		    		            Image imgFiltree2 = filtreMedian(ImageLoader.exec(url + imgComparee));
		    		            double[][] histo2 = histogrammeNormalisation(histogrammeDiscretion(histogrammeImage(imgFiltree2), imgFiltree2), imgFiltree2.getXDim() * imgFiltree2.getYDim());
		    		            map.put(item.getName(), calculSimilarite(histo1,histo2));
		                    }
		            	}
	        		}
	        	System.out.println("Lowest Entry is: " + map.firstEntry()); 
		}
		
		
		public static double calculSimilarite(double[][] histo1, double[][] histo2) {
			double calcul = 0;
			for (int i=1; i<128; i++) {
				calcul += (Math.pow((histo1[0][i] - histo2[0][i]), 2) + Math.pow((histo1[1][i] - histo2[1][i]), 2) + Math.pow((histo1[2][i] - histo2[2][i]), 2));
			}
			return Math.sqrt(calcul);
		}
}
 


