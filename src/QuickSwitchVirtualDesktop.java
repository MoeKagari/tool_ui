import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tool.function.FunctionUtils;

/* 使用鼠标切换 win10 中的虚拟桌面 */
public class QuickSwitchVirtualDesktop extends Application {
	public static void main(String[] args) {
		QuickSwitchVirtualDesktop.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setScene(new Scene(new BorderPane(this.createSwitchButton()) {
			{
				this.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
			}
		}) {
			{
				this.setFill(null);
			}
		});
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.show();
	}

	private Button createSwitchButton() {
		double width = 100, height = 50;
		double width_h = 20, width_v = 20;
		double angle = 60 * Math.PI / 360;

		Polygon polygon = new Polygon();
		polygon.getPoints().addAll(
				0.0, height / 2,
				height / 2 / Math.tan(angle), 0.0,
				height / 2 / Math.tan(angle) + width_h, 0.0,
				height / 2 / Math.tan(angle) + width_h - (height - width_v) / 2 / Math.tan(angle), (height - width_v) / 2,
				width, (height - width_v) / 2//
				,
				width, height - (height - width_v) / 2,
				height / 2 / Math.tan(angle) + width_h - (height - width_v) / 2 / Math.tan(angle), height - (height - width_v) / 2,
				height / 2 / Math.tan(angle) + width_h, height - 0.0,
				height / 2 / Math.tan(angle), height - 0.0,
				0.0, height - height / 2
		/**/ );

		Button button = new Button();
		button.setGraphic(polygon);
		button.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
		button.setOnAction(ev -> {
			this.switchDirection(KeyEvent.VK_LEFT);
			Platform.exit();
		});
		return button;
	}

	private void switchDirection(int r_or_l) {
		try {
			Robot robot = new Robot();
			FunctionUtils.stream(KeyEvent.VK_CONTROL, KeyEvent.VK_WINDOWS, r_or_l).forEach(robot::keyPress);
			robot.delay(1000);
			FunctionUtils.stream(KeyEvent.VK_CONTROL, KeyEvent.VK_WINDOWS, r_or_l).forEach(robot::keyRelease);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
}
