import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public abstract class WindowBase extends Application {
	protected abstract Parent createParent(Stage primaryStage);

	protected MenuBar createMenuBar(Stage primaryStage) {
		return null;
	}

	protected WindowSize getDefaultWindowSize() {
		return new WindowSize(800, 600);
	}

	protected boolean isResizeable() {
		return true;
	}

	protected boolean isDefaultAlwaysOnTop() {
		return false;
	}

	protected String getDefaultWindowName() {
		return "";
	}

	@Override
	public final void start(Stage primaryStage) throws Exception {
		WindowSize defaultWindowSize = this.getDefaultWindowSize();
		primaryStage.setScene(
				new Scene(
						new BorderPane(this.createParent(primaryStage), this.createMenuBar(primaryStage), null, null, null),
						defaultWindowSize.width, defaultWindowSize.height
				/**/)
		/**/);
		primaryStage.setAlwaysOnTop(this.isDefaultAlwaysOnTop());
		primaryStage.setTitle(this.getDefaultWindowName());
		primaryStage.setResizable(this.isResizeable());
		primaryStage.centerOnScreen();
		primaryStage.show();
	}

	protected class WindowSize {
		private final double width, height;

		public WindowSize(double width, double height) {
			this.width = width;
			this.height = height;
		}

		public double getWidth() {
			return this.width;
		}

		public double getHeight() {
			return this.height;
		}
	}
}
