package dk.easv.moviecollectionproject.GUI.Controller;

import dk.easv.moviecollectionproject.BE.Category;
import dk.easv.moviecollectionproject.BLL.BLCategory;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CategoryController {

    private final BLCategory blCategory = new BLCategory();
    public Button createPlaylistBtn;
    private MCController mcController; // Reference to MCController

    @FXML
    private TableView<Category> categoryTableView;

    @FXML
    private TextField categoryNameField;
    @FXML
    private TextField editCategoryNameField;


    // Set the main MCController instance (this method should be called by MCController)
    public void setController(MCController mcController) {
        this.mcController = mcController;
    }

    // Method to handle the addition of a new category from the TextField
    public void addCategoryToDB() {
        String categoryName = categoryNameField.getText();

        if (categoryName.isEmpty()) {
            // Show some message or warning when the field is empty
            System.out.println("Category name cannot be empty!");
            return;
        }

        Category category = new Category();
        category.setName(categoryName);
        blCategory.addCategory(category);

        if (mcController != null) {
            mcController.refreshTableView(); // Safely call refresh on MCController
        } else {
            System.err.println("MCController is not initialized. Cannot refresh table view.");
        }

        closeStage(categoryNameField);  // Close the current stage (pop-up window)
    }

    public void updateCategory() {
        if (mcController != null) {
            Category category = new Category();
            category.setName(editCategoryNameField.getText());
            mcController.onEditButtonClicked(category);
            this.closeStage(editCategoryNameField);

        } else {
            System.err.println("MCController is not initialized. Cannot update category.");
        }
    }


    // Method to delete the selected category from the TableView
    public void onDeleteCategoryClicked(Category selectedCategory) {
        if (selectedCategory != null) {
            blCategory.removeCategory(selectedCategory.getId());
            if (mcController != null) {
                mcController.refreshTableView(); // Refresh the main view
            } else {
                System.err.println("MCController is not initialized. Cannot refresh table view.");
            }
        } else {
            System.out.println("No Category Selected for Deletion");
        }
    }

    // Helper method to close the current stage (pop-up window)
    private void closeStage(TextField textField) {
        Stage currentStage = (Stage) textField.getScene().getWindow();
        mcController.refreshTableView();
        currentStage.close();
    }



    // Optional method to edit an existing category (can be expanded if needed)
    public void onEditCategoryClicked(Category selectedCategory) {
        if (selectedCategory != null) {
            editCategoryNameField.setText(selectedCategory.getName().toString());
            // Further editing logic can be implemented here
        } else {
            System.out.println("No Category Selected for Editing");
        }
    }
}
