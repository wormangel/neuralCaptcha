package br.ufcg.neuralcaptcha;
import java.io.IOException;

import br.ufcg.neuralcaptcha.core.NeuralCaptcha;



public class Main {
	
	public static void main(String args[]) throws IOException, InterruptedException {
    	NeuralCaptcha rec = new NeuralCaptcha();
        rec.inicializaRede();
        rec.treinaRede();
    }
	
}
