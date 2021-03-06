package com.project.study;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.WithAccount;
import com.project.modules.account.repository.AccountRepository;
import com.project.modules.account.service.AccountService;
import com.project.modules.domain.Account;
import com.project.modules.domain.Study;
import com.project.modules.domain.Tag;
import com.project.modules.domain.Zone;
import com.project.modules.study.form.StudyForm;
import com.project.modules.study.repository.StudyRepository;
import com.project.modules.study.service.StudyService;
import com.project.modules.tag.form.TagForm;
import com.project.modules.tag.repository.TagRepository;
import com.project.modules.zone.form.ZoneForm;
import com.project.modules.zone.repository.ZoneRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class StudySettingControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    StudyService studyService;
    @Autowired
    StudyRepository studyRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AccountService accountService;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    ZoneRepository zoneRepository;


    String ROOT_PATH = "/study/test/settings";

    @BeforeEach
    public void beforeEach() {
        Account account = accountRepository.findByNickname("test");

        StudyForm studyForm = new StudyForm();
        studyForm.setPath("test");
        studyForm.setTitle("test");
        studyForm.setShortDescription("test");
        studyForm.setFullDescription("test");

        studyService.createNewStudy(modelMapper.map(studyForm, Study.class), account);
    }

    @AfterEach
    public void afterEach() {
        studyRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ??????(??????, ?????? ??????) ???")
    void studySettingForm() throws Exception {
        mockMvc.perform(get(ROOT_PATH + "/description"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/description"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("studyDescriptionForm"));
    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ??????, ?????? ??????")
    void updateStudyInfo() throws Exception {
        mockMvc.perform(post(ROOT_PATH + "/description")
                        .param("shortDescription", "modified_short_desc")
                        .param("fullDescription", "modified_full_desc")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT_PATH + "/description"))
                .andExpect(flash().attributeExists("message"));

        Study test = studyRepository.findByPath("test");
        assertNotNull(test);
        assertEquals(test.getFullDescription(), "modified_full_desc");
        assertEquals(test.getShortDescription(), "modified_short_desc");
    }

    @Test
    @WithAccount("test")
    @DisplayName("?????? ?????? ???")
    void bannerSettingForm() throws Exception {
        mockMvc.perform(get(ROOT_PATH + "/banner"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"));
    }

    @Test
    @WithAccount("test")
    @DisplayName("?????? ?????? ??????")
    void updateBanner() throws Exception {
        mockMvc.perform(post(ROOT_PATH + "/banner")
                        .param("image", "image")
                        .with(csrf()))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT_PATH + "/banner"));

        Study study = studyRepository.findByPath("test");
        assertEquals(study.getImage(), "image");
    }

    @Test
    @WithAccount("test")
    @DisplayName("?????? ?????????")
    void bannerEnable() throws Exception {
        mockMvc.perform(post(ROOT_PATH + "/banner/enable")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT_PATH + "/banner"));

        Study study = studyRepository.findByPath("test");
        assertTrue(study.isUseBanner());
    }

    @Test
    @WithAccount("test")
    @DisplayName("?????? ????????????")
    void bannerDisable() throws Exception {
        mockMvc.perform(post(ROOT_PATH + "/banner/disable")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT_PATH + "/banner"));

        Study study = studyRepository.findByPath("test");
        assertFalse(study.isUseBanner());
    }

    @Test
    @WithAccount("test")
    @DisplayName("?????? ?????? ???")
    void studyTagsForm() throws Exception {
        mockMvc.perform(get(ROOT_PATH + "/tags"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("tags"))
                .andExpect(model().attributeExists("whitelist"));
    }

    @Test
    @WithAccount("test")
    @DisplayName("?????? ??????")
    void addTag() throws Exception {
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("spring");
        mockMvc.perform(post(ROOT_PATH + "/tags/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());
        Study study = studyRepository.findByPath("test");
        Tag findTag = tagRepository.findByTitle("spring");
        Set<Tag> tags = study.getTags();
        assertNotNull(findTag);
        assertTrue(tags.contains(findTag));
    }

    @Test
    @WithAccount("test")
    @DisplayName("?????? ??????")
    void removeTag() throws Exception {
        // given
        Study study = studyRepository.findByPath("test");
        Tag tag = Tag.builder().title("spring").build();
        tagRepository.save(tag);
        studyService.addTag(study, tag);
        // when
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("spring");
        mockMvc.perform(post(ROOT_PATH + "/tags/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk());
        assertFalse(study.getTags().contains(tag));
        assertNotNull(tagRepository.findByTitle("spring"));
    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? ???")
    void studyZonesForm() throws Exception {
        mockMvc.perform(get(ROOT_PATH + "/zones"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("zones"))
                .andExpect(model().attributeExists("whitelist"));

    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? ??????")
    void addZone() throws Exception {
        Zone zone = getRandomZone();

        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(zone.toString());
        mockMvc.perform(post(ROOT_PATH + "/zones/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zoneForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        Study study = studyRepository.findByPath("test");
        assertTrue(study.getZones().contains(zone));
    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? ??????")
    void removeZone() throws Exception {
        Zone zone = getRandomZone();
        Study study = studyRepository.findByPath("test");
        ZoneForm zoneForm = new ZoneForm();
        zoneForm.setZoneName(zone.toString());
        studyService.addZone(study, zone);

        mockMvc.perform(post(ROOT_PATH + "/zones/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zoneForm))
                        .with(csrf()))
                .andExpect(status().isOk());

        assertFalse(study.getZones().contains(zone));
    }

    private Zone getRandomZone() {
        List<Zone> all = zoneRepository.findAll();
        Zone zone = all.get(0);
        if (zone == null) {
            throw new IllegalStateException("DB??? ?????? ????????? ???????????? ????????????.");
        }
        return zone;
    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? ???")
    void studySettingForm2() throws Exception {
        mockMvc.perform(get(ROOT_PATH + "/study"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"));
    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ??????")
    void publishStudy() throws Exception {
        mockMvc.perform(post(ROOT_PATH + "/study/publish")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT_PATH + "/study"))
                .andExpect(flash().attributeExists("message"));
        Study study = studyRepository.findByPath("test");
        assertTrue(study.isPublished());
    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????????")
    void closeStudy() throws Exception {
        Study study = studyRepository.findByPath("test");
        study.setPublished(true);

        mockMvc.perform(post(ROOT_PATH + "/study/close")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT_PATH + "/study"))
                .andExpect(flash().attributeExists("message"));

    }

    // TODO published (true -> true(exception), false -> false(exception), ?????? ?????? ?????? ????????? ?????? ??????)

    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? ??????")
    void startRecruit() throws Exception {
        Study study = studyRepository.findByPath("test");
        study.setRecruitingUpdatedDateTime(LocalDateTime.now().minusHours(2));
        mockMvc.perform(post(ROOT_PATH + "/recruit/start")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT_PATH + "/study"))
                .andExpect(flash().attributeExists("message"));

        assertTrue(study.isRecruiting());
    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? ??????")
    void stopRecruit() throws Exception {
        Study study = studyRepository.findByPath("test");
        study.setRecruiting(true);
        study.setRecruitingUpdatedDateTime(LocalDateTime.now().minusHours(2));

        mockMvc.perform(post(ROOT_PATH + "/recruit/stop")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT_PATH + "/study"))
                .andExpect(flash().attributeExists("message"));

        assertFalse(study.isRecruiting());
    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? ?????? ??????(?????? ?????? ??? 1?????? ?????? ??????)")
    void startRecruitFail() throws Exception {
        Study study = studyRepository.findByPath("test");
        study.setRecruitingUpdatedDateTime(LocalDateTime.now());
        study.setRecruiting(false);

        mockMvc.perform(post(ROOT_PATH + "/recruit/start")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT_PATH + "/study"))
                .andExpect(flash().attributeExists("message"));

        assertFalse(study.isRecruiting());
    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? ?????? ??????(?????? ?????? ??? 1?????? ?????? ??????)")
    void stopRecruitFail() throws Exception {
        Study study = studyRepository.findByPath("test");
        study.setRecruitingUpdatedDateTime(LocalDateTime.now());
        study.setRecruiting(true);

        mockMvc.perform(post(ROOT_PATH + "/recruit/stop")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT_PATH + "/study"))
                .andExpect(flash().attributeExists("message"));

        assertTrue(study.isRecruiting());
    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? ??????")
    void editPath() throws Exception {
        mockMvc.perform(post(ROOT_PATH + "/study/path")
                        .param("newPath", "abc")
                        .with(csrf()))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/abc/settings" + "/study"));
        Study study = studyRepository.findByPath("abc");
        assertNotNull(study);
    }
    // TODO ???????????? ?????? ?????? ?????? ????????? ??????

    @Test
    @WithAccount("test")
    @DisplayName("????????? ?????? ??????")
    void editTitle() throws Exception {
        mockMvc.perform(post(ROOT_PATH + "/study/title")
                        .param("newTitle", "miniCooper")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(ROOT_PATH + "/study"));

        Study study = studyRepository.findByPath("test");
        assertEquals(study.getTitle(), "miniCooper");
    }

    @Test
    @WithAccount("test")
    @DisplayName("????????? ??????")
    void removeStudy() throws Exception {
        Study study = studyRepository.findByPath("test");
        study.setPublished(false);
        mockMvc.perform(post(ROOT_PATH + "/study/remove")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        Study findStudy = studyRepository.findByPath("test");
        assertNull(findStudy);
    }

    //TODO ??????????????? ?????? ??????
//    @Test
//    @WithAccount("test")
//    @DisplayName("????????? ?????? ??????")
//    void removeStudyFail() throws Exception {
//        Study study = studyRepository.findByPath("test");
//        study.setPublished(true);
//
//        mockMvc.perform(post(ROOT_PATH + "/study/remove")
//                .with(csrf()))
//                .andExpect(result -> result.getResolvedException().getClass().isAssignableFrom(IllegalArgumentException.class))
//                .andReturn();
//
//        Study findStudy = studyRepository.findByPath("test");
//        assertNotNull(findStudy);
//    }
}
