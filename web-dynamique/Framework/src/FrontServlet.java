package etu2053.framework.servlet;

import etu2053.framework.Mapping;
import etu2053.framework.annotation.Url;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
/**
 *
 * @author andy
 */
public class FrontServlet extends HttpServlet {
    HashMap<String,Mapping> mappingUrls;
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        HashMap<String,Mapping> mappingUrl = FrontServlet.getAllMapping();
        this.setMappingUrls(mappingUrl);
    }
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
     

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        out.println("Servlet : Front Servlet");
        out.println("");
        out.println("Context Path :"+request.getContextPath());
        out.println("");
        out.println("URL :"+request.getRequestURL());
        out.println("");
        out.println("Parametre :");
        Enumeration<String> liste = request.getParameterNames();
        while(liste.hasMoreElements()){
            String element = liste.nextElement();
            String[] elementValues = request.getParameterValues(element);
            for(int i=0 ; i<elementValues.length ; i++){
                out.println(element+" "+(i+1)+" : "+elementValues[i]);
            }
        }
        out.println("");

        out.println("Mapping Urls :");
        HashMap<String,Mapping> mappingUrl = this.getMappingUrls();
        Set keys = mappingUrl.keySet();
        Iterator itr = keys.iterator();
        while(itr.hasNext()){
            String key = (String) itr.next();
            out.print("Key : "+key+" , ");
            out.println("Value : Class: "+mappingUrl.get(key).getClassName()+", Method: "+mappingUrl.get(key).getMethod());
        }
        
        out.println("");
        itr = keys.iterator();
        while(itr.hasNext()){
            String key = (String) itr.next();
            if(key.equals(request.getServletPath())){
                try {
                    Class<?> classMapping = Class.forName("etu2053.framework.model."+mappingUrl.get(key).getClassName());
                    Object objet = classMapping.newInstance();
                    Method method = objet.getClass().getMethod(mappingUrl.get(key).getMethod());
                    out.println("Action : "+String.valueOf(method.invoke(objet)));
                } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
                    out.println(ex.getMessage());
                }
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

     public HashMap<String, Mapping> getMappingUrls() {
        return mappingUrls;
    }

    public void setMappingUrls(HashMap<String, Mapping> mappingUrls) {
        this.mappingUrls = mappingUrls;
    }
    
    public static HashMap<String,Mapping> getAllMapping(){
        HashMap<String , Mapping> mappingUrls = new HashMap<>();
        Set<Method> method = new Reflections("etu2053.framework.model",new MethodAnnotationsScanner()).getMethodsAnnotatedWith(Url.class);
        Iterator<Method> itr = method.iterator();
        while(itr.hasNext()){
            Method m = itr.next();
            
            Mapping tempMapping = new Mapping();
            tempMapping.setClassName(m.getDeclaringClass().getSimpleName());
            tempMapping.setMethod(m.getName());
            
            Url url = m.getAnnotation(Url.class);
            String cle = url.lien();
            
            mappingUrls.put(cle, tempMapping);
        }
        return mappingUrls;
    }
}

