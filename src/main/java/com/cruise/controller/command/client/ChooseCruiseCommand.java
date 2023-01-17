package com.cruise.controller.command.client;

import com.cruise.appcontext.AppContext;
import com.cruise.controller.AllPath;
import com.cruise.controller.PaginationUtil;
import com.cruise.controller.command.Command;
import com.cruise.exceptions.ServiceException;
import com.cruise.model.Cruise;
import com.cruise.service.CruiseService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class ChooseCruiseCommand implements Command {
    private CruiseService cruiseService;
    public ChooseCruiseCommand(){
        cruiseService = AppContext.getInstance().getCruiseService();
    }
    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        String forward = AllPath.CHOOSE_CRUISE_PAGE;
        String cruiseId = req.getParameter("findCruiseId");
        List<Cruise> cruises = new ArrayList<>();
        if (cruiseId != null){
            try {
                cruises.add(cruiseService.findById(Integer.parseInt(cruiseId)));
            } catch (ServiceException e) {
                req.setAttribute("error", e.getMessage());
            }
            req.setAttribute("cruises", cruises);
            return forward;
        }
        int orderBy = PaginationUtil.getSortBy(req);
        int limit = PaginationUtil.getLimit(req);
        int offset = PaginationUtil.getPage(req) * limit;
        int amount;
        try {
            amount = cruiseService.getAllCruise().size();
        } catch (ServiceException e) {
            req.setAttribute("error", e.getMessage());
            return forward;
        }
        int pages = PaginationUtil.getPages(amount, limit);
        try {
            cruiseService.checkAllCruise();
            cruises = cruiseService.getCruisesInOrderAndLimit(orderBy, limit, offset);
        } catch (ServiceException e) {
            req.setAttribute("error", e.getMessage());
            return forward;
        }
        req.setAttribute("pages", pages);
        req.setAttribute("cruises", cruises);
        return forward;
    }
}
