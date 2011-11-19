package ui;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

import org.joone.engine.Monitor;
import org.joone.engine.NeuralNetEvent;
import org.joone.engine.NeuralNetListener;

import br.ufcg.neuralcaptcha.core.NeuralCaptcha;


public class ReconhecedorSwing extends JFrame implements NeuralNetListener {
	
	private static NeuralCaptcha reconhecedor = null;

	private JButton btnTreinar, btnValidar;
	
	private JTextField txtInputTreinamento, txtInputValidacao, txtInputVerificacao,
		txtOutputTreinamento, txtOutputValidacao, txtOutputVerificacao;
	
	// Bot�o para escolher a placa (diret�rio com as fotos dos caracteres individuais da placa)
	private JButton btnProcurarPlaca;
	
	public static void main(String[] args) {
		ReconhecedorSwing rec = new ReconhecedorSwing();
		
		reconhecedor = new NeuralCaptcha(rec);
		reconhecedor.inicializaRede();

	}
	
	public ReconhecedorSwing() {
		btnTreinar = new JButton("Treinar as redes");
		getContentPane().add(btnTreinar);
		btnValidar = new JButton("Validar as redes");
		getContentPane().add(btnValidar);
		// btnVerificar = new JButton("Verificar placa de carro");
		//getContentPane().add(btnVerificar);
	}
	

	public void cicleTerminated(NeuralNetEvent e) {
		// TODO Auto-generated method stub

	}

	public void errorChanged(NeuralNetEvent e) {
		Monitor mon = (Monitor)e.getSource();
        System.out.println("Cycle: "+(mon.getTotCicles()-mon.getCurrentCicle())+" RMSE:"+mon.getGlobalError());
	}

	public void netStarted(NeuralNetEvent e) {
		System.out.println("Training...");
	}

	public void netStopped(NeuralNetEvent e) {
		System.out.println("Stopped");
	}

	public void netStoppedError(NeuralNetEvent e, String error) {
		// TODO Auto-generated method stub

	}

}
