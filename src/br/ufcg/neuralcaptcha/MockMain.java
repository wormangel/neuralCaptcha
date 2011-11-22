package br.ufcg.neuralcaptcha;

import java.io.IOException;

import br.ufcg.neuralcaptcha.core.NeuralCaptcha;


public class MockMain {

    NeuralCaptcha rec;

    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
		new MockMain();
	}

	public MockMain() throws IOException, InterruptedException, ClassNotFoundException{
		rec = new NeuralCaptcha();
        //rec.carregarRede();
		rec.treinaRede();
        rec.executaConjuntoDeTeste();
        
        //System.out.println("Resultado: " + rec.identificaCaractere(FileManager.DIRETORIO_TESTE + "l" + "\\" + "7012.bmp"));

		//rec.salvarRede();
	}

}
