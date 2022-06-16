package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainController implements Initializable {

	@FXML
	Canvas canvas;
	GraphicsContext gc;

	private int map[][];
	private int turn = 1;
	private double width;
	private double height;
	static final int BLACK = 1; // blue
	static final int WHITE = 2; // green
	private Stage primaryStage;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		gc = canvas.getGraphicsContext2D();
		width = canvas.getWidth() / 20;
		height = canvas.getHeight() / 20;
		initCanvas();

		canvas.setOnMouseClicked(e -> {
			draw(e.getX(), e.getY());
		});
	}

	private void initCanvas() {
		// 캔버스 클리어
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

		// 오목판 배경색칠
		gc.setFill(Color.ORANGE);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

		// 오목판 만들기
		gc.setStroke(Color.BLACK);
		for (double i = width; i < canvas.getWidth(); i += width) {
			gc.strokeLine(height, i, canvas.getHeight() - height, i);
		}
		for (double i = height; i < canvas.getHeight(); i += height) {
			gc.strokeLine(i, width, i, canvas.getWidth() - width);
		}

		// 배열 초기화
		map = new int[19][19];

		// 흑돌 선
		turn = BLACK;
	}

	private void draw(double x, double y) {
		int indexX = (int) ((x + (width / 2)) / (width)) - 1;
		int indexY = (int) ((y + (height / 2)) / (height)) - 1;

		// 이미 돌이 두어진 곳이라면
		if (map[indexY][indexX] != 0) {
			return;
		}

		map[indexY][indexX] = turn;

		// 턴에 따라 돌의 색 지정
		if (turn == BLACK) {
			gc.setFill(Color.BLUE);
		} else {
			gc.setFill(Color.GREEN);
		}

		gc.fillOval(((indexX + 1) * width) - width / 2, ((indexY + 1) * height) - height / 2, width - 2, height - 2);
		gc.strokeOval(((indexX + 1) * width) - width / 2, ((indexY + 1) * height) - height / 2, width - 2, height - 2);

		// 승리했다면
		if (victoryCheck(indexX, indexY)) {
			showAlert();
			return;
		}

		turn = (turn == BLACK) ? WHITE : BLACK;
	}

	private boolean victoryCheck(int x, int y) {
		int countRow = 1, countCol = 1, countElevenToFive = 1, countOneToSeven = 1;

		for (int i = 1; i < 5; i++) { // 오른쪽으로 찾기
			if (x + i < 19 && map[y][x + i] == turn) {
				countRow++;
			} else {
				break;
			}
		}

		for (int i = 1; i < 5; i++) { // 왼쪽으로 찾기
			if (x - i >= 0 && map[y][x - i] == turn) {
				countRow++;
			} else {
				break;
			}
		}

		for (int i = 1; i < 5; i++) { // 아래로 찾기
			if (y + i < 19 && map[y + i][x] == turn) {
				countCol++;
			} else {
				break;
			}
		}

		for (int i = 1; i < 5; i++) { // 위로 찾기
			if (y - i >= 0 && map[y - i][x] == turn) {
				countCol++;
			} else {
				break;
			}
		}

		for (int i = 1; i < 5; i++) { // 대각선 오른쪽 아래로 찾기
			if (y + i < 19 && x + i < 19 && map[y + i][x + i] == turn) {
				countElevenToFive++;
			} else {
				break;
			}
		}

		for (int i = 1; i < 5; i++) { // 대각선 왼쪽 위로 찾기
			if (y - i >= 0 && x - i >= 0 && map[y - i][x - i] == turn) {
				countElevenToFive++;
			} else {
				break;
			}
		}

		for (int i = 1; i < 5; i++) { // 대각선 왼쪽 아래로 찾기
			if (y - i >= 0 && x + i < 19 && map[y - i][x + i] == turn) {
				countOneToSeven++;
			} else {
				break;
			}
		}

		for (int i = 1; i < 5; i++) { // 대각선 오른쪽 위로 찾기
			if (y + i < 19 && x - i >= 0 && map[y + i][x - i] == turn) {
				countOneToSeven++;
			} else {
				break;
			}
		}

		if (countRow >= 5 || countCol >= 5 || countElevenToFive >= 5 || countOneToSeven >= 5) {
			return true;
		}
		return false;
	}

	private void showAlert() {
		String winner = (turn == BLACK) ? "흑" : "백";
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("게임 종료!");
		alert.setHeaderText(winner + "돌이 승리했습니다!");
		alert.setContentText("게임을 다시 시작하겠습니까?");
		alert.showAndWait();
		if (alert.getResult() == ButtonType.OK) {
			initCanvas();
		} else {
			primaryStage.close();
		}
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

}
