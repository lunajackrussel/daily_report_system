package controllers.reports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.persistence.EntityManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Report;
import utils.DBUtil;

/**
 * Servlet implementation class ReportsIndexServlet
 */
@WebServlet("/reports/index")
public class ReportsIndexServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReportsIndexServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EntityManager em = DBUtil.createEntityManager();

        int page;
        try{
            page = Integer.parseInt(request.getParameter("page"));
        } catch(Exception e) {
            page = 1;
        }


        List<Report> reports = em.createNamedQuery("getAllReports", Report.class)
                                  .setFirstResult(15 * (page - 1))
                                  .setMaxResults(15)
                                  .getResultList();

       long reports_count = (long)em.createNamedQuery("getReportsCount", Long.class)
                                    .getSingleResult();
       /*countで件数を取ってくる
        long likes_count = (long)em.createNamedQuery("getLikesCount", Long.class)
               .getSingleResult();
       long comments_count = (long)em.createNamedQuery("getCommentsCount", Long.class)
               .getSingleResult();
       List<Like> likes = em.createNamedQuery("getAllLikes", Like.class).getResultList();//全件tableから持ってきてる*/

        ListIterator<Report> iterator = reports.listIterator();
        List<String> likes_count_list = new ArrayList<String>();
       // List<String> comments_count_list = new ArrayList<String>();

        while (iterator.hasNext()) {
            Report rp = iterator.next();
            // 日報毎のいいね数を取得
            long likes_count = (long)em.createNamedQuery("getLikesCountByReport", Long.class)
                                     .setParameter("report", rp)
                                     .getSingleResult();
            // 日報毎のコメント数を取得
            /*long comments_count = (long)em.createNamedQuery("getCommentsCountByReport", Long.class)
                                     .setParameter("report", rp)
                                     .getSingleResult();*/
            //likes_count_list.add("1");
            //comments_count_list.add("2");
            //likes_count_list.add(likes_count);
            //comments_count_list.add(comments_count);
            likes_count_list.add(Long.toString(likes_count));
           // Long.toString(comments_count);
        }
        //lesson14参考　レポートを条件にして取ってくる処理


        em.close();

        request.setAttribute("reports", reports);
        request.setAttribute("reports_count", reports_count);
        request.setAttribute("page", page);
        request.setAttribute("likes_count_list", likes_count_list);
        //request.setAttribute("comments_count_list", comments_count_list);

        if(request.getSession().getAttribute("flush") != null) {
            request.setAttribute("flush", request.getSession().getAttribute("flush"));
            request.getSession().removeAttribute("flush");
        }

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/reports/index.jsp");
        rd.forward(request, response);
    }

}