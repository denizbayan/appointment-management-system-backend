package com.appointmentManagementSystem.controller;

import com.appointmentManagementSystem.model.EntityBlogPost;
import com.appointmentManagementSystem.model.EntityDictionaryWord;
import com.appointmentManagementSystem.payload.AddBlogPostPayload;
import com.appointmentManagementSystem.payload.AddDictionaryWordPayload;
import com.appointmentManagementSystem.payload.MessageResponse;
import com.appointmentManagementSystem.repository.DictionaryWordRepository;
import com.appointmentManagementSystem.service.BlogPostService;
import com.appointmentManagementSystem.service.DictionaryWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/dictionary")
public class PsyDictionaryController {

    @Autowired
    DictionaryWordService dictionaryWordService;

    @GetMapping("/getDictionary")
    public List<EntityDictionaryWord> getDictionary(){
        return dictionaryWordService.getDictionary();

    }

    @DeleteMapping("/deleteDictionaryWord/{postID}")
    @PreAuthorize(" hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteDictionaryWord(@PathVariable long postID){
        dictionaryWordService.deleteDictionaryWord(postID);
        return null;
    }

    @PostMapping("/saveDictionaryWord")
    @PreAuthorize(" hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> saveDictionaryWord(@RequestBody AddDictionaryWordPayload newDictionaryWord){
        EntityDictionaryWord response = dictionaryWordService.saveDictionaryWord(newDictionaryWord);

        if (response == null){
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Güncellemek istediğiniz kelime bulunamamıştır. Lütfen sayfanızı yeniledikten sonra tekrar deneyiniz."));
        }else{
            if(newDictionaryWord.getId() == -1){
                return ResponseEntity
                        .ok()
                        .body(new MessageResponse("Kelime başarıyla eklenmiştir."));
            }else{
                return ResponseEntity
                        .ok()
                        .body(new MessageResponse("Kelime başarıyla güncellenmiştir."));
            }

        }


    }

}
