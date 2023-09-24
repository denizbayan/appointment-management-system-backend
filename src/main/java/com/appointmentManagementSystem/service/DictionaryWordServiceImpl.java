package com.appointmentManagementSystem.service;

import com.appointmentManagementSystem.model.EntityBlogPost;
import com.appointmentManagementSystem.model.EntityDictionaryWord;
import com.appointmentManagementSystem.payload.AddBlogPostPayload;
import com.appointmentManagementSystem.payload.AddDictionaryWordPayload;
import com.appointmentManagementSystem.repository.BlogPostRepository;
import com.appointmentManagementSystem.repository.DictionaryWordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DictionaryWordServiceImpl implements DictionaryWordService {

    DictionaryWordRepository dictionaryWordRepository ;

    @Autowired
    public DictionaryWordServiceImpl(DictionaryWordRepository dictionaryWordRepository) {
        this.dictionaryWordRepository=dictionaryWordRepository;
    }

    public List<EntityDictionaryWord> getDictionary(){
        return dictionaryWordRepository.findAllByDeleted(false);
    }

    public EntityDictionaryWord saveDictionaryWord(AddDictionaryWordPayload newDictionaryWord){

        if(newDictionaryWord.getId() == -1L){
            EntityDictionaryWord newWord = new EntityDictionaryWord();
            newWord.setDeleted(false);
            newWord.setWord(newDictionaryWord.getWord());
            newWord.setMeaning(newDictionaryWord.getMeaning());

            return dictionaryWordRepository.save(newWord);
        }else{
            Optional<EntityDictionaryWord> bp = dictionaryWordRepository.findByIdAndDeleted(newDictionaryWord.getId(),false);
            if (bp.isPresent()){
                EntityDictionaryWord existingWord = bp.get();
                existingWord.setWord(newDictionaryWord.getWord());
                existingWord.setMeaning(newDictionaryWord.getMeaning());

                return dictionaryWordRepository.save(existingWord);
            }else{
                return null;
            }
        }

    }

    public Long deleteDictionaryWord(Long postID){return dictionaryWordRepository.updateDeleted(postID)==0?0L:1L;}

}

