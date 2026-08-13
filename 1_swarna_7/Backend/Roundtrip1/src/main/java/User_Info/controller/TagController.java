package User_Info.controller;

import User_Info.constants.TagConstants;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    @GetMapping
    public List<String> getAllTags() {
        return TagConstants.ALL_TAGS;
    }
}
