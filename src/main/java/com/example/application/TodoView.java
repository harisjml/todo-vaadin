package com.example.application;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("")
public class TodoView extends VerticalLayout {

    private TodoRepo repo;
    private VerticalLayout todos;

    public TodoView(TodoRepo repo) {
        // add(new H2("Hello Haris"));
        this.repo = repo; // this is interface

        var task = new TextField();
        var button = new Button("Add Task");
        todos = new VerticalLayout();

        //css
        todos.setPadding(false);
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickShortcut(Key.ENTER); //Enable task value submitted when user press enter key

        // function add task
        button.addClickListener(click -> {
            var todo = repo.save(new Todo(task.getValue())); 
            todos.add(createTodoLayout(todo));
            task.clear();// after button click, textfield will be blank
        });

        // the todo value will be exist even page is refreshed
        repo.findAll().forEach(todo -> todos.add(createTodoLayout(todo)));
        // layout
        add(
                new H1("Todo Task"),
                new HorizontalLayout(task, button),
                todos);

    }
    private HorizontalLayout createTodoLayout(Todo todo) {
        var checkbox = new Checkbox(todo.getTask(), todo.isComplete(), e -> {
            todo.setComplete(e.getValue());
            repo.save(todo);
        });

        var editIcon = new Icon(VaadinIcon.EDIT);
        var deleteIcon = new Icon(VaadinIcon.TRASH);

        editIcon.addClickListener(e -> editTodo(todo));
        deleteIcon.addClickListener(e -> deleteTodoAndRefresh(todo));

        return new HorizontalLayout(checkbox, editIcon, deleteIcon);
    }

    private void editTodo(Todo todo) {
        Dialog editDialog = new Dialog();
        editDialog.setModal(true);

        TextField editedTaskField = new TextField("Edit Task");
        editedTaskField.setValue(todo.getTask());
        
        Button updateButton = new Button("Update Task");
        updateButton.addClickListener(updateClick -> {
            // Get the updated task value from the TextField
            String updatedTask = editedTaskField.getValue();
    
            // Update the todo with the new task value
            todo.setTask(updatedTask);
            repo.save(todo);
    
            // Close the dialog
            editDialog.close();
    
            // Refresh the todos after updating
            refreshTodos();
        });
        
        editDialog.add(editedTaskField, updateButton);

        editDialog.open();
    }

    private void deleteTodoAndRefresh(Todo todo) {
        repo.delete(todo);
        refreshTodos();
    }

    private void refreshTodos() {
        // Clear and reload todos from the repository
        todos.removeAll();
        repo.findAll().forEach(todo -> todos.add(createTodoLayout(todo)));
    }
    
}
