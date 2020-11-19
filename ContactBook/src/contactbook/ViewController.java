package contactbook;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ViewController implements Initializable {

    @FXML
    TableView table;
    @FXML
    TextField inputLastname;
    @FXML
    TextField inputFirstname;
    @FXML
    TextField inputEmail;
    @FXML
    Button addNewContactButton;
    @FXML
    StackPane menuPane;
    @FXML
    Pane contactPane;
    @FXML
    Pane exportPane;
    @FXML
    TextField inputExportName;
    @FXML
    Button exportButton;
    @FXML
    AnchorPane anchor;
    @FXML
    SplitPane mainSplit;

    DB db = new DB();
    
    private final String MENU_CONTACTS = "Kontaktok";
    private final String MENU_LIST = "Lista";
    private final String MENU_EXPORT = "Exportálás";
    private final String MENU_EXIT = "Kilépés";

    private final ObservableList<Person> data = FXCollections.observableArrayList(
                    //new Person("Nagy", "Marcell", "makk.marci@valami.com "),
                    //new Person("Nagy", "Levente", "superhero@gmail.com ")
        );

    @FXML
    private void addContact(ActionEvent event) {
        
        String email = inputEmail.getText();
        
        if (email.length() > 3 && email.contains("@") && email.contains(".")) {
            Person newPerson = new Person(inputLastname.getText(), inputFirstname.getText(), inputEmail.getText());
            data.add(newPerson);
            db.addContact(newPerson);
            inputLastname.clear();
            inputFirstname.clear();
            inputEmail.clear();
        } else {
            alert("Adj meg egy valódi e-mail címet!");
        }
    }

    @FXML
    private void exportList(ActionEvent event) {
        
        String fileName = inputExportName.getText();
        fileName = fileName.replaceAll("\\s+", "");
        
        if (fileName != null && !fileName.equals("")) {
            PdfGeneration pdfCreator = new PdfGeneration();
            pdfCreator.pdfGeneration(fileName, data);
        } else {
            alert("Adj meg egy fájlnevet!");
        }
    }

    public void setTableData() {

        TableColumn lastNameCol = new TableColumn("Vezetéknév");
        lastNameCol.setMinWidth(110);
        lastNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        lastNameCol.setCellValueFactory(new PropertyValueFactory<Person, String>("lastName"));

        lastNameCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Person, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Person, String> event) {
                Person actualPerson = (Person) event.getTableView().getItems().get(event.getTablePosition().getRow());
                actualPerson.setLastName(event.getNewValue());
                db.updateContact(actualPerson);
            }
        });

        TableColumn firstNameCol = new TableColumn("Keresztnév");
        firstNameCol.setMinWidth(110);
        firstNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        firstNameCol.setCellValueFactory(new PropertyValueFactory<Person, String>("firstName"));

        firstNameCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Person, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Person, String> event) {
                Person actualPerson = (Person) event.getTableView().getItems().get(event.getTablePosition().getRow());
                actualPerson.setFirstName(event.getNewValue());
                db.updateContact(actualPerson);
            }
        });

        TableColumn emailCol = new TableColumn("E-mail cím");
        emailCol.setMinWidth(220);
        emailCol.setCellFactory(TextFieldTableCell.forTableColumn());
        emailCol.setCellValueFactory(new PropertyValueFactory<Person, String>("email"));

        emailCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Person, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Person, String> event) {
                Person actualPerson = (Person) event.getTableView().getItems().get(event.getTablePosition().getRow());
                actualPerson.setEmail(event.getNewValue());
                db.updateContact(actualPerson);
            }
        });

        table.getColumns().addAll(lastNameCol, firstNameCol, emailCol);
        data.addAll(db.getAllContacts());
        table.setItems(data);
    }

    private void setMenuData() {
        
        TreeItem<String> treeItemRoot1 = new TreeItem<>("Menü");
        TreeView<String> treeView = new TreeView<>(treeItemRoot1);
        treeView.setShowRoot(false);

        TreeItem<String> nodeItemA = new TreeItem<>(MENU_CONTACTS);
        TreeItem<String> nodeItemB = new TreeItem<>(MENU_EXIT);

        nodeItemA.setExpanded(true);

        Node contactsNode = new ImageView(new Image(getClass().getResourceAsStream("/contacts.png")));
        Node exportNode = new ImageView(new Image(getClass().getResourceAsStream("/export.png")));
        TreeItem<String> nodeItemA1 = new TreeItem<>(MENU_LIST, contactsNode);
        TreeItem<String> nodeItemA2 = new TreeItem<>(MENU_EXPORT, exportNode);

        nodeItemA.getChildren().addAll(nodeItemA1, nodeItemA2);
        treeItemRoot1.getChildren().addAll(nodeItemA, nodeItemB);

        menuPane.getChildren().add(treeView);

        treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                TreeItem<String> selectedItem = (TreeItem<String>) newValue;
                String selectedMenu;
                selectedMenu = selectedItem.getValue();

                if (null != selectedMenu) {
                    switch (selectedMenu) {
                        case MENU_CONTACTS:
                            try {
                                selectedItem.setExpanded(true);
                            } catch (Exception ex) {
                            }
                            break;
                        case MENU_LIST:
                            contactPane.setVisible(true);
                            exportPane.setVisible(false);
                            break;
                        case MENU_EXPORT:
                            contactPane.setVisible(false);
                            exportPane.setVisible(true);
                            break;
                        case MENU_EXIT:
                            System.exit(0);
                            break;
                    }
                }
            }
        });
    } 

    private void alert(String text){
        
        mainSplit.setDisable(true);
        mainSplit.setOpacity(0.4);
        
        Label label = new Label(text);
        Button alertButton = new Button("OK");
        VBox vbox = new VBox(label, alertButton);
        vbox.setAlignment(Pos.CENTER);
        
        alertButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e){
                mainSplit.setDisable(false);
                mainSplit.setOpacity(1);
                vbox.setVisible(false);
            }
        });
      
        anchor.getChildren().add(vbox);
        anchor.setTopAnchor(vbox, 300.0);
        anchor.setLeftAnchor(vbox, 250.0);
     
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setTableData();
        setMenuData();
        
    }
}
