package dk.easv.moviecollectionproject.GUI.Model;

import dk.easv.moviecollectionproject.BE.Category;
import dk.easv.moviecollectionproject.BE.Movie;
import dk.easv.moviecollectionproject.BLL.BLCategory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;

import java.util.List;
import java.util.stream.Collectors;

public class MLCategory {
    private final ObservableList<Category> categories = FXCollections.observableArrayList();
    private final BLCategory blCategory = new BLCategory();

    public ObservableList<Category> getCategories() {
        return categories;
    }

    public void loadCategories() {
        categories.clear();
        categories.addAll(blCategory.getAllCategories());
    }

    public void configureColumns(TableColumn<Category, String> categoryNameColumn) {
        categoryNameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
    }

    public List<Category> filterCategory(String query) {
        List<Category> searchForCategories;
        searchForCategories = this.blCategory.getAllCategories();
        return searchForCategories.stream()
                .filter(category -> category.getName().toLowerCase().contains(query.toLowerCase())
                )
                .collect(Collectors.toList());
    }
}
