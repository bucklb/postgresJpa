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
import static org.junit.Assert.assertNull;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AnswerPersistenceTests {

    @Autowired
    private AnswerRepository answerRepository;

    // We've put a bunch of answers in.  They should be retrieved
    @Test
    public void testFindAll(){

        List<Answer> answers = answerRepository.findAll();
        assertNotNull( answers );
        assertEquals(3, answers.size());
    }

    // One question id rules them all
    @Test
    public void testFindByQuestionId() {

        List<Answer> answers = answerRepository.findByQuestionId(99l);
        assertNotNull( answers );
        assertEquals(3, answers.size());
    }


    // Zero question gets nowt
    @Test
    public void testFindByZeroQuestionId() {

        List<Answer> answers = answerRepository.findByQuestionId(0l);
        assertNotNull( answers );
        assertEquals(0, answers.size());
    }


}
