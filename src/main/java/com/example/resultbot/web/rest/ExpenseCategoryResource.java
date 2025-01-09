package com.example.resultbot.web.rest;

import com.example.resultbot.entity.ExpenseCategory;
import com.example.resultbot.service.ExpenseCategoryService;
import com.example.resultbot.service.dto.ExpenseCategoryDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/expense-category")
public class ExpenseCategoryResource {
    private final ExpenseCategoryService expenseCategoryService;

    public ExpenseCategoryResource(ExpenseCategoryService expenseCategoryService) {
        this.expenseCategoryService = expenseCategoryService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody ExpenseCategoryDto expenseCategoryDto) throws URISyntaxException {
        ExpenseCategoryDto result = expenseCategoryService.create(expenseCategoryDto);
        return ResponseEntity.created(new URI("/api/expense-category/create/" + result.getId())).body(result);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@RequestBody ExpenseCategoryDto expenseCategoryDto, @PathVariable Long id) throws URISyntaxException {
        if (expenseCategoryDto.getId() != 0 && !expenseCategoryDto.getId().equals(id)) {
            return ResponseEntity.badRequest().body("Invalid id");
        }
        ExpenseCategoryDto result = expenseCategoryService.update(expenseCategoryDto);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAll() {
        List<ExpenseCategoryDto> expenseCategories = expenseCategoryService.findAllExpenseCategories();
        return ResponseEntity.ok(expenseCategories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        ExpenseCategory expenseCategory = expenseCategoryService.findById(id);
        return ResponseEntity.ok(expenseCategory);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        ExpenseCategory expenseCategory = expenseCategoryService.delete(id);
        return ResponseEntity.ok(expenseCategory);
    }
}
