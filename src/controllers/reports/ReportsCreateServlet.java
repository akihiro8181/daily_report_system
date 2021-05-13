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

import models.Employee;
import models.Report;
import models.validators.ReportValidator;
import utils.DBUtil;

/**
 * Servlet implementation class ReportsCreateServlet
 */
@WebServlet("/reports/create")
public class ReportsCreateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReportsCreateServlet() {
        super();
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // String型の_token変数を宣言し、 hiddenとして送信された_tokenを代入
        String _token = request.getParameter("_token");
        // 送信された_tokenがない場合やrequestのsessionIdが値と異なっていた場合にデータの登録ができないようにするif
        if(_token != null && _token.equals(request.getSession().getId())) {
            // // EntityManager型のem変数を宣言し、DBUnitのStaticメソッド呼び出して初期化
            EntityManager em = DBUtil.createEntityManager();

            // Report(DTO)のインスタンス生成
            Report r = new Report();

            // ReportのEmployeeに現在ログイン中のユーザ情報を格納
            r.setEmployee((Employee)request.getSession().getAttribute("login_employee"));

            // Date型のreport_date変数を宣言し、現在日付を代入
            Date report_date = new Date(System.currentTimeMillis());
            // String型のrd_str変数にリクエストから受け取った日付を代入
            String rd_str = request.getParameter("report_date");

            // 日付が入力されている場合、受け取った日付をreport_dateに格納する
            // しかし受け取った日付は文字列なのでDate型に変換する処理を行う
            if(rd_str != null && !rd_str.equals("")) {
                report_date = Date.valueOf(request.getParameter("report_date"));
            }

            // ReportインスタンスのReport_dateにreport_dateを代入
            r.setReport_date(report_date);

            // ReportインスタンスのTitleに受け取ったtitleを代入
            r.setTitle(request.getParameter("title"));
            // ReportインスタンスのContentに受け取ったcontentを代入
            r.setContent(request.getParameter("content"));

            // Timestamp型のcurrentTime変数を宣言し、Timestampのインスタンスを生成(現在時刻);
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            // Reportインスタンスのcreated_atとupdate_atにcurrentTime変数を代入
            r.setCreated_at(currentTime);
            r.setUpdated_at(currentTime);

            // バリデーションを実行してエラーがあったら新規登録のフォームに戻る
            List<String> errors = ReportValidator.validate(r);
            if(errors.size() > 0) {
                em.close();

                // フォームに初期値を設定、さらにエラーメッセージを送る
                request.setAttribute("_token", request.getSession().getId());
                request.setAttribute("report", r);
                request.setAttribute("errors", errors);

                RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/reports/new.jsp");
                rd.forward(request, response);
            } else {
                // トランザクションの始め
                em.getTransaction().begin();
                em.persist(r);  // reportsテーブルに設定したReportインスタンスの値(Employee,report_date,title,content,created_at,updated_at)を保存
                em.getTransaction().commit();   // 処理の確定
                em.close();     // emを閉じる(リソースの解放)
                request.getSession().setAttribute("flush", "登録が完了しました。");

                response.sendRedirect(request.getContextPath() + "/reports/index");
            }
        }
    }

}
