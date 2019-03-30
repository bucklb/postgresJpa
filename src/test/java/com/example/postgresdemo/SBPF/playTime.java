package com.example.postgresdemo.SBPF;

import org.junit.Test;
import org.springframework.web.servlet.mvc.method.annotation.ServletWebArgumentResolverAdapter;

import java.util.ArrayList;
import java.util.List;

// Prove to myself what can be treated like "pass by reference"
public class playTime {

    class BB {
        String key;
        String valiue;

        public BB(String key, String valiue) {
            this.key = key;
            this.valiue = valiue;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValiue() {
            return valiue;
        }

        public void setValiue(String valiue) {
            this.valiue = valiue;
        }
    }


    private void attempt(String fred, List<String> al, BB bb, long l, Long ll) {
        fred = "attempt";
        al.add(fred);
        bb.key="skeleton";
        bb.valiue="little";
        l=69;
        ll=69l;
    }



    // Can modify a class/object we pass in but not a String, long.
    // ?? Rule of thumb - if we have to use "new" to create it then we can probably amend its value in a call ??
    @Test
    public void go() {
        ArrayList<String> al = new ArrayList<>();
        String bert="";
        long l=96;
        Long ll=96L;
        BB bb = new BB("","");
        attempt(bert,al,bb,l, ll);
        System.out.println(bert + " : " + al.get(0) + " + " + bb.getKey() + " : " + l + " : " + ll);
    }



}
