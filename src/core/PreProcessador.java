package core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;

import javax.imageio.ImageIO;

public class PreProcessador {

	public static int[][] processaImagem(String path) throws IOException, InterruptedException{
        // Este é o captcha original
        BufferedImage img = ImageIO.read(new File(path));

        // Remove excesso da imagem
        img = removeParteInutil(img);
        // Diminui a imagem
        img = downSample(img);

        // Converte para bitmap 1bpp
        img = converteParaBitmap(img);

        // Recorta os caracteres
        BufferedImage caracteres[] = recorta(img);

        // A entrada da rede será um array de 5 linhas, cada linha contendo o bitmap de cada caracteres
        int[][] entradaDaRede =
            new int[NeuralCaptcha.TAMANHO_CAPTCHA][NeuralCaptcha.TAMANHO_CARACTERE_W * NeuralCaptcha.TAMANHO_CARACTERE_H];
        for (int i = 0; i < NeuralCaptcha.TAMANHO_CAPTCHA; i++){
            entradaDaRede[i] = Util.extraiBitmap(caracteres[i]);
        }
        
		return entradaDaRede;
	}

    /**
     * Remove a linha inferior do captcha, que contém uma mensagem do site.
     * @param img A imagem original do captcha
     * @return O captcha sem a linha inferior
     */
    private static BufferedImage removeParteInutil(BufferedImage img){
        return null;
    }

    /**
     * Realiza a diminuição da imagem para o tamanho padronizado que será usado pela rede.
     * @param img A imagem a ser diminuída
     * @return A imagem no novo tamanho após o downsample
     */
    private static BufferedImage downSample(BufferedImage img){
        return null;
    }

    /**
     * Converte uma imagem para bitmap monocromático 1bpp. Cada pixel da imagem será ou branco ou preto.
     * @param img A imagem a ser convertida
     * @return A imagem já convertida no novo formato
     */
    private static BufferedImage converteParaBitmap(BufferedImage img){
        return null;
    }

    /**
     * Recorta os caracteres do captcha uniformemente e os retorna em imagens separadas.
     * @param img A imagem do captcha a ser recortada
     * @return As imagens individuais contendo cada caractere
     */
    private static BufferedImage[] recorta(BufferedImage img){
        return null;
    }
}
