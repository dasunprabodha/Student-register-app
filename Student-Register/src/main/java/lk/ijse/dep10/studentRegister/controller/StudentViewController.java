package lk.ijse.dep10.studentRegister.controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import lk.ijse.dep10.studentRegister.db.DBConnection;
import lk.ijse.dep10.studentRegister.model.Student;
import lk.ijse.dep10.studentRegister.model.util.Gender;

import java.sql.*;
import java.time.LocalDate;

public class StudentViewController {

    @FXML
    public Button btnDeleteStudent;

    @FXML
    public Button btnNewStudent;

    @FXML
    public Button btnSaveStudent;

    @FXML
    public Label lblAddress;

    @FXML
    public Label lblDOB;

    @FXML
    public Label lblFirstName;

    @FXML
    public Label lblId;

    @FXML
    public Label lblLastName;

    @FXML
    public RadioButton rdFemale;

    @FXML
    public RadioButton rdMale;
    @FXML
    public AnchorPane root;
    @FXML
    public TableView<Student> tblStudents;

    @FXML
    public ToggleGroup tglGender;

    @FXML
    public TextField txtAddress;

    @FXML
    public DatePicker txtDOB;

    @FXML
    public TextField txtFirstName;

    @FXML
    public TextField txtId;

    @FXML
    public TextField txtLastName;

    @FXML
    public TextField txtSearch;

    public void initialize() {
        lblId.setLabelFor(txtId);
        lblFirstName.setLabelFor(txtFirstName);
        lblLastName.setLabelFor(txtLastName);
        lblAddress.setLabelFor(txtAddress);
        lblDOB.setLabelFor(txtDOB);
        txtSearch.textProperty().addListener((ov, previous, current) -> {
            Connection connection = DBConnection.getInstance().getConnection();
            try (Statement stm = connection.createStatement()) {

                String sql = "SELECT * FROM Student WHERE first_name LIKE '%1$s' OR last_name LIKE  '%1$s' OR address LIKE '%1$s'";
                sql = String.format(sql, "%" + current + "%");
                ResultSet rst = stm.executeQuery(sql);

                ObservableList<Student> studentList = tblStudents.getItems();
                studentList.clear();


                while (rst.next()) {
                    int id = rst.getInt("id");
                    String firstname = rst.getString("first_name");
                    String lastName = rst.getString("last_name");
                    String address = rst.getString("address");
                    Gender gender = Gender.valueOf(rst.getString("gender"));
                    LocalDate dob = rst.getDate("dob").toLocalDate();
                    studentList.add(new Student(id, firstname, lastName, address, gender, dob));

                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        tblStudents.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tblStudents.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tblStudents.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tblStudents.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("address"));
        tblStudents.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("gender"));
        tblStudents.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));

        loadAllStudent();

        Platform.runLater(() -> {
            root.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_ANY), () -> btnNewStudent.fire());
            root.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_ANY), () -> btnSaveStudent.fire());
            root.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.F1), () -> tblStudents.requestFocus());
        });
        tblStudents.getSelectionModel().selectedItemProperty().addListener((ov, previous, current) -> {
            btnDeleteStudent.setDisable(current == null);
            if (current == null) return;
            txtId.setText(current.getId() + "");
            txtLastName.setText(current.getLastName());
            txtAddress.setText(current.getAddress());
            tglGender.selectToggle(current.getGender() == Gender.MALE ? rdMale : rdFemale);
            txtDOB.setValue(current.getDateOfBirth());

        });
    }

    public void loadAllStudent() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            System.out.println(connection);
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT  * FROM Student");
            ObservableList<Student> studentList = tblStudents.getItems();

            while (rst.next()) {
                int id = rst.getInt("id");
                String firstname = rst.getString("first_name");
                String lastName = rst.getString("last_name");
                String address = rst.getString("address");
                Gender gender = Gender.valueOf(rst.getString("gender"));
                Date dob = rst.getDate("dob");
                studentList.add(new Student(id, firstname, lastName, address, gender, dob.toLocalDate()));
            }
            Platform.runLater(btnNewStudent::fire);


        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load details").showAndWait();
            Platform.exit();

        }

        }

    @FXML
    void btnDeleteStudentOnAction(ActionEvent event) {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            String sql = "DELETE FROM Student WHERE id =%d";
            sql = String.format(sql, tblStudents.getSelectionModel().getSelectedItem().getId());
            stm.executeUpdate(sql);

            tblStudents.getItems().remove(tblStudents.getSelectionModel().getSelectedItem());

            if (tblStudents.getItems().isEmpty()) btnNewStudent.fire();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to delete details").show();
        }
    }

    @FXML
    void btnNewStudentOnAction(ActionEvent event) {
        ObservableList<Student> studentList = tblStudents.getItems();
        int newID = (studentList.isEmpty() ? 1 : studentList.get(studentList.size() - 1).getId() + 1);
        txtId.setText(newID + "");
        txtFirstName.clear();
        txtAddress.clear();
        txtLastName.clear();
        txtDOB.setValue(null);
        tglGender.selectToggle(null);
        tblStudents.getSelectionModel().clearSelection();

        txtFirstName.requestFocus();
    }
    private boolean isDataValid() {
        boolean isDataValid = true;
        for (Node node : new Node[]{txtFirstName, txtLastName, txtAddress, txtDOB, rdFemale, rdMale}) {
            node.getStyleClass().remove("invalid");
        }


        String firstName = txtFirstName.getText();
        String lastName = txtLastName.getText();
        String address = txtAddress.getText();
        Toggle selectedToggle = tglGender.getSelectedToggle();
        LocalDate dob = txtDOB.getValue();


        if (dob == null || !(dob.isBefore(LocalDate.of(2010, 1, 1)) && dob.isAfter(LocalDate.of(1980, 1, 1)))) {
            isDataValid = false;
            txtDOB.requestFocus();
            txtDOB.getStyleClass().add("invalid");
        }

        if (selectedToggle == null) {
            isDataValid = false;
            rdMale.requestFocus();
            rdFemale.getStyleClass().add("invalid");
            rdMale.getStyleClass().add("invalid");

        }


        if (address.strip().length() < 3) {
            isDataValid = false;
            txtAddress.requestFocus();
            txtAddress.selectAll();
            txtAddress.getStyleClass().add("invalid");

        }


        if (!lastName.matches("[A-Za-z ]+")) {
            isDataValid = false;
            txtLastName.requestFocus();
            txtLastName.selectAll();
            txtLastName.getStyleClass().add("invalid");

        }


        if (!firstName.matches("[A-Za-z ]+")) {
            isDataValid = false;
            txtFirstName.requestFocus();
            txtFirstName.selectAll();
            txtFirstName.getStyleClass().add("invalid");

        }
        return isDataValid;
    }


    @FXML
    void btnSaveStudentOnAction(ActionEvent event) {
        if (!isDataValid()) return;

        try {
            Student student = new Student(Integer.parseInt(txtId.getText()), txtFirstName.getText(), txtLastName.getText(), txtAddress.getText(), tglGender.getSelectedToggle() == rdMale ? Gender.MALE : Gender.FEMALE, txtDOB.getValue());
            Connection connection = DBConnection.getInstance().getConnection();


            Statement stm = connection.createStatement();
            Student selectetedStudent = tblStudents.getSelectionModel().getSelectedItem();
            if (selectetedStudent == null) {


                String sql = "INSERT INTO Student VALUES (%d,'%s','%s', '%s','%s','%s')";
                sql = String.format(sql, student.getId(), student.getFirstName(), student.getLastName(), student.getAddress(), student.getGender().name(), student.getDateOfBirth());

                stm.executeUpdate(sql);
            } else {
                String sql = "UPDATE Student SET first_name='%s', last_name='%s', address ='%s',gender='%s' ,dob ='%'WHERE id=%d ";

                sql = String.format(sql, student.getFirstName(), student.getLastName(), student.getAddress(), student.getGender(), student.getDateOfBirth(), student.getId());
                stm.executeUpdate(sql);


                ObservableList<Student> studentList = tblStudents.getItems();
                int selectedStudentIndex = studentList.indexOf(selectetedStudent);
                studentList.set(selectedStudentIndex, student);
                tblStudents.refresh();


            }
            tblStudents.getItems().add(student);
            btnNewStudent.fire();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to save details").showAndWait();
        }

    }

    @FXML
    void tblStudentsOnKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.DELETE) btnDeleteStudent.fire();

    }

}
