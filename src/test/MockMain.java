package test;

import java.io.IOException;

import org.joone.engine.Monitor;
import org.joone.engine.NeuralNetEvent;
import org.joone.engine.NeuralNetListener;

import core.PreProcessador;
import core.NeuralCaptcha;

public class MockMain implements NeuralNetListener {

	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
		MockMain mock = new MockMain();
	}
	
	public MockMain() throws IOException, InterruptedException, ClassNotFoundException{
		NeuralCaptcha rec = new NeuralCaptcha(this);
		
		rec.treinaRede();
		
		//rec.SalvarRede();
		//rec.validaRede();
		
		//System.out.println(rec.identificaNumero("C:\\temp\\porFuncao\\validacao\\letras\\A\\3 (2).jpg"));;
	}

	public void cicleTerminated(NeuralNetEvent e) {
		System.out.println("Ciclo terminado." + e.getSource());
		
	}

	public void errorChanged(NeuralNetEvent e) {
		Monitor mon = (Monitor)e.getSource();
        System.out.println("Ciclo: "+(mon.getTotCicles()-mon.getCurrentCicle())+" RMSE:"+mon.getGlobalError());
		
	}

	public void netStarted(NeuralNetEvent e) {
		System.out.println("Treinando...");
		
	}

	public void netStopped(NeuralNetEvent e) {
		System.out.println("Treinado!");
		
	}

	public void netStoppedError(NeuralNetEvent e, String error) {
		System.out.println("Erro! " + error);
		
	}

}
