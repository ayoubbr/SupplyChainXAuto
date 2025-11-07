package ma.youcode.supplyChainX.controller;


import ma.youcode.supplyChainX.security.SecuredAction;
import ma.youcode.supplyChainX.shared.enums.Role;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/secure")
public class SecureController {

    @GetMapping("/admin-data")
    @SecuredAction(roles = {Role.ADMIN})
    public String adminOnly() {
        return "This data is only accessible to ADMIN users";
    }

    @GetMapping("/user-data")
    @SecuredAction(roles = {Role.CHEF_PRODUCTION, Role.ADMIN})
    public String userAccess() {
        return "This data is accessible to CHEF_PRODUCTION or ADMIN";
    }
}
