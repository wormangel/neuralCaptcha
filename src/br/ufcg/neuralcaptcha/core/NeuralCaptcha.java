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
import org.joone.io.MemoryOutputSynapse;
import org.joone.net.NeuralNet;

import br.ufcg.neuralcaptcha.util.BitMapper;
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
	private final int NEURONIOS_DE_SAIDA = 31; // 26 letras + 10 d�gitos - 5 exclus�es
	private final String ADVANCED_COLUMN_SELECTOR = "1-" + String.valueOf(TAMANHO_CARACTERE_W * TAMANHO_CARACTERE_H);

	private NeuralNet rede;
	private Monitor monitor;
	
	public NeuralCaptcha() { }
	
	/**
	 * Cria uma inst�ncia do reconhecedor e se registra como observador dos eventos da rede neural.
	 */
	public NeuralCaptcha(NeuralNetListener listener) {
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
		
		// get the monitor object to train or feed forward
		monitor = new Monitor();
		
		// set the monitor parameters
		monitor.setLearningRate(0.8);
		monitor.setMomentum(0.3);
		monitor.setTrainingPatterns(54); // TODO Quantidade de padroes de treinamento
		monitor.setTotCicles(150);
		monitor.setLearning(true);
		
		input.setMonitor(monitor);
		hidden.setMonitor(monitor);
		output.setMonitor(monitor);

		// Adiciona as camadas criadas � rede neural
		rede = new NeuralNet();

		rede.addLayer(input, NeuralNet.INPUT_LAYER);
		rede.addLayer(hidden, NeuralNet.HIDDEN_LAYER);
		rede.addLayer(output, NeuralNet.OUTPUT_LAYER);
	}

	public void treinaRede() throws IOException, InterruptedException {
		// Prepara a entrada para o treinamento da rede
		FileManager.geraArquivosDeTreinamento();

		FileInputSynapse inputSynapse1 = new FileInputSynapse();

		inputSynapse1.setInputFile(new File(FileManager.ENTRADA_TREINAMENTO));
		inputSynapse1.setName("input1");
		inputSynapse1.setAdvancedColumnSelector(ADVANCED_COLUMN_SELECTOR);
		inputSynapse1.setFirstRow(1);
		inputSynapse1.setLastRow(10); // TODO Quantidade de padroes de treinamento

		// Coloca esta sinapse como entrada da camada de entrada
		rede.getInputLayer().addInputSynapse(inputSynapse1);

		// Prepara a sa�da para o treinamento da rede
		FileInputSynapse desiredSynapse1 = new FileInputSynapse();

		desiredSynapse1.setInputFile(new File(FileManager.SAIDA_TREINAMENTO));
		desiredSynapse1.setName("desired1");
		desiredSynapse1.setAdvancedColumnSelector("1-2");
		desiredSynapse1.setFirstRow(1);
		desiredSynapse1.setLastRow(10); // TODO Quantidade de padroes de treinamento

		TeachingSynapse trainer = new TeachingSynapse();
		trainer.setDesired(desiredSynapse1);
		trainer.setMonitor(monitor);

		// Coloca esta sinapse como sa�da da camada de sa�da
		rede.setTeacher(trainer);
		rede.getOutputLayer().addOutputSynapse(trainer);

		rede.go(true);
		System.out.println("Treinamento acabou!. �ltimo RMSE="+ rede.getMonitor().getGlobalError());
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
		File arquivoDeEntrada = FileManager.criaArquivoParaReconhecimento(entradaDaRede);

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

	public void adicionaListener(NeuralNetListener listener){
		rede.addNeuralNetListener(listener);
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
