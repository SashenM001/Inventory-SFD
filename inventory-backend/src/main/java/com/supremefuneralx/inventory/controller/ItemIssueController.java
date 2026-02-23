package com.supremefuneralx.inventory.controller;

import com.supremefuneralx.inventory.dto.ItemIssueDTO;
import com.supremefuneralx.inventory.service.ItemIssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/issues")
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:8081", "http://localhost:3000",
        "http://localhost:8084", "https://inventory-sfd.vercel.app" })
public class ItemIssueController {

    private final ItemIssueService issueService;

    @PostMapping
    public ResponseEntity<ItemIssueDTO> issueItem(@RequestBody ItemIssueDTO issueDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(issueService.issueItem(issueDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemIssueDTO> getIssueById(@PathVariable Long id) {
        return ResponseEntity.ok(issueService.getIssueById(id));
    }

    @GetMapping
    public ResponseEntity<List<ItemIssueDTO>> getAllIssues() {
        return ResponseEntity.ok(issueService.getAllIssues());
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<ItemIssueDTO>> getIssuesByItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(issueService.getIssuesByItem(itemId));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<ItemIssueDTO>> getRecentIssues() {
        return ResponseEntity.ok(issueService.getRecentIssues());
    }

    @GetMapping("/range")
    public ResponseEntity<List<ItemIssueDTO>> getIssuesBetweenDates(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        return ResponseEntity.ok(issueService.getIssuesBetweenDates(startDate, endDate));
    }
}
