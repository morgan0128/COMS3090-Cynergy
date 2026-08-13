package coms309;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
class WelcomeController {

    @GetMapping("/")
    public String welcome() {
        return "Hello and welcome to COMS 309";
    }
    
    @GetMapping("/{name}")
    public String welcome(@PathVariable String name) {
        return "Hello and welcome to COMS 309 " + name + "!";
    }

    @GetMapping("/Unit1")
    public String info() { return "In Unit1, we explore the basics of Maven and Spring Boot"; }

    //Alternate method for GET mapping
    @RequestMapping( method = { RequestMethod.GET }, value = { "/Unit1/Exp1" })
    public String exp1() {
        String str = "<html><body><font color=\"red\">"
                + "<h3>In exp1, changes include:" + "<br>"
                + "1. Formatting change to home path" + "<br>"
                + "2. Unit1 Path and string to practice GET annotation" + "<br>"
                + "3. Importing Request Mapping and Request Method to test new ways to use GET" + "<br>"
                + "4. Changed server port from application.properties"
                + "</h3></font></body></html>";
        return str;
    }
}
