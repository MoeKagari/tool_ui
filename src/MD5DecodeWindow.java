
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tool.FXUtils;
import tool.compress.MD5;
import tool.function.FunctionUtils;

public class MD5DecodeWindow extends WindowBase {
	@Override
	protected boolean isResizeable() {
		return false;
	}

	@Override
	protected Parent createParent(Stage primaryStage) {
		TextArea resultTextArea = new TextArea();
		VBox.setVgrow(resultTextArea, Priority.ALWAYS);
		resultTextArea.setEditable(false);

		TextField passwordTextField = new TextField();
		passwordTextField.setPromptText("输入密文");
		HBox.setHgrow(passwordTextField, Priority.ALWAYS);

		TextField needCompareTextField = new TextField();
		needCompareTextField.setPromptText("输入密文");
		HBox.setHgrow(needCompareTextField, Priority.ALWAYS);

		class EncodePlan {
			final String name;
			final Function<String, String> value;

			public EncodePlan(String name, Function<String, String> value) {
				this.name = name;
				this.value = value;
			}
		}
		List<EncodePlan> encodePlanList = FunctionUtils.asList(
				new EncodePlan("MD5", password -> {
					return MD5.getMD5(password);
				}),

				new EncodePlan("(小)MD5(MD5)", password -> {
					return MD5.getMD5(MD5.getMD5(password));
				}),
				new EncodePlan("(小)MD5(MD5(MD5))", password -> {
					return MD5.getMD5(MD5.getMD5(MD5.getMD5(password)));
				}),

				new EncodePlan("(大)MD5(MD5)", password -> {
					return MD5.getMD5(MD5.getMD5(password).toUpperCase());
				}),
				new EncodePlan("(大)MD5(MD5(MD5))", password -> {
					return MD5.getMD5(MD5.getMD5(MD5.getMD5(password).toUpperCase()).toUpperCase());
				})
		/**/);

		Button doButton = new Button("执行");
		doButton.setOnAction(ev -> {
			String password = passwordTextField.getText();
			String needCompare = needCompareTextField.getText();

			String plan = null;
			ArrayList<String> encodeResults = new ArrayList<>();
			for (EncodePlan encodePlan : encodePlanList) {
				String name = encodePlan.name;
				String value = encodePlan.value.apply(password);

				encodeResults.add("\t" + name + "\n\t\t" + value + "");
				if (needCompare.equals(value)) {
					plan = name;
				}
			}

			resultTextArea.appendText(String.format(
					"%s\n%s\n%s",
					String.format("原始密文 : \"%s\"", password),
					String.format("与\"%s\"符合的方案 : %s", needCompare, plan == null ? "没有" : plan),
					String.join("\n", encodeResults)
			/**/));
		});

		return FXUtils.createVBox(2, Pos.CENTER, false,
				box -> {
					box.setPadding(new Insets(2));
				},
				doButton,
				FXUtils.createHBox(5, Pos.CENTER, false, new Label("需加密:"), passwordTextField),
				FXUtils.createHBox(5, Pos.CENTER, false, new Label("需比较:"), needCompareTextField),
				resultTextArea
		/**/);
	}

	public static void main(String[] args) {
		MD5DecodeWindow.launch(args);
	}
}
