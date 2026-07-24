package com.example.todoapp.service;

import com.example.todoapp.model.Todo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TodoService class.
 * Tests cover CRUD operations and edge cases.
 */
@DisplayName("TodoService Tests")
class TodoServiceTest {

    private TodoService todoService;

    @BeforeEach
    void setUp() {
        todoService = new TodoService();
    }

    // ==================== getAllTodos Tests ====================

    @Test
    @DisplayName("Should return all todos")
    void testGetAllTodos() {
        // When
        var todos = todoService.getAllTodos();

        // Then
        assertNotNull(todos);
        assertFalse(todos.isEmpty());
        assertTrue(todos.size() >= 5); // Service initializes with 5 todos
    }

    @Test
    @DisplayName("Should return a copy of todos list")
    void testGetAllTodosReturnsCopy() {
        // When
        var todos1 = todoService.getAllTodos();
        var todos2 = todoService.getAllTodos();

        // Then
        assertNotSame(todos1, todos2);
        assertEquals(todos1.size(), todos2.size());
    }

    // ==================== getTodoById Tests ====================

    @Test
    @DisplayName("Should find a todo by valid ID")
    void testGetTodoByIdWithValidId() {
        // Given
        Long testId = 1L;

        // When
        Todo todo = todoService.getTodoById(testId);

        // Then
        assertNotNull(todo);
        assertEquals(testId, todo.getId());
    }

    @Test
    @DisplayName("Should return null for non-existent ID")
    void testGetTodoByIdWithInvalidId() {
        // Given
        Long invalidId = 999L;

        // When
        Todo todo = todoService.getTodoById(invalidId);

        // Then
        assertNull(todo);
    }

    @Test
    @DisplayName("Should find todo by ID 3")
    void testGetTodoByIdThree() {
        // When
        Todo todo = todoService.getTodoById(3L);

        // Then
        assertNotNull(todo);
        assertEquals("Build Project", todo.getTitle());
    }

    // ==================== createTodo Tests ====================

    @Test
    @DisplayName("Should create a new todo successfully")
    void testCreateTodoSuccess() {
        // Given
        Todo newTodo = new Todo(100L, "New Test Todo", false);

        // When
        Todo created = todoService.createTodo(newTodo);

        // Then
        assertNotNull(created);
        assertEquals(100L, created.getId());
        assertEquals("New Test Todo", created.getTitle());
        assertFalse(created.isCompleted());

        // Verify it's in the list
        Todo retrieved = todoService.getTodoById(100L);
        assertNotNull(retrieved);
    }

    @Test
    @DisplayName("Should throw exception when creating todo with duplicate ID")
    void testCreateTodoDuplicateId() {
        // Given
        Todo duplicateTodo = new Todo(1L, "Duplicate ID", false);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            todoService.createTodo(duplicateTodo);
        });
    }

    @Test
    @DisplayName("Should create todo with null ID")
    void testCreateTodoWithNullId() {
        // Given
        Todo newTodo = new Todo(null, "Todo with null ID", false);

        // When
        Todo created = todoService.createTodo(newTodo);

        // Then
        assertNotNull(created);
        assertEquals("Todo with null ID", created.getTitle());
    }

    @Test
    @DisplayName("Should create completed todo")
    void testCreateCompletedTodo() {
        // Given
        Todo completedTodo = new Todo(101L, "Already Completed", true);

        // When
        Todo created = todoService.createTodo(completedTodo);

        // Then
        assertTrue(created.isCompleted());
    }

    // ==================== updateTodo Tests ====================

    @Test
    @DisplayName("Should update existing todo successfully")
    void testUpdateTodoSuccess() {
        // Given
        Long todoId = 1L;
        Todo updatedData = new Todo(null, "Updated Title", true);

        // When
        Todo updated = todoService.updateTodo(todoId, updatedData);

        // Then
        assertNotNull(updated);
        assertEquals(todoId, updated.getId());
        assertEquals("Updated Title", updated.getTitle());
        assertTrue(updated.isCompleted());
    }

    @Test
    @DisplayName("Should return null when updating non-existent todo")
    void testUpdateTodoNotFound() {
        // Given
        Long nonExistentId = 999L;
        Todo updatedData = new Todo(null, "New Title", false);

        // When
        Todo result = todoService.updateTodo(nonExistentId, updatedData);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should update only title when updating todo")
    void testUpdateTodoTitle() {
        // Given
        Long todoId = 2L;
        Todo updateData = new Todo(null, "Updated Practice Java", false);

        // When
        Todo updated = todoService.updateTodo(todoId, updateData);

        // Then
        assertNotNull(updated);
        assertEquals("Updated Practice Java", updated.getTitle());
    }

    @Test
    @DisplayName("Should update completed status")
    void testUpdateTodoCompletionStatus() {
        // Given
        Long todoId = 1L;
        Todo updateData = new Todo(null, "Learn Spring Boot", true);

        // When
        Todo updated = todoService.updateTodo(todoId, updateData);

        // Then
        assertTrue(updated.isCompleted());
    }

    // ==================== deleteTodo Tests ====================

    @Test
    @DisplayName("Should delete an existing todo")
    void testDeleteTodoSuccess() {
        // Given
        Long todoId = 1L;
        assertTrue(todoService.getAllTodos().stream().anyMatch(t -> t.getId().equals(todoId)));

        // When
        todoService.deleteTodo(todoId);

        // Then
        assertNull(todoService.getTodoById(todoId));
    }

    @Test
    @DisplayName("Should not throw exception when deleting non-existent todo")
    void testDeleteNonExistentTodo() {
        // Given
        Long nonExistentId = 999L;

        // When & Then - should not throw exception
        assertDoesNotThrow(() -> {
            todoService.deleteTodo(nonExistentId);
        });
    }

    @Test
    @DisplayName("Should reduce todo count after deletion")
    void testDeleteTodoReducesCount() {
        // Given
        int initialCount = todoService.getAllTodos().size();

        // When
        todoService.deleteTodo(1L);

        // Then
        int finalCount = todoService.getAllTodos().size();
        assertEquals(initialCount - 1, finalCount);
    }

    // ==================== Edge Cases and Integration Tests ====================

    @Test
    @DisplayName("Should handle empty string title")
    void testCreateTodoWithEmptyTitle() {
        // Given
        Todo emptyTitleTodo = new Todo(102L, "", false);

        // When
        Todo created = todoService.createTodo(emptyTitleTodo);

        // Then
        assertNotNull(created);
        assertEquals("", created.getTitle());
    }

    @Test
    @DisplayName("Should handle long title")
    void testCreateTodoWithLongTitle() {
        // Given
        String longTitle = "A".repeat(1000);
        Todo longTitleTodo = new Todo(103L, longTitle, false);

        // When
        Todo created = todoService.createTodo(longTitleTodo);

        // Then
        assertEquals(longTitle, created.getTitle());
    }
}
