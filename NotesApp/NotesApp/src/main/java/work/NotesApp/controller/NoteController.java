package work.NotesApp.controller;

import work.NotesApp.entity.Note;
import work.NotesApp.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "*")
public class NoteController {

    @Autowired
    private NoteRepository noteRepository;

    // Debug endpoint to test if controller is working
    @GetMapping("/test")
    public String test() {
        return "Notes API is working! Time: " + java.time.LocalDateTime.now();
    }

    // Simple POST test endpoint
    @PostMapping("/test")
    public String testPost(@RequestBody String testMessage) {
        return "Received: " + testMessage;
    }

    @GetMapping
    public ResponseEntity<?> getAllNotes() {
        try {
            List<Note> notes = noteRepository.findAll();
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving notes: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createNote(@RequestBody Note note) {
        try {
            // Basic validation
            if (note.getTitle() == null || note.getTitle().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Title is required");
            }
            if (note.getContent() == null || note.getContent().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Content is required");
            }

            Note savedNote = noteRepository.save(note);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedNote);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating note: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getNoteById(@PathVariable Long id) {
        try {
            Optional<Note> note = noteRepository.findById(id);
            if (note.isPresent()) {
                return ResponseEntity.ok(note.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Note not found with id: " + id);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving note: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateNote(@PathVariable Long id, @RequestBody Note noteDetails) {
        try {
            Optional<Note> optionalNote = noteRepository.findById(id);
            if (optionalNote.isPresent()) {
                Note note = optionalNote.get();

                // Update only if new values are provided
                if (noteDetails.getTitle() != null) {
                    note.setTitle(noteDetails.getTitle());
                }
                if (noteDetails.getContent() != null) {
                    note.setContent(noteDetails.getContent());
                }

                Note updatedNote = noteRepository.save(note);
                return ResponseEntity.ok(updatedNote);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Note not found with id: " + id);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating note: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable Long id) {
        try {
            if (noteRepository.existsById(id)) {
                noteRepository.deleteById(id);
                return ResponseEntity.ok().body("Note deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Note not found with id: " + id);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting note: " + e.getMessage());
        }
    }

    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Notes API is healthy! Database connected: " +
                (noteRepository != null ? "Yes" : "No"));
    }
}