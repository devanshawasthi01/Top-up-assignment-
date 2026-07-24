package com.example.todoapp.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Todo model class.
 * Tests constructors, getters, and setters.
 */
@DisplayName("Todo Model Tests")
class TodoTest {

    private Todo todo;

    @BeforeEach
    void setUp() {
        todo = new Todo();
    }

    // ==================== Constructor Tests ====================

    @Test
    @DisplayName("Should create Todo with default constructor")
    void testDefaultConstructor() {
        // When
        Todo newTodo = new Todo();

        // Then
        assertNotNull(newTodo);
        assertNull(newTodo.getId());
        assertNull(newTodo.getTitle());
        assertFalse(newTodo.isCompleted());
    }

    @Test
    @DisplayName("Should create Todo with parameterized constructor")
    void testParameterizedConstructor() {
        // Given
        Long id = 1L;
        String title = "Learn Spring Boot";
        boolean completed = false;

        // When
        Todo newTodo = new Todo(id, title, completed);

        // Then
        assertNotNull(newTodo);
        assertEquals(id, newTodo.getId());
        assertEquals(title, newTodo.getTitle());
        assertEquals(completed, newTodo.isCompleted());
    }

    @Test
    @DisplayName("Should create completed Todo")
    void testParameterizedConstructorWithCompleted() {
        // When
        Todo completedTodo = new Todo(1L, "Completed Task", true);

        // Then
        assertTrue(completedTodo.isCompleted());
    }

    @Test
    @DisplayName("Should handle null ID in constructor")
    void testParameterizedConstructorWithNullId() {
        // When
        Todo newTodo = new Todo(null, "Task", false);

        // Then
        assertNull(newTodo.getId());
        assertEquals("Task", newTodo.getTitle());
    }

    @Test
    @DisplayName("Should handle null title in constructor")
    void testParameterizedConstructorWithNullTitle() {
        // When
        Todo newTodo = new Todo(1L, null, false);

        // Then
        assertEquals(1L, newTodo.getId());
        assertNull(newTodo.getTitle());
    }

    // ==================== ID Tests ====================

    @Test
    @DisplayName("Should set and get ID")
    void testSetAndGetId() {
        // Given
        Long id = 123L;

        // When
        todo.setId(id);

        // Then
        assertEquals(id, todo.getId());
    }

    @Test
    @DisplayName("Should set null ID")
    void testSetNullId() {
        // Given
        todo.setId(123L);

        // When
        todo.setId(null);

        // Then
        assertNull(todo.getId());
    }

    @Test
    @DisplayName("Should set and get large ID")
    void testSetAndGetLargeId() {
        // Given
        Long largeId = Long.MAX_VALUE;

        // When
        todo.setId(largeId);

        // Then
        assertEquals(largeId, todo.getId());
    }

    @Test
    @DisplayName("Should set and get negative ID")
    void testSetAndGetNegativeId() {
        // Given
        Long negativeId = -1L;

        // When
        todo.setId(negativeId);

        // Then
        assertEquals(negativeId, todo.getId());
    }

    // ==================== Title Tests ====================

    @Test
    @DisplayName("Should set and get title")
    void testSetAndGetTitle() {
        // Given
        String title = "Learn Spring Boot";

        // When
        todo.setTitle(title);

        // Then
        assertEquals(title, todo.getTitle());
    }

    @Test
    @DisplayName("Should set null title")
    void testSetNullTitle() {
        // Given
        todo.setTitle("Original Title");

        // When
        todo.setTitle(null);

        // Then
        assertNull(todo.getTitle());
    }

    @Test
    @DisplayName("Should set empty string title")
    void testSetEmptyTitle() {
        // When
        todo.setTitle("");

        // Then
        assertEquals("", todo.getTitle());
    }

    @Test
    @DisplayName("Should set long title")
    void testSetLongTitle() {
        // Given
        String longTitle = "A".repeat(5000);

        // When
        todo.setTitle(longTitle);

        // Then
        assertEquals(longTitle, todo.getTitle());
        assertEquals(5000, todo.getTitle().length());
    }

    @Test
    @DisplayName("Should set title with special characters")
    void testSetTitleWithSpecialCharacters() {
        // Given
        String specialTitle = "Learn @#$%^&*() Spring!";

        // When
        todo.setTitle(specialTitle);

        // Then
        assertEquals(specialTitle, todo.getTitle());
    }

    @Test
    @DisplayName("Should set title with unicode characters")
    void testSetTitleWithUnicodeCharacters() {
        // Given
        String unicodeTitle = "Learn 春季 Boot 🚀";

        // When
        todo.setTitle(unicodeTitle);

        // Then
        assertEquals(unicodeTitle, todo.getTitle());
    }

    // ==================== Completed Tests ====================

    @Test
    @DisplayName("Should set and get completed status as true")
    void testSetAndGetCompletedTrue() {
        // When
        todo.setCompleted(true);

        // Then
        assertTrue(todo.isCompleted());
    }

    @Test
    @DisplayName("Should set and get completed status as false")
    void testSetAndGetCompletedFalse() {
        // When
        todo.setCompleted(false);

        // Then
        assertFalse(todo.isCompleted());
    }

    @Test
    @DisplayName("Should toggle completed status")
    void testToggleCompletedStatus() {
        // Given
        todo.setCompleted(false);

        // When
        todo.setCompleted(true);

        // Then
        assertTrue(todo.isCompleted());

        // When
        todo.setCompleted(false);

        // Then
        assertFalse(todo.isCompleted());
    }

    // ==================== Combined Tests ====================

    @Test
    @DisplayName("Should set and get all fields")
    void testSetAndGetAllFields() {
        // Given
        Long id = 1L;
        String title = "Comprehensive Test";
        boolean completed = true;

        // When
        todo.setId(id);
        todo.setTitle(title);
        todo.setCompleted(completed);

        // Then
        assertEquals(id, todo.getId());
        assertEquals(title, todo.getTitle());
        assertTrue(todo.isCompleted());
    }

    @Test
    @DisplayName("Should update all fields multiple times")
    void testUpdateAllFieldsMultipleTimes() {
        // First update
        todo.setId(1L);
        todo.setTitle("First Title");
        todo.setCompleted(false);

        assertEquals(1L, todo.getId());
        assertEquals("First Title", todo.getTitle());
        assertFalse(todo.isCompleted());

        // Second update
        todo.setId(2L);
        todo.setTitle("Second Title");
        todo.setCompleted(true);

        assertEquals(2L, todo.getId());
        assertEquals("Second Title", todo.getTitle());
        assertTrue(todo.isCompleted());
    }

    @Test
    @DisplayName("Should copy constructor values correctly")
    void testCopyConstructorValues() {
        // Given
        Todo original = new Todo(1L, "Original", true);

        // When
        Todo copy = new Todo(original.getId(), original.getTitle(), original.isCompleted());

        // Then
        assertEquals(original.getId(), copy.getId());
        assertEquals(original.getTitle(), copy.getTitle());
        assertEquals(original.isCompleted(), copy.isCompleted());
    }

    @Test
    @DisplayName("Should maintain data integrity between instances")
    void testDataIntegrityBetweenInstances() {
        // Given
        Todo todo1 = new Todo(1L, "Task 1", false);
        Todo todo2 = new Todo(2L, "Task 2", true);

        // When - modify todo1
        todo1.setTitle("Modified Task 1");
        todo1.setCompleted(true);

        // Then - todo2 should remain unchanged
        assertEquals("Task 2", todo2.getTitle());
        assertTrue(todo2.isCompleted());
    }

    // ==================== Equality and Comparison Tests ====================

    @Test
    @DisplayName("Two todos with same values should be comparable")
    void testTodosWithSameValues() {
        // Given
        Todo todo1 = new Todo(1L, "Same Task", false);
        Todo todo2 = new Todo(1L, "Same Task", false);

        // Then
        assertEquals(todo1.getId(), todo2.getId());
        assertEquals(todo1.getTitle(), todo2.getTitle());
        assertEquals(todo1.isCompleted(), todo2.isCompleted());
    }

    @Test
    @DisplayName("Two todos with different IDs should be comparable")
    void testTodosWithDifferentIds() {
        // Given
        Todo todo1 = new Todo(1L, "Task", false);
        Todo todo2 = new Todo(2L, "Task", false);

        // Then
        assertNotEquals(todo1.getId(), todo2.getId());
    }
}
