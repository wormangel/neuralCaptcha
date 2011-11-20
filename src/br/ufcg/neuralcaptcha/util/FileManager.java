package br.ufcg.neuralcaptcha.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import br.ufcg.neuralcaptcha.core.NeuralCaptcha;

public class FileManager {
	
	// Diret�rios contendo as imagens
	public static final String DIRETORIO_TREINAMENTO = "res/training/";
	public static final String DIRETORIO_VALIDACAO = "res/validation/";
    public static final String DIRETORIO_TESTE = "res/testing/";
	
	// Treinamento
	public static final String ENTRADA_TREINAMENTO = "res/trainingInput.txt";
	public static final String SAIDA_TREINAMENTO = "res/trainingOutput.txt";
	
	// Valida��o
	public static final String ENTRADA_VALIDACAO = "res/validationInput.txt";
	public static final String SAIDA_VALIDACAO = "res/validationOutput.txt";
	
	/**
	 * Recebe um array bidimensional de bits e o escreve em um arquivo que servir� de entrada para a rede neural.
     * O array deve estar corretamente processado (5 linhas, uma para cada caractere, cada linha contendo o bitmap do caractere)
	 * @param entradaDaRede O array j� pr�-processado
	 * @return O arquivo criado que servir� de entrada para a rede neural
	 * @throws IOException
	 */
	public static File criaArquivoParaReconhecimento(int[][] entradaDaRede) throws IOException{
		File arquivoDeEntrada = new File("C:\\entradaNeural.txt");
    	FileWriter writer = new FileWriter(arquivoDeEntrada);
    	writer.flush();
    	StringBuilder str = new StringBuilder();
    	for(int i = 0; i < NeuralCaptcha.TAMANHO_CAPTCHA; i++){
            for (int j = 0; j < NeuralCaptcha.TAMANHO_CARACTERE_W * NeuralCaptcha.TAMANHO_CARACTERE_H; j++){
                str.append(entradaDaRede[i][j]);
    		    str.append(";");
            }
    	}
    	str.deleteCharAt(str.length()-1);
    	writer.write(str.toString());
    	writer.close();
    	return arquivoDeEntrada;
	}
	
	/**
     * Varre o diret�rio de treinamento e os diret�rios de cada caractere, extrai o bitmap das imagens em um array e monta
     * o conjunto de todos esses bitmaps em um arquivo de texto que servir� de entrada para a rede. Monta tamb�m um arquivo
     * de texto contendo as sa�das esperadas para cada padr�o de treinamento, em linhas correspondentes ao arquivo de entrada.
     * @throws IOException
     * @throws InterruptedException
     */
	public static void geraArquivosDeTreinamento() throws IOException, InterruptedException{
		File diretoriosTreinamento = new File(DIRETORIO_TREINAMENTO);

		File inputTreinamento = new File(ENTRADA_TREINAMENTO);
		FileWriter writerInput = new FileWriter(inputTreinamento);
		writerInput.flush();
		
		File outputTreinamento = new File(SAIDA_TREINAMENTO);
		FileWriter writerOutput = new FileWriter(outputTreinamento);
		writerOutput.flush();
		
		// Pra cada diret�rio (correspondente a um caractere) no diret�rio de treinamento
		for (String dirCaractere : diretoriosTreinamento.list()) {
			if (dirCaractere.length() > 1){
				continue;
			}
			// Obt�m a lista de imagens no diret�rio desse caractere
            
			File dirComImagensDoCaractere = new File(DIRETORIO_TREINAMENTO + "/" + dirCaractere);
			// Pra cada imagem desse caractere
			for (String arquivoImagem : dirComImagensDoCaractere.list()) {
				// Converte para array de ints
				int[] imagemEmBits = BitmapExtractor.extraiBitmap(dirComImagensDoCaractere + "/" + arquivoImagem);
				// Converte para string e escreve no arquivo de entrada
				writerInput.write(BitMapper.converteArrayDeBitsParaString(imagemEmBits) + System.getProperty("line.separator"));
				
				// Escreve no arquivo de sa�da esperada a linha correspondente ao caractere
				writerOutput.write(BitMapper.obtemSaidaDesejada(dirCaractere) + System.getProperty("line.separator"));
			}
		}
		
		writerInput.close();
		writerOutput.close();		
	}

    /**
     * Varre o diret�rio de valida��o e os diret�rios de cada caractere, extrai o bitmap das imagens em um array e monta
     * o conjunto de todos esses bitmaps em um arquivo de texto que servir� de entrada para a rede. Monta tamb�m um arquivo
     * de texto contendo as sa�das esperadas para cada padr�o de treinamento, em linhas correspondentes ao arquivo de entrada.
     * @throws IOException
     * @throws InterruptedException
     */
	public static void geraArquivosDeValidacao() throws IOException, InterruptedException{
		File diretoriosValidacao = new File(DIRETORIO_VALIDACAO);

		File inputValidacao = new File(ENTRADA_VALIDACAO);
		FileWriter writerInput = new FileWriter(inputValidacao);
		writerInput.flush();
		
		File outputValidacao = new File(SAIDA_VALIDACAO);
		FileWriter writerOutput = new FileWriter(outputValidacao);
		writerOutput.flush();
		
		// Pra cada diret�rio (letra) no diret�rio de valida��o
		for (String dirCaractere : diretoriosValidacao.list()) {
			if (dirCaractere.length() > 1){
				continue;
			}
			// Obt�m a lista de imagens no diret�rio desse caractere
            
			File dirComImagensDoCaractere = new File(DIRETORIO_VALIDACAO + "\\" + dirCaractere);
			// Pra cada imagem desse caractere
			for (String arquivoImagem : dirComImagensDoCaractere.list()) {
				
				// Converte para array de ints
				int[] imagemEmBits = BitmapExtractor.extraiBitmap(dirComImagensDoCaractere + "\\" + arquivoImagem);
				// Converte para string e escreve no arquivo de entrada
				writerInput.write(BitMapper.converteArrayDeBitsParaString(imagemEmBits) + System.getProperty("line.separator"));
				
				// Escreve no arquivo de sa�da esperada a linha correspondente ao caractere
				writerOutput.write(BitMapper.obtemSaidaDesejada(dirCaractere) + System.getProperty("line.separator"));
			}
		}
		
		writerInput.close();
		writerOutput.close();		
	}

}
