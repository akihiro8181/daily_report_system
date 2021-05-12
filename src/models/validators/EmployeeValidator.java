package models.validators;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import models.Employee;
import utils.DBUtil;

public class EmployeeValidator {

    /* クラス(static)メソッドのvalidate()メソッド宣言
     *
     * 戻り値:List<String>
     * 引数1:Employee
     * 引数2:Boolean
     * 引数3:Boolean
     *
     */
    public static List<String> validate(Employee e, Boolean codeDuplicateCheckFlag, Boolean passwordCheckFlag) {
        // List型配列のerrors配列を宣言し、可変長のArrayListで初期化
        List<String> errors = new ArrayList<String>();

        // String型のcode_error変数を宣言し、validateCode()メソッドの戻り値を代入する
        String code_error = validateCode(e.getCode(), codeDuplicateCheckFlag);

        // code_error変数が""(空白)じゃない場合、errorsにcode_errorを追加する
        if(!code_error.equals("")) {
            errors.add(code_error);
        }

        // String型のname_error変数を宣言し、validateName()メソッドの戻り値を代入する
        String name_error = validateName(e.getName());

        // name_error変数が""(空白)じゃない場合、errorsにname_errorを追加する
        if(!name_error.equals("")) {
            errors.add(name_error);
        }

        // String型のPassword_error変数を宣言し、validatePassword()メソッドの戻り値を代入する
        String password_error = validatePassword(e.getPassword(), passwordCheckFlag);

        // password_error変数が""(空白)じゃない場合、errorsにpassword_errorを追加する
        if(!password_error.equals("")) {
            errors.add(password_error);
        }

        return errors;
    }

    // 社員番号
    private static String validateCode(String code, Boolean codeDuplicateCheckFlag) {
        // 必須入力チェック
        if(code == null || code.equals("")) {
            return "社員番号を入力してください。";
        }

        // すでに登録されている社員番号との重複チェック
        if(codeDuplicateCheckFlag) {
            EntityManager em = DBUtil.createEntityManager();
            long employees_count = (long)em.createNamedQuery("checkRegisteredCode", Long.class).setParameter("code", code).getSingleResult();
            em.close();
            if(employees_count > 0) {
                return "入力された社員番号の情報はすでに存在しています。";
            }
        }

        return "";
    }

    // 社員名の必須入力チェック
    private static String validateName(String name) {
        if(name == null || name.equals("")) {
            return "氏名を入力してください。";
        }

        return "";
    }

    // パスワードの必須入力チェック
    private static String validatePassword(String password, Boolean passwordCheckFlag) {
        // パスワードを変更する場合のみ実行
        if(passwordCheckFlag && (password == null || password.equals(""))) {
            return "パスワードを入力してください。";
        }
        return "";
    }

}
