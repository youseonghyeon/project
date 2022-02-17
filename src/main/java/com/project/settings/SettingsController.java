package com.project.settings;

import com.project.account.AccountService;
import com.project.account.CurrentUser;
import com.project.domain.Account;
import com.project.settings.form.NicknameForm;
import com.project.settings.form.Notifications;
import com.project.settings.form.PasswordForm;
import com.project.settings.form.Profile;
import com.project.settings.validator.NicknameValidator;
import com.project.settings.validator.PasswordFormValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class SettingsController {

    @InitBinder("passwordForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(new PasswordFormValidator());
    }

    @InitBinder("nicknameForm")
    public void initBinder_nickname(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(nicknameValidator);
    }

    static final String PROFILE_VIEW_NAME = "settings/profile";
    static final String PROFILE_URL = "/settings/profile";

    static final String PASSWORD_VIEW_NAME = "settings/password";
    static final String PASSWORD_URL = "/settings/password";

    static final String NOTIFICATIONS_VIEW_NAME = "settings/notifications";
    static final String NOTIFICATIONS_URL = "/settings/notifications";

    static final String ACCOUNT_VIEW_NAME = "settings/account";
    static final String ACCOUNT_URL = "/settings/account";

    private final AccountService accountService;
    private final ModelMapper modelMapper;
    private final NicknameValidator nicknameValidator;

    @GetMapping(PROFILE_URL)
    public String profileUpdateForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, Profile.class));

        return PROFILE_VIEW_NAME;
    }

    @PostMapping(PROFILE_URL)
    public String updateProfile(
            @CurrentUser Account account,
            @ModelAttribute @Validated Profile profile,
            Errors errors,
            Model model,
            RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return PROFILE_VIEW_NAME;
        }

        accountService.updateProfile(account, profile);
        attributes.addFlashAttribute("message", "프로필을 수정했습니다.");
        return "redirect:" + PROFILE_URL;
    }

    @GetMapping(PASSWORD_URL)
    public String passwordUpdateForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new PasswordForm());
        return PASSWORD_VIEW_NAME;
    }

    @PostMapping(PASSWORD_URL)
    public String passwordUpdate(
            @CurrentUser Account account,
            @ModelAttribute @Validated PasswordForm password,
            Errors errors,
            Model model,
            RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return PASSWORD_VIEW_NAME;
        }
        accountService.updatePassword(account, password);
        attributes.addFlashAttribute("message", "비밀번호를 수정했습니다.");
        return "redirect:" + PASSWORD_URL;
    }

    @GetMapping(NOTIFICATIONS_URL)
    public String notificationsUpdateForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, Notifications.class));
        return NOTIFICATIONS_VIEW_NAME;
    }

    @PostMapping(NOTIFICATIONS_URL)
    public String notificationsUpdate(
            @CurrentUser Account account,
            @ModelAttribute Notifications notifications,
            Model model,
            Errors errors,
            RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return NOTIFICATIONS_VIEW_NAME;
        }
        accountService.updateNotifications(account, notifications);
        model.addAttribute(account);
        attributes.addFlashAttribute("message", "알림을 수정했습니다.");
        return "redirect:" + NOTIFICATIONS_URL;
    }

    @GetMapping(ACCOUNT_URL)
    public String accountUpdateForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, NicknameForm.class));
        return ACCOUNT_VIEW_NAME;
    }

    @PostMapping(ACCOUNT_URL)
    public String accountUpdate(
            @CurrentUser Account account,
            @ModelAttribute @Validated NicknameForm nicknameForm,
            Model model,
            Errors errors,
            RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return ACCOUNT_URL;
        }
        accountService.updateAccount(account, nicknameForm);
        model.addAttribute(account);
        attributes.addFlashAttribute("message", "변경 성공");
        return "redirect:" + ACCOUNT_URL;
    }


}
