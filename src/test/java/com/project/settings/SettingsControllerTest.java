package com.project.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.WithAccount;
import com.project.modules.account.repository.AccountRepository;
import com.project.modules.account.service.AccountService;
import com.project.modules.domain.Account;
import com.project.modules.domain.Tag;
import com.project.modules.domain.Zone;
import com.project.modules.tag.form.TagForm;
import com.project.modules.zone.form.ZoneForm;
import com.project.modules.tag.repository.TagRepository;
import com.project.modules.zone.repository.ZoneRepository;
import com.project.modules.zone.service.ZoneService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    AccountService accountService;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    ZoneRepository zoneRepository;
    @Autowired
    ZoneService zoneService;

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

    static final String ZONES_VIEW_NAME = "settings/zones";
    static final String ZONES_URL = "/" + ZONES_VIEW_NAME;

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @WithAccount("test")
    @DisplayName("????????? ?????? ???")
    @Test
    void updateProfileForm() throws Exception {
        mockMvc.perform(get(PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    @WithAccount("test")
    @DisplayName("????????? ???????????? - ????????? ??????")
    @Test
    void updateProfile() throws Exception {
        String bio = "?????? ????????? ???????????? ??????.";
        mockMvc.perform(post(PROFILE_URL)
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(PROFILE_URL))
                .andExpect(flash().attributeExists("message"));

        Account account = accountRepository.findByNickname("test");
        assertEquals(bio, account.getBio());
    }

    @WithAccount("test")
    @DisplayName("????????? ???????????? - ????????? 30?????? ??????")
    @Test
    void updateProfile_with_error() throws Exception {
        String bio = "?????? ????????? ???????????? ??????. ?????? ????????? ???????????? ??????. ?????? ????????? ???????????? ??????. ?????? ????????? ???????????? ??????.";
        mockMvc.perform(post(PROFILE_URL)
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account account = accountRepository.findByNickname("test");
        assertNull(account.getBio());
    }

    @WithAccount("test")
    @DisplayName("???????????? ?????? ???")
    @Test
    void updatePasswordForm() throws Exception {
        mockMvc.perform(get(PASSWORD_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @WithAccount("test")
    @DisplayName("???????????? ???????????? - ????????? ??????")
    @Test
    void updatePassword() throws Exception {
        String pw = "ag8dfgs8twg823";
        mockMvc.perform(post(PASSWORD_URL)
                        .param("newPassword", pw)
                        .param("newPasswordConfirm", pw)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(PASSWORD_URL))
                .andExpect(flash().attributeExists("message"));

        Account account = accountRepository.findByNickname("test");
        assertTrue(passwordEncoder.matches(pw, account.getPassword()));
    }

    @WithAccount("test")
    @DisplayName("???????????? ???????????? - ????????? ?????? - ???????????? ?????????")
    @Test
    void updatePassword_withError() throws Exception {
        String pw = "ag8dfgs8twg823";
        mockMvc.perform(post(PASSWORD_URL)
                        .param("newPassword", pw)
                        .param("newPasswordConfirm", "0000000000")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(PASSWORD_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"));
    }

    @WithAccount("test")
    @DisplayName("?????? ?????? ???")
    @Test
    void updateNotificationsForm() throws Exception {
        mockMvc.perform(get(NOTIFICATIONS_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("notifications"));
    }

    @WithAccount("test")
    @DisplayName("?????? ???????????? - ??????")
    @Test
    void updateNotifications() throws Exception {
        mockMvc.perform(post(NOTIFICATIONS_URL)
                        .param("studyCreatedByEmail", "false")
                        .param("studyCreatedByWeb", "false")
                        .param("studyEnrollmentResultByEmail", "false")
                        .param("studyEnrollmentResultByWeb", "true")
                        .param("studyUpdatedByEmail", "false")
                        .param("studyUpdatedByWeb", "false")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(NOTIFICATIONS_URL))
                .andExpect(flash().attributeExists("message"));

        Account account = accountRepository.findByNickname("test");
        assertTrue(account.isStudyEnrollmentResultByWeb());
    }

    @WithAccount("test")
    @DisplayName("?????? ?????? ???")
    @Test
    void updateTagsForm() throws Exception {
        mockMvc.perform(get(TAGS_URL))
                .andExpect(view().name(TAGS_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("tags"));
    }

    @WithAccount("test")
    @DisplayName("????????? ?????? ??????")
    @Test
    void addTag() throws Exception {
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post(TAGS_URL + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        Tag newTag = tagRepository.findByTitle("newTag");
        assertNotNull(newTag);
        assertTrue(accountRepository.findByNickname("test").getTags().contains(newTag));
    }

    @WithAccount("test")
    @DisplayName("????????? ?????? ??????")
    @Test
    void removeTag() throws Exception {
        Account account = accountRepository.findByNickname("test");
        Tag newTag = tagRepository.save(Tag.builder().title("newTag").build());
        accountService.addTag(account, newTag);

        assertTrue(account.getTags().contains(newTag));

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post(TAGS_URL + "/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        Tag findTag = tagRepository.findByTitle("newTag");
        assertNotNull(findTag);
        assertFalse(accountRepository.findByNickname("test").getTags().contains(newTag));
    }

    @WithAccount("test")
    @DisplayName("?????? ?????? ?????? ???")
    @Test
    void updateZoneForm() throws Exception {
        mockMvc.perform(get(ZONES_URL))
                .andExpect(view().name(ZONES_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("zones"));
    }

    @WithAccount("test")
    @DisplayName("????????? ?????? ??????")
    @Test
    void addZone() throws Exception {
        ZoneForm zoneForm = new ZoneForm();
        Zone zone = zoneRepository.findAll().get(0);
        zoneForm.setZoneName(zone.toString());

        mockMvc.perform(post(ZONES_URL + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zoneForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        Zone findZone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        assertNotNull(findZone);
        assertTrue(accountRepository.findByNickname("test").getZones().contains(findZone));
    }

    @WithAccount("test")
    @DisplayName("????????? ?????? ??????")
    @Test
    void removeZone() throws Exception {
        ZoneForm zoneForm = new ZoneForm();
        Zone zone = zoneRepository.findAll().get(0);
        zoneForm.setZoneName(zone.toString());
        Account account = accountRepository.findByNickname("test");
        accountService.addZone(account, zone);

        assertTrue(account.getZones().contains(zone));

        mockMvc.perform(post(ZONES_URL + "/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zoneForm))
                        .with(csrf()))
                .andExpect(status().isOk());
        Zone findZone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        assertNotNull(findZone);
        assertFalse(accountRepository.findByNickname("test").getZones().contains(findZone));
    }
}
