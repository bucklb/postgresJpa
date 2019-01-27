package com.example.postgresdemo;

import com.example.postgresdemo.model.Answer;
import com.example.postgresdemo.model.Question;
import com.example.postgresdemo.repository.AnswerRepository;
import com.example.postgresdemo.repository.QuestionRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QuestionPersistenceTests {

    @Autowired
    private QuestionRepository questionRepository;

    // We've put a bunch of answers in.  They should be retrieved
    @Test
    public void testFindAll(){

        List<Question> questions = questionRepository.findAll();
        assertNotNull( questions );
        assertEquals(1, questions.size());

    }





}
