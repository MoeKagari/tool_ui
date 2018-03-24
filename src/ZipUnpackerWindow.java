import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.zip.ZipFile;

import javafx.scene.Parent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tool.function.FunctionUtils;

public class ZipUnpackerWindow extends WindowBase {
	public static void main(String[] args) {
		ZipUnpackerWindow.launch(args);
	}

	@Override
	protected Parent createParent(Stage primaryStage) {
		Pane pane = new Pane();

		pane.setOnDragOver(ev -> {
			ev.acceptTransferModes(TransferMode.ANY);
		});
		pane.setOnDragDropped(ev -> {
			Dragboard dragboard = ev.getDragboard();
			if (dragboard.hasFiles()) {
				dragboard.getFiles()
						.forEach(file -> {
							Thread unpackFileThread = new Thread(() -> {
								this.unpackFile(file);
							});
							unpackFileThread.setDaemon(true);
							unpackFileThread.start();
						});
			}
		});

		return pane;
	}

	private void unpackFile(File sourceFile) {
		if (FunctionUtils.isFalse(sourceFile.exists()) || sourceFile.isDirectory()) {
			return;
		}

		long startTime = System.currentTimeMillis();
		{
			String directory = sourceFile.getParentFile().getAbsolutePath() + File.separator + startTime;
			try {
				Files.createDirectories(new File(directory).toPath());
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}

			try (ZipFile zipFile = new ZipFile(sourceFile, Charset.forName("shift-jis"))) {
				Collections.list(zipFile.entries())
						.parallelStream()
						.forEach(zipEntry -> {
							try {
								Path targetPath = new File(directory + File.separator + zipEntry.getName()).toPath();
								Files.createDirectories(targetPath.getParent());
								Files.copy(zipFile.getInputStream(zipEntry), targetPath, StandardCopyOption.REPLACE_EXISTING);
								System.out.println(targetPath.toString());
							} catch (Exception e) {
								e.printStackTrace();
							}
						});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println(String.format("\n%s\n%f", sourceFile.getAbsolutePath(), (endTime - startTime) / 1000.0));
	}
}
