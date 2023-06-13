package com.appointmentManagementSystem.service;

import com.appointmentManagementSystem.model.EntityBlogPost;
import com.appointmentManagementSystem.model.EntityDictionaryWord;
import com.appointmentManagementSystem.payload.AddBlogPostPayload;
import com.appointmentManagementSystem.payload.AddDictionaryWordPayload;

import java.util.List;

public interface DictionaryWordService {
    public List<EntityDictionaryWord> getDictionary();

    public EntityDictionaryWord saveDictionaryWord(AddDictionaryWordPayload newDictionaryWord);

    public Long deleteDictionaryWord(Long postID);


}
