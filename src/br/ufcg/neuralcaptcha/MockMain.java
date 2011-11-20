package br.ufcg.neuralcaptcha;

import java.io.IOException;

import org.joone.engine.Monitor;
import org.joone.engine.NeuralNetEvent;
import org.joone.engine.NeuralNetListener;

import br.ufcg.neuralcaptcha.core.NeuralCaptcha;
import br.ufcg.neuralcaptcha.util.FileManager;


public class MockMain implements NeuralNetListener {

	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
		new MockMain();
	}
	
	public MockMain() throws IOException, InterruptedException, ClassNotFoundException{
		NeuralCaptcha rec = new NeuralCaptcha(this);
		rec.treinaRede();
        
        //System.out.println("Resultado: " + rec.identificaCaractere(FileManager.DIRETORIO_TESTE + "a" + "\\" + "8842.bmp"));
        //System.out.println("Resultado: " + rec.identificaCaractere(FileManager.DIRETORIO_TESTE + "b" + "\\" + "8570.bmp"));
		
		rec.salvarRede();
		//rec.validaRede();
	}

	public void cicleTerminated(NeuralNetEvent e) {
		System.out.println("Ciclo terminado." + e.getSource());
		
	}

	public void errorChanged(NeuralNetEvent e) {
		Monitor mon = (Monitor)e.getSource();
        System.out.println("Ciclo: "+(mon.getTotCicles()-mon.getCurrentCicle())+" RMSE:"+mon.getGlobalError());
		
	}

	public void netStarted(NeuralNetEvent e) {
		System.out.println("Rede começou!");
		
	}

	public void netStopped(NeuralNetEvent e) {
		System.out.println("Rede parou!");
		
	}

	public void netStoppedError(NeuralNetEvent e, String error) {
		System.out.println("Erro! " + error);
		
	}

}
