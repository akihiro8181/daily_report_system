package controllers.reports;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Employee;
import models.Report;
import utils.DBUtil;

/**
 * Servlet implementation class ReportsEditServlet
 */
@WebServlet("/reports/edit")
public class ReportsEditServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReportsEditServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // EntityManager型のem変数を宣言し、DBUnitのStaticメソッド呼び出して初期化
        EntityManager em = DBUtil.createEntityManager();

        // requestから送られたidを所持しているReportオブジェクトをReport型のr変数に代入
        Report r = em.find(Report.class, Integer.parseInt(request.getParameter("id")));

        em.close(); // emを閉じる(リソースの解放)

        // セッションスコープにある現在ログイン中のユーザのオブジェクトをEmployee型のlogin_employeeに代入
        Employee login_employee = (Employee)request.getSession().getAttribute("login_employee");

        if(r != null && login_employee.getId() == r.getEmployee().getId()){
            request.setAttribute("report",r);
            request.setAttribute("_token", request.getSession().getId());
            request.getSession().setAttribute("report_id", r.getId());
        }

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/reports/edit.jsp");
        rd.forward(request, response);
    }

}
