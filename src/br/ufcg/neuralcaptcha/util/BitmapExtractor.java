package br.ufcg.neuralcaptcha.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import br.ufcg.neuralcaptcha.core.NeuralCaptcha;


public class BitmapExtractor {

	/**
     * Converte em um array de inteiros o bitmap da imagem passada como par�metro.
     * O array cont�m apenas 0s (pixels brancos da imagem) e 1s (pixels pretos da imagem).
     * 
     * A imagem deve ser um bitmap monocrom�tico 1bpp.
     * @param img A imagem de que se deseja extrair o bitmap
     * @return Um array de inteiros contendo o bitmap da imagem.
     */
    public static int[] extraiBitmap(BufferedImage img) {
		int[] array = new int[NeuralCaptcha.TAMANHO_CARACTERE_W * NeuralCaptcha.TAMANHO_CARACTERE_H];
		int count = 0;
		for(int y = 0; y < NeuralCaptcha.TAMANHO_CARACTERE_H; y++){
			for ( int x=0;x< NeuralCaptcha.TAMANHO_CARACTERE_W; x++ ) {
				if (img.getRGB(x, y)== java.awt.Color.BLACK.getRGB()) {
					array[count] = 1;
				} else {
					array[count] = 0;
				}
				count++;
			}
		}
		return array;
	}

    /**
     * Converte em um array de inteiros o bitmap da imagem obtida no path passado como par�metro.
     * O array cont�m apenas 0s (pixels brancos da imagem) e 1s (pixels pretos da imagem).
     *
     * A imagem deve ser um bitmap monocrom�tico 1bpp.
     * @param pathImg O path para a imagem de que se deseja extrair o bitmap
     * @return Um array de inteiros contendo o bitmap da imagem.
     */
    public static double[] extraiBitmap(String pathImg) throws IOException {
    	BufferedImage img = ImageIO.read(new File(pathImg));
        
        double[] array = new double[NeuralCaptcha.TAMANHO_CARACTERE_W * NeuralCaptcha.TAMANHO_CARACTERE_H];

		int count = 0;
		for(int y = 0; y < NeuralCaptcha.TAMANHO_CARACTERE_H; y++){
			for ( int x=0; x < NeuralCaptcha.TAMANHO_CARACTERE_W; x++ ) {
				if (img.getRGB(x, y)== java.awt.Color.BLACK.getRGB()) {
					array[count] = 1.0;
				} else {
					array[count] = -1.0;
				}
				count++;
			}
		}
		return array;
	}
    
}
