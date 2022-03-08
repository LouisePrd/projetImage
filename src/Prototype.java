	import java.io.BufferedWriter;
	import java.io.File;
	import java.io.FileWriter;
	import java.io.IOException;
	import java.util.ArrayList;
	import java.util.Collections;
	import java.util.Comparator;
	import java.util.HashMap;
	import java.util.LinkedHashMap;
	import java.util.LinkedList;
	import java.util.List;
	import java.util.Map;
	import java.util.Map.Entry;
	import java.util.Scanner;
	import fr.unistra.pelican.Image;
	import fr.unistra.pelican.algorithms.io.ImageLoader;

public class Prototype {

		public static void main(String[] args) throws IOException {
			
        	Image imgFiltree = filtreMedian(ImageLoader.exec("C:\\Users\\Louise\\Desktop\\DUT info\\broad\\0003.png"));
        	double[][] histo1 = histogrammeNormalisation(histogrammeDiscretion(histogrammeImage(imgFiltree), imgFiltree), imgFiltree.getXDim() * imgFiltree.getYDim());
        	
        	//PARTIE 1
			//recherche("C:\\Users\\Louise\\Desktop\\DUT info\\broad\\", "0003.png" , histo1);
			
			//PARTIE 2
			//preTraitement("C:\\Users\\Louise\\Desktop\\DUT info\\broad\\");
			//rechercheV2(histo1);
			
			//PARTIE 3
        	//histogrammeHSV(imgFiltree);
		}
		
		
		
		//PARTIE 1
		
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
		
		
		public static void recherche(String url, String imgComparee, double[][] histo1) throws IOException {
			Map<String, Double> map = new HashMap<String, Double>();
			File dir  = new File(url);
	        	for(File item : dir.listFiles()){ //parcourt du fichier
	        		if(!item.getName().equals(imgComparee)) {
	        			Image imgFiltree2 = filtreMedian(ImageLoader.exec(url + item.getName()));
		    		    double[][] histo2 = histogrammeNormalisation(histogrammeDiscretion(histogrammeImage(imgFiltree2), imgFiltree2), imgFiltree2.getXDim() * imgFiltree2.getYDim());
		    		    map.put(item.getName(), calculSimilarite(histo1,histo2));
		             }
		        }
	        	Map<String, Double> mapSort = sortByValue(map);
	        	List keys = new ArrayList(mapSort.keySet());
	        	for (int i = 0; i < 10; i++) {
	        	    System.out.println(keys.get(i));
	        	} //affiche les noms des 10 images les + similaires
		}
		
		
		public static double calculSimilarite(double[][] histo1, double[][] histo2) {
			double calcul = 0;
			for (int i=1; i<128; i++) {
				calcul += (Math.pow((histo1[0][i] - histo2[0][i]), 2) + Math.pow((histo1[1][i] - histo2[1][i]), 2) + Math.pow((histo1[2][i] - histo2[2][i]), 2));
			}
			return Math.sqrt(calcul);
		}
		
		//tri de la map par ordre croissant
		 public static HashMap<String, Double> sortByValue(Map<String, Double> map) {
		        List<Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double> >(map.entrySet());
		 
		        Collections.sort(list, new Comparator<Map.Entry<String, Double> >() {
		            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
		                return (o1.getValue()).compareTo(o2.getValue());
		            }
		        });
		         
		        HashMap<String, Double> temp = new LinkedHashMap<String, Double>();
		        	for (Entry<String, Double> aa : list) {
		        		temp.put(aa.getKey(), aa.getValue());
		        	}
		        return temp;
		}
		 
		 
		 
		 //PARTIE 2
		 
		 //Fonction de prétraitement qui va écrire dans un nv txt l'histogramme
		 public static void preTraitement(String url) throws IOException {
			 File dir  = new File(url);
			 int numPhoto = 0;
	        	for(File item : dir.listFiles()){ //parcourt du fichier
	        		Image imgFiltree = filtreMedian(ImageLoader.exec(url + item.getName()));
	        		double[][] histo1 = histogrammeNormalisation(histogrammeDiscretion(histogrammeImage(imgFiltree), imgFiltree), imgFiltree.getXDim() * imgFiltree.getYDim());
	        		FileWriter fw = new FileWriter("C:\\Users\\Louise\\Desktop\\DUT info\\BD\\" + numPhoto++ + ".txt");
	        		BufferedWriter bw = new BufferedWriter(fw);
	        		for (int i=1; i<128; i++) {
	    				double val1 = histo1[0][i];
	    				String v = Double.toString(val1);
	    				bw.write(v + "\n");
	        		}
	        		bw.close();
	        	}
	        	System.out.println("fin du prétraitement");	
		 }
		 

		 //fonction de recherche qui va utiliser les txt créés précedemment
		 //problème : ne fonctionne que sur 1 histogramme donc distance euclidienne pas correcte
		 public static void rechercheV2(double[][] histo1) throws IOException {
				double calcul = 0;
			 	Map<String, Double> map = new HashMap<String, Double>();
				String url = "C:\\Users\\Louise\\Desktop\\DUT info\\BD\\";
				File dir  = new File(url);
		        	for(File item : dir.listFiles()){ //parcourt du fichier
		        		int index = 0;
		        		Scanner obj = new Scanner(item);
		        		while (obj.hasNextLine()) {
		        			++index;
		        			String val = obj.nextLine();
		        			double doubleValue = Double.parseDouble(val);
							calcul += (Math.pow((histo1[0][index] - doubleValue), 2));
							map.put(item.getName(), calcul);
		        		}
		        	}
		        
		        //puis tri des valeurs comme pour la partie 1
		        Map<String, Double> mapSort = sortByValue(map);
		        List keys = new ArrayList(mapSort.keySet());
		        for (int i = 0; i < 10; i++) {
		        	System.out.println(keys.get(i));
		        }	
		 }
		 
		 
		//PARTIE 3
		
		//On souhaite reprendre la partie 1 avec cette fois des histogrammes avec des HSV
		public static void histogrammeHSV(Image img) throws IOException {

				double[][] tabH = new double[1][360];
				double[][] tabVS = new double[2][1];
				
				for (int i = 0; i < img.getXDim()-1 ; i++) {
					for (int j = 0; j < img.getYDim()-1 ;j++) {
						
						//on récupère les valeurs de r g et b pour chaque pixel
						int r = img.getPixelXYBByte(i, j, 0);
						int g = img.getPixelXYBByte(i, j, 1);
						int b = img.getPixelXYBByte(i, j, 2);
						
						//on cherche M tel que M = max{R, G, B}
						double[] tabMax = {r, g, b};
						double max = 0;
					    	for (int k=0 ; k< 3 ; k++){
					    		if(tabMax[k]>max){
					    			max=tabMax[k];}
					    	}
					   //Et m tel que m = min{R, G, B}
					   double min = 0;
					   		for (int k=0 ; k< 3 ; k++){
					   			if(tabMax[k]<max){
					   				max=tabMax[k];}
						    }
					   		
					  //Définition de V et S en fonction des équations
					  double V = max/255;
					  if(max>0) {
						  double S = 1-(min/max);
						  tabVS[2][(int) S]  += 1;
					  }  
					  else {
						  double S = 1-(min/max);
						  tabVS[2][(int) S]  += 1;
					  }
					  
					  //on remplit le premier histogramme avec V
					  tabVS[1][(int) V] += 1;
					  
					  
					  //on a S et V, maintenant on calcule H
					  if (g >= b) {
						  double H = Math.sqrt(Math.pow(r,2) + Math.pow(g,2) + Math.pow(b,2) - r*g - r*b - g*b);
					  }
					  else {
						  
					  }
					  
					}
				}
	}
}