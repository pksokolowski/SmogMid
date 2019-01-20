package com.github.pksokolowski.smogmid.info;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InfoController {

    private InfoService infoService;

    public InfoController(InfoService infoService) {
        this.infoService = infoService;
    }

    @GetMapping("/info")
    public String getInfoView( Model model){
        var info = infoService.getInfo();
        model.addAttribute("logsCount", info.getLogsCount());
        model.addAttribute("logsWithIndexes", info.getLogsWithIndexes());
        model.addAttribute("updateTime", info.getUpdateTime());
        model.addAttribute("updateDuration", info.getUpdateDuration());
        model.addAttribute("indexLevelDistribution", info.getIndexLevelDistribution());
        model.addAttribute("keyPollutants", info.getKeyPollutants());
        model.addAttribute("details", info.getDetails());
        return "info";
    }
}
