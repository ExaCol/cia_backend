package CIA.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import CIA.app.services.UsrService;

@RestController
@RequestMapping("/usr")
public class UsrController {
    @Autowired
    private UsrService usrService;

    public UsrController(UsrService usrService) {
        this.usrService = usrService;
    }
}
