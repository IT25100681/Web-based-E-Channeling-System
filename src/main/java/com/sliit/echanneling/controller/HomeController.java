package com.sliit.echanneling.controller;

import com.sliit.echanneling.service.HospitalService;
import com.sliit.echanneling.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final HospitalService hospitalService;
    private final ScheduleService scheduleService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("specializations", hospitalService.getAllSpecializations());
        model.addAttribute("hospitals", hospitalService.getAllHospitals());
        model.addAttribute("schedules", scheduleService.searchAvailableSchedules(null, null));
        return "index";
    }
}
