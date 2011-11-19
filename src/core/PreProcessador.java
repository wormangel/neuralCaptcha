package core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class PreProcessador {

	// Imagem original
	private static BufferedImage imagem;
	private static int[] pixelMap;
	
	// Parâmetros do downsampling
	private static double downSampleLeft, downSampleRight, downSampleTop, downSampleBottom;
	private static double proporcaoDownSampleX, proporcaoDownSampleY;
	private static int tamanhoImagemDownSampleW = 10, tamanhoImagemDownSampleH = 15; 
	
	// Imagem processada
	private static BufferedImage downSample;
	
	public static int[] ProcessaImagem(String path) throws IOException, InterruptedException{
		
		fazDownSample(path);
		return extraiString(downSample);		
		
	}
	
	private static int[] extraiString(BufferedImage downSample) {
		int[] array = new int[150];
		int count = 0;
		for(int y = 0; y < tamanhoImagemDownSampleH; y++){
			for ( int x=0;x<tamanhoImagemDownSampleW;x++ ) {
				if (downSample.getRGB(x, y)== java.awt.Color.BLACK.getRGB()) {
					array[count] = 1;
				} else {
					array[count] = 0; 
				}
				count++;
			}
		}
		return array;
	}

	private static void fazDownSample(String path) throws IOException, InterruptedException {
		imagem = ImageIO.read(new File(path));
		downSample = new BufferedImage(tamanhoImagemDownSampleW, tamanhoImagemDownSampleH, 
				java.awt.image.BufferedImage.TYPE_INT_RGB);
		
		
		int w = imagem.getWidth();
        int h = imagem.getHeight();
        
        java.awt.image.PixelGrabber grabber = new java.awt.image.PixelGrabber(imagem,0,0,w,h,true);
        
    	grabber.grabPixels();
        pixelMap = (int[])grabber.getPixels();
        encontraBordas(w,h);
        
        // calcula a proporçãod do downsample
        proporcaoDownSampleX = (double)(downSampleRight - downSampleLeft)/(double)tamanhoImagemDownSampleW;
        proporcaoDownSampleY = (double)(downSampleBottom - downSampleTop)/(double)tamanhoImagemDownSampleH;
        
        for ( int y=0;y<tamanhoImagemDownSampleH;y++ ) {
            for ( int x=0;x<tamanhoImagemDownSampleW;x++ ) {
                if ( downSampleQuadrant(x,y) )
                    downSample.setRGB(x,y,java.awt.Color.BLACK.getRGB());
                else
                    downSample.setRGB(x,y,java.awt.Color.WHITE.getRGB());
            }
        }
	}

	private static void encontraBordas(int w, int h){
		// cima
        for ( int y=0;y<h;y++ ) {
            if ( !linhaHorizontalEstahVazia(y) ) {
                downSampleTop=y;
                break;
            }
        }
        // baixo
        for ( int y=h-1;y>=0;y-- ) {
            if ( !linhaHorizontalEstahVazia(y) ) {
                downSampleBottom=y;
                break;
            }
        }
        // esquerda
        for ( int x=0;x<w;x++ ) {
            if ( !linhaVerticalEstahVazia(x) ) {
                downSampleLeft = x;
                break;
            }
        }
        
        // direita
        for ( int x=w-1;x>=0;x-- ) {
            if ( !linhaVerticalEstahVazia(x) ) {
                downSampleRight = x;
                break;
            }
        }
	}
	
	/**
     * Verifica se existem pixels na linha horizontal especificada.
     * Utilizado no método de encontrar bordas da imagem.
     */
    private static boolean linhaHorizontalEstahVazia(int y) {
        int w = imagem.getWidth();
        for ( int i=0;i<w;i++ ) {
            if ( pixelMap[(y*w)+i] !=-1 )
                return false;
        }
        return true;
    }
    
    /**
     * Verifica se existem pixels na linha vertical especificada.
     * Utilizado no método de encontrar bordas da imagem.
     */
    private static boolean linhaVerticalEstahVazia(int x) {
        int w = imagem.getWidth();
        int h = imagem.getHeight();
        for ( int i=0;i<h;i++ ) {
            if ( pixelMap[(i*w)+x] !=-1 )
                return false;
        }
        return true;
    }

    /**
     * Called to downsample a quadrant of the image.
     *
     * @param x The x coordinate of the resulting
     * downsample.
     * @param y The y coordinate of the resulting
     * downsample.
     * @return Returns true if there were ANY pixels
     * in the specified quadrant.
     */
    protected static boolean downSampleQuadrant(int x,int y) {
        int w = imagem.getWidth();
        int startX = (int)(downSampleLeft+(x*proporcaoDownSampleX));
        int startY = (int)(downSampleTop+(y*proporcaoDownSampleY));
        int endX = (int)(startX + proporcaoDownSampleX);
        int endY = (int)(startY + proporcaoDownSampleY);
        
        for ( int yy=startY;yy<=endY;yy++ ) {
            for ( int xx=startX;xx<=endX;xx++ ) {
                int loc = xx+(yy*w);
                
                if ( pixelMap[ loc  ]!= -1 )
                    return true;
            }
        }
        
        return false;
    }

    
}
