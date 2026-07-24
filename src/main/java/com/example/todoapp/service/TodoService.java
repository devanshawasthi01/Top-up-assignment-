package com.example.todoapp.service;

import com.example.todoapp.model.Todo;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class TodoService {

    private List<Todo> todos = new CopyOnWriteArrayList<>();

    public TodoService() {
        todos.add(new Todo(1L, "Learn Spring Boot", false));
        todos.add(new Todo(2L, "Practice Java", false));
        todos.add(new Todo(3L, "Build Project", false));
        todos.add(new Todo(4L, "Prepare Interview", false));
        todos.add(new Todo(5L, "Revise DSA", false));
    }

    public List<Todo> getAllTodos() {
        return new ArrayList<>(todos);
    }

    public Todo getTodoById(Long id) {
        return todos.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Todo createTodo(Todo todo) {
        if (todo.getId() != null && todos.stream().anyMatch(t -> t.getId().equals(todo.getId()))) {
            throw new IllegalArgumentException("Todo with the same ID already exists.");
        }
        todos.add(todo);
        return todo;
    }

    public Todo updateTodo(Long id, Todo newTodo) {
        for (Todo t : todos) {
            if (t.getId().equals(id)) {
                t.setTitle(newTodo.getTitle());
                t.setCompleted(newTodo.isCompleted());
                return t;
            }
        }
        return null;
    }

    public void deleteTodo(Long id) {
        todos.removeIf(t -> t.getId().equals(id));
    }
}
