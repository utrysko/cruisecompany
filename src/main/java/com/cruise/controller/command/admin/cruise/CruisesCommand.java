package com.cruise.controller.command.admin.cruise;

import com.cruise.controller.appcontext.AppContext;
import com.cruise.controller.AllPath;
import com.cruise.utils.PaginationUtil;
import com.cruise.controller.command.Command;
import com.cruise.exceptions.ServiceException;
import com.cruise.model.entities.Cruise;
import com.cruise.model.service.CruiseService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for getting cruises. Accessible only by admin.
 *
 * @author Vasyl Utrysko
 * @version 1.0
 */
public class CruisesCommand implements Command {

    private static final Logger LOG = LogManager.getLogger(CruisesCommand.class);

    private final CruiseService cruiseService;
    public CruisesCommand(){
        cruiseService = AppContext.getInstance().getCruiseService();
    }
    public CruisesCommand(CruiseService cruiseService){
        this.cruiseService = cruiseService;
    }

    /**
     * Called from main controller. Tries to get cruises in some order.
     *
     * @param req for get parameters to getting cruises
     * @return path to forward from main controller
     */
    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) {
        String forward = AllPath.CRUISES_PAGE;
        String cruiseId = req.getParameter("findCruiseId");
        List<Cruise> cruises = new ArrayList<>();
        if (cruiseId != null){
            return findById(req, forward, cruiseId, cruises);
        }
        int orderBy = PaginationUtil.getSortBy(req);
        int limit = PaginationUtil.getLimit(req);
        int offset = PaginationUtil.getPage(req) * limit;
        int amount;
        int pages;
        try {
            amount = cruiseService.countAll();
            pages = PaginationUtil.getPages(amount, limit);
            cruises = cruiseService.getCruisesInOrderAndLimit(orderBy, limit, offset);
        } catch (ServiceException e) {
            LOG.error(e.getMessage());
            req.setAttribute("error", e.getMessage());
            return forward;
        }
        req.setAttribute("pages", pages);
        req.setAttribute("cruises", cruises);
        return forward;
    }

    /**
     * Method to get Cruise by id.
     * @param req to get Cruise Id
     * @return path to forward from main controller
     */
    private String findById(HttpServletRequest req, String forward, String cruiseId, List<Cruise> cruises) {
        try {
            cruises.add(cruiseService.findById(Integer.parseInt(cruiseId)));
        } catch (ServiceException e) {
            LOG.error(e.getMessage());
            req.setAttribute("error", e.getMessage());
        }
        req.setAttribute("cruises", cruises);
        return forward;
    }
}
