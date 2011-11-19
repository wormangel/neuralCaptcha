package br.ufcg.neuralcaptcha.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.joone.engine.FullSynapse;
import org.joone.engine.LinearLayer;
import org.joone.engine.Monitor;
import org.joone.engine.NeuralNetListener;
import org.joone.engine.SigmoidLayer;
import org.joone.engine.learning.TeachingSynapse;
import org.joone.io.FileInputSynapse;
import org.joone.io.MemoryOutputSynapse;
import org.joone.net.NeuralNet;

import br.ufcg.neuralcaptcha.util.BitMapper;
import br.ufcg.neuralcaptcha.util.BitmapExtractor;


/**
 * Classe principal do projeto, cont�m a rede neural MLP e fornece m�todos para que uma aplica��o efetue
 * opera��es usando a rede.
 * Os seguintes caracteres n�o foram consideradas por n�o serem suportados no captcha escolhido: I,O,V,0,1
 *
 * @author Lucas Medeiros, V�tor Amaral, V�tor Avelino
 *
 */
public class NeuralCaptcha {

	private NeuralNet rede;

	private final int NEURONIOS_DE_SAIDA = 31; // 26 letras + 10 d�gitos - 5 exclus�es

    public final static int TAMANHO_CAPTCHA = 5;
    public final static int TAMANHO_CARACTERE_W = 28, TAMANHO_CARACTERE_H = 45;
    private final static String ADVANCED_COLUMN_SELECTOR = "1-" + String.valueOf(TAMANHO_CARACTERE_W * TAMANHO_CARACTERE_H);

    // Diret�rios contendo as imagens
	private final String diretorioArquivosTreinamento = "C:\\temp\\porFuncao\\treinamento\\letras";
	private final String diretorioArquivosValidacao = "C:\\temp\\porFuncao\\validacao\\letras";
	
	// Treinamento
	private final String arquivoEntradaTreinamento = "C:\\temp\\inputTreinamento.txt";
	private final String arquivoSaidaTreinamento = "C:\\temp\\outputTreinamento.txt";
	
	// Valida��o
	private final String arquivoEntradaValidacao = "C:\\temp\\inputValidacao.txt";
	private final String arquivoSaidaValidacao = "C:\\temp\\outputValidacao.txt";
	
	/**
     * Recebe uma string correspondente ao caminho no disco para a imagem a ser identificada.
     * A imagem � pr�-processada para obter a entrada da rede neural e submetida � mesma em seguida.
     * 
     * @param pathImagemNaoTratada O caminho para a imagem contendo o captcha a ser identificado.
     * @throws InterruptedException 
     * @throws IOException 
     */
    public String identificaCaptcha(String pathImagemNaoTratada) throws IOException, InterruptedException {
    	int[][] entradaDaRede = PreProcessor.processaImagem(pathImagemNaoTratada);
    	return identificaCaptcha(entradaDaRede);
    }
    
	/**
     * Recebe um array de bits correspondente � imagem do captcha j� pr�-processada e identifica o mesmo
     * @param entradaDaRede O array de bits correspondente aos 5 caracteres do captcha, j� pr�-processados
     * @throws IOException 
     */
    public String identificaCaptcha(int[][] entradaDaRede) throws IOException{
    	// Cria arquivo tempor�rio para servir de entrada para a rede
    	File arquivoDeEntrada = criaArquivoParaReconhecimento(entradaDaRede);
    	
    	// Remove sinapses de entrada anteriores
    	rede.getInputLayer().removeAllInputs();
    	
    	// Adiciona uma nova camada de entrada 
		FileInputSynapse inputSynapse1 = new FileInputSynapse();

		inputSynapse1.setInputFile(arquivoDeEntrada);
		inputSynapse1.setName("input1");
		inputSynapse1.setAdvancedColumnSelector(ADVANCED_COLUMN_SELECTOR);
		inputSynapse1.setFirstRow(1);
		inputSynapse1.setLastRow(TAMANHO_CAPTCHA); // 5 caracteres

		// Coloca esta sinapse como entrada da camada de entrada
		rede.getInputLayer().addInputSynapse(inputSynapse1);
		
		// Remove sinapses de sa�da anteriores
    	rede.getOutputLayer().removeAllOutputs();
    	
    	// Adiciona uma nova camada de sa�da
		MemoryOutputSynapse outputSynapse1 = new MemoryOutputSynapse();

		// Coloca esta sinapse como sa�da da camada de entrada
		rede.getOutputLayer().addOutputSynapse(outputSynapse1);
		
		rede.getMonitor().setTotCicles(1);
		rede.getMonitor().setTrainingPatterns(1);
		rede.getMonitor().setLearning(false);
		rede.go();
		double[] resposta = outputSynapse1.getNextPattern();
		
		return BitMapper.converteArrayDeBitsParaLetra(resposta);
	}

	public void treinaRede() throws IOException, InterruptedException {
    	// Prepara a entrada para o treinamento da rede
    	geraArquivosDeTreinamento();

		FileInputSynapse inputSynapse1 = new FileInputSynapse();

		inputSynapse1.setInputFile(new File(arquivoEntradaTreinamento));
		inputSynapse1.setName("input1");
		inputSynapse1.setAdvancedColumnSelector(ADVANCED_COLUMN_SELECTOR);
		inputSynapse1.setFirstRow(1);
		inputSynapse1.setLastRow(54); // TODO Quantidade de padroes de treinamento

		// Coloca esta sinapse como entrada da camada de entrada
		rede.getInputLayer().addInputSynapse(inputSynapse1);

		// Prepara a sa�da para o treinamento da rede
		FileInputSynapse desiredSynapse1 = new FileInputSynapse();

		desiredSynapse1.setInputFile(new File(arquivoSaidaTreinamento));
		desiredSynapse1.setName("desired1");
		desiredSynapse1.setAdvancedColumnSelector("ADVANCED_COLUMN_SELECTOR");
		desiredSynapse1.setFirstRow(1);
		desiredSynapse1.setLastRow(54); // TODO Quantidade de padroes de treinamento

		TeachingSynapse trainer = new TeachingSynapse();
		trainer.setDesired(desiredSynapse1);

		// Coloca esta sinapse como sa�da da camada de sa�da
		rede.setTeacher(trainer);
		rede.getOutputLayer().addOutputSynapse(trainer);

    	// get the monitor object to train or feed forward
        Monitor monitor = rede.getMonitor();

        // set the monitor parameters
        monitor.setLearningRate(0.8);
        monitor.setMomentum(0.3);
        monitor.setTrainingPatterns(54); // TODO Quantidade de padroes de treinamento
        monitor.setTotCicles(150);
        monitor.setLearning(true);
        rede.go(true);
        System.out.println("Treinamento acabou!. �ltimo RMSE="+ rede.getMonitor().getGlobalError());
    }
	
	/**
	 * Recebe um array bidimensional de bits e o escreve em um arquivo que servir� de entrada para a rede neural.
     * O array deve estar corretamente processado (5 linhas, uma para cada caractere, cada linha contendo o bitmap do caractere)
	 * @param entradaDaRede O array j� pr�-processado
	 * @return O arquivo criado que servir� de entrada para a rede neural
	 * @throws IOException
	 */
	private File criaArquivoParaReconhecimento(int[][] entradaDaRede) throws IOException{
		File arquivoDeEntrada = new File("C:\\entradaNeural.txt");
    	FileWriter writer = new FileWriter(arquivoDeEntrada);
    	writer.flush();
    	StringBuilder str = new StringBuilder();
    	for(int i = 0; i < TAMANHO_CAPTCHA; i++){
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
    

    public void validaRede() throws IOException, InterruptedException {
    	// Prepara a entrada para o treinamento da rede
    	geraArquivosDeValidacao();

		FileInputSynapse inputSynapse1 = new FileInputSynapse();

		inputSynapse1.setInputFile(new File(arquivoEntradaValidacao));
		inputSynapse1.setName("input1");
		inputSynapse1.setAdvancedColumnSelector("1-150");
		inputSynapse1.setFirstRow(1);
		inputSynapse1.setLastRow(54); // 26 letras - 9 exclu�das

		// Coloca esta sinapse como entrada da camada de entrada
		rede.getInputLayer().addInputSynapse(inputSynapse1);

		// Prepara a sa�da para o treinamento da rede
		FileInputSynapse desiredSynapse1 = new FileInputSynapse();

		desiredSynapse1.setInputFile(new File(arquivoSaidaValidacao));
		desiredSynapse1.setName("desired1");
		desiredSynapse1.setAdvancedColumnSelector("1-150");
		desiredSynapse1.setFirstRow(1);
		desiredSynapse1.setLastRow(54);

		TeachingSynapse trainer = new TeachingSynapse();
		trainer.setDesired(desiredSynapse1);

		// Coloca esta sinapse como sa�da da camada de sa�da
		rede.setTeacher(trainer);
		rede.getOutputLayer().addOutputSynapse(trainer);

    	// get the monitor object to train or feed forward
        Monitor monitor = rede.getMonitor();

        // set the monitor parameters
        monitor.setLearningRate(0.8);
        monitor.setMomentum(0.3);
        monitor.setTrainingPatterns(54);
        monitor.setTotCicles(150);
        monitor.setLearning(false);
        rede.go(true);
        System.out.println("Network stopped. Last RMSE="+ rede.getMonitor().getGlobalError());
    }
    
	public void inicializaRede() {
		// Rede para letras

		// Camadas da rede
		LinearLayer input = new LinearLayer();
		SigmoidLayer hidden = new SigmoidLayer();
		SigmoidLayer output = new SigmoidLayer();

		// Tamanhos
        // Tamanho da camada de entrada
		input.setRows(TAMANHO_CARACTERE_W * TAMANHO_CARACTERE_H); // N�mero de pixels de cada caractere

        // Tamanho da camada escondida - Configur�vel, altera o desempenho
		hidden.setRows(100);

         // Tamanho da camada de sa�da - n�mero de respostas poss�veis da rede (n�mero de caracteres reconhec�veis)
		output.setRows(NEURONIOS_DE_SAIDA); //

		input.setLayerName("inputLayer");
		hidden.setLayerName("hiddenLayer");
		output.setLayerName("outputLayer");

		// Sinapses
		FullSynapse synapse_IH = new FullSynapse(); /* input -> hidden conn. */
		FullSynapse synapse_HO = new FullSynapse(); /* hidden -> output conn. */

		// Conecta a camada de entrada � camada escondida
		input.addOutputSynapse(synapse_IH);
		hidden.addInputSynapse(synapse_IH);

		// Conecta a camada escondida � camada de sa�da
		hidden.addOutputSynapse(synapse_HO);
		output.addInputSynapse(synapse_HO);

		// Adiciona as camadas criadas � rede neural
		rede = new NeuralNet();

		rede.addLayer(input, NeuralNet.INPUT_LAYER);
		rede.addLayer(hidden, NeuralNet.HIDDEN_LAYER);
		rede.addLayer(output, NeuralNet.OUTPUT_LAYER);
	}

	public void adicionaListener(NeuralNetListener listener){
		rede.addNeuralNetListener(listener);
	}
	
	public NeuralCaptcha(){}
	
	/**
	 * Cria uma inst�ncia do reconhecedor e se registra como observador dos eventos da rede neural.
	 */
	public NeuralCaptcha(NeuralNetListener listener){
		inicializaRede();
		adicionaListener(listener);
	}
	
	public NeuralCaptcha(NeuralNetListener listener, boolean carregaRede) throws IOException, ClassNotFoundException{
		if (carregaRede) {
			carregaRede();
		} else {
			inicializaRede();
		}		
		
		adicionaListener(listener);
	}

    /**
     * Varre o diret�rio de treinamento e os diret�rios de cada caractere, extrai o bitmap das imagens em um array e monta
     * o conjunto de todos esses bitmaps em um arquivo de texto que servir� de entrada para a rede. Monta tamb�m um arquivo
     * de texto contendo as sa�das esperadas para cada padr�o de treinamento, em linhas correspondentes ao arquivo de entrada.
     * @throws IOException
     * @throws InterruptedException
     */
	private void geraArquivosDeTreinamento() throws IOException, InterruptedException{
		File diretoriosTreinamento = new File(diretorioArquivosTreinamento);

		File inputTreinamento = new File(arquivoEntradaTreinamento);
		FileWriter writerInput = new FileWriter(inputTreinamento);
		writerInput.flush();
		
		File outputTreinamento = new File(arquivoSaidaTreinamento);
		FileWriter writerOutput = new FileWriter(outputTreinamento);
		writerOutput.flush();
		
		// Pra cada diret�rio (correspondente a um caractere) no diret�rio de treinamento
		for (String dirCaractere : diretoriosTreinamento.list()) {
			if (dirCaractere.length() > 1){
				continue;
			}
			// Obt�m a lista de imagens no diret�rio desse caractere
            
			File dirComImagensDoCaractere = new File(diretorioArquivosTreinamento + "\\" + dirCaractere);
			// Pra cada imagem desse caractere
			for (String arquivoImagem : dirComImagensDoCaractere.list()) {
				
				// Converte para array de ints
				int[] imagemEmBits = BitmapExtractor.extraiBitmap(dirComImagensDoCaractere + "\\" + arquivoImagem);
				// Converte para string e escreve no arquivo de entrada
				writerInput.write(BitMapper.converteArrayDeBitsParaString(imagemEmBits) + "\n");
				
				// Escreve no arquivo de sa�da esperada a linha correspondente ao caractere
				writerOutput.write(BitMapper.obtemSaidaDesejada(dirCaractere) + "\n");
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
	private void geraArquivosDeValidacao() throws IOException, InterruptedException{
		File diretoriosValidacao = new File(diretorioArquivosValidacao);

		File inputValidacao = new File(arquivoEntradaValidacao);
		FileWriter writerInput = new FileWriter(inputValidacao);
		writerInput.flush();
		
		File outputValidacao = new File(arquivoSaidaValidacao);
		FileWriter writerOutput = new FileWriter(outputValidacao);
		writerOutput.flush();
		
		// Pra cada diret�rio (letra) no diret�rio de valida��o
		for (String dirCaractere : diretoriosValidacao.list()) {
			if (dirCaractere.length() > 1){
				continue;
			}
			// Obt�m a lista de imagens no diret�rio desse caractere
            
			File dirComImagensDoCaractere = new File(diretorioArquivosValidacao + "\\" + dirCaractere);
			// Pra cada imagem desse caractere
			for (String arquivoImagem : dirComImagensDoCaractere.list()) {
				
				// Converte para array de ints
				int[] imagemEmBits = BitmapExtractor.extraiBitmap(dirComImagensDoCaractere + "\\" + arquivoImagem);
				// Converte para string e escreve no arquivo de entrada
				writerInput.write(BitMapper.converteArrayDeBitsParaString(imagemEmBits) + "\n");
				
				// Escreve no arquivo de sa�da esperada a linha correspondente ao caractere
				writerOutput.write(BitMapper.obtemSaidaDesejada(dirCaractere) + "\n");
			}
		}
		
		writerInput.close();
		writerOutput.close();		
	}

    // M�todos para lidar com a persist�ncia da rede (para n�o ter que treina-la novamente a cada execu��o)

    /**
     * Salva a rede neural em disco.
     * @throws IOException
     */
	public void salvarRede() throws IOException{
		FileOutputStream stream = new FileOutputStream("C:\\temp\\redeNeural.mlp");
		ObjectOutputStream out = new ObjectOutputStream(stream);
		out.writeObject(rede);
		out.close();
	}

    /**
     * Verifica se a rede neural foi persistidda no caminho pr�-determinado. Se sim, carrega a rede armazenada, se n�o,
     * inicializa a rede neural.
     * @throws IOException
     * @throws ClassNotFoundException
     */
	public void carregaRede() throws IOException, ClassNotFoundException {
		File redeSalva = new File("C:\\temp\\redeNeural.mlp");
		if (redeSalva.exists()) {
			carregaRedeArmazenada();
		} else {
			inicializaRede();
		}
	}

    /**
     * Carrega a rede neural armazenada em disco.
     * @throws IOException
     * @throws ClassNotFoundException
     */
	public void carregaRedeArmazenada() throws IOException, ClassNotFoundException{
		FileInputStream stream = new FileInputStream("C:\\temp\\redeNeural.mlp");
		ObjectInputStream out = new ObjectInputStream(stream);
		rede = (NeuralNet) out.readObject();
	}
}
