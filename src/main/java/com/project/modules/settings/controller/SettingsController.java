package com.project.modules.settings.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.modules.account.service.AccountService;
import com.project.modules.account.util.CurrentAccount;
import com.project.modules.domain.Account;
import com.project.modules.domain.Tag;
import com.project.modules.domain.Zone;
import com.project.modules.settings.form.NicknameForm;
import com.project.modules.settings.form.Notifications;
import com.project.modules.settings.form.PasswordForm;
import com.project.modules.settings.form.Profile;
import com.project.modules.zone.form.ZoneForm;
import com.project.modules.zone.repository.ZoneRepository;
import com.project.modules.settings.validator.NicknameValidator;
import com.project.modules.settings.validator.PasswordFormValidator;
import com.project.modules.tag.form.TagForm;
import com.project.modules.tag.repository.TagRepository;
import com.project.modules.tag.service.TagService;
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
    static final String PASSWORD_VIEW = "settings/password";
    static final String NOTIFICATIONS_VIEW = "settings/notifications";
    static final String ACCOUNT_VIEW = "settings/account";
    static final String TAGS_VIEW = "settings/tags";
    static final String ZONES_VIEW = "settings/zones";

    private final AccountService accountService;
    private final ModelMapper modelMapper;
    private final NicknameValidator nicknameValidator;
    private final TagRepository tagRepository;
    private final ObjectMapper objectMapper;
    private final TagService tagService;
    private final ZoneRepository zoneRepository;

    /** Profile 폼/수정 */
    @GetMapping("/profile")
    public String profileUpdateForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, Profile.class));
        return PROFILE_VIEW;
    }

    @PostMapping("/profile")
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
        return "redirect:/settings/profile";
    }

    /** Password 폼/수정 */
    @GetMapping("/password")
    public String passwordUpdateForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new PasswordForm());
        return PASSWORD_VIEW;
    }

    @PostMapping("/password")
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
        return "redirect:/settings/password";
    }

    /** Notification 폼/수정 */
    @GetMapping("/notifications")
    public String notificationsUpdateForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, Notifications.class));
        return NOTIFICATIONS_VIEW;
    }

    @PostMapping("/notifications")
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
        return "redirect:/settings/notifications";
    }

    /** Account 폼/수정 */
    @GetMapping("/account")
    public String accountUpdateForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, NicknameForm.class));
        return ACCOUNT_VIEW;
    }

    @PostMapping("/account")
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
        return "redirect:/settings/account";
    }

    /** Tag 폼/추가/삭제 */
    @GetMapping("/tags")
    public String updateTagsForm(@CurrentAccount Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account);
        Set<Tag> tags = accountService.getTags(account);
        model.addAttribute("tags", tags.stream().map(Tag::getTitle).collect(Collectors.toList()));

        List<String> allTags = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allTags));

        return TAGS_VIEW;
    }

    @PostMapping("/tags/add")
    @ResponseBody
    public ResponseEntity updateTags(
            @CurrentAccount Account account,
            @RequestBody TagForm tagForm) {
        Tag tag = tagService.findOrCreateNew(tagForm.getTagTitle());
        accountService.addTag(account, tag);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tags/remove")
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

    /** Zone 폼/추가/삭제 */
    @GetMapping("/zones")
    public String updateZonesForm(@CurrentAccount Account account, Model model) throws JsonProcessingException {
        model.addAttribute(account);

        Set<Zone> zones = accountService.getZones(account);
        model.addAttribute("zones", zones.stream().map(Zone::toString).collect(Collectors.toList()));

        List<String> allZones = zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allZones));
        return ZONES_VIEW;
    }

    @PostMapping("/zones/add")
    @ResponseBody
    public ResponseEntity addZones(@CurrentAccount Account account, @RequestBody ZoneForm zoneForm) {
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }
        accountService.addZone(account, zone);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/zones/remove")
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
