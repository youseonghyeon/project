package com.project.settings.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.account.service.AccountService;
import com.project.account.util.CurrentAccount;
import com.project.domain.Account;
import com.project.domain.Tag;
import com.project.domain.Zone;
import com.project.settings.form.*;
import com.project.settings.validator.NicknameValidator;
import com.project.settings.validator.PasswordFormValidator;
import com.project.tag.TagForm;
import com.project.tag.TagRepository;
import com.project.tag.TagService;
import com.project.zone.ZoneForm;
import com.project.zone.ZoneRepository;
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
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/settings")
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

    static final String PROFILE_VIEW = "settings/profile";
    static final String PROFILE = "/profile";

    static final String PASSWORD_VIEW = "settings/password";
    static final String PASSWORD = "/password";

    static final String NOTIFICATIONS_VIEW = "settings/notifications";
    static final String NOTIFICATIONS = "/notifications";

    static final String ACCOUNT_VIEW = "settings/account";
    static final String ACCOUNT = "/account";

    static final String TAGS_VIEW = "settings/tags";
    static final String TAGS = "/tags";

    static final String ZONES_VIEW = "settings/zones";
    static final String ZONES = "/zones";

    private final AccountService accountService;
    private final ModelMapper modelMapper;
    private final NicknameValidator nicknameValidator;
    private final TagRepository tagRepository;
    private final ObjectMapper objectMapper;
    private final TagService tagService;
    private final ZoneRepository zoneRepository;

    @GetMapping(PROFILE)
    public String profileUpdateForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, Profile.class));

        return PROFILE_VIEW;
    }

    @PostMapping(PROFILE)
    public String updateProfile(
            @CurrentAccount Account account,
            @ModelAttribute @Validated Profile profile,
            Errors errors,
            Model model,
            RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return PROFILE_VIEW;
        }

        accountService.updateProfile(account, profile);
        attributes.addFlashAttribute("message", "프로필을 수정했습니다.");
        return "redirect:/settings" + PROFILE;
    }

    @GetMapping(PASSWORD)
    public String passwordUpdateForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new PasswordForm());
        return PASSWORD_VIEW;
    }

    @PostMapping(PASSWORD)
    public String passwordUpdate(
            @CurrentAccount Account account,
            @ModelAttribute @Validated PasswordForm password,
            Errors errors,
            Model model,
            RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return PASSWORD_VIEW;
        }
        accountService.updatePassword(account, password);
        attributes.addFlashAttribute("message", "비밀번호를 수정했습니다.");
        return "redirect:/settings" + PASSWORD;
    }

    @GetMapping(NOTIFICATIONS)
    public String notificationsUpdateForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, Notifications.class));
        return NOTIFICATIONS_VIEW;
    }

    @PostMapping(NOTIFICATIONS)
    public String notificationsUpdate(
            @CurrentAccount Account account,
            @ModelAttribute Notifications notifications,
            Model model,
            Errors errors,
            RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return NOTIFICATIONS_VIEW;
        }
        accountService.updateNotifications(account, notifications);
        model.addAttribute(account);
        attributes.addFlashAttribute("message", "알림을 수정했습니다.");
        return "redirect:/settings" + NOTIFICATIONS;
    }

    @GetMapping(ACCOUNT)
    public String accountUpdateForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, NicknameForm.class));
        return ACCOUNT_VIEW;
    }

    @PostMapping(ACCOUNT)
    public String accountUpdate(
            @CurrentAccount Account account,
            @ModelAttribute @Validated NicknameForm nicknameForm,
            Model model,
            Errors errors,
            RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return ACCOUNT_VIEW;
        }
        accountService.updateAccount(account, nicknameForm);
        model.addAttribute(account);
        attributes.addFlashAttribute("message", "변경 성공");
        return "redirect:/settings" + ACCOUNT;
    }

    @GetMapping(TAGS)
    public String updateTagsForm(@CurrentAccount Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account);
        Set<Tag> tags = accountService.getTags(account);
        model.addAttribute("tags", tags.stream().map(Tag::getTitle).collect(Collectors.toList()));

        List<String> allTags = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allTags));

        return TAGS_VIEW;
    }

    @PostMapping(TAGS + "/add")
    @ResponseBody
    public ResponseEntity updateTags(
            @CurrentAccount Account account,
            @RequestBody TagForm tagForm) {
        Tag tag = tagService.findOrCreateNew(tagForm.getTagTitle());
        accountService.addTag(account, tag);
        return ResponseEntity.ok().build();
    }

    @PostMapping(TAGS + "/remove")
    @ResponseBody
    public ResponseEntity deleteTag(
            @CurrentAccount Account account,
            @RequestBody TagForm tagForm) {
        String title = tagForm.getTagTitle();
        Tag tag = tagRepository.findByTitle(title);
        if (tag == null) {
            return ResponseEntity.badRequest().build();
        }
        accountService.removeTag(account, tag);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ZONES)
    public String updateZonesForm(@CurrentAccount Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account);

        Set<Zone> zones = accountService.getZones(account);
        model.addAttribute("zones", zones.stream().map(Zone::toString).collect(Collectors.toList()));

        List<String> allZones = zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allZones));
        return ZONES_VIEW;
    }

    @PostMapping(ZONES + "/add")
    @ResponseBody
    public ResponseEntity addZones(@CurrentAccount Account account, @RequestBody ZoneForm zoneForm) {
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }
        accountService.addZone(account, zone);
        return ResponseEntity.ok().build();
    }

    @PostMapping(ZONES + "/remove")
    @ResponseBody
    public ResponseEntity removeZones(@CurrentAccount Account account, @RequestBody ZoneForm zoneForm) {
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }
        accountService.removeZone(account, zone);
        return ResponseEntity.ok().build();
    }
}
