//package User_Info;
//import User_Info.controller.EventsController;
//import User_Info.controller.FriendshipController;
//import User_Info.controller.ProfileController;
//import User_Info.repository.EventsRepository;
//import User_Info.repository.ProfileRepository;
//import User_Info.repository.User_InfoRepository;
//import User_Info.service.EventsService;
//import User_Info.service.FriendshipService;
//import User_Info.service.NotificationsService;
//import User_Info.model.Profile;
//import User_Info.model.Events;
//import User_Info.model.EventInvitation;
//import User_Info.model.Review;
//import io.restassured.RestAssured;
//import io.restassured.response.Response;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//
//import static org.mockito.Mockito.when;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.verifyNoMoreInteractions;
//import static org.mockito.Mockito.mock;
//import static org.mockito.ArgumentMatchers.anyInt;
//import static org.mockito.ArgumentMatchers.any;
//
//
//import java.util.*;
//
//import static org.hamcrest.Matchers.hasSize;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//
//@RunWith(SpringRunner.class)
//@WebMvcTest(controllers = {
//        ProfileController.class,
//        EventsController.class,
//        FriendshipController.class
//})
//@AutoConfigureMockMvc(addFilters = false)
//@Import({EventsService.class, FriendshipService.class})
//public class TanyaSystemTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    // ---------- MOCKED REPOSITORIES ----------
//    @MockitoBean
//    private User_InfoRepository userRepo;
//
//    @MockitoBean
//    private ProfileRepository profileRepo;
//
//    @MockitoBean
//    private EventsRepository eventsRepo;
//
//    @MockitoBean
//    private FriendshipService friendshipService;
//
//    @MockitoBean
//    private EventsService eventsService;
//
//    @MockitoBean
//    private NotificationsService notificationsService;
//
//    @Test
//    public void testGetProfileSuccess() throws Exception {
//        Profile profile = new Profile();
//        profile.setProfileBio("Hello there!");
//        profile.setProfileName("Mica");
//        profile.setAge(21);
//        profile.setGender("female");
//        profile.setInterests(Set.of("tech", "sports"));
//
//        when(profileRepo.findByProfileId(10)).thenReturn(profile);
//
//        mockMvc.perform(get("/api/profile/10"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.profileName").value("Mica"))
//                .andExpect(jsonPath("$.profileBio").value("Hello there!"))
//                .andExpect(jsonPath("$.gender").value("female"))
//                .andExpect(jsonPath("$.interests", hasSize(2)));
//    }
//
//    @Test
//    public void testGetProfileNotFound() throws Exception {
//        when(profileRepo.findByProfileId(99)).thenReturn(null);
//
//        mockMvc.perform(get("/api/profile/99"))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.message").value("Profile not found by profileId."));
//    }
//
//
//    @Test
//    public void testEditProfile() throws Exception {
//        Profile existing = new Profile();
//        existing.setProfileName("OldName");
//        existing.setProfileBio("Old bio");
//        existing.setAge(20);
//
//        when(profileRepo.findByProfileId(12)).thenReturn(existing);
//        when(profileRepo.save(any(Profile.class))).thenReturn(existing);
//
//        String json = """
//            {
//              "profileName": "NewName",
//              "profileBio": "Updated Bio",
//              "age": 22,
//              "gender": "female",
//              "interests": ["Art", "Tech"]
//            }
//            """;
//
//        mockMvc.perform(put("/api/profile/edit/profile/12")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.profileName").value("NewName"))
//                .andExpect(jsonPath("$.age").value(22))
//                .andExpect(jsonPath("$.interests", hasSize(2)));
//    }
//
//    @Test
//    public void testGetEventById() throws Exception {
//        Events event = new Events();
//        event.setEventName("Hackathon");
//        event.setEventLocation("MU");
//        event.setDescription("Coding event");
//
//        when(eventsRepo.findById(5)).thenReturn(Optional.of(event));
//
//        mockMvc.perform(get("/api/events/5"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.eventName").value("Hackathon"))
//                .andExpect(jsonPath("$.eventLocation").value("MU"));
//    }
//
//    @Test
//    public void testGetEventByIdNotFound() throws Exception {
//        when(eventsRepo.findById(999)).thenReturn(Optional.empty());
//
//        mockMvc.perform(get("/api/events/999"))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.message").value("Event not found"));
//    }
//
//    @Test
//    public void testFriendsAttendingEventNotFound() throws Exception {
//        when(eventsRepo.findById(100)).thenReturn(Optional.empty());
//
//        mockMvc.perform(get("/api/friends/1/friendsAttending/100"))
//                .andExpect(status().isNotFound())
//                .andExpect(content().string("Event not found associated with that id"));
//    }
//}