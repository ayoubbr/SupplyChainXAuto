package ma.youcode.supplyChainX.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import ma.youcode.supplyChainX.model.User;
import ma.youcode.supplyChainX.repository.UserRepository;
import ma.youcode.supplyChainX.shared.enums.Role;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
public class SecurityAspect {

    private final HttpServletRequest request;
    private final UserRepository userRepository;

    @Before("@annotation(securedAction)")
    public void secure(JoinPoint joinPoint, SecuredAction securedAction) {
        String email = request.getHeader("X-User-Email");
        String password = request.getHeader("X-User-Password");

        if (email == null || password == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing authentication headers");
        }

        User user = userRepository.findByEmailAndPassword(email.trim(), password.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        Role[] allowedRoles = securedAction.roles();

        boolean authorized = Arrays.stream(allowedRoles)
                .anyMatch(role -> role == user.getRole());

        if (!authorized) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: insufficient role");
        }

        System.out.println("✅ Access granted to: " + user.getEmail()
                + " for " + joinPoint.getSignature().getName());
    }
}
