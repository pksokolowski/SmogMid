package com.github.pksokolowski.smogmid.info;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class InfoController {

    private InfoService infoService;

    public InfoController(InfoService infoService) {
        this.infoService = infoService;
    }

    @GetMapping("/info")
    @ResponseBody
    public String getInfo(){
        return infoService.getInfo();
    }
}
