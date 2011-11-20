package br.ufcg.neuralcaptcha.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import org.joone.io.FileOutputSynapse;
import org.joone.io.MemoryOutputSynapse;
import org.joone.net.NeuralNet;

import br.ufcg.neuralcaptcha.util.BitMapper;
import br.ufcg.neuralcaptcha.util.BitmapExtractor;
import br.ufcg.neuralcaptcha.util.FileManager;


/**
 * Classe principal do projeto, cont�m a rede neural MLP e fornece m�todos para que uma aplica��o efetue
 * opera��es usando a rede.
 * Os seguintes caracteres n�o foram consideradas por n�o serem suportados no captcha escolhido: I,O,V,0,1
 *
 * @author Lucas Medeiros, V�tor Amaral, V�tor Avelino
 *
 */
public class NeuralCaptcha {

	public final static int TAMANHO_CAPTCHA = 5;
	public final static int TAMANHO_CARACTERE_W = 28, TAMANHO_CARACTERE_H = 45;
	public final static int NEURONIOS_DE_SAIDA = 2; // 26 letras + 10 d�gitos - 5 exclus�es
	private final static String ADVANCED_COLUMN_SELECTOR = "1-" + String.valueOf(TAMANHO_CARACTERE_W * TAMANHO_CARACTERE_H);

	private NeuralNet rede;
	private LinearLayer input;
	private SigmoidLayer hidden;
	private SigmoidLayer output;

	public NeuralCaptcha() { }

	/**
	 * Cria uma inst�ncia do reconhecedor e se registra como observador dos eventos da rede neural.
	 */
	public NeuralCaptcha(NeuralNetListener listener) {
		inicializaRede();
		adicionaListener(listener);
	}
    
	public void inicializaRede() {
		// cria rede
		rede = new NeuralNet();

		// Camadas da rede
		input = new LinearLayer();
		hidden = new SigmoidLayer();
		output = new SigmoidLayer();

		input.setLayerName("inputLayer");
		hidden.setLayerName("hiddenLayer");
		output.setLayerName("outputLayer");

		// Tamanhos
		// Tamanho da camada de entrada
		input.setRows(TAMANHO_CARACTERE_W * TAMANHO_CARACTERE_H); // N�mero de pixels de cada caractere
		// Tamanho da camada escondida - Configur�vel, altera o desempenho
		hidden.setRows(100);
		// Tamanho da camada de sa�da - n�mero de respostas poss�veis da rede (n�mero de caracteres reconhec�veis)
		output.setRows(NEURONIOS_DE_SAIDA); //

		// Sinapses
		FullSynapse synapse_IH = new FullSynapse(); /* input -> hidden conn. */
		FullSynapse synapse_HO = new FullSynapse(); /* hidden -> output conn. */

		// Conecta a camada de entrada � camada escondida
		input.addOutputSynapse(synapse_IH);
		hidden.addInputSynapse(synapse_IH);

		// Conecta a camada escondida � camada de sa�da
		hidden.addOutputSynapse(synapse_HO);
		output.addInputSynapse(synapse_HO);
	}

	public void treinaRede() throws IOException, InterruptedException {
		// Prepara a entrada para o treinamento da rede
		FileManager.geraArquivosDeTreinamento();

		FileInputSynapse inputSynapse1 = new FileInputSynapse();
		inputSynapse1.setInputFile(new File(FileManager.ENTRADA_TREINAMENTO));
		inputSynapse1.setAdvancedColumnSelector(ADVANCED_COLUMN_SELECTOR);
		//		inputSynapse1.setFirstRow(1);
		//		inputSynapse1.setLastRow(10); // TODO Quantidade de padroes de treinamento

		// Coloca esta sinapse como entrada da camada de entrada
		input.addInputSynapse(inputSynapse1);

		TeachingSynapse trainer = new TeachingSynapse();

		// Prepara a sa�da para o treinamento da rede
		FileInputSynapse desiredSynapse1 = new FileInputSynapse();
		desiredSynapse1.setInputFile(new File(FileManager.SAIDA_TREINAMENTO));
		desiredSynapse1.setAdvancedColumnSelector("1,2");
		//		desiredSynapse1.setFirstRow(1);
		//		desiredSynapse1.setLastRow(10); // TODO Quantidade de padroes de treinamento

		trainer.setDesired(desiredSynapse1);

		/* Creates the error output file */
		FileOutputSynapse error = new FileOutputSynapse();
		error.setFileName("res/error.txt");
		//error.setBuffered(false);
		trainer.addResultSynapse(error);

		// Coloca esta sinapse como sa�da da camada de sa�da
		output.addOutputSynapse(trainer);

		// Adiciona as camadas criadas � rede neural
		rede.addLayer(input, NeuralNet.INPUT_LAYER);
		rede.addLayer(hidden, NeuralNet.HIDDEN_LAYER);
		rede.addLayer(output, NeuralNet.OUTPUT_LAYER);
		rede.setTeacher(trainer);

		// configure monitor parameters
		Monitor monitor = rede.getMonitor();
		monitor.setLearningRate(0.8);
		monitor.setMomentum(0.3);
		monitor.setTrainingPatterns(10); // TODO Quantidade de linhas no arquivo de entrada de treinamento
		monitor.setTotCicles(10);
		monitor.setLearning(true);

		rede.go(true);
		System.out.println("Treinamento acabou!. �ltimo RMSE=" + rede.getMonitor().getGlobalError());
	}

	public void validaRede() throws IOException, InterruptedException {
		// Prepara a entrada para o treinamento da rede
		FileManager.geraArquivosDeValidacao();

		FileInputSynapse inputSynapse1 = new FileInputSynapse();

		inputSynapse1.setInputFile(new File(FileManager.ENTRADA_VALIDACAO));
		inputSynapse1.setName("input1");
		inputSynapse1.setAdvancedColumnSelector(ADVANCED_COLUMN_SELECTOR);
		inputSynapse1.setFirstRow(1);
		inputSynapse1.setLastRow(54); // 26 letras - 9 exclu�das

		// Coloca esta sinapse como entrada da camada de entrada
		rede.getInputLayer().addInputSynapse(inputSynapse1);

		// Prepara a sa�da para o treinamento da rede
		FileInputSynapse desiredSynapse1 = new FileInputSynapse();

		desiredSynapse1.setInputFile(new File(FileManager.SAIDA_VALIDACAO));
		desiredSynapse1.setName("desired1");
		desiredSynapse1.setAdvancedColumnSelector(ADVANCED_COLUMN_SELECTOR);
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

	/**
	 * Processa o captcha previamente obtido da internet e identifica seus caracteres.
	 * 
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public String identificaCaptcha() throws IOException, InterruptedException {
		PreProcessor.processaImagem();
		String output = "";
		for (int i = 0; i < NeuralCaptcha.TAMANHO_CAPTCHA; i++){
			output += identificaCaractere(FileManager.DIRETORIO_LIVE_LETRAS + i + ".bmp");
		}
		return output;
	}

	/**
	 * Recebe um array de bits correspondente � imagem do captcha j� pr�-processada e identifica o mesmo
	 * @param entradaDaRede O array de bits correspondente aos 5 caracteres do captcha, j� pr�-processados
	 * @throws IOException 
	 */
	public String identificaCaptcha(int[][] entradaDaRede) throws IOException{
		// Cria arquivo tempor�rio para servir de entrada para a rede
		File arquivoDeEntrada = FileManager.criaArquivoParaReconhecimento(entradaDaRede);

		// Remove sinapses de entrada anteriores
		rede.getInputLayer().removeAllInputs();

		// Adiciona uma nova camada de entrada 
		FileInputSynapse inputSynapse1 = new FileInputSynapse();

		inputSynapse1.setInputFile(arquivoDeEntrada);
		inputSynapse1.setName("input1");
		inputSynapse1.setAdvancedColumnSelector(ADVANCED_COLUMN_SELECTOR);

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

		return BitMapper.traduzRespostaDaRede(resposta);
	}

	/**
	 * Recebe o path para a imagem de um caractere previamente processada e realiza a identifica��o do mesmo
	 * @param pathImagemProcessada O caminho para a imagem do caractere (bitmap 1bpp)
	 * @return O caractere identificado pela RNA
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public String identificaCaractere(String pathImagemProcessada) throws IOException, InterruptedException{
		int[][] entradaDaRede = new int[1][TAMANHO_CARACTERE_W * TAMANHO_CARACTERE_H];
		entradaDaRede[0] = BitmapExtractor.extraiBitmap(pathImagemProcessada);
		return identificaCaptcha(entradaDaRede);
	}

	public void adicionaListener(NeuralNetListener listener){
		rede.addNeuralNetListener(listener);
	}

    public void salvarRede() throws IOException {
        FileManager.salvarRede(rede);
    }

    public void carregarRede() throws IOException, ClassNotFoundException {
        rede = FileManager.carregaRedeArmazenada();
    }
}
