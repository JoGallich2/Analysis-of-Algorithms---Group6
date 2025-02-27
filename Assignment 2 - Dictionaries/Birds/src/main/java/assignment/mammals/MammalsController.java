package assignment.mammals;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ouda
 */
public class MammalsController implements Initializable {

    @FXML
    private MenuBar mainMenu;
    @FXML
    private ImageView image;
    @FXML
    private BorderPane MammalPortal;
    @FXML
    private Label title;
    @FXML
    private Label about;
    @FXML
    private Button play;
    @FXML
    private Button puase;
    @FXML
    private ComboBox size;
    @FXML
    private TextField name;
    Media media;
    MediaPlayer player;
    OrderedDictionary database = null;
    MammalRecord mammal = null;
    int mammalSize = 1;

    @FXML
    public void exit() {
        Stage stage = (Stage) mainMenu.getScene().getWindow();
        stage.close();
    }

    public void find() {
        DataKey key = new DataKey(this.name.getText(), mammalSize);
        try {
            mammal = database.find(key);
            showMammal();
        } catch (DictionaryException ex) {
            displayAlert(ex.getMessage());
        }
    }

    public void delete() throws DictionaryException {
        MammalRecord previousMammal = null;
        try {
            previousMammal = database.predecessor(mammal.getDataKey());
        } catch (DictionaryException ex) {

        }
        MammalRecord nextMammal = null;
        try {
            nextMammal = database.successor(mammal.getDataKey());
        } catch (DictionaryException ex) {

        }
        DataKey key = mammal.getDataKey();
        try {
            database.remove(key);
        } catch (DictionaryException ex) {
            System.out.println("Error in delete "+ ex);
        }
        if (database.isEmpty()) {
            this.MammalPortal.setVisible(false);
            displayAlert("No more mammals in the database to show");
        } else {
            if (previousMammal != null) {
                mammal = previousMammal;
                showMammal();
            } else if (nextMammal != null) {
                mammal = nextMammal;
                database.root = new Node(mammal);
            }
        }
    }

    private void showMammal() {
        play.setDisable(false);
        puase.setDisable(true);
        if (player != null) {
            player.stop();
        }
        String img = mammal.getImage();
        Image mammalImage = new Image("file:src/main/resources/assignment/mammals/images/" + img);
        image.setImage(mammalImage);
        title.setText(mammal.getDataKey().getMammalName());
        about.setText(mammal.getAbout());
    }

    private void displayAlert(String msg) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Alert.fxml"));
            Parent ERROR = loader.load();
            AlertController controller = (AlertController) loader.getController();

            Scene scene = new Scene(ERROR);
            Stage stage = new Stage();
            stage.setScene(scene);

            stage.getIcons().add(new Image("file:src/main/resources/assignment/mammals/images/UMIcon.png"));
            stage.setTitle("Dictionary Exception");
            controller.setAlertText(msg);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException ex1) {

        }
    }

    public void getSize() {
        switch (this.size.getValue().toString()) {
            case "Small":
                this.mammalSize = 1;
                break;
            case "Medium":
                this.mammalSize = 2;
                break;
            case "Large":
                this.mammalSize = 3;
                break;
            default:
                break;
        }
    }

    public void first() {
        //Checks to see if the database is empty
        if (database.isEmpty()) {
            System.out.println("Database is empty.");
            return;
        }
        try {
            mammal = database.smallest();
        } catch (DictionaryException e) {
            throw new RuntimeException(e);
        }
        name.setText(mammal.getDataKey().getMammalName());
        about.setText(mammal.getAbout());
        showMammal();
        //Show the size as well because he wanted us to do that.
    }

    public void last() {
        //Checks to see if the database is empty
        if (database.isEmpty()) {
            System.out.println("Database is empty.");
            return;
        }
        try {
            mammal = database.largest();
        } catch (DictionaryException e) {
            throw new RuntimeException(e);
        }
        name.setText(mammal.getDataKey().getMammalName());
        about.setText(mammal.getAbout());
        showMammal();
        //Show the size as well because he wanted us to do that.
    }

    public void next() {
        //Checks to see if the database is empty
        if (database.isEmpty()) {
            System.out.println("Database is empty.");
            return;
        }
        //Checks to see if the database is empty
        if (mammal == null) {
            System.out.println("No current selection.");
            return;
        }

        try {
            MammalRecord Successor = database.successor(mammal.getDataKey());
            mammal = Successor;
            name.setText(mammal.getDataKey().getMammalName());
            about.setText(mammal.getAbout());
            showMammal();
        } catch (DictionaryException e) {
            System.out.println("No next mammal available.");
        }
    }

    public void previous() {
        //Checks to see if the database is empty
        if (database.isEmpty()) {
            System.out.println("Database is empty.");
            return;
        }
        //Checks to see if the database is empty
        if (mammal == null) {
            System.out.println("No current selection.");
            return;
        }

        try {
            MammalRecord Predecessor = database.predecessor(mammal.getDataKey());
            mammal = Predecessor;
            name.setText(mammal.getDataKey().getMammalName());
            about.setText(mammal.getAbout());
            showMammal();
        } catch (DictionaryException e) {
            System.out.println("No next mammal available.");
        }
    }

    public void play() {
        String filename = "src/main/resources/assignment/mammals/sounds/" + mammal.getSound();
        media = new Media(new File(filename).toURI().toString());
        player = new MediaPlayer(media);
        play.setDisable(true);
        puase.setDisable(false);
        player.play();
    }

    public void puase() {
        play.setDisable(false);
        puase.setDisable(true);
        if (player != null) {
            player.stop();
        }
    }

    public void loadDictionary() {
        Scanner input;
        int line = 0;
        try {
            String mammalName = "";
            String description;
            int size = 0;
            input = new Scanner(new File("MammalsDatabase.txt"));
            while (input.hasNext()) // read until  end of file
            {
                String data = input.nextLine();
                switch (line % 3) {
                    case 0:
                        size = Integer.parseInt(data);
                        break;
                    case 1:
                        mammalName = data;
                        break;
                    default:
                        description = data;
                        MammalRecord mammalRecord = new MammalRecord(new DataKey(mammalName, size), description, mammalName + ".mp3", mammalName + ".jpg");
                        database.insert(mammalRecord);

                        // Print the data of the inserted mammal
                        System.out.println("Inserted Mammal:");
                        System.out.println("Name: " + mammalRecord.getDataKey().getMammalName());
                        System.out.println("Size: " + mammalRecord.getDataKey().getMammalSize());
                        break;
                }
                line++;
            }
        } catch (IOException e) {
            System.out.println("There was an error in reading or opening the file: MammalsDatabase.txt");
            System.out.println(e.getMessage());
        } catch (DictionaryException ex) {
            Logger.getLogger(MammalsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.MammalPortal.setVisible(true);
        this.first();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        database = new OrderedDictionary();
        size.setItems(FXCollections.observableArrayList(
                "Small", "Medium", "Large"
        ));
        size.setValue("Small");
    }

}
