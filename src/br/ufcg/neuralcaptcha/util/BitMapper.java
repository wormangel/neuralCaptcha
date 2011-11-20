package br.ufcg.neuralcaptcha.util;

import br.ufcg.neuralcaptcha.core.NeuralCaptcha;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BitMapper {
	
	// Strings representando a saï¿½da desejada da rede para cada caractere
    
    // Sï¿½o 31 neurï¿½nios de saï¿½da, um para cada caractere possï¿½vel, jï¿½ levando em conta as exclusï¿½es (ver dadosAmostras.txt)
    private static final String SAIDA_ESPERADA_A = "1;0";
	private static final String SAIDA_ESPERADA_B = "0;1";
	private static final String SAIDA_ESPERADA_C = "0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
	private static final String SAIDA_ESPERADA_D = "0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
    private static final String SAIDA_ESPERADA_E = "0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
	private static final String SAIDA_ESPERADA_F = "0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
	private static final String SAIDA_ESPERADA_G = "0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
    private static final String SAIDA_ESPERADA_H = "0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
	//private static final String SAIDA_ESPERADA_I = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
    private static final String SAIDA_ESPERADA_J = "0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
	private static final String SAIDA_ESPERADA_K = "0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
	private static final String SAIDA_ESPERADA_L = "0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
	private static final String SAIDA_ESPERADA_M = "0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
	private static final String SAIDA_ESPERADA_N = "0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
	//private static String SAIDA_ESPERADA_O = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
	private static final String SAIDA_ESPERADA_P = "0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
    private static final String SAIDA_ESPERADA_Q = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
	private static final String SAIDA_ESPERADA_R = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
    private static final String SAIDA_ESPERADA_S = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
    private static final String SAIDA_ESPERADA_T = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0";
	private static final String SAIDA_ESPERADA_U = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0";
    //private static final String SAIDA_ESPERADA_V = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
    private static final String SAIDA_ESPERADA_W = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0";
    private static final String SAIDA_ESPERADA_X = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0";
	private static final String SAIDA_ESPERADA_Y = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0";
	private static final String SAIDA_ESPERADA_Z = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0";

	//private static final String SAIDA_ESPERADA_0 = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
	//private static final String SAIDA_ESPERADA_1 = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
	private static final String SAIDA_ESPERADA_2 = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0";
	private static final String SAIDA_ESPERADA_3 = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0";
	private static final String SAIDA_ESPERADA_4 = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0";
	private static final String SAIDA_ESPERADA_5 = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0";
	private static final String SAIDA_ESPERADA_6 = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0";
	private static final String SAIDA_ESPERADA_7 = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0";
	private static final String SAIDA_ESPERADA_8 = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0";
	private static final String SAIDA_ESPERADA_9 = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1";

    /**
     * Mapa interno que associa os caracteres identificï¿½veis com as saï¿½das desejadas da RNA
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

    /**
     * Array interno que enumera todas as respostas possíveis da rede.
     * O caractere identificado pela rede corresponde ao caractere na posição deste array igual à posição do valor mais
     * alto no array retornado pela rede neural.
     */
    private static final String[] RESPOSTAS_DA_REDE;
    static {
        String[] tmp = {"A","B","C","D","E","F","G","H","J","K","L","M","N","P","Q","R","S","T","U",
                "W","X","Y","Z","2","3","4","5","6","7","8","9"};
        RESPOSTAS_DA_REDE = tmp;
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
     * Converte um double[] para String, separando os valores com ";"
     * @param entrada o array de double
     * @return uma String contendo os valores do array separados por ";"
     */
    public static String converteArrayDeBitsParaString(double[] entrada){
		StringBuilder str = new StringBuilder();
    	for(int i = 0; i < entrada.length; i++){
    		str.append(String.valueOf(entrada[i]));
    		str.append(";");
    	}
    	str.deleteCharAt(str.length()-1);
    	return str.toString();
	}


    /**
     * Traduz a resposta da rede neural para o caractere identificado pela mesma
     * @param resposta A resposta obtida pela rede neural
     * @return O caractere que foi identificado como mais provável pela rede
     */
    public static String traduzRespostaDaRede(double[] resposta){
        return RESPOSTAS_DA_REDE[obtemIndiceComMaiorValorNoArray(resposta)];
    }

    /**
     * Recebe um double[] e retorna o índice do array cujo valor é o maior dentro do array
     * @param array O array de double
     * @return A posição do maior elemento do array (começando em 0)
     */
    private static int obtemIndiceComMaiorValorNoArray(double[] array){
        int bigger = 0;
        for(int i = 1; i < array.length; i++){
            if (array[i] > array[0]){
                bigger = i;
            }
        }
        return bigger;
    }

    /**
     * Obtï¿½m uma String representando a saï¿½da desejada para um determinado caractere
     * @param caractere O caractere desejado
     * @return Uma String contendo o padrï¿½o de saï¿½da esperado
     */
    public static String obtemSaidaDesejada(String caractere){
        return SAIDAS_DESEJADAS.get(caractere.toUpperCase());
    }

}
