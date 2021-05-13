package models.validators;

import java.util.ArrayList;
import java.util.List;

import models.Report;

public class ReportValidator {

    /* クラス(static)メソッドのvalidate()メソッド宣言
    *
    * 戻り値:List<String>
    * 引数:Report
    *
    * 説明:titleとcontentのバリデーションチェックを行い、結果をerrors配列で返すメソッド
    */

    public static List<String> validate(Report r) {
        // List型コレクションのerrosを宣言し、ArryaList配列で初期化
        List<String> errors = new ArrayList<String>();

        // String型のtitle_error変数を宣言し、validateTitle()メソッドの戻り値を代入
        String title_error = _validateTitle(r.getTitle());

        // title_errorが空じゃない場合、errors配列にtitle_errorを追加
        if(!title_error.equals("")) {
            errors.add(title_error);
        }

        // String型のcontent_error変数を宣言し、validateContent()メソッドの戻り値を代入
        String content_error = _validateContent(r.getContent());
        // title_errorが空じゃない場合、errors配列にtitle_errorを追加
        if(!content_error.equals("")) {
            errors.add(content_error);
        }

        return errors;
    }

    /* privateメソッドの_validateTitle()メソッド宣言
    *
    * 戻り値:String
    * 引数:String
    *
    * 説明:titleが""(空白)の場合、またはtitleがnullの場合に文字列を返すメソッド
    */
    private static String _validateTitle(String title) {
        if(title == null || title.equals("")) {
            return "タイトルを入力してください。";
            }

        return "";
    }

    /* privateメソッドの_validateContent()メソッド宣言
    *
    * 戻り値:String
    * 引数:String
    *
    * 説明:contentが""(空白)の場合、またはcontentがnullの場合に文字列を返すメソッド
    */
    private static String _validateContent(String content) {
        if(content == null || content.equals("")) {
            return "内容を入力してください。";
            }

        return "";
    }

}
