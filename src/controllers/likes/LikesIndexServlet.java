package controllers.likes;

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

import models.Like;
import models.Report;
import utils.DBUtil;

/**
 * Servlet implementation class LikesIndexServlet
 */
@WebServlet("/likes/index")
public class LikesIndexServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public LikesIndexServlet() {
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
        List<Report> reports = em.createNamedQuery("getAllReports", Report.class).getResultList();
        List<Like> likes = em.createNamedQuery("getLikesByReport", Like.class)
                                  .setFirstResult(15 * (page - 1))
                                  .setMaxResults(15)
                                  .getResultList();

        ListIterator<Report> iterator = reports.listIterator();

        List<String> likes_count_list = new ArrayList<String>();


        while (iterator.hasNext()) {
            Report rp = iterator.next();
            // 日報毎のいいね数を取得
            long likes_count = (long)em.createNamedQuery("getLikesCountByReport", Long.class)
                                     .setParameter("report", rp)
                                     .getSingleResult();

            Long.toString(likes_count);
        }


        em.close();

        request.setAttribute("likes", likes);
        request.setAttribute("page", page);
        request.setAttribute("likes_count_list", likes_count_list);

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/likes/index.jsp");
        rd.forward(request, response);
    }
}


