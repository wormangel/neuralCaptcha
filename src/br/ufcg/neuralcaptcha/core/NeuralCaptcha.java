package br.ufcg.neuralcaptcha.core;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Vector;

import org.joone.engine.FullSynapse;
import org.joone.engine.LinearLayer;
import org.joone.engine.Monitor;
import org.joone.engine.NeuralNetEvent;
import org.joone.engine.NeuralNetListener;
import org.joone.engine.SigmoidLayer;
import org.joone.engine.learning.TeachingSynapse;
import org.joone.io.FileInputSynapse;
import org.joone.io.FileOutputSynapse;
import org.joone.io.MemoryInputSynapse;
import org.joone.io.MemoryOutputSynapse;
import org.joone.net.NeuralNet;
import org.joone.net.NeuralNetValidator;
import org.joone.net.NeuralValidationEvent;
import org.joone.net.NeuralValidationListener;
import org.joone.util.LearningSwitch;

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
public class NeuralCaptcha implements Serializable, NeuralNetListener, NeuralValidationListener {

	private final static double THRESHOLD = 0.1;
	private final static int INTERVALO_DE_VALIDACAO = 20;

	public final static int TAMANHO_CAPTCHA = 5;
	public final static int TAMANHO_CARACTERE_W = 10, TAMANHO_CARACTERE_H = 16;
	public final static int NEURONIOS_DE_SAIDA = 31; // 26 letras + 10 d�gitos - 5 exclus�es
	private final static String ADVANCED_COLUMN_SELECTOR = "1-" + String.valueOf(TAMANHO_CARACTERE_W * TAMANHO_CARACTERE_H);

	private NeuralNet rede;
	private LinearLayer input;
	private SigmoidLayer hidden;
	private SigmoidLayer output;
	private long startms;
	private MemoryOutputSynapse outputSynapse1;
	private boolean debug = true;

	public NeuralCaptcha() { 
		inicializaRede();
		adicionaListener(this);
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
		hidden.setRows(64);
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
		// Gera o conjunto de dados de treinamento
		FileManager.geraArquivosDeTreinamento();
		// Gera o conjunto de dados de valida��o
		FileManager.geraArquivosDeValidacao();

		// Conjunto de entrada: TREINAMENTO
		FileInputSynapse inputSynapseTreinamento = new FileInputSynapse();
		inputSynapseTreinamento.setInputFile(new File(FileManager.ENTRADA_TREINAMENTO));
		inputSynapseTreinamento.setAdvancedColumnSelector(ADVANCED_COLUMN_SELECTOR);
		// Conjunto de entrada: VALIDA��O
		FileInputSynapse inputSynapseValidacao = new FileInputSynapse();
		inputSynapseValidacao.setInputFile(new File(FileManager.ENTRADA_VALIDACAO));
		inputSynapseValidacao.setAdvancedColumnSelector(ADVANCED_COLUMN_SELECTOR);

		// Switch do conjunto de dados de ENTRADA
		LearningSwitch switchEntrada = new LearningSwitch();
		switchEntrada.addTrainingSet(inputSynapseTreinamento);
		switchEntrada.addValidationSet(inputSynapseValidacao);

		// Coloca o switch de entrada como entrada da camada de entrada
		input.addInputSynapse(switchEntrada);

		// Conjunto de sa�da: TREINAMENTO
		FileInputSynapse desiredSynapseTreinamento = new FileInputSynapse();
		desiredSynapseTreinamento.setInputFile(new File(FileManager.SAIDA_TREINAMENTO));
		desiredSynapseTreinamento.setAdvancedColumnSelector("1-31");
		// Conjunto de sa�da: VALIDA��O
		FileInputSynapse desiredSynapseValidacao = new FileInputSynapse();
		desiredSynapseValidacao.setInputFile(new File(FileManager.SAIDA_VALIDACAO));
		desiredSynapseValidacao.setAdvancedColumnSelector("1-31");

		// Switch do conjunto de dados de SA�DA
		LearningSwitch switchSaida = new LearningSwitch();
		switchSaida.addTrainingSet(desiredSynapseTreinamento);
		switchSaida.addValidationSet(desiredSynapseValidacao);

		// Associa o switch de sa�da com o supervisor
		TeachingSynapse trainer = new TeachingSynapse();
		trainer.setDesired(switchSaida);

		// Coloca esta sinapse como sa�da da camada de sa�da
		output.addOutputSynapse(trainer);

		/* Creates the error output file */
		FileOutputSynapse error = new FileOutputSynapse();
		error.setFileName("res/error.txt");
		//error.setBuffered(false);
		trainer.addResultSynapse(error);

		// Adiciona as camadas criadas � rede neural
		rede.addLayer(input, NeuralNet.INPUT_LAYER);
		rede.addLayer(hidden, NeuralNet.HIDDEN_LAYER);
		rede.addLayer(output, NeuralNet.OUTPUT_LAYER);
		rede.setTeacher(trainer);

		// configure monitor parameters
		Monitor monitor = rede.getMonitor();
		monitor.setLearningRate(0.1);
		monitor.setMomentum(0.8);
		monitor.setTrainingPatterns(1435); // TODO Quantidade de linhas no arquivo de entrada de treinamento
		monitor.setValidationPatterns(1414);
		monitor.setTotCicles(50);
		monitor.setLearning(true);

		startms = System.currentTimeMillis();
		rede.go(true);
		System.out.println(System.getProperty("line.separator") + "Treinamento acabou!. �ltimo RMSE=" + rede.getMonitor().getGlobalError());
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
	public String identificaCaptcha(double[][] entradaDaRede) throws IOException{
		// Cria arquivo tempor�rio para servir de entrada para a rede
//		File arquivoDeEntrada = FileManager.criaArquivoParaReconhecimento(entradaDaRede);

		// Remove sinapses de entrada anteriores
		input.removeAllInputs();
		// Adiciona uma nova camada de entrada 
//		FileInputSynapse inputSynapse1 = new FileInputSynapse();
//		inputSynapse1.setInputFile(arquivoDeEntrada);
//		inputSynapse1.setAdvancedColumnSelector(ADVANCED_COLUMN_SELECTOR);
//		input.addInputSynapse(inputSynapse1);
		
		MemoryInputSynapse inputSynapse = new MemoryInputSynapse();
		inputSynapse.setInputArray(entradaDaRede);
		inputSynapse.setAdvancedColumnSelector(ADVANCED_COLUMN_SELECTOR);

		// Coloca esta sinapse como entrada da camada de entrada
		input.addInputSynapse(inputSynapse);

		// Adiciona uma nova camada de sa�da
		outputSynapse1 = new MemoryOutputSynapse();

		// Coloca esta sinapse como sa�da da camada de entrada
		output.addOutputSynapse(outputSynapse1);
		rede.getMonitor().setTotCicles(1);
		rede.getMonitor().setTrainingPatterns(1);
		rede.getMonitor().setLearning(false);
		System.out.println("argh!");
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
		double[][] entradaDaRede = new double[1][TAMANHO_CARACTERE_W * TAMANHO_CARACTERE_H];
		entradaDaRede[0] = BitmapExtractor.extraiBitmap(pathImagemProcessada);
		System.out.println("EXTRAIU OS BITS");
		return identificaCaptcha(entradaDaRede);
	}

	public void executaConjuntoDeTeste() throws IOException, InterruptedException {
		debug = false;
		DecimalFormat df = new DecimalFormat("#.##");
		StringBuilder result = new StringBuilder();
		File diretoriosTeste = new File(FileManager.DIRETORIO_TESTE);
		int acertosGeral = 0;
		
		for (String dirCaractere : diretoriosTeste.list()) {
			Thread.sleep(500);
			result.append(System.getProperty("line.separator") + "Caractere: " + dirCaractere.toUpperCase() +
					System.getProperty("line.separator"));
			File dirComImagensDoCaractere = new File(diretoriosTeste + "/" + dirCaractere);
			int acertos = 0;
			int totalAmostras = dirComImagensDoCaractere.list().length;
			System.out.println("Testando diretório: " + dirCaractere.toUpperCase());
			for (String arquivoImagem : dirComImagensDoCaractere.list()) {
				String path = dirComImagensDoCaractere + "/" + arquivoImagem;
				String respostaDaRede = identificaCaractere(path);
				if (dirCaractere.toUpperCase().equals(respostaDaRede)){
					acertos++;
					acertosGeral++;
				} else {
					System.out.println("Identificou " + dirCaractere.toUpperCase() + " como " + respostaDaRede);
				}
			}

			result.append("Total: " + totalAmostras + " amostras, " + acertos + " acertos, " +
					(totalAmostras - acertos) + " erros, " + df.format(((double) acertos / totalAmostras) * 100.0) + "% de corretude.");
		}
		System.out.println(result);
		System.out.println("Acertos no total: " + acertosGeral);
	}

	public void adicionaListener(NeuralNetListener listener){
		rede.addNeuralNetListener(listener);
	}

	public void salvarRede() throws IOException {
		// Armazena os listeners da rede pois estes terao que ser removidos para salvar
		Vector<NeuralNetListener> listeners = rede.getListeners();
		rede.removeAllListeners();

		// Salva a rede
		FileManager.salvarRede(rede);

		// Registra os listeners na rede novamente
		for (NeuralNetListener listener : listeners){
			rede.addNeuralNetListener(listener);
		}
	}

	public void carregarRede() throws IOException, ClassNotFoundException {
		rede = FileManager.carregaRedeArmazenada();
	}

	public NeuralNet redeNeural() {
		return this.rede;
	}

	public void netStarted(NeuralNetEvent e) {
		if (debug) {
			System.out.println("Rede come�ou!");
		}
	}

	public void cicleTerminated(NeuralNetEvent e) {
		if (debug) {

			// Prints out the cycle and the training error
			int cycle = rede.getMonitor().getTotCicles() - rede.getMonitor().getCurrentCicle()+1;

			System.out.println("Ciclo #"+cycle);
			System.out.println("    Erro de treinamento:   " + rede.getMonitor().getGlobalError());

			if (cycle % INTERVALO_DE_VALIDACAO == 0) { // We validate the net every INTERVALO_DE_VALIDACAO cycles

				// Creates a copy of the neural network
				rede.getMonitor().setExporting(true);
				NeuralNet newNet = rede.cloneNet();
				rede.getMonitor().setExporting(false);

				// Cleans the old listeners
				// This is a fundamental action to avoid that the validating net
				// calls the cicleTerminated method of this class
				newNet.removeAllListeners();

				// Set all the parameters for the validation
				NeuralNetValidator nnv = new NeuralNetValidator(newNet);
				nnv.addValidationListener(this);
				nnv.start();  // Validates the net
			}
		}
	}

	public void netStopped(NeuralNetEvent e) {
		if (debug) {
			System.out.println("Rede terminou ap�s " + ((System.currentTimeMillis() - startms) / 1000.0) + "s");
		}
	}

	public void errorChanged(NeuralNetEvent e) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	public void netStoppedError(NeuralNetEvent e, String error) {
		System.out.println("Erro! " + error);
	}

	public void netValidated(NeuralValidationEvent event) {
		// Shows the RMSE at the end of the cycle
		NeuralNet NN = (NeuralNet)event.getSource();
		System.out.println("    Erro de valida��o: "+NN.getMonitor().getGlobalError());
		if (NN.getMonitor().getGlobalError() < THRESHOLD){
			NN.stop();
		}
	}
}
