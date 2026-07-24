package com.example.todoapp.controller;

import com.example.todoapp.model.Todo;
import com.example.todoapp.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

/**
 * Controller tests for TodoController.
 * Tests REST API endpoints using MockMvc.
 */
@WebMvcTest(TodoController.class)
@DisplayName("TodoController Tests")
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TodoService todoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Todo testTodo;

    @BeforeEach
    void setUp() {
        testTodo = new Todo(1L, "Learn Spring Boot", false);
    }

    // ==================== GET /api/todos Tests ====================

    @Test
    @DisplayName("Should return all todos with status 200")
    void testGetAllTodos() throws Exception {
        // Given
        List<Todo> todos = Arrays.asList(
            new Todo(1L, "Learn Spring Boot", false),
            new Todo(2L, "Practice Java", false)
        );
        when(todoService.getAllTodos()).thenReturn(todos);

        // When & Then
        mockMvc.perform(get("/api/todos")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].title", is("Learn Spring Boot")))
            .andExpect(jsonPath("$[1].id", is(2)));

        // Verify
        verify(todoService, times(1)).getAllTodos();
    }

    @Test
    @DisplayName("Should return empty list when no todos exist")
    void testGetAllTodosEmpty() throws Exception {
        // Given
        when(todoService.getAllTodos()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/todos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    // ==================== GET /api/todos/{id} Tests ====================

    @Test
    @DisplayName("Should return todo by ID with status 200")
    void testGetTodoById() throws Exception {
        // Given
        when(todoService.getTodoById(1L)).thenReturn(testTodo);

        // When & Then
        mockMvc.perform(get("/api/todos/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.title", is("Learn Spring Boot")))
            .andExpect(jsonPath("$.completed", is(false)));

        // Verify
        verify(todoService, times(1)).getTodoById(1L);
    }

    @Test
    @DisplayName("Should return null when todo not found by ID")
    void testGetTodoByIdNotFound() throws Exception {
        // Given
        when(todoService.getTodoById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/todos/999"))
            .andExpect(status().isOk());

        verify(todoService, times(1)).getTodoById(999L);
    }

    @Test
    @DisplayName("Should return completed todo")
    void testGetCompletedTodo() throws Exception {
        // Given
        Todo completedTodo = new Todo(1L, "Learn Spring Boot", true);
        when(todoService.getTodoById(1L)).thenReturn(completedTodo);

        // When & Then
        mockMvc.perform(get("/api/todos/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.completed", is(true)));
    }

    // ==================== POST /api/todos Tests ====================

    @Test
    @DisplayName("Should create a new todo with status 200")
    void testCreateTodo() throws Exception {
        // Given
        Todo newTodo = new Todo(3L, "Build Project", false);
        when(todoService.createTodo(isA(Todo.class))).thenReturn(newTodo);

        // When & Then
        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTodo)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(3)))
            .andExpect(jsonPath("$.title", is("Build Project")))
            .andExpect(jsonPath("$.completed", is(false)));

        // Verify
        verify(todoService, times(1)).createTodo(isA(Todo.class));
    }

    @Test
    @DisplayName("Should handle invalid JSON in create request")
    void testCreateTodoInvalidJSON() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should create completed todo")
    void testCreateCompletedTodo() throws Exception {
        // Given
        Todo completedTodo = new Todo(4L, "Completed Task", true);
        when(todoService.createTodo(isA(Todo.class))).thenReturn(completedTodo);

        // When & Then
        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(completedTodo)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.completed", is(true)));
    }


    @Test
    @DisplayName("Should get status 200 when posting valid todo")
    void testCreateTodoValid() throws Exception {
        // Given
        Todo newTodo = new Todo(100L, "Another Task", false);
        when(todoService.createTodo(isA(Todo.class))).thenReturn(newTodo);

        // When & Then
        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTodo)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(100)))
            .andExpect(jsonPath("$.title", is("Another Task")));
    }

    // ==================== PUT /api/todos/{id} Tests ====================

    @Test
    @DisplayName("Should update existing todo with status 200")
    void testUpdateTodo() throws Exception {
        // Given
        Todo updateData = new Todo(null, "Updated Title", true);
        Todo updatedTodo = new Todo(1L, "Updated Title", true);
        when(todoService.updateTodo(eq(1L), isA(Todo.class))).thenReturn(updatedTodo);

        // When & Then
        mockMvc.perform(put("/api/todos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.title", is("Updated Title")))
            .andExpect(jsonPath("$.completed", is(true)));

        // Verify
        verify(todoService, times(1)).updateTodo(eq(1L), isA(Todo.class));
    }

    @Test
    @DisplayName("Should return null when updating non-existent todo")
    void testUpdateTodoNotFound() throws Exception {
        // Given
        Todo updateData = new Todo(null, "Updated Title", false);
        when(todoService.updateTodo(eq(999L), isA(Todo.class))).thenReturn(null);

        // When & Then
        mockMvc.perform(put("/api/todos/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should update only title")
    void testUpdateTodoTitle() throws Exception {
        // Given
        Todo updateData = new Todo(null, "New Title", false);
        Todo updatedTodo = new Todo(1L, "New Title", false);
        when(todoService.updateTodo(eq(1L), isA(Todo.class))).thenReturn(updatedTodo);

        // When & Then
        mockMvc.perform(put("/api/todos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
            .andExpect(jsonPath("$.title", is("New Title")));
    }

    @Test
    @DisplayName("Should update completion status")
    void testUpdateTodoCompletionStatus() throws Exception {
        // Given
        Todo updateData = new Todo(null, "Learn Spring Boot", true);
        Todo updatedTodo = new Todo(1L, "Learn Spring Boot", true);
        when(todoService.updateTodo(eq(1L), isA(Todo.class))).thenReturn(updatedTodo);

        // When & Then
        mockMvc.perform(put("/api/todos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
            .andExpect(jsonPath("$.completed", is(true)));
    }

    // ==================== DELETE /api/todos/{id} Tests ====================

    @Test
    @DisplayName("Should delete todo with status 200")
    void testDeleteTodo() throws Exception {
        // Given
        doNothing().when(todoService).deleteTodo(1L);

        // When & Then
        mockMvc.perform(delete("/api/todos/1"))
            .andDo(print())
            .andExpect(status().isOk());

        // Verify
        verify(todoService, times(1)).deleteTodo(1L);
    }

    @Test
    @DisplayName("Should not throw exception when deleting non-existent todo")
    void testDeleteNonExistentTodo() throws Exception {
        // Given
        doNothing().when(todoService).deleteTodo(999L);

        // When & Then
        mockMvc.perform(delete("/api/todos/999"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should handle multiple deletions")
    void testDeleteMultipleTodos() throws Exception {
        // Given
        doNothing().when(todoService).deleteTodo(anyLong());

        // When & Then
        for (int i = 1; i <= 3; i++) {
            mockMvc.perform(delete("/api/todos/" + i))
                .andExpect(status().isOk());
        }

        verify(todoService, times(3)).deleteTodo(anyLong());
    }

    // ==================== Integration-like Tests ====================

    @Test
    @DisplayName("Should handle sequence: create -> get -> update -> delete")
    void testCreateGetUpdateDeleteSequence() throws Exception {
        // Create
        Todo newTodo = new Todo(10L, "Test Todo", false);
        when(todoService.createTodo(isA(Todo.class))).thenReturn(newTodo);

        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTodo)))
            .andExpect(status().isOk());

        // Get
        when(todoService.getTodoById(10L)).thenReturn(newTodo);
        mockMvc.perform(get("/api/todos/10"))
            .andExpect(status().isOk());

        // Update
        Todo updatedTodo = new Todo(10L, "Updated Test Todo", true);
        when(todoService.updateTodo(eq(10L), isA(Todo.class))).thenReturn(updatedTodo);

        mockMvc.perform(put("/api/todos/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTodo)))
            .andExpect(status().isOk());

        // Delete
        doNothing().when(todoService).deleteTodo(10L);
        mockMvc.perform(delete("/api/todos/10"))
            .andExpect(status().isOk());
    }
}
