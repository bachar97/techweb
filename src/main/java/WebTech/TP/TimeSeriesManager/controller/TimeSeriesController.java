package WebTech.TP.TimeSeriesManager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TimeSeriesController {


    @GetMapping("/app/timeseries")
    public ModelAndView getTimeSeries() {
        ModelAndView mav = new ModelAndView("createTimeSeries");
        mav.addObject("name");
        return mav;
    }
}
