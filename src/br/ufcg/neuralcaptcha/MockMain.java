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


public class MockMain {

    NeuralCaptcha rec;

    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
		new MockMain();
	}

	public MockMain() throws IOException, InterruptedException, ClassNotFoundException{
		rec = new NeuralCaptcha();
		rec.treinaRede();
        
        //System.out.println("Resultado: " + rec.identificaCaractere(FileManager.DIRETORIO_TESTE + "a" + "\\" + "8842.bmp"));

		rec.salvarRede();
	}

}
