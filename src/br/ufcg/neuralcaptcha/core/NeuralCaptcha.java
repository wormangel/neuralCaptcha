package br.ufcg.neuralcaptcha.core;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Vector;

import org.joone.engine.*;
import org.joone.engine.learning.TeachingSynapse;
import org.joone.io.FileInputSynapse;
import org.joone.io.FileOutputSynapse;
import org.joone.io.MemoryOutputSynapse;
import org.joone.net.NeuralNet;

import br.ufcg.neuralcaptcha.util.BitMapper;
import br.ufcg.neuralcaptcha.util.BitmapExtractor;
import br.ufcg.neuralcaptcha.util.FileManager;
import org.joone.net.NeuralNetValidator;
import org.joone.net.NeuralValidationEvent;
import org.joone.net.NeuralValidationListener;
import org.joone.util.LearningSwitch;


/**
 * Classe principal do projeto, contï¿½m a rede neural MLP e fornece mï¿½todos para que uma aplicaï¿½ï¿½o efetue
 * operaï¿½ï¿½es usando a rede.
 * Os seguintes caracteres nï¿½o foram consideradas por nï¿½o serem suportados no captcha escolhido: I,O,V,0,1
 *
 * @author Lucas Medeiros, Vï¿½tor Amaral, Vï¿½tor Avelino
 *
 */
public class NeuralCaptcha implements Serializable, NeuralNetListener, NeuralValidationListener {

    private final static double THRESHOLD = 0.1;
    private final static int INTERVALO_DE_VALIDACAO = 20;

	public final static int TAMANHO_CAPTCHA = 5;
	public final static int TAMANHO_CARACTERE_W = 28, TAMANHO_CARACTERE_H = 45;
	public final static int NEURONIOS_DE_SAIDA = 31; // 26 letras + 10 dï¿½gitos - 5 exclusï¿½es
	private final static String ADVANCED_COLUMN_SELECTOR = "1-" + String.valueOf(TAMANHO_CARACTERE_W * TAMANHO_CARACTERE_H);

	private NeuralNet rede;
	private LinearLayer input;
	private SigmoidLayer hidden;
	private SigmoidLayer output;
    private long startms;

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
		input.setRows(TAMANHO_CARACTERE_W * TAMANHO_CARACTERE_H); // Nï¿½mero de pixels de cada caractere
		// Tamanho da camada escondida - Configurï¿½vel, altera o desempenho
		hidden.setRows(100);
		// Tamanho da camada de saï¿½da - nï¿½mero de respostas possï¿½veis da rede (nï¿½mero de caracteres reconhecï¿½veis)
		output.setRows(NEURONIOS_DE_SAIDA); //

		// Sinapses
		FullSynapse synapse_IH = new FullSynapse(); /* input -> hidden conn. */
		FullSynapse synapse_HO = new FullSynapse(); /* hidden -> output conn. */

		// Conecta a camada de entrada ï¿½ camada escondida
		input.addOutputSynapse(synapse_IH);
		hidden.addInputSynapse(synapse_IH);

		// Conecta a camada escondida ï¿½ camada de saï¿½da
		hidden.addOutputSynapse(synapse_HO);
		output.addInputSynapse(synapse_HO);
	}

	public void treinaRede() throws IOException, InterruptedException {
		// Gera o conjunto de dados de treinamento
		FileManager.geraArquivosDeTreinamento();
        // Gera o conjunto de dados de validação
        FileManager.geraArquivosDeValidacao();

        // Conjunto de entrada: TREINAMENTO
		FileInputSynapse inputSynapseTreinamento = new FileInputSynapse();
		inputSynapseTreinamento.setInputFile(new File(FileManager.ENTRADA_TREINAMENTO));
		inputSynapseTreinamento.setAdvancedColumnSelector(ADVANCED_COLUMN_SELECTOR);
        // Conjunto de entrada: VALIDAÇÃO
        FileInputSynapse inputSynapseValidacao = new FileInputSynapse();
		inputSynapseValidacao.setInputFile(new File(FileManager.ENTRADA_VALIDACAO));
		inputSynapseValidacao.setAdvancedColumnSelector(ADVANCED_COLUMN_SELECTOR);

        // Switch do conjunto de dados de ENTRADA
        LearningSwitch switchEntrada = new LearningSwitch();
        switchEntrada.addTrainingSet(inputSynapseTreinamento);
        switchEntrada.addValidationSet(inputSynapseValidacao);

		// Coloca o switch de entrada como entrada da camada de entrada
		input.addInputSynapse(switchEntrada);

        // Conjunto de saída: TREINAMENTO
		FileInputSynapse desiredSynapseTreinamento = new FileInputSynapse();
		desiredSynapseTreinamento.setInputFile(new File(FileManager.SAIDA_TREINAMENTO));
		desiredSynapseTreinamento.setAdvancedColumnSelector("1-31");
        // Conjunto de saída: VALIDAÇÃO
        FileInputSynapse desiredSynapseValidacao = new FileInputSynapse();
		desiredSynapseValidacao.setInputFile(new File(FileManager.SAIDA_VALIDACAO));
		desiredSynapseValidacao.setAdvancedColumnSelector("1-31");

        // Switch do conjunto de dados de SAÍDA
        LearningSwitch switchSaida = new LearningSwitch();
        switchSaida.addTrainingSet(desiredSynapseTreinamento);
        switchSaida.addValidationSet(desiredSynapseValidacao);

        // Associa o switch de saída com o supervisor
		TeachingSynapse trainer = new TeachingSynapse();
		trainer.setDesired(switchSaida);

        // Coloca esta sinapse como saï¿½da da camada de saï¿½da
		output.addOutputSynapse(trainer);

		/* Creates the error output file */
		FileOutputSynapse error = new FileOutputSynapse();
		error.setFileName("res/error.txt");
		//error.setBuffered(false);
		trainer.addResultSynapse(error);

		// Adiciona as camadas criadas ï¿½ rede neural
		rede.addLayer(input, NeuralNet.INPUT_LAYER);
		rede.addLayer(hidden, NeuralNet.HIDDEN_LAYER);
		rede.addLayer(output, NeuralNet.OUTPUT_LAYER);
		rede.setTeacher(trainer);

		// configure monitor parameters
		Monitor monitor = rede.getMonitor();
		monitor.setLearningRate(0.4);
		monitor.setMomentum(0.5);
		monitor.setTrainingPatterns(1435); // TODO Quantidade de linhas no arquivo de entrada de treinamento
        monitor.setValidationPatterns(1414);
		monitor.setTotCicles(100);
		monitor.setLearning(true);

        startms = System.currentTimeMillis();
		rede.go(true);
		System.out.println(System.getProperty("line.separator") + "Treinamento acabou!. Último RMSE=" + rede.getMonitor().getGlobalError());
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
	 * Recebe um array de bits correspondente ï¿½ imagem do captcha jï¿½ prï¿½-processada e identifica o mesmo
	 * @param entradaDaRede O array de bits correspondente aos 5 caracteres do captcha, jï¿½ prï¿½-processados
	 * @throws IOException 
	 */
	public String identificaCaptcha(int[][] entradaDaRede) throws IOException{
		// Cria arquivo temporï¿½rio para servir de entrada para a rede
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

		// Remove sinapses de saï¿½da anteriores
		rede.getOutputLayer().removeAllOutputs();

		// Adiciona uma nova camada de saï¿½da
		MemoryOutputSynapse outputSynapse1 = new MemoryOutputSynapse();

		// Coloca esta sinapse como saï¿½da da camada de entrada
		rede.getOutputLayer().addOutputSynapse(outputSynapse1);

		rede.getMonitor().setTotCicles(1);
		rede.getMonitor().setTrainingPatterns(1);
		rede.getMonitor().setLearning(false);
		rede.go();
		double[] resposta = outputSynapse1.getNextPattern();

		return BitMapper.traduzRespostaDaRede(resposta);
	}

	/**
	 * Recebe o path para a imagem de um caractere previamente processada e realiza a identificaï¿½ï¿½o do mesmo
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
        System.out.println("Rede começou!");
    }

    public void cicleTerminated(NeuralNetEvent e) {
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

    public void netStopped(NeuralNetEvent e) {
        System.out.println("Rede terminou após " + ((System.currentTimeMillis() - startms) / 1000.0) + "s");
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
        System.out.println("    Erro de validação: "+NN.getMonitor().getGlobalError());
        if (NN.getMonitor().getGlobalError() < THRESHOLD){
            NN.stop();
        }
    }
}
