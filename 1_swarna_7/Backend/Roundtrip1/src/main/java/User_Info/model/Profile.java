package User_Info.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.*;

import User_Info.model.User_Info;

@Entity
public class Profile {

    @Id
    @Column(name = "profile_id")
    private int profileId;

    private String profileBio;
    private String profileName;
    private Integer age;
    private String gender;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "profile_interests",
            joinColumns = @JoinColumn(name = "profile_id")
    )
    @Column(name = "interest")
    private Set<String> interests = new HashSet<>();

    @OneToOne
    @MapsId
    @JoinColumn(name = "profileId")
// REMOVED @JsonBackReference
    @JsonIgnore
    private User_Info user_info;

    @Lob
    @Column(name = "profile_picture", columnDefinition = "LONGBLOB")
    private byte[] profilePicture;

    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }


    public Profile() {
    }

    public int getProfileId() {
        return profileId;
    }

//    public void setProfileId(int profileId) {
//        this.profileId = profileId;
//    }

    public String getProfileBio() {
        return profileBio;
    }

    public void setProfileBio(String profileBio) {
        this.profileBio = profileBio;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public User_Info getUser_info() {
        return user_info;
    }

    public void setUser_info(User_Info user_info) {
        this.user_info = user_info;
    }

    public Set<String> getInterests() {
        return interests;
    }

    public void setInterests(Set<String> interests) {
        this.interests = interests;
    }

    @JsonIgnore
    public boolean hasProfileBio() {
        return profileBio != null;
    }

    @JsonIgnore
    public boolean hasProfileName() {
        return profileName != null;
    }

    @JsonIgnore
    public boolean hasAge() {
        return age != null;
    }

    @JsonIgnore
    public boolean hasGender() {
        return gender != null;
    }

    @JsonIgnore
    public boolean hasInterests() {
        return interests != null && !interests.isEmpty();
    }
}