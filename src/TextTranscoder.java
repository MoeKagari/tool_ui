import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tool.FXUtils;
import tool.function.FunctionUtils;

public class TextTranscoder extends WindowBase {
	public static void main(String[] args) {
		launch(args);
	}

	private Button saveTo;
	private CheckBox autoWrap;
	private ComboBox<Charset> combo;

	private File file = null;
	private TextArea text;

	@Override
	protected Parent createParent(Stage primaryStage) {
		HBox top = new HBox(2);
		top.setAlignment(Pos.CENTER);
		{
			this.saveTo = new Button("另存为");
			this.saveTo.setOnAction(ev -> {
				String content = this.text.getText();
				if (StringUtils.isEmpty(content)) {
					Alert alert = new Alert(AlertType.NONE, "暂未有文件加载\n请先加载文件", ButtonType.CLOSE);
					alert.setTitle("提示");
					alert.showAndWait();
				} else {
					FileChooser fileChooser = new FileChooser();
					fileChooser.setTitle("选择保存的位置");
					FunctionUtils.notNull(fileChooser.showSaveDialog(primaryStage), targetFile -> {
						try {
							Files.write(targetFile.toPath(), content.getBytes(this.combo.getValue().charsetName));
						} catch (IOException ex) {
							FXUtils.showExceptionDialog(ex);
						}
					});
				}
			});

			this.combo = new ComboBox<>(FXCollections.observableArrayList(Charset.values()));
			this.combo.setValue(Charset.日文);
			this.combo.setEditable(false);
			this.combo.valueProperty().addListener((observable, oldValue, newValue) -> this.reloadFile());
			HBox.setMargin(this.combo, new Insets(4));

			this.autoWrap = new CheckBox("自动换行");
			this.autoWrap.setSelected(true);
		}
		top.getChildren().addAll(this.saveTo, this.combo, this.autoWrap);

		this.text = new TextArea();
		this.text.setEditable(false);
		this.text.wrapTextProperty().bind(this.autoWrap.selectedProperty());
		this.text.setOnDragOver(ev -> ev.acceptTransferModes(TransferMode.ANY));
		this.text.setOnDragDropped(ev -> {
			List<File> files = ev.getDragboard().getFiles();
			if (files != null && files.size() == 1) {
				this.file = files.get(0);
			} else {
				this.file = null;
			}
			this.reloadFile();
		});

		return new BorderPane(this.text, top, null, null, null);
	}

	private void reloadFile() {
		if (this.file != null) {
			try {
				this.text.setText(new String(Files.readAllBytes(this.file.toPath()), this.combo.getValue().charsetName));
			} catch (IOException ex) {
				this.text.setText("");
				FXUtils.showExceptionDialog(ex);
			}
		}
	}

	private enum Charset {
		日文("shift-jis"),
		简体中文("gbk"),
		Unicode("utf-8"),
		;

		private final String charsetName;

		private Charset(String charsetName) {
			this.charsetName = charsetName;
		}

		@Override
		public String toString() {
			return super.toString() + "(" + this.charsetName + ")";
		}
	}
}
