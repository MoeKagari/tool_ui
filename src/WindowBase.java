import org.apache.commons.lang3.exception.ExceptionUtils;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;

public abstract class WindowBase extends Application {
	public static void showExceptionDialog(Throwable th) {
		GridPane root = new GridPane();
		root.setVisible(false);
		root.setMaxWidth(Double.MAX_VALUE);
		root.add(new Label("异常详细 : "), 0, 0);
		{
			TextArea textArea = new TextArea();
			textArea.setEditable(false);
			textArea.setWrapText(false);
			textArea.setText(ExceptionUtils.getStackTrace(th));
			textArea.setMaxWidth(Double.MAX_VALUE);
			textArea.setMaxHeight(Double.MAX_VALUE);
			GridPane.setVgrow(textArea, Priority.ALWAYS);
			GridPane.setHgrow(textArea, Priority.ALWAYS);

			root.add(textArea, 0, 1);
		}

		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setTitle("发生异常");
		dialog.initModality(Modality.APPLICATION_MODAL);
		{
			DialogPane dialogPane = dialog.getDialogPane();
			dialogPane.getButtonTypes().addAll(ButtonType.CLOSE);
			dialogPane.setContentText(th.getMessage());
			dialogPane.setExpandableContent(root);
		}
		dialog.showAndWait();
	}

	protected abstract Parent createParent(Stage primaryStage);

	protected MenuBar createMenuBar(Stage primaryStage) {
		return new MenuBar();
	}

	private StringProperty title = new SimpleStringProperty(this.defaultTitle());

	public String defaultTitle() {
		return "";
	}

	public final StringProperty titleProperty() {
		return this.title;
	}

	@Override
	public final void start(Stage primaryStage) throws Exception {
		Scene scene = new Scene(new BorderPane(this.createParent(primaryStage), this.createMenuBar(primaryStage), null, null, null), 800, 600);

		primaryStage.titleProperty().bind(this.title);
		primaryStage.centerOnScreen();
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
