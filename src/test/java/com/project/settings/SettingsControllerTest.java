package com.project.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.WithAccount;
import com.project.account.AccountRepository;
import com.project.account.AccountService;
import com.project.domain.Account;
import com.project.domain.Tag;
import com.project.domain.Zone;
import com.project.settings.form.TagForm;
import com.project.settings.form.ZoneForm;
import com.project.tag.TagRepository;
import com.project.zone.ZoneRepository;
import com.project.zone.ZoneService;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

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

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @WithAccount("test")
    @DisplayName("프로필 수정 폼")
    @Test
    void updateProfileForm() throws Exception {
        mockMvc.perform(get(SettingsController.PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    @WithAccount("test")
    @DisplayName("프로필 수정하기 - 입력값 정상")
    @Test
    void updateProfile() throws Exception {
        String bio = "짧은 소개를 수정하는 경우.";
        mockMvc.perform(post(SettingsController.PROFILE_URL)
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.PROFILE_URL))
                .andExpect(flash().attributeExists("message"));

        Account account = accountRepository.findByNickname("test");
        assertEquals(bio, account.getBio());
    }

    @WithAccount("test")
    @DisplayName("프로필 수정하기 - 입력값 30글자 초과")
    @Test
    void updateProfile_with_error() throws Exception {
        String bio = "길게 소개를 수정하는 경우. 길게 소개를 수정하는 경우. 길게 소개를 수정하는 경우. 길게 소개를 수정하는 경우.";
        mockMvc.perform(post(SettingsController.PROFILE_URL)
                        .param("bio", bio)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());

        Account account = accountRepository.findByNickname("test");
        assertNull(account.getBio());
    }

    @WithAccount("test")
    @DisplayName("비밀번호 수정 폼")
    @Test
    void updatePasswordForm() throws Exception {
        mockMvc.perform(get(SettingsController.PASSWORD_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @WithAccount("test")
    @DisplayName("비밀번호 수정하기 - 입력값 정상")
    @Test
    void updatePassword() throws Exception {
        String pw = "ag8dfgs8twg823";
        mockMvc.perform(post(SettingsController.PASSWORD_URL)
                        .param("newPassword", pw)
                        .param("newPasswordConfirm", pw)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.PASSWORD_URL))
                .andExpect(flash().attributeExists("message"));

        Account account = accountRepository.findByNickname("test");
        assertTrue(passwordEncoder.matches(pw, account.getPassword()));
    }

    @WithAccount("test")
    @DisplayName("비밀번호 수정하기 - 입력값 에러 - 패스워드 불일치")
    @Test
    void updatePassword_withError() throws Exception {
        String pw = "ag8dfgs8twg823";
        mockMvc.perform(post(SettingsController.PASSWORD_URL)
                        .param("newPassword", pw)
                        .param("newPasswordConfirm", "0000000000")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.PASSWORD_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().attributeExists("account"));
    }

    @WithAccount("test")
    @DisplayName("알림 수정 폼")
    @Test
    void updateNotificationsForm() throws Exception {
        mockMvc.perform(get(SettingsController.NOTIFICATIONS_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("notifications"));
    }

    @WithAccount("test")
    @DisplayName("알림 수정하기 - 성공")
    @Test
    void updateNotifications() throws Exception {
        mockMvc.perform(post(SettingsController.NOTIFICATIONS_URL)
                        .param("studyCreatedByEmail", "false")
                        .param("studyCreatedByWeb", "false")
                        .param("studyEnrollmentResultByEmail", "false")
                        .param("studyEnrollmentResultByWeb", "true")
                        .param("studyUpdatedByEmail", "false")
                        .param("studyUpdatedByWeb", "false")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.NOTIFICATIONS_URL))
                .andExpect(flash().attributeExists("message"));

        Account account = accountRepository.findByNickname("test");
        assertTrue(account.isStudyEnrollmentResultByWeb());
    }

    @WithAccount("test")
    @DisplayName("태그 수정 폼")
    @Test
    void updateTagsForm() throws Exception {
        mockMvc.perform(get(SettingsController.TAGS_URL))
                .andExpect(view().name(SettingsController.TAGS_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("tags"));
    }

    @WithAccount("test")
    @DisplayName("계정에 태그 추가")
    @Test
    void addTag() throws Exception {
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post(SettingsController.TAGS_URL + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        Tag newTag = tagRepository.findByTitle("newTag");
        assertNotNull(newTag);
        assertTrue(accountRepository.findByNickname("test").getTags().contains(newTag));
    }

    @WithAccount("test")
    @DisplayName("계정에 태그 제거")
    @Test
    void removeTag() throws Exception {
        Account account = accountRepository.findByNickname("test");
        Tag newTag = tagRepository.save(Tag.builder().title("newTag").build());
        accountService.addTag(account, newTag);

        assertTrue(account.getTags().contains(newTag));

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post(SettingsController.TAGS_URL + "/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        Tag findTag = tagRepository.findByTitle("newTag");
        assertNotNull(findTag);
        assertFalse(accountRepository.findByNickname("test").getTags().contains(newTag));
    }

    @WithAccount("test")
    @DisplayName("관심 지역 수정 폼")
    @Test
    void updateZoneForm() throws Exception {
        mockMvc.perform(get(SettingsController.ZONES_URL))
                .andExpect(view().name(SettingsController.ZONES_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(model().attributeExists("zones"));
    }

    @WithAccount("test")
    @DisplayName("계정에 지역 추가")
    @Test
    void addZone() throws Exception {
        ZoneForm zoneForm = new ZoneForm();
        Zone zone = zoneRepository.findAll().get(0);
        zoneForm.setZoneName(zone.toString());

        mockMvc.perform(post(SettingsController.ZONES_URL + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zoneForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        Zone findZone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        assertNotNull(findZone);
        assertTrue(accountRepository.findByNickname("test").getZones().contains(findZone));
    }

    @WithAccount("test")
    @DisplayName("계정에 지역 제거")
    @Test
    void removeZone() throws Exception {
        ZoneForm zoneForm = new ZoneForm();
        Zone zone = zoneRepository.findAll().get(0);
        zoneForm.setZoneName(zone.toString());
        Account account = accountRepository.findByNickname("test");
        accountService.addZone(account, zone);

        assertTrue(account.getZones().contains(zone));

        mockMvc.perform(post(SettingsController.ZONES_URL + "/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zoneForm))
                        .with(csrf()))
                .andExpect(status().isOk());
        Zone findZone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvinceName());
        assertNotNull(findZone);
        assertFalse(accountRepository.findByNickname("test").getZones().contains(findZone));
    }
}
