
import java.io.File;
import java.util.Base64;

import javax.swing.filechooser.FileSystemView;

import org.apache.commons.io.FileUtils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import tool.FXUtils;

public class Base64DecoderWindow extends WindowBase {
	@Override
	protected WindowSize getDefaultWindowSize() {
		return new WindowSize(500, 150);
	}

	@Override
	protected boolean isDefaultAlwaysOnTop() {
		return true;
	}

	@Override
	protected String getDefaultWindowName() {
		return "Base64解码器";
	}

	@Override
	protected Parent createParent(Stage primaryStage) {
		TextField exportTo = new TextField(FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath());
		HBox.setHgrow(exportTo, Priority.ALWAYS);

		TextArea content = new TextArea("");
		VBox.setVgrow(content, Priority.ALWAYS);
		content.setWrapText(true);
		content.setFont(new Font(20));

		Button export = new Button("输出");
		export.setOnAction(ev -> {
			try {
				FileUtils.writeByteArrayToFile(
						new File(exportTo.getText() + File.separator + String.valueOf(System.currentTimeMillis())),
						Base64.getDecoder().decode(content.getText())
				/**/);
			} catch (Exception e) {
				FXUtils.showExceptionDialog(primaryStage, e);
			}
		});

		Button clearText = new Button("清空");
		clearText.setOnAction(ev -> {
			content.setText("");
		});

		return FXUtils.createVBox(2, Pos.CENTER, false,
				box -> box.setPadding(new Insets(20, 10, 20, 10)),
				FXUtils.createHBox(4, Pos.CENTER, false,
						clearText,
						export,
						FXUtils.createNewLabel("输出目录"), FXUtils.createNewLabel(":"),
						exportTo
				/**/),
				content
		/**/);
	}

	public static void main(String[] args) {
		Base64DecoderWindow.launch(args);
	}
}
