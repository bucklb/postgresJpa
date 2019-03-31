package com.example.postgresdemo.controller;

import com.example.postgresdemo.exception.ApplicationException;
import com.example.postgresdemo.model.MixIn_A;
import com.example.postgresdemo.model.MixIn_B;
import com.example.postgresdemo.service.MixInHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("mixin/")
@RestController
public class MixInController {

    @Autowired
    MixInHelper mixInHelper;

    @Autowired
    HttpServletRequest httpServletRequest;


    // TODO : allow a list of fields to be passed in, somehow, to check filtering
    // TODO : two end points - a filterOut fields and a serialize fields ???
    //
    // Probably sensible to have an endpoint where we can pass in made-up end points to emulate service (maybe via header!!)
    //
    @GetMapping("")
    public String doJwt() {

        String ansa=null;
        // Want to run through headers we may have got. But not necessarily ALL the time

        try {
            // Devolve request checking to JwtHelper
            String[] fields = {"id","name","size", "bclass"};
            ansa = mixInHelper.applyMixIn(getTestAClass(), fields,
                    Object.class, MixInHelper.DynamicMixIn.class,
                    true, true);
        } catch (Exception e) {
            // rethrow to pick up any interactionId
            throw new ApplicationException(httpServletRequest, e);
        }

        return ansa;
    }






    private MixIn_A getTestAClass() {
        MixIn_B[] bclass = {
                new MixIn_B("ego","big","small"),
                new MixIn_B("super-ego","little","tall")};
        String[] color = {"black", "blue"};

        MixIn_A aClass = new MixIn_A("69","naughty", color,69, bclass);

        return aClass;
    }


}
