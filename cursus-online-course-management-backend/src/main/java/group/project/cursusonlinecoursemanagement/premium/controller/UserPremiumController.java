package group.project.cursusonlinecoursemanagement.premium.controller;

import group.project.cursusonlinecoursemanagement.premium.domain.dto.response.UserPremiumResponse;
import group.project.cursusonlinecoursemanagement.premium.service.UserPremiumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/user-premium")
@RequiredArgsConstructor
public class UserPremiumController {
    private final UserPremiumService userPremiumService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserPremiumResponse> getUserPremiumByUserId(@PathVariable UUID userId){
        return new ResponseEntity<>(userPremiumService.getUserPremiumByUserId(userId), HttpStatus.OK);
    }
}
