package com.supremefuneralx.inventory.service;

import com.supremefuneralx.inventory.dto.ItemIssueDTO;
import com.supremefuneralx.inventory.entity.ItemIssue;
import com.supremefuneralx.inventory.entity.InventoryItem;
import com.supremefuneralx.inventory.repository.ItemIssueRepository;
import com.supremefuneralx.inventory.repository.InventoryItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemIssueService {

    private final ItemIssueRepository issueRepository;
    private final InventoryItemRepository itemRepository;

    public ItemIssueDTO issueItem(ItemIssueDTO issueDTO) {
        InventoryItem item = itemRepository.findById(issueDTO.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + issueDTO.getItemId()));

        if (item.getQuantity() < issueDTO.getQuantity()) {
            throw new RuntimeException("Insufficient quantity available. Available: " + item.getQuantity());
        }

        ItemIssue issue = ItemIssue.builder()
                .item(item)
                .quantity(issueDTO.getQuantity())
                .issuedTo(issueDTO.getIssuedTo())
                .reason(issueDTO.getReason())
                .notes(issueDTO.getNotes())
                .build();

        // Decrease the quantity
        item.setQuantity(item.getQuantity() - issueDTO.getQuantity());
        itemRepository.save(item);

        ItemIssue savedIssue = issueRepository.save(issue);
        return mapToDTO(savedIssue);
    }

    @Transactional(readOnly = true)
    public ItemIssueDTO getIssueById(Long id) {
        ItemIssue issue = issueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Issue not found with id: " + id));
        return mapToDTO(issue);
    }

    @Transactional(readOnly = true)
    public List<ItemIssueDTO> getAllIssues() {
        return issueRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ItemIssueDTO> getIssuesByItem(Long itemId) {
        return issueRepository.findByItemId(itemId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ItemIssueDTO> getIssuesBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return issueRepository.findIssuesBetweenDates(startDate, endDate).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ItemIssueDTO> getRecentIssues() {
        return issueRepository.findRecentIssues().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private ItemIssueDTO mapToDTO(ItemIssue issue) {
        return ItemIssueDTO.builder()
                .id(issue.getId())
                .itemId(issue.getItem().getId())
                .itemName(issue.getItem().getName())
                .quantity(issue.getQuantity())
                .issuedTo(issue.getIssuedTo())
                .reason(issue.getReason())
                .notes(issue.getNotes())
                .createdAt(issue.getCreatedAt())
                .build();
    }
}
