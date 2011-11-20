package br.ufcg.neuralcaptcha.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class PreProcessor {

	public static void processaImagem() throws IOException, InterruptedException {
		Process p = Runtime.getRuntime().exec("ruby src/processor.rb");
		p.waitFor();
		BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line = "";
		while ((line = buf.readLine()) != null) {
			System.out.println(line);
		}
	}

}
