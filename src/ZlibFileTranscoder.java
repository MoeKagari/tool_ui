import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import javafx.scene.Parent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tool.compress.ZLib;

public class ZlibFileTranscoder extends WindowBase {
	public static void main(String[] args) {
		launch(args);
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
		Path source = sourceFile.toPath();
		Path root = source.getParent();
		Path target = root.resolve("unpack");
		try {
			Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					try {
						byte[] bytes = Files.readAllBytes(file);
						bytes = ZLib.decompressOptional(bytes).orElse(bytes);

						String exten;
						switch (String.format("%d%d", Byte.toUnsignedInt(bytes[0]), Byte.toUnsignedInt(bytes[1]))) {
							case "13780":
								exten = "png";
								break;
							case "6787":
								exten = "swf";
								break;
							case "7368":
								exten = "mp3";
								break;
							case "255216":
								exten = "jpg";
								break;
							default:
								exten = "txt";
								break;
						}

						Path targetFile = target.resolve(root.relativize(file).toString() + "." + exten);
						Files.createDirectories(targetFile.getParent());
						Files.write(targetFile, bytes);
					} catch (Exception ex) {
						ex.printStackTrace();
					}

					return super.visitFile(file, attrs);
				}
			});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
