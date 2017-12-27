import java.net.URI;
import java.net.URISyntaxException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class ChatClient extends Application {

	private static final String SERVER = "ws://project-peeps-server.herokuapp.com";
	public static chatWindow chatWin;
	//Refer to other class
	private static PeepsSocketClient client;
	private static String userName = "";

	public static void main(String[] args) {
		try {
			client = new PeepsSocketClient(new URI(SERVER));
			launch(args);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void start(Stage stage) {
		stage.setTitle("Main window");
		stage.getIcons().add(new Image("https://vopeeps.com/wp-content/uploads/VO-Peeps-Logo-new-05-610x535.png"));
		Button openLoginWindowButton = new Button("Begin Program");
		openLoginWindowButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				new LoginWindow().show();
			}
		});
		openLoginWindowButton.setPadding(new Insets(80));
		stage.setScene(new Scene(openLoginWindowButton));
		stage.show();
	}

	// Individual login windows to be open
	class LoginWindow extends Stage {
		// Where user enters in name
		// private LabeledTextField nameField;
		private TextField nameField;
		private Label nameLabel;
		private Button loginButton;

		public LoginWindow() {
			setTitle("Login");
			setScene(createScene());
			getIcons().add(new Image("https://vopeeps.com/wp-content/uploads/VO-Peeps-Logo-new-05-610x535.png"));
			registerListeners();
		}

		private Scene createScene() {
			// nameField = new LabeledTextField("Name:", false);
			nameField = new TextField();
			nameLabel = new Label("Name: ");
			loginButton = new Button("Submit");
			HBox bottomBox = new HBox(loginButton);
			bottomBox.setAlignment(Pos.CENTER_RIGHT);

			VBox rootBox = new VBox(20, nameLabel, nameField, bottomBox);
			rootBox.setPadding(new Insets(10));

			return new Scene(rootBox);
		}

		private void registerListeners() {
			loginButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					// Triggered when user clicks login button :
					userName = nameField.getText();
					System.out.println(userName + " has logged in.");

					client.connect();
					try {
						//Allows time for the server to connect
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// Hides the original login window
					((Node) (event.getSource())).getScene().getWindow().hide();

					// Opens another Window to initialize chat
					chatWin = new chatWindow();
					chatWin.show();
				}
			});
		}
	}

	// Individual chat windows to be open
	class chatWindow extends Stage {
		// Text area for user to type in message
		private TextArea messageBox;
		// Text area for inputed message to show
		private TextArea chatBox;

		private Button submitButton;

		public chatWindow() {
			setTitle("Chat");
			setScene(createScene());
			getIcons().add(new Image("https://vopeeps.com/wp-content/uploads/VO-Peeps-Logo-new-05-610x535.png"));
			//sends to Heroku server
			client.send(userName);
			registerListeners();
		}

		private Scene createScene() {
			messageBox = new TextArea();
			messageBox.setPromptText("Enter message here: ");

			chatBox = new TextArea();
			// We just want to append text to it that's it
			chatBox.setEditable(false);

			submitButton = new Button("Submit");

			HBox upperBox = new HBox(submitButton);
			upperBox.setAlignment(Pos.TOP_RIGHT);

			VBox rootBox = new VBox(20, chatBox, messageBox, upperBox);
			rootBox.setPadding(new Insets(10));
			return new Scene(rootBox);
		}

		// Controls submit Button action in chat window
		private void registerListeners() {
			submitButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					// Triggered when user clicks submit button :
					System.out.println("Message recieved: " + messageBox.getText());
					client.send(messageBox.getText());
					messageBox.clear();

				}
			});
		}
		public void printMessage(String message) {
			chatBox.appendText("\n" + message);
		}
	}
}
