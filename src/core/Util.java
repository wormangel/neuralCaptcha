package core;

public class Util {
	
	public static String converteArrayDeBitsParaNumero(double[] resposta) {
    	for (int i = 0; i<= resposta.length; i++){
    		if (resposta[i] != 0) {
    			int numero = i + 1;
    			return String.valueOf(numero);
    		}
    	}
		return null;
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
    
    public static String converteArrayDeBitsParaString(int[] entrada){
		StringBuilder str = new StringBuilder();
    	for(int i = 0; i < entrada.length; i++){
    		str.append(String.valueOf(entrada[i]));
    		str.append(";");
    	}
    	str.deleteCharAt(str.length()-1);
    	return str.toString();
	}
    
    public static String converteDeLetraParaArrayDeBits(String letra) {
		if (letra.equals("A"))
			return SAIDA_ESPERADA_A;
		if (letra.equals("B"))
			return SAIDA_ESPERADA_B;
		if (letra.equals("C"))
			return SAIDA_ESPERADA_C;
		if (letra.equals("D"))
			return SAIDA_ESPERADA_D;
		if (letra.equals("F"))
			return SAIDA_ESPERADA_F;
		if (letra.equals("G"))
			return SAIDA_ESPERADA_G;
		if (letra.equals("I"))
			return SAIDA_ESPERADA_I;
		if (letra.equals("K"))
			return SAIDA_ESPERADA_K;
		if (letra.equals("L"))
			return SAIDA_ESPERADA_L;
		if (letra.equals("M"))
			return SAIDA_ESPERADA_M;
		if (letra.equals("N"))
			return SAIDA_ESPERADA_N;
		if (letra.equals("O"))
			return SAIDA_ESPERADA_O;
		if (letra.equals("P"))
			return SAIDA_ESPERADA_P;
		if (letra.equals("R"))
			return SAIDA_ESPERADA_R;
		if (letra.equals("U"))
			return SAIDA_ESPERADA_U;
		if (letra.equals("Y"))
			return SAIDA_ESPERADA_Y;
		if (letra.equals("Z"))
			return SAIDA_ESPERADA_Z;
		
		return null;
	}

    public static String converteDeNumeroParaArrayDeBits(String numero) {
		if (numero.equals("0"))
			return SAIDA_ESPERADA_0;
		if (numero.equals("1"))
			return SAIDA_ESPERADA_1;
		if (numero.equals("2"))
			return SAIDA_ESPERADA_2;
		if (numero.equals("3"))
			return SAIDA_ESPERADA_3;
		if (numero.equals("4"))
			return SAIDA_ESPERADA_4;
		if (numero.equals("5"))
			return SAIDA_ESPERADA_5;
		if (numero.equals("6"))
			return SAIDA_ESPERADA_6;
		if (numero.equals("7"))
			return SAIDA_ESPERADA_7;
		if (numero.equals("8"))
			return SAIDA_ESPERADA_8;
		if (numero.equals("9"))
			return SAIDA_ESPERADA_9;
		
		return null;
	}

    public static String SAIDA_ESPERADA_A = "1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
	public static String SAIDA_ESPERADA_B = "0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
	public static String SAIDA_ESPERADA_C = "0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
	public static String SAIDA_ESPERADA_D = "0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0";
	public static String SAIDA_ESPERADA_F = "0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0";
	public static String SAIDA_ESPERADA_G = "0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0";
	public static String SAIDA_ESPERADA_I = "0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0";
	public static String SAIDA_ESPERADA_K = "0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0";
	public static String SAIDA_ESPERADA_L = "0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0";
	public static String SAIDA_ESPERADA_M = "0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0";
	public static String SAIDA_ESPERADA_N = "0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0";
	public static String SAIDA_ESPERADA_O = "0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0";
	public static String SAIDA_ESPERADA_P = "0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0";
	public static String SAIDA_ESPERADA_R = "0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0";
	public static String SAIDA_ESPERADA_U = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0";
	public static String SAIDA_ESPERADA_Y = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0";
	public static String SAIDA_ESPERADA_Z = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1";
	
	public static String SAIDA_ESPERADA_0 = "1;0;0;0;0;0;0;0;0;0";
	public static String SAIDA_ESPERADA_1 = "0;1;0;0;0;0;0;0;0;0";
	public static String SAIDA_ESPERADA_2 = "0;0;1;0;0;0;0;0;0;0";
	public static String SAIDA_ESPERADA_3 = "0;0;0;1;0;0;0;0;0;0";
	public static String SAIDA_ESPERADA_4 = "0;0;0;0;1;0;0;0;0;0";
	public static String SAIDA_ESPERADA_5 = "0;0;0;0;0;1;0;0;0;0";
	public static String SAIDA_ESPERADA_6 = "0;0;0;0;0;0;1;0;0;0";
	public static String SAIDA_ESPERADA_7 = "0;0;0;0;0;0;0;1;0;0";
	public static String SAIDA_ESPERADA_8 = "0;0;0;0;0;0;0;0;1;0";
	public static String SAIDA_ESPERADA_9 = "0;0;0;0;0;0;0;0;0;1";
}
