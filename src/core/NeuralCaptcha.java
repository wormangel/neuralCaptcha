package core;

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

/**
 * Classe principal do projeto, cont�m as redes neurais MLP e fornece m�todos para que uma aplica��o efetue
 * opera��es usando a rede.
 * As seguintes letras n�o foram consideradas devido � escassez de samples: E,H,J,Q,S,T,V,W,X
 * Os seguintes n�meros n�o foram considerados devido � escassez de samples: 
 * @author Lucas Medeiros, V�tor Amaral, V�tor Avelino
 *
 */
public class NeuralCaptcha {

	private final int numeroDeSaidasLetra = 17; // Excluindo 9 letras
	private final int numeroDeSaidasNumero = 10;
	
	private final String diretorioArquivosTreinamentoLetras = "C:\\temp\\porFuncao\\treinamento\\letras";
	private final String diretorioArquivosTreinamentoNumeros = "C:\\temp\\porFuncao\\treinamento\\numeros";
	private final String diretorioArquivosValidacaoLetras = "C:\\temp\\porFuncao\\validacao\\letras";
	private final String diretorioArquivosValidacaoNumeros = "C:\\temp\\porFuncao\\validacao\\numeros";
	
	// Treinamento
	private final String arquivoEntradaTreinamentoLetras = "C:\\temp\\inputTreinamentoLetras.txt";
	private final String arquivoSaidaTreinamentoLetras = "C:\\temp\\outputTreinamentoLetras.txt";
	private final String arquivoEntradaTreinamentoNumeros = "C:\\temp\\inputTreinamentoNumeros.txt";
	private final String arquivoSaidaTreinamentoNumeros = "C:\\temp\\outputTreinamentoNumeros.txt";
	
	// Valida��o
	private final String arquivoEntradaValidacaoLetras = "C:\\temp\\inputValidacaoLetras.txt";
	private final String arquivoSaidaValidacaoLetras = "C:\\temp\\outputValidacaoLetras.txt";
	private final String arquivoEntradaValidacaoNumeros = "C:\\temp\\inputValidacaoNumeros.txt";
	private final String arquivoSaidaValidacaoNumeros = "C:\\temp\\outputValidacaoNumeros.txt";

	private NeuralNet redeAlfabeto = null;
	private NeuralNet redeNumeros = null;
	
    public static void main(String args[]) throws IOException, InterruptedException {
    	NeuralCaptcha rec = new NeuralCaptcha();
        
        rec.inicializaRedes();
        rec.treinaRedes();
    }
    
    public int[] preProcessa(String caminhoImagem) throws IOException, InterruptedException{
    	return PreProcessador.ProcessaImagem(caminhoImagem);
    }
    
    /**
     * Recebe uma string correspondente ao caminho no disco para a imagem a ser identificada.
     * @param pathImagem
     * @throws InterruptedException 
     * @throws IOException 
     */
    public String identificaNumero(String pathImagem) throws IOException, InterruptedException {
    	int[] entradaDaRede = preProcessa(pathImagem);
    	return identificaNumero(entradaDaRede);
    }
    
    /**
     * Recebe um array de bits correspondente � imagem downsamplezada e identifica a placa
     * @param entradaDaRede A imagem, j� pre-processada
     * @throws IOException 
     */
    public String identificaNumero(int[] entradaDaRede) throws IOException{
    	// Cria arquivo tempor�rio para servir de entrada para a rede
    	File arquivoDeEntrada = CriaArquivoParaReconhecimento(entradaDaRede);
    	
    	// Remove sinapses de entrada anteriores
    	redeNumeros.getInputLayer().removeAllInputs();
    	
    	// Adiciona uma nova camada de entrada 
		FileInputSynapse inputSynapse1 = new FileInputSynapse();

		inputSynapse1.setInputFile(arquivoDeEntrada);
		inputSynapse1.setName("input1");
		inputSynapse1.setAdvancedColumnSelector("1-150");
		inputSynapse1.setFirstRow(1);
		inputSynapse1.setLastRow(1); // Somente uma linha, correspondente ao n�mero a ser identificado

		// Coloca esta sinapse como entrada da camada de entrada
		redeNumeros.getInputLayer().addInputSynapse(inputSynapse1);
		
		// Remove sinapses de sa�da anteriores
		redeNumeros.getOutputLayer().removeAllOutputs();
    	
    	// Adiciona uma nova camada de sa�da
		MemoryOutputSynapse outputSynapse1 = new MemoryOutputSynapse();

		// Coloca esta sinapse como sa�da da camada de entrada
		redeNumeros.getOutputLayer().addOutputSynapse(outputSynapse1);
		
		redeNumeros.getMonitor().setTotCicles(1);
		redeNumeros.getMonitor().setTrainingPatterns(1);
		redeNumeros.getMonitor().setLearning(false);
		redeNumeros.go();
		double[] resposta = outputSynapse1.getNextPattern();
		
		return Util.converteArrayDeBitsParaNumero(resposta);
	}
    
	/**
     * Recebe uma string correspondente ao caminho no disco para a imagem a ser identificada.
     * @param pathImagem
     * @throws InterruptedException 
     * @throws IOException 
     */
    public String identificaLetra(String pathImagem) throws IOException, InterruptedException {
    	int[] entradaDaRede = preProcessa(pathImagem);
    	return identificaLetra(entradaDaRede);
    }
    
	/**
     * Recebe um array de bits correspondente � imagem downsamplezada e identifica a placa
     * @param entradaDaRede A imagem, j� pre-processada
     * @throws IOException 
     */
    public String identificaLetra(int[] entradaDaRede) throws IOException{
    	// Cria arquivo tempor�rio para servir de entrada para a rede
    	File arquivoDeEntrada = CriaArquivoParaReconhecimento(entradaDaRede);
    	
    	// Remove sinapses de entrada anteriores
    	redeAlfabeto.getInputLayer().removeAllInputs();
    	
    	// Adiciona uma nova camada de entrada 
		FileInputSynapse inputSynapse1 = new FileInputSynapse();

		inputSynapse1.setInputFile(arquivoDeEntrada);
		inputSynapse1.setName("input1");
		inputSynapse1.setAdvancedColumnSelector("1-150");
		inputSynapse1.setFirstRow(1);
		inputSynapse1.setLastRow(1); // Apenas a letra que o cara quer identificar

		// Coloca esta sinapse como entrada da camada de entrada
		redeAlfabeto.getInputLayer().addInputSynapse(inputSynapse1);
		
		// Remove sinapses de sa�da anteriores
    	redeAlfabeto.getOutputLayer().removeAllOutputs();
    	
    	// Adiciona uma nova camada de sa�da
		MemoryOutputSynapse outputSynapse1 = new MemoryOutputSynapse();

		// Coloca esta sinapse como sa�da da camada de entrada
		redeAlfabeto.getOutputLayer().addOutputSynapse(outputSynapse1);
		
		redeAlfabeto.getMonitor().setTotCicles(1);
		redeAlfabeto.getMonitor().setTrainingPatterns(1);
		redeAlfabeto.getMonitor().setLearning(false);
		redeAlfabeto.go();
		double[] resposta = outputSynapse1.getNextPattern();
		
		return Util.converteArrayDeBitsParaLetra(resposta);
	}

	public void treinaRedes() throws IOException, InterruptedException {		
    	treinaRedeAlfabeto();
    	treinaRedeNumeros();    	
    }
	
	/**
	 * Recebe um array de bits e escreve em um arquivo que servir� de entrada para a rede neural.
	 * @param entradaDaRede
	 * @return
	 * @throws IOException
	 */
	private File CriaArquivoParaReconhecimento(int[] entradaDaRede) throws IOException{
		File arquivoDeEntrada = new File("C:\\entradaNeural.txt");
    	FileWriter writer = new FileWriter(arquivoDeEntrada);
    	writer.flush();
    	StringBuilder str = new StringBuilder();
    	for(int i = 0; i < entradaDaRede.length; i++){
    		str.append(entradaDaRede.toString());
    		str.append(";");
    	}
    	str.deleteCharAt(str.length()-1);
    	writer.write(str.toString());
    	writer.close();
    	return arquivoDeEntrada;
	}
    
    protected void treinaRedeAlfabeto() throws IOException, InterruptedException{
    	
    	// Prepara a entrada para o treinamento da rede
    	geraArquivosDeTreinamentoParaLetras();
    	
		FileInputSynapse inputSynapse1 = new FileInputSynapse();

		inputSynapse1.setInputFile(new File(arquivoEntradaTreinamentoLetras));
		inputSynapse1.setName("input1");
		inputSynapse1.setAdvancedColumnSelector("1-150");
		inputSynapse1.setFirstRow(1);
		inputSynapse1.setLastRow(54); // 26 letras - 9 exclu�das

		// Coloca esta sinapse como entrada da camada de entrada
		redeAlfabeto.getInputLayer().addInputSynapse(inputSynapse1);

		// Prepara a sa�da para o treinamento da rede
		FileInputSynapse desiredSynapse1 = new FileInputSynapse();
		
		desiredSynapse1.setInputFile(new File(arquivoSaidaTreinamentoLetras));
		desiredSynapse1.setName("desired1");
		desiredSynapse1.setAdvancedColumnSelector("1-150");
		desiredSynapse1.setFirstRow(1);
		desiredSynapse1.setLastRow(54);

		TeachingSynapse trainer = new TeachingSynapse();
		trainer.setDesired(desiredSynapse1);
		
		// Coloca esta sinapse como sa�da da camada de sa�da
		redeAlfabeto.setTeacher(trainer);
		redeAlfabeto.getOutputLayer().addOutputSynapse(trainer);
    	
    	// get the monitor object to train or feed forward
        Monitor monitor = redeAlfabeto.getMonitor();
        
        // set the monitor parameters
        monitor.setLearningRate(0.8);
        monitor.setMomentum(0.3);
        monitor.setTrainingPatterns(54);
        monitor.setTotCicles(150);
        monitor.setLearning(true);
        redeAlfabeto.go(true);
        System.out.println("Treinamento acabou!. �ltimo RMSE="+redeAlfabeto.getMonitor().getGlobalError());
    }
    
    protected void treinaRedeNumeros() throws IOException, InterruptedException{
    	
    	// Prepara a entrada para o treinamento da rede
    	geraArquivosDeTreinamentoParaNumeros();
    	
		FileInputSynapse inputSynapse1 = new FileInputSynapse();

		inputSynapse1.setInputFile(new File(arquivoEntradaTreinamentoNumeros));
		inputSynapse1.setName("input1");
		inputSynapse1.setAdvancedColumnSelector("1-150");
		inputSynapse1.setFirstRow(1);
		inputSynapse1.setLastRow(76); // 10 d�gitos

		// Coloca esta sinapse como entrada da camada de entrada
		redeNumeros.getInputLayer().addInputSynapse(inputSynapse1);

		// Prepara a sa�da para o treinamento da rede
		FileInputSynapse desiredSynapse1 = new FileInputSynapse();
		
		desiredSynapse1.setInputFile(new File(arquivoSaidaTreinamentoNumeros));
		desiredSynapse1.setName("desired1");
		desiredSynapse1.setAdvancedColumnSelector("1-150");
		desiredSynapse1.setFirstRow(1);
		desiredSynapse1.setLastRow(76);

		TeachingSynapse trainer = new TeachingSynapse();
		trainer.setDesired(desiredSynapse1);
    	
		// Coloca esta sinapse como sa�da da camada de sa�da
		redeNumeros.setTeacher(trainer);
		redeNumeros.getOutputLayer().addOutputSynapse(trainer);
    	
    	// get the monitor object to train or feed forward
        Monitor monitor = redeNumeros.getMonitor();
        
        // set the monitor parameters
        monitor.setLearningRate(0.8);
        monitor.setMomentum(0.3);
        monitor.setTrainingPatterns(76);
        monitor.setTotCicles(150);
        monitor.setLearning(true);
        redeNumeros.go(true);
        System.out.println("Treinamento acabou! �ltimo RMSE="+redeNumeros.getMonitor().getGlobalError());
    }
    
    public void validaRedes() throws IOException, InterruptedException {
    	validaRedeAlfabeto();
    	//validaRedeNumeros();
    }
    
    public void validaRedeAlfabeto() throws IOException, InterruptedException{
    	// Prepara a entrada para o treinamento da rede
    	geraArquivosDeValidacaoParaLetras();
    	
		FileInputSynapse inputSynapse1 = new FileInputSynapse();

		inputSynapse1.setInputFile(new File(arquivoEntradaValidacaoLetras));
		inputSynapse1.setName("input1");
		inputSynapse1.setAdvancedColumnSelector("1-150");
		inputSynapse1.setFirstRow(1);
		inputSynapse1.setLastRow(54); // 26 letras - 9 exclu�das

		// Coloca esta sinapse como entrada da camada de entrada
		redeAlfabeto.getInputLayer().addInputSynapse(inputSynapse1);

		// Prepara a sa�da para o treinamento da rede
		FileInputSynapse desiredSynapse1 = new FileInputSynapse();
		
		desiredSynapse1.setInputFile(new File(arquivoSaidaValidacaoLetras));
		desiredSynapse1.setName("desired1");
		desiredSynapse1.setAdvancedColumnSelector("1-150");
		desiredSynapse1.setFirstRow(1);
		desiredSynapse1.setLastRow(54);

		TeachingSynapse trainer = new TeachingSynapse();
		trainer.setDesired(desiredSynapse1);
		
		// Coloca esta sinapse como sa�da da camada de sa�da
		redeAlfabeto.setTeacher(trainer);
		redeAlfabeto.getOutputLayer().addOutputSynapse(trainer);
    	
    	// get the monitor object to train or feed forward
        Monitor monitor = redeAlfabeto.getMonitor();
        
        // set the monitor parameters
        monitor.setLearningRate(0.8);
        monitor.setMomentum(0.3);
        monitor.setTrainingPatterns(54);
        monitor.setTotCicles(150);
        monitor.setLearning(false);
        redeAlfabeto.go(true);
        System.out.println("Network stopped. Last RMSE="+redeAlfabeto.getMonitor().getGlobalError());
    }
    
	public void inicializaRedes() {
		inicializaRedeAlfabeto();
		inicializaRedeNumeros();
	}

	private void inicializaRedeAlfabeto(){
		// Rede para letras

		// Camadas da rede
		LinearLayer input = new LinearLayer();
		SigmoidLayer hidden = new SigmoidLayer();
		SigmoidLayer output = new SigmoidLayer();

		// Tamanhos
		input.setRows(150); // 10x15 pixels cada caractere
		hidden.setRows(100);
		output.setRows(17);

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
		redeAlfabeto = new NeuralNet();

		redeAlfabeto.addLayer(input, NeuralNet.INPUT_LAYER);
		redeAlfabeto.addLayer(hidden, NeuralNet.HIDDEN_LAYER);
		redeAlfabeto.addLayer(output, NeuralNet.OUTPUT_LAYER);
	}

	private void inicializaRedeNumeros(){
		// Rede para n�meros

		// Camadas da rede
		LinearLayer input = new LinearLayer();
		SigmoidLayer hidden = new SigmoidLayer();
		SigmoidLayer output = new SigmoidLayer();

		// Tamanhos
		input.setRows(150); // 10x15 pixels cada caractere
		hidden.setRows(100);
		output.setRows(10);

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
		redeNumeros = new NeuralNet();

		redeNumeros.addLayer(input, NeuralNet.INPUT_LAYER);
		redeNumeros.addLayer(hidden, NeuralNet.HIDDEN_LAYER);
		redeNumeros.addLayer(output, NeuralNet.OUTPUT_LAYER);

	}
	
	public void adicionaListener(NeuralNetListener listener){
		redeAlfabeto.addNeuralNetListener(listener);
		redeNumeros.addNeuralNetListener(listener);
	}
	
	public NeuralCaptcha(){}
	
	/**
	 * Cria uma inst�ncia do reconhecedor e se registra como observador dos eventos das redes neurais.
	 */
	public NeuralCaptcha(NeuralNetListener listener){
		inicializaRedes();
		adicionaListener(listener);
	}
	
	public NeuralCaptcha(NeuralNetListener listener, boolean carregaRede) throws IOException, ClassNotFoundException{
		if (carregaRede) {
			carregaRedes();
		} else {
			inicializaRedes();
		}		
		
		adicionaListener(listener);
	}

	private void geraArquivosDeTreinamentoParaLetras() throws IOException, InterruptedException{
		File diretoriosTreinamento = new File(diretorioArquivosTreinamentoLetras);

		File inputTreinamento = new File(arquivoEntradaTreinamentoLetras);
		FileWriter writerInput = new FileWriter(inputTreinamento);
		writerInput.flush();
		
		File outputTreinamento = new File(arquivoSaidaTreinamentoLetras);
		FileWriter writerOutput = new FileWriter(outputTreinamento);
		writerOutput.flush();
		
		// Pra cada diret�rio (letra) no diret�rio de treinamento
		for (String dirLetra : diretoriosTreinamento.list()) {
			if (dirLetra.equals(".svn") || dirLetra.length() > 1){
				continue;
			}
			// Obt�m a lista de imagens no diret�rio dessa letra
			File dirComImagensDaLetra = new File(diretorioArquivosTreinamentoLetras + "\\" + dirLetra);
			// Pra cada imagem dessa letra
			for (String arquivoImagem : dirComImagensDaLetra.list()) {
				if (arquivoImagem.contains("svn")){
					continue;
				}
				// Faz downsample e converte para array de ints
				int[] imagemEmBits = preProcessa(dirComImagensDaLetra + "\\" + arquivoImagem);
				// Converte para string e escreve no arquivo de entrada
				writerInput.write(Util.converteArrayDeBitsParaString(imagemEmBits) + "\n");
				
				// Escreve no arquivo de sa�da esperada a linha correspondente ao caractere
				writerOutput.write(Util.converteDeLetraParaArrayDeBits(dirLetra) + "\n");
			}
		}
		
		writerInput.close();
		writerOutput.close();		
	}
	
	private void geraArquivosDeValidacaoParaLetras() throws IOException, InterruptedException{
		File diretoriosValidacao = new File(diretorioArquivosValidacaoLetras);

		File inputValidacao = new File(arquivoEntradaValidacaoLetras);
		FileWriter writerInput = new FileWriter(inputValidacao);
		writerInput.flush();
		
		File outputValidacao = new File(arquivoSaidaValidacaoLetras);
		FileWriter writerOutput = new FileWriter(outputValidacao);
		writerOutput.flush();
		
		// Pra cada diret�rio (letra) no diret�rio de valida��o
		for (String dirLetra : diretoriosValidacao.list()) {
			if (dirLetra.equals(".svn") || dirLetra.length() > 1){
				continue;
			}
			// Obt�m a lista de imagens no diret�rio dessa letra
			File dirComImagensDaLetra = new File(diretorioArquivosValidacaoLetras + "\\" + dirLetra);
			// Pra cada imagem dessa letra
			for (String arquivoImagem : dirComImagensDaLetra.list()) {
				if (arquivoImagem.contains("svn")){
					continue;
				}
				// Faz downsample e converte para array de ints
				int[] imagemEmBits = preProcessa(dirComImagensDaLetra + "\\" + arquivoImagem);
				// Converte para string e escreve no arquivo de entrada
				writerInput.write(Util.converteArrayDeBitsParaString(imagemEmBits) + "\n");
				
				// Escreve no arquivo de sa�da esperada a linha correspondente ao caractere
				writerOutput.write(Util.converteDeLetraParaArrayDeBits(dirLetra) + "\n");
			}
		}
		
		writerInput.close();
		writerOutput.close();		
	}

	private void geraArquivosDeTreinamentoParaNumeros() throws IOException, InterruptedException{
		File diretoriosTreinamento = new File(diretorioArquivosTreinamentoNumeros);

		File inputTreinamento = new File(arquivoEntradaTreinamentoNumeros);
		FileWriter writerInput = new FileWriter(inputTreinamento);
		writerInput.flush();
		
		File outputTreinamento = new File(arquivoSaidaTreinamentoNumeros);
		FileWriter writerOutput = new FileWriter(outputTreinamento);
		writerOutput.flush();
		
		// Pra cada diret�rio (n�mero) no diret�rio de treinamento
		for (String dirNumero : diretoriosTreinamento.list()) {
			if (dirNumero.equals(".svn") || dirNumero.length() > 1){
				continue;
			}
			// Obt�m a lista de imagens no diret�rio dessa letra
			File dirComImagensDoNumero = new File(diretorioArquivosTreinamentoNumeros + "\\" + dirNumero);
			// Pra cada imagem dessa n�mero
			for (String arquivoImagem : dirComImagensDoNumero.list()) {
				if (arquivoImagem.contains("svn")){
					continue;
				}
				// Faz downsample e converte para array de ints
				int[] imagemEmBits = preProcessa(dirComImagensDoNumero + "\\" + arquivoImagem);
				// Converte para string e escreve no arquivo de entrada
				writerInput.write(Util.converteArrayDeBitsParaString(imagemEmBits) + "\n");
				
				// Escreve no arquivo de sa�da esperada a linha correspondente ao caractere
				writerOutput.write(Util.converteDeNumeroParaArrayDeBits(dirNumero) + "\n");
			}
		}
		
		writerInput.close();
		writerOutput.close();	
	}

	private void geraArquivosDeValidacaoParaNumeros() throws IOException, InterruptedException{
		File diretoriosValidacao = new File(diretorioArquivosValidacaoNumeros);

		File inputValidacao = new File(arquivoEntradaValidacaoNumeros);
		FileWriter writerInput = new FileWriter(inputValidacao);
		writerInput.flush();
		
		File outputValidacao = new File(arquivoSaidaValidacaoNumeros);
		FileWriter writerOutput = new FileWriter(outputValidacao);
		writerOutput.flush();
		
		// Pra cada diret�rio (letra) no diret�rio de valida��o
		for (String dirNumero : diretoriosValidacao.list()) {
			if (dirNumero.equals(".svn") || dirNumero.length() > 1){
				continue;
			}
			// Obt�m a lista de imagens no diret�rio dessa letra
			File dirComImagensDoNumero = new File(diretorioArquivosValidacaoNumeros + "\\" + dirNumero);
			// Pra cada imagem dessa letra
			for (String arquivoImagem : dirComImagensDoNumero.list()) {
				if (arquivoImagem.contains("svn")){
					continue;
				}
				// Faz downsample e converte para array de ints
				int[] imagemEmBits = preProcessa(dirComImagensDoNumero + "\\" + arquivoImagem);
				// Converte para string e escreve no arquivo de entrada
				writerInput.write(Util.converteArrayDeBitsParaString(imagemEmBits) + "\n");
				
				// Escreve no arquivo de sa�da esperada a linha correspondente ao caractere
				writerOutput.write(Util.converteDeNumeroParaArrayDeBits(dirNumero) + "\n");
			}
		}
		
		writerInput.close();
		writerOutput.close();		
	}

	public void SalvarRede() throws IOException{
		salvaRedeAlfabeto();
		salvaRedeNumeros();
	}
	
	public void salvaRedeAlfabeto() throws IOException{
		FileOutputStream stream = new FileOutputStream("C:\\temp\\redeNeuralAlfabeto.mlp");
		ObjectOutputStream out = new ObjectOutputStream(stream);
		out.writeObject(redeAlfabeto);
		out.close();
	}
	
	public void salvaRedeNumeros() throws IOException{
		FileOutputStream stream = new FileOutputStream("C:\\temp\\redeNeuralNumeros.mlp");
		ObjectOutputStream out = new ObjectOutputStream(stream);
		out.writeObject(redeNumeros);
		out.close();
	}
	
	public void carregaRedes() throws IOException, ClassNotFoundException {
		File redeLetrasSalva = new File("C:\\temp\\redeNeuralAlfabeto.mlp");
		if (redeLetrasSalva.exists()) {
			carregaRedeAlfabeto();
		} else {
			inicializaRedeAlfabeto();
		}			
		
		File redeNumerosSalva = new File("C:\\temp\\redeNeuralNumeros.mlp");
		if (redeNumerosSalva.exists()) {
			carregaRedeNumeros();
		} else {
			inicializaRedeNumeros();
		}
	}
	
	public void carregaRedeAlfabeto() throws IOException, ClassNotFoundException{
		FileInputStream stream = new FileInputStream("C:\\temp\\redeNeuralAlfabeto.mlp");
		ObjectInputStream out = new ObjectInputStream(stream);
		redeNumeros = (NeuralNet) out.readObject();
	}
	
	public void carregaRedeNumeros() throws IOException, ClassNotFoundException{
		FileInputStream stream = new FileInputStream("C:\\temp\\redeNeuralNumeros.mlp");
		ObjectInputStream out = new ObjectInputStream(stream);
		redeNumeros = (NeuralNet) out.readObject();
	}

}
