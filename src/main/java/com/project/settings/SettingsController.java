package com.project.settings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.account.AccountService;
import com.project.account.CurrentUser;
import com.project.domain.Account;
import com.project.domain.Tag;
import com.project.settings.form.*;
import com.project.settings.validator.NicknameValidator;
import com.project.settings.validator.PasswordFormValidator;
import com.project.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    static final String PROFILE_URL = "/" + PROFILE_VIEW_NAME;

    static final String PASSWORD_VIEW_NAME = "settings/password";
    static final String PASSWORD_URL = "/" + PASSWORD_VIEW_NAME;

    static final String NOTIFICATIONS_VIEW_NAME = "settings/notifications";
    static final String NOTIFICATIONS_URL = "/" + NOTIFICATIONS_VIEW_NAME;

    static final String ACCOUNT_VIEW_NAME = "settings/account";
    static final String ACCOUNT_URL = "/" + ACCOUNT_VIEW_NAME;

    static final String TAGS_VIEW_NAME = "settings/tags";
    static final String TAGS_URL = "/" + TAGS_VIEW_NAME;

    private final AccountService accountService;
    private final ModelMapper modelMapper;
    private final NicknameValidator nicknameValidator;
    private final TagRepository tagRepository;
    private final ObjectMapper objectMapper;

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

    @GetMapping(TAGS_URL)
    public String updateTagsForm(@CurrentUser Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account);
        Set<Tag> tags = accountService.getTags(account);
        model.addAttribute("tags", tags.stream().map(Tag::getTitle).collect(Collectors.toList()));

        List<String> allTags = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allTags));

        return TAGS_VIEW_NAME;
    }

    @PostMapping("/settings/tags/add")
    @ResponseBody
    public ResponseEntity updateTags(
            @CurrentUser Account account,
            @RequestBody TagForm tagForm) {
        String title = tagForm.getTagTitle();
        Tag tag = tagRepository.findByTitle(title);
        if (tag == null) {
            tag = tagRepository.save(Tag.builder().title(tagForm.getTagTitle()).build());
        }
        accountService.addTag(account, tag);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/settings/tags/remove")
    @ResponseBody
    public ResponseEntity deleteTag(
            @CurrentUser Account account,
            @RequestBody TagForm tagForm) {
        String title = tagForm.getTagTitle();
        Tag tag = tagRepository.findByTitle(title);
        if (tag == null) {
            return ResponseEntity.badRequest().build();
        }
        accountService.removeTag(account, tag);
        return ResponseEntity.ok().build();
    }
}
