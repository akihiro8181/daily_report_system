package listeners;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Application Lifecycle Listener implementation class Propertieslistener
 *
 */
@WebListener
public class Propertieslistener implements ServletContextListener {

    /**
     * Default constructor.
     */
    public Propertieslistener() {
    }

    /**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0)  {
    }

    /**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0)  {
        ServletContext context = arg0.getServletContext();

        // String型の変数pathを宣言し、application.propertiesのパスを代入
        String path = context.getRealPath("/META-INF/application.properties");

        try {
            // InputStream型のis変数を宣言し、application.propertiesへの接続を開く
            InputStream is = new FileInputStream(path);
            // Properties型のproperties変数を宣言し、Propertiesのインスタンスを代入
            Properties properties = new Properties();
            // 入力ストリームからキーと要素を対としたリストを読み込む
            properties.load(is);
            // 入力ストリームを閉じてリソースを解放する
            is.close();


            Iterator<String> pit = properties.stringPropertyNames().iterator();
            while(pit.hasNext()) {
                String pname = pit.next();
                context.setAttribute(pname, properties.getProperty(pname));
            }
        } catch(FileNotFoundException e) {
        } catch(IOException e) {}
    }

}
