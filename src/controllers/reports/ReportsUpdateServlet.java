package controllers.reports;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Report;
import models.validators.ReportValidator;
import utils.DBUtil;

/**
 * Servlet implementation class ReportsUpdateServlet
 */
@WebServlet("/reports/update")
public class ReportsUpdateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReportsUpdateServlet() {
        super();
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String _token = request.getParameter("_token");

        if(_token != null && _token.equals(request.getSession().getId())){
            // EntityManager型のem変数を宣言し、DBUnitのStaticメソッド呼び出して初期化
            EntityManager em = DBUtil.createEntityManager();

            // requestから送られたreport_idを所持しているReportオブジェクトをReport型のr変数に代入
            Report r = em.find(Report.class, (Integer)(request.getSession().getAttribute("report_id")));

            // Reportインスタンスのreport_dateにリクエストから受け取った日付を代入
            r.setReport_date(Date.valueOf(request.getParameter("report_date")));

            // ReportインスタンスのTitleに受け取ったtitleを代入
            r.setTitle(request.getParameter("title"));

            // ReportインスタンスのContentに受け取ったcontentを代入
            r.setContent(request.getParameter("content"));

            // Reportインスタンスのupdate_atに現在時刻を代入
            r.setUpdated_at(new Timestamp(System.currentTimeMillis()));

            // リデーションを実行してエラーがあったら新規登録のフォームに戻る
            List<String> errors = ReportValidator.validate(r);
            if(errors.size() > 0){
                em.close();

                // フォームに初期値を設定、さらにエラーメッセージを送る
                request.setAttribute("_token", request.getSession().getId());
                request.setAttribute("report", r);
                request.setAttribute("errors", errors);

                RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/reports/edit.jsp");
                rd.forward(request, response);

            } else {
                // トランザクション始め
                em.getTransaction().begin();
                em.getTransaction().commit();   // 更新の確定
                em.close(); // emを閉じる(リソースの解放)

                request.getSession().setAttribute("flush", "更新が完了しました。");

                // 更新成功時、sessionに残っているreport_id削除
                request.getSession().removeAttribute("report_id");

                response.sendRedirect(request.getContextPath() + "/reports/index");

            }


        }


    }

}
