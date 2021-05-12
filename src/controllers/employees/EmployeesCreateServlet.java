package controllers.employees;

import java.io.IOException;
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
import models.validators.EmployeeValidator;
import utils.DBUtil;
import utils.EncryptUtil;

/**
 * Servlet implementation class EmployeesCreateServlet
 */
@WebServlet("/employees/create")
public class EmployeesCreateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public EmployeesCreateServlet() {
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
            // EntityManager型のem変数を宣言し、DBUnitのStaticメソッド呼び出して初期化
            EntityManager em = DBUtil.createEntityManager();

            // Employee(DTO)のインスタンス生成
            Employee e = new Employee();

            // Employeeインスタンスのcodeにrequestから受け取ったcodeを代入
            e.setCode(request.getParameter("code"));
            // Employeeインスタンスのnameにrequestから受け取ったnameを代入
            e.setName(request.getParameter("name"));
            // Employeeインスタンスのpasswordにrequestから受け取ったpasswordを暗号化し代入
            e.setPassword(
                EncryptUtil.getPasswordEncrypt(
                    request.getParameter("password"),
                        (String)this.getServletContext().getAttribute("pepper")
                    )
                );
            // Employeeインスタンスのadmin_flagにrequestから受け取ったadmin_flagを代入
            e.setAdmin_flag(Integer.parseInt(request.getParameter("admin_flag")));

            // Timestamp型のcurrentTime変数を宣言し、Timestampのインスタンスを生成(現在時刻);
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            // Employeeインスタンスのcreated_atとupdate_atにcurrentTime変数を代入
            e.setCreated_at(currentTime);
            e.setUpdated_at(currentTime);

            // Employeeインスタンスのdelete_flagに0を代入
            e.setDelete_flag(0);

            // バリデーションを実行してエラーがあったら新規登録のフォームに戻る
            List<String> errors = EmployeeValidator.validate(e, true, true);
            if(errors.size() > 0) {
                em.close();

                // フォームに初期値を設定、さらにエラーメッセージを送る
                request.setAttribute("_token", request.getSession().getId());
                request.setAttribute("employee", e);
                request.setAttribute("errors", errors);

                RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/employees/new.jsp");
                rd.forward(request, response);
            } else {

                // トランザクションの始め
                em.getTransaction().begin();
                em.persist(e);      // employeesテーブルに設定したEmployeeインスタンスの値(code,name,password,admin_flag,created_at,updated_at,delete_flag)を保存
                em.getTransaction().commit();   // 処理の確定
                request.getSession().setAttribute("flush", "登録が完了しました。");   // 登録成功時のメッセージをセッションスコープに格納
                em.close();     // emを閉じる（リソースの解放）

                // indexページへリダイレクト(従業員一覧ページ)
                response.sendRedirect(request.getContextPath() + "/employees/index");
            }
        }
    }

}
