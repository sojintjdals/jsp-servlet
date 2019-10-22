package test.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.security.auth.message.callback.PrivateKeyCallback.Request;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Servlet implementation class HelloController
 */
@WebServlet(
		urlPatterns = { 
				"/HelloController", 
				"*.do"
		}, 
		initParams = { 
				@WebInitParam(name = "propertyConfig", value = "commandURI.properties")
		})
public class HelloController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private Map<String, Object> commandMap = new HashMap<String, Object>();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HelloController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		
		String props = config.getInitParameter("propertyConfig");
		String realFolder = "/property";
		
		ServletContext context = config.getServletContext();
		
		String realFilePath = context.getRealPath(realFolder) + File.separator + props;
		
		Properties prop = new Properties();
		
		FileInputStream fis = null;
		
		try {
			fis = new FileInputStream(realFilePath);
			
			 prop.load(fis);
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			if(fis != null) try {fis.close();} catch (Exception e2) {}
	}
		Iterator<?> keyIter = prop.keySet().iterator();
		
		while (keyIter.hasNext()) {
			String key = (String)keyIter.next();
			
			String value = prop.getProperty(key);
			
			try {
				Class<?> commandClass = Class.forName(value);
				Object commandInstance = commandClass.newInstance();
				commandMap.put(key, commandInstance);
			}catch(Exception e){
				throw new ServletException(e);
			}
		}
	}
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
		requestPro(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		requestPro(request, response);
		doGet(request, response);
	}
	
	private void requestPro(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String view = null;
		
		CommandProcess cp = null;
		
		try {
			String command = request.getRequestURI();
			
			if(command.indexOf(request.getContextPath()) == 0)
				command = command.substring(request.getContextPath().length());
				
			cp = (CommandProcess)commandMap.get(command);
			
			view = cp.requestPro(request, response);
		}catch(Throwable e){
		throw new ServletException(e);	
		}
		
		RequestDispatcher dispatcher = request.getRequestDispatcher(view);
		
		dispatcher.forward(request, response);
	}
}
	 		
	


