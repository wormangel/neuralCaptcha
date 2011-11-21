package br.ufcg.neuralcaptcha;

import java.io.IOException;
import java.io.Serializable;

import org.joone.engine.Monitor;
import org.joone.engine.NeuralNetEvent;
import org.joone.engine.NeuralNetListener;

import br.ufcg.neuralcaptcha.core.NeuralCaptcha;
import org.joone.net.NeuralNet;
import org.joone.net.NeuralNetValidator;
import org.joone.net.NeuralValidationEvent;
import org.joone.net.NeuralValidationListener;


public class MockMain implements NeuralNetListener, NeuralValidationListener, Serializable {

	long startms;
    NeuralCaptcha rec;
    final int INTERVALO_DE_VALIDACAO = 2;
    final double THRESHOLD = 0.1;

    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
		new MockMain();
	}

	public MockMain() throws IOException, InterruptedException, ClassNotFoundException{
		rec = new NeuralCaptcha(this);
        startms = System.currentTimeMillis();
		rec.treinaRede();
        
        //System.out.println("Resultado: " + rec.identificaCaractere(FileManager.DIRETORIO_TESTE + "a" + "\\" + "8842.bmp"));

		rec.salvarRede();
	}

	public void cicleTerminated(NeuralNetEvent e) {
        NeuralNet net = rec.redeNeural();
        
		// Prints out the cycle and the training error
        int cycle = net.getMonitor().getTotCicles() - net.getMonitor().getCurrentCicle()+1;

        System.out.println("Ciclo #"+cycle);
        System.out.println("    Erro de treinamento:   " + net.getMonitor().getGlobalError());

        if (cycle % INTERVALO_DE_VALIDACAO == 0) { // We validate the net every INTERVALO_DE_VALIDACAO cycles

            // Creates a copy of the neural network
            net.getMonitor().setExporting(true);
            NeuralNet newNet = net.cloneNet();
            net.getMonitor().setExporting(false);

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

	public void errorChanged(NeuralNetEvent e) {
		//Monitor mon = (Monitor)e.getSource();
        //System.out.println("Erro mudou: (Ciclo "+(mon.getTotCicles()-mon.getCurrentCicle())+") RMSE: "+mon.getGlobalError());
		
	}

	public void netStarted(NeuralNetEvent e) {
		System.out.println("Rede comeï¿½ou!");
		
	}

	public void netStopped(NeuralNetEvent e) {
		System.out.println("Rede parou!");
		
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
