package controllers.toppage;

import java.io.IOException;
import java.util.List;

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
 * Servlet implementation class TopPageIndexServlet
 */
@WebServlet("/index.html")
public class TopPageIndexServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public TopPageIndexServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // EntityManager型のem変数を宣言し、DBUnitのStaticメソッド呼び出して初期化
        EntityManager em = DBUtil.createEntityManager();

        //  Employee型のlogin_employeeに現在ログイン中のユーザ情報を格納
        Employee login_employee = (Employee)request.getSession().getAttribute("login_employee");

        // 開くページ数を取得（デフォルトは1ページ目）
        int page;
        try{
            page = Integer.parseInt(request.getParameter("page"));
        } catch(Exception e){
            page = 1;
        }

        // List型コレクションreportsを宣言し、Reportクラス（DTO）で宣言した「getMyAllReports」のSelect文を実行し代入
        List<Report> reports = em.createNamedQuery("getMyAllReports",Report.class)
                                    .setParameter("employee",login_employee)
                                    .setFirstResult(15 * (page - 1))
                                    .setMaxResults(15)
                                    .getResultList();

        // 自分の日報の件数を取得
        long reports_count = (long)em.createNamedQuery("getMyReportsCount", Long.class)
                                        .setParameter("employee", login_employee)
                                        .getSingleResult();

        // reportsテーブルのデータを取得したため、emを閉じる
        em.close();

        // List型配列のreportsをrequest先の"reports"に値を渡す
        // index.jspで${reports}等と書くことでreportsテーブルのデータを表示可能
        request.setAttribute("reports", reports);
        request.setAttribute("reports_count", reports_count);
        request.setAttribute("page", page);

        // フラッシュメッセージがセッションスコープにセットされていたら
        if(request.getSession().getAttribute("flush") != null) {
            // セッションスコープ内のフラッシュメッセージをリクエストスコープに保存し、セッションスコープからは削除する
            request.setAttribute("flush", request.getSession().getAttribute("flush"));
            request.getSession().removeAttribute("flush");
        }


        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/topPage/index.jsp");
        rd.forward(request, response);
    }

}
