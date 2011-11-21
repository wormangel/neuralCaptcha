package br.ufcg.neuralcaptcha.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import br.ufcg.neuralcaptcha.core.NeuralCaptcha;
import br.ufcg.neuralcaptcha.util.FileManager;

public class UI {

	private static Display display;
	private static NeuralCaptcha network;

	private Shell shell;
	private Text inputBox;
	
	public UI(Display display) {
		shell = new Shell(display);
		shell.setText("        Projeto de IA");
		shell.setSize(230, 310);

		configureLayout(shell);
		addElementsOnShell();
		center(shell);

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void configureLayout(Shell shell) {
		RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
		rowLayout.marginTop = 10;
        rowLayout.marginLeft = 10;
        shell.setLayout(rowLayout);
	}

	private void addElementsOnShell() {
		final Label label = new Label(shell, SWT.NONE);
		label.setLayoutData(new RowData(205, 71));
		label.setText("Click to get captcha!");

		Button getCaptchaButton = new Button(shell, SWT.PUSH);
		getCaptchaButton.setText("Get captcha!");
		getCaptchaButton.setLayoutData(new RowData(205, 30));
		getCaptchaButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				Image image;
				try {
					image = new Image(display, FileManager.downloadSample());
					label.setImage(image);
				} catch (Exception e) {
					e.printStackTrace();
					inputBox.setText("ERROR!!!");
				}
			}
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});

		Button loadNetworkButton = new Button(shell, SWT.PUSH);
		loadNetworkButton.setText("Load network");
		loadNetworkButton.setLayoutData(new RowData(205, 30));
		loadNetworkButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				try {
					network.carregarRede();
				} catch (Exception e) {
					e.printStackTrace();
					inputBox.setText("ERROR!!!");
				}
			}
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
		
		Button trainButton = new Button(shell, SWT.PUSH);
		trainButton.setText("Train network");
		trainButton.setLayoutData(new RowData(205, 30));
		trainButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				try {
					network.treinaRede();
				} catch (Exception e) {
					e.printStackTrace();
					inputBox.setText("ERROR!!!");
				}
			}
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
		
		Button saveNetworkButton = new Button(shell, SWT.PUSH);
		saveNetworkButton.setText("Save network");
		saveNetworkButton.setLayoutData(new RowData(205, 30));
		saveNetworkButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				try {
					network.salvarRede();
				} catch (Exception e) {
					e.printStackTrace();
					inputBox.setText("ERROR!!!");
				}
			}
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});

		Button recognizeButton = new Button(shell, SWT.PUSH);
		recognizeButton.setText("Recognize!");
		recognizeButton.setLayoutData(new RowData(205, 30));
		recognizeButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent arg0) {
				try {
					inputBox.setText(network.identificaCaptcha());
				} catch (Exception e) {
					e.printStackTrace();
					inputBox.setText("ERROR!!!");
				}
			}
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});

		inputBox = new Text(shell, SWT.BORDER);
		inputBox.setLayoutData(new RowData(195, 18));
	}



	public void center(Shell shell) {
		Rectangle bds = shell.getDisplay().getBounds();
		Point p = shell.getSize();

		int nLeft = (bds.width - p.x) / 2;
		int nTop = (bds.height - p.y) / 2;

		shell.setBounds(nLeft, nTop, p.x, p.y);
	}

	public static void main(String[] args) {
		network = new NeuralCaptcha();
		display = new Display();
		new UI(display);
		display.dispose();
	}

}
