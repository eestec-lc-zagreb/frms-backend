package hr.eestec_zg.frmsbackend.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AjaxAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication)	throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_OK);

        authentication = SecurityContextHolder.getContext().getAuthentication();
        response.getWriter().print("{\"username\": \"" + authentication.getName() + "\"}");
    }
}