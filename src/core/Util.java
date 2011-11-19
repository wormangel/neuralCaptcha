package core;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Util {

    // Strings representando a saída desejada da rede para cada caractere
    // São 31 neurônios de saída, um para cada caractere possível, já levando em conta as exclusões (ver dadosAmostras.txt)
    private static String SAIDA_ESPERADA_A = "1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
	private static String SAIDA_ESPERADA_B = "0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
	private static String SAIDA_ESPERADA_C = "0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
	private static String SAIDA_ESPERADA_D = "0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
    private static String SAIDA_ESPERADA_E = "0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
	private static String SAIDA_ESPERADA_F = "0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
	private static String SAIDA_ESPERADA_G = "0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
    private static String SAIDA_ESPERADA_H = "0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
	//private static String SAIDA_ESPERADA_I = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
    private static String SAIDA_ESPERADA_J = "0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
	private static String SAIDA_ESPERADA_K = "0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
	private static String SAIDA_ESPERADA_L = "0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
	private static String SAIDA_ESPERADA_M = "0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
	private static String SAIDA_ESPERADA_N = "0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
	//private static String SAIDA_ESPERADA_O = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
	private static String SAIDA_ESPERADA_P = "0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
    private static String SAIDA_ESPERADA_Q = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
	private static String SAIDA_ESPERADA_R = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
    private static String SAIDA_ESPERADA_S = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
    private static String SAIDA_ESPERADA_T = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0";
	private static String SAIDA_ESPERADA_U = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0";
    //private static String SAIDA_ESPERADA_V = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
    private static String SAIDA_ESPERADA_W = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0";
    private static String SAIDA_ESPERADA_X = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0";
	private static String SAIDA_ESPERADA_Y = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0";
	private static String SAIDA_ESPERADA_Z = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0";

	//private static String SAIDA_ESPERADA_0 = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
	//private static String SAIDA_ESPERADA_1 = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
	private static String SAIDA_ESPERADA_2 = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0";
	private static String SAIDA_ESPERADA_3 = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0";
	private static String SAIDA_ESPERADA_4 = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0";
	private static String SAIDA_ESPERADA_5 = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0";
	private static String SAIDA_ESPERADA_6 = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0";
	private static String SAIDA_ESPERADA_7 = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0";
	private static String SAIDA_ESPERADA_8 = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0";
	private static String SAIDA_ESPERADA_9 = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1";

    /**
     * Mapa interno que associa os caracteres identificáveis com as saídas desejadas da RNA
     */
    private static final Map<String, String> SAIDAS_DESEJADAS;
    static {
        Map<String, String> tmp = new HashMap<String, String>();
        tmp.put("A", SAIDA_ESPERADA_A);
        tmp.put("B", SAIDA_ESPERADA_B);
        tmp.put("C", SAIDA_ESPERADA_C);
        tmp.put("D", SAIDA_ESPERADA_D);
        tmp.put("E", SAIDA_ESPERADA_E);
        tmp.put("F", SAIDA_ESPERADA_F);
        tmp.put("G", SAIDA_ESPERADA_G);
        tmp.put("H", SAIDA_ESPERADA_H);
        //tmp.put("I", SAIDA_ESPERADA_I);
        tmp.put("J", SAIDA_ESPERADA_J);
        tmp.put("K", SAIDA_ESPERADA_K);
        tmp.put("L", SAIDA_ESPERADA_L);
        tmp.put("M", SAIDA_ESPERADA_M);
        tmp.put("N", SAIDA_ESPERADA_N);
        //tmp.put("O", SAIDA_ESPERADA_O);
        tmp.put("P", SAIDA_ESPERADA_P);
        tmp.put("Q", SAIDA_ESPERADA_Q);
        tmp.put("R", SAIDA_ESPERADA_R);
        tmp.put("S", SAIDA_ESPERADA_S);
        tmp.put("T", SAIDA_ESPERADA_T);
        tmp.put("U", SAIDA_ESPERADA_U);
        //tmp.put("V", SAIDA_ESPERADA_V);
        tmp.put("W", SAIDA_ESPERADA_W);
        tmp.put("X", SAIDA_ESPERADA_X);
        tmp.put("Y", SAIDA_ESPERADA_Y);
        tmp.put("Z", SAIDA_ESPERADA_Z);

        //tmp.put("0", SAIDA_ESPERADA_0);
        //tmp.put("1", SAIDA_ESPERADA_1);
        tmp.put("2", SAIDA_ESPERADA_2);
        tmp.put("3", SAIDA_ESPERADA_3);
        tmp.put("4", SAIDA_ESPERADA_4);
        tmp.put("5", SAIDA_ESPERADA_5);
        tmp.put("6", SAIDA_ESPERADA_6);
        tmp.put("7", SAIDA_ESPERADA_7);
        tmp.put("8", SAIDA_ESPERADA_8);
        tmp.put("9", SAIDA_ESPERADA_9);

        SAIDAS_DESEJADAS = Collections.unmodifiableMap(tmp);
    }

    public static String converteArrayDeBitsParaLetra(double[] resposta) {
    	for (int i = 0; i<= resposta.length; i++){
    		if (resposta[i] != 0) {
    			int posicaoAscii = i + 65;
    			return String.valueOf( (char) posicaoAscii );
    		}
    	}
		return null;
	}

    /**
     * Converte um int[] para String, separando os valores com ";"
     * @param entrada o array de inteiros
     * @return uma String contendo os valores do array separados por ";"
     */
    public static String converteArrayDeBitsParaString(int[] entrada){
		StringBuilder str = new StringBuilder();
    	for(int i = 0; i < entrada.length; i++){
    		str.append(String.valueOf(entrada[i]));
    		str.append(";");
    	}
    	str.deleteCharAt(str.length()-1);
    	return str.toString();
	}

    /**
     * Obtém uma String representando a saída desejada para um determinado caractere
     * @param caractere O caractere desejado
     * @return Uma String contendo o padrão de saída esperado
     */
    public static String obtemSaidaDesejada(String caractere){
        return SAIDAS_DESEJADAS.get(caractere.toUpperCase());
    }

    /**
     * Converte em um array de inteiros o bitmap da imagem passada como parâmetro.
     * O array contém apenas 0s (pixels brancos da imagem) e 1s (pixels pretos da imagem).
     * 
     * A imagem deve ser um bitmap monocromático 1bpp.
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
     * Converte em um array de inteiros o bitmap da imagem obtida no path passado como parâmetro.
     * O array contém apenas 0s (pixels brancos da imagem) e 1s (pixels pretos da imagem).
     *
     * A imagem deve ser um bitmap monocromático 1bpp.
     * @param pathImg O path para a imagem de que se deseja extrair o bitmap
     * @return Um array de inteiros contendo o bitmap da imagem.
     */
    public static int[] extraiBitmap(String pathImg) throws IOException {
         BufferedImage img = ImageIO.read(new File(pathImg));
        
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
}