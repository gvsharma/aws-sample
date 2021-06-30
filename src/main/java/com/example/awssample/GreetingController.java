package com.example.awssample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class GreetingController {

    @Autowired
    private DynamoDBEnhanced dde;

    @Autowired
    private PublishTextSMS msg;

    @GetMapping("/")
    public String greetingForm(Model model) {
        model.addAttribute("greeting", new Greeting());
        return "greeting";
    }

    public String greetingSubmit(@ModelAttribute Greeting greeting) {
        dde.injectDynamoItem(greeting);
        msg.sendMessage(greeting.getId());
        return "result";
    }
}
