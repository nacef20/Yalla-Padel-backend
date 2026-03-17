package tn.esprit.todomodule.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.todomodule.dto.TodoStatsDTO;
import tn.esprit.todomodule.entity.Level;
import tn.esprit.todomodule.entity.Todo;
import tn.esprit.todomodule.entity.TodoStatus;
import tn.esprit.todomodule.service.TodoService;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public ResponseEntity<List<Todo>> getAll() {
        return ResponseEntity.ok(todoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Todo> getById(@PathVariable Long id) {
        return todoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Todo>> getByStatus(@PathVariable TodoStatus status) {
        return ResponseEntity.ok(todoService.findByStatus(status));
    }

    @GetMapping("/stats")
    public ResponseEntity<TodoStatsDTO> getStats() {
        return ResponseEntity.ok(todoService.getStats());
    }

    @PostMapping("/add")
    public ResponseEntity<Todo> create(@Valid @RequestBody Todo todo, @RequestParam(required = false, defaultValue = "1") Long userId) {
        Todo created = todoService.create(todo, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Todo> update(
            @PathVariable Long id,
            @Valid @RequestBody Todo todo) {
        Todo updated = todoService.update(id, todo);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam Long id) {
        try {
            // using 1L as default user and true as isTeacher to bypass auth checks
            todoService.deleteById(id, 1L, true);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
